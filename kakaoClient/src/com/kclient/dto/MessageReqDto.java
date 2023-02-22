package com.kclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MessageReqDto {
	private String username;
	private String messageValue;

}
