package com.flipkart.rabbitmqspoutexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.latent.storm.rabbitmq.RabbitMQSpout;
import io.latent.storm.rabbitmq.config.ConnectionConfig;
import io.latent.storm.rabbitmq.config.ConsumerConfig;
import io.latent.storm.rabbitmq.config.ConsumerConfigBuilder;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.Scheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.List;

public class DequeueTopology {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args){
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        Scheme scheme = new Scheme() {
            @Override
            public List<Object> deserialize(byte[] bytes) {
                String jsonStr = new String(bytes);
                ContentMetadata contentMetadata = null;
                try {
                    contentMetadata = objectMapper.readValue(jsonStr, ContentMetadata.class);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                return new Values(contentMetadata);
            }

            @Override
            public Fields getOutputFields() {
                return new Fields("contentMetadata");
            }
        };
        ConnectionConfig connectionConfig = ConnectionConfig.getFromStormConfig((Utils.getRabbitMQConfig()));
        ConsumerConfig consumerConfig = new ConsumerConfigBuilder().connection(connectionConfig)
                                                                   .prefetch(60)
                                                                   .queue("queue2")
                                                                   .build();
        UrlQueueDeclarator urlQueueDeclarator = new UrlQueueDeclarator("exchange1", "queue2");
        RabbitMQSpout rabbitMQSpout = new RabbitMQSpout(scheme, urlQueueDeclarator);
        SimpleBolt simpleBolt = new SimpleBolt();
        topologyBuilder.setSpout("rabbitMQSpout", rabbitMQSpout).addConfigurations(consumerConfig.asMap());
        topologyBuilder.setBolt("simpleBolt", simpleBolt).shuffleGrouping("rabbitMQSpout");
        StormTopology topology = topologyBuilder.createTopology();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("DequeueTopology", Utils.getStormConfiguration(), topology);
        System.out.println("Submitted storm topology to local cluster successfully.");
    }
}
