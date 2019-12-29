@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import com.google.common.collect.ImmutableMap
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.model.MultipartBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTableClient
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.readField
import net.pearx.craftdumper.common.helper.toAssetsPath
import net.pearx.craftdumper.common.helper.toPlusMinusString
import net.pearx.craftdumper.common.helper.toTexturesPath

val DumperModels = dumperTableClient {
    registryName = craftdumper("models")
    header = listOf("Variant", "Particle Texture", "Model Textures", "Model Path", "Class Name", "Is Ambient Occlusion", "Is GUI 3D", "Is Built In Renderer")
    amounts { this += Minecraft.getMinecraft().modelManager.modelRegistry.keys }
    count { Minecraft.getMinecraft().modelManager.modelRegistry.count() }
    table {
        Minecraft.getMinecraft().modelManager.modelRegistry.keys.forEach { key ->
            val model = Minecraft.getMinecraft().modelManager.modelRegistry.getObject(key)!!
            row(header.size) {
                add { key.toString() }
                with(model) {
                    add {
                        @Suppress("UNNECESSARY_SAFE_CALL")
                        particleTexture?.let { ResourceLocation(it.iconName) }?.toTexturesPath().orEmpty()
                    }
                    add {
                        val textures = mutableListOf<TextureAtlasSprite>()
                        for (quad in getQuads(null, null, 0)) {
                            if (quad.sprite !in textures)
                                textures.add(quad.sprite)
                        }
                        textures.joinToString(separator = System.lineSeparator()) { ResourceLocation(it.iconName).toTexturesPath() }
                    }
                    add { getModelPath(key) }
                    add { this::class.java.name }
                    add { isAmbientOcclusion.toPlusMinusString() }
                    add { isGui3d.toPlusMinusString() }
                    add { isBuiltInRenderer.toPlusMinusString() }
                }
            }
        }
    }
}

@SideOnly(Side.CLIENT)
private fun IBakedModel.getModelPath(location: ModelResourceLocation): String {
    return when (this::class.java.name) {
        "net.minecraftforge.client.model.ModelLoader\$VanillaModelWrapper\$1" -> this.readField<Any>("this$1").readField<ResourceLocation>("location").toAssetsPath(postfix = ".json")
        "net.minecraft.client.renderer.block.model.MultipartBakedModel" -> {
            val lst = mutableListOf<String>()
            for (subModel in (this as MultipartBakedModel).selectors.values) {
                val subPath = subModel.getModelPath(location)
                if (subPath !in lst)
                    lst.add(subPath)
            }
            lst.joinToString(separator = System.lineSeparator())
        }
        "net.minecraftforge.client.model.MultiModel\$Baked" -> {
            val lst = mutableListOf<String>()
            lst.add(readField<ResourceLocation>("location").toAssetsPath(postfix = ".json"))
            for (subModel in readField<ImmutableMap<String, IBakedModel>>("parts").values) {
                val subPath = subModel.getModelPath(location)
                if (subPath !in lst)
                    lst.add(subPath)
            }
            lst.joinToString(separator = System.lineSeparator())
        }
        "net.minecraft.client.renderer.block.model.WeightedBakedModel" -> {
            val lst = mutableListOf<String>()
            for (model in readField<List<Any>>("models", "field_177565_b")) {
                val path = model.readField<IBakedModel>("model", "field_185281_b").getModelPath(location)
                if (path !in lst)
                    lst.add(path)
            }
            lst.joinToString(separator = System.lineSeparator())
        }
        "net.minecraftforge.client.model.BakedItemModel" -> location.toAssetsPath("models/item", ".json")
        else -> ""
    }
}