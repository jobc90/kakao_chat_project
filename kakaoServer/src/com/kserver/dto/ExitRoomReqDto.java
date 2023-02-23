package com.kserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExitRoomReqDto {
	Object roomName;
	String username;

}
