package com.example.mypractice.canvas;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.mypractice.R;

public class ColorMatrixFilterAct
        extends Activity implements OnClickListener {


    private Button button = null;
    private ColorMatrixView colorView = null;
    private EditText[] editTextArray = null;
    private float colorArray[] = null;
    private int[] EditTextID = {R.id.Edit1, R.id.Edit2, R.id.Edit3, R.id.Edit4, R.id.Edit5,
            R.id.Edit6, R.id.Edit7, R.id.Edit8, R.id.Edit9, R.id.Edit10,
            R.id.Edit11, R.id.Edit12, R.id.Edit13, R.id.Edit14, R.id.Edit15,
            R.id.Edit16, R.id.Edit17, R.id.Edit18, R.id.Edit19, R.id.Edit20};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_matrix);


        button = (Button) findViewById(R.id.Button);
        button.setOnClickListener(this);

        editTextArray = new EditText[20];
        colorArray = new float[20];
        for (int i = 0; i < 20; i++) {
            editTextArray[i] = (EditText) findViewById(EditTextID[i]);
        }

        colorView = (ColorMatrixView) findViewById(R.id.myColorView);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < 20; i++) {
            colorArray[i] = Float.valueOf(editTextArray[i].getText().toString().trim());
            System.out.println("i = " + i + ":" + editTextArray[i].getText().toString().trim());
        }
        colorView.setColorArray(colorArray);
    }
}