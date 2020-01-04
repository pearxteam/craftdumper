package net.pearx.craftdumper.common

import net.minecraft.command.ICommandSender
import net.minecraft.world.World
import net.pearx.craftdumper.common.dumper.Dumper

interface CommonProxy {
    fun createDump(command: CraftDumperCommand, sender: ICommandSender, toDump: List<Pair<Dumper, CraftDumperCommand.DumpType>>)

    val world: World
}