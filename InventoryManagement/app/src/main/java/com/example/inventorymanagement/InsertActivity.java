package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InsertActivity extends AppCompatActivity
{
    //TODO: Improvements needed, optional field list must be able to be grown or omitted per user requirements. Currently, this works for only 1 optional field,and is by default NULL until code is changed to check for isEmpty().
    private DatabaseManager dbManager;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbManager = new DatabaseManager(this);
        setContentView(R.layout.activity_insert);

        /*
        Button will either display and call "Insert" if we're using the Insert activity,
        or it will display and call "Update" if we're using the Update activity based on
        whether or not the intent has a Parcelable Extra
         */
        b = (Button)findViewById(R.id.b_add);
        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(getIntent().hasExtra("drug"))
                {
                    //Button will call DatabaseManager update()
                    update((Drugs)getIntent().getParcelableExtra("drug"));
                }
                else
                {
                    //Button will call DatabaseManager insert()
                    insert();
                }
            }
        });
        if(getIntent().hasExtra("drug"))
        {
            //Sets the button to "Update" and fills the fields with the retrieved data
            b.setText("Update");
            populateFields((Drugs)getIntent().getParcelableExtra("drug"));
        }
        else
        {
            //Sets the button to "Insert", new records to be added
            b.setText("Insert");
        }

    }

    public void insert()
    {
        EditText i_ndc = findViewById(R.id.i_ndc),
        i_drugName = findViewById(R.id.i_drugName),
        i_drugStrength = findViewById(R.id.i_drugStrength),
        i_dosageForm = findViewById(R.id.i_dosageForm),
        i_manufacturer = findViewById(R.id.i_manufacturer),
        l_optionalTitle = findViewById(R.id.l_additionalField),
        i_optionalData = findViewById(R.id.i_additionalField);

        String ndc = i_ndc.getText().toString(),
        drugName = i_drugName.getText().toString(),
        drugStrength = i_drugStrength.getText().toString(),
        dosageForm = i_dosageForm.getText().toString(),
        manufacturer = i_manufacturer.getText().toString(),
        optionalTitle = l_optionalTitle.getText().toString(),
        optionalData = i_optionalData.getText().toString();

        try
        {
            Drugs drug = new Drugs(-1, ndc, drugName, drugStrength, dosageForm, manufacturer, optionalTitle, optionalData);

            dbManager.insert(drug);

            Toast.makeText(this, "Drug added successfully, ID#" + drug.getId(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }


        //Clears data for next entry
        i_ndc.setText("");
        i_drugName.setText("");
        i_drugStrength.setText("");
        i_dosageForm.setText("");
        i_manufacturer.setText("");
        i_optionalData.setText("");
    }

    public void update(Drugs drug)
    {
        EditText i_ndc = findViewById(R.id.i_ndc),
                i_drugName = findViewById(R.id.i_drugName),
                i_drugStrength = findViewById(R.id.i_drugStrength),
                i_dosageForm = findViewById(R.id.i_dosageForm),
                i_manufacturer = findViewById(R.id.i_manufacturer),
                l_optionalTitle = findViewById(R.id.l_additionalField),
                i_optionalData = findViewById(R.id.i_additionalField);

        drug.setNDC(i_ndc.getText().toString());
        drug.setDrugName(i_drugName.getText().toString());
        drug.setDrugStrength(i_drugStrength.getText().toString());
        drug.setDosageForm(i_dosageForm.getText().toString());
        drug.setManufacturer(i_manufacturer.getText().toString());
        drug.setOptionalFieldTitle(l_optionalTitle.getText().toString());
        drug.setOptionalFieldData(i_optionalData.getText().toString());

        try
        {
            dbManager.updateDrug(drug);

            Toast.makeText(this, "Drug modified successfully, ID#" + drug.getId(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error " + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }

    public void populateFields(Drugs drug)
    {

        EditText i_ndc = findViewById(R.id.i_ndc),
                i_drugName = findViewById(R.id.i_drugName),
                i_drugStrength = findViewById(R.id.i_drugStrength),
                i_dosageForm = findViewById(R.id.i_dosageForm),
                i_manufacturer = findViewById(R.id.i_manufacturer),
                l_optionalTitle = findViewById(R.id.l_additionalField),
                i_optionalData = findViewById(R.id.i_additionalField);

        i_ndc.setText(drug.getNDC());
        i_drugName.setText(drug.getDrugName());
        i_drugStrength.setText(drug.getDrugStrength());
        i_dosageForm.setText(drug.getDosageForm());
        i_manufacturer.setText(drug.getManufacturer());
        l_optionalTitle.setText(drug.getOptionalFieldTitle());
        i_optionalData.setText(drug.getOptionalFieldData());
    }

    public void goBack(View view)
    {
        this.finish();
    }

}
