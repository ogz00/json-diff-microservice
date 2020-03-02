package com.oguz.waes.scalableweb.service.content;

import com.oguz.waes.scalableweb.dto.request.ContentDto;
import com.oguz.waes.scalableweb.dto.response.ContentResponseDto;
import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.Target;

import java.util.Map;
import java.util.Optional;

public interface ContentService {

    ContentResponseDto save(ContentDto dto, String id, Target target);

    Map.Entry<String, Optional<Content>> findContent(String id, Target target);

    String generateCacheKey(String id, Target target);
}
