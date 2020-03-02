package com.oguz.waes.scalableweb.cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class CacheTest {

    @InjectMocks
    private LRUCache<Integer, String> lruCache;

    @Before
    public void before() {
        lruCache.putEntry(1, "a");
        lruCache.putEntry(2, "b");
        lruCache.putEntry(3, "c");
    }

    @Test
    public void testGetEntry_Success() {
        String c = lruCache.getEntry(3);
        assertNotNull(c);
        assertEquals(c, "c");
    }

    @Test
    public void testPutEntry_Success() {
        lruCache.putEntry(4, "d");
        assertNotNull(lruCache.getEntry(4));
        assertEquals(lruCache.getEntry(4), "d");
    }

    @Test
    public void testPutEntryMoveToTop_Success() {
        lruCache.putEntry(5, "e");
        assertNotNull(lruCache.getEntry(5));
        assertEquals(lruCache.getStart(), "e");
    }

    @Test
    public void testEvictEntry_Success() {
        lruCache.evict(3);
        assertNull(lruCache.getEntry(3));
    }
}
