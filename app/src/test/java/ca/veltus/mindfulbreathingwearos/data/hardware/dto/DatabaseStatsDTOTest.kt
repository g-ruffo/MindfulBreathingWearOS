package ca.veltus.mindfulbreathingwearos.data.hardware.dto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DatabaseStatsDTOTest {

    @Test
    fun `test DatabaseStatsDTO to DatabaseStats conversion`() {
        val currentTimeMillis = System.currentTimeMillis()
        val expectedDateFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val expectedDate = expectedDateFormat.format(Date(currentTimeMillis))

        val dto = DatabaseStatsDTO(count = 10, lastAddedTimestamp = currentTimeMillis)

        val result = dto.toDatabaseStats()

        assertEquals(10, result.count)
        assertEquals(expectedDate, result.lastAddedDate)
    }

    @Test
    fun `test DatabaseStatsDTO to DatabaseStats conversion with null timestamp`() {
        val dto = DatabaseStatsDTO(count = 10, lastAddedTimestamp = null)

        val result = dto.toDatabaseStats()

        assertEquals(10, result.count)
        assertNull(result.lastAddedDate)
    }
}