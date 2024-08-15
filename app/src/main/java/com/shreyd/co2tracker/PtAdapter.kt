package com.shreyd.co2tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shreyd.co2tracker.model.RoutesResponse

class PtAdapter(private val mList: List<RoutesResponse.Route.Leg.Step>) : RecyclerView.Adapter<PtAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pt_cardview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        if (itemsViewModel.transitDetails.transitLine.vehicle.name.text == "Subway") {
            holder.CardImage.setImageResource(R.drawable.baseline_train_24)
            holder.CardText.text = "${itemsViewModel.transitDetails.transitLine.name}: ${itemsViewModel.transitDetails.stopDetails.departureStop.name} TO ${itemsViewModel.transitDetails.stopDetails.arrivalStop.name}"
        }
        else {
            holder.CardImage.setImageResource(R.drawable.baseline_directions_bus_24)
            holder.CardText.text = "${itemsViewModel.transitDetails.transitLine.nameShort} ${itemsViewModel.transitDetails.transitLine.name}: ${itemsViewModel.transitDetails.stopDetails.departureStop.name} TO ${itemsViewModel.transitDetails.stopDetails.arrivalStop.name}"
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val CardImage: ImageView = itemView.findViewById(R.id.CardImage)
        val CardText: TextView = itemView.findViewById(R.id.card_text)
    }
}