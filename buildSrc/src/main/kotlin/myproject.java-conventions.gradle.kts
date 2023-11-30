// Define Java conventions for this organization.
// Projects need to use the Java, Checkstyle and Spotbugs plugins.

plugins {
    java
    checkstyle

    // NOTE: external plugin version is specified in implementation dependency artifact of the project's build file
    id("com.github.spotbugs")
}

// Projects should use Maven Central for external dependencies
// This could be the organization's private repository
repositories {
    maven("https://maven.aliyun.com/repository/public/")
    maven("https://mirrors.huaweicloud.com/repository/maven/")
    // 或者公司本地仓库
    mavenCentral()
}

// Projects have the 'com.example' group by convention
group = "com.example"
version = "1.0-SNAPSHOT"

// 推荐使用java扩展的toolchain，而不是sourceCompatibility和targetCompatibility
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// 配置字符编码
tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    // Enable deprecation messages when compiling Java code
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.test {
    useJUnitPlatform()
}

// Use the Checkstyle rules provided by the convention plugin
// Do not allow any warnings
checkstyle {
    config = resources.text.fromString(com.example.CheckstyleUtil.getCheckstyleConfig("/checkstyle.xml"))
    maxWarnings = 0
}
