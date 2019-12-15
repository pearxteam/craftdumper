package net.pearx.craftdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

typealias DumpAmountsCreator = DumpAmounts.() -> Unit

class DumpAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")

    fun sort(): List<Pair<String, Int>> = toList().sortedByDescending { (_, v) -> v }
}

interface Dumper : IForgeRegistryEntry<Dumper> {
    override fun getRegistryType(): Class<Dumper> = Dumper::class.java

    fun dumpAmounts(): DumpAmounts?

    fun dumpContents()
}

abstract class DumperBase : Dumper {
    private var registryName: ResourceLocation? = null
    private var amountsCreator: DumpAmountsCreator? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): Dumper {
        registryName = name
        return this
    }

    override fun dumpAmounts(): DumpAmounts? = if (amountsCreator == null) null else DumpAmounts().apply { amountsCreator!!() }

    fun amounts(block: DumpAmountsCreator) {
        amountsCreator = block
    }
}