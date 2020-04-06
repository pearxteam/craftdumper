//@file:JvmMultifileClass
//@file:JvmName("VanillaDumpers")
//
//package net.pearx.craftdumper.common.dumper.standard.vanilla
//
//import net.minecraftforge.common.capabilities.Capability
//import net.minecraftforge.common.capabilities.CapabilityManager
//import net.pearx.craftdumper.common.dumper.add
//import net.pearx.craftdumper.common.dumper.dumperTable
//import net.pearx.craftdumper.common.dumper.row
//import net.pearx.craftdumper.common.helper.getOwnerModId
//import net.pearx.craftdumper.common.helper.internal.craftdumper
//import net.pearx.craftdumper.common.helper.readField
//import java.util.*
//
//val DumperCapabilities = dumperTable {
//    registryName = craftdumper("capabilities")
//    header = listOf("Mod ID", "Interface", "Default Instance Class", "Storage Class")
//    amounts { providers().values.forEach { +it.storage::class.getOwnerModId() } }
//    count { providers().size }
//    table {
//        providers().forEach { (key, value) ->
//            row(header.size) {
//                add { value.storage::class.getOwnerModId() }
//                add { key }
//                add {
//                    val defaultInstance = value.defaultInstance
//                    if (defaultInstance == null) "" else defaultInstance::class.java.name
//                }
//                add { value.storage::class.java.name }
//            }
//        }
//    }
//}
//
//private fun providers() = CapabilityManager.INSTANCE.readField<IdentityHashMap<String, Capability<*>>>("providers")