import org.apache.commons.codec.binary.Base64
// apply to all subprojects
buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/public/")
    }
    // 构建脚本需要用到的三方库, 或者 用于引入第三方插件的lib，然后在脚本中 apply(plugin = "pluginId")
    dependencies {
        classpath(libs.codec)
    }
}

plugins {
    id("java")
}

/**
 * 验证project解析的钩子函数： gradle.beforeProject/afterProject
 */
extra["hasTests"] = true

/**
 * 使用buildscript中引入的依赖
 */
tasks.register("encode") {
    doLast {
        val encodedString = Base64().encode("hello world\n".toByteArray())
        println(String(encodedString))
    }
}

/**
 * 使用extra properties
 */
val springVersion by extra("3.1.0")
val emailNotification by extra {"build@163.com"}
sourceSets.all() { extra["purpose"] = null }
sourceSets {
    main {
        extra["purpose"] = "production"
    }
    test {
        extra["purpose"] = "test"
    }
    create("plugin") {
        extra["purpose"] = "production"
    }
}
tasks.register("printProperties") {
    val productionSourceSets = provider {
        sourceSets.matching { it.extra["purpose"] == "production" }.map { it.name }
    }
    doLast {
        println(springVersion)
        println(emailNotification)
        productionSourceSets.get().forEach { println(it) }
    }
}

/**
 * lazy properties: Provider(read only) and Property(configurable)
 */
abstract class Greeting : DefaultTask() {
    @get:Input
    abstract val greeting: Property<String>

    @Internal
    val message: Provider<String> = greeting.map { "$it from Gradle" }

    @TaskAction
    fun printMessage() {
        logger.quiet(message.get())
    }
}
tasks.register<Greeting>("greeting") {
    greeting = "Hi"
}

/**
 * create properties or provider: project.objects.property(...) or property/provider.map(...)
 * connecting properties together: task <-> extension
 */
// a project extension
interface MessageExtension {
    val greeting: Property<String>
}
// Create the project extension
val messages = project.extensions.create<MessageExtension>("messages")
// Create the greeting task
tasks.register<Greeting>("greeting2") {
    // Attach the greeting from the project extension
    // Note that the values of the project extension have not been configured yet
    greeting = messages.greeting
}
messages.apply {
    // Configure the greeting on the extension
    // Note that there is no need to reconfigure the task's `greeting` property. This is automatically updated as the extension property changes
    greeting = "Hi2"
}

/**
 * working with task inputs and outputs
 * gradle gradle-tutorials:shared:consumer
 */
abstract class Producer : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun produce() {
        val message = "Hello, World!"
        val output = outputFile.get().asFile
        output.writeText( message)
        logger.quiet("Wrote '${message}' to ${output}")
    }
}
abstract class Consumer : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @TaskAction
    fun consume() {
        val input = inputFile.get().asFile
        val message = input.readText()
        logger.quiet("Read '${message}' from ${input}")
    }
}

// producer and consumer are of type 'TaskProvider'
val producer = tasks.register<Producer>("producer")
val consumer = tasks.register<Consumer>("consumer")
consumer {
    // Connect the producer task output to the consumer task input
    // Don't need to add a task dependency to the consumer task. This is automatically added
    inputFile = producer.flatMap { it.outputFile }
}
producer {
    // Set values for the producer lazily
    // Don't need to update the consumer.inputFile property. This is automatically updated as producer.outputFile changes
    outputFile = layout.buildDirectory.file("file.txt")
}

// Change the build directory.
// Don't need to update producer.outputFile and consumer.inputFile. These are automatically updated as the build directory changes
layout.buildDirectory = layout.buildDirectory.dir("output")


