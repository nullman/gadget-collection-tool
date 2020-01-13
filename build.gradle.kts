import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.61"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

version = "1.0.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    jcenter()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.h2database:h2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("junit:junit:4.+")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.3.61")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
