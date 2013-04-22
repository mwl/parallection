package dk.mwl.parallection;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallectionFrom<T> {
    private final ExecutorService executor;
    private final Vector<Future> futures = new Vector<>();
    private final List<T> list = new ArrayList<>();

    private ParallectionFrom(List<T> list, int threads) {
        this.list.addAll(list);
        executor = Executors.newFixedThreadPool(threads);
    }

    public static <T> ParallectionFrom parallection(List<T> list) {
        return new ParallectionFrom(list, 10);
    }

    public ParallectionFrom<T> foreach(final Action<T> action) {
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
