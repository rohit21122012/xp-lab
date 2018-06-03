package example;

import io.github.benas.randombeans.annotation.Randomizer;
import io.github.benas.randombeans.randomizers.CompanyRandomizer;
import io.github.benas.randombeans.randomizers.EmailRandomizer;
import io.github.benas.randombeans.randomizers.FullNameRandomizer;
import io.github.benas.randombeans.randomizers.ParagraphRandomizer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

public class RandomDataGenerator {

  public static void main(String[] args) throws InterruptedException {
    int numberOfBeans = 1;
    generateBeansAndWriteToZK(numberOfBeans);
  }

  class Person {
    @Randomizer(FullNameRandomizer.class)
    private String name;
    @Randomizer(CompanyRandomizer.class)
    private String company;
    @Randomizer(EmailRandomizer.class)
    private String email;
    @Randomizer(ParagraphRandomizer.class)
    private String description;    @Randomizer(ParagraphRandomizer.class)
    private String description2;    @Randomizer(ParagraphRandomizer.class)
    private String description3;    @Randomizer(ParagraphRandomizer.class)
    private String description4;    @Randomizer(ParagraphRandomizer.class)
    private String description5;    @Randomizer(ParagraphRandomizer.class)
    private String description6;    @Randomizer(ParagraphRandomizer.class)
    private String description7;    @Randomizer(ParagraphRandomizer.class)
    private String description8;    @Randomizer(ParagraphRandomizer.class)
    private String description9;    @Randomizer(ParagraphRandomizer.class)
    private String description10;

    private List<String> l;

    public Person(List<String> l) {
      this.l = randomListOf(100_000_000, String.class);
    }

    @Override
    public String toString() {
      return "Person{" +
          "name='" + name + '\'' +
          ", company='" + company + '\'' +
          ", email='" + email + '\'' +
          ", description='" + description + '\'' +
          ", description2='" + description2 + '\'' +
          ", description3='" + description3 + '\'' +
          ", description4='" + description4 + '\'' +
          ", description5='" + description5 + '\'' +
          ", description6='" + description6 + '\'' +
          ", description7='" + description7 + '\'' +
          ", description8='" + description8 + '\'' +
          ", description9='" + description9 + '\'' +
          ", description10='" + description10 + '\'' +
          ", l=" + toString(l) +
          '}';
    }

    private String toString(List<String> l) {
      StringBuilder lS = new StringBuilder("");
      for (String lx: l) {
        lS.append(lx);
      }
      return lS.toString();
    }
  }


  private static CuratorFramework connectToZK() {
    String connection = "localhost:2181";
    CuratorFramework client = CuratorFrameworkFactory.newClient(connection, 5000, 5000, new ExponentialBackoffRetry(1000, 3));
    client.start();
    return client;
  }

  private static void writeToZK(Person p, CuratorFramework client) throws Exception {
    String personsNameSpace = "person";
    client.usingNamespace(personsNameSpace).create().forPath("/" + p.name.replace(" ", "_"), p.toString().getBytes());
  }

  private static void generateBeansAndWriteToZK(int numberOfBeans) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(50);
    for (int i = 0; i < numberOfBeans; i++) {

      final int j = i;
      executorService.execute(() -> {
        CuratorFramework client = connectToZK();
        Person p = random(Person.class);
        System.out.println("p : " + p.toString());
        try {
          writeToZK(p, client);
          System.out.println(j + " " + Thread.currentThread() + "has written to zk person : " + p.name);
          client.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
    executorService.awaitTermination(100, TimeUnit.SECONDS);
    executorService.shutdown();
  }


}
