package com.example.revoluttask.data

interface IconDataSource {
    fun loadIconResource(ticker: String): Int
}