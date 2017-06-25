package com.itheima.lucene.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itheima.lucene.dao.BookDao;
import com.itheima.lucene.dao.BookDaoImpl;
import com.itheima.lucene.pojo.Book;

public class CreateIndexTest {

	@Test
	public void testCreateIndex() throws Exception {
		// 1. 采集数据
		BookDao dao = new BookDaoImpl();
		List<Book> bookList = dao.queryBookList();
		// 2. 创建Document文档对象
		List<Document> documents = new ArrayList<Document>();
		for (Book book : bookList) {
			Document doc = new Document();
			// Document文档中添加Field域
			// 图书Id
			// Store.YES:表示存储到文档域中
			doc.add(new StringField("id", book.getId().toString(), Store.YES));
			doc.add(new TextField("name", book.getName(), Store.YES));
			doc.add(new StoredField("pic", book.getPic()));
			doc.add(new FloatField("price", book.getPrice(), Store.YES));
			doc.add(new TextField("desc", book.getDesc(), Store.NO));
			// 把document放入到list中
			documents.add(doc);
		}
		// 3. 创建分析器（分词器）
		// Analyzer analyzer = new StandardAnalyzer();
		Analyzer analyzer = new IKAnalyzer();

		// 4. 创建IndexWriterConfig配置信息类
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

		// 5. 创建Directory对象，声明索引库存储位置
		Directory directory = FSDirectory.open(new File("f:\\temp\\index"));
		// 6. 创建IndexWriter写入对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
		// 7. 把Document写入到索引库中
		for (Document document : documents) {
			indexWriter.addDocument(document);
		}
		// 8. 释放资源
		indexWriter.close();
	}

	@Test
	public void testIndexDelete() throws Exception {
		// 3. 创建分析器（分词器）
		// Analyzer analyzer = new StandardAnalyzer();
		Analyzer analyzer = new IKAnalyzer();

		// 4. 创建IndexWriterConfig配置信息类
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

		// 5. 创建Directory对象，声明索引库存储位置
		Directory directory = FSDirectory.open(new File("f:\\temp\\index"));
		// 6. 创建IndexWriter写入对象
		IndexWriter indexWriter = new IndexWriter(directory, config);

		// 条件删除,索引还在,只删除索引对应的document
		// indexWriter.deleteDocuments(new Term("name", "solr"));
		// 删除所有,同时删除索引和document,即全部删除
		indexWriter.deleteAll();

		indexWriter.close();
	}

	@Test
	public void testIndexUpdate() throws Exception {
		// 3. 创建分析器（分词器）
		// Analyzer analyzer = new StandardAnalyzer();
		Analyzer analyzer = new IKAnalyzer();

		// 4. 创建IndexWriterConfig配置信息类
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

		// 5. 创建Directory对象，声明索引库存储位置
		Directory directory = FSDirectory.open(new File("f:\\temp\\index"));
		// 6. 创建IndexWriter写入对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		//创建document
		Document doc = new Document();
		
		doc.add(new StringField("id", "66", Store.YES));
		doc.add(new TextField("name", "小鸡仔",Store.YES));
		
		indexWriter.updateDocument(new Term("id", "1"), doc);
		
		indexWriter.close();
	}
}
