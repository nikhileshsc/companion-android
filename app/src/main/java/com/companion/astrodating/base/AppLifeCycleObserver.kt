package com.companion.astrodating.base

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import com.companion.astrodating.ui.home.viewmodel.HomeUserViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.companion.astrodating.ui.home.data.requestData.updateOnlineStatusRequestData
import com.companion.astrodating.ui.home.domain.repository.IGetHomeUserRepository
import com.companion.astrodating.util.StorePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor(
//    private val homeUserViewModel: HomeUserViewModel,
    private val repository: IGetHomeUserRepository,
    @ApplicationContext private val context: Context
) : DefaultLifecycleObserver {

    private var job: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStart(owner: LifecycleOwner) {
        startPinging()
    }

    override fun onStop(owner: LifecycleOwner) {
        stopPinging()
    }

    private fun startPinging() {
        val token = StorePreferences.getAuthToken()
        if (token.isNullOrEmpty()) return

        job = scope.launch {
            while (isActive) {
                try {
                    repository.getOnlineStatusDetails(
                        token,
                        updateOnlineStatusRequestData(true)
                    )
                } catch (e: Exception) {
                    // Log error if needed
                }
                delay(70_000) //70_000 70 seconds
            }
        }
    }

    private fun stopPinging() {
        job?.cancel()
        job = null
    }
}
