package BaseSig;

import java.util.Date;

public class TimeWindow{

	private final Date startOfWindow;
	private final Date endOfWindow;
	
	public TimeWindow(Date start, Date end)
	{
		this.startOfWindow = start;
		this.endOfWindow = end;
	}

	public Date getEndOfWindow() {
		return endOfWindow;
	}

	public Date getStartOfWindow() {
		return startOfWindow;
	}
}
