package com.example.nestssh

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.activity_add_connection.*
import kotlinx.android.synthetic.main.activity_conn_dir.*
import java.net.InetAddress

/*
* connsName 保存所有connectiond的saveName,以及计数总connection个数的totalNum
* connection
*   saveName作为SharedPreferences名称
*   host 主机地址，
*   pwd 密码，
*   user_name 主机用户名，
*   time_out 连接超时时长
*/
class AddConnection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_connection)
    }


    @SuppressLint("SetTextI18n")
    public fun saveConn(view: View){
        val spName: SharedPreferences = getSharedPreferences("connsName", Context.MODE_PRIVATE)
        if (spName.contains(saveName.text.toString())){
            connStatus.text = "connection name already exist"
        } else {
            val sp: SharedPreferences = getSharedPreferences(saveName.text.toString(), Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sp.edit()
            val editorNames: SharedPreferences.Editor = spName.edit()
            val totalNum = spName.getInt("totalNum", 0)

            editorNames.putInt("totalNum", totalNum+1)
            editorNames.putString(saveName.text.toString(), host.text.toString())

            editorNames.apply()

            editor.putString("host", host.text.toString())
            editor.putString("pwd", pwd.text.toString())
            editor.putString("user_name", userName.text.toString())
            editor.putString("time_out", timeOut.text.toString())
            editor.putString("port", port.text.toString())

            editor.apply()

            val broadcastIntent = Intent()
            broadcastIntent.setAction("Action.Refresh")
            sendBroadcast(broadcastIntent)
            Log.i("broadcast", "send action.fresh")
            this.finish()
        }
    }

    /*
    * 通过ping操作测试连接是否成功
    */
    @SuppressLint("SetTextI18n")
    public fun testConn(view: View){
        var status = false
        connStatus.text = "testing"

        val mHandler = @SuppressLint("HandlerLeak")
        object :Handler(){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.obj == "OVER"){
                    if(status){
                        connStatus.text = "ping ok"
                    } else {
                        connStatus.text = "ping not ok"
                    }
                }
                else{
                    connStatus.text = "testing"
                }
            }
        }

        Thread {
            status = InetAddress.getByName(host.text.toString()).isReachable(3000)
            val msg = Message()
            msg.obj = "OVER"
            mHandler.sendMessage(msg)
        }.start()
    }
}