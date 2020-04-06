package net.pearx.craftdumper.common

import net.minecraft.command.CommandSource
import net.minecraft.util.ResourceLocation
import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.command.CraftDumperCommand
import net.pearx.craftdumper.common.command.DumpType
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.dumper.DumperRegistry
import net.pearx.craftdumper.common.helper.internal.currentDateTime
import net.pearx.craftdumper.common.helper.internal.getRegistryElementName
import java.io.File

interface CommonProxy {
    fun createDump(source: CommandSource, toDump: List<Pair<Dumper, DumpType>>)

    val outputDirectory: File


}