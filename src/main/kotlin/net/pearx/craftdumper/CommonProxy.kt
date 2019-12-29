package net.pearx.craftdumper

import net.minecraft.command.ICommandSender
import net.pearx.craftdumper.dumper.Dumper

interface CommonProxy {
    fun createDump(command: CraftDumperCommand, sender: ICommandSender, dumper: Dumper, type: CraftDumperCommand.DumpType)
}