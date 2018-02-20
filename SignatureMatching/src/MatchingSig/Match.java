package MatchingSig;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import BaseSig.Occurrence;
import BaseSig.Occurrences;
import BaseSig.SignatureType;
import Configuration.Config;
import FullSig.SignatureNode;
import Helper.ListHelper;
import Helper.Tuple;
import InstanceData.ProcessModel;

public class Match {

	//TODO Apply a general punishment factor on the artifically generated likelhoods
	
	//assumes that the occurrences are alraeady ORDERED correctly
	//assume that the length of signature and occurrences are in line (e.g., trunaceted to a specific maximum length so that all matches length based)
	public boolean match(SignatureType type,
			SignatureNode signatureStartingNode, //the signature constructed from the log
			List<Occurrence> newOccurencesThatShouldMatchTheSig, //new occurrences
			Occurrences oldOccurrencesToCompareTo) //old behavior in the processes that can be used to get a
		//a likelihood value that can be compared
	{
		/* Compare the new occurrences with the signature, 
		 * You will get novel observation likelihood 
		 * 
		 * Construct a reference likelihood that can be compared with the novel observation likelihood
		 * If the novel observation likelihood is substantial below the reference likelihood then an anomaly was found
		 */
		
		/* To compute the novel observation likelihood:
		 * 
		 * 1)Map one step of the new Occurrences on the Signature 
		 * 2)Collect the Likelihood over the stepps and multiply them to aggregat them
		 * 3)Map the next step
		 * 
		 * Map AT MOST so many steps as typically were used from the old log data to construct the signature (use average steps)
		 * if the new date contains less, then only map the steps of the new data
		 */
		
		/* What to do if the mapping failes because the signature does not contain the same steps in the same order
		 * 1) The step is found is equally found at the signature, just at a different position then required
		 * "quick" traverse to the equal step. Apply a punishment factor because no likelhood from the conncetion edge can be applied.
		 * 
		 * 2) The step is not found at the signature
		 * 2.1) A similar step is found (just different parallelities)
		 * Search for similar steps, similar steps must at least have the same processes then the searched step
		 * but can have a different parellelity (more or less parellel executions). Apply a punishment factor that
		 * depends on the how different the parellities are
		 * 
		 * 2.2) A similar step is found (at least some of the observed processes occur at the sig and the novel behavior at the same time)
		 * Search for the most similar sig node found to the novel behavior occurrence. Use that similartiy as the likelyhood. 
		 *  
		 * 2.3) The step is not found (also no similar one)
		 * Apply a punishment factor and continue on waiting for the next step that we find again
		 * and then take this one and continue on from there
		 */
		
		if(type == SignatureType.Before)
		{
			Collections.reverse(newOccurencesThatShouldMatchTheSig);			
		}
				
		BigDecimal likelihoodNewBehavior = CompareBehaviorWithSig(signatureStartingNode, newOccurencesThatShouldMatchTheSig);
		
		
		BigDecimal referenceLizeklihood = GetReferenceLikelyhood(type, signatureStartingNode, newOccurencesThatShouldMatchTheSig, oldOccurrencesToCompareTo);
		
	//	System.out.println("Like New Beh:"+likelihoodNewBehavior);
	//	System.out.println("Like Ref Beh:"+referenceLizeklihood);

		//the referenceLizeklihood should be smaller, if it is not then this one return false
		return !(likelihoodNewBehavior.subtract(referenceLizeklihood).compareTo(BigDecimal.ZERO)>=0);
	}


	private BigDecimal GetReferenceLikelyhood(
			SignatureType type, SignatureNode signatureStartingNode,
			List<Occurrence> newOccurencesThatShouldMatchTheSig, Occurrences oldOccurrencesToCompareTo) {
		//calculat the refereence likelihood
		/* Therefore old occurrences from the logs are used
		 * Choose the occurrences that match best with based on:
		 * 1) Length, the length heavily influences the calculated likelyhood. So the length should be equal
		 * This should be ensured by the CALLER of this method
		 * 2) Processes, compare only with old occurrences that represent at least roughly the same processes
		 */

		List<Tuple<List<Occurrence>, BigDecimal>> oldOccurrencesAndTheirSimilarity = new ArrayList<>();
		
		//order old occurrences based on their similarity to the new one based on the type and amount of processes
		//that it contains
		for(List<Occurrence> oldInidivudalOccurrence : oldOccurrencesToCompareTo.individualNoiseFreeOccurrences())
		{
			BigDecimal similarity = simpleOccurrenceSimilarity(oldInidivudalOccurrence, newOccurencesThatShouldMatchTheSig);
			
			oldOccurrencesAndTheirSimilarity.add(new Tuple<List<Occurrence>, BigDecimal>(oldInidivudalOccurrence, similarity));
		}
		
		Comparator<Tuple<List<Occurrence>, BigDecimal>> byOccurrenceSim = (e1, e2) -> {
			return e1.second.compareTo(e2.second);
		};
		
		long limitTo = (long) (oldOccurrencesAndTheirSimilarity.size() * Config.percentageOfMostSimilarOldOccurrencesToCompareTo);
		limitTo = Math.max(limitTo, 1);//prevents that we limit it to 0
		List<List<Occurrence>> selectedOldOccurencesToCompareWith =oldOccurrencesAndTheirSimilarity.stream().sorted(byOccurrenceSim).limit(limitTo).map(x->x.first).collect(Collectors.toList());
		
		if(type == SignatureType.Before)
		{
			for(List<Occurrence> eachOldOccurrence : selectedOldOccurencesToCompareWith)
			{
				Collections.reverse(eachOldOccurrence);			
			}
		}
		
		
		Comparator<BigDecimal> comapreBigDec = (e1, e2) -> {
			return e1.compareTo(e2);
		};
		
		long occurrenceCountToMatch = newOccurencesThatShouldMatchTheSig.size();

		Optional<BigDecimal> minLikelyhood = selectedOldOccurencesToCompareWith.stream().map(x->
					CompareBehaviorWithSig(signatureStartingNode, 
					x.stream().limit(occurrenceCountToMatch).collect(Collectors.toList())
				)).min(comapreBigDec);
		
		/*Optional<BigDecimal> minLikelyhood2 = selectedOldOccurencesToCompareWith.stream().map(x->
		CompareBehaviorWithSig(signatureStartingNode, 
		x.stream().limit(occurrenceCountToMatch).collect(Collectors.toList())
	)).max(comapreBigDec);*/
		
		if(!minLikelyhood.isPresent())
		{
			return new BigDecimal(1.0);
		}
		
		BigDecimal minLikelyhoodOfLoggedBehavior = minLikelyhood.get();
		
		BigDecimal finalReferenceLihelihood = minLikelyhoodOfLoggedBehavior.multiply(Config.unterExepectedMinimalPropFaktor);
		
		return finalReferenceLihelihood;
	}


