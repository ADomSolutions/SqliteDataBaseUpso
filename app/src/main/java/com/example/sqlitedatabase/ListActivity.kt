package com.example.sqlitedatabase


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sqlitedatabase.databinding.ActivityListBinding
import java.io.File

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private var db: SQLiteDatabase? = null
    private var myFile: File? = null
    private var context: Context? = null

    private lateinit var itemArraylist:ArrayList<DataSet>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        myFile = ConnectionClass.myFile
        db = SQLiteDatabase.openOrCreateDatabase(myFile!!.absolutePath, null, null)

        context = this
        binding.itmList.layoutManager= LinearLayoutManager(this)
        binding.itmList.hasFixedSize()
        itemArraylist = arrayListOf()
        showDataList()

    }
    @SuppressLint("Recycle")
    private fun showDataList() {
        try {
            val cursor =
                db!!.rawQuery("SELECT * FROM student", null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val sId = cursor.getInt(0)
                    val name = cursor.getString(1).toString()
                    val address = cursor.getString(2).toString()
                    val sClass = cursor.getString(3).toString()
                    val age = cursor.getInt(4)
                    var x: ByteArray? = byteArrayOf(0x00)
                    if (cursor.getBlob(5) != null) {
                        x = cursor.getBlob(5)
                    }
                   val itemDS = x?.let { DataSet(sId,name,address,sClass, age, it) }
                    itemArraylist.add(itemDS!!)

                }
                val adapter = ItmAdapter(itemArraylist)
                binding.itmList.adapter = adapter
                adapter.setOnItemClickListener(object :ItmAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {
                        val itmPos = itemArraylist[position]
                       val  sId = itmPos.studentID
                        val intent = Intent(context, Student::class.java)
                        intent.putExtra("msg", "edit")
                        intent.putExtra("sid", sId)
                        startActivity(intent)
                        finish()
                    }

                })
            }

        } catch (ex: Exception) {
            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}