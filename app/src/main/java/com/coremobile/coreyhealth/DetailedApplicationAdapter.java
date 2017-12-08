package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.coremobile.coreyhealth.newui.Fields;
import com.coremobile.coreyhealth.newui.MessageActivityCMN;
import com.coremobile.coreyhealth.widget.CoreyEditText;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DetailedApplicationAdapter extends ArrayAdapter<Fields> {
    private Context mContext;
    private static String TAG = "Corey_DetailedApplicationAdapter";
    public ArrayList<Fields> mFieldItems;
    HashMap<String, ListItemObj> mListItemObjMap;
    ArrayList<ListItem> mListItemobjArray;
    ArrayList<ListItem> mStsListItemobjArray;
    ArrayList<String> mListItemArray = new ArrayList<String>();
    ArrayList<String> statusArrayList = new ArrayList<String>();
    HashMap<String, String> mStatusColorHashMap = new HashMap<String, String>();
    //   ArrayList<ListItem>
    int resource;
    String title;
    int mQuad;
    boolean changeui;
    String organizationName;
    ArrayAdapter<String> mAutoCompleteAdapter;
    AutoCompleteTextView EditTextAuto;
    TextView textview1;
    JSONHelperClass jsonHelperClass;
    private String mNewNote;
    Fields listItem;
    private Activity mActivity;
    Dialog dialog;
    int year;
    int month;
    int day;
    int hour;
    int min;
    DatePicker dp;
    TimePicker tp;
    private static String APP_PATH;
    MyApplication application;
    DetailedViewHolder holder;
    String objectId;
    EditText subscript1;
    int mMsgId;
    public static int positionSelected = -1;
    public static View view;

    // public DetailedApplicationAdapter(Context _context, int _resource,
    //    ArrayList<Fields> _items, boolean isEditable, String _orgname) {
   /*
    * Constructor
    */
    public DetailedApplicationAdapter(Context _context, int _resource,
                                      ArrayList<Fields> _items, boolean isEditable, String _orgname, HashMap<String, ListItemObj> ListItemObjMap, Activity activity, String objectId, int mMsgId) {
        super(_context, _resource, _items);
        mContext = _context;
        resource = _resource;
        mFieldItems = _items;
        changeui = isEditable;
        this.objectId = objectId;
        this.mMsgId = mMsgId;
        mListItemObjMap = ListItemObjMap;
        mActivity = activity;
        jsonHelperClass = new JSONHelperClass();
        organizationName = _orgname;
        application = (MyApplication) mContext
                .getApplicationContext();
        APP_PATH = application.AppConstants.getAppFilesPath();
        //  mStatusColorHashMap=jsonHelperClass.getStatusColourMap();
        if (mListItemObjMap.get("DepartmentStatus") != null) {
            mStsListItemobjArray = mListItemObjMap.get("DepartmentStatus").ListItemArray;
            mStatusColorHashMap = getstatuscolormap(mStsListItemobjArray);
        }
        //  mAutoCompleteAdapter.clear();
        if (mStatusColorHashMap != null)
            statusArrayList = new ArrayList<String>(mStatusColorHashMap.keySet());
    }

    @Override
    public int getCount() {
        return mFieldItems.size();
    }

    @Override
    public Fields getItem(int position) {
        return mFieldItems.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater liService = LayoutInflater.from(mContext);
        Log.d(TAG, "position in getview =" + position);
        listItem = mFieldItems.get(position);


        if (changeui) {

            if (convertView == null) {
                holder = new DetailedViewHolder();
                convertView = liService.inflate(R.layout.patientdetailadapter,
                        null);

                //	holder.editText = (EditText) convertView
                //	.findViewById(R.id.line2edit);
                holder.editText = (CoreyEditText) convertView
                        //	holder.editText = (EditText) convertView
                        .findViewById(R.id.line2edit);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.line1);
                holder.textview1 = (TextView) convertView
                        .findViewById(R.id.line2);
                holder.textnew = (TextView) convertView
                        .findViewById(R.id.txtnew);
                //	holder.EditTextAuto = (AutoCompleteTextView) convertView
                //			.findViewById(R.id.lin2editauto);
                //	holder.EditTextAuto = (InstantCompleteTextView) convertView
                holder.EditTextAuto = (AutoCompleteTextView) convertView
                        .findViewById(R.id.lin2editauto);
                holder.mImageView = (ImageView) convertView
                        .findViewById(R.id.imageView1);

                convertView.setTag(holder);
            } else {
                holder = (DetailedViewHolder) convertView.getTag();
            }

            //   holder.editText.setTag(listItem);
            holder.editText.clearFocus();
            holder.EditTextAuto.clearFocus();
            holder.textview1.clearFocus();
            holder.editText.setTag(listItem);
            holder.EditTextAuto.setTag(listItem);
            holder.textview1.setTag(listItem);
            holder.editText.setHeight(40);

            Log.d(TAG, "I am called listItem.isEditable "
                    + listItem.isEditable);
            Log.d(TAG, "Fieldname =" + listItem.getFieldDispText());
            Log.d(TAG, "listvalue =" + listItem.getlistValue());
            Log.d(TAG, "IsNew =" + listItem.getIsNew());
            if (listItem.getIsNew() != null) {
                if (listItem.getIsNew().equalsIgnoreCase("true")) {
                    Log.d(TAG, "isnew is true");
                    holder.textnew.setVisibility(View.VISIBLE);
                } else {
                    holder.textnew.setVisibility(View.GONE);
                }
            }

            //   if (listItem.isEditable && (listItem.getlistValue()==null))
            if (listItem.isEditable) {

                if (listItem != null) {


                    holder.mImageView.setVisibility(View.VISIBLE);
                    holder.textview1.setVisibility(View.GONE);
                    //    holder.mImageView.setTag(holder.editText);
                    //    holder.mImageView.setTag(holder.editText);
                    Log.d(TAG, "Entering to next if");
                    if (listItem.getlistValue() != null) {
                        holder.mImageView.setImageResource(R.drawable.dropdown);
                        holder.mImageView.setTag(holder.editText);
                        //	holder.editText.setVisibility(View.GONE);

                        //   holder.textview1.setVisibility(View.VISIBLE);
                        //    holder.textview1.setText(listItem.getFieldValue());
                        holder.editText.setVisibility(View.VISIBLE);
                        holder.editText.setEnabled(false);
                        holder.editText.setOnClickListener(mOnClickListener);
                        //   holder.editText.setClickable(false);
                        //  holder.editText.setFocusable(false);
                        //    holder.textview1.setOnClickListener(mOnClickListener);
                        holder.mImageView.setOnClickListener(mOnClickListener);
                        Log.d(TAG, "position in getview when setid =" + position);
                        holder.mImageView.setId(position);
                        //  holder.EditTextAuto.setFocusable(true);
                        holder.EditTextAuto.setVisibility(View.GONE);
                    }
                    //    else if((listItem.geteditableListValue()!=null) && (jsonHelperClass.getListItemArray(listItem.geteditableListValue(), organizationName)!=null))
                    else if ((listItem.geteditableListValue() != null) && (mListItemObjMap.get(listItem.geteditableListValue()) != null)) {
                        holder.mImageView.setImageResource(R.drawable.dropdown);
                        holder.mImageView.setTag(holder.EditTextAuto);
                        Log.d(TAG, "Listitemname" + listItem.getFieldDispText());
                        Log.d(TAG, "geteditableListValue" + listItem.geteditableListValue());
                        //	Log.d(TAG, "getListItemArray" +jsonHelperClass.getListItemArray(listItem.geteditableListValue(), organizationName));
                        //	mListItemArray= jsonHelperClass.getListItemArray(listItem.geteditableListValue(), organizationName);
                        mListItemobjArray = mListItemObjMap.get(mFieldItems.get(position).geteditableListValue()).ListItemArray;
                        mListItemArray = getValueArray(mListItemobjArray);

                        Log.d(TAG, "mListItemArray" + mListItemArray);
                        //  	mAutoCompleteAdapter= new ArrayAdapter<String>(this.getContext(),R.layout.detail_fieldauto,mListItemArray);
                        mAutoCompleteAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, mListItemArray);
                        //		holder.EditTextAuto = (AutoCompleteTextView) convertView
                        //			.findViewById(R.id.lin2editauto);
                        //holder.EditTextAuto.setAdapter(mAutoCompleteAdapter);

                        holder.editText.setVisibility(View.GONE);
                        holder.EditTextAuto.setVisibility(View.VISIBLE);
                        holder.EditTextAuto.setText(Utils.null2empty(listItem
                                .getFieldValue()));
                        //	 int textsize=holder.EditTextAuto.getText().length();

                        //	 holder.textView.setFocusableInTouchMode(false);

                        //
                        holder.EditTextAuto.setFocusable(true);
                        holder.EditTextAuto.setClickable(true);
                        holder.EditTextAuto.setFocusableInTouchMode(true);
                        holder.editText.setClickable(true);
                        holder.editText.setFocusable(true);
                        holder.EditTextAuto.setCursorVisible(true);
                        holder.EditTextAuto.addTextChangedListener(new MyTextWatcherAuto(holder.EditTextAuto));
                        holder.EditTextAuto.setHorizontallyScrolling(false);
                        //    holder.EditTextAuto.setMaxLines(5);
                        //    holder.EditTextAuto.setEllipsize(null);
                        Utils.makeTextViewMultiLine(holder.EditTextAuto);
                 /*
                 /*
                  * following code works fine.  But enable after confirmation
				  */
//                        holder.mImageView.setOnClickListener(mOnClickListener2);
                        holder.mImageView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

                                ArrayList<ListItem> mListItemobj = mListItemObjMap.get(mFieldItems.get(position).geteditableListValue()).ListItemArray;
                                final ArrayList<String> mListItem = getValueArray(mListItemobj);

                                ArrayAdapter<String> itemsAdapter =
                                        new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mListItem);

                                builder.setAdapter(itemsAdapter,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                AutoCompleteTextView editText = (AutoCompleteTextView) view.getTag();

                                                editText.setText(mListItem.get(which));

                                            }
                                        });

                                builder.show();
                            }
                        });
                        holder.mImageView.setId(position);
                    } else if (listItem.IsTypeDateTime()) {

                        holder.mImageView.setImageResource(R.drawable.ic_calendar);
                        holder.mImageView.setTag(holder.editText);

                        holder.editText.setVisibility(View.VISIBLE);
                        holder.editText.setHint("YYYY-MM-DD hh:mm");
                        holder.editText.setCursorVisible(true);
                        //   holder.editText.setClickable(false);
                        //  holder.editText.setFocusable(false);

                        holder.mImageView.setId(position);

                        holder.mImageView.setOnClickListener(mOnClickListener);
                        Log.d(TAG, "position in getview when setid =" + position);
                        //  holder.EditTextAuto.setFocusable(true);
                        //   holder.EditTextAuto.setVisibility(View.GONE);

                    } else if (listItem.IsTypeDateTimeYear()) {

                        holder.mImageView.setImageResource(R.drawable.ic_calendar);
                        holder.mImageView.setTag(holder.editText);

                        holder.editText.setVisibility(View.VISIBLE);
                        holder.editText.setHint("YYYY-MM-DD hh:mm");
                        holder.editText.setCursorVisible(true);
                        //   holder.editText.setClickable(false);
                        //  holder.editText.setFocusable(false);

                        holder.mImageView.setId(position);
                        holder.mImageView.setOnClickListener(mOnClickListener);
                        Log.d(TAG, "position in getview when setid =" + position);
                        //  holder.EditTextAuto.setFocusable(true);
                        //   holder.EditTextAuto.setVisibility(View.GONE);

                    }
    /*	   	else if(listItem.IsTypeAddress())
               {
		  	
		   		//if address is editable us this to display icons and other flows
		   	 
		   	}  */
                    //    else if(listItem.getlistValue()==null)
                    else {
                        holder.mImageView.setOnClickListener(mOnClickListener1);
                        Log.d(TAG, "mOnClickListener1 registered");
                        //   holder.textView.setFocusableInTouchMode(false);
                        holder.mImageView.setTag(holder.editText);
                        holder.editText.setVisibility(View.VISIBLE);
                        holder.EditTextAuto.setVisibility(View.GONE);
                        // 	holder.editText.requestFocus();
                        //   holder.editText.setFocusableInTouchMode(true);
                        holder.editText.setFocusable(true);
                        holder.editText.setClickable(true);
                        holder.editText.setCursorVisible(true);
                        holder.EditTextAuto.setFocusable(true);
                        holder.EditTextAuto.setClickable(true);

                        //

                        holder.editText.addTextChangedListener(new MyTextWatcher(holder.editText));
                        holder.editText.setHorizontallyScrolling(false);
                        Utils.makeTextViewMultiLine(holder.editText);
            /*
              * following code works fine.  But enable after confirmation
			  */
                        //    holder.mImageView.setOnClickListener(mOnClickListener1);

                        //  holder.editText.setMaxLines(3);
                        //  holder.editText.setEllipsize(null);
                    }
    /*	    else
            {
					
		    } */
        /*   if(listItem.getlistValue()!=null)
            {
		    	holder.mImageView.setVisibility(View.INVISIBLE);
		    } */
                    holder.textView.setText(Utils.null2empty(listItem
                            .getFieldDispText()));   //Issue #COREY-1351 fix read from fielddisplaytext instead of fieldname
                    if (holder.editText != null) {
                        holder.editText.setText(Utils.null2empty(listItem
                                .getFieldValue()));
                        //holder.editText.setLines(1);
                        holder.editText.setHorizontallyScrolling(false);
                        Utils.makeTextViewMultiLine(holder.editText);
                        //  holder.editText.setMaxLines(3);
                        //  holder.editText.setEllipsize(null);
                        // as adapterlayout is shared by message list and
                        // detailed info
                        // screens, text views are coded as single line in the
                        // layout file.
                        // make the subscript (details) multiline.
        /*	if(listItem.getlistValue()==null && !listItem.getEditable())
            {
			Utils.makeTextViewMultiLine(holder.editText);
			
		    } */
                    }
/*		    if (holder.EditTextAuto != null) 
            {
		//	holder.EditTextAuto.setText(Utils.null2empty(listItem
			//	.getFieldValue()));
			// as adapterlayout is shared by message list and
			// detailed info
			// screens, text views are coded as single line in the
			// layout file.
			// make the subscript (details) multiline.
		    //	holder.editText.setLines(1);
			if(listItem.getlistValue()==null)
		    {
			//Utils.makeTextViewMultiLine(holder.EditTextAuto);
			
		    }
		    } */
            /*
                    if (listItem.getFieldName().equalsIgnoreCase("Add New Node")) {
                        if (mNewNote != null) {
                            holder.editText.setText(mNewNote);
                            holder.editText.setSelection(mNewNote.length());
                          
                        }
                        holder.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    setNewNote(v.getText().toString());
                                }
                                return false;
                            }
                        });
                    }
                    
             */
                }

            } else {

                holder.mImageView.setVisibility(View.GONE);
                Log.d(TAG, "address in meritus2 lands in is editable loop");
                //holder.textView.setFocusableInTouchMode(false);
                //holder.textView.setText(listItem.getFieldName());
                holder.textView.setText(Utils.null2empty(listItem
                        .getFieldDispText()));   //Issue #COREY-1351 fix read from fielddisplaytext instead of fieldname
                //	holder.editText.setText(listItem.getFieldValue());
                holder.editText.setVisibility(View.GONE);
                holder.EditTextAuto.setVisibility(View.GONE);
                holder.textview1.setVisibility(View.VISIBLE);
                String fldvalue = listItem.getFieldValue().replace("\\n", "\n");
                holder.textview1.setText(fldvalue);
                //	holder.textview1.setText(listItem.getFieldValue());
                if (listItem.getFieldType().equalsIgnoreCase("phone"))
                    holder.textview1.setAutoLinkMask(0x04);
                if (listItem.getlistValue() == null && !listItem.getEditable()) {
                    //	Utils.makeTextViewMultiLine(holder.editText);
                    Utils.makeTextViewMultiLine(holder.editText);
                }
    /*	 if(listItem.getFieldName().equalsIgnoreCase("Note"))
         {
     		holder.editText.setBackgroundResource(R.drawable.speech);
     	//	holder.EditTextAuto.setBackgroundResource(R.drawable.speech);
     		Log.d(TAG,"background image set for field"+ listItem.getFieldName());
     	} */
        /*
         * holder.editText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
		 * 0, 0);
		 */
            }
            return convertView;

        } else {
            convertView = null;
            if (convertView == null) {
                holder = new DetailedViewHolder();
                convertView = liService.inflate(R.layout.adapterlayout, null);

                holder.textView2 = (TextView) convertView
                        .findViewById(R.id.line2);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.line1);
                holder.mImageView = (ImageView) convertView
                        .findViewById(R.id.imageView1);
                convertView.setTag(holder);
            } else {
                holder = (DetailedViewHolder) convertView.getTag();
            }

            if (listItem != null) {
                Log.d(TAG, "Fieldname =" + listItem.getFieldName());
                Log.d(TAG, "Fielddisplaytext =" + listItem.getFieldDispText());
                Log.d(TAG, "Fieldvalue =" + listItem.getFieldValue());
        /*holder.textView.setText(TextUtils.isEmpty(listItem
			.getFieldName()) ? listItem.getFieldDispText()
			: listItem.getFieldName());
			*/
                if (listItem.getFieldType().equalsIgnoreCase("phone"))
                    holder.textView2.setAutoLinkMask(0x04);
                String Dispfield = TextUtils.isEmpty(listItem
                        .getFieldDispText()) ? listItem.getFieldName()
                        : listItem.getFieldDispText();
                String DispVal = listItem.getFieldValue();

                holder.textView.setText(TextUtils.isEmpty(listItem
                        .getFieldDispText()) ? listItem.getFieldName()
                        : listItem.getFieldDispText());
                if (holder.textView2 != null) {
	/*    holder.textView2.setText(TextUtils.isEmpty(listItem
			    .getFieldValue()) ? listItem.getOpenUrl()
			    : listItem.getFieldValue()); */

                    String fldvalue = listItem.getFieldValue().replace("\\n", "\n");
                    holder.textView2.setText(fldvalue);
                    //	holder.textView2.setText(listItem.getFieldValue());
                    //    Utils.makeTextViewMultiLine(holder.textView2);
                    if (listItem.getFieldStatus() != null) {
                        String colour;
                        if (mStatusColorHashMap.get(listItem.getFieldStatus()) != null) {
                            colour = "#" + mStatusColorHashMap.get(listItem.getFieldStatus());
                        } else colour = "#808080";
                        int hex = Color.parseColor(colour);
                        holder.textView2.setBackgroundColor(hex);
                        //	holder.textView.setBackgroundColor(hex);
                    }
                    if (listItem.getFieldType().equalsIgnoreCase("Address")) {
                        holder.textView2.setTextColor(Color.BLUE);
                        holder.textView2.setPaintFlags(holder.textView2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

	/*		 holder.mImageView.setVisibility(View.VISIBLE);
			 holder.mImageView.setImageResource(R.drawable.location); */
                    }
                }

                if (listItem.getImageUrl() != null && !listItem.getImageUrl().isEmpty() && !listItem.getImageUrl().equalsIgnoreCase("")) {
                    String img1 = listItem.getImageUrl();
                    Picasso.with(mContext).load(img1).placeholder(R.drawable.loading_icon).into(holder.mImageView);
                } else holder.mImageView.setVisibility(View.GONE);
            }

            if (listItem.getFieldValue() != null && listItem.getFieldValue().contains("http")) {

                SpannableString ss = new SpannableString(
                        listItem.getFieldValue());
                Linkify.addLinks(ss, Linkify.WEB_URLS);
                holder.textView2.setText(ss);
                // subscript.setMovementMethod(LinkMovementMethod.getInstance());
            }
            return convertView;

        }

    }

    public void setNewNote(String note) {
        mNewNote = note;
    }

    public String getNewNote() {
        return mNewNote;
    }

    class DetailedViewHolder {

        //	EditText editText;
        CoreyEditText editText;
        TextView textView;
        TextView textview1;
        TextView textView2;
        ImageView mImageView;
        TextView textnew;
        AutoCompleteTextView EditTextAuto;
//	InstantCompleteTextView EditTextAuto;
    }

    ArrayList<String> getValueArray(ArrayList<ListItem> ListItemobjArray) {
        ArrayList<ListItem> listobjarray = ListItemobjArray;
        ArrayList<String> lArray = new ArrayList<String>();
        for (ListItem lItem : listobjarray) {
            lArray.add(lItem.getdisplayText());
        }
        return lArray;
    }

    HashMap<String, String> getstatuscolormap(ArrayList<ListItem> ListItemobjArray) {
        ArrayList<ListItem> listobjarray = ListItemobjArray;
        HashMap<String, String> lmap = new HashMap<String, String>();
        String stskey;
        String stsval;
        for (ListItem lItem : listobjarray) {
            stskey = lItem.getValue();
            stsval = lItem.getBackgroundColor();
            lmap.put(stskey, stsval);

        }
        return lmap;
    }

    class MyTextWatcher implements TextWatcher {
        View mView;
        int textcountdff = 0;
        int textcountbef = 0;
        int textcounton = 0;

        public MyTextWatcher(View v) {
            Log.d(TAG, "view in textwatcher =" + v);
            mView = v;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            textcountbef = count;
            Log.d(TAG, "textcountbef =" + textcountbef);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            textcounton = count;
            Log.d(TAG, "textcounton =" + textcounton);
        }

        @Override
        public void afterTextChanged(Editable s) {
            int textsize = s.length();
            Fields item = (Fields) mView.getTag();
            String Fldval = item.getFieldValue();
            String newFldval = s.toString();
            Log.d(TAG, "textsize =" + textsize);
            if (s != null) {
			/*
			if((textcountbef>0)&& (s.length()!=0))
			{
			//	Fields item = (Fields) mView.getTag();
				item.setTempData(s.toString());
			} */
                if (!newFldval.equals(Fldval)) item.setTempData(s.toString());
            }
        }

    }

    class MyTextWatcherAuto implements TextWatcher {
        View mView;

        public MyTextWatcherAuto(View v) {
            mView = v;
            Log.d(TAG, "view in textwatcheauto =" + v);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            Log.d(TAG, "count beforetextchanged =" + count);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            Log.d(TAG, "count ontextchanged =" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Fields item = (Fields) mView.getTag();
            Log.d(TAG, "s in afterTextchanged =" + s);
            if (s != null) {
                item.setTempData(s.toString());
            }
            if (s.length() == 0) {
                // 	((AutoCompleteTextView) mView).showDropDown();
            }

        }

    }

    OnClickListener mOnClickListener1 = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "mOnClickListener1 invoked");
            CoreyEditText editText = (CoreyEditText) v.getTag();
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            editText.setCursorVisible(true);
            editText.addTextChangedListener(new MyTextWatcher(editText));
        }
    };

    OnClickListener mOnClickListener2 = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "mOnClickListener2 invoked");

            AutoCompleteTextView EditTextAuto = (AutoCompleteTextView) v.getTag();
            EditTextAuto.setFocusable(true);
            EditTextAuto.setFocusableInTouchMode(true);
            EditTextAuto.requestFocus();
            EditTextAuto.setCursorVisible(true);
            EditTextAuto.addTextChangedListener(new MyTextWatcher(EditTextAuto));

        }
    };
    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onclick listener invoked");
            int idd = v.getId();
            positionSelected = idd;
            OnClickHelper(idd, v);
		
	/*    EditText editText = (CoreyEditText) v.getTag();
	    editText.setFocusable(true);
	    editText.setFocusableInTouchMode(true);
	 //   editText.setClickable(false);
	    editText.setCursorVisible(true);
	  //  editText.requestFocus();
	  //  editText.setFocusable(true);
	    editText.requestFocus();
	    editText.addTextChangedListener(new MyTextWatcher(editText));*/

        }
    };

    public void OnClickHelper(int idd, final View v) {
        final int idpos = idd;
        Log.d(TAG, "idd =" + idd);
        Fields listItem1 = mFieldItems.get(idd);
        String openUrl = listItem1.getOpenUrl();
        String fieldvalue = listItem1.getFieldValue();
        String fieldnme = listItem1.getFieldName();
        Log.d(TAG, "openUrl =" + openUrl);
        Log.d(TAG, "fieldvalue =" + fieldvalue);
        Log.d(TAG, "fieldname= " + fieldnme);
        Log.d(TAG, "fieldtype= " + listItem1.getFieldType());
        view = v;

		/*
		 * If web page
		 * *****************************************************************
		 */
        if (fieldvalue != null && fieldvalue.contains("http")
                || openUrl != null && openUrl.contains("http")) {
            String displayText = listItem1.getFieldDispText();
               /*     if ("Google Glass".equalsIgnoreCase(displayText)) {
                        Log.d(TAG, ">>> Google Glass >>>");
                        new WebViewDialogHelper(isEditable, mDetailedApplicationAdapter.getNewNote(), DetailedApplicationData.this, new WebViewDialogHelper.IListener() {
                            @Override
                            public void onTextChanged(String text) {
                                Log.d(TAG, "WebViewDialog onTextChanged: " + text);
                                mDetailedApplicationAdapter.setNewNote(text);
                                mDetailedApplicationAdapter.notifyDataSetChanged();
                            }
                        }).showDialog(openUrl);
                    } 
                    else */
            {
                Intent urlIntent = new Intent(mActivity,
                        WebViewActivityCMN.class);

                if (openUrl != null) {
                    if (!openUrl.isEmpty()) urlIntent.putExtra("myurl", openUrl);
                } else if (fieldvalue != null) {
                    if (!fieldvalue.isEmpty()) urlIntent.putExtra("myurl", fieldvalue);
                } else {
                    Log.d(TAG, "No urls present");
                }
                if (fieldnme != null) {
                    if (!fieldnme.isEmpty())
                        urlIntent.putExtra("objname", fieldnme);
                }
	    /*	    urlIntent.putExtra("myurl", mFArrayList.get(arg2)
					.getOpenUrl().isEmpty() ? mFArrayList.get(arg2)
					.getFieldValue() : mFArrayList.get(arg2)
					.getOpenUrl());
	    */
                mActivity.startActivity(urlIntent);
            }
        }

        if (listItem1.IsTypeAddress()) {
            if (listItem1.getFieldValue() != null) {
                Log.d(TAG, "address is =" + listItem1.getFieldValue());

                String address = listItem1.getFieldValue();
		/*	Intent mapsIntent =  new Intent(mActivity,
					WebViewActivityCMN.class); */


                Uri gmmIntentUri = Uri.parse("geo:0,0?q=my" + address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mActivity.startActivity(mapIntent);


            }
        }
		/*
		 * If web page ends
		 * *****************************************************************
		 */
		
		/*
		 * For admin messages
		 * *****************************************************************
		 */
        if (fieldnme.equals("AdminMessages")) {
            Log.d(TAG, "Admin messages click enabled");
            Intent MessagingIntent = new Intent(mActivity,
                    MessageActivityCMN.class);
		/*	Log.d(TAG, "mAssignedUsrId"+mAssignedUsrId);
			Log.d(TAG, "mMsgContext"+mMsgContext);
			MessagingIntent.putExtra("usr", mAssignedUsrId);
			MessagingIntent.putExtra("ctxt", mMsgContext); 
			MessagingIntent.putExtra("objid",listItem.get );*/
            mActivity.startActivity(MessagingIntent);

        }
		/*
		 * For status
		 * *****************************************************************
		 */
        if (changeui) // corey-2159 break and raised corey-2256.  When not in editable node, edit should not be allowed
        {
            if (listItem1.isEditable) {
                if (listItem1.getlistValue() != null) {
                    String mListValue = listItem1.getlistValue();
                    Boolean oldver = false;
                    Log.d(TAG, "mListValue" + mListValue);
                    JSONHelperClass jsonHelperClass = new JSONHelperClass();
                    //	ListItemObj listitemobj=jsonHelperClass.getListItemObj(mListValue, mOrganization);
                    ListItemObj listitemobj1 = mListItemObjMap.get(mListValue);
                    if (listitemobj1 != null) {
                        Log.d(TAG, "listitemobj is" + listitemobj1);
                        Log.d(TAG, "ListItemArray from listitemobj is" + listitemobj1.ListItemArray);
                        List<String> keys = new ArrayList<String>(mListItemObjMap.keySet());

                        for (String key : keys) {
                            System.out.println(key + ": " + mListItemObjMap.get(key).mName);
                            Log.d(TAG, "key is =" + key);
                            Log.d(TAG, "name of list is =" + mListItemObjMap.get(key).mName);
                            Log.d(TAG, "ListItemArray is" + mListItemObjMap.get(key).ListItemArray);
                        }
                        mListItemobjArray = listitemobj1.ListItemArray;

                        if (Boolean.parseBoolean(listItem1.getsMultiSelect())) {
                            Intent myIntent = new Intent(mActivity, MultiSelectActivityCMN.class);
                            String listitemname = listItem1.getlistValue();
                            Log.d(TAG, "listitemname=" + listitemname);
                            myIntent.putExtra("listvaluename", listitemname);
                            myIntent.putExtra("fieldId", listItem1.getFieldId());
                            myIntent.putExtra("ObjID", objectId);
                            myIntent.putExtra("msgid", mMsgId);
                            if (listItem1.getFieldId().equalsIgnoreCase("211") || listItem1.getFieldId().equalsIgnoreCase("233")) {
                                mActivity.startActivityForResult(myIntent, 211);
                            } else {
                                mActivity.startActivity(myIntent);
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);


                            builder.setAdapter(new ListValueAdapter(mActivity, mListItemobjArray),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                            View view = ((ListActivity) mActivity).getListView().getChildAt(idpos);
                                            //	View view= getView(idpos, null, null);

                                            //    TextView subscript = (TextView) view
                                            //                       .findViewById(R.id.line2edit);
//                   	EditText subscript = (CoreyEditText) view.findViewById(R.id.line2edit);
                                            CoreyEditText editText = (CoreyEditText) v.getTag();

                                            String mValue = mListItemobjArray.get(which).getValue();
                                            String mdisplaytext = mListItemobjArray.get(which).getdisplayText();


                                            editText.setText(mListItemobjArray.get(which).getdisplayText());

                                            //     mFArrayList.get(mOnClickPosition).setTempData(mStatus);
                                            mFieldItems.get(idpos).setTempData(mValue);
                                            //  Boolean isStatuschanged = true;


                                        }
                                    });

                            builder.show();
                        }
                    }
                }
                if (listItem1.getFieldType() != null) {
                    if (listItem1.getFieldType().equalsIgnoreCase("Datetime")) {

//                        View view = getView(idpos, null, null);
//                        if (view == null) {
//                            return;
//                        }
                        View view = ((ListActivity) mActivity).getListView().getChildAt(idpos);


                        final CoreyEditText subscript = (CoreyEditText) v.getTag();
                        dialog = new Dialog(mContext);

                        dialog.setContentView(R.layout.date_time_dlg);
                        dialog.setTitle("Pick Date & time");
                        dp = (DatePicker) dialog.findViewById(R.id.datePicker1);
                        tp = (TimePicker) dialog.findViewById(R.id.timePicker1);
                        Button dtbtn = (Button) dialog.findViewById(R.id.btnOk);
                        tp.setOnTimeChangedListener(new OnTimeChangedListener() {
                            @Override
                            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				   /* 	hour=	hourOfDay;
				    	min=minute;*/

                            }
                        });
                        dialog.show();
                        dtbtn.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                year = dp.getYear();
                                month = dp.getMonth() + 1;
                                day = dp.getDayOfMonth();
                                hour = tp.getCurrentHour();
                                min = tp.getCurrentMinute();
                                StringBuilder sb = new StringBuilder();
                                sb.append(Integer.toString(year));
                                sb.append("-");
                                sb.append(Integer.toString(month));
                                sb.append("-");
                                sb.append(Integer.toString(day));
                                sb.append(" ");
                                sb.append(Integer.toString(hour));
                                sb.append(":");
                                sb.append(Integer.toString(min));
                                String date_time = sb.toString();

                                StringBuilder sb1 = new StringBuilder();

                                sb1.append(Integer.toString(month));
                                sb1.append("-");
                                sb1.append(Integer.toString(day));
                                sb1.append("-");
                                sb1.append(Integer.toString(year));
                                sb1.append(" ");
                                sb1.append(Integer.toString(hour));
                                sb1.append(":");
                                sb1.append(Integer.toString(min));
                                String date_time1 = sb1.toString();
                                //.trim();
                                Log.d(TAG, "selected time is = " + sb);
                                subscript.setText(date_time1);
                                String formattedtime = Utils.converttime2utc(date_time);
                                Log.d(TAG, "formattedtime = " + formattedtime);
                                //     mFArrayList.get(mOnClickPosition).setTempData(mStatus);
                                mFieldItems.get(idpos).setTempData(formattedtime);
                                dialog.dismiss();
//                                notifyDataSetChanged();
                            }

                        });
                    } else if (listItem1.getFieldType().equalsIgnoreCase("Datetimeyear")) {

//                        View view = getView(idpos, null, null);
//
//                        if (view == null) {
//                            return;
//                        }

                        View view = ((ListActivity) mActivity).getListView().getChildAt(idpos);


                        final CoreyEditText subscript = (CoreyEditText) v.getTag();


                        dialog = new Dialog(mContext);

                        dialog.setContentView(R.layout.date_dlg);
                        dialog.setTitle("Pick Date");
                        dp = (DatePicker) dialog.findViewById(R.id.datePicker2);

                        Button dtbtn = (Button) dialog.findViewById(R.id.datebtnOk);

                        dialog.show();
                        dtbtn.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                year = dp.getYear();
                                month = dp.getMonth() + 1;
                                day = dp.getDayOfMonth();

                                StringBuilder sb = new StringBuilder();
                                sb.append(Integer.toString(year));
                                sb.append("-");
                                sb.append(Integer.toString(month));
                                sb.append("-");
                                sb.append(Integer.toString(day));
                                sb.append(" ");

                                String date_time = sb.toString();

                                StringBuilder sb1 = new StringBuilder();

                                sb1.append(Integer.toString(month));
                                sb1.append("-");
                                sb1.append(Integer.toString(day));
                                sb1.append("-");
                                sb1.append(Integer.toString(year));
                                sb1.append(" ");

                                String date_time1 = sb1.toString();
                                //.trim();
                                Log.d(TAG, "selected time is = " + sb);
                                subscript.setText(date_time1);
	          /*		 String formattedtime = Utils.converttime2utc(date_time);
	          		Log.d(TAG, "formattedtime = "+formattedtime);
	                   //     mFArrayList.get(mOnClickPosition).setTempData(mStatus);
	                        mFieldItems.get(idpos).setTempData(formattedtime); */
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date strDate;
                                mFieldItems.get(idpos).setTempData(date_time);
                                dialog.dismiss();
//                                notifyDataSetChanged();
				/*	try {
						strDate = sdf.parse(date_time);
						if (new Date().after(strDate)) {
		            		 
		            		 mFieldItems.get(idpos).setTempData(date_time); 
			            	 dialog.dismiss();
		            	 }
						else
						{
							Toast.makeText(mContext,
		 	    				    "Date of birth should not be greater than today.  Please enter valid date", Toast.LENGTH_SHORT).show();
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Log.d(TAG, "parse error ");
						e.getMessage();
					} */


                            }

                        });
                    }
                }
            }

        }
    }


}
