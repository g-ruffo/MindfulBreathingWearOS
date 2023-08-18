package ca.veltus.mindfulbreathingwearos.data.hardware.dto

import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DatabaseStatsDTO(
val count: Int,
val lastAddedTimestamp: Long?
)

fun DatabaseStatsDTO.toDatabaseStats(): DatabaseStats {
    val dateFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
    val lastAddedFormatted = this.lastAddedTimestamp?.let {
        dateFormat.format(Date(it))
    }
    return DatabaseStats(
        count = this.count,
        lastAddedDate = lastAddedFormatted
    )
}