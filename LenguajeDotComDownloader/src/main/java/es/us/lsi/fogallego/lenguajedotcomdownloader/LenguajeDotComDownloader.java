package es.us.lsi.fogallego.lenguajedotcomdownloader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.impl.core.NodeProxy;
import org.parboiled.common.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.neo4j.helpers.collection.MapUtil.map;

public class LenguajeDotComDownloader {

    // Jsoup
    private static final int TIMEOUT = 30000;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

    // Downloader
    private static final String BASE_URL = "http://lenguaje.com/cgi-bin/Thesauro.exe?edition_field=";
    private static final int DEFAULT_BATCH = 50;
    private static final String HTML_FOLDER = "C:\\lenguaje.com\\";

    // Neo4j
    private static final String NEO4J_DB_FOLDER = "C:\\Users\\fogallego\\Documents\\Neo4j\\";
    private static final String NEO4J_DB_FULL_PATH = NEO4J_DB_FOLDER + "semanticdb.graphdb";
    private static final String QUERY_GET_PRIORITY_NODES = "match(w:WORD) WHERE w.priority > 1 RETURN w ORDER BY w.priority DESC LIMIT {numLimit}";
    private static final String QUERY_MERGE_NEW_NODES = "MERGE(w:WORD {lemma:{lemmaStr}}) ON CREATE SET w.priority = {priority} return w";
    private static final int NEW_WORDS_PRIORITY = 10;
    public static final int MILLIS_SLEEP_AFTER_ERROR = 30000;
    public static final int MILLIS_SLEEP_AFTER_DOWNLOAD = 5000;
    private static GraphDatabaseService graphDb;

    public static void main(String[] args) {

//        if (args.length != 1) {
//            System.err.println("Usage: <db_path>");
//            System.exit(0);
//        }

        System.out.println("Starting downloader of Lenguaje.com");

        graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(NEO4J_DB_FULL_PATH)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "1024M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
        registerShutdownHook(graphDb);

        List<String> lstWords;
        do {
            lstWords = getPriorityWords(DEFAULT_BATCH);
            System.out.println("Downloading from a list of " + lstWords.size() + " words.");
            lstWords.stream().map(LenguajeDotComDownloader::download).forEach(LenguajeDotComDownloader::saveWordResults);
            System.out.println();
        } while (lstWords.size() > 0);
        System.out.println("No more results, finishing now...");

        graphDb.shutdown();

    }

    private static void saveWordResults(WordResults wordResults) {

        List<SynonymSense> lstSynonymSense = wordResults.getLstSynonymSense();
        try (Transaction tx = graphDb.beginTx()) {

            Long tInit = System.currentTimeMillis();
            org.neo4j.graphdb.Node mainWordNode = findAndRepairRepeatedNodes(OwnLabels.WORD, "lemma", wordResults.getWord());
//            System.out.println("--- time finding main word: "+(System.currentTimeMillis() - tInit));

            for (SynonymSense sense : lstSynonymSense) {
//                tInit = System.currentTimeMillis();
                org.neo4j.graphdb.Node senseSynonymsNode = graphDb.createNode(OwnLabels.SYNONYM_SENSE);
                senseSynonymsNode.setProperty("posTag", sense.getPosTag());
//                System.out.println("--- time creating sensesynm_node: " + (System.currentTimeMillis() - tInit));

                // Relationship with the mainWord
//                tInit = System.currentTimeMillis();
                Relationship mainRelationship = senseSynonymsNode.createRelationshipTo(mainWordNode, OwnRelationships.SYNONYM);
                mainRelationship.setProperty("lemma", sense.getLemma());
//                System.out.println("--- time creating mainRelationship: " + (System.currentTimeMillis() - tInit));

                tInit = System.currentTimeMillis();
                sense.getLstSynonyms().forEach(s -> {
                    // Creating new nodes if no exists with default priority
//                    System.out.println("------ "+s);
//                    long tInitAux = System.currentTimeMillis();
                    graphDb.execute(QUERY_MERGE_NEW_NODES, map("lemmaStr", s, "priority", NEW_WORDS_PRIORITY));
//                    System.out.println("------ time executing QUERY_MERGE_NEW_NODES: " + (System.currentTimeMillis() - tInitAux));

//                    tInitAux = System.currentTimeMillis();
                    org.neo4j.graphdb.Node wordNode = findAndRepairRepeatedNodes(OwnLabels.WORD, "lemma", s);
//                    System.out.println("------ time finding new word node: " + (System.currentTimeMillis() - tInitAux));

                    // Creating relations
//                    tInitAux = System.currentTimeMillis();
                    senseSynonymsNode.createRelationshipTo(wordNode, OwnRelationships.SYNONYM);
//                    System.out.println("------ time creation relationship with new word node: " + (System.currentTimeMillis() - tInitAux));
                });
                System.out.println("---time creating all synonyms relationships: " + (System.currentTimeMillis() - tInit));
                System.out.println();
            }

            for (AntonymSense antonymSense : wordResults.getLstAntonyms()) {

//                System.out.println("------ "+antonymSense.getAntonym());
//                tInit = System.currentTimeMillis();
                org.neo4j.graphdb.Node senseAntonymsNode = graphDb.createNode(OwnLabels.ANTONYM_SENSE);
                senseAntonymsNode.setProperty("posTag", antonymSense.getPosTag());
//                System.out.println("------ time creating antonymsense node: " + (System.currentTimeMillis() - tInit));

//                long tInitAux = System.currentTimeMillis();
                graphDb.execute(QUERY_MERGE_NEW_NODES, map("lemmaStr", antonymSense.getAntonym(), "priority", NEW_WORDS_PRIORITY));
//                System.out.println("------ time executing QUERY_MERGE_NEW_NODES: " + (System.currentTimeMillis() - tInitAux));

//                tInitAux = System.currentTimeMillis();
                org.neo4j.graphdb.Node wordNode = findAndRepairRepeatedNodes(OwnLabels.WORD, "lemma", antonymSense.getAntonym());
//                System.out.println("------ time finding new word node: " + (System.currentTimeMillis() - tInitAux));

//                tInitAux = System.currentTimeMillis();
                Relationship mainRelationship = senseAntonymsNode.createRelationshipTo(mainWordNode, OwnRelationships.ANTONYM);
                mainRelationship.setProperty("lemma", antonymSense.getLemma());
//                System.out.println("------ time creation relationship with main word: " + (System.currentTimeMillis() - tInitAux));

//                tInitAux = System.currentTimeMillis();
                senseAntonymsNode.createRelationshipTo(wordNode, OwnRelationships.ANTONYM);
//                System.out.println("------ time creation relationship with new word node: " + (System.currentTimeMillis() - tInitAux));

            }
            mainWordNode.setProperty("priority", 0);

            tx.success();
        }
    }

    private static org.neo4j.graphdb.Node findAndRepairRepeatedNodes(Label label, String key, Object value) {
        org.neo4j.graphdb.Node node;
        try {
            node = graphDb.findNode(label, key, value);
        } catch (MultipleFoundException e) {
            System.err.println("Duplicated nodes!! Fixing right now");

            ResourceIterator<org.neo4j.graphdb.Node> iterator = graphDb.findNodes(label, key, value);
            node = iterator.next();

            while(iterator.hasNext()) {
                org.neo4j.graphdb.Node repeatedNode = iterator.next();
                Iterable<Relationship> relationships = repeatedNode.getRelationships();
                final org.neo4j.graphdb.Node finalNode = node;
                relationships.forEach(relationshipOfRepeated -> {
                    org.neo4j.graphdb.Node otherNodeOfRepeatedNode = relationshipOfRepeated.getOtherNode(repeatedNode);
                    Relationship copiedRelationShip = otherNodeOfRepeatedNode.createRelationshipTo(finalNode, relationshipOfRepeated.getType());
                    if (relationshipOfRepeated.hasProperty("lemma")) {
                        copiedRelationShip.setProperty("lemma", relationshipOfRepeated.getProperty("lemma"));
                    }

                    relationshipOfRepeated.delete();
                });
                repeatedNode.delete();
            }
        }

        return node;
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
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

    public static List<String> getPriorityWords(int num) {
        List<String> lstWord = new ArrayList<>();

        try (Transaction tx = graphDb.beginTx()) {
            // match(w:WORD) WHERE w.priority > 1 RETURN w ORDER BY w.priority DESC LIMIT 3
            Result results = graphDb.execute(QUERY_GET_PRIORITY_NODES, map("numLimit", num));
            while (results.hasNext()) {
                String lemma = (String) ((NodeProxy) results.next().get("w")).getProperty("lemma");
                lstWord.add(lemma);
            }
            tx.success();
        }

        return lstWord;
    }

    public static WordResults download(String word) {

        System.out.println("Downloading from word: " + word);

        WordResults wordResults = null;

        while (wordResults == null) {
            try {
                wordResults = downloadHook(word);
                try {
                    Thread.sleep(MILLIS_SLEEP_AFTER_DOWNLOAD);
                } catch (InterruptedException e1) {
                    System.err.println("Interrupted thread!!");
                }
            } catch (IOException e) {
                System.err.println("Error downloading synonyms for word: " + word);
                try {
                    Thread.sleep(MILLIS_SLEEP_AFTER_ERROR);
                } catch (InterruptedException e1) {
                    System.err.println("Interrupted thread!!");
                }
            }
        }

        return wordResults;
    }

    public static WordResults downloadHook(String mainWord) throws IOException {

        List<SynonymSense> lstSynonymSense = new ArrayList<>();
        List<AntonymSense> lstAntonyms = new ArrayList<>();

        Document doc = Jsoup.connect(BASE_URL + mainWord).userAgent(USER_AGENT).timeout(TIMEOUT).get();

        FileUtils.writeAllText(doc.outerHtml(), HTML_FOLDER + mainWord + System.currentTimeMillis() + ".html");

        Elements elLemma = doc.select(".Lemma");

        if (elLemma.size() > 0) {

            List<Node> lstNode = elLemma.parents().get(0).childNodes();
            String currentTag = null;
            String currentLemma = null;

            for (int i = 1; i < lstNode.size() - 3; i++) {

                Node node = lstNode.get(i);

                if (node instanceof TextNode && !((TextNode) node).text().equals("")) {
                    // Lemma tag
                    currentTag = ((TextNode) node).getWholeText().replace(" - ", "");
                } else {

                    String classNode = node.attr("class");

                    switch (classNode) {
                        case "Synonyms":
                            List<Node> lstNodeSense = node.childNodes();
                            for (Node sense : lstNodeSense) {

                                String synonymsStrList = StringEscapeUtils.unescapeHtml4(((Element) sense).text());

                                String[] synonyms = synonymsStrList.split(", ");
                                List<String> lstWord = Arrays.asList(synonyms);

                                SynonymSense synonymSense = new SynonymSense();
                                synonymSense.setPosTag(currentTag);
                                synonymSense.setLstSynonyms(lstWord);
                                synonymSense.setLemma(currentLemma);
                                lstSynonymSense.add(synonymSense);
                            }
                            break;
                        case "Antonyms":
                            lstNodeSense = node.childNodes();
                            for (Node sense : lstNodeSense) {
                                String antonymsStrList = StringEscapeUtils.unescapeHtml4(((Element) sense).text());
                                String[] antonyms = antonymsStrList.split(", ");

                                for (String antonym : antonyms) {
                                    lstAntonyms.add(new AntonymSense(currentTag, currentLemma, antonym));
                                }
                            }
                            break;
                        case "Lemma":
                            currentLemma = ((Element) node).text();
                    }
                }
            }

        }

        return new WordResults(mainWord, lstSynonymSense, lstAntonyms);
    }

}
