package forrealdatingapp;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleThreadWorker {
    private static final LinkedBlockingQueue<Callable<?>> taskQueue = new LinkedBlockingQueue<>();
    private static final Thread workerThread;

    static {
        workerThread = new Thread(() -> {
            while (true) { // Keep thread alive
                try {
                    Callable<?> task = (Callable<?>) taskQueue.take(); // Take the next task (waits if empty)
                    // Execute the task and handle the result
                    Future<?> result = Executors.newSingleThreadExecutor().submit(task);
                    Object returnValue = result.get(); // Get the result (blocking if not ready)
                    // You can handle the result here, e.g., passing it back to the main thread
                    System.out.println("Task Result: " + returnValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        workerThread.setDaemon(true); // Thread stops when the program ends
        workerThread.start(); // Start the worker thread
    }

    public static Future<?> execute(Callable<?> task) {
        try {
            taskQueue.add(task); // Add task to queue
            return null; // This is a placeholder; Future is handled inside worker thread
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
