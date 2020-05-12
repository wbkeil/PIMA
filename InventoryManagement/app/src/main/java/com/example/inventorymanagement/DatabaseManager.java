package com.example.inventorymanagement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "drugDatabase",
    TABLE_DRUGS = "drugs",
    ID = "id",
    NDC = "NDC",
    DRUG_NAME = "drugName",
    DRUG_STRENGTH = "drugStrength",
    DOSAGE_FORM = "dosageForm",
    MANUFACTURER = "manufacturer",
    OPTIONAL_TITLE = "optionalDataTitle",
    OPTIONAL_DATA = "optionalData";

    public DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists " + TABLE_DRUGS);
        onCreate(db);
    }

    //Returns the rowID for the drug just inserted to be used as the Drug's m_id data member
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
}
