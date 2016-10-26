package me.ivanfenenko.swipetoclose;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.product_image_pager) ViewPager viewPager;

    private ViewPagerImageAdapter adapter;
    private ArrayList<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        imageList.add("https://pp.vk.me/c636027/v636027619/2a97c/OSy1iqY_KCo.jpg");
        imageList.add("https://pp.vk.me/c836621/v836621619/620c/h-eCZ2u0A4c.jpg");
        imageList.add("https://pp.vk.me/c837621/v837621619/37e3/80_l7IbzpDE.jpg");
        imageList.add("https://pp.vk.me/c636019/v636019619/2c720/rSdQ0OPoMkE.jpg");
        imageList.add("https://pp.vk.me/c630619/v630619619/50dce/71yDN3in9qc.jpg");

        adapter = new ViewPagerImageAdapter(this, imageList);
        viewPager.setAdapter(adapter);

        adapter.setClickCallback(
                (int position, View sharedView) -> {
                    if (AndroidUtils.isLollipop()) {
                        getWindow().setSharedElementEnterTransition(new DetailsTransition());
                        getWindow().setEnterTransition(new Fade());
                        getWindow().setSharedElementReturnTransition(new DetailsTransition());
                    }
                    ImageGalleryActivity.launchActivity(MainActivity.this, imageList, sharedView, position);
                }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ImageGalleryActivity.CODE_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {
                ActivityCompat.postponeEnterTransition(this);
                int page = data.getIntExtra(ImageGalleryActivity.EXTRA_SELECTED, -1);
                if (page != -1) {
                    viewPager.setCurrentItem(page, false);
                }
                ActivityCompat.startPostponedEnterTransition(MainActivity.this);
            }
        }
    }
}
