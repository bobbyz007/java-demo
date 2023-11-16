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