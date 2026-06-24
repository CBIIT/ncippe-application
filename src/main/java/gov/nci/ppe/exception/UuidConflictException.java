package gov.nci.ppe.exception;

public class UuidConflictException extends RuntimeException {
    public UuidConflictException(String uuid) {
        super("UUID already in use: " + uuid);
    }
}
