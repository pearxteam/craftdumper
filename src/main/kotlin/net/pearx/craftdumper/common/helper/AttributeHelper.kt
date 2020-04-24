package net.pearx.craftdumper.common.helper

import com.mojang.authlib.GameProfile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.registries.ForgeRegistries
import java.util.*

val IAttribute.translationKey
    get() = "attribute.name.$name"
val IAttribute.displayName: String
    get() = if (isClient) I18n.format(translationKey) else name

fun getAttributes(includeFakePlayer: Boolean): Set<IAttribute> {
    val attributes = hashSetOf<IAttribute>()
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

private fun <T : LivingEntity> addAttributes(attributes: MutableSet<IAttribute>, entity: T) {
    entity.attributes.allAttributes.forEach { attrInstance ->
        attributes += attrInstance.attribute
    }
}