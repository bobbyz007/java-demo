import java.nio.file.Files

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
