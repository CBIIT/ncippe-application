package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;
import java.util.List;

import gov.nci.ppe.constants.PPEUserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Record of previous request to send Group Notification")
public class GroupMessageHistoryRecordDto {
    @ApiModelProperty(value = "List of roles the message was sent to")
    private List<PPEUserType> audiences;

    @ApiModelProperty(value = "Subject of Message")
    private Subject subject;

    @ApiModelProperty(value = "Body of the Message")
    private Message message;

    @ApiModelProperty(value = "Sender details")
    private MessageSenderDto sender;

    @ApiModelProperty(value = "Request sent on")
    private Timestamp dateSent;
}
