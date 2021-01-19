package com.example.medicalapp.ui.form

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StateSaver(recyclerView: RecyclerView) {

    private var states: SparseArray<SparseArray<Parcelable>> = SparseArray()

    init {
        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewAttachedToWindow(view: View) {
                val adapterPosition =
                    recyclerView.findContainingViewHolder(view)?.adapterPosition ?: return
                var container = states[adapterPosition]
                if (container == null) {
                    container = SparseArray<Parcelable>()
                    view.saveHierarchyState(container)
                    states.append(adapterPosition, container)
                } else {
                    view.restoreHierarchyState(container)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
//                val adapterPosition = recyclerView.findContainingViewHolder(view)?.adapterPosition ?: return
//                val container = SparseArray<Parcelable>()
//                view.saveHierarchyState(container)
//                states.append(adapterPosition, container)
            }

        })

        recyclerView.setRecyclerListener { holder ->
            val adapterPosition = holder.adapterPosition
            val container = SparseArray<Parcelable>()
            holder.itemView.saveHierarchyState(container)
            states.append(adapterPosition, container)
        }
    }

}