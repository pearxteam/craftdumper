@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperAttributes = dumperTable {
    registryName = craftdumper("attributes")
    header = listOf("Name", "Translation Key", "Display Name", "Default Value", "Parent", "Class Name", "Should Watch")
    count { getAttributes().count() }
    table {
        getAttributes().forEach { attribute ->
            row(header.size) {
                add { attribute.name }
                add { "attribute.name.${attribute.name}" }
                add { I18n.translateToLocalFormatted("attribute.name.${attribute.name}") }
                add { attribute.defaultValue.toString() }
                add { attribute.parent?.name.orEmpty() }
                add { attribute::class.java.name }
                add { attribute.shouldWatch.toPlusMinusString() }
            }
        }
    }
}

private fun getAttributes(): Set<IAttribute> {
    val attributes = hashSetOf<IAttribute>()
    ForgeRegistries.ENTITIES.forEach { entityEntry ->
        val entity = entityEntry.newInstance(CraftDumper.proxy.world)
        if(entity is EntityLiving) {
            entity.attributeMap.allAttributes.forEach { attrInstance ->
                attributes += attrInstance.attribute
            }
        }
    }
    return attributes
}