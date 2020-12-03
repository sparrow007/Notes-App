package com.jackandphantom.mytodo.task


/**
 * This class will be shown at the list of the filter in the main screen
 * and it's also going to be helpfull in the viewModel so that
 * we can easily set the filtering type in the viewModel
 *
 * There will be three type for the filtering
 */

enum class TaskFilterType {

    /**
     * When there is no filter is apply to the tasks
     */
    ALL_TASKS,

    /**
     * Filter the tasks based on the property isComplete of the task
     * show all the active tasks
     */
    ACTIVE_TASKS,

    /**
     * Filter the tasks whose are completed so it's also based on the attribute isComplete
     * Show all completed tasks
     */

    COMPLETED_TASKS

}