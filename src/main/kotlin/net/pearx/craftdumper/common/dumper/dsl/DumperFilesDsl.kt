package net.pearx.craftdumper.common.dumper.dsl

import net.minecraft.util.ResourceLocation
import net.pearx.craftdumper.common.dumper.DumpFile
import net.pearx.craftdumper.common.dumper.DumperFiles
import net.pearx.craftdumper.common.helper.client
import java.io.File
import java.io.InputStream
import kotlin.random.Random

typealias DumperFilesCreator = suspend SequenceScope<DumpFile>.() -> Unit

@DumperDsl
class DumperFilesContext : DumperContext() {
    private lateinit var filesCreator: DumperFilesCreator

    fun files(block: DumperFilesCreator) {
        filesCreator = block
    }

    suspend inline fun SequenceScope<DumpFile>.fileReading(getPath: () -> String, getData: () -> InputStream) {
        val path = try {
            getPath()
        }
        catch (e: Throwable) {
            "error_${Random.nextInt()}"
        }
        val data = try {
            getData()
        }
        catch (e: Throwable) {
            ByteArray(0).inputStream()
        }
        yield(DumpFile.Copying(path, data))
    }

    suspend inline fun SequenceScope<DumpFile>.fileDirect(getPath: () -> String, noinline writer: (File) -> Unit) {
        val path = try {
            getPath()
        }
        catch (e: Throwable) {
            "error_${Random.nextInt()}"
        }
        yield(DumpFile.Direct(path, writer))
    }

    @PublishedApi
    internal fun create(): DumperFiles = DumperFilesImplementation(registryName, translationKey, amountsCreator, countCreator, filesCreator)
}

inline fun dumperFiles(init: DumperFilesContext.() -> Unit): DumperFiles = DumperFilesContext().apply(init).create()
inline fun dumperFilesClient(init: DumperFilesContext.() -> Unit): DumperFiles? = client { dumperFiles(init) }

class DumperFilesImplementation(
    _registryName: ResourceLocation?,
    _translationKey: String?,
    amountsCreator: DumpAmountsCreator?,
    countCreator: DumpCountCreator?,
    private val filesCreator: DumperFilesCreator
) : DumperImplementation(_registryName, _translationKey, amountsCreator, countCreator), DumperFiles {
    override fun dumpFiles(): Iterable<DumpFile> = Iterable { iterator(filesCreator) }
}