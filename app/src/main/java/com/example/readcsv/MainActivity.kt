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
import com.example.sqliteexample.DBHelper.Companion.COLUMN_NAMES
import java.io.*


class MainActivity : AppCompatActivity() {

    private lateinit var database: DBHelper
    private val minId: Int = 1
    var maxId: Int? = null
    lateinit var viewModel: QueryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(QueryViewModel::class.java)

        database = DBHelper(this, null)
        database.refreshDB()

        val data = readCSV(loadCSV())
        setUpDatabase(data)

        val showInfoText = findViewById<TextView>(R.id.show_info)

        viewModel.curID.observe(this, Observer {
            val input: Int? = viewModel.curID.value
            if (input != null) {
                val id: Int = input!!
                showInfoText.text = getRow(id)
            }
            else {
                showInfoText.text = ""
            }
        })

        setCurrentID()
    }

    private fun loadCSV(): BufferedReader {
        val contactsData: InputStream = resources.openRawResource(R.raw.contacts)
        return BufferedReader(InputStreamReader(contactsData))
    }

    private fun readCSV(buffer: BufferedReader): List<List<String>> {

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
        maxId = csvData.size
        for (line in csvData) {
            database.addData(line)
        }
    }

    private fun getRow(rowId: Int): String {
        if (maxId != null) {
            val max: Int = maxId!!

            // validate the number
            if (rowId in minId..max) {
                val info: Array<String> = database.getData(rowId)
                val msg: String = COLUMN_NAMES.zip(info).joinToString { "\n${it.first}: ${it.second}" }
                return "Contact info:\n $msg"
            }
            else {
                return "ID is not in the correct range."
            }
        }
        else {
            // if maxId hasn't been set, then the
            // size of the data is 0
            return "There is no data to pull from."
        }
    }

    private fun setCurrentID() {
        val idText = findViewById<EditText>(R.id.enterNumber)
        val enterButton = findViewById<Button>(R.id.enterIdButton)
        enterButton.setOnClickListener {
            val idTxt: String =  idText.text.toString()
            if (idTxt.filter { it.isDigit() } == idTxt) {
                try {
                    viewModel.curID.value = idTxt.toInt()
                }
                catch (nfe:NumberFormatException) {
                    Log.d("Error", "number entered is not numeric")
                }
            }
        }
    }


}