package com.kclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;

import com.kclient.ChattingClient;
import lombok.RequiredArgsConstructor;
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
						ChattingClient.getInstance().getContentView().append(joinRespDto.getWelcomeMessage() + "\n");
						ChattingClient.getInstance().getUserListModel().clear();
						ChattingClient.getInstance().getUserListModel().addElement("---전체---");
						ChattingClient.getInstance().getUserListModel().addAll(joinRespDto.getConnectedUsers());
						ChattingClient.getInstance().getUserList().setSelectedIndex(0);
						break;
					case "sendMessage":
						MessageRespDto messageRespDto = gson.fromJson(responseDto.getBody(), MessageRespDto.class);
						ChattingClient.getInstance().getContentView().append(messageRespDto.getMessageValue() + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
