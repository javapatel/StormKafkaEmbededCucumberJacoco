package com.rest.api;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;

public class LocalTopologySubmitter {
    public void submitTopology(String name, Config config, StormTopology topology) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(name, config, topology);
    }

    public void killTopology(String topologyName) {
        LocalCluster localCluster = new LocalCluster();
        localCluster.killTopology(topologyName);
    }
}
