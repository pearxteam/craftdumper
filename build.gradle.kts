import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.matthewprenger.cursegradle.*
import net.minecraftforge.gradle.common.util.MinecraftExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.OffsetDateTime

plugins {
    id("net.minecraftforge.gradle")
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
val mcpMappingsChannel: String by project
val mcpMappingsVersion: String by project

val jeiVersion: String by project
val jeiMcVersion: String by project
val kotlinForForgeVersion: String by project
val projectEFileId: String by project

val kotlinxCoroutinesVersion: String by project

val jdkVersion: String by project

val curseforgeProjectId: String by project
val curseforgeReleaseType: String by project

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
    maven { url = uri("https://files.minecraftforge.net/maven") }
    maven { url = uri("https://www.cursemaven.com") }
    maven { url = uri("https://dvs1.progwml6.com/files/maven") } // JEI
    maven { url = uri("https://thedarkcolour.github.io/KotlinForForge/") } // Kotlin for Forge
}

dependencies {
    "minecraft"("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    "implementation"(kotlin("stdlib-jdk$jdkVersion"))
    "implementation"(kotlin("reflect"))
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk$jdkVersion:$kotlinxCoroutinesVersion")

    "implementation"("thedarkcolour:kotlinforforge:$kotlinForForgeVersion")
    "runtimeOnly"(fg.deobf("mezz.jei:jei-$jeiMcVersion:$jeiVersion"))
    "implementation"(fg.deobf("curse.maven:projecte-226410:$projectEFileId"))
}

configure<MinecraftExtension> {
    mappings(mcpMappingsChannel, mcpMappingsVersion)
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        create("client")
        create("server")
        create("data") {
            args("--mod", "craftdumper", "-all", "--output", file("src/generated/resources"))
        }

        configureEach {
            workingDirectory(project.file("run/$name"))
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")

            mods {
                create("craftdumper") {
                    source(sourceSets["main"])
                }
            }
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(sourcesJar)
}

publishing {
    repositories {
        maven {
            credentials {
                username = "pearxteam"
                password = githubAccessToken
            }
            name = "github"
            url = uri("https://maven.pkg.github.com/pearxteam/craftdumper")
        }
    }

    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

configure<CurseExtension> {
    apiKey = curseforgeApiKey ?: "0"
    project(closureOf<CurseProject> {
        id = curseforgeProjectId
        releaseType = curseforgeReleaseType
        changelog = modChangelog
        relations(closureOf<CurseRelation> {
            requiredDependency("kotlin-for-forge")
        })

        val arts = (publishing.publications["maven"] as MavenPublication).artifacts
        val mainArt = arts.first { it.classifier == null }
        val additionalArts = arts.filter { it.classifier != null }
        mainArtifact(mainArt.file, closureOf<CurseArtifact> {
            displayName = "[$minecraftVersion] CraftDumper $version"
        })
        additionalArts.forEach { addArtifact(it.file) }

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
    jar {
        finalizedBy("reobfJar")
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.$jdkVersion"
        kotlinOptions.freeCompilerArgs = listOf("-Xno-param-assertions")
    }
    withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to "CraftDumper",
                    "Specification-Vendor" to "PearX Team",
                    "Specification-Version" to modVersion,
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "PearX Team",
                    "Implementation-Timestamp" to OffsetDateTime.now().toString()
                )
            )
        }
    }
    register("publishDevelop") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["github"] })
    }
    register("publishRelease") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["github"] })
        dependsOn(named("curseforge"))
        dependsOn(named("githubRelease"))
    }
}