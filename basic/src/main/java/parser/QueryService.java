package parser;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import domain.DataWrapper;

public interface QueryService {
	public DataWrapper getDataWrapper(final String tickerSymbol);
	public ConcurrentMap<String, DataWrapper> getDataWrapperMap(final List<String> tickerSymbol);
}