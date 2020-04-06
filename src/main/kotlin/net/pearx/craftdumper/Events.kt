package net.pearx.craftdumper

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.pearx.craftdumper.common.command.CraftDumperCommand

@Mod.EventBusSubscriber(modid = ID)
object Events {
    @SubscribeEvent
    fun serverStarting(event: FMLServerStartingEvent) {
        CraftDumperCommand.register(event.commandDispatcher)
    }
}