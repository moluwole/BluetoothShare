package com.orion.yung.bluetoothshare

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.find
import java.io.File


/**
 * Created by yung on 8/3/17.
 */

data class Files(var _id: Int, var filename: String, var filepath: String, var date_saved: String)

class DataAdapter constructor(mList: List<Files>?) : RecyclerView.Adapter<DataAdapter.MyViewHolder>() {

    var mList: List<Files>? = null
    var appContext: Context? = null
    private val DISCOVER_DURATION = 300
    private val REQUEST_BLU = 1

    init {
        this.mList = mList
    }


    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        var record_file: Files? = mList?.get(position)
        holder?.date_saved?.text = record_file?.date_saved
        holder?.file_name?.text = record_file?.filename?.removePrefix("/")


//        holder?.container?.setOnClickListener {
//            //var mp = MediaPlayer.create(appContext, Uri.fromFile(File(record_file?.filepath)))
//            //mp.start()
//            var storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/BluetoothShare")
//            var directory_files = storageDir.listFiles()
//            if (directory_files != null) {
//
//                Log.e("positions", mList?.count().toString() + "||" + directory_files.size.toString())
//
//                Log.e("names", directory_files[position].name + "||" + record_file?.filename)
//
//                /*var intent = Intent(Intent.ACTION_VIEW)
//                intent.setDataAndType(Uri.parse(directory_files[position].absolutePath), "video*//*")
//                appContext?.startActivity(intent)*/
//                TODO(reason = "Create an intent to play the audio or create another Activity to handle that")
//
//              /*  var mp = MediaPlayer()
//                mp.setDataSource(appContext, Uri.fromFile(File(directory_files[position].absolutePath)))
//                mp.prepare()
//                mp.start()*/
//            }
//        }
        var file_path: String? = null

        var storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/BluetoothShare")
        var directory_files = storageDir.listFiles()
        if (directory_files != null) {
            file_path = directory_files[position].absolutePath
        }

        holder?.container?.setOnLongClickListener {
            var intent = Intent(appContext as MainActivity, Dialog::class.java)
            intent.putExtra("filename", record_file?.filename)
            intent.putExtra("filepath", file_path)
            intent.putExtra("id", record_file?._id)

            appContext?.startActivity(intent)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_list, parent, false)
        val vh = MyViewHolder(view)
        appContext = view.context
        return vh
    }

    override fun getItemCount(): Int {
        return mList?.count() ?: 0
    }


    class MyViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var file_name = layoutView.find<TextView>(R.id.item_name)
        var date_saved = layoutView.find<TextView>(R.id.item_date)

        var container = layoutView.find<LinearLayout>(R.id.list_container)
    }
}