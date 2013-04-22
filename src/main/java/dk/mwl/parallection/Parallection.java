package dk.mwl.parallection;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;

public class Parallection<T> {
    private final ExecutorService executor;
    private final Vector<Future<T>> futures = new Vector<>();
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
            futures.add(executor.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    action.action(element);
                    return element;
                }
            }));
        }
        return this;
    }

    public Parallection<T> join() throws ExecutionException, InterruptedException {
        List<T> result = new ArrayList<>();
        for (Future<T> future : futures) {
            result.add(future.get());
        }
        return parallection(result);
    }

    /**
     * TODO: As results arrive from parent run new runs should start
     * @return
     */
    public Parallection<T> stream() {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * TODO:
     * @param converter
     * @param <N>
     * @return
     */
    public <N> Parallection<N> convert(Convert<T, N> converter) {
        throw new RuntimeException("Not yet implemented");
    }
}
