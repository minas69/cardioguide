package com.example.medicalapp

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.Adapter<*>.autoScrollToStart(recyclerView: RecyclerView) {
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            recyclerView.scrollToPosition(getItemCount() - 1)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

            if (!layoutManager.stackFromEnd) {
                onItemRangeInserted(positionStart, itemCount)
            }
        }
    })
}