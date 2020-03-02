package com.oguz.waes.scalableweb.dto.response;

import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.Target;
import lombok.Data;

@Data
public class ContentResponseDto {

    private String uuid;
    private String rootId;
    private Target target;
    private String data;

    private ContentResponseDto() {

    }

    public static ContentResponseDto of(Content content) {
        ContentResponseDto dto = new ContentResponseDto();
        dto.data = (content.getData());
        dto.uuid = (content.getUuid());
        dto.rootId = (content.getRootId());
        dto.target = (content.getTarget());
        return dto;
    }

}
