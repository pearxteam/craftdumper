import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.matthewprenger.cursegradle.*
import net.minecraftforge.gradle.user.UserBaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("net.minecraftforge.gradle.forge")
    id("com.matthewprenger.cursegradle")
    id("com.github.breadmoirai.github-release")
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
}

val modVersion: String by project
val modReleaseType: String by project
val modDescription: String by project
val modChangelog: String by project
val modDependencies: String by project
val modAcceptedMcVersions: String by project

val forgeVersion: String by project
val minecraftVersion: String by project
val mcpMappingsVersion: String by project

val jeiVersion: String by project
val jeiMcVersion: String by project
val forgelinVersion: String by project
val projectEMcVersion: String by project
val projectEVersion: String by project

val jdkVersion: String by project

val curseforgeProjectId: String by project

val githubReleaseBranch: String by project

val pearxRepoUsername: String? by project
val pearxRepoPassword: String? by project
val curseforgeApiKey: String? by project
val devBuildNumber: String? by project
val githubAccessToken: String? by project

version = if (devBuildNumber != null) "$modVersion-dev-$devBuildNumber" else modVersion
group = "net.pearx.craftdumper"
description = modDescription

java {
    sourceCompatibility = JavaVersion.toVersion(jdkVersion)
}

repositories {
    maven { url = uri("https://maven.shadowfacts.net/") } // Forgelin
    maven { url = uri("http://dvs1.progwml6.com/files/maven") } // JEI
    maven { url = uri("https://minecraft.curseforge.com/api/maven") } // ProjectE
}

dependencies {
    "runtime"("mezz.jei:jei_$jeiMcVersion:$jeiVersion")
    "compile"("net.shadowfacts:Forgelin:$forgelinVersion")
    "compile"("projecte:ProjectE:$projectEMcVersion:$projectEVersion")
}

configure<UserBaseExtension> {
    version = "$minecraftVersion-$forgeVersion"
    runDir = "run"
    mappings = mcpMappingsVersion
    replace("VERSION = \"\"", "VERSION = \"$modVersion\"")
    replace("DESCRIPTION = \"\"", "DESCRIPTION = \"$modDescription\"")
    replace("ACCEPTED_MINECRAFT_VERSIONS = \"\"", "ACCEPTED_MINECRAFT_VERSIONS = \"$modAcceptedMcVersions\"")
    replace("DEPENDENCIES = \"\"", "DEPENDENCIES = \"$modDependencies\"")
    replaceIn("Reference.kt")
}

publishing {
    repositories {
        fun AuthenticationSupported.pearxCredentials() {
            credentials {
                username = pearxRepoUsername
                password = pearxRepoPassword
            }
        }
        maven {
            pearxCredentials()
            name = "develop"
            url = uri("https://repo.pearx.net/maven2/develop/")
        }
        maven {
            pearxCredentials()
            name = "release"
            url = uri("https://repo.pearx.net/maven2/release/")
        }
    }

    publications {
        register<MavenPublication>("maven") {
            artifact(tasks.getByName<Jar>("jar"))
        }
    }
}

configure<CurseExtension> {
    apiKey = curseforgeApiKey ?: "0"
    project(closureOf<CurseProject> {
        id = curseforgeProjectId
        releaseType = "release"
        changelog = modChangelog
        relations(closureOf<CurseRelation> {
            requiredDependency("shadowfacts-forgelin")
        })
        mainArtifact(tasks.named("jar").get(), closureOf<CurseArtifact> {
            displayName = "[$minecraftVersion] CraftDumper $version"
        })
        options(closureOf<Options> {
            detectNewerJava = true
        })
        addGameVersion("Java 10") // hack
    })
}

configure<GithubReleaseExtension> {
    setToken(githubAccessToken)
    setOwner("pearxteam")
    setRepo("craftdumper")
    setTargetCommitish(githubReleaseBranch)
    setBody(modChangelog)
    setReleaseAssets((publishing.publications["maven"] as MavenPublication).artifacts.map { it.file })
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.$jdkVersion"
        kotlinOptions.freeCompilerArgs = listOf("-Xno-param-assertions")
    }
    register("publishDevelop") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["develop"] })
    }
    register("publishRelease") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["release"] })
        dependsOn(named("curseforge"))
        dependsOn(named("githubRelease"))
    }
}