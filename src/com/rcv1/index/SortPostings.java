package com.rcv1.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/*
 * @author ramesh
 * Class for sorting postings list for rcv1 index
 * @date 10/13/2015
 */
public class SortPostings {

	public static BufferedReader loadIndex(String path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reader;
	}

	public static LinkedHashMap<String, List<Integer>> sortPostingsInAscendingOrderOfDocId(BufferedReader source)
			throws Exception {
		File newFile = null;
		LinkedHashMap<String, List<Integer>> index = null;
		List<Integer> postingList = null;
		try {
			if (source != null) {
				String termPosting;
				index = new LinkedHashMap<String, List<Integer>>();
				while ((termPosting = source.readLine()) != null) {
					String[] postings = termPosting.split("\\\\");
					String term = postings[0];
					String termCount = postings[1];
					String posting = postings[2];
					if (posting != null) {
						Integer startIndex = posting.indexOf("[");
						Integer endIndex = posting.indexOf("]");
						String postingString = posting.substring(startIndex + 1, endIndex);
						String[] docPostings = postingString.split(",");
						postingList = new LinkedList<Integer>();
						for (String docPosting : docPostings) {
							String[] docs = docPosting.trim().split("/");
							postingList.add(Integer.parseInt(docPosting.split("/")[0].trim()));
						}
					}
					Collections.sort(postingList, new DocumentComparator());
					index.put(term, postingList);
				}
			}
		} catch (Exception ex) {

		}
		return index;
	}

	public static LinkedHashMap<String, List<Integer>> sortPostingsInDescendingOrderOfFreq(BufferedReader source)
			throws Exception {
		File newFile = null;
		LinkedHashMap<String, List<Integer>> index = null;
		List<Integer> postingList = null;
		try {
			if (source != null) {
				String termPosting;
				index = new LinkedHashMap<String, List<Integer>>();
				while ((termPosting = source.readLine()) != null) {
					String[] postings = termPosting.split("\\\\");
					String term = postings[0];
					String termCount = postings[1];
					String posting = postings[2];
					if (posting != null) {
						Integer startIndex = posting.indexOf("[");
						Integer endIndex = posting.indexOf("]");
						String postingString = posting.substring(startIndex + 1, endIndex);
						String[] docPostings = postingString.split(",");
						postingList = new LinkedList<Integer>();
						for (String docPosting : docPostings) {
							String[] docs = docPosting.split("/");
							postingList.add(Integer.parseInt(docPosting.split("/")[0].trim()));
						}	
					}
					Collections.sort(postingList, new FrequencyComparator());
					index.put(term, postingList);

				}

			}
		} catch (Exception ex) {

		}
		return index;
	}

	public static void main(String[] args) {
		try {
			LinkedHashMap<String, List<Integer>> index1 = sortPostingsInAscendingOrderOfDocId(
					loadIndex("resources/term.idx"));
			/*LinkedHashMap<String, List<Integer>> index2 = sortPostingsInDescendingOrderOfFreq(
					loadIndex("resources/term.idx"));*/
			System.out.println(index1);
			/*for (Entry<String, List<Integer>> entry : index1.entrySet()) {
				System.out.println(entry.getKey() + "------->" + entry.getValue());
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
