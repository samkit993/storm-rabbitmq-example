package com.flipkart.rabbitmqspoutexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;


import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;


public class SimpleBolt extends BaseRichBolt {

    private ObjectMapper objectMapper = new ObjectMapper();

    public SimpleBolt(){

    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector){

    }

    @Override
    public void execute(Tuple input){
        try{
            System.out.println(objectMapper.writeValueAsString(input.getValueByField("contentMetadata")));
        }catch(Exception e){
            System.err.println("Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer var1){

    }
}
