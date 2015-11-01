package ch.hsr.searchengineupdater;

import java.util.Comparator;

public class TupleComparator implements Comparator<Tuple> {

	@Override
	public int compare(Tuple arg0, Tuple arg1) {
		return Double.compare(arg0.similarity , arg1.similarity)  * -1;
	}

}
