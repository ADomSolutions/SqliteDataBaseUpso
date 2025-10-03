package com.example.sqlitedatabase

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItmAdapter(private val itmList: ArrayList<DataSet>):
    RecyclerView.Adapter<ItmAdapter.ItmHolder>() {
    private lateinit var mListener:OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener =listener
    }
    class ItmHolder(itmView: View,listener: OnItemClickListener): RecyclerView.ViewHolder(itmView) {
        val itmName: TextView =itmView.findViewById(R.id.tv_name)
        val itmAddress: TextView =itmView.findViewById(R.id.tv_address)
        val itmClass: TextView =itmView.findViewById(R.id.tv_class)
        val itmAge: TextView = itmView.findViewById(R.id.tv_age)
        val itmImg: ImageView = itmView.findViewById(R.id.st_img)

        init {
            itmView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItmHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item,
            parent,false)
        return ItmHolder(itemView,mListener)
    }

    override fun getItemCount(): Int {
        return itmList.size
    }

    override fun onBindViewHolder(holder: ItmHolder, position: Int) {
        val currentItem =itmList[position]
        holder.itmName.text =currentItem.studentName
        holder.itmAddress.text =currentItem.studentAddress
        holder.itmClass.text =currentItem.studentClass
        holder.itmAge.text =currentItem.age.toString()
        val x = currentItem.img

        if (x.isNotEmpty()){
            val bitmap = BitmapFactory.decodeByteArray(x, 0, x.size)
            holder.itmImg.setImageBitmap(bitmap)
        }



    }

}