plugins {
    id("java")
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

allprojects {
    group = "org.example"
    version = "1.0-SNAPSHOT"

    // 配置字符编码
    tasks.withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }

    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        // 或者公司本地仓库
        mavenCentral()
    }
}

subprojects {
    apply(plugin= "java")

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
