@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.dumper.standard.vanilla

import com.google.gson.GsonBuilder
import net.minecraft.world.storage.loot.*
import net.minecraft.world.storage.loot.conditions.LootCondition
import net.minecraft.world.storage.loot.conditions.LootConditionManager
import net.minecraft.world.storage.loot.functions.LootFunction
import net.minecraft.world.storage.loot.functions.LootFunctionManager
import net.pearx.craftdumper.dumper.dumperFiles
import net.pearx.craftdumper.dumper.file
import net.pearx.craftdumper.helper.internal.craftdumper
import net.pearx.craftdumper.helper.toAssetsPath

val DumperLootTables = dumperFiles {
    registryName = craftdumper("loot_tables")
    amounts {
        for (table in LootTableList.getAll())
            this += table
    }
    count { LootTableList.getAll().size  }
    files {
        val manager = LootTableManager(null)
        val gs = GsonBuilder().registerTypeAdapter(RandomValueRange::class.java, RandomValueRange.Serializer()).registerTypeAdapter(LootPool::class.java, LootPool.Serializer()).registerTypeAdapter(LootTable::class.java, LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry::class.java, LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction::class.java, LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition::class.java, LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget::class.java, LootContext.EntityTarget.Serializer()).setPrettyPrinting().create()
        for (loc in LootTableList.getAll()) {
            file({ loc.toAssetsPath("loot_tables", ".json") }) {
                gs.toJson(manager.getLootTableFromLocation(loc)).toByteArray().inputStream()
            }
        }
    }
}