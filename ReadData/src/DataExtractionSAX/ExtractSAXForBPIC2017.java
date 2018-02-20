package DataExtractionSAX;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import InstanceData.ProcessModel;

public class ExtractSAXForBPIC2017 {

	public static List<ProcessModel> analyzeXES(String path) {

		File inputFile = new File(path);

		return analyzeXES(inputFile);
	}
	
	public static List<ProcessModel> analyzeXES(File inputFile) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        
		XESHandler userhandler = new XESHandler();

		try {
			saxParser = factory.newSAXParser();
			
		    saxParser.parse(inputFile, userhandler);
		  
		} catch (Exception e) {
			System.out.println(e);		
			
		}
            
		 return userhandler.getInstances().entrySet().stream().map(x->x.getValue()).collect(Collectors.toList());
	}
}
