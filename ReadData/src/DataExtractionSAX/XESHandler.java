package DataExtractionSAX;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import Helper.DateHelper;
import InstanceData.ProcessModel;

/*
 * This one is specifically designed for BPIC 2017 authorization logs (NOT OFFER LOGS) 
 */
public class XESHandler extends DefaultHandler {

	   private final Map<String, ProcessModel> instances = new HashMap<>();
	   private Map<String, Date> activitiesStarted = null;

	   private int countExtractedActivites = 0;
	   
	   private boolean currentlyEvent = false;
	   private EventLifeCycle eventStateFound = null;
	   
	   private String eventConceptName = null;
	   private Date eventTimestamp = null;
	   
	   @Override
	   public void startElement(
	   String uri, 
	   String localName,
	   String qName, 
	   Attributes attributes)
	   {
		   if(qName.equalsIgnoreCase("trace"))
		   {			   
			   activitiesStarted = new HashMap<>();
		   }
		   else if(qName.equalsIgnoreCase("event"))
		   {
			   currentlyEvent = true;
		   }
		   else if(currentlyEvent)
		   {
			   if(qName.equalsIgnoreCase("string"))
			   {
				   if(attributes.getValue("key").equalsIgnoreCase("concept:name"))
				   {
					   eventConceptName = attributes.getValue("value");
				   }
				   if(attributes.getValue("key").equalsIgnoreCase("lifecycle:transition"))
				   {
				       switch(attributes.getValue("value"))
				       {
				       case "schedule":
				       case "start":
				    	   eventStateFound = EventLifeCycle.Start;
				       break;
				       case "complete":
				    	   eventStateFound = EventLifeCycle.Complete;
				       break;
				       }
				   }
			   }
			   else if(qName.equalsIgnoreCase("date"))
			   {
				   if(attributes.getValue("key").equalsIgnoreCase("time:timestamp"))
				   {
					   String dateString = attributes.getValue("value");
					   Date date = javax.xml.bind.DatatypeConverter.parseDateTime(dateString).getTime();
					   
					   eventTimestamp = date;
				   }
			   }
			   
		   }
	   }
	   
	   @Override
	   public void endElement(String uri, 
			   String localName, String qName)
	   {
		   if(qName.equalsIgnoreCase("event"))
		   {
			   currentlyEvent = false;
		   
			   if(eventStateFound != null)
			   {
				   if(eventStateFound == EventLifeCycle.Start)
				   {
					   activitiesStarted.put(eventConceptName, eventTimestamp);
				   }
				   else 
				   {
					   Date startDate = activitiesStarted.remove(eventConceptName);
					   
					   if(startDate == null)
					   {
						   System.out.println("did not find start event for:"+eventConceptName);
						   
						   startDate = new Date(eventTimestamp.getYear(), eventTimestamp.getMonth(), eventTimestamp.getDate());
						   startDate.setHours(eventTimestamp.getHours()-1);
						   startDate.setMinutes(eventTimestamp.getMinutes());
					   }
					   
					  // if((startDate.getYear()+1900==2017 && eventTimestamp.getYear()+1900==2017))
					   {																   
						   ProcessModel instanceToAddTo = null;
							
							if(instances.containsKey(eventConceptName))
							{
								instanceToAddTo = instances.get(eventConceptName);
							}
							else
							{
								instanceToAddTo = new ProcessModel(eventConceptName);
								instances.put(eventConceptName, instanceToAddTo);
							}
							
							
							long amountOfProcessTasks = tasksBetween(startDate, eventTimestamp);
			        		amountOfProcessTasks = Math.max(amountOfProcessTasks, 1);
			        		
							instanceToAddTo.addNovelInstance(startDate, eventTimestamp, amountOfProcessTasks);
														
							countExtractedActivites++;
							
							if(countExtractedActivites > 1000)
							{
								System.out.println("finish");
								throw new RuntimeException("Stop parsing");
							}
					   }
				   }
					
				   eventConceptName = null;
				   eventStateFound = null;
				   eventTimestamp = null;
			   }
		
		   }	   
	   }

	public Map<String, ProcessModel> getInstances() {
		return instances;
	}
	
	
	public static long tasksBetween(Date one, Date two)
	{
		if(one == null || two == null)
		{
			return 0;
		}
		
		long amount =  Math.abs(one.getTime()-two.getTime())/1000; 
		
		if(amount > 10 && amount/60>0)
		{
			amount = amount/60;
		}
		
		if(amount > 10 && amount/60>0)
		{
			amount = amount/60;
		}
		
		if(amount > 10 && amount/24>0)
		{
			//amount = amount/24;
		}
		
		return amount;
	}
	
}
