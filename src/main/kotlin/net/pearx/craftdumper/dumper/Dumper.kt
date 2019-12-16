package net.pearx.craftdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

class DumpAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")

    fun sort(): List<Pair<String, Int>> = toList().sortedByDescending { (_, v) -> v }
}

interface DumpProgressReporter {
    var progress: Double
}

interface Dumper : IForgeRegistryEntry<Dumper> {
    override fun getRegistryType(): Class<Dumper> = Dumper::class.java

    fun getAmounts(): DumpAmounts?

    fun getCount(): Int

    fun dumpContents(reporter: DumpProgressReporter)
}

typealias DumpAmountsCreator = DumpAmounts.() -> Unit
typealias DumpCountCreator = () -> Int

abstract class DumperBase : Dumper {
    private var registryName: ResourceLocation? = null
    private var amountsCreator: DumpAmountsCreator? = null
    private var countCreator: DumpCountCreator? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): Dumper = apply { registryName = name }

    override fun getAmounts(): DumpAmounts? = if (amountsCreator == null) null else DumpAmounts().apply { amountsCreator!!() }

    override fun getCount(): Int = countCreator?.invoke() ?: 0

    fun amounts(block: DumpAmountsCreator) {
        amountsCreator = block
    }

    fun count(block: DumpCountCreator) {
        countCreator = block
    }
}