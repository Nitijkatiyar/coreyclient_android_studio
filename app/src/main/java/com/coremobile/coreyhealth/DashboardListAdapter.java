package com.coremobile.coreyhealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coremobile.coreyhealth.newui.Fields;
import com.coremobile.coreyhealth.newui.HorizontalListView;
import com.coremobile.coreyhealth.newui.RowDisplayObject;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

//import android.util.Log;

@SuppressLint("DefaultLocale")
public class DashboardListAdapter extends ArrayAdapter<RowDisplayObject> {

    protected static final int REQUEST_FOR_ACTIVITY_CODE = 0;
    private static final String TAG = "Corey_DashboardListadapter";
    private Context mContext;
    private ArrayList<RowDisplayObject> dashBoardData;
    HashMap<String, ListItemObj> mListItemObjMap;
    ArrayList<String> statusArrayList = new ArrayList<String>();
    HashMap<String, String> mStatusColorHashMap = new HashMap<String, String>();
    int resource;
    int Callertab;
    int Quadrent;
    int mMessageId;
    CoreyDBHelper mCoreyDBHelper;
    String tobeH;
    String objname;
    private HorizontalListView mListView;
    public static Boolean mAutosync;
    String DashboardEditTextrow1;
    String DashboardEditTextrow2;
    String Dashboard121msgTextrow2;
    String ApplicationName;
    String EditText;
    MyApplication application;
    ArrayList<ListItem> mListItemArray;
    HashMap<String, String> mStatusColor = new HashMap<String, String>();
    int isnewColor;
    String UpdateBtnColour;
    int width;
    int height;
    float imgwidth;
    float imgheight;
    float imgscale;
    String mAppName;
    Matrix matrix;
    //  private static String APP_PATH= "/data/data/com.coremobile.coreyhealth/files/";
    private static String APP_PATH;

    public DashboardListAdapter(Context context, HorizontalListView listView, int textViewResourceId,
                                ArrayList<RowDisplayObject> mRowTwoObjectData, int callertab, int quadrent,
                                int messageID, HashMap<String, ListItemObj> ListItemObjMap, Boolean autosync, float[] ImageParms) {
        super(context, textViewResourceId, mRowTwoObjectData);
        // TODO Auto-generated constructor stub


        mListView = listView;
        mContext = context;
        resource = textViewResourceId;
        dashBoardData = mRowTwoObjectData;
        Callertab = callertab;
        Quadrent = quadrent;
        mMessageId = messageID;
        mCoreyDBHelper = new CoreyDBHelper(mContext);
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        isnewColor = jsonHelperClass.getIsNewColour();
        UpdateBtnColour = jsonHelperClass.getUpdateBtnColour();
        mListItemObjMap = ListItemObjMap;
        mStatusColorHashMap = jsonHelperClass.getStatusColourMap();
        if (mStatusColorHashMap != null)
            statusArrayList = new ArrayList<String>(mStatusColorHashMap.keySet());
        application = (MyApplication) mContext
                .getApplicationContext();
        APP_PATH = application.AppConstants.getAppFilesPath();
        ApplicationName = application.AppConstants.getAppName();
        DashboardEditTextrow1 = application.AppConstants.getDashboardEditTextRow1();
        DashboardEditTextrow2 = application.AppConstants.getDashboardEditTextRow2();
        Dashboard121msgTextrow2 = application.AppConstants.getDashboard121msgTextRow1();
        mAutosync = autosync;
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        Log.d(TAG, "view width =" + width + "height = " + height);
        float density = displayMetrics.density;
        float scaleddensity = displayMetrics.scaledDensity;
        Log.d(TAG, "view scaleddensity =" + scaleddensity + "density = " + density);
        imgwidth = ImageParms[0];
        imgheight = ImageParms[1];
        imgscale = ImageParms[2];


        // create a matrix for the manipulation
        matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleddensity, scaleddensity);

    }

    public int getLastMessageid() {
        String[] projection = new String[]{DatabaseProvider.KEY_MSGID};
        String sortOrder = DatabaseProvider.KEY_MSGID + " DESC";
         Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI, projection,
                DatabaseProvider.KEY_MESSAGE_TYPE + " =?",
                new String[]{DatabaseProvider.MSG_TYPE_MARKET_TRACKER},
                sortOrder);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            return -1;
        }
        int lastmessageid = cursor.getInt(0);
        cursor.close();
        return lastmessageid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder holder;
            LayoutInflater liService = LayoutInflater.from(mContext);
            final RowDisplayObject listItem = dashBoardData.get(position);

            if (convertView == null) {
        /*
         * LinearLayout linearLayout = new LinearLayout(mContext);
		 * linearLayout.setLayoutParams(new LayoutParams(200, 200));
		 */

                convertView = liService.inflate(resource, null);
                RelativeLayout rr = (RelativeLayout) convertView
                        .findViewById(R.id.dashboardlayout);
    /*	rr.setLayoutParams(new LayoutParams(mListView.getHeight(),
            mListView.getHeight())); */
                rr.setLayoutParams(new LayoutParams((width / 3) - 1,
                        mListView.getHeight()));
                rr.invalidate();
                rr.requestLayout();
                holder = new ViewHolder();

                holder.mainText = (TextView) convertView
                        .findViewById(R.id.dashboard_mainText);

                holder.subscript = (TextView) convertView
                        .findViewById(R.id.dashboard_subscript);
                holder.iv = (ImageView) convertView.findViewById(R.id.image);

                holder.editiv = (ImageView) convertView
                        .findViewById(R.id.editImage);


                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }
            holder.subscript.setTag(listItem);
            int imageh = holder.iv.getHeight();
            int imagew = holder.iv.getWidth();
            Log.d(TAG, "imageh =" + imageh + "imagew = " + imagew);
            if (listItem != null) {

                holder.mainText.setText(listItem.getDisplayName());
                Log.d(TAG, "dispcol =" + listItem.getDispCol());

                holder.subscript.setVisibility(View.GONE);
                holder.mainText.setTextSize(12);
                String sbscript = listItem.getDisplaySubscript();
                if (sbscript != null && !sbscript.equalsIgnoreCase("")) {
                    holder.mainText.setLines(2);
                    holder.mainText.append(" \n");
                    holder.mainText.append(sbscript);
                }


                if (listItem.isEditable) {
                    if (!mAutosync) {
                        holder.editiv.setVisibility(View.VISIBLE);
                    } else {
                        holder.editiv.setVisibility(View.VISIBLE);
                    }
                    holder.subscript.setVisibility(View.GONE);
                    if (Quadrent == 2) {
                        if (listItem.getEditText() != null)
                            holder.subscript.setText(listItem.getEditText());
                    } else if (Quadrent == 3 || Quadrent == 4) {
                        if (listItem.getEditText() != null)
                            holder.subscript.setText(listItem.getEditText());
                    }


                    int hex = Color.parseColor(UpdateBtnColour);
                    holder.editiv.setBackgroundColor(hex);
                    Log.d("Dashboardadaptor", "before objectstatus in iseditalb, objectname=" + listItem.getDisplayName());
                    Log.d("Dashboardadaptor", "before objectstatus in iseditalb, colour=" + holder.subscript.getDrawingCacheBackgroundColor());
                    if (listItem.getIsFullImage()) {
                        holder.mainText.setVisibility(View.GONE);
                        holder.subscript.setVisibility(View.GONE);
                        int bkgndcolor = mContext.getResources().getColor(R.color.light_gray);
                        holder.iv.setBackgroundColor(bkgndcolor);
                        holder.iv.setPadding(0, 0, 0, 0);
                    }
                    holder.editiv.setVisibility(View.GONE);
                    holder.subscript.setVisibility(View.GONE);
                } else {
                    if (!mAutosync) {
                        holder.editiv.setVisibility(View.GONE);
                        holder.subscript.setVisibility(View.GONE);
                        holder.subscript.setText(null);
                    } else {
                        if (Quadrent == 2) {
                            holder.editiv.setVisibility(View.GONE);
                            if (listItem.getEditText() != null)
                                holder.subscript.setText(listItem.getEditText());
                            holder.editiv.setBackgroundColor(Color.WHITE);
                        } else if (Quadrent == 3 && listItem.isEditable) {
                            if (listItem.getEditText() != null)
                                holder.subscript.setText(listItem.getEditText());
                        } else if (Quadrent == 4 && listItem.isEditable) {
                            if (listItem.getEditText() != null)
                                holder.subscript.setText(listItem.getEditText());
                        } else {
                            holder.editiv.setVisibility(View.GONE);
                            holder.subscript.setVisibility(View.GONE);
                            holder.subscript.setText(null);
                        }

                        holder.subscript.setVisibility(View.GONE);
                        Log.d(TAG, "item is not editable and image is scaled");

                    }
                    if (listItem.getIsFullImage()) {
                        holder.mainText.setVisibility(View.GONE);
                        holder.subscript.setVisibility(View.GONE);
                        int bkgndcolor = mContext.getResources().getColor(R.color.light_gray);
                        holder.iv.setBackgroundColor(bkgndcolor);
                        holder.iv.setPadding(0, 0, 0, 0);
                    }
                    holder.editiv.setVisibility(View.GONE);
                    holder.subscript.setVisibility(View.GONE);
                }

                Boolean isnew = listItem.getIsNew();

                if (isnew) {
                    holder.mainText.setBackgroundColor(isnewColor);

                } else {
                    holder.mainText.setBackgroundColor(Color.WHITE);
                }
                if (listItem.getObjectStatus() != null) {
                    String colorRaw = null;
                    String colour;
                    String Lstvalname;
                    int hex;
                    Lstvalname = getListValueName(listItem);
                    Log.d(TAG, "Lstvalname=" + Lstvalname);
                    if (Lstvalname != null) {
                        ListItemObj listitemobj = mListItemObjMap.get(Lstvalname);
                        String sts = listItem.getObjectStatus();
                        colorRaw = listitemobj.getColor(listItem.getObjectStatus());
                    }
                    if (colorRaw != null) {
                        colour = "#" + colorRaw;
                    } else colour = UpdateBtnColour;
                    hex = Color.parseColor(colour);
                    holder.editiv.setBackgroundColor(hex);
                    holder.subscript.setVisibility(View.VISIBLE);
                    holder.subscript.setVisibility(View.GONE);

                } else {
                    String colour = UpdateBtnColour;
                }
                String path;

                if (Quadrent == 3) {

                    String img1 = listItem.getImageUrl1();
                    if (!TextUtils.isEmpty(img1) && img1 != null) {
                        path = img1.toString();
                    } else {
                        path = listItem.getImageUrl().toString();
                    }
                     Picasso.with(mContext).load(path).placeholder(R.drawable.loading_icon).into(holder.iv);
                    holder.subscript.setVisibility(View.GONE);
                } else {
                    path = listItem.getImageUrl().toString();

                }

                Picasso.with(mContext).load(path).placeholder(R.drawable.loading_icon).into(holder.iv);

                FileInputStream in;
                BufferedInputStream buf;


                if ("Phone Number".equalsIgnoreCase(listItem.getDisplayName())) {
                    holder.subscript
                            .setAutoLinkMask(android.text.util.Linkify.PHONE_NUMBERS);
                } else {
                    holder.subscript.setAutoLinkMask(0x0);
                }
                holder.subscript.setVisibility(View.GONE);
            }

            holder.subscript.setTag(position);
            holder.subscript.setVisibility(View.GONE);
            return convertView;

        } catch (Exception e) {
            e.getMessage();
            return convertView;

        }

    }

    OnClickListener myClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Integer pos = (Integer) v.getTag();

            Intent detailedIntent = new Intent(mContext,
                    DetailedApplicationData.class);
            detailedIntent.putExtra("ObjID", dashBoardData.get(pos)
                    .getObjectid());
            detailedIntent.putExtra("ObjName", dashBoardData.get(pos)
                    .getDisplayName());
            detailedIntent.putExtra("IsEditable", true);
            detailedIntent.putExtra("msgid", mMessageId);

            mContext.startActivity(detailedIntent);

        }

    };


    class ViewHolder {

        public ImageView iv;
        public ImageView editiv;
        public TextView subscript;
        public TextView mainText;
        public ProgressBar progressBar;

    }

    public String getListValueName(RowDisplayObject lItem) {

        ArrayList<Fields> ListofFields;
        ListofFields = lItem.fieldsList;
        String lstval = null;
        for (Fields flst : ListofFields) {
            //	 if(flst.getlistValue()!= null)
            if (flst.getHasStatus().equalsIgnoreCase("true")) {
                lstval = flst.getlistValue();
                break;
            }
        }


        return lstval;
    }
}
