package me.ivanfenenko.swipetoclose;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ivanfenenko.swipetoclose.imageadapter.RecyclingPagerAdapter;
import me.ivanfenenko.swipetoclose.widget.SwipeDismissContainer;

/**
 * Created by klickrent on 26/10/2016.
 */

public class FullScreenPagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private List<String> imageIdList;
    private PagerLoadedCallback pagerLoadedCallback;
    private ArrayList<View> fadeViews;
    private View backgroundView;

    private boolean isInfiniteLoop;
    private boolean pageLoaded = false;

    public FullScreenPagerAdapter(Context context, List<String> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList;
        isInfiniteLoop = false;
    }

    public void setPagerLoadedCallback(PagerLoadedCallback pagerLoadedCallback) {
        this.pagerLoadedCallback = pagerLoadedCallback;
    }

    public void setFadeViews(@NonNull View ... views) {
        fadeViews = new ArrayList<>(Arrays.asList(views));
    }

    public void setBackgroundView(View backgroundView) {
        this.backgroundView = backgroundView;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : imageIdList.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            holder.container = new SwipeDismissContainer(context);
            holder.imageView = new ImageView(context);
            holder.container.addView(holder.imageView);
            holder.container.setSwipeView(holder.imageView);
            holder.container.setFadeViews(fadeViews);
            holder.container.setBackgroundView(backgroundView);
            view = holder.container;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ViewCompat.setTransitionName(holder.imageView, "image_gallery_transition" + position);

        holder.imageView.setImageDrawable(null);
        String imagePath = imageIdList.get(position);
        Picasso.with(context).load(imagePath).into(holder.imageView);

        // Notify that image view is loaded
        if (!pageLoaded && pagerLoadedCallback != null) {
            view.post(() -> pagerLoadedCallback.pageLoaded());
        }
        pageLoaded = true;

        return view;
    }

    private static class ViewHolder {

        ImageView imageView;
        SwipeDismissContainer container;

    }

    public interface PagerLoadedCallback {
        void pageLoaded();
    }

}

