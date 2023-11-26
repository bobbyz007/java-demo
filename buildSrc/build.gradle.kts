plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.14")
    testImplementation("junit:junit:4.13")
}
