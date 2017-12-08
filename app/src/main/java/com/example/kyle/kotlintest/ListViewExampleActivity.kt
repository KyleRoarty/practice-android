package com.example.kyle.kotlintest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class ListViewExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view_example2)

        val lv = findViewById<ListView>(R.id.list_view)

        val operating_systems = arrayListOf<String>("Android", "iPhone", "WindowsMobile", "Blackberry",
                                        "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux")

        

    }
}
