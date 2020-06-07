package com.example.revoluttask

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.revoluttask.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkDataSource = NetworkBasicCurrencyDataSource(
            Retrofit.Builder()
                .baseUrl("https://hiring.revolut.codes/api/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(RatesNetworkService::class.java)
        )

        val ratesType = Types.newParameterizedType(
            List::class.java,
            BasicCurrencyRate::class.java
        )
        val moshi = Moshi.Builder().build();
        val localDataSource = LocalBasicCurrencyDataSource(
            applicationContext.getSharedPreferences("RATES", Context.MODE_PRIVATE),
            moshi.adapter(ratesType)
        )
        val currencyRateRepo =
            CurrencyRateRepoImpl(networkDataSource, localDataSource, applicationContext)

        currencyRateRepo.getRates().observeForever(::println)
    }
}
