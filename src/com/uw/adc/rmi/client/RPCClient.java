package com.uw.adc.rmi.client;

import com.uw.adc.rmi.RPC;
import com.uw.adc.rmi.model.DataTransfer;
import com.uw.adc.rmi.model.DataTransferImpl;
import com.uw.adc.rmi.model.Stats;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RPCClient {

	private static final Log logger = LogFactory.getLog(RPCClient.class);
	private static final Logger statsLogger = Logger.getLogger("RPC_CLIENT_STATISTICS_LOG");
	
	private List<Stats> statsList = new ArrayList<Stats>();
	
	public static void main(String args[]) 
    {		
		logger.debug("--------RPC PROCESS STARTED---------");		
		
		String host = (args.length < 1) ? null : args[0];
		String port = (args.length < 1) ? null : args[1];
				
        try 
        {       	
        	/*Registry registry = LocateRegistry.getRegistry(host);
            RPC stub = (RPC) registry.lookup("RPCServer");
            String response = stub.sayHello();
            System.out.println("response: " + response);*/ 
        	
        	RPCClient client = new RPCClient();
        	client.process(host, port);
        } 
        catch (Exception e) 
        { 
        	logger.error("RPCClient exception: " + e.getMessage()); 
           e.printStackTrace(); 
        } 
        
        logger.debug("--------RPC PROCESS ENDED---------");
    }
	
	public void process(String host, String port) throws RemoteException{
		
		logger.debug("Inside process()");
		
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader("input/Input.txt"));
			
			Registry registry = LocateRegistry.getRegistry(host);
            RPC stub = (RPC) registry.lookup("RPCServer");

			while ((currentLine = br.readLine()) != null) {
				logger.debug(currentLine);
				
				String strArray[] = currentLine.split(" ");
				if(strArray!=null && strArray.length > 0){
					
					DataTransfer request = new DataTransferImpl();
					if(strArray.length > 1 && strArray[1] != null){
						request.setKey(strArray[1]);
					}
					if(strArray.length > 2 && strArray[2] != null){
						request.setValue(strArray[2]);
					}
					
					switch (strArray[0]){
						case "GET": invokeRemoteGetMethod(stub, request);
									break;
						case "PUT": invokeRemotePutMethod(stub, request);
									break;
						case "DELETE": invokeRemoteDeleteMethod(stub, request);
					}
				}				
			}
			
			//Compute Performance
			computePerformance();

		} catch (IOException e) {
			logger.error(e.getStackTrace());
		} catch (Exception e){
			logger.error(e.getStackTrace());
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				logger.error(ex.getStackTrace());
			}
		}
		
	}
	
	private void invokeRemoteGetMethod(RPC stub, DataTransfer request) throws RemoteException{
		
		try{						
			Date beforeDate = new Date();
			stub.getData(request);
			Date afterDate = new Date();			
			logger.debug("Response:"+request.toString());
			
			long time = afterDate.getTime() - beforeDate.getTime();			
			Stats curentStats = new Stats("GET", time);
			statsList.add(curentStats);
			statsLogger.info(curentStats.toString());			
		}
		catch(Exception e){
			logger.error("Error in GET operation for "+request.toString());
			logger.error("Error Message:"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void invokeRemotePutMethod(RPC stub, DataTransfer request) throws RemoteException{
		
		try{			
			Date beforeDate = new Date();
			boolean response = stub.putData(request);
			Date afterDate = new Date();
			logger.debug("Response:"+response);
			
			long time = afterDate.getTime() - beforeDate.getTime();			
			Stats curentStats = new Stats("PUT", time);
			statsList.add(curentStats);
			statsLogger.info(curentStats.toString());						
		}
		catch(Exception e){
			logger.error("Error in PUT operation for "+request.toString());
			logger.error("Error Message:"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void invokeRemoteDeleteMethod(RPC stub, DataTransfer request) throws RemoteException{
		
		try{			
			Date beforeDate = new Date();
			boolean response = stub.deleteData(request);
			Date afterDate = new Date();
			logger.debug("Response:"+response);	
			
			long time = afterDate.getTime() - beforeDate.getTime();			
			Stats curentStats = new Stats("DELETE", time);
			statsList.add(curentStats);
			statsLogger.info(curentStats.toString());			
		}
		catch(Exception e){
			logger.error("Error in DELETE operation for "+request.toString());
			logger.error("Error Message:"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void computePerformance(){
		
		statsLogger.info("---------PERFORMANCE ANALYSIS---------");
		
		int i=0;		 
		long getTotalTime=0, putTotalTime=0, delTotalTime=0;
		int getRequestCount=0, putRequestCount=0, delRequestCount=0;
		long getAvgTime, putAvgTime, delAvgTime;
		
		while(i<statsList.size()){
			
			Stats statsObj = (Stats)statsList.get(i);
			switch (statsObj.getOperation()){
				case "GET":		getTotalTime = getTotalTime + statsObj.getTime();
								++getRequestCount;
								break;
				case "PUT":		putTotalTime = putTotalTime + statsObj.getTime();
								++putRequestCount;
								break;
				case "DELETE":	delTotalTime = delTotalTime + statsObj.getTime();
								++delRequestCount;								
			}
			
			++i;			
		}
		
		if(getRequestCount>0)statsLogger.info("Average Compute time for RPC GET request:"+getTotalTime/getRequestCount);
		if(putRequestCount>0)statsLogger.info("Average Compute time for RPC PUT request:"+putTotalTime/putRequestCount);
		if(delRequestCount>0)statsLogger.info("Average Compute time for RPC DELETE request:"+delTotalTime/delRequestCount);		
		
		statsLogger.info("---------PERFORMANCE ANALYSIS COMPLETE---------");
		
	}
}
