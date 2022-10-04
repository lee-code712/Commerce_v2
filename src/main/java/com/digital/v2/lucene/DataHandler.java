package com.digital.v2.lucene;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

public class DataHandler {	// lucene 레퍼지토리 정보

	static String flag = "";
	private static final File fileIndex = new File("C:/Users/unipoint/eclipse-workspace/Commerce_v2/index/");
	private static Directory dir = null;
	
	static {
		
		if ("".equals(flag)) {
			synchronized (flag) {
				try {
					dir = FSDirectory.open(Paths.get(fileIndex.toURI()));
					
					IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
					IndexWriter writer = new IndexWriter(dir, config);
					
					writer.commit();
					writer.flush();			
					writer.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	public static boolean write(Document doc) throws Exception {

		if (doc == null)
			return false;
		
		try {
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter writer = new IndexWriter(dir, config);
			
			writer.addDocument(doc);
			writer.commit();
			writer.flush();			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
	public static boolean update(Document newDoc, Term updateTerm) throws Exception {
		
		if (newDoc == null)
			return false;
		
		try {
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter writer = new IndexWriter(dir, config);
			
			writer.updateDocument(updateTerm, newDoc);
			writer.commit();
			writer.flush();			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
	public static boolean deleteByTwoTerms(Term term1, Term term2) throws Exception {

		try {
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter writer = new IndexWriter(dir, config);
			
			TermQuery termQuery1 = new TermQuery(term1);
			TermQuery termQuery2 = new TermQuery(term2);

			BooleanQuery wordQuery = new BooleanQuery.Builder()
				.add(termQuery1, Occur.MUST)
				.add(termQuery2, Occur.MUST)
				.build();
			
			writer.deleteDocuments(wordQuery);
			writer.commit();
			writer.flush();			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
	public static List<Document> wildCardQuery(String key, String value) {
		
		Document doc = null;
		List<Document> docList = new ArrayList<Document>();
		
		try {
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			Query wordQuery = new WildcardQuery(new Term(key, "*" + value +"*"));
			
			TopDocs foundDocsBody = searcher.search(wordQuery, 1000);
			for (ScoreDoc sd : foundDocsBody.scoreDocs) {
				doc = searcher.doc(sd.doc);
				docList.add(doc);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return docList;
	}
	
	public static List<Document> findListHardly(String key, String value) {
		
		List<Document> docList = new ArrayList<Document>();
		Document doc = null;
		
		try {
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			Query wordQuery = new QueryBuilder(new StandardAnalyzer()).createBooleanQuery(key, value, Occur.MUST);
			
			TopDocs foundDocsBody = searcher.search(wordQuery, 1000);
			for (ScoreDoc sd : foundDocsBody.scoreDocs) {
				doc = searcher.doc(sd.doc);
				docList.add(doc);
//				System.out.println(doc.getFields());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return docList;
	}
	
	public static Document findHardly(String key, String value) {
		
		Document doc = null;
		
		try {
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			Query wordQuery = new QueryBuilder(new StandardAnalyzer()).createBooleanQuery(key, value, Occur.MUST);
			
			TopDocs foundDocsBody = searcher.search(wordQuery, 1000);
			for (ScoreDoc sd : foundDocsBody.scoreDocs) {
				doc = searcher.doc(sd.doc);				
//				System.out.println(doc.getFields());
				return doc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Document findHardlyByTwoTerms(Term term1, Term term2) {
		
		Document doc = null;
		
		try {
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			TermQuery termQuery1 = new TermQuery(term1);
			TermQuery termQuery2 = new TermQuery(term2);

			BooleanQuery wordQuery = new BooleanQuery.Builder()
				.add(termQuery1, Occur.MUST)
				.add(termQuery2, Occur.MUST)
				.build();
			
			TopDocs foundDocsBody = searcher.search(wordQuery, 1000);
			for (ScoreDoc sd : foundDocsBody.scoreDocs) {
				doc = searcher.doc(sd.doc);				
//				System.out.println(doc.getFields());
				return doc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
