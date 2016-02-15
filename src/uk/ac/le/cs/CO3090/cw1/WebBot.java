package uk.ac.le.cs.CO3090.cw1;

public class WebBot implements Runnable, StatisticalAnalysisInterface {
	
	
	 public static int MAX_PAGES_NUM=50; 
	 public static int TIME_OUT=10000;
	 public static int MAX_QUEUE_SIZE=20000;
	 public static int MAX_THREAD_NUM=10;
	 public static int MAX_CHAR_COUNT=1000000;
	 public static  String ALPHABET="abcdefghijklmnopqrstuvwxyz";
	 
	 
	 int current_character_count=0;
	 static int total_character_count=0;
	 	 
	 
	 
	@Override
	public void run() {
		// TODO complete this method
	
		
	}

	@Override
	public void count(String URL) throws InterruptedException{
		// TODO complete this method
		
	}

	

	@Override
	public void showTotalStatistics() {
		// TODO complete this method
	
	}
	
	
	
	public static void main(String[] args){
		// TODO complete this method
	}
	

}
