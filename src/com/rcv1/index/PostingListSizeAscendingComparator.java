package com.rcv1.index;

import java.util.Comparator;

public class PostingListSizeAscendingComparator implements Comparator<Posting> {

	@Override
	public int compare(Posting entry1, Posting entry2) {
		// TODO Auto-generated method stub
		if (entry1.getCount() < entry2.getCount()) {
			return -1;
		} else if (entry1.getCount() == entry2.getCount()) {
			return 0;
		} else {
			return 1;
		}
	}
}

