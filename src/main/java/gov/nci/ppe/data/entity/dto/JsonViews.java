package gov.nci.ppe.data.entity.dto;

/**
 * Contains List of JsonView interfaces used to control the serialization to Json of DTO objects
 *  
 * @author debsarka0
 *
 */
public class JsonViews {
	
	/**
	 * Use to tag Detailed JSON serialization of Provider entities
	 * 
	 * @author debsarka0
	 *
	 */
	public interface ProviderDetailView {
	}

	/**
	 * Use to tag Detailed JSON serialization of Participant entities
	 * 
	 * @author debsarka0
	 *
	 */
	public interface ParticipantDetailView {
	}

	/**
	 * Use to tag Detailed JSON serialization of CRC entities
	 * 
	 * @author debsarka0
	 *
	 */
	public interface CrcDetailView {
	}

	/**
	 * Use to serialize summary data for all users
	 * @author debsarka0
	 *
	 */
	public interface UsersSummaryView {
		
	}
}
