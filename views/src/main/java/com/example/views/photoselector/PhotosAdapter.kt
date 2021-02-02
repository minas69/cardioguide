package com.example.views.photoselector

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.views.R
import com.example.views.dpToPx
import com.example.views.inflate
import com.example.views.utils.set
import java.io.File
import java.util.*

class PhotosAdapter(
    var onAddButtonClickListener: (() -> Unit)? = null,
    var onRemoveButtonClickListener: ((Pair<String, String?>) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val PHOTO_VIEW_TYPE = 1
        private const val ADD_PHOTO_BUTTON_VIEW_TYPE = 2
    }

    var maxPhotos: Int = 1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var data = listOf<Pair<String, String?>>()
    private val updateCallback = UpdateCallback()

    fun updateItems(newItems: List<Pair<String, String?>>) {
        val oldItems = data.toList()
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(oldItems, newItems))
        data = newItems
        diffResult.dispatchUpdatesTo(updateCallback)
    }

    override fun getItemViewType(position: Int): Int {
        if (data.count() < maxPhotos && position == itemCount - 1) {
            return ADD_PHOTO_BUTTON_VIEW_TYPE
        }
        return PHOTO_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        PHOTO_VIEW_TYPE -> PhotoViewHolder(parent.inflate(R.layout.photo_item))
        else -> AddPhotoViewHolder(parent.inflate(R.layout.add_photo_item))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddPhotoViewHolder -> {
                holder.addPhotoButton.setOnClickListener {
                    onAddButtonClickListener?.invoke()
                }
            }
            is PhotoViewHolder -> {
                holder.imageView.set(
                    File(data[position].first),
                    CenterCrop(),
                    RoundedCorners(8.dpToPx())
                )
                holder.removeButton.setOnClickListener {
                    onRemoveButtonClickListener?.invoke(data[holder.adapterPosition])
                }
            }
        }
    }

    override fun getItemCount()
        = if (data.count() >= maxPhotos) data.count() else data.count() + 1

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.photoUri)
        val removeButton = view.findViewById<ImageButton>(R.id.removeButton)
    }

    class AddPhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addPhotoButton = (view as ViewGroup).getChildAt(0) as Button
    }

    inner class UpdateCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)

            if (data.count() >= maxPhotos) {
                notifyItemRemoved(data.count())
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }

    }

    class DiffUtilCallback(
        private val oldData: List<Pair<String, String?>>,
        private val newData: List<Pair<String, String?>>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldData.size

        override fun getNewListSize() = newData.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldData[oldItemPosition].first == newData[newItemPosition].first

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldData[oldItemPosition].first == newData[newItemPosition].first
    }
}