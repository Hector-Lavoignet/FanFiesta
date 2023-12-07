package me.hectorlavoignet.proyectorubenborrador

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import me.hectorlavoignet.proyectorubenborrador.databinding.ActivityInicioLoginBinding
import me.hectorlavoignet.proyectorubenborrador.util.PreferenceHelper
import me.hectorlavoignet.proyectorubenborrador.util.PreferenceHelper.set



class InicioLogin : AppCompatActivity() {

    private lateinit var binding: ActivityInicioLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInicioLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_inicio_login)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_perfil
            )
        )

         val bundle: Bundle? = intent.extras
         val email : String? = bundle?.getString("email")
         val provider : String? =bundle?.getString("provider")
            setup(email?: "", provider?: "")

        val bCerrarSesion : Button = findViewById(R.id.bCerrarSesion)
        bCerrarSesion.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            clearSessionPreference()
            goToLogin()
        }

        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

   private fun setup(email: String, provider: String) {
        val tvEmail : TextView = findViewById(R.id.tvEmail)
        val tvProvider : TextView = findViewById(R.id.tvProvider)

        tvEmail.text = email
        tvProvider.text = provider
    }


    private fun goToLogin() {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    private fun clearSessionPreference() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["session"] = false
    }

}