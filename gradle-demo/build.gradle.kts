plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // spring boot:web, aop, 管理服务，比如应用健康检查等等 http://localhost:8081/actuator/health
    implementation(libs.bundles.springBootLib)

    implementation(libs.log4j)

    testImplementation(libs.spring.boot.starter.test)

    implementation(libs.bundles.utilLib)
}
