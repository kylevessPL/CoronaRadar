package pl.piasta.coronaradar

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec

open class BaseViewModelTest : BehaviorSpec() {

    init {
        listener(InstantExecutorListener())
    }
}

class InstantExecutorListener : TestListener {

    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread() = true
        })
    }
}