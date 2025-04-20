package com.example.quickseat

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BusAdapter(private val busList: List<DataModel>, private val listener: OnItemClickListener ) :
    RecyclerView.Adapter<BusAdapter.BusViewHolder>() {

    inner class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busNo: TextView = itemView.findViewById(R.id.tvBusNo)
        val route: TextView = itemView.findViewById(R.id.tvRoute)
        val Date: TextView = itemView.findViewById(R.id.tvDate)
        val departureTime: TextView = itemView.findViewById(R.id.tvTiming)
        val seatsAvailable: TextView = itemView.findViewById(R.id.tvSeatsAvailable)
//        val busImage: ImageView = itemView.findViewById(R.id.busImage)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_items, parent, false)
        return BusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = busList[position]
        holder.busNo.text = bus.busNo
        holder.route.text = bus.busRoute
        holder.Date.text = bus.busDate
        holder.departureTime.text = bus.busTime
        holder.seatsAvailable.text = bus.busSeats
//        Glide.with(holder.itemView.context).load(bus.imageUrl).into(holder.busImage)

        // Handle click events
        holder.itemView.setOnClickListener {
            listener.onItemClick(bus)


        }
    }

    override fun getItemCount() = busList.size

    interface OnItemClickListener {
        fun onItemClick(bus: DataModel)
    }
}
