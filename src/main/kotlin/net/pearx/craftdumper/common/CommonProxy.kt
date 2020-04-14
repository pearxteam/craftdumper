package net.pearx.craftdumper.common

import net.minecraft.command.CommandSource
import net.pearx.craftdumper.common.command.DumpType
import net.pearx.craftdumper.common.dumper.Dumper
import java.io.File

interface CommonProxy {
    fun createDump(source: CommandSource, toDump: List<Pair<Dumper, DumpType>>)

    val outputDirectory: File


}