package com.oguz.waes.scalableweb.controller;

import com.oguz.waes.scalableweb.dto.request.ContentDto;
import com.oguz.waes.scalableweb.dto.response.ContentResponseDto;
import com.oguz.waes.scalableweb.dto.response.ContentsDiffResult;
import com.oguz.waes.scalableweb.entity.enums.Target;
import com.oguz.waes.scalableweb.exception.PrettyException;
import com.oguz.waes.scalableweb.service.content.ContentService;
import com.oguz.waes.scalableweb.service.contentdiff.ContentDiffService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/diff", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class ContentController {

    @Qualifier("defaultContentService")
    private final ContentService contentService;
    private final ContentDiffService diffService;

    @Autowired
    public ContentController(ContentService service, ContentDiffService diffService) {
        this.contentService = service;
        this.diffService = diffService;
    }

    @PostMapping(value = "/{id}/left", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentResponseDto> addLeftContent(@PathVariable("id") String rootId,
                                                             @Valid @RequestBody ContentDto dto) throws PrettyException {
        log.info(String.format("Left content of %s updating..", rootId));
        return new ResponseEntity<>(contentService.save(dto, rootId, Target.LEFT), HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/right", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentResponseDto> addRightContent(@PathVariable("id") String rootId,
                                                              @Valid @RequestBody ContentDto dto) throws PrettyException {
        log.info(String.format("Right content of %s updating..", rootId));
        return new ResponseEntity<>(contentService.save(dto, rootId, Target.RIGHT), HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentsDiffResult> diff(@PathVariable("id") String rootId) throws PrettyException {
        log.info(String.format("Differences between the contents of %s calculating...", rootId));
        return new ResponseEntity<>(diffService.findResultsOf(rootId), HttpStatus.OK);
    }
}
