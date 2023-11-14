plugins {
    alias(libs.plugins.spring.boot)
    application
    `maven-publish`
}

dependencies {
    // spring boot:web, aop, 管理服务，比如应用健康检查等等 http://localhost:8081/actuator/health
    implementation(libs.bundles.springBootLib)

    implementation(libs.log4j)

    testImplementation(libs.spring.boot.starter.test)

    implementation(libs.bundles.utilLib)
}

// register or create task
tasks.register<Copy>("copyTask") {
    from("src/main/resources")
    into("build/resources")
    include("*.txt")
}

/**
 * 使用register方法：
 * gradle one 只会运行task one， 因为register方法是按需（被调用时）初始化
 *
 * 使用create方法：
 * gradle one 会同时运行task one和two， 因为create创建task时都会初始化。
 */
tasks.register("one") {
    println("this is task one")
}
tasks.register("two") {
    println("this is task two")
}

/**
 * task dependency： 执行greet任务会先执行hello任务
 * 注意：不在doLast的代码是在配置阶段首先执行， doLast的代码在后面的运行阶段执行
 */
tasks.register("hello") {
    println("this is hello task")
    doLast {
        println("Hello!")
    }
}
tasks.register("greet") {
    dependsOn("hello")
    println("this is greet task")
    doLast{
        println("How are you?")
    }
}

application {
    mainClass = "com.example.Bootstrap"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example"
            artifactId = "tutorial"
            version = "1.0"

            from(components["java"])
        }
    }
}
