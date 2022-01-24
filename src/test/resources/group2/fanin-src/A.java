/*
 * AVGFanIn:
 *  methods = 20
 *  sum(FanIn) = 50
 *  avg = 2.5
 */
class A
{
	private String name = "classA";
	private int magic = 42;

	/*
	 * FanIn implicit constructor A(): 6
	 * (4 calls from class B, 2 calls from class Main)
	 */

	/* FanIn: 0 */
	private int prvMethod() { doNothing_(); return 1; }

	/* FanIn: 0 */
	private void fieldWrite() { magic = 0; }

	/* FanIn: 0 */
	public void unused1() {}

	/* FanIn: 1 */
	protected void unused2(int n) {}

	/* FanIn: 1 */
	public String unused3() { return name; }

	/* FanIn: 1 */
	private int unused4() { return B.truth; }

	/* FanIn: 2 */
	public int unused5(int n) { return n+magic; }

	/* FanIn: 2 */
	private void unused6(int n, int m) { magic = n-m; }

	/* FanIn: 4 */
	public boolean unused7(int n, int m, int k) {
		if (n+m > magic) return true;
		else return false;
	}

	/* FanIn: 3 (calls from Main.foo, B.b1, B.b3) */
	public void doNothing() {}

	/* FanIn: 1 (call from A.prvMethod) */
	private void doNothing_() {}

	/* FanIn: 2 (one parameter, one recursive call) */
	private void recursion(int x) { if (x > 0) recursion(x-1); }

	/* FanIn: 2 (one recursive call, one from B.b3) */
	public void endless() { endless(); }

	/* FanIn: 4 (calls from B.b2, Main.main) */
	public static int staticWork(int i, String s) {
		return s.length() * 2;
	}

	/* FanIn: 3 (one parameter, one field read, one call) */
	public static void staticWork(B b) {
		b.integer = b.truth+1;
	}

	/* FanIn: 4 (one parameter, calls from B.b2, Main.main, Main.foo) */
	public int calc(int val) {
		return val*2;
	}

	/* FanIn: 2 (one parameter, one call from B.b1) */
	public boolean chunk(boolean val) {
		return val || false;
	}

	/* FanIn: 3 (one parameter, one read, one call from Main.foo) */
	public int foo(B b) {
		return b.integer + 2;
	}

	/* FanIn: 9 (three parameters, three reads, calls from self, Main.foo, B.b3) */
	public int lots(B b, int i, int j) {
		if (i+j > 0)
			if (magic > b.integer)
				return lots(b, 0, 0);
		return B.truth;
	}
}
