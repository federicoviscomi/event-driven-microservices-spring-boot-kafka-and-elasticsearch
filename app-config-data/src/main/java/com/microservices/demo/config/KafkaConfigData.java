package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.StringJoiner;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {
    private String bootstrapServers;

    @Override
    public String toString() {
        return new StringJoiner("\n\t", KafkaConfigData.class.getSimpleName() + "[\n\t", "]")
                .add("bootstrapServers='" + bootstrapServers + "'")
                .add("schemaRegistryUrlKey='" + schemaRegistryUrlKey + "'")
                .add("schemaRegistryUrl='" + schemaRegistryUrl + "'")
                .add("topicName='" + topicName + "'")
                .add("topicNamesToCreate=" + topicNamesToCreate)
                .add("numOfPartitions=" + numOfPartitions)
                .add("replicationFactor=" + replicationFactor)
                .toString();
    }

    private String schemaRegistryUrlKey;
    private String schemaRegistryUrl;
    private String topicName;
    private List<String> topicNamesToCreate;
    private Integer numOfPartitions;
    private Short replicationFactor;
}
