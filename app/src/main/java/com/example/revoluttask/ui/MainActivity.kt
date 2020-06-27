package com.example.revoluttask.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.revoluttask.R
import com.example.revoluttask.data.Resource
import com.example.revoluttask.di.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val simpleDateFormat = SimpleDateFormat("d MMM HH:mm", Locale.getDefault())
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

        viewModel.getRates().observe(this, Observer { resource ->
            if (resource.status == Resource.Status.SUCCESS) {
                progress_bar.visibility = View.GONE
                rates_recycler.visibility = View.VISIBLE

                (rates_recycler.adapter as RatesAdapter).rates =
                    resource.data?.rates?.toMutableList()!!

                resource.data.timestamp.let { timestamp ->
                    rates_text.text = getString(
                        R.string.rates_text,
                        simpleDateFormat.format(Date(timestamp))
                    )
                }
            } else if (resource.status == Resource.Status.LOADING) {
                rates_recycler.visibility = View.GONE
                progress_bar.visibility = View.VISIBLE
            } else if (resource.status == Resource.Status.ERROR) {
                // TODO clarify with PM
            }
        })
    }
}
