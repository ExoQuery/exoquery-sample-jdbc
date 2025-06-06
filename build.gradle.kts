plugins {
  kotlin("jvm") version "2.1.20" // Currently the plugin is only available for Kotlin-JVM
  id("io.exoquery.exoquery-plugin") version "2.1.20-L.1.3.2.PL"
  kotlin("plugin.serialization") version "2.1.20"
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/service/local/repositories/releases/content/")
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
  api("io.exoquery:exoquery-runner-jdbc:L.1.3.2.PL-L.1.3.2")
  implementation("org.postgresql:postgresql:42.7.0")
  implementation("io.zonky.test:embedded-postgres:2.0.7")
  implementation("io.zonky.test.postgres:embedded-postgres-binaries-linux-amd64:16.2.0")

  // OPTIONAL: Just for testing
  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-common"))
  testImplementation(kotlin("test-annotations-common"))
}

tasks.register<Jar>("customFatJar") {
  // copy the standard `jar` task’s contents first
  with(tasks.named<Jar>("jar").get())

  manifest {
    attributes["Main-Class"] = "com.baeldung.fatjar.Application"
  }

  archiveBaseName.set("all-in-one-jar")
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  from({
    configurations.runtimeClasspath.get()
      .map { if (it.isDirectory) it else zipTree(it) }
  })
}

// OPTIONAL: Make tests show in the build log even when they pass
tasks.withType<Test>().configureEach {
  testLogging {
    events("passed", "skipped", "failed")
    showStandardStreams = true
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}

configurations.forEach {
  it.exclude(group = "com.sschr15.annotations", module = "jb-annotations-kmp")
}
