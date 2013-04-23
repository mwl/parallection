package dk.mwl.parallection;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parallection<T> {
    private final ExecutorService executor;
    private final Vector<Future> futures = new Vector<>();
    private final List<T> list = new ArrayList<>();

    private Parallection(List<T> list, int threads) {
        this.list.addAll(list);
        executor = threads < 1 ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool(threads);
    }

    public static <T> Parallection parallection(List<T> list) {
        return parallection(list, 10);
    }

    public static <T> Parallection parallection(List<T> list, int numberOfThreads) {
        return new Parallection(list, numberOfThreads);
    }

    public Parallection<T> foreach(final Action<T> action) {
        for (final T element : list) {
            futures.add(executor.submit(new Runnable() {
                @Override
                public void run() {
                    action.action(element);
                }
            }));
        }
        return this;
    }

    public void join() {
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new ParallectionException("Future has been interrupted", e);
            } catch (ExecutionException e) {
                throw new ParallectionException("Execution failed", e);
            }
        }
    }

}
