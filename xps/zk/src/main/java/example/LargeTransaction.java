package example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LargeTransaction {
  public static void main(String[] args) throws Exception {
    CuratorFramework client = connectToZK();
    List<CuratorOp> curatorOpList = new ArrayList<>();
    int numTrans = 1000;
    for (String cp : client.getChildren().forPath("/person")) {
      CuratorOp c = client.transactionOp().create().forPath("/p10/" + cp, client.getData().forPath("/person/" + cp));
      curatorOpList.add(c);
      numTrans--;
      System.out.println("numTrans: " + numTrans);
      if (numTrans == 0) break;
    }
    try {
      Collection<CuratorTransactionResult> result = client.transaction().forOperations(curatorOpList);
      for (CuratorTransactionResult c : result) {
        System.out.println(c.getForPath() + " - " + c.getType());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private static CuratorFramework connectToZK() {
    String connection = "localhost:2181";
    CuratorFramework client = CuratorFrameworkFactory.newClient(connection, 5000, 5000, new ExponentialBackoffRetry(1000, 3));
    client.start();
    return client;
  }
}
