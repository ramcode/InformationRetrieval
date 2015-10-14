package com.rcv1.index;

import java.util.Comparator;

public class DocumentComparator implements Comparator<Integer> {

	@Override
	public int compare(Integer doc1, Integer doc2) {
		if (doc1 < doc2) {
			return -1;
		} else if (doc1 == doc2) {
			return 0;
		} else {
			return 1;
		}
	}

}
