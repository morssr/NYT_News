package com.mls.mor.nytnews.utilities

fun printThreadInfo(tag: String = "", message: String = "") {
    println("$tag: thread: ${Thread.currentThread().name} $message")
}