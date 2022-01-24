class Person
{
	private String firstName;
	public String lastName;
	private int age;
	public char gender;


	public void setName(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public int getAge() {
		return this.age;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}
}
