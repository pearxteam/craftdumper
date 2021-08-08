package net.pearx.craftdumper.common.dumper

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
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
    fun getTextComponent() = TranslationTextComponent("craftdumper.output.$translationKey").apply {
        style = style.setClickEvent(ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString()))
    }
}

interface DumpProgressReporter {
    var progress: Int
}

interface Dumper : IForgeRegistryEntry<Dumper> {
    val translationKey: String

    fun getTitle() = TranslationTextComponent("craftdumper.dumper.$translationKey")

    fun getSubtitleData() = TranslationTextComponent("craftdumper.toast.subtitle.data")

    fun getSubtitleAmounts() = TranslationTextComponent("craftdumper.toast.subtitle.amounts")

    fun getAmounts(): DumpAmounts?

    fun getCount(): Int

    fun dumpData(reporter: DumpProgressReporter): List<DumpOutput>

    fun dumpAmounts(): DumpOutput? {
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
                            appendLine()
                        appendCsvRow(key, value.toString())
                    }
                }
            }
            return DumpOutput("amounts", amountsFile)
        }
        return null
    }
}