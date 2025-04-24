package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.dataModels.MunicipalityInfo;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<MunicipalityInfo> searchHistory;
    private OnMunicipalityClickListener listener;


    public SearchHistoryAdapter(List<MunicipalityInfo> searchHistory, OnMunicipalityClickListener listener) {
        this.searchHistory = searchHistory;
        this.listener = listener;
    }

    public interface OnMunicipalityClickListener {
        void onShowClicked(MunicipalityInfo info);
        void onDeleteClicked(MunicipalityInfo info);
    }

    @NonNull
    @Override
    public SearchHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_municipality, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryAdapter.ViewHolder holder, int position) {
        MunicipalityInfo info = searchHistory.get(position);
        holder.nameText.setText(info.getName());
        holder.populationText.setText("Väkiluku: " + info.getPopulation());
        holder.employmentRateText.setText("Työllisyysaste: " + info.getEmploymentRate() + "%");

        //holder.showButton.setOnClickListener(v -> listener.onShowClicked(info));
        holder.showButton.setOnClickListener(v -> {
            if (listener != null) {
                Log.d("DEBUG", "Show clicked for: " + info.getName());
                listener.onShowClicked(info);
            }
        });
        //holder.deleteButton.setOnClickListener(v -> listener.onDeleteClicked(info));
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                Log.d("DEBUG", "Delete clicked for: " + info.getName());
                listener.onDeleteClicked(info);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchHistory.size();
    }

    public void updateData(List<MunicipalityInfo> newSearchHistory) {
        searchHistory.clear(); // keep same reference
        searchHistory.addAll(newSearchHistory);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, populationText, employmentRateText;
        ImageButton showButton, deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.municipalityNameTV);
            populationText = itemView.findViewById(R.id.populationTV);
            employmentRateText = itemView.findViewById(R.id.employmentRateTV);
            showButton = itemView.findViewById(R.id.showMunicipality);
            deleteButton = itemView.findViewById(R.id.deleteMunicipality);
        }
    }
}