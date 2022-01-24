package pack1;

public class VierteKlasse {
	
	private int val;
	
	public VierteKlasse() {
		this.val = 5;
	}
	
	public int getValueFromOtherClass() {
		return DritteKlasse.value;
	}
	
	public int getSum() {
		return val + DritteKlasse.value;
	}

}
