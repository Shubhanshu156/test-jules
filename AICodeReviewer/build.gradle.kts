plugins {
    id("java") // Java plugin
    id("org.jetbrains.intellij.platform") version "2.0.0-beta2" // IntelliJ Platform plugin
}

intellijPlatform {
    pluginConfiguration {
        name = "AI Code Reviewer"
        id = "com.example.AICodeReviewer" // Replace with a unique ID later
        version = "0.0.1"
        vendor {
            name = "YourName/Company" // Placeholder
            email = "contact@example.com" // Placeholder
        }
        description = "An intelligent code reviewer powered by AI."
        changeNotes = "Initial version."
    }

    // Configure the target IntelliJ Platform version
    pluginVerification {
        // Define IDE versions for plugin verification
        ides {
            recommended() // Uses the same IDE version as Gradle
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    intellijPlatform {
        // Define the target IntelliJ Platform dependency (e.g., IntelliJ IDEA Community Edition)
        // bundledPlugin("com.intellij.java") // Example: Add dependency on Java plugin if needed
        // No, the plugin.xml already has <depends>com.intellij.modules.java</depends>
        // and the template from JetBrains for a new plugin does not add this by default here.
        // It's usually for depending on *other* plugins, not core modules like Java support.
    }
}

// Configure Java compilation options
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    // Set the JVM arguments for the `runIde` task.
    runIde {
        jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.base/java.io=ALL-UNNAMED")
        // Add other necessary --add-opens options if required during development
    }
}
