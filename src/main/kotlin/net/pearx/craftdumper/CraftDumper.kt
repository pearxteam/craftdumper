package net.pearx.craftdumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.pearx.craftdumper.dumper.DumperRegistry
import net.pearx.craftdumper.helper.internal.currentDateTime
import net.pearx.craftdumper.helper.internal.getRegistryElementName
import org.apache.logging.log4j.Logger
import java.io.File

@Mod(modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    name = NAME,
    modid = ID,
    version = VERSION,
    acceptedMinecraftVersions = ACCEPTED_MINECRAFT_VERSIONS,
    dependencies = DEPENDENCIES,
    acceptableRemoteVersions = "*")
object CraftDumper {
    lateinit var log: Logger private set
    lateinit var outputDirectory: File private set

    fun getOutputFile(prefix: String, postfix: String = "") = outputDirectory.resolve("${prefix}_${currentDateTime()}$postfix")

    fun getOutputFile(registryName: ResourceLocation, postfix: String = "") = getOutputFile(DumperRegistry.getRegistryElementName(registryName), postfix)

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        outputDirectory = event.modConfigurationDirectory.parentFile.resolve(ID)
        log = event.modLog
        with(event.modMetadata) {
            autogenerated = false
            modId = ID
            name = NAME
            description = DESCRIPTION
            version = VERSION
            authorList = AUTHORS
        }
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        log.info("Registering '/craftdumper' command")
        event.registerServerCommand(CraftDumperCommand())
    }
}