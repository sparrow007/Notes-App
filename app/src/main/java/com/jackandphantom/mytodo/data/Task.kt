package com.jackandphantom.mytodo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "tasks")
data class Task @JvmOverloads constructor(
    @ColumnInfo(name = "title") var title : String = " ",
    @ColumnInfo(name = "description") var description : String = "",
    @ColumnInfo(name = "completed") var isCompleted : Boolean = false,
    @PrimaryKey @ColumnInfo(name = "entry_id") var entry_id : String = UUID.randomUUID().toString())