package com.distributedsystems.consensus;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClusterNode represents a single physical server in our cluster.
 * It tracks its own identity and its current role in the cluster.
 */
public class ClusterNode {
    public enum NodeStatus { LEADER, FOLLOWER, DEAD }

    private final int id;
    private NodeStatus status;
    private final AtomicBoolean isAlive = new AtomicBoolean(true);

    public ClusterNode(int id) {
        this.id = id;
        this.status = NodeStatus.FOLLOWER;
    }

    public int getId() {
        return id;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public boolean isAlive() {
        return isAlive.get();
    }

    public void stop() {
        isAlive.set(false);
        this.status = NodeStatus.DEAD;
    }

    public void start() {
        isAlive.set(true);
        this.status = NodeStatus.FOLLOWER;
    }

    @Override
    public String toString() {
        return "Node-" + id + " [" + status + "]";
    }
}
