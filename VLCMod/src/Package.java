import java.io.Serializable;

public class Package implements Serializable{

	private static final long serialVersionUID = 1L;
	public String command;
	public String File;
	
	public Package(String command, String File){
		this.command = command;
		this.File = File;
	}

}
