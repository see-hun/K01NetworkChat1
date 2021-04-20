package chat4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class MultiServer {

	//멤버변수
	static ServerSocket serverSocket = null;
	static Socket socket= null;
	static PrintWriter out = null;
	static BufferedReader in = null;
	static String s = "";
	
	//생성자
	public MultiServer() {
		//실행부 없음
	}
	
	//서버의 초기화를 담당할 메소드로 main()메소드의 모든 내용이 포함됨.
	public static void init() {
		
		//클라이언트 이름능 버반랑 용도.
		String name = "";
		
		try {
			//클라이언트의 접속 대기
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			//접속 허가
			socket = serverSocket.accept();
			System.out.println(socket.getInetAddress()+":"+socket.getPort());
			
			//스트림 생성
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new
			InputStreamReader(socket.getInputStream()));
			
			//클라이언트의 최초 메세지인 대화명을 읽어서 Echo
			if(in != null) {
				name = in.readLine();
				System.out.println(name+"접속");
				out.println(">" + name +"님이 접속했습니다.");
			}
			
			//클라이언트가 전송하는 메세지를 지속적으로 읽어서 Echo한다.
			while(in != null) {
				s = in.readLine();
				if(s == null) {
					break;
				}
				System.out.println(name+"==>"+ s);
				//클라이언트에게 Echo 할떄 메소드를 호출한다.
				snedAllMsg(name, s);
			}
			System.out.println("Bye..!!!");
		}
		catch (Exception e) {
			System.out.println("예외:"+e);
		}
		finally {
			try {
				in.close();
				out.close();
				socket.close();
				serverSocket.close();
			}
			catch (Exception e) {
				System.out.println("예외2:"+e);
			}
		}
	}
	//서버가 클라이언트에게 메세지를 Echo해주는 메소드
	public static void snedAllMsg(String name, String msg) {
		try {
			out.println(">"+name+"==>"+ msg);
		}
		catch (Exception e) {
			System.out.println("예외:"+e);
		}
	}
	
	//main()메소드는 프로그램의 출발점의 역활만 담당한다.
	public static void main(String[] args) {

		init();
	}

}
