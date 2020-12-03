package com.jackandphantom.mytodo.data.source

import com.jackandphantom.mytodo.data.Task
import com.jackandphantom.mytodo.data.Result

/*
* Interface for the data layer
* */

interface TaskRepository {

    suspend fun getTasks(forceUpdate : Boolean = false): Result<List<Task>>

    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)

}