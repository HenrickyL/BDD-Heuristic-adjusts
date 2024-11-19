package pacote;

class TimeManager {
	private long startTime;
	private long last = 0;
	private static long maxTime = 1800000;//30min - 1800000
	
	
	void resetStartTime() {
		startTime = System.currentTimeMillis();
		last =0;
	}
	
	void setMaxTime(int value) {
		maxTime = value;
		last =0;
	}
	
	void PrintElapsedTime() {
		long current = System.currentTimeMillis();
		long elapsed = current - startTime;
		System.out.println(">> Elapsed Time: "+ elapsed);
	}
    boolean onTime() {
    	long current = System.currentTimeMillis();
		long elapsed = current - startTime;
//		System.out.println(">> Elapsed Time: "+ elapsed);
//		long memory = runtime.totalMemory() - runtime.freeMemory();
//		System.out.println(">> memory: " + (memory - initmemory));
		if(maxTime > 0 && (elapsed >= maxTime || (last > 0 && elapsed+(current-last) >= maxTime))){
			System.out.println("<< fim do proceso - excedeu "+maxTime+" ms");
			return true;
		};
		last = current;
		return false;
    }
}

