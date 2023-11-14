plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("war")
}

dependencies {
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

    implementation(libs.netty)
    implementation(libs.jol.cli)
    implementation(libs.bundles.curatorLib)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.bundles.resilience4jLib)
    compileOnly(libs.javax.servlet.api)
}

tasks.bootRun{
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005")
}
