@file:Mod.EventBusSubscriber(modid = ID, bus = Mod.EventBusSubscriber.Bus.MOD)

package net.pearx.craftdumper.common.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import net.pearx.craftdumper.ID
import net.pearx.craftdumper.common.dumper.standard.vanilla.*
import net.pearx.craftdumper.common.helper.internal.getRegistryElementNames
import net.pearx.craftdumper.common.helper.internal.lookupRegistryElements
import net.pearx.craftdumper.common.helper.registerNonNull

lateinit var DumperRegistry: IForgeRegistry<Dumper> private set

fun lookupDumperRegistry(name: String) = DumperRegistry.lookupRegistryElements(name)

fun getDumperNames(): List<String> = DumperRegistry.getRegistryElementNames()


@SubscribeEvent
fun onNewRegistry(event: RegistryEvent.NewRegistry) {
    DumperRegistry = RegistryBuilder<Dumper>().setName(ResourceLocation(ID, "dumpers")).setType(Dumper::class.java).disableSaving().create()
}

@SubscribeEvent
fun onRegisterDumpers(event: RegistryEvent.Register<Dumper>) {
    with(event.registry) {
        register(DumperAdvancements)
        register(DumperArmor)
        register(DumperAttributes)
        register(DumperAxes)
        register(DumperBiomes)
        register(DumperBlocks)
        register(DumperCapabilities)
        register(DumperEnchantments)
        register(DumperEntities)
        register(DumperFluids)
        register(DumperFood)
        register(DumperHoes)
        register(DumperItemStacks)
        register(DumperLootTables)
        register(DumperItemTags)
        register(DumperBlockTags)
        register(DumperFluidTags)
        register(DumperEntityTags)
        registerNonNull(DumperModels)
        register(DumperPotionEffects)
        register(DumperPotions)
        register(DumperContainers)
        register(DumperPaintings)
        register(DumperParticles)
        register(DumperPickaxes)
        register(DumperSmeltingRecipes)
        register(DumperBlastingRecipes)
        register(DumperSmokingRecipes)
        register(DumperCampfireCookingRecipes)
        register(DumperStonecuttingRecipes)
        register(DumperShapelessRecipes)
        register(DumperShapedRecipes)
        register(DumperShovels)
        register(DumperSoundEvents)
        register(DumperSwords)
        register(DumperTileEntities)
        register(DumperVillagerProfessions)
        register(DumperVillagerPointsOfInterest)
        register(DumperVillagerMemoryModules)
        register(DumperVillagerSensors)
        register(DumperVillagerSchedules)
        register(DumperVillagerActivities)
        register(DumperStats)
        register(DumperDimensionTypes)
    }
}