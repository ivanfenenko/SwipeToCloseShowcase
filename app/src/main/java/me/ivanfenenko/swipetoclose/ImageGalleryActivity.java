package me.ivanfenenko.swipetoclose;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by klickrent on 16/08/16.
 */

public class ImageGalleryActivity extends AppCompatActivity {

    public static final int CODE_IMAGE_GALLERY = 3516;

    public static final String EXTRA_IMAGES = "EXTRA_IMAGES";
    public static final String EXTRA_SELECTED = "EXTRA_SELECTED";

    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private int itemSelected = 0;

    public static void launchActivity(Activity activity,
                                      ArrayList<String> images,
                                      View sharedView,
                                      int selected) {
        Intent intent = new Intent(activity, ImageGalleryActivity.class);
        intent.putExtra(EXTRA_IMAGES, images);
        intent.putExtra(EXTRA_SELECTED, selected);
        if (AndroidUtils.isLollipop()) {
            ActivityOptions transitionActivityOptions = ActivityOptions
                    .makeSceneTransitionAnimation(activity, sharedView, ViewCompat.getTransitionName(sharedView));
            Bundle bundle = transitionActivityOptions.toBundle();
            activity.startActivityForResult(intent, CODE_IMAGE_GALLERY, bundle);
        } else {
            activity.startActivityForResult(intent, CODE_IMAGE_GALLERY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        ButterKnife.bind(this);

        ActivityCompat.postponeEnterTransition(this);
        if (AndroidUtils.isLollipop()) getWindow().setExitTransition(new Fade());

        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(null);

        List<String> images = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_IMAGES);
        itemSelected = getIntent().getIntExtra(EXTRA_SELECTED, 0);

        FullScreenPagerAdapter imagePagerAdapter = new FullScreenPagerAdapter(this, images);
        viewPager.setAdapter(imagePagerAdapter);

        // This is used here to postpone animation until image view is loaded
        imagePagerAdapter.setPagerLoadedCallback(() -> ActivityCompat.startPostponedEnterTransition(ImageGalleryActivity.this));
        imagePagerAdapter.setFadeViews(appBarLayout);
        imagePagerAdapter.setBackgroundView(coordinatorLayout);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(viewPager.getCurrentItem() + 1 + " / " + viewPager.getAdapter().getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (viewPager.getAdapter().getCount() > itemSelected) {
            viewPager.setCurrentItem(itemSelected);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == itemSelected)
            super.onBackPressed();
        else {
            Intent i = new Intent();
            i.putExtra(EXTRA_SELECTED, viewPager.getCurrentItem());
            setResult(RESULT_OK, i);
            finish();
        }
    }

}
