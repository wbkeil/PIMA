package com.example.inventorymanagement;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import static java.lang.System.in;
import static java.lang.System.out;

public class DatabaseManager extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "drugDatabase",
    DB_FILEPATH = "/data/data/com.example.inventorymanagement/databases/",
    TABLE_DRUGS = "currentDrugList",
    TABLE_MASTER = "masterDrugList",
    ID = "id",
    NDC = "NDC",
    DRUG_NAME = "drugName",
    DRUG_STRENGTH = "drugStrength",
    DOSAGE_FORM = "dosageForm",
    MANUFACTURER = "manufacturer",
    OPTIONAL_TITLE = "optionalDataTitle",
    OPTIONAL_DATA = "optionalData";
    Context ctext;

    public DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sqlCreate = "create table " + TABLE_DRUGS +
                "(" +
                ID + " integer, " +
                NDC + " text, " +
                DRUG_NAME + " text, " +
                DRUG_STRENGTH + " text, " +
                DOSAGE_FORM + " text, " +
                MANUFACTURER + " text," +
                OPTIONAL_TITLE + " text," +
                OPTIONAL_DATA + " text" +
                ")";
        db.execSQL(sqlCreate);

        sqlCreate = "create table " + TABLE_MASTER +
                "(" +
                NDC + " text, " +
                DRUG_NAME + " text, " +
                DRUG_STRENGTH + " text, " +
                DOSAGE_FORM + " text, " +
                MANUFACTURER + " text" +
                ")";
        db.execSQL(sqlCreate);

        try
        {
            InputStream is = ctext.getAssets().open("masterlist.csv");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line = "";
            String tableName = TABLE_MASTER;
            String columns = NDC + ", " + DRUG_NAME + ", " + DRUG_STRENGTH + ", " + DOSAGE_FORM + ", " + MANUFACTURER;
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
            String str2 = ");";

            db.beginTransaction();
            while ((line = buffer.readLine()) != null) {
                StringBuilder sb = new StringBuilder(str1);
                String[] str = line.split(",");
                sb.append("'" + str[0] + "',"); //NDC
                sb.append("'" + str[1] + "',"); //Drug Name
                sb.append("'" + str[2] + "',"); //Drug Strength
                sb.append("'" + str[3] + "',"); //Dosage Form
                sb.append("'" + str[4] + "'"); //Manufacturer
                sb.append(str2);
                db.execSQL(sb.toString());
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }
        catch (Exception ex)
        {
            Toast.makeText(ctext, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists " + TABLE_DRUGS);

        String sqlCreate = "create table " + TABLE_DRUGS +
                "(" +
                ID + " integer, " +
                NDC + " text, " +
                DRUG_NAME + " text, " +
                DRUG_STRENGTH + " text, " +
                DOSAGE_FORM + " text, " +
                MANUFACTURER + " text," +
                OPTIONAL_TITLE + " text," +
                OPTIONAL_DATA + " text" +
                ")";
        db.execSQL(sqlCreate);
    }

    public void insert(Drugs drugs)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlInsert = "INSERT INTO " + TABLE_DRUGS + " (" +
                NDC + ", " + DRUG_NAME + ", " + DRUG_STRENGTH + ", " + DOSAGE_FORM + ", " +
                MANUFACTURER + ", " + OPTIONAL_TITLE + ", " + OPTIONAL_DATA + ") " +
                "VALUES ('" + drugs.getNDC() + "', '" + drugs.getDrugName() + "', '" +
                drugs.getDrugStrength() + "', '" + drugs.getDosageForm() + "', '" +
                drugs.getManufacturer() + "', '" + drugs.getOptionalFieldTitle() + "', '" +
                drugs.getOptionalFieldData() + "');";

                db.execSQL(sqlInsert);

        sqlInsert = "UPDATE " + TABLE_DRUGS +
                    " SET " +  ID + " = (SELECT MAX(_rowid_) FROM " + TABLE_DRUGS + ") " +
                    "WHERE _rowid_ = (SELECT MAX(_rowid_) FROM " + TABLE_DRUGS + ")";
        db.execSQL(sqlInsert);
        db.close();

        db = this.getReadableDatabase();
        String sqlQuery = "SELECT MAX(_rowid_) FROM " + TABLE_DRUGS;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if(cursor.moveToFirst())
        {
            drugs.setId(cursor.getInt(0));
        }
        cursor.close();
        db.close();
    }


    public void updateDrug(Drugs drugs)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlUpdate = "UPDATE " + TABLE_DRUGS +
                " SET " + NDC + " = '" + drugs.getNDC() + "', " +
                DRUG_NAME + " = '" + drugs.getDrugName() + "', " +
                DRUG_STRENGTH + " = '" + drugs.getDrugStrength() + "', " +
                DOSAGE_FORM + " = '" + drugs.getDosageForm() + "', " +
                MANUFACTURER + " = '" + drugs.getManufacturer() + "', " +
                OPTIONAL_TITLE + " = '" + drugs.getOptionalFieldTitle() + "', " +
                OPTIONAL_DATA + " = '" + drugs.getOptionalFieldData() + "' " +
                "WHERE " + ID + " = " + drugs.getId() + ";";

        db.execSQL(sqlUpdate);
        db.close();
    }

    public void deleteById(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlDelete = "delete from " + TABLE_DRUGS + " where " + ID + " = " + id;

        db.execSQL(sqlDelete);
        db.close();
    }

    public Drugs selectById(int id)
    {
        String sqlQuery = "select * from " + TABLE_DRUGS;
        sqlQuery += " where " + ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        Drugs drugs = null;
        if (cursor.moveToFirst())
            drugs = new Drugs(
                cursor.getInt(0), // id
                cursor.getString(1), //NDC
                cursor.getString(2), //Drug Name
                cursor.getString(3), //Drug Strength
                cursor.getString(4), //Dosage Form
                cursor.getString(5), //Manufacturer
                cursor.getString(6), //Optional Title
                cursor.getString(7)  //Optional Data
            );
        db.close();
        return drugs;
    }

    public Drugs selectByNDC(String i_ndc)
    {
        String sqlQuery = "select * from " + TABLE_MASTER;
        sqlQuery += " where " + NDC + " = \"" + i_ndc + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        Drugs drugs = null;
        if(cursor.moveToFirst())
        {
            drugs = new Drugs(
                    0, //ID
            cursor.getString(0), //NDC
            cursor.getString(1), //Drug Name
            cursor.getString(2), //Drug Strength
            cursor.getString(3), //Dosage Form
            cursor.getString(4),  //Manufacturer
                    null, //Optional Title
                    null //Optional Data
            );
        }

        db.close();
        return drugs;
    }

    public ArrayList<Drugs> selectAll()
    {
        String sqlQuery = "select * from " + TABLE_DRUGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<Drugs> drugs = new ArrayList<Drugs>();
        while (cursor.moveToNext())
        {
            Drugs currentDrug = new Drugs(
                cursor.getInt(0), //ID
                cursor.getString(1), //NDC
                cursor.getString(2), //Drug Name
                cursor.getString(3), //Drug Strength
                cursor.getString(4), //Dosage Form
                cursor.getString(5), //Manufacturer
                cursor.getString(6), //Optional Title
                cursor.getString(7)  //Optional Data
            );
            drugs.add(currentDrug);
        }
        db.close();
        return drugs;
    }

    public void importDatabase(InputStream in) throws IOException
    {
        File dst = new File(DB_FILEPATH, DATABASE_NAME);
        try {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public void importCSV(InputStream is) throws Exception{
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = "";
            boolean isGoodFile = false;
            //Priming read to check header row
            line = buffer.readLine();
            if(line.equals("\"id\",\"NDC\",\"drugName\",\"drugStrength\",\"dosageForm\",\"manufacturer\",\"optionalDataTitle\",\"optionalData\"")) {
                isGoodFile = true;
            }
            if (isGoodFile) {
                while ((line = buffer.readLine()) != null) {
                    String[] str = line.split(",");

                    String ndc = str[1].replaceAll("\"", ""),
                            drugName = str[2].replaceAll("\"", ""),
                            drugStrength = str[3].replaceAll("\"", ""),
                            dosageForm = str[4].replaceAll("\"", ""),
                            manufacturer = str[5].replaceAll("\"", ""),
                            optionalTitle = str[6].replaceAll("\"", ""),
                            optionalData = str[7].replaceAll("\"", "");
                    Drugs drugs = new Drugs(-1, ndc, drugName, drugStrength, dosageForm, manufacturer, optionalTitle, optionalData);
                    insert(drugs);
                }
            }
            else
            {
                throw new InvalidFileException();
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    //MODE LEGEND: 0 = CSV, 1 = SQLite
    public void exportDatabase(String filename, int mode) throws IOException
    {
        boolean success = false;
        File src = new File(DB_FILEPATH, DATABASE_NAME);
        File path = new File(Environment.getExternalStorageDirectory(), "//InventoryManagement//");
        //Makes the path if it doesn't exist
        path.mkdirs();
        File dst = null;
        if (mode == 0) //Exporting to .CSV
        {
            filename += ".csv";
            try {
                dst = new File(path, filename);
                dst.createNewFile();
                CSVWriter csvWriter = new CSVWriter(new FileWriter(dst));
                SQLiteDatabase dbManager = this.getReadableDatabase();

                Cursor csvCursor = dbManager.rawQuery("SELECT * FROM " + TABLE_DRUGS, null);
                csvWriter.writeNext(csvCursor.getColumnNames());
                while (csvCursor.moveToNext()) {
                    String record[] =
                            {
                                    csvCursor.getString(0), //ID
                                    csvCursor.getString(1), //NDC
                                    csvCursor.getString(2), //Drug Name
                                    csvCursor.getString(3), //Drug Strength
                                    csvCursor.getString(4), //Dosage Form
                                    csvCursor.getString(5), //Manufacturer
                                    csvCursor.getString(6), //Optional Title
                                    csvCursor.getString(7)  //Optional Data
                            };
                    csvWriter.writeNext(record);
                }
                csvWriter.close();
                csvCursor.close();
                success = true;
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        else if (mode == 1) //Exporting to .SQLite
        {
            filename += ".sqlite";
            dst = new File(path, filename);
            try (InputStream in = new FileInputStream(src))
            {
                try (OutputStream out = new FileOutputStream(dst))
                {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0)
                    {
                        out.write(buf, 0, len);
                    }
                    success = true;
                }
                finally
                {
                    out.close();
                }
            }
            finally
            {
                in.close();
            }
        }

        if(success)
        {
            Toast.makeText(ctext, "Database exported successfully to " + path + filename, Toast.LENGTH_SHORT).show();
        }
    }

    public void clearDatabase()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 0, 0);
    }
}
