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
    private fun incrementBy(key: String, value: Int): Int {
        val prev = this[key] ?: 0
        this[key] = prev + value
        return prev
    }

    operator fun String?.plusAssign(value: Int) {
        incrementBy(this ?: "null", value)
    }

    @JvmName("plusAssignIterableString") // name clash
    operator fun Iterable<String?>.plusAssign(value: Int) {
        forEach { it += value }
    }

    operator fun Array<String?>.plusAssign(value: Int) {
        forEach { it += value }
    }

    operator fun String?.unaryPlus() {
        this += 1
    }

    @JvmName("unaryPlusIterableString") // name clash
    operator fun Iterable<String?>.unaryPlus() {
        forEach { it += 1 }
    }

    operator fun Array<String?>.unaryPlus() {
        forEach { it += 1 }
    }

    operator fun ResourceLocation?.plusAssign(value: Int) {
        incrementBy(this?.namespace ?: "null", value)
    }

    @JvmName("plusAssignIterableResourceLocation") // name clash
    operator fun Iterable<ResourceLocation?>.plusAssign(value: Int) {
        forEach { it += value }
    }

    operator fun Array<ResourceLocation?>.plusAssign(value: Int) {
        forEach { it += value }
    }

    operator fun ResourceLocation?.unaryPlus() {
        this += 1
    }

    @JvmName("unaryPlusIterableResourceLocation") // name clash
    operator fun Iterable<ResourceLocation?>.unaryPlus() {
        forEach { it += 1 }
    }

    operator fun Array<ResourceLocation?>.unaryPlus() {
        forEach { it += 1 }
    }

    fun sorted(): List<Pair<String, Int>> = toList().sortedByDescending { (_, v) -> v }
}

class DumpOutput(private val translationKey: String, private val path: File) {
    fun getTextComponent() = TextComponentTranslation("craftdumper.output.$translationKey.name").apply {
        with(style) {
            clickEvent = ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())
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
            amountsFile.parentFile.mkdirs()
            amountsFile.printWriter().use { writer ->
                with(writer) {
                    var start = true
                    for ((key, value) in amounts.sorted()) {
                        if (start)
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