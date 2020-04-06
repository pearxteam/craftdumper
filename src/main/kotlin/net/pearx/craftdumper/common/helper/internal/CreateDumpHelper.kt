@file:JvmMultifileClass
@file:JvmName("InternalHelper")

package net.pearx.craftdumper.common.helper.internal

import net.pearx.craftdumper.common.command.CraftDumperCommand
import net.pearx.craftdumper.common.command.DumpType
import net.pearx.craftdumper.common.dumper.Dumper

internal fun Collection<Pair<Dumper, DumpType>>.getTotalCount() = sumBy {
    var i = 0
    if(it.second.data)
        i += it.first.getCount()
    if(it.second.amounts)
        i += 1
    i
}