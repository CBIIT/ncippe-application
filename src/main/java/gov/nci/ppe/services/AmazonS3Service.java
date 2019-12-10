package gov.nci.ppe.services;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;

import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.impl.ApiException;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-11
 */
@Component
public interface AmazonS3Service {

	S3Object getObjectFromS3(String s3Key) throws ApiException;

	ListObjectsV2Result listObjectsOnS3(String prefix) throws ApiException;

	void putObjectOnS3(InputStream inputStream, String s3DestinationFolderKey, long contentLength, String contentType,
			CannedAccessControlList accessControl) throws ApiException;

	void putObjectOnS3(InputStream inputStream, String s3Bucket, String s3DestinationFolderKey, long contentLength,
			String contentType, CannedAccessControlList accessControl) throws ApiException;

	void deleteObjectsOnS3(List<KeyVersion> s3KeysToBeDeleted) throws ApiException;

	void copyObjectOnS3(String s3SourceKey, String s3DestinationKey, CannedAccessControlList accessControl)
			throws ApiException;

	File getFileFromS3(String s3Key, String originalFileName, String fileExtension) throws ApiException;

	URL getPresignedS3URL(String s3Key) throws ApiException;

	URL getResourceUrl(String bucketName, String key);

	void putObjectOnS3(InputStream inputStream, String s3DestinationFolderKey, long contentLength, String contentType,
			CannedAccessControlList accessControl, Participant patient, User admin, String originalFileName, String UploadedFileType)
			throws ApiException;

}