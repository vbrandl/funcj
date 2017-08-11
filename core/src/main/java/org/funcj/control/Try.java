package org.funcj.control;

import org.funcj.data.IList;
import org.funcj.util.*;
import org.funcj.util.Functions.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * Simple monadic wrapper for computations which result in either a successfully computed value
 * or an error.
 * <p>
 * Try is essentially a discriminated union of {@code Success} (which wraps the result value)
 * and {@code Failure} (which wraps an exception).
 * @param <T> successful result type
 */
public interface Try<T> {

    /**
     * Create a {@code Success} value that wraps a successful result.
     * @param value successful result to be wrapped
     * @param <T> successful result type
     * @return a {@code Success} value
     */
    static <T> Try<T> success(T value) {
        return new Success<T>(value);
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param error error result
     * @param <T> successful result type
     * @return a {@code Failure} value
     */
    static <T> Try<T> failure(Exception error) {
        return new Failure<T>(error);
    }

    /**
     * Create a {@code Try} value from a function which either yields a result or throws.
     * @param f function which may throw
     * @param <T> successful result type
     * @return {@code Try} value which wraps the function result
     */
    static <T> Try<T> of(FunctionsEx.F0<T> f) {
        try {
            return new Success<T>(f.apply());
        } catch (Exception ex) {
            return new Failure<T>(ex);
        }
    }

    /**
     * Applicative function application.
     * @param tf function wrapped in a {@code Try}
     * @param ta function argument wrapped in a {@code Try}
     * @param <A> function argument type
     * @param <B> function return type
     * @return the result of applying the function to the argument, wrapped in a {@code Try}
     */
    static <A, B> Try<B> ap(Try<F<A, B>> tf, Try<A> ta) {
        return ta.apply(tf);
    }

    /**
     * Standard applicative traversal.
     * @param lt list of values
     * @param f function to be applied to each value in the list
     * @param <T> type of list elements
     * @param <U> type wrapped by the {@code Try} returned by the function
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <T, U> Try<IList<U>> traverse(IList<T> lt, F<T, Try<U>> f) {
        return lt.foldRight(
                (t, tlu) -> f.apply(t).apply(tlu.map(lu -> lu::add)),
                success(IList.nil())
        );
    }

    /**
     * Standard applicative traversal.
     * @param lt list of values
     * @param f function to be applied to each value in the list
     * @param <T> type of list elements
     * @param <U> type wrapped by the {@code Try} returned by the function
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <T, U> Try<List<U>> traverse(List<T> lt, F<T, Try<U>> f) {
        return Folds.foldRight(
                (t, tlt) -> f.apply(t).apply(tlt.map(lu -> u -> {lu.add(u); return lu;})),
                success(new ArrayList<>()),
                lt
        );
    }

    /**
     * Standard applicative sequencing.
     * @param ltt list of {@code Try} values
     * @param <T> type of list elements
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <T> Try<IList<T>> sequence(IList<Try<T>> ltt) {
        return ltt.foldRight(
            (tt, tlt) -> tt.apply(tlt.map(lt -> lt::add)),
            success(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * @param ltt list of {@code Try} values
     * @param <T> type of list elements
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <T> Try<List<T>> sequence(List<Try<T>> ltt) {
        return Folds.foldRight(
                (tt, tlt) -> tt.apply(tlt.map(lt -> t -> {lt.add(t); return lt;})),
                success(new ArrayList<>()),
                ltt
        );
    }

    /**
     * Indicates if this is a {code Success} value.
     * @return true if this value is a {code Success} value
     */
    boolean isSuccess();

    /**
     * Downgrade this value into an {@link java.util.Optional}.
     * @return a populated {@code Optional} value if this is a {Code Success} value,
     * otherwise an empty {@code Optional}
     */
    Optional<T> asOptional();

    /**
     * Either return the wrapped value if it's a {@code Success}, otherwise return the supplied default value.
     * @param defaultValue value to be returned if this is a failure value.
     * @return the success result value if it's a {@code Success}, otherwise return the supplied default value.
     */
    T getOrElse(T defaultValue);

    /**
     * Return the wrapped value if it's a {@code Success}, otherwise throw the result exception.
     * @return the wrapped value if it's a {@code Success}
     * @throws Exception if the wrapped value is a {@code Failure}
     */
    T getOrThrow() throws Exception;

    /**
     * Return the wrapped value if it's a {@code Success}, otherwise throw a RuntimeException.
     * @return the wrapped value if it's a {@code Success}
     */
    T get();

    /**
     * Push the result to a {@link java.util.function.Consumer}.
     * @param success consumer to be applied to {@code Success} values
     * @param failure consumer to be applied to {@code Failure} values
     */
    void handle(Consumer<Success<T>> success, Consumer<Failure<T>> failure);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param success function to be applied to {@code Success} values
     * @param failure function to be applied to {@code Failure} values
     * @param <R> return type of functions
     * @return the result of applying either function
     */
    <R> R match(F<Success<T>, ? extends R> success, F<Failure<T>, ? extends R> failure);

    /**
     * Functor function application.
     * If this value is a {@code Success} then apply the function to the value,
     * otherwise if this is a {@code Failure} then leave it untouched.
     * @param f function to be applied
     * @param <R> function return type
     * @return a {@code Try} that wraps the function result, or the original failure
     */
    <R> Try<R> map(F<? super T, ? extends R> f);

    /**
     * Applicative function application (inverted).
     * If the {@code tf} parameter is a {@code Success} value and this is a {@code Success} value,
     * then apply the function wrapped in the {@code tf} to this.
     * @param tf function wrapped in a {@code Try}
     * @param <R> return type of function
     * @return a {@code Try} wrapping the result of applying the function, or a {@code Failure} value
     */
    <R> Try<R> apply(Try<F<T, R>> tf);

    /**
     * Monadic bind/flatMap.
     * If this is a {@code Success} then apply the function to the value and return the result,
     * otherwise return the {@code Failure} result.
     * @param f function to be applied
     * @param <R> type parameter to the {@code Try} returned by the function
     * @return the result of combining this value with the function {@code f}
     */
    <R> Try<R> flatMap(F<? super T, Try<R>> f);

    /**
     * Variant of flatMap which ignores this value.
     * @param f function to be invoked
     * @param <R> type parameter to the {@code Try} returned by the function
     * @return the result of combining this value with the function {@code f}
     */
    default <R> Try<R> flatMap(F0<Try<R>> f) {
        return flatMap(u -> f.apply());
    }

    /**
     * Builder API for chaining together n {@code Try}s,
     * and applying an n-ary function at the end.
     * @param tb next {@code Try} value to chain
     * @param <U> successful result type for next {@code Try}
     * @return next builder
     */
    default <U> ApplyBuilder._2<T, U> and(Try<U> tb) {
        return new ApplyBuilder._2<T, U>(this, tb);
    }

    /**
     * Success type.
     * @param <T> successful result type
     */
    final class Success<T> implements Try<T> {

        public final T value;

        private Success(T value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return value;
        }

        @Override
        public T getOrThrow() throws Exception {
            return value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void handle(Consumer<Success<T>> success, Consumer<Failure<T>> failure) {
            success.accept(this);
        }

        @Override
        public <R> R match(F<Success<T>, ? extends R> success, F<Failure<T>, ? extends R> failure) {
            return success.apply(this);
        }

        @Override
        public <R> Try<R> map(F<? super T, ? extends R> f) {
            return success(f.apply(value));
        }

        @Override
        public <R> Try<R> apply(Try<F<T, R>> tf) {
            return tf.map(f -> f.apply(value));
        }

        @Override
        public <R> Try<R> flatMap(F<? super T, Try<R>> f) {
            return f.apply(value);
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.of(value);
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == null)
                return false;

            if (!(obj instanceof Success<?>))
                return false;

            final Success<?> rhs = (Success<?>)obj;

            return value.equals(rhs.value);
        }
    }

    /**
     * Failure type
     * @param <T> successful result type
     */
    final class Failure<T> implements Try<T> {

        public final Exception error;

        private Failure(Exception error) {
            this.error = Objects.requireNonNull(error);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T getOrThrow() throws Exception {
            throw error;
        }

        @Override
        public T get() {
            throw new RuntimeException(error);
        }

        @Override
        public void handle(Consumer<Success<T>> success, Consumer<Failure<T>> failure) {
            failure.accept(this);
        }

        @Override
        public <R> R match(F<Success<T>, ? extends R> success, F<Failure<T>, ? extends R> failure) {
            return failure.apply(this);
        }

        @Override
        public <R> Try<R> map(F<? super T, ? extends R> f) {
            return cast();
        }

        @Override
        public <R> Try<R> apply(Try<F<T, R>> tf) {
            return cast();
        }

        @Override
        public <R> Try<R> flatMap(F<? super T, Try<R>> f) {
            return cast();
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "Failure(" + error + ")";
        }

        @Override
        public int hashCode() {
            return error.hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == null)
                return false;

            if (!(obj instanceof Failure<?>))
                return false;

            final Failure<?> rhs = (Failure<?>)obj;

            // In general the equals() method for Exception classes isn't implemented,
            // which means we get object equality. This is rarely useful so here
            // we instead compare the string representations.
            return error.toString().equals(rhs.error.toString());
        }

        private <U> Try<U> cast() {
            return (Try<U>) this;
        }
    }

    class ApplyBuilder {
        public static class _2<A, B> {
            private final Try<A> ta;
            private final Try<B> tb;

            _2(Try<A> ta, Try<B> tb) {
                this.ta = ta;
                this.tb = tb;
            }

            public <R> Try<R> map(F<A, F<B, R>> f) {
                return tb.apply(ta.map(f));
            }

            public <R> Try<R> map(F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Try<C> tc) {
                return new _3<C>(tc);
            }

            public class _3<C> {
                private final Try<C> tc;

                private _3(Try<C> tc) {
                    this.tc = tc;
                }

                public <R> Try<R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), tc);
                }

                public <R> Try<R> map(F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Try<D> td) {
                    return new _4<D>(td);
                }

                public class _4<D> {
                    private final Try<D> td;

                    private _4(Try<D> td) {
                        this.td = td;
                    }

                    public <R> Try<R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), td);
                    }

                    public <R> Try<R> map(F4<A, B, C, D, R> f) {
                        return map(f.curry());
                    }

                    public <E> _5<E> and(Try<E> te) {
                        return new _5<E>(te);
                    }

                    public class _5<E> {
                        private final Try<E> te;

                        private _5(Try<E> te) {
                            this.te = te;
                        }

                        public <R> Try<R> map(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
                            return ap(_4.this.map(f), te);
                        }

                        public <R> Try<R> map(F5<A, B, C, D, E, R> f) {
                            return map(f.curry());
                        }

                        public <G> _6<G> and(Try<G> tg) {
                            return new _6<G>(tg);
                        }

                        public class _6<G> {
                            private final Try<G> tg;

                            private _6(Try<G> tg) {
                                this.tg = tg;
                            }

                            public <R> Try<R> map(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
                                return ap(_5.this.map(f), tg);
                            }

                            public <R> Try<R> map(F6<A, B, C, D, E, G, R> f) {
                                return map(f.curry());
                            }
                        }
                    }
                }
            }
        }
    }
}