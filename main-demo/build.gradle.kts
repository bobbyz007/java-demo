plugins {
    id("myproject.java-conventions")
    alias(libs.plugins.spring.boot)
    id("war")
}

dependencies {
    // spring boot:web, aop, 管理服务，比如应用健康检查等等 http://localhost:8081/actuator/health
    implementation(libs.bundles.springBootLib)

    // db libs and redis
    implementation(libs.bundles.dbBaseLib)
    implementation(libs.redisson.springboot.starter)

    // mysql driver
    runtimeOnly(libs.mysql.connector)

    // dubbo libs
    implementation(libs.bundles.dubboRelatedLib)

    providedRuntime(libs.spring.boot.starter.tomcat)
    testImplementation(libs.spring.boot.starter.test)

    implementation(libs.bundles.utilLib)

    implementation(libs.netty.http)
    implementation(libs.jol.cli)
    implementation(libs.bundles.curatorLib)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.bundles.resilience4jLib)
    implementation(libs.selenium)
    implementation(libs.kafka.clients)
}

configurations {
    all {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
}

tasks.bootRun{
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005")
}
