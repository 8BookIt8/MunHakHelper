package com.bookit.munhakhelper;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class LiteratureActivity extends AppCompatActivity {

    public static String fileName;
    private ArrayList<TextView> contents = new ArrayList<TextView>();
    private ArrayList<String> wonmun = new ArrayList<String>();
    private ArrayList<String> hasuk = new ArrayList<String>();
    private ArrayList<Boolean> isHasuk = new ArrayList<Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_literature);

        TextView tvTitleLiterature = findViewById(R.id.tv_title_literature);
        tvTitleLiterature.setText(fileName);

        String path = getFilesDir().getPath() + "/" + fileName + ".txt";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

            String line;
            for (int i = 1; (line = reader.readLine()) != null; i++) {
                if (i % 2 == 1) {
                    wonmun.add(line);
                    addContent(line, i);
                } else {
                    hasuk.add(line);
                }
                isHasuk.add(false);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView ivDelete = findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(path);
                file.delete();
                new MainActivity().reloadLiteratureList();
                finish();
            }
        });
    }

    private void addContent(String content, int i) {
        LinearLayout lyContents = findViewById(R.id.ly_contents);

        LinearLayout llContent = new LinearLayout(getApplicationContext());
        llContent.setOrientation(LinearLayout.HORIZONTAL);

        ImageView ivEye = new ImageView(getApplicationContext());
        ivEye.setImageResource(R.drawable.eye);
        ivEye.setPadding(0, 24, 0, 24);
        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dataI = i / 2;
                isHasuk.set(dataI, !(isHasuk.get(dataI)));

                TextView tvContent = contents.get(dataI);
                if (isHasuk.get(dataI)) {
                    ivEye.setImageResource(R.drawable.eye_cancel);
                    tvContent.setText(hasuk.get((dataI)));
                } else {
                    ivEye.setImageResource(R.drawable.eye);
                    tvContent.setText(wonmun.get((dataI)));
                }
            }
        });
        llContent.addView(ivEye);

        TextView tvContent = new TextView(getApplicationContext());
        tvContent.setId(View.generateViewId());
        tvContent.setPadding(24, 24, 0, 24);
        tvContent.setTextSize(Dimension.DP, 40f);
        tvContent.setText(content);
        tvContent.setTextColor(Color.parseColor("#000000"));
        contents.add(tvContent);
        llContent.addView(tvContent);

        lyContents.addView(llContent);
    }
}
