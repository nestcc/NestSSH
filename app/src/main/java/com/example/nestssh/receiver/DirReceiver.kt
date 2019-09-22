package com.example.nestssh.receiver

import android.util.Log
import com.example.nestssh.conn.jschConn

class DirReceiver {
    private var dirRoot = ""

    val jsch = jschConn()

    fun init(method: String){
        if (method == "EXEC"){
            jsch.initExec()
        } else {
            Log.i("initalize error", "$method not exist")
        }
    }

    fun refreshDirRoot(): String{
        this.dirRoot = this.jsch.exec("pwd")
        return this.dirRoot
    }

    fun getDirList(dir: String): List<String> {
        return jsch.exec("cd $dir&&ls").split(' ')
    }


    fun close(method: String){
        if (method == "EXEC"){
            this.jsch.closeExec()
        } else {
            Log.i("closing error", "$method not exist")
        }
    }
}