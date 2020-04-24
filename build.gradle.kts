import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.matthewprenger.cursegradle.*
import net.minecraftforge.gradle.common.util.MinecraftExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.OffsetDateTime

plugins {
    id("net.minecraftforge.gradle")
    id("com.matthewprenger.cursegradle")
    id("com.wynprice.cursemaven")
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
val mcpMappingsMinecraftVersion: String by project
val mcpMappingsVersion: String by project

val jeiVersion: String by project
val jeiMcVersion: String by project
val kottleFileId: String by project
val projectEFileId: String by project

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
    maven { url = uri("https://dvs1.progwml6.com/files/maven") } // JEI
}

dependencies {
    "minecraft"("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    "runtimeOnly"(fg.deobf("mezz.jei:jei-$jeiMcVersion:$jeiVersion"))
    "compile"(fg.deobf("curse.maven:kottle:$kottleFileId"))
    "compile"(fg.deobf("curse.maven:projecte:$projectEFileId"))
}

configure<MinecraftExtension> {
    mappings(mcpMappingsChannel, "$mcpMappingsVersion-$mcpMappingsMinecraftVersion")
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

//configure<UserBaseExtension> {
//    version = "$minecraftVersion-$forgeVersion"
//    runDir = "run"
//    mappings = mcpMappingsVersion
//    replace("VERSION = \"\"", "VERSION = \"$modVersion\"")
//    replace("DESCRIPTION = \"\"", "DESCRIPTION = \"$modDescription\"")
//    replace("ACCEPTED_MINECRAFT_VERSIONS = \"\"", "ACCEPTED_MINECRAFT_VERSIONS = \"$modAcceptedMcVersions\"")
//    replace("DEPENDENCIES = \"\"", "DEPENDENCIES = \"$modDependencies\"")
//    replaceIn("Reference.kt")
//}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(sourcesJar)
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
            requiredDependency("kottle")
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
    withType<Jar> {
        manifest {
            attributes(mapOf(
                "Specification-Title" to "CraftDumper",
                "Specification-Vendor" to "PearX Team",
                "Specification-Version" to modVersion,
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "PearX Team",
                "Implementation-Timestamp" to OffsetDateTime.now().toString()
            ))
        }
    }
    register("publishDevelop") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["develop"] })
    }
    register("publishRelease") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["release"] })
//        dependsOn(named("curseforge"))
        // Line above is commented because
        dependsOn(named("githubRelease"))
    }
}