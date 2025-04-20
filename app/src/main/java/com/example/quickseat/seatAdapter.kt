package com.example.quickseat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.contracts.Returns

class SeatAdapter(private val seatList: List<SeatModel>,
                  private var selectedSeatPosition: Int = RecyclerView.NO_POSITION ,
                  private var onSeatSelectedListener: ((SeatModel) -> Unit)? = null
) :
    RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    private var selectedSeat: SeatModel? = null // Track selected seat
    inner class SeatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seatNumber: TextView = itemView.findViewById(R.id.seatNumber)
//        val seatStatus: TextView = itemView.findViewById(R.id.seatStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_seat_items, parent, false)
        return SeatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seatList[position]
        holder.seatNumber.text = seat.seatNumber
        if (seat.isBooked) {
            holder.seatNumber.setBackgroundColor(Color.RED) }
        else {
            if (position == selectedSeatPosition) {
                holder.seatNumber.setBackgroundResource(R.color.blue) // Selected color
                // ... (optional: selected seat text color)
            } else {
                holder.seatNumber.setBackgroundResource(R.color.green) // Unselected color/drawable
                // ... (optional: default text color)
            }
        }
        holder.itemView.setOnClickListener {
            if (!seat.isBooked) { // Only allow selection if not booked
                selectedSeat = seat // Update selected seat
                val previousSelectedPosition = selectedSeatPosition
                selectedSeatPosition = holder.adapterPosition
                notifyItemChanged(previousSelectedPosition) // Update previous selected
                notifyItemChanged(selectedSeatPosition)     // Update newly selected

                onSeatSelectedListener?.let { it(seat) }

//                onSeatSelectedListener?.invoke(seat) // Notify the listener
            }
        }


    }

    override fun getItemCount(): Int = seatList.size

    fun getSelectedSeat(): SeatModel? {
        return selectedSeat // Return selected seat info
    }


}
