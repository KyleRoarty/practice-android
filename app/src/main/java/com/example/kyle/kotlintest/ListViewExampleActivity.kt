package com.example.kyle.kotlintest

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class ListViewExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_list_view_example2)
        val lv = findViewById<ListView>(R.id.list_view)

        val toolbarLayout = layoutInflater.inflate(R.layout.toolbar, findViewById<ViewGroup>(android.R.id.content))
        val myToolbar = toolbarLayout.findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)

        val operating_systems = arrayListOf<String>("Android", "iPhone", "WindowsMobile", "Blackberry",
                                        "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux")

        val adapter = StableArrayAdapter(this, android.R.layout.simple_list_item_1, operating_systems)

        lv.adapter = adapter

        lv.setOnItemClickListener(
                object : AdapterView.OnItemClickListener {
                    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long){
                        val item = parent.getItemAtPosition(position) as String
                        view.animate().setDuration(2000).alpha(0f).withEndAction(
                                object : Runnable {
                                    override fun run() {
                                        operating_systems.remove(item)
                                        adapter.notifyDataSetChanged()
                                        view.alpha = 1f
                                    }
                                }
                        )

                    }
                }
        )

    }

    private class StableArrayAdapter(context: Context, resource: Int, objects: ArrayList<String>) : MySimpleArrayAdapter(context, objects) {
        val mIdMap = HashMap<String, Int>()

        init {
            for (i in objects.indices) {
                mIdMap.put(objects.get(i), i)
            }
        }

        override fun getItemId(position: Int): Long{
            val item = getItem(position)
            return mIdMap.get(item)!!.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }
    }
}


