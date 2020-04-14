package net.pearx.craftdumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.pearx.craftdumper.client.ClientProxy
import net.pearx.craftdumper.common.CommonProxy
import net.pearx.craftdumper.common.command.DumpTypeArgument
import net.pearx.craftdumper.common.command.DumperArgument
import net.pearx.craftdumper.common.dumper.DumperRegistry
import net.pearx.craftdumper.common.helper.internal.currentDateTime
import net.pearx.craftdumper.common.helper.internal.getRegistryElementName
import net.pearx.craftdumper.common.network.initNetwork
import net.pearx.craftdumper.server.ServerProxy
import org.apache.logging.log4j.LogManager
import java.util.function.Supplier

@Mod("craftdumper")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object CraftDumper {
    val log = LogManager.getLogger()

    val proxy: CommonProxy = DistExecutor.runForDist<CommonProxy>({ Supplier { ClientProxy() } }, { Supplier { ServerProxy() } })

    fun getOutputFile(prefix: String, postfix: String = "") = proxy.outputDirectory.resolve("${prefix}_${currentDateTime()}$postfix")

    fun getOutputFile(registryName: ResourceLocation, postfix: String = "") = getOutputFile(DumperRegistry.getRegistryElementName(registryName), postfix)

    @SubscribeEvent
    fun init(event: FMLCommonSetupEvent) {
        initNetwork()
        DumperArgument.register()
        DumpTypeArgument.register()
    }
}