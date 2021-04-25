package ru.minat0.minetail.core.storage;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T, L> {
    Optional<T> get(@NotNull L l);

    Collection<T> getAll();

    void create(@NotNull T t);

    void update(@NotNull L l);

//    void save(@NotNull L l);

    void delete(@NotNull L l);
}
