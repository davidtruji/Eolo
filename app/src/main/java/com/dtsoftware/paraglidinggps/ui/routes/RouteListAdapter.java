package com.dtsoftware.paraglidinggps.ui.routes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Route;
import com.dtsoftware.paraglidinggps.Utils;

import java.util.List;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.RouteViewHolder> {

    private final LayoutInflater mInflater;
    private List<Route> routes; // Cached copy
    private Context context; // Para poder usar los recursos
    private static ClickListener itemClickListener;


    RouteListAdapter(Context context, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        RouteListAdapter.itemClickListener = clickListener;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvRouteName, tvRouteDistance;

        private RouteViewHolder(View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvRouteDistance = itemView.findViewById(R.id.tvRouteDistance);
        }

        public void setClickListener(Route route, ClickListener clickListener) {
            itemView.setOnClickListener(view -> clickListener.onItemClicked(route));
        }

    }

    public interface ClickListener {
        void onItemClicked(Route route);
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.route_list_item, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        if (routes != null) {
            Route current = routes.get(position);

            holder.tvRouteName.setText(current.getRouteName());
            holder.tvRouteDistance.setText(Utils.getDistanceString(current.getDistance())+ " Km");

            holder.setClickListener(current, itemClickListener);

        }  // Covers the case of data not being ready yet.
        // holder.wordItemView.setText("No Word");

    }

    void setRoutes(List<Route> routeList) {
        routes = routeList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (routes != null)
            return routes.size();
        else return 0;
    }

    @SuppressWarnings("unused")
    public Route getItem(int index) {
        return routes.get(index);
    }


}