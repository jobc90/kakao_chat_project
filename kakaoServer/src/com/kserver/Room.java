package com.kserver;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Room {
    private String chattingRoomName;
    private String username;
    private List<ConnectedSocket> connectedSocket = new ArrayList<>();

    public Room(String chattingRoomName, String username, List<ConnectedSocket> connectedSocket) {
        this.chattingRoomName = chattingRoomName;
        this.username = username;
        this.connectedSocket = connectedSocket;
    }

}
