
import java.util.Comparator;
import java.util.List;

public class PostingSizeDefaultComparator implements Comparator<List<String>> {

	@Override
	public int compare(List<String> o1, List<String> o2) {
		if (o1.size() < o2.size()) {
			return -1;
		} else if (o1.size() == o2.size()) {
			return 0;
		} else {
			return 1;
		}
	}

}
