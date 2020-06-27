package com.example.revoluttask

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.agoda.kakao.screen.Screen.Companion.idle
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.example.revoluttask.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ExampleInstrumentedTest {
    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun verifyTickersDisplayed() {
        onScreen<MainActivityScreen> {
            recycler {
                hasSize(32)
                firstChild<MainActivityScreen.Item> {
                    isVisible()
                    ticker { hasText("EUR") }
                    amount { hasText("1.0") }
                    description { hasText("Euro") }
                }
            }
            progressBar {
                isGone()
            }
            ratesText {
                containsText("Rates at")
            }
        }
    }

    @Test
    fun verifyRatesChangedAfterType() {
        onScreen<MainActivityScreen> {
            recycler {
                childAt<MainActivityScreen.Item>(2) {
                    amount {
                        clearText()
                        typeText("10.0")
                    }
                }

                idle()

                firstChild<MainActivityScreen.Item> {
                    isVisible()
                    amount { hasText("10.0") }
                    ticker { hasNoText("EUR") }
                }

                childAt<MainActivityScreen.Item>(1) {
                    ticker { hasText("EUR") }
                    amount { hasNoText("1.0") }
                    description { hasText("Euro") }
                }
            }
        }
    }
}
