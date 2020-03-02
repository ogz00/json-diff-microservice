package com.oguz.waes.scalableweb.service;

import com.oguz.waes.scalableweb.cache.LRUCache;
import com.oguz.waes.scalableweb.dto.response.ContentResponseDto;
import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.Target;
import com.oguz.waes.scalableweb.repository.ContentRepository;
import com.oguz.waes.scalableweb.service.content.DefaultContentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Optional;

import static com.oguz.waes.scalableweb.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContentServiceTest {
    @Mock
    private ContentRepository repository;

    @Mock
    private LRUCache<String, Content> cache;

    @InjectMocks
    private DefaultContentService contentService;

    private Content content;

    @Before
    public void before() {
        GLOBAL_CONTENT_DTO.setContent(TEST_DATA);
        content = mock(Content.class);
        when(content.getData()).thenReturn(TEST_DATA);
        when(content.getRootId()).thenReturn(R_1_);
        when(content.getTarget()).thenReturn(TARGET_RIGHT);
        when(repository.save(any(Content.class))).thenReturn(content);
    }

    @Test
    public void testGenerateHash_Success() {
        String hash = contentService.generateCacheKey(R_1_, TARGET_RIGHT);
        assertNotNull(hash);
        assertEquals(HASH_R1_RIGHT, hash);
    }

    @Test
    public void testFindContentCache_Success() {
        when(cache.getEntry(anyString())).thenReturn(content);
        Map.Entry<String, Optional<Content>> cacheHitPair = contentService.findContent(R_1_, TARGET_RIGHT);
        assertTrue(cacheHitPair.getValue().isPresent());
    }

    @Test
    public void testFindContentStorageSuccess() {
        when(cache.getEntry(anyString())).thenReturn(null);
        when(repository.findTopByRootIdAndTargetOrderByTimeDesc(anyString(), any(Target.class))).thenReturn(Optional.of(content));
        Map.Entry<String, Optional<Content>> dbHitPair = contentService.findContent(R_1_, TARGET_RIGHT);
        assertTrue(dbHitPair.getValue().isPresent());
    }

    @Test
    public void testSaveContent_Success() {
        when(cache.getEntry(anyString())).thenReturn(content);
        ContentResponseDto dto = contentService.save(GLOBAL_CONTENT_DTO, R_1_, TARGET_RIGHT);
        assertNotNull(dto);
        assertEquals(content.getData(), dto.getData());
        assertEquals(content.getRootId(), dto.getRootId());
        assertEquals(content.getTarget(), dto.getTarget());
    }

}
