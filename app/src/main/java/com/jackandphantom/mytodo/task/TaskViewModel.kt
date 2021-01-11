package com.jackandphantom.mytodo.task

import androidx.lifecycle.*
import com.jackandphantom.mytodo.Event
import com.jackandphantom.mytodo.R
import com.jackandphantom.mytodo.data.Result.Success
import com.jackandphantom.mytodo.data.Task
import com.jackandphantom.mytodo.data.source.TaskRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

   private val _items = MutableLiveData<List<Task>>().apply { value = emptyList() }
    val items : LiveData<List<Task>> = _items

   private var currentFiltering = TaskFilterType.ACTIVE_TASKS

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading = _dataLoading

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent : LiveData<Event<String>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent : LiveData<Event<Unit>> = _newTaskEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText : LiveData<Event<Int>> = _snackbarText

    val empty : LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        load(true)
    }


    fun load(forceUpdate : Boolean) {
        _dataLoading.value = true

        viewModelScope.launch {
           val taskResult = taskRepository.getTasks(forceUpdate)

            if (taskResult is Success) {

               val tasks = taskResult.data
                val taskToShow = ArrayList<Task>()

                for (task in tasks) {

                    when (currentFiltering) {
                        TaskFilterType.ALL_TASKS -> taskToShow.add(task)

                        TaskFilterType.ACTIVE_TASKS -> if(!task.isCompleted) taskToShow.add(task)

                        TaskFilterType.COMPLETED_TASKS -> if (task.isCompleted) taskToShow.add(task)
                    }
                }

                _dataLoading.value = false
                _items.value = ArrayList(taskToShow)

            }else {
                _dataLoading.value = false
                _items.value = emptyList()

            }

        }
    }

    fun clearCompleteTask() {
        viewModelScope.launch {
            taskRepository.clearCompletedTasks()
            showSnackbarMessage(R.string.completed_tasks_cleared)
            load(false)
        }
    }

    fun completeTask(task : Task, completed : Boolean) = viewModelScope.launch {
        if (completed) {
            taskRepository.completeTask(task);
            showSnackbarMessage(R.string.task_marked_complete)
        }else {
            taskRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }
    fun refresh() {
        load(true)
    }

    fun openTask(taskId : String) {
        _openTaskEvent.value = Event(taskId)
    }

    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

    fun showSnackbarMessage(message : Int) {
        _snackbarText.value = Event(message)
    }

}