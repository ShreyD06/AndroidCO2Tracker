package com.shreyd.co2tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DriveAdapter(private val mList: List<Drive2>) : RecyclerView.Adapter<DriveAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
// inflates the card_view_design view
// that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_recent_drives, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

// sets the image to the imageview from our itemHolder class
        holder.emit.text = "${itemsViewModel.emission} kg"

// sets the text to the textview from our itemHolder class
        holder.distance.text = "${itemsViewModel.distance} mi"

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val emit: TextView = itemView.findViewById(R.id.emit)
        val distance: TextView = itemView.findViewById(R.id.distance)
    }
}