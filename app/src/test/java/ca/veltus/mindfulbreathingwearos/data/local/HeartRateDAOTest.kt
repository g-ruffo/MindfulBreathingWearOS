package ca.veltus.mindfulbreathingwearos.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateCacheEntity
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeartRateDAOTest {

    private lateinit var db: TestHeartRateDatabase
    private lateinit var dao: HeartRateDAO

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            TestHeartRateDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.heartRateDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAllHeartRates() = runBlocking {
        val heartRates = listOf(
            HeartRateEntity(90.0, 1629212300000L, "HIGH", 1),
            HeartRateEntity(95.0, 1629212300005L, "MEDIUM", 2)
        )
        dao.insertAllHeartRates(heartRates)

        val stats = dao.getDatabaseStats()
        assertEquals(2, stats.count)
        assertEquals(1629212300005L, stats.lastAddedTimestamp)
    }

    @Test
    fun testGetAllFromCache() = runBlocking {
        val cacheData = listOf(
            HeartRateCacheEntity(80.0, 1629212300010L, "LOW"),
            HeartRateCacheEntity(85.0, 1629212300015L, "MEDIUM")
        )
        dao.insertAllHeartRateCache(cacheData)

        val retrievedData = dao.getAllFromCache()
        assertEquals(2, retrievedData.size)
    }

    @Test
    fun testClearCache() = runBlocking {
        val cacheData = listOf(
            HeartRateCacheEntity(70.0, 1629212300020L, "LOW")
        )
        dao.insertAllHeartRateCache(cacheData)
        dao.clearCache()

        val retrievedData = dao.getAllFromCache()
        assertTrue(retrievedData.isEmpty())
    }

    @Test
    fun testGetCacheStats() = runBlocking {
        val cacheData = listOf(
            HeartRateCacheEntity(65.0, 1629212300025L, "LOW")
        )
        dao.insertAllHeartRateCache(cacheData)

        val stats = dao.getCacheStats()
        assertEquals(1, stats.count)
        assertEquals(1629212300025L, stats.lastAddedTimestamp)
    }
}