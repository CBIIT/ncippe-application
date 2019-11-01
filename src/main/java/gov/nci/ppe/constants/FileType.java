package gov.nci.ppe.constants;

/**
 * Enum for allowed file types in the portal
 * @author PublicisSapient
 * @version 1.0 
 * @since   2019-09-13
 */
public enum FileType{
	PPE_FILETYPE_ECONSENT_FORM("PPE_FILETYPE_ECONSENT_FORM"), PPE_FILETYPE_BIOMARKER_REPORT("PPE_FILETYPE_BIOMARKER_REPORT");

	private String fileType;

	FileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileType() {
		return fileType;
	}
}
