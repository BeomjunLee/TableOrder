package com.example.ordermain_1.PageDR

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ordermain_1.R
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_camerlogo.setOnClickListener {

            QRscan()

        }
        //주석주석

        next.setOnClickListener {
            val intent =Intent(this, MenuPageUI::class.java)
            startActivity(intent)
        }

    }

        private fun QRscan() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("QR 코드 스캔해요")  //프롬프트
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)   //원하는 규격
        integrator.setCameraId(0)   //0은 후면, 1은 전면카메라
        integrator.setBeepEnabled(false)    //소리낼지말지
        integrator.setBarcodeImageEnabled(true) //이미지
        integrator.initiateScan()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null) {
            if(result.contents != null){
                Toast.makeText(this,"scanned: ${result.contents} format: ${result.formatName}", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()
            }


//            if(result.barcodeImagePath !=null){
//                val bitmap = BitmapFactory.decodeFile(result.barcodeImagePath)
//                scannedBitmap.setImageBitmap(bitmap)
//            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}