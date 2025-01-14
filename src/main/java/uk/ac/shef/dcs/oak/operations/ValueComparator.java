package uk.ac.shef.dcs.oak.operations;

import java.util.Comparator;
import java.util.Map;

/**
 * @author annalisa ValueComparator for Map<String, Double>.
 * 
 */
public class ValueComparator implements Comparator<String> {

	Map<String, Double> base;

	public ValueComparator(Map<String, Double> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}