class A
{
	public static int defaultInt = -1;
	private static int classCount = 0;
	protected final String name = "Class A";
	protected int id;

	public A() {
		classCount++;
		this.id = classCount;
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public static int getClassCount() {
		return classCount;
	}
}
