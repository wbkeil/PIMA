package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
        setTitle("Delete Items");
        updateView();
    }

    //Same as UpdateActivity updateView()
    public void updateView()
    {
        ArrayList<Drugs> drugs = dbManager.selectAll();
        RelativeLayout layout = new RelativeLayout(this);
        ScrollView scrollView = new ScrollView(this);
        RadioGroup group = new RadioGroup(this);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
        int count = 0;
        //Build radio group
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


        //Sets up back button
        Button backButton = new Button(this);
        backButton.setText(R.string.back);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                DeleteActivity.this.finish();
            }
        });

        //Sets up Clear All button to drop all contents of DB
        Button clearButton = new Button(this);
        clearButton.setText("CLEAR ALL");
        clearButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                new AlertDialog.Builder(DeleteActivity.this)
                        .setTitle("CONFIRM DELETION")
                        .setMessage("Are you sure you would like to clear the contents of the database?\n(This cannot be undone)")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                dbManager.clearDatabase();
                                Toast.makeText(DeleteActivity.this, "Database Cleared", Toast.LENGTH_SHORT).show();
                                DeleteActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                updateView();
                            }
                        }).show();
            }
        });
        // add radio group to scroll view
        scrollView.addView(group, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        // add scrollview to the relative layout
        layout.addView(scrollView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        // add back button to the relative layout
        RelativeLayout.LayoutParams backParams
                = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        backParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        backParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        backParams.setMargins(0, 0, 0, 60);

        layout.addView(backButton, backParams);

        RelativeLayout.LayoutParams clearParams
                = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        clearParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        clearParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        clearParams.setMargins(0, 0, 60, 60);

        layout.addView(clearButton, clearParams);


        setContentView(layout);  // link the layout to the activity
    }

    private class RadioButtonHandler implements RadioGroup.OnCheckedChangeListener
    {
        public void onCheckedChanged(final RadioGroup group, final int checkedId){
            new AlertDialog.Builder(DeleteActivity.this)
                    .setTitle("CONFIRM DELETION")
                    .setMessage("Are you sure you would like this entry deleted?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            dbManager.deleteById(checkedId);
                            Toast.makeText(DeleteActivity.this, "Drug Deleted", Toast.LENGTH_SHORT).show();
                            updateView();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            updateView();
                        }
                    }).show();
        }

    }
}
