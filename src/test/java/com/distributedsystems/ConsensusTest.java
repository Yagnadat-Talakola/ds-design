package com.distributedsystems;

import com.distributedsystems.consensus.ClusterManager;
import com.distributedsystems.consensus.ClusterNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConsensusTest {
    @Test
    public void testLeaderElectionHighestId() {
        ClusterManager manager = new ClusterManager();
        ClusterNode n1 = new ClusterNode(1);
        ClusterNode n2 = new ClusterNode(10);
        ClusterNode n3 = new ClusterNode(5);

        manager.addNode(n1);
        manager.addNode(n2);
        manager.addNode(n3);

        // Highest ID (10) should be leader
        assertEquals(ClusterNode.NodeStatus.LEADER, n2.getStatus());
        assertEquals(ClusterNode.NodeStatus.FOLLOWER, n1.getStatus());
        assertEquals(ClusterNode.NodeStatus.FOLLOWER, n3.getStatus());
    }

    @Test
    public void testFailover() {
        ClusterManager manager = new ClusterManager();
        ClusterNode n1 = new ClusterNode(1);
        ClusterNode n2 = new ClusterNode(10);

        manager.addNode(n1);
        manager.addNode(n2);

        // Kill current leader (n2)
        manager.onNodeFailure(10);

        // n1 should now be leader
        assertEquals(ClusterNode.NodeStatus.LEADER, n1.getStatus());
    }
}
