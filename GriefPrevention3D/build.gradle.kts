plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "enginehub"
        url = uri("https://maven.enginehub.org/repo/")
    }
    maven {
        name = "sonatype-snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.13.2")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.16")
    implementation("com.google.code.gson:gson:2.13.2")
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.gson:gson:2.13.2")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        relocate("com.google.gson", "com.example.griefprevention3d.libs.gson")
        archiveBaseName.set("GriefPrevention3D")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}