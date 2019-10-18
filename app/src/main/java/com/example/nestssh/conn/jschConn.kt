package com.example.nestssh.conn

import android.os.Environment
import android.util.Log
import com.jcraft.jsch.*
import java.io.*
import java.net.ConnectException
import java.util.*


/*
* 此为jsch链接远程主机的控制文件
* setConfig设置登录参数
* connect登录主机，此步获取session
*
* initExec登录主机并且初始化Exec，获取session
* exec(cmd)执行cmd命令 ： String,打开channel，执行命令，关闭channel
* closeExec关闭session
*/


class jschConn {
    private val jsch: JSch = JSch()

    private var host = "127.0.0.1"
    private var pwd = ""
    private var userName = ""
    private var timeOut = 3000
    private var port = 22

//    private var oStream: OutputStream? = null
    private var iStream: InputStream? = null
    private var sess: Session? = null
//    private var channel: Channel? = null
    private var channelExec: ChannelExec? = null

    fun setConfig(host: String, usr: String, pwd: String, timeout: Int, port: Int) {
        this.host = host
        this.userName = usr
        this.pwd = pwd
        this.timeOut = timeout
        this.port = port
    }


    fun initSession(){
        this.sess = jsch.getSession(this.userName, this.host, this.port)
        this.sess!!.setPassword(this.pwd)

        val config = Properties()
        config.setProperty("StrictHostKeyChecking", "no")
        config.setProperty("PreferredAuthentications", "password");

        this.sess!!.setConfig(config)
        this.sess!!.timeout = this.timeOut

        try {
            this.sess!!.connect()
        } catch (connExc: ConnectException) {
            Log.i("error","can not connect with " + this.host + ", please check your config and server")
        } catch (jschExc: JSchException) {
            Log.i("JSchException", jschExc.toString())
        }
    }


    fun exec(cmd: String): String{
        try{
            this.channelExec = this.sess!!.openChannel("exec") as ChannelExec
            this.iStream = this.channelExec!!.getInputStream()
            this.channelExec!!.setCommand(cmd)
            this.channelExec!!.setErrStream(System.err)
            this.channelExec!!.connect()
        } catch (jschExc: JSchException){
            Log.i("JSchException", jschExc.toString())
            return "ERROR_WHILE_GETTING_SESSION"
        }


        var result = ""
//        val temp = ByteArray(1024)
        val iBuffer = BufferedReader(InputStreamReader(this.iStream))

        while (true) {
            val line = iBuffer.readLine()
            if (line == null) break
//            println("line: $line")
            result += "$line "
        }
        this.channelExec!!.disconnect()
        return result.dropLastWhile { it == ' ' }
    }


    fun closeExec(){
        this.sess!!.disconnect()
    }

    fun downloadFile(downPath: String, saveName: String){
        val sftpChannel = this.sess!!.openChannel("sftp") as ChannelSftp
        val file = File(Environment.getExternalStorageDirectory().path + "/",saveName)
        if (file.exists()) {
            Log.i("File status", "exist")
        } else {
            Log.i("File status", "not exist")
        }
        try {
            val fOutStream = FileOutputStream(file)
            sftpChannel.get(downPath, fOutStream)
        } catch (e: FileNotFoundException) {
            Log.i("Operating file fail", e.toString())
        }


        sftpChannel.disconnect()
    }

}