package com.orion.yung.bluetoothshare

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    companion object {
        val LOG_TAG = "Bluetooth Share: "
        var AudioSavePath: String? = null
        var AudioFileName: String? = null
        var recorder: MediaRecorder? = null
        var mStartRecorder: Boolean = true
        var storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/BluetoothShare")
        val RequestPermissionCode = 1
        val date: String = SimpleDateFormat("EEE MMM dd, yyyy").format(Date())
    }


    fun startRecording() {
        try {
            // Snackbar.make(recycler_view, AudioSavePath.toString(), Snackbar.LENGTH_INDEFINITE).show()
            recorder = MediaRecorder()
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            recorder?.setOutputFile(AudioSavePath)


            recorder?.prepare()
            recorder?.start()
            Toast.makeText(applicationContext, "Recording Started", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, e.toString())
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun saveVoiceText(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".mp3"
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO), RequestPermissionCode)
    }


    fun record() {
        if (!checkPermission()) {
            requestPermission()
            if (!storageDir.exists()) storageDir.mkdirs()
            AudioFileName = saveVoiceText()
            AudioSavePath = storageDir.toString() + "/" + AudioFileName
            onRecord(mStartRecorder)
        } else {
            if (!storageDir.exists()) storageDir.mkdirs()
            AudioFileName = saveVoiceText()
            AudioSavePath = storageDir.toString() + "/" + AudioFileName
            onRecord(mStartRecorder)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size > 0) {
                val StoragePermission: Boolean = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val RecordPermission: Boolean = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (StoragePermission && RecordPermission) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun checkPermission(): Boolean {
        var result: Int = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        var result1: Int = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    fun stopRecording() {
        recorder?.stop()
        recorder?.release()
        recorder = null
        Toast.makeText(applicationContext, "Recording Stopped", Toast.LENGTH_LONG).show()
        database.use {
            insert("files", "filename" to AudioFileName, "filepath" to AudioSavePath, "date_saved" to date)
        }
        ReloadUI()
    }


    fun ReloadUI() {
        val myRecyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        myRecyclerView.adapter = null
        myRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        myRecyclerView.layoutManager = mLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
                myRecyclerView.context,
                mLayoutManager.orientation)
        myRecyclerView.addItemDecoration(dividerItemDecoration)

        // Specify an adapter

        var mList: List<Files> = fileData()

        val mAdapter = DataAdapter(mList)
        myRecyclerView.adapter = mAdapter
    }

    fun fileData(): List<Files> = database.use {
        var filesParser = classParser<Files>()
        select("files").column("_id").column("filename").column("filepath").column("date_saved").parseList(filesParser).toList()
    }


    fun onRecord(start: Boolean) {
        when (start) {
            true -> {
                fab.setImageResource(R.drawable.ic_record_voice_over_24dp)
                startRecording()
                mStartRecorder = false
            }
            false -> {
                fab.setImageResource(R.drawable.ic_mic_black_24dp)
                stopRecording()
                mStartRecorder = true
            }
        }

        //  Toast.makeText(applicationContext, mStartRecorder.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        ReloadUI()
        fab.setOnClickListener { _ ->
            record()
        }
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
        // R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
