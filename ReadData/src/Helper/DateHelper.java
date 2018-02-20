package Helper;

import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	public static Date middleDayYear(int year)
	{
		Calendar myCal1 = Calendar.getInstance();               
		myCal1.set(Calendar.DAY_OF_MONTH, 31);
		myCal1.set(Calendar.MONTH, 11);
		myCal1.set(Calendar.YEAR, year);
		myCal1.set(Calendar.SECOND, 1);
		
		myCal1.set(Calendar.HOUR_OF_DAY, 23);
		myCal1.set(Calendar.MINUTE, 59);
		
		myCal1.set(Calendar.DAY_OF_YEAR, 365/2);

		return myCal1.getTime();
	}
	
	public static Date firstDayYear(int year)
	{
		Calendar myCal1 = Calendar.getInstance();               
		myCal1.set(Calendar.DAY_OF_MONTH, 1);
		myCal1.set(Calendar.MONTH, 0);
		myCal1.set(Calendar.YEAR, year);
		myCal1.set(Calendar.SECOND, 1);
		
		myCal1.set(Calendar.HOUR_OF_DAY, 0);
		myCal1.set(Calendar.MINUTE, 1);
		
		return myCal1.getTime();
	}
	
	public static Date firstDay2Q(int year)
	{
		Calendar myCal1 = Calendar.getInstance();               
		myCal1.set(Calendar.YEAR, year);
		myCal1.set(Calendar.DAY_OF_YEAR, 365/4);

		myCal1.set(Calendar.SECOND, 1);
		
		myCal1.set(Calendar.HOUR_OF_DAY, 0);
		myCal1.set(Calendar.MINUTE, 1);
		
		return myCal1.getTime();
	}
	
	public static Date lastDayYear(int year)
	{
		Calendar myCal1 = Calendar.getInstance();               
		myCal1.set(Calendar.DAY_OF_MONTH, 31);
		myCal1.set(Calendar.MONTH, 11);
		myCal1.set(Calendar.YEAR, year);
		myCal1.set(Calendar.SECOND, 1);
		
		myCal1.set(Calendar.HOUR_OF_DAY, 23);
		myCal1.set(Calendar.MINUTE, 59);
		
		return myCal1.getTime();
	}
	
	//prüfen ob main gleich oder nach after kommt
	public static boolean equalOrAfter(Date main, Date after)
	{
		if(main == null  || after == null)
		{
			return false;
		}
		
		return main.after(after) || main.getTime() == after.getTime();
	}
	
	//prüft if main gleich oder for before kommt
	public static boolean equalOrBefore(Date main, Date before)
	{
		if(main == null  || before == null)
		{
			return false;
		}
		
		return main.before(before) || main.getTime() == before.getTime();
	}
	
	public static boolean equal(Date first, Date second)
	{
		if(first == null  || second == null)
		{
			return false;
		}
		
		return  first.getTime() == second.getTime();
	}
	
	public static Date nextMillisec(Date date)
	{
		if(date == null)
		{
			return null;
		}
		
		Date result = new Date(date.getTime()+1);
		
		return result;
	}
	
	public static Date previousMillisec(Date date)
	{
		if(date == null)
		{
			return null;
		}
		
		Date result = new Date(date.getTime()-1);
		
		return result;
	}
	
	public static long tasksBetween(Date one, Date two)
	{
		if(one == null || two == null)
		{
			return 0;
		}
		
		long amount =  Math.abs(one.getTime()-two.getTime())/1000; 
		
		if(amount > 10)
		{
			amount = amount/60;
		}
		
		if(amount > 10)
		{
			amount = amount/60;
		}
		
		if(amount > 10)
		{
		//	amount = amount/24;
		}
		
		return amount;
	}
	
	//at least on of them must not be equal to zero
	public static boolean hasHoursMintuesSeconds(Date date)
	{
		boolean result = true;
		
		if(date != null)
		{
			if(date.getMinutes() == 0)
			{
				result = false;
			}
			else if(date.getSeconds() == 0)
			{
				result = false;
			}
			else if(date.getHours() == 0)
			{
				result = false;
			}
		}
		
		return result;
	}
}
