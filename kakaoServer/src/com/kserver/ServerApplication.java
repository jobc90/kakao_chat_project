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
import com.kserver.dto.ExitRoomReqDto;
import com.kserver.dto.ExitRoomRespDto;
import com.kserver.dto.JoinChattingReqDto;
import com.kserver.dto.JoinChattingRespDto;
import com.kserver.dto.JoinReqDto;
import com.kserver.dto.JoinRespDto;
import com.kserver.dto.MessageReqDto;
import com.kserver.dto.MessageRespDto;
import com.kserver.dto.RequestDto;
import com.kserver.dto.ResponseDto;

import lombok.Data;
import lombok.Getter;


//class SendRoomDto {
//    private String chattingRoomName;
//    private String username;
//    private List<String> userList = new ArrayList<>();
//
//    public SendRoomDto(Room room) {
//        this.chattingRoomName = chattingRoomName;
//        this.username = username;
//        for (ConnectedSocket sockList : room.getConnectedSocket()) {
//        	userList.add(sockList.getUsername());
//        }
//        
//    }
//}


@Data
class ConnectedSocket extends Thread {
    private static List<ConnectedSocket> socketList = new ArrayList<>();
    private static List<Room> roomList = new ArrayList<>();
    private static List<String> roomNames = new ArrayList<>();
    private static List<ConnectedSocket> joinSocketList = new ArrayList<>();
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Gson gson;

    @Getter
	private String username;
	private String chattingRoomName;
	private Object roomName;
	private String roomKing;

	
	
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
						JoinRespDto joinRespDto = new JoinRespDto(roomNames);
			
						sendToLogin(requestDto.getResource(), "ok", gson.toJson(joinRespDto));
						break;
						
					case "addChatting":
						AddChattingRoomReqDto addChattingRoomReqDto = gson.fromJson(requestDto.getBody(), AddChattingRoomReqDto.class);
						chattingRoomName = addChattingRoomReqDto.getChattingRoomName();
						roomKing = username;
						Room room = new Room(chattingRoomName, roomKing, this);
						roomList.add(room);
						roomNames.add(room.getChattingRoomName());
						
                        AddChattingRoomRespDto addChattingRoomRespDto = new AddChattingRoomRespDto(roomNames);
                        addRoomList(requestDto.getResource(), "ok", gson.toJson(addChattingRoomRespDto));
                        
						break;
						
					case "joinChatting": 
						JoinChattingReqDto joinChattingReqDto = gson.fromJson(requestDto.getBody(), JoinChattingReqDto.class);
						roomName = joinChattingReqDto.getRoomName();

						for(Room room1 : roomList) {
							if (room1.getChattingRoomName().equals(roomName)) {
								roomKing = room1.getRoomKing();
								break;
							}	
						}
						JoinChattingRespDto joinChattingRespDto = new JoinChattingRespDto(roomName, roomKing, "채팅방에 입장하셨습니다.");
						joinChattingRoom(requestDto.getResource(), "ok", gson.toJson(joinChattingRespDto), roomKing);

						
						break;
						
					case "exitRoom": 
						ExitRoomReqDto exitRoomReqDto = gson.fromJson(requestDto.getBody(), ExitRoomReqDto.class);

						
						
						ExitRoomRespDto exitRoomRespDto = new ExitRoomRespDto(roomNames, exitRoomReqDto.getUsername());
						exitRoom(requestDto.getResource(), "ok", gson.toJson(exitRoomRespDto));
						
						break;
						
					case "sendMessage":
						MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(), MessageReqDto.class);			
						String message = messageReqDto.getUsername() + ": " + messageReqDto.getMessageValue();

						MessageRespDto messageRespDto = new MessageRespDto(roomName, message);
						sendMessage(requestDto.getResource(), "ok", gson.toJson(messageRespDto));
						
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
	


	private void addRoomList(String resource, String status, String body) throws IOException {
		ResponseDto responseDto = new ResponseDto(resource, status, body);
		for(ConnectedSocket connectedSocket : socketList) {
			OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			
			out.println(gson.toJson(responseDto));
		}
	}
	private void joinChattingRoom(String resource, String status, String body, String username) throws IOException {
	    ResponseDto responseDto = new ResponseDto(resource, status, body);
	    for (Room room : roomList) {
	        if (room.getChattingRoomName().equals(roomName)) {
				room.getJoinSocketList().add(this);
				OutputStream outputStream = socket.getOutputStream();
	    		PrintWriter out = new PrintWriter(outputStream, true);
	    			
	    		out.println(gson.toJson(responseDto));
	    		
	        	
	        }
	    }
	}
//	private void exitRoom(String resource, String status, String body) throws IOException {
//		ResponseDto responseDto = new ResponseDto(resource, status, body);
//		for (Room room4 : roomList) {
//	        if (room4.getChattingRoomName().equals(roomName)) {
//	        	System.out.println(room4.getChattingRoomName());
//	        	System.out.println(roomName);
//	        		for (ConnectedSocket connectedSocket : room4.getJoinSocketList()) {
//	        			OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
//		        		PrintWriter out = new PrintWriter(outputStream, true);
//		        		out.println(gson.toJson(responseDto));
//	        		}
//	        	
//	        	
//	        }
//		}
//		
//	}
	
	private void exitRoom(String resource, String status, String body) throws IOException {
		ResponseDto responseDto = new ResponseDto(resource, status, body);
		for (Room room4 : roomList) {
	        if (room4.getChattingRoomName().equals(roomName)) {
	        	if(room4.getRoomKing() != username) {
	        		OutputStream outputStream = socket.getOutputStream();
	        		PrintWriter out = new PrintWriter(outputStream, true);
	        		out.println(gson.toJson(responseDto));
	        		} else {
	        			roomList.remove(room4);
	        			roomNames.remove(room4.getChattingRoomName());
	        			for (ConnectedSocket connectedSocket : room4.getJoinSocketList()) {
	        				OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
	        				PrintWriter out = new PrintWriter(outputStream, true);
	        				out.println(gson.toJson(responseDto));
	        				}break;
	        			}
	        	} 
	        }
		}
	
		
	
	
	private void sendMessage(String resource, String status, String body) throws IOException {
	    ResponseDto responseDto = new ResponseDto(resource, status, body);
	    for (Room room2 : roomList) {
	    	if (room2.getChattingRoomName().equals(roomName)) {
	    		for (ConnectedSocket connectedSocket : room2.getJoinSocketList()) {
	    			OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
	    			PrintWriter out = new PrintWriter(outputStream, true);
	    			out.println(gson.toJson(responseDto));
	    		}
	    		break;
	    	}
	    }

	}
	
//	private void sendToUser(String resource, String status, String body, String toUser) throws IOException {
//		ResponseDto responseDto = new ResponseDto(resource, status, body);		
//		for(ConnectedSocket connectedSocket : socketList) {
//			if(connectedSocket.getUsername().equals(toUser) || connectedSocket.getUsername().equals(username)) {
//				OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
//				PrintWriter out = new PrintWriter(outputStream, true);
//				
//				out.println(gson.toJson(responseDto));
//			}
//		}
//	}


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
