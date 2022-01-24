package pack1;

public class SiebteKlasse extends SechsteKlasse {
	
	private int value3;

	SiebteKlasse(int value1, int value2, int value3) {
		super(value1, value2);
		this.value3 = value3;
	}
	
	public void changeValues(int value1, int value2, int value3) {
		setValues(value1, value2);
		this.value3 = value3;
	}
	
	public int getValue2() {
		return value3;
	}

}
