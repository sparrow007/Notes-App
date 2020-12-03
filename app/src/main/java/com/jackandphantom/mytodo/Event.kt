package com.jackandphantom.mytodo

import androidx.lifecycle.Observer

open class Event<out T>(private val content : T) {


    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set


    fun getContentIfNotHandle() : T?{
        return if (hasBeenHandled)
               null
        else {
            hasBeenHandled = true
            content
        }
    }


    fun peekContent():T = content
}

class EvenObserver<T> (private val onEventUnhandledContent : (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {

        event?.getContentIfNotHandle()?.let {
            onEventUnhandledContent(it)
        }

    }

}