plugins {
    id("java")
    alias(libs.plugins.spring.boot) apply false
    id("version-catalog")
    id("maven-publish")
}

// 推荐使用java扩展的toolchain，而不是sourceCompatibility和targetCompatibility
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
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

/**
 * 发布toml文件：基于catalog和publishing插件
 *
 * 使用已发布的toml文件:
 * dependencyResolutionManagement {
 *     versionCatalogs {
 *         libs {
 *             from("com.example.catalog:catalog:1.0.1")
 *             // 我们也可以重写覆盖catalog中的版本
 *             version("groovy", "3.0.6")
 *         }
 *     }
 * }
 */
catalog {
    // declare the aliases, bundles and versions in this block
    versionCatalog {
        from(files("./gradle/libs.versions.toml"))
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example.catalog"
            artifactId = "catalog"
            version = "1.0.1"
            from(components["versionCatalog"])
        }
    }
}
