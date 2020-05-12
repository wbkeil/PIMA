package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeleteActivity extends AppCompatActivity
{
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbManager = new DatabaseManager(this);
        updateView();
    }

    //Same as UpdateActivity updateView()
    public void updateView()
    {
        ArrayList<Drugs> drugs = dbManager.selectAll();
        RelativeLayout layout = new RelativeLayout(this);
        ScrollView scrollView = new ScrollView(this);
        RadioGroup group = new RadioGroup(this);

        //Build radio group
        for (Drugs drug : drugs)
        {
            RadioButton rb = new RadioButton(this);
            rb.setId(drug.getId());
            rb.setText(drug.toString());
            group.addView(rb);
        }

        //Set up RadioButtonHandler and listener
        RadioButtonHandler rbh = new RadioButtonHandler();
        group.setOnCheckedChangeListener(rbh);

        Button backButton = new Button(this);
        backButton.setText(R.string.back);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                DeleteActivity.this.finish(); // pay attention to this statement
            }
        });

        // add radio group to scroll view
        scrollView.addView(group);
        // add scrollview to the relative layout
        layout.addView(scrollView);

        // add back button to the relative layout
        RelativeLayout.LayoutParams params
                = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(0, 0, 0, 60);
        layout.addView(backButton, params);

        setContentView(layout);  // link the layout to the activity
    }



    private class RadioButtonHandler implements RadioGroup.OnCheckedChangeListener
    {
        public void onCheckedChanged(RadioGroup group, int checkedId){
            dbManager.deleteById(checkedId);
            Toast.makeText(DeleteActivity.this, "Drug Deleted", Toast.LENGTH_SHORT).show();

            updateView();
        }
    }


}
