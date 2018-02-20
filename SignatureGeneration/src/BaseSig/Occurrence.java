package BaseSig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Configuration.Config;
import Helper.ListHelper;
import Helper.UniqueObjectIdentifier;
import InstanceData.ProcessModel;

public class Occurrence extends UniqueObjectIdentifier{

	private final Date startDateOfOccurrenceSlot;
	private final Date endDateOfOccurrenceSlot;
	
	private final List<CutInstances> cutInstances = new ArrayList<>();

	@Override
	public String toString() {
		return "Occurrence [startOC=" + startDateOfOccurrenceSlot + ", endOC="
				+ endDateOfOccurrenceSlot + ", cutIns=" + cutInstances + "]";
	}

	public Occurrence(
			Date startDateOfOccurrenceSlot,
			Date endDateOfOccurrenceSlot,
			List<CutInstances> cutInstances) {
		super();
		this.startDateOfOccurrenceSlot = startDateOfOccurrenceSlot;
		this.endDateOfOccurrenceSlot = endDateOfOccurrenceSlot;
		
		if(ListHelper.hasValue(cutInstances))
		{
			this.cutInstances.addAll(cutInstances);
		}
	}

	public Date getStartDateOfOccurrenceSlot() {
		return startDateOfOccurrenceSlot;
	}

	public Date getEndDateOfOccurrenceSlot() {
		return endDateOfOccurrenceSlot;
	}

	public List<CutInstances> getCutInstances() {
		return cutInstances;
	}
	
	//check if this occurrence cover the same processes 
	//as the other one
	public boolean coversSameProcesses(Occurrence other)
	{
		if(other.getCutInstances().size() != getCutInstances().size())
		{
			return false;
		}
				
		for(CutInstances eachOther : other.getCutInstances())
		{
			boolean found = false;
			
			for(CutInstances eachInternal : getCutInstances())
			{
				if(eachOther.equals(eachInternal))
				{
					found = true;
					break;
				}
			}
			
			if(!found)
			{
				return false;
			}
		}
		
		
		return true;
	}
	
	public int maxSlotsCoveredByThisOccurrence()
	{
		int result = 0;
		
		for(CutInstances each : cutInstances)
		{
			int slots = each.getProcessThatWasCut().
					maxAmountOfSlotsInTimespan(startDateOfOccurrenceSlot, endDateOfOccurrenceSlot);
			
			result = Math.max(slots, result); 
		}
		
		return result;
	}
	
	public boolean coversEnoughSlots()
	{
		return maxSlotsCoveredByThisOccurrence()>=Config.mitSlotCoverageSizePerOccurrence;
	}
	
	//used to merge multiple insufficient ocurrences so that they become sufficient
	public static Occurrence merge(Occurrence... occurrences)
	{	
		Date minStartDate = null;
		Date maxEndDate = null;
		
		for(Occurrence each : occurrences)
		{
			Date ocStart = each.getStartDateOfOccurrenceSlot();
			Date ocEnd = each.getEndDateOfOccurrenceSlot();
			
			if(minStartDate == null)
			{
				minStartDate = ocStart;
			}
			else if(minStartDate.after(ocStart))
			{
				minStartDate = ocStart;
			}
			
			if(maxEndDate == null)
			{
				maxEndDate = ocEnd;
			}
			else if(maxEndDate.before(ocEnd))
			{
				maxEndDate = ocEnd;
			}
		}
		
		//instance and amount
		Map<ProcessModel, Integer> instancesMap= new HashMap<>();
		
		for(Occurrence each : occurrences)
		{
			for(CutInstances eachInstance : each.cutInstances)
			{
 				ProcessModel process = eachInstance.getProcessThatWasCut();
				int amount = eachInstance.getAmountOfTimesitWasCut();
				
				if(instancesMap.containsKey(process))
				{
					int knownMaxCuts = instancesMap.get(process);
					
					if(amount>knownMaxCuts)
					{
						instancesMap.put(process, amount);
					}
				}
				else 
				{
					instancesMap.put(process, amount);
				}
			}
			
		}
		
		
		//we HANDLE THAT SOME CUT ISNTANCES OCUR MULTIPLE TIMES
		//because the cuts store the max cut amount
		
		List<CutInstances> instances =
				instancesMap.entrySet().stream()
				.map(e -> new CutInstances(e.getKey(),e.getValue())).collect(Collectors.toList());		
		
		return new Occurrence(minStartDate, maxEndDate, instances);
	}
}
