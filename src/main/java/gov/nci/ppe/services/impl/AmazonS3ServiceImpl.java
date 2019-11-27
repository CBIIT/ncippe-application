package gov.nci.ppe.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.internal.SdkBufferedInputStream;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import gov.nci.ppe.configurations.EmailServiceConfig;
import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.AmazonS3Service;
import gov.nci.ppe.services.CodeService;
import gov.nci.ppe.services.EmailLogService;
import gov.nci.ppe.services.FileService;
import gov.nci.ppe.services.NotificationService;

/**
 * This is the service implementation class that deals with all the S3 related
 * tasks.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-15
 */
@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {

	private static final Logger logger = LogManager.getLogger(AmazonS3ServiceImpl.class);

	private AmazonS3 amazonS3Client;

	@Autowired
	private EmailLogService emailLogService;

	@Autowired
	private EmailServiceConfig emailServiceConfig;

	@Autowired
	private FileService reportService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private CodeService codeService;

	@Autowired
	private NotificationServiceConfig notificationServiceConfig;

	@Value("${patient.report.bucket.name}")
	private String applicationDataBucket;

	public AmazonS3ServiceImpl() {
		amazonS3Client = AmazonS3ClientBuilder.defaultClient();
	}

	@Override
	public S3Object getObjectFromS3(String s3Key) throws ApiException {
		logger.trace("Entering getObjectFromS3  with Key: {}", s3Key);
		GetObjectRequest getObjectRequest = new GetObjectRequest(applicationDataBucket, s3Key);
		try {
			logger.trace("Exiting getObjectFromS3  with Key: {}", s3Key);
			return amazonS3Client.getObject(getObjectRequest);
		} catch (SdkClientException sdkClientException) {
			logger.error("Exception occured while getting object from s3 with key: {}", s3Key, sdkClientException);
			throw new ApiException("Exception occured while getting object from s3 with key: " + s3Key);
		}
	}

	@Override
	public ListObjectsV2Result listObjectsOnS3(String prefix) throws ApiException {
		logger.trace("Entering listObjectsOnS3  with prefix: {}", prefix);
		try {
			ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(applicationDataBucket)
					.withPrefix(prefix);
			logger.trace("Exiting listObjectsOnS3 with prefix: {}", prefix);
			return amazonS3Client.listObjectsV2(listObjectsV2Request);
		} catch (SdkClientException sdkClientException) {
			logger.error("Exception occured while listObjectsOnS3 with prefix: {}", prefix, sdkClientException);
			throw new ApiException("Exception occured while listObjectsOnS3 with prefix: " + prefix);
		}

	}

	@Override
	public void putObjectOnS3(InputStream inputStream, String s3DestinationFolderKey, long contentLength,
			String contentType, CannedAccessControlList accessControl) throws ApiException {
		putObjectOnS3(applicationDataBucket, inputStream, s3DestinationFolderKey, contentLength, contentType,
				accessControl);
	}

	@Override
	public void putObjectOnS3(InputStream inputStream, String s3Bucket, String s3DestinationFolderKey,
			long contentLength, String contentType, CannedAccessControlList accessControl) throws ApiException {
		putObjectOnS3(s3Bucket, inputStream, s3DestinationFolderKey, contentLength, contentType, accessControl);
	}

	/**
	 * This method will put objects on s3 which includes data files, images,
	 * application binaries, training portal analytics
	 *
	 * @param bucketName             holds the bucket name on which file should be
	 *                               saved.
	 * @param inputStream            stream holding the object which will be put on
	 *                               s3
	 * @param s3DestinationFolderKey destination path on s3 where this object will
	 *                               be saved
	 * @param contentLength          length of the object which will be saved to s3
	 *                               * @param contentType type of the object which
	 *                               will be saved to s3 *
	 * @param accessControl          holds the access control on s3 for the
	 *                               destination file
	 */
	private void putObjectOnS3(String bucketName, InputStream inputStream, String s3DestinationFolderKey,
			long contentLength, String contentType, CannedAccessControlList accessControl) throws ApiException {
		logger.trace("Entering putObjectOnS3 for application with bucket: {} and  destinationKey: {}", bucketName,
				s3DestinationFolderKey);
		SdkBufferedInputStream sdkBufferedInputStream = null;
		try {
			sdkBufferedInputStream = new SdkBufferedInputStream(inputStream);
			ObjectMetadata metadata = new ObjectMetadata();
			if (StringUtils.isNotBlank(contentType)) {
				metadata.setContentType(contentType);
			}
			metadata.setContentLength(contentLength);
			PutObjectRequest putObject = new PutObjectRequest(bucketName, s3DestinationFolderKey,
					sdkBufferedInputStream, metadata).withCannedAcl(accessControl);
			amazonS3Client.putObject(putObject);
		} catch (SdkClientException sdkClientException) {
			logger.error("Exception occured while calling putObjectOnS3 for destination: {}", s3DestinationFolderKey,
					sdkClientException);
			throw new ApiException(
					"Exception occured while calling putObjectOnS3 for destination: " + s3DestinationFolderKey);
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(sdkBufferedInputStream);
		}
		logger.trace("Exiting putObjectOnS3 for application with bucket: {} and  destinationKey: {}", bucketName,
				s3DestinationFolderKey);
	}

	@Override
	public void deleteObjectsOnS3(List<KeyVersion> s3KeysToBeDeleted) throws ApiException {
		logger.trace("Entering deleteObjectsOnS3  with keys: {}", s3KeysToBeDeleted);
		try {
			DeleteObjectsRequest deleteObjectRequest = new DeleteObjectsRequest(applicationDataBucket);
			deleteObjectRequest.setKeys(s3KeysToBeDeleted);
			amazonS3Client.deleteObjects(deleteObjectRequest);
		} catch (SdkClientException sdkClientException) {
			logger.error("Exception occured while delete object on s3 with key: {}", s3KeysToBeDeleted,
					sdkClientException);
			throw new ApiException("Exception occured while delete object on s3 with key: " + s3KeysToBeDeleted);
		}
		logger.trace("Exiting deleteObjectsOnS3  with keys: {}", s3KeysToBeDeleted);

	}

	@Override
	public void copyObjectOnS3(String s3SourceKey, String s3DestinationKey, CannedAccessControlList accessControl)
			throws ApiException {
		logger.trace("Entering copyObjectOnS3  with s3SourceKey: {} and s3DestinationKey: {} ", s3SourceKey,
				s3DestinationKey);
		try {
			CopyObjectRequest copyObjRequest = new CopyObjectRequest(applicationDataBucket, s3SourceKey,
					applicationDataBucket, s3DestinationKey).withCannedAccessControlList(accessControl);
			amazonS3Client.copyObject(copyObjRequest);
		} catch (SdkClientException sdkClientException) {
			boolean throwException = true;
			if (sdkClientException instanceof AmazonServiceException) {
				String errorCode = ((AmazonServiceException) sdkClientException).getErrorCode();
				if (StringUtils.equals("NoSuchKey", errorCode)) {
					throwException = false;
				}
			}
			if (throwException) {
				logger.error(
						"Exception occured while copying object on s3 with s3SourceKey: {} and s3DestinationKey: {}",
						s3SourceKey, s3DestinationKey, sdkClientException);
				throw new ApiException("Exception occured while copying object on s3 with s3SourceKey:: " + s3SourceKey
						+ " and s3DestinationKey: " + s3DestinationKey);
			}
		}
		logger.trace("Exiting copyObjectOnS3  with s3SourceKey: {} and s3DestinationKey: {} ", s3SourceKey,
				s3DestinationKey);

	}

	/**
	 * Method to fetch the file from S3 based on the key s3Key - search key
	 * originalFileName - name of the file fileExtension - file extension
	 */
	@Override
	public File getFileFromS3(String s3Key, String originalFileName, String fileExtension) throws ApiException {
		logger.trace("Entering getFileFromS3() with  s3Key: {} originalFileName: {} fileExtenson: {}", s3Key,
				originalFileName, fileExtension);
		InputStream fileContent = null;
		File targetFile = null;
		try {
			S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(applicationDataBucket, s3Key));
			fileContent = s3Object.getObjectContent();
			if (fileContent == null) {
				logger.error("Unable to get the file from s3 for s3Key: {}", s3Key);
				throw new ApiException("Unable to get the file from s3 for s3Key: " + s3Key);
			}
			logger.debug("After getting file content from s3 with s3Key: {} originalFileName: {} fileExtenson: {}",
					s3Key, originalFileName, fileExtension);
			if (StringUtils.isEmpty(originalFileName)) {
				originalFileName = s3Key;
			}
			targetFile = File.createTempFile(originalFileName, fileExtension);
			Files.copy(fileContent, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			logger.trace("Exiting getFileFromS3() with  s3Key: {} originalFileName: {} fileExtenson: {}", s3Key,
					originalFileName, fileExtension);
			return targetFile;
		} catch (SdkClientException | IOException exception) {
			logger.error("Exception occured while getFileFromS3 with  s3Key: {} originalFileName: {} fileExtenson: {}",
					s3Key, originalFileName, fileExtension, exception);
			throw new ApiException("Exception occured while getFileFromS3 with  s3Key: " + s3Key + " originalFileName:"
					+ originalFileName + " fileExtenson: " + fileExtension);
		} finally {
			IOUtils.closeQuietly(fileContent);
		}
	}

	@Override
	public URL getPresignedS3URL(String s3Key) throws ApiException {
		logger.trace("Entering getPresignedS3URL() with  s3Key: {} ", s3Key);
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(applicationDataBucket,
				s3Key).withMethod(HttpMethod.GET).withExpiration(expiration);
		logger.trace("Exiting getPresignedS3URL() with  s3Key: {} ", s3Key);
		return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
	}

	@Override
	public URL getResourceUrl(String bucketName, String key) {
		return amazonS3Client.getUrl(bucketName, key);
	}

	@Override
	public void putObjectOnS3(InputStream inputStream, String s3DestinationFolderKey, long contentLength,
			String contentType, CannedAccessControlList accessControl, Participant patient, User admin,
			String originalFileName, String uploadedFileType) throws ApiException {
		putObjectOnS3(applicationDataBucket, inputStream, s3DestinationFolderKey, contentLength, contentType,
				accessControl);

		// If the file been uploaded is a test report and the user has opted for email
		// notification, then send a confirmation email to the Admin/User uploading the
		// report.
		if (FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType().equalsIgnoreCase(uploadedFileType)
				&& admin.getAllowEmailNotification()) {
			String emailStatus = sendEmailToAdminAfterFileUpload(patient, admin.getEmail());
			logger.info("Action of sending email was " + emailStatus);
		}

		String fileSource = "Mocha";
		sendEmailAfterFileUpload(patient, uploadedFileType);

		URL newUrl = getResourceUrl(applicationDataBucket, s3DestinationFolderKey);
		try {
			String publicUrlForFileOnS3 = java.net.URLDecoder.decode(newUrl.toString(), StandardCharsets.UTF_8.name());
			Code _code = getCodeforFileType(uploadedFileType);
			if (FileType.PPE_FILETYPE_ECONSENT_FORM.getFileType().equalsIgnoreCase(uploadedFileType)) {
				fileSource = "CRC";
			}
			logFileMetadata(publicUrlForFileOnS3, s3DestinationFolderKey, originalFileName, fileSource,
					admin.getUserId(), patient, _code);
			insertNotification(patient, uploadedFileType);
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException : {}", e.getMessage());
		}
	}

	/**
	 * 
	 * @param url         - public endpoint using which the report can be directly
	 *                    accessed
	 * @param description - a brief description about the report
	 * @param reportName  - name of the report
	 * @param Source      - name of the entity that provided the report
	 * @param uploadedBy  - user who uploaded the report
	 * @param patient     - userid of the patient
	 */
	private void logFileMetadata(String url, String searchKey, String reportName, String Source, Long uploadedBy,
			Participant patient, Code _code) {
		reportService.logFileMetadata(url, searchKey, reportName, Source, uploadedBy, patient, _code);
	}

	/**
	 * Method to make an entry into the PortalNotification table.
	 * 
	 * @param patient - Participant object
	 */
	private void insertNotification(Participant patient, String UploadedFileType) {

		Map<Long, String> userDetailMap = getUserIdAndFirstName(patient, UploadedFileType);

		if (StringUtils.isNotBlank(UploadedFileType)
				&& FileType.PPE_FILETYPE_ECONSENT_FORM.getFileType().equalsIgnoreCase(UploadedFileType)) {
			userDetailMap.forEach((userId, userName) -> {
				notificationService.addNotification(notificationServiceConfig.getUploadEConsentFormNotificationFrom(),
						notificationServiceConfig.getUploadEConsentFormNotificationSubject(),
						notificationServiceConfig.getUploadEConsentFormNotificationMessage(), userId, userName,
						patient.getFullName(), patient.getPatientId());
			});
		} else {
			userDetailMap.forEach((userId, userName) -> {
				notificationService.addNotification(
						notificationServiceConfig.getUploadTestReportNotificationMessageFrom(),
						notificationServiceConfig.getUploadTestReportNotificationMessageSubject(),
						notificationServiceConfig.getUploadTestReportNotificationMessage(), userId, userName,
						patient.getFullName(), patient.getPatientId());
			});
		}
	}

	/**
	 * Helper method to send out emails after file upload
	 * 
	 * @param emailIds        - list of emailIds to mail to
	 * @param patientFullName - Full Name of the Patient (FirstName LastName)
	 * @param actionFor       - File type Uploaded
	 * @param patientId
	 */
	private void sendEmailAfterFileUpload(Participant patient, String actionFor) {
		if (StringUtils.isNotBlank(actionFor)
				&& FileType.PPE_FILETYPE_ECONSENT_FORM.getFileType().equalsIgnoreCase(actionFor)
				&& patient.getAllowEmailNotification()) {
			// Send email to Patient only
			emailLogService.sendEmailToPatientAfterUploadingEconsent(patient.getEmail(), patient.getFirstName());

		} else {
			Map<String, String> emailIds = getEmailIds(patient);
			emailIds.forEach((name, emailId) -> {
				emailLogService.sendEmailAfterUploadingReport(name, emailId, patient.getFullName(),
						emailServiceConfig.getSenderEmailAddress(), emailServiceConfig.getEmailUploadFileSubject(),
						emailServiceConfig.getEmailUploadFileHTMLBody(),
						emailServiceConfig.getEmailUploadFileTextBody(), patient.getPatientId());
			});
		}
	}

	/**
	 * Retrieve the first name and email Ids for a participant and their related
	 * provider and CRC Add the email ids to the collection only if the user has
	 * opted for email notification.
	 * 
	 * @param patient          - Participant object
	 * @param uploadedFileType - For the type of report for which the email is being
	 *                         sent.
	 */
	private Map<String, String> getEmailIds(Participant patient) {
		Map<String, String> emailIds = new HashMap<String, String>();

		Set<Provider> providers = patient.getProviders();
		providers.forEach(provider -> {
			if (provider.getAllowEmailNotification()) {
				emailIds.put(provider.getFirstName(), provider.getEmail()); // email for providers
			}
		});
		CRC crcforPatient = patient.getCRC();
		if (crcforPatient.getAllowEmailNotification()) {
			emailIds.put(crcforPatient.getFirstName(), crcforPatient.getEmail()); // email for CRC
		}
		return emailIds;
	}

	/**
	 * Retrieve the userid and first name for a participant and their related
	 * provider and CRC
	 * 
	 * @param patient          - Participant object
	 * @param uploadedFileType - For the type of report for which the email is being
	 *                         sent.
	 * @return
	 */
	private Map<Long, String> getUserIdAndFirstName(Participant patient, String uploadedFileType) {
		Map<Long, String> userDetailMap = new HashMap<Long, String>();
		userDetailMap.put(patient.getUserId(), patient.getFirstName());
		/* eConsent details are only sent to Patients */
		if (StringUtils.isNotBlank(uploadedFileType)
				&& FileType.PPE_FILETYPE_ECONSENT_FORM.getFileType().equalsIgnoreCase(uploadedFileType)) {
			return userDetailMap;
		}
		Set<Provider> providers = patient.getProviders();
		providers.forEach(provider -> {
			userDetailMap.put(provider.getUserId(), provider.getFirstName());
		});

		CRC crc = patient.getCRC();
		if (crc != null) {
			userDetailMap.put(crc.getCrcId(), crc.getFirstName());
		}

		return userDetailMap;
	}

	/**
	 * This method returns the code id for different file types during uploads
	 * 
	 * @param fileTypeCodeName - This value corresponds to the type of file being
	 *                         uploaded
	 * @return
	 */
	public Code getCodeforFileType(String fileTypeCodeName) {
		return codeService.getCode(fileTypeCodeName);
	}

	/**
	 * Method to send out email to patients, their providers and CRC when a
	 * biomarker report is uploaded by the admin
	 * 
	 * @param patient - An object of Participant
	 * @param admin   - An object of User
	 * @return
	 */
	private String sendEmailToAdminAfterFileUpload(Participant participant, String emailId) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		dtf.format(now);
		String[] dateTime = StringUtils.split(dtf.format(now), "-");

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { participant.getFirstName(), participant.getLastName(),
				participant.getPatientId(), dateTime[0], dateTime[1] };
		String replaceThisString[] = { "%{FirstName}", "%{LastName}", "%{UserGUID}", "%{date}", "%{time}" };
		String updatedTextBody = StringUtils.replaceEach(emailServiceConfig.getEmailTextBodyForAdmin(),
				replaceThisString, replaceStringWith);

		/* Replace the variables in the Subject Line */
		String replaceSubjectStringWith[] = { participant.getPatientId() };
		String replaceThisStringForSubject[] = { "%{UserGUID}" };
		String subject = StringUtils.replaceEach(emailServiceConfig.getEmailSubjectForAdmin(),
				replaceThisStringForSubject, replaceSubjectStringWith);

		return emailLogService.sendEmailNotification(emailId, emailServiceConfig.getSenderEmailAddress(), subject,
				updatedTextBody, updatedTextBody);
	}

}
