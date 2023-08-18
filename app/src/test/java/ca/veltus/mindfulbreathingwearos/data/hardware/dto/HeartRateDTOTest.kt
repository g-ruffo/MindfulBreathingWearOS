package ca.veltus.mindfulbreathingwearos.data.hardware.dto

import android.os.SystemClock
import androidx.health.services.client.data.DataPointAccuracy
import androidx.health.services.client.data.SampleDataPoint
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.Instant

class HeartRateDTOTest {

    // Mock a SampleDataPoint
    private val mockSampleDataPoint: SampleDataPoint<Double> = mockk()

    // Mocks for nested properties
    private val mockAccuracy: DataPointAccuracy = mockk()
    private val mockDuration: Duration = Duration.ofMillis(1000)
    private val bootInstant: Instant = Instant.now()
    private val currentTime = bootInstant.toEpochMilli() + mockDuration.toMillis()

    @Test
    fun `test toHeartRateCacheEntity conversion`() {
        val dto = HeartRateDTO(98.5, currentTime, "UNKNOWN")
        val entity = dto.toHeartRateCacheEntity()

        assertEquals(98.5, entity.value, 0.001)
        assertEquals(currentTime, entity.timeInstant)
        assertEquals("UNKNOWN", entity.accuracy)
    }

    @Test
    fun `test toHeartRate conversion`() {
        val dto = HeartRateDTO(98.9, currentTime, "UNKNOWN")
        val heartRate = dto.toHeartRate()

        assertEquals(98, heartRate.value)
    }

    @Test
    fun `test toHeartRateDTOList conversion`() {
        val mockInstant = Instant.parse("2023-07-30T02:21:51.894Z")

        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 1692376052L
        every { mockSampleDataPoint.getTimeInstant(any()) } returns mockInstant

        val mockList = listOf(mockSampleDataPoint, mockSampleDataPoint)

        every { mockSampleDataPoint.value } returns 98.5
        every { mockSampleDataPoint.timeDurationFromBoot } returns mockDuration
        every { mockSampleDataPoint.accuracy } returns mockAccuracy
        every { mockAccuracy.toString() } returns "UNKNOWN"

        val dtoList = mockList.toHeartRateDTOList()

        assertEquals(2, dtoList.size)

        val firstDto = dtoList[0]

        assertEquals(98.5, firstDto.value, 0.001)
        assertEquals("UNKNOWN", firstDto.accuracy)

        val secondDto = dtoList[1]
        assertEquals(98.5, secondDto.value, 0.001)
        assertEquals("UNKNOWN", secondDto.accuracy)
    }

    @Test
    fun `test determineHeartRateAccuracy returns correct accuracy`() {
        val accuracy = determineHeartRateAccuracy(mockAccuracy)

        assertEquals("UNKNOWN", accuracy)
    }
}