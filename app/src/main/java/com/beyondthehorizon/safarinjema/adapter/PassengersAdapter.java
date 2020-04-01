package com.beyondthehorizon.safarinjema.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.safarinjema.R;
import com.beyondthehorizon.safarinjema.models.TripModel;
import com.beyondthehorizon.safarinjema.views.PassengerDetailsActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.PassengerDetails;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.MyViewHolder>
        implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<TripModel> providerModelArrayList;
    private Context ctx;
    private ArrayList<TripModel> providersListFiltered;
    private SharedPreferences sharedpreferences;
    // Animation
    private Animation moveUp;

    public PassengersAdapter(Context ctx, ArrayList<TripModel> providerModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.providerModelArrayList = providerModelArrayList;
        this.providersListFiltered = providerModelArrayList;
        this.ctx = ctx;
    }

    @Override
    public PassengersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.passenger_layout_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(PassengersAdapter.MyViewHolder holder, int position) {
        final TripModel provider = providerModelArrayList.get(position);
        sharedpreferences = ctx.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        holder.providerName.setText(provider.getNames());
        holder.travelDate.setText(provider.getDateTime());

        holder.providerPhone.setText(provider.getPhoneNo());
        holder.saccoName.setText(provider.getSaccoName());
        holder.vehiclePlate.setText(provider.getNumberPlate());

        holder.itemView.setOnClickListener(view -> {
            Gson gson = new Gson();
            Intent providerDetails = new Intent(ctx, PassengerDetailsActivity.class);
            editor.putString(PassengerDetails, gson.toJson(provider));
            editor.apply();
            providerDetails.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(providerDetails);
        });
    }

    @Override
    public int getItemCount() {
        return providerModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView providerName, travelDate;
        TextView providerPhone;
        TextView saccoName, vehiclePlate;

        public MyViewHolder(View itemView) {
            super(itemView);
            providerName = itemView.findViewById(R.id.username);
            providerPhone = itemView.findViewById(R.id.userphone);
            saccoName = itemView.findViewById(R.id.saccoName);
            vehiclePlate = itemView.findViewById(R.id.vehiclePlate);
            travelDate = itemView.findViewById(R.id.travelDate);
        }

    }

    @Override
    public Filter getFilter() {
        return filteredProvidersList;
    }

    private Filter filteredProvidersList = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<TripModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                providerModelArrayList = providersListFiltered;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (TripModel item : providersListFiltered) {
                    if (item.getNames().toLowerCase().contains(filterPattern) ||
                            item.getPhoneNo().toLowerCase().contains(filterPattern) ||
                            item.getSaccoName().toLowerCase().contains(filterPattern) ||
                            item.getDateTime().toLowerCase().contains(filterPattern) ||
                            item.getDriverName().toLowerCase().contains(filterPattern) ||
                            item.getFrom().toLowerCase().contains(filterPattern) ||
                            item.getNumberPlate().toLowerCase().contains(filterPattern) ||
                            item.getDestination().toLowerCase().contains(filterPattern) ||
                            item.getIdNo().toLowerCase().contains(filterPattern)) {

                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            providerModelArrayList = (ArrayList<TripModel>) filterResults.values;
            notifyDataSetChanged();
        }
    };
}