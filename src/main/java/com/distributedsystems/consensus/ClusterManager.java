package com.distributedsystems.consensus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * ClusterManager handles the membership and leader election logic.
 * It follows a simplified Bully Algorithm: The alive node with the 
 * highest ID becomes the leader.
 */
public class ClusterManager {
    private final List<ClusterNode> nodes = new ArrayList<>();

    public void addNode(ClusterNode node) {
        nodes.add(node);
        electLeader(); // Re-elect whenever membership changes
    }

    /**
     * The Election Logic:
     * 1. Filter only alive nodes.
     * 2. Find the one with the maximum ID.
     * 3. Promote it to LEADER and demote others to FOLLOWER.
     */
    public void electLeader() {
        Optional<ClusterNode> highestAliveNode = nodes.stream()
                .filter(ClusterNode::isAlive)
                .max(Comparator.comparingInt(ClusterNode::getId));

        if (highestAliveNode.isPresent()) {
            ClusterNode leader = highestAliveNode.get();
            
            // Set all other nodes to followers
            for (ClusterNode node : nodes) {
                if (node.equals(leader)) {
                    node.setStatus(ClusterNode.NodeStatus.LEADER);
                } else if (node.isAlive()) {
                    node.setStatus(ClusterNode.NodeStatus.FOLLOWER);
                }
            }
            System.out.println("ELECTION COMPLETED: New Leader is " + leader);
        } else {
            System.out.println("ELECTION FAILED: No alive nodes in cluster.");
        }
    }

    public void onNodeFailure(int id) {
        nodes.stream()
             .filter(n -> n.getId() == id)
             .findFirst()
             .ifPresent(node -> {
                 node.stop();
                 System.out.println("CRITICAL: " + node + " has failed!");
                 electLeader(); // Trigger immediate election
             });
    }

    public List<ClusterNode> getNodes() {
        return nodes;
    }
}
