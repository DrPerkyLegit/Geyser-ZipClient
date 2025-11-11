package org.geyser.extension.zipclient.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskScheduler {
    private final ScheduledExecutorService scheduler;
    private final AtomicInteger taskIdCounter = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    public TaskScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(2); // 2 threads, change if needed
    }

    public int runLater(Runnable task, long delay, TimeUnit unit) {
        int taskId = taskIdCounter.incrementAndGet();
        ScheduledFuture<?> future = scheduler.schedule(task, delay, unit);
        tasks.put(taskId, future);
        return taskId;
    }

    public int runRepeating(Runnable task, long initialDelay, long period, TimeUnit unit) {
        int taskId = taskIdCounter.incrementAndGet();
        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(task, initialDelay, period, unit);
        tasks.put(taskId, future);
        return taskId;
    }

    public void cancelTask(int taskId) {
        ScheduledFuture<?> future = tasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
