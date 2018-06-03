package example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class NodeCopier {

  public static void main(String[] args) throws Exception {
    CuratorFramework client = connectToZK();
    for (String cp: client.getChildren().forPath("/person")){
      System.out.println("Child path: " + cp);
      client.usingNamespace("p4").create().forPath("/" + cp, client.getData().forPath("/person/" + cp));
      client.usingNamespace("p5").create().forPath("/" + cp, client.getData().forPath("/person/" + cp));
      client.usingNamespace("p6").create().forPath("/" + cp, client.getData().forPath("/person/" + cp));
      client.usingNamespace("p7").create().forPath("/" + cp, client.getData().forPath("/person/" + cp));
    }
  }



  private static CuratorFramework connectToZK() {
    String connection = "localhost:2181";
    CuratorFramework client = CuratorFrameworkFactory.newClient(connection, 5000, 5000, new ExponentialBackoffRetry(1000, 3));
    client.start();
    return client;
  }


}
