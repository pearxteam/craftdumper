@file:Mod.EventBusSubscriber(modid = ID)

package net.pearx.craftdumper

import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.pearx.craftdumper.common.command.CraftDumperCommand

@SubscribeEvent
fun serverStarting(event: RegisterCommandsEvent) {
    CraftDumperCommand.register(event.dispatcher)
}