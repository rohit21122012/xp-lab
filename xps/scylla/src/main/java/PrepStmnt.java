import com.datastax.driver.core.*;

public class PrepStmnt {

    private static Cluster cluster = Cluster.builder().addContactPoints("scylla-node1", "scylla-node2", "scylla-node3").build();
    private static Session session = cluster.connect("catalog");

    private static PreparedStatement insertStmnt = session.prepare("INSERT INTO mutant_data (first_name,last_name,address,picture_location) VALUES (?,?,?,?)");
    private static PreparedStatement deleteStmnt = session.prepare("DELETE FROM mutant_data WHERE first_name = ? AND last_name = ?");

    private static void disconnectDB() {
        session.close();
        cluster.close();
    }

    private static void insertQuery() {
        System.out.print("\n\nInserting ......");
        session.execute(insertStmnt.bind("Rohit", "Patiyal", "174", "http://fb.com/rp"));
        selectQuery();
    }

    private static void deleteQuery() {
        System.out.print("\n\nDeleting ......");
        session.execute(deleteStmnt.bind("Rohit", "Patiyal"));
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
        System.out.println("Running PrepStmnt Class");
        selectQuery();
        insertQuery();
        deleteQuery();
        disconnectDB();
    }

}
