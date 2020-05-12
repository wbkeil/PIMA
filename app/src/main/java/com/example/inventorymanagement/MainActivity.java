package com.example.inventorymanagement;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String writeAccess = Manifest.permission.WRITE_EXTERNAL_STORAGE; //For export of database/CSV
    private static final String readAccess = Manifest.permission.READ_EXTERNAL_STORAGE; //For import of database/CSV
    private static final String cameraAccess = Manifest.permission.CAMERA; //For barcode scanner
    private static final String[] permissionArray = new String[] {writeAccess, readAccess, cameraAccess};
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private String exportFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Items: " + drawDrugList());
    }

    protected void checkPermissions()
    {
        final List<String> missingPermissions = new ArrayList<String>();
        for(final String permission : permissionArray)
        {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED)
            {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[permissionArray.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, permissionArray,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
                //Opens the InsertActivity, drugs can be scanned/manually added here.
            case R.id.action_add:
                Intent insertIntent = new Intent(this, InsertActivity.class);
                this.startActivity(insertIntent);
                return true;

                //Opens the DeleteActivity, deletes items in the database
            case R.id.action_delete:
                Intent deleteIntent = new Intent(this, DeleteActivity.class);
                this.startActivity(deleteIntent);
                return true;

                //Opens the UpdateActivity, which uses the InsertActivity to update drugs
            case R.id.action_update:
                Intent updateIntent = new Intent(this, UpdateActivity.class);
                this.startActivity(updateIntent);
                return true;

            case R.id.action_import:
                importDatabase();
                return true;

            case R.id.action_export:
                try
                {
                    if(drawDrugList() != 0)
                    {
                        exportDatabase();
                    }
                    else
                    {
                        new AlertDialog.Builder(this)
                                .setTitle("EMPTY EXPORT")
                                .setMessage("There is no data to export.")
                                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        //Intentionally empty, no action required
                                    }
                                })
                                .show();
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
                }
                finally
                {
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onResume()
    {
        super.onResume();
        getSupportActionBar().setTitle("Items: " + drawDrugList());
    }

    public int drawDrugList()
    {
        //Pulls the information from the Drug table for display
        DatabaseManager dbManager = new DatabaseManager(this);
        ArrayList<Drugs> drugs = dbManager.selectAll();

        //Sets up views/layouts
        RelativeLayout window = findViewById(R.id.relativeLayout);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //Clears any leftover views (for onResume())
        window.removeAllViews();

        //Sets up layout parameters
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );

        //Adds all drugs to textViews, adds textViews to a linearLayout
        int count = 0;
        for (Drugs drug : drugs)
        {
            TextView textView = new TextView(this);
            textView.setText(drug.toString());
            if(count % 2 == 0)
            {
                textView.setBackgroundColor(Color.rgb(160,200 ,255));
            }
            else
            {
                textView.setBackgroundColor(Color.TRANSPARENT);
            }
            linearLayout.addView(textView);
            count++;
        }
        //Adds the linearLayout to the scrollView, the scrollView to the window
        scrollView.addView(linearLayout);
        window.addView(scrollView, params);
        return count;
    }

    public void exportDatabase()
    {
        final DatabaseManager dbManager = new DatabaseManager(this);
        final EditText input = new EditText(this);
        final String[] exportChoices = {"CSV", "SQLite (includes master table)"};
        final RadioGroup group = new RadioGroup(this);
        RelativeLayout view = new RelativeLayout(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter custom name");
        //Set up choices for CSV or SQLite export
        for (int i = 0; i < exportChoices.length; i++)
        {
            RadioButton rb = new RadioButton(this);
            rb.setText(exportChoices[i]);
            rb.setId(i);
            rb.setPaddingRelative(0,0,45,0);
            group.addView(rb);
        }

        //Sets the buttons side-by-side instead of stacked
        group.setOrientation(LinearLayout.HORIZONTAL);

        //Sets the option for CSV to be checked by default
        ((RadioButton)group.getChildAt(0)).setChecked(true);

        RelativeLayout.LayoutParams radioParams
                = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        radioParams.setMargins(0,0,0,0);
        radioParams.addRule(RelativeLayout.ALIGN_TOP);

        RelativeLayout.LayoutParams inputParams
                = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        inputParams.setMargins(0,90,0,0);

        view.addView(input, inputParams);
        view.addView(group, radioParams);

        //Generates the popup window that appears when the user chooses export
        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Name Export")
                .setMessage("Please enter a name for your export")
                /*
                    Rather than yes/no to export, I'm borrowing the 2 button system
                    to let the user choose a custom name, or a default time/date based
                    export of the database (for backups that are self-documenting)
                 */
                .setPositiveButton("-Custom Name (Default if left empty)-", new DialogInterface.OnClickListener()
                {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    try
                    {
                        if (input.getText().toString().isEmpty() == true)
                        {
                            exportFilename = Calendar.getInstance().getTime().toString();
                            dbManager.exportDatabase(exportFilename, group.getCheckedRadioButtonId());
                        }
                        else
                        {
                            dbManager.exportDatabase(input.getText().toString(), group.getCheckedRadioButtonId());
                        }
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                })
                .setNegativeButton("-Default (Date + Time)-", new DialogInterface.OnClickListener()
                {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    try
                    {
                        exportFilename = Calendar.getInstance().getTime().toString();
                        dbManager.exportDatabase(exportFilename, group.getCheckedRadioButtonId());
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).show();
    }

    public void importDatabase()
    {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(Intent.createChooser(chooseFile, "Select database or CSV file to import"), 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK)
        {
            try
            {
                Uri uri = data.getData();
                String fileType = uri.getLastPathSegment();
                final InputStream iStream = getContentResolver().openInputStream(uri);
                final DatabaseManager dbManager = new DatabaseManager(this);
                if(fileType.endsWith(".sqlite"))
                {
                    dbManager.importDatabase(iStream);
                    Toast.makeText(this, "IMPORT COMPLETE SUCCESSFULLY!", Toast.LENGTH_SHORT).show();
                }
                else if(fileType.endsWith(".csv"))
                {
                    new AlertDialog.Builder(this)
                            .setTitle("APPEND?")
                            .setMessage("Append imported list to current list?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    try
                                    {
                                        dbManager.importCSV(iStream);
                                        drawDrugList();
                                    }
                                    catch (Exception ex)
                                    {
                                        Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("No (will clear current list)", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    try
                                    {
                                        dbManager.clearDatabase();
                                        dbManager.importCSV(iStream);
                                        drawDrugList();
                                    }
                                    catch (Exception ex)
                                    {
                                        Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();

                }
                else
                {
                    throw new Exception("File is not a SQLite file, or CSV");
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(this,"Import unsuccessful:" + ex.toString(), Toast.LENGTH_LONG).show();
            }
            finally
            {
                drawDrugList();
            }
        }
        else
        {
            //resultCode not ok, do nothing
        }
    }
}
