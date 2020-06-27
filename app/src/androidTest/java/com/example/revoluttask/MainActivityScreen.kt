package com.example.revoluttask

import android.view.View
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import org.hamcrest.Matcher

class MainActivityScreen : Screen<MainActivityScreen>() {
    class Item(parent: Matcher<View>) : KRecyclerItem<Item>(parent) {
        val ticker: KTextView = KTextView(parent) { withId(R.id.ticker) }
        val description: KTextView = KTextView(parent) { withId(R.id.description) }
        val amount: KEditText = KEditText(parent) { withId(R.id.amount) }
    }

    val recycler = KRecyclerView(
        { withId(R.id.rates_recycler) }, { itemType(::Item) })
    val progressBar = KProgressBar { withId(R.id.progress_bar) }
    val ratesText = KTextView { withId(R.id.rates_text) }
}