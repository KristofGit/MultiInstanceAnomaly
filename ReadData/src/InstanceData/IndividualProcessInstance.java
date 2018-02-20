package InstanceData;

import java.util.Date;
import java.util.List;

import Helper.UniqueObjectIdentifier;

public class IndividualProcessInstance extends UniqueObjectIdentifier{
	private final Date start;
	private final Date end;
	private final long amountOfTasks;	
	
	//generated data
	private long amountOfSlots;
	private long millisecodsSlotSize;
	private List<Slot> slotsOfProcess = null;
	
	public IndividualProcessInstance(Date start, Date end, long amountOfTasks) {
		super();
		this.start = start;
		this.end = end;
		this.amountOfTasks = amountOfTasks;
		
		this.slotitize();
	}
	
	private void slotitize()
	{
		amountOfSlots = amountOfTasks;
		millisecodsSlotSize = (end.getTime() - start.getTime()) / amountOfSlots;
		
		slotsOfProcess = Slot.generate(start, millisecodsSlotSize, amountOfSlots);
	}

	public List<Slot> getSlotsOfProcess() {
		return slotsOfProcess;
	}
	
	// slotwith is the amount of slots at the start and end of the process that are shringekd
	/* To shrink a the start end the first/last k slots are taken, their "mid" is calculated, the k slots are 
	 * removed and replaced with a artifical slot that starts at the start of the first slot and goes until the mid  
	 */
	/*public void filterSlotNoise(int slotWith)
	{
		//adapt the slots
		//first 
		{
			Date startSlot= slotsOfProcess.get(0).getStartOfSlot();
			Date endSlot = slotsOfProcess.get(slotWith-1).getEndOfSlot();
			
			Date newStartSlot = new Date(startSlot.getTime() + ((endSlot.getTime()-startSlot.getTime())/2));
			
			for(int i=0;i<slotWith;i++)
			{
				slotsOfProcess.remove(0);
			}
			slotsOfProcess.add(new Slot(newStartSlot, endSlot));
		}
		
		
		//last
		{
			Date startSlot= slotsOfProcess.get(slotsOfProcess.size()-1).getStartOfSlot();
			Date endSlot = slotsOfProcess.get(slotsOfProcess.size()-slotWith).getEndOfSlot();
			
			Date newEndSlot = new Date(startSlot.getTime() + ((endSlot.getTime()-startSlot.getTime())/2));
			
			for(int i=0;i<slotWith;i++)
			{
				slotsOfProcess.remove(slotsOfProcess.size()-1);
			}
			slotsOfProcess.add(new Slot(startSlot, newEndSlot));
		}
				
		//adapt the start/end of this process 
		refreshStartEnd();
	}*/
	
	//update the start end variable based on the first/last slot in slots of process
	/*private void refreshStartEnd()
	{
		if(slotsOfProcess != null)
		{
			Slot first = slotsOfProcess.get(0);
			Slot last = slotsOfProcess.get(slotsOfProcess.size()-1);
			
			start = first.getStartOfSlot();
			end = last.getEndOfSlot();
		}
	}*/

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public long getAmountOfTasks() {
		return amountOfTasks;
	}
}
