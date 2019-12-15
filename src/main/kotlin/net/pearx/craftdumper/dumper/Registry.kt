package net.pearx.craftdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import net.pearx.craftdumper.ID
import net.pearx.craftdumper.helper.getRegistryElementNames
import net.pearx.craftdumper.helper.lookupRegistryElements

lateinit var DumperRegistry: IForgeRegistry<Dumper> private set

fun lookupDumperRegistry(name: String) = lookupRegistryElements(DumperRegistry, name)

fun getDumperNames(): List<String> = getRegistryElementNames(DumperRegistry)

@Mod.EventBusSubscriber(modid = ID)
object Events {
    @SubscribeEvent
    fun onNewRegistry(event: RegistryEvent.NewRegistry) {
        DumperRegistry = RegistryBuilder<Dumper>().setName(ResourceLocation(ID, "dumpers")).setType(Dumper::class.java).disableSaving().create()
    }

    @SubscribeEvent
    fun onRegisterDumpers(event: RegistryEvent.Register<Dumper>) {
        with(event.registry) {

        }
    }
}