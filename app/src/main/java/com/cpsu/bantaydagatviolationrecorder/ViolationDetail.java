package com.cpsu.bantaydagatviolationrecorder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.cpsu.bantaydagatviolationrecorder.Data.Violation;
import com.cpsu.bantaydagatviolationrecorder.Data.ViolationRepo;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ViolationDetail extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText mName, mVessel, mOtherComp;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageButton mAddPhoto;
    ImageView mImage;
    CheckBox mOwnPrmt, mVslPrmt;
    String title, id, mCurrentPhotoPath;
    TextView mDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_detail);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");


        init();

        if (title.equalsIgnoreCase("View Violation")) {

            id = intent.getStringExtra("id");
            ViolationRepo vRepo = new ViolationRepo();
            Violation violation = vRepo.getViolationById(id);
            byte[] photo = vRepo.getViolationPhoto(id);
            mName.setText(violation.getName());
            mVessel.setText(violation.getVesselNo());
            mOtherComp.setText(violation.getOtherComplaints());
            if (violation.getOwnersPermit() == 1) {
                mOwnPrmt.setChecked(true);
            } else {
                mOwnPrmt.setChecked(false);
            }

            if (violation.getVesselPermit() == 1) {
                mVslPrmt.setChecked(true);
            } else {
                mVslPrmt.setChecked(false);
            }

            mDateTime.setText("Date: " + violation.getDateTime());
            mDateTime.setVisibility(View.VISIBLE);

            ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
            Bitmap bitmap= BitmapFactory.decodeStream(imageStream);
            mImage.setImageBitmap(bitmap);

        }

        getSupportActionBar().setTitle(title);
    }

    private void init() {
        mName = findViewById(R.id.name);
        mVessel = findViewById(R.id.vesselNo);
        mOwnPrmt = findViewById(R.id.chkOwnPrmt);
        mVslPrmt = findViewById(R.id.chkVslPrmt);
        mOtherComp = findViewById(R.id.otherComp);
        mAddPhoto = findViewById(R.id.btnAddPhoto);
        mAddPhoto.setOnClickListener(this);
        mImage = findViewById(R.id.imgPhoto);
        mDateTime = findViewById(R.id.txtDateTime);
    }

    private byte[] getBitmap(ImageView imageView) {
        Bitmap b = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] img = bos.toByteArray();
        return img;
    }
    private void save() {
        if (isValid()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateTime = sdf.format(new Date());
            String name = mName.getText().toString();
            String vessel = mVessel.getText().toString();
            int isOwnPrmt = mOwnPrmt.isChecked() ? 1:0;
            int isVslPrmt = mVslPrmt.isChecked() ? 1:0;
            String otherComp = mOtherComp.getText().toString();
            byte[] image = getBitmap(mImage);

            Violation violation = new Violation();
            violation.setDateTime(dateTime);
            violation.setName(name);
            violation.setVesselNo(vessel);
            violation.setOwnersPermit(isOwnPrmt);
            violation.setVesselPermit(isVslPrmt);
            violation.setOtherComplaints(otherComp);

            ViolationRepo violationRepo = new ViolationRepo();

            if (title.equalsIgnoreCase("Record Violation")) {
                id = RandomStringUtils.randomAlphanumeric(8);
                violation.setvId(id);
                violationRepo.insert(violation);
                violationRepo.insertPic(id, image);
                Toast.makeText(this, "Violation has been successfully recorded!.", Toast.LENGTH_SHORT).show();
                finish();
            } else if (title.equalsIgnoreCase("View Violation")){
                violation.setvId(id);
                violationRepo.update(violation);
                violationRepo.updatePic(id, image);
                Toast.makeText(this, "Violation has been successfully updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void launchCamera() {
        /*Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);*/

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.d("mylog", "Exception while creating file: " + ex.toString());
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Log.d("mylog", "Photofile not null");
            Uri photoURI = FileProvider.getUriForFile(ViolationDetail.this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

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
        Log.d("mylog", "Path: " + mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));

                int currentBitmapWidth = bitmap.getWidth();
                int currentBitmapHeight = bitmap.getHeight();

                int ivWidth = mImage.getWidth();
                int ivHeight = mImage.getHeight();
                int newWidth = ivWidth;

                int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

                Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                mImage.setImageBitmap(newbitMap);
                //mImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private boolean isValid() {
        if (mName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show();
            mName.requestFocus();
            return false;
        }

        if (mVessel.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter vessel number.", Toast.LENGTH_SHORT).show();
            mVessel.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == mAddPhoto) {
            launchCamera();
        }
    }
}