package com.ajiaco.unimatch

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ajiaco.unimatch.databinding.ActivityEditProfileBinding
import android.app.AlertDialog
import android.widget.EditText
import android.widget.TableLayout
import android.widget.Toast
import com.ajiaco.unimatch.PersonalProfileActivity
import com.ajiaco.unimatch.ui.ProfileCreationActivity
import com.ajiaco.unimatch.ui.ProfileFragment


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var imageView: ImageView
    private val GALLERY_REQUEST_CODE = 1000
    private val CAMERA_REQUEST_CODE = 1001
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar imageView
        imageView = binding.image

        // Configurar botones de imagen
        binding.btnGallery.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnCamera.setOnClickListener {
            checkCameraPermission()
        }

        // Configurar botón para la descripción (textarea)
        binding.textarea.setOnClickListener {
            showDescriptionDialog()
        }

        // Configurar las tablas como contenedores de información
        setupInformationContainers()
        // En tu Activity
        binding.rowOcupacion.setOnClickListener {
            showOcupacionDialog()
        }

        binding.rowGenero.setOnClickListener {
            showGeneroDialog()
        }

        binding.rowEducacion.setOnClickListener {
            showEducacionDialog()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, PersonalProfileActivity::class.java)
            startActivity(intent)

        }

    }

    private fun setupInformationContainers() {
        // Aquí puedes agregar los elementos a las tablas programáticamente
        // Por ejemplo, para la primera tabla (table1):
        binding.table1.apply {
            addView(createClickableRow("Ocupación", "Añadir") { showOcupacionDialog() })
            addView(createClickableRow("Género", "Seleccionar") { showGeneroDialog() })
            addView(createClickableRow("Educación", "Añadir") { showEducacionDialog() })
        }
    }

    private fun createClickableRow(label: String, value: String, onClick: () -> Unit): TableRow {
        val row = TableRow(this).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
            setOnClickListener { onClick() }
        }

        // Añadir TextView para la etiqueta
        row.addView(TextView(this).apply {
            text = label
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        })

        // Añadir TextView para el valor
        row.addView(TextView(this).apply {
            text = value
            gravity = Gravity.END
        })

        return row
    }

    private fun showDescriptionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Descripción")
            .setView(EditText(this).apply {
                setText(binding.textarea.text)
                hint = "Escribe aquí tu descripción"
                gravity = Gravity.TOP or Gravity.START
                minLines = 3
            })
            .setPositiveButton("Guardar") { dialog, _ ->
                val editText = (dialog as AlertDialog).findViewById<EditText>(android.R.id.edit)
                binding.textarea.setText(editText?.text.toString())
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showOcupacionDialog() {
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)

        builder.setTitle("Ocupación")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val newValue = editText.text.toString()
                if (newValue.isNotEmpty()) {
                    updateTableValue(binding.table1, 0, newValue)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showGeneroDialog() {
        val opciones = arrayOf("Hombre", "Mujer", "Otro")
        AlertDialog.Builder(this)
            .setTitle("Género")
            .setItems(opciones) { _, which ->
                updateTableValue(binding.table1, 1, opciones[which])
            }
            .show()
    }

    private fun showEducacionDialog() {
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)

        builder.setTitle("Educación")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val newValue = editText.text.toString()
                if (newValue.isNotEmpty()) {
                    updateTableValue(binding.table1, 2, newValue)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateTableValue(table: TableLayout, rowIndex: Int, newValue: String) {
        val row = table.getChildAt(rowIndex) as? TableRow
        val valueTextView = row?.getChildAt(1) as? TextView
        valueTextView?.text = newValue
    }

    // Métodos existentes para manejo de imagen
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        imageView.setImageURI(uri)
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        imageView.setImageBitmap(it)
                    }
                }
            }
          }
       }
}