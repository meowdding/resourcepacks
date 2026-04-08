#!/usr/bin/env
@file:DependsOn("com.google.code.gson:gson:2.11.0")

import com.google.gson.Gson
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.io.path.readText

data class PackEntry(val id: String, val name: String)

val root = __FILE__.toPath().parent.parent.parent
val destination = root.resolve("dest").createDirectories()

println("1")

Gson().fromJson(root.resolve("packs.json").readText(), Array<PackEntry>::class.java).forEach { (id, name) ->
    val sourceFolder = root.resolve(id)
    val zipFile = root.resolve(destination).resolve("${name}.zip")

    println("2 $id")

    if (sourceFolder.exists() && sourceFolder.isDirectory()) {
        println("Zipping $id -> ${zipFile.name}")
        zipFolder(sourceFolder, zipFile)
    } else {
        println("$id cant be found")
    }
}

fun zipFolder(sourceFolder: Path, zipFile: Path) {
    println("3")
    ZipOutputStream(zipFile.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)).use { zipOut ->
        Files.walk(sourceFolder).forEach { filePath ->
            println("4 $filePath")
            if (Files.isRegularFile(filePath)) {
                println("5")
                val zipEntry = ZipEntry(sourceFolder.relativize(filePath).toString())
                zipOut.putNextEntry(zipEntry)
                Files.copy(filePath, zipOut)
                zipOut.closeEntry()
                println("6")
            }
        }
    }
}