	private BigDecimal CompareBehaviorWithSig(SignatureNode signatureStartingNode,
			List<Occurrence> newOccurencesThatShouldMatchTheSig) {
		Set<SignatureNode> allSignatureNodes = new HashSet<>();
		
        Stack<SignatureNode> nodesToExplore = new Stack<>();
        nodesToExplore.push(signatureStartingNode);
        
        while(!nodesToExplore.isEmpty())
        {
        	SignatureNode nodes = nodesToExplore.pop();
        	
        	allSignatureNodes.add(nodes);
        	
        	List<SignatureNode> potentialNodes = nodes.getAllSigNodes();  	
        	potentialNodes.stream().filter(x -> !allSignatureNodes.contains(x)).forEach(x->nodesToExplore.push(x));        	
        }
			
		
		BigDecimal likelihoodNewBehavior = new BigDecimal("1");
		
		SignatureNode currentSignatureNode = signatureStartingNode;
		
		//Occurrence currentOccurrence = newOccurencesThatShouldMatchTheSig.get(0);

		for (int i = 0; i < newOccurencesThatShouldMatchTheSig.size(); i++) {
			
			Occurrence nextOccurrence = newOccurencesThatShouldMatchTheSig.get(i);

			Tuple<SignatureNode, BigDecimal> requiredSucessor = currentSignatureNode.hasEqualSucessorOccurrence(nextOccurrence);
			
			if(requiredSucessor == null)
			{
				//Search for !equal! step in the signature, apply punishment artifical likelyhood
				//There should be always at most one (or none)
				List<SignatureNode> signatureNodesThatPerfectlyMatchTheOccurrence = allSignatureNodes.stream().filter(
						x->x.hasEqualOccurrence(nextOccurrence)).collect(Collectors.toList());
				
				if(ListHelper.hasValue(signatureNodesThatPerfectlyMatchTheOccurrence))
				{
					SignatureNode node = signatureNodesThatPerfectlyMatchTheOccurrence.get(0);
					
					likelihoodNewBehavior = likelihoodNewBehavior.multiply(Config.likelhoodNotDirectSucessor);
					currentSignatureNode = node;
				}
				else
				{
					//search for an not equal but compareable signature with different parallel executions
					//but at lest each one of the processes in occurrece occurs once in it
					//there can be more then one
					//search for the one that has the highest fitness (least differences between the searched parallities 
					//and the found one based one 
					
					//IDEA: Additonal options would e.g. also to take some occurrences that do not contain all processes
					//then additonal punishments can be calculate based on the amount of not contained processes

					//there can be more then one, so identify the one 
					List<SignatureNode> signatureNodesCoverSameProcesses = 
							allSignatureNodes.stream().filter(x->x.coversSameProcesses(nextOccurrence)).collect(Collectors.toList());

					if(ListHelper.hasValue(signatureNodesCoverSameProcesses))
					{
						//search for the "best" one, so with the hightest fitness/likelhood
						List<Tuple<SignatureNode, BigDecimal>> nodeFitness = signatureNodesCoverSameProcesses.stream().map(
								x->new Tuple<SignatureNode, BigDecimal>(x,fitnessNonParellelMatchingOccurrences(x, nextOccurrence))).collect(Collectors.toList());
												
						//node with best likelihood
						Tuple<SignatureNode, BigDecimal> max = null;
						
						for(Tuple<SignatureNode, BigDecimal> nodes : nodeFitness)
						{
							if(max == null)
							{
								max = nodes;
							}
							else if((max.second.compareTo(nodes.second))==-1) //-1 is if the first is smaller then the second
							{
								max = nodes;
							}
						}
						
						//use the identified most similar node as a replacement
						likelihoodNewBehavior = likelihoodNewBehavior.multiply(max.second).multiply(Config.likelhoodUnknownParellelity);
						currentSignatureNode = max.first;
					}
					else
					{
						//search the most similar occurrence and use the differences as next likelihood
						//search for the "best" one, so with the hightest fitness/likelhood
						List<Tuple<SignatureNode, BigDecimal>> nodeFitness = allSignatureNodes.stream().map(
								x->new Tuple<SignatureNode, BigDecimal>(x,similarityOfCoveredProcesses(x, nextOccurrence))).collect(Collectors.toList());
												
						//node with best likelihood
						Tuple<SignatureNode, BigDecimal> max = null;
						
						for(Tuple<SignatureNode, BigDecimal> nodes : nodeFitness)
						{
							if(max == null)
							{
								max = nodes;
							}
							else if((max.second.compareTo(nodes.second))==-1) //-1 is if the first is smaller then the second
							{
								max = nodes;
							}
						}
						
						//use the identified most similar node as a replacement
						likelihoodNewBehavior = likelihoodNewBehavior.multiply(max.second).multiply(Config.likelhoodCompletelyUnknownPunishment);
						currentSignatureNode = max.first;
	
						
						
						
						//fall back to punishment likelihood
						//likelihoodNewBehavior = likelihoodNewBehavior.multiply(Config.likelhoodCompletelyUnknownBehavior);
						//can not be set => currentSignatureNode = requiredSucessor.first;
					}
				}
			}
			else
			{
				//update likelihood and transition to the new node
				likelihoodNewBehavior = likelihoodNewBehavior.multiply(requiredSucessor.second);
				currentSignatureNode = requiredSucessor.first;
			}
			
			//currentOccurrence = nextOccurrence;
		}
		
		return likelihoodNewBehavior;
	}
	
	//berechnen der unterschiede zwischen node und oc
	//IGNORIERT PARELLITÄTEN, zählt einfach wie viele Prozesse im NODE vorkommen die NICHT in OC sind und umgehekrt
	//und verwendet das als Ergebnis indem es durch die summe der abgedeckten unterschiedliche Prozesse
	//von NODE und OC dividiert wird.
	private BigDecimal similarityOfCoveredProcesses(SignatureNode node, Occurrence oc)
	{
		List<String> uniqueProcessesInNode = node.cutInstancesName.stream().distinct().collect(Collectors.toList());
		List<String> uniqueProcessesInOC = oc.getCutInstances().stream().map(x->x.getProcessThatWasCut().getName())
				.distinct().collect(Collectors.toList());
		
		long allDistinctProcessesCovered = uniqueProcessesInNode.size()+uniqueProcessesInOC.size();
		long differenceBetweenBothLists = uniqueProcessesInNode.stream().filter(x->!uniqueProcessesInOC.contains(x)).count();
		differenceBetweenBothLists += uniqueProcessesInOC.stream().filter(x->!uniqueProcessesInNode.contains(x)).count();

		if(differenceBetweenBothLists == 0 || allDistinctProcessesCovered == 0)
		{
			return new BigDecimal(1);
		}		
		else if(allDistinctProcessesCovered == differenceBetweenBothLists)
		{
			return Config.likelhoodCompletelyUnknownBehavior;
		}
		
		//if both are equal then the result is best
		BigDecimal result = new BigDecimal("1").subtract(new BigDecimal(differenceBetweenBothLists).divide(new BigDecimal(allDistinctProcessesCovered), MathContext.DECIMAL128));

		return result;
	}
	
