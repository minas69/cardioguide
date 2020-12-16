package com.example.medicalapp.ui.form

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalapp.R
import com.example.medicalapp.inflate
import kotlinx.android.synthetic.main.add_photo_item.view.*

class PhotosAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val PHOTO_VIEW_TYPE = 1
        private const val ADD_PHOTO_BUTTON_VIEW_TYPE = 2
    }

    private var photoCount = 0

    override fun getItemViewType(position: Int) = when (position) {
        itemCount - 1 -> ADD_PHOTO_BUTTON_VIEW_TYPE
        else -> PHOTO_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        PHOTO_VIEW_TYPE -> ViewHolder(parent.inflate(R.layout.photo_item))
        else -> ViewHolder(parent.inflate(R.layout.add_photo_item))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == itemCount - 1) {
            holder.itemView.addPhoto.setOnClickListener {
                photoCount += 1
                notifyItemInserted(itemCount - 2)
            }
        }
    }

    override fun getItemCount() = photoCount + 1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}