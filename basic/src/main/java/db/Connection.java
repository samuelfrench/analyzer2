package db;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;

import domain.SMAMomentumBoolMatrix;
import domain.SMAMomentumBoolMatrix.SIGNAL;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Connection {

	public static void dbTestOp(){
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient( "localhost" );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DB db = mongoClient.getDB( "mydb" );
		DBCollection coll = db.getCollection("testCollection");
		BasicDBObject doc = new BasicDBObject("name", "MongoDB")
	    .append("type", "database")
	    .append("count", 1)
	    .append("info", new BasicDBObject("x", 203).append("y", 102));
		coll.insert(doc);
		
		DBObject myDoc = coll.findOne();
		System.out.println(myDoc);
		
		// 1. Ordered bulk operation
		BulkWriteOperation builder = coll.initializeOrderedBulkOperation();
		builder.insert(new BasicDBObject("_id", 1));
		builder.insert(new BasicDBObject("_id", 2));
		builder.insert(new BasicDBObject("_id", 3));

		builder.find(new BasicDBObject("_id", 1)).updateOne(new BasicDBObject("$set", new BasicDBObject("x", 2)));
		builder.find(new BasicDBObject("_id", 2)).removeOne();
		builder.find(new BasicDBObject("_id", 3)).replaceOne(new BasicDBObject("_id", 3).append("x", 4));

		BulkWriteResult result = builder.execute();

		// 2. Unordered bulk operation - no guarantee of order of operation
		builder = coll.initializeUnorderedBulkOperation();
		builder.find(new BasicDBObject("_id", 1)).removeOne();
		builder.find(new BasicDBObject("_id", 2)).removeOne();

		result = builder.execute();
	}
	
	/*
	 * Status codes
	 * -1 - not implemented
	 * 0 - success
	 */
	public static int insertTrendReversals(SMAMomentumBoolMatrix matrix){
		ConcurrentMap<Long, ConcurrentMap<Long, SIGNAL>> signal = matrix.getSignal();
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient( "localhost" );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DB db = mongoClient.getDB( "mydb" );
		DBCollection coll = db.getCollection("debug_signals");
		
		BulkWriteOperation builder = coll.initializeOrderedBulkOperation();
		signal.keySet().parallelStream().forEach((k)->{
			signal.get(k).keySet().parallelStream().forEach((p)->{
				BasicDBObject doc = new BasicDBObject("ts",k)
			    .append("period", p)
			    .append("val", signal.get(k).get(p))
			    .append("debug", "0");
				builder.insert(doc);	
			});
		});
		
		BulkWriteResult result = builder.execute();
		
		if(result.getInsertedCount()>0){
			return 0;
		}
		

		return -1;
	}
	
	public static List<String> getTrendReversals(){
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient( "localhost" );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DB db = mongoClient.getDB( "mydb" );
		DBCollection coll = db.getCollection("debug_signals");
		DBCursor cursor = coll.find();
		//List<String> returnList = new ArrayList<>();
		try {
			   while(cursor.hasNext()) {
				   
			       System.out.println(cursor.next());			   }
			} finally {
			   cursor.close();
			}
		
return null;
	}
	
}
