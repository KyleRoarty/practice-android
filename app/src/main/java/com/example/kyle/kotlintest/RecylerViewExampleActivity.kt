package com.example.kyle.kotlintest

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_recycler_view.*
import kotlinx.android.synthetic.main.alert_layout.*
import java.io.Serializable
import java.util.zip.Inflater

class RecylerViewExampleActivity : AppCompatActivity(), TestDialog.OnInputData {
    val rv by lazy {
        findViewById<RecyclerView>(R.id.recycler_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        //rv = findViewById<RecyclerView>(R.id.recycler_view)
        val toolbar = findViewById<Toolbar>(R.id.inc_toolbar)
        setSupportActionBar(toolbar)

        val operating_systems = arrayListOf<String>("Android", "iPhone", "WindowsMobile", "Blackberry",
                "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "Android", "iPhone", "WindowsMobile", "Blackberry",
                "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux")

        val adapter = MySimpleArrayAdapter2(operating_systems)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.test_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if(id == R.id.add){
            val dialog = TestDialog()
            dialog.show(fragmentManager, "abc")
            //Toast.makeText(applicationContext, "Add stuff", Toast.LENGTH_SHORT).show()
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onInput(name: String) {
        (rv.adapter as MySimpleArrayAdapter2).add(name)
    }
}

class TestDialog : DialogFragment() {

    interface OnInputData{
        fun onInput(name: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        with(builder) {

            setMessage("Hello, Dialog")
            setView(R.layout.alert_layout)

//            var name = username.text.toString()

            setPositiveButton("Hi!") { dialog, id ->
                val name = (dialog as AlertDialog).findViewById<EditText>(R.id.username).text.toString()
                val mCallback = activity as OnInputData
                mCallback.onInput(name)
                Toast.makeText(activity, "Hi, ${name}!", Toast.LENGTH_SHORT).show()


            }
            setNegativeButton("Bye") { dialog, id -> Toast.makeText(activity, "Bye", Toast.LENGTH_SHORT).show() }
        }
        return builder.create()
    }
}


