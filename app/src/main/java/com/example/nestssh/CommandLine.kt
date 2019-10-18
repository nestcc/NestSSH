package com.example.nestssh

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.nestssh.conn.jschConn
import kotlinx.android.synthetic.main.activity_command_line.*
import kotlin.concurrent.thread

class CommandLine : AppCompatActivity() {
    private val jsch = jschConn()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_command_line)

        val getInfo = getIntent()

        val host_conn = getInfo.getStringExtra("host")
        val user_name = getInfo.getStringExtra("user_name")
        val port = getInfo.getStringExtra("port")
        val time_out = getInfo.getStringExtra("timeout")
        val password = getInfo.getStringExtra("pwd")

        showInfo.text = "$host_conn , $user_name, $port"

        jsch.setConfig(host_conn, user_name, password, time_out.toInt(), port.toInt())
        Thread {
            jsch.initSession()
        }.start()
    }

    public fun downloadFile(view: View){
        Thread {
            jsch.downloadFile("./nccconfig.txt", "ncc.txt")
        }.start()
    }
}
