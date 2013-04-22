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
        executor = Executors.newFixedThreadPool(threads);
    }

    public static <T> Parallection parallection(List<T> list) {
        return new Parallection(list, 10);
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

    public void join() throws ExecutionException, InterruptedException {
        for (Future future : futures) {
            future.get();
        }
    }

}