package com.example.revoluttask.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.revoluttask.R
import com.example.revoluttask.data.Resource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: RatesViewModel by viewModels {
            ViewModelFactory(applicationContext)
        }

        rates_recycler.layoutManager = LinearLayoutManager(this)
        val adapter = RatesAdapter(viewModel::enterCurrencyValue)
        adapter.setHasStableIds(true)
        rates_recycler.adapter = adapter

        viewModel.getRates().observeForever { resource ->
            if (resource.status == Resource.Status.SUCCESS) {
                (rates_recycler.adapter as RatesAdapter).rates = resource.data?.toMutableList()!!
            }
        }
    }
}
