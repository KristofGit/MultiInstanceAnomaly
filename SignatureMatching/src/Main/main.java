package Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import BaseSig.BaseSignature;
import BaseSig.Occurrence;
import BaseSig.Occurrences;
import BaseSig.SignatureType;
import FullSig.FullSig;
import FullSig.SignatureNode;
import Helper.Tuple;
import InstanceData.IndividualProcessInstance;
import InstanceData.ProcessModel;
import MatchingSig.Match;

public class main {

	public static void main(String[] args) {

		SignatureNode sigStartNode = null;
		Occurrences ocs;
		{
			Tuple<ProcessModel, List<ProcessModel>> sigGenData = getSigGenData();
			List<ProcessModel> sigGenAll = sigGenData.second;
			ProcessModel sigGenMain = sigGenData.first;
			
			BaseSignature baseSig = new BaseSignature(SignatureType.Before);
			
			ocs = baseSig.generateOcurrencesForProcess(sigGenMain, sigGenAll);
			List<List<Occurrence>> forEachProcessOccurrence = ocs.individualNoiseFreeOccurrences();
			
			//count typical occurrence count, used when mapping the novel data on the signature
			long nonEmptyOccurrenceLists = forEachProcessOccurrence.stream().filter(x->!x.isEmpty()).count();
			long sumOfOccurrences = forEachProcessOccurrence.stream().mapToInt(x->x.size()).sum();
			long averageOccurrecesCount = sumOfOccurrences/nonEmptyOccurrenceLists;
			
			FullSig fullSig = new FullSig();
			sigStartNode = fullSig.mergeIntoSignature(sigGenMain, SignatureType.Before, forEachProcessOccurrence);
		}
		
		
		Tuple<ProcessModel, List<ProcessModel>> newDateToMatch = getNewData();
		
		
		List<ProcessModel> matchAll = newDateToMatch.second;
		ProcessModel matchMain = newDateToMatch.first;
		
		BaseSignature matchPrepbaseSig = new BaseSignature(SignatureType.Before);
		
		Occurrences matchPrepocs = matchPrepbaseSig.generateOcurrencesForProcess(matchMain, matchAll);
		List<List<Occurrence>> forEachProcessOccurrence = matchPrepocs.individualNoiseFreeOccurrences();

		List<Occurrence> newData = forEachProcessOccurrence.get(0);
		
		Match match = new Match();
		boolean anomalyFound = match.match(SignatureType.Before, sigStartNode, newData, ocs);
		
		System.out.println("Anomaly found:"+anomalyFound);
	}
	
	public static Tuple<ProcessModel, List<ProcessModel>> getNewData()
	{
		//use occurrences that can not be part of the signature
		//therefore we add a new process instance with a new name X
		List<IndividualProcessInstance> bi = new ArrayList<>();
		bi.add(new IndividualProcessInstance(getTestDate(0,30), getTestDate(4,45), 10));
		ProcessModel b = new ProcessModel("b", bi);
		
		List<IndividualProcessInstance> ci = new ArrayList<>();
		ci.add(new IndividualProcessInstance(getTestDate(2,30), getTestDate(6,30), 12));
		ProcessModel c = new ProcessModel("c", ci);
		
		List<IndividualProcessInstance> ai = new ArrayList<>();
		ai.add(new IndividualProcessInstance(getTestDate(1,0), getTestDate(4,0), 8));
		ai.add(new IndividualProcessInstance(getTestDate(4,50), getTestDate(6,15), 3));
		ProcessModel a = new ProcessModel("a", ai);

		List<IndividualProcessInstance> xi = new ArrayList<>();
		xi.add(new IndividualProcessInstance(getTestDate(0,30), getTestDate(6,30), 24));
		ProcessModel x = new ProcessModel("X", xi);
		
		//dummy main process for before signature generation
		List<IndividualProcessInstance> di = new ArrayList<>();
		di.add(new IndividualProcessInstance(getTestDate(7,0), getTestDate(14,0), 8));
		ProcessModel D = new ProcessModel("D", di);

		List<ProcessModel> all = new ArrayList<>();
		all.add(a);
		all.add(b);
		all.add(c);
		all.add(x);
		all.add(D);
		
		//different parellities, so that similar occurrences are observable but e.g., one of the process occurs more often 
		//then in the signature
		//simulated by addion an addigonal process "a" that overapps with all others
		/*List<IndividualProcessInstance> bi = new ArrayList<>();
		bi.add(new IndividualProcessInstance(getTestDate(0,30), getTestDate(4,45), 10));
		ProcessInstances b = new ProcessInstances("b", bi);
		
		List<IndividualProcessInstance> ci = new ArrayList<>();
		ci.add(new IndividualProcessInstance(getTestDate(2,30), getTestDate(6,30), 12));
		ProcessInstances c = new ProcessInstances("c", ci);
		
		List<IndividualProcessInstance> ai = new ArrayList<>();
		ai.add(new IndividualProcessInstance(getTestDate(1,0), getTestDate(4,0), 8));
		ai.add(new IndividualProcessInstance(getTestDate(4,50), getTestDate(6,15), 3));
		ai.add(new IndividualProcessInstance(getTestDate(0,45), getTestDate(6,20), 10)); //<----- overlapps
		ProcessInstances a = new ProcessInstances("a", ai);

		//dummy main process for before signature generation
		List<IndividualProcessInstance> di = new ArrayList<>();
		di.add(new IndividualProcessInstance(getTestDate(7,0), getTestDate(14,0), 8));
		ProcessInstances D = new ProcessInstances("D", di);

		List<ProcessInstances> all = new ArrayList<>();
		all.add(a);
		all.add(b);
		all.add(c);
		all.add(D);*/
		
		//Data matches sig partly (same occurrences) but the occurrences have a different order
		//spiegelung des 1 zu 1 matches
		
		/*List<IndividualProcessInstance> ci = new ArrayList<>();
		ci.add(new IndividualProcessInstance(getTestDate(0,30), getTestDate(6,00), 12));
		ProcessInstances c = new ProcessInstances("c", ci);
		
		List<IndividualProcessInstance> ai = new ArrayList<>();
		ai.add(new IndividualProcessInstance(getTestDate(1,0), getTestDate(2,30), 8));
		ai.add(new IndividualProcessInstance(getTestDate(3,30), getTestDate(7,30), 3));
		ProcessInstances a = new ProcessInstances("a", ai);

		List<IndividualProcessInstance> bi = new ArrayList<>();
		bi.add(new IndividualProcessInstance(getTestDate(2,40), getTestDate(8,00), 20));
		ProcessInstances b = new ProcessInstances("b", bi);

		//dummy main process for before signature generation
		List<IndividualProcessInstance> di = new ArrayList<>();
		di.add(new IndividualProcessInstance(getTestDate(8,30), getTestDate(14,0), 8));
		ProcessInstances D = new ProcessInstances("D", di);

		List<ProcessInstances> all = new ArrayList<>();
		all.add(a);
		all.add(b);
		all.add(c);
		all.add(D);*/
		
		//data matches sig one by one
		//test process for SIG generation
		/*List<IndividualProcessInstance> bi = new ArrayList<>();
		bi.add(new IndividualProcessInstance(getTestDate(0,30), getTestDate(4,45), 10));
		ProcessInstances b = new ProcessInstances("b", bi);
		
		List<IndividualProcessInstance> ci = new ArrayList<>();
		ci.add(new IndividualProcessInstance(getTestDate(2,30), getTestDate(6,30), 12));
		ProcessInstances c = new ProcessInstances("c", ci);
		
		List<IndividualProcessInstance> ai = new ArrayList<>();
		ai.add(new IndividualProcessInstance(getTestDate(1,0), getTestDate(4,0), 8));
		ai.add(new IndividualProcessInstance(getTestDate(4,50), getTestDate(6,15), 3));
		ProcessInstances a = new ProcessInstances("a", ai);

		//dummy main process for before signature generation
		List<IndividualProcessInstance> di = new ArrayList<>();
		di.add(new IndividualProcessInstance(getTestDate(7,0), getTestDate(14,0), 8));
		ProcessInstances D = new ProcessInstances("D", di);

		List<ProcessInstances> all = new ArrayList<>();
		all.add(a);
		all.add(b);
		all.add(c);
		all.add(D);*/
		
		return new Tuple<ProcessModel, List<ProcessModel>>(D, all);
	}
	
	public static Tuple<ProcessModel, List<ProcessModel>> getSigGenData()
	{
		//test process for SIG generation
		List<IndividualProcessInstance> bi = new ArrayList<>();
		bi.add(new IndividualProcessInstance(getTestDate(0,30), getTestDate(4,45), 10));
		ProcessModel b = new ProcessModel("b", bi);
		
		List<IndividualProcessInstance> ci = new ArrayList<>();
		ci.add(new IndividualProcessInstance(getTestDate(2,30), getTestDate(6,30), 12));
		ProcessModel c = new ProcessModel("c", ci);
		
		List<IndividualProcessInstance> ai = new ArrayList<>();
		ai.add(new IndividualProcessInstance(getTestDate(1,0), getTestDate(4,0), 8));
		ai.add(new IndividualProcessInstance(getTestDate(4,50), getTestDate(6,15), 3));
		ProcessModel a = new ProcessModel("a", ai);

		//dummy main process for before signature generation
		List<IndividualProcessInstance> di = new ArrayList<>();
		di.add(new IndividualProcessInstance(getTestDate(7,0), getTestDate(14,0), 8));
		ProcessModel D = new ProcessModel("D", di);

		List<ProcessModel> all = new ArrayList<>();
		all.add(a);
		all.add(b);
		all.add(c);
		all.add(D);
		
		return new Tuple<ProcessModel, List<ProcessModel>>(D, all);
	}
	
	public static Date getTestDate(int hour, int minute)
	{
		Calendar myCal1 = Calendar.getInstance();               
		myCal1.set(Calendar.DAY_OF_MONTH, 1);
		myCal1.set(Calendar.MONTH, 1);
		myCal1.set(Calendar.YEAR, 2000);
		myCal1.set(Calendar.SECOND, 1);
		
		myCal1.set(Calendar.HOUR_OF_DAY, hour);
		myCal1.set(Calendar.MINUTE, minute);
		
		return myCal1.getTime();
	}

}
