package com.jackandphantom.mytodo.di

import android.content.Context
import androidx.room.Room
import com.jackandphantom.mytodo.data.source.DataSource
import com.jackandphantom.mytodo.data.source.local.TaskLocalDataSource
import com.jackandphantom.mytodo.data.source.local.ToDoDatabase
import com.jackandphantom.mytodo.data.source.remote.TaskRemoteDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
object AppModule {

   @Qualifier
   @Retention(AnnotationRetention.RUNTIME)
   annotation class TasksRemoteDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TasksLocalDataSource


    @JvmStatic
    @TasksRemoteDataSource
    @Singleton
    @Provides
    fun provideTaskRemoteDataSource() : DataSource {
       return TaskRemoteDataSource
    }


    /**
     * In order to create the local data source you need to provide the
     * database object as well as dispatcher so for that we have given methods for
     * both requirements
     */
    @JvmStatic
    @TasksLocalDataSource
    @Singleton
    @Provides
    fun provideTaskLocalDataSource(database : ToDoDatabase, ioDispatcher : CoroutineDispatcher) : DataSource {
        return TaskLocalDataSource(database.taskDao(), ioDispatcher)
    }

    /**
     * Create the database object in the provides
     * so that it will help to create local data source
     * Room database
     */

    @JvmStatic
    @Singleton
    @Provides
    fun provideRoomDatabase(context : Context) : ToDoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java,
            "ToDo Database").build()

    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDispatcher() = Dispatchers.IO

}