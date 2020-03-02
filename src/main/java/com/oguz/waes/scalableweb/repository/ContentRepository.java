package com.oguz.waes.scalableweb.repository;

import com.oguz.waes.scalableweb.entity.domain.Content;
import com.oguz.waes.scalableweb.entity.enums.Target;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {
    Optional<Content> findTopByRootIdAndTargetOrderByTimeDesc(@NotBlank String id, Target target);
}
