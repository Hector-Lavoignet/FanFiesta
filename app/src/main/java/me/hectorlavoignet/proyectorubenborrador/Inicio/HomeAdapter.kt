package me.hectorlavoignet.proyectorubenborrador.Inicio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.hectorlavoignet.proyectorubenborrador.R

class HomeAdapter(private val HomeList : List<HomeFf>) : RecyclerView.Adapter<HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HomeViewHolder(layoutInflater.inflate(R.layout.item_partidos, parent, false))
    }

    override fun getItemCount(): Int {
        return HomeList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = HomeList[position]
        holder.render(item)
    }
}