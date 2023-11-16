import org.apache.commons.codec.binary.Base64

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/public/")
    }
    // 构建脚本需要用到的三方库
    dependencies {
        "classpath"(group = "commons-codec", name = "commons-codec", version = "1.16.0")
    }
}

plugins {
    id("java")
}
extra["hasTests"] = true

tasks.register("encode") {
    doLast {
        // 使用buildscript中引入的依赖
        val encodedString = Base64().encode("hello world\n".toByteArray())
        println(String(encodedString))
    }
}

