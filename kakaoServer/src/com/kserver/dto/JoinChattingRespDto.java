package com.kserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class JoinChattingRespDto {
	private Object roomName;
	private String roomKing;
	private String welcomeMessage;

}
