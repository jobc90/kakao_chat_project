package com.kclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MessageRespDto {
	private Object roomName;
	private String messageValue;

}
