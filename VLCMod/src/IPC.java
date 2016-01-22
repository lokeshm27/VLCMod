import java.io.ObjectInputStream;
import java.net.Socket;

public class IPC extends Thread{
	Socket socket;
	Operations op = new Operations();
	Controls c = new Controls();
	
	public IPC(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run(){
		try{
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Package p = (Package) in.readObject();
			op.process(p);
		}catch(Exception e){
			op.errorHandler(134, e.getMessage(), "run", "IPC", true);
		}
	}
}
