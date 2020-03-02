package com.oguz.waes.scalableweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oguz.waes.scalableweb.entity.enums.ComparisonResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Serializable class for hold the desired comparison result
 */
@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ContentsDiffResult {

    /**
     * The desired diff Id for convenient content
     */
    @JsonProperty("id")
    private String id;

    /**
     * Current Status of comparison
     */
    @JsonProperty("comparisonResult")
    private ComparisonResult comparisonResult;

    /**
     * Comparison details for current contents
     */
    @JsonProperty("details")
    private List<Detail> details;

    private ContentsDiffResult(Builder builder) {
        this.id = builder.id;
        this.comparisonResult = builder.result;
        this.details = builder.details;
    }

    public static class Builder {
        String id;
        ComparisonResult result;
        List<Detail> details;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setResult(ComparisonResult result) {
            this.result = result;
            return this;
        }

        public Builder setDetails(List<Detail> details) {
            this.details = details;
            return this;
        }

        public ContentsDiffResult build() {
            return new ContentsDiffResult(this);
        }
    }
}
