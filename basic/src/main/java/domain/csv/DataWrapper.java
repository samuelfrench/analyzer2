package domain.csv;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class DataWrapper {
	private Long createTs;
	private Long beginTs;
	private Long endTs;
	private String ticker;
	
	public DataWrapper(Long createTs, Long beginTs, Long endTs, String ticker,
			String exchangeName, ConcurrentMap<Long, DataRecord> records) {
		super();
		this.createTs = createTs;
		this.beginTs = beginTs;
		this.endTs = endTs;
		this.ticker = ticker;
		this.exchangeName = exchangeName;
		this.records = records;
	}
	
	public DataWrapper() {
		// TODO Auto-generated constructor stub
	}

	public Long getCreateTs() {
		return createTs;
	}
	public void setCreateTs(Long createTs) {
		this.createTs = createTs;
	}
	public Long getBeginTs() {
		return beginTs;
	}
	public void setBeginTs(Long beginTs) {
		this.beginTs = beginTs;
	}
	public Long getEndTs() {
		return endTs;
	}
	public void setEndTs(Long endTs) {
		this.endTs = endTs;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public ConcurrentMap<Long, DataRecord> getRecords() {
		return records;
	}
	public void setRecords(ConcurrentMap<Long, DataRecord> records) {
		this.records = records;
	}
	private String exchangeName;
	private ConcurrentMap<Long,DataRecord> records; //keyed by timestamp
	
	
	
	
	/**
	 * SMA Map
	 * 
	 * Outer map is Timestamp to SMA Values
	 * Inner Map is #of previous periods being considered
	 * 
	 * Warning: If the # of previous periods used in the calculation is not 
	 * 	available in the map (such as 10 periods for the first time point of the day, the value will return null)
	 * 
	 * 
	 */
	private Optional<ConcurrentMap<Long,ConcurrentMap<Integer,Double>>> sMACrossover;
	
	public Optional<ConcurrentMap<Long,ConcurrentMap<Integer,Double>>> getsMACrossover() {
		return sMACrossover;
	}

	public void setsMACrossover(Optional<ConcurrentMap<Long,ConcurrentMap<Integer,Double>>> sMACrossover) {
		this.sMACrossover = sMACrossover;
	}
}
