package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity
{
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbManager = new DatabaseManager(this);
        updateView();
    }

    public void updateView()
    {
        ArrayList<Drugs> drugs = dbManager.selectAll();
        RelativeLayout layout = new RelativeLayout(this);
        ScrollView scrollView = new ScrollView(this);
        RadioGroup group = new RadioGroup(this);

        RelativeLayout.LayoutParams params
                = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        int count = 0;
        //Build radio button group
        for (Drugs drug : drugs)
        {
            RadioButton rb = new RadioButton(this);
            rb.setId(drug.getId());
            rb.setText(drug.toString());
            rb.setLayoutParams(params);
            group.addView(rb);
            if(count % 2 == 0)
            {
                rb.setBackgroundColor(Color.rgb(160,200 ,255));
            }
            else
            {
                rb.setBackgroundColor(Color.TRANSPARENT);
            }
            count++;
        }

        //Set up RadioButtonHandler and listener
        RadioButtonHandler rbh = new RadioButtonHandler();
        group.setOnCheckedChangeListener(rbh);

        Button backButton = new Button(this);
        backButton.setText(R.string.back);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                UpdateActivity.this.finish();
            }
        });

        // add radio group to scroll view
        scrollView.addView(group, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        // add scrollview to the relative layout
        layout.addView(scrollView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams backParams
                = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // add back button to the relative layout
        backParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        backParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        backParams.setMargins(0, 0, 0, 60);
        layout.addView(backButton, backParams);

        setContentView(layout);  // link the layout to the activity
    }

    private class RadioButtonHandler implements RadioGroup.OnCheckedChangeListener
    {
        public void onCheckedChanged(RadioGroup group, int checkedId){
            Drugs drug = dbManager.selectById(checkedId);

            Intent intent = new Intent(UpdateActivity.this, InsertActivity.class);
            intent.putExtra("drug", drug);
            startActivityForResult(intent, 0);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        updateView();
    }
}

