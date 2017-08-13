package com.orion.yung.bluetoothshare

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by yung on 8/3/17.
 */
class MySqlHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "BluetoothShare") {
    companion object {
        private var instance: MySqlHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MySqlHelper {
            if (instance == null) {
                instance = MySqlHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.createTable("files", true, "_id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT, "filename" to TEXT, "filepath" to TEXT, "date_saved" to TEXT)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

//Access property for Context
val Context.database: MySqlHelper
    get() = MySqlHelper.getInstance(applicationContext)