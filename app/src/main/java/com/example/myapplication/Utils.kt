package com.example.myapplication

import android.app.Activity
import android.widget.Toast
import java.io.File

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG)
}

private val math: String by lazy { "" }

fun makeDir(path: String) = path.let { File(it) }.also { it.mkdir() }

fun main(){
    "aaa".myLet {
        print(it)
    }
}

fun <T> T.myLet(m: (T) -> Unit) {
    m(this)
}