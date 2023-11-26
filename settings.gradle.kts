dependencyResolutionManagement {
    versionCatalogs {
        /**
         * 1. 在使用 TOML 文件时，默认名是 libs， 如果创建的文件放置于 project/gradle/ 目录下面，
         * 则在 settings.gradle 文件中可以省略声明。建议显示声明。
         * 2. TOML 文件中变量命名大小写敏感，且以小写字母开头, 命名中可以如包含 - 或者 _或者. 在build.gradle.kts引用时，
         * 全部转化为 . 的引用方式。
         * */
        /*create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }*/
    }
    /**
     * Central declaration of repositories
     * 不用配置allprojects或subprojects或子项目中指定，此处统一配置仓库
     */
    repositories {
        repositories {
            maven("https://maven.aliyun.com/repository/public/")
            maven("https://mirrors.huaweicloud.com/repository/maven/")
            // 或者公司本地仓库
            mavenCentral()
        }
    }
    // 默认值
    repositoriesMode = RepositoriesMode.PREFER_PROJECT
    // 如果子项目中配置了repositories，会给出警告，使用settings中的配置
    // repositoriesMode = RepositoriesMode.PREFER_SETTINGS
    // 如果子项目中配置了repositories，直接构建失败
    // repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

rootProject.name = "java-demo"
include("main-demo")

include("gradle-tutorials:gradle-demo")
include("gradle-tutorials:shared")
include("gradle-tutorials:pub-api")
include("gradle-tutorials:services:person-service")
include("gradle-tutorials:services:webservice")

include("dubbo-provider")
include("dubbo-consumer")
include("dubbo-interface")


