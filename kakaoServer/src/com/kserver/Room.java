package com.kserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Room {
    private String chattingRoomName;
    private String roomKing;
    private List<ConnectedSocket> joinSocketList = new ArrayList<>();

    public Room(String chattingRoomName, String roomKing, ConnectedSocket joinSocket) {
        this.chattingRoomName = chattingRoomName;
        this.roomKing = roomKing;
    }

}
