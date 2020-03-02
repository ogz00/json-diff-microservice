package com.oguz.waes.scalableweb.entity.domain;

import com.oguz.waes.scalableweb.entity.enums.Target;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Document(collection = "content")
@Data
@CompoundIndex(useGeneratedName = true, def = "{'id': 1, 'target': 1, 'hash': 1}", unique = true)
@NoArgsConstructor
public class Content {
    @Id
    private String uuid;

    @Indexed
    @NotBlank(message = "id field couldn't be blank")
    private String rootId;

    @Field(name = "target")
    @NotBlank(message = "target field couldn't be blank")
    private Target target;

    @Field(name = "data")
    @NotBlank(message = "data field couldn't be blank")
    private String data;

    @Field(name = "hash")
    @NotBlank(message = "hash field couldn't be blank")
    private String hash;

    @Field(name = "version")
    @NotBlank(message = "version field couldn't be blank")
    private long time;

    public Content(Builder builder) {
        this.uuid = builder.uuid;
        this.rootId = builder.rootId;
        this.target = builder.target;
        this.data = builder.data;
        this.hash = builder.hash;
        this.time = new Date().getTime();
    }


    public static class Builder {
        String uuid;
        String rootId;
        Target target;
        String data;
        String hash;

        public Builder setUUID(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setRootId(String rootId) {
            this.rootId = rootId;
            return this;
        }

        public Builder setTarget(Target target) {
            this.target = target;
            return this;
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Builder setHash(String hash) {
            this.hash = hash;
            return this;
        }

        public Content build() {
            return new Content(this);
        }

    }

}
