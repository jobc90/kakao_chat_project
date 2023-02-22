package com.kclient;

import java.awt.CardLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;
import com.kclient.dto.AddChattingRoomRespDto;
import com.kclient.dto.JoinChattingRespDto;
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
			CardLayout layout = ChattingClientK.getInstance().getMainCard();
			
			
			
			while(true) {
				
				String request = in.readLine();
				ResponseDto responseDto = gson.fromJson(request, ResponseDto.class);
				switch(responseDto.getResource()) {
					case "join":
						layout.show(ChattingClientK.getInstance().getMainPane(), "chattingList");
						JoinRespDto joinRespDto = gson.fromJson(responseDto.getBody(), JoinRespDto.class);
						ChattingClientK.getInstance().getChattingListModel().clear();
						ChattingClientK.getInstance().getChattingListModel().addElement("---<<<채팅방 목록>>>---");
						ChattingClientK.getInstance().getChattingListModel().addAll(joinRespDto.getRoomNames());
						
						break;
					case "addChatting": 
						AddChattingRoomRespDto addChattingRoomRespDto = gson.fromJson(responseDto.getBody(), AddChattingRoomRespDto.class);
						ChattingClientK.getInstance().getChattingListModel().clear();
						ChattingClientK.getInstance().getChattingListModel().addElement("---<<<채팅방 목록>>>---");
						ChattingClientK.getInstance().getChattingListModel().addAll(addChattingRoomRespDto.getRoomNames());
						break;
						
					case "joinChatting": 
						JoinChattingRespDto joinChattingRespDto = gson.fromJson(responseDto.getBody(), JoinChattingRespDto.class);
						layout.show(ChattingClientK.getInstance().getMainPane(), "chattingRoom");
						ChattingClientK.getInstance().getRoomLabel().setText("채팅방 :" + joinChattingRespDto.getRoomName() +", 방장: " + joinChattingRespDto.getRoomKing());
						ChattingClientK.getInstance().getChattingView().setText(null);
						ChattingClientK.getInstance().getChattingView().append(joinChattingRespDto.getWelcomeMessage() + "\n");

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
