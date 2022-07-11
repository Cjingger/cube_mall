package com.cubemall.sdes;

import com.cubemall.search.CubemallSearchApplication;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Author: sublun
 * @Date: 2021/4/25 18:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CubemallSearchApplication.class)
public class RestClientTest {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testRestClient() throws IOException {
        restHighLevelClient.indices().create(new CreateIndexRequest("test"), RequestOptions.DEFAULT);
    }

    @Test
    public void aggsTest() throws IOException {
        SearchRequest request = new SearchRequest()
                .indices("blog_1")
                .source(new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .aggregation(new ValueCountAggregationBuilder("doc_count").field("mobile"))
                .aggregation(new TermsAggregationBuilder("group_count").field("mobile").size(10))
                );
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }
}
