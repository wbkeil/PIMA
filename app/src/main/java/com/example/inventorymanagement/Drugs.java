package com.example.inventorymanagement;

import android.os.Parcel;
import android.os.Parcelable;

public class Drugs implements Parcelable
{
    String m_ndc, m_drugName, m_drugStrength, m_dosageForm,
            m_manufacturer, m_optionalFieldTitle, m_optionalFieldData;
    int m_id; //returned from DatabaseManager insert() function

    public Drugs(Parcel in)
    {
        m_id = in.readInt();
        String[] drug = new String[7];
        in.readStringArray(drug);
        m_ndc = drug[0];
        m_drugName = drug[1];
        m_drugStrength = drug[2];
        m_dosageForm = drug[3];
        m_manufacturer = drug[4];
        m_optionalFieldTitle = drug[5];
        m_optionalFieldData = drug[6];
    }

    public Drugs(int id, String i_ndc, String i_drugName, String i_drugStrength,
                String i_dosageForm, String i_manufacturer,
                String i_optionalFieldTitle, String i_optionalFieldData)
    {
        m_id = id;
        m_ndc = i_ndc;
        m_drugName = i_drugName;
        m_drugStrength = i_drugStrength;
        m_dosageForm = i_dosageForm;
        m_manufacturer = i_manufacturer;
        m_optionalFieldTitle = i_optionalFieldTitle;
        m_optionalFieldData = i_optionalFieldData;

    }

    public int getId()
    {
        return m_id;
    }

    public void setId(int id)
    {
        m_id = id;
    }

    public String getNDC()
    {
        return m_ndc;
    }

    public void setNDC(String ndc)
    {
        m_ndc = ndc;
    }

    public String getDrugName()
    {
        return m_drugName;
    }

    public void setDrugName(String drugName)
    {
        m_drugName = drugName;
    }

    public String getDrugStrength()
    {
        return m_drugStrength;
    }

    public void setDrugStrength(String drugStrength)
    {
        m_drugStrength = drugStrength;
    }

    public String getDosageForm()
    {
        return m_dosageForm;
    }

    public void setDosageForm(String dosageForm)
    {
        m_dosageForm = dosageForm;
    }

    public String getManufacturer()
    {
        return m_manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        m_manufacturer = manufacturer;
    }

    public String getOptionalFieldTitle()
    {
        return m_optionalFieldTitle;
    }

    public void setOptionalFieldTitle(String optionalFieldTitle)
    {
        m_optionalFieldTitle = optionalFieldTitle;
    }

    public String getOptionalFieldData()
    {
        return m_optionalFieldData;
    }

    public void setOptionalFieldData(String optionalFieldData)
    {
        m_optionalFieldData = optionalFieldData;
    }

    public String toString()
    {
        StringBuilder item = new StringBuilder();
        item.append(String.format("%-35s%-100s%n" +
                        "%-29s%-100s%n" +
                        "%-28s%-100s%n" +
                        "%-27s%-100s%n" +
                        "%-28s%-100s",
                "NDC:", m_ndc,
                "Drug Name:", m_drugName,
                "Drug Strength:", m_drugStrength,
                "Dosage Form:", m_dosageForm,
                "Manufacturer:", m_manufacturer));
        if(!m_optionalFieldTitle.isEmpty())
        {
                item.append(String.format("%n%-32s%s",m_optionalFieldTitle + ":", m_optionalFieldData));
        }
        return item.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeInt(m_id);
        out.writeStringArray(new String[]
                {this.m_ndc,
                this.m_drugName,
                this.m_drugStrength,
                this.m_dosageForm,
                this.m_manufacturer,
                this.m_optionalFieldTitle,
                this.m_optionalFieldData});
    }

    public static final Parcelable.Creator<Drugs> CREATOR = new Parcelable.Creator<Drugs>()
    {
        public Drugs createFromParcel(Parcel in) {
            return new Drugs(in);
        }

        public Drugs[] newArray(int size) {
            return new Drugs[size];
        }
    };
}