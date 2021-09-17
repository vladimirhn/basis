package koptional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class KOptional<T> extends StandardOptionalWrapper<T>{

    private final Optional<T> value;

    @Override
    protected Optional<T> getValue() {
        return this.value;
    }

    private KOptional(T value) {
        this.value = Optional.ofNullable(value);
    }

    public static<T> KOptional<T> empty() {
        return new KOptional<>(null);
    }

    public static <T> KOptional<T> of(T value) {
        return new KOptional<>(value);
    }

    public KOptional<T> ifSomething(Consumer<? super T> action) {
        value.ifPresent(action::accept);
        return this;
    }

    public KOptional<T> ifNothing(Runnable action) {
        if (value.isEmpty()) {
            action.run();
        }
        return this;
    }

    public KOptional<T> ifSomethingMap(Function<? super T, T> function) {
        return value.map(t -> KOptional.of(function.apply(t))).orElse(this);
    }

    public KOptional<T> ifNothingMap(Supplier<T> supplier) {
        if (value.isPresent()) {
            return this;
        } else {
            return KOptional.of(supplier.get());
        }
    }
}
