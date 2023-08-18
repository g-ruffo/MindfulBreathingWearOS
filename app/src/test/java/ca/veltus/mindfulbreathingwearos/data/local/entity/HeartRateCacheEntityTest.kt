package ca.veltus.mindfulbreathingwearos.data.local.entity

import junit.framework.TestCase.assertEquals
import org.junit.Test

class HeartRateCacheEntityTest {

    @Test
    fun `test conversion from HeartRateCacheEntity to HeartRateEntity`() {
        val cacheEntity = HeartRateCacheEntity(
            value = 95.5,
            timeInstant = 1629212300000L,
            accuracy = "HIGH",
            cacheId = 1
        )

        val entity = cacheEntity.toHeartRateEntity()

        assertEquals(95.5, entity.value, 0.001)
        assertEquals(1629212300000L, entity.timeInstant)
        assertEquals("HIGH", entity.accuracy)
        assertEquals(1, entity.id)
    }
}