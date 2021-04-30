package gov.nci.ppe.data.entity.dto;

import java.util.UUID;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Sender of the Message")
public class MessageSenderDto {

    @ApiModelProperty(value = "Sender UUID")
    private UUID senderUUID;

    @ApiModelProperty(value = "First name of sender")
    private String firstName;

    @ApiModelProperty(value = "Last name of sender")
    private String lastName;

    @ApiModelProperty(value = "Email of sender")
    private String email;
}
