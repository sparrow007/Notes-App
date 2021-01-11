package com.jackandphantom.mytodo.task.di

import com.jackandphantom.mytodo.task.ui.TaskFragment
import dagger.Subcomponent


@Subcomponent(modules = [TaskModule::class])
interface TaskComponent {


    @Subcomponent.Factory
    interface factory {
        fun create() : TaskComponent
    }

   //Inject your fragment / Activity

    fun inject(taskFragment: TaskFragment)
}