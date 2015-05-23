package output.csv.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import parser.support.DataWrapperTrendFunc;
import domain.DataWrapper;
import domain.SMAMomentumBoolMatrix;
import domain.SMAMomentumBoolMatrix.SHIFT;

public class DebugWriteSMA {
	
	
	/*
	 * TODO - 
	 * 
	 * Need to write a very similar method to write the shift directions to a file to verify
	 */
	
	
	
	
	
	
	public static boolean writeSMAAndShiftMatrixToFile(final DataWrapper dataWrapper, final SMAMomentumBoolMatrix matrix,final String fileName){
		
		List<Long> timeStamps = DataWrapperTrendFunc.getListOfTimestamps(dataWrapper.getsMAMatrix().get());
		ConcurrentMap<Long, ConcurrentMap<Long,Optional<Double>>> rawSMA = dataWrapper.getsMAMatrix().get();
		ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shiftMap = DataWrapperTrendFunc.getMomentumMatrix(dataWrapper).getShift();
		//prepare file io

		CSVFormat format = CSVFormat.MYSQL;
		FileWriter fileWriter = null;
		CSVPrinter writer;
		try{
			fileWriter = new FileWriter(fileName);
			writer = new CSVPrinter(fileWriter, format);
		} catch (IOException e){
			e.printStackTrace();
			return true;
		}
		try {
			writer.println();
			for(Long t: timeStamps){
				List<Long> periods = rawSMA.get(t).keySet().stream().sorted().collect(Collectors.toList());
				writer.print("Timestamp: " + t.toString() + ",");
				for(Long p: periods){
					writer.print("p: " + p + ", val: " + rawSMA.get(t).get(p).get());
				}
				writer.println();
			}
		}catch (IOException e) {
			e.printStackTrace();
			try {
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return true;
		}
		
		try {
			writer.println();
			for(Long t: timeStamps){
				List<Long> periods = rawSMA.get(t).keySet().stream().sorted().collect(Collectors.toList());
				writer.print("Timestamp: " + t.toString() + ",");
				for(Long p: periods){
					writer.print("p: " + p + ", val: " + shiftMap.get(t).get(p).toString());
				}
				writer.println();
			}
		}catch (IOException e) {
			e.printStackTrace();
			try {
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return true;
		}
		
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
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
