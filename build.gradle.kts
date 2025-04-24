plugins {
  kotlin("jvm") version "2.1.0" // Currently the plugin is only available for Kotlin-JVM
  id("io.exoquery.exoquery-plugin") version "2.1.0-0.1.0.PL"
  kotlin("plugin.serialization") version "2.1.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  api("io.exoquery:exoquery-jdbc:0.1.0.PL-0.1.0")
  implementation("org.postgresql:postgresql:42.7.0")

  implementation("io.zonky.test:embedded-postgres:2.0.7")
  implementation("io.zonky.test.postgres:embedded-postgres-binaries-linux-amd64:16.2.0")

  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-common"))
  testImplementation(kotlin("test-annotations-common"))
}

tasks.test {
  useJUnitPlatform()
}
