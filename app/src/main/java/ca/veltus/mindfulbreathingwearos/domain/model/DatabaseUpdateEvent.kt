package ca.veltus.mindfulbreathingwearos.domain.model

data class DatabaseUpdateEvent(
    var uncachedUpdated: Boolean = false,
    var cacheUpdated: Boolean = false,
    var databaseUpdated: Boolean = false
)
