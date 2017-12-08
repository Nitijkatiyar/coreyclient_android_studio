package com.coremobile.coreyhealth.newui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.CoreyDBHelper;
import com.coremobile.coreyhealth.DatabaseProvider;
import com.coremobile.coreyhealth.DetailedApplicationData;
import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.RetrieveCallerInfoTask;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

import static com.coremobile.coreyhealth.newui.MessageTabActivityCMN.patientInfo;

public final class TestFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private static final String TAG = "Corey_TestFragment";
    static int mMsgId;
    static int mMesId;
    static String mObjId;
    static String mSubject;
    static String APP_PATH;
    static ArrayList<MessageItem> mMessageItems;
    private CoreyDBHelper mCoreyDBHelper;
    int bmapwidth;
    ViewGroup root;
    int bmapheight;

    public static TestFragment newInstance(String content, ArrayList<MessageItem> messageItems, int num) {
        TestFragment fragment = new TestFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        Log.d("TestFragment", "constructor num" + num);
        mMessageItems = messageItems;
        Log.d("TestFragment", "MessageItems" + mMessageItems);
        fragment.setArguments(args);

        fragment.mContent = content.toString();


        return fragment;
    }

    public TestFragment() {
        // TODO Auto-generated constructor stub
    }


    private String mContent = "???";
    private int mNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        Log.d("TestFragment", "OnCreate mNum" + mNum);
        if (mNum > 0)
            mNum--;
        mCoreyDBHelper = new CoreyDBHelper(getActivity());

        try {
            mSubject = mMessageItems.get(mNum).subject;
            mMsgId = mMessageItems.get(mNum).msgid;
            Log.d("TestFragment", "  Oncreate subject=" + mSubject);
            Log.d("TestFragment", "  Oncreate mMsgId=" + mMsgId);
        } catch (Exception e) {
            Log.d("TestFragment", "Exception in Oncreate");
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Sync failed! Please login again", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    /*
     * This method is under the control of Android os.  Android maintain some cache where test fragments
     * are cached in. So using the message id or any other parameter from this method will not work
     * consistantly. When user click onclick or any other function, get the parameters from messagescheduletabactivty
     * at that time only.
     * Actually onpageselected event of addslideframe is the uptodate one.  Trace everything from there
     * Eventhough page is changed for the user, this function may not be called because android
     * supplies the fragment from cache.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FileInputStream in;
        BufferedInputStream buf;
        Uri uri;
        String path;
        Boolean imgurlpresent = false;
        Boolean imgurl1present = false;
        mMesId = MsgScheduleTabActivity.mMessageId;


        root = (ViewGroup) inflater.inflate(R.layout.row0layout, null);

        //if(MyApplication.INSTANCE.AppConstants.getAppName()!= "SALES")

        ImageView imageView = (ImageView) root.findViewById(R.id.image);
        TextView text = (TextView) root.findViewById(R.id.text1);

        if (MessageTabActivityCMN.mAutosync) {
            text.setAutoLinkMask(0x04);
            text.setMovementMethod(new ScrollingMovementMethod());
        }

        APP_PATH = MyApplication.INSTANCE.AppConstants.getAppFilesPath();
        // Need to change the parameters as per app
        //  String ImgUrl = mCoreyDBHelper.getObjImageUrl(mMsgId, "PatientData", "Calendar");
        //  String ImgUrl = mCoreyDBHelper.getObjImageUrlbyqdr(mMsgId, 1, "Calendar");
        //    String ImgUrl = mCoreyDBHelper.getObjImageUrlbyqdr(mMesId, 1, "Calendar");
        String ImgUrl = mMessageItems.get(mNum).ImageUrl1;
        if (ImgUrl != null && !ImgUrl.equalsIgnoreCase("") && !ImgUrl.isEmpty()) {
            imgurlpresent = true;
            uri = Uri.parse(ImgUrl);
            ImgUrl = uri.getLastPathSegment();
            Log.d(TAG, "ImgUrl=" + ImgUrl);
            path = APP_PATH
                    + ImgUrl.toString().toLowerCase()
                    .replaceAll("\\s+", "").replaceAll("-", "").replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "");
        /*
         * set the image for 1st icon
		 */
            try {
                in = new FileInputStream(path);
                buf = new BufferedInputStream(in);
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                imageView.setImageBitmap(bMap);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }
            } catch (Exception e) {
                Log.d("Error reading file", e.toString());
            }
        }
        //  String ImgUrl1= mCoreyDBHelper.getObjImageUrl(mMsgId, "PatientData", "MessagingObj");
        //   String ImgUrl1= mCoreyDBHelper.getObjImageUrlbyqdr(mMsgId, 1, "MessagingObj");
     /*   if(	bMap!=null)
        {
        	  bmapwidth=bMap.;
        	     bmapheight;
        } */
        //   String ImgUrl1= mCoreyDBHelper.getObjImageUrlbyqdr(mMesId, 1, "MessagingObj");
        String ImgUrl1 = mMessageItems.get(mNum).ImageUrl2;
        if (ImgUrl1 != null && !ImgUrl1.equalsIgnoreCase("") && !ImgUrl1.isEmpty()) {
            imgurl1present = true;
            uri = Uri.parse(ImgUrl1);
            ImgUrl1 = uri.getLastPathSegment();

            Log.d(TAG, "ImgUrl1=" + ImgUrl1);
   
		/*
         * set the image for 2nd icon -message
		 */
            path = APP_PATH
                    + ImgUrl1.toString().toLowerCase()
                    .replaceAll("\\s+", "").replaceAll("-", "").replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "");
            try {
                in = new FileInputStream(path);
                buf = new BufferedInputStream(in);
                Bitmap bMap1 = BitmapFactory.decodeStream(buf);
                imageView.setImageBitmap(bMap1);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }
            } catch (Exception e) {
                Log.d("Error reading file", e.toString());
            }
        }
        //   int imageid= MyApplication.INSTANCE.AppConstants.getRow0DrawableId();
        //    imageView.setImageDrawable(getResources().getDrawable(imageid)); //For sales
        //  imageView1.setImageDrawable(getResources().getDrawable(R.drawable.corefy));
        // imageView1.setImageResource(android.R.drawable.ic_dialog_email);
        //    imageView1.setImageResource(drawable.msg_icon);
        //    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_calendar)); //For sales
        //     imageView.setImageDrawable(getResources().getDrawable(R.drawable.patient_icon))  //For health

        imageView.setOnClickListener(MessageDlg);

        //  text.setGravity(Gravity.CENTER);
        //  mContent = Html.fromHtml(mContent).toString();
        text.setText(Html.fromHtml(mContent));
         patientInfo = mContent;
        //  text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0);
        //text.setCompoundDrawablePadding(5);


        if (mMessageItems.get(mNum).MsgObjId != null) {
            mObjId = mMessageItems.get(mNum).MsgObjId;
        }
        Log.d(TAG, "layout generated");
        return root;
    }

    private OnClickListener MessageDlg = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // Log.d("TestFragment","Inside Image onclick");
            Toast.makeText(getActivity(), "image clicked", Toast.LENGTH_LONG);
            Intent intent = new Intent(getActivity(),
                    DetailedApplicationData.class);

            // intent.putExtra("msgid", mMsgId);
            intent.putExtra("msgid", mMesId);
            intent.putExtra("Appname", "Outlook");
            intent.putExtra("AppQuadId", 1);
            intent.putExtra("ObjectName", "Calendar");
            intent.putExtra("ApplicationId", 3);

            intent.putExtra("ObjID", mObjId);
            intent.putExtra("IsEditable", true);
            // Log.i("TestFragment","Mesagedlg messageid=" +mMesId);
            if (mObjId != null) {
                startActivity(intent);
            }

        }
    };
    private OnClickListener patientClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            MessageItem msg = mMessageItems.get(mNum);
            if (msg.type == DatabaseProvider.MSG_TYPE_PHONE_CALL) {
                int msgId;
                msgId = mCoreyDBHelper.getMsgIdForCaller(msg.phonenumber);
                final String objName = mCoreyDBHelper.getPatientNamebyMsgId(msgId);
                Log.d(TAG, "Click on a phone msg " + msgId);
                if (msgId == -1) {
                    new RetrieveCallerInfoTask(getActivity(), msg.name, msg.phonenumber, msg.company) {
                        protected void onPostExecute(Integer msgId) {
                            super.onPostExecute(msgId);
                            Log.d(TAG, "retrieved phon msgId " + msgId);
                            if (msgId >= 0) {
                                startDetailsActivity(msgId, objName);
                            }
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    startDetailsActivity(msgId, objName);
                }
            } else {
                //           startDetailsActivity(mMsgId);

                Log.d("TestFragment", "onclick messageid=" + mMesId);
                mMesId = MsgScheduleTabActivity.mMessageId;
                Log.d("TestFragment", "onclick messageid1=" + mMesId);
                startDetailsActivity(mMesId, "");
            }
        }
    };

    private void startDetailsActivity(int msgId, String objName) {
        Intent intent = new Intent(getActivity(),
                DetailedApplicationData.class);
        //     intent.putExtra("Messageid", mMsgId);
        intent.putExtra("msgid", msgId);
        intent.putExtra("Appname", "Outlook");
        intent.putExtra("AppQuadId", 1);
        intent.putExtra("ObjectName", "Calendar");
        intent.putExtra("ApplicationId", 3);
        intent.putExtra("ObjName", objName);
        String ObjId = MsgScheduleTabActivity.getCache().getRow0ObjectId();
        if (ObjId == null) return; //fix for   COREY-2253

        //  int nObjId= ObjId
        intent.putExtra("ObjID", ObjId);
        //  Log.i("Testfragment","Onpage selected Position=" +position);
        //   Log.i("TestFragment","startdetailactivity messageid=" +msgId);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
