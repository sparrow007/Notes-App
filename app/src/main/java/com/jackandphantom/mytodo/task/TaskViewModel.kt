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

    /**
     * UI layer should interact with the live data because live data can not set the value
     * so it would work like abstraction for UI layer, only observe the value don't change it.
      */
   private val _items = MutableLiveData<List<Task>>().apply { value = emptyList() }
    val items : LiveData<List<Task>> = _items

   private var currentFiltering = TaskFilterType.ACTIVE_TASKS

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading = _dataLoading

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent : LiveData<Event<String>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent : LiveData<Event<Unit>> = _newTaskEvent

    val empty : LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    /**
     * When ever the viewModel is create in fragment or Activity init will always will be run first
     * so always try to run those codes which are going fetch data from local or remote source.
     */
    init {
        load(true)
    }

    /**
     * load the data from either local or remote source
     * @param forceUpdate check force in the remote source
     */
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
            load(false)
        }
    }

    fun completeTask(task : Task, completed : Boolean) = viewModelScope.launch {
        if (completed) {
            taskRepository.completeTask(task);
        }else {
            taskRepository.activateTask(task)
        }
    }
    fun refresh() {
        load(true)
    }

    fun openTask(taskId : String) {
        _openTaskEvent.value = Event(taskId)
    }

    fun addNewTask() {
        //database values +
        _newTaskEvent.value = Event(Unit)
    }

}