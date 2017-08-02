package com.flipkart.rabbitmqspoutexample;


import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class SimpleSpout extends BaseRichSpout{

    SpoutOutputCollector outputCollector;
    ContentMetadata contentMetadata = new ContentMetadata("STORES", "s1", "pc1", SherlockContentType.BESTSELLERS);

    public SimpleSpout(){

    }

    public void open(Map var1, TopologyContext var2, SpoutOutputCollector outputCollector){
        this.outputCollector = outputCollector;
    }

    public void nextTuple(){
        for(int i=0;i<100;i++){
            outputCollector.emit(new Values(contentMetadata));
        }
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer){
        declarer.declare(new Fields("contentMetadata"));
    }

}
