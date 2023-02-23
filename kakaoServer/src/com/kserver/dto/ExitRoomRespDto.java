package com.kserver.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExitRoomRespDto {
	private List<String> roomNames;
	private String username;

}
