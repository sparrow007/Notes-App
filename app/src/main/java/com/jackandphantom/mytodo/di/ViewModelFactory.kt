package com.jackandphantom.mytodo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.MapKey
import dagger.Module
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass


class ToDoViewModelFactory @Inject constructor(
    private val creators :  @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
): ViewModelProvider.Factory{



    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

      var creator: Provider<out ViewModel>? = creators.get(modelClass)

        if(creator == null) {

            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                }
            }
        }

        if (creator == null) {
            throw IllegalStateException("Unknown model class $modelClass")
        }

        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        }catch (e : Exception) {
            throw RuntimeException(e)
        }

    }


}

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun getViewModelFactory(viewModelFactory : ToDoViewModelFactory) : ViewModelProvider.Factory

}

@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey (val value : KClass<out ViewModel>)