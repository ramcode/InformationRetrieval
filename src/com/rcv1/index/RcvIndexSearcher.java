package com.rcv1.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * @author ramesh
 * Class for sorting postings list for rcv1 index
 * @date 10/13/2015
 */
public class RcvIndexSearcher {

	private Map<String, LinkedList<String>> sourceIndex;
	private Map<String, LinkedList<String>> sortedIndex;
	private Map<String, LinkedList<String>> sortedIndexByDocId;
	private Map<String, LinkedList<String>> sortedIndexBYFreqId;
	private List<Posting> postingsList = new ArrayList<Posting>();

	public Map<String, LinkedList<String>> getSortedIndexByDocId() {
		return sortedIndexByDocId;
	}

	public void setSortedIndexByDocId(Map<String, LinkedList<String>> sortedIndexByDocId) {
		this.sortedIndexByDocId = sortedIndexByDocId;
	}

	public Map<String, LinkedList<String>> getSortedIndexBYFreqId() {
		return sortedIndexBYFreqId;
	}

	public void setSortedIndexBYFreqId(Map<String, LinkedList<String>> sortedIndexBYFreqId) {
		this.sortedIndexBYFreqId = sortedIndexBYFreqId;
	}

	private BufferedReader source;

	public Map<String, LinkedList<String>> getSourceIndex() {
		return sourceIndex;
	}

	public Map<String, LinkedList<String>> getSortedIndex() {
		return sortedIndex;
	}

	public void setSortedIndex(Map<String, LinkedList<String>> sortedIndex) {
		this.sortedIndex = sortedIndex;
	}

	public void setSourceIndex(Map<String, LinkedList<String>> sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	public BufferedReader getSource() {
		return source;
	}

	public void setSource(BufferedReader source) {
		this.source = source;
	}

	public void setSource(String path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setSource(reader);
	}

	public void setSourceIndex(BufferedReader source) {
		Map<String, LinkedList<String>> index = null;
		LinkedList<String> postingList = null;
		List<Posting> postingsList = null;
		try {
			if (source != null) {
				String termPosting;
				index = new LinkedHashMap<String, LinkedList<String>>();
				postingsList = new ArrayList<Posting>();
				while ((termPosting = source.readLine()) != null) {
					String[] postings = termPosting.split("\\\\");
					String term = postings[0];
					String posting = postings[2];
					Posting post = new Posting();
					Long count = Long.parseLong(postings[1].substring(1));
					if (posting != null) {
						Integer startIndex = posting.indexOf("[");
						Integer endIndex = posting.indexOf("]");
						String postingString = posting.substring(startIndex + 1, endIndex);
						String[] docPostings = postingString.split(",");
						postingList = new LinkedList<String>();
						for (String docPosting : docPostings) {
							postingList.add(docPosting);
						}
					}
					post.setTerm(term);
					post.setPostingList(postingList);
					post.setCount(count);
					postingsList.add(post);
					index.put(term, postingList);
				}
			}
			setSourceIndex(index);
			setPostingsList(postingsList);
			setSortedIndex(getPostingsForTopTerms(postingsList));
		} catch (Exception ex) {

		}
	}

	public List<Posting> getPostingsList() {
		return postingsList;
	}

	public void setPostingsList(List<Posting> postingsList) {
		this.postingsList = postingsList;
	}

	public Map<String, LinkedList<String>> getPostingsInAscendingOrderOfDocId() throws Exception {
		Map<String, LinkedList<String>> newIndex = null;
		try {
			newIndex = new LinkedHashMap<String, LinkedList<String>>(getSourceIndex());
			for (Entry<String, LinkedList<String>> entry : newIndex.entrySet()) {
				newIndex.put(entry.getKey(), entry.getValue());
				Collections.sort(entry.getValue(), new DocumentComparator());
			}
		} catch (Exception ex) {

		}
		return newIndex;
	}

	public Map<String, LinkedList<String>> getPostingsInDescendingOrderOfFreq() throws Exception {
		Map<String, LinkedList<String>> newIndex = null;
		try {
			newIndex = new LinkedHashMap<String, LinkedList<String>>(getSourceIndex());
			for (Entry<String, LinkedList<String>> entry : newIndex.entrySet()) {
				newIndex.put(entry.getKey(), entry.getValue());
				Collections.sort(entry.getValue(), new FrequencyComparator());
			}
		} catch (Exception ex) {

		}
		return newIndex;
	}

	public Map<String, LinkedList<String>> getPostingsForTopTerms(List<Posting> postingsList) throws Exception {
		Map<String, LinkedList<String>> sortedList = null;
		try {
			sortedList = new LinkedHashMap<String, LinkedList<String>>();
			Collections.sort(postingsList, new PostingListDescendingComparator());
			for (Posting entry : postingsList) {
				sortedList.put(entry.getTerm(), entry.getPostingList());
			}
		} catch (Exception ex) {
		}
		return sortedList;
	}

	public List<String> getTopK(Integer k) {
		List<String> topKterms = null;
		try {
			topKterms = new LinkedList<String>();
			if (getSortedIndex() != null && getSortedIndex().size() > 0) {
				List<Entry<String, LinkedList<String>>> entryList = new LinkedList<Entry<String, LinkedList<String>>>(
						sortedIndex.entrySet());
				for (Entry<String, LinkedList<String>> entry : entryList.subList(0, k)) {
					topKterms.add(entry.getKey());
				}
			}
		} catch (Exception ex) {

		}
		return topKterms;
	}

	public List<String> getPostingsByDocId(List<String> queries) throws Exception {
		List<String> postings = null;
		List<String> postingStrings = new LinkedList<String>();
		try {
			for (String query : queries) {
				postings = new LinkedList<String>();
				if (!getSortedIndexByDocId().keySet().contains(query)) {
					postingStrings.add("term not found");
				} else {
					List<String> results = getSortedIndexByDocId().get(query);
					for (String result : results) {
						postings.add(result.split("/")[0].trim());
					}
					String posting = postings.toString();
					postingStrings.add(posting.substring(1, posting.length() - 1));
				}
			}
		} catch (Exception ex) {

		}
		return postingStrings;
	}

	public List<String> getPostingsByFreq(List<String> queries) throws Exception {
		List<String> postings = null;
		List<String> postingStrings = new ArrayList<String>();
		try {
			for (String query : queries) {
				postings = new ArrayList<String>();
				if (!getSortedIndexBYFreqId().keySet().contains(query)) {
					postingStrings.add("term not found");
				} else {
					List<String> results = getSortedIndexBYFreqId().get(query);
					for (String result : results) {
						postings.add(result.split("/")[0].trim());
					}
					String posting = postings.toString();
					postingStrings.add(posting.substring(1, posting.length() - 1));
				}
			}
		} catch (Exception ex) {

		}
		return postingStrings;
	}

	public List<String> getLeastK(Integer k) {
		List<String> topKterms = null;
		try {
			topKterms = new ArrayList<String>();
			if (getSortedIndex() != null && getSortedIndex().size() > 0) {
				int size = getSortedIndex().size();
				List<Entry<String, LinkedList<String>>> entryList = new ArrayList<Entry<String, LinkedList<String>>>(
						sortedIndex.entrySet());
				List<Entry<String, LinkedList<String>>> subList = entryList.subList(size - k, size);
				for (int i = subList.size() - 1; i > -1; i--) {
					topKterms.add(subList.get(i).getKey());
				}
			}
		} catch (Exception ex) {

		}
		return topKterms;
	}

	public LinkedList<String> getTAATQueryAndResults(List<String> queries) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexByDocId().get(query));
				}
			}
			// Collections.sort(list);
		} catch (Exception ex) {
		}
		return getTAATIntersections(queryPostings);
	}
	
	public LinkedList<String> getTAATQueryOrResults(List<String> queries) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexByDocId().get(query));
				}
			}
			// Collections.sort(list);
		} catch (Exception ex) {
		}
		return getTAATUnion(queryPostings);
	}

	public LinkedList<String> getTAATIntersections(Map<String, LinkedList<String>> postings) {
		LinkedList<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		LinkedList<String> firstPosting = postingList.get(0);
		LinkedList<String> newList = firstPosting;
		for(int i=1;i<postingList.size();i++) {
			newList = getTAATIntersections(newList, postingList.get(i));
		}	
		return newList;
	}
	
	private LinkedList<String> getTAATIntersections(LinkedList<String> list1, LinkedList<String> list2) {
		LinkedList<String> newList = new LinkedList<String>();
		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				Integer doc1Id = Integer.parseInt(list1.get(i).split("/")[0].trim());
				Integer doc2Id = Integer.parseInt(list2.get(j).split("/")[0].trim());
				if (doc1Id.equals(doc2Id)) {
					newList.add(list1.get(i).trim());
				}
			}
		}
		return newList;
	}
	
	public LinkedList<String> getTAATUnion(Map<String, LinkedList<String>> postings) {
		LinkedList<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		LinkedList<String> firstPosting = postingList.get(0);
		LinkedList<String> newList = firstPosting;
		for(int i=1;i<postingList.size();i++) {
			newList = getTAATUnion(newList, postingList.get(i));
		}	
		return newList;
	}
	
	private LinkedList<String> getTAATUnion(LinkedList<String> list1, LinkedList<String> list2) {
		int i = 0;
		int j = 0;
		int length1 = list1.size();
		int length2 = list2.size();
		LinkedHashSet<String> newList = new LinkedHashSet<String>();
		while (i != length1 && j != length2) {
			Integer doc1Id = Integer.parseInt(list1.get(i).split("/")[0].trim());
			Integer doc2Id = Integer.parseInt(list2.get(j).split("/")[0].trim());
			if (doc1Id.equals(doc2Id)) {
				newList.add(list1.get(i).trim());
			} else {
				newList.add(list1.get(i).trim());
				newList.add(list2.get(j).trim());
			}
			i++;
			j++;
		}
		if(i<list1.size()){
			for(int k=0; k<list1.size();k++){
			newList.add(list1.get(k));
		}
		}
		else if(j<list2.size()){
			for(int k=0; k<list1.size();k++){
				newList.add(list1.get(k));
			}
		}
		return new LinkedList<String>(newList);
	}
	
	public LinkedList<String> getDAATQueryAndResults(List<String> queries) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexByDocId().get(query));
				}
			}
			// Collections.sort(list);
		} catch (Exception ex) {
		}
		return getDAATIntersections(queryPostings);
	}
	
	public LinkedHashSet<String> getDAATQueryOrResults(List<String> queries) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexByDocId().get(query));
				}
			}
			// Collections.sort(list);
		} catch (Exception ex) {
		}
		return getDAATUnion(queryPostings);
	}
	
	
	public LinkedList<String> getDAATIntersections(Map<String, LinkedList<String>> postings) {
		LinkedList<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		LinkedList<Integer> pointers = new LinkedList<Integer>();
		LinkedList<String> results = new LinkedList<String>();
		// ParallelListIterator it = new ParallelListIterator(postingList);
		for (int i = 0; i < postingList.size(); i++) {
			pointers.add(0);
		}
		while (checkPointerIndex(pointers, postingList)) {
			LinkedList<Integer> indexValues = new LinkedList<Integer>();
			for (int i = 0; i < pointers.size(); i++) {
				indexValues.add(Integer.parseInt(postingList.get(i).get(pointers.get(i)).split("/")[0].trim()));
			}
				if (isListOfSameElements(indexValues)) {
					results.add(indexValues.get(0).toString());
					for (int k = 0; k < pointers.size(); k++) {
						pointers.set(k, pointers.get(k) + 1);

					}
				} else {
					Integer maxValue = Collections.max(indexValues);
					int indexOfMaxElement = indexValues.indexOf(Collections.max(indexValues));
					for (int k = 0; k < pointers.size(); k++) {
						Integer dupMax = Integer.parseInt(postingList.get(k).get(pointers.get(k)).split("/")[0].trim());
						if (k != indexOfMaxElement && !dupMax.equals(maxValue)) {
							pointers.set(k, pointers.get(k) + 1);

						}
					}
				}
			}
		return results;
	}
	
	public LinkedHashSet<String> getDAATUnion(Map<String, LinkedList<String>> postings) {
		LinkedList<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		LinkedList<Integer> pointers = new LinkedList<Integer>();
		LinkedHashSet<String> results = new LinkedHashSet<String>();
		// ParallelListIterator it = new ParallelListIterator(postingList);
		for (int i = 0; i < postingList.size(); i++) {
			pointers.add(0);
		}
		while (checkPointerIndex(pointers, postingList)) {
			for (int i = 0; i < pointers.size(); i++) {
				results.add(postingList.get(i).get(pointers.get(i)).split("/")[0].trim());
				pointers.set(i, pointers.get(i) + 1);
			}
		}
		for(int j=0;j<pointers.size();j++){
			results.addAll(postingList.get(j).subList(pointers.get(j), postingList.get(j).size()));
		}
		return results;
	}
	
	
	private boolean checkPointerIndex(List<Integer> pointers, List<LinkedList<String>> postingList){
		boolean check = false;
		for(int i=0;i<pointers.size();i++){
			if(pointers.get(i)<postingList.get(i).size()){
				check = true;
				continue;
			}
			else{
				check = false;
				break;
			}
		}
		return check;
	}
	
	public static boolean isListOfSameElements(List<? extends Object> l) {
	    Set<Object> set = new HashSet<Object>(l.size());
	    for (Object o : l) {
	        if (set.isEmpty()) {
	            set.add(o);
	        } else {
	            if (set.add(o)) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
	
	public static String toCsv(final List<?> list) {
        return toString(list, ", ");
    }

    public static String toString(final List<?> list, String delimiter) {
        final StringBuilder b = new StringBuilder();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                b.append(list.get(i).toString());
                if (i != list.size() - 1) {
                    b.append(delimiter);
                }
            }
        }
        return b.toString();
    }
	
	
	
	
	

	public static void main(String[] args) {
		try {
			RcvIndexSearcher searcher = new RcvIndexSearcher();
			searcher.setSource(args[0]);
			searcher.setSourceIndex(searcher.getSource());
			searcher.setSortedIndexByDocId(searcher.getPostingsInAscendingOrderOfDocId());
			searcher.setSortedIndexBYFreqId(searcher.getPostingsInDescendingOrderOfFreq());
			BufferedWriter logger = new BufferedWriter(new FileWriter(new File(args[1])));
			StringBuffer sb = new StringBuffer();
			//FUNCTION: getTopK
			logger.write("FUNCTION: getTopK "+args[2]+"\n");
			sb.append("Result: ");
			sb.append(toCsv(searcher.getTopK(Integer.parseInt(args[2]))));
			logger.write(sb.toString());
			//Parsing queries from query file
			BufferedReader br = new BufferedReader(new FileReader(new File(args[3])));
			String queryTerms = null;
			while((queryTerms = br.readLine())!=null){
				String[] queries = queryTerms.split(" ");
				sb = new StringBuffer();
				for(String query : queries){
				sb.append("FUNCTION: getPostings "+query+"\n");
				LinkedList<String> queryString = new LinkedList<String>();
				queryString.add(query);
				for(String result : searcher.getPostingsByDocId(queryString)){
					sb.append("Ordered by doc IDs: ").append(result+"\n");
				}
				for(String result : searcher.getPostingsByFreq(queryString)){
					sb.append("Ordered by TF: ").append(result+"\n");
				}
				}
				logger.write(sb.toString());
				sb = new StringBuffer();
				List<String> queryList = new LinkedList<String>(Arrays.asList(queries)); 
				logger.write("FUNCTION: termAtATimeQueryAnd "+toCsv(queryList));
				Long startTime = System.currentTimeMillis();
				LinkedList<String> postingsForTAAT = searcher.getTAATQueryAndResults(queryList);
				Long endTime = System.currentTimeMillis();
				sb.append(postingsForTAAT.size()+" documents are found\n");
				
				
			}
			List<String> queries = new ArrayList<String>();
			queries.add("test1");
			queries.add("test2");
			queries.add("test3");
			for (String result : searcher.getTAATQueryOrResults(queries)) {
				System.out.println(result);
			}
			/*for (String result : searcher.getPostingsByFreq(queries)) {
				System.out.println(result);
			}*/

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
