package com.oguz.waes.scalableweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.oguz.waes.scalableweb.repository")
public class MongoDBConfig {
}
