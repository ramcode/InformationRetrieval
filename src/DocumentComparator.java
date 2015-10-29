
import java.util.Comparator;

public class DocumentComparator implements Comparator<String> {

	@Override
	public int compare(String doc1, String doc2) {
		Integer doc1Id = Integer.parseInt(doc1.split("/")[0].trim());
		Integer doc2Id = Integer.parseInt(doc2.split("/")[0].trim());
		if (doc1Id < doc2Id) {
			return -1;
		} else if (doc1Id.equals(doc2Id)) {
			return 0;
		} else {
			return 1;
		}
	}

}
