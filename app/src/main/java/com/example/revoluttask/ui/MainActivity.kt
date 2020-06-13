package com.example.revoluttask.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.revoluttask.R

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel: RatesViewModel by viewModels {
            ViewModelFactory(applicationContext)
        }

        viewModel.getRates().observeForever { rates ->
            println(rates)
        }

        // Delete later
        var wasChanged = false
        handler.postDelayed({
            if (!wasChanged) {
                wasChanged = true
                viewModel.enterCurrencyValue(100.0, "RUB")
            }
        }, 1000)
    }
}
