package com.example.week3exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class TweetAdapter(val tweets: List<Tweet>): RecyclerView.Adapter<TweetAdapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val username: TextView = itemView.findViewById(R.id.textView)

        val handle: TextView =  itemView.findViewById(R.id.major)

        val context: TextView = itemView.findViewById(R.id.button)

        val icon: ImageView = itemView.findViewById(R.id.imageView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_tweet, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentTweet = tweets[position]
        holder.username.text = currentTweet.username
        holder.handle.text = currentTweet.handle
        holder.context.text = currentTweet.content

        Picasso.get().setIndicatorsEnabled(true)

        if (currentTweet.iconUrl.isNotEmpty()) {
            Picasso.get()
                .load(currentTweet.iconUrl)
                .into(holder.icon)
        }


    }

    override fun getItemCount(): Int {
        return tweets.size
    }


}