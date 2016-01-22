import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class Controls {
	private static OutputStream out;
	private static BufferedReader in;
	static String resp;
	static int respint;
	String temp1 = "status change: ( ";
	String temp2 = "status change: ( time";
	String temp3 = "status change: ( new input:";
	static boolean found;
	static Operations op = new Operations();

	public void add(String File) {
		try {
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			out = socket.getOutputStream();
			out.write(("add file:///" + File + "\n").getBytes());
			socket.close();
		}catch (ConnectException e2){
			op.errorHandler(201, e2.getMessage(), "add", "Control", false);
		}catch(Exception e) {
			op.errorHandler(110, e.getMessage(), "add", "Controls", true);
		}
	}

	public void enqueue(String File) {
		try {
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			out = socket.getOutputStream();
			out.write(("enqueue file:///" + File + "\n").getBytes());
			socket.close();
		}catch (ConnectException e2){
			op.errorHandler(202, e2.getMessage(), "enqueue", "Control", false);
		} catch (Exception e) {
			op.errorHandler(111, e.getMessage(), "enqueue", "Controls", true);
		}
	}

	public boolean isPlaying() { 
		try {
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			Thread readerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						found = false;
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String temp = in.readLine().toString();
						while (temp.contains(temp1) || temp.contains(temp2) || temp.contains(temp3)) {
							temp = in.readLine().toString();
						}
						respint = Integer.parseInt(temp);
						found = true;
					}catch (ConnectException e2){
						op.errorHandler(203, e2.getMessage(), "isPlaying", "Control", false);
					} catch (Exception e) {
						op.errorHandler(112, e.getMessage(), "isPlaying", "Controls", true);
					}
				}
			});

			readerThread.setDaemon(true);
			readerThread.start();
			out = socket.getOutputStream();
			out.write(("is_playing\n").getBytes());
			while (!found) {
				Thread.sleep(50);
			}
			found = false;
			socket.close();
			boolean ret;
			if (respint == 0) {
				ret = false;
			} else {
				ret = true;
			}
			return ret;
		}catch (ConnectException e2){
			op.errorHandler(204, e2.getMessage(), "isPlaying", "Control", false);
		} catch (Exception e) {
			op.errorHandler(113, e.getMessage(), "isPlaying", "Controls", true);
		}

		return null != null;
	}

	public String getTitle() {
		try {
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			Thread readerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						found = false;
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String temp = in.readLine().toString();
						while (temp.contains(temp1) || temp.contains(temp2) || temp.contains(temp3)) {
							temp = in.readLine().toString();
						}
						resp = temp;
						found = true;
					}catch (ConnectException e2){
						op.errorHandler(205, e2.getMessage(), "getTitle", "Control", false);
					} catch (Exception e) {
						op.errorHandler(117, e.getMessage(), "getTitle", "Controls", true);
					}
				}
			});
			readerThread.setDaemon(true);
			readerThread.start();
			out = socket.getOutputStream();
			out.write(("get_title\n").getBytes());
			while (!found) {
				Thread.sleep(50);
			}
			found = false;
			socket.close();
			return resp;
		}catch (ConnectException e2){
			op.errorHandler(206, e2.getMessage(), "getTitle", "Control", false);
		} catch (Exception e) {
			op.errorHandler(116, e.getMessage(), "getTitle", "Controls", true);
		}
		return null;
	}

	public int getTime() {
		try {
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			Thread readerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						found = false;
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String temp = in.readLine().toString();
						while (temp.contains(temp1) || temp.contains(temp2) || temp.contains(temp3)) {
							temp = in.readLine().toString();
						}
						respint = Integer.parseInt(temp);
						found = true;
					}catch (ConnectException e2){
						op.errorHandler(207, e2.getMessage(), "getTime", "Control", false);
					} catch (Exception e) {
						op.errorHandler(119, e.getMessage(), "getTime", "Controls", true);
					}
				}
			});
			
			readerThread.setDaemon(true);
			readerThread.start();
			out = socket.getOutputStream();
			out.write(("get_time\n").getBytes());
			while(!found){
				Thread.sleep(50);
			}
			found = false;
			socket.close();
			return respint;
			
		}catch (ConnectException e2){
			op.errorHandler(208, e2.getMessage(), "getTime", "Control", false);
		} catch (Exception e) {
			op.errorHandler(118, e.getMessage(), "getTime", "Controls", true);
		}
		return 0;
	}

	public void seek(int Time) {
		try {
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			out = socket.getOutputStream();
			out.write(("seek " + Time + "\n").getBytes());
			socket.close();
		}catch (ConnectException e2){
			op.errorHandler(209, e2.getMessage(), "seek", "Control", false);
		} catch (Exception e) {
			op.errorHandler(114, e.getMessage(), "seek", "Operations", true);
		}
	}

	public void pause() {
		try {
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			out = socket.getOutputStream();
			out.write(("pause\n").getBytes());
			socket.close();
		}catch (ConnectException e2){
			op.errorHandler(210, e2.getMessage(), "pause", "Control", false);
		} catch (Exception e) {
			op.errorHandler(115, e.getMessage(), "pause", "Operations", true);
		}
	}

	public int getLength(){
		try{
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			Thread readerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						found = false;
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String temp = in.readLine().toString();
						while(temp.contains(temp1) || temp.contains(temp2) || temp.contains(temp3)){
							temp = in.readLine().toString();
						}
						respint = Integer.parseInt(temp);
						found = true;
					}catch(ConnectException e1){
						op.errorHandler(211, e1.getMessage(), "getLength", "Control", false);
					}catch(Exception e3){
						op.errorHandler(121, e3.getMessage(), "getLenght", "Controls", true);
					}
				}
			});
			
			readerThread.setDaemon(true);
			readerThread.start();
			out = socket.getOutputStream();
			out.write(("get_length\n").getBytes());
			while(!found){
				Thread.sleep(50);
			}
			found = false;
			socket.close();
			return respint;
		}catch(ConnectException e2){
			op.errorHandler(212, e2.getMessage(), "getLength", "Control", false);
		}catch(Exception e){
			op.errorHandler(120, e.getMessage(), "getLength", "Controls", true);
		}
		return 0;
	}

	public void quit(){
		try{
			InetAddress localhost = InetAddress.getByName("localhost");
			Socket socket = new Socket(localhost, 8081);
			out = socket.getOutputStream();
			out.write(("quit\n").getBytes());
			if(!socket.isClosed()){
				out.write(("help\n").getBytes());
			}
			socket.close();
		}catch(ConnectException e2){
			op.errorHandler(213, e2.getMessage(), "quit", "Controls", false);
		}catch (Exception e){
			op.errorHandler(131, e.getMessage(), "quit", "Controls", true);
		}
	}

}