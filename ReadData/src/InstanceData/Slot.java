package InstanceData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Slot {

	@Override
	public String toString() {
		return "Slot [start=" + startOfSlot + ", end=" + endOfSlot + "]";
	}

	private Date startOfSlot;
	private Date endOfSlot;
	
	public Slot(Date start, Date end)
	{
		this.startOfSlot = start;
		this.endOfSlot = end;
	}
	
	public static List<Slot> generate(Date start, long length, long amount)
	{
		List<Slot> result = new ArrayList<>();
		
		Date slotStart = start;

		for(int i=0;i<amount;i++)
		{
			Date slotEnd = new Date(slotStart.getTime()+length);

			result.add(new Slot(slotStart, slotEnd));
			
			slotStart = slotEnd;
		}
		
		return result;
	}

	public Date getStartOfSlot() {
		return startOfSlot;
	}

	public Date getEndOfSlot() {
		return endOfSlot;
	}
}
