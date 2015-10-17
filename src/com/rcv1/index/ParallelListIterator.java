package com.rcv1.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ParallelListIterator<T> implements Iterable<List<T>> {

	private List<LinkedList<T>> lists = null;

	public ParallelListIterator(List<LinkedList<T>> lists) {
		this.lists = new LinkedList<LinkedList<T>>();
		this.lists = lists;
	}

	public Iterator<List<T>> iterator() {
		return new Iterator<List<T>>() {
			private int loc = 0;

			public boolean hasNext() {
				boolean hasNext = false;
				for (List<T> list : lists) {
					hasNext |= (loc < list.size());
				}
				return hasNext;
			}

			public List<T> next() {
				List<T> vals = new ArrayList<T>(lists.size());
				for (int i = 0; i < lists.size(); i++) {
					vals.add(loc < lists.get(i).size() ? lists.get(i).get(loc) : null);
				}
				loc++;
				return vals;
			}
			
			public List<T> advanceNext() {
				List<T> vals = new ArrayList<T>(lists.size());
				for (int i = 0; i < lists.size(); i++) {
					vals.add(loc < lists.get(i).size() ? lists.get(i).get(loc) : null);
				}
				loc++;
				return vals;
			}

			public void remove() {
				for (List<T> list : lists) {
					if (loc < list.size()) {
						list.remove(loc);
					}
				}
			}
		};
	}
}