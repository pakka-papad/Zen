package com.github.pakka_papad.storage_explorer

import android.os.Environment
import java.io.File
import java.io.FileFilter

class MusicFileExplorer {

    private val root = Environment.getExternalStorageDirectory().absolutePath
    private var currentPath = root

    val isRoot: Boolean
        get() {
            return root == currentPath
        }

    private val filterDirectoriesAndMusic = object : FileFilter {
        override fun accept(pathname: File?): Boolean {
            if (pathname == null || !pathname.exists()) return false
            return pathname.isDirectory ||
                    pathname.extension == "mp3" ||
                    pathname.extension == "wav" ||
                    pathname.extension == "m4a" ||
                    pathname.extension == "flac" ||
                    pathname.extension == "aac"
        }
    }

    private val listeners = arrayListOf<DirectoryChangeListener>()

    fun addListener(listener: DirectoryChangeListener){
        listeners.add(listener)
        val directory = File(currentPath)
        if (!directory.exists() || !directory.isDirectory) return
        val files = directory.listFiles(filterDirectoriesAndMusic) ?: arrayOf()
        listener.onDirectoryChanged(currentPath,files)
    }

    fun removeListener(listener: DirectoryChangeListener){
        listeners.remove(listener)
    }

    fun moveInsideDirectory(directoryPath: String){
        currentPath = directoryPath
        val directory = File(currentPath)
        if(!directory.exists() || !directory.isDirectory) return
        val files = directory.listFiles(filterDirectoriesAndMusic) ?: arrayOf()
        listeners.forEach { it.onDirectoryChanged(currentPath,files) }
    }

    fun moveToParent(){
        if (currentPath == root) return
        var directory = File(currentPath)
        directory = directory.parentFile ?: return
        if (!directory.exists() || !directory.isDirectory) return
        currentPath = directory.absolutePath
        val files = directory.listFiles(filterDirectoriesAndMusic) ?: arrayOf()
        listeners.forEach { it.onDirectoryChanged(currentPath,files) }
    }


    interface DirectoryChangeListener {
        fun onDirectoryChanged(path: String, files: Array<File>)
    }

}