package Eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import BaseSig.BaseSignature;
import BaseSig.CutInstances;
import BaseSig.Occurrence;
import BaseSig.Occurrences;
import BaseSig.SignatureType;
import DataExtraction.Extract;
import DataExtractionSAX.ExtractSAXForBPIC2017;
import FullSig.FullSig;
import FullSig.SignatureNode;
import Helper.DateHelper;
import Helper.RandomHelper;
import Helper.Tuple;
import InstanceData.IndividualProcessInstance;
import InstanceData.ProcessModel;
import MatchingSig.Match;

public class DatePreparation {

	/*
	 * Read all process instances from the xes files
	 * 
	 * Split the data in Signature Generation and Test Data
	 * based on timespands. First timespan is Sig Gen. Second is Test.
	 * 
	 * Some Take % from the test Data and "anomaylize" them by:
	 * 1) Moving process instances around so that they take place on a different point in time
	 * 2) Copy process instances so that they occur directly after another
	 * 3) Copy process instance so that they occur massively in parralel
	 * 4) Change the "sourounding" of a specific process by adding to/removing the/adapting (timespan/dates) the processes that sourround it
	 * 
	 */
	private final Random rng = new Random();

	private void anomalyze(List<Occurrence> noiseFreeNormalOccurrences)
	{
		int whichApproach = rng.nextInt(4);
		
		switch (whichApproach) {
		case 0:
			anomalyzeCreateParallities(noiseFreeNormalOccurrences);
			break;
		case 1:
			anomalyzeCreateSequences(noiseFreeNormalOccurrences);
			break;	
		case 2:
			anomalyzeAlterExecutionOrder(noiseFreeNormalOccurrences);
			break;	
		case 3:
			createAnomalousVersionByAddingOrRemovingProcesses(noiseFreeNormalOccurrences);		
			break;
		default:
			System.out.println("Wrong anomalyzation approach!");
			break;
		}
	}
	
	//Move a instance that occurs in the occurrences around (i.e., backward or forward)
	//private static Occurrences anomalyzeMoveInstances(SignatureType type, Occurrences ocs)
	//{
		//ProcessModel mainProcess = ocs.getGeneratedFor();
		
		
		/*//all instances of the main model along with occurrence information
		List<IndividualProcessInstance> instancesInWindow = 
				ocs.getInstanceOccurrences().stream().map(x->x.getInstance()).collect(Collectors.toList());
		
		//adapt the instances to simulate an anomalie
		//we have to do this dynamically, so we search the earliest/latest date of occurrences in ocs
		//then based on this we move some of the individual process instances around
		Date start = ocs.startOfOccurrences();
		Date end = ocs.endOfOccurrences();
		
		int anomaliesToCreate = (int) ((int)instancesInWindow.size()*0.5);
		
		anomaliesToCreate = Math.max(anomaliesToCreate, 10);
		
		List<IndividualProcessInstance> randomInstances = instancesInWindow.stream()
				  .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
				      Collections.shuffle(collected);
				      return collected.stream();
				  }))
				  .limit(anomaliesToCreate)
				  .collect(Collectors.toList());
		
		//differenz zwischen start/ende der occurrence und der Instanz berechnen
		
		//remove the randomly chosen instances, in the following they are replaced
		//by counterpatres that contain anomalies
		instancesInWindow.removeAll(randomInstances);
		
		for(IndividualProcessInstance eachInstance : randomInstances)
		{
			//alter start/end
			/*long timeRangeStart = Math.abs(start.getTime()-eachInstance.getStart().getTime());
			long timeRangeEnd = Math.abs(end.getTime() - eachInstance.getEnd().getTime());
			
			long durationOfInstance = eachInstance.getEnd().getTime()-eachInstance.getStart().getTime();
			
			long amountOfTasks = eachInstance.getAmountOfTasks();
			IndividualProcessInstance anomaly = null;
			
			if(timeRangeStart>timeRangeEnd)
			{
				Date startAnoamlousInstance = start;
				Date endOfAnomalousInstance = new Date(start.getTime()+durationOfInstance);
				
				anomaly = new IndividualProcessInstance(startAnoamlousInstance, endOfAnomalousInstance, amountOfTasks);
			}
			else
			{
				Date startAnoamlousInstance = new Date(end.getTime()-durationOfInstance);
				Date endOfAnomalousInstance = end;
				
				anomaly = new IndividualProcessInstance(startAnoamlousInstance, endOfAnomalousInstance, amountOfTasks);
			}
			
			anomaly.setBelongsTo(eachInstance.getBelongsToModelName());

			instancesInWindow.add(anomaly);*/
			
			//add/remove instances
		/*	Date instanceStart = eachInstance.getStart();
			Date instanceEnd = eachInstance.getEnd();
					
			long amountOfTasks = eachInstance.getAmountOfTasks();
			
			IndividualProcessInstance anomaly = new IndividualProcessInstance(
					instanceStart, instanceEnd, amountOfTasks);
			anomaly.setBelongsTo(UUID.randomUUID().toString());

			instancesInWindow.add(anomaly);*/
		/*}
				
		//we have to construct Process models from the instances
		//This requires to aggregate multiple instances of the same model in the same Process model object
		//this map helps to do this
		Map<String, List<IndividualProcessInstance>> instancesToProcessMap = new HashMap<String, List<IndividualProcessInstance>>();
		
		for(IndividualProcessInstance eachInstance : instancesInWindow)
		{
			String processName =  eachInstance.getBelongsToModelName();
			
			List<IndividualProcessInstance> inatencesForModel = null;

			if(instancesToProcessMap.containsKey(processName))
			{
				inatencesForModel = instancesToProcessMap.get(processName);
			}
			else
			{
				inatencesForModel = new ArrayList<>();
			}
			
			inatencesForModel.add(eachInstance);				
			instancesToProcessMap.put(processName, inatencesForModel);
		}
		
		List<ProcessModel> models = instancesToProcessMap.entrySet().stream().map((x)->{
			
			String processName = x.getKey();
			List<IndividualProcessInstance> inastances = x.getValue();
			
			return new ProcessModel(processName, inastances);
		}).collect(Collectors.toList());
		
		BaseSignature baseSig = new BaseSignature(type);
		Occurrences oscAnomalized = baseSig.generateOcurrencesForProcess(
				mainProcess, models);
		
		return oscAnomalized;
	}*/
	
	//copy a instance so that it occurs multiple times roughly at the same point in time as the original one 
	private static boolean anomalyzeCreateParallities(List<Occurrence> noiseFreeNormalOccurrences)
	{
		//take a single occurrence and copy it multiple times on all occurrences to mimic parallities		
		for(int i=0;i<noiseFreeNormalOccurrences.size();i++)
		{
			Occurrence eachOccurrence = noiseFreeNormalOccurrences.get(i);
			//copy, else the adds below would also end up at this list
			List<CutInstances> cuts = new ArrayList<CutInstances>(eachOccurrence.getCutInstances());

			for(int j=0;j<noiseFreeNormalOccurrences.size() || j<10;j++)
			{
				eachOccurrence.getCutInstances().addAll(cuts);
			}
		}
		
		return true;
	}
	
	//copy instances so that they occur multiple times, but with an offset, e.g., after another
	private static boolean anomalyzeCreateSequences(List<Occurrence> noiseFreeNormalOccurrences)
	{		
		List<CutInstances> cuts = noiseFreeNormalOccurrences.get(0).getCutInstances();
		
		//take the cut instances from the first occurrence and copy them onto all other occurrences once
		for(int i=1;i<noiseFreeNormalOccurrences.size();i++)
		{
			Occurrence eachOccurrence = noiseFreeNormalOccurrences.get(i);

			for(int j=1;j<3;j++)
			{
				eachOccurrence.getCutInstances().addAll(cuts);
			}
		}
		
		return true;
	}
	
	//Change order of occurrences, simply do this by suffeling their order
	private static void anomalyzeAlterExecutionOrder(List<Occurrence> noiseFreeNormalOccurrences)
	{
		Collections.shuffle(noiseFreeNormalOccurrences);
	}
	
	//remove or add instances
	private static void createAnomalousVersionByAddingOrRemovingProcesses(List<Occurrence> noiseFreeNormalOccurrences)
	{				
		for(int i=0;i<noiseFreeNormalOccurrences.size();i++)
		{
			Occurrence eachOccurrence = noiseFreeNormalOccurrences.get(i);
			
				List<CutInstances> cutInstances = eachOccurrence.getCutInstances();
				
				//Collections.shuffle(cutInstances);
				
				//eines wird zufällig entfernt, eines unten hinzugefügt
				
				int originalSize = cutInstances.size();
				
				if(i%2==0)
				{
					for(int j=0;j<originalSize;j++)
					{
					cutInstances.remove(0);
					


				//create an artifical anomaly cut instance
				//only needs a name and no instances because the matching only reads the name from the model
				//so no need to come up with fake instances
					for(int y=1;y<3;y++)
					{
						ProcessModel anomalousDummyModel = new ProcessModel(UUID.randomUUID().toString());
						CutInstances anomalous = new CutInstances(anomalousDummyModel, 1);
						
						cutInstances.add(anomalous);					}

					}	
				}
				else
				{
					
					for(int j=0;j<originalSize || j<10;j++)
					{					
						cutInstances.add(cutInstances.get(0));
					}
				}
		
		}
		
	}
	
	AtomicInteger totalChecks = new AtomicInteger();

	AtomicInteger falsePositives = new AtomicInteger();
	AtomicInteger trueNegatives = new AtomicInteger();
	AtomicInteger falseNegative = new AtomicInteger();
	AtomicInteger truePositive = new AtomicInteger();
	
