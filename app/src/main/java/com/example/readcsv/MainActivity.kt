package com.example.readcsv

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sqliteexample.DBHelper
import java.io.*


class MainActivity : AppCompatActivity() {

    var database: DBHelper? = null
    val minId: Int = 1
    var maxId: Int? = null
    lateinit var viewModel: QueryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(QueryViewModel::class.java)

        database = DBHelper(this, null)

        val data = readCSV()
        setUpDatabase(data)

        val showInfoText = findViewById<TextView>(R.id.show_info)

        viewModel.curID.observe(this, Observer {
            val input: String? = viewModel.curID.value
            if (input != null) {
                val id: Int = input!!.toInt()
                showInfoText.text = getRow(id)
            }
        })

        setCurrentID()
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

    private fun getRow(rowId: Int): String {
        if (database != null) {
            val db = database!!
            if (maxId != null) {
                val max: Int = maxId!!

                // validate the number
                if (rowId in minId..max) {
                    val msg: String = db.getData(rowId).joinToString(", ")
                    return "Contact info: $msg"
                }
                else {
                    return "ID is not in the correct range."
                }
            }
        }
        return "Data does not exist."
    }

    private fun setCurrentID() {
        val idText = findViewById<EditText>(R.id.enterNumber)
        val enterButton = findViewById<Button>(R.id.enterIdButton)
        enterButton.setOnClickListener {
            viewModel.curID.value = idText.text.toString()
        }
    }


}