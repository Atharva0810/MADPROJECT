package com.example.madprojectmicro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private List<Hotel> hotelList;

    public HotelAdapter(List<Hotel> hotelList) {
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);

        // Set room number
        holder.roomNoTextView.setText(hotel.getRoomNumber());

        // Set room type
        holder.roomTypeTextView.setText(hotel.getRoomType());

        // Set room status
        if ("occupied".equals(hotel.getStatus())) {
            holder.statusTextView.setText("Occupied");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));

            // You might need to fetch the customer name separately or adjust your Hotel class
            // For now, we'll just hide this
            holder.occupiedByTextView.setVisibility(View.GONE);
        } else {
            holder.statusTextView.setText("Available");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            holder.occupiedByTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView roomNoTextView, roomTypeTextView, statusTextView, occupiedByTextView;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNoTextView = itemView.findViewById(R.id.roomNoTextView);
            roomTypeTextView = itemView.findViewById(R.id.roomTypeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            occupiedByTextView = itemView.findViewById(R.id.occupiedByTextView);
        }
    }
}