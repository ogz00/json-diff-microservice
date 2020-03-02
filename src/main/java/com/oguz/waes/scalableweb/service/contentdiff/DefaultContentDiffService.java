package com.oguz.waes.scalableweb.service.contentdiff;

import com.oguz.waes.scalableweb.cache.LRUCache;
import com.oguz.waes.scalableweb.dto.response.ContentsDiffResult;
import com.oguz.waes.scalableweb.dto.response.Detail;
import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.ComparisonResult;
import com.oguz.waes.scalableweb.entity.enums.Target;
import com.oguz.waes.scalableweb.exception.PrettyException;
import com.oguz.waes.scalableweb.service.content.ContentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class DefaultContentDiffService implements ContentDiffService {

    private final ContentService service;
    private final LRUCache<String, ContentsDiffResult> cache;

    @Autowired
    public DefaultContentDiffService(ContentService service, LRUCache<String, ContentsDiffResult> cache) {
        this.service = service;
        this.cache = cache;
    }


    /**
     * Generates results for current situation according to scenario and keep current situation at the cache.
     * In case of any updates happened at one of the child contents, this result will be evicted from the cache.
     * @param id given root id
     * @return ContentsDiffResult
     */
    public ContentsDiffResult findResultsOf(String id) {
        ContentsDiffResult cacheHit = cache.getEntry(id);
        if (cacheHit != null)
            return cacheHit;

        Map.Entry<String, Optional<Content>> rightContentPair = service.findContent(id, Target.RIGHT);
        Map.Entry<String, Optional<Content>> leftContentPair = service.findContent(id, Target.LEFT);

        if (rightContentPair.getValue().isEmpty() || leftContentPair.getValue().isEmpty())
            throw PrettyException.getInstance("Please create content for both directions first!")
                    .setStatus(HttpStatus.BAD_REQUEST);

        ContentsDiffResult result = calculateDiff(leftContentPair.getValue().get(), rightContentPair.getValue().get());
        cache.putEntry(id, result);
        return result;
    }

    /**
     * If equal returns @ComparisonResult.EQUAL
     * If not of equal size returns @ComparisonResult.SIZE_NOT_MATCH
     * If contents of children's are in same size but not equal as a binary string,
     * returns lists of offsets and length with @ComparisonResult.NOT_EQUAL
     * @param leftContent
     * @param rightContent
     * @return
     */
    private ContentsDiffResult calculateDiff(Content leftContent, Content rightContent) {
        ContentsDiffResult.Builder builder = new ContentsDiffResult.Builder().setId(leftContent.getRootId());
        if (leftContent.getHash().equals(rightContent.getHash()))
            return builder
                    .setResult(ComparisonResult.EQUAL)
                    .build();
        else if (leftContent.getData().length() != rightContent.getData().length()) {
            return builder
                    .setResult(ComparisonResult.SIZE_NOT_MATCH)
                    .build();
        } else {
            return builder.setResult(ComparisonResult.NOT_EQUAL)
                    .setDetails(generateDetails(leftContent.getData(), rightContent.getData()))
                    .build();
        }
    }

    private List<Detail> generateDetails(String left, String right) {
        StringCharacterIterator leftIterator = new StringCharacterIterator(left);
        StringCharacterIterator rightIterator = new StringCharacterIterator(right);
        List<Detail> details = new ArrayList<>();
        Detail detail = new Detail();
        while (leftIterator.current() != CharacterIterator.DONE) {
            while (leftIterator.next() != rightIterator.next() && leftIterator.current() != CharacterIterator.DONE)
                detail.setLength(detail.getLength() + 1);
            if (detail.getLength() > 0) {
                detail.setOffset(leftIterator.getIndex() - detail.getLength());
                details.add(detail);
                detail = new Detail();
            }
        }
        return details;

    }
}
