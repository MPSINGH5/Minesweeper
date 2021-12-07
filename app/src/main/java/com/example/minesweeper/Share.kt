package com.example.minesweeper

import android.Manifest.permission.SEND_SMS
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import java.util.jar.Manifest


class Share : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_share)







        val sender:TextView=findViewById(R.id.Sender)
        val receiver:TextView=findViewById(R.id.Receiver)
        val phone:TextView=findViewById(R.id.Phone)
        val send: Button =findViewById(R.id.Send)
        val sharedPref = getSharedPreferences("Minesweeper", MODE_PRIVATE)
        val highScore= sharedPref.getInt("High Score", Int.MAX_VALUE)
        val best= ""+((highScore/1000)/60)+" m "+((highScore/1000)%60)+" s"//Formatting time accordingly
        //Setting onClicklistener for share
        send.setOnClickListener{
            if(sender.text.toString().trim()=="" || receiver.text.toString().trim()=="" || phone.text.toString().trim()=="")//Checking whether user entered the required info or not
                Toast.makeText(this, "Enter required information first", Toast.LENGTH_SHORT).show()
             else {


                val message ="Hello ${receiver.text.toString()}. ${sender.text.toString()}'s new best time in Minesweeper is  ${best}"
                        val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phone.text.toString().trim(), null, message, null, null)
                   Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                   finish()

            }


             }
        }
    }
