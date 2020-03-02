package com.oguz.waes.scalableweb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Data
public class ContentDto {
    @JsonProperty("content")
    @NotBlank(message = "Content couldn't be blank")
    private String content;
}
