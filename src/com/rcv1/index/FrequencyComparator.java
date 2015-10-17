package com.rcv1.index;

import java.util.Comparator;

public class FrequencyComparator implements Comparator<String> {

	@Override
	public int compare(String doc1, String doc2) {
		Integer freq1 = Integer.parseInt(doc1.split("/")[1].trim()); 
		Integer freq2 = Integer.parseInt(doc2.split("/")[1].trim());
		if (freq1 < freq2) {
			return 1;
		} else if (freq1 == freq2) {
			return 0;
		} else {
			return -1;
		}
	}

}
