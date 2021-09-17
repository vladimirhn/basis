package koptional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class StandardOptionalWrapper<T> {

    protected abstract Optional<T> getValue();

    public T get() {
        return getValue().get();
    }

    public boolean isPresent() {
        return getValue().isPresent();
    }

    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    public void ifPresent(Consumer<? super T> action) {
        getValue().ifPresent(action);
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        getValue().ifPresentOrElse(action, emptyAction);
    }

    public Optional<T> filter(Predicate<? super T> predicate) {
        return getValue().filter(predicate);
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        return getValue().map(mapper);
    }

    public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
        return getValue().flatMap(mapper);
    }

    public Optional<T> or(Supplier<? extends Optional<? extends T>> supplier) {
        return getValue().or(supplier);
    }

    public Stream<T> stream() {
        return getValue().stream();
    }

    public T orElse(T other) {
        return getValue().orElse(other);
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return getValue().orElseGet(supplier);
    }

    public T orElseThrow() {
        return getValue().orElseThrow();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return getValue().orElseThrow(exceptionSupplier);
    }

    @Override
    public boolean equals(Object obj) {
        return getValue().equals(obj);
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
