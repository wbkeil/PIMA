package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class InsertActivity extends AppCompatActivity
{
    private Button bInsertUpdate,
            bScanner;

    private DatabaseManager dbManager;

    private EditText i_ndc,
            i_drugName,
            i_drugStrength,
            i_dosageForm,
            i_manufacturer,
            l_optionalTitle,
            i_optionalData;

    private String scannerReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        //Instantiate the DB manager
        dbManager = new DatabaseManager(this);

        //Link buttons/labels/fields to ones in view
        i_ndc = findViewById(R.id.i_ndc);
        i_drugName = findViewById(R.id.i_drugName);
        i_drugStrength = findViewById(R.id.i_drugStrength);
        i_dosageForm = findViewById(R.id.i_dosageForm);
        i_manufacturer = findViewById(R.id.i_manufacturer);
        l_optionalTitle = findViewById(R.id.l_additionalField);
        i_optionalData = findViewById(R.id.i_additionalField);
        bInsertUpdate = findViewById(R.id.b_add);
        bScanner = findViewById(R.id.b_scanner);

        //Set up OnClickListeners
        bScanner.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                scanner();
            }
        });

        /*
        The insert/update button will either display and call "Insert" if we're using the
        Insert activity, or it will display and call "Update" if we're using the Update activity
        based on whether or not the intent has a Parcelable Extra. First we set up
        the correct function call for the button...
         */
        bInsertUpdate.setOnClickListener(new View.OnClickListener()
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

        /*
        ...then we change the label on the button to reflect the action that will be taken
         */
        if(getIntent().hasExtra("drug"))
        {
            //Sets the button to "Update" and fills the fields with the retrieved data
            bInsertUpdate.setText("Update");
            setTitle("Update Drug");
            populateFields((Drugs)getIntent().getParcelableExtra("drug"));
        }
        else
        {
            //Sets the button to "Insert", new records to be added
            bInsertUpdate.setText("Insert");
            setTitle("Add Drug");
        }
    }

    public void clearFields()
    {
        //Clears data for next entry
        i_ndc.setText("");
        i_drugName.setText("");
        i_drugStrength.setText("");
        i_dosageForm.setText("");
        i_manufacturer.setText("");
        i_optionalData.setText("");
    }
    //When we receive the NDC, we will first check to see if it is the correct length before we search.

    public void getDrugFromDatabase(String ndc)
    {
        EditText i_ndc = findViewById(R.id.i_ndc);

        if(!ndc.isEmpty())
        {
            if (ndc.charAt(0) == '3' && ndc.length() > 11) //ndc is 12 digits, we have a full barcode
            {
                ndc = ndc.substring(1, ndc.length() - 3);
            }
            if (ndc.length() == 10 || //Something like 0XXXX-XXXX-XX
                    (ndc.length() == 11 && !ndc.startsWith("0"))) //Something like XXXXX-XXX-XX
            {
                ndc = ndc.substring(0, ndc.length() - 2);
            }
        }
        try
        {
            Drugs drug = dbManager.selectByNDC(ndc);
            if(!l_optionalTitle.getText().toString().isEmpty())
            {
                drug.setOptionalFieldTitle(l_optionalTitle.getText().toString());
                drug.setOptionalFieldData(i_optionalData.getText().toString());
            }
            populateFields(drug);
        }
        catch(Exception ex)
        {
            if(!ndc.isEmpty())
            {
                Toast.makeText(this, "Drug not found", Toast.LENGTH_LONG).show();
            }
            else
            {
                //Nothing scanned, back button pressed, no message required
            }
            clearFields();
            i_ndc.setText(ndc);
        }
    }

    public void goBack(View view)
    {
        this.finish();
    }

    public void insert()
    {
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
        clearFields();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK && data != null)
        {
            scannerReturn = data.getData().toString();
        }
        else
        {
            scannerReturn = "";;
        }
        getDrugFromDatabase(scannerReturn);
    }

    public void populateFields(Drugs drug)
    {
        i_ndc.setText(drug.getNDC());
        i_drugName.setText(drug.getDrugName());
        i_drugStrength.setText(drug.getDrugStrength());
        i_dosageForm.setText(drug.getDosageForm());
        i_manufacturer.setText(drug.getManufacturer());
        l_optionalTitle.setText(drug.getOptionalFieldTitle());
        i_optionalData.setText(drug.getOptionalFieldData());
    }

    public void scanner()
    {
        String ndc = "";
        EditText i_ndc = findViewById(R.id.i_ndc);

        if(i_ndc.getText().toString().isEmpty()) //Looking to open camera to scan barcode
        {
            Intent intent = new Intent(InsertActivity.this, ScannerActivity.class);
            startActivityForResult(intent,0);
        }
        else //NDC is going to be entered manually/searched
        {
            ndc = i_ndc.getText().toString();
            getDrugFromDatabase(ndc);
        }

    }

    public void update(Drugs drug)
    {
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
}

/*
    Some information about NDCs:
    An NDC (National Drug Code) is an 11 digit number that specifies exactly which drug is in any
    given bottle in a  "XXXXX-XXXX-XX" format, where the first 5 digits specify the manufacturer,
    the second 4 specify the drug name and strength, and the last 2 specify the packaging size. This
    is also used as the product's UPC (Universal Packaging Code) and is attached and appended 1 digit
    (a classifier attached to the beginning, and a product package code at the end). Medications
    ALWAYS begin with 3 to classify the barcode as being for a drug, and the last digit varies
    depending on the packaging type. For our uses, we only care about the first 8 or 9. NDCs that
    begin with "0" are between 000x and 9999, and roll over to 1xxxx afterward. For example:

    0998-0630-xx --> Maxitrol 3.5mg/mL Ophthalmic Suspension by Alcon Laboratories
    10014-0001-xx --> Oxygen 990mL/L Gas by Aero All Gas Company
*/