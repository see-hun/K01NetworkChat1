package chat1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
 
public class MultiServer {

	public static void main(String[] args) {

		ServerSocket serverSocket = null;
		Socket socket= null;
		PrintWriter out = null;
		BufferedReader in = null;
		String s = "";
		
		try {
			/*
			9999번으로 포트번호를 설정하여 서버객체를 생성하고
			클라이언트의 접속을 기다린다.
			 */
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			//...접속대기중...
			
			/*
			클라이언트 접속요청을 하면accept()메소드를 통해
			접속을 허가한다.
			 */
			socket = serverSocket.accept();
			
			/*
			getInetAddress() / getPort()
				: 원격지 즉 클라이언트의 IP주소와 포트 번호를 얻어온다.
			getLocalAddress() / getLocalPort()
				: 연결이 시작된 네크워크 즉 서버측의 정보를 얻어온다.
			 */
			System.out.println(socket.getInetAddress()+"(클라이언트)의"
					+socket.getPort()+"포트를 통해"
					+socket.getLocalAddress()+"(서버)의"
					+socket.getLocalPort()+"포트로 연결되었습니다.");
			//System.out.println(socket.getInetAddress()+":"+socket.getPort());
			
			//서버-> 클라이언트 측으로 메세지를 전송하기 위한 출력스트림.
			out = new PrintWriter(socket.getOutputStream(), true);
			//클라이언트로 부터 메세지를 받기위한 입력스트림
			in = new BufferedReader(new
			InputStreamReader(socket.getInputStream()));
			
			//클라이언트가 보낸 메세지를 라인단위로 읽어온다.
			s = in.readLine();
			//읽어온 내용을 콘솔에 출력한다.
			System.out.println("Client에서 읽어옴:"+s);
			//클라이언트로 응답메세지(Echo)를 보내준다.
			out.println("Server에서 응답:" + s);
			//콘솔애 종료메세지를 출력한다.
			System.out.println("Bye...!!!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				//입출력 스트림 종료(자원해제)
				in.close();
				out.close();
				//소켓 종료(자원해제)
				socket.close();
				serverSocket.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
