package com.kclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;
import com.kclient.dto.AddChattingRoomRespDto;
import com.kclient.dto.JoinRespDto;
import com.kclient.dto.MessageRespDto;
import com.kclient.dto.ResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientRecive extends Thread {

	private final Socket socket;
	private InputStream inputStream;
	private Gson gson;
	
	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			gson = new Gson();
			
			while(true) {
				String request = in.readLine();
				ResponseDto responseDto = gson.fromJson(request, ResponseDto.class);
				switch(responseDto.getResource()) {
					case "join":
						JoinRespDto joinRespDto = gson.fromJson(responseDto.getBody(), JoinRespDto.class);
						ChattingClientK.getInstance().getUserListModel().clear();
						ChattingClientK.getInstance().getUserListModel().addElement("---전체---");
						ChattingClientK.getInstance().getUserListModel().addAll(joinRespDto.getConnectedUsers());
						break;
					case "addChatting": 
						
						AddChattingRoomRespDto addChattingRoomRespDto = gson.fromJson(responseDto.getBody(), AddChattingRoomRespDto.class);
						ChattingClientK.getInstance().getChattingListModel().clear();
						ChattingClientK.getInstance().getChattingListModel().addElement("---<<<채팅방 목록>>>---");
						ChattingClientK.getInstance().getChattingListModel().addAll(addChattingRoomRespDto.getChattingRooms());
						break;
					case "sendMessage":
						MessageRespDto messageRespDto = gson.fromJson(responseDto.getBody(), MessageRespDto.class);
						ChattingClientK.getInstance().getChattingView().append(messageRespDto.getMessageValue() + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
