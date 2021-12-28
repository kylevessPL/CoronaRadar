package pl.piasta.coronaradar.ui.base

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.mockk.clearAllMocks

open class BaseViewModelTest(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(body) {

    constructor() : this({})

    init {
        listener(InstantExecutorListener())
    }
}

class InstantExecutorListener : TestListener {

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread() = true
        })
    }

    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
        super.afterContainer(testCase, result)
        clearAllMocks()
    }
}