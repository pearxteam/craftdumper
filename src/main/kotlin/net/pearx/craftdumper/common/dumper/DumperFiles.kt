package net.pearx.craftdumper.common.dumper

import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.helper.client
import java.io.File
import java.io.InputStream
import kotlin.random.Random

sealed class DumpFile(val path: String) {
    abstract fun write(absolutePath: File)

    class Copying(path: String, val data: InputStream) : DumpFile(path) {
        override fun write(absolutePath: File) {
            data.use { dump ->
                absolutePath.outputStream().buffered().use { file ->
                    dump.copyTo(file)
                }
            }
        }
    }

    class Direct(path: String, val writer: (File) -> Unit) : DumpFile(path) {
        override fun write(absolutePath: File) {
            writer(absolutePath)
        }
    }
}

typealias DumperFileData = Iterable<DumpFile>

interface DumperFiles : Dumper {
    fun dumpFiles(): DumperFileData

    override fun dumpData(reporter: DumpProgressReporter): List<DumpOutput> {
        val baseDirectory = CraftDumper.getOutputFile(registryName!!)
        dumpFiles().forEachIndexed { index, file ->
            val dumpPath = baseDirectory
                .resolve(file.path)
            dumpPath.parentFile.mkdirs()
            try {
                file.write(dumpPath)
            } catch(e: Throwable) {
                CraftDumper.log.error("An error occurred while creating a dump!", e)
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