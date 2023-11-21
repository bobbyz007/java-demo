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



