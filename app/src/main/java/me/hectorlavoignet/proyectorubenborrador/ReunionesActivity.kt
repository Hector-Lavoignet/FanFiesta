package me.hectorlavoignet.proyectorubenborrador

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import me.hectorlavoignet.proyectorubenborrador.Inicio.HomeActivity
import me.hectorlavoignet.proyectorubenborrador.Mensajes.MensajesActivity
import me.hectorlavoignet.proyectorubenborrador.Perfil.PerfilActivity

class ReunionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reuniones)

        val ibPerfilAct : ImageButton = findViewById(R.id.ibPerfilAct)
            ibPerfilAct.setOnClickListener {
            goToPerfil()
        }
        val ibHome : ImageButton = findViewById(R.id.ibHome)
            ibHome.setOnClickListener {
            goToHome()
        }
        val ibMensajes : ImageButton = findViewById(R.id.ibMensajes)
            ibMensajes.setOnClickListener {
            goToMensajes()
        }
    }

    private fun goToMensajes() {
        val intent = Intent(this, MensajesActivity::class.java)
            startActivity(intent)
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
    }

    private fun goToPerfil() {
        val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
    }
}