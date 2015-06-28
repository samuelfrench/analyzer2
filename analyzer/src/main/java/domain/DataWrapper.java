package domain;

import java.util.concurrent.ConcurrentMap;

public class DataWrapper {
	
	private String ticker;
	
	public DataWrapper(Long createTs, Long beginTs, Long endTs, String ticker,
			String exchangeName, ConcurrentMap<Long, DataRecord> records) {
		super();
		this.ticker = ticker;
		this.records = records;
	}
	
	public DataWrapper() {
		// TODO Auto-generated constructor stub
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public ConcurrentMap<Long, DataRecord> getRecords() {
		return records;
	}
	public void setRecords(ConcurrentMap<Long, DataRecord> records) {
		this.records = records;
	}
	private ConcurrentMap<Long,DataRecord> records; //keyed by timestamp
}
