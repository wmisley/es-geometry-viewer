import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import static org.elasticsearch.index.query.QueryBuilders.*;


public class EsClient {
    public static final Logger logger = LogManager.getLogger(EsClient.class);
    protected Client client = null;
    protected String esIndex = "";

    public EsClient() {
    }

    public void connect(String esHost, int esPort, String esIndex) throws Exception {
        this.esIndex = esIndex;

        logger.info("Connecting to Elasticsearch...");
        client = TransportClient.builder().build()
                .addTransportAddress(new
                        InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
        logger.info("Connected...");
    }

    public void disconnect() {
        client.close();
    }

    public void matchAllQuery(EsFeatureDrawable drawable) {
        //QueryBuilder qb = termQuery("AOO", "-999999");
        QueryBuilder qb = termQuery("ZVH", "-999999");

        //QueryBuilder qb = termQuery("ZI005_FNA", "Saint Louis Cathedral");

        SearchResponse scrollResp = client.prepareSearch(esIndex)
                .addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(100).execute().actionGet(); //100 hits per shard will be returned for each scroll

        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                HashMap map = (HashMap) hit.getSource().get("shape");

                if (map != null) {
                    drawable.drawFeature(map);
                } else {
                    logger.warn("<null> \"shape\" field found, not drawing shape");
                }
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
        } while (scrollResp.getHits().getHits().length == 0) ;
    }

    static public void main(String[] args) {
        EsClient esClient = new EsClient();

        try {
            esClient.connect("eshost", 9300, "building_p");
            esClient.matchAllQuery(null);
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            esClient.disconnect();
        }
    }
}
