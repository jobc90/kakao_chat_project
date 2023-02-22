package com.kclient.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class AddChattingRoomRespDto {
	private List<String> roomNames;

}
