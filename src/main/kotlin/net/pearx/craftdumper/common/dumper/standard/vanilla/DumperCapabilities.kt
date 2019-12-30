@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.readField
import java.util.*

val DumperCapabilities = dumperTable {
    registryName = craftdumper("capabilities")
    header = listOf("Interface", "Default Instance Class", "Storage Class")
    count { CapabilityManager.INSTANCE.readField<IdentityHashMap<String, Capability<*>>>("providers").size }
    table {
        CapabilityManager.INSTANCE.readField<IdentityHashMap<String, Capability<*>>>("providers").forEach { cap ->
            row(header.size) {
                with(cap) {
                    add { key }
                    add {
                        val defaultInstance = value.defaultInstance
                        if (defaultInstance == null) "" else defaultInstance::class.java.name
                    }
                    add { value.storage::class.java.name }
                }
            }
        }
    }
}