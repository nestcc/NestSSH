package com.example.nestssh

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.nestssh.receiver.DirReceiver
import kotlinx.android.synthetic.main.activity_conn_dir.*
import android.util.Log
import com.example.nestssh.adapter.DirListAdapter


class ConnDir : AppCompatActivity() {
    private var privateRoot = ""

    private var host_conn = ""
    private var pwd_conn = ""
    private var user_name = ""
    private var timeout = ""
    private var port = ""

    private val dir = DirReceiver()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conn_dir)

        val getInfo = getIntent()
        this.host_conn = getInfo.getStringExtra("host")
        this.pwd_conn = getInfo.getStringExtra("pwd")
        this.user_name = getInfo.getStringExtra("user_name")
        this.timeout = getInfo.getStringExtra("timeout")
        this.port = getInfo.getStringExtra("port")

        this.textView4.setText("$host_conn $user_name $timeout$port")

        this.dir.jsch.setConfig(this.host_conn, this.user_name, this.pwd_conn, this.timeout.toInt(), this.port.toInt())
        Thread {
            this.dir.init("EXEC")
            this.privateRoot = this.dir.refreshDirRoot()
            val dirList = this.dir.getDirList(this.privateRoot)
            Log.i("dir list in create", "$dirList")

            runOnUiThread(Runnable {
//                Log.i("status", "UI Thread")
                textView4.text = this.privateRoot + " in_create"
                val adapter = DirListAdapter(this, dirList)
                this.dirList.adapter = adapter
                this.dirList.setOnItemClickListener { parent, view, position, id ->
                    if (dirList[position].contains('.')){
                        Log.i("not dir warning", "$position is not a dir")
                    } else {
                        changeDir(this.privateRoot + "/" + dirList[position].dropWhile { it == ' ' })
                    }
                }
            })
        }.start()
    }


    @SuppressLint("SetTextI18n")
    private fun changeDir(dir: String){
        Thread {
            this.privateRoot = dir
            val dirList = this.dir.getDirList(this.privateRoot)
            Log.i("dir list in change", "$dirList, " + dirList.isEmpty().toString())

            runOnUiThread(Runnable {
//                Log.i("status", "UI Thread")
                textView4.text = this.privateRoot + " in_change"
                val adapter = DirListAdapter(this, dirList)
                this.dirList.adapter = adapter
                this.dirList.setOnItemClickListener { parent, view, position, id ->
                    if (dirList[position].contains('.')){
                        Log.i("not dir warning", "$position is not a dir")
                    } else {
                        changeDir(this.privateRoot + dirList[position].dropWhile { it == ' ' })
                    }
                }
            })
        }.start()
    }


    override fun onDestroy() {
        this.dir.close("EXEC")
        this.dir.close("SHELL")
        super.onDestroy()
    }
}
