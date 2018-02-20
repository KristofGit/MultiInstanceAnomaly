package DataExtraction;

import java.io.Console;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.logging.XLogging.Importance;
import org.deckfour.xes.logging.XLoggingListener;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import Configuration.Config;
import Configuration.LogType;
import DataExtractionSAX.ExtractSAXForBPIC2017;
import Helper.DateHelper;
import InstanceData.ProcessModel;

public class Extract {

	public static List<ProcessModel> analyzeXES() {
		
		List<ProcessModel> result = new ArrayList<>();
		
		XLogging.setListener(new XLoggingListener() {
			
			@Override
			public void log(String arg0, Importance arg1) {
				System.out.println(arg1 + ":"+arg0);
			}
		});
		
		List<File> files = new ArrayList<File>();
		listf(Config.PathToLogFiles,files);	
	
		//List<ProcessModel> result = null;
		
		for(File xesFile : files)
		{
			if(!xesFile.getName().toLowerCase().contains("xes"))
			{
				continue;
			}
			
			XesXmlParser xesParser = new XesXmlParser();

			if(!xesParser.canParse(xesFile))
			{
				xesParser = new XesXmlGZIPParser();
				if (!xesParser.canParse(xesFile)) {
					throw new IllegalArgumentException("Unparsable log file: " + xesFile.getAbsolutePath());
				}
			}		
			
			try {
				
				if(Config.LogSource == LogType.BPIC15)
				{
					result = parseLogBPIC2015(xesParser, xesFile);
				}
				else if(Config.LogSource == LogType.BPIC17)
				{
					result = ExtractSAXForBPIC2017.analyzeXES(xesFile);
				}
				else if(Config.LogSource == LogType.HEP)
				{
					result = parseLogHEPActivity(xesParser, xesFile);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			//currently we only support one file (executions of the same activity are not merged if stored in multiple files!)
			//so top after the first
			break;
		}
		
		return result;
	}
    public static <T> T coalesce(T... ts) {
        for (T t : ts)
            if (t != null)
                return t;
 
        return null;
    }
	private static List<ProcessModel> parseLogHEPActivity(XesXmlParser parser, File xesFile) throws Exception {

		Map<String, ProcessModel> instances = new HashMap<>();

		List<XLog> xLogs = parser.parse(xesFile);

		int countTraces = 0, countActivities = 0, ignoredActivites=0, takenAcitivies=0;
		
		  for (XLog xLog : xLogs) {
	        	
	        	for (XTrace trace : xLog) {
	        		
					for (XEvent event : trace) {

						countActivities++;

						XAttributeMap attributeMap = event.getAttributes();

		        		XAttributeLiteral traceName = (XAttributeLiteral)attributeMap.get("concept:name"); //identifies the activity


		        		Date timestamp =  getDate(attributeMap, "time:timestamp");

						Date startOfProcess = timestamp;
		        		Date endOfProcess = new Date(timestamp.getTime()+1000*60*60*12);

		        		long amountOfProcessTasks = DateHelper.tasksBetween(startOfProcess, endOfProcess);
		        		
		        		
		        		
		        		//ensures that there is at least one task
		        		amountOfProcessTasks = Math.max(amountOfProcessTasks, 1);
		        		
		        		String traceIdentifier = traceName.getValue();

		        		ProcessModel instanceToAddTo = null;
						
						if(instances.containsKey(traceIdentifier))
						{
							instanceToAddTo = instances.get(traceIdentifier);
						}
						else
						{
							instanceToAddTo = new ProcessModel(traceIdentifier);
							instances.put(traceIdentifier, instanceToAddTo);
						}
						
						//if(amountOfProcessTasks > Config.minProcessTasks)
						{
							instanceToAddTo.addNovelInstance(startOfProcess, endOfProcess, amountOfProcessTasks);
						}
						//else
						{
							//System.out.println("Ignored a process, not enough tasks!");
						}
						
						takenAcitivies++;
						
					}
					countTraces ++;		

					System.out.println("traces:"+ countTraces + " Activities:"+countActivities + " ignored:"+ignoredActivites);
	        	}
		  }
		  
        return instances.values().stream().collect(Collectors.toList());
	}
			
	//assumes acitivites as processes because the BPIC logs gainaned not enough information on multiple processes
	//so we turn each activity into a process and check how occurs in relation to other activities
	private static List<ProcessModel> parseLogBPIC2015(XesXmlParser parser, File xesFile) throws Exception {

		Map<String, ProcessModel> instances = new HashMap<>();

		List<XLog> xLogs = parser.parse(xesFile);

		int countTraces = 0, countActivities = 0, ignoredActivites=0, takenAcitivies=0;
		
		  for (XLog xLog : xLogs) {
	        	
	        	for (XTrace trace : xLog) {
	        		
					for (XEvent event : trace) {

						countActivities++;

						XAttributeMap attributeMap = event.getAttributes();

		        		XAttributeLiteral traceName = (XAttributeLiteral)attributeMap.get("activityNameEN"); //identifies the activity

		        		Date due =  getDate(attributeMap, "dueDate");
		        		Date finished =  getDate(attributeMap, "dateFinished");
		        		Date timestamp =  getDate(attributeMap, "time:timestamp");
		        		Date planned = getDate(attributeMap, "planned");

						Date startOfProcess = coalesce(planned);
		        		Date endOfProcess = coalesce(finished, timestamp, due);
		        		
		        		if(startOfProcess == null || endOfProcess == null)
		        		{
		        			ignoredActivites++;
		        			continue;		
		        		}
		        		else if(startOfProcess.equals(endOfProcess))
		        		{
		        			ignoredActivites++;
		        			continue;
		        		}
		        		
		        		if(startOfProcess.getYear()+1900!=2015 && endOfProcess.getYear()+1900!=2015)
		        		{
		        			ignoredActivites++;
		        			continue;
		        		}
		        		/*else if(!DateHelper.hasHoursMintuesSeconds(startOfProcess) || 
		        				!DateHelper.hasHoursMintuesSeconds(endOfProcess))
		        		{
		        			ignoredActivites++;
		        			continue;
		        		}*/
		        		
		        		if(startOfProcess.after(endOfProcess))
		        		{
		        			Date temp = startOfProcess;
		        			startOfProcess = endOfProcess;
		        			endOfProcess = temp;
		        		}
		        		
		        		long amountOfProcessTasks = DateHelper.tasksBetween(startOfProcess, endOfProcess);
		        		
		        		
		        		
		        		//ensures that there is at least one task
		        		amountOfProcessTasks = Math.max(amountOfProcessTasks, 1);
		        		
		        		String traceIdentifier = traceName.getValue();

		        		ProcessModel instanceToAddTo = null;
						
						if(instances.containsKey(traceIdentifier))
						{
							instanceToAddTo = instances.get(traceIdentifier);
						}
						else
						{
							instanceToAddTo = new ProcessModel(traceIdentifier);
							instances.put(traceIdentifier, instanceToAddTo);
						}
						
						//if(amountOfProcessTasks > Config.minProcessTasks)
						{
							instanceToAddTo.addNovelInstance(startOfProcess, endOfProcess, amountOfProcessTasks);
						}
						//else
						{
							//System.out.println("Ignored a process, not enough tasks!");
						}
						
						takenAcitivies++;
						
					}
					countTraces ++;		
					
					System.out.println("traces:"+ countTraces + " Activities:"+countActivities + " ignored:"+ignoredActivites);
	        	}
		  }
		  
        return instances.values().stream().collect(Collectors.toList());
	}
			
	private static Date getDate(XAttributeMap attributeMap, String valuename) throws ParseException
	{
		Date result = null;
		
		XAttribute attribute =  attributeMap.get(valuename);
	
		if(attribute != null)
		{
			if(attribute instanceof XAttributeTimestamp)
			{
				result = ((XAttributeTimestamp)attribute).getValue();
			}
			else if(attribute instanceof XAttributeLiteral)
			{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
				
				String dateString = ((XAttributeLiteral)attribute).getValue();
				
				result = df.parse(dateString);
			}
		}
		
		return result;
	}
	
	private static void listf(String directoryName, List<File> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	            listf(file.getAbsolutePath(), files);
	        }
	    }
	}
}
