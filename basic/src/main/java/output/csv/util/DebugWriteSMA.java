package output.csv.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import domain.DataWrapper;

public class DebugWriteSMA {
	//true = error
	//false = success
	public static boolean writeSMAMatrixToFile(final DataWrapper dataWrapper, final String fileName){
		if(!dataWrapper.getsMAMatrix().isPresent()){
			return true;
		}
		ConcurrentMap<Long, String> tsLine = dataWrapper.getRecords().keySet().stream().sorted().distinct().collect(Collectors.toConcurrentMap((p)->p,  (p)->"empty"));
		ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>>  smaMatrix = dataWrapper.getsMAMatrix().get(); 
		
		CSVFormat format = CSVFormat.MYSQL;
		FileWriter fileWriter = null;
		
		try{
			fileWriter = new FileWriter(fileName);
			final CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, format);
			tsLine.keySet().stream().sorted().sequential().forEachOrdered((t)-> {
				StringBuilder sb = new StringBuilder("ts: ");
				sb.append(t.toString());
				ConcurrentMap<Long,Optional<Double>> mapForTs = smaMatrix.get(t);
				List<Long> lookBack = mapForTs.keySet().parallelStream().sorted().distinct().collect(Collectors.toList());
				for(Long l: lookBack){
					sb.append(",");
					sb.append("prd:");
					sb.append(l);
					if(mapForTs.get(l).isPresent()){
						sb.append(":");
						sb.append(mapForTs.get(l));
					} else {
						sb.append(":empty");
					}
				}
				try {
					csvFilePrinter.printRecord(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			});
			csvFilePrinter.flush();
			csvFilePrinter.close();
			try{
				fileWriter.close();
			} catch (IOException e){
				System.err.println(e.getLocalizedMessage() + " : " + " no need to close FileWriter");
			}
		} catch (IOException e){
			e.printStackTrace();
			return true;
		}
		return false;
	}
}
