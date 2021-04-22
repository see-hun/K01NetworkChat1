package chat5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

//서버가 보내는 Echo메세지를 읽어오는 쓰레드 클래스
public class Receiver extends Thread {
	
	Socket socket;
	BufferedReader in = null;
	
	//클라이언트가 접속할때 사용한 Socket객체를 기반으로 입력스트립 생성
	public Receiver(Socket socket) {
		this.socket = socket;
		
		try {
			in = new BufferedReader(new
			InputStreamReader(this.socket.getInputStream()));
		}
		catch (Exception e) {
			System.out.println("예외>Receiver>생성자:"+ e);
		}
	}
	
	/*
	run()메소드에서 는 서버가 보내는 Echo메세지를 
	지속적으로 읽어오고, 예외발생시 while루프를 탈출한다.
	 */
	@Override
	public void run() {
		
		while(in != null) {
			try {
				System.out.println("Thread Receive : "+in.readLine());
			}
			catch (SocketException ne) {
				System.out.println("SocketException");
				break;
			}
			catch (Exception e) {
				System.out.println("예외>Receiver>run1:"+e);
			} 
		}
		
		try {
			in.close();
		}
		catch (Exception e) {
			System.out.println("예외>Receiver>run2:"+e);
		}
	}

}
