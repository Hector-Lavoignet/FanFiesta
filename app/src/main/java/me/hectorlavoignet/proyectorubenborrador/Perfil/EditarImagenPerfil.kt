package me.hectorlavoignet.proyectorubenborrador.Perfil

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import me.hectorlavoignet.proyectorubenborrador.R

class EditarImagenPerfil : AppCompatActivity() {

    private lateinit var imagenPerfilActualizar : ImageView
    private lateinit var BtnElegirImagen : Button
    private lateinit var BtnActualizarImagen : Button
    private var imageUri : Uri?=null

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_imagen_perfil)

        imagenPerfilActualizar = findViewById(R.id.ImagenPerfilActualizar)
        BtnElegirImagen = findViewById(R.id.BtnElegirImagenDe)
        BtnActualizarImagen = findViewById(R.id.BtnActualizarImagen)

        progressDialog = ProgressDialog(this@EditarImagenPerfil)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        BtnElegirImagen.setOnClickListener {
            //Toast.makeText(applicationContext, "Seleccionar imagen de", Toast.LENGTH_SHORT).show()
            MostrarDialog()
        }
        BtnActualizarImagen.setOnClickListener {
            ValidarImagen()
            //Toast.makeText(applicationContext, "Actualizar Imagen", Toast.LENGTH_SHORT).show()
        }

    }

    private fun ValidarImagen(){
        if (imageUri == null){
            Toast.makeText(applicationContext,"Es necesario una imagen", Toast.LENGTH_SHORT).show()
        }else{
            SubirImagen()
        }
    }

    private fun SubirImagen() {
        progressDialog.setMessage("Actualizando imagen...")
        progressDialog.show()
        val rutaImagen = "Perfil_usuario/"+firebaseAuth.uid
        val referenceStorage = FirebaseStorage.getInstance().getReference(rutaImagen)
        referenceStorage.putFile(imageUri!!).addOnSuccessListener {tarea->
        val uriTarea : Task<Uri> = tarea.storage.downloadUrl
        while (!uriTarea.isSuccessful);
        val urlImagen = "${uriTarea.result}"
            ActualizarImagenBD(urlImagen)

        }.addOnFailureListener {e->
            Toast.makeText(applicationContext,"No se ha podido subir la imagen debido a: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ActualizarImagenBD(urlImagen: String) {
        progressDialog.setMessage("Actualizando imagen de perfil")
        val hashmap : HashMap<String, Any> = HashMap()
        if (imageUri!=null){
            hashmap["imagen"] = urlImagen
        }

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(firebaseAuth.uid!!).updateChildren(hashmap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,"Su imagen ha sido actualizada", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e->
            Toast.makeText(applicationContext,"No se ha actualizado la imagen debido a: ${e.message}", Toast.LENGTH_SHORT).show()


        }

    }

    private fun AbrirGaleria(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaActivityResultLauncher.launch(intent)
    }


    private val galeriaActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{resultado->
            if (resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imageUri = data!!.data
                imagenPerfilActualizar.setImageURI(imageUri)
            }else{
                Toast.makeText(applicationContext,"Cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }

        }
    )



    private fun AbrirCamara(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        camaraActivityResultLauncher.launch(intent)
    }

    private val camaraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado_camara->
           if(resultado_camara.resultCode == RESULT_OK){
               imagenPerfilActualizar.setImageURI(imageUri)
        }else{
            Toast.makeText(applicationContext,"Cancelado por el usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun MostrarDialog(){
        val btn_abrir_galeria : Button
        val btn_abrir_camara : Button

        val dialog = Dialog(this@EditarImagenPerfil)

        dialog.setContentView(R.layout.cuadro_d_seleccionar)

        btn_abrir_galeria = dialog.findViewById(R.id.Btn_abrir_galeria)
        btn_abrir_camara = dialog.findViewById(R.id.Btn_abrir_camara)

        btn_abrir_galeria.setOnClickListener {
            //Toast.makeText(applicationContext, "Abrir galeria", Toast.LENGTH_SHORT).show()
            AbrirGaleria()
            dialog.dismiss()
        }
        btn_abrir_camara.setOnClickListener {
            //Toast.makeText(applicationContext, "Abrir camara", Toast.LENGTH_SHORT).show()
            AbrirCamara()
            dialog.dismiss()
        }

        dialog.show()

    }
}