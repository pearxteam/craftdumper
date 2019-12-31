package net.pearx.craftdumper.common.dumper

import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.helper.client
import java.io.InputStream
import kotlin.random.Random

data class DumpFile(val path: String, val data: InputStream)

typealias DumperFileData = Iterable<DumpFile>

interface DumperFiles : Dumper {
    fun dumpFiles(): DumperFileData

    override fun dumpData(reporter: DumpProgressReporter): List<DumpOutput> {
        val baseDirectory = CraftDumper.getOutputFile(registryName!!)
        dumpFiles().forEachIndexed { index, file ->
            val dumpPath = baseDirectory
                .resolve(file.path)
            dumpPath.parentFile.mkdirs()
            file.data.use { dump ->
                dumpPath.outputStream().buffered().use { file ->
                    dump.copyTo(file)
                }
            }
            reporter.progress = index + 1
        }
        return listOf(DumpOutput("directory", baseDirectory))
    }
}

typealias DumperFilesCreator = suspend SequenceScope<DumpFile>.() -> Unit

class DumperFilesContext : DumperBase(), DumperFiles {
    private lateinit var filesCreator: DumperFilesCreator

    override fun dumpFiles(): DumperFileData = Iterable { iterator(filesCreator) }

    fun files(block: DumperFilesCreator) {
        filesCreator = block
    }
}

inline fun dumperFiles(init: DumperFilesContext.() -> Unit): DumperFiles = DumperFilesContext().apply(init)

inline fun dumperFilesClient(init: DumperFilesContext.() -> Unit): DumperFiles? = client { DumperFilesContext().apply(init) }

suspend inline fun SequenceScope<DumpFile>.file(getPath: () -> String, getData: () -> InputStream) {
    val path = try { getPath() } catch(e: Throwable) { "error_${Random.nextInt()}" }
    val data = try { getData() } catch(e: Throwable) { ByteArray(0).inputStream() }
    yield(DumpFile(path, data))
}