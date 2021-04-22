package chat6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiServer {

	//멤버변수
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	//클라이언트 정보저장을위한 map 컬랙션 생성
	Map<String, PrintWriter> clientMap;
	
	//생성자
	public MultiServer() {
		//클라이언트의 이름과 출력스트림을 저장할HashMap 컬렉션 생성
		clientMap = new HashMap<String, PrintWriter>();
		//HashMap 동기화설정, 쓰레드가 사용자정보에 동시에 접근하는것을 차단함
		Collections.synchronizedMap(clientMap);
	}
	
	//채팅 서버 초기화
	public void init() {
		try {
			//서버 소캣 오픈
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다");
			
			/*
			1명의 클라이언트가 접속할때마다 접속을 허용 (Accept)해주고
			동시에 MultiServerT 쓰레드를 생성한다.
			해당 쓰레드는 1명의 클라이언트가 전송하는 메세지를 읽어서Echo
			해주는 역할을 담당한다.
			 */
			while(true) {
				socket = serverSocket.accept();
				
				System.out.println(socket.getInetAddress()+"(클라이언트)의"
						+socket.getPort()+"포트를 통해"
						+socket.getLocalAddress()+"(서버)의"
						+socket.getLocalPort()+"포트로 연결되었습니다.");
				
				Thread mst = new MultiServerT(socket);
				mst.start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				serverSocket.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
		chat4까지는 init()이 static이었으나, chat5부터는 일반적인
		멤버메소드를 변경한다. 따라서 객체를 생성후호출하는 방식으로
		변경된다
	 */
	public static void main(String[] args) {

			MultiServer ms = new MultiServer();
			ms.init();
	}
	//접속된 모든 클라이언트 측으로 서버의 메세지를 Echo해주는 역할 담당
	public void sendAllMsg(String name, String msg) {
		
		//Map에 저장된 객체의 키값(대화명)을 먼저 얻어온다.
		Iterator<String> it = clientMap.keySet().iterator();
		
		//저장된 객체(클라이언트)의 캣수만큼 반복한다.
		while(it.hasNext()) {
			try {
				//각 클라이언트의 PrintWriter객체를 얻어온다.
				PrintWriter it_out = (PrintWriter)
				clientMap.get(it.next());
				
				/*
				클라이언트에게 메세지를 전달할떄 매개변수로 name이
				있는 경우와 없는경우를 구분해서 전달하게된다.
				 */
				if(name.equals("")) {
					//입장, 퇴장에서 사용되는 부분
					it_out.println(msg);
				}
				else {
					//메세지를 보낼때 사용되는 부분
					it_out.println("["+name+"]:"+msg);
				}
			}
			catch (Exception e) {
				System.out.println("예외:"+e);
			}
		}
	}
	/*
	내부클래스
		:init()에 기술 되었던 스트림을 생성후 메세지를 읽기/쓰기 하던
		부분이 해당 내부클래스로 이동되었다.
	 */
	class MultiServerT extends Thread{
		
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;
		/*
		내부클래스의 생성자
			:1명의 클라이언트가 접속할때 생성햇던 Socket객체를
			매개변수로 받아 이를 기반으로 입출력 스트림을 생성한다.
		 */
		public MultiServerT(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new
				InputStreamReader(socket.getInputStream()));
			}
			catch (Exception e) {
				System.out.println("예외:"+e);
			}
		}
		@Override
		/*
		쓰레드로 동작할 run()에서는 클라이언트의 접속자명과
		메세지를 지속적으로 읽어 Echo해주는 역할을 한다.
		 */
		public void run() {
			
			String name = "";
			String s = "";
			
			try {
				
				if(in != null) {
					
				//클라이언트의 이름을 읽어온다.
				name = in.readLine();
				
				//방금 접속한 클라이언트를 제외한 나머지에게 접속을 알린다.
				sendAllMsg("",name+"님이 입장하셧습니다");
				//현재 접속한 클라이언트를 HashMap에 저장한다.
				clientMap.put(name,out);
				
				//접속자의 이름을 서버의 콘솔에 띄워주고
				System.out.println(name+"접속");
				//HashMap 에 저장된 객체의 수로 현재 접속자를 파악할수 있따.
				System.out.println("현재 접속자 수는"+clientMap.size()+"명입니다");
				
				}
				//입력한 메세지는 모든 클라이언트에게 Echo된다
				while(in != null) {
					s = in.readLine();
					if(s== null) 
						break;
					//서버에 콘솔에 출력되고...
					System.out.println(name + ">>" + s);
					//클라이언트 측으로 전송한다.
					sendAllMsg(name, s);
				}
			}
			catch (Exception e) {
				System.out.println("예외:"+e);
			}
			finally {
				
				/*
				클라이언트가 접속을 종료하면 Socket예외가 발생하게 되어
				finally절로 진입하게 된다. 이때 "대화명"을 통해 정보를
				삭제한다.
				 */
				clientMap.remove(name);
				sendAllMsg("",name+"님이 퇴장하셧습니다");
				System.out.println(name+"["+ Thread.currentThread().getName()+"]퇴장");
				System.out.println("현재 접속자 수는"+clientMap.size()+"명입니다");
				
				try {
					in.close();
					out.close();
					socket.close();
				}
			
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


