package dk.mwl.parallection;

public interface Convert<F, T> {
    T to(F from);
}
