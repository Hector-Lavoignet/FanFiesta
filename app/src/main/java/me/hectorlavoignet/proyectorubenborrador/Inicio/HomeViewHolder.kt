package me.hectorlavoignet.proyectorubenborrador.Inicio

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.hectorlavoignet.proyectorubenborrador.R

class HomeViewHolder (view : View):RecyclerView.ViewHolder(view) {


        val partido : TextView = view.findViewById(R.id.tvPartido)
        val horaPartido : TextView = view.findViewById(R.id.tvHoraPartido)
        val direccionEvento : TextView = view.findViewById(R.id.tvDireccionEvento)
        val ImagenPartido : ImageView = view.findViewById(R.id.ivPartido)

    fun render(home : HomeFf){
        partido.text = home.NombrePartido
        horaPartido.text = home.HoraPartido
        direccionEvento.text = home.DireccionEvento
    }
}