package com.example.sqlitedatabase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sqlitedatabase.databinding.ActivityStudentBinding
import java.io.ByteArrayOutputStream
import java.io.File

class Student : AppCompatActivity() {
    private lateinit var binding: ActivityStudentBinding
    private var imageByteArray: ByteArray? = null
    private var db: SQLiteDatabase? = null
    private var myFile: File? = null
    private lateinit var msg: String
    private var sId: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de bordes visuales
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Verificar si la base de datos existe
        myFile = ConnectionClass.myFile
        if (myFile == null || !myFile!!.exists()) {
            Toast.makeText(this, "Primero debe crear la base de datos.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Conexión a la base de datos
        db = SQLiteDatabase.openOrCreateDatabase(myFile!!.absolutePath, null, null)

        // Verificar la intención (Agregar o Editar)
        val i = intent
        msg = i.getStringExtra("msg").toString()
        sId = i.getIntExtra("sid", 0)
        when (msg) {
            "add" -> binding.btnSave.text = "Insertar Datos"
            "edit" -> {
                binding.btnSave.text = "Actualizar Datos"
                binding.btnDel.visibility = View.VISIBLE
                showData()
            }
        }
    }

    // Subir imagen
    fun uploadStudentImage(view: View) {
        val myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        myFileIntent.type = "image/*"
        activityResultLauncher.launch(myFileIntent)
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                imageByteArray = stream.toByteArray()
                binding.imageView.setImageBitmap(myBitmap)
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Insertar datos
    private fun insertData() {
        val sName = binding.edtName.text.toString()
        val sAddress = binding.edtAddress.text.toString()
        val sClass = binding.edtClass.text.toString()
        val sAge = binding.edtAge.text.toString()

        // Validaciones
        if (sName.isEmpty() || sAddress.isEmpty() || sClass.isEmpty() || sAge.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val values = ContentValues()
            values.put("studentName", sName)
            values.put("address", sAddress)
            values.put("class", sClass)
            values.put("age", sAge)
            values.put("studentPhoto", imageByteArray)
            db!!.insert("student", null, values)
            Toast.makeText(this, "Datos Insertados", Toast.LENGTH_SHORT).show()
            clr() // Limpia los campos
            binding.edtName.requestFocus() // Coloca el foco en el campo de Nombre
        } catch (ex: Exception) {
            Toast.makeText(this, "Error al insertar: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }


    // Actualizar datos
    private fun updateData() {
        val sName = binding.edtName.text.toString()
        val sAddress = binding.edtAddress.text.toString()
        val sClass = binding.edtClass.text.toString()
        val sAge = binding.edtAge.text.toString()

        // Validaciones
        if (sName.isEmpty() || sAddress.isEmpty() || sClass.isEmpty() || sAge.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val values = ContentValues()
            values.put("studentName", sName)
            values.put("address", sAddress)
            values.put("class", sClass)
            values.put("age", sAge)
            values.put("studentPhoto", imageByteArray)
            db!!.update("student", values, "studentId =$sId", null)
            Toast.makeText(this, "Datos Actualizados", Toast.LENGTH_SHORT).show()
            goToMainMenu() // Regresa al menú principal
        } catch (ex: Exception) {
            Toast.makeText(this, "Error al actualizar: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }


    // Eliminar datos
    fun delData(view: View) {
        try {
            db!!.delete("student", "studentId =$sId", null)
            Toast.makeText(this, "Datos Eliminados!", Toast.LENGTH_SHORT).show()
            goToMainMenu() // Regresa al menú principal
        } catch (ex: Exception) {
            Toast.makeText(this, "Error al eliminar: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }
    //volver al menu principal despues de eliminar o actualizar
    private fun goToMainMenu() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }


    // Mostrar datos existentes
    private fun showData() {
        try {
            val cursor = db!!.rawQuery("SELECT * FROM student WHERE studentId = $sId", null)
            if (cursor.moveToFirst()) {
                binding.edtName.setText(cursor.getString(1))
                binding.edtAddress.setText(cursor.getString(2))
                binding.edtClass.setText(cursor.getString(3))
                binding.edtAge.setText(cursor.getString(4))
                cursor.getBlob(5)?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    binding.imageView.setImageBitmap(bitmap)
                    imageByteArray = it
                }
            }
            cursor.close()
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // Limpiar campos
    private fun clr() {
        binding.edtName.text.clear()
        binding.edtAddress.text.clear()
        binding.edtClass.text.clear()
        binding.edtAge.text.clear()
        binding.imageView.setImageBitmap(null)
        imageByteArray = null
        binding.edtAge.clearFocus()
        binding.edtName.requestFocus()//Vuelvo a hacer foco en nombre despues de limpiar
    }

    // Guardar datos (Insertar o Actualizar)
    fun btnClick(view: View) {
        when (msg) {
            "add" -> insertData()
            "edit" -> updateData()
        }
    }
}
