package me.hectorlavoignet.proyectorubenborrador

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PantallaRegistro : AppCompatActivity() {

    private lateinit var etNombre : EditText
    private lateinit var etCorreo : EditText
    private lateinit var etContra : EditText
    private lateinit var etRepetirContra : EditText
    private lateinit var bLogin : Button

    private lateinit var auth : FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_registro)
        InicializarVariables()

        bLogin.setOnClickListener {
            ValidarDatos()
        }
       /* val bLoginRegister : Button = findViewById(R.id.bLoginRegister)
            bLoginRegister.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }*/
    }


    private fun InicializarVariables() {
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etEmail)
        etContra = findViewById(R.id.etContra)
        etRepetirContra = findViewById(R.id.etRepetirContra)

        bLogin = findViewById(R.id.bLoginRegisterNew)

        auth = FirebaseAuth.getInstance()
    }
    private fun ValidarDatos() {
        val nombre_usuario : String = etNombre.text.toString()
        val email : String = etCorreo.text.toString()
        val password : String = etContra.text.toString()
        val rPassword : String = etRepetirContra.text.toString()

        if (nombre_usuario.isEmpty()){
           Toast.makeText(applicationContext, "Ingrese nombre de usuario", Toast.LENGTH_SHORT).show()
        }
        else if (email.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese su correo", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese su contraseña", Toast.LENGTH_SHORT).show()
        }
        else if (rPassword.isEmpty()){
            Toast.makeText(applicationContext, "Por favor repita su contraseña", Toast.LENGTH_SHORT).show()
        }
        else if (!password.equals(rPassword)){
            Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }else{
            RegistrarUsuario(email, password)
        }
    }

     private fun RegistrarUsuario(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful){
                var uid : String = ""
                uid = auth.currentUser!!.uid
                reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(uid)

                val hasmap = HashMap<String, Any>()
                val h_nombre_usuario : String = etNombre.text.toString()
                val h_email : String = etCorreo.text.toString()

                hasmap["uid"] = uid
                hasmap["n_usuario"] = h_nombre_usuario
                hasmap["email"] = h_email
                hasmap["imagen"] = ""
                hasmap["buscar"] = h_nombre_usuario.lowercase()

                /*Nuevos datos de usuarios*/
                hasmap["nombres"] = ""
                hasmap["apellidos"] = ""
                hasmap["edad"] = ""
                hasmap["profesion"] = ""
                hasmap["domicilio"] = ""
                hasmap["telefono"] = ""
                hasmap["estado"] = "offline"

                reference.updateChildren(hasmap).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful){
                        val intent = Intent(this@PantallaRegistro, MainActivity::class.java)
                        Toast.makeText(applicationContext, "Se ha registrado con exito", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }

                }
                    .addOnFailureListener {e ->
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(applicationContext, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()

            }

        }
            .addOnFailureListener {e2->
                Toast.makeText(applicationContext, "${e2.message}", Toast.LENGTH_SHORT).show()
            }
    }

}