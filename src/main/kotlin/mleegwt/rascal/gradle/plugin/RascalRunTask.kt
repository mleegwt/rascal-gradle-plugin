package mleegwt.rascal.gradle.plugin.mleegwt.rascal.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File

@Suppress("UnstableApiUsage")
abstract class RascalRunTask : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val rascalInput: SourceDirectorySet

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val editInput: SourceDirectorySet

    @get:OutputDirectory
    abstract val rascalOutputDirectory: DirectoryProperty
    @get:OutputDirectory
    abstract val editOutputDirectory: DirectoryProperty

    @TaskAction
    fun runRascal(inputChanges: InputChanges) {
        val rascalInputChanges = inputChanges.getFileChanges(rascalInput).toList()
        val editInputChanges = inputChanges.getFileChanges(editInput).toList()

        // Removing first, might be re-generated
        removeOutputs(rascalInputChanges.mapNotNull(::getRemovedFile), rascalOutputDirectory)
        removeOutputs(editInputChanges.mapNotNull(::getRemovedFile), editOutputDirectory)

        // Determining files to handle due to incremental support
        val (rascalChanges, editChanges) = determineFilesLists(rascalInputChanges, editInputChanges)

        // Acutally performing each rascal transformation file to each edited file,
        // considering incremental support figured out above
        rascalChanges.forEach { rascalFile ->
            editChanges.forEach { editFile -> runRascal(rascalFile, editFile) }

            storeInOutputForIncremental(rascalFile)
        }
    }

    private fun storeInOutputForIncremental(rascalFile: File) {
        rascalOutputDirectory.file(rascalFile.normalize().path).get().asFile.writeText(rascalFile.readText())
    }

    private fun removeOutputs(removeFiles: List<File>, outputDir: DirectoryProperty) {
        removeFiles.forEach { outputDir.file(it.normalize().path).get().asFile.delete() }
    }

    private fun runRascal(rascalFile: File, editFile: File) {
        // TODO MLE Do something

    }

    private fun determineFilesLists(rascalChanges: List<FileChange>, editChanges: List<FileChange>): Pair<List<File>, List<File>> {
        return when {
            rascalChanges.size == 0 -> {
                // Only incremental changes for edit input, hence, get all rascal files
                // but only perform incrementally for edit input changes
                getFiles(rascalInput) to editChanges.mapNotNull(::getFile)
            }
            editChanges.size == 0 -> {
                // Only incremental changes for rascal input, hence, get all edit files
                // but only perform incrementally for rascal input changes
                rascalChanges.mapNotNull(::getFile) to getFiles(editInput)
            }
            else -> {
                // Mixed incremental approach, too difficult, so perform all
                getFiles(rascalInput) to getFiles(editInput)
            }
        }
    }
    private fun getFiles(files: Iterable<File?>) = files.filterNotNull().filter { file -> file.isFile }
    private fun getFile(change: FileChange) = if (change.fileType == FileType.FILE && change.changeType != ChangeType.REMOVED) change.file else null
    private fun getRemovedFile(change: FileChange) = if (change.fileType == FileType.FILE && change.changeType == ChangeType.REMOVED) change.file else null
}
