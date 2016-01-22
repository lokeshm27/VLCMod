import java.io.Serializable;

public class Settings implements Serializable{
	
	private static final long serialVersionUID = 1L;
	boolean Start, Resume, Ask;
	boolean High, Med, Low;
	boolean Contin;
	int resume, performance;
	int secs;
	
	public Settings(int resume, int performance, boolean contin){
		this.Contin = contin;
		if((resume > 3 || resume < 1) || (performance > 3 || performance < 1)){
			this.resume = 2;
			this.performance = 2;
		}else{
			this.resume = resume;
			this.performance = performance ;
		}
		
		if(this.resume == 1){
			Start = true;
			Resume = false;
			Ask = false;		
		}else if(this.resume == 2){
			Start = false;
			Resume = true;
			Ask = false;
		}else{
			Start = false;
			Resume = false;
			Ask = true;
		}
		
		if(this.performance == 1){
			Low = true;
			Med = false;
			High = false;
		}else if(this.performance == 2){
			Low = false;
			Med = true;
			High = false;
		}else{
			Low = false;
			Med = false;
			High = true;
		}
		
		secs = this.performance * 2000;
	}
}
