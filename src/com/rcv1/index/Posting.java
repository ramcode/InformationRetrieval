package com.rcv1.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Posting {

	private String term;
	private Long count;
	private LinkedList<String> postingList = new LinkedList<String>();

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public LinkedList<String> getPostingList() {
		return postingList;
	}

	public void setPostingList(LinkedList<String> postingList) {
		Collections.sort(postingList, new DocumentComparator());
		this.postingList = postingList;
	}

	public static Long getPostingListSize(String term, List<Posting> postingsList) {
		Map<String, Long> postings = new LinkedHashMap<String, Long>();
		for (Posting posting : postingsList) {
			postings.put(posting.getTerm(), posting.getCount());
		}
		return postings.get(term);

	}
	
	/*public List<String> getDAATIntersections(List<Posting> queryPostings){
		Collections.sort(queryPostings,new PostingListSizeAscendingComparator());
		List<String> combinedList = new LinkedList<String>();
		for(int i=0; i<queryPostings.size();i++){
			combinedList = findIntersection(queryPostings.get(i), queryPostings.get(i+1));
			return findIntersection(posting1, posting2)
		}
		
	}*/
	
	private List<String> findIntersection(LinkedList<String> posting1, LinkedList<String> posting2) {
		List<String> combinedList = new LinkedList<String>();
		int i = 0, j = 0;
		while (i != posting1.size() && j != posting1.size()) {
			Integer doc1Id = Integer.parseInt(posting1.get(i).split("/")[0]);
			Integer doc2Id = Integer.parseInt(posting1.get(j).split("/")[0]);
			if (doc1Id == doc2Id) {
				combinedList.add(posting1.get(i));
			} else if (doc1Id < doc2Id) {
				i++;
			} else {
				j++;
			}
		}
		return combinedList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((postingList == null) ? 0 : postingList.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Posting other = (Posting) obj;
		if (count == null) {
			if (other.count != null)
				return false;
		} else if (!count.equals(other.count))
			return false;
		if (postingList == null) {
			if (other.postingList != null)
				return false;
		} else if (!postingList.equals(other.postingList))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
}
