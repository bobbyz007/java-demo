// Define Java Library conventions for this organization.
// Projects need to use the organization's Java conventions and publish using Maven Publish

plugins {
    `java-library`
    `maven-publish`
    id("version-catalog")
    id("myproject.java-conventions")
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


// The project requires libraries to have a README containing sections configured below
val readmeCheck by tasks.registering(com.example.ReadmeVerificationTask::class) {
    readme = layout.projectDirectory.file("README.md")
    readmePatterns = listOf("^## API$", "^## Changelog$")
}

tasks.named("check") { dependsOn(readmeCheck) }
