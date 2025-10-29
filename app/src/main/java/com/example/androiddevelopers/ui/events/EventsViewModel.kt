package com.example.androiddevelopers.ui.events

import androidx.lifecycle.ViewModel
import com.example.androiddevelopers.R

class EventsViewModel : ViewModel() {

    val events = listOf(
        HistoricEvent(
            id = 1,
            title = "Caída de Babilonia",
            date = "539 a. C.",
            shortDescription = "Ciro el Grande conquista Babilonia",
            detailedDescription = "En el 29 de octubre de 539 a. C., Ciro el Grande tomó Babilonia, poniendo fin al Imperio neobabilónico y permitiendo el retorno de los judíos exiliados."
        ),
        HistoricEvent(
            id = 2,
            title = "Batalla del Puente Milvio – retorno de Constantino",
            date = "312 d. C.",
            shortDescription = "Constantino regresa a Roma tras vencer en el Puente Milvio",
            detailedDescription = "El 29 de octubre de 312, el emperador Constantino el Grande regresó a Roma después de su victoria en la Batalla del Puente Milvio, acontecimiento clave en su ascenso y posterior conversión al cristianismo."
        ),
        HistoricEvent(
            id = 3,
            title = "Estreno de la ópera Don Giovanni",
            date = "1787",
            shortDescription = "Mozart presenta Don Giovanni en Praga",
            detailedDescription = "El 29 de octubre de 1787 se estrenó la ópera *Don Giovanni*, compuesta por Wolfgang Amadeus Mozart, en el Teatro Estatal de Praga."
        ),
        HistoricEvent(
            id = 4,
            title = "Toma de funciones Mussolini en Italia",
            date = "1922",
            shortDescription = "Benito Mussolini forma gobierno en Italia",
            detailedDescription = "El 29 de octubre de 1922, el rey Víctor Manuel III encargó a Benito Mussolini la formación de gobierno, lo que marcó el inicio del régimen fascista en Italia."
        ),
        HistoricEvent(
            id = 5,
            title = "Martes Negro — desplome bursátil de 1929",
            date = "1929",
            shortDescription = "Gran caída del mercado de valores en EE. UU.",
            detailedDescription = "El 29 de octubre de 1929 es conocido como “Black Tuesday” (martes negro), cuando la Bolsa de Nueva York sufrió una caída catastrófica, iniciando la Gran Depresión."
        ),
        HistoricEvent(
            id = 6,
            title = "Reconversión de Turquía en república",
            date = "1923",
            shortDescription = "Se formaliza la República de Turquía",
            detailedDescription = "El 29 de octubre de 1923, Turquía se proclama oficialmente como república tras la disolución del Imperio otomano, con Mustafa Kemal Atatürk como presidente."
        ),
        HistoricEvent(
            id = 7,
            title = "Fundación de la base Marambio",
            date = "1969",
            shortDescription = "Argentina establece base antártica",
            detailedDescription = "El 29 de octubre de 1969, el gobierno argentino fundó la base Marambio en la Antártida. "
        ),
        HistoricEvent(
            id = 8,
            title = "Primer mensaje enviado por ARPANET",
            date = "1969",
            shortDescription = "Antecesor de Internet envía primer mensaje",
            detailedDescription = "En la misma fecha, 29 de octubre de 1969, se envió el primer mensaje a través de ARPANET, precursora de Internet. "
        ),
        HistoricEvent(
            id = 9,
            title = "Secuestro del vuelo Lufthansa 615",
            date = "1972",
            shortDescription = "Secuestro terrorista del avión Lufthansa",
            detailedDescription = "El 29 de octubre de 1972, dos palestinos secuestraron el vuelo Lufthansa 615 y exigieron la liberación de presos del grupo Septiembre Negro. "
        ),
        HistoricEvent(
            id = 10,
            title = "Bombardeo a los aeródromos japoneses por EE. UU.",
            date = "1944",
            shortDescription = "Operación aérea contra Japón en la Segunda Guerra Mundial",
            detailedDescription = "El 29 de octubre de 1944, aviones del Task Group 38.2 de la Marina de EE. UU. atacaron aeródromos japoneses alrededor de Manila, reclamando numerosos objetivos destruidos. "
        )
    )


    fun getEventById(id: Int): HistoricEvent? {
        return events.find { it.id == id }
    }

}