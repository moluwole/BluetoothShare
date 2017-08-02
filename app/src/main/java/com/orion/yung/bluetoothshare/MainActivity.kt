package com.orion.yung.bluetoothshare

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.support.v4.content.ContextCompat
import android.util.Log


class MainActivity : AppCompatActivity() {


    companion object {
        val LOG_TAG = "Bluetooth Share: "
        var AudioSavePath: String? = null
        var recorder: MediaRecorder? = null
        var mStartRecorder: Boolean = true
        var storageDir = File(Environment.getExternalStorageDirectory().absolutePath + "/BluetoothShare")
        val RequestPermissionCode = 1
    }


    fun startRecording() {
        try {
            recorder = MediaRecorder()
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
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

    fun saveVoiceText(): String{
        return "/" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".3gp"
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO), RequestPermissionCode)
    }


    fun record() {
        if (!checkPermission()) {
            requestPermission()
        }
        if (!storageDir.exists()) storageDir.mkdir()
        AudioSavePath = Environment.getExternalStorageDirectory().absolutePath + "/BluetoothShare" + saveVoiceText()
        onRecord(mStartRecorder)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            RequestPermissionCode -> if(grantResults.size > 0){
                val StoragePermission: Boolean = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val RecordPermission: Boolean = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if(StoragePermission && RecordPermission){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun checkPermission(): Boolean{
        var result: Int = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        var result1: Int = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    fun stopRecording(){
        recorder?.stop()
        recorder?.release()
        recorder = null
        Toast.makeText(applicationContext,"Recording Stopped",Toast.LENGTH_LONG).show()
        mStartRecorder = true
    }

    fun onRecord(start: Boolean){
        if(start) startRecording()
        else
            stopRecording()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            record()
            mStartRecorder = false
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
