package com.coremobile.coreyhealth.newui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CMN_ImageUploadActivity extends BaseActivityCMN {

    Bitmap bitmap;
    ImageView selectedImageVw, cancelImageView;
    TextView uploadTextVw;
    Button sendBtnVw;
    EditText titleEditTextVw, descEditTextVw;
    private int GALLERY = 1, CAMERA = 2;
    public String token, patientid, objid, text;
    private String UPLOAD_URL, filePath = null;
    private long totalSize = 0;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    private String serverResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        selectedImageVw = (ImageView) findViewById(R.id.pickedImageVw);
        uploadTextVw = (TextView) findViewById(R.id.uploadTextVw);

        sendBtnVw = (Button) findViewById(R.id.sendButtonVw);
        titleEditTextVw = (EditText) findViewById(R.id.titleEditVw);
        descEditTextVw = (EditText) findViewById(R.id.descriptionEditVw);
        progressBar = (ProgressBar) findViewById(R.id.progressBarr);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        cancelImageView = (ImageView) findViewById(R.id.cancelImageVw);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.view_upload_text));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra("OBJECT_ID")) {
            objid = getIntent().getExtras().getString("OBJECT_ID");
        }


        if (getIntent().hasExtra("PATIENT_ID")) {
            patientid = getIntent().getExtras().getString("PATIENT_ID");
        }
        uploadTextVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();
            }
        });

        sendBtnVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkTools networkTools = NetworkTools.getInstance();
                if (networkTools.checkNetworkConnection(CMN_ImageUploadActivity.this)) {
                    text = titleEditTextVw.getText().toString().trim();
                    if (filePath != null && text != "" && !text.isEmpty() && text.length() != 0) {

                        new UploadFileToServer(text).execute();
                    } else
                        Toast.makeText(getApplicationContext(), "No image file to upload", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageVw.setImageResource(android.R.drawable.dialog_frame);
                titleEditTextVw.setText("");
                descEditTextVw.setText("");
                txtPercentage.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                filePath = null;
            }
        });
    }

    private void showPictureDialog() {
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        pictureDialog.setCancelable(true);
        String[] pictureDialogItems = {
                "Gallery",
                "Camera", "Cancel"
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //choosePhotoFromGallary();
                                selectImageFromGallery();
                                dialog.dismiss();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                dialog.dismiss();
                                break;
                            case 2:
                                dialog.dismiss();
                        }
                    }
                });
        pictureDialog.show();
    }


    public void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");//android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
    }

    private void takePhotoFromCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.coremobile.coreyhealth.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {

            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            Uri selectedImageUri = data.getData();
            filePath = getPath(selectedImageUri);


            decodeFile(filePath);


        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {

            filePath = mCurrentPhotoPath;

            decodeFile(filePath);
        }
    }

    /**
     * The method decodes the image file to avoid out of memory issues. Sets the
     * selected image in to the ImageView.
     *
     * @param filePath
     */
    public void decodeFile(String filePath) {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        selectedImageVw.setImageBitmap(bitmap);
    }

    private String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        UploadFileToServer(String text) {

            token = CMN_Preferences.getUserToken(getApplicationContext());


            UPLOAD_URL = /*"https://dev.mobilecorey.com/serverintegration_appstore/patientimageupload.aspx?"*/
                    MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "PatientUploadImage.aspx?" +
                            "token=" + token + "&objid=" + objid  + "&patientid=" + patientid;
            try {
                UPLOAD_URL = UPLOAD_URL + "&text=" + URLEncoder.encode(text,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            cancelImageView.setEnabled(false);
            sendBtnVw.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            txtPercentage.setVisibility(View.VISIBLE);
            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return uploadFile();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() throws UnsupportedEncodingException {
            String responseString = null;

            Log.e("URL", UPLOAD_URL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("website",
                        new StringBody("www"));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("TAG", "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            txtPercentage.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            sendBtnVw.setEnabled(true);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // do nothing
                        cancelImageView.setEnabled(true);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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


