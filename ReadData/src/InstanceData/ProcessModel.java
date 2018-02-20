package InstanceData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import Helper.DateHelper;
import Helper.RandomHelper;
import Helper.Tuple;
import Helper.UniqueObjectIdentifier;

public class ProcessModel{

	@Override
	public String toString() {
		return "ProcessModel [name=" + name + "]";
	}

	private List<IndividualProcessInstance> individualProcessInstances = new ArrayList<>();
	
	private String name;

	public Tuple<ProcessModel, ProcessModel> splitRandomly()
	{
		ArrayList<IndividualProcessInstance> firstpart = new ArrayList<>(individualProcessInstances);
		ArrayList<IndividualProcessInstance> secondPart = new ArrayList<>();

		while(firstpart.size()>individualProcessInstances.size()*0.5)
		{
			int nextRandom = RandomHelper.random.nextInt(firstpart.size());
						
			secondPart.add(firstpart.remove(nextRandom));
		}
		
		ProcessModel first = new ProcessModel(getName(), firstpart);
		ProcessModel second = new ProcessModel(getName(), secondPart);

		return new Tuple<ProcessModel, ProcessModel>(first, second);
	}
	
	public ProcessModel(String name) {
		super();
		this.name = name;
	}
	
	public ProcessModel(String name, List<IndividualProcessInstance> individualProcessInstances) {
		super();
		this.name = name;
		this.individualProcessInstances.addAll(individualProcessInstances);
	}
	
	public void addNovelInstance(Date start, Date end, long taskCount)
	{
		IndividualProcessInstance value = new IndividualProcessInstance(start, end, taskCount);

		individualProcessInstances.add(value);
	}

	public String getName() {
		return name;
	}
	
	public List<Integer> yearsProcess()
	{
		return individualProcessInstances.stream().map(x->(x.getStart().getYear()+1900)).distinct().collect(Collectors.toList());
	}
	
	public ProcessModel limitToTimespan(Date start, Date end)
	{
		ProcessModel result = new ProcessModel(getName());
		
		result.individualProcessInstances = matchesRange(start, end);
		
		return result;
	}
	
	public boolean hasContent()
	{
		return !individualProcessInstances.isEmpty();
	}
	
	@Override
	public boolean equals(Object obj) {

		boolean result = false;
		
		if(obj != null && obj instanceof ProcessModel)
		{
			ProcessModel other = (ProcessModel)obj;
			
			//it is important that we only use the name here for equality comparison
			result = other.name.equals(this.name);
		}
				
		return result;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return name.hashCode();
	}
	
	//maximum amount of slots that are covered by the timespan below for a single instance
	//so if multiple instances are covered then the one with the maximum is taken
	public int maxAmountOfSlotsInTimespan(Date start, Date end)
	{
		int result = 0;
		
		for(IndividualProcessInstance eachProcess : occuredDuringDate(start))
		{
			int individualResult = 0;

			List<Slot> slots = eachProcess.getSlotsOfProcess();
			
			for(Slot eachSlot : slots)
			{
				Date slotStart = eachSlot.getStartOfSlot();
				Date slotEnd = eachSlot.getEndOfSlot();
				
				//for now the slot must be completely be enclosed to be counted
				if(DateHelper.equalOrAfter(slotStart, start) &&
						DateHelper.equalOrBefore(slotEnd, end))
				{
					individualResult++;
				}
			}	
			
			result = Math.max(individualResult, result);
		}
		
		return result;
	}
	
	//returns processes that are executed when a specific data occurs
	public List<IndividualProcessInstance> occuredDuringDate(Date date)
	{
		List<IndividualProcessInstance> result = new ArrayList<>();

		for(IndividualProcessInstance eachProcess : individualProcessInstances)
		{
			boolean shouldBeContained = false;

			Date startOfProcess = eachProcess.getStart();
			Date endOfProcess = eachProcess.getEnd();
		
			if(DateHelper.equalOrBefore(startOfProcess, date) &&
					DateHelper.equalOrAfter(endOfProcess, date))
			{
				shouldBeContained = true;
			}
			
			if(shouldBeContained)
			{
				result.add(eachProcess);
			}
		}
		
		return result;
	}
	
	//suche nach prozessen welche möglichst knapp starten oder enden nach dem Datum in date
	//es werden die gesucht die am knappesten rankommen an date, liste da ja welche am selben tag starten/enden könnten
	public List<IndividualProcessInstance> nextStartOrEnd(Date date)
	{
		List<IndividualProcessInstance> result = new ArrayList<>();

		Date mostEarlyDateFound = null;
		
		for(IndividualProcessInstance eachProcess : individualProcessInstances)
		{
			Date startOfProcess = eachProcess.getStart();
			Date endOfProcess = eachProcess.getEnd();
				
			if(startOfProcess.after(date))
			{
				if(mostEarlyDateFound == null)
				{
					mostEarlyDateFound = startOfProcess;
				}
				
				if(DateHelper.equalOrAfter(mostEarlyDateFound, startOfProcess))
				{
					if(!DateHelper.equal(startOfProcess, mostEarlyDateFound))						 
					{
						result.clear(); //we found some that are EVEN close to the parameter value date
					}
					
					mostEarlyDateFound = startOfProcess;
					
					result.add(eachProcess);				
				}
			}
			
			if(endOfProcess.after(date))
			{
				if(mostEarlyDateFound == null)
				{
					mostEarlyDateFound = endOfProcess;
				}
				
				if(DateHelper.equalOrAfter(mostEarlyDateFound, endOfProcess))
				{
					if(!DateHelper.equal(endOfProcess, mostEarlyDateFound))						 
					{
						result.clear(); //we found some that are EVEN close to the parameter value date
					}
					
					mostEarlyDateFound = endOfProcess;
					
					result.add(eachProcess);				
				}
			}
		}
		
		return result;
	}
	
	public List<IndividualProcessInstance> matchesRange(Date startWindow, Date endWindow)
	{
		/* 1) Starts inbetween
		 * 2) Goes over the whole range
		 * 3) starts before start but ends before end
		 * 4) starts before end but and afterwards
		 */
		
		List<IndividualProcessInstance> result = new ArrayList<>();
		
		for(IndividualProcessInstance eachProcess : individualProcessInstances)
		{
			boolean shouldBeContained = false;

			Date startOfProcess = eachProcess.getStart();
			Date endOfProcess = eachProcess.getEnd();
			
			//1) Starts inbetween
			if(DateHelper.equalOrBefore(startWindow, startOfProcess) &&
					DateHelper.equalOrAfter(endWindow, endOfProcess))
			{
				shouldBeContained = true;
			}
			//2) Goes over the whole range (tracks both)
			//3) starts before start but ends before end
			else if(DateHelper.equalOrBefore(startWindow, endOfProcess) &&
					DateHelper.equalOrAfter(endWindow, endOfProcess))
			{
				shouldBeContained = true;
			}
			//2) Goes over the whole range (tracks both)
			//4) starts before end but and afterwards
			else if(DateHelper.equalOrBefore(startWindow, startOfProcess)&&
					DateHelper.equalOrAfter(endWindow, startOfProcess))
			{
				shouldBeContained = true;
			}
			
			if(shouldBeContained)
			{
				result.add(eachProcess);
			}
		}
				
		return result;
	}

	public List<IndividualProcessInstance> getIndividualProcessInstances() {
		return Collections.unmodifiableList(individualProcessInstances);
	}
}
