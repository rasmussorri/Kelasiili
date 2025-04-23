package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.dataModels.MunicipalityInfo;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<MunicipalityInfo> searchHistory;

    public SearchHistoryAdapter(List<MunicipalityInfo> searchHistory) {
        this.searchHistory = searchHistory;
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

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.municipalityNameTV);
            populationText = itemView.findViewById(R.id.populationTV);
            employmentRateText = itemView.findViewById(R.id.employmentRateTV);
        }
    }
}