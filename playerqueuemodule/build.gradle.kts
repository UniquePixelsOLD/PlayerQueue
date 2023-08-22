plugins {
    id("java")
    //id("com.github.johnrengelman.shadow") version ("8.1.1")
}

group = "net.uniquepixels"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {

    compileOnly("eu.cloudnetservice.cloudnet:platform-inject-api:4.0.0-RC9")
    compileOnly("eu.cloudnetservice.cloudnet:bridge:4.0.0-RC9")
    implementation("eu.cloudnetservice.cloudnet:wrapper-jvm:4.0.0-RC9")

    implementation("org.mongodb:mongodb-driver-sync:4.10.2")

    implementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

}

//tasks {
//    shadowJar {
//        dependencies {
//            exclude("eu.cloudnetservice.cloudnet:platform-inject-api:4.0.0-RC9")
//            exclude("eu.cloudnetservice.cloudnet:bridge:4.0.0-RC9")
//        }
//    }
//}