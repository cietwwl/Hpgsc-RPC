package org.hdl.hpgsc.test.benchmark;

/**
 * nfs-rpc Apache License http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import org.hdl.hggsc.rpc.client.HpgscClient;
import org.hdl.hggsc.rpc.client.IClient;

/**
 * Abstract ExchangeClient Factory,create custom nums ExchangeClient
 * 
 */
public class ExchangeClientFactory {

    // Cache ExchangeClient
    private static ConcurrentHashMap<String, FutureTask<List<IClient>>> clients = new ConcurrentHashMap<String, FutureTask<List<IClient>>>();

    public IClient get(final String targetIP, final int targetPort, final int connectTimeout) throws Exception {
        return get(targetIP, targetPort, connectTimeout, 1);
    }

    public IClient get(final String targetIP, final int targetPort, final int connectTimeout,final int clientNums) throws Exception {
        String key = targetIP + ":" + targetPort;
        if (clients.containsKey(key)) {
            if (clientNums == 1) {
                return clients.get(key).get().get(0);
            } else {
                Random random = new Random();
                return clients.get(key).get().get(random.nextInt(clientNums));
            }
        } else {
            FutureTask<List<IClient>> task = new FutureTask<List<IClient>>(
                                                                                         new Callable<List<IClient>>() {

                                                                                             public List<IClient> call()
                                                                                                                               throws Exception {
                                                                                                 List<IClient> clients = new ArrayList<IClient>(
                                                                                                                                                              clientNums);
                                                                                                 for (int i = 0; i < clientNums; i++) {
                                                                                                     clients.add(createClient(targetIP,
                                                                                                                              targetPort,
                                                                                                                              connectTimeout));
                                                                                                 }
                                                                                                 return clients;
                                                                                             }
                                                                                         });
            FutureTask<List<IClient>> currentTask = clients.putIfAbsent(key, task);
            if (currentTask == null) {
                task.run();
            } else {
                task = currentTask;
            }
            if (clientNums == 1) return task.get().get(0);
            else {
                Random random = new Random();
                return task.get().get(random.nextInt(clientNums));
            }
        }
    }

    public void removeClient(String key, IClient ExchangeClient) {
        try {
            clients.remove(key);
        } catch (Exception e) {
        }
    }

    public static ExchangeClientFactory getInstance() {
        throw new UnsupportedOperationException("should be implemented by true class");
    }

    protected IClient createClient(String targetIP, int targetPort, int connectTimeout) throws Exception {
    	 HpgscClient client = HpgscClient.build("benchmark-client", targetIP, targetPort, connectTimeout);
         return client;
    }

}
