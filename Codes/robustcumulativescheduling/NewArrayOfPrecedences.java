package robustcumulativescheduling;

import java.util.Comparator;

public class NewArrayOfPrecedences {
    
    private int tValue;
    private boolean isDelayed;
    
    public NewArrayOfPrecedences(int tValue, boolean isDelayed ) {
        
        this.tValue = tValue;
        this.isDelayed = isDelayed;
    }
    
    public int whatIsTValue() {
        return tValue;
    }
    
    public boolean whatIsDelayStatus() {
        return isDelayed;
    }
    
    public void setTValue(int tValue) {
        this.tValue = tValue;
    }
    
    public void setDelayStatus(boolean isDelayed) {
        this.isDelayed = isDelayed;
    }
    
    public static class NewArrayOfPrecedencesByTValue implements Comparator<NewArrayOfPrecedences> {
        @Override
        public int compare(NewArrayOfPrecedences o1, NewArrayOfPrecedences o2) {
            return o1.whatIsTValue() > o2.whatIsTValue() ? 1 : (o1.whatIsTValue() < o2.whatIsTValue() ? -1 : 0);
        }
   }
   
   public static class ComparatorByTValue implements Comparator<Integer> {
       private final NewArrayOfPrecedences[] NewArrayOfPrecedences;
       public ComparatorByTValue(NewArrayOfPrecedences[] list_of_NewArrayOfPrecedences) {
           this.NewArrayOfPrecedences = list_of_NewArrayOfPrecedences;
       }
       @Override
       public int compare(Integer a, Integer b) {
           return NewArrayOfPrecedences[a].whatIsTValue() - NewArrayOfPrecedences[b].whatIsTValue();
       }
   }
   
   public static class ComparatorByTValueNew implements Comparator<Integer> {
       private final int[] NewArrayOfPrecedences;
       public ComparatorByTValueNew(int[] list_of_NewArrayOfPrecedences) {
           this.NewArrayOfPrecedences = list_of_NewArrayOfPrecedences;
       }
       @Override
       public int compare(Integer a, Integer b) {
           return NewArrayOfPrecedences[a] - NewArrayOfPrecedences[b];
       }
   }
}