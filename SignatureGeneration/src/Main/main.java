package Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import BaseSig.BaseSignature;
import BaseSig.Occurrence;
import BaseSig.Occurrences;
import BaseSig.SignatureType;
import FullSig.FullSig;
import FullSig.SignatureNode;
import InstanceData.IndividualProcessInstance;
import InstanceData.ProcessModel;

public class main {

	public static void main(String[] args) {

		//test process
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
		all.add(D);
 		
		BaseSignature baseSig = new BaseSignature(SignatureType.Before);
		
		Occurrences ocs = baseSig.generateOcurrencesForProcess(D, all);
		List<List<Occurrence>> forEachProcessOccurrence = ocs.individualNoiseFreeOccurrences();
		
		//count typical occurrence count, used when mapping the novel data on the signature
		long nonEmptyOccurrenceLists = forEachProcessOccurrence.stream().filter(x->!x.isEmpty()).count();
		long sumOfOccurrences = forEachProcessOccurrence.stream().mapToInt(x->x.size()).sum();
		long averageOccurrecesCount = sumOfOccurrences/nonEmptyOccurrenceLists;
		
		FullSig fullSig = new FullSig();
		SignatureNode sigStartNode = fullSig.mergeIntoSignature(D, SignatureType.Before, forEachProcessOccurrence);		
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
		
		return myCal1.getTime();*/
	}

}
