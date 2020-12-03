package com.jackandphantom.mytodo.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jackandphantom.mytodo.data.Task


@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase(){

 abstract fun taskDao() : TaskDao

}