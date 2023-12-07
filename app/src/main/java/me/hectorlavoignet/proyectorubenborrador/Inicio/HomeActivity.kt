package me.hectorlavoignet.proyectorubenborrador.Inicio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.hectorlavoignet.proyectorubenborrador.MainActivity
import me.hectorlavoignet.proyectorubenborrador.Mensajes.MensajesActivity
import me.hectorlavoignet.proyectorubenborrador.Perfil.PerfilActivity
import me.hectorlavoignet.proyectorubenborrador.R
import me.hectorlavoignet.proyectorubenborrador.ReunionesActivity

class HomeActivity : AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
         initRecyclerView()

        val ibReuniones : ImageButton = findViewById(R.id.ibReuniones)
        ibReuniones.setOnClickListener {
            goToReuniones()
        }
        val ibPerfil : ImageButton = findViewById(R.id.ibPerfilN)
        ibPerfil.setOnClickListener {
            goToPerfil()
        }
        val ibMensajes : ImageButton = findViewById(R.id.ibMensajes)
        ibMensajes.setOnClickListener {
            goToMensajes()
        }
         val ibHome : ImageButton = findViewById(R.id.ibHome)
         ibHome.setOnClickListener {
             val intent = Intent(this, MainActivity::class.java)
             startActivity(intent)
         }
    }

    private fun goToMensajes() {
        val intent = Intent(this, MensajesActivity::class.java)
        startActivity(intent)
    }

    private fun goToPerfil() {
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    private fun goToReuniones() {
        val intent = Intent(this, ReunionesActivity::class.java)
        startActivity(intent)
    }
    private fun initRecyclerView(){
        val recyclerView : RecyclerView = findViewById(R.id.rvHome)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HomeAdapter(HomeProvider.HomeList)
    }
}