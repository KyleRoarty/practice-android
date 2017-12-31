package com.example.kyle.kotlintest

import android.content.Context
import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.*

open class MySimpleArrayAdapter(context: Context, val values: ArrayList<String>) : ArrayAdapter<String>(context, -1, values) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.learn_layout, parent, false)
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

open class MySimpleArrayAdapter2(val list: ArrayList<String>) :  RecyclerView.Adapter<MySimpleArrayAdapter2.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.learn_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeAt(position: Int){
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
        //notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val textView = itemView.findViewById<TextView>(R.id.firstLine)
        val text2View = itemView.findViewById<TextView>(R.id.secondLine)
        val imageView = itemView.findViewById<ImageView>(R.id.icon)

        init {
            itemView.setOnClickListener(this)
        }


        fun bindItems(item: String) {
            textView.setText(item)

            if(item.startsWith("iPhone")){
                imageView.setImageResource(R.drawable.no)
                text2View.setText("Nope")
            } else {
                imageView.setImageResource(R.drawable.ok)
                text2View.setText("Yes")
            }
        }

        override fun onClick(p0: View) {
            p0.animate().setDuration(1000).alpha(0f).withEndAction(
                    object : Runnable {
                        override fun run() {
                            removeAt(adapterPosition)
                        }
                    }
            )
        }
    }



}