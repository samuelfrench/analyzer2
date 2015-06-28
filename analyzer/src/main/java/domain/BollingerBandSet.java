package domain;

import java.util.concurrent.ConcurrentMap;

public class BollingerBandSet {
	public enum TIME_UNIT {MINUTE, 
		FIVE_MINUTE, FIFTEEN_MINUTE, HOUR, DAY, WEEK};
	
	private String ticker;
	private TIME_UNIT unit;
	private Double stdDev;
	private Integer periods;
	
	/*
	 * This is a map of timestamp to bolinger band data
	 */
	ConcurrentMap<Long, BollingerBandRecord> records;
	
	public BollingerBandSet(){
		super();
	}
	public BollingerBandSet(String ticker, TIME_UNIT unit, 
			Double standardDev,	Integer periods) {
		super();
		this.ticker = ticker;
		this.unit = unit;
		this.stdDev = standardDev;
		this.periods = periods;
	}
	public BollingerBandSet(String ticker, TIME_UNIT unit, Double standardDev,
			Integer periods, ConcurrentMap<Long, BollingerBandRecord> records) {
		super();
		this.ticker = ticker;
		this.unit = unit;
		this.stdDev = standardDev;
		this.periods = periods;
		this.records = records;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public TIME_UNIT getUnit() {
		return unit;
	}

	public void setUnit(TIME_UNIT unit) {
		this.unit = unit;
	}

	public Double getStandardDev() {
		return stdDev;
	}

	public void setStandardDev(Double standardDev) {
		this.stdDev = standardDev;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public ConcurrentMap<Long, BollingerBandRecord> getRecords() {
		return records;
	}

	public void setRecords(ConcurrentMap<Long, BollingerBandRecord> records) {
		this.records = records;
	}
}
