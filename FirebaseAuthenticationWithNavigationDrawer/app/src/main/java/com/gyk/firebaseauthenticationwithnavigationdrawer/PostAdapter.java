package com.gyk.firebaseauthenticationwithnavigationdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends BaseAdapter {
    private List<Post> postList;
    private LayoutInflater layoutInflater;

    public PostAdapter(List<Post> postList, Context context){
        this.postList = postList;
        this.layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int i) {
        return postList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView;
        rowView = layoutInflater.inflate(R.layout.post_row_layout,null);
        ImageView photo = (ImageView) rowView.findViewById(R.id.imageViewPhoto);
        TextView title = (TextView) rowView.findViewById(R.id.textViewTitle);
        TextView content = (TextView) rowView.findViewById(R.id.textViewContent);

        Post currentPost = postList.get(i);

        title.setText(currentPost.getTitle());
        content.setText(currentPost.getContent());
        photo.setImageResource(currentPost.getPhoto());

        return rowView;
    }
}
