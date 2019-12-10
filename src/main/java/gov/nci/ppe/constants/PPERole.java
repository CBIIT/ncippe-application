package gov.nci.ppe.constants;

public enum PPERole {
	ROLE_CRC("ROLE_PPE_CRC"), ROLE_PROVIDER("ROLE_PPE_PROVIDER"), ROLE_PARTICIPANT("ROLE_PPE_PARTICIPANT");

	private String roleName;

	PPERole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

}
