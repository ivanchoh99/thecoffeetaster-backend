import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version libs.versions.spring.boot
	id("io.spring.dependency-management") version libs.versions.dependency.managment
}

group = "com.thecoffeetaster"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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

tasks.named<Jar>("jar") {
	enabled = false
}

tasks.named<BootJar>("bootJar") {
	layered {
		enabled = true
	}
	archiveFileName.set("app.jar")
}