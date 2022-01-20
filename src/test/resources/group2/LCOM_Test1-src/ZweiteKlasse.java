package pack1;

public class ZweiteKlasse {
	
	private int value1;
	private int value2;
	private String text;
	
	public ZweiteKlasse() {
		value1 = 3;
		value2 = 5;
		text = "sinnlos";
	}
	
	public void deletetext() {
		text = "";
	}
	
	public void changeText(String text) {
		this.text = text;
	}
	
	public int getSum() {
		return value1 + value2;
	}
	
	public void changeValues(int a, int b) {
		value1 = a;
		value2 = b;
	}
	
	public int mult() {
		int ergebnis = 0;
		ergebnis = value1 * value2;
		return ergebnis;
	}
	
	public String getText() {
		return text;
	}
	
	public int add(int c, int d) {
		int e = c + d;
		return e;
	}

}
