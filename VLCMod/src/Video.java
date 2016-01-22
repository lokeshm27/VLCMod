import java.io.Serializable;

public class Video implements Serializable{

	private static final long serialVersionUID = 1L;
	public String Path;
	public int Time;
	
	public Video(String Path, int Time){
		this.Path = Path;
		this.Time = Time;
	}

}
