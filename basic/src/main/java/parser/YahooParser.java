package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import domain.csv.DataRecord;
import domain.csv.DataWrapper;
import edu.emory.mathcs.backport.java.util.Arrays;

public class YahooParser {
	private static final String[] dailyQuery = {"http://chartapi.finance.yahoo.com/instrument/1.0/","/chartdata;type=quote;range=1d/csv"};

	private static String getDailyURI(String ticker){
		StringBuilder sb = new StringBuilder(dailyQuery[0]);
		sb.append(ticker);
		sb.append(dailyQuery[1]);
		return sb.toString();
	}
	
	public static String getIntraDaily(String ticker){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String query = getDailyURI(ticker);
  	  	HttpGet httpget = new HttpGet(query);
  	  	System.out.println("executing request " + httpget.getRequestLine());
  	  	CloseableHttpResponse response;
  	  InputStreamReader reader = null;
  	  	try {
			response = httpclient.execute(httpget);
			HttpEntity resEntity = response.getEntity();
			
			if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                reader = new InputStreamReader(resEntity.getContent());
                char[] buff = new char[4096];
                StringBuffer strBuffer = new StringBuffer();
                while(reader.read(buff)!=-1){
                	strBuffer.append(buff);
                }
                return strBuffer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	public static String[] splitViaLine(String s){
		return s.split("\n");
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> splitViaLineList(String s){
		return (List<String>) Arrays.asList(splitViaLine(s)).stream().filter((p)-> ((String)p).length() > 1).collect(Collectors.toList());
	}
	
	public static String getFormattedDate(long unixTimestamp){
		Date date = new Date(unixTimestamp*1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
		return sdf.format(date);
	}
	
	
	final static int RECORD_START = 17;
	
	public static DataWrapper getData(List<String> rawInput){
		DataWrapper wrap = new DataWrapper();
		wrap.setTicker(rawInput.get(1).split(":")[1]);
		wrap.setExchangeName(rawInput.get(3).split(":")[1]);
		wrap.setCreateTs(Instant.now().getEpochSecond());
		wrap.setBeginTs(Long.parseLong(rawInput.get(9).split(":")[1].split(",")[0],10));
		wrap.setEndTs(Long.parseLong(rawInput.get(9).split(":")[1].split(",")[1],10));
		rawInput = rawInput.subList(RECORD_START, rawInput.size()-1);
		ConcurrentMap<Long, DataRecord> map = new ConcurrentHashMap<>();
		
		rawInput.stream().forEach((i)->{
			if(i.length()>10){
				String[] split = i.split(",");
				DataRecord record = new DataRecord(Long.parseLong(split[0],10),Double.parseDouble(split[1]),
						Double.parseDouble(split[2]),Double.parseDouble(split[3]),Double.parseDouble(split[4]),
						Long.parseLong(split[5],10));
				map.put(Long.parseLong(split[0],10), record);
			}
		});
		wrap.setRecords(map);
		
		return wrap;
	}
}
