package com.itheima.lucene.test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SearchIndexTest {

	@Test
	public void testSearchIndex() throws Exception {
		// 1. 创建Query搜索对象
		// 创建分词对象
		Analyzer analyzer = new StandardAnalyzer();
		// 创建搜索解析器，第一个参数：默认Field域，第二个参数：分词器
		QueryParser queryParser = new QueryParser("name", analyzer);
		// 创建搜索对象
		Query query = queryParser.parse("name:java");

		doSearch(query);
	}

	@Test
	public void testTermQuery() throws IOException{
		Query query = new TermQuery(new Term("name", "lucene"));
		
		doSearch(query);
	}
	
	//范围查询
	@Test
	public void testNumericRangeQuery() throws IOException{
		// 创建NumericRangeQuery搜索对象,数字范围查询.
		// 五个参数分别是：域名、最小值、最大值、是否包含最小值，是否包含最大值
		Query query = NumericRangeQuery.newFloatRange("price", 60f, 80f, true, true);
		
		doSearch(query);
	}
	
	//组合条件查询
	@Test
	public void testBooleanQuery() throws IOException{
		//创建TermQuery
		Query query1 = new TermQuery(new Term("name","lucene"));
		//创建NumericRangeQuery
		Query query2 = NumericRangeQuery.newFloatRange("price", 60f, 80f, true, true);
		//创建BooleanQUery
		BooleanQuery booleanQuery = new BooleanQuery();
		//组合条件查询
		booleanQuery.add(query1, Occur.SHOULD);
		booleanQuery.add(query2,Occur.SHOULD);
		
		doSearch(booleanQuery);
		
	}
	
	@Test
	public void testMultiFieldQueryParser() throws Exception{
		Analyzer analyzer = new IKAnalyzer();
		
		String[] fields = {"name","desc"};
		
		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, analyzer);
		
		Query query = multiFieldQueryParser.parse("lucene");
		
		System.out.println(query);
		
		doSearch(query);
	}
	
	
	private void doSearch(Query query) throws IOException {
		// 2. 创建Directory流对象,声明索引库位置
		Directory directory = FSDirectory.open(new File("f:\\temp\\index"));
		// 3. 创建索引读取对象IndexReader
		IndexReader indexReader = DirectoryReader.open(directory);
		// 4. 创建索引搜索对象IndexSearcher
		IndexSearcher searcher = new IndexSearcher(indexReader);
		// 5. 使用索引搜索对象，执行搜索，返回结果集TopDocs
		// 第一个参数：搜索对象，第二个参数：返回的数据条数，指定查询结果最顶部的n条数据返回
		TopDocs topDocs = searcher.search(query, 10);
		// 获取查询结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		// 6. 解析结果集
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("========================");
			// 获取文档
			int docID = scoreDoc.doc;
			Document doc = searcher.doc(docID);
			System.out.println("docId: " + docID);
			System.out.println("bookID: " + doc.get("id"));
			System.out.println("bookName: " + doc.get("name"));
			System.out.println("bookPic: " + doc.get("pic"));
			System.out.println("bookPrice: " + doc.get("price"));
			System.out.println("bookDesc: " + doc.get("desc"));
		}
		// 7. 释放资源
		indexReader.close();
	}
}
