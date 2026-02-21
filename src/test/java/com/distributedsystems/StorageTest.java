package com.distributedsystems;

import com.distributedsystems.storage.InMemoryStorageEngine;
import com.distributedsystems.storage.StorageEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

public class StorageTest {
    @Test
    public void testBasicPutAndGet() {
        StorageEngine engine = new InMemoryStorageEngine();
        engine.put("key1", "value1");
        assertEquals(Optional.of("value1"), engine.get("key1"));
    }

    @Test
    public void testDelete() {
        StorageEngine engine = new InMemoryStorageEngine();
        engine.put("key1", "value1");
        engine.delete("key1");
        assertEquals(Optional.empty(), engine.get("key1"));
    }
}
