package uk.nhs.hee.tis.revalidation.config;

import com.mongodb.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AxonEventStoreConfig {

    @Value("${app.reval.event.store.db}")
    private String eventStoreDB;

    @Value("${app.reval.event.store.collection}")
    private String eventStoreTable;

    @Bean
    public EventStorageEngine storageEngine(final MongoTemplate axonMongoTemplate) {
        return MongoEventStorageEngine.builder().mongoTemplate(axonMongoTemplate)
                .eventSerializer(JacksonSerializer.builder().build()).build();
    }

    @Bean
    public MongoTemplate axonMongoTemplate() {
        return DefaultMongoTemplate.builder()
                .mongoDatabase(new MongoClient(), eventStoreDB)
                .domainEventsCollectionName(eventStoreTable)
                .build();
    }
}
