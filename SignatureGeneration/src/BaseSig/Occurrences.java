package BaseSig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.OptionalLong;

import Helper.ListHelper;
import InstanceData.ProcessModel;

public class Occurrences {

	/* List of Process Instances that were found to be executed on a specific point in time */

	private final ProcessModel generatedFor;
	
	//when mergen these we can generate the signatures
	//but before this they must be filtered/adapted to address noise!!
	//this are all instances that belong to fe
	private final List<InstanceOccurrence> instanceOccurrences = new ArrayList<>();
	
	public Occurrences(ProcessModel generatedFor)
	{
		this.generatedFor = generatedFor;
	}
	
	public void addNewOccurrences(InstanceOccurrence occurrence)
	{
		if(occurrence != null)
		{
			instanceOccurrences.add(occurrence);
		}
	}
	
	
	public Date startOfOccurrences()
	{
		OptionalLong min = instanceOccurrences.stream().mapToLong(x->x.earliestOccurrence().getTime()).min();
		
		Date result = null;
		
		if(min.isPresent())
		{
			result = new Date(min.getAsLong());
		}
		
		return result;
	}
	
	public Date endOfOccurrences()
	{
		OptionalLong max = instanceOccurrences.stream().mapToLong(x->x.earliestOccurrence().getTime()).max();

		Date result = null;
		
		if(max.isPresent())
		{
			result = new Date(max.getAsLong());
		}
		
		return result;
	}
	
	/*
	 * Each individual occurrence list is a sequence of "slots" that occur one after another in the logs
	 * Each "slot" holds which processes (more specifically their instances) take place duing the slot
	 * That information can be used to construct a network (signature) which represents which
	 * Processes that occur at the same time and how they follow on each other (e.g., that B is executed after A) or 
	 * that A and B occur in parallel and then are followed by C
	 * 
	 */
	public List<List<Occurrence>> individualNoiseFreeOccurrences()
	{
		List<List<Occurrence>> result = new ArrayList<>();
		
		for(InstanceOccurrence eachInstanceOccurrence : instanceOccurrences)
		{
			List<Occurrence> occurrences = eachInstanceOccurrence.getNoiseCancledOcurrences();
			
			if(ListHelper.hasValue(occurrences))
			{
				result.add(occurrences);
			}
		}
		
		return result;
	}

	public ProcessModel getGeneratedFor() {
		return generatedFor;
	}

	public List<InstanceOccurrence> getInstanceOccurrences() {
		return instanceOccurrences;
	}
}
