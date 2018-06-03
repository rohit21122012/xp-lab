package example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class WatchCount {
    private static CuratorFramework connectToZK() {
        String connection = "localhost:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(connection, 50000, 50000, new ExponentialBackoffRetry(1000, 3));
        client.start();
        return client;
    }
    public static void main(String[] args) throws Exception {
        CuratorFramework client = connectToZK();
        CuratorListener listener = (curatorFramework, curatorEvent) -> System.out.println("curatorEvent = [" + curatorEvent + "]");
        client.getCuratorListenable().addListener(listener);
        client.create().orSetData().inBackground().forPath("/zktest", "payload".getBytes());
        Thread.sleep(1000);
        client.setData().inBackground().forPath("/zktest", "payload2".getBytes());
        client.getData().watched().forPath("/zktest");
        client.getData().forPath("/zktest");
        Thread.sleep(5000);
        NodeCache nc = new NodeCache(client, "/varadhi");
        nc.start();
        Thread.sleep(5000);
        TreeCache tc = new TreeCache(client, "/varadhi");
        tc.start();
        Thread.sleep(5000);
        TreeCache tc2 = new TreeCache(client, "/varadhi/app/subscriptions/RQ_DEFAULT_1/sub_RQ_DEFAULT_1");
        tc2.start();
        Thread.sleep(5000);

    }

}
