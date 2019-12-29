package net.pearx.craftdumper.common.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import net.pearx.craftdumper.ID
import net.pearx.craftdumper.common.dumper.standard.vanilla.DumperLootTables
import net.pearx.craftdumper.common.dumper.standard.vanilla.DumperSoundEvents
import net.pearx.craftdumper.common.dumper.standard.vanilla.DumperTileEntities
import net.pearx.craftdumper.common.dumper.standard.vanilla.DumperVillagerProfessions
import net.pearx.craftdumper.common.helper.internal.getRegistryElementNames
import net.pearx.craftdumper.common.helper.internal.lookupRegistryElements

lateinit var DumperRegistry: IForgeRegistry<Dumper> private set

fun lookupDumperRegistry(name: String) = DumperRegistry.lookupRegistryElements(name)

fun getDumperNames(): List<String> = DumperRegistry.getRegistryElementNames()

@Mod.EventBusSubscriber(modid = ID)
object DumperRegistryEvents {
    @SubscribeEvent
    @JvmStatic
    fun onNewRegistry(event: RegistryEvent.NewRegistry) {
        DumperRegistry = RegistryBuilder<Dumper>().setName(ResourceLocation(ID, "dumpers")).setType(Dumper::class.java).disableSaving().create()
    }

    @SubscribeEvent
    @JvmStatic
    fun onRegisterDumpers(event: RegistryEvent.Register<Dumper>) {
        with(event.registry) {
            register(DumperLootTables)
            register(DumperSoundEvents)
            register(DumperTileEntities)
            register(DumperVillagerProfessions)
        }
    }
}