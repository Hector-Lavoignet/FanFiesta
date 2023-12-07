package me.hectorlavoignet.proyectorubenborrador

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import me.hectorlavoignet.proyectorubenborrador.Inicio.HomeActivity
import me.hectorlavoignet.proyectorubenborrador.Perfil.ProviderType
import me.hectorlavoignet.proyectorubenborrador.util.PreferenceHelper
import me.hectorlavoignet.proyectorubenborrador.util.PreferenceHelper.set
import me.hectorlavoignet.proyectorubenborrador.util.PreferenceHelper.get


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog : ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.SplashTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        val preferences = PreferenceHelper.defaultPrefs(this)
        if (preferences["session", false])
            loginInicio()

        val bRegistroLogin: Button = findViewById(R.id.bRegistroLogin)

        bRegistroLogin.setOnClickListener {
            registro()
        }
        val bLogin: Button = findViewById(R.id.bLogin)
        val etEmail: EditText = findViewById(R.id.etNombre)
        val etPassword: EditText = findViewById(R.id.etEmail)

        bLogin.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        etEmail.text.toString(),
                        etPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            login(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            showAlert()
                        }
                    }

            }
        }
        val tvReset: TextView = findViewById(R.id.tvResetPassword)

        tvReset.setOnClickListener {
            sendPasswordReset()
        }


    }

    private fun sendPasswordReset() {

        val intent = Intent(this, ResetPassword::class.java)
        startActivity(intent)
    }


    private fun loginInicio() {
        val i = Intent(this, HomeActivity::class.java)
        //createSessionPreference()
        startActivity(i)
        finish()
    }

    private fun registro() {
        val i = Intent(this, PantallaRegistro::class.java)
        startActivity(i)
    }

    private fun createSessionPreference() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["session"] = true
    }

    private fun InicializarVariables(){
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Iniciando Sesion")
        progressDialog.setCanceledOnTouchOutside(false)
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun login(email: String, provider: ProviderType) {
        val l = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(l)
    }

    private fun ComprobarSesion() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()
        } else {
            Toast.makeText(
                applicationContext, "Bienvenido(a) ${firebaseUser.email}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStart() {
        ComprobarSesion()
        super.onStart()
    }
}

