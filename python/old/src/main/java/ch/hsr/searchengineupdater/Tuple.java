package ch.hsr.searchengineupdater;

public class Tuple {
	
	public Tuple(long id, double sim){
		this.itemid = id;
		this.similarity = sim;
	}
	public long itemid;
	public double similarity;
}
