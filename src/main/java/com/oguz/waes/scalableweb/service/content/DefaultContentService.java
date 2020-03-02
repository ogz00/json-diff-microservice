package com.oguz.waes.scalableweb.service.content;

import com.oguz.waes.scalableweb.cache.LRUCache;
import com.oguz.waes.scalableweb.dto.request.ContentDto;
import com.oguz.waes.scalableweb.dto.response.ContentResponseDto;
import com.oguz.waes.scalableweb.entity.Pair;
import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.Target;
import com.oguz.waes.scalableweb.repository.ContentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class DefaultContentService implements ContentService {

    /**
     * According to my assumption this system will have write heavy usage. And because of the heavy writes, system
     * must keep history of write requests according to their timestamp. All of implementation had made for
     * this two requirements.
     */

    private final ContentRepository repository;
    private final LRUCache<String, Content> cache;

    @Autowired
    public DefaultContentService(ContentRepository repository, LRUCache<String, Content> cache) {
        this.repository = repository;
        this.cache = cache;
    }

    /**
     * Save function for requested diff content
     * If requested id exists at the system find last version of that content and update it's data iteratively.
     * Else create new content with desired id and persist this value for both lruCache and MongoDB.
     * With this approach I try to avoid from update process which is the costly operation for MongoDB.
     * And implement cache mechanism for avoid from the database request for repeatedly using content.
     * <p>
     * Also with this implementation system could keep history of add requests.
     *
     * @param dto    user body payload
     * @param id     desired id extracted from url path
     * @param target left or right content of document
     * @return user related form of created content
     */
    @Override
    public ContentResponseDto save(ContentDto dto, String id, Target target) {
        Content.Builder contentBuilder = new Content.Builder()
                .setUUID(generateRandomUUID())
                .setTarget(target)
                .setRootId(id);

        Map.Entry<String, Optional<Content>> optContentPair = findContent(id, target);

        String updatedData = concat(
                new String[]{optContentPair.getValue().map(Content::getData).orElse(""), dto.getContent()});

        Content created = contentBuilder
                .setData(updatedData)
                .setHash(getHash(updatedData))
                .build();

        created = repository.save(created);
        cache.putEntry(optContentPair.getKey(), created);
        cache.evict(id);

        return ContentResponseDto.of(created);

    }

    public String generateCacheKey(String id, Target target) {
        return getHash(id + target);
    }

    /**
     * If last updated content is exists at the cache return this content,
     * else look at persistence db and if exists update the cache and return content.
     *
     * @param id     desired id
     * @param target target direction
     * @return Pair<String, Optional < Content>> hash value for cache key and founded content
     */
    public Map.Entry<String, Optional<Content>> findContent(String id, Target target) {
        String hash = generateCacheKey(id, target);
        Optional<Content> cacheHit = findContentOnCache(hash);
        return cacheHit.isPresent() ? Pair.of(hash, cacheHit) : Pair.of(hash, findContentOnStorage(id, target, hash));
    }

    private Optional<Content> findContentOnCache(String hash) {
        Content cacheHit = cache.getEntry(hash);
        return Optional.ofNullable(cacheHit);
    }

    private Optional<Content> findContentOnStorage(String id, Target target, String hash) {
        Optional<Content> persist = repository.findTopByRootIdAndTargetOrderByTimeDesc(id, target);
        persist.ifPresent(content -> cache.putEntry(hash, content));
        return persist;
    }


    private String concat(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String str : array)
            sb.append(str);
        return sb.toString();
    }

    private String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate hash for given string.
     * This function might move to static utils class, but because of the this is just a concept approach
     * I didn't prefer the create new class for just one helper method.
     *
     * @param string data for hashing
     * @return hash function result
     */
    private String getHash(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(string.getBytes());
            byte[] digest = md.digest();
            return String.format("%032x", new BigInteger(1, digest)).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("e");
        }
    }
}
