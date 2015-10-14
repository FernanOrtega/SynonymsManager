import es.us.lsi.fogallego.lenguajedotcomdownloader.OwnLabels;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

public class TestNeo4j {

    public static void main(String[] args) {
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder( "C:\\Users\\forte\\Documents\\Neo4j\\test.graphdb" )
                .setConfig( GraphDatabaseSettings.pagecache_memory, "1024M" )
                .setConfig( GraphDatabaseSettings.string_block_size, "60" )
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
        registerShutdownHook(graphDb);
        try (Transaction tx = graphDb.beginTx()) {
            graphDb.execute("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r");
            tx.success();
        }

        try (Transaction tx = graphDb.beginTx()) {

            Node word1 = graphDb.createNode(OwnLabels.WORD);
            word1.setProperty("lemma", "maduro");
            word1.setProperty("priority", Integer.MAX_VALUE);

            Node word2 = graphDb.createNode(OwnLabels.WORD);
            word2.setProperty("lemma", "coche");
            word2.setProperty("priority", Integer.MAX_VALUE);

            Node word3 = graphDb.createNode(OwnLabels.WORD);
            word3.setProperty("lemma", "cama");
            word3.setProperty("priority", 10);

            Node word4 = graphDb.createNode(OwnLabels.WORD);
            word4.setProperty("lemma", "hotel");
            word4.setProperty("priority", 8);

            Node word5 = graphDb.createNode(OwnLabels.WORD);
            word5.setProperty("lemma", "vuelo");
            word5.setProperty("priority", 60);

            Node word6 = graphDb.createNode(OwnLabels.WORD);
            word6.setProperty("lemma", "ratón");
            word6.setProperty("priority", 7);

            Node word7 = graphDb.createNode(OwnLabels.WORD);
            word7.setProperty("lemma", "botella");
            word7.setProperty("priority", 30);

            Node word8 = graphDb.createNode(OwnLabels.WORD);
            word8.setProperty("lemma", "lija");
            word8.setProperty("priority", 0);

            tx.success();
        }

        System.out.println("Done successfully");


        //

        graphDb.shutdown();
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

}
