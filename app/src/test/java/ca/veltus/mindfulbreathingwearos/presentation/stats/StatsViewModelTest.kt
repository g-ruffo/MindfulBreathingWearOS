package ca.veltus.mindfulbreathingwearos.presentation.stats

import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseUpdateEvent
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_cache_stats.GetCacheStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_connection_state.GetDatabaseConnectionStateUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_stats.GetDatabaseStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_updates.GetDatabaseUpdatesUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_timer_time.GetTimerTimeUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_uncached_stats.GetUncachedStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection.ToggleDatabaseConnectionUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.verify
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest

class StatsViewModelTest {

    private val getDatabaseUpdatesUseCase: GetDatabaseUpdatesUseCase = mockk()
    private val getDatabaseConnectionStateUseCase: GetDatabaseConnectionStateUseCase = mockk()
    private val toggleDatabaseConnectionUseCase: ToggleDatabaseConnectionUseCase = mockk()
    private val getTimerTimeUseCase: GetTimerTimeUseCase = mockk()
    private val getCacheStatsUseCase: GetCacheStatsUseCase = mockk()
    private val getDatabaseStatsUseCase: GetDatabaseStatsUseCase = mockk()
    private val getUncachedStatsUseCase: GetUncachedStatsUseCase = mockk()

    private val dispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(dispatcher)

    private lateinit var viewModel: StatsViewModel

    @Before
    fun setUp() {
        every { getDatabaseUpdatesUseCase() } returns flowOf(DatabaseUpdateEvent())
        every { getDatabaseConnectionStateUseCase() } returns flowOf(true)
        every { getTimerTimeUseCase() } returns flowOf(0)
        every { getCacheStatsUseCase() } returns flowOf(Resource.Loading())
        every { getDatabaseStatsUseCase() } returns flowOf(Resource.Loading())
        every { getUncachedStatsUseCase() } returns flowOf(Resource.Loading())
        viewModel = StatsViewModel(
            getDatabaseUpdatesUseCase,
            getDatabaseConnectionStateUseCase,
            toggleDatabaseConnectionUseCase,
            getTimerTimeUseCase,
            getCacheStatsUseCase,
            getDatabaseStatsUseCase,
            getUncachedStatsUseCase
        )
    }

    @Test
    fun `when toggleDatabaseEnabled is called, toggleDatabaseConnectionUseCase should be invoked`() {
        testScope.runBlockingTest {
            viewModel.toggleDatabaseEnabled()
            verify { toggleDatabaseConnectionUseCase.invoke(viewModel.timerTimeSeconds.value) }
        }
    }

    @Test
    fun `when updateTimerTime is called with positive value, timer time should increase`() {
        val initialTime = viewModel.timerTimeSeconds.value
        val addedTime = 60
        viewModel.updateTimerTime(addedTime)
        assert(viewModel.timerTimeSeconds.value == initialTime + addedTime)
    }

    @Test
    fun `when updateTimerTime is called with negative value, timer time should decrease`() {
        val initialTime = viewModel.timerTimeSeconds.value
        val subtractedTime = -60
        viewModel.updateTimerTime(subtractedTime)
        assert(viewModel.timerTimeSeconds.value == initialTime + subtractedTime)
    }
    @After
    fun tearDown() {
        dispatcher.cleanupTestCoroutines()
    }
}