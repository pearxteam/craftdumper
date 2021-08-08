package net.pearx.craftdumper.common.helper

import com.mojang.authlib.GameProfile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.attributes.Attribute
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.registries.ForgeRegistries
import java.util.*

val Attribute.displayName: String
    get() = if (isClient) I18n.format(attributeName) else attributeName

fun getAttributes(includeFakePlayer: Boolean): Set<Attribute> {
    val attributes = hashSetOf<Attribute>()
    if (includeFakePlayer)
        addAttributes(attributes, FakePlayer(defaultWorld, GameProfile(UUID(0, 0), "craftdumper")))
    ForgeRegistries.ENTITIES.forEach { entityEntry ->
        val entity = entityEntry.create(defaultWorld)
        if (entity is LivingEntity) {
            addAttributes(attributes, entity)
        }
    }
    return attributes
}

private fun <T : LivingEntity> addAttributes(attributes: MutableSet<Attribute>, entity: T) {
    entity.attributeManager.instances.forEach { attrInstance ->
        attributes += attrInstance.attribute
    }
}