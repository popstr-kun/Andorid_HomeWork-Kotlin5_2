package com.example.lab15

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.lab15.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbrw    = DataBase(this).writableDatabase
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

        binding.listView.adapter = adapter
        setListener()
    }
    override fun onDestroy() {
        dbrw.close()
        super.onDestroy()
    }
    private fun setListener() {
        binding.btnInsert.setOnClickListener {
            if (binding.edBook.length() < 1 || binding.edPrice.length() < 1) Toast.makeText(this,"欄位請勿留空",Toast.LENGTH_SHORT).show()
            else
                try {
                    dbrw.execSQL("INSERT INTO myTable(book, price) VALUES(?,?)", arrayOf(binding.edBook.text.toString(), binding.edPrice.text.toString()))
                    Toast.makeText(this,"新增:${binding.edBook.text},價格:${binding.edPrice.text}",Toast.LENGTH_SHORT).show()
                    cleanEditText()
                } catch (e: Exception) {
                    Toast.makeText(this,"新增失敗:$e",Toast.LENGTH_SHORT).show()
                }
        }
        binding.btnUpdate.setOnClickListener{
            if (binding.edBook.length() < 1 || binding.edPrice.length() < 1) Toast.makeText(this,"欄位請勿留空",Toast.LENGTH_SHORT).show()
            else
                try {
                    dbrw.execSQL("UPDATE myTable SET price = ${binding.edPrice.text} WHERE book LIKE '${binding.edBook.text}'")
                    Toast.makeText(this,"更新:${binding.edBook.text},價格:${binding.edPrice.text}",Toast.LENGTH_SHORT).show()
                    cleanEditText()
                } catch (e: Exception) {
                    Toast.makeText(this,"更新失敗:$e",Toast.LENGTH_SHORT).show()
                }
        }
        binding.btnDelete.setOnClickListener{
            if (binding.edBook.length() < 1) Toast.makeText(this,"書名請勿留空",Toast.LENGTH_SHORT).show()
            else
                try {
                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE '${binding.edBook.text}'")
                    Toast.makeText(this,"刪除:${binding.edBook.text}",Toast.LENGTH_SHORT).show()
                    cleanEditText()
                } catch (e: Exception) {
                    Toast.makeText(this,"刪除失敗:$e",Toast.LENGTH_SHORT).show()
                }
        }
        binding.btnQuery.setOnClickListener{
            val queryString = if (binding.edBook.length() < 1)  "SELECT * FROM myTable"
            else  "SELECT * FROM myTable WHERE book LIKE '${binding.edBook.text}'"
            val c = dbrw.rawQuery(queryString, null)
            c.moveToFirst()
            items.clear()
            Toast.makeText(this,"共有${c.count}筆資料",Toast.LENGTH_SHORT).show()
            for (i in 0 until c.count) {
                items.add("書名:${c.getString(0)}\t\t\t\t 價格:${c.getInt(1)}")
                c.moveToNext()
            }
            adapter.notifyDataSetChanged()
            c.close()
        }
    }
    private fun cleanEditText() {
        binding.edBook.setText("")
        binding.edPrice.setText("")
    }
}