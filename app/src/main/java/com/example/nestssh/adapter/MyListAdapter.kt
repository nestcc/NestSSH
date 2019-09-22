package com.example.nestssh.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.nestssh.R
import kotlinx.android.synthetic.main.conn_list.view.*

/*
* 连接选择界面列表Adapter
* list为连接列表，AddConnection中的connsName
* position为列表第几条
*/

class MyListAdapter(private val context: Context, private val list: List<Any?>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {

            view = LayoutInflater.from(context).inflate(R.layout.conn_list,null)
            view.connName.text = this.list[position].toString()
            view.del.setOnClickListener{
                val deletBroadcast = Intent()
                deletBroadcast.action = "Action.DeletConnection"
                deletBroadcast.putExtra("name", this.list[position].toString())
                context.sendBroadcast(deletBroadcast)
                Log.i("broadcast", "send Action.DeletConnection")
            }

        }else{
            view = convertView
        }

        return view
    }

    override fun getCount(): Int {
        return this.list.size
    }

    override fun getItem(position: Int): Any {
        return this.list[position].toString()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }



}