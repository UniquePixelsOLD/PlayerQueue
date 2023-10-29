plugins {
    id("java")
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version ("1.0.1")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

val group = "net.uniquepixels"
val version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.cloudnetservice.eu/repository/releases/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

    implementation("redis.clients:jedis:4.3.1")

    implementation("org.mongodb:mongodb-driver-sync:4.10.2")

    implementation("net.uniquepixels:core:latest")
}

tasks.register("generateTemplates") {

}

tasks {
    shadowJar {
        dependencies {
            include(dependency("org.mongodb:mongodb-driver-sync:4.10.2"))
            include(dependency("org.mongodb:mongodb-driver-core:4.10.2"))
            include(dependency("org.mongodb:bson:4.10.2"))
            include(dependency("redis.clients:jedis:4.3.1"))
            include(dependency("org.apache.commons:commons-pool2:2.11.1"))
            exclude(dependency("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT"))
        }
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(14)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}