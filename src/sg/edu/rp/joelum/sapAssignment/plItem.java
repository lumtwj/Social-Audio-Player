package sg.edu.rp.joelum.sapAssignment;

public class plItem {
	int pId;
	String pName;
	
	public plItem(int pId, String pName) {
		super();
		this.pId = pId;
		this.pName = pName;
	}

	public int getpId() {
		return pId;
	}

	public String getpName() {
		return pName;
	}
	
	public String toString() {
		return pName;
	}
}
