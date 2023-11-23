import com.example.gradle.ServerEnvironment

buildscript {
    repositories {
        // 本地仓库，对应maven-publish插件的 publishToMavenLocal任务
        mavenLocal()
        gradlePluginPortal()
    }
    dependencies {
        // com.example.gradle.main-plugin插件对应的依赖
        classpath("com.example.gradle.main-plugin:com.example.gradle.main-plugin.gradle.plugin:1.0.0")
    }
}
apply(plugin = "com.example.gradle.main-plugin")

dependencies {
    implementation(project(":gradle-tutorials:pub-api"))
    implementation(project(":gradle-tutorials:shared"))
}

/**
 * write a simple plugin
 */
class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("hello") {
            doLast {
                println("Hello from the GreetingPlugin")
            }
        }
    }
}
// apply plugin
apply<GreetingPlugin>()

/**
 * create extension object
 */
interface GreetingPluginExtension {
    val message: Property<String>
}
class GreetingPlugin2 : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'greeting' extension object
        val extension = project.extensions.create<GreetingPluginExtension>("greeting")
        extension.message.convention("Hello from GreetingPlugin")
        // Add a task that uses configuration from the extension object
        project.task("hello2") {
            doLast {
                println(extension.message.get())
            }
        }
    }
}

apply<GreetingPlugin2>()
// Configure the extension
the<GreetingPluginExtension>().message = "Hi from Gradle"

/**
 * extension including multiple properties
 */
interface GreetingPluginExtension3 {
    val message: Property<String>
    val greeter: Property<String>
}
class GreetingPlugin3 : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<GreetingPluginExtension3>("greeting3")
        project.task("hello3") {
            doLast {
                println("${extension.message.get()} from ${extension.greeter.get()}")
            }
        }
    }
}
apply<GreetingPlugin3>()
// Configure the extension using a DSL block
// you have several related properties you need to specify on a single plugin.
// Gradle adds a configuration block for each extension object
configure<GreetingPluginExtension3> {
    message = "Hi"
    greeter = "Gradle"
}

// 插件com.example.gradle.main-plugin的配置
configure<NamedDomainObjectContainer<ServerEnvironment>>() {
    create("dev") {
        url = "http://localhost:8080"
    }

    create("staging") {
        url = "http://staging.enterprise.com"
    }

    create("production") {
        url = "http://prod.enterprise.com"
    }
}
