package structures;

public class Friend {
	public String name;
	public String school;
	
	public Friend(String name, String school) {
		super();
		this.name = name;
		this.school = school;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Friend other = (Friend) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (school == null) {
			if (other.school != null)
				return false;
		} else if (!school.equals(other.school))
			return false;
		return true;
	}
	
	public String toString() {
		String friend = this.name;
		
		if (this.school.compareTo("") == 0)
			friend += "|n";
		else
			friend += "|y|" + this.school;
		
		return friend;
	}
	
	public String getName() {
		return name;
	}
}
