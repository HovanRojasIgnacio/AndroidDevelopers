package com.example.androiddevelopers.ui.events

enum class EventType(val typeName: String, val apiPath: String) {
    EVENTS("Eventos", "events"),
    BIRTHS("Nacimientos", "births"),
    DEATHS("Fallecimientos", "deaths")
}