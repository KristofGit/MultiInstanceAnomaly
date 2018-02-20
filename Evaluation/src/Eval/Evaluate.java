package Eval;

import DataExtractionSAX.ExtractSAXForBPIC2017;

public class Evaluate {

	
	public void calculatEvaluation()
	{
		DatePreparation data = new DatePreparation();
		
		for(int i=0;i<100;i++)
		{
		data.readDataAndEvaluateData();
		}

	}
}
