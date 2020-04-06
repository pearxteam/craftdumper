@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import com.google.gson.GsonBuilder
import net.minecraft.world.storage.loot.*
import net.minecraft.world.storage.loot.conditions.LootConditionManager
import net.minecraft.world.storage.loot.functions.LootFunctionManager
import net.pearx.craftdumper.common.dumper.dumperFiles
import net.pearx.craftdumper.common.dumper.file
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toAssetsPath

val DumperLootTables = dumperFiles {
    registryName = craftdumper("loot_tables")
    amounts { +getLootTables() }
    count { getLootTables().size  }
    files {
        val manager = LootTableManager()
        for (loc in getLootTables()) {
            file({ loc.toAssetsPath("loot_tables", ".json") }) {
                LootTableManager.toJson(manager.getLootTableFromLocation(loc)).toString().byteInputStream()
            }
        }
    }
}

private fun getLootTables() = LootTables.func_215796_a()