package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClient {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);
    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData, AdminClient adminClient, RetryTemplate retryTemplate, WebClient webClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics() {
        try {
            CreateTopicsResult createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("reached max num of retry for kafka topic", t);
        }
        checkTopicsCreated();
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        Integer multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                int retry = retryCount++;
                if (retry > maxRetry) {
                    throw new KafkaClientException("reached max retry");
                }
                sleepTimeMs *= multiplier;
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException e) {
                    throw new KafkaClientException();
                }
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() {
        LOG.debug("checkSchemaRegistry+");
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        LOG.debug("maxRetry {}", maxRetry);
        int multiplier = retryConfigData.getMultiplier().intValue();
        LOG.debug("multiplier {}", multiplier);
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        LOG.debug("sleepTimeMs {}", sleepTimeMs);
        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            int retry = retryCount++;
            if (retry > maxRetry) {
                throw new KafkaClientException("reached max retry");
            }
            try {
                LOG.debug("sleeping {}", sleepTimeMs);
                Thread.sleep(sleepTimeMs);
            } catch (InterruptedException e) {
                throw new KafkaClientException();
            }
            sleepTimeMs *= multiplier;
        }
        LOG.debug("checkSchemaRegistry-");
    }

    private HttpStatus getSchemaRegistryStatus() {
        LOG.debug("getSchemaRegistryStatus+");
        HttpStatus status;
        try {
            status = webClient.
                    method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchange()
                    .map(ClientResponse::statusCode)
                    .block();
        } catch (Exception e) {
            LOG.debug("error", e);
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        LOG.debug("getSchemaRegistryStatus- {}", status);
        return status;
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics == null) {
            return false;
        }
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNamesToCreate = kafkaConfigData.getTopicNamesToCreate();
        LOG.info("Creating topics");
        LOG.info(topicNamesToCreate.toString());
        LOG.info("Attempt {}", retryContext.getRetryCount());
        List<NewTopic> kafkaTopics = topicNamesToCreate.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumOfPartitions(),
                kafkaConfigData.getReplicationFactor()
        )).collect(Collectors.toList());
        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics() {
        try {
            return retryTemplate.execute(this::doGetTopics);
        } catch (Throwable e) {
            throw new KafkaClientException("reached max num of retrys for reading kafka topics", e);
        }
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        LOG.info("Reading kafka topic {}, attempt {}", kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topicListings = adminClient.listTopics().listings().get();
        if (null != topicListings) {
            topicListings.forEach(topic -> LOG.debug("Topic with name {}", topic.name()));
        }
        return topicListings;
    }
}
