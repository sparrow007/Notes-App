package com.jackandphantom.mytodo.data.source.remote

import com.jackandphantom.mytodo.data.Result
import com.jackandphantom.mytodo.data.Task
import com.jackandphantom.mytodo.data.source.DataSource
import kotlinx.coroutines.delay
import java.lang.Exception

/**
 * Remote data source for the application, currently this app is not connected to any remote source so this would
 * work as the dummy data source just realize that if we have network source how will handle those cases.
 */
object TaskRemoteDataSource : DataSource {

    private val SERVICE_LATENCY_IN_MILLS = 1000L

    private var TASK_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Super Hero", "There has been so many abidance for the host and produces different creatures")
        addTask("Indian Programmer", "There are so many great coder all over india, they will now create so many different possibilities")
    }

    override suspend fun getTasks(): Result<List<Task>> {
       val taskList = TASK_SERVICE_DATA.values.toList()
       delay(SERVICE_LATENCY_IN_MILLS)
       return Result.Success(taskList)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        delay(SERVICE_LATENCY_IN_MILLS)
        val task = TASK_SERVICE_DATA[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Task not found"))
    }

    override suspend fun saveTask(task: Task) {
         TASK_SERVICE_DATA[task.entry_id] = task
    }

    override suspend fun completeTask(task: Task) {
        val completeTask = Task(task.title, task.description, true, task.entry_id)
        TASK_SERVICE_DATA[task.entry_id] = completeTask
    }

    override suspend fun completeTask(taskId: String) {
        //Not required
    }

    override suspend fun activateTask(taskId: String) {

    }

    override suspend fun activateTask(task: Task) {
        val activateTask = Task(task.title, task.description, true, task.entry_id)
        TASK_SERVICE_DATA[task.entry_id] = activateTask
    }

    override suspend fun clearCompleteTask() {
       TASK_SERVICE_DATA = TASK_SERVICE_DATA.filterValues {
           !it.isCompleted
       } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTask() {
        TASK_SERVICE_DATA.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        TASK_SERVICE_DATA.remove(taskId)
    }

    private fun addTask(title : String, description : String) {
        val task = Task(title, description)
        TASK_SERVICE_DATA[task.entry_id] = task
    }

}