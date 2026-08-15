plugins {
	java
	id("org.springframework.boot") version libs.versions.spring.boot
	id("io.spring.dependency-management") version libs.versions.dependency.managment
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = ""

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.web.starter)
	implementation(libs.spring.boot.data.jpa.starter)
	implementation(libs.spring.boot.validation.starter)
	implementation(libs.spring.boot.security.starter)
	implementation(libs.spring.boot.logging.starter)
	compileOnly(libs.lombok)
	annotationProcessor(libs.lombok)
	testImplementation(libs.spring.boot.test.starter)
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
