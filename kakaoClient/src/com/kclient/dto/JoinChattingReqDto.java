package com.kclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JoinChattingReqDto {
	private int chattingRoomNum;
	private String fromUser;

}