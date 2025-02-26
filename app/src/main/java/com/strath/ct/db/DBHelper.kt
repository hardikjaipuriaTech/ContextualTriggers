package com.strath.ct.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DBHelper(context: Context) :SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_STEPS_TABLE_STMT)
        db.execSQL(CREATE_WORK_TABLE_STMT)
        Log.d("DB", "table created")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(STEPS_DROP_TABLE_STMT)
        db.execSQL(WORK_DROP_TABLE_STMT)
        onCreate(db)
    }

    companion object {
        const val TAG = "DBHelper"
        const val DB_VERSION = 3
        const val DB_NAME = "ContextualTriggers.db"
        private var instance: DBHelper? = null

        private const val STEPS_TABLE_NAME = "Steps"
        private const val STEPS_COLUMN_DATE = "Date"
        private const val STEPS_COLUMN_STEPS = "Steps"

        private const val WORK_TABLE_NAME = "Work"
        private const val WORK_COLUMN_EXIT = "ExitTime"


        private const val CREATE_STEPS_TABLE_STMT = ("CREATE TABLE " + STEPS_TABLE_NAME + "( "
                + STEPS_COLUMN_DATE + " INTEGER PRIMARY KEY, "
                + STEPS_COLUMN_STEPS + " INTEGER DEFAULT 0"
                + ");")
        private const val CREATE_WORK_TABLE_STMT = ("CREATE TABLE " + WORK_TABLE_NAME + " ( "
                + WORK_COLUMN_EXIT + " INTEGER "
                + ");")
        private const val STEPS_DROP_TABLE_STMT = "DROP TABLE IF EXISTS $STEPS_TABLE_NAME"
        private const val WORK_DROP_TABLE_STMT = "DROP TABLE IF EXISTS $WORK_TABLE_NAME"
        fun init(context: Context) {
            if (instance == null) instance = DBHelper(context)
            Log.d("DB", "initialised")
        }

        fun addSteps(date: Long, steps: Int) {
            println("Adding $steps to $date")
            val db = instance!!.writableDatabase
            val values = ContentValues()
            values.put(STEPS_COLUMN_DATE, date)
            values.put(STEPS_COLUMN_STEPS, steps)
            db.replace(STEPS_TABLE_NAME, null, values)
        }


        @SuppressLint("Range")
        fun getSteps(date: Long): Int {
            val db = instance!!.readableDatabase
            val projection = arrayOf(
                STEPS_COLUMN_STEPS
            )
            val selection = "$STEPS_COLUMN_DATE LIKE ?"
            val args = arrayOf(date.toString())
            val c = db.query(STEPS_TABLE_NAME, projection, selection, args, null, null, null)
            var steps = 0
            if (c != null && c.count > 0) {
                c.moveToFirst()
                steps = c.getInt(c.getColumnIndex(STEPS_COLUMN_STEPS))
                c.close()
            }
            return steps
        }

        fun addWorkExit(date: Long) {
            val values = ContentValues()
            values.put(WORK_COLUMN_EXIT, date)
            instance!!.writableDatabase.insert(WORK_TABLE_NAME, null, values)
        }

        val avgWorkExitTime: Long
            get() {
                val query = "SELECT AVG($WORK_COLUMN_EXIT) FROM $WORK_TABLE_NAME"
                val cursor = instance!!.writableDatabase.rawQuery(query, arrayOf())
                return if (cursor.moveToFirst()) cursor.getLong(0) else Int.MAX_VALUE.toLong()
            }
    }
}