package com.distributedsystems.storage;

import java.util.Optional;

/**
 * StorageEngine defines the basic operations for a local key-value node.
 * This abstraction allows us to swap the local storage implementation
 * (e.g., in-memory vs. disk-based) without affecting the distribution logic.
 */
public interface StorageEngine {
    void put(String key, String value);
    Optional<String> get(String key);
    void delete(String key);
}
