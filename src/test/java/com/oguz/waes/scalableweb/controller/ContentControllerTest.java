package com.oguz.waes.scalableweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oguz.waes.scalableweb.dto.request.ContentDto;
import com.oguz.waes.scalableweb.dto.response.ContentResponseDto;
import com.oguz.waes.scalableweb.dto.response.ContentsDiffResult;
import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.ComparisonResult;
import com.oguz.waes.scalableweb.entity.enums.Target;
import com.oguz.waes.scalableweb.service.content.ContentService;
import com.oguz.waes.scalableweb.service.contentdiff.ContentDiffService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.oguz.waes.scalableweb.Constants.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ContentController.class)
public class ContentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ContentService contentService;
    @MockBean
    ContentDiffService contentDiffService;

    private ContentResponseDto responseDtoLeft;
    private ContentResponseDto responseDtoRight;

    private ContentDto contentDto;
    private ContentDto contentDtoInvalid;
    private ContentsDiffResult equalResult;

    @Before
    public void before() {
        JacksonTester.initFields(this, new ObjectMapper());
        Content content = mock(Content.class);
        when(content.getData()).thenReturn(TEST_DATA);
        when(content.getUuid()).thenReturn("uuid");
        when(content.getRootId()).thenReturn(R_1_);
        when(content.getTarget()).thenReturn(Target.LEFT);
        responseDtoLeft = ContentResponseDto.of(content);
        when(content.getTarget()).thenReturn(Target.RIGHT);
        when(content.getData()).thenReturn(TEST_DATA_2);
        responseDtoRight = ContentResponseDto.of(content);

        contentDto = new ContentDto();
        contentDtoInvalid = new ContentDto();
        contentDto.setContent(TEST_DATA);
        contentDtoInvalid.setContent("");

        equalResult = new ContentsDiffResult();
        equalResult.setComparisonResult(ComparisonResult.EQUAL);
    }

    @Test
    public void addLeftContent_Success() throws Exception {
        given(contentService.save(any(ContentDto.class), anyString(), any(Target.class))).willReturn(responseDtoLeft);
        mockMvc.perform(post("/diff/{id}/left", R_1_)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(contentDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid", is(responseDtoLeft.getUuid())))
                .andExpect(jsonPath("$.data", is(responseDtoLeft.getData())))
                .andExpect(jsonPath("$.rootId", is(responseDtoLeft.getRootId())))
                .andExpect(jsonPath("$.target", is(responseDtoLeft.getTarget().name())));

    }

    @Test
    public void addRightContent_Success() throws Exception {
        given(contentService.save(any(ContentDto.class), anyString(), any(Target.class))).willReturn(responseDtoRight);
        mockMvc.perform(post("/diff/{id}/right", R_1_)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(contentDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.target", is(responseDtoRight.getTarget().toString())))
                .andExpect(jsonPath("$.data", is(responseDtoRight.getData())));

    }

    @Test
    public void addLeftContent_Invalid() throws Exception {
        mockMvc.perform(post("/diff/{id}/left", R_1_)
                .content(convertObjectToJsonBytes(contentDtoInvalid))
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getDiffRequestEqual_Success() throws Exception {
        given(contentDiffService.findResultsOf(anyString())).willReturn(equalResult);
        mockMvc.perform(post("/diff/{id}", R_1_)
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comparisonResult", is(ComparisonResult.EQUAL.name())));
    }
}
