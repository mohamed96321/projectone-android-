package com.ma.socialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.socialapp.FragmentHome;
import com.ma.socialapp.FragmentSearchWorker;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.ProfileSearchResultActivity;
import com.ma.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapters extends RecyclerView.Adapter<SearchAdapters.myViewHolder> {

    Context context;
    ArrayList<DataUsers> workerDataArrayList;

    public SearchAdapters(Context context, ArrayList<DataUsers> workerDataArrayList) {
        this.context = context;
        this.workerDataArrayList = workerDataArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.frag_search_result, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        DataUsers workerData = workerDataArrayList.get(position);

        String careerStrLan = holder.itemView.getResources().getString(R.string.career);
        String specificationStrLan = holder.itemView.getResources().getString(R.string.specification);
        String phoneStrLan = holder.itemView.getResources().getString(R.string.phone);
        String cityStrLan = holder.itemView.getResources().getString(R.string.city);
        String addressStrLan = holder.itemView.getResources().getString(R.string.address);
        String countryStrLan = holder.itemView.getResources().getString(R.string.country);

        if (!workerData.getSpecification().equals("noSpecification")){

            holder.cardView.setVisibility(View.VISIBLE);

            holder.fullName.setText(workerData.getFullName());
            holder.career.setText(careerStrLan+" : "+workerData.getCareer());
            holder.specification.setText(specificationStrLan+" : "+workerData.getSpecification());
            holder.phone.setText(phoneStrLan+" : "+workerData.getPhoneNumber());
            holder.city.setText(cityStrLan+" : "+workerData.getCity());
            holder.address.setText(addressStrLan+" : "+workerData.getAddress());
            holder.country.setText(countryStrLan+" : "+workerData.getCountry());
            Picasso.get().load(workerData.getProfileImage()).into(holder.circleProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return workerDataArrayList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView fullName;
        TextView phone;
        TextView career;
        TextView specification;
        TextView address;
        TextView city;
        TextView country;
        CircleImageView circleProfileImage;
        CardView cardView;
        TextView line;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.username_search);
            career = itemView.findViewById(R.id.search_career);
            specification = itemView.findViewById(R.id.search_specialization);
            phone = itemView.findViewById(R.id.search_phone);
            city = itemView.findViewById(R.id.search_city);
            address = itemView.findViewById(R.id.search_address);
            country = itemView.findViewById(R.id.search_country);
            line = itemView.findViewById(R.id.line);

            cardView = itemView.findViewById(R.id.card_view_search);
            circleProfileImage = itemView.findViewById(R.id.image_profile_search);

            cardView.setVisibility(View.GONE);

            circleProfileImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileSearchResultActivity.class);
                int i = getAdapterPosition();
                intent.putExtra("fullName", workerDataArrayList.get(i).getFullName());
                intent.putExtra("career", workerDataArrayList.get(i).getCareer());
                intent.putExtra("specification", workerDataArrayList.get(i).getSpecification());
                intent.putExtra("phoneNumber", workerDataArrayList.get(i).getPhoneNumber());
                intent.putExtra("address", workerDataArrayList.get(i).getAddress());
                intent.putExtra("city", workerDataArrayList.get(i).getCity());
                intent.putExtra("country", workerDataArrayList.get(i).getCountry());
                intent.putExtra("profileImage", workerDataArrayList.get(i).getProfileImage());
                intent.putExtra("userId", workerDataArrayList.get(i).getUserId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }
    }
}
