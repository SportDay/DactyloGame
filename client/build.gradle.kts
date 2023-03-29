plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")

    implementation("org.fxmisc.richtext:richtextfx:0.11.0")

    implementation("com.google.code.gson:gson:2.10")

    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")

    implementation("commons-io:commons-io:2.11.0")
    implementation("org.openjfx","javafx-base",javafx.version, "win")
    implementation("org.openjfx","javafx-base",javafx.version, "mac")
    implementation("org.openjfx","javafx-base",javafx.version, "linux")

    implementation("org.openjfx","javafx-controls",javafx.version, "win")
    implementation("org.openjfx","javafx-controls",javafx.version, "mac")
    implementation("org.openjfx","javafx-controls",javafx.version, "linux")

    implementation("org.openjfx","javafx-fxml",javafx.version, "win")
    implementation("org.openjfx","javafx-fxml",javafx.version, "mac")
    implementation("org.openjfx","javafx-fxml",javafx.version, "linux")

    implementation("org.openjfx","javafx-graphics",javafx.version, "win")
    implementation("org.openjfx","javafx-graphics",javafx.version, "mac")
    implementation("org.openjfx","javafx-graphics",javafx.version, "linux")


    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:win")
    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:linux")
    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:mac")

}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

javafx {
    version = "19"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("fr.projet.Main")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    javadoc {
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
                "Description" to "Un jeu pour apprendre à taper au clavier et s’améliorer en se mesurant à d’autres joueurs."
            )
        }
        from({
            configurations.runtimeClasspath.get().filter{ it.isDirectory }.map{zipTree(it)}
        })
        enabled = false
    }

    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveBaseName.set("DactyloGame")
        archiveClassifier.set("")
        archiveAppendix.set("Client")
        description = "Un jeu pour apprendre à taper au clavier et s’améliorer en se mesurant à d’autres joueurs."
    }

    test {
        useJUnitPlatform()
    }
}