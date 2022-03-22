package com.example.sqliteexample

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.StringBuilder

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        val defineColumns: String =
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "first_name VARCHAR(255), " +
            "last_name VARCHAR(255), " +
            "company_name VARCHAR(255), " +
            "address VARCHAR(255), " +
            "city VARCHAR(255), " +
            "county VARCHAR(255), " +
            "state VARCHAR(2), " +
            "zip VARCHAR(5), " +
            "phone1 VARCHAR(12), " +
            "phone VARCHAR(12), " +
            "email VARCHAR(256)"

        Log.d("Debug", "Creating database")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("CREATE TABLE $TABLE_NAME($defineColumns)")
    }

    private fun replaceTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun refreshDB() {
        val db = this.writableDatabase
        replaceTable(db)
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        replaceTable(db)
    }

    // This method is for adding data in our database
    fun addData(csvData: List<String>) {

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        for ((i, col) in COLUMN_NAMES.withIndex()) {
            values.put(col, csvData[i])
        }

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    fun getData(rowId: Int): Array<String> {
        val db = this.readableDatabase

        val colNames: String = COLUMN_NAMES.joinToString()

        val cursor: Cursor = db.rawQuery("SELECT $colNames FROM $TABLE_NAME WHERE id == $rowId;", null)
        cursor.moveToFirst();

        val data = mutableListOf<String>()
        for (cn in COLUMN_NAMES) {
            val strData: String = cursor.getString(cursor.getColumnIndex(cn).toInt())
            data.add(strData)
        }
        db.close()
        return data.toTypedArray()
    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private const val DATABASE_NAME = "MyContacts"

        // below is the variable for database version
        private const val DATABASE_VERSION = 1

        // below is the variable for table name
        const val TABLE_NAME = "Contacts"

        val COLUMN_NAMES = listOf<String>(
            "first_name",
            "last_name",
            "company_name",
            "address",
            "city",
            "county",
            "state",
            "zip",
            "phone1",
            "phone",
            "email").toTypedArray()
    }
}