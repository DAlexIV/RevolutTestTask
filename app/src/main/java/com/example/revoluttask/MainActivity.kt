package com.example.revoluttask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.revoluttask.data.NetworkDataSourceImpl
import com.example.revoluttask.data.RatesNetworkService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repo = NetworkDataSourceImpl(
            Retrofit.Builder()
                .baseUrl("https://hiring.revolut.codes/api/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(RatesNetworkService::class.java)
        )

        Thread {
            repo.getRates()
        }.start()
    }
}
