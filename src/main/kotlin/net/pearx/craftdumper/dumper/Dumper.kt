package net.pearx.craftdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.registries.IForgeRegistryEntry
import java.io.File

class DumpAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")

    fun sort(): List<Pair<String, Int>> = toList().sortedByDescending { (_, v) -> v }
}

enum class DumpOutputType(val value: String, val hasProgress: Boolean) {
    AMOUNTS("amounts", false),
    DATA("data", true)
}

class DumpOutput(val translationKey: String, val path: File)

interface DumpProgressReporter {
    var progress: Float
}

interface Dumper : IForgeRegistryEntry<Dumper> {
    override fun getRegistryType(): Class<Dumper> = Dumper::class.java

    val translationKey: String

    fun getDisplayName(): String = I18n.translateToLocalFormatted("craftdumper.dumper.$translationKey.name")

    fun getAmounts(): DumpAmounts?

    fun getCount(): Int

    fun dumpData(reporter: DumpProgressReporter): List<DumpOutput>
}

typealias DumpAmountsCreator = DumpAmounts.() -> Unit
typealias DumpCountCreator = () -> Int

abstract class DumperBase : Dumper {
    private var registryName: ResourceLocation? = null
    private var amountsCreator: DumpAmountsCreator? = null
    private var countCreator: DumpCountCreator? = null
    private var _translationKey: String? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): Dumper = apply { registryName = name }

    override var translationKey: String
        get() = _translationKey ?: registryName?.path ?: "null"
        set(value) {
            _translationKey = value
        }

    override fun getAmounts(): DumpAmounts? = if (amountsCreator == null) null else DumpAmounts().apply { amountsCreator!!() }

    override fun getCount(): Int = countCreator?.invoke() ?: 0

    fun amounts(block: DumpAmountsCreator) {
        amountsCreator = block
    }

    fun count(block: DumpCountCreator) {
        countCreator = block
    }
}