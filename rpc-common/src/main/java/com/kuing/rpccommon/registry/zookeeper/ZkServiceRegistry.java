package com.kuing.rpccommon.registry.zookeeper;

import com.kuing.rpccommon.beans.Constant;
import com.kuing.rpccommon.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class ZkServiceRegistry implements ServiceRegistry {

    private final CuratorFramework curatorFramework;

    public ZkServiceRegistry(String address) {
        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(address)
                .sessionTimeoutMs(Constant.ZK_SESSION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
    }

    @Override
    public void registry(String data) throws Exception {
        curatorFramework.start();

        String path = Constant.ZK_CHILDREN_PATH;
        curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(path, data.getBytes());

    }
}
