package ca.veltus.mindfulbreathingwearos.domain.model

data class DatabaseUpdateEvent(
    var cacheUpdated: Boolean = false,
    var databaseUpdated: Boolean = false
)
