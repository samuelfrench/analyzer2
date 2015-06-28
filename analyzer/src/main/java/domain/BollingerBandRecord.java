package domain;

public class BollingerBandRecord {
	private Double upperBand;
	private Double lowerBand;
	private Double close;
	public BollingerBandRecord(Double upperBand, Double lowerBand, Double close) {
		super();
		this.upperBand = upperBand;
		this.lowerBand = lowerBand;
		this.close = close;
	}
	public BollingerBandRecord(Double close) {
		this.close = close;
	}
	public BollingerBandRecord() {
		
	}
	public Double getUpperBand() {
		return upperBand;
	}
	public void setUpperBand(Double upperBand) {
		this.upperBand = upperBand;
	}
	public Double getLowerBand() {
		return lowerBand;
	}
	public void setLowerBand(Double lowerBand) {
		this.lowerBand = lowerBand;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}
}
