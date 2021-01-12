package com.jackandphantom.mytodo.addittask

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackandphantom.mytodo.Event
import com.jackandphantom.mytodo.data.Result
import com.jackandphantom.mytodo.data.Task
import com.jackandphantom.mytodo.data.source.TaskRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskEditViewModel @Inject constructor(
    private val repository: TaskRepository
        ) : ViewModel() {

    //Two way binding
    val title  = MutableLiveData<String>()

    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading : LiveData<Boolean> = _dataLoading

    private val _taskUpdateEvent = MutableLiveData<Event<Unit>>()
    val taskUpdateEvent : LiveData<Event<Unit>> = _taskUpdateEvent

    private var isNewTask : Boolean = false
    private var isDataLoaded = false
    private var taskCompleted = false
    private var taskId : String? = null


    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    fun start(taskId : String?) {
        if (_dataLoading.value == true) return

        this.taskId = taskId
        if (taskId == null) {
            isNewTask = true
            return
        }

        if (isDataLoaded) return

        isNewTask = false
        _dataLoading.value = true

        viewModelScope.launch {
            repository.getTask(taskId).let {
                if (it is Result.Success) {
                    onTaskLoaded(it.data)
                }else {
                    onDataNotAvailable()
                }
            }
        }

    }

    private fun onTaskLoaded(task : Task) {
        title.value = task.title
        description.value = task.description
        taskCompleted = task.isCompleted
        isDataLoaded = true
        _dataLoading.value = false
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    fun saveTask() {



        val currentTitle = title.value
        val currentDescription = description.value

      //  Log.e("MY TAG", "YES I AM SAVE TASK "+ currentDescription + currentTitle)

        if (currentDescription == null || currentTitle == null) {
            return
        }

        if (currentTitle.isEmpty() || currentDescription.isEmpty()) return

        val currentTaskId = taskId

        if (isNewTask || currentTaskId == null) {
            val task = Task(currentTitle, currentDescription)
            createTask(task)
        }else {

            val task = Task(currentTitle, currentDescription, isCompleted = taskCompleted, currentTaskId)
            updateTask(task)
        }
    }

    private fun createTask(task : Task) = viewModelScope.launch {
        repository.saveTask(task)
      //  Log.e("MY TAG", "YES I AM CALING")
        _taskUpdateEvent.value = Event(Unit)
    }

    private fun updateTask(task: Task) {

        if (isNewTask) {
            return
        }
        viewModelScope.launch {
            repository.saveTask(task)
            _taskUpdateEvent.value = Event(Unit)
        }
    }

    fun deleteTask()   {
        if (taskId == null) return

        val currentTaskId : String = this.taskId!!

        viewModelScope.launch {
            repository.deleteTask(currentTaskId)
            _taskUpdateEvent.value = Event(Unit)
        }
    }
}