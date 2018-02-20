package BaseSig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.OptionalLong;

import Helper.ListHelper;
import InstanceData.IndividualProcessInstance;

public class InstanceOccurrence {

	private final IndividualProcessInstance instance; //instance of main model for which the occurrence below were generated for 
	private final List<Occurrence> signatureGenerationOccurrences = new ArrayList<>();
	
	public Date earliestOccurrence()
	{ 
		Date result = null;
		
		OptionalLong min = signatureGenerationOccurrences.stream().mapToLong(x->x.getStartDateOfOccurrenceSlot().getTime()).min();
		
		if(min.isPresent())
		{
			result = new Date(min.getAsLong());
		}
		
		return result;
	}
	
	public Date latestOccurrence()
	{
		Date result = null;
		
		OptionalLong max = signatureGenerationOccurrences.stream().mapToLong(x->x.getStartDateOfOccurrenceSlot().getTime()).max();

		if(max.isPresent())
		{
			result = new Date(max.getAsLong());
		}
		
		return result;
	}
	
	public InstanceOccurrence(IndividualProcessInstance instance, 
			List<Occurrence> signatureGenerationOccurrences)
	{
		this.instance = instance;
		if(ListHelper.hasValue(signatureGenerationOccurrences))
		{
			this.signatureGenerationOccurrences.addAll(signatureGenerationOccurrences);
		}
	}
	
	/*
	 * Go over all occurrences and adapt/remove them if necessary
	 * This should prevent nouise from occurrences that are too short, i.e., if the processes
	 * that a ocurrence "covers" contain in the covered space not enough tasks
	 */
	
	//TODO instead of slots use the real amount of tasks that occur in the occurrence
	//e.g. a single process must start and end at least X tasks to assume the occurrence as relevant
	public List<Occurrence> getNoiseCancledOcurrences()
	{
		List<Occurrence> originalOccurrences = signatureGenerationOccurrences;
		
		List<Occurrence> result = new ArrayList<>();
		
		//holds the slots that are not large enough and that occur in a row
		//used to check if merging them would be sufficient
		List<Occurrence> notEnoughSlotsRow = new ArrayList<>();
				
		//filter/address occurrences that are too short, i.e., that do not cover enouight 
		for(int i=0;i<originalOccurrences.size();i++)
		{
			Occurrence occurrence = originalOccurrences.get(i);
			
			if(!occurrence.coversEnoughSlots())
			{				
				notEnoughSlotsRow.add(occurrence);
				
				//check if merging the slots would be enough to make them sufficient
				//if yes replace the original ones with the merged (remove the old ones and insert the new one)
				//clear the hold rows
				
				Occurrence merged = Occurrence.merge(notEnoughSlotsRow.toArray(new Occurrence[0]));
				
				if(merged.coversEnoughSlots())
				{
					result.add(merged);
					notEnoughSlotsRow.clear();
				}
			}			
			else
			{
				notEnoughSlotsRow.clear();

				result.add(occurrence);
			}
		}
		
		
		//Merge occurrcences that are equal (cover the same processes) and are placed in the list directly after each other
		if(result.size()>1)
		{
			Occurrence firstOne = result.get(0);
			
			for(int i=1;i<result.size();i++)
			{
				Occurrence secondOne = result.get(i);

				if(firstOne.coversSameProcesses(secondOne))
				{
					//merge both
					Occurrence merged = Occurrence.merge(firstOne, secondOne);
										
					//replace the second one with the merged result
					result.set(i, merged);
					result.remove(i-1);  //removes the first one					
				}
				
				firstOne = result.get(i); //read it again to get the merged result
			}
		}		
				
		return result;
	}

	public IndividualProcessInstance getInstance() {
		return instance;
	}
}
