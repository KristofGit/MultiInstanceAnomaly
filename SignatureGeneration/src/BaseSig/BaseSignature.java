package BaseSig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Configuration.Config;
import Helper.DateHelper;
import Helper.ListHelper;
import InstanceData.ProcessModel;
import InstanceData.IndividualProcessInstance;

public class BaseSignature {
	
	private SignatureType signatureType = SignatureType.Before;
	
	public BaseSignature(SignatureType signatureType)
	{
		assert(signatureType != null);
		
		this.signatureType = signatureType;
	}
	
	//all contains all insatnces, also the main one
	public Occurrences generateOcurrencesForProcess(ProcessModel main, List<ProcessModel> all)
	{
		Occurrences result = new Occurrences(main);
				
		for(IndividualProcessInstance eachInstance : main.getIndividualProcessInstances())
		{
			List<ProcessModel> relevantInstances = extractWindow(eachInstance, all);
			List<Occurrence> identifiesOccurrences = generateForProcessInstance(eachInstance, relevantInstances);
						
			result.addNewOccurrences(new InstanceOccurrence(eachInstance, identifiesOccurrences));
		}
				
		return result;
	}
	
	private List<Occurrence> generateForProcessInstance(IndividualProcessInstance main, List<ProcessModel> all)
	{	
		List<Occurrence> result = new ArrayList<>();
		
		TimeWindow window = getWindow(main, signatureType);

		/* 1) Get start (based on window start) for inital ocurrence
		 * 2) Detect end of occurrence and hereby the start of the next one
		 * 3) Repate until we end up at the end of the window
		 */
		
		Date windowEnd = window.getEndOfWindow();
		Date windowStart = window.getStartOfWindow();
		
		Date startDateForNextOccurrence = windowStart;
		
		//identify all processes that start before and end after the start of the window
		//those will form the inital occurrence
		
		for(;;)
		{
			//we reached the end of the window
			if(DateHelper.equalOrAfter(startDateForNextOccurrence, windowEnd))
			{
				break;
			}
			
			List<CutInstances> processesThatAreCut = getCutProcesses(startDateForNextOccurrence, all);
			
			//did not find any processes at the current date, so seach for the next ones and repeate
			if(!ListHelper.hasValue(processesThatAreCut))
			{
				startDateForNextOccurrence = DateHelper.nextMillisec(getNextProcessStartOrEndDate(startDateForNextOccurrence, all));
				
				//there seems to be no more following process
				if(startDateForNextOccurrence == null)
				{
					//so stop
					break;
				}
				
				//update with new position
				processesThatAreCut = getCutProcesses(startDateForNextOccurrence, all);			
			}
			
			Date endDateOfOccurrence = DateHelper.nextMillisec(getNextProcessStartOrEndDate(startDateForNextOccurrence, all));
		
			//there seems to be no more following process
			if(endDateOfOccurrence == null)
			{
				//store the final last occurrence
				//use the end of the window as a dummy end date
				result.add(new Occurrence(startDateForNextOccurrence, windowEnd, processesThatAreCut));
				
				//so stop
				break;
			}
			else
			{
				if(endDateOfOccurrence.after(windowEnd))
				{
					endDateOfOccurrence = windowEnd;
				}
				
				//store the final last occurrence
				result.add(new Occurrence(startDateForNextOccurrence, endDateOfOccurrence, processesThatAreCut));
			}
			
			startDateForNextOccurrence = endDateOfOccurrence;
		}
		
		
		return result;
	}
	
	//no filtering just the general process, any of the contained instances could be cut
	private List<CutInstances> getCutProcesses(Date date, List<ProcessModel> other)
	{
		List<CutInstances> result = new ArrayList<>();
		
		for(ProcessModel eachInstance : other)
		{
			List<IndividualProcessInstance> areCutByOccurrenceStart = 
					eachInstance.occuredDuringDate(date);	
			
			if(ListHelper.hasValue(areCutByOccurrenceStart))
			{
				result.add(new CutInstances(eachInstance, areCutByOccurrenceStart.size()));
			}
		}
		
		return result;
	}
	
	//searches for the date when the most closes process (timespan closest to the value date) starts or ends
	private Date getNextProcessStartOrEndDate(Date date, List<ProcessModel> other)
	{
		Date result = null;
		
		for(ProcessModel eachInstance : other)
		{
			List<IndividualProcessInstance> nextStartingOrEnding = eachInstance.nextStartOrEnd(date);
			
			if(ListHelper.hasValue(nextStartingOrEnding))
			{
				Date start= nextStartingOrEnding.get(0).getStart();
				Date end = nextStartingOrEnding.get(0).getEnd();
				
				if(result == null)
				{
					if(start.after(date))
					{
						result = start;
					}
					else 
					{
						result = end;
					}
				}
				else
				{
					if(start.after(date) && start.before(result))
					{
						result = start;
					}
					else if(end.before(result))
					{
						result = end;
					}
				}
			}
		}
		
		return result;
	}
	
	private List<ProcessModel> extractWindow(IndividualProcessInstance main, List<ProcessModel> other)
	{
		TimeWindow window = getWindow(main, signatureType);
				
		Date windowsStart = window.getStartOfWindow();
		Date windowsEnd = window.getEndOfWindow();

		List<ProcessModel> relevantInstances = new ArrayList<>();
		
		for(ProcessModel eachInstance : other)
		{
			List<IndividualProcessInstance> matchingResult = eachInstance.matchesRange(windowsStart, windowsEnd);
		
			if(ListHelper.hasValue(matchingResult))
			{
				relevantInstances.add(new ProcessModel(eachInstance.getName(), matchingResult));
			}			
		}
		
		return relevantInstances;
	}
	
	private TimeWindow getWindow(IndividualProcessInstance main, SignatureType type)
	{
		final double windowSize = Config.WindowSize;

		long processSize = main.getEnd().getTime()-main.getStart().getTime();
		
		//long windowSpan = 1000*60*60*24*2;
		
		//Date windowsStart = new Date((long)(main.getStart().getTime()-(windowSpan*windowSize)));
		//Date windowsEnd = new Date((long)(main.getEnd().getTime()+(windowSpan*windowSize)));

		Date windowsStart = new Date((long)(main.getStart().getTime()-(processSize*windowSize)));
		Date windowsEnd = new Date((long)(main.getEnd().getTime()+(processSize*windowSize)));

		Date processStart = main.getStart();
		Date processEnd = main.getEnd();
		
		switch (type) {
		case After:
			windowsStart = DateHelper.nextMillisec(processEnd); //so that the main process is no langer part of the window
			break;
		case Before:
			windowsEnd = DateHelper.previousMillisec(processStart); //so that the main process is no langer part of the window
			break;
		case Intermediate:
			windowsStart = processStart;
			windowsEnd = processEnd;
			break;
		}
				
		return new TimeWindow(windowsStart, windowsEnd);
		
	}
}
