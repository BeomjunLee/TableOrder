package com.example.ordermain_1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ordermain_1.Item.DrinkmenuItem
import com.example.ordermain_1.R
import kotlinx.android.synthetic.main.item_layout_realmenu.view.*

class DrinkMeun_Adapter:RecyclerView.Adapter<DrinkMeun_Adapter.DrinkMenuViewHolder>() {

    private var drinkmenuItemList : List<DrinkmenuItem>?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkMenuViewHolder {
        return DrinkMenuViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_layout_realmenu,parent,false)
        )

    }

    override fun onBindViewHolder(holder: DrinkMenuViewHolder, position: Int) {
        drinkmenuItemList?.let{
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return drinkmenuItemList?.size ?:0
    }

    class DrinkMenuViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        private val menuImg : ImageView = itemView.real_menu_img


        fun bind(drinkmeunuitem: DrinkmenuItem){

            Glide.with(itemView).load(drinkmeunuitem.drinkmenuimg).into(menuImg)
            itemView.real_menuname_txt.text = drinkmeunuitem.drinkmenuname
            itemView.real_menuinformation_txt.text = drinkmeunuitem.drinkmenuinformation
            itemView.real_menuprice_txt.text = drinkmeunuitem.drinkmenuprice

        }


    }


    fun submitList(list:List<DrinkmenuItem>?){
        drinkmenuItemList = list
        notifyDataSetChanged()
    }

}