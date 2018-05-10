package com.stp.ssm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.R;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;
import static android.view.View.OnClickListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.btnitemdelete;
import static com.stp.ssm.R.id.lblitemlist;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.list_item_adj;

public class ListItemAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public ListItemAdapter(Context context, int resource, ArrayList<String> data) {
        super(context, resource, data);
        this.mInflater = from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = mInflater.inflate(list_item_adj, parent, false);
            vh = new ViewHolder();

            vh.lblitemlist = (TextView) convertView.findViewById(lblitemlist);
            vh.btnitemdelete = (ImageButton) convertView.findViewById(btnitemdelete);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }


        String object = getItem(position);
        vh.lblitemlist.setText(object);
        vh.btnitemdelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteListener.OnDelete(position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView lblitemlist;
        ImageButton btnitemdelete;
    }
}
