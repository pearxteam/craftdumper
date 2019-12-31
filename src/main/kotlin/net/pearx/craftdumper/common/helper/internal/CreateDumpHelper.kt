package net.pearx.craftdumper.common.helper.internal

import net.pearx.craftdumper.common.CraftDumperCommand
import net.pearx.craftdumper.common.dumper.Dumper

internal fun List<Pair<Dumper, CraftDumperCommand.DumpType>>.getTotalCount() = sumBy {
    var i = 0
    if(it.second.data)
        i += it.first.getCount()
    if(it.second.amounts)
        i += 1
    i
}