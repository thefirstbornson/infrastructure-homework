import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.3.50" apply false
    id("io.gitlab.arturbosch.detekt").version("1.10.0")
    id("org.owasp.dependencycheck").version("6.1.0")
    checkstyle
    jacoco
    id ("com.github.ben-manes.versions") version "0.46.0"
}

detekt {
    config = files("detekt/config.yml")
    buildUponDefaultConfig = true
}

configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.version?.endsWith("-SNAPSHOT") == true) {
                throw GradleException("found snapshot dependency for $requested")
            }
        }
}


allprojects {
    group = "com.stringconcat"
    version = "0.0.1"

    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict","-Werror")
            jvmTarget = "1.8"
        }
    }

}

java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}



dependencies {
    // spring modules
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":presentation"))
    implementation(project(":persistence"))
    implementation(project(":useCasePeople"))
    implementation(project(":businessPeople"))
    implementation(project(":quoteGarden"))
    implementation(project(":avatarsDicebear"))

    // dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    //persistance
    implementation("org.postgresql:postgresql:42.3.4")
    implementation("org.liquibase:liquibase-core:4.9.1")

    // tests
    testCompile("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.4".toBigDecimal()
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    finalizedBy(tasks.jacocoTestCoverageVerification)
}