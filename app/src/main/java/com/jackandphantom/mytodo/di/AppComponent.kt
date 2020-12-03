package com.jackandphantom.mytodo.di

import android.content.Context
import com.jackandphantom.mytodo.addittask.di.TaskEditSubComponent
import com.jackandphantom.mytodo.task.di.TaskComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component (
    modules = [
        subCompModule::class,
        AppModule::class,
        AppModuleBinds::class,
        ViewModelFactoryModule::class]
)
interface AppComponent {

    @Component.Factory
    interface factory {
        fun create(@BindsInstance applicationContext : Context) : AppComponent
    }

    fun taskComponent() : TaskComponent.factory
    fun taskEditComponent() : TaskEditSubComponent.factory

}


@Module (
    subcomponents = [TaskComponent::class, TaskEditSubComponent::class]
)
object subCompModule