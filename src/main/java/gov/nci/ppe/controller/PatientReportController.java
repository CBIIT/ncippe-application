package gov.nci.ppe.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.HttpResponseConstants;
import gov.nci.ppe.constants.PatientReportConstants;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.entity.dto.FileDTO;
import gov.nci.ppe.data.entity.dto.JsonViews;
import gov.nci.ppe.services.AmazonS3Service;
import gov.nci.ppe.services.AuthorizationService;
import gov.nci.ppe.services.FileService;
import gov.nci.ppe.services.UserService;
import gov.nci.ppe.services.impl.ApiException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class for Participant's Report related actions.
 * 
 * @author PublicisSapient
 * @version 2.0
 * @since 2019-08-13
 */

@RestController
public class PatientReportController {

	private static final Logger logger = LogManager.getLogger(PatientReportController.class);

	@Autowired
	private AmazonS3Service amazonS3Service;

	@Autowired
	private UserService userService;

	@Autowired
	private FileService reportService;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	@Qualifier("dozerBean")
	private Mapper dozerBeanMapper;

	@Value("${patient.report.bucket.name}")
	private String applicationDataBucket;

	/**
	 * Uploads a File and associates it with the participant
	 * 
	 * @param file             - Patient file to Upload
	 * @param patientId        - Unique ID of the Patient whose file is to be
	 *                         uploaded
	 * @param uploadedFileType - Uploaded File type
	 * @return error response in case of missing parameters an internal exception or
	 *         success response if file has been stored successfully
	 */
	@PostMapping(value = "/api/patientReport")
	@ApiOperation(value = "Uploads a File andd associates it with the participant")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Patient Report uploaded"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 400, message = "Uploaded File is empty") })
	public ResponseEntity<String> handleFileUpload(
			@ApiParam(value = "Patient file to Upload", required = true) @RequestParam(value = "reportFile", required = true) MultipartFile file,
			@ApiParam(value = "Patient ID of the Patient whose file is to be uploaded", required = true) @RequestParam(value = "patientId", required = true) String patientId,
			@ApiParam(value = "Uploaded File type", required = true, allowableValues = "PPE_FILETYPE_BIOMARKER_REPORT, PPE_FILETYPE_ECONSENT_FORM") @RequestParam(value = "uploadedFileType", required = true) String uploadedFileType,
			HttpServletRequest req, Locale locale) {

		String requestingUserUUID = req.getHeader(CommonConstants.HEADER_UUID);
		logger.info("File Upload request for patient id=" + patientId + " for file type " + uploadedFileType
				+ " filename =" + file.getOriginalFilename() + " by " + requestingUserUUID);

		if (!authorizationService.authorizeFileUpload(requestingUserUUID, patientId, uploadedFileType)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Authorized");
		}
		if (!file.isEmpty()) {
			// This will form the part of the key using which the application will retrieve
			// file.
			StringBuilder folderWithFileName = new StringBuilder(patientId);

			/* Only take the filename and not the extension */
			folderWithFileName.append("/")
					.append(file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".")));
			try (InputStream inputStream = file.getInputStream();) {
				/* Get patient details */
				Optional<User> patientOptional = userService.findActiveParticipantByPatientId(patientId);
				Participant patient = (Participant) patientOptional.get();

				/* Get Admin details */
				Optional<User> adminOptional = userService.findByUuid(req.getHeader(CommonConstants.HEADER_UUID));
				User adminUser = adminOptional.get();

				amazonS3Service.putObjectOnS3(inputStream, folderWithFileName.toString(), file.getSize(),
						file.getContentType(), CannedAccessControlList.BucketOwnerFullControl, patient, adminUser,
						file.getOriginalFilename(), uploadedFileType);
				return ResponseEntity.status(HttpStatus.OK)
						.body(messageSource.getMessage(HttpResponseConstants.FILE_UPLOAD_SUCCESS,
								new Object[] { file.getOriginalFilename(), patientId }, locale));

			} catch (Exception exception) {
				logger.error(exception.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(messageSource.getMessage(HttpResponseConstants.FILE_UPLOAD_INTERNAL_ERROR, null, locale));
			}
		} else {
			logger.error(" The file upload process failed as the file you are uploading is empty.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageSource.getMessage(
					HttpResponseConstants.UPLOADED_FILE_EMPTY, new Object[] { file.getOriginalFilename() }, locale));
		}
	}

	/**
	 * Returns a stream object to caller containing file requested for
	 * 
	 * @param - reportGUID - Unique ID of the report to be streamed
	 * 
	 * @return error response in case of missing parameters an internal exception or
	 *         success response if file has been stored successfully
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Returns a stream object to caller containing file requested for")
	@ApiResponses({ @ApiResponse(code = 200, message = "Report downloaded"),
			@ApiResponse(code = 400, message = "File GUID Missing"),
			@ApiResponse(code = 404, message = "Requested File not found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@GetMapping(value = "/api/patientReport/{reportGUID}")
	public @ResponseBody ResponseEntity fetchParticipantReportAsFile(HttpServletRequest req,
			@ApiParam(value = "Unique ID of the report to be streamed", required = true) @PathVariable String reportGUID,
			Locale locale) {

		if (StringUtils.isAllBlank(reportGUID)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(messageSource.getMessage(HttpResponseConstants.MISSING_FILE_GUID, null, locale));
		}
		String requestingUserUUID = req.getHeader(CommonConstants.HEADER_UUID);

		Optional<FileMetadata> rptOptional = reportService.getFileByFileGUID(reportGUID);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", MediaType.APPLICATION_PDF_VALUE);
		InputStreamResource resource = null;
		if (rptOptional.isPresent()) {
			FileMetadata fileMetadata = rptOptional.get();

			if (!authorizationService.authorizeFileDownload(requestingUserUUID,
					fileMetadata.getParticipant().getPatientId(), fileMetadata.getFileType().getCodeName())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Authorized");
			}

			File participantReport = new File("Error.pdf");
			try {
				participantReport = amazonS3Service.getFileFromS3(fileMetadata.getSearchKey(),
						StringUtils.remove(fileMetadata.getFileName(), PatientReportConstants.FILE_EXTENSION_PDF),
						PatientReportConstants.FILE_EXTENSION_PDF);
				logger.debug("file is saved locally to " + participantReport.getPath());
				httpHeaders.set("Content-Disposition", "attachment; filename=" + fileMetadata.getFileName());
				resource = new InputStreamResource(new FileInputStream(participantReport));
			} catch (ApiException apiEx) {
				logger.error("Error Message : " + apiEx.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
						messageSource.getMessage(HttpResponseConstants.FILE_DOWNLOAD_INTERNAL_ERROR, null, locale));
			} catch (FileNotFoundException fileNotFndEx) {
				logger.error("FILENOTFOUND Error Message : " + fileNotFndEx.getMessage());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(
						HttpResponseConstants.FILE_DOWNLOAD_NOT_FOUND, new Object[] { reportGUID }, locale));
			}
		}

		return new ResponseEntity(resource, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Marks the report as having been viewed by the specified user
	 * 
	 * @param reportGUID - Unique ID of the report to be marked as read
	 * @param uuid       - Unique ID of the user having read the report
	 * @param req
	 * @return
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "Marks the report as having been viewed by the specified user")
	@PostMapping(value = "/api/patientReport/{reportGUID}/markAsRead")
	public @ResponseBody ResponseEntity<String> markReportAsViewed(
			@ApiParam(value = "Unique ID of the report to be marked as read", required = true) @PathVariable String reportGUID,
			HttpServletRequest req, Locale locale) throws JsonProcessingException {

		Optional<FileMetadata> rptOptional = reportService.getFileByFileGUID(reportGUID);
		if (rptOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource
					.getMessage(HttpResponseConstants.FILE_DOWNLOAD_NOT_FOUND, new Object[] { reportGUID }, locale));
		}
		String requestingUserUUID = req.getHeader(CommonConstants.HEADER_UUID);
		Optional<User> usrOptional = userService.findByUuid(requestingUserUUID);

		if (usrOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
		}

		FileMetadata updatedRpt = reportService.markReportAsViewed(rptOptional.get(), usrOptional.get());
		FileDTO updatedFileDTO = dozerBeanMapper.map(updatedRpt, FileDTO.class);
		ObjectMapper mapper = new ObjectMapper();

		return ResponseEntity
				.ok(mapper.writerWithView(JsonViews.UsersSummaryView.class).writeValueAsString(updatedFileDTO));

	}

}
