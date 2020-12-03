package com.jackandphantom.mytodo.task.di

import androidx.lifecycle.ViewModel
import com.jackandphantom.mytodo.di.ViewModelKey
import com.jackandphantom.mytodo.task.TaskViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TaskModule {

    @Binds
    @IntoMap
    @ViewModelKey(TaskViewModel::class)
    abstract fun provideViewModel(taskViewModel: TaskViewModel) : ViewModel

}