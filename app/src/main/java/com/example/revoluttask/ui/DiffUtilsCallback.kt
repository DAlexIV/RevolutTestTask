package com.example.revoluttask.ui

import androidx.recyclerview.widget.DiffUtil

open class DiffUtilsCallback<T> @JvmOverloads constructor(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val itemsComparator: (T, T) -> Boolean = { var1, var2 -> var1 == var2 },
    private val contentsComparator: (T, T) -> Boolean = { var1, var2 -> var1 == var2 }
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItemPosition < oldList.size && newItemPosition < newList.size &&
                itemsComparator.invoke(oldList[oldItemPosition], newList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItemPosition < oldList.size && newItemPosition < newList.size &&
                contentsComparator.invoke(oldList[oldItemPosition], newList[newItemPosition])
}