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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.springframework:spring-orm:6.2.3")
    implementation("org.hibernate.orm:hibernate-core:6.6.10.Final")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).apply {
                exclude(
                    "**/config/**",
                    "**/entity/**",
                    "**/dto/**",
                    "**/exception/**",
                    "**/*Config*.java",
                    "**/GlobalExceptionHandler.java",
                    "**/mdc/**",
                    "**/Pagination.java",
                    "**/aspects/**",
                    "**/*Aspect.java"
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
        //property("sonar.login", System.getenv("SONAR_TOKEN") ?: "")
//        property("sonar.login", "d641a182d06032100fab82f2899abbd790e3ff7b\n")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.junit.reportPaths", "${layout.buildDirectory.get()}/test-results/test")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.java.binaries", "${layout.buildDirectory.get()}/classes")
        property("sonar.coverage.exclusions",
            "**/*," +
                    "!**/service/**" // Negation pattern: exclude everything except service
        )
    }
}