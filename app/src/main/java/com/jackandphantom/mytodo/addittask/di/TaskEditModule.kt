package com.jackandphantom.mytodo.addittask.di

import androidx.lifecycle.ViewModel
import com.jackandphantom.mytodo.addittask.TaskEditViewModel
import com.jackandphantom.mytodo.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class TaskEditModule {

    @IntoMap
    @ViewModelKey(TaskEditViewModel::class)
    @Binds
    abstract fun bindViewModel(taskEditViewModel: TaskEditViewModel) : ViewModel
}