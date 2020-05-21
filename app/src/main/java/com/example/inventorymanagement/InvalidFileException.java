package com.example.inventorymanagement;

public class InvalidFileException extends Exception
{
    public InvalidFileException()
    {
        super("File is not a valid PIMA CSV or SQLite file");
    }
}
