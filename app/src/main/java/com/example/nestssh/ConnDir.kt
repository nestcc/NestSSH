package com.example.nestssh

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.nestssh.receiver.DirReceiver
import kotlinx.android.synthetic.main.activity_conn_dir.*
import android.util.Log
import android.view.animation.OvershootInterpolator
import com.example.nestssh.adapter.DirListAdapter

/*
* 处理目录信息
* 向DirReceiver发送查看请求
* 处理DirReceiver返回的dir list
* 将dir list整合进dirListView的Adapter中，使得dirListViewer显示dir list
* */
class ConnDir : AppCompatActivity() {
    private var privateRoot = "NO_CONNECTION" // 当前目录

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

        var flBtnUp = false

        this.floatingMenuButton.bringToFront()

        val getInfo = getIntent()
        this.host_conn = getInfo.getStringExtra("host")
        this.pwd_conn = getInfo.getStringExtra("pwd")
        this.user_name = getInfo.getStringExtra("user_name")
        this.timeout = getInfo.getStringExtra("timeout")
        this.port = getInfo.getStringExtra("port")

        this.connAlert.setText("$host_conn $user_name $timeout$port")

        this.dir.jsch.setConfig(this.host_conn, this.user_name, this.pwd_conn, this.timeout.toInt(), this.port.toInt())
        Thread {
            this.dir.init("EXEC")
            this.privateRoot = this.dir.refreshDirRoot()
            val dirList = this.dir.getDirList(this.privateRoot)

            runOnUiThread(Runnable {
                connAlert.text = this.privateRoot + " in_create"
                val adapter = DirListAdapter(this, dirList)
                this.dirList.adapter = adapter
                this.dirList.setOnItemClickListener { parent, view, position, id ->
                    if (dirList[position].contains('.')){
                        Log.i("not-dir warning", "$position is not a dir")
                    } else {
                        changeDir(this.privateRoot + "/" + dirList[position].dropWhile { it == ' ' })
                    }
                }
            })
        }.start()

        floatingMenuButton.setOnClickListener {
            if (this.privateRoot != "/"){
                val toRoot = this.privateRoot.dropLastWhile { it != '/' }.dropLast(1)
                if (toRoot == ""){
                    changeDir("/")
                } else {
                    changeDir(toRoot)
                }
            } else if (this.privateRoot == "/"){
                connAlert.text = "root dictionary"
            } else if (this.privateRoot == "NO_CONNECTION"){
                connAlert.text = "no connection"
            } else {
                connAlert.text = "error"
            }
        }

        floatingMenuButton.setOnLongClickListener {
            if (flBtnUp){
                hideMenu()
                flBtnUp = false
            } else {
                showMenu()
                flBtnUp = true
            }
            true
        }

        floatingCloseButton.setOnClickListener {
            this.finish()
        }

        flostingOpenStream.setOnClickListener {
            val commandStream = Intent(this, CommandLine::class.java)
                .putExtra("host", this.host_conn)
                .putExtra("port", this.port)
                .putExtra("pwd", this.pwd_conn)
                .putExtra("user_name", this.user_name)
                .putExtra("timeout", this.timeout)

            startActivity(commandStream)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun changeDir(dir: String){
        Thread {
            this.privateRoot = dir
            val dirList = this.dir.getDirList(this.privateRoot)
            Log.i("dirList", dirList.toString())

            runOnUiThread(Runnable {
                connAlert.text = this.privateRoot + " in_change"
                val adapter = DirListAdapter(this, dirList)
                this.dirList.adapter = adapter
                this.dirList.setOnItemClickListener { parent, view, position, id ->
                    if (dirList[position].contains('.')){
                        Log.i("not-dir warning", "$position is not a dir")
                    } else if (this.privateRoot != "/"){
                        changeDir(this.privateRoot + "/" + dirList[position].dropWhile { it == ' ' })
                    } else if (this.privateRoot == "/"){
                        changeDir(this.privateRoot + dirList[position].dropWhile { it == ' ' })
                    }
                }
            })
        }.start()
    }


    override fun onDestroy() {
        this.dir.close("EXEC")
//        this.dir.close("SHELL")
        super.onDestroy()
    }


    fun showMenu() {
        val showClose = ObjectAnimator.ofFloat(this.floatingCloseButton, "translationY", 0f, -440f)
        val showClient = ObjectAnimator.ofFloat(this.flostingOpenStream, "translationY", 0f, -220f)

        val animatorSet = AnimatorSet()
        animatorSet.setDuration(500)
        animatorSet.setInterpolator(OvershootInterpolator())
        animatorSet.playTogether(showClose, showClient)

        animatorSet.start()
    }


    fun hideMenu(){
        val showClose = ObjectAnimator.ofFloat(this.floatingCloseButton, "translationY", -440f, 0f)
        val showClient = ObjectAnimator.ofFloat(this.flostingOpenStream, "translationY", -220f, 0f)

        val animatorSet = AnimatorSet()
        animatorSet.setDuration(300)
        animatorSet.playTogether(showClose, showClient)

        animatorSet.start()
    }
}
