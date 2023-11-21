import java.time.Duration

plugins {
    id("java")
}

dependencies {
    implementation(project(":gradle-tutorials:shared"))
}

/**
 * 验证project解析的钩子函数： gradle.beforeProject/afterProject
 */
extra["hasTests"] = true

/**
 * copy single/multiple file(s)
 */
tasks.register<Copy>("copyResource") {
    from(layout.projectDirectory.file("src/main/resources/test.conf"),
        layout.projectDirectory.file("src/main/resources/test2.conf"))
    into(layout.buildDirectory.dir("toDist"))
}

/**
 * copy files filtered in directory(基于from)
 */
tasks.register<Copy>("copyInclude") {
    // 针对from下的文件，不包含from指定的文件夹
    from(layout.projectDirectory.dir("src/main/resources"))
    // 遍历所有子目录，包含文件夹结构
    include("**/*.conf")
    // 遍历直接子目录
    // include("*.conf")
    into(layout.buildDirectory.dir("toDist1"))
}

/**
 * copy files filtered in directory(基于 include)
 */
tasks.register<Copy>("copyIncludeFrom") {
    from(layout.projectDirectory.dir("src/main")) {
        // 使用include，拷贝时会包含指定的文件夹
        include("resources/**/*.conf")
    }
    into(layout.buildDirectory.dir("toDist2"))
}

/**
 * 打包文件夹下的所有文件
 */
tasks.register<Zip>("packageDist") {
    archiveFileName = "my-dist.zip"
    destinationDirectory = layout.buildDirectory.dir("archive")

    // 压缩包不包含 toDist 目录
    from(layout.buildDirectory.dir("toDist1"))
}

/**
 * 解压本质上就是copy，这也就是为什么from中嵌套zipTree
 */
tasks.register<Copy>("unpackFiles") {
    from(zipTree(layout.buildDirectory.file("archive/my-dist.zip")))
    into(layout.buildDirectory.dir("unpack/dist"))
}

/**
 * 解压部分文件夹
 */
tasks.register<Copy>("unpackFiles2") {
    from(zipTree(layout.buildDirectory.file("archive/my-dist.zip"))) {
        include("dir1/**")
        eachFile() {
            // 排除dir1目录，* 是将数组展开（spread）为可变数目变量'...'
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
        includeEmptyDirs = false
    }
    into(layout.buildDirectory.dir("unpack/dist1"))
}

/**
 * file collections lazy creation: passong a closure or Provide to the files() method.
 */
tasks.register("listFiles") {
    val resourcesDir = layout.projectDirectory.dir("src/main/resources")
    doLast {
        var dir: File? = null
        // 传入closure，延迟到运行时解析： 此时dir为null
        val collection = resourcesDir.files({dir?.listFiles()})
        // or provider
        // val collection = resourcesDir.files(provider({dir?.listFiles()}))

        dir = resourcesDir.file("dir1").asFile
        println("Contents of ${dir.name}")
        collection.map { it.relativeTo(resourcesDir.asFile) }.sorted().forEach { println(it) }

        println("------------------------------------------------")
        dir = resourcesDir.file("spring").asFile
        println("Contents of ${dir.name}")
        // lazy creation:延迟到运行时再解析 file collection
        collection.map { it.relativeTo(resourcesDir.asFile) }.sorted().forEach { println(it) }

        // 支持+/-
        println("------------------------------------------------")
        val union = collection + resourcesDir.files("test.conf")
        union.map { it.relativeTo(resourcesDir.asFile) }.sorted().forEach { println(it) }
    }
}

/**
 * fileTree 是有结构的
 */
tasks.register("listFiles2") {
    doLast {
        var tree = project.fileTree("src/main/resources")
        tree.include("**/*.yaml")

        tree.forEach() {
            println(it)
        }
    }
}

// skipping task
// gradle gradle-tutorials:pub-api:hello -PskipHello
val hello by tasks.registering {
    doLast {
        println("hello world")
    }
}
hello {
    val skipProvider = providers.gradleProperty("skipHello")
    onlyIf("there is no property skipHello") {
        !skipProvider.isPresent
    }
}

// task enable/disable/timeout
tasks.register("hangingTask") {
    doLast() {
        Thread.sleep(1000)
    }
    timeout = Duration.ofMillis(500)

    // enable or disable task
    enabled = true
}

// task rule
// gradle gradle-tutorials:pub-api:pingJustin
tasks.addRule("Pattern: ping<ID>") {
    val taskName = this
    // or this.startsWith(...)
    if (startsWith("ping")) {
        task(taskName) {
            doLast {
                println("Pinging: " + (taskName.replace("ping", "")))
            }
        }
    }
}
tasks.register("groupPing") {
    dependsOn("pingServer1", "pingServer2")
}

// finalizer task
val taskX by tasks.registering {
    doLast() {
        println("taskX")
        throw RuntimeException()
    }
}
val taskY by tasks.registering {
    doLast() {
        println("taskY")
    }
}
taskX {
    finalizedBy(taskY)
}

// writing task types
abstract class GreetingTask : DefaultTask() {
    @get:Input // 在get函数上添加注解Input，表示这个字段作为task的输入
    abstract val greeting: Property<String>
    init {
        greeting.convention("hello from GreetingTask")
    }
    @TaskAction
    fun greet() {
        println(greeting.get())
    }
}
tasks.register<GreetingTask>("greet") // use convention greeting
tasks.register<GreetingTask>("greetCustomize") {// customize the greeting
    greeting = "greetings from GreetingTask"
}

// incremental task
// 第二遍执行，如果任务输入没有变化，则任务执行状态是UP-TO-DATE，可以增删改文件查看效果
// 另外对于非文件类型的属性变化， gradle无法判断这种属性变化对输出的影响，则这种属性值变化都会执行全量构建。
// gradle gradle-tutorials:pub-api:incrementalReverse -PtaskInputProperty=changed
abstract class IncrementalReverseTask : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val inputProperty: Property<String>

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        println(
            if (inputChanges.isIncremental) "Executing incrementally"
            else "Executing non-incrementally"
        )

        inputChanges.getFileChanges(inputDir).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return@forEach

            println("${change.changeType}: ${change.normalizedPath}")
            val targetFile = outputDir.file(change.normalizedPath).get().asFile
            if (change.changeType == ChangeType.REMOVED) {
                targetFile.delete()
            } else {
                targetFile.writeText(change.file.readText().reversed())
            }
        }
    }
}
tasks.register<IncrementalReverseTask>("incrementalReverse") {
    inputDir = file("src/main/resources/incremental-inputs")
    outputDir = layout.buildDirectory.dir("incremental-outputs")
    inputProperty = project.findProperty("taskInputProperty") as String? ?: "original"
}

/**
 * Declaring and Using Command Line Options for task
 * 基于option注解， 在命令行参数自动识别option选项
 * gradle gradle-tutorials:pub-api:verifyUrl --url=www.baidu.com
 */
abstract class UrlVerify : DefaultTask() {
    @set:Option(option = "url", description = "Configures the URL to be verified")
    @get:Input
    abstract var url: String

    @TaskAction
    fun verify() {
        println("Verifying URL: $url")
    }
}
tasks.register<UrlVerify>("verifyUrl")

/**
 * Worker api:  perform independent units of work concurrently
 */
// The parameters for a single unit of work
interface ReverseParameters : WorkParameters {
    val fileToReverse : RegularFileProperty
    val destinationDir : DirectoryProperty
}
// The implementation of a single unit of work
abstract class ReverseFile @Inject constructor(val fileSystemOperations: FileSystemOperations) : WorkAction<ReverseParameters> {
    override fun execute() {
        fileSystemOperations.copy {
            from(parameters.fileToReverse)
            into(parameters.destinationDir)
            filter { line: String -> line.reversed() }
        }
    }
}
// The WorkerExecutor will be injected by Gradle at runtime
abstract class ReverseFiles @Inject constructor(private val workerExecutor: WorkerExecutor) : SourceTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun reverseFiles() {
        // Create a WorkQueue to submit work items
        val workQueue = workerExecutor.noIsolation()

        // Create and submit a unit of work for each file
        source.forEach { file ->
            workQueue.submit(ReverseFile::class) {
                fileToReverse = file
                destinationDir = outputDir
            }
        }
    }
}
// gradle gradle-tutorials:pub-api:reverseFiles
tasks.register<ReverseFiles>("reverseFiles") {
    source("src/main/resources/concurrent-input")
    outputDir = layout.buildDirectory.dir("concurrent-output")
}

