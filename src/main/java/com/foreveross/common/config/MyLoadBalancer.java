/*******************************************************************************
 * Copyright (c) Oct 23, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.iff.infra.util.Assert;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.Logger;
import org.iff.infra.util.SocketHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 自己实现了一个RoundRobin负载均衡器，可以自动failover。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Oct 23, 2017
 */
public class MyLoadBalancer<T extends Server> extends DynamicServerListLoadBalancer<T> {

    private LoadBalancerRoundRobin loadBalancerRoundRobin;

    public MyLoadBalancer() {
        super();
    }

    public MyLoadBalancer(IClientConfig clientConfig, IRule rule, IPing ping, ServerList<T> serverList,
                          ServerListFilter<T> filter, ServerListUpdater serverListUpdater) {
        super(clientConfig, rule, ping, serverList, filter, serverListUpdater);
    }

    public MyLoadBalancer(IClientConfig niwsClientConfig) {
        super(niwsClientConfig);
    }

    @Override
    public synchronized void setServersList(List lsrv) {
        Assert.notEmpty(lsrv);
        super.setServersList(lsrv);
        if (loadBalancerRoundRobin == null) {
            loadBalancerRoundRobin = new LoadBalancerRoundRobin(getServerList(false).toArray(new Server[0]));
        }
    }

    @Override
    public Server chooseServer(Object key) {
        Server server = loadBalancerRoundRobin.next();
        ServerStats stats = getLoadBalancerStats().getServerStats().get(server);
        int failureCount = stats.getSuccessiveConnectionFailureCount();
        if (failureCount > 0) {
            loadBalancerRoundRobin.disable(server);
            return loadBalancerRoundRobin.next();
        }
        return server;
    }

    /**
     * implement a round robin load balancer.
     *
     * @author zhaochen
     */
    class LoadBalancerRoundRobin implements Iterator<Server> {
        private Object[][] servers;
        private int currentIndex = 0;
        private long lastPrintLogTime = System.currentTimeMillis();

        LoadBalancerRoundRobin(Server[] servers) {
            Assert.notEmpty(servers, "servers url can't be empty!");
            this.servers = new Object[servers.length][];
            for (int i = 0; i < servers.length; i++) {
                this.servers[i] = new Object[]{ //
                        servers[i]/*0: Server            */, //
                        false/*     1: available      */, //
                        0L/*        2: last test time */, //
                        0/*         3: service times  */, //
                        0/*         4: fail times     */, //
                        0/*         5: recover times  *///
                };
            }
            currentIndex = Math.max(0, new Random().nextInt(this.servers.length));
        }

        void disable(Server server) {
            for (int i = 0; i < servers.length; i++) {
                if (((Server) servers[i][0]).getId().equals(server.getId())) {
                    servers[i][1] = false;
                    servers[i][4] = (int) servers[i][4] + 1;
                }
            }
        }

        void test(Server server) {
            Object[] ual = null;
            for (int i = 0; i < servers.length; i++) {
                if (((Server) servers[i][0]).getId().equals(server.getId())) {
                    ual = servers[i];
                }
            }
            if (ual == null) {
                return;
            }
            try {
                if (System.currentTimeMillis() - (long) ual[2] < 5000) {
                    return;
                }
                ual[1] = SocketHelper.test(server.getHost(), server.getPort());
                ual[2] = System.currentTimeMillis();
                ual[5] = (int) ual[5] + 1;
                if ((boolean) ual[1]) {
                    getLoadBalancerStats().getServerStats().get(server).clearSuccessiveConnectionFailureCount();
                }
            } catch (Exception e) {
            }
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public synchronized Server next() {
            if (System.currentTimeMillis() - lastPrintLogTime > 300000) {
                lastPrintLogTime = System.currentTimeMillis();
                Logger.info(toString());
            }
            for (int i = 0; i < servers.length * 2; i++) {
                currentIndex = currentIndex % servers.length;
                Object[] ual = servers[currentIndex++];
                if ((boolean) ual[1]) {
                    ual[3] = (int) ual[3] + 1;
                    return (Server) ual[0];
                } else {
                    test((Server) ual[0]);
                }
            }
            return (Server) servers[0][0];
        }

        @Override
        public void remove() {
            //do nothing
        }

        @Override
        public String toString() {
            return GsonHelper.toJsonString(servers);
        }
    }
}