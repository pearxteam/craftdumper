@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ModelManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.data.EmptyModelData
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTableClient
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.readField
import net.pearx.craftdumper.common.helper.toPlusMinusString
import net.pearx.craftdumper.common.helper.toTexturesPath
import java.util.*

val DumperModels = dumperTableClient {
    registryName = craftdumper("models")
    header = listOf("Variant", "Particle Texture", "Model Textures", "Quad Count", "Class Name", "Is Ambient Occlusion", "Is GUI 3D", "Is Built In Renderer", "Overrides")
    amounts { +Minecraft.getInstance().modelManager.modelRegistry.keys }
    count { Minecraft.getInstance().modelManager.modelRegistry.count() }
    table {
        Minecraft.getInstance().modelManager.modelRegistry.keys.forEach { key ->
            val model = Minecraft.getInstance().modelManager.modelRegistry[key]!!
            row(header.size) {
                add { key.toString() }
                with(model) {
                    val quads = getQuads(null, null, Random(0), EmptyModelData.INSTANCE)
                    add {
                        getParticleTexture(EmptyModelData.INSTANCE)?.name?.toTexturesPath().orEmpty()
                    }
                    add {
                        val textures = mutableListOf<TextureAtlasSprite>()
                        for (quad in quads) {
                            if (quad.func_187508_a() !in textures)
                                textures.add(quad.func_187508_a())
                        }
                        textures.joinToString(separator = System.lineSeparator()) { it.name.toTexturesPath() }
                    }
                    add { quads.size.toString() }
                    add { this::class.java.name }
                    add { isAmbientOcclusion.toPlusMinusString() }
                    add { isGui3d.toPlusMinusString() }
                    add { isBuiltInRenderer.toPlusMinusString() }
                    add { overrides.overrides.joinToString(System.lineSeparator()) {
                        it.location.toString()
                    } }
                }
            }
        }
    }
}


private val ModelManager.modelRegistry
    @OnlyIn(Dist.CLIENT)
    get() = readField<Map<ResourceLocation, IBakedModel>>("field_174958_a", "modelRegistry")