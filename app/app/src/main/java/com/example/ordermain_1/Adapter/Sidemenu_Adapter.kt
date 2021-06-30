package com.example.ordermain_1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ordermain_1.Item.SidemenuItem
import com.example.ordermain_1.R
import kotlinx.android.synthetic.main.item_layout_realmenu.view.*


class Sidemenu_Adapter:RecyclerView.Adapter<Sidemenu_Adapter.SideMenuViewHolder>() {

    private var sidemenuItemList : List<SidemenuItem>?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SideMenuViewHolder {
        return SideMenuViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_layout_realmenu,parent,false)
        )

    }

    override fun onBindViewHolder(holder: SideMenuViewHolder, position: Int) {
        sidemenuItemList?.let{
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return sidemenuItemList?.size ?:0
    }

    class SideMenuViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        private val menuImg : ImageView = itemView.real_menu_img


        fun bind(sidemeunuitem: SidemenuItem){

            Glide.with(itemView).load(sidemeunuitem.sidemenuimg).into(menuImg)
            itemView.real_menuname_txt.text = sidemeunuitem.sidemenuname
            itemView.real_menuinformation_txt.text = sidemeunuitem.sidemenuinformation
            itemView.real_menuprice_txt.text = sidemeunuitem.sidemenuprice

        }


    }


    fun submitList(list:List<SidemenuItem>?){
        sidemenuItemList = list
        notifyDataSetChanged()
    }


}