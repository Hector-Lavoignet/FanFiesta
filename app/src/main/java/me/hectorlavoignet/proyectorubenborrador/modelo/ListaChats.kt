package me.hectorlavoignet.proyectorubenborrador.modelo

class ListaChats {

    private var uid : String = ""

    constructor()

    constructor(uid:String){
        this.uid
    }

    fun getUid() : String?{
        return uid
    }

    fun setUid(uid : String?){
        this.uid = uid!!
    }

}