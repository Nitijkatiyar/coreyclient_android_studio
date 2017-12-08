package com.coremobile.coreyhealth.newui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coremobile.coreyhealth.JSONHelperClass;
import com.coremobile.coreyhealth.ListItemObj;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ChildIconAdapter extends
        ArrayAdapter<RowDisplayObject> {
    private String TAG = "Corey_ChildIconAdapter";
    protected static final int REQUEST_FOR_ACTIVITY_CODE = 0;
    private Context mContext;
    private ArrayList<RowDisplayObject> dashBoardData;
    int resource;
    int Callertab;
    int Quadrent = 3;
    int mMessageId;
    String tobeH;
    String objname;
    private GridView mListView;
    int isnewColor;
    int width;
    int height;
    Matrix matrix;
    Boolean isPortrait;
    float scaleddensity;
    int adjustfactor;
    HashMap<String, String> mStatusColor = new HashMap<String, String>();
    HashMap<String, ListItemObj> listItemObjMap;
    HashMap<Integer, Boolean> oneTouchIcon = new HashMap<>();


    String mAppName;
    // private static String APP_PATH = "/data/data/com.coremobile.coreyhealth/files/";
    private static String APP_PATH;

    public ChildIconAdapter(Context context,
                            GridView listView, int textViewResourceId,
                            ArrayList<RowDisplayObject> mRowTwoObjectData, int msgId, HashMap<String, ListItemObj> listItemObjMap) {
        super(context, textViewResourceId, mRowTwoObjectData);
        // TODO Auto-generated constructor stub
        mListView = listView;
        mContext = context;
        this.listItemObjMap = listItemObjMap;
        resource = textViewResourceId;
        dashBoardData = mRowTwoObjectData;
        mMessageId = msgId;
        MyApplication application = (MyApplication) mContext.getApplicationContext();
        APP_PATH = application.AppConstants.getAppFilesPath();
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        isnewColor = jsonHelperClass.getIsNewColour();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false;
        }
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isPortrait = true;
        }
        scaleddensity = displayMetrics.scaledDensity;
        Log.d(TAG, "view scaleddensity =" + scaleddensity);

        adjustfactor = (int) (scaleddensity * 50.0);
        // create a matrix for the manipulation
        matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleddensity, scaleddensity);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder holder;
            LayoutInflater liService = LayoutInflater.from(mContext);
            if (convertView == null) {
                convertView = liService.inflate(resource, null);
                RelativeLayout rr = (RelativeLayout) convertView
                        .findViewById(R.id.childlayout);
                int gridwidth = rr.getWidth();
                int gridheight = rr.getHeight();
                Log.d(TAG, "gridwidth =" + gridwidth + "gridheight" + gridheight);
                holder = new ViewHolder();

                holder.mainText = (TextView) convertView
                        .findViewById(R.id.child_mainText);

                holder.subscript = (TextView) convertView
                        .findViewById(R.id.child_subscript);
                holder.switchIv = (ImageView) convertView.findViewById(R.id.switch_image);

                holder.iv = (ImageView) convertView.findViewById(R.id.child_image);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }
            holder.subscript.setTag(dashBoardData.get(position));
            int imageh = holder.iv.getHeight();
            int imagew = holder.iv.getWidth();
            if (dashBoardData.get(position) != null) {

                holder.mainText.setText(dashBoardData.get(position).getDisplayName());
                //holder.mainText.setLines(2);
                String sbscript = dashBoardData.get(position).getDisplaySubscript();
                holder.mainText.append(" \n");
                holder.mainText.append(sbscript);
                holder.mainText.setLines(2);

                Log.d(TAG, "Portrait = " + isPortrait);
                if (isPortrait) {
                    holder.iv.getLayoutParams().height = (height / 4) - adjustfactor;
                } else {
                    holder.iv.getLayoutParams().height = (height / 4) - adjustfactor;
                }

                holder.iv.requestLayout();

                if (dashBoardData.get(position).isEditable) {
                    holder.subscript.setVisibility(View.VISIBLE);
//                    int clor = mContext.getResources().getColor(R.color.holo_green_lght);
//                    holder.subscript.setBackgroundColor(clor);

                    if (dashBoardData.get(position).getEditText() != null) {
                        holder.subscript.setText(dashBoardData.get(position).getEditText());
                    }
//                    else holder.subscript.setText("Update");
                } else {
                    holder.subscript.setVisibility(View.VISIBLE);
                    int clor = mContext.getResources().getColor(R.color.white);
                    holder.subscript.setBackgroundColor(clor);
                }
                String path;

                if (dashBoardData.get(position).getObjType().equalsIgnoreCase("Switch")) {
                    holder.switchIv.setVisibility(View.VISIBLE);
                    Log.d("Status", "" + dashBoardData.get(position).getObjectStatus() + ".." + dashBoardData.get(position).getDisplayName());
                    String imageIncompletePath = listItemObjMap.get("ToggleStatuses").getListItemArray().get(0).getImage();
                    String imageCompletePath = listItemObjMap.get("ToggleStatuses").getListItemArray().get(1).getImage();
                    String status = "";
                    if (MessageTabActivityCMN.checkedState.get(dashBoardData.get(position).getObjectId()) != null) {
                        status = MessageTabActivityCMN.checkedState.get(dashBoardData.get(position).getObjectId());
                    } else {
                        status = dashBoardData.get(position).getObjectStatus();
                    }
                    if (status != null && status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("YES")) {
                        if (imageCompletePath != null) {
                            Picasso.with(mContext)
                                    .load(imageCompletePath)
                                    .placeholder(R.drawable.ic_placeholderswitch)
                                    .centerCrop()
                                    .resize(30, 30)
                                    .into(holder.switchIv);
                        } else {
                            holder.switchIv.setImageResource(R.drawable.completed);
                        }
//                        ImageLoader imgLoader = new ImageLoader(mContext);
//                        Log.e("ImagePath", "" + imageCompletePath);
//                        try {
//                            imgLoader.DisplayImage(imageCompletePath, R.drawable.ic_placeholderswitch,
//                                    holder.switchIv);
//                        } catch (Exception e) {
//                            e.getMessage();
//                        }
//                        oneTouchIcon.put(position, true);
                    } else {
                        if (imageIncompletePath != null) {
                            Picasso.with(mContext)
                                    .load(imageIncompletePath)
                                    .placeholder(R.drawable.ic_placeholderswitch)
                                    .centerCrop()
                                    .resize(30, 30)
                                    .into(holder.switchIv);
                        } else {
                            holder.switchIv.setImageResource(R.drawable.incomplete);
                        }
//                        ImageLoader imgLoader = new ImageLoader(mContext);
//                        try {
//                            imgLoader.DisplayImage(imageIncompletePath, R.drawable.ic_placeholderswitch,
//                                    holder.switchIv);
//                        } catch (Exception e) {
//                            e.getMessage();
//                        }
//                        oneTouchIcon.put(position, true);
                    }
                } else {
                    holder.switchIv.setVisibility(View.GONE);
                    Boolean isnew = dashBoardData.get(position).getIsNew();

                    if (isnew) {
                        holder.mainText.setBackgroundColor(isnewColor);

                    } else {
                        holder.mainText.setBackgroundColor(Color.WHITE);
                    }
                }
                String img1 = dashBoardData.get(position).getImageUrl1();
                if (!TextUtils.isEmpty(img1) && img1 != null) {
                    path = img1.toString();
                } else {
                    path = dashBoardData.get(position).getImageUrl().toString();

                }

                Picasso.with(mContext).load(path).placeholder(R.drawable.loading_icon).into(holder.iv);


                if (dashBoardData.get(position).getIsFullImage()) {
                    Log.d(TAG, "Isfull image is" + dashBoardData.get(position).getIsFullImage());
                    holder.mainText.setVisibility(View.GONE);
                    holder.subscript.setVisibility(View.GONE);
                    int bkgndcolor = mContext.getResources().getColor(R.color.transparent);
                    holder.iv.setBackgroundColor(bkgndcolor);
                    holder.iv.setPadding(0, 0, 0, 0);
                    holder.iv.setScaleType(ScaleType.FIT_XY);
                }
            }

