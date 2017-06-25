package com.itheima.lucene.dao;

import java.util.List;

import com.itheima.lucene.pojo.Book;

public interface BookDao {
	List<Book> queryBookList();
}
