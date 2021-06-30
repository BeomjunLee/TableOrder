package com.example.ordermain_1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordermain_1.Item.Menu_informationItem
import com.example.ordermain_1.R
import kotlinx.android.synthetic.main.item_layout_menuinformation.view.*

class MenuInformationRecyclerViewAdapter:RecyclerView.Adapter<MenuInformationRecyclerViewAdapter.MenuInformationViewHolder>(){

    private var menuinformationItemList : List<Menu_informationItem>?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuInformationViewHolder {
        return MenuInformationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout_menuinformation, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MenuInformationViewHolder, position: Int) {
        menuinformationItemList?.let{
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return menuinformationItemList?.size ?: 0
    }

    class MenuInformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(menuinforitem: Menu_informationItem){

            //매장 이름 및 리뷰
            itemView.store_name_txt.text=menuinforitem.store_name
            itemView.store_review_txt.text=menuinforitem.store_reivew


            //주문금액 등등
            itemView.menu_price_txt.text=menuinforitem.price
            itemView.menu_howsell_txt.text = menuinforitem.howsell
            itemView.menu_cooktime_txt.text = menuinforitem.cooktime

        }
    }

    fun submitList(list:List<Menu_informationItem>?){
        menuinformationItemList = list
        notifyDataSetChanged()
    }


}

