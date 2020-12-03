package com.jackandphantom.mytodo.data.source.local

import androidx.room.*
import com.jackandphantom.mytodo.data.Task

/**
 * Data access object for the task table
 *
 * Providing different queries to the database which includes insert, update, update and delete
 * */

@Dao
interface TaskDao {

    /*
    * select all the tasks from the task table
    *
    *@return all tasks.
    *
    * */

    @Query("Select * from Tasks")
    suspend fun getTasks() : List<Task>

    /**
     *  Select the task by id. (requested task can not be found)
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */

    @Query("Select * from Tasks where entry_id= :taskId")
    suspend fun getTask(taskId : String) : Task?

    /**
     * Insert a task in the databse. if the task is already existed then replace it.
     *
     * @param task task to be inserted.
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTask(task: Task)

    /**
     *  Update the task.
     *
     * @param task the task to be updated.
     * @return  the number of task updated. This should be always be 1
     */

    @Update
    suspend fun update(task : Task) : Int

    /**
     * Update the complete status of the task
     *
     * @param taskId id of the task
     * @param isCompleted status to be updated
     */

   @Query("UPDATE Tasks SET completed = :isCompleted WHERE entry_id = :taskId")
   suspend fun updateCompleted(taskId : String, isCompleted : Boolean)

    /**
     * Delete the task by id.
     *
     *@return the number of the task deleted. This should always be 1.
     */

   @Query("DELETE FROM Tasks WHERE entry_id = :taskId")
   suspend fun deleteTaskById(taskId: String) : Int

    /**
     * Delete all the tasks
     */

    @Query("DELETE FROM tasks")
    suspend fun deleteTasks()

    /**
     * Delete all the completed task from the table
     *
     * @return the number of the task deleted.
     */

    @Query("DELETE FROM tasks WHERE completed = 1")
   suspend fun deleteCompletedTasks() : Int

}