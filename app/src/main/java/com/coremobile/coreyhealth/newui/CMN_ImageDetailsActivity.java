package com.coremobile.coreyhealth.newui;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CMN_ImageDetailsActivity extends BaseActivityCMN {

    private TextView titleTextView, downloadTextVw, shareTextVw;
    private ImageView imageView, actionImageVw;
    private LinearLayout actionLayoutVw;
    private static boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        String title = getIntent().getStringExtra("title");
        final String image = getIntent().getStringExtra("image");
        titleTextView = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.grid_item_image);
        downloadTextVw = (TextView) findViewById(R.id.downloadTextVw);
        shareTextVw = (TextView) findViewById(R.id.shareTextVw);
        actionImageVw = (ImageView) findViewById(R.id.actionImageVw);
        actionLayoutVw = (LinearLayout) findViewById(R.id.actionLayoutVw);
        titleTextView.setText(Html.fromHtml(title));

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.view_image_detail));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.with(this).load(image).into(imageView);

        actionImageVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(flag==false) {
                    actionLayoutVw.setVisibility(View.VISIBLE);
                    flag=true;
                }
                else if(flag==true){
                    actionLayoutVw.setVisibility(View.GONE);
                    flag=false;
                }
            }

        });

        downloadTextVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String currentTimeStampStr=dateFormat.format(date); //2017/07/11 12:08:43
                Picasso.with(getApplicationContext()).load(image).into(new TargetPhoneGallery(getContentResolver(), "image_"+currentTimeStampStr, ""));

                actionLayoutVw.setVisibility(View.GONE);
            }
        });

        shareTextVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    class TargetPhoneGallery implements Target {
        private final WeakReference<ContentResolver> resolver;
        private final String name;
        private final String desc;

        private String response = null;

        public TargetPhoneGallery(ContentResolver r, String name, String desc) {
            this.resolver = new WeakReference<ContentResolver>(r);
            this.name = name;
            this.desc = desc;
        }

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
            ContentResolver r = resolver.get();
            if (r != null) {
                try {
                    MediaStore.Images.Media.insertImage(r, bitmap, name, desc);
                    response = "Image has been saved into gallery";
                } catch (Exception e) {
                    response = "download failed";
                } finally {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
            Toast.makeText(getApplicationContext(), "download failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        flag=false;
        actionLayoutVw.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        actionLayoutVw.setVisibility(View.GONE);

        flag=false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
