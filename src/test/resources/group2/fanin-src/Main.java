class Main
{
	public static void main(String[] args) {
		int i = (new A()).calc(23);
		int j = A.staticWork(2, "what?");
		System.out.println(i+j);
	}

	private void foo() {
		A a = new A();
		B b = new B();
		int i = a.lots(b, 1, 0);
		for (; i<a.calc(2); i++) {
			a.foo(b); a.doNothing(); a.doNothing(); a.doNothing();
		}
	}
}
