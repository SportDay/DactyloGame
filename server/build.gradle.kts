plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

configurations {
    testCompileOnly
    testCompileClasspath
}

group = "fr.projet"
version = ""
//setBuildDir("$rootDir/build")

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")

    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("commons-io:commons-io:2.11.0")
}

application {
    mainClass.set("fr.projet.DactyloGameServer")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    jar {
        manifest {
            attributes(
                "Multi-Release" to true,
                "Main-Class" to application.mainClass.get(),
                "Description" to "Serveur Pour DactyloGame"
            )
        }
        from({
            configurations.runtimeClasspath.get().filter { it.isDirectory }.map { zipTree(it) }
        })
        enabled = false
    }

    shadowJar {
        archiveBaseName.set("DactyloGame")
        archiveClassifier.set("")
        archiveAppendix.set("Server")
        description = "Serveur Pour DactyloGame."
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
    run.get().standardInput = System.`in`
}