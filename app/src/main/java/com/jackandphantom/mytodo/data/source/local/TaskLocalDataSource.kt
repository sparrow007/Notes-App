package com.jackandphantom.mytodo.data.source.local

import com.jackandphantom.mytodo.data.Result
import com.jackandphantom.mytodo.data.Task
import com.jackandphantom.mytodo.data.source.DataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * Local data source for the task
 * @param taskDao data access object for the task database
 * @param ioDispatcher running the coroutine on the IO thread which uses the thread pool for the disk and networks calls
 */

class TaskLocalDataSource internal constructor(
   private val taskDao : TaskDao,
   private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO
) : DataSource {


    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(taskDao.getTasks())
        }catch (e : Exception) {
            Result.Error (e)
        }
    }

    /**
     * @param taskId id of the task.
     *
     * first get the task from DB then check if task is exist or not the return according that information
     */

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {

        try {
            val task = taskDao.getTask(taskId)
            if(task != null) {
                return@withContext Result.Success(task)
            }else {
                return@withContext Result.Error(Exception("Task not found"))
            }
        }catch (e : Exception) {
            return@withContext Result.Error(e)
        }

    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        taskDao.saveTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        taskDao.updateCompleted(task.entry_id, true)
    }

    override suspend fun completeTask(taskId: String) = withContext(ioDispatcher) {
        taskDao.updateCompleted(taskId , true)
    }


    override suspend fun activateTask(taskId: String) = withContext(ioDispatcher) {
        taskDao.updateCompleted(taskId , false)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        taskDao.updateCompleted(task.entry_id , false)
    }

    override suspend fun clearCompleteTask() = withContext<Unit>(ioDispatcher) {
        taskDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTask() = withContext(ioDispatcher) {
        taskDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) = withContext<Unit>(ioDispatcher) {
        taskDao.deleteTaskById(taskId)
    }
}