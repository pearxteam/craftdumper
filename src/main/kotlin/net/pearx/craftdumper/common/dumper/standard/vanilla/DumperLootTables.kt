@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import net.minecraft.loot.LootTableManager
import net.minecraft.loot.LootTables
import net.minecraftforge.fml.server.ServerLifecycleHooks
import net.pearx.craftdumper.common.dumper.dsl.dumperFiles
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toAssetsPath

val DumperLootTables = dumperFiles {
    registryName = craftdumper("loot_tables")
    amounts { +getLootTables() }
    count { getLootTables().size }
    files {
        val manager = ServerLifecycleHooks.getCurrentServer().lootTableManager
        for (loc in getLootTables()) {
            fileDirect({ loc.toAssetsPath("loot_tables", ".json") }) { file ->
                val json = LootTableManager.toJson(manager.getLootTableFromLocation(loc))
                file.bufferedWriter().use { writer ->
                    val jsonWriter = JsonWriter(writer).apply {
                        isLenient = true
                        setIndent("  ")
                    }
                    jsonWriter.isLenient = true
                    Streams.write(json, jsonWriter)
                }
            }
        }
    }
}

private fun getLootTables() = LootTables.getReadOnlyLootTables()