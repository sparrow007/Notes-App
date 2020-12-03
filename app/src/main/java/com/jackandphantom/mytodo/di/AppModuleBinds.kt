package com.jackandphantom.mytodo.di

import com.jackandphantom.mytodo.data.source.DefaultTaskRepository
import com.jackandphantom.mytodo.data.source.TaskRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class AppModuleBinds {

    @Singleton
    @Binds
    abstract fun getRepository(taskRepository: DefaultTaskRepository) : TaskRepository
}