package com.example.ordermain_1.PageDR

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.ordermain_1.Item.RealMenuItem
import com.example.ordermain_1.R
import kotlinx.android.synthetic.main.activity_go_order_page.*

class GoOrderPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_order_page)
        
        val  name = intent.getStringExtra("no")

        tv_name.text= name






    }
}