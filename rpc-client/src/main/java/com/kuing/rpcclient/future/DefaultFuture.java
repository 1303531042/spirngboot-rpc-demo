package com.kuing.rpcclient.future;

import com.kuing.rpccommon.protocol.RpcResponse;

public class DefaultFuture {
    private RpcResponse rpcResponse;
    private volatile boolean isSucceed = false;
    private final Object object = new Object();

    public RpcResponse getRpcResponse(int timout) {
        synchronized (object) {
            while (!isSucceed) {
                try {
                    object.wait(timout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return rpcResponse;
        }
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        if (isSucceed) {
            return;
        }
        synchronized (object) {
            this.rpcResponse = rpcResponse;
            this.isSucceed = true;
            object.notify();

        }
    }
}
