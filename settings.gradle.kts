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
}

rootProject.name = "java-demo"
include("main-demo")
include("gradle-demo")
include("dubbo-provider")
include("dubbo-consumer")
include("dubbo-interface")


