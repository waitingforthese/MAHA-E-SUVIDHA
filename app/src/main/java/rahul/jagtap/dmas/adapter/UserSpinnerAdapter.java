package rahul.jagtap.dmas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import rahul.jagtap.dmas.R;
import rahul.jagtap.dmas.model.User;

//public class UserSpinnerAdapter extends ArrayAdapter<User> {
//
//    public UserSpinnerAdapter(Context context, ArrayList<User> algorithmList) {
//        super(context, 0, algorithmList);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable
//            View convertView, @NonNull ViewGroup parent) {
//        return initView(position, convertView, parent);
//    }
//
//    @Override
//    public View getDropDownView(int position, @Nullable
//            View convertView, @NonNull ViewGroup parent) {
//        return initView(position, convertView, parent);
//    }
//
//    private View initView(int position, View convertView, ViewGroup parent) {
//        // It is used to set our custom view.
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_list, parent, false);
//        }
//
//        TextView tvName = convertView.findViewById(R.id.tvName);
//        TextView tvPhone = convertView.findViewById(R.id.tvPhone);
//        User currentItem = getItem(position);
//
//        // It is used the name to the TextView when the
//        // current item is not null.
//        if (currentItem != null) {
//            tvName.setText(currentItem.getName());
//            tvPhone.setText(currentItem.getContactNo() + "(" + currentItem.getEmail() + ")");
//        }
//        return convertView;
//    }
//}