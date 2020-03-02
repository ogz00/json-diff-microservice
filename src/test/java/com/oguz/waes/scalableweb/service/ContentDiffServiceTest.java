package com.oguz.waes.scalableweb.service;

import com.oguz.waes.scalableweb.cache.LRUCache;
import com.oguz.waes.scalableweb.dto.response.ContentsDiffResult;
import com.oguz.waes.scalableweb.dto.response.Detail;
import com.oguz.waes.scalableweb.entity.Pair;
import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.ComparisonResult;
import com.oguz.waes.scalableweb.entity.enums.Target;
import com.oguz.waes.scalableweb.exception.PrettyException;
import com.oguz.waes.scalableweb.service.content.ContentService;
import com.oguz.waes.scalableweb.service.contentdiff.DefaultContentDiffService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.oguz.waes.scalableweb.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ContentDiffServiceTest {

    @Mock
    private ContentService contentService;

    @Mock
    private LRUCache<String, Content> cache;

    @InjectMocks
    private DefaultContentDiffService diffService;

    private Content rightContent;
    private Content leftContent;

    private List<Detail> details = new ArrayList<>();

    @Before
    public void before() {
        rightContent = mock(Content.class);
        leftContent = mock(Content.class);
        when(rightContent.getRootId()).thenReturn(R_1_);
        when(leftContent.getRootId()).thenReturn(R_1_);
        when(cache.getEntry(anyString())).thenReturn(null);

        Map.Entry<String, Optional<Content>> rightContentPair = Pair.of(HASH_R1_RIGHT, Optional.of(rightContent));
        Map.Entry<String, Optional<Content>> leftContentPair = Pair.of(HASH_R1_LEFT, Optional.of(leftContent));

        when(contentService.findContent(R_1_, Target.RIGHT)).thenReturn(rightContentPair);
        when(contentService.findContent(R_1_, Target.LEFT)).thenReturn(leftContentPair);

        when(rightContent.getHash()).thenReturn(HASH_CONTENT);
        Detail detail = new Detail();
        detail.setLength(1);
        detail.setOffset(10);
        details.add(detail);
    }

    @Test
    public void testCalculateDiffForEmptyDirection_Failed() {
        when(contentService.findContent(R_1_, Target.RIGHT)).thenReturn(
                Pair.of(HASH_R1_RIGHT, Optional.empty())
        );
        assertThrows(PrettyException.class, () -> diffService.findResultsOf(R_1_));
    }

    @Test
    public void testCalculateDiffEqual_Success() {
        when(leftContent.getHash()).thenReturn(HASH_CONTENT);
        ContentsDiffResult result = diffService.findResultsOf(R_1_);
        assertNotNull(result);
        assertEquals(result.getComparisonResult(), ComparisonResult.EQUAL);
        assertEquals(result.getId(), leftContent.getRootId());
    }

    @Test
    public void testCalculateDiffSizeNotMatch_Success() {
        when(leftContent.getHash()).thenReturn(HASH_CONTENT + "A");
        when(leftContent.getData()).thenReturn(TEST_DATA);
        when(rightContent.getData()).thenReturn(TEST_DATA_2);

        ContentsDiffResult result = diffService.findResultsOf(R_1_);
        assertNotNull(result);
        assertEquals(result.getComparisonResult(), ComparisonResult.SIZE_NOT_MATCH);
        assertEquals(result.getId(), leftContent.getRootId());
        assertEquals(result.getId(), rightContent.getRootId());
    }

    @Test
    public void testCalculateDiffNotEqual_Success() {
        when(leftContent.getHash()).thenReturn(HASH_CONTENT + "A");
        when(rightContent.getHash()).thenReturn(HASH_CONTENT + "B");
        when(leftContent.getData()).thenReturn(TEST_DATA + "1");
        when(rightContent.getData()).thenReturn(TEST_DATA + "2");

        ContentsDiffResult result = diffService.findResultsOf(R_1_);
        assertNotNull(result);
        assertEquals(result.getComparisonResult(), ComparisonResult.NOT_EQUAL);
        assertEquals(result.getDetails(), details);
    }
}
