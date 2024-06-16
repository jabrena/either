package info.jab.fp.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A sealed interface representing a computation that may either result in a value (success)
 * or an exception (failure).
 *
 * @param <T> the type of the value
 *
 * @author Juan Antonio Breña Moral
 * @author ChatGPT-40
 */
public sealed interface Result<T> permits Result.Success, Result.Failure {
    /**
     * Creates a failed Result with the given exception.
     *
     * @param exception the exception
     * @param <T> the type of the value
     * @return a failed Result
     */
    static <T> Result<T> failure(Throwable exception) {
        return new Result.Failure<>(exception);
    }

    /**
     * Creates a successful Result with the given value.
     *
     * @param value the value
     * @param <T> the type of the value
     * @return a successful Result
     */
    static <T> Result<T> success(T value) {
        return new Result.Success<>(value);
    }

    /**
     * Checks if the Result is successful.
     *
     * @return true if successful, false otherwise
     */
    boolean isSuccess();

    /**
     * Checks if the Result is a failure.
     *
     * @return true if a failure, false otherwise
     */
    boolean isFailure();

    /**
     * Performs the given action if the Result is successful.
     *
     * @param consumer the action to be performed
     */
    void ifSuccess(Consumer<T> consumer);

    /**
     * Performs the given action if the Result is a failure.
     *
     * @param consumer the action to be performed
     */
    void ifFailure(Consumer<Throwable> consumer);

    /**
     * Returns the value if present, otherwise returns an empty Optional.
     *
     * @return an Optional with the value or empty if failure
     */
    Optional<T> getValue();

    /**
     * Returns the exception if present, otherwise returns an empty Optional.
     *
     * @return an Optional with the exception or empty if success
     */
    Optional<Throwable> getException();

    /**
     * Returns the value if the Result is successful, otherwise returns the result of the specified Supplier.
     *
     * @param other a Supplier whose result is returned if the Result is a failure
     * @return the value if the Result is successful, otherwise the result of the Supplier
     */
    T getOrElse(Supplier<? extends T> other);

    /**
     * Applies the given function to the value if the Result is successful and returns a new Result.
     *
     * @param mapper the function to apply to the value
     * @param <U> the type of the value of the new Result
     * @return a new Result with the mapped value if successful, otherwise the original failure
     */
    <U> Result<U> map(Function<? super T, ? extends U> mapper);

    /**
     * Applies the given function to the value if the Result is successful and returns a new Result.
     * The function should return a new Result.
     *
     * @param mapper the function to apply to the value
     * @param <U> the type of the value of the new Result
     * @return the Result returned by the mapper if successful, otherwise the original failure
     */
    <U> Result<U> flatMap(Function<? super T, Result<U>> mapper);

    /**
     * Applies the given function to the exception if the Result is a failure and returns a new Result
     * with the mapped value.
     *
     * @param mapper the function to apply to the exception
     * @return a new Result with the mapped value if failure, otherwise the original success
     */
    Result<T> recover(Function<? super Throwable, ? extends T> mapper);

    /**
     * Applies the given function to the exception if the Result is a failure and returns a new Result.
     * The function should return a new Result.
     *
     * @param mapper the function to apply to the exception
     * @return the Result returned by the mapper if failure, otherwise the original success
     */
    Result<T> recoverCatching(Function<? super Throwable, Result<T>> mapper);

    /**
     * Executes the given supplier and returns a Result. If the supplier throws an exception,
     * it returns a failed Result with the thrown exception.
     *
     * @param supplier the supplier to execute
     * @param <T> the type of the value
     * @return a successful Result if the supplier succeeds, otherwise a failed Result
     */
    static <T> Result<T> mapCatching(CheckedSupplier<T> supplier) {
        try {
            return new Result.Success<>(supplier.get());
        } catch (Throwable e) {
            return new Result.Failure<>(e);
        }
    }

    /**
     * A functional interface representing a supplier that may throw an exception.
     *
     * @param <T> the type of the value
     */
    @FunctionalInterface
    interface CheckedSupplier<T> {
        /**
         * Gets a result, potentially throwing an exception.
         *
         * @return a result
         * @throws Throwable if unable to supply a result
         */
        T get() throws Throwable;
    }

    /**
     * Record representing a successful Result in the Result.
     *
     * <p>This record encapsulates the successful state of a computation within the Result,
     * holding the value resulting from a successful computation.
     *
     * @param <T> the type of the value returned in a successful computation
     * @param value the value resulting from a successful computation
     */
    record Success<T>(T value) implements Result<T> {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public void ifSuccess(Consumer<T> consumer) {
            consumer.accept(value);
        }

        @Override
        public void ifFailure(Consumer<Throwable> consumer) {
            // No action needed for success
        }

        @Override
        public Optional<T> getValue() {
            return Optional.ofNullable(value);
        }

        @Override
        public Optional<Throwable> getException() {
            return Optional.empty();
        }

        @Override
        public T getOrElse(Supplier<? extends T> other) {
            return value;
        }

        @Override
        public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
            return new Result.Success<>(mapper.apply(value));
        }

        @Override
        public <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public Result<T> recover(Function<? super Throwable, ? extends T> mapper) {
            return this; // No recovery needed for success
        }

        @Override
        public Result<T> recoverCatching(Function<? super Throwable, Result<T>> mapper) {
            return this; // No recovery needed for success
        }
    }

    /**
     * Record representing a failed Result in the Result.
     *
     * <p>This record encapsulates the failure state of a computation within the Result,
     * holding an exception that caused the failure.
     *
     * @param <T> the type of the value expected in a successful computation
     * @param exception the exception that caused the failure
     */
    record Failure<T>(Throwable exception) implements Result<T> {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public void ifSuccess(Consumer<T> consumer) {
            // No action needed for failure
        }

        @Override
        public void ifFailure(Consumer<Throwable> consumer) {
            consumer.accept(exception);
        }

        @Override
        public Optional<T> getValue() {
            return Optional.empty();
        }

        @Override
        public Optional<Throwable> getException() {
            return Optional.ofNullable(exception);
        }

        @Override
        public T getOrElse(Supplier<? extends T> other) {
            return other.get();
        }

        @Override
        public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
            return new Result.Failure<>(exception);
        }

        @Override
        public <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
            return new Result.Failure<>(exception);
        }

        @Override
        public Result<T> recover(Function<? super Throwable, ? extends T> mapper) {
            return new Result.Success<>(mapper.apply(exception));
        }

        @Override
        public Result<T> recoverCatching(Function<? super Throwable, Result<T>> mapper) {
            return mapper.apply(exception);
        }
    }
}
