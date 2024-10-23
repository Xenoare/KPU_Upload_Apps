package com.example.kpuayaya.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.kpuayaya.R
import com.example.kpuayaya.databinding.ItemRecentBinding
import com.example.kpuayaya.model.PostModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.NonDisposableHandle

class RecentAdapter(options: FirestoreRecyclerOptions<PostModel>, private val listener: OnItemClickListener) : FirestoreRecyclerAdapter<PostModel, RecentAdapter.RecentViewHolder>(options) {
    class RecentViewHolder(private val binding: ItemRecentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostModel) {
            with(binding) {
                tvTitle.text = item.name
                tvSource.text = item.description
                ivItem.load(item.file) {
                    crossfade(true)
                    placeholder(R.color.black)
                    transformations(CircleCropTransformation())
                }
            }
        }

    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int, model: PostModel) {
        val data = getItem(position)
        val id = snapshots.getSnapshot(position).id
        holder.itemView.setOnClickListener {
            listener.onItemClick(id)
        }
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = ItemRecentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return RecentViewHolder(binding)
    }

    interface OnItemClickListener {
        fun onItemClick(documentId: String)
    }

}