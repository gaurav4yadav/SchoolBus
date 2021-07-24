package com.slef.schoolbustracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun studentloginfunc(view: View) {

        val intent = Intent(this,studentLogin::class.java)
        startActivity(intent)
    }
    fun instituteloginfunc(view: View) {
        val intent =Intent (this,institueLogin::class.java)
        startActivity(intent)
    }

    fun registerfun(view: View) {

        val intent =Intent (this,driverlogin::class.java)
        startActivity(intent)
    }
}
