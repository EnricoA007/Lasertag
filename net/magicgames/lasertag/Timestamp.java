package net.magicgames.lasertag;

public class Timestamp {

	private int seconds,minutes;
	
	public Timestamp(int minutes, int seconds) {
		this.seconds=seconds;this.minutes=minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public boolean isFinished() {
		return seconds == 0 && minutes == 0;
	}
	
	public void calculate() {
		
		if(seconds == 0) {
			minutes--;
			seconds = 59;
		}else {
			seconds--;
		}
		
	}
	
	@Override
	public String toString() {
		int m = getMinutes();
		int s = getSeconds();
		String rm = "" +m;
		String rs = "" +s;
		
		if(10 > m) {
			rm = "0" + m;
		}
		
		if(10 > s) {
			rs = "0"+ s;
		}
		
		return rm + ":" + rs;
		
	}
	
}
