package com.flipkart.rabbitmqspoutexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.latent.storm.rabbitmq.TupleToMessage;
import io.latent.storm.rabbitmq.config.ConnectionConfig;
import io.latent.storm.rabbitmq.config.ProducerConfig;
import io.latent.storm.rabbitmq.config.ProducerConfigBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import backtype.storm.Config;
import backtype.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Utils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private static Map<String, Object> rabbitMQConfig = new HashMap<>();

    private static String rabbitExchangeName = "amq.direct";
    static {
        rabbitMQConfig.put("rabbitmq.host", "10.33.65.22");
        rabbitMQConfig.put("rabbitmq.heartbeat", 60);
        rabbitMQConfig.put("rabbitmq.ssl", false);
        rabbitMQConfig.put("rabbitmq.ha.hosts", "10.33.65.22:5672|10.32.189.165:5672" );
    }

    public static ConnectionConfig getRabbitMQConnectionConfig(Map<String, Object> rabbitMQConfig){
        return ConnectionConfig.getFromStormConfig(rabbitMQConfig);
    }

    private static ProducerConfig getRabbitMQSinkConfig(ConnectionConfig connectionConfig, String rabbitExchangeName){
        return new ProducerConfigBuilder().connection(connectionConfig)
                .contentEncoding("UTF-8")
                .exchange(rabbitExchangeName)
                .routingKey("")
                .build();
    }

    public static TupleToMessage getTupleToMessage(final String exchangeName){
        return new TupleToMessage() {

            @Override
            protected byte[] extractBody(Tuple input) {
                ContentMetadata contentMetadata = (ContentMetadata)input.getValueByField("contentMetadata");
                String jsonStr;
                try{
                    jsonStr = objectMapper.writeValueAsString(contentMetadata);
                }catch(Exception e){
                    log.error("Error serializing contentMetadata:", e);
                    jsonStr = "";
                }
                return jsonStr.getBytes();
            }

            @Override
            protected String determineExchangeName(Tuple tuple) {
                return exchangeName;
            }

            @Override
            protected Integer extractPriority(Tuple input) {
                ContentMetadata contentMetadata = (ContentMetadata)input.getValueByField("contentMetadata");
                return Utils.getPriority(contentMetadata);

            }
        };
    }

    public static Config getStormConfiguration() {
        Integer messageTimeoutInSecs = 100;
        Integer numWorkers = 2;
        Integer numAckers = 2;

        Config config = new Config();
        config.setNumAckers(numAckers);
        config.setNumWorkers(numWorkers);
        config.setMessageTimeoutSecs(messageTimeoutInSecs);
        return config;
    }

    public static Integer getPriority(ContentMetadata contentMetadata) {
        if ("STORES".equals(contentMetadata.getSubViewType()) && SherlockContentType.BESTSELLERS.equals(contentMetadata.getSherlockContentType())) {
            return 1;
        } else if ("STORES".equals(contentMetadata.getSubViewType()) && SherlockContentType.DISCOUNTS.equals(contentMetadata.getSherlockContentType())) {
            return 2;
        } else if ("STORES".equals(contentMetadata.getSubViewType()) && SherlockContentType.OFFERS.equals(contentMetadata.getSherlockContentType())) {
            return 3;
        }
        return 0;
    }
}
