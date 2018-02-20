package BaseSig;

import InstanceData.ProcessModel;

public class CutInstances {

	@Override
	public String toString() {
		return "CutInstances [processThatWasCut=" + processThatWasCut + ", amountOfTimesitWasCut="
				+ amountOfTimesitWasCut + "]";
	}

	private final ProcessModel processThatWasCut;
	private final int amountOfTimesitWasCut;
	
	public CutInstances(ProcessModel instance, int timesCut)
	{
		this.processThatWasCut = instance;
		this.amountOfTimesitWasCut = timesCut;
	}

	public int getAmountOfTimesitWasCut() {
		return amountOfTimesitWasCut;
	}

	public ProcessModel getProcessThatWasCut() {
		return processThatWasCut;
	}
	
	
	@Override
	public boolean equals(Object obj) {

		boolean result = false;
		
		if(obj != null && obj instanceof ProcessModel)
		{
			CutInstances other = (CutInstances)obj;
			
			result = other.amountOfTimesitWasCut == amountOfTimesitWasCut &&
					other.processThatWasCut.equals(processThatWasCut);
		}
				
		return result;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return processThatWasCut.hashCode() * 37 + amountOfTimesitWasCut;
	}
	
}
