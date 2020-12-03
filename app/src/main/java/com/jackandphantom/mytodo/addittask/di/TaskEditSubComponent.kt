package com.jackandphantom.mytodo.addittask.di

import com.jackandphantom.mytodo.addittask.TaskEditFragment
import dagger.Subcomponent


@Subcomponent (modules = [TaskEditModule::class])
interface TaskEditSubComponent {

    @Subcomponent.Factory
    interface factory {
        fun create() : TaskEditSubComponent
    }

    fun inject(taskEditFragment: TaskEditFragment)
}