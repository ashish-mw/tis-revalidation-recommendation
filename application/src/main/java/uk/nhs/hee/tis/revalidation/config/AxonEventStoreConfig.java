package uk.nhs.hee.tis.revalidation.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.mongodb.MongoClientOptions.builder;
import static com.mongodb.MongoCredential.createCredential;

@Slf4j
@Configuration
public class AxonEventStoreConfig {

    @Value("${app.reval.event.store.db}")
    private String eventStoreDB;

    @Value("${app.reval.event.store.collection}")
    private String eventStoreTable;

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.username}")
    private String userName;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.data.mongodb.authentication-database}")
    private String authenticationDatabase;

    @Bean
    public EventStorageEngine storageEngine(final MongoTemplate axonMongoTemplate) {
        return MongoEventStorageEngine.builder().mongoTemplate(axonMongoTemplate)
                .eventSerializer(JacksonSerializer.builder().build()).build();
    }

    @Bean
    public MongoTemplate axonMongoTemplate(final MongoClient mongoClient) {
        return DefaultMongoTemplate.builder()
                .mongoDatabase(mongoClient, eventStoreDB)
                .domainEventsCollectionName(eventStoreTable)
                .build();
    }

    @Bean
    public MongoClient mongoClient(final MongoCredential mongoCredential) {
        return new MongoClient(new ServerAddress(host, port), mongoCredential, builder().build());
    }

    @Bean
    public MongoCredential mongoCredential() {
        return createCredential(userName, authenticationDatabase, password.toCharArray());
    }
}
