package com.example.nestssh

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.nestssh.adapter.MyListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refresh()
        }
    }

    private val delet = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
//            Log.i("broadcast", "received Action.DeletConnection")
            val action = intent.action
            val delName = intent.getStringExtra("name")
//            Log.i("delet name", delName)
            val spName = getSharedPreferences("connsName", Context.MODE_PRIVATE)
            if (spName.contains(delName)){
                val nameEditor = spName.edit()
                nameEditor.remove(delName)
                nameEditor.putInt("totalNum", spName.getInt("totalNum", 0)-1)
                nameEditor.apply()

                val sp = getSharedPreferences(delName, Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.clear()
                editor.apply()
                refresh()
            } else{
                Log.i("delet error", "delet object not found")
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        refresh()

        fab.setOnClickListener {
            startActivity(Intent(this, AddConnection::class.java))
        }

        //register broadcast receiver
        val intentRefresh = IntentFilter("Action.Refresh")
        registerReceiver(receiver, intentRefresh)

        val delIntent = IntentFilter("Action.DeletConnection")
        registerReceiver(delet, delIntent)

    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        unregisterReceiver(delet)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        val spName = getSharedPreferences("connsName", Context.MODE_PRIVATE)
//        val totalNum = spName.getInt("totalNum", 0)

        val nameList = spName.all
        nameList.remove("totalNum")

        val keyList = nameList.keys.toList()
        val adapter: MyListAdapter = MyListAdapter(this, keyList)
        ssh_list.adapter = adapter
        ssh_list.setOnItemClickListener { parent, view, position, id ->
//            startActivity(Intent(this, ManageConnection::class.java).putExtra("saveName", keyList[position]))
            val manage = Intent(this, ManageConnection::class.java)
            manage.putExtra("save_name", keyList[position])
            startActivity(manage)
        }

    }

}
