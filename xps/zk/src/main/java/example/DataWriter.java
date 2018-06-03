package example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataWriter {
    private static CuratorFramework connectToZK() {
        String connection = "localhost:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(connection, 50000, 50000, new ExponentialBackoffRetry(1000, 3));
        client.start();
        return client;
    }

    private static void writeToZK(CuratorFramework conn, String path, String data) {
        try {
            conn.create().forPath(path, data.getBytes());
        } catch (Exception e) {
            System.err.println("ZK write failed with error : " + e.getMessage());
        }
    }

    private static List<CuratorFramework> getConnPool(int connCount) {
        List<CuratorFramework> conns = new ArrayList<>(connCount);
        for (int i = 0; i < connCount; i++) {
            conns.add(connectToZK());
        }
        return conns;
    }

    private static int connId = 0;

    private static CuratorFramework getConn(List<CuratorFramework> conns) {
        return conns.get((connId++) % conns.size());
    }

    private static void awaitTerminationAfterShutdown(ExecutorService threadPool, List<CuratorFramework> conns) {
        threadPool.shutdown();

        try {
            if (!threadPool.awaitTermination(500, TimeUnit.SECONDS)) {
                conns.forEach(CuratorFramework::close);
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void prepareThreadTasks(int threadCount, int connCount, int opsPerThread, int opsPerTransaction) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<CuratorFramework> conns = getConnPool(connCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int j = i;
            executorService.execute(() -> {
                countDownLatch.countDown();
                try {
                    countDownLatch.await(1000, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Countdown await failed");
                }
                System.out.println("Thread " + j + " starting task");
                for (int k = 0; k < opsPerThread; k++) {
                    if (opsPerTransaction == 1) {
                        writeToZK(getConn(conns), "/" + LocalTime.now().toString() + Thread.currentThread().getName() + k, Thread.currentThread().getName() + k);
                    } else {
                        writeToZKInTrans(getConn(conns), k, opsPerTransaction);
                    }

                    if (opsPerThread == -1) k = 0; //continue as in an infinite loop
                }
                System.out.println("Thread " + j + " finished task");
            });
        }
        awaitTerminationAfterShutdown(executorService, conns);
    }

    private static void writeToZKInTrans(CuratorFramework conn, int k, int numOps) {
        try {
            List<CuratorOp> curatorOpList = new ArrayList<>();
            for (int i = 0; i < numOps; i++) {
                CuratorOp c = conn.transactionOp().create().forPath("/" + System.nanoTime() + Thread.currentThread().getName() + k, Thread.currentThread().getName().getBytes());
                curatorOpList.add(c);
            }
            try {
                Collection<CuratorTransactionResult> result = conn.transaction().forOperations(curatorOpList);
                for (CuratorTransactionResult c : result) {
                    System.out.println(c.getForPath() + " - " + c.getType());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("ZK write failed with error : " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        prepareThreadTasks(100, 100, 100, 10);
    }
}
