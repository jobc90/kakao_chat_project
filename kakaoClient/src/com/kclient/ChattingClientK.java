package com.kclient;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import dto.JoinReqDto;
import dto.RequestDto;


public class ChattingClientK extends JFrame {
	private static ChattingClientK instance;
	
	public static ChattingClientK getInstance() {
		if(instance == null) {
			instance = new ChattingClientK();
		}
		return instance;
	}
	
	private Socket socket;
	private Gson gson;
	private String username;

	private JPanel mainPane;
	private DefaultListModel<String> userListModel;
	private JTextField nameInput;
	private JLabel kakaoIcon;
	private JPanel loginPane;
	private JPanel chattingListPane;
	private JPanel chattingRoomPane;
	private JLabel kakaoIcon2;
	private JTextField messageInput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChattingClientK frame = ChattingClientK.getInstance();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private ChattingClientK() {
		gson = new Gson();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		mainPane = new JPanel();
		mainPane.setBackground(new Color(255, 235, 59));
		mainPane.setForeground(new Color(0, 0, 0));
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(mainPane);
		mainPane.setLayout(new CardLayout(0, 0));
		
		loginPane = new JPanel();
		loginPane.setBackground(new Color(255, 235, 59));
		mainPane.add(loginPane, "name_7843377080600");
		loginPane.setLayout(null);
		
		chattingListPane = new JPanel();
		chattingListPane.setBackground(new Color(255, 235, 59));
		mainPane.add(chattingListPane, "name_7846480972100");
		chattingListPane.setLayout(null);
		
		kakaoIcon2 = new JLabel("");
		kakaoIcon2.setBackground(new Color(255, 235, 59));
		kakaoIcon2.setIcon(new ImageIcon("C:\\junil\\BC\\workspace\\-AWS-_Java_study_202212_jobc\\카톡아이콘4.png"));
		kakaoIcon2.setBounds(12, 10, 96, 96);
		chattingListPane.add(kakaoIcon2);
		
		JButton addChattingButton = new JButton("");
		addChattingButton.setIcon(new ImageIcon("C:\\junil\\BC\\workspace\\-AWS-_Java_study_202212_jobc\\채팅방추가아이콘.png"));
		addChattingButton.setBackground(new Color(240, 240, 240));
		addChattingButton.setBounds(12, 116, 96, 96);
		chattingListPane.add(addChattingButton);
		
		JScrollPane chattingListScroll = new JScrollPane();
		chattingListScroll.setBounds(120, 0, 334, 751);
		chattingListPane.add(chattingListScroll);
		
		JList chattingList = new JList();
		chattingListScroll.setViewportView(chattingList);
		
		chattingRoomPane = new JPanel();
		chattingRoomPane.setBackground(new Color(255, 235, 59));
		mainPane.add(chattingRoomPane, "name_8130709658300");
		chattingRoomPane.setLayout(null);
		
		JLabel chattingRoomName = new JLabel("채팅방 :");
		chattingRoomName.setFont(new Font("D2Coding", Font.BOLD, 20));
		chattingRoomName.setHorizontalAlignment(SwingConstants.LEFT);
		chattingRoomName.setIcon(new ImageIcon("C:\\junil\\BC\\workspace\\-AWS-_Java_study_202212_jobc\\카톡아이콘4.png"));
		chattingRoomName.setBackground(new Color(255, 235, 59));
		chattingRoomName.setBounds(0, 0, 394, 88);
		chattingRoomPane.add(chattingRoomName);
		
		JButton exitButton = new JButton("New button");
		exitButton.setBackground(new Color(255, 235, 59));
		exitButton.setIcon(new ImageIcon("C:\\junil\\BC\\workspace\\-AWS-_Java_study_202212_jobc\\나가기아이콘3.png"));
		exitButton.setBounds(406, 25, 36, 42);
		chattingRoomPane.add(exitButton);
		
		JScrollPane chattingScroll = new JScrollPane();
		chattingScroll.setBounds(12, 98, 430, 545);
		chattingRoomPane.add(chattingScroll);
		
		JTextArea chattingView = new JTextArea();
		chattingScroll.setViewportView(chattingView);
		
		JScrollPane messageScroll = new JScrollPane();
		messageScroll.setBounds(12, 653, 367, 88);
		chattingRoomPane.add(messageScroll);
		
		messageInput = new JTextField();
		messageScroll.setViewportView(messageInput);
		messageInput.setColumns(10);
		
		JButton sendButton = new JButton("");
		sendButton.setBackground(new Color(255, 235, 59));
		sendButton.setIcon(new ImageIcon("C:\\junil\\BC\\workspace\\-AWS-_Java_study_202212_jobc\\전송아이콘7.png"));
		sendButton.setBounds(381, 653, 73, 88);
		chattingRoomPane.add(sendButton);
		
		nameInput = new JTextField();
		nameInput.setBounds(81, 501, 285, 40);
		nameInput.setFont(new Font("D2Coding", Font.BOLD, 20));
		nameInput.setHorizontalAlignment(SwingConstants.CENTER);
		loginPane.add(nameInput);
		nameInput.setColumns(10);
		
		JButton connectButton = new JButton("");
		connectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String ip = null;
				int port = 0;
				
				ip = "127.0.0.1";
				port = 9090;
				
				try {
					socket = new Socket(ip, port);
					
					username = nameInput.getText();
					
					JOptionPane.showMessageDialog(null, 
							username + "님 환영합니다.", 
							"접속성공", 
							JOptionPane.INFORMATION_MESSAGE);
					
					connectButton.setEnabled(false);
					connectButton.removeMouseListener(this);
					
					ClientRecive clientRecive = new ClientRecive(socket);
					clientRecive.start();
					
					
					JoinReqDto joinReqDto = new JoinReqDto(username);
					String joinReqDtoJson = gson.toJson(joinReqDto);
					RequestDto requestDto = new RequestDto("join", joinReqDtoJson);
					String requestDtoJson = gson.toJson(requestDto);
					
					OutputStream outputStream = socket.getOutputStream();
					PrintWriter out = new PrintWriter(outputStream, true);
					out.println(requestDtoJson);
					
					
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
					
				} catch (ConnectException e1) {
					JOptionPane.showMessageDialog(null, 
							"서버 접속 실패", 
							"접속실패", 
							JOptionPane.ERROR_MESSAGE);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		connectButton.setBounds(81, 551, 285, 40);
		ImageIcon icon = new ImageIcon("C:\\junil\\BC\\workspace\\-AWS-_Java_study_202212_jobc\\카톡로그인.png");
		Image scaledImage = icon.getImage().getScaledInstance(285, 40, Image.SCALE_DEFAULT);
		ImageIcon scaledIcon = new ImageIcon(scaledImage);
		connectButton.setIcon(scaledIcon);
		connectButton.setSelectedIcon(null);
		connectButton.setBackground(new Color(255, 235, 59));
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			
			}
		});
		loginPane.add(connectButton);
		
		kakaoIcon = new JLabel("");
		kakaoIcon.setBounds(166, 208, 112, 98);
		kakaoIcon.setBackground(new Color(255, 235, 59));
		kakaoIcon.setHorizontalAlignment(SwingConstants.CENTER);
		kakaoIcon.setIcon(new ImageIcon("C:\\junil\\BC\\workspace\\-AWS-_Java_study_202212_jobc\\카톡아이콘.png"));
		loginPane.add(kakaoIcon);
		
		JLabel lblNewLabel = new JLabel("사용자 이름을 입력하세요.");
		lblNewLabel.setForeground(Color.LIGHT_GRAY);
		lblNewLabel.setFont(new Font("D2Coding", Font.ITALIC, 10));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBackground(new Color(255, 235, 59));
		lblNewLabel.setBounds(81, 480, 138, 19);
		loginPane.add(lblNewLabel);
		

		
		userListModel = new DefaultListModel<>();
	}
	
	private void sendRequest(String resource, String body) {
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			
			RequestDto requestDto = new RequestDto(resource, body);
			
			out.println(gson.toJson(requestDto));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
