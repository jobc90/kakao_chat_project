package com.kclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import com.kclient.ChattingClientK;
import com.kserver.ConnectedSocket;

import lombok.RequiredArgsConstructor;
import dto.AddChattingRoomReqDto;
import dto.AddChattingRoomRespDto;
import dto.JoinRespDto;
import dto.MessageRespDto;
import dto.ResponseDto;

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
						ChattingClientK.getInstance().get .append(joinRespDto.getWelcomeMessage() + "\n");
						ChattingClientK.getInstance().getUserListModel().clear();
						ChattingClientK.getInstance().getUserListModel().addElement("---전체---");
						ChattingClientK.getInstance().getUserListModel().addAll(joinRespDto.getConnectedUsers());
						ChattingClientK.getInstance().getUserList().setSelectedIndex(0);
						break;
					case "addChatting": 
						AddChattingRoomRespDto addChattingRoomRespDto = gson.fromJson(responseDto.getBody(), AddChattingRoomRespDto.class);
						ChattingClientK.getInstance().getChattingList().add(addChattingRoomRespDto.getChattingRooms());
						break;
					case "sendMessage":
						MessageRespDto messageRespDto = gson.fromJson(responseDto.getBody(), MessageRespDto.class);
						ChattingClientK.getInstance().getContentView().append(messageRespDto.getMessageValue() + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
