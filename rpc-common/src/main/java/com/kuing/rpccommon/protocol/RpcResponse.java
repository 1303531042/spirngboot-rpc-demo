package com.kuing.rpccommon.protocol;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RpcResponse {
    /**
     * 响应ID
     */
    private String responseId;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 返回结果
     */
    private Object result;

}
