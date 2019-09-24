package com.example.nestssh.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.nestssh.R
import kotlinx.android.synthetic.main.dir_list.view.*
import kotlinx.android.synthetic.main.dir_list_unexpected.view.*

class DirListAdapter(private val context: Context, private val list: List<Any?>): BaseAdapter() {
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (list == listOf("")){
            view = LayoutInflater.from(context).inflate(R.layout.dir_list_unexpected, null)
        } else if (list == listOf("ERROR_WHILE_GETTING_SESSION")){
            view = LayoutInflater.from(context).inflate(R.layout.dir_list_unexpected, null)
            view.unexpection_info.text = "Wrong log in information"
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.dir_list,null)
            view.item_name.text = this.list[position].toString().dropWhile { it == ' ' }
        }
        return view
    }


    override fun getCount(): Int {
        return this.list.size
    }


    override fun getItem(position: Int): Any {
        return this.list[position].toString() + " "
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}