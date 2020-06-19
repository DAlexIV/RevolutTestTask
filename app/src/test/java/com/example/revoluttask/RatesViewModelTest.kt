package com.example.revoluttask

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.example.revoluttask.data.Resource
import com.example.revoluttask.data.model.BasicCurrencyRate
import com.example.revoluttask.data.model.RatesData
import com.example.revoluttask.di.ViewModelFactory
import com.example.revoluttask.ui.RatesViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks


@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class RatesViewModelTest {
    lateinit var model: RatesViewModel

    @Before
    fun init() {
        val viewModelFactory =
            ViewModelFactory(ApplicationProvider.getApplicationContext())
        model = viewModelFactory.create(RatesViewModel::class.java)
    }

    @Test
    fun loadingTest() {
        var dataReceived = 0
        val mockedObserver = Observer<Resource<RatesData>> { data ->
            ++dataReceived
            if (dataReceived == 1) {
                assert(data.status == Resource.Status.LOADING)
            }
        }
        model.getRates().observeForever(mockedObserver)

        /**
         * It will lead to flaky tests, but for the sake of test application I guess it's fine
         * In the real production world, I'll probably make retrofit to work on the main thread or
         * just use RxJava with trampoline scheduler
         */
        Thread.sleep(10000)
        runUiThreadTasksIncludingDelayedTasks()

        assert(dataReceived >= 1)
    }

    @Test
    fun networkCallSuccessTest() {
        var dataReceived = 0
        val mockedObserver = Observer<Resource<RatesData>> { data ->
            ++dataReceived
            if (dataReceived == 2) {
                assert(data.status == Resource.Status.SUCCESS)
            }
        }
        model.getRates().observeForever(mockedObserver)

        Thread.sleep(10000)
        runUiThreadTasksIncludingDelayedTasks()

        assert(dataReceived >= 2) // Since we poll network every second
    }

    @Test
    fun manualEditTest() {
        var correctManualDataGot = false
        val mockedObserver = Observer { data: Resource<RatesData> ->
            if (data.status == Resource.Status.SUCCESS) {
                data.data?.rates?.forEach { rate ->
                    if (rate.basicCurrencyRate.tickerString == "RUB"
                        && rate.basicCurrencyRate.rate == 100.0
                    ) {
                        correctManualDataGot = true
                    }
                }
            }
        }
        model.getRates().observeForever(mockedObserver)

        Thread.sleep(10000)
        runUiThreadTasksIncludingDelayedTasks()

        model.enterCurrencyValue(100.0, "RUB")
        runUiThreadTasksIncludingDelayedTasks()
        assert(correctManualDataGot)
    }

    @Test
    fun changeModeTest() {
        var dataReceived = 0
        val mockedObserver = Observer<Resource<RatesData>> {
            ++dataReceived
        }
        model.getRates().observeForever(mockedObserver)

        Thread.sleep(10000)
        runUiThreadTasksIncludingDelayedTasks()
        model.activeMode = RatesViewModel.Companion.Mode.EDIT
        dataReceived = 0

        Thread.sleep(10000)
        runUiThreadTasksIncludingDelayedTasks()

        assert(dataReceived == 0)
    }

    @Test
    fun localStorageSuccessTest() {
        val sharedPreferences: SharedPreferences =
            ApplicationProvider.getApplicationContext<Context>().getSharedPreferences(
                "RATES",
                Context.MODE_PRIVATE
            )
        val testString = """[{"rate":1.0,"tickerString":"EUR"}]"""

        sharedPreferences.edit().putString("RATES", testString).commit()
        var localDataReceived = false
        val mockedObserver = Observer<Resource<RatesData>> { data ->
            if (data.status == Resource.Status.SUCCESS
                && data.data?.rates?.size == 1
                && data.data?.rates?.get(0)?.basicCurrencyRate == BasicCurrencyRate("EUR", 1.0)
            ) {
                localDataReceived = true
            }
        }

        model.getRates().observeForever(mockedObserver)
        Thread.sleep(10000)
        runUiThreadTasksIncludingDelayedTasks()
        assert(localDataReceived)
    }
}