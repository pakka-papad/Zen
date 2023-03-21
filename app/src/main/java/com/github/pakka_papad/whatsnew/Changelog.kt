package com.github.pakka_papad.whatsnew

data class Changelog(
    val versionCode: Int,
    val versionName: String,
    val changes: List<String>,
    val date: String,
)