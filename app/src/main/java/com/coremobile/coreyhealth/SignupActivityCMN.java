package com.coremobile.coreyhealth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//import android.util.Log;

public class SignupActivityCMN extends BaseActivityCMN implements IServerConnect
{

    private static String TAG = "Corey_SignupActivityCMN";
    static final private int HELP_DIALOG = 0;
    private static final int DIALOG_WRONG_CREDENTIALS = 1;
    static final private int REGISTRATION_COMPLETE = 3;
    private static final int REFRESH_CONFIGURATION = 2;
    private static final int ABOUT_CRYSTAL = 4;
    private static final int MISSING_CONFIGURATION = 5;
    private static final int CONFIGURATION_LOADED = 6;
    private static final int SHOW_PROGRESS_DIALOG = 7;
    private static final int CLOSE_PROGRESS_DIALOG = 8;

    protected int _waitTime = 20000; // time to display the splash screen in ms

    private String CMN_SERVER_BASE_URL_DEFINE;
    private final static String CMN_SERVER_SIGNUP_API = "signup.aspx?";
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    public ProgressDialog mDialog;
    String mContactmail;
    String Appstring = "SURG" ;
    
    Button mSignup;
    Button mSignin;
    EditText mUsernameField;
    EditText mPasswordField;
    EditText mEmailAddressField;
    AutoCompleteTextView mOrganizationNameField;
    EditText confirmPasswordField;

    String mUsername;
    String mPassword;
    String mConfirmPassword;
    String mEmailaddress;
    String mOrganization;
    String registrationString;
    MyApplication application;

    UIHandler uiHandler;
    ArrayAdapter<String> mAutoCompleteAdapter;

    private final class UIHandler extends Handler
    {
        public static final int DISPLAY_UI_TOAST = 0;
        //public static final int MISSING_CONFIGURATION = 1;
        //public static final int PROGRESS_DIALOG = 1;

        public UIHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
            case UIHandler.DISPLAY_UI_TOAST:
                Context context = getApplicationContext();
                Toast t = Toast.makeText(context, (String)msg.obj, Toast.LENGTH_LONG);
                t.show();
                break;

            case MISSING_CONFIGURATION:
                showDialog(MISSING_CONFIGURATION);
                break;

            case CONFIGURATION_LOADED:
                showDialog(CONFIGURATION_LOADED);
                populateOrganizationFileds();
                break;

            case SHOW_PROGRESS_DIALOG:
                showConfigurationDialog();
                break;

            case CLOSE_PROGRESS_DIALOG:
                closeConfigurationDialog();
                break;

            default:
                break;
            }
        }
    }

    public void populateOrganizationFileds ()
    {
        mAutoCompleteAdapter.clear();

        for(int i = 0; i < MyApplication.organizationData.size(); i++)
        {
            //Log.i(TAG,"crystal data is null");
            String organization = MyApplication.organizationData.get(i).mOrgName;
            Log.d(TAG,"orgNames: " + organization);
            mAutoCompleteAdapter.add(organization);
        }
    }

    protected void handleUIRequest(int type, String message)
    {
        Message msg = uiHandler.obtainMessage(type);
        msg.obj = message;
        uiHandler.sendMessage(msg);
    }

    private OnClickListener mSignupClicked = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            //Log.v(TAG, "Sign up onClick");
            InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMgr.hideSoftInputFromWindow(mOrganizationNameField.getWindowToken(), 0);
            mUsername = mUsernameField.getText().toString().trim();
            mPassword = mPasswordField.getText().toString().trim();
            mConfirmPassword = confirmPasswordField.getText().toString().trim();
            String orgName = mOrganizationNameField.getText().toString().trim();
            Log.d(TAG,"password: ****");

            Log.d(TAG,"confirm Password: ****");
            if((mUsername.length() == 0) || (mPassword.length() == 0) || (mEmailAddressField.getText().toString().trim().length() == 0) || (mOrganizationNameField.getText().toString().trim().length() == 0))
            {
                Toast.makeText(getApplicationContext(), "All of the above Input fields are mandatory and can not be left empty. Please enter valid details", Toast.LENGTH_SHORT).show();
            }
            else if (!MyApplication.isConfigurationLoaded())
            {
                Toast.makeText(getApplicationContext(), "No Valid Organization found. Please Refresh the Configuration from the Menu.", Toast.LENGTH_LONG).show();
            }
            else if ((!MyApplication.isOrganizationPresent(orgName)))
            {
                Toast.makeText(getApplicationContext(), "Unsupported Organization. Please enter a valid organization name (or) Contact us to create and support your organization", Toast.LENGTH_LONG).show();
            }
            else
            {
                if( mPassword.equals(mConfirmPassword))
                {
                    //try {
                    mEmailaddress = mEmailAddressField.getText().toString().trim();//URLEncoder.encode(mEmailAddressField.getText().toString().trim(), "UTF-8");
                    mOrganization = mOrganizationNameField.getText().toString().trim();//URLEncoder.encode(mOrganizationNameField.getText().toString().trim(),"UTF-8");
                    //} /*catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    //e.getMessage();
                    //}*/

                    String deviceId = mcurrentUserPref.getString("deviceId", null);
                    JSONHelperClass jsonHelperClass = new JSONHelperClass();
                    CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mOrganizationNameField.getText().toString());
                    //String url = String.format(CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_SIGNUP_API + "username=%s&password=%s&email=%s&organization=%s&deviceid=%s&devicetype=Android",mUsername,mPassword,mEmailaddress,mOrganization, deviceId);
                    String url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_SIGNUP_API;
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("username", mUsername);
                    data.put("password", mPassword);
                    data.put("email", mEmailaddress);
                    data.put("organization", mOrganization);
                    data.put("deviceid", deviceId);
                    data.put("devicetype", "Android");
                    Log.d(TAG,"sign up url: " + url);
                    Log.d(TAG,"username: "+ mUsername);
                    Log.d(TAG,"password: ****");
                    Log.d(TAG,"email: "+ mEmailaddress);
                    Log.d(TAG,"organization: "+ mOrganization);
                    Log.d(TAG,"deviceid: "+ deviceId);
                    Log.d(TAG,"devicetype: "+ "Android");
                    if(Utils.checkInternetConnection())
                    {
                        new VerifyUser(SignupActivityCMN.this,data).execute(url);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to access the Network. Please check your Network connectivity and try again.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Passwords do not match. The Confirm Password must match the Password and are case sensitive", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private OnClickListener mSigninClicked = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            //Log.v(TAG, "Signin onClick");
            finish();
            startActivity(new Intent(SignupActivityCMN.this, SigninActivityCMN.class));

        }
    };


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i(TAG,"onActivityResult");
        switch(requestCode){
        case SIGNIN_ACTIVITY:
            //setResult(resultCode);
            finish();
            break;
            default:
                break;
        }
    }*/

    @Override
    protected void onResume()
    {
        super.onResume();
        if (MyApplication.shouldResetApp())
        {
            Toast.makeText(getApplicationContext(), "Cache Expired. Please Refresh Configuration and Signin again.", Toast.LENGTH_LONG).show();
        }
        MyApplication.resetApp(false);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupscreen);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        Utils.setAppTitle(this, getString(R.string.app_name));
        mUsernameField = (EditText)findViewById(R.id.signup_username);
        addClearButton(mUsernameField);
        mPasswordField= (EditText)findViewById(R.id.signup_password);
        addClearButton(mPasswordField);
        confirmPasswordField = (EditText)findViewById(R.id.signup_confirmpassword);
        addClearButton(confirmPasswordField);
        mEmailAddressField = (EditText)findViewById(R.id.signup_emailaddress);
        addClearButton(mEmailAddressField);
        mOrganizationNameField = (AutoCompleteTextView)findViewById(R.id.signup_companyname);
        addClearButton(mOrganizationNameField);
        mSignup = (Button)findViewById(R.id.signup_signup);
        mSignup.setOnClickListener(mSignupClicked);
        mSignin = (Button)findViewById(R.id.signup_signin);
        mSignin.setOnClickListener(mSigninClicked);
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        application = (MyApplication) getApplication();
        
        Appstring=application.AppConstants.DefaultCredentialsAppstring();
        Appstring=application.AppConstants.DefaultCredentialsAppstring();
        mContactmail = jsonHelperClass.getAppAttributes("PATIENT").get(1);
        Button contactus = (Button) findViewById(R.id.contactus);
        contactus.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.performSendEmail(SignupActivityCMN.this,mContactmail);
            }
        });

        contactus = (Button) findViewById(R.id.coremobilenetworks);
        contactus.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.performSendEmail(SignupActivityCMN.this,mContactmail);
            }
        });


        ArrayList<String> mOrganizationNames = new ArrayList<String>();
        if((MyApplication.crystalData == null) || (MyApplication.organizationData == null ) || (MyApplication.organizationData.size() == 0))
        {
            showDialog(MISSING_CONFIGURATION);
        }
        else
        {
            for(int i = 0; i < MyApplication.organizationData.size(); i++)
            {
                //Log.i(TAG,"crystal data is null");
                String organization = MyApplication.organizationData.get(i).mOrgName;
                mOrganizationNames.add(organization);
            }
        }
        //Read organization name from file
        /*InputStreamReader isr = null;
        FileInputStream fIn = null;

            char[] inputBuffer = new char[255];

            String data = null;

            try {

                fIn = openFileInput("orgNames.txt");

                isr = new InputStreamReader(fIn);


                isr.read(inputBuffer);

                data = new String(inputBuffer);
                Log.i(TAG,"orgnames: " + data);
                String[] firstSplit = data.split("/");
                Log.i(TAG,"split: " + firstSplit.length);
                for(int i = 0;i < firstSplit.length; i++){
                    mOrganizationNames.add(firstSplit[i]);
                    Log.i(TAG,firstSplit[i]);
                }
            }

            catch (Exception e) {

                e.getMessage();



            }*/
        mAutoCompleteAdapter= new ArrayAdapter<String>(this,R.layout.autocompletelayout,mOrganizationNames);
        mOrganizationNameField.setAdapter(mAutoCompleteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0,REFRESH_CONFIGURATION,Menu.NONE,"Refresh Configuration");
        menu.add(0,HELP_DIALOG,Menu.NONE,"Help");
        menu.add(0,ABOUT_CRYSTAL,Menu.NONE,"About");
        return super.onCreateOptionsMenu(menu);
    }

    public void refreshConfiguration()
    {

        Thread wThread = new HandlerThread("UIHandler");
        wThread.start();
        uiHandler = new UIHandler(((HandlerThread) wThread).getLooper());

        ((MyApplication)getApplication()).loadOrganizations();
        Thread waitThread = new Thread ()
        {
            @Override
            public void run()
            {

                try
                {
                    handleUIRequest(SHOW_PROGRESS_DIALOG, "");
                    int waited = 0;
                    while(MyApplication._parsing && (waited < _waitTime))
                    {
                        //Log.i(TAG,"MyApplication._parsing && (waited < _splashTime)");
                        sleep(100);
                        if(MyApplication._parsing)
                        {
                            //Log.i(TAG,"if(MyApplication._parsing) ");
                            waited += 100;
                        }
                    }
                }
                catch(InterruptedException e)
                {
                    e.getMessage();
                }
                finally
                {
                    handleUIRequest(CLOSE_PROGRESS_DIALOG, "");
                    if((MyApplication.crystalData == null) || (MyApplication.organizationData == null ) || (MyApplication.organizationData.size() == 0))
                    {
                        Log.d(TAG,"CONFIGURATION STILL MISSING");
                        handleUIRequest(MISSING_CONFIGURATION, "Failed to load basic configuration from the server. Please check your network settings and perform Refresh Configuration from the Menu");
                    }
                    else
                    {
                        handleUIRequest(CONFIGURATION_LOADED, "Configuration loaded successfully");
                        Log.d(TAG,"CONFIGURATION AVAILABLE");
                    }
                }
            }
        };

        waitThread.start();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
        case HELP_DIALOG:
            showDialog(HELP_DIALOG);
            break;
        case REFRESH_CONFIGURATION:
            refreshConfiguration();
            break;
        case ABOUT_CRYSTAL:
            startActivity(new Intent(SignupActivityCMN.this,AboutActivityCMN.class));
            break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // TODO Auto-generated method stub
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public Dialog onCreateDialog(int id)
    {
        switch(id)
        {
        case (HELP_DIALOG) :
            AlertDialog.Builder helpDialog = new AlertDialog.Builder(this);
            helpDialog.setTitle(String.format(
                                    getResources().getString(R.string.login_help_title),
                                    getResources().getString(R.string.app_version)));
            helpDialog.setMessage(MyApplication.crystalData.mCrystalHelp);
            return helpDialog.create();

        case REGISTRATION_COMPLETE :
            AlertDialog.Builder regDialog = new AlertDialog.Builder(this);
            regDialog.setTitle("Message");
            regDialog.setMessage(this.registrationString);
            regDialog.setCancelable(false);
            regDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    getContentResolver().delete(DatabaseProvider.CONTENT_URI, null, null);
                    Intent signinActivityIntent = new Intent(SignupActivityCMN.this,SigninActivityCMN.class);
                    signinActivityIntent.putExtra("CallingActivity", "SignupActivityCMN");
                    finish();
                    startActivity(signinActivityIntent);
                }
            });

            return regDialog.create();

        case MISSING_CONFIGURATION :
            AlertDialog.Builder missingDialog = new AlertDialog.Builder(this);
            missingDialog.setTitle("Message");
            missingDialog.setMessage("Failed to load basic configuration.\nPlease check your network settings and Refresh Configuration from the Menu\n\nPlease Contact us if the problem persists");
            missingDialog.setCancelable(false);
            missingDialog.setPositiveButton("Ok", null);

            return missingDialog.create();

        case CONFIGURATION_LOADED :
            AlertDialog.Builder loadedDialog = new AlertDialog.Builder(this);
            loadedDialog.setTitle("Message");
            loadedDialog.setMessage("Configuration loaded successfully.");
            loadedDialog.setCancelable(false);
            loadedDialog.setPositiveButton("Ok", null);
            return loadedDialog.create();

        case DIALOG_WRONG_CREDENTIALS:
            return new AlertDialog.Builder(this)
                   .setMessage("Try Again")
                   .setCancelable(false)
                   .setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                }
            })
                   .create();
        }
        return null;
    }

    @Override
    public void gotUserInfoFromServer(JSONObject json)
    {
        if(json!= null)
        {
            //Log.v(TAG,"json: " + json);
            try
            {
                //Log.i("SignupActivityCMN","result: " + json.getString("result"));
                String result = json.getString("result");
                String resultText = json.getString("text");
                if(result.equals("0"))
                {
                    String organizationName = json.getString("organization");
                    SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    editor.clear();
                    editor.commit();
                    editor.putString("Username", mUsername);
                    //editor.putString("Password", mPassword);
                    editor.putString("Organization", organizationName);
                    editor.commit();
                    getContentResolver().delete(DatabaseProvider.CONTENT_URI, null, null);
                    Intent authenticationActivityIntent = new Intent(SignupActivityCMN.this,AuthenticationActivityCMN.class);
                    finish();
                    startActivity(authenticationActivityIntent);

                }
                else if(result.equals("3"))
                {
                    registrationString = resultText;
                    //showDialog(ASYNCTEXT_REGISTRATION);
                    SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    editor.clear();
                    editor.commit();
                    editor.putString("Username", mUsername);
                    //editor.putString("Password", mPassword);
                    editor.putString("Organization", mOrganizationNameField.getText().toString());
                    editor.commit();

                    /*
                                        DialogInterface.OnClickListener registrationCompleteListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    getContentResolver().delete(DatabaseProvider.CONTENT_URI, null, null);
                                                    Intent signinActivityIntent = new Intent(SignupActivityCMN.this,SigninActivityCMN.class);
                                                    finish();
                                                    startActivity(signinActivityIntent);
                                                    break;
                                                }
                                            }
                                        };

                                        Builder builder = new AlertDialog.Builder(this);
                                        builder.setTitle("Message");
                                        builder.setMessage(registrationString);
                                        builder.setPositiveButton("ok", registrationCompleteListener);
                                        builder.setNegativeButton(null, null);
                                        builder.show();
                                        */

                    showDialog(REGISTRATION_COMPLETE);
                }
                else
                {
                    Log.d(TAG,"signup error: " + resultText);
                    Toast.makeText(getApplicationContext(), resultText, Toast.LENGTH_SHORT).show();
                }

            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.getMessage();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Unable to access the Network. Please check your Network connectivity and try again.", Toast.LENGTH_LONG).show();
        }

    }

    public void showConfigurationDialog()
    {
        //Log.i(TAG,"show Dialog");
        if(mDialog == null)
        {
            //refreshData.setVisibility(View.INVISIBLE);
            mDialog = ProgressDialog.show(this, "",
                                          "Loading Configuration from server...", true,true);
        }
    }

    public void closeConfigurationDialog()
    {
        if(mDialog != null)
        {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    //From interface
    public void showDialog()
    {
        if (isFinishing())
        {
            return;
        }
        mDialog = ProgressDialog.show(this, "",
                                      "Signing up...", true);
    }

    public void closeDialog()
    {
        if (isFinishing())
        {
            return;
        }
        if(mDialog != null)
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        if(mDialog != null)
        {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mDialog != null)
        {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void throwToast(String stringToToast)
    {
        if (isFinishing())
        {
            return;
        }
        Toast.makeText(getApplicationContext(), stringToToast, Toast.LENGTH_LONG).show();
    }

    private void addClearButton(final EditText editText)
    {
        final Drawable x = getResources().getDrawable(R.drawable.presence_offline);//your x image, this one from standard android images looks pretty good actually
        x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
        editText.setCompoundDrawables(null, null, editText.getText().toString().equals("") ? null : x, null);
        editText.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (editText.getCompoundDrawables()[2] == null)
                {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP)
                {
                    return false;
                }
                if (event.getX() > editText.getWidth() - editText.getPaddingRight() - x.getIntrinsicWidth())
                {
                    editText.setText("");
                    editText.setCompoundDrawables(null, null, null, null);
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                editText.setCompoundDrawables(null, null, editText.getText().toString().equals("") ? null : x, null);
            }

            @Override
            public void afterTextChanged(Editable arg0)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
        });
    }

	
}
