package tech.thatgravyboat.skyblockapi.utils

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.DurationUnit

public object Scheduling {

    private val counter = AtomicInteger(0)
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(10) { target: Runnable? ->
        Thread(target, "Scheduling-Thread-${counter.getAndIncrement()}").apply {
            isDaemon = true
        }
    }

    public fun schedule(time: Duration, runnable: suspend () -> Unit): ScheduledFuture<*> = scheduler.schedule(
        { runCatchBlocking { runnable() } },
        time.toLong(DurationUnit.MILLISECONDS),
        TimeUnit.MILLISECONDS,
    )

    public fun schedule(initalDelay: Duration, delay: Duration, runnable: suspend () -> Unit): ScheduledFuture<*> = scheduler.scheduleAtFixedRate(
        { runCatchBlocking { runnable() } },
        initalDelay.toLong(DurationUnit.MILLISECONDS),
        delay.toLong(DurationUnit.MILLISECONDS),
        TimeUnit.MILLISECONDS,
    )

    public fun async(runnable: suspend () -> Unit) {
        CompletableFuture.runAsync {
            runCatchBlocking { runnable() }
        }
    }
}
