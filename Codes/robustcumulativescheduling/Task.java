package robustcumulativescheduling;

import java.util.Comparator;

public class Task {
    private int est;
    private int lct;
    private int delayedLct;
    private int p;
    private int allowedDelay;
    private int taskHeight;
    
    public Task(int est, int lct, int delayedLct, int p, int allowedDelay, int taskHeight) {
        
        if (delayedLct != lct + allowedDelay)
            throw new IllegalArgumentException("This task is not valid!");
        this.est = est;
        this.lct = lct;
        this.p = p;
        this.allowedDelay = allowedDelay;
        this.taskHeight = taskHeight;
        this.delayedLct = lct + allowedDelay;
    }
    
    public int earliestStartingTime() {
        return est;
    }
    
    public int latestCompletionTime() {
        return lct;
    }
    
    public int delayedLatestCompletionTime() {
        return delayedLct;
    }
    
    public int processingTime() {
        return p;
    }
    
    public int allowedDelay() {
        return allowedDelay;
    }
    
    public int height() {
        return taskHeight;
    }
    
    public int delayedProcessingTime() {
        return processingTime() + allowedDelay();
    }
    
    public int energy() {
        return height() * processingTime();
    }
    public int envelope(int C) {
        return C * earliestStartingTime() + height() * processingTime();
    }
    
    public int envelopeForUpperBound(int C) {
        return C * latestCompletionTime() - height() * processingTime();
    }
    
    public int envelopeForUpperBoundForTheDelayedPortion(int C) {
        return C * delayedLatestCompletionTime() - height() * allowedDelay();
    }
    
    public int delayedEnergy() {
        return height() * (processingTime() + allowedDelay());
    }
    
    public int energyOnlyForTheDelayedPortion() {
        return height() * allowedDelay();
    }
              
    public int delayedEnvelope(int C) {
        return C * earliestStartingTime() + height() * (processingTime() + allowedDelay());
    }
    
       public int delayedEnvelopeForUpperBound(int C) {
        return C * delayedLatestCompletionTime() - height() * (processingTime() + allowedDelay());
    }  
    
    public int earliestCompletionTime() {
        return earliestStartingTime() + processingTime();
    }
    
     public int delayedEarliestCompletionTime() {
        return earliestCompletionTime() + allowedDelay();
    }
     
    public int latestStartingTime() {
        return latestCompletionTime() - processingTime();
    }
   
    public int latestStartingTimeOfDelayedTask() {
        return delayedLatestCompletionTime() - processingTime() - allowedDelay();
    }
    
    public void setEarliestStartingTime(int est) {
        this.est = est;
    }
    
    public void setEarliestStartingTimeWithCheck(int est) {
        if(est > earliestStartingTime())
            this.est = est;
    }
    
    public void setLatestCompletionTime(int lct) {
        this.lct = lct;
    }
    
    public void setDelayedLatestCompletionTime(int delayedLct) {
        this.delayedLct = delayedLct;
    }
    
    public void setProcessingTime(int p) {
        this.p = p;
    }
    public void setDelay(int allowedDelay) {
        this.allowedDelay = allowedDelay;
    }
    
    public void setHeight(int taskHeight) {
        this.taskHeight = taskHeight;
    }
        
    public static class ComparatorByEst implements Comparator<Integer> {
        private final  Task[] tasks;
        public ComparatorByEst( Task[] list_of_tasks) {
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].earliestStartingTime() - tasks[b].earliestStartingTime();
        }
    }
    
    public static class ComparatorByLct implements Comparator<Integer> {        
        private final  Task[] tasks;
        public ComparatorByLct( Task[] list_of_tasks) {
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].latestCompletionTime() - tasks[b].latestCompletionTime();
            
        }
    }
       
    public static class ComparatorByDelayedLct implements Comparator<Integer> {
        private final  Task[] tasks;
        public ComparatorByDelayedLct( Task[] list_of_tasks) {
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].delayedLatestCompletionTime() - tasks[b].delayedLatestCompletionTime();
        }
    }
    
    public static class ComparatorByEct implements Comparator<Integer> {
        private final  Task[] tasks;
        public ComparatorByEct( Task[] list_of_tasks) {
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].earliestCompletionTime() - tasks[b].earliestCompletionTime();
        }
    }
    
    public static class ComparatorByDelayedEct implements Comparator<Integer> {
        private final  Task[] tasks;
        public ComparatorByDelayedEct( Task[] list_of_tasks) {
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].delayedEarliestCompletionTime() - tasks[b].delayedEarliestCompletionTime();
        }
    }
    
    public static class ComparatorByLst implements Comparator<Integer> {
        private final  Task[] tasks;
        public ComparatorByLst( Task[] list_of_tasks) {
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].latestStartingTime() - tasks[b].latestStartingTime();
        }
    }
    
    public static class ComparatorByLstOfDelayedTask implements Comparator<Integer> {
        private final  Task[] tasks;
        public ComparatorByLstOfDelayedTask( Task[] list_of_tasks) {
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].latestStartingTimeOfDelayedTask() - tasks[b].latestStartingTimeOfDelayedTask();
        }
    }
    
    public static class ComparatorByHeight implements Comparator<Integer> {
        private final  Task[] tasks;        
        public ComparatorByHeight( Task[] list_of_tasks) {           
            this.tasks = list_of_tasks;
        }
        @Override
        public int compare(Integer a, Integer b) {
            return tasks[a].height() - tasks[b].height();
            
        }
    }
    
    public static class TaskByHeight implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.height() < o2.height() ? 1 : (o1.height() > o2.height() ? -1 : 0);
        }
    }
    
    @Override
    public String toString() {
        return "(est = " + this.est + ", lct = " + this.lct + ", lct_d = " + this.delayedLct + ", p = " + this.p + ", d = " + this.allowedDelay + ", h = " + this.taskHeight + ")";
        
    }
}
