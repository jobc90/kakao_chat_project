package com.kserver.dto;

import java.util.List;

import com.kserver.Room;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class AddChattingRoomRespDto {
	private List<Room> chattingRooms;

}
