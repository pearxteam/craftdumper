package net.pearx.craftdumper.common

import net.minecraft.command.ICommandSender
import net.pearx.craftdumper.common.dumper.Dumper

interface CommonProxy {
    fun createDump(command: CraftDumperCommand, sender: ICommandSender, dumper: Dumper, type: CraftDumperCommand.DumpType)
}