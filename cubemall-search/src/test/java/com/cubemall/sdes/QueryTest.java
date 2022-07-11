package com.cubemall.sdes;


import com.cubemall.search.CubemallSearchApplication;
import com.cubemall.search.model.Blog;
import com.kkb.cubemall.common.utils.PageUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.lang.annotation.Native;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CubemallSearchApplication.class)
public class QueryTest {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    public void testQuery01(){
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
                .build();
        SearchHits<Blog> searchHits = template.search(query, Blog.class);
        //取总记录数
        long totalHits = searchHits.getTotalHits();
        System.out.println("总记录数" + totalHits);
        //取文档列表
        List<SearchHit<Blog>> hits = searchHits.getSearchHits();
        hits.forEach(System.out::println);
    }

    @Test
    public void testQuery02(){
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("看电影", "title", "content"))
                .withFilter(QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery("title", "电影"))
                .should(QueryBuilders.termQuery("content", "祝福"))
                )
                .withFilter(QueryBuilders.termQuery("mobile", "13000000000"))
                //分页设置
                .withPageable(PageRequest.of(0, 10))
                //高亮设置
                .withHighlightBuilder(new HighlightBuilder().field("title").field("content")
                //高亮显示前缀
                .preTags("<em>")
                //后缀
                .postTags("</em>")
        ).build();
        //使用template对象执行查询
        SearchHits<Blog> searchHits = template.search(query, Blog.class);
        //取总记录数
        long totalHits = searchHits.getTotalHits();
        System.out.println("总记录数:" + totalHits);
        searchHits.stream().forEach(hit -> {
                    Blog blog = hit.getContent();
                    //取高亮结果
                    Map<String, List<String>> highFields = hit.getHighlightFields();
                    String title = highFields.get("title").get(0);
                    String content = highFields.get("content").get(0);
                    blog.setTitle(title);
                    blog.setContent(content);
                    System.out.println(blog);

        });
        if (searchHits.hasAggregations()) {
            Aggregations aggregations = searchHits.getAggregations();
            assert aggregations != null;
            ParsedStringTerms aggregation = aggregations.get("mobile_group");
            List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
            buckets.forEach(bucket ->{
                System.out.println(bucket.getKeyAsString());
                System.out.println(bucket.getDocCount());
            });




        }
    }
}
