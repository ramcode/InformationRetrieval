
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CSE535Assignment {

	public static void main(String[] args) {
		BufferedWriter logger = null;
		BufferedReader br = null;
		try {
			RcvIndexSearcher searcher = new RcvIndexSearcher(args[0]);
			logger = new BufferedWriter(new FileWriter(new File(args[1])));
			StringBuffer sb = new StringBuffer();
			// FUNCTION: getTopK
			logger.write("FUNCTION: getTopK " + args[2] + "\n");
			sb.append("Result: ");
			sb.append(RcvIndexSearcher.toCsv(searcher.getTopK(Integer.parseInt(args[2]))) + "\n");
			logger.write(sb.toString());
			// Parsing queries from query file
			br = new BufferedReader(new FileReader(new File(args[3])));
			String queryTerms = null;
			while ((queryTerms = br.readLine()) != null) {
				String[] queries = queryTerms.split(" ");
				sb = new StringBuffer();
				for (String query : queries) {
					sb.append("FUNCTION: getPostings " + query + "\n");
					LinkedList<String> queryString = new LinkedList<String>();
					queryString.add(query);
					for (String result : searcher.getPostingsByDocId(queryString)) {
						sb.append("Ordered by doc IDs: ").append(result + "\n");
					}
					for (String result : searcher.getPostingsByFreq(queryString)) {
						sb.append("Ordered by TF: ").append(result + "\n");
					}
				}
				logger.write(sb.toString());
				sb = new StringBuffer();
				List<String> queryList = new LinkedList<String>(Arrays.asList(queries));
				String queryString = RcvIndexSearcher.toCsv(queryList);
				logger.write("FUNCTION: termAtATimeQueryAnd " + queryString + "\n");
				Long startTime = System.currentTimeMillis();
				RcvIndexSearcher.setComparisons(0);
				List<String> postingsForTAATAnd = searcher.getTAATQueryAndResults(queryList, false);
				Long endTime = System.currentTimeMillis();
				sb.append(postingsForTAATAnd.size() + " documents are found\n");
				sb.append(RcvIndexSearcher.getComparisons() + " comparisions are made\n");
				sb.append((endTime - startTime) / 1000.0 + " seconds are used\n");
				RcvIndexSearcher.setComparisons(0);
				searcher.getTAATQueryAndResults(queryList, true);
				sb.append(RcvIndexSearcher.getComparisons() + " comparisions are made with optimization\n");
				if (postingsForTAATAnd.size() > 0) {
					sb.append("Result: " + RcvIndexSearcher.toCsv(postingsForTAATAnd) + "\n");
				} else {
					sb.append("terms not found\n");
				}
				logger.write(sb.toString());
				sb = new StringBuffer();
				logger.write("FUNCTION: termAtATimeQueryOr " + queryString + "\n");
				startTime = System.currentTimeMillis();
				RcvIndexSearcher.setComparisons(0);
				List<String> postingsForTAATOr = searcher.getTAATQueryOrResults(queryList, false);
				endTime = System.currentTimeMillis();
				sb.append(postingsForTAATOr.size() + " documents are found\n");
				sb.append(RcvIndexSearcher.getComparisons() + " comparisions are made\n");
				sb.append((endTime - startTime) / 1000.0 + " seconds are used\n");
				RcvIndexSearcher.setComparisons(0);
				searcher.getTAATQueryOrResults(queryList, true);
				sb.append(RcvIndexSearcher.getComparisons() + " comparisions are made with optimization\n");
				if (postingsForTAATOr.size() > 0) {
					sb.append("Result: " + RcvIndexSearcher.toCsv(postingsForTAATOr) + "\n");
				} else {
					sb.append("terms not found\n");
				}
				logger.write(sb.toString());
				sb = new StringBuffer();
				logger.write("FUNCTION: docAtATimeQueryAnd " + queryString + "\n");
				startTime = System.currentTimeMillis();
				RcvIndexSearcher.setComparisons(0);
				List<String> postingsForDAATAnd = searcher.getDAATQueryAndResults(queryList);
				endTime = System.currentTimeMillis();
				sb.append(postingsForDAATAnd.size() + " documents are found\n");
				sb.append(RcvIndexSearcher.getComparisons() + " comparisions are made\n");
				sb.append((endTime - startTime) / 1000.0 + " seconds are used\n");
				if (postingsForDAATAnd.size() > 0) {
					sb.append("Result: " + RcvIndexSearcher.toCsv(postingsForDAATAnd) + "\n");
				} else {
					sb.append("terms not found\n");
				}
				logger.write(sb.toString());
				sb = new StringBuffer();
				logger.write("FUNCTION: docAtATimeQueryOr " + queryString + "\n");
				startTime = System.currentTimeMillis();
				RcvIndexSearcher.setComparisons(0);
				List<String> postingsForDAATOr = searcher.getDAATQueryOrResults(queryList);
				endTime = System.currentTimeMillis();
				sb.append(postingsForDAATOr.size() + " documents are found\n");
				sb.append(RcvIndexSearcher.getComparisons() + " comparisions are made\n");
				sb.append((endTime - startTime) / 1000.0 + " seconds are used\n");
				if (postingsForDAATOr.size() > 0) {
					sb.append("Result: " + RcvIndexSearcher.toCsv(postingsForDAATOr) + "\n");
				} else {
					sb.append("terms not found\n");
				}
				logger.write(sb.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			try {
				br.close();
				logger.flush();
				logger.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
