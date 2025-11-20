plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    // CORE (usa automáticamente configuraciones de Boot)
    implementation("org.springframework.boot:spring-boot-starter")
    // Web REST
    implementation("org.springframework.boot:spring-boot-starter-web")
    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Validaciones (@NotNull, @Email, etc.)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // PostgreSQL driver
    runtimeOnly("org.postgresql:postgresql")
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
tasks.withType<Test> {
	useJUnitPlatform()
}
