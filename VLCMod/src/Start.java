import java.awt.Desktop;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class Start {
	static File runFlag = new File("C:\\ProgramData\\VLCMod\\Cache\\Running.dat");
	static File timeFlag = new File("C:\\ProgramData\\VLCMod\\Cache\\startTime.dat");
	static Operations op = new Operations();
	static Controls c = new Controls();
	static CountDownLatch latch = new CountDownLatch(1);
	static int PORT = Operations.PORT;

	public static void main(String args[]) {
		if (!(args.length == 2)) {
			try {
				File setup = new File("C:\\ProgramData\\VLCMod\\Settings.exe");
				Desktop.getDesktop().open(setup);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (runFlag.exists()) {
				clientMode(args[0], args[1]);
			} else {
				serverMode(args[0], args[1]);
			}
		}
	}

	public static void clientMode(String cmd, String Path) {
		Date date = new Date();
		String flag1;
		if (cmd.equals("play")) {
			if (op.isUnderBorder(date)) {
				flag1 = "enqueue";
			} else {
				flag1 = "add";
			}
		} else {
			flag1 = cmd;
		}
		Package pk = new Package(flag1, Path);
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			@SuppressWarnings("resource")
			Socket socket = new Socket(localhost, PORT);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(pk);
			out.flush();
		} catch (Exception e) {
			op.errorHandler(135, e.getMessage(), "clientMode", "Start", true);
		}
		System.exit(0);
	}

	static Thread serverThread = new Thread(new Runnable() {
		@Override
		public void run() {
			try{
				InetAddress localhost = InetAddress.getLocalHost();
				@SuppressWarnings("resource")
				ServerSocket server = new ServerSocket(PORT, 0, localhost);
				while(true){
					Socket socket = server.accept();
					new IPC(socket).start();
				}
			}catch (Exception e){
				op.errorHandler(105, e.getMessage(), "ServerThread", "Start", true);
			}
		}
	});
	
	public static void serverMode(String cmd, String Path) {
		try {
			op.setrunFlag();
			serverThread.setDaemon(true);
			serverThread.start();
			String VLCPath = op.getVLCPath();
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(VLCPath));
			pb.command("CMD", "/c", "vlc.exe --extraintf rc --rc-host=localhost:8081");
			Process p = pb.start();
			Thread waitThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						p.waitFor();
						op.onClose();
					} catch (InterruptedException e) {
						op.errorHandler(127, e.getMessage(), "serverMode", "Start", false);
					}
				}
			});

			waitThread.setDaemon(true);
			waitThread.start();
			op.process(new Package(cmd, Path));
			latch.await();
		} catch (Exception e) {
			op.errorHandler(109, e.getMessage(), "serverMode", "Start", true);
		}
	}

}
