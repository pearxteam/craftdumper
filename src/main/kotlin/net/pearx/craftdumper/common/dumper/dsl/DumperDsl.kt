package net.pearx.craftdumper.common.dumper.dsl

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistryEntry
import net.pearx.craftdumper.common.dumper.DumpAmounts
import net.pearx.craftdumper.common.dumper.Dumper

typealias DumpAmountsCreator = DumpAmounts.() -> Unit
typealias DumpCountCreator = () -> Int

@DslMarker
annotation class DumperDsl

@DumperDsl
abstract class DumperContext {
    var registryName: ResourceLocation? = null
    var translationKey: String? = null
    protected var amountsCreator: DumpAmountsCreator? = null
    protected var countCreator: DumpCountCreator? = null

    fun amounts(block: DumpAmountsCreator) {
        amountsCreator = block
    }

    fun count(block: DumpCountCreator) {
        countCreator = block
    }
}

abstract class DumperImplementation(
    _registryName: ResourceLocation?,
    private val _translationKey: String?,
    private val amountsCreator: DumpAmountsCreator?,
    private val countCreator: DumpCountCreator?
) : ForgeRegistryEntry<Dumper>(), Dumper {
    init {
        registryName = _registryName
    }

    override val translationKey: String
        get() = _translationKey ?: registryName?.path.toString()

    override fun getAmounts(): DumpAmounts? = if (amountsCreator == null) null else DumpAmounts().also { amountsCreator.invoke(it) }

    override fun getCount(): Int = countCreator?.invoke() ?: 0
}