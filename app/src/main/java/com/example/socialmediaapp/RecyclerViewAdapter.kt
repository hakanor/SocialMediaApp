package com.example.socialmediaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.model.Post

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ModelViewHolder>() {

    private var postList: MutableList<Post> = mutableListOf()

    fun setData(data: List<Post>) {
        postList.clear() // Clear the existing data
        postList.addAll(data) // Add new data
        notifyDataSetChanged() // Notify the adapter that data has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ModelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val post = postList[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun deleteItem(adapterPosition: Int) {
        postList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post) {
            val postIdTextView: TextView = itemView.findViewById(R.id.postIdTextView)
            val postDateTextView: TextView = itemView.findViewById(R.id.postDateTextView)
            val postUserIdTextView: TextView = itemView.findViewById(R.id.postUserIdTextView)
            val postDescriptionTextView: TextView = itemView.findViewById(R.id.contentTextView)

            postIdTextView.text = post.id
            postDateTextView.text = post.date
            postUserIdTextView.text = post.userId
            postDescriptionTextView.text = post.content
        }
    }
}
