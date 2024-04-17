package com.forgblord.todo_prototype.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "project_id")
    val project_id: Int = 0,
    val title: String,
    val colorCode: Int,
)
