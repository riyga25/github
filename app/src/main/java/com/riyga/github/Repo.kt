package com.riyga.github

data class Repo (
    val id: Int,
    val name: String,
    val full_name: String,
    val description: String,
    val owner_id: Int,
    val owner_login: String,
    val owner_avatar: String,
    val stargazers_count: Int = 0,
    val forks_count: Int = 0,
    val language: String = ""
)