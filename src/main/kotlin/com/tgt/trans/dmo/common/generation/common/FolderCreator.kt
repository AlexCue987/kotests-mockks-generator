package com.tgt.trans.dmo.common.generation.common

import java.io.File

fun createFolder(path: String) {
    val file = File(path)
    if(!file.exists()) {
        file.mkdirs()
    }
}

