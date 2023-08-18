package ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection

import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
@ExtendWith(MockitoExtension::class)
class ToggleDatabaseConnectionUseCaseTest {

    @Mock
    lateinit var mockRepository: BreathingRepository

    lateinit var useCase: ToggleDatabaseConnectionUseCase

    @BeforeEach
    fun setUp() {
        useCase = ToggleDatabaseConnectionUseCase(mockRepository)
    }

    @org.junit.jupiter.api.Test
    fun `when invoke is called, it should call toggleDatabaseConnection on repository with correct millis`() {
        val timerTimeInSeconds = 5
        val expectedMillis = 5000L

        useCase.invoke(timerTimeInSeconds)

        verify(mockRepository).toggleDatabaseConnection(timerTime = expectedMillis)
    }
}