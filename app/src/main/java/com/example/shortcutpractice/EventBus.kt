package com.example.shortcutpractice

object EventBus {
    private val listeners = mutableMapOf<Class<out Events>, MutableSet<(Events) -> Unit>>()

    fun <T : Events> subscribe(eventType: Class<T>, listener: (T) -> Unit) {
        val subscriberListeners = listeners.getOrPut(eventType) { mutableSetOf() }
        subscriberListeners.add(listener as (Events) -> Unit)
    }

    fun <T : Events> unsubscribe(eventType: Class<T>, listener: (T) -> Unit) {
        listeners[eventType]?.remove(listener as (Events) -> Unit)
    }

    fun publish(event: Events) {
        listeners[event::class.java]?.forEach { it(event) }
    }
}

sealed class Events {
    data class KeyPress(val message: String) : Events()
    data class ShortcutCompleted(val message: String) : Events()
}

