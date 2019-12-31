package net.pearx.craftdumper.common.dumper

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.event.ClickEvent
import net.minecraftforge.registries.IForgeRegistryEntry
import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.helper.internal.appendCsvRow
import java.io.File

class DumpAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")

    operator fun plusAssign(values: Collection<ResourceLocation?>) = values.forEach { plusAssign(it) }

    fun sorted(): List<Pair<String, Int>> = toList().sortedByDescending { (_, v) -> v }
}

class DumpOutput(val translationKey: String, val path: File) {
    fun getTextComponent() = TextComponentTranslation("craftdumper.output.$translationKey.name").apply {
        with(style) {
            clickEvent =  ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())
        }
    }
}

interface DumpProgressReporter {
    var progress: Int
}

interface Dumper : IForgeRegistryEntry<Dumper> {
    override fun getRegistryType(): Class<Dumper> = Dumper::class.java

    val translationKey: String

    fun getTitle(): ITextComponent = TextComponentTranslation("craftdumper.dumper.$translationKey.name")

    fun getSubtitleData() = TextComponentTranslation("craftdumper.toast.subtitle.data")

    fun getSubtitleAmounts() = TextComponentTranslation("craftdumper.toast.subtitle.amounts")

    fun getAmounts(): DumpAmounts?

    fun getCount(): Int

    fun dumpData(reporter: DumpProgressReporter): List<DumpOutput>

    fun dumpAmounts(): DumpOutput?
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

    override fun dumpAmounts(): DumpOutput? {
        val amounts = getAmounts()
        if (amounts != null) {
            val amountsFile = CraftDumper.getOutputFile(registryName!!, "_amounts.csv")
            amountsFile.printWriter().use { writer ->
                with(writer) {
                    var start = true
                    for ((key, value) in amounts.sorted()) {
                        if(start)
                            start = false
                        else
                            appendln()
                        appendCsvRow(key, value.toString())
                    }
                }
            }
            return DumpOutput("amounts", amountsFile)
        }
        return null
    }
}