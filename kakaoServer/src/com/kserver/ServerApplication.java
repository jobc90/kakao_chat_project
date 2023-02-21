package com.kserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.kserver.dto.AddChattingRoomReqDto;
import com.kserver.dto.AddChattingRoomRespDto;
import com.kserver.dto.JoinChattingReqDto;
import com.kserver.dto.JoinChattingRespDto;
import com.kserver.dto.JoinReqDto;
import com.kserver.dto.JoinRespDto;
import com.kserver.dto.MessageReqDto;
import com.kserver.dto.MessageRespDto;
import com.kserver.dto.RequestDto;
import com.kserver.dto.ResponseDto;

import lombok.Data;



@Data
class ConnectedSocket extends Thread {
    private static List<ConnectedSocket> socketList = new ArrayList<>();
    private static List<Room> chattingRooms = new ArrayList<>();
    private List<ConnectedSocket> connectedSocket = new ArrayList<>();
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Gson gson;

	private String username;
	private String chattingRoomName;

	private int chattingRoomNum;
	
	
	public ConnectedSocket(Socket socket) {
		this.socket = socket;
		gson = new Gson();
		socketList.add(this);
	}

	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			
			
			
			while(true) {
				String request = in.readLine();	// requestDto(JSON)
				RequestDto requestDto = gson.fromJson(request, RequestDto.class);
				
				
				switch(requestDto.getResource()) {
					case "join": 
						
						JoinReqDto joinReqDto = gson.fromJson(requestDto.getBody(), JoinReqDto.class);
						username = joinReqDto.getUsername();
						List<String> connectedUsers = new ArrayList<>();
						
						for(ConnectedSocket connectedSocket : socketList) {
							connectedUsers.add(connectedSocket.getUsername());
						}
						
						JoinRespDto joinRespDto = new JoinRespDto(username + "님이 접속하였습니다.", connectedUsers);
			
						sendToLogin(requestDto.getResource(), "ok", gson.toJson(joinRespDto));
						break;
						
					case "addChatting":
						AddChattingRoomReqDto addChattingRoomReqDto = gson.fromJson(requestDto.getBody(), AddChattingRoomReqDto.class);
						chattingRoomName = addChattingRoomReqDto.getChattingRoomName();
//						connectedSocket.add(this);
						System.out.println(this);
						
						Room room = new Room(chattingRoomName, username, Arrays.asList(this));
						chattingRooms.add(room);
                        
                        AddChattingRoomRespDto addchChattingRoomRespDto = new AddChattingRoomRespDto(chattingRooms);
                        System.out.println(chattingRooms);
                        sendToAll(room.getChattingRoomName(), requestDto.getResource(), "ok", gson.toJson(addchChattingRoomRespDto));
                        
						break;
						
					case "joinChatting": 
						JoinChattingReqDto joinChattingReqDto = gson.fromJson(requestDto.getBody(), JoinChattingReqDto.class);
						chattingRoomNum = joinChattingReqDto.getChattingRoomNum();
						
						JoinChattingRespDto joinChattingRespDto = new JoinChattingRespDto(chattingRoomNum, "채팅방에 입장하셨습니다.");
						sendToUser(requestDto.getResource(), "ok", gson.toJson(joinChattingRespDto), joinChattingReqDto.getFromUser());
						
						break;
						
					case "sendMessage":
						MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(), MessageReqDto.class);
						
						
						if(messageReqDto.getToUser().equalsIgnoreCase("all")) {
							String message = messageReqDto.getFromUser() + "[전체]: " + messageReqDto.getMessageValue();
							MessageRespDto messageRespDto = new MessageRespDto(message);
							sendToUser(requestDto.getResource(), "ok", gson.toJson(messageRespDto), messageReqDto.getToUser());
						}
						break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendToLogin(String resource, String status, String body) throws IOException {
		ResponseDto responseDto = new ResponseDto(resource, status, body);
		for(ConnectedSocket connectedSocket : socketList) {
			OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			
			out.println(gson.toJson(responseDto));
		}
	}
	
	private void sendToAll(String chattingRoomName, String resource, String status, String body) throws IOException {
	    ResponseDto responseDto = new ResponseDto(resource, status, body);	
	    for (Room room : chattingRooms) {
	        if (room.getChattingRoomName().equals(chattingRoomName)) {
	            for (ConnectedSocket socket : room.getConnectedSocket()) {
	                OutputStream outputStream = socket.getSocket().getOutputStream();
	                PrintWriter out = new PrintWriter(outputStream, true);
	                out.println(gson.toJson(responseDto));
	            }
	        }
	    }
	}

//	private void sendToAll(String resource, String status, String body) throws IOException {
//	    ResponseDto responseDto = new ResponseDto(resource, status, body);
//	    for (Room room : chattingRooms) {
//	        if (room.getChattingRoomName().equals(chattingRoomName)) {
//	            OutputStream outputStream = ((ConnectedSocket) room.getConnectedSocket()).getSocket().getOutputStream();
//	            PrintWriter out = new PrintWriter(outputStream, true);
//	            out.println(gson.toJson(responseDto));
//	        }
//	    }
//	}

	private void sendToUser(String resource, String status, String body, String toUser) throws IOException {
	    ResponseDto responseDto = new ResponseDto(resource, status, body);
	    for (Room room : chattingRooms) {
	        if (room.getChattingRoomName().equals(chattingRoomName) && (room.getUsername().equals(toUser) || room.getUsername().equals(username))) {
	            OutputStream outputStream = ((ConnectedSocket) room.getConnectedSocket()).getSocket().getOutputStream();
	            PrintWriter out = new PrintWriter(outputStream, true);
	            out.println(gson.toJson(responseDto));
	        }
	    }
	}

}

public class ServerApplication {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(9090);
			System.out.println("=====<<< 서버 실행 >>>=====");

			while (true) {
				Socket socket = serverSocket.accept(); // 클라이언트의 접속을 기다리는 녀석
				ConnectedSocket connectedSocket = new ConnectedSocket(socket);
				connectedSocket.start();
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.println("=====<<< 서버 종료 >>>=====");
		}

	}

}
