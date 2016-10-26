package me.ivanfenenko.swipetoclose;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.ivanfenenko.swipetoclose.imageadapter.RecyclingPagerAdapter;

/**
 * Created by klickrent on 26/10/2016.
 */

public class ViewPagerImageAdapter extends RecyclingPagerAdapter {

    private Context context;
    private List<String> imageIdList;
    private ClickCallback callback;

    public ViewPagerImageAdapter(Context context, List<String> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList;
    }

    public void setClickCallback(ClickCallback callback) {
        this.callback = callback;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return imageIdList.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
         ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new ImageView(context);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ViewCompat.setTransitionName(holder.imageView, "image_gallery_transition" + position);

        holder.imageView.setOnClickListener((View v) -> {
            if (callback != null) callback.onImageClicked(position, holder.imageView);
        });

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(context)
                .load(imageIdList.get(position))
                .into(holder.imageView);

        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    public interface ClickCallback {
        void onImageClicked(int position, View sharedView);
    }

}
