plugins {
    alias(libs.plugins.spring.boot)
    id("war")
}

dependencies {
    implementation(project(":dubbo-interface"))
    // spring boot:web, aop, 管理服务，比如应用健康检查等等 http://localhost:8081/actuator/health
    implementation(libs.bundles.springBootLib)

    // db libs
    implementation(libs.bundles.dbBaseLib)

    // mysql driver
    runtimeOnly(libs.mysql.connector)

    // dubbo libs
    implementation(libs.bundles.dubboRelatedLib)

    implementation(libs.log4j)

    providedRuntime(libs.spring.boot.starter.tomcat)
    testImplementation(libs.spring.boot.starter.test)

    implementation(libs.bundles.utilLib)
}

