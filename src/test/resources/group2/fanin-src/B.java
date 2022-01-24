/*
 * AVGFanIn:
 *  methods = 5 (4 + generated cinit())
 *  sum(FanIn) = 1
 *  avg = 0.2
 */
class B
{
	public int integer = 2;
	public static int truth = 23;

	/* FanIn: 1 (call in Main.foo) */
	public B() {
		A a = new A();
		A.staticWork(this);
	}

	/* FanIn: 0 */
	private void b1() {
		A a = new A();
		if (a.chunk(false)) {
			a.doNothing();
		}
	}

	/* FanIn: 0 */
	private void b2() {
		A a = new A();
		if (A.staticWork(1, "") > 2) {
			a.calc(2);
		}
	}

	/* FanIn: 0 */
	private void b3() {
		A a = new A();
		a.doNothing();
		a.endless();
		a.lots(this, 0, 1);
	}
}
