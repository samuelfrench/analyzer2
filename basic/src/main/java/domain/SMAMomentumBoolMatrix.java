package domain;

import java.util.concurrent.ConcurrentMap;

public class SMAMomentumBoolMatrix {
	
	/*this tells us if our SMA of the rate of change between 
	 *the difference between the high and low values of a period(SMA_r)
	 *is moving upward or down within the period p (H-L)/2
	 *
	 *NA means not applicable, empty means to be filled
	 */
	public static enum SHIFT {UP, DOWN, FLAT, NA, EMPTY};
	
	ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shift; //within current period
	
	
	public static enum SIGNAL {YES,NO}; //yes = shift is in opposite direction from previous interval
	//no - trend has not reversed
	
	ConcurrentMap<Long, ConcurrentMap<Long, SIGNAL>> signal;
	public synchronized ConcurrentMap<Long, ConcurrentMap<Long, SIGNAL>> getSignal() {
		return signal;
	}
	public synchronized void setSignal(
			ConcurrentMap<Long, ConcurrentMap<Long, SIGNAL>> signal) {
		this.signal = signal;
	}
}
