package Configuration;

import java.math.BigDecimal;

public class Config {

	//BPIC17
	
	public static final String PathToLogFiles = "/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Large Scale Anomaly Detection GIT/myproje/Evaluierung/Daten/BPIC 2017/applications/";

	public static final LogType LogSource = LogType.BPIC17;
	
	//public static final int slotWithNoiseCanceling = 2;
	//public static final int minProcessTasks = slotWithNoiseCanceling*2;//because start and end has noise canceling

	public static final int mitSlotCoverageSizePerOccurrence = 3; //process that is part of an occurrence must cover at least two slots
	public static final int minProcessTasks = mitSlotCoverageSizePerOccurrence*2;//because start and end has noise canceling

	public static final double WindowSize = 4 ; //x times the length of the process 
	
	
	//likelyhood used as an alteranative for the edge likelhood
	//if the searched node is not found as a sucessor of the current signature node
	//BUT is at least equally present in one of all the signature nodes
	public static final BigDecimal likelhoodNotDirectSucessor = new BigDecimal("0.60");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownBehavior = new BigDecimal("0.20");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownPunishment = new BigDecimal("0.20");
	
	//when the found compareable occurrence hast slightly differen parallities but same processes
	public static final BigDecimal likelhoodUnknownParellelity = new BigDecimal("0.50");

	
	//how many of the old most smimilar executions the current one (that is checked for anomalies) is compared to
	//to determine if its legit or not
	public static final double percentageOfMostSimilarOldOccurrencesToCompareTo = 0.2; 

	//mulitply with the found minimal propability in the observed data 
	public static final BigDecimal unterExepectedMinimalPropFaktor = new BigDecimal("1.2");
	
	
	//BPIC 2015
	
	/*public static final String PathToLogFiles = "/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Large Scale Anomaly Detection GIT/myproje/Evaluierung/Daten/BPIC 2015/";
	//public static final String PathToLogFiles = "/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Large Scale Anomaly Detection/Evaluierung/Daten/HEP/";

	public static final LogType LogSource = LogType.BPIC15;
	
	//public static final int slotWithNoiseCanceling = 2;
	//public static final int minProcessTasks = slotWithNoiseCanceling*2;//because start and end has noise canceling

	public static final int mitSlotCoverageSizePerOccurrence = 4; //process that is part of an occurrence must cover at least two slots
	public static final int minProcessTasks = mitSlotCoverageSizePerOccurrence*2;//because start and end has noise canceling

	public static final double WindowSize = 5; //x times the length of the process */
	


	//likelyhood used as an alteranative for the edge likelhood
	//if the searched node is not found as a sucessor of the current signature node
	//BUT is at least equally present in one of all the signature nodes
	//BPIC1
	/*public static final BigDecimal likelhoodNotDirectSucessor = new BigDecimal("0.65");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownBehavior = new BigDecimal("0.2");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownPunishment = new BigDecimal("0.35");
	
	//when the found compareable occurrence hast slightly differen parallities but same processes
	public static final BigDecimal likelhoodUnknownParellelity = new BigDecimal("0.55");

	
	//how many of the old most smimilar executions the current one (that is checked for anomalies) is compared to
	//to determine if its legit or not
	public static final double percentageOfMostSimilarOldOccurrencesToCompareTo = 0.2; 

	//mulitply with the found minimal propability in the observed data 
	public static final BigDecimal unterExepectedMinimalPropFaktor = new BigDecimal("0.00007");*/
	
	//BPIC2
	/*public static final BigDecimal likelhoodNotDirectSucessor = new BigDecimal("0.65");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownBehavior = new BigDecimal("0.2");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownPunishment = new BigDecimal("0.35");
	
	//when the found compareable occurrence hast slightly differen parallities but same processes
	public static final BigDecimal likelhoodUnknownParellelity = new BigDecimal("0.55");

	
	//how many of the old most smimilar executions the current one (that is checked for anomalies) is compared to
	//to determine if its legit or not
	public static final double percentageOfMostSimilarOldOccurrencesToCompareTo = 0.2; 

	//mulitply with the found minimal propability in the observed data 
	public static final BigDecimal unterExepectedMinimalPropFaktor = new BigDecimal("0.00005");*/
	
	
	//FOR BPIC 3
	/*public static final BigDecimal likelhoodNotDirectSucessor = new BigDecimal("0.65");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimacl likelhoodCompletelyUnknownBehavior = new BigDecimal("0.2");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownPunishment = new BigDecimal("0.35");
	
	//when the found compareable occurrence hast slightly differen parallities but same processes
	public static final BigDecimal likelhoodUnknownParellelity = new BigDecimal("0.55");

	
	//how many of the old most smimilar executions the current one (that is checked for anomalies) is compared to
	//to determine if its legit or not
	public static final double percentageOfMostSimilarOldOccurrencesToCompareTo = 0.2; 

	//mulitply with the found minimal propability in the observed data 
	public static final BigDecimal unterExepectedMinimalPropFaktor = new BigDecimal("0.0005");*/
	
	//BPIC 4
	/*public static final BigDecimal likelhoodNotDirectSucessor = new BigDecimal("0.65");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownBehavior = new BigDecimal("0.2");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownPunishment = new BigDecimal("0.35");
	
	//when the found compareable occurrence hast slightly differen parallities but same processes
	public static final BigDecimal likelhoodUnknownParellelity = new BigDecimal("0.55");

	
	//how many of the old most smimilar executions the current one (that is checked for anomalies) is compared to
	//to determine if its legit or not
	public static final double percentageOfMostSimilarOldOccurrencesToCompareTo = 0.2; 

	//mulitply with the found minimal propability in the observed data 
	public static final BigDecimal unterExepectedMinimalPropFaktor = new BigDecimal("0.00005");*/
	
	//BPIC5
	/*public static final BigDecimal likelhoodNotDirectSucessor = new BigDecimal("0.65");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownBehavior = new BigDecimal("0.2");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownPunishment = new BigDecimal("0.35");
	
	//when the found compareable occurrence hast slightly differen parallities but same processes
	public static final BigDecimal likelhoodUnknownParellelity = new BigDecimal("0.55");

	
	//how many of the old most smimilar executions the current one (that is checked for anomalies) is compared to
	//to determine if its legit or not
	public static final double percentageOfMostSimilarOldOccurrencesToCompareTo = 0.2; 

	//mulitply with the found minimal propability in the observed data 
	public static final BigDecimal unterExepectedMinimalPropFaktor = new BigDecimal("0.00005");*/
	

    //HEP
	/*//public static final String PathToLogFiles = "/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Large Scale Anomaly Detection/Evaluierung/Daten/BPIC/";
	public static final String PathToLogFiles = "/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Large Scale Anomaly Detection/Evaluierung/Daten/HEP/";

	public static final LogType LogSource = LogType.HEP;
	
	//public static final int slotWithNoiseCanceling = 2;
	//public static final int minProcessTasks = slotWithNoiseCanceling*2;//because start and end has noise canceling

	public static final int mitSlotCoverageSizePerOccurrence = 2; //process that is part of an occurrence must cover at least two slots
	public static final int minProcessTasks = mitSlotCoverageSizePerOccurrence*2;//because start and end has noise canceling

	public static final double WindowSize = 20; //x times the length of the process 
	
	
	//likelyhood used as an alteranative for the edge likelhood
	//if the searched node is not found as a sucessor of the current signature node
	//BUT is at least equally present in one of all the signature nodes
	public static final BigDecimal likelhoodNotDirectSucessor = new BigDecimal("0.6");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownBehavior = new BigDecimal("0.1");

	//if there was absolutely no change to find any representation of the observed behavior in the signature
	public static final BigDecimal likelhoodCompletelyUnknownPunishment = new BigDecimal("0.4");
	
	//when the found compareable occurrence hast slightly differen parallities but same processes
	public static final BigDecimal likelhoodUnknownParellelity = new BigDecimal("0.5");

	
	//how many of the old most smimilar executions the current one (that is checked for anomalies) is compared to
	//to determine if its legit or not
	public static final double percentageOfMostSimilarOldOccurrencesToCompareTo = 0.2; 

	//mulitply with the found minimal propability in the observed data 
	public static final BigDecimal unterExepectedMinimalPropFaktor = new BigDecimal("0.01"); */
}
