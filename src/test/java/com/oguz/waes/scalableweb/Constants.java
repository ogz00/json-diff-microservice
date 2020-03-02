package com.oguz.waes.scalableweb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oguz.waes.scalableweb.dto.request.ContentDto;
import com.oguz.waes.scalableweb.entity.enums.Target;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Constants {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    public static final String R_1_ = "1";
    public static final String R_2 = "2";
    public static final String TEST_DATA = "test_data=";
    public static final String TEST_DATA_2 = "test_data_2=";
    public static final String HASH_R1_RIGHT = "F4527BE18B7B4D451FFE899836DD44B8";
    public static final String HASH_R1_LEFT = "2F03A0B6A4A1054F8BF75A65B48438A7";
    public static final String HASH_CONTENT = "25F1B04EBFBA3198491A737172F48214";
    public static final Target TARGET_RIGHT = Target.RIGHT;
    public static final Target TARGET_LEFT = Target.LEFT;
    public static final ContentDto GLOBAL_CONTENT_DTO = new ContentDto();



    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