	public void readDataAndEvaluateData()
	{
		List<ProcessModel> processInstance = Extract.analyzeXES();
		//Extract read = new Extract();
		//List<ProcessModel> processInstance = read.analyzeXES();
		
		List<Integer> yearsFound = processInstance.stream().map(x->x.yearsProcess()).flatMap(x-> x.stream()).distinct().collect(Collectors.toList());

		//System.out.println("Covered years:"+yearsFound);
		
		//extract data from a specitic year
		//int yearToExtract = 2014; 
		
		//filter for timespan
		//remove empty processinstances that did not have a single execution at the timespan
		/*List<ProcessModel> processInstanceFILTERED = 
				processInstance.stream().map(
						x->x.limitToTimespan(DateHelper.firstDayYear(yearToExtract), DateHelper.lastDayYear(yearToExtract)))
				.filter(x->x.hasContent())
				.collect(Collectors.toList());*/
		
		List<ProcessModel> processInstanceFILTERED = processInstance;
		List<ProcessModel> piTrain = new ArrayList<>();
		List<ProcessModel> piTest = new ArrayList<>();

		processInstance.stream().forEach(x->{
			Tuple<ProcessModel, ProcessModel> splitted = x.splitRandomly();
			
			piTrain.add(splitted.first);
			piTest.add(splitted.second);

		});
		
		//split 50/50 into training and test data
		/*Use the original whole process processInstanceFILTERED when generating the signatures
		 *Or when validating the signatures for "old" behavior/sourounding behavior
		 *
		 *Use the filtered train/test stuff as main processes for sig generation/sig validation
		 *
		 *This enables that the sourounding is intact during sig generation and validation (nothing is cut off e.g. at the borders)
		 *TODO: Maybe we have to ignore stuff at the later end of the test data because there the end is still cut off or not available
		 *as a workaround we can e.g., only check for before sigs for them
		 */
		//first half of the year
		/*List<ProcessModel> piTrain = 
				processInstance.stream().map(
						x->x.limitToTimespan(DateHelper.firstDayYear(yearToExtract), DateHelper.middleDayYear(yearToExtract)))
				.filter(x->x.hasContent())
				.collect(Collectors.toList());
		
		//second half ot the year
		List<ProcessModel> piTest = 
				processInstance.stream().map(
						x->x.limitToTimespan(DateHelper.middleDayYear(yearToExtract), DateHelper.lastDayYear(yearToExtract)))
				.filter(x->x.hasContent())
				.collect(Collectors.toList());*/
		
		//processInstance.clear();//we do no longer need it
		
		// Evlalute how it behaves on non anomlaous data
		/* Select a process
		 * Generate a signature for the process
		 * Select the same process in the test data
		 * Validate test data instances of that process against the signature
		 * They should identified as non anomalaous 
		 */
		
		//int totalChecks =0 , anomalous =0, nonAnomalous=0;
		AtomicInteger pmChecks = new AtomicInteger();

		SignatureType type = SignatureType.Before;
		
		piTrain.parallelStream().forEach(y->{
			ProcessModel eachTrain = y;
						
			//basted on the name
			Optional<ProcessModel> relatedInTestData = piTest.stream().filter(x->x.equals(eachTrain)).findFirst();
			
			if(!relatedInTestData.isPresent())
			{
				//use continue in normal loop
				//return in stream foreach will not end the whole loop but just a single iteration
				return;
			}
			
			Occurrences ocsTrain;
			SignatureNode sigStartNode;
			{
				BaseSignature baseSigTrain = new BaseSignature(type);
				
				ocsTrain = baseSigTrain.generateOcurrencesForProcess(eachTrain, processInstanceFILTERED);
				List<List<Occurrence>> forEachProcessOccurrence = ocsTrain.individualNoiseFreeOccurrences();
				
				//count typical occurrence count, used when mapping the novel data on the signature
				long nonEmptyOccurrenceLists = forEachProcessOccurrence.stream().filter(x->!x.isEmpty()).count();
				long sumOfOccurrences = forEachProcessOccurrence.stream().mapToInt(x->x.size()).sum();
				//long averageOccurrecesCount = sumOfOccurrences/nonEmptyOccurrenceLists;
				
				FullSig fullSig = new FullSig();
				sigStartNode = fullSig.mergeIntoSignature(eachTrain, type, forEachProcessOccurrence);
			}
			
				BaseSignature baseSigTest = new BaseSignature(type);
				Occurrences ocsTestNormal = baseSigTest.generateOcurrencesForProcess(relatedInTestData.get(), processInstanceFILTERED);
			
				//first check the stuff as normal
				{
					List<List<Occurrence>> forEachProcessOccurrenceTestNormal = ocsTestNormal.individualNoiseFreeOccurrences();
				
					for(List<Occurrence> eachTestInstanceNormal : forEachProcessOccurrenceTestNormal)
					{
						if(!(eachTestInstanceNormal.size()>0))
						{
							System.out.println("no data left!");
							continue;
						}
						
						totalChecks.incrementAndGet(); 

						if(RandomHelper.random.nextBoolean())
						{
							Match match = new Match();
							boolean anomalyFound = match.match(type, sigStartNode, eachTestInstanceNormal, ocsTrain);
												
							if(anomalyFound)
							{
								falsePositives.incrementAndGet();
							}
							else
							{
								trueNegatives.incrementAndGet();
							}
							
						}
						else
						{
							List<Occurrence> eachTestInstanceABNORMAL = eachTestInstanceNormal;
							anomalyze(eachTestInstanceABNORMAL); //modifies directly the list
							
							Match match2 = new Match();
							boolean anomalyFound2 = match2.match(type, sigStartNode, eachTestInstanceABNORMAL, ocsTrain);
							
							if(anomalyFound2)
							{
								truePositive.incrementAndGet();
							}
							else
							{
								falseNegative.incrementAndGet();
							}		
						}	
					}
				}
						
			pmChecks.incrementAndGet();

			System.out.println(
					"totalChecks:"+totalChecks.get() +  "pm checks " + pmChecks.get() + " From " + piTrain.size());
			System.out.println(" truePositive:"+truePositive.get() +
					" trueNegatives:"+trueNegatives.get() +
					" falsePositives:"+falsePositives.get() +
					" falseNegative:"+falseNegative.get() );

			QualityCalc calc = new QualityCalc(truePositive.get(), falsePositives.get(), trueNegatives.get(), falseNegative.get());
			calc.calculateAndPrintQuality();
		});
		
		System.out.print("Done!");
	}	
}
