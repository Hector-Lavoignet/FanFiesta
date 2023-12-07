package me.hectorlavoignet.proyectorubenborrador.Mensajes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import me.hectorlavoignet.proyectorubenborrador.Inicio.HomeActivity
import me.hectorlavoignet.proyectorubenborrador.Mensajes.Fragmentos.FragmentoChats
import me.hectorlavoignet.proyectorubenborrador.Mensajes.Fragmentos.FragmentoUsuarios
import me.hectorlavoignet.proyectorubenborrador.Perfil.PerfilActivity
import me.hectorlavoignet.proyectorubenborrador.R
import me.hectorlavoignet.proyectorubenborrador.modelo.Chat
import me.hectorlavoignet.proyectorubenborrador.modelo.Usuario

class MensajesActivity : AppCompatActivity() {

    var reference : DatabaseReference?=null
    var firebaseUser : FirebaseUser?=null
    private lateinit var nombre_usuario : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensajes)
        InicializarComponentes()
        ObtenerDato()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_perfil->{
                val intent = Intent(applicationContext, PerfilActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_acerca_de->{
                Toast.makeText(applicationContext, "Acerca de", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.menu_salir->{
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun InicializarComponentes(){

        val toolbar : Toolbar = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseUser!!.uid)
        nombre_usuario = findViewById(R.id.Nombre_Usuario)

        val tabLayout : TabLayout = findViewById(R.id.TabLayoutMain)
        val viewPager : ViewPager = findViewById(R.id.ViewPagerMain)


        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var contMensajesNoLeidos = 0

                for (dataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceptor().equals(firebaseUser!!.uid)&& !chat.isVisto()){
                        contMensajesNoLeidos += 1
                    }
                }
                if (contMensajesNoLeidos == 0){
                    viewPagerAdapter.addItem(FragmentoChats(), "Chats")
                }else{
                    viewPagerAdapter.addItem(FragmentoChats(), "[$contMensajesNoLeidos] Chats")
                }
                viewPagerAdapter.addItem(FragmentoUsuarios(), "Usuarios")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun ObtenerDato(){
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                    nombre_usuario.text = usuario!!.getN_Usuario()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    class ViewPagerAdapter(fragmentManager : FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        private val listaFragmentos : MutableList<Fragment> = ArrayList()
        private val listaTitulos : MutableList<String> = ArrayList()

        override fun getCount(): Int {
            return listaFragmentos.size
        }

        override fun getItem(position: Int): Fragment {
            return listaFragmentos[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return listaTitulos[position]
        }
        fun addItem(fragment : Fragment, titulo : String){
            listaFragmentos.add(fragment)
            listaTitulos.add(titulo)
        }
    }
}