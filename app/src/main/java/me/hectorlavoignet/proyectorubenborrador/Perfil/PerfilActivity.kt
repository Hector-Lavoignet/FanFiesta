package me.hectorlavoignet.proyectorubenborrador.Perfil


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import me.hectorlavoignet.proyectorubenborrador.R
import me.hectorlavoignet.proyectorubenborrador.modelo.Usuario


enum class ProviderType{
    BASIC
}
class PerfilActivity : AppCompatActivity() {

    private lateinit var P_imagen : ImageView
    private lateinit var P_n_usuario : TextView
    private lateinit var P_email : TextView
    private lateinit var P_nombres : EditText
    private lateinit var P_apellidos : EditText
    private lateinit var P_edad : EditText
    private lateinit var P_telefono : EditText
    private lateinit var btn_guardar : Button
    private lateinit var Editar_imagen : ImageView

//    private lateinit var storage: FirebaseStorage
//    private lateinit var firebaseAuth: FirebaseAuth
    var user : FirebaseUser?=null
    var reference : DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        InicializarVariables()
        ObtenerDatos()
        btn_guardar.setOnClickListener {
            ActualizarInformacion()
        }
        Editar_imagen.setOnClickListener {
            val intent = Intent(applicationContext, EditarImagenPerfil::class.java)
            startActivity(intent)
        }

    }
    private fun InicializarVariables(){
        P_imagen = findViewById(R.id.P_imagen)
        P_n_usuario = findViewById(R.id.P_n_usuario)
        P_email = findViewById(R.id.P_email)
        P_nombres = findViewById(R.id.P_Nombres)
        P_apellidos = findViewById(R.id.P_apellidos)
        P_edad = findViewById(R.id.P_edad)
        P_telefono = findViewById(R.id.P_telefono)
        btn_guardar = findViewById(R.id.btn_Guardar)
        Editar_imagen = findViewById(R.id.Editar_imagen)

        user = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(user!!.uid)


    }

    private fun ObtenerDatos(){
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val usuario : Usuario?= snapshot.getValue(Usuario::class.java)
                    val str_n_usuario = usuario!!.getN_Usuario()
                    val str_email = usuario.getEmail()
                    val str_nombres = usuario.getNombres()
                    val str_apellidos = usuario.getApellidos()
                    val str_edad = usuario.getEdad()
                    val str_telefono = usuario.getTelefono()

                    P_n_usuario.text = str_n_usuario
                    P_email.text = str_email
                    P_nombres.setText(str_nombres)
                    P_apellidos.setText(str_apellidos)
                    P_edad.setText(str_edad)
                    P_telefono.setText(str_telefono)
                    Glide.with(applicationContext).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(P_imagen)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun ActualizarInformacion(){
        val str_nombres = P_nombres.text.toString()
        val str_apellidos = P_apellidos.text.toString()
        val str_edad = P_edad.text.toString()
        val str_telefono = P_telefono.text.toString()

        val hashmap = HashMap<String, Any>()
        hashmap["nombres"] = str_nombres
        hashmap["apellidos"] = str_apellidos
        hashmap["edad"] = str_edad
        hashmap["telefono"] = str_telefono

        reference!!.updateChildren(hashmap).addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(applicationContext, "Se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "No se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { e->
            Toast.makeText(applicationContext, "Ha ocurrido un error ${e.message} ", Toast.LENGTH_SHORT).show()

        }
    }
}


