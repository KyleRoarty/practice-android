package com.example.kyle.kotlintest

import android.app.ListActivity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

class ListViewCustom : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val operating_systems = arrayListOf<String>("Android", "iPhone", "WindowsMobile", "Blackberry",
                                        "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux")

        val adapter = MySimpleArrayAdapter(this, operating_systems)
        setListAdapter(adapter)

    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val item = listAdapter.getItem(position)
        Toast.makeText(this, "$item selected", Toast.LENGTH_LONG).show()
    }

    private class StableArrayAdapter(context: Context, resource: Int, objects: ArrayList<String>) : ArrayAdapter<String>(context, resource, objects) {
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


