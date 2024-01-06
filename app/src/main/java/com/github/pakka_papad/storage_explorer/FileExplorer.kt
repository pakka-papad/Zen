package com.github.pakka_papad.storage_explorer

import android.os.Environment
import com.github.pakka_papad.data.music.SongExtractor
import java.io.File
import java.io.FileFilter

class MusicFileExplorer(
    private val songExtractor: SongExtractor
) {

    private val root = Environment.getExternalStorageDirectory().absolutePath
    private var currentPath = root

    val isRoot: Boolean
        get() {
            return root == currentPath
        }

    private val filterDirectories = object : FileFilter {
        override fun accept(pathname: File?): Boolean {
            if (pathname == null || !pathname.exists()) return false
            return pathname.isDirectory
        }
    }

    private val listeners = arrayListOf<DirectoryChangeListener>()

    fun addListener(listener: DirectoryChangeListener){
        listeners.add(listener)
        val directory = File(currentPath)
        if (!directory.exists() || !directory.isDirectory) return
        val directories = (directory.listFiles(filterDirectories) ?: arrayOf()).map {
            Directory(name = it.name, absolutePath = it.absolutePath)
        }
        val songs = songExtractor.extractMini(currentPath)
        listener.onDirectoryChanged(currentPath,DirectoryContents(directories,songs))
    }

    fun removeListener(listener: DirectoryChangeListener){
        listeners.remove(listener)
    }

    fun moveInsideDirectory(directoryPath: String){
        currentPath = directoryPath
        val directory = File(currentPath)
        if(!directory.exists() || !directory.isDirectory) return
        val directories = (directory.listFiles(filterDirectories) ?: arrayOf()).map {
            Directory(name = it.name, absolutePath = it.absolutePath)
        }
        val songs = songExtractor.extractMini(currentPath)
        listeners.forEach { it.onDirectoryChanged(currentPath, DirectoryContents(directories,songs)) }
    }

    fun moveToParent(){
        if (currentPath == root) return
        var directory = File(currentPath)
        directory = directory.parentFile ?: return
        if (!directory.exists() || !directory.isDirectory) return
        currentPath = directory.absolutePath
        val directories = (directory.listFiles(filterDirectories) ?: arrayOf()).map {
            Directory(name = it.name, absolutePath = it.absolutePath)
        }
        val songs = songExtractor.extractMini(currentPath)
        listeners.forEach { it.onDirectoryChanged(currentPath, DirectoryContents(directories,songs)) }
    }


    interface DirectoryChangeListener {
        fun onDirectoryChanged(path: String, files: DirectoryContents)
    }

}