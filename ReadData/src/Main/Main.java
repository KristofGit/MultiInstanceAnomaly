package Main;

import java.util.List;

import DataExtraction.Extract;
import InstanceData.ProcessModel;

public class Main {

	public static void main(String[] args) {
		Extract readDate = new Extract();
	
		 List<ProcessModel> processInstances  = readDate.analyzeXES();
	}

}
