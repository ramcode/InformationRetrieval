package com.rcv1.index;

import java.util.Comparator;

public class FrequencyComparator implements Comparator<Integer> {

	@Override
	public int compare(Integer freq1, Integer freq2) {
		if (freq1 < freq2) {
			return 1;
		} else if (freq1 == freq2) {
			return 0;
		} else {
			return -1;
		}
	}

}
