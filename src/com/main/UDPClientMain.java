package com.main;

import com.Operation;
import com.client.UDPClient;
import com.uw.adc.rmi.model.Stats;
import com.uw.adc.rmi.util.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import static com.uw.adc.rmi.util.Constants.UDP_STATS_LOGGER;

/**
 * Created by Anurita on 10/20/2016.
 */
public class UDPClientMain {
    public static void main(String args[]) {

        try {
            System.out.println("Received args: " + Arrays.toString(args));

            // String csvFile = "D:\\Anurita\\UW\\Fall 2016\\ADC\\test1.csv"; //kvp-operations.csv";
            String csvFile = args[2];
            String host = args[0];
            String port = args[1];
            System.out.println(String.format("Will now connect to server:port - %s:%s", host, port));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));
            UDPClient udpClient = new UDPClient();
            String currentData;
            while ((currentData = bufferedReader.readLine()) != null) {

                udpClient.encodePacket(currentData, host, port);
            }

            UDPClientMain.computePerformance(udpClient);

        } catch (Exception e) {

            System.out.println("Client error: " + e);
        }
    }

    private static void computePerformance(UDPClient udpClient) {

        Constants.UDP_STATS_LOGGER.info("---------PERFORMANCE ANALYSIS---------");

        int i = 0;
        long getTotalTime = 0, putTotalTime = 0, delTotalTime = 0;
        int getRequestCount = 0, putRequestCount = 0, delRequestCount = 0;
        long getAvgTime, putAvgTime, delAvgTime;
        List<Stats> statsList = udpClient.getStatsList();
        while (i < statsList.size()) {

            Stats statsObj = statsList.get(i);
            Operation op = Operation.fromValue(statsObj.getOperation());
            switch (op) {
                case GET:
                    getTotalTime = getTotalTime + statsObj.getTime();
                    ++getRequestCount;
                    break;
                case PUT:
                    putTotalTime = putTotalTime + statsObj.getTime();
                    ++putRequestCount;
                    break;
                case DELETE:
                    delTotalTime = delTotalTime + statsObj.getTime();
                    ++delRequestCount;
            }

            ++i;
        }

        if (getRequestCount > 0)
            UDP_STATS_LOGGER.info("Average Compute time for UDP GET request:" + getTotalTime / getRequestCount + "ms");
        if (putRequestCount > 0)
            UDP_STATS_LOGGER.info("Average Compute time for UDP PUT request:" + putTotalTime / putRequestCount + "ms");
        if (delRequestCount > 0)
            UDP_STATS_LOGGER.info("Average Compute time for UDP DELETE request:" + delTotalTime / delRequestCount + "ms");

        UDP_STATS_LOGGER.info("---------PERFORMANCE ANALYSIS COMPLETE---------");

    }
}
