package FullSig;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import BaseSig.CutInstances;
import BaseSig.Occurrence;
import Helper.Tuple;
import Helper.UniqueObjectIdentifier;
import InstanceData.ProcessModel;

public class SignatureNode extends UniqueObjectIdentifier{

	//for speed reason we only store the name of the processes
	//note the data is sorted alphabetically!
	public List<String> cutInstancesName = new ArrayList<>();
	
	//edges to other signature nodes, and how likely the transition is from
	//here to there, The next node which we connect to is the key
	//value is die wahscheinlichkeit für diesne Übergang laut den Logs
	public Map<SignatureNode, BigDecimal> edges = new HashMap<>();
	
	//edge generation helper
	//a node and how often this node directs to that other node
	public Map<SignatureNode, Integer> edgeConnectionsHelper = new HashMap<>();

	public SignatureNode(Occurrence oc)
	{
		cutInstancesName.addAll(extractOrderedInstanceNames(oc));		
	}
	
	//dummy should only be used for the anchor point in the signature graph (i.e
	// the process that the signature is generated for
	public SignatureNode(ProcessModel main)
	{
		cutInstancesName.add(main.getName());
	}
	
	public List<SignatureNode> getAllSigNodes()
	{
		List<SignatureNode> result = new ArrayList<>(edges.keySet());
			
		return result;
	}
	
	//check for itself
	public boolean hasEqualOccurrence(Occurrence oc)
	{
		return isEqualOrderAndContent(extractOrderedInstanceNames(oc), cutInstancesName);
	}
	
	//check if the same processes are covered. But ignore if they are covered more or less frequently (so parallel executions)
	public boolean coversSameProcesses(Occurrence oc)
	{
		Set<String> coveredBySignatureNode = new HashSet<>(cutInstancesName);

		Set<String> coveredByOc = new HashSet<>();
		
		oc.getCutInstances().stream().forEach(x-> coveredByOc.add(x.getProcessThatWasCut().getName()));
		
		//different processes are covered by each
		if(coveredBySignatureNode.size() != coveredByOc.size())
		{
			return false;
		}
		
		coveredBySignatureNode.removeAll(coveredByOc);
		
		//when its empty then both covered the same processes (but potenitally wwith difference parallelities)
		return coveredBySignatureNode.isEmpty();
	}
	
	//checks for the sugnature nodes that are direct sucessors
	public Tuple<SignatureNode, BigDecimal> hasEqualSucessorOccurrence(Occurrence oc)
	{
		SignatureNode resultNode = null;
		BigDecimal edgeLikelyhood = null;
		
		boolean found = false;
		
		for(Entry<SignatureNode, BigDecimal> eachEdge : edges.entrySet())
		{
			if(eachEdge.getKey().hasEqualOccurrence(oc))
			{
				found = true;
				
				resultNode = eachEdge.getKey();
				edgeLikelyhood = eachEdge.getValue();
				
				break;
			}
			
		}
		
		if(!found)
		{
			return null;
		}
		else
		{
			return new Tuple<SignatureNode, BigDecimal>(resultNode, edgeLikelyhood);
		}
	}
	
	public void caclulatePropabilities()
	{
		int value=  edgeConnectionsHelper.values().stream()
		.collect(Collectors.summingInt(Integer::intValue));
		
		BigDecimal totalConnectionAmount = new BigDecimal(value);
		
		edgeConnectionsHelper.forEach((k,v) -> {
		
			BigDecimal connectionsCount = new BigDecimal(v);		
			BigDecimal propability = connectionsCount.divide(totalConnectionAmount, MathContext.DECIMAL128);
			
			edges.put(k, propability);
		});
	}
	
	public void addConnection(SignatureNode addEdgeTo)
	{
		if(edgeConnectionsHelper.containsKey(addEdgeTo))
		{
			int amount = edgeConnectionsHelper.get(addEdgeTo);
			
			edgeConnectionsHelper.put(addEdgeTo, (amount+1));
		}
		else
		{
			edgeConnectionsHelper.put(addEdgeTo, 1);
		}
	}
	
	private List<String> extractOrderedInstanceNames(Occurrence oc)
	{
		List<String> result = new ArrayList<>();
		
		for(CutInstances eachInstance : oc.getCutInstances())
		{
			for(int i=0;i<eachInstance.getAmountOfTimesitWasCut();i++)
			{
				result.add(eachInstance.getProcessThatWasCut().getName());
			}
		}
		
		Collections.sort(result);
		
		return result;
	}
	
	private boolean isEqualOrderAndContent(List<String> first, List<String> second)
	{
		if(first.size() != second.size())
		{
			return false;
		}
		
		for(int i=0;i<first.size();i++)
		{
			String firstString = first.get(i);
			String secondString = second.get(i);
			
			if(!firstString.equals(secondString))
			{
				return false;
			}
		}
		
		return true;
	}
}
