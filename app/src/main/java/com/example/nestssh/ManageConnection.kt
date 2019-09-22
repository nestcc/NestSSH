package com.example.nestssh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_manage_connection.*

class ManageConnection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_connection)

        val getConnName = getIntent()
        val saveName = getConnName.getStringExtra("save_name")
        this.connTitle.text = saveName

        val sp = getSharedPreferences(saveName, Context.MODE_PRIVATE)
        val Host = sp.getString("host", "0.0.0.0")
        val Pwd = sp.getString("pwd", "")
        val UserName = sp.getString("user_name", "root")
        val TimeOut = sp.getString("time_out", "3000")
        val port = sp.getString("port", "22")

        this.host.setText(Host)
        this.pwd.setText(Pwd)
        this.userName.setText(UserName)
        this.timeOut.setText(TimeOut)
        this.port.setText(port)
    }


    public fun connect(view: View){
        val saveName = connTitle.text.toString()
        val sp = getSharedPreferences(saveName, Context.MODE_PRIVATE)
        val spEditor = sp.edit()
        spEditor.putString("host", host.text.toString())
        spEditor.putString("pwd", pwd.text.toString())
        spEditor.putString("user_name", userName.text.toString())
        spEditor.putString("time_out", timeOut.text.toString())
        spEditor.putString("port", port.text.toString())

        spEditor.apply()
        val commLine = Intent(this, ConnDir::class.java).putExtra("host", host.text.toString())
        commLine.putExtra("pwd", pwd.text.toString())
        commLine.putExtra("timeout", timeOut.text.toString())
        commLine.putExtra("user_name", userName.text.toString())
        commLine.putExtra("port", port.text.toString())
        startActivity(commLine)
    }


    public fun save(view: View){
        val saveName = connTitle.text.toString()
        val sp = getSharedPreferences(saveName, Context.MODE_PRIVATE)
        val spEditor = sp.edit()
        spEditor.putString("host", host.text.toString())
        spEditor.putString("pwd", pwd.text.toString())
        spEditor.putString("user_name", userName.text.toString())
        spEditor.putString("time_out", timeOut.text.toString())
        spEditor.putString("port", port.text.toString())

        spEditor.apply()
        this.finish()
    }

}
