package com.flipkart.rabbitmqspoutexample;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

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
