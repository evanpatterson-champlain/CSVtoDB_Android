package com.example.readcsv

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteexample.DBHelper
import java.io.*


class MainActivity : AppCompatActivity() {

    var database: DBHelper? = null
    val minId: Int = 1
    var maxId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = DBHelper(this, null)

        val data = readCSV()
        setUpDatabase(data)
    }

    private fun readCSV(): List<List<String>> {
        val contactsData: InputStream = resources.openRawResource(R.raw.contacts)
        val buffer = BufferedReader(InputStreamReader(contactsData))

        var data: MutableList<List<String>> = mutableListOf()

        try {
            buffer.forEachLine {
                data.add(it.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()))
            }
        } catch (e: IOException) {
            println(e)
        }

        data.removeFirst()

        return data.toList()
    }

    private fun setUpDatabase(csvData: List<List<String>>) {
        if (database != null) {
            maxId = csvData.size
            for (line in csvData) {
                database!!.addData(line)
            }
        }
    }

    private fun getRow(rowId: Int) {
        if (database != null) {
            val db = database!!
            if (maxId != null) {
                val max: Int = maxId!!
                if (rowId in minId..max) {
                    db.getData(rowId)
                }
            }
        }
    }


}