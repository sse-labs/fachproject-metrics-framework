/*
 * AVGFanOut:
 *  methods = 7+1
 *  sum(FanOut) = 14
 *  avg = 1.75
 */
class B
{
	private Person person;

	/* default constructor has fanout 1 (calls init on java.lang.Object) */

	/* fanout: 0 */
	public void zero() {}

	/* fanout: 0 */
	public void zero_a(Person p) {
		char g = p.gender;
		String n = p.lastName;
	}

	/* fanout: 0 */
	public boolean zero_b(Person p) {
		if (p.gender == 'a') return true;
		return false;
	}

	/* fanout: 1 */
	public String one(Person p) {
		return p.getFirstName();
	}

	/* fanout: 1 */
	public void one_a(Person p) {
		p.lastName = "a last name";
	}

	/* fanout: 3 */
	public void three(Person p) {
		p.setName("first", "last");
	}

	/* fanout: 8 */
	public void eight(Person p) {
		String f = p.getFirstName(); // 1
		p.gender = 'b'; // 1
		p.lastName = "last name"; // 1
		p.setName(p.lastName, f); // 3
		p.setGender(p.gender); // 2
	}
}
