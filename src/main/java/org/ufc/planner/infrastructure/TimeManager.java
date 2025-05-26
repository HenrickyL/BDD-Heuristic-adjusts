package org.ufc.planner.infrastructure;

public class TimeManager {
    private long startTime;
    private long last = 0;
    private static long maxTime = 2*60*1000; //5MIN //= 1800000;//30min - 1800000


    public void resetStartTime() {
        startTime = System.currentTimeMillis();
        last =0;
    }

   public  void setMaxTime(int value) {
        maxTime = value;
        last =0;
   }
   public boolean verifyBreak(){
       this.PrintElapsedTime();
//       this.resetStartTime();
       if(this.onTime()) {
           System.out.println(">> Break by MaxLimitTime");
           return true;
       }else{
           return false;
       }
   }

    public void PrintElapsedTime() {
        long current = System.currentTimeMillis();
        long elapsed = current - startTime;
        System.out.println(">> Elapsed Time: "+ elapsed);
    }
    public boolean onTime() {
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
