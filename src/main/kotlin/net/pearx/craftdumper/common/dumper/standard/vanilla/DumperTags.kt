@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.tags.*
import net.minecraftforge.registries.IForgeRegistryEntry
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperItemTags = dumperTags<Item>({ ItemTags.getCollection() }, "item", "Items")
val DumperBlockTags = dumperTags<Block>({ BlockTags.getCollection() }, "block", "Blocks")
val DumperFluidTags = dumperTags<Fluid>({ FluidTags.getCollection() }, "fluid", "Fluids")
val DumperEntityTags = dumperTags<EntityType<*>>({ EntityTypeTags.getCollection() }, "entity", "Entities")



private inline fun <T : IForgeRegistryEntry<T>> dumperTags(crossinline collection: () -> ITagCollection<T>, name: String, columnName: String) = dumperTable {
    registryName = craftdumper("tags_${name}")
    header = listOf("Tag", columnName)
    amounts { collection().idTagMap.forEach { (id, tag) -> id += tag.allElements.size } }
    count { collection().idTagMap.size }
    table {
        data {
            collection().idTagMap.forEach { (id, tag) ->
                row {
                    add { id.toString() }
                    add { tag.allElements.joinToString(System.lineSeparator()) { it.registryName.toString() } }
                }
            }
        }
    }
}