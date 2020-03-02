package com.oguz.waes.scalableweb.service.contentdiff;

import com.oguz.waes.scalableweb.dto.response.ContentsDiffResult;

public interface ContentDiffService {
    ContentsDiffResult findResultsOf(String id);
}
