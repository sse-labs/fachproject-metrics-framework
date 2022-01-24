class C
{
	/* references: 0 */
	public void noOp() {}

	/* references: 0 */
	public int incInt(int i) {
		i = i+1;
		return i;
	}

	/* references: 0 */
	public C myself() {
		return this;
	}

	/* references: 2 (classes A,B) */
	public void emptyBody(int i, A a, double d, B b) {}

	/* references: 1 (class A) */
	public int staticFieldAccess() {
		return A.defaultInt;
	}

	/* references: 2 (classes A,B) */
	public void staticFieldAccess2() {
		B.defaultInt = A.defaultInt;
	}

	/* references: 1 (class B) */
	public int getField(B b) {
		return b.publicField;
	}

	/* references: 1 (class B) */
	public void setField(B b) {
		b.setId(0);
		b.publicField = 23;
	}

	/* references: 2 (classes A,B) */
	public void setField2(B b) {
		b.setId(A.defaultInt);
	}

	/* references: 1 (class B) */
	public B bFactory() {
		B b = new B();
		return b;
	}

	/* references: 1 (class B) */
	public B getAB() {
		return bFactory();
	}

	/* references: 2 (classes B,String) */
	public boolean compare(B b) {
		return b.getName().equals("Class B");
	}

	/* references: 3 (classes A,B,String) */
	public String aOrB() {
		if ((new A()).getId() > this.bFactory().getId()) return "A";
		else return "B";
	}
}
