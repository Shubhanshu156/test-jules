plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.6.0"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    // IntelliJ Platform dependencies
    intellijPlatform {
        intellijIdeaCommunity("2023.1")
        bundledPlugin("com.intellij.java")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }

    // Other dependencies
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
}

// Configure IntelliJ Platform Plugin
intellijPlatform {
    pluginConfiguration {
        name = "AI Code Reviewer"
        version = project.version.toString()
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Java compatibility settings
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    runIde {
        jvmArgs(
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.base/java.io=ALL-UNNAMED",
            "--add-opens", "java.base/java.util=ALL-UNNAMED",
            "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED"
        )
    }
}