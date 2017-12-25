package com.example.kyle.kotlintest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MySimpleArrayAdapter(context: Context, val values: ArrayList<String>) : ArrayAdapter<String>(context, -1, values) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var rowView = inflater.inflate(R.layout.learn_layout, parent, false)
        val textView = rowView.findViewById<TextView>(R.id.firstLine)
        val imageView = rowView.findViewById<ImageView>(R.id.icon)

        textView.setText(values.get(position))
        val s = values.get(position)

        if(s.startsWith("iPhone")){
            imageView.setImageResource(R.drawable.no)
        } else {
            imageView.setImageResource(R.drawable.ok)
        }
        return rowView
    }
}