	//annahme die prozesse sind gleich, daher oc und node denken zumindest die gleiche prozesse ab aber in unterschiedlicher menge (parallelitäten)
	private BigDecimal fitnessNonParellelMatchingOccurrences(SignatureNode node, Occurrence oc)
	{
		//anzahl aller prozesse _ nimmt maximum von beiden, je nachdem wer mehr hat
		long countProcessesNode = node.cutInstancesName.size();
		long countProcessesOC = oc.getCutInstances().stream().mapToInt(x->x.getAmountOfTimesitWasCut()).sum();
		
		long amountAll = Math.max(countProcessesNode, countProcessesOC);
				
		//anzahl der Unterschiede
		//daher für jede instanz die gleichzeitig kommt ein unterschied mehr
		//für jede die weniger ist auch ein unterschied mehr
		//dann ausrechnen welchen anteil die unterschiede an allen prozessen haben und dass als fitness nehmen
		//kann auch gleich als likelihood verwenden
		
		List<String> prozesseInNode = new ArrayList<String>(node.cutInstancesName);
		//List<String> prozesseInOc = oc.getCutInstances().stream().map(x->x.getProcessThatWasCut().getName()).collect(Collectors.toList());
		List<String> prozesseInOc = new ArrayList<>();
		oc.getCutInstances().stream().forEach(c -> {
			String processName = c.getProcessThatWasCut().getName();
			IntStream.range(0, c.getAmountOfTimesitWasCut()).forEach(x->prozesseInOc.add(processName));
		});
		

		Set<String> prozesseHandles = new HashSet<>();
		
		long differenceAll = 0;
		
		for(String processName : prozesseInNode)
		{
			if(!prozesseHandles.contains(processName))
			{
				long countNode = prozesseInNode.stream().filter(x->x.equals(processName)).count();
				long countOC = prozesseInOc.stream().filter(x->x.equals(processName)).count();
				
				long difference = Math.abs(countNode-countOC);
				differenceAll+=difference;
				
				prozesseHandles.add(processName);
			}
		}
		
		//eins minus da bei geringeren differenzen der wert besser (höher) sein soll
		BigDecimal result = new BigDecimal(1).subtract(new BigDecimal(differenceAll).divide(new BigDecimal(amountAll), MathContext.DECIMAL128));
		
		return result;
	}
	
	
	//very basic
	//compares to occurrence lists and calculates their similarity
	//therefore, we check how many processes they hold, how frequnetly, and determine and count the differences
	//
	private BigDecimal simpleOccurrenceSimilarity(List<Occurrence> one, List<Occurrence> two)
	{
		List<String> processesOne = new ArrayList<>();
		List<String> processesTwo = new ArrayList<>();
		
		one.forEach(x->x.getCutInstances().forEach(y->processesOne.add(y.getProcessThatWasCut().getName())));
		two.forEach(x->x.getCutInstances().forEach(y->processesTwo.add(y.getProcessThatWasCut().getName())));

		long totalSizeBefore = processesOne.size()+processesTwo.size();

		List<String> processesOneClone = new ArrayList<>(processesOne);

		processesOneClone.forEach(x->{
			if(processesOne.contains(x) && processesTwo.contains(x))
			{
				processesOne.remove(x);
				processesTwo.remove(x);	
			}
		});
		
		long totalSizeAfterwards = processesOne.size() + processesTwo.size();
		
		//When the size aferwards becomes smaller then the size before both occurrences lists contained some similar processes
		//
		BigDecimal result = new BigDecimal(1);
		
		if(totalSizeBefore == 0)
		{
			result = new BigDecimal(0);
		}		
		else if(totalSizeAfterwards != 0)
		{
			result = new BigDecimal(1).subtract(new BigDecimal(totalSizeAfterwards).divide(new BigDecimal(totalSizeBefore), MathContext.DECIMAL128));
		}		
		
		return result;
	}
}
