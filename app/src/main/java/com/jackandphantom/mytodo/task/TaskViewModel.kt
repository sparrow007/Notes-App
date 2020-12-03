package com.jackandphantom.mytodo.task

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.jackandphantom.mytodo.Event
import com.jackandphantom.mytodo.R
import com.jackandphantom.mytodo.data.Result
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

   private val _currentFilteringLabel = MutableLiveData<Int>()
   val currentFilteringLabel : LiveData<Int> = _currentFilteringLabel


   private val _noTaskLabel = MutableLiveData<Int>()
    val noTaskLabel : LiveData<Int> = _noTaskLabel

   private val _noTaskIconRes = MutableLiveData<Int>()
    val noTaskIconRes : LiveData<Int> = _noTaskIconRes

    private val _taskAddViewVisible = MutableLiveData<Boolean>()
    val taskAddViewVisible : LiveData<Boolean> = _taskAddViewVisible

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
        setFilteringType(TaskFilterType.ALL_TASKS)
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

    private fun setFilteringType(requestType : TaskFilterType) {
        currentFiltering = requestType

        when (requestType) {

            TaskFilterType.ALL_TASKS -> {
              setFilter(R.string.label_all, R.string.no_tasks_all,
              R.drawable.logo_no_fill, true)
            }
            TaskFilterType.ACTIVE_TASKS -> {
                setFilter(R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp, false)
            }

            TaskFilterType.COMPLETED_TASKS -> {
                setFilter(R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp, false)
            }
        }

    }

    /**
     * Provide data for the particular filter type of the task
     * @param filteringLableString provide label name
     * @param noTaskLabelString provide text when there is no task available
     * @param noTaskIconDrawable provide icon for provided label
     * @param taskAddVisible task add visible
     */

    private fun setFilter (
        @StringRes filteringLableString : Int , @StringRes noTaskLabelString : Int,
        @DrawableRes noTaskIconDrawable : Int, taskAddVisible : Boolean
    ) {
       _currentFilteringLabel.value = filteringLableString
        _noTaskLabel.value = noTaskLabelString
        _noTaskIconRes.value = noTaskIconDrawable
        _taskAddViewVisible.value = taskAddVisible
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