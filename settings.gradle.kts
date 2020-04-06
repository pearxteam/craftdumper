val minecraftVersion: String by settings
rootProject.name = "craftdumper-$minecraftVersion"

pluginManagement {
    val forgeGradleVersion: String by settings
    val curseGradleVersion: String by settings
    val curseMavenVersion: String by settings
    val githubReleaseVersion: String by settings
    val kotlinVersion: String by settings

    val moduleMappings = mapOf(
        "net.minecraftforge.gradle" to "net.minecraftforge.gradle:ForgeGradle"
    )

    val versionMappings = mapOf(
        "net.minecraftforge.gradle" to forgeGradleVersion,
        "com.matthewprenger.cursegradle" to curseGradleVersion,
        "com.wynprice.cursemaven" to curseMavenVersion,
        "com.github.breadmoirai.github-release" to githubReleaseVersion,
        "org.jetbrains.kotlin.jvm" to kotlinVersion
    )

    repositories {
        gradlePluginPortal()
        maven { url = uri("http://files.minecraftforge.net/maven") }
    }

    resolutionStrategy {
        eachPlugin {
            val id = requested.id.id
            for((requested, target) in moduleMappings) {
                if(id == requested) {
                    useModule("$target:${versionMappings[requested]}")
                    return@eachPlugin
                }
            }

            for((mappingId, mappingVersion) in versionMappings) {
                if(id == mappingId)
                    useVersion(mappingVersion)
            }
        }
    }
}