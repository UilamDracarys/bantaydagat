package com.cpsu.bantaydagatviolationrecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cpsu.bantaydagatviolationrecorder.Data.Violation;
import com.cpsu.bantaydagatviolationrecorder.Data.ViolationRepo;
import com.cpsu.bantaydagatviolationrecorder.Utils.SearchableAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton fabAdd;
    public static int PERMISSION_ALL = 1;
    ArrayList<HashMap<String, String>> violationList;
    SearchableAdapter adapter;
    TextView mViolationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        loadViolations();
    }

    private void init() {
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == fabAdd) {
            Intent intent = new Intent(MainActivity.this, ViolationDetail.class);
            intent.putExtra("title", "Record Violation");
            startActivity(intent);
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void loadViolations() {

        ViolationRepo violationRepo = new ViolationRepo();
        violationList = violationRepo.getViolations();
        ListView lv = findViewById(R.id.violationList);
        lv.setFastScrollEnabled(true);

        if (violationList.size() != 0) {
            lv = findViewById(R.id.violationList);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        mViolationID = view.findViewById(R.id.vId);
                        String violationId = mViolationID.getText().toString();
                        Intent mIntent;
                        mIntent = new Intent(getApplicationContext(), ViolationDetail.class);
                        mIntent.putExtra("id", violationId);
                        mIntent.putExtra("title", "View Violation");
                        startActivity(mIntent);

                }
            });
            adapter = new SearchableAdapter(MainActivity.this,
                    violationList,
                    R.layout.violation_list_item,
                    new String[]{"ID", "NameVessel", "DateTime"},
                    new int[]{R.id.vId, R.id.nameAndVessel, R.id.dateTime});
            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }

    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadViolations();
    }
}