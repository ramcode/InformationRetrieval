
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * @author ramesh
 * Class for querying postings list for rcv1 index
 * @date 10/13/2015
 */
public class RcvIndexSearcher {

	private Map<String, LinkedList<String>> sourceIndex = new LinkedHashMap<String, LinkedList<String>>();
	private Map<String, LinkedList<String>> sortedIndex = new LinkedHashMap<String, LinkedList<String>>();
	private Map<String, LinkedList<String>> sortedIndexByDocId = new LinkedHashMap<String, LinkedList<String>>();
	private Map<String, LinkedList<String>> sortedIndexBYFreqId = new LinkedHashMap<String, LinkedList<String>>();
	private List<Posting> postingsList = new ArrayList<Posting>();
	static int comparisons = 0;

	public RcvIndexSearcher() {

	}

	/*
	 * Constructor for initialising index data
	 * 
	 * @params sourcefile
	 */
	public RcvIndexSearcher(String sourceFile) {
		this.setSource(sourceFile);
	}

	public static int getComparisons() {
		return comparisons;
	}

	public static void setComparisons(int comparisons) {
		RcvIndexSearcher.comparisons = comparisons;
	}

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

	public void setSource(String path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setSourceReader(reader);
	}

	/*
	 * Method to initialize all the required data for querying
	 * 
	 * @params source (input source)
	 */
	public void setSourceReader(BufferedReader source) {
		Map<String, LinkedList<String>> index = null;
		LinkedList<String> postingList = null;
		List<Posting> postingsList = null;
		try {
			if (source != null) {
				String termPosting = null;
				index = new LinkedHashMap<String, LinkedList<String>>();
				postingsList = new ArrayList<Posting>();
				while ((termPosting = source.readLine()) != null && termPosting.length() > 0) {
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
				setSourceIndex(index);
				setSortedIndexByDocId(getPostingsInAscendingOrderOfDocId(index));
				setSortedIndexBYFreqId(getPostingsInDescendingOrderOfFreq(index));
				setPostingsList(postingsList);
				setSortedIndex(getPostingsForTopTerms(postingsList));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<Posting> getPostingsList() {
		return postingsList;
	}

	public void setPostingsList(List<Posting> postingsList) {
		this.postingsList = postingsList;
	}

	/*
	 * Method which returns postings in ascending order of doc id
	 * 
	 * @params original index
	 * 
	 * @return index for postings in ascending order of doc id
	 */
	public Map<String, LinkedList<String>> getPostingsInAscendingOrderOfDocId(
			Map<String, LinkedList<String>> sourceIndex) throws Exception {
		Map<String, LinkedList<String>> newIndex = null;
		try {
			newIndex = new LinkedHashMap<String, LinkedList<String>>();
			for (Entry<String, LinkedList<String>> entry : sourceIndex.entrySet()) {
				LinkedList<String> postingList = new LinkedList<>(entry.getValue());
				Collections.sort(postingList, new DocumentComparator());
				newIndex.put(entry.getKey(), postingList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newIndex;
	}

	/*
	 * Method which returns postings in descending order of term frequency
	 * 
	 * @params original index
	 * 
	 * @return index for postings in descending order of term frequency
	 */
	public Map<String, LinkedList<String>> getPostingsInDescendingOrderOfFreq(
			Map<String, LinkedList<String>> sourceIndex) throws Exception {
		Map<String, LinkedList<String>> newIndex = null;
		try {
			newIndex = new LinkedHashMap<String, LinkedList<String>>();
			for (Entry<String, LinkedList<String>> entry : sourceIndex.entrySet()) {
				LinkedList<String> postingList = new LinkedList<>(entry.getValue());
				Collections.sort(postingList, new FrequencyComparator());
				newIndex.put(entry.getKey(), postingList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newIndex;
	}

	/*
	 * Method to get postings sorted by posting size
	 * 
	 * @params original postings list(list of posting objects)
	 * 
	 * @return postings in sorted order of postings size(descending)
	 */

	public Map<String, LinkedList<String>> getPostingsForTopTerms(List<Posting> postingsList) throws Exception {
		Map<String, LinkedList<String>> sortedList = null;
		try {
			sortedList = new LinkedHashMap<String, LinkedList<String>>();
			List<Posting> sortedPostings = new LinkedList<Posting>(postingsList);
			for (Posting entry : postingsList) {
				Posting newPosting = new Posting(entry);
				sortedPostings.add(newPosting);
			}
			Collections.sort(sortedPostings, new PostingListSizeDescendingComparator());
			for (Posting entry : sortedPostings) {
				sortedList.put(entry.getTerm(), entry.getPostingList());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sortedList;
	}

	/*
	 * Method which returns top k terms from sorted index of postings size
	 * 
	 * @param k (top k postings)
	 * 
	 * @return top k terms
	 */

	public List<String> getTopK(Integer k) {
		List<String> topKterms = null;
		try {
			topKterms = new LinkedList<String>();
			if (getSortedIndex() != null && getSortedIndex().size() > 0) {
				List<Entry<String, LinkedList<String>>> entryList = new LinkedList<Entry<String, LinkedList<String>>>(
						sortedIndex.entrySet());
				for (Entry<String, LinkedList<String>> entry : entryList.size() > k ? entryList.subList(0, k)
						: entryList.subList(0, entryList.size())) {
					topKterms.add(entry.getKey());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return topKterms;
	}

	/*
	 * Method to return postings for queries in ascending order of doc id
	 * 
	 * @param queries
	 * 
	 * @return postings for queries
	 */
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
			ex.printStackTrace();
		}
		return postingStrings;
	}

	/*
	 * Method to return postings for queries in descending order of term
	 * frequencies
	 * 
	 * @param queries
	 * 
	 * @return postings for queries
	 */
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
			ex.printStackTrace();
		}
		return topKterms;
	}

	/*
	 * Method to get TAAT query results for Boolean AND queries
	 * 
	 * @param queries
	 * 
	 * @return list of postings
	 */

	public List<String> getTAATQueryAndResults(List<String> queries, boolean optimizationRequired) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		List<String> newList = new LinkedList<String>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexBYFreqId().get(query));
				} else {
					return newList;
				}
			}
		} catch (Exception ex) {
		}
		newList = getTAATIntersections(queryPostings, optimizationRequired);
		Collections.sort(newList, new DocumentComparator());
		return newList;
	}

	/*
	 * Method to get TAAT query results for Boolean OR queries
	 * 
	 * @param queries
	 * 
	 * @return list of postings
	 */

	public List<String> getTAATQueryOrResults(List<String> queries, boolean optimizationRequired) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		List<String> newList = new LinkedList<String>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexBYFreqId().get(query));
				}
			}
			// Collections.sort(list);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (queryPostings.size() > 0) {
			newList = getTAATUnion(queryPostings, optimizationRequired);
			Collections.sort(newList, new DocumentComparator());
			return newList;
		} else {
			return newList;
		}

	}

	/*
	 * Helper method for posting intersections
	 */

	private List<String> getTAATIntersections(Map<String, LinkedList<String>> postings, boolean optimizationRequired) {
		LinkedList<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		List<String> newList = new LinkedList<String>();
		if (postingList.size() > 0) {
			if (optimizationRequired) {
				Collections.sort(postingList, new PostingSizeDefaultComparator());
			}
			LinkedList<String> firstPosting = postingList.get(0);
			newList = firstPosting;
			if (postingList.size() > 1) {
				for (int i = 1; i < postingList.size(); i++) {
					newList = getTAATIntersections(newList, postingList.get(i));
				}
			}
		}
		return newList;
	}

	/*
	 * Helper method for posting intersections
	 */
	private List<String> getTAATIntersections(List<String> list1, List<String> list2) {
		List<String> newList = new LinkedList<String>();
		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				comparisons++;
				String doc1Id = list1.get(i).split("/")[0].trim();
				String doc2Id = list2.get(j).split("/")[0].trim();
				if (doc1Id.equals(doc2Id)) {
					if (!isElementPresent(doc1Id, newList)) {
						newList.add(doc1Id);
					}
				}
			}
		}
		return newList;
	}

	/*
	 * Helper method for postings union
	 */
	public List<String> getTAATUnion(Map<String, LinkedList<String>> postings, boolean optimizationRequired) {
		LinkedList<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		List<String> newList = new LinkedList<String>();
		if (postingList.size() > 0) {
			if (optimizationRequired) {
				Collections.sort(postingList, new PostingSizeDefaultComparator());
			}
			LinkedList<String> firstPosting = postingList.get(0);
			newList = firstPosting;
			if (postingList.size() > 1) {
				for (int i = 1; i < postingList.size(); i++) {
					newList = getTAATUnion(newList, postingList.get(i));
				}
			}
		}
		return newList;
	}

	/*
	 * Helper method for postings union
	 */
	private List<String> getTAATUnion(List<String> list1, List<String> list2) {
		int i = 0;
		int j = 0;
		int length1 = list1.size();
		int length2 = list2.size();
		List<String> newList = new LinkedList<String>();
		while (i != length1 && j != length2) {
			comparisons++;
			String doc1Id = list1.get(i).split("/")[0].trim();
			String doc2Id = list2.get(j).split("/")[0].trim();
			if (!isElementPresent(doc1Id, newList)) {
				newList.add(doc1Id);
			}
			if (!isElementPresent(doc2Id, newList)) {
				newList.add(doc2Id);
			}
			i++;
			j++;
		}
		if (i < list1.size()) {
			for (int k = 0; k < list1.size(); k++) {
				String docId = list1.get(k).split("/")[0].trim();
				if (!isElementPresent(docId, newList)) {
					newList.add(docId);
				}
			}
		} else if (j < list2.size()) {
			for (int k = 0; k < list2.size(); k++) {
				String docId = list2.get(k).split("/")[0].trim();
				if (!isElementPresent(docId, newList)) {
					newList.add(docId);
				}
			}
		}
		return newList;
	}

	/*
	 * Method to get DAAT query results for Boolean AND queries
	 * 
	 * @param queries
	 * 
	 * @return list of postings
	 */

	public List<String> getDAATQueryAndResults(List<String> queries) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		List<String> newList = new LinkedList<String>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexByDocId().get(query));
				} else {
					return newList;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		newList = getDAATIntersections(queryPostings);
		Collections.sort(newList, new DocumentComparator());
		return newList;
	}

	/*
	 * Method to get DAAT query results for Boolean OR queries
	 * 
	 * @param queries
	 * 
	 * @return list of postings
	 */
	public List<String> getDAATQueryOrResults(List<String> queries) {
		Map<String, LinkedList<String>> queryPostings = new LinkedHashMap<String, LinkedList<String>>();
		List<String> newList = new LinkedList<String>();
		try {
			for (String query : queries) {
				if (getSortedIndexByDocId().keySet().contains(query)) {
					queryPostings.put(query, getSortedIndexByDocId().get(query));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (queryPostings.size() > 0) {
			newList = getDAATUnion(queryPostings);
			Collections.sort(newList, new DocumentComparator());
			return newList;
		} else {
			return newList;
		}

	}

	/*
	 * Helper method for DAAT intersections
	 */
	public List<String> getDAATIntersections(Map<String, LinkedList<String>> postings) {
		LinkedList<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		Collections.sort(postingList, new PostingSizeDefaultComparator());
		LinkedList<Integer> pointers = new LinkedList<Integer>();
		List<String> results = new LinkedList<String>();
		for (int i = 0; i < postingList.size(); i++) {
			pointers.add(i, 0);
		}
		while (checkPointerIndex(pointers, postingList)) {
			LinkedList<String> indexValues = new LinkedList<String>();
			for (int i = 0; i < pointers.size(); i++) {
				indexValues.add(postingList.get(i).get(pointers.get(i)).split("/")[0].trim());
			}
			if (isListOfSameElements(indexValues)) {
				results.add(indexValues.get(0).toString());
				for (int k = 0; k < pointers.size(); k++) {
					pointers.set(k, pointers.get(k) + 1);

				}
			} else {
				Integer maxValue = Integer.parseInt(findMaxDocId(indexValues));
				for (int k = 0; k < pointers.size(); k++) {
					comparisons++;
					Integer indexValue = Integer.parseInt(postingList.get(k).get(pointers.get(k)).split("/")[0].trim());
					if (indexValue < maxValue) {
						pointers.set(k, pointers.get(k) + 1);

					}
				}
			}
		}
		return results;
	}

	/*
	 * Helper method for DAAT unions
	 */
	public List<String> getDAATUnion(Map<String, LinkedList<String>> postings) {
		List<LinkedList<String>> postingList = new LinkedList<LinkedList<String>>(postings.values());
		Collections.sort(postingList, new PostingSizeDefaultComparator());
		LinkedList<Integer> pointers = new LinkedList<Integer>();
		List<String> results = new LinkedList<String>();
		for (int i = 0; i < postingList.size(); i++) {
			pointers.add(i, 0);
		}
		while (!checkPostingsComplete(pointers, postingList)) {
			LinkedList<String> indexValues = new LinkedList<String>();
			for (int i = 0; i < pointers.size(); i++) {
				if (pointers.get(i) != null && pointers.get(i) < postingList.get(i).size()) {
					indexValues.add(postingList.get(i).get(pointers.get(i)).split("/")[0].trim());
				} else {
					pointers.set(i, null);
				}
			}
			if (isListOfSameElements(indexValues)) {
				results.add(indexValues.get(0).toString());
				for (int k = 0; k < pointers.size(); k++) {
					if (pointers.get(k) != null) {
						pointers.set(k, pointers.get(k) + 1);
					}
				}
			} else {
				String minValue = findMinDocId(indexValues);
				results.add(minValue);
				for (int k = 0; k < pointers.size(); k++) {
					comparisons++;
					if (pointers.get(k) != null) {
						Integer indexValue = Integer
								.parseInt(postingList.get(k).get(pointers.get(k)).split("/")[0].trim());
						if (indexValue.equals(Integer.parseInt(minValue))) {
							pointers.set(k, pointers.get(k) + 1);

						}
					}

				}
			}
		}
		return results;
	}

	/*
	 * private method to check pointers for postings
	 */
	private boolean checkPostingsComplete(List<Integer> pointers, List<LinkedList<String>> postingList) {
		boolean check = false;
		for (int i = 0; i < pointers.size(); i++) {
			if (pointers.get(i) == null || pointers.get(i).equals(postingList.get(i).size())) {
				check = true;
				continue;
			} else {
				check = false;
				break;
			}
		}
		return check;
	}

	/*
	 * private method to check pointer indexes for postings
	 * 
	 */
	private boolean checkPointerIndex(List<Integer> pointers, List<LinkedList<String>> postingList) {
		boolean check = false;
		for (int i = 0; i < pointers.size(); i++) {
			if (pointers.get(i) < postingList.get(i).size()) {
				check = true;
				continue;
			} else {
				check = false;
				break;
			}
		}
		return check;
	}

	/*
	 * method to check whether all elements of a intersection list are same or
	 * not
	 */

	public static boolean isListOfSameElements(List<? extends Object> l) {
		Set<Object> set = new HashSet<Object>(l.size());
		for (Object o : l) {
			comparisons++;
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

	private <T> boolean isElementPresent(T key, Collection<T> collection) {
		boolean check = false;
		for (Object object : collection) {
			comparisons++;
			if (object.equals(key)) {
				check = true;
				break;
			}
		}
		return check;
	}

	private String findMaxDocId(List<String> values) {
		String max = null;
		if (values.size() > 0) {
			max = values.get(0);
			for (int i = 1; i < values.size(); i++) {
				comparisons++;
				if (Integer.parseInt(values.get(i)) > Integer.parseInt(max)) {
					max = values.get(i);
				}
			}
		}
		return max;
	}

	private String findMinDocId(List<String> values) {
		String min = null;
		if (values.size() > 0) {
			min = values.get(0);
			for (int i = 1; i < values.size(); i++) {
				comparisons++;
				if (Integer.parseInt(values.get(i)) < Integer.parseInt(min)) {
					min = values.get(i);
				}
			}
		}
		return min;
	}
}
