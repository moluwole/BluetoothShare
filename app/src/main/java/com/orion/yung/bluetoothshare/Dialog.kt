package com.orion.yung.bluetoothshare

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.find
import java.io.File


class Dialog : Activity(), MediaPlayer.OnPreparedListener {

    var mp = MediaPlayer()

    override fun onPrepared(p0: MediaPlayer?) {
        mp.setAudioStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE)
        mp.setVolume(1.5f, 1.5f)
        mp.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        intent = intent

        // val dialog_play = find<TextView>(R.id.dialog_play)
        val dialog_delete = find<TextView>(R.id.dialog_delete)
        val dialog_send = find<TextView>(R.id.dialog_send)
        val file = File(intent.getStringExtra("filepath"))

//        dialog_play.setOnClickListener{
//          //  var mp = MediaPlayer.create(this, Uri.fromFile(file))
//          //  var mp = MediaPlayer()
//           /* mp.setDataSource(this, Uri.fromFile(file))
//            mp.prepare()
//            mp.start()*/
//
//            var inputStream = FileInputStream(file)
//            mp.setDataSource(inputStream.fd)
//            mp.prepare()
//            inputStream.close()
//            //finish()
//        }

        dialog_delete.setOnClickListener {

            var id: Int = intent.getIntExtra("id", 0)
            file.delete()
            database.use {
                delete("files", """_id = $id""")
            }

            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        dialog_send.setOnClickListener {
            sendBluetooth(file)
            finish()
        }
    }

    fun sendBluetooth(data: File?) {

        var intent: Intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "audio/"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(data))
        // appContext?.startActivity(intent)

        var pm: PackageManager? = packageManager
        var appList = pm?.queryIntentActivities(intent, 0)
        if (appList?.count()!! >= 0) {
            var packageName: String? = null
            var className: String? = null
            var found: Boolean = false

            for (info in appList) {
                packageName = info.activityInfo.packageName
                if (packageName.equals("com.android.bluetooth")) {
                    className = info.activityInfo.name
                    found = true
                    break
                }
            }

            if (!found) {
                Toast.makeText(applicationContext, "Bluetooth not found on Device", Toast.LENGTH_LONG).show()
            } else {
                intent.setClassName(packageName, className)
                startActivity(intent)
            }
        }
    }
}
