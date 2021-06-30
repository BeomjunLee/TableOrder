package com.example.ordermain_1.Adapter


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ordermain_1.App

import com.example.ordermain_1.R
import com.example.ordermain_1.Item.RealMenuItem
import com.example.ordermain_1.PageDR.GoOrderPage
import kotlinx.android.synthetic.main.item_layout_realmenu.view.*



class RealMenu_Adapter:RecyclerView.Adapter<RealMenu_Adapter.RealMenuViewHolder>() {

    private var realmenuItemList : List<RealMenuItem>?=null
    val TAG : String ="로그"



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealMenuViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_realmenu,parent,false)
        return RealMenuViewHolder(view)

//                .apply {
//            itemView.setOnClickListener {
//                val intent = Intent(parent.context,GoOrderPage::class.java).apply {
//                    putExtra("data",)
//                }
//
//            }
//        }


    }

    override fun onBindViewHolder(holder: RealMenuViewHolder, position: Int) {
        realmenuItemList?.let{
            holder.bind(it[position])
        }
//
//        holder.itemView.setOnClickListener{
//            val intent = Intent(holder.itemView?.context, GoOrderPage::class.java)
//            ContextCompat.startActivity(holder.itemView.context,intent,null)
//        }

    }


    override fun getItemCount(): Int {
        return realmenuItemList?.size ?:0
    }

    class RealMenuViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        init{
            itemView.setOnClickListener {
                val intent = Intent(itemView.context,GoOrderPage::class.java)
                intent.putExtra("no",itemView.real_menuname_txt.text)
                itemView.context.startActivity(intent)
            }
        }


       private val menuImg : ImageView = itemView.real_menu_img


        fun bind(realmeunuitem: RealMenuItem){
            Glide.with(itemView).load(realmeunuitem.realmenuimg).into(menuImg)
            itemView.real_menuname_txt.text = realmeunuitem.realmenuname
            itemView.real_menuinformation_txt.text = realmeunuitem.realmenuinformation
            itemView.real_menuprice_txt.text = realmeunuitem.realmenuprice

//            itemView.setOnClickListener {
//                val intent = Intent(itemView?.context,GoOrderPage::class.java)
//
//                intent.apply {
//                    putExtra("data", realmeunuitem.toString())
//                }.run {
//                    ContextCompat.startActivity(itemView?.context, intent,null)
//                    Log.d(TAG, "bind: 됐니?")
//                }




                //이주석을 받아라
//            }


        }



    }


    fun submitList(list:List<RealMenuItem>?){
       realmenuItemList = list
        notifyDataSetChanged()
    }




}