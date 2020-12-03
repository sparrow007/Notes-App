package com.jackandphantom.mytodo.data.source

import com.jackandphantom.mytodo.data.Result
import com.jackandphantom.mytodo.data.Task

interface DataSource {

   suspend fun getTasks() : Result<List<Task>>

   suspend fun getTask(taskId : String) : Result<Task>

   suspend fun saveTask(task : Task)

   suspend fun completeTask(task : Task)

   suspend fun completeTask(taskId: String)

   suspend fun activateTask(taskId: String)

   suspend fun activateTask(task : Task)

   suspend fun clearCompleteTask()

   suspend fun deleteAllTask()

   suspend fun deleteTask(taskId : String)


}