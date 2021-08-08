@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.entity.LivingEntity
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.defaultWorld
import net.pearx.craftdumper.common.helper.displayName
import net.pearx.craftdumper.common.helper.getAttributes
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperEntities = dumperTable {
    registryName = craftdumper("entities")
    header {
        listOf(
            "ID", "Name", "Class Name", "Classification", "Is Serializable", "Is Summonable", "Is Immune to Fire", "Can Spawn Far from Player", "Loot Table", "Width", "Height", "Size Fixed", "Tracking Range", "Update Frequency", "Should Send Velocity Updates", "Tags",
            *getAttributes(false).map { it.displayName }.toTypedArray()
        )
    }
    amounts { +ForgeRegistries.ENTITIES.keys }
    count { ForgeRegistries.ENTITIES.count() }
    table {
        data {
            val attrs = getAttributes(false)
            ForgeRegistries.ENTITIES.forEach { entity ->
                row {
                    with(entity) {
                        add { registryName.toString() }
                        add { name.unformattedComponentText }
                        add { create(defaultWorld)?.let { it::class.java.name } ?: "<unknown>" }
                        add { classification.getName() }
                        add { isSerializable.toPlusMinusString() }
                        add { isSummonable.toPlusMinusString() }
                        add { isImmuneToFire.toPlusMinusString() }
                        add { func_225437_d().toPlusMinusString() }
                        add { lootTable.toString() }
                        add { width.toString() }
                        add { height.toString() }
                        add { size.fixed.toPlusMinusString() }
                        add { func_233602_m_().toString() } //trackingRange
                        add { if (updateFrequency == Int.MAX_VALUE) "" else updateFrequency.toString() }
                        add { shouldSendVelocityUpdates().toPlusMinusString() }
                        add { tags.joinToString(System.lineSeparator()) { it.toString() } }
                        when (val instance = create(defaultWorld)) {
                            is LivingEntity -> {
                                for (attr in attrs) {
                                    add {
                                        instance.getAttribute(attr)?.value?.toString().orEmpty()
                                    }
                                }
                            }
                            else -> repeat(attrs.size) { add("") }
                        }
                    }
                }
            }
        }
    }
}