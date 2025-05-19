package com.example.qlct.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qlct.R;

import java.util.List;

public class Category_adapter extends ArrayAdapter<Category_hdp> {

    public Category_adapter(@NonNull Context context, int resource, @NonNull List<Category_hdp> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.category_list_item, parent, false); // Giả sử tên file là category_list_item.xml

            holder = new ViewHolder();
            holder.categoryIconImageView = convertView.findViewById(R.id.category_icon);
            holder.categoryNameTextView = convertView.findViewById(R.id.category_txtview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category_hdp currentCategory = getItem(position);

        if (currentCategory != null) {
            holder.categoryNameTextView.setText(currentCategory.getCategory_name());

            Glide.with(getContext())
                    .load(currentCategory.getImageURL())
                    .placeholder(R.drawable.circle_icon)
                    .error(R.drawable.circle_icon)
                    .into(holder.categoryIconImageView);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView categoryIconImageView;
        TextView categoryNameTextView;
    }
}