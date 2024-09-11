package com.example.demo.basic

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demo.basic.shape.ShapeDrawActivity
import com.example.demo.basic.texture.TextureActivity
import com.example.opengles.R

class BasicListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun onShapeDrawActivity(view: View) {
        startActivity(Intent(this, ShapeDrawActivity::class.java))
    }

    fun onTextureDrawActivity(view: View) {
        startActivity(Intent(this, TextureActivity::class.java))
    }

}