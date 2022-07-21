package com.bookit.munhakhelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.Buffer;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<String> listData = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView ivInformation = findViewById(R.id.iv_information);
        ivInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.drawable.app_icon);
                dialog.setTitle("Application Inforrmation");
                dialog.setMessage("사용된 글꼴 : \n- 쿠키런체\n\nOpenSource : github.com/8bookit8/MunHakHelper");
                dialog.show();
            }
        });

        ListView lvLiterature = findViewById(R.id.lv_literature);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_literature, listData);
        lvLiterature.setAdapter(adapter);
        reloadLiteratureList();
        lvLiterature.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LiteratureActivity.fileName = listData.get(i);
                Intent intent = new Intent(getApplicationContext(), LiteratureActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabImport = findViewById(R.id.fab_import);
        fabImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInternalFile();
            }
        });
    }

    private boolean isTxt(String fileName) {
        Log.i("testTag", fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1).equals("txt");
    }

    public void reloadLiteratureList() {
        listData.clear();
        for (File file : getFilesDir().listFiles()) {
            if (!isTxt(file.getName())) {
                continue;
            }
            String fileName = file.getName();
            listData.add(fileName.substring(0, fileName.lastIndexOf(".")));
        }
        adapter.notifyDataSetChanged();
    }

    private void openInternalFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "문학 파일을 읽어오기 위해 필요한 권한입니다.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1008);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, 1008);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1008 && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            String path = fileUri.getPath();
            if (isTxt(path)) {
                try {
                    String copyPath = getFilesDir().getPath() + "/" + path.substring(path.lastIndexOf("/") + 1);
                    File file = new File(copyPath);
                    file.createNewFile();

                    FileInputStream inStream = (FileInputStream) getContentResolver().openInputStream(Uri.parse(fileUri.toString()));
                    FileOutputStream outStream = new FileOutputStream(copyPath);
                    FileChannel inChannel = inStream.getChannel();
                    FileChannel outChannel = outStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inStream.close();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                reloadLiteratureList();
            } else {
                Toast.makeText(this, "확장자가 .txt인 파일만 불러올 수 있습니다.", Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}