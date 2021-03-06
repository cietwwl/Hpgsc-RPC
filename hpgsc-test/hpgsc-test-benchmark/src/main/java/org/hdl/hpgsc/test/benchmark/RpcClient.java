/*
 * Copyright 1999-2011 Alibaba Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hdl.hpgsc.test.benchmark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * RpcClient.java
 * @author tony.chenl
 */
public class RpcClient extends AbstractClientRunnable{
    private static int length = 100;
    static DemoRequest request ;
    static{
        length = Integer.valueOf(System.getProperty("message.length","1024"));
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length;i++){
            sb.append("a");
        }
        request = new DemoRequest();
        request.setMessage(sb.toString());
    }

    /**
     * @param targetIP
     * @param targetPort
     * @param clientNums
     * @param rpcTimeout
     * @param barrier
     * @param latch
     * @param startTime
     * @param endTime
     */
    public RpcClient(String targetIP, int targetPort, int clientNums, int rpcTimeout, CyclicBarrier barrier,CountDownLatch latch, long startTime, long endTime){
        super(targetIP, targetPort, clientNums, rpcTimeout, barrier, latch, startTime, endTime);
    }

    @Override
    public Object invoke(ExchangeClientFactory factory) throws Exception {
    	DemoRequest result = factory.get(targetIP, targetPort, rpcTimeout, clientNums).syncRequest(101, request, DemoRequest.class);
        return result;
    }
}
