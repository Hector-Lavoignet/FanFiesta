package me.hectorlavoignet.proyectorubenborrador

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ResetPassword : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etResetPassword : EditText = findViewById(R.id.etResetPassword)
        val btnReset : Button = findViewById(R.id.btnReset)

        btnReset.setOnClickListener {
            sendPasswordReset(etResetPassword.text.toString())
        }

        firebaseAuth = Firebase.auth
    }

    private fun sendPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(baseContext, "Correo Enviado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(baseContext, "Error, no se pudo completar el proceso", Toast.LENGTH_SHORT).show()
                }

            }
    }

}