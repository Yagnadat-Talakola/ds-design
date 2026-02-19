package com.distributedsystems.storage;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple thread-safe in-memory implementation of the StorageEngine.
 * In a real distributed system, this might be replaced by an LSM-tree (like LevelDB)
 * or a B-Tree based storage engine for persistence.
 */
public class InMemoryStorageEngine implements StorageEngine {
    private final Map<String, String> store = new ConcurrentHashMap<>();

    @Override
    public void put(String key, String value) {
        store.put(key, value);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public void delete(String key) {
        store.remove(key);
    }
}
