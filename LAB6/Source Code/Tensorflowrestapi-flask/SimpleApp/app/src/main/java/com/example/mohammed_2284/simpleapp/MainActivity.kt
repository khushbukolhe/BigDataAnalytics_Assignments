package com.example.mohammed_2284.simpleapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.mohammed_2284.simpleapp.retrofit.APIKindaStuff
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.util.Base64


import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private var ivImage: ImageView? = null
    private var textView: TextView? = null
    private var upflag: Boolean? = false
    private var selectedImage: Uri? = null
    private var bitmap: Bitmap? = null
    internal var imagepath = ""
    internal var fname: String = ""
    internal var file = File("")
    val jsonObj = JsonObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ivImage = findViewById<ImageView>(R.id.ivImage) as ImageView
        textView = findViewById<TextView>(R.id.textView) as TextView


        btnPOST.setOnClickListener {

            saveFile(bitmap, file)

            jsonObj.addProperty("imageBase64", encoder())
            //  POST demo
            APIKindaStuff
                    .service
                    .getVectors(jsonObj)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            println("---TTTT :: POST Throwable EXCEPTION:: " + t.message)
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val msg = response.body()?.string()
                                textView?.text = msg
                                println("---TTTT :: POST msg from server :: " + msg)
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                            }
                            else{
                                val msg = response.body()?.string()
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                            }

                        }
                    })
        }

        btnGET.setOnClickListener {
            APIKindaStuff
                    .service
                    .greetUser("Team 5")
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            println("---TTTT :: GET Throwable EXCEPTION:: " + t.message)
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                //val msg = response.body()?.string()
                                val msg = "We are Team 5"
                                println("---TTTT :: GET msg from server :: " + msg)
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }

        getPic.setOnClickListener {
            val cameraintent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraintent, 101)
        }
    }

    fun encoder(): String{
        val bytes = File(imagepath).readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        //val base64 = android.util.Base64.encode(bytes, android.util.Base64.DEFAULT)
        return base64
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {
            when (requestCode) {
                101 -> if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        selectedImage = data.data // the uri of the image taken
                        if ((data.extras!!.get("data") as Bitmap).toString() == "null") {
                            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        } else {
                            bitmap = data.extras!!.get("data") as Bitmap
                        }

                        ivImage?.setVisibility(View.VISIBLE)
                        ivImage?.setImageBitmap(bitmap)

                        //                            Saving image to mobile internal memory for sometime
                        val root = applicationContext.filesDir.toString()
                        val myDir = File("$root/predict")
                        myDir.mkdirs()

                        val generator = Random()
                        var n = 10000
                        n = generator.nextInt(n)

                        //                            Give the file name that u want
                        fname = "null$n.jpg"

                        imagepath = "$root/predict/$fname"
                        file = File(myDir, fname)
                        upflag = true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun saveFile(sourceUri: Bitmap?, destination: File) {

        if (destination.exists()) destination.delete()
        try {
            val out = FileOutputStream(destination)
            sourceUri!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}