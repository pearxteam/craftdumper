@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import com.mojang.authlib.GameProfile
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.defaultWorld
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString
import java.util.*

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
    addAttributes(attributes, FakePlayer(defaultWorld, GameProfile(UUID(0, 0), "craftdumper")))
    ForgeRegistries.ENTITIES.forEach { entityEntry ->
        val entity = entityEntry.newInstance(defaultWorld)
        if (entity is EntityLivingBase) {
            addAttributes(attributes, entity)
        }
    }
    return attributes
}

private fun <T : EntityLivingBase> addAttributes(attributes: MutableSet<IAttribute>, entity: T) {
    entity.attributeMap.allAttributes.forEach { attrInstance ->
        attributes += attrInstance.attribute
    }
}