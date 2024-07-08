package com.example.week3exercise

import java.io.Serializable

data class Tweet (
    val username: String,
    val content: String,
    val handle: String,

    val iconUrl: String
) : Serializable {
    constructor() : this("","","", "")
}

