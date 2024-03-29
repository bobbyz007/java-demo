plugins {
    // 会间接引人java-gradle-plugin,org.jetbrains.kotlin.jvm等插件
    `kotlin-dsl`
}

repositories {
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.2.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
}
