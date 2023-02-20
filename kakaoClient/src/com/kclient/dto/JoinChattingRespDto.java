package com.kclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class JoinChattingRespDto {
	private int chattingRoomNum;
	private String welcomeMessage;

}
