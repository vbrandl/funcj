package org.typemeta.funcj.functions;

import org.typemeta.funcj.tuples.*;

/**
 * Interfaces for composable functions that have no return type,
 * that may throw generic exceptions.
 */
public abstract class SideEffectGenEx {

    /**
     *
     * Side-effect of arity 0.
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F0<X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <X>       the exception type
         * @return          the function
         */
        static <X extends Exception> F0<X> of(F0<X> f) {
            return f;
        }

        /**
         * Apply this function
         * @throws X        the exception
         */
        void apply() throws X;
    }

    /**
     * Side-effect of arity 1.
     * @param <A>       the function argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F<A, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, X extends Exception> F<A, X> of(F<A, X> f) {
            return f;
        }

        /**
         * Apply this function
         * @param a         the function argument
         * @throws X        the exception
         */
        void apply(A a) throws X;
    }

    /**
     * Side-effect of arity 2.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F2<A, B, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, X extends Exception> F2<A, B, X> of(F2<A, B, X> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @throws X        the exception
         */
        void apply(A a, B b) throws X;

        /**
         * Partially apply this function.
         * @param a         the value to partially apply this function to
         * @return          the partially applied function
         */
        default F<B, X> partial(A a) {
            return b -> apply(a, b);
        }

        /**
         * Flip this function by reversing the order of its arguments.
         * @return          the flipped function
         */
        default F2<B, A, X> flip() {
            return (b, a) -> apply(a, b);
        }

        /**
         * Convert this function to one that operates on a {@link Tuple2}.
         * @return          a function that operates on a {@link Tuple2}
         */
        default F<Tuple2<A, B>, X> tupled() {
            return t2 -> apply(t2._1, t2._2);
        }
    }

    /**
     * Side-effect of arity 3.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F3<A, B, C, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, X extends Exception> F3<A, B, C, X> of(F3<A, B, C, X> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @throws X        the exception
         */
        void apply(A a, B b, C c) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the value
         * @return          the partially applied function
         */
        default F2<B, C, X> partial(A a) {
            return (b, c) -> apply(a, b, c);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F<C, X> partial(A a, B b) {
            return c -> apply(a, b, c);
        }

        /**
         * Convert this function to one that operates on a {@link Tuple3}.
         * @return          a function that operates on a {@link Tuple3}
         */
        default F<Tuple3<A, B, C>, X> tupled() {
            return t3 -> apply(t3._1, t3._2, t3._3);
        }
    }

    /**
     * Side-effect of arity 4.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F4<A, B, C, D, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, X extends Exception> F4<A, B, C, D, X> of(F4<A, B, C, D, X> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @throws X        the exception
         */
        void apply(A a, B b, C c, D d) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F3<B, C, D, X> partial(A a) {
            return (b, c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F2<C, D, X> partial(A a, B b) {
            return (c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F<D, X> partial(A a, B b, C c) {
            return d -> apply(a, b, c, d);
        }
    }

    /**
     * Side-effect of arity 5.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F5<A, B, C, D, E, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, X extends Exception> F5<A, B, C, D, E, X> of(F5<A, B, C, D, E, X> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @throws X        the exception
         */
        void apply(A a, B b, C c, D d, E e) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F4<B, C, D, E, X> partial(A a) {
            return (b, c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F3<C, D, E, X> partial(A a, B b) {
            return (c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F2<D, E, X> partial(A a, B b, C c) {
            return (d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F<E, X> partial(A a, B b, C c, D d) {
            return e -> apply(a, b, c, d, e);
        }
    }

    /**
     * Side-effect of arity 6.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F6<A, B, C, D, E, G, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, G, X extends Exception> F6<A, B, C, D, E, G, X> of(F6<A, B, C, D, E, G, X> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @param g         the function's sixth argument
         * @throws X        the exception
         */
        void apply(A a, B b, C c, D d, E e, G g) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F5<B, C, D, E, G, X> partial(A a) {
            return (b, c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F4<C, D, E, G, X> partial(A a, B b) {
            return (c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F3<D, E, G, X> partial(A a, B b, C c) {
            return (d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F2<E, G, X> partial(A a, B b, C c, D d) {
            return (e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to five values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @return          the partially applied function
         */
        default F<G, X> partial(A a, B b, C c, D d, E e) {
            return g -> apply(a, b, c, d, e, g);
        }
    }

    /**
     * Side-effect of arity 7.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <H>       the function's seventh argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F7<A, B, C, D, E, G, H, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, G, H, X extends Exception> F7<A, B, C, D, E, G, H, X> of(F7<A, B, C, D, E, G, H, X> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @param g         the function's sixth argument
         * @param h         the function's seventh argument
         * @throws X        the exception
         */
        void apply(A a, B b, C c, D d, E e, G g, H h) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F6<B, C, D, E, G, H, X> partial(A a) {
            return (b, c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F5<C, D, E, G, H, X> partial(A a, B b) {
            return (c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F4<D, E, G, H, X> partial(A a, B b, C c) {
            return (d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F3<E, G, H, X> partial(A a, B b, C c, D d) {
            return (e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to five values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @return          the partially applied function
         */
        default F2<G, H, X> partial(A a, B b, C c, D d, E e) {
            return (g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to six values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @param g         the sixth value
         * @return          the partially applied function
         */
        default F<H, X> partial(A a, B b, C c, D d, E e, G g) {
            return h -> apply(a, b, c, d, e, g, h);
        }
    }

    /**
     * Side-effect of arity 8.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <H>       the function's seventh argument type
     * @param <I>       the function's eighth argument type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F8<A, B, C, D, E, G, H, I, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @param <I>       the function's eighth argument type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, G, H, I, X extends Exception> F8<A, B, C, D, E, G, H, I, X> of(F8<A, B, C, D, E, G, H, I, X> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @param g         the function's sixth argument
         * @param h         the function's seventh argument
         * @param i         the function's eighth argument
         * @throws X        the exception
         */
        void apply(A a, B b, C c, D d, E e, G g, H h, I i) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F7<B, C, D, E, G, H, I, X> partial(A a) {
            return (b, c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F6<C, D, E, G, H, I, X> partial(A a, B b) {
            return (c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F5<D, E, G, H, I, X> partial(A a, B b, C c) {
            return (d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F4<E, G, H, I, X> partial(A a, B b, C c, D d) {
            return (e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to five values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @return          the partially applied function
         */
        default F3<G, H, I, X> partial(A a, B b, C c, D d, E e) {
            return (g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to six values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @param g         the sixth value
         * @return          the partially applied function
         */
        default F2<H, I, X> partial(A a, B b, C c, D d, E e, G g) {
            return (h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to seven values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @param g         the sixth value
         * @param h         the seventh value
         * @return          the partially applied function
         */
        default F<I, X> partial(A a, B b, C c, D d, E e, G g, H h) {
            return i -> apply(a, b, c, d, e, g, h, i);
        }
    }
}
