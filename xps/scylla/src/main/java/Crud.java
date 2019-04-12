import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Crud {

    private static Cluster cluster = Cluster.builder().addContactPoints("scylla-node1", "scylla-node2", "scylla-node3").build();
    private static Session session = cluster.connect("catalog");


    private static void disconnectDB() {
        session.close();
        cluster.close();
    }

    private static void insertQuery() {
        System.out.print("\n\nInserting ......");
        session.execute("INSERT INTO mutant_data (first_name,last_name,address,picture_location) VALUES ('Rohit','Patiyal','174','http://fb.com/rp')");
        selectQuery();
    }

    private static void deleteQuery() {
        System.out.print("\n\nDeleting ......");
        session.execute("DELETE FROM mutant_data WHERE first_name = 'Rohit' and last_name = 'Patiyal'");
        selectQuery();
    }

    private static void selectQuery() {
        System.out.println("\n\nDisplaying Results:");
        ResultSet resultSet = session.execute("SELECT * FROM mutant_data");
        for (Row row: resultSet) {
            String firstName = row.getString("first_name");
            String lastName = row.getString("last_name");
            System.out.println("Name: " + firstName + " " + lastName);
        }
    }


    public static void main(String[] args){
        System.out.println("Running Crud Class");
        selectQuery();
        insertQuery();
        deleteQuery();
        disconnectDB();
    }

}
