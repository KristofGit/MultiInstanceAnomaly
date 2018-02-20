package FullSig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import BaseSig.Occurrence;
import BaseSig.Occurrences;
import BaseSig.SignatureType;
import InstanceData.ProcessModel;

public class FullSig {

	public SignatureNode mergeIntoSignature(ProcessModel main, SignatureType type,  Occurrences  ocs)
	{
		return mergeIntoSignature(main, type, ocs.individualNoiseFreeOccurrences());
	}
	
	public SignatureNode mergeIntoSignature(ProcessModel main, SignatureType type,  List<List<Occurrence>>  occurrences)
	{
		SignatureNode initalSignatureNode = new SignatureNode(main);
		
		List<SignatureNode> allNodes = new ArrayList<>();
		allNodes.add(initalSignatureNode);
		
		/*
		 * 3 Durchläufe:
		 * 1) einmal um alle Occurrences in Signature Nodes umzuwandeln
		 * 2) ein zweites mal um die Occurrences mit einander zu verbinden indem man sagt wer mit wem
		 * 3) ein drittes mal um die prozentverteilungen der Verbindungen ausrechnen zu lassen
		 */
		
		//key is the occurrence and value is the assigned siganture node
		//occurrences are unique, signature nodes most likely not
		Map<Occurrence, SignatureNode> signatureNodeRelation = new HashMap<>();
		
		//nodes generieren, sicherstellen dass jeder node nur einmalig vorkommt
		for(List<Occurrence> eachBaseSig : occurrences)
		{
			for(Occurrence eachoc : eachBaseSig)
			{
				boolean found = false;
				
				for(SignatureNode eachSigNode : allNodes)
				{
					if(eachSigNode.hasEqualOccurrence(eachoc))
					{
						signatureNodeRelation.put(eachoc, eachSigNode);

						found = true;
						break;
					}
				}
				
				if(!found)
				{
					SignatureNode node = new SignatureNode(eachoc);
					
					allNodes.add(node);
					signatureNodeRelation.put(eachoc, node);
				}
			}
		}
		
		//Connect the inital nodes  nodes to the detault starting node that represents the 
		//main process
		for(List<Occurrence> eachBaseSig : occurrences)
		{
			if(type == SignatureType.Before)
			{
				Collections.reverse(eachBaseSig);
			}
			
			Occurrence firstNode = eachBaseSig.get(0);
			
			SignatureNode firstSigNode = signatureNodeRelation.get(firstNode);

			initalSignatureNode.addConnection(firstSigNode);
		}
		
		//add connections, note the order must be respected
		//for before signatures the order is reversed
		//for others its like in the list
		for(List<Occurrence> eachBaseSig : occurrences)
		{
			//we do this above
			/*if(type == SignatureType.Before)
			{
				Collections.reverse(eachBaseSig);
			}*/
			
			Occurrence firstNode = eachBaseSig.get(0);
			
			for(Occurrence secondNode : eachBaseSig.subList(1, eachBaseSig.size()))
			{
				SignatureNode firstSigNode = signatureNodeRelation.get(firstNode);
				SignatureNode secondSigNode = signatureNodeRelation.get(secondNode);
				
				firstSigNode.addConnection(secondSigNode);
				
				firstNode = secondNode;
			}
		}
		
		
		allNodes.stream().forEach(x -> x.caclulatePropabilities());
		
		return initalSignatureNode;
	}
}