//            if (oneTouchIcon.get(position) != null && oneTouchIcon.get(position)) {
//                ImageLoader imgLoader = new ImageLoader(mContext);
//                String imageCompletePath = listItemObjMap.get("ToggleStatuses").getListItemArray().get(1).getImage();
//                try {
//                    imgLoader.DisplayImage(imageCompletePath, R.drawable.ic_placeholderswitch,
//                            holder.switchIv);
//                } catch (Exception e) {
//                    e.getMessage();
//                }
//            } else if (oneTouchIcon.get(position) != null && !oneTouchIcon.get(position)) {
//                ImageLoader imgLoader = new ImageLoader(mContext);
//                String imageIncompletePath = listItemObjMap.get("ToggleStatuses").getListItemArray().get(0).getImage();
//                try {
//                    imgLoader.DisplayImage(imageIncompletePath, R.drawable.ic_placeholderswitch,
//                            holder.switchIv);
//                } catch (Exception e) {
//                    e.getMessage();
//                }
//            }
            holder.subscript.setTag(position);
            return convertView;


        } catch (Exception e) {
            e.getMessage();
            return convertView;

        }

    }


    class ViewHolder {

        public ImageView iv, switchIv;
        //	public ImageView editiv;
        public TextView subscript;
        public TextView mainText;

    }
}