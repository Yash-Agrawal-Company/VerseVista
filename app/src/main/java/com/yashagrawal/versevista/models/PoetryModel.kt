package com.yashagrawal.versevista.models

data class PoetryModel(
    var poetryId : String? = null,
    var poetry : String? = null,
    var username : String? = null,
    var date : String? = null,
    var uid : String? = null,
    var globalRating : Float = 0.0f,
)