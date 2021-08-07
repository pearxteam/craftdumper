val minecraftVersion: String by settings
rootProject.name = "craftdumper-$minecraftVersion"

pluginManagement {
    val forgeGradleVersion: String by settings
    val curseGradleVersion: String by settings
    val githubReleaseVersion: String by settings
    val kotlinVersion: String by settings

    repositories {
        gradlePluginPortal()
        maven { url = uri("https://files.minecraftforge.net/maven") }
    }

    plugins {
        id("com.matthewprenger.cursegradle") version curseGradleVersion
        id("com.github.breadmoirai.github-release") version githubReleaseVersion
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("net.minecraftforge.gradle:ForgeGradle:${forgeGradleVersion}")
            }
        }
    }
}