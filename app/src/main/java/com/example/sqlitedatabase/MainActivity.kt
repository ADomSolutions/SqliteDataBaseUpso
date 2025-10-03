package com.example.sqlitedatabase

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sqlitedatabase.databinding.ActivityMainBinding
import java.io.File

/*DOMENICO ALEJANDRO TP FINAL DE APLICACIONES MOVILES CON UN CRUD EN SQLITE*/

/*LA IDEA DE ESTE PROYECTO SIMPLE ES QUE SE PUEDa AGREGAR,MODIFICAR,
ACTUALIZAR Y ELIMINAR ALUMNOS DE LA UPSO CON INFO SIMPLE COMO NOMBRE
EDAD, DIRECCION, TURNO Y UNA FOTO DE TAMAÑO PEQUEÑO(opcional)*/

/*ALGUNAS OBSERVACIONES: EL PRIMER BOTON SIRVE PARA CREAR LA BASE DE DATOS,
 EL SEGUNDO BOTON AGREGA A LA BASE DE DATOS UN NUEVO ALUMNO,
 EL TERCER BOTON SE USA PARA EDITAR, LISTAR Y ELIMINAR,
 NOTA: SI SE EDITA O SE ELIMINA DESPUES DE ESA ACCIÓN VUELVE
 A LA PANTALLA PRINCIPAL.
 EL ULTIMO BOTON ELIMINA LA BASE DE DATOS ALUMNOS,
 O SEA, ELIMINA LA BASE DE DATOS Y HAY QUE CREAR UNA NUEVA
 PARA VOLVER A AGREGAR A LOS ALUMNOS.
*/

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var db: SQLiteDatabase? = null
    private var myFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar ajustes visuales
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Crear base de datos y tablas al iniciar
        createDatabase()
        createTables()

        // Configurar listeners para botones
        setupListeners()
    }

    // Crear la base de datos si no existe
    private fun createDatabase() {
        try {
            val folder =
                File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/Database")
            if (!folder.exists()) {
                folder.mkdir()
            }
            myFile = File(folder, "MyDb")
            ConnectionClass.myFile = myFile
            db = SQLiteDatabase.openOrCreateDatabase(myFile!!.absolutePath, null, null)
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    // Crear tablas necesarias
    private fun createTables() {
        try {
            var createTable =
                "CREATE TABLE IF NOT EXISTS student (studentId INTEGER PRIMARY KEY AUTOINCREMENT, studentName TEXT, address TEXT, class TEXT, age INTEGER, studentPhoto BLOB)"
            db!!.execSQL(createTable)

            createTable =
                "CREATE TABLE IF NOT EXISTS class (classId INTEGER PRIMARY KEY AUTOINCREMENT, className TEXT)"
            db!!.execSQL(createTable)
        } catch (ex: Exception) {
            Toast.makeText(this, "Error creando tablas: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Configurar acciones para botones
    private fun setupListeners() {
        // Crear base de datos
        binding.btnCreateDatabase.setOnClickListener {
            val folder = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/Database")
            if (!folder.exists()) {
                folder.mkdir()
            }
            val myFile = File(folder, "MyDb")
            if (!myFile.exists()) {
                SQLiteDatabase.openOrCreateDatabase(myFile.absolutePath, null, null).apply {
                    execSQL("CREATE TABLE IF NOT EXISTS student (studentId INTEGER PRIMARY KEY AUTOINCREMENT, studentName TEXT, address TEXT, class TEXT, age INTEGER, studentPhoto BLOB)")
                    close()
                }
                Toast.makeText(this, "Base de datos de Alumno ha sido creada!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "La base de datos ya existe, si necesita crearla nuevamente elimine la anterior.", Toast.LENGTH_SHORT).show()
            }
        }

        // Eliminar base de datos
        binding.btnDeleteRecord.setOnClickListener {
            val folder = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/Database")
            val myFile = File(folder, "MyDb")
            if (myFile.exists()) {
                myFile.delete()
                Toast.makeText(this, "Base de datos eliminada.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No existe una base de datos para eliminar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Acción del botón "Agregar Datos"
    fun addData(view: View) {
        val intent = Intent(this, Student::class.java)
        intent.putExtra("msg", "add")
        startActivity(intent)
    }

    // Acción del botón "Editar/Listar"
    fun editData(view: View) {
        val folder = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/Database")
        val myFile = File(folder, "MyDb")
        if (myFile.exists()) {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Primero debe crear la base de datos.", Toast.LENGTH_SHORT).show()
        }
    }
}
