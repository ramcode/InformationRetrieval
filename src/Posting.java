
import java.util.Collections;
import java.util.LinkedList;

public class Posting {

	private String term;
	private Long count;
	private LinkedList<String> postingList = new LinkedList<String>();

	public Posting() {

	}

	public Posting(Posting posting) {
		this.term = posting.getTerm();
		this.count = posting.getCount();
		this.postingList = posting.getPostingList();
	}

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
