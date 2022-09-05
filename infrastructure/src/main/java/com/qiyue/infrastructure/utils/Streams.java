package com.qiyue.infrastructure.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams<S> {

    public static final int PARALLEL_SIZE = 1000;
    private Stream<S> stream;

    public Streams() {
        this.stream = Stream.empty();
    }

    public Streams(Stream<S> stream) {
        if (Objects.isNull(stream)) {
            this.stream = Stream.empty();
        } else {
            this.stream = stream;
        }
    }

    public static <S> Streams<S> of(Collection<S> sCollection) {
        if (Objects.isNull(sCollection)) {
            return new Streams<>();
        }
        if (sCollection.size() >= PARALLEL_SIZE) {
            return new Streams<>(sCollection.parallelStream());
        }
        return new Streams<>(sCollection.stream());
    }

    public static <S> Stream<S> stream(Collection<S> tCollection) {
        if (Objects.isNull(tCollection)) {
            return Stream.empty();
        }
        if (tCollection.size() >= PARALLEL_SIZE) {
            return tCollection.parallelStream();
        }
        return tCollection.stream();
    }

    @SafeVarargs
    public static <S> Streams<S> allInOne(Collection<S>... collections) {
        return new Streams<>(Arrays.stream(collections).flatMap(Collection::stream));
    }

    /**
     * 集合
     * 将源对象转换为目标对象
     *
     * @param <T>       目标对象泛型
     * @param converter 转换器
     * @return 目标对象集合
     */
    public <T> Streams<T> map(Function<S, T> converter) {
        if (Objects.isNull(converter)) {
            return new Streams<>();
        }
        return new Streams<>(this.stream.map(converter));
    }

    public List<S> toList() {
        return this.stream.collect(Collectors.toList());
    }

    public Set<S> toSet() {
        return this.stream.collect(Collectors.toSet());
    }

    public Streams<S> filter(Predicate<S> predicate) {
        this.stream = this.stream.filter(predicate.negate());
        return this;
    }

    @SafeVarargs
    public final <T, K> Streams<S> filter(Function<S, K> sourceKey, Function<T, K> targetKey,
                                          Collection<T>... tCollection) {
        if (Objects.isNull(sourceKey) || Objects.isNull(targetKey)) {
            return this;
        }
        Set<K> tkSet = allInOne(tCollection).map(targetKey).toSet();
        this.stream = this.stream.filter(s -> !tkSet.contains(sourceKey.apply(s)));
        return this;
    }

    public Streams<S> get(@NotNull Predicate<S> predicate) {
        this.stream = this.stream.filter(predicate);
        return this;
    }

    @SafeVarargs
    public final <T, K> Streams<S> get(Function<S, K> sourceKey, Function<T, K> targetKey,
                                       Collection<T>... tCollection) {
        if (Objects.isNull(sourceKey) || Objects.isNull(targetKey)) {
            return this;
        }
        Set<K> tkSet = allInOne(tCollection).map(targetKey).toSet();
        this.stream = this.stream.filter(s -> tkSet.contains(sourceKey.apply(s)));
        return this;
    }

    public void forEach(Consumer<S> consumer) {
        if (this.stream.isParallel()) {
            this.stream.forEachOrdered(consumer);
        } else {
            this.stream.forEach(consumer);
        }
    }

}
