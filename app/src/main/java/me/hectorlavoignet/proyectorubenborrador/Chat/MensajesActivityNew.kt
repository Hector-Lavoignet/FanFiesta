package me.hectorlavoignet.proyectorubenborrador.Chat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import me.hectorlavoignet.proyectorubenborrador.Adaptador.AdaptadorChat
import me.hectorlavoignet.proyectorubenborrador.R
import me.hectorlavoignet.proyectorubenborrador.modelo.Chat
import me.hectorlavoignet.proyectorubenborrador.modelo.Usuario

class MensajesActivityNew : AppCompatActivity() {

    private lateinit var imagen_perfil_chat : ImageView
    private lateinit var IB_Adjuntar : ImageButton
    private lateinit var N_usuario_chat : TextView
    private lateinit var Et_mensaje : EditText
    private lateinit var IB_Enviar : ImageButton
    var uid_usuario_seleccionado : String = ""
    var firebaseUser : FirebaseUser ?= null
    private var imagenUri : Uri ?= null

    lateinit var RV_chats : RecyclerView
    var chatAdapter : AdaptadorChat ?= null
    var chatList : List<Chat> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensajes_new)
        InicializarVariables()
        ObtenerUid()
        LeerInfoUsuarioSeleccionado()

        IB_Adjuntar.setOnClickListener {
            AbrirGaleria()
        }

        IB_Enviar.setOnClickListener {
            val mensaje = Et_mensaje.text.toString()
            if (mensaje.isEmpty()){
                Toast.makeText(applicationContext, "Ingresa un mensaje", Toast.LENGTH_SHORT).show()
            }else{
                EnviarMensaje(firebaseUser!!.uid, uid_usuario_seleccionado, mensaje)
                Et_mensaje.setText("")
            }
        }

    }



    private fun ObtenerUid(){
        intent = intent
        uid_usuario_seleccionado = intent.getStringExtra("uid_usuario").toString()

    }

    private fun EnviarMensaje(uid_emisor : String, uid_receptor : String, mensaje : String) {

        val reference = FirebaseDatabase.getInstance().reference
        val mensajeKey = reference.push().key

        val infoMensaje = HashMap<String, Any?>()
        infoMensaje["id_mensaje"] = mensajeKey
        infoMensaje["emisor"] = uid_emisor
        infoMensaje["receptor"] = uid_receptor
        infoMensaje["mensaje"] = mensaje
        infoMensaje["url"] = ""
        infoMensaje["visto"] = false
        reference.child("Chats").child(mensajeKey!!).setValue(infoMensaje).addOnCompleteListener { tarea->
            if(tarea.isSuccessful){
                val listaMensajesEmisor = FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                    .child(firebaseUser!!.uid)
                    .child(uid_usuario_seleccionado)

                listaMensajesEmisor.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()){
                            listaMensajesEmisor.child("uid").setValue(uid_usuario_seleccionado)
                        }

                        val listaMensajesReceptor = FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                            .child(uid_usuario_seleccionado)
                            .child(firebaseUser!!.uid)
                        listaMensajesReceptor.child("uid").setValue(firebaseUser!!.uid)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }

        }

    }

    private fun InicializarVariables(){

        RV_chats = findViewById(R.id.RV_chats)
        RV_chats.setHasFixedSize(true)

        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        RV_chats.layoutManager = linearLayoutManager

        IB_Adjuntar = findViewById(R.id.IB_Adjuntar)
        imagen_perfil_chat = findViewById(R.id.imagen_perfil_chat)
        N_usuario_chat = findViewById(R.id.N_usuario_chat)
        Et_mensaje = findViewById(R.id.Et_mensaje)
        IB_Enviar = findViewById(R.id.ibEnviar)
        firebaseUser = FirebaseAuth.getInstance().currentUser
    }

    private fun LeerInfoUsuarioSeleccionado(){
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios")
            .child(uid_usuario_seleccionado)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario : Usuario?= snapshot.getValue(Usuario::class.java)

                N_usuario_chat.text = usuario!!.getN_Usuario()

                Glide.with(applicationContext).load(usuario.getImagen())
                    .placeholder(R.drawable.ic_item_usuario)
                    .into(imagen_perfil_chat)

                RecuperarMensajes(firebaseUser!!.uid, uid_usuario_seleccionado, usuario.getImagen())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun RecuperarMensajes(EmisorUid: String, ReceptorUid: String, ReceptorImagen: String?) {
        chatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()
                for (sn in snapshot.children){
                    val chat = sn.getValue(Chat::class.java)

                    if (chat!!.getReceptor().equals(EmisorUid) && chat.getEmisor().equals(ReceptorUid)
                        || chat.getReceptor().equals(ReceptorUid) && chat.getEmisor().equals(EmisorUid)){
                        (chatList as ArrayList<Chat>).add(chat)
                    }

                    chatAdapter = AdaptadorChat(this@MensajesActivityNew, (chatList as ArrayList<Chat>), ReceptorImagen!!)
                    RV_chats.adapter = chatAdapter

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun AbrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaARL.launch(intent)
    }

    private val galeriaARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado->
            if(resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data

                val cargandoImagen = ProgressDialog(this)
                cargandoImagen.setMessage("Por favor espere")
                cargandoImagen.setCanceledOnTouchOutside(false)
                cargandoImagen.show()

                val carpetaImagenes = FirebaseStorage.getInstance().reference.child("Imagenes de mensajes")
                val reference = FirebaseDatabase.getInstance().reference
                val idMensaje = reference.push().key
                val nombreImagen = carpetaImagenes.child("$idMensaje.jpg")

                val uploadTask :StorageTask<*>
                uploadTask = nombreImagen.putFile(imagenUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation nombreImagen.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        cargandoImagen.dismiss()
                        val downloadUrl = task.result
                        val url = downloadUrl.toString()

                        val infoMensajeImagen = HashMap<String, Any?>()
                        infoMensajeImagen["id_mensaje"] =idMensaje
                        infoMensajeImagen["emisor"] = firebaseUser!!.uid
                        infoMensajeImagen["receptor"] =uid_usuario_seleccionado
                        infoMensajeImagen["mensaje"] = "Se ha enviado la imagen"
                        infoMensajeImagen["url"] = url
                        infoMensajeImagen["visto"] = false

                        reference.child("Chats").child(idMensaje!!).setValue(infoMensajeImagen)
                            .addOnCompleteListener { tarea->
                                if(tarea.isSuccessful){
                                    val listaMensajesEmisor = FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                                        .child(firebaseUser!!.uid)
                                        .child(uid_usuario_seleccionado)

                                    listaMensajesEmisor.addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (!snapshot.exists()){
                                                listaMensajesEmisor.child("uid").setValue(uid_usuario_seleccionado)
                                            }

                                            val listaMensajesReceptor = FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                                                .child(uid_usuario_seleccionado)
                                                .child(firebaseUser!!.uid)
                                            listaMensajesReceptor.child("uid").setValue(firebaseUser!!.uid)

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                                }

                            }
                        Toast.makeText(applicationContext, " La imagen se ha enviado", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            else{
                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }

        }
    )

}