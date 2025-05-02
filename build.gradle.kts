//plugins {
//    java
//    id("org.springframework.boot") version "3.4.4"
//    id("io.spring.dependency-management") version "1.1.7"
//}
//group = "org.example"
//version = "0.0.1-SNAPSHOT"
//
//
//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(17)
//    }
//}
//
//configurations {
//    compileOnly {
//        extendsFrom(configurations.annotationProcessor.get())
//    }
//}
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation("org.springframework.boot:spring-boot-starter-web")
//
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("mysql:mysql-connector-java:8.0.33")
//    implementation("org.springframework:spring-orm:6.2.3")
//    implementation("org.hibernate.orm:hibernate-core:6.6.10.Final")
//    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
//    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("org.springframework.boot:spring-boot-starter-validation")
//    compileOnly("org.projectlombok:lombok")
//    annotationProcessor("org.projectlombok:lombok")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//}
//
//tasks.withType<Test> {
//    useJUnitPlatform()
//}

plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.sonarqube") version "6.0.1.5171"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)

    systemProperty("java.io.tmpdir", "${layout.buildDirectory.get()}/tmp")
    systemProperty("jdk.attach.allowAttachSelf", "true")

    jvmArgs = listOf(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "-Djdk.io.File.enableADS=true"
    )

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

jacoco {
    toolVersion = "0.8.11"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",
                    "**/entity/**",
                    "**/dto/**",
                    "**/exception/**",
                    "**/*Config*",
                    "**/GlobalExceptionHandler*",
                    "**/mdc/**",
                    "**/Pagination*",
                    "**/aspects/**",
                    "**/*Aspect*"
                )
            }
        })
    )
}

sonar {
    properties {
        property("sonar.projectKey", "Jalet-fry_Filmoteca")
        property("sonar.organization", "jalet-fry")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.inclusions", "**/service/implementation**/*.java")
        property("sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
    }
}