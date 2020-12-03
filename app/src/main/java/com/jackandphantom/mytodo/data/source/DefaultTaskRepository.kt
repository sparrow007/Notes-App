package com.jackandphantom.mytodo.data.source

import android.util.Log
import com.jackandphantom.mytodo.data.Result
import com.jackandphantom.mytodo.data.Task
import com.jackandphantom.mytodo.data.source.local.TaskLocalDataSource
import com.jackandphantom.mytodo.data.source.remote.TaskRemoteDataSource
import com.jackandphantom.mytodo.di.AppModule.TasksLocalDataSource
import com.jackandphantom.mytodo.di.AppModule.TasksRemoteDataSource
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

class DefaultTaskRepository @Inject constructor(
    @TasksLocalDataSource private val taskLocalDataSource: DataSource,
    @TasksRemoteDataSource private val taskRemoteDataSource: DataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskRepository {

    //Creating a cache for storing the task with value and pair
    private var cacheTask : ConcurrentMap<String, Task>? = null

    override suspend fun getTasks(forceUpdate : Boolean): Result<List<Task>> = withContext(ioDispatcher) {

        if (!forceUpdate) {
            cacheTask?.let { cacheTasks ->
                return@withContext Result.Success(cacheTasks.values.sortedBy { it.entry_id })
            }
        }

       val newTasks = fetchDataFromRemoteOrLocal(forceUpdate)

        (newTasks as? Result.Success)?. let { refreshCache(it.data) }

       cacheTask?.values?.let { tasks ->
           return@withContext Result.Success(tasks.sortedBy { it.entry_id })
       }

        (newTasks as Result.Success)?.let {
             if(it.data.isEmpty())
                 return@withContext Result.Success(it.data)
        }

        return@withContext Result.Error(Exception("Illegal state"))
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> = withContext(ioDispatcher) {

        //Returns the data if it's available in the cache
       if (!forceUpdate) {
           getTaskWithId(taskId)?.let {
               return@withContext Result.Success(it)
           }

       }

        val newTask = fetchDataFromRemoteOrLocal(taskId, forceUpdate)

        //Refresh
        (newTask as Result.Success).let { cacheTask(newTask.data)}

        return@withContext newTask
    }



    private suspend fun fetchDataFromRemoteOrLocal(taskId: String, forceUpdate: Boolean) : Result<Task> {

        val task = taskRemoteDataSource.getTask(taskId)

        when(task) {

            is Result.Error -> Log.e("TAG", "Remote data source is faild")

            is Result.Success -> {
                refreshLocalDataSource(task.data)
                return task

            }

            else -> throw IllegalStateException()

        }

        //Don't read from local if it's forces
        if(forceUpdate) {
           return Result.Error(Exception("Refresh faild"))
        }

        val localTasks = taskLocalDataSource.getTask(taskId)
        if (localTasks is Result.Success) return localTasks

        return Result.Error(Exception("Error fetching from remote and local "))

    }


    override suspend fun saveTask(task: Task) {

        cacheAndPerform(task) {
            coroutineScope {
                this.launch { taskLocalDataSource.saveTask(it) }
                this.launch { taskRemoteDataSource.saveTask(it) }
            }
        }
    }

    override suspend fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            coroutineScope {
                launch { taskLocalDataSource.completeTask(it) }
                launch { taskRemoteDataSource.completeTask(it) }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            getTaskWithId(taskId)?.let {
                completeTask(it)
            }
        }
    }

    override suspend fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = false
            coroutineScope {
                launch { taskLocalDataSource }
                launch { taskRemoteDataSource }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            getTaskWithId(taskId)?.let {
                activateTask(it)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { taskRemoteDataSource.clearCompleteTask() }
            launch { taskLocalDataSource.clearCompleteTask() }

            withContext(ioDispatcher) {
               cacheTask?.values?.removeAll {it.isCompleted}

            }
        }
    }

    override suspend fun deleteAllTasks() {
        coroutineScope {
            launch { taskLocalDataSource.deleteAllTask() }
            launch { taskRemoteDataSource.deleteAllTask() }

            cacheTask?.clear()
        }
    }

    override suspend fun deleteTask(taskId: String) {

        coroutineScope {
            launch { taskLocalDataSource.deleteTask(taskId) }
            launch { taskRemoteDataSource.deleteTask(taskId) }
        }

        cacheTask?.remove(taskId)

    }

    //Get the task with id
    private fun getTaskWithId(taskId: String) = cacheTask?.get(taskId)

    //function which will fetch the data either from local and remote

    private suspend fun fetchDataFromRemoteOrLocal(forceUpdate: Boolean) : Result<List<Task>> {
       // Remote first
        //Call the getRemoteFirst if you find your data from the method then return

        //local if remote fails
        val localTasks = taskLocalDataSource.getTasks()
        if (localTasks is Result.Success) return localTasks
        else return Result.Error(Exception("Error fetching from remote and local"))

    }

    //Perform the remote search first
    private suspend fun getRemoteFirst(forceUpdate: Boolean) : Result<List<Task>>{
        val remote = taskRemoteDataSource.getTasks()
        when (remote) {

            is Result.Error -> Log.e("MY TAG", "THERE IS NO DATA FOUND ON SERVER")

            is Result.Success -> {
                refreshLocalDataSource(remote.data)
                return  remote

            }
            else -> throw IllegalStateException()
        }

        if(forceUpdate) {
            return  Result.Error(Exception("can't force refresh  :  remote data source is unavailable"))
        }
        return Result.Error(Exception("Currently don't have any remote data"))
    }

    //Refresh single task in the database
    private suspend fun refreshLocalDataSource(task : Task) {
        taskLocalDataSource.saveTask(task)
    }

    //Refresh the local data source
    private suspend fun refreshLocalDataSource(tasks : List<Task>) {
        taskLocalDataSource.deleteAllTask()

        for ( data in tasks) {
            taskLocalDataSource.saveTask(data)
        }
    }



    private fun refreshCache(tasks : List<Task>) {
         //First clear all the cache data
        cacheTask?.clear()

        //Iterate through all the task and store them in cache
        for (data in tasks)  {
          cacheAndPerform(data) {}
        }

    }

    /**
     * Task of this function is to store the task in the cache then return the task
     * that's why new object is created for the task and assign all the values of task in it
     * @param task going to store in the cache
     */

    private fun cacheTask(task : Task) : Task {
        //Create new task object for storing in the cache
        val cachedTask = Task(task.title, task.description, task.isCompleted, task.entry_id)

       //Create cache if it's not exist
        if(cacheTask == null) {
            cacheTask = ConcurrentHashMap()
        }

        cacheTask?.put(task.entry_id, cachedTask)

       return task
    }

    private inline fun cacheAndPerform(task : Task, perform : (Task) -> Unit) {
       val cache = cacheTask(task)
        perform(cache)
    }
}