package com.example.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.basic.BasicListActivity
import com.example.demo.opengl.OpenGLActivity
import com.example.opengles.R


class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun goBasicDraw(v: View?) {
        startActivity(Intent(this, BasicListActivity::class.java))
    }

    fun goOpenGLActivity(v: View?) {
        startActivity(Intent(this, OpenGLActivity::class.java))
    }



}