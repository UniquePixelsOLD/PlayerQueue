plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

group = "net.uniquepixels"
version = "1.0.0"
description = "UniquePixels Queue System (paper based)"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    //maven("https://maven.enginehub.org/repo/")
    //maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.alessiodp.com/releases/")
    mavenCentral()
    mavenLocal()
}

val cloudNetVersion = "4.0.0-RC9"

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")

    implementation("net.uniquepixels:core:latest")
    implementation("net.uniquepixels:core-api:latest")

    implementation("org.mongodb:mongodb-driver-sync:4.10.1")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

//    shadowJar {
//        dependencies {
//            exclude(dependency("eu.cloudnetservice.cloudnet:bridge:$cloudNetVersion"))
//            exclude(dependency("eu.cloudnetservice.cloudnet:common:$cloudNetVersion"))
//            exclude(dependency("eu.cloudnetservice.cloudnet:driver:$cloudNetVersion"))
//            exclude(dependency("eu.cloudnetservice.cloudnet:platform-inject-api:$cloudNetVersion"))
//            exclude(dependency("eu.cloudnetservice.cloudnet:wrapper-jvm:$cloudNetVersion"))
//        }
//    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }


    reobfJar {
        // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
        // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
        outputJar.set(layout.buildDirectory.file("dist/PlayerQueuePaper-${project.version}.jar"))
    }
}