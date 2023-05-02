package robustcumulativescheduling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import org.chocosolver.util.tools.ArrayUtils;

public class Propagator_edgeFindingConstraint extends Propagator<IntVar> {

    private final int delays[];
    private final int resourceHeight;
    private final int delayNumber;
    private final Task[] Tasks_updated_at_each_iteration;
    int[][] updated_Est_Lct_And_dLcts;
    private final int lengthOfVariables;
    private final int[] FalseStatePrecedencesForLowerBoundNew;
    private final int[] TrueStatePrecedencesForLowerBoundNew;
    private int[] currentEnvelopeForFalseState_new_approach;
    private int[] currentEnvelopeForTrueState_new_approach;
    private final int numberOfAllDistinctCapacities;
    private final int[] FalseStatePrecedencesForUpperBound;
    private final int[] TrueStatePrecedencesForUpperBound;
    Integer[] indices_of_tasks_sorted_by_false_state_precArray;
    Integer[] indices_of_tasks_sorted_by_true_state_precArray;
    Integer[] indices_of_tasks_sorted_by_false_state_precArray_upperbound;
    Integer[] indices_of_tasks_sorted_by_true_state_precArray_upperbound;
    private final Integer[] indices_of_tasks_sorted_by_est_P;
    private final Integer[] indices_of_tasks_sorted_by_lct_P;
    private final Integer[] indices_of_tasks_sorted_by_delayed_lct_P;
    private final Integer[] indices_of_tasks_sorted_by_ect_P;
    private final Integer[] indices_of_tasks_sorted_by_delayed_ect_P;
    private final Integer[] indices_of_tasks_sorted_by_height_P;
    boolean foundPrecedenceInPrecArray;
    public static final int MINUS_INFINITY = -1073741824;
    public static final int INFINITY = 1073741824;
    ArrayList<Integer> VectorTOfLctAndDelayedLcts;
    private int sizeOfVectorTOfLctAndDelayedLcts;
    private int[] indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed;
    private int[] indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound;
    private int[] updateForFalse_new_approach;
    private int[] updateForTrue_new_approach;
    private int[] temp_updateForFalse_new_approach;
    private int[] temp_updateForTrue_new_approach;
//    private int[] updateForFalse_new_approachUpperBound;
//    private int[] updateForTrue_new_approachUpperBound; 
//    private int[] temp_updateForFalse_new_approachUpperBound;
//    private int[] temp_updateForTrue_new_approachUpperBound;
    private boolean iteratedOnce;
    private int numberOfUnchangedLctsOnBranching;
    private final int[] theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities;
    private final boolean[] thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound;
    private final boolean[] thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound;
    boolean filterMakespan;
    private final boolean NCumulativeConstraintsPosted;
    thetaLambdaTree TLT_P;
    thetaLambdaTree TLT_P_upperbound;
    thetaTree TT_P;
    thetaTree TT_P_upperbound;
    private final int numberOfTasks;
    private final int[] task_index_to_node_index_for_est;
    private int[] calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach;
    private int[] calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach;
    private final NewArrayOfPrecedences[] sortedAndMergedLctAndDelayedLctArrayTwoDimensional;
    private final int[] valuesOfSortedAndMergedLctAndDelayedLctArray;
    private int counterOnSortedAndMergedLctAndDelayedLctArray;
    private int indexOfThisTaskInTheOriginalArrayConsiredingTvaluesArray;
    private int currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray;
    private boolean stateOfThisTaskInTheOriginalArrayConsiredingTvaluesArray;
    private int counterOnSortedEstArray;
    private final boolean filterLowerBound;
    private final boolean filterUpperBound;
//    private final int lengthOfCapacityRange;
    private int calculatedEnvelopeUpperBound;
    private boolean processingPrecedencesOfFalseCoulmnIsOver;
    private boolean processingPrecedencesOfTrueCoulmnIsOver;
    //  private boolean processingPrecedencesOfFalseCoulmnIsOver_upperbound;
    // private boolean processingPrecedencesOfTrueCoulmnIsOver_upperbound;
    //  private int indexOfLastTaskToBeProcessedForAdjustment_upperbound;
    private int[] thisCapacityAmongAllDistinctCapacitiesExistsAtThisIndexOfTheArrayOfAllCapacitiesRequiredToBeProcessed;
    //   private int[] thisCapacityAmongAllDistinctCapacitiesExistsAtThisIndexOfTheArrayOfAllCapacitiesRequiredToBeProcessedUpperBound;
    private final boolean activateImprovingDetectionLowerBound;
    private final int twiceTheNumberOfTasks;
    private final int[] allDistinctCapacities;
    private final boolean[] thisTaskHasDistinctCapacityFromThePrevious;
    private int root;
    //   private int root_ub;
    private final int[] lowerBoundOfStartingTimes;
    private final int[] upperBoundOfStartingTimes;
    private final int[] lowerBoundOfProcessingTime;
    private final int[] lowerBoundOfEndingTimes;
    private final int[] upperBoundOfEndingTimes;
    private int[] temp;
    private final int numberOfTasksMinusOne;
    private final int cumulativeConstraintCounter;

//Esat dde;
//   private int z1 = 0;
    // private int z2 = 0;
    //private int z3 = 0;
    public Propagator_edgeFindingConstraint(IntVar[] vars, int nbTasks, int nbResources, int[] capacities, int[] ka, boolean filterMakespan, boolean filterLowerBound, boolean filterUpperBound, boolean activateImprovingDetectionLowerBound, boolean NCumulativeConstraintsPosted, int cumulativeConstraintCounter, boolean CAPProblemConsidered, int r) {
        super(ArrayUtils.append(vars), PropagatorPriority.VERY_SLOW, false);
        this.delays = ka;
        this.resourceHeight = capacities[0];
        this.lengthOfVariables = vars.length;
        this.root = MINUS_INFINITY;
        //  this.root_ub = INFINITY;
        this.numberOfTasks = nbTasks;
        this.delayNumber = r;
        this.filterLowerBound = filterLowerBound;
        this.filterUpperBound = filterUpperBound;
        this.cumulativeConstraintCounter = cumulativeConstraintCounter;
        this.filterMakespan = filterMakespan;
        this.Tasks_updated_at_each_iteration = new Task[numberOfTasks];
        this.updated_Est_Lct_And_dLcts = new int[2][numberOfTasks];
        this.activateImprovingDetectionLowerBound = activateImprovingDetectionLowerBound;
        this.FalseStatePrecedencesForLowerBoundNew = new int[numberOfTasks];
        this.TrueStatePrecedencesForLowerBoundNew = new int[numberOfTasks];
        this.theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities = new int[numberOfTasks];
        this.FalseStatePrecedencesForUpperBound = new int[numberOfTasks];
        this.TrueStatePrecedencesForUpperBound = new int[numberOfTasks];
        this.twiceTheNumberOfTasks = 2 * numberOfTasks;
        this.valuesOfSortedAndMergedLctAndDelayedLctArray = new int[twiceTheNumberOfTasks];
        this.sortedAndMergedLctAndDelayedLctArrayTwoDimensional = new NewArrayOfPrecedences[twiceTheNumberOfTasks];
        this.NCumulativeConstraintsPosted = NCumulativeConstraintsPosted;
        this.thisTaskHasDistinctCapacityFromThePrevious = new boolean[numberOfTasks];
        this.indices_of_tasks_sorted_by_est_P = new Integer[numberOfTasks];
        this.indices_of_tasks_sorted_by_lct_P = new Integer[numberOfTasks];
        this.indices_of_tasks_sorted_by_delayed_lct_P = new Integer[numberOfTasks];
        this.indices_of_tasks_sorted_by_ect_P = new Integer[numberOfTasks];
        this.indices_of_tasks_sorted_by_delayed_ect_P = new Integer[numberOfTasks];
        this.indices_of_tasks_sorted_by_height_P = new Integer[numberOfTasks];
        this.iteratedOnce = false;
        this.numberOfTasksMinusOne = numberOfTasks - 1;
        this.task_index_to_node_index_for_est = new int[numberOfTasks];
        this.foundPrecedenceInPrecArray = false;
        this.lowerBoundOfStartingTimes = new int[numberOfTasks];
        this.upperBoundOfStartingTimes = new int[numberOfTasks];
        this.lowerBoundOfProcessingTime = new int[numberOfTasks];
        this.lowerBoundOfEndingTimes = new int[numberOfTasks];
        this.upperBoundOfEndingTimes = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            indices_of_tasks_sorted_by_est_P[o] = o;
            indices_of_tasks_sorted_by_lct_P[o] = o;
            indices_of_tasks_sorted_by_delayed_lct_P[o] = o;
        }
        for (int i = 0; i < twiceTheNumberOfTasks; i++) {
            sortedAndMergedLctAndDelayedLctArrayTwoDimensional[i] = new NewArrayOfPrecedences(-1, false);
        }
        if (filterLowerBound) {
            this.indices_of_tasks_sorted_by_false_state_precArray = new Integer[numberOfTasks];
            this.indices_of_tasks_sorted_by_true_state_precArray = new Integer[numberOfTasks];
            for (int i = 0; i < numberOfTasks; i++) {
                FalseStatePrecedencesForLowerBoundNew[i] = MINUS_INFINITY;
                TrueStatePrecedencesForLowerBoundNew[i] = MINUS_INFINITY;
            }
        }
        if (filterUpperBound) {
            this.indices_of_tasks_sorted_by_false_state_precArray_upperbound = new Integer[numberOfTasks];
            this.indices_of_tasks_sorted_by_true_state_precArray_upperbound = new Integer[numberOfTasks];
            for (int i = 0; i < numberOfTasks; i++) {
                FalseStatePrecedencesForUpperBound[i] = INFINITY;
                TrueStatePrecedencesForUpperBound[i] = INFINITY;
            }
        }
        //  this.VectorTOfLctAndDelayedLcts = new Vector();
        this.VectorTOfLctAndDelayedLcts = new ArrayList();
        this.sizeOfVectorTOfLctAndDelayedLcts = 0;
        for (int j = 0; j < numberOfTasks; j++) {
            Tasks_updated_at_each_iteration[j] = new Task(vars[j].getLB(), vars[j].getUB() + vars[nbTasks + j].getLB(), vars[j].getUB() + vars[nbTasks + j].getLB() + ka[j], vars[nbTasks + j].getLB(), ka[j], vars[3 * nbTasks + j].getLB());
        }
        for (int o = 0; o < numberOfTasks; o++) {
            indices_of_tasks_sorted_by_height_P[o] = o;
        }
        sortIndicesWithJavaLibrary(indices_of_tasks_sorted_by_height_P, new Task.ComparatorByHeight(Tasks_updated_at_each_iteration));
        int numOfDistinctCapacities = 1;
        int j;
        int i = 0;
        thisTaskHasDistinctCapacityFromThePrevious[indices_of_tasks_sorted_by_height_P[0]] = true;
        while (i < numberOfTasks) {
            int val = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_height_P[i]].height();
            j = i + 1;
            while (j < numberOfTasks && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_height_P[j]].height() == val) {
                j++;
            }
            i = j;
            numOfDistinctCapacities++;
            if (j < numberOfTasks) {
                thisTaskHasDistinctCapacityFromThePrevious[indices_of_tasks_sorted_by_height_P[j]] = true;
            }
        }
        this.allDistinctCapacities = new int[numOfDistinctCapacities - 1];
        int s = 0;
        for (int v = 0; v < numberOfTasks; v++) {
            if (thisTaskHasDistinctCapacityFromThePrevious[indices_of_tasks_sorted_by_height_P[v]]) {
                allDistinctCapacities[s] = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_height_P[v]].height();
                s++;
            }
        }
        this.numberOfAllDistinctCapacities = allDistinctCapacities.length;
        this.thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound = new boolean[numberOfAllDistinctCapacities];
        this.thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound = new boolean[numberOfAllDistinctCapacities];
//      this.lengthOfCapacityRange = allDistinctCapacities[numOfDistinctCapacities - 2] + 1;
        for (int u = 0; u < numberOfAllDistinctCapacities; u++) {
            int c = allDistinctCapacities[u];
            for (int v = 0; v < numberOfTasks; v++) {
                if (Tasks_updated_at_each_iteration[v].height() == c) {
                    theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[v] = u;
                }
            }
        }
        if (activateImprovingDetectionLowerBound) {
            for (int o = 0; o < numberOfTasks; o++) {
                indices_of_tasks_sorted_by_ect_P[o] = o;
                indices_of_tasks_sorted_by_delayed_ect_P[o] = o;
            }
        }
        if (filterLowerBound) {
            this.TLT_P = new thetaLambdaTree(Tasks_updated_at_each_iteration, indices_of_tasks_sorted_by_est_P, delayNumber, resourceHeight, 0);
            TT_P = new thetaTree(Tasks_updated_at_each_iteration, delayNumber, resourceHeight, true);
        }
        if (filterUpperBound) {
            this.TLT_P_upperbound = new thetaLambdaTree(Tasks_updated_at_each_iteration, indices_of_tasks_sorted_by_est_P, delayNumber, resourceHeight, 0);
            TT_P_upperbound = new thetaTree(Tasks_updated_at_each_iteration, delayNumber, resourceHeight, true);
        }
        //    System.out.print("we stop here to debbug\n");
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
        //   System.out.println();

        // if (resourceHeight == 10)
        //  {
        //    if (NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0) {
        //   System.out.println("We are processing the resource with C = " + resourceHeight);
//                
        //    z1++;
        //  System.out.println("z1 = " + z1);
        //  }
        //  if (NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0) {
        //     System.out.println("z1 = " + z1);
        //       z1++;
        //  }
        //   }
        //   else {
        // z2++;
        //   if (NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0)
        //   System.out.println("z2 = " + z2);
        //  }
        //   else  if (resourceHeight == 8) {
        // if (NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0){
        //   System.out.println("We are processing the resource with C = " + resourceHeight);
        //   z2++;
        //    System.out.println("z2 = " + z2);
        //   }
//else  if (resourceHeight == 6) {
        // if (NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0){
        //   System.out.println("We are processing the resource with C = " + resourceHeight);
        //  z3++;
        //  System.out.println("z3 = " + z3);
        //}
        //  }
        //   System.out.println("z2 = " + z2);   
//z2++;
        // if (z == 7853)
        //   System.out.println("z = " + z);
        // }
        //      else if (resourceHeight == 4) {
        //    System.out.println("z3 = " + z3);  
        //  System.out.println("We are processing the resource with C = " + resourceHeight);
        //z3++;
        //         } 
        /*
     System.out.println();
     if (resourceHeight == 10) {
         z1++;
         System.out.println("z1 = " + z1);
         System.out.println("We are processing the resource with C = " + resourceHeight);
     }
     if (resourceHeight == 8) {
         z2++;
         System.out.println("z2 = " + z2);
         System.out.println("We are processing the resource with C = " + resourceHeight);
     }
         */
        //if (NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0) {
        //  System.out.println("domains before filtering");
        /*
        if (z2 >= 925 || z1 >= 779) 
            debugFlag = true;
         */
        //  if (z2>=832 && NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0)
        //   debugFlag = true;
        // if (z1 >= 2095 || z2 >= 2097) {
        //System.out.println();
      //  System.out.println("starting times");
        for (int t = 0; t < numberOfTasks; t++) {
         //   System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() + "]");
        }
        // }
        // System.out.println("ui");
        // }

        //  if(z2>=855)
        //       debugFlag = false;
        // }
        //   System.out.println("processing times");
        //  for (int t = numberOfTasks; t < twiceTheNumberOfTasks; t++) {
        // System.out.println("[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
        // }
        //   if (z1 >= 2095 || z2 >= 2097) {
        //System.out.println("ending times");
        // for (int t = twiceTheNumberOfTasks; t < 3 * numberOfTasks; t++) {
        // System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
        //  }
        //   }
        /*
        System.out.println("heights");
        for (int t = 3 * numberOfTasks; t < 4 * numberOfTasks; t++) {
        System.out.println("[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
        }
       
       
                   //HERE IN CRANE PROBLEM IS DIFFERENT. IN THAT PROBLEM CRANES ARE VARIABLES AS WELL, SO   capacity and makespan ARE AFTER THEM.   

       System.out.println("capacity");
        System.out.println("[" + vars[4 * numberOfTasks].getLB() + ", " + vars[4 * numberOfTasks].getUB() +"]");
         */
        //}
        //   System.out.printf("makespan bounds at this iteration: ");
        //    System.out.println("[" + vars[4 * numberOfTasks + 1].getLB() + ", " + vars[4 * numberOfTasks + 1].getUB() +"]");
        //  if (vars[12].getLB() == 7 && vars[12].getUB() == 16) {
        // System.out.println("these are your tasks before even touching with filtering");
        //  for (int j = 0; j < numberOfTasks; j++) {
        //  System.out.println(Tasks_updated_at_each_iteration[j]);
        //   }
        // }
        boolean filtered = false;
        boolean filteredLowerBound;
        boolean filteredUpperBound;
        int currentMakespanBound;
        do {
            this.numberOfUnchangedLctsOnBranching = 0;
            for (int j = 0; j < numberOfTasks; j++) {
                final int est = vars[j].getLB();
                final int lct = vars[j].getUB() + vars[numberOfTasks + j].getLB();
                final int delayed_lct = lct + delays[j];
                if (lct == Tasks_updated_at_each_iteration[j].latestCompletionTime()) {
                    numberOfUnchangedLctsOnBranching++;
                }
                Tasks_updated_at_each_iteration[j].setEarliestStartingTime(est);
                Tasks_updated_at_each_iteration[j].setLatestCompletionTime(lct);
                Tasks_updated_at_each_iteration[j].setDelayedLatestCompletionTime(delayed_lct);
                updated_Est_Lct_And_dLcts[0][j] = est;
                updated_Est_Lct_And_dLcts[1][j] = lct;
                lowerBoundOfStartingTimes[j] = vars[j].getLB();
                upperBoundOfStartingTimes[j] = vars[j].getUB();
                lowerBoundOfProcessingTime[j] = vars[numberOfTasks + j].getLB();
                lowerBoundOfEndingTimes[j] = vars[twiceTheNumberOfTasks + j].getLB();
                upperBoundOfEndingTimes[j] = vars[twiceTheNumberOfTasks + j].getUB();
                lowerBoundOfStartingTimes[j] = Math.max(lowerBoundOfStartingTimes[j], lowerBoundOfEndingTimes[j] - lowerBoundOfProcessingTime[j]);
                upperBoundOfStartingTimes[j] = Math.min(upperBoundOfStartingTimes[j], upperBoundOfEndingTimes[j] - lowerBoundOfProcessingTime[j]);
                lowerBoundOfEndingTimes[j] = Math.max(lowerBoundOfEndingTimes[j], lowerBoundOfStartingTimes[j] + lowerBoundOfProcessingTime[j]);
                upperBoundOfEndingTimes[j] = Math.min(upperBoundOfEndingTimes[j], upperBoundOfStartingTimes[j] + lowerBoundOfProcessingTime[j]);
            }
            //  if (z2 == 4652) {
            //  System.out.println();
            // System.out.println("Tasks at this new iteration");    
//for (int j = 0; j < numberOfTasks; j++) {
            //   System.out.println("Task number " + j + ":" + " " + Tasks_updated_at_each_iteration[j]);
            //  }
            // } 
//                      //System.out.println("debug");
//}

        //    System.out.println("these are your tasks");
            for (int j = 0; j < numberOfTasks; j++) {
           //     System.out.println(Tasks_updated_at_each_iteration[j]);
            }
            /*
             if (
                      //resourceHeight == 6 &&
//                      
                  (vars[0].getLB() == 0 && 0 == vars[0].getUB()) 
                 && 
                      (vars[1].getLB() == 12 && 12 == vars[1].getUB() )                     
                   && 
                      (vars[2].getLB() == 27 && 27 == vars[2].getUB() )                     
&& 
                  (vars[3].getLB() == 23 && 23 == vars[3].getUB() )                     
&& 
                 (vars[4].getLB() == 4 && 4 == vars[4].getUB() )                     
&& 
                  (vars[5].getLB() == 13 && 13 == vars[5].getUB() )                     
&& 
                  (vars[6].getLB() == 11 && 11 == vars[6].getUB() )                     
&& 
                  (vars[7].getLB() == 10 && 10 == vars[7].getUB() )                      
&& 
                  (vars[8].getLB() == 18 && 18 == vars[8].getUB() )                     
&& 
                  (vars[9].getLB() == 0 && 0 == vars[9].getUB() )                     
                      )
             */
            //      System.out.print("we stop here to debbug\n");
            /*         
                  if (                         
                  (resourceHeight == 6 &&
                      
                  (vars[0].getLB() == 0 && 0 == vars[0].getUB()) 
                  && 
                  (vars[1].getLB() == 0 && 0 == vars[1].getUB() )                     
                   && 
                  (vars[2].getLB() == 2 && 2 == vars[2].getUB() )                     
&& 
                  (vars[3].getLB() == 3 && 3 == vars[3].getUB() )                     
&& 
                  (vars[4].getLB() == 5 && 5 == vars[4].getUB() )                     
&& 
                  (vars[5].getLB() == 10 && 10 == vars[5].getUB() )                     
&& 
                  (vars[6].getLB() == 6 && 6 == vars[6].getUB() )                     
&& 
                  (vars[7].getLB() == 12 && 12 == vars[7].getUB() )                      
&& 
                  (vars[8].getLB() == 15 && 15 == vars[8].getUB() )                     
&& 
                  (vars[9].getLB() == 12 && 12 == vars[9].getUB() )                     
&& 
                  (vars[10].getLB() == 19 && 19 == vars[10].getUB() )                      
&& 
                  (vars[11].getLB() == 20 && 20 == vars[11].getUB() )                     
&& 
                  (vars[12].getLB() == 17 && 17 == vars[12].getUB() )                     
&& 
                  (vars[13].getLB() == 25 && 25 == vars[13].getUB() )                     
&& 
                  (vars[14].getLB() == 25 && 25 == vars[14].getUB() )                     
&&
                  (vars[15].getLB() == 27 && 27 == vars[15].getUB() )                     
&&
                  (vars[16].getLB() == 30 && 30 == vars[16].getUB() )                     
                          ) 
                          ||
                                        (resourceHeight == 3 &&
                      
                  (vars[0].getLB() == 3 && 3 == vars[0].getUB()) 
                  && 
                  (vars[1].getLB() == 5 && 5 == vars[1].getUB() )                     
                   && 
                  (vars[2].getLB() == 6 && 6 == vars[2].getUB() )                     
&& 
                  (vars[3].getLB() == 12 && 12 == vars[3].getUB() )                     
&& 
                  (vars[4].getLB() == 12 && 12 == vars[4].getUB() )                     
&& 
                  (vars[5].getLB() == 17 && 17 == vars[5].getUB() )                     
&& 
                  (vars[6].getLB() == 25 && 25 == vars[6].getUB() )                     
&& 
                  (vars[7].getLB() == 27 && 27 == vars[7].getUB() )                      
&& 
                  (vars[8].getLB() == 30 && 30 == vars[8].getUB() )                      
                          )
                          ||
                           (resourceHeight == 4 &&
                      
                  (vars[0].getLB() == 0 && 0 == vars[0].getUB()) 
                  && 
                  (vars[1].getLB() == 3 && 3 == vars[1].getUB() )                     
                   && 
                  (vars[2].getLB() == 5 && 5 == vars[2].getUB() )                     
&& 
                  (vars[3].getLB() == 10 && 10 == vars[3].getUB() )                     
&& 
                  (vars[4].getLB() == 6 && 6 == vars[4].getUB() )                     
&& 
                  (vars[5].getLB() == 0 && 0 == vars[5].getUB() )                     
&& 
                  (vars[6].getLB() == 12 && 12 == vars[6].getUB() )                     
&& 
                  (vars[7].getLB() == 19 && 19 == vars[7].getUB() )                      
&& 
                  (vars[8].getLB() == 20 && 20 == vars[8].getUB() )                     
&& 
                  (vars[9].getLB() == 17 && 17 == vars[9].getUB() )                     
&& 
                  (vars[10].getLB() == 25 && 25 == vars[10].getUB() )                      
&& 
                  (vars[11].getLB() == 6 && 6 == vars[11].getUB() )                     
&& 
                  (vars[12].getLB() == 27 && 27 == vars[12].getUB() )                     
&& 
                  (vars[13].getLB() == 30 && 30 == vars[13].getUB() )                     
                          ) 

                      )
              System.out.print("we stop here to debbug\n");
             */

            int temp1;
            int temp2;

            if (filterLowerBound) {
                insertionSortWithComparator(indices_of_tasks_sorted_by_est_P, new Task.ComparatorByEst(Tasks_updated_at_each_iteration));
                insertionSortWithComparator(indices_of_tasks_sorted_by_lct_P, new Task.ComparatorByLct(Tasks_updated_at_each_iteration));
                insertionSortWithComparator(indices_of_tasks_sorted_by_delayed_lct_P, new Task.ComparatorByDelayedLct(Tasks_updated_at_each_iteration));
                if (activateImprovingDetectionLowerBound) {
                    insertionSortWithComparator(indices_of_tasks_sorted_by_ect_P, new Task.ComparatorByEct(Tasks_updated_at_each_iteration));
                    insertionSortWithComparator(indices_of_tasks_sorted_by_delayed_ect_P, new Task.ComparatorByDelayedEct(Tasks_updated_at_each_iteration));
                }
                if (!iteratedOnce || numberOfUnchangedLctsOnBranching < numberOfTasks || filterUpperBound) {
                    joinAndSortAllLctAndDelayedLcts();
                }
                // }
//         else if (filterUpperBound && activateImprovingDetectionUpperBound) {
//             insertionSortWithComparator(indices_of_tasks_sorted_by_est_P, new Task.ComparatorByEst(Tasks_updated_at_each_iteration));
//             insertionSortWithComparator(indices_of_tasks_sorted_by_lst_P, new Task.ComparatorByLst(Tasks_updated_at_each_iteration));
//             insertionSortWithComparator(indices_of_tasks_sorted_by_lst_delayed_P, new Task.ComparatorByLstOfDelayedTask(Tasks_updated_at_each_iteration));
//         }
                if (!iteratedOnce) {
                    iteratedOnce = true;
                }
                //  if (filterLowerBound) {
                do {
                    filteredLowerBound = false;
                    //     if ( z1 == 3026)
                    //   System.out.printf("%s", "stop");
                    detectionOfLowerBounds();

                    for (int a = 0; a < numberOfTasks; a++) {
                        if (updated_Est_Lct_And_dLcts[0][a] > vars[a].getLB()) {
                            //  if (resourceHeight == 10)
                            //System.out.println("z1 = " + z1);
//else
//System.out.println("z2 = " + z2);
                            //    if (z2>=900||z1==750)                
                            //    System.out.println("A filtering occurs here low.");
//                      if (z1 == 876)

//if (debugFlag)
                            // {
                            // if (z2>=832 
                            //&& z2<= 854&& NCumulativeConstraintsPosted && cumulativeConstraintCounter == 0
                            //  ){
//            if (z2>=458||z1>=401) {
//               if (NCumulativeConstraintsPosted)
//                    System.out.println("cumulativeConstraintCounter = " + cumulativeConstraintCounter);
//                
                            //    System.out.println("vars");
                            // for (int t = 0; t < numberOfTasks; t++) {
                            //  System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
                            //  }
                            //  }
                            //  if (z2 == 4652) {
                            //  System.out.println("z1 = " + z1);
                            //   System.out.println("Tasks before filtering");    
//for (int j = 0; j < numberOfTasks; j++) {
                            //  System.out.println("Task number " + j + ":" + " " + Tasks_updated_at_each_iteration[j]);
                            //  }
                            //  }
                            // }
//               }
// if (vars[12].getLB() == 7 && vars[12].getUB() == 16)
//            System.out.println("stop here to debug");
// }
//     if (z2>=832 )
//if (NCumulativeConstraintsPosted)
//  System.out.println("cumulativeConstraintCounter = " + cumulativeConstraintCounter);         
                            //                     if (NCumulativeConstraintsPosted   && cumulativeConstraintCounter == 0)
                            //  System.out.println("in lower bound");
// }
                            lowerBoundOfStartingTimes[a] = updated_Est_Lct_And_dLcts[0][a];
                            //  lowerBoundOfEndingTimes[a] = Math.max(lowerBoundOfEndingTimes[a], lowerBoundOfStartingTimes[a] + lowerBoundOfProcessingTime[a]);
                            temp1 = lowerBoundOfStartingTimes[a] + lowerBoundOfProcessingTime[a];
                            if (temp1 > lowerBoundOfEndingTimes[a]) {
                                lowerBoundOfEndingTimes[a] = temp1;
                            }

/// if ()
                            if (checkVariableBounds()) {
                                // System.out.println("kl");
                                /*
                    lowerBoundOfStartingTimes[a] = Math.max(lowerBoundOfStartingTimes[a],lowerBoundOfEndingTimes[a]-lowerBoundOfProcessingTime[a]);
                    upperBoundOfStartingTimes[a] = Math.min(upperBoundOfStartingTimes[a], upperBoundOfEndingTimes[a] - lowerBoundOfProcessingTime[a]);
                    lowerBoundOfEndingTimes[a] = Math.max(lowerBoundOfEndingTimes[a], lowerBoundOfStartingTimes[a] + lowerBoundOfProcessingTime[a]);
                    upperBoundOfEndingTimes[a] = Math.min(upperBoundOfEndingTimes[a],upperBoundOfStartingTimes[a]+lowerBoundOfProcessingTime[a]);
                                 */
                                //  vars[a].updateLowerBound(updated_Est_Lct_And_dLcts[0][a], this);
                                //  if (z2 == 4652)
                                //    System.out.println("we filter the lower bound of task " + a + " from " + vars[a].getLB() + " to " + lowerBoundOfStartingTimes[a]);
                                vars[a].updateLowerBound(lowerBoundOfStartingTimes[a], this);
                                //   vars[a].updateLowerBound(Math.max(updated_Est_Lct_And_dLcts[0][a], vars[twiceTheNumberOfTasks + a].getLB() - vars[numberOfTasks + a].getLB()) , this);
                                vars[numberOfTasks + a].updateLowerBound(lowerBoundOfProcessingTime[a], this);
                                //   vars[twiceTheNumberOfTasks + a].updateLowerBound(Math.max(vars[twiceTheNumberOfTasks + a].getLB(), updated_Est_Lct_And_dLcts[0][a] + vars[numberOfTasks + a].getLB()), this);
                                vars[twiceTheNumberOfTasks + a].updateLowerBound(lowerBoundOfEndingTimes[a], this);
                                final int new_est = Math.max(vars[a].getLB(), vars[twiceTheNumberOfTasks + a].getLB() - vars[numberOfTasks + a].getLB());
                                Tasks_updated_at_each_iteration[a].setEarliestStartingTime(new_est);
                                updated_Est_Lct_And_dLcts[0][a] = new_est;
                                filtered = true;
                                filteredLowerBound = true;
                                //  anEstWasJustUpdated = true;
                                //  if (z1 >= 2095 || z2 >= 2097) {

                                //    System.out.println("Tasks after filtering");    
//for (int j = 0; j < numberOfTasks; j++) {
                                //   System.out.println("Task number " + j + ":" + " " + Tasks_updated_at_each_iteration[j]);
                                //      }
                                // }
                                //  System.out.println();
// if (debugFlag)
                            }
                        }
                    }
                    if (!filteredLowerBound) {
                        filtered = false;
                        //    anEstWasJustUpdated = false;
                        //   if (z1 >= 2095 || z2 >= 2097) 
//System.out.println();
                    }
                    //System.out.println();
                    if (filteredLowerBound) {
                        insertionSortWithComparator(indices_of_tasks_sorted_by_est_P, new Task.ComparatorByEst(Tasks_updated_at_each_iteration));
                        if (activateImprovingDetectionLowerBound) {
                            insertionSortWithComparator(indices_of_tasks_sorted_by_ect_P, new Task.ComparatorByEct(Tasks_updated_at_each_iteration));
                            insertionSortWithComparator(indices_of_tasks_sorted_by_delayed_ect_P, new Task.ComparatorByDelayedEct(Tasks_updated_at_each_iteration));
                        }
                    }
                } while (filteredLowerBound);
            }
            if (filterUpperBound) {
                //  if (anEstWasJustUpdated)                   
//              if (!filterLowerBound && activateImprovingDetectionUpperBound) {
//             insertionSortWithComparator(indices_of_tasks_sorted_by_lct_P, new Task.ComparatorByEst(Tasks_updated_at_each_iteration));
//           //  insertionSortWithComparator(indices_of_tasks_sorted_by_lst_P, new Task.ComparatorByLst(Tasks_updated_at_each_iteration));
//             //insertionSortWithComparator(indices_of_tasks_sorted_by_lst_delayed_P, new Task.ComparatorByLstOfDelayedTask(Tasks_updated_at_each_iteration));
//         }     
                insertionSortWithComparator(indices_of_tasks_sorted_by_est_P, new Task.ComparatorByEst(Tasks_updated_at_each_iteration));
            }
            if (filterUpperBound) {
                do {
                    filteredUpperBound = false;
                    //  if (z1 == 525)
                    //    System.out.println("debug");
                    //  for (int j = 0; j < numberOfTasks; j++) {
                    //  System.out.println("j - > " + j + " " + Tasks_updated_at_each_iteration[j]);
                    //   }
                    detectionOfUpperBounds();
                    //  for (int j = 0; j < numberOfTasks; j++) {
                    //  System.out.println("j - > " + j + " " + Tasks_updated_at_each_iteration[j]);
                    // System.out.println("lower Bound Of Starting Times " + j + ": " + lowerBoundOfStartingTimes[j]); 
                    //  System.out.println("lower Bound Of Ending Times " +  j + ": " + lowerBoundOfEndingTimes[j]); 
                    //   System.out.println("upper Bound Of Ending Times " +  j + ": " + upperBoundOfEndingTimes[j]); 
                    //  }
                    for (int a = 0; a < numberOfTasks; a++) {
                        //  System.out.println("vars[a].getUB()" + vars[a].getUB());
                        // System.out.println("vars[numberOfTasks + a].getLB()" + vars[numberOfTasks + a].getLB());

                        //  if (updated_Est_Lct_And_dLcts[1][a] < vars[a].getUB() + vars[numberOfTasks + a].getLB()) {
                        if (updated_Est_Lct_And_dLcts[1][a] < vars[twiceTheNumberOfTasks + a].getUB()) {
                            //  int u = vars[a].getUB() - (Tasks_updated_at_each_iteration[a].latestCompletionTime() - updated_Est_Lct_And_dLcts[1][a]);
                            int u = updated_Est_Lct_And_dLcts[1][a];
// if (debugFlag)                  
                            //    if (z2>=900||z1==750)                
                            // System.out.println("A filtering occurs here up.");
                            // if (z2==937||z1==787)

//            if (z2>=458||z1>=401) {
//                         if (NCumulativeConstraintsPosted)
//                    System.out.println("cumulativeConstraintCounter = " + cumulativeConstraintCounter);
//                                                 
//                                                              System.out.println("vars");
//
//            if (z1 >= 14265 || z2 >= 14569) 
//        for (int t = 0; t < numberOfTasks; t++) {
//           //   System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
//       }
                            // System.out.println("in upper bound");
//                                                          }
//                     if (detectedInImprovingDetection && !detectedInEF)
//                     { 
                            // if (z1 >= 191570 || z2 >= 195510)             
                            //  if (z1 >= 182680 || z2 >= 187230)             
//for (int j = 0; j < numberOfTasks; j++) {
                            //  System.out.println("j - > " + j + " " + Tasks_updated_at_each_iteration[j]);
                            //  }
//                                            System.out.println("interesting"); 
//  }
                            //if (z1 == 37)
                            // System.out.println("here");
                            //      if (NCumulativeConstraintsPosted)
                            //   System.out.println("cumulativeConstraintCounter = " + cumulativeConstraintCounter);
                            //  if (z2>=458||z1>=401) 
                            //  if (detectedInImprovingDetection && !detectedInEF)
                            //    if (z1 >= 191570 || z2 >= 195510)             
                            //   if (z1 >= 182680 || z2 >= 187230)             
                            //     System.out.println("we filter the upper bound of task " + a + " from " + Tasks_updated_at_each_iteration[a].latestCompletionTime() + " to " + updated_Est_Lct_And_dLcts[1][a]);
                            upperBoundOfEndingTimes[a] = u;
                            //   upperBoundOfStartingTimes[a] = Math.min(upperBoundOfStartingTimes[a], upperBoundOfEndingTimes[a]-lowerBoundOfProcessingTime[a]);
                            temp2 = upperBoundOfEndingTimes[a] - lowerBoundOfProcessingTime[a];
                            if (temp2 < upperBoundOfStartingTimes[a]) {
                                upperBoundOfStartingTimes[a] = temp2;
                            }

                            if (checkVariableBounds()) {
//System.out.println("ui");    
                                vars[a].updateUpperBound(upperBoundOfStartingTimes[a], this);
                                //  vars[a].updateUpperBound(u, this);
                                //vars[twiceTheNumberOfTasks + a].updateLowerBound(lowerBoundOfEndingTimes[a],this);
                                vars[twiceTheNumberOfTasks + a].updateUpperBound(upperBoundOfEndingTimes[a], this);
                                //vars[twiceTheNumberOfTasks + a].updateUpperBound(Math.min(vars[twiceTheNumberOfTasks + a].getUB(), vars[a].getUB() + vars[numberOfTasks + a].getLB()), this);
                                final int new_lct = Math.min(vars[a].getUB(), vars[twiceTheNumberOfTasks + a].getUB() - vars[numberOfTasks + a].getLB()) + vars[numberOfTasks + a].getLB();
                                final int delayed_lct2 = new_lct + delays[a];
                                Tasks_updated_at_each_iteration[a].setLatestCompletionTime(new_lct);
                                Tasks_updated_at_each_iteration[a].setDelayedLatestCompletionTime(delayed_lct2);
                                updated_Est_Lct_And_dLcts[1][a] = new_lct;
//numberOfChangedLctsThroughFiltering++;
                                filtered = true;
                                filteredUpperBound = true;
                                //System.out.println("filtered in up");
                                //    aLctWasJustUpdatedOnBranching = false;   
                                //   aLctWasJustUpdatedOnFiltering = true;
                            }
                        }
                        //      else
                        //    aLctWasJustUpdatedOnFiltering = false;

                        // System.out.println();
                    }
                    // if (!filteredUpperBound)
                    //numberOfChangedLctsThroughFiltering = 0;
                } while (filteredUpperBound);
                if (!filteredUpperBound) {
                    filtered = false;
                }
            }
            //if (!filterUpperBound)
            //  aLctWasJustUpdatedOnFiltering = false;
        } while (filtered);

        //  System.out.println("Tasks after filtering");
        // for (int j = 0; j < numberOfTasks; j++)
        //System.out.println("j: -> " + j + Tasks_updated_at_each_iteration[j]);
        /*
        System.out.println("domains after filtering");
        System.out.println("starting times");
        for (int t = 0; t < numberOfTasks; t++) {
        System.out.println("[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
        }
        System.out.println("processing times");
        for (int t = numberOfTasks; t < twiceTheNumberOfTasks; t++) {
        System.out.println("[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
        }
        System.out.println("ending times");
        for (int t = twiceTheNumberOfTasks; t < 3 * numberOfTasks; t++) {
        System.out.println("[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
        }
        System.out.println("heights");
        for (int t = 3 * numberOfTasks; t < 4 * numberOfTasks; t++) {
        System.out.println("[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
        }
        System.out.println("capacity");
        System.out.println("[" + vars[4 * numberOfTasks].getLB() + ", " + vars[4 * numberOfTasks].getUB() +"]");
        System.out.println("makespan");
        System.out.println("[" + vars[4 * numberOfTasks + 1].getLB() + ", " + vars[4 * numberOfTasks + 1].getUB() +"]");
         */
        // System.out.println();  
        //   System.out.println("mlb" + vars[lengthOfVariables - 1].getLB());
        // System.out.println("a" + (vars[lengthOfVariables - 1].getLB() + resourceHeight - 1) / resourceHeight);
        ///  int g; 
        //   System.out.println();
        // System.out.println("makespan bound now is ");
        //  System.out.println("[" + vars[4 * numberOfTasks + 1].getLB() + ", " + vars[4 * numberOfTasks + 1].getUB() +"]");
        if (filterMakespan) {
            IntVar makespan = vars[lengthOfVariables - 1];
            currentMakespanBound = (root + resourceHeight - 1) / resourceHeight;
            //int a = (vars[lengthOfVariables - 1].getLB() + resourceHeight - 1) / resourceHeight;
            //  System.out.println("a = " + a);
            //      System.out.println("makespan before filtering = " + makespan.getLB());
            //int a =  (root + resourceHeight - 1) / resourceHeight;
            if (currentMakespanBound > makespan.getLB()) {
                // if (root > makespan.getLB()) {
                //    System.out.print("makepan lb was " + makespan.getLB());
                makespan.updateLowerBound(currentMakespanBound, this);
                // makespan.updateLowerBound(root, this);
                //   System.out.print(", we filtered it up to " + currentMakespanBound + "\n");
                //System.out.println("something happens here");
            }
        }
    }

    @Override
    public ESat isEntailed() {
        return ESat.UNDEFINED;
    }

    public void detectionOfLowerBounds() throws ContradictionException {

        for (int h = 0; h < numberOfAllDistinctCapacities; h++) {
            if (thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[h]) {
                thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[h] = false;
            }
        }
        foundPrecedenceInPrecArray = false;
        for (int q = 0; q < numberOfTasks; q++) {
            task_index_to_node_index_for_est[indices_of_tasks_sorted_by_est_P[q]] = q;
            if (FalseStatePrecedencesForLowerBoundNew[q] != MINUS_INFINITY) {
                FalseStatePrecedencesForLowerBoundNew[q] = MINUS_INFINITY;
            }
            if (TrueStatePrecedencesForLowerBoundNew[q] != MINUS_INFINITY) {
                TrueStatePrecedencesForLowerBoundNew[q] = MINUS_INFINITY;
            }
        }
        if (activateImprovingDetectionLowerBound) {
            int iteratorOverValuesOfSortedAndMergedLctAndDelayedLctArray = 0;
            int duplicateDetector;
            int counterOnEctArray = 0;
            int counterOnDelayedEctArray = 0;
            // int ff = VectorTOfLctAndDelayedLcts.elementAt(0);
            int ff = VectorTOfLctAndDelayedLcts.get(0);

            while (counterOnEctArray < numberOfTasks && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_ect_P[counterOnEctArray]].earliestCompletionTime() < ff) {
                counterOnEctArray++;
            }
            while (counterOnDelayedEctArray < numberOfTasks
                    && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_ect_P[counterOnDelayedEctArray]].delayedEarliestCompletionTime() < ff) {
                counterOnDelayedEctArray++;
            }
            int x = 0;
            while (x < sizeOfVectorTOfLctAndDelayedLcts) {
                if (x < sizeOfVectorTOfLctAndDelayedLcts - 1) {
                    duplicateDetector = 0;
                    do {
                        iteratorOverValuesOfSortedAndMergedLctAndDelayedLctArray++;
                        duplicateDetector++;
                    } //     while (valuesOfSortedAndMergedLctAndDelayedLctArray[iteratorOverValuesOfSortedAndMergedLctAndDelayedLctArray] == VectorTOfLctAndDelayedLcts.elementAt(x));
                    while (valuesOfSortedAndMergedLctAndDelayedLctArray[iteratorOverValuesOfSortedAndMergedLctAndDelayedLctArray] == VectorTOfLctAndDelayedLcts.get(x));

                    //  ff = VectorTOfLctAndDelayedLcts.elementAt(x + 1);
                    ff = VectorTOfLctAndDelayedLcts.get(x + 1);

                    while (counterOnEctArray < numberOfTasks
                            && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_ect_P[counterOnEctArray]].earliestCompletionTime() < ff) {
                        if ((duplicateDetector == 1 && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_ect_P[counterOnEctArray]].latestCompletionTime() == VectorTOfLctAndDelayedLcts.get(x))) {
                            counterOnEctArray++;
                            continue;
                        }
                        FalseStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_ect_P[counterOnEctArray]] = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_ect_P[counterOnEctArray]].earliestCompletionTime();
                        // FalseStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_ect_P[counterOnEctArray]] = VectorTOfLctAndDelayedLcts.elementAt(x);
                        thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indices_of_tasks_sorted_by_ect_P[counterOnEctArray]]] = true;
                        foundPrecedenceInPrecArray = true;
                        counterOnEctArray++;
                    }
                    while (counterOnDelayedEctArray < numberOfTasks && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_ect_P[counterOnDelayedEctArray]].delayedEarliestCompletionTime() < ff) {
                        if ((duplicateDetector == 1 && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_ect_P[counterOnDelayedEctArray]].delayedLatestCompletionTime() == VectorTOfLctAndDelayedLcts.get(x))) {
                            counterOnDelayedEctArray++;
                            continue;
                        }
                        TrueStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_delayed_ect_P[counterOnDelayedEctArray]] = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_ect_P[counterOnDelayedEctArray]].delayedEarliestCompletionTime();
                        // TrueStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_delayed_ect_P[counterOnDelayedEctArray]] = VectorTOfLctAndDelayedLcts.elementAt(x);
                        thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indices_of_tasks_sorted_by_delayed_ect_P[counterOnDelayedEctArray]]] = true;
                        foundPrecedenceInPrecArray = true;
                        counterOnDelayedEctArray++;
                    }
                }
                x++;
            }
        }
        root = TLT_P.initializethetaLambdaTreeForLowerBounds(delayNumber);
        int counterForLct = numberOfTasksMinusOne;
        int counterForDelayedLct = counterForLct;
        int indexOfThisTasksAmongOrdinaryTasks;
        for (int t = sizeOfVectorTOfLctAndDelayedLcts - 1; t >= 0; t--) {
            int thisElementOfVectorT = VectorTOfLctAndDelayedLcts.get(t);
            int indexOfThisTaskAsALeafInTheTree;
            if (TLT_P.requiredEnvelopeInTheRoot(delayNumber) > resourceHeight * thisElementOfVectorT) {
                //  System.out.print("fail in EF\n");
                contradiction(null, "No");
            }
            while (TLT_P.lambdaEnvelopeInTheRoot(delayNumber) > resourceHeight * thisElementOfVectorT) {
                NewArrayOfPrecedences informationOfResponsibleLeafInTheTree = TLT_P.responsibleTaskForLowerBound(delayNumber, NCumulativeConstraintsPosted);
                int indexOfResponsibleLeafAmongTheTasks = TLT_P.indexOfResponsibleLeafAmongTheTasks((informationOfResponsibleLeafInTheTree.whatIsTValue()));
                boolean test = Tasks_updated_at_each_iteration[indexOfResponsibleLeafAmongTheTasks].earliestStartingTime() < thisElementOfVectorT;
                if (Tasks_updated_at_each_iteration[indexOfResponsibleLeafAmongTheTasks].allowedDelay() == 0) {
                    if (test) {
                        if (thisElementOfVectorT > FalseStatePrecedencesForLowerBoundNew[indexOfResponsibleLeafAmongTheTasks]) {
                            FalseStatePrecedencesForLowerBoundNew[indexOfResponsibleLeafAmongTheTasks] = thisElementOfVectorT;
                            foundPrecedenceInPrecArray = true;
                            thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indexOfResponsibleLeafAmongTheTasks]] = true;
                        }
                    }
                    TLT_P.unScheduleTaskFromLambdaDependingOnSituation(informationOfResponsibleLeafInTheTree.whatIsTValue(), true, true);
                } else if (informationOfResponsibleLeafInTheTree.whatIsDelayStatus()) {
                    if (test) {
                        // if (thisElementOfVectorT > TrueStatePrecedencesForLowerBoundNew[indexOfResponsibleLeafAmongTheTasks]){
                        TrueStatePrecedencesForLowerBoundNew[indexOfResponsibleLeafAmongTheTasks] = thisElementOfVectorT;
                        foundPrecedenceInPrecArray = true;
                        thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indexOfResponsibleLeafAmongTheTasks]] = true;
                        // }
                    }
                    TLT_P.unScheduleTaskFromLambdaDependingOnSituation(informationOfResponsibleLeafInTheTree.whatIsTValue(), false, true);
                } else {
                    if (test) {
                        if (thisElementOfVectorT > FalseStatePrecedencesForLowerBoundNew[indexOfResponsibleLeafAmongTheTasks]) {
                            FalseStatePrecedencesForLowerBoundNew[indexOfResponsibleLeafAmongTheTasks] = thisElementOfVectorT;
                            foundPrecedenceInPrecArray = true;
                            thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indexOfResponsibleLeafAmongTheTasks]] = true;
                        }
                    }
                    TLT_P.unScheduleTaskFromLambdaDependingOnSituation(informationOfResponsibleLeafInTheTree.whatIsTValue(), true, false);
                }
            }
            if (t > 0) {
                while (counterForDelayedLct >= 0 && thisElementOfVectorT == Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_lct_P[counterForDelayedLct]].delayedLatestCompletionTime()) {
                    indexOfThisTaskAsALeafInTheTree = task_index_to_node_index_for_est[indices_of_tasks_sorted_by_delayed_lct_P[counterForDelayedLct]];
                    indexOfThisTasksAmongOrdinaryTasks = indices_of_tasks_sorted_by_delayed_lct_P[counterForDelayedLct];
                    TLT_P.undelayFromThetaAndDelayInLambdaOrMoveToLambda(indexOfThisTaskAsALeafInTheTree, indexOfThisTasksAmongOrdinaryTasks, true, false);
                    counterForDelayedLct--;
                }
                while (counterForLct >= 0 && thisElementOfVectorT == Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_P[counterForLct]].latestCompletionTime()) {
                    indexOfThisTasksAmongOrdinaryTasks = indices_of_tasks_sorted_by_lct_P[counterForLct];
                    indexOfThisTaskAsALeafInTheTree = task_index_to_node_index_for_est[indexOfThisTasksAmongOrdinaryTasks];
                    TLT_P.undelayFromThetaAndDelayInLambdaOrMoveToLambda(indexOfThisTaskAsALeafInTheTree, indexOfThisTasksAmongOrdinaryTasks, false, true);
                    counterForLct--;
                }
            }
        }
        if (foundPrecedenceInPrecArray) {
            adjustmentOfLowerBoundsNewCode();
        }
    }

    // Complexity: (|C| * n^2 * k^2 * log n)     
    public void adjustmentOfLowerBoundsNewCode() throws ContradictionException {

        for (int o = 0; o < numberOfTasks; o++) {
            indices_of_tasks_sorted_by_false_state_precArray[o] = o;
            indices_of_tasks_sorted_by_true_state_precArray[o] = o;
        }
        int numberOfCapacitiesToProcess = 0;
        for (int t = 0; t < numberOfAllDistinctCapacities; t++) {
            if (thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[t]) {
                numberOfCapacitiesToProcess++;
            }
        }
        this.indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed = new int[numberOfCapacitiesToProcess];
        this.thisCapacityAmongAllDistinctCapacitiesExistsAtThisIndexOfTheArrayOfAllCapacitiesRequiredToBeProcessed = new int[numberOfAllDistinctCapacities];
        for (int o = 0; o < numberOfAllDistinctCapacities; o++) {
            thisCapacityAmongAllDistinctCapacitiesExistsAtThisIndexOfTheArrayOfAllCapacitiesRequiredToBeProcessed[o] = -1;
        }
        int m = 0;
        for (int s = 0; s < numberOfAllDistinctCapacities; s++) {
            if (thisCorrespondingCapacityShouldBeProceededForAdjustmentLowerBound[s]) {
                indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[m] = s;
                thisCapacityAmongAllDistinctCapacitiesExistsAtThisIndexOfTheArrayOfAllCapacitiesRequiredToBeProcessed[s] = m;
                m++;
            }
        }
        sortIndicesWithJavaLibrary(indices_of_tasks_sorted_by_false_state_precArray, new NewArrayOfPrecedences.ComparatorByTValueNew(FalseStatePrecedencesForLowerBoundNew));
        sortIndicesWithJavaLibrary(indices_of_tasks_sorted_by_true_state_precArray, new NewArrayOfPrecedences.ComparatorByTValueNew(TrueStatePrecedencesForLowerBoundNew));
        int iteratorOverTwoDimensionalPrecArrayForFalse = 0;
        int iteratorOverTwoDimensionalPrecArrayForTrue = 0;
        while (iteratorOverTwoDimensionalPrecArrayForFalse < numberOfTasks && FalseStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_false_state_precArray[iteratorOverTwoDimensionalPrecArrayForFalse]] == MINUS_INFINITY) {
            iteratorOverTwoDimensionalPrecArrayForFalse++;
        }
        while (iteratorOverTwoDimensionalPrecArrayForTrue < numberOfTasks && TrueStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_true_state_precArray[iteratorOverTwoDimensionalPrecArrayForTrue]] == MINUS_INFINITY) {
            iteratorOverTwoDimensionalPrecArrayForTrue++;
        }
        this.currentEnvelopeForFalseState_new_approach = new int[numberOfCapacitiesToProcess];
        this.currentEnvelopeForTrueState_new_approach = new int[numberOfCapacitiesToProcess];
        this.calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach = new int[numberOfCapacitiesToProcess];
        this.calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach = new int[numberOfCapacitiesToProcess];
        this.updateForFalse_new_approach = new int[numberOfCapacitiesToProcess];
        this.updateForTrue_new_approach = new int[numberOfCapacitiesToProcess];
        this.temp_updateForFalse_new_approach = new int[numberOfCapacitiesToProcess];
        this.temp_updateForTrue_new_approach = new int[numberOfCapacitiesToProcess];
        for (int y = 0; y < numberOfCapacitiesToProcess; y++) {
            currentEnvelopeForFalseState_new_approach[y] = MINUS_INFINITY;
            currentEnvelopeForTrueState_new_approach[y] = MINUS_INFINITY;
            calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[y] = MINUS_INFINITY;
            calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[y] = MINUS_INFINITY;
            updateForFalse_new_approach[y] = MINUS_INFINITY;
            updateForTrue_new_approach[y] = MINUS_INFINITY;
            temp_updateForFalse_new_approach[y] = MINUS_INFINITY;
            temp_updateForTrue_new_approach[y] = MINUS_INFINITY;
        }
        this.counterOnSortedAndMergedLctAndDelayedLctArray = 0;
        int current_TValueAtThisIndex;
        int lastTvalueMaintained = MINUS_INFINITY;
        TT_P.initializeThetaTreeNewApproach(Tasks_updated_at_each_iteration, numberOfCapacitiesToProcess);
        int indexOfTheTaskBeingProcessedInTheOriginalArray;
        this.processingPrecedencesOfFalseCoulmnIsOver = (iteratorOverTwoDimensionalPrecArrayForFalse == numberOfTasks);
        this.processingPrecedencesOfTrueCoulmnIsOver = (iteratorOverTwoDimensionalPrecArrayForTrue == numberOfTasks);
        while (iteratorOverTwoDimensionalPrecArrayForFalse < numberOfTasks && iteratorOverTwoDimensionalPrecArrayForTrue < numberOfTasks) {
            if (FalseStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_false_state_precArray[iteratorOverTwoDimensionalPrecArrayForFalse]] <= TrueStatePrecedencesForLowerBoundNew[indices_of_tasks_sorted_by_true_state_precArray[iteratorOverTwoDimensionalPrecArrayForTrue]]) {
                indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_false_state_precArray[iteratorOverTwoDimensionalPrecArrayForFalse];
                current_TValueAtThisIndex = FalseStatePrecedencesForLowerBoundNew[indexOfTheTaskBeingProcessedInTheOriginalArray];
                if (NCumulativeConstraintsPosted) {
                    scheduleAllRegularOrDelayedTasksTerminatingNoLaterThanTValueAtThisIndex(current_TValueAtThisIndex, lastTvalueMaintained, indexOfTheTaskBeingProcessedInTheOriginalArray, 1, numberOfCapacitiesToProcess);
                } else {
                    scheduleAllRegularOrDelayedTasksTerminatingNoLaterThanTValueAtThisIndex(current_TValueAtThisIndex, lastTvalueMaintained, indexOfTheTaskBeingProcessedInTheOriginalArray, 0, numberOfCapacitiesToProcess);
                }
                lastTvalueMaintained = current_TValueAtThisIndex;
                iteratorOverTwoDimensionalPrecArrayForFalse++;
                if (iteratorOverTwoDimensionalPrecArrayForFalse == numberOfTasks) {
                    processingPrecedencesOfFalseCoulmnIsOver = true;
                }
            } else {
                indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_true_state_precArray[iteratorOverTwoDimensionalPrecArrayForTrue];
                current_TValueAtThisIndex = TrueStatePrecedencesForLowerBoundNew[indexOfTheTaskBeingProcessedInTheOriginalArray];
                scheduleAllRegularOrDelayedTasksTerminatingNoLaterThanTValueAtThisIndex(current_TValueAtThisIndex, lastTvalueMaintained, indexOfTheTaskBeingProcessedInTheOriginalArray, 1, numberOfCapacitiesToProcess);
                lastTvalueMaintained = current_TValueAtThisIndex;
                iteratorOverTwoDimensionalPrecArrayForTrue++;
                if (iteratorOverTwoDimensionalPrecArrayForTrue == numberOfTasks) {
                    processingPrecedencesOfTrueCoulmnIsOver = true;
                }
            }
        }
        while (iteratorOverTwoDimensionalPrecArrayForFalse < numberOfTasks) {
            indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_false_state_precArray[iteratorOverTwoDimensionalPrecArrayForFalse];
            current_TValueAtThisIndex = FalseStatePrecedencesForLowerBoundNew[indexOfTheTaskBeingProcessedInTheOriginalArray];
            if (NCumulativeConstraintsPosted) {
                scheduleAllRegularOrDelayedTasksTerminatingNoLaterThanTValueAtThisIndex(current_TValueAtThisIndex, lastTvalueMaintained, indexOfTheTaskBeingProcessedInTheOriginalArray, 1, numberOfCapacitiesToProcess);
            } else {
                scheduleAllRegularOrDelayedTasksTerminatingNoLaterThanTValueAtThisIndex(current_TValueAtThisIndex, lastTvalueMaintained, indexOfTheTaskBeingProcessedInTheOriginalArray, 0, numberOfCapacitiesToProcess);
            }
            lastTvalueMaintained = current_TValueAtThisIndex;
            iteratorOverTwoDimensionalPrecArrayForFalse++;
            if (iteratorOverTwoDimensionalPrecArrayForFalse == numberOfTasks) {
                processingPrecedencesOfFalseCoulmnIsOver = true;
            }
        }
        while (iteratorOverTwoDimensionalPrecArrayForTrue < numberOfTasks) {
            indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_true_state_precArray[iteratorOverTwoDimensionalPrecArrayForTrue];
            current_TValueAtThisIndex = TrueStatePrecedencesForLowerBoundNew[indexOfTheTaskBeingProcessedInTheOriginalArray];
            // TODO: This function could be called n times and the complexity is at least k^2 n log n.
            if (iteratorOverTwoDimensionalPrecArrayForTrue + 1 == numberOfTasks) {
                processingPrecedencesOfTrueCoulmnIsOver = true;
            }
            scheduleAllRegularOrDelayedTasksTerminatingNoLaterThanTValueAtThisIndex(current_TValueAtThisIndex, lastTvalueMaintained, indexOfTheTaskBeingProcessedInTheOriginalArray, 1, numberOfCapacitiesToProcess);
            lastTvalueMaintained = current_TValueAtThisIndex;
            iteratorOverTwoDimensionalPrecArrayForTrue++;
        }
    }

    public void scheduleAllRegularOrDelayedTasksTerminatingNoLaterThanTValueAtThisIndex(int current_TValueAtThisIndex, int lastTvalueMaintained, int indexOfTheTaskBeingProcessedInTheOriginalArray, int delayedOrNotDimension, int numberOfCapacitiesToProcess) {
        boolean stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray;

//        if (delayedOrNotDimension == 0)
//            stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray = false;
//        else
//            stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray = true;
//        
        stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray = decideStatus(delayedOrNotDimension);

        boolean iteratedThroughTheLoop = false;
        boolean aRegularTaskWasUnScheduledAtFirst = false;
        boolean aDelayedTaskWasUnScheduledAtFirst = false;
        this.temp = new int[numberOfCapacitiesToProcess];
        if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].delayedLatestCompletionTime() <= lastTvalueMaintained
                || Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].latestCompletionTime() <= lastTvalueMaintained) {
            for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                TT_P.scheduleWithC(task_index_to_node_index_for_est[indexOfTheTaskBeingProcessedInTheOriginalArray], indexOfTheTaskBeingProcessedInTheOriginalArray, allDistinctCapacities[e], false, true, e, numberOfCapacitiesToProcess);
            }
            if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].latestCompletionTime() <= lastTvalueMaintained
                    && Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].delayedLatestCompletionTime() > lastTvalueMaintained) {
                aRegularTaskWasUnScheduledAtFirst = true;
            } else {
                aDelayedTaskWasUnScheduledAtFirst = true;
            }
        }
        int indexOfTheTaskSkeptForBeingScheduled = -1;
        while (counterOnSortedAndMergedLctAndDelayedLctArray < twiceTheNumberOfTasks
                && valuesOfSortedAndMergedLctAndDelayedLctArray[counterOnSortedAndMergedLctAndDelayedLctArray] <= current_TValueAtThisIndex) {
            iteratedThroughTheLoop = true;
            currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray = valuesOfSortedAndMergedLctAndDelayedLctArray[counterOnSortedAndMergedLctAndDelayedLctArray];
            indexOfThisTaskInTheOriginalArrayConsiredingTvaluesArray = sortedAndMergedLctAndDelayedLctArrayTwoDimensional[counterOnSortedAndMergedLctAndDelayedLctArray].whatIsTValue();
            stateOfThisTaskInTheOriginalArrayConsiredingTvaluesArray = sortedAndMergedLctAndDelayedLctArrayTwoDimensional[counterOnSortedAndMergedLctAndDelayedLctArray].whatIsDelayStatus();
            boolean thisValueBelongsToADelayedCase;
            if (indexOfThisTaskInTheOriginalArrayConsiredingTvaluesArray
                    != indexOfTheTaskBeingProcessedInTheOriginalArray) {
                if (!stateOfThisTaskInTheOriginalArrayConsiredingTvaluesArray) {
                    thisValueBelongsToADelayedCase = false;
                    for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                        takeCareOfNecessarySchedulings(TT_P, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], thisValueBelongsToADelayedCase, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, indexOfThisTaskInTheOriginalArrayConsiredingTvaluesArray, e, numberOfCapacitiesToProcess);
                    }
                    if (counterOnSortedAndMergedLctAndDelayedLctArray <= twiceTheNumberOfTasks
                            && valuesOfSortedAndMergedLctAndDelayedLctArray[counterOnSortedAndMergedLctAndDelayedLctArray]
                            != valuesOfSortedAndMergedLctAndDelayedLctArray[counterOnSortedAndMergedLctAndDelayedLctArray + 1]) {
                        if (!stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
                            takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        } else {
                            takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        }
                    }
                } else {
                    if (!(processingPrecedencesOfFalseCoulmnIsOver && delayNumber == 1)) {
                        thisValueBelongsToADelayedCase = true;
                        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                            takeCareOfNecessarySchedulings(TT_P, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], thisValueBelongsToADelayedCase, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, indexOfThisTaskInTheOriginalArrayConsiredingTvaluesArray, e, numberOfCapacitiesToProcess);
                        }
                    }
                    if (counterOnSortedAndMergedLctAndDelayedLctArray <= twiceTheNumberOfTasks
                            && valuesOfSortedAndMergedLctAndDelayedLctArray[counterOnSortedAndMergedLctAndDelayedLctArray]
                            != valuesOfSortedAndMergedLctAndDelayedLctArray[counterOnSortedAndMergedLctAndDelayedLctArray + 1]) {
                        if (!stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
                            takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        } else {
                            takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        }
                    }
                }
            } else {
                indexOfTheTaskSkeptForBeingScheduled = indexOfThisTaskInTheOriginalArrayConsiredingTvaluesArray;
            }
            counterOnSortedAndMergedLctAndDelayedLctArray++;
        }
        if (aRegularTaskWasUnScheduledAtFirst || aDelayedTaskWasUnScheduledAtFirst) {
            takeCareOfNecessaryUpdatingOnceThereIsSomeUnscheduledTask(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, true);
        } else if (!iteratedThroughTheLoop) {
            takeCareOfSomeNecessarryUpdatingOnceTheLoopIsNotIterated(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray);
        }
        int locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed = thisCapacityAmongAllDistinctCapacitiesExistsAtThisIndexOfTheArrayOfAllCapacitiesRequiredToBeProcessed[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indexOfTheTaskBeingProcessedInTheOriginalArray]];
        if (!stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
            if ((aRegularTaskWasUnScheduledAtFirst || aDelayedTaskWasUnScheduledAtFirst)) {
                if (locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed != -1
                        && currentEnvelopeForFalseState_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime()
                        && currentEnvelopeForFalseState_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray]) {
                    updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray] = updateForFalse_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed];
                }
            } else if (locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed != -1
                    && updateForFalse_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime()
                    && updateForFalse_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray]) {
                updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray] = updateForFalse_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed];
            }
        } else {
            if ((aRegularTaskWasUnScheduledAtFirst || aDelayedTaskWasUnScheduledAtFirst)) {
                if (locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed != -1
                        && currentEnvelopeForTrueState_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime()
                        && currentEnvelopeForTrueState_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray]) {
                    updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray] = updateForTrue_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed];
                }
            } else if (locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed != -1
                    && updateForTrue_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime()
                    && updateForTrue_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed] > updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray]) {
                updated_Est_Lct_And_dLcts[0][indexOfTheTaskBeingProcessedInTheOriginalArray] = updateForTrue_new_approach[locationIndexOfTheCapacityOfThisTaskInTheArrayOfAllCapacitiesRequiredToBeProcessed];
            }
        }
        if (!(processingPrecedencesOfFalseCoulmnIsOver && processingPrecedencesOfTrueCoulmnIsOver)
                && iteratedThroughTheLoop) {
            if (indexOfTheTaskSkeptForBeingScheduled == indexOfTheTaskBeingProcessedInTheOriginalArray) {
                if ((counterOnSortedAndMergedLctAndDelayedLctArray < twiceTheNumberOfTasks
                        && valuesOfSortedAndMergedLctAndDelayedLctArray[counterOnSortedAndMergedLctAndDelayedLctArray] > current_TValueAtThisIndex)) {
                    if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].delayedLatestCompletionTime() <= current_TValueAtThisIndex) {
                        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                            takeCareOfNecessarySchedulings(TT_P, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], true, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, indexOfTheTaskSkeptForBeingScheduled, e, numberOfCapacitiesToProcess);
                        }
                    } else if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].latestCompletionTime() <= current_TValueAtThisIndex) {
                        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                            takeCareOfNecessarySchedulings(TT_P, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], false, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, indexOfTheTaskSkeptForBeingScheduled, e, numberOfCapacitiesToProcess);
                        }
                    }
                    if (!stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
                        if (!(aRegularTaskWasUnScheduledAtFirst || aDelayedTaskWasUnScheduledAtFirst)) {
                            takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        } else {
                            takeCareOfNecessaryUpdatingOnceThereIsSomeUnscheduledTask(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        }
                    } else {
                        if (!(aRegularTaskWasUnScheduledAtFirst || aDelayedTaskWasUnScheduledAtFirst)) {
                            takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        } else {
                            takeCareOfNecessaryUpdatingOnceThereIsSomeUnscheduledTask(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, false);
                        }
                    }
                } else {
                    int indexOfJustProcessedTaskInTheOriginalArrayConsiredingTvaluesArray = indexOfThisTaskInTheOriginalArrayConsiredingTvaluesArray;
                    boolean stateOfJustProcessedTaskInTheOriginalArrayConsiredingTvaluesArray = stateOfThisTaskInTheOriginalArrayConsiredingTvaluesArray;
                    for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                        takeCareOfNecessarySchedulings(TT_P, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], stateOfJustProcessedTaskInTheOriginalArrayConsiredingTvaluesArray, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, indexOfJustProcessedTaskInTheOriginalArrayConsiredingTvaluesArray, e, numberOfCapacitiesToProcess);
                    }
                }
            }
        }
        if (!(processingPrecedencesOfFalseCoulmnIsOver && processingPrecedencesOfTrueCoulmnIsOver)
                && (aRegularTaskWasUnScheduledAtFirst || aDelayedTaskWasUnScheduledAtFirst)) {
            if (aRegularTaskWasUnScheduledAtFirst && !aDelayedTaskWasUnScheduledAtFirst) {
                for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                    TT_P.scheduleWithC(task_index_to_node_index_for_est[indexOfTheTaskBeingProcessedInTheOriginalArray], indexOfTheTaskBeingProcessedInTheOriginalArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], false, false, e, numberOfCapacitiesToProcess);
                }
            } else {
                for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                    TT_P.scheduleWithC(task_index_to_node_index_for_est[indexOfTheTaskBeingProcessedInTheOriginalArray], indexOfTheTaskBeingProcessedInTheOriginalArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], true, false, e, numberOfCapacitiesToProcess);
                }
            }
            if (!stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
                takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, true);
            } else {
                takeCareOfUpdating(numberOfCapacitiesToProcess, stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, true);
            }
        }
    }

    public void detectionOfUpperBounds() throws ContradictionException {

        //  detectedInImprovingDetection = false;
        //    detectedInEF = false;
        // for (int h = 0; h < lengthOfCapacityRange; h++) {
        //   thisCapacityShouldBeProceededForAdjustmentUpperbound[h] = false;
        //  }
        for (int h = 0; h < numberOfAllDistinctCapacities; h++) {
            if (thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound[h]) {
                thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound[h] = false;
            }
        }
        foundPrecedenceInPrecArray = false;
        for (int i = 0; i < numberOfTasks; i++) {
            if (FalseStatePrecedencesForUpperBound[i] != INFINITY) {
                FalseStatePrecedencesForUpperBound[i] = INFINITY;
            }
            if (TrueStatePrecedencesForUpperBound[i] != INFINITY) {
                TrueStatePrecedencesForUpperBound[i] = INFINITY;
            }
        }
//I DON'T THINK ONE CAN SIMPLY APPLY THE SAME IDEA OF IMPROVING DETECTION FOR LOWER BOUNDS TO THE UPPER BOUNDS AS WELL.
//BECAUSE ONCE ect_i >= lct_Omega one can deduce Omega < i, however once lst_i <= est_Omega one cannot necessarily deduce i < Omega.
//EVEN THE IDEA OF FILTERING FOR LCT_I < EST_OMEGA IS NONSENSE. BECAUSE IF SUCH A CASE ALREADY OCCURS, WHAT DO WE WANT TO FILTER DOWN 
//THE LCT_I TO?!

        TLT_P_upperbound.initializethetaLambdaTreeForUpperBounds(delayNumber);
        for (int k = 0; k < numberOfTasks; k++) {
            int indexOfThisTaskInTheOriginalArray = indices_of_tasks_sorted_by_est_P[k];
            if (!filterLowerBound && TLT_P_upperbound.requiredEnvelopeInTheRootUpperBound(delayNumber) < resourceHeight * Tasks_updated_at_each_iteration[indexOfThisTaskInTheOriginalArray].earliestStartingTime()) {
                contradiction(null, "No");
            }
            while (TLT_P_upperbound.lambdaEnvelopeInTheRootForUpperBound(delayNumber) < resourceHeight * Tasks_updated_at_each_iteration[indexOfThisTaskInTheOriginalArray].earliestStartingTime()) {
                NewArrayOfPrecedences A = TLT_P_upperbound.responsibleTaskForUpperBound(delayNumber, NCumulativeConstraintsPosted);
                int indexOfResponsibleLeafAmongTheTasks = A.whatIsTValue();
                int tempi = Tasks_updated_at_each_iteration[indexOfThisTaskInTheOriginalArray].earliestStartingTime();
                boolean test = (A.whatIsDelayStatus()
                        && Tasks_updated_at_each_iteration[indexOfResponsibleLeafAmongTheTasks].delayedLatestCompletionTime() > Tasks_updated_at_each_iteration[indexOfThisTaskInTheOriginalArray].earliestStartingTime())
                        || (!A.whatIsDelayStatus()
                        && Tasks_updated_at_each_iteration[indexOfResponsibleLeafAmongTheTasks].latestCompletionTime() > Tasks_updated_at_each_iteration[indexOfThisTaskInTheOriginalArray].earliestStartingTime());

                if (test) {
                    if (A.whatIsDelayStatus()
                            && Tasks_updated_at_each_iteration[indexOfResponsibleLeafAmongTheTasks].delayedLatestCompletionTime() > tempi) {
                        TrueStatePrecedencesForUpperBound[indexOfResponsibleLeafAmongTheTasks] = Tasks_updated_at_each_iteration[indexOfThisTaskInTheOriginalArray].earliestStartingTime();
                        thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indexOfResponsibleLeafAmongTheTasks]] = true;
                        foundPrecedenceInPrecArray = true;
                    } else if (Tasks_updated_at_each_iteration[indexOfResponsibleLeafAmongTheTasks].latestCompletionTime() > tempi) {
                        FalseStatePrecedencesForUpperBound[indexOfResponsibleLeafAmongTheTasks] = Tasks_updated_at_each_iteration[indexOfThisTaskInTheOriginalArray].earliestStartingTime();
                        thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound[theCapacityOfThisTaskExistsAtThisIndexOfTheVectorOfAllCapacities[indexOfResponsibleLeafAmongTheTasks]] = true;
                        foundPrecedenceInPrecArray = true;
                    }
                }
                if (Tasks_updated_at_each_iteration[indexOfResponsibleLeafAmongTheTasks].allowedDelay() == 0) {
                    TLT_P_upperbound.unScheduleDelayedOrUndelayedPortionFromLambdaForUpperbound(indexOfResponsibleLeafAmongTheTasks, true, true);
                } else if (A.whatIsDelayStatus()) {
                    TLT_P_upperbound.unScheduleDelayedOrUndelayedPortionFromLambdaForUpperbound(indexOfResponsibleLeafAmongTheTasks, false, true);
                } else {
                    TLT_P_upperbound.unScheduleDelayedOrUndelayedPortionFromLambdaForUpperbound(indexOfResponsibleLeafAmongTheTasks, true, false);
                }
            }
            if (k < numberOfTasksMinusOne) {
                TLT_P_upperbound.unScheduleFromThetaAndMoveToLambdaForUpperBound(indexOfThisTaskInTheOriginalArray);
            }
        }
        if (foundPrecedenceInPrecArray) {
            //   adjustmentOfUpperBounds();
            adjustmentOfUpperBoundsNewApproach();

        }
    }

    /* 
    public void adjustmentOfUpperBounds() {
        
        for (int o = 0; o < numberOfTasks; o++) {
            indices_of_tasks_sorted_by_false_state_precArray_upperbound[o] = o;
            indices_of_tasks_sorted_by_true_state_precArray_upperbound[o] = o;
        }
   
      sortIndicesWithJavaLibrary( indices_of_tasks_sorted_by_false_state_precArray_upperbound, new NewArrayOfPrecedences.ComparatorByTValueNew(FalseStatePrecedencesForUpperBound));
              sortIndicesWithJavaLibrary( indices_of_tasks_sorted_by_true_state_precArray_upperbound, new NewArrayOfPrecedences.ComparatorByTValueNew(TrueStatePrecedencesForUpperBound));
  int indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray = numberOfTasksMinusOne;
        int indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray = numberOfTasksMinusOne;
        while (indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray >= 0 && FalseStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_false_state_precArray_upperbound[indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray]] == INFINITY) {
            indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray--;
        }
        while (indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray >= 0 && TrueStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_true_state_precArray_upperbound[indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray]] == INFINITY) {
            indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray--;
        }

        int c;
        for (int u = 0; u < thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound.length; u++) {
            if (thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound[u]){
                                c = allDistinctCapacities[u];
                counterOnSortedEstArray = numberOfTasksMinusOne;
                TT_P_upperbound.initializeThetaTreeForUpperBound(Tasks_updated_at_each_iteration);
                this.processingPrecedencesOfFalseCoulmnIsOver_upperbound = false;
                this.processingPrecedencesOfTrueCoulmnIsOver_upperbound = false;
                this.indexOfLastTaskToBeProcessedForAdjustment_upperbound = 0;
                this.indexOfLastTaskToBeProcessedForAdjustment_upperbound = Math.min(indices_of_tasks_sorted_by_false_state_precArray_upperbound[0], indices_of_tasks_sorted_by_true_state_precArray_upperbound[0]);
                int indexOfTheTaskBeingProcessedInTheOriginalArray;
                int iteratorOverTwoDimensionalPrecArrayForFalse = indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray;
                int iteratorOverTwoDimensionalPrecArrayForTrue = indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray;
                if (iteratorOverTwoDimensionalPrecArrayForFalse == -1) {
                    processingPrecedencesOfFalseCoulmnIsOver_upperbound = true;
                }
                if (iteratorOverTwoDimensionalPrecArrayForTrue == -1) {
                    processingPrecedencesOfTrueCoulmnIsOver_upperbound = true;
                }
                while (iteratorOverTwoDimensionalPrecArrayForFalse >= 0 && iteratorOverTwoDimensionalPrecArrayForTrue >= 0) {
                    if (FalseStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse]]
                            >= TrueStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue]]) {
                        indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse];
                        if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].height() == c) {
                            scheduleEligibleTasksForFalseStateUpperbound( indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForFalse,
                                        c);
                        }
                        iteratorOverTwoDimensionalPrecArrayForFalse--;
                        if (iteratorOverTwoDimensionalPrecArrayForFalse == -1) {
                            processingPrecedencesOfFalseCoulmnIsOver_upperbound = true;
                        }
                    } else {
                        indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue];
                        if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].height() == c) {
                            scheduleEligibleTasksForTrueStateUpperbound(indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForTrue,
                                       c);
                        }
                        iteratorOverTwoDimensionalPrecArrayForTrue--;
                        if (iteratorOverTwoDimensionalPrecArrayForTrue == -1) {
                            processingPrecedencesOfTrueCoulmnIsOver_upperbound = true;
                        }
                    }
                }
                while (iteratorOverTwoDimensionalPrecArrayForFalse >= 0) {
                    indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse];
                    if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].height() == c) {
                        scheduleEligibleTasksForFalseStateUpperbound(indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForFalse,
                                    c);
                    }
                    iteratorOverTwoDimensionalPrecArrayForFalse--;
                    
                    if (iteratorOverTwoDimensionalPrecArrayForFalse == -1) {
                        processingPrecedencesOfFalseCoulmnIsOver_upperbound = true;
                    }
                }
                while (iteratorOverTwoDimensionalPrecArrayForTrue >= 0) {
                    indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue];
                    if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].height() == c) {
                        scheduleEligibleTasksForTrueStateUpperbound(indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForTrue,
                                    c);
                    }
                    iteratorOverTwoDimensionalPrecArrayForTrue--;
                    if (iteratorOverTwoDimensionalPrecArrayForTrue == -1) {
                        processingPrecedencesOfTrueCoulmnIsOver_upperbound = true;
                    }
                }
            }
        }
    }
   
    
        
        
    public void scheduleEligibleTasksForFalseStateUpperbound(int indexOfTheTaskBeingProcessedInTheOriginalArray,
            int iteratorOverTwoDimensionalPrecArrayForFalse,  int c
    ) {        
        int updForFalse = INFINITY;
        int estValueAtThisIndex = FalseStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse]];
        boolean stateOfDelay = false;
        if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex
                )
        {
            TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], c, true, true);
          //  thisTaskNotDelayedIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = false;
            TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], c, true, true);
         //   delayedPortionOfThisTaskIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = false;
        }
        while (counterOnSortedEstArray >= 0 && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray]].earliestStartingTime() >= estValueAtThisIndex) {
            int indexOfThisTaskInTheOriginalArray = indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray];
            boolean delayedPortion = false;
          //  if (!thisTaskNotDelayedIsInThetaUpperBound[indexOfThisTaskInTheOriginalArray]) {
                TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0]], c, delayedPortion, false);
              //  thisTaskNotDelayedIsInThetaUpperBound[indexOfThisTaskInTheOriginalArray] = true;
            //}
          //  else
              //  System.out.println("gf");
           // if (!delayedPortionOfThisTaskIsInThetaUpperBound[indexOfThisTaskInTheOriginalArray]) {
                TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0]], c, !delayedPortion, false);
            //    delayedPortionOfThisTaskIsInThetaUpperBound[indexOfThisTaskInTheOriginalArray] = true;
           // }
            //  else
               // System.out.println("gf");
          
            counterOnSortedEstArray--;
        }
        int calculatedEnvelope = calculatedEnvelope(Tasks_updated_at_each_iteration, TT_P_upperbound, c, estValueAtThisIndex, stateOfDelay, delayNumber, false);
        int numerator = calculatedEnvelope - (resourceHeight - c) * estValueAtThisIndex;
        int diff = diffForUpperbound(numerator, c);
    //    updForFalse = Math.min(updForFalse, diff);
       
 if (diff < updForFalse)
        updForFalse = diff;
  



// updateForUpperbound = Math.min(updateForUpperbound, diff);
   //    if (diff < updateForUpperbound)
     //   updateForUpperbound = diff;
        
        if (updForFalse >= Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestCompletionTime()
                && 
                updForFalse < Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].latestCompletionTime()) {
            updated_Est_Lct_And_dLcts[1][indexOfTheTaskBeingProcessedInTheOriginalArray] = updForFalse;
}
        if (indexOfTheTaskBeingProcessedInTheOriginalArray != indexOfLastTaskToBeProcessedForAdjustment_upperbound && !processingPrecedencesOfTrueCoulmnIsOver_upperbound) {
            if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex) {
                TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], c, false, false);
              //  thisTaskNotDelayedIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = true;
                TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], c, true, false);
              //  delayedPortionOfThisTaskIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = true;
            }
        }
    }
    
    public void scheduleEligibleTasksForTrueStateUpperbound(int indexOfTheTaskBeingProcessedInTheOriginalArray,
            int iteratorOverTwoDimensionalPrecArrayForTrue, int c
    ) {
        int updForTrue = INFINITY;
        int estValueAtThisIndex = TrueStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue]];
        boolean stateOfDelay = true;
        if (
                //thisTaskNotDelayedIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray]
               // || delayedPortionOfThisTaskIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray]
                
                        Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex

                
                
                
                
                ) {
            TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], c, true, true);
          //  thisTaskNotDelayedIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = false;
            TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1]], c, true, true);
           // delayedPortionOfThisTaskIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = false;
        }
        while (counterOnSortedEstArray >= 0 && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray]].earliestStartingTime() >= estValueAtThisIndex) {
            int indexOfThisTaskInTheOriginalArray = indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray];
            boolean delayedPortion = false;
            TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0]], c, delayedPortion, false);
            counterOnSortedEstArray--;
        }
        int calculatedEnvelope = calculatedEnvelope(Tasks_updated_at_each_iteration, TT_P_upperbound, c, estValueAtThisIndex, stateOfDelay, delayNumber, false);
        int numerator = calculatedEnvelope - (resourceHeight - c) * estValueAtThisIndex;
        int diff = diffForUpperbound(numerator, c);
       // updForTrue = Math.min(updForTrue, diff);
         if (diff < updForTrue)
        updForTrue = diff;

        if (updForTrue >=
                Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].delayedEarliestCompletionTime()
                && updForTrue < Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].delayedLatestCompletionTime()) {        
           updated_Est_Lct_And_dLcts[1][indexOfTheTaskBeingProcessedInTheOriginalArray] = updForTrue - Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].allowedDelay();
        }
        if (indexOfTheTaskBeingProcessedInTheOriginalArray != indexOfLastTaskToBeProcessedForAdjustment_upperbound && !processingPrecedencesOfFalseCoulmnIsOver_upperbound) {
            if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex) {
                TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], c, false, false);
              //  thisTaskNotDelayedIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = true;
                TT_P_upperbound.scheduleTaskWithCForUpperBound(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], c, true, false);
              //  delayedPortionOfThisTaskIsInThetaUpperBound[indexOfTheTaskBeingProcessedInTheOriginalArray] = true;
            }
        }
    }
     */
    public void adjustmentOfUpperBoundsNewApproach() {

        calculatedEnvelopeUpperBound = INFINITY;
        for (int o = 0; o < numberOfTasks; o++) {
            indices_of_tasks_sorted_by_false_state_precArray_upperbound[o] = o;
            indices_of_tasks_sorted_by_true_state_precArray_upperbound[o] = o;
        }
        int numberOfCapacitiesToProcess = 0;
        for (int t = 0; t < numberOfAllDistinctCapacities; t++) {
            if (thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound[t]) {
                numberOfCapacitiesToProcess++;
            }
        }
        this.indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound = new int[numberOfCapacitiesToProcess];
        int m = 0;
        for (int s = 0; s < numberOfAllDistinctCapacities; s++) {
            if (thisCorrespondingCapacityShouldBeProceededForAdjustmentUpperBound[s]) {
                indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[m] = s;
                m++;
            }
        }
        sortIndicesWithJavaLibrary(indices_of_tasks_sorted_by_false_state_precArray_upperbound, new NewArrayOfPrecedences.ComparatorByTValueNew(FalseStatePrecedencesForUpperBound));
        sortIndicesWithJavaLibrary(indices_of_tasks_sorted_by_true_state_precArray_upperbound, new NewArrayOfPrecedences.ComparatorByTValueNew(TrueStatePrecedencesForUpperBound));
        int indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray = numberOfTasksMinusOne;
        int indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray = numberOfTasksMinusOne;
        while (indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray >= 0 && FalseStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_false_state_precArray_upperbound[indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray]] == INFINITY) {
            indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray--;
        }
        while (indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray >= 0 && TrueStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_true_state_precArray_upperbound[indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray]] == INFINITY) {
            indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray--;
        }
        counterOnSortedEstArray = numberOfTasksMinusOne;
        TT_P_upperbound.initializeThetaTreeForUpperBoundNewApproach(Tasks_updated_at_each_iteration, numberOfCapacitiesToProcess);
        // this.processingPrecedencesOfFalseCoulmnIsOver_upperbound = false;
        //this.processingPrecedencesOfTrueCoulmnIsOver_upperbound = false;
        // this.indexOfLastTaskToBeProcessedForAdjustment_upperbound = Math.min(indices_of_tasks_sorted_by_false_state_precArray_upperbound[0], indices_of_tasks_sorted_by_true_state_precArray_upperbound[0]);
        int indexOfTheTaskBeingProcessedInTheOriginalArray;
        int iteratorOverTwoDimensionalPrecArrayForFalse = indexOfFirstElementOfIndicesOfTasksSortedByFalseStatePrecArray;
        int iteratorOverTwoDimensionalPrecArrayForTrue = indexOfFirstElementOfIndicesOfTasksSortedByTrueStatePrecArray;
//        if (iteratorOverTwoDimensionalPrecArrayForFalse == -1) {
//            processingPrecedencesOfFalseCoulmnIsOver_upperbound = true;
//        }
//        if (iteratorOverTwoDimensionalPrecArrayForTrue == -1) {
//            processingPrecedencesOfTrueCoulmnIsOver_upperbound = true;
//        }
        while (iteratorOverTwoDimensionalPrecArrayForFalse >= 0 && iteratorOverTwoDimensionalPrecArrayForTrue >= 0) {
            if (FalseStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse]]
                    >= TrueStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue]]) {
                indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse];
                scheduleEligibleTasksForFalseStateUpperboundNewApproach(indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForFalse, numberOfCapacitiesToProcess);
                iteratorOverTwoDimensionalPrecArrayForFalse--;
//                if (iteratorOverTwoDimensionalPrecArrayForFalse == -1) {
//                    processingPrecedencesOfFalseCoulmnIsOver_upperbound = true;
//                }
            } else {
                indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue];
                scheduleEligibleTasksForTrueStateUpperboundNewApproach(indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForTrue,
                        numberOfCapacitiesToProcess);
                iteratorOverTwoDimensionalPrecArrayForTrue--;
//                if (iteratorOverTwoDimensionalPrecArrayForTrue == -1) {
//                    processingPrecedencesOfTrueCoulmnIsOver_upperbound = true;
//                }
            }
        }
        while (iteratorOverTwoDimensionalPrecArrayForFalse >= 0) {
            indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse];
            scheduleEligibleTasksForFalseStateUpperboundNewApproach(indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForFalse,
                    numberOfCapacitiesToProcess);
            iteratorOverTwoDimensionalPrecArrayForFalse--;
//            if (iteratorOverTwoDimensionalPrecArrayForFalse == -1) {
//                processingPrecedencesOfFalseCoulmnIsOver_upperbound = true;
//            }
        }
        while (iteratorOverTwoDimensionalPrecArrayForTrue >= 0) {
            indexOfTheTaskBeingProcessedInTheOriginalArray = indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue];
            scheduleEligibleTasksForTrueStateUpperboundNewApproach(indexOfTheTaskBeingProcessedInTheOriginalArray, iteratorOverTwoDimensionalPrecArrayForTrue,
                    numberOfCapacitiesToProcess);
            iteratorOverTwoDimensionalPrecArrayForTrue--;
//            if (iteratorOverTwoDimensionalPrecArrayForTrue == -1) {
//                processingPrecedencesOfTrueCoulmnIsOver_upperbound = true;
//            }
        }
    }

    public void scheduleEligibleTasksForFalseStateUpperboundNewApproach(int indexOfTheTaskBeingProcessedInTheOriginalArray,
            int iteratorOverTwoDimensionalPrecArrayForFalse, int numberOfCapacitiesToProcess
    ) {
        int[] updForFalseUpperBoundNewApproach = new int[numberOfCapacitiesToProcess];
        //  for (int r = 0; r < numberOfCapacitiesToProcess; r++) {
        // updForFalseUpperBoundNewApproach[r] =  INFINITY;
        //  }
        int estValueAtThisIndex = FalseStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_false_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForFalse]];
        boolean stateOfDelay = false;
        if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex) {
            for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], true, true, e, numberOfCapacitiesToProcess);
                TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], true, true, e, numberOfCapacitiesToProcess);
            }
        }
        while (counterOnSortedEstArray >= 0 && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray]].earliestStartingTime() >= estValueAtThisIndex) {
            int indexOfThisTaskInTheOriginalArray = indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray];
            boolean delayedPortion = false;
            for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], delayedPortion, false, e, numberOfCapacitiesToProcess);
                TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], !delayedPortion, false, e, numberOfCapacitiesToProcess);
            }
            counterOnSortedEstArray--;
        }
        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            calculatedEnvelopeUpperBound = calculatedEnvelopeNewApproach(Tasks_updated_at_each_iteration, TT_P_upperbound, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], estValueAtThisIndex, stateOfDelay, delayNumber, false, e);
            int numerator = calculatedEnvelopeUpperBound - (resourceHeight - allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]]) * estValueAtThisIndex;
            int diff = diffForUpperbound(numerator, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]]);
            if (diff < INFINITY) {
                updForFalseUpperBoundNewApproach[e] = diff;
            }
        }
        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].height() == allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]]
                    && updForFalseUpperBoundNewApproach[e] >= Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestCompletionTime()
                    && updForFalseUpperBoundNewApproach[e] < Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].latestCompletionTime()) {
                updated_Est_Lct_And_dLcts[1][indexOfTheTaskBeingProcessedInTheOriginalArray] = updForFalseUpperBoundNewApproach[e];
            }
        }
        /*
            if (indexOfTheTaskBeingProcessedInTheOriginalArray != indexOfLastTaskToBeProcessedForAdjustment_upperbound && !processingPrecedencesOfTrueCoulmnIsOver_upperbound) {
                if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex) {
                    for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                        TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], false, false, e, numberOfCapacitiesToProcess);
                        TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], true, false, e, numberOfCapacitiesToProcess);
                    }
                }
            }
         */
    }

    public void scheduleEligibleTasksForTrueStateUpperboundNewApproach(int indexOfTheTaskBeingProcessedInTheOriginalArray,
            int iteratorOverTwoDimensionalPrecArrayForTrue, int numberOfCapacitiesToProcess
    ) {
        int[] updForTrueUpperBoundNewApproach = new int[numberOfCapacitiesToProcess];
        //  for (int r = 0; r < numberOfCapacitiesToProcess; r++) {
        //  updForTrueUpperBoundNewApproach[r] =  INFINITY;
        // }
        int estValueAtThisIndex = TrueStatePrecedencesForUpperBound[indices_of_tasks_sorted_by_true_state_precArray_upperbound[iteratorOverTwoDimensionalPrecArrayForTrue]];
        boolean stateOfDelay = true;
        if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex) {
            for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], true, true, e, numberOfCapacitiesToProcess
                );
                TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], true, true, e, numberOfCapacitiesToProcess);
            }
        }
        while (counterOnSortedEstArray >= 0 && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray]].earliestStartingTime() >= estValueAtThisIndex) {
            int indexOfThisTaskInTheOriginalArray = indices_of_tasks_sorted_by_est_P[counterOnSortedEstArray];
            boolean delayedPortion = false;
            for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfThisTaskInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], delayedPortion, false, e, numberOfCapacitiesToProcess
                );
            }
            counterOnSortedEstArray--;
        }
        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            calculatedEnvelopeUpperBound = calculatedEnvelopeNewApproach(Tasks_updated_at_each_iteration, TT_P_upperbound, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], estValueAtThisIndex, stateOfDelay, delayNumber, false, e);
            int numerator = calculatedEnvelopeUpperBound - (resourceHeight - allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]]) * estValueAtThisIndex;
            int diff = diffForUpperbound(numerator, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]]);
            if (diff < INFINITY) {
                updForTrueUpperBoundNewApproach[e] = diff;
            }
        }
        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].height() == allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]]
                    && updForTrueUpperBoundNewApproach[e]
                    >= Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].delayedEarliestCompletionTime()
                    && updForTrueUpperBoundNewApproach[e] < Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].delayedLatestCompletionTime()) {
                updated_Est_Lct_And_dLcts[1][indexOfTheTaskBeingProcessedInTheOriginalArray] = updForTrueUpperBoundNewApproach[e] - Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].allowedDelay();
            }
        }
        /*
        if (indexOfTheTaskBeingProcessedInTheOriginalArray != indexOfLastTaskToBeProcessedForAdjustment_upperbound && !processingPrecedencesOfFalseCoulmnIsOver_upperbound) {
            if (Tasks_updated_at_each_iteration[indexOfTheTaskBeingProcessedInTheOriginalArray].earliestStartingTime() >= estValueAtThisIndex) {
                for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                    TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], false, false, e, numberOfCapacitiesToProcess
                    );
                    TT_P_upperbound.scheduleTaskWithCForUpperBoundNewApproach(TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][1], TLT_P_upperbound.indicesOfTasksCoreespondingToLeavesOfTTForUpperbound()[TLT_P_upperbound.positionOfTasksAsALeafOfTTForUpperbound()[indexOfTheTaskBeingProcessedInTheOriginalArray][0]], allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessedUpperBound[e]], true, false, e, numberOfCapacitiesToProcess);
                }
            }
        }
         */
    }

    /*
    public static Integer[] sortWithJavaLibrary(Task[] tasks, Comparator<Integer> comparator) {
        
        int n = tasks.length;
        Integer[] tasks_indices = new Integer[n];
        for (int q = 0; q < n; q++) {
            tasks_indices[q] = q;
        }
        Arrays.sort(tasks_indices, comparator);
        return tasks_indices;
    }   
   
    
    
    public static Integer[] sortWithJavaLibraryInNonIncreasingOrder(Integer[] array) {
        
        int n = array.length;
        Integer[] tasks_indices = new Integer[n];
        for (int q = 0; q < n; q++) {
            tasks_indices[q] = q;
        }
        Arrays.sort(tasks_indices, new IntComparator(array));
        return tasks_indices;
    }
    
     */
    public static void sortIndicesWithJavaLibrary(Integer[] indices, Comparator<Integer> comparator) {

        int n = indices.length;
        Arrays.sort(indices, comparator);
    }

    /*
    public static class IntComparator implements Comparator<Integer> {
        
        private Integer[] array;
        
        public IntComparator(Integer[] array) {
            this.array = array;
        }
        
        @Override
        public int compare(Integer a, Integer b) {
            return array[b] - array[a];
        }
    }
     */
    public static void insertionSortWithComparator(Integer[] a, Comparator c) {
        for (int i = 0; i < a.length; i++) {
            Integer v = a[i];
            int j;
            for (j = i - 1; j >= 0; j--) {
                if (c.compare(a[j], v) <= 0) {
                    break;
                }
                a[j + 1] = a[j];
            }
            a[j + 1] = v;
        }
    }

    // public Vector MergeLctAndDelayedLct(Task[] Tasks) {
    public ArrayList MergeLctAndDelayedLct(Task[] Tasks) {

        //  Vector<Integer> T = new Vector();;
        ArrayList<Integer> T = new ArrayList();
        int i = 0;
        int n = numberOfTasks;
        int j = 0;
        //    int u = 0;
        while (i < n || j < n) {
            if (j == n || (i < n && Tasks[indices_of_tasks_sorted_by_lct_P[i]].latestCompletionTime()
                    <= Tasks[indices_of_tasks_sorted_by_delayed_lct_P[j]].delayedLatestCompletionTime())) {
                if (T.isEmpty() || T.get(T.size() - 1) != Tasks[indices_of_tasks_sorted_by_lct_P[i]].latestCompletionTime()) {
                    T.add(Tasks[indices_of_tasks_sorted_by_lct_P[i]].latestCompletionTime());
                }
                i++;
            } else if (j < n) {
                if (T.isEmpty() || T.get(T.size() - 1) != Tasks[indices_of_tasks_sorted_by_delayed_lct_P[j]].delayedLatestCompletionTime()) {
                    T.add(Tasks[indices_of_tasks_sorted_by_delayed_lct_P[j]].delayedLatestCompletionTime());
                }
                j++;
            }
        }
        return T;
    }

    public void joinAndSortAllLctAndDelayedLcts() {
        VectorTOfLctAndDelayedLcts = MergeLctAndDelayedLct(Tasks_updated_at_each_iteration);
        sizeOfVectorTOfLctAndDelayedLcts = VectorTOfLctAndDelayedLcts.size();
        int u = 0;
        int w1 = 0, w2 = 0, z = 0;
        while (u < sizeOfVectorTOfLctAndDelayedLcts) {
            int temp = VectorTOfLctAndDelayedLcts.get(u++);
            while (w1 != numberOfTasks && w2 != numberOfTasks
                    && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_P[w1]].latestCompletionTime() == temp
                    && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_lct_P[w2]].delayedLatestCompletionTime() == temp) {
                while (w1 != numberOfTasks && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_P[w1]].latestCompletionTime() == temp) {
                    sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setTValue(indices_of_tasks_sorted_by_lct_P[w1]);
                    sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setDelayStatus(false);
                    valuesOfSortedAndMergedLctAndDelayedLctArray[z] = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_P[w1]].latestCompletionTime();
                    w1++;
                    z++;
                }
                while (w2 != numberOfTasks && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_lct_P[w2]].delayedLatestCompletionTime() == temp) {
                    sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setTValue(indices_of_tasks_sorted_by_delayed_lct_P[w2]);
                    sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setDelayStatus(true);
                    valuesOfSortedAndMergedLctAndDelayedLctArray[z] = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_lct_P[w2]].delayedLatestCompletionTime();
                    w2++;
                    z++;
                }
            }
            while (w1 != numberOfTasks && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_P[w1]].latestCompletionTime() == temp) {
                sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setTValue(indices_of_tasks_sorted_by_lct_P[w1]);
                sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setDelayStatus(false);
                valuesOfSortedAndMergedLctAndDelayedLctArray[z] = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_P[w1]].latestCompletionTime();
                z++;
                w1++;
            }
            while (w2 != numberOfTasks
                    && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_lct_P[w2]].delayedLatestCompletionTime() == temp) {
                sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setTValue(indices_of_tasks_sorted_by_delayed_lct_P[w2]);
                sortedAndMergedLctAndDelayedLctArrayTwoDimensional[z].setDelayStatus(true);
                valuesOfSortedAndMergedLctAndDelayedLctArray[z] = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_delayed_lct_P[w2]].delayedLatestCompletionTime();
                z++;
                w2++;
            }
        }
    }

// Complexity: O(k^2 log n)
    /*
    public int calculatedEnvelope(Task[] T, thetaTree TT, int c, int tValueAtThisIndex, boolean stateOfDelay, int delayNumber, boolean filteringLowerbound) {
        int foundLeaf;
        if (filteringLowerbound) {
            foundLeaf = TT.maxEst(c, delayNumber, tValueAtThisIndex, stateOfDelay);
            return TT.computeEnvelope(delayNumber, foundLeaf, stateOfDelay);
        } else {
            foundLeaf = TT.minLct(c, delayNumber, tValueAtThisIndex, stateOfDelay);
            return TT.computeEnvelopeForUpperbound(delayNumber, foundLeaf, stateOfDelay);
        }
    }
    
    
     */
    public int calculatedEnvelopeNewApproach(Task[] T, thetaTree TT, int c, int tValueAtThisIndex, boolean stateOfDelay, int delayNumber, boolean filteringLowerbound, int dimensionOfCapacity) {
        int foundLeaf;
        if (filteringLowerbound) {
            foundLeaf = TT.maxEstNewApproach(c, delayNumber, tValueAtThisIndex, stateOfDelay, dimensionOfCapacity);
            return TT.computeEnvelope(delayNumber, foundLeaf, stateOfDelay);
        } else {
            foundLeaf = TT.minLctNewApproach(c, delayNumber, tValueAtThisIndex, stateOfDelay, dimensionOfCapacity);
            return TT.computeEnvelopeForUpperbound(delayNumber, foundLeaf, stateOfDelay);
        }
    }

    public int diff(int numerator, int c) {
        //  int diff;
        int diff = (numerator >= 0) ? (numerator + c - 1) / c : -(-numerator / c);
//        if (numerator >= 0) {
//            diff = (numerator + c - 1) / c;
//        } else {
//            diff = -(-numerator / c);
//        }
        return diff;
    }

    public int diffForUpperbound(int numerator, int c) {
        int diff;
        diff = (int) Math.floor(numerator / c);
        return diff;
    }

    /*
    public int computesEnvelopeAtTheAppropriateTime(Task[] T, thetaTree TT, int bound,  int c, boolean stateOfDelay)
    {
         int calculatedEnvelope = calculatedEnvelope(T, TT, c, bound, stateOfDelay, delayNumber, true);
            int numerator = calculatedEnvelope - (resourceHeight - c) * bound;
          int  diff = diff(numerator, c);            
           return diff;
    }
     */
    public int computesEnvelopeAtTheAppropriateTimeNewApproach(Task[] T, thetaTree TT, int bound, int c, boolean stateOfDelay, int dimensionOfCapacity) {
        int calculatedEnvelope = calculatedEnvelopeNewApproach(T, TT, c, bound, stateOfDelay, delayNumber, true, dimensionOfCapacity);
        int numerator = calculatedEnvelope - (resourceHeight - c) * bound;
        int diff = diff(numerator, c);
        return diff;
    }

    public void takeCareOfNecessarySchedulings(thetaTree TT, int c, boolean thisValueBelongsToADelayedCase, boolean stateOfDelay,
            int indexOfThisTaskInTheOriginalArray, int dimensionOfCapacity, int numberOfCapacitiesToProcess) {
        TT.scheduleWithC(task_index_to_node_index_for_est[indexOfThisTaskInTheOriginalArray], indexOfThisTaskInTheOriginalArray, c, thisValueBelongsToADelayedCase, false, dimensionOfCapacity, numberOfCapacitiesToProcess);
        //thisTaskIsInThetaNotDelayed[indexOfThisTaskInTheOriginalArray] = true;
        // if (thisValueBelongsToADelayedCase)
        //thisTaskIsInThetaAndDelayed[indexOfThisTaskInTheOriginalArray] = true;
    }

    public void takeCareOfUpdating(int numberOfCapacitiesToProcess, boolean stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, boolean checkTempUpdatedArray) {
        if (!stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
            for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                currentEnvelopeForFalseState_new_approach[e] = computesEnvelopeAtTheAppropriateTimeNewApproach(Tasks_updated_at_each_iteration, TT_P, currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, e);
                calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[e] = computesEnvelopeAtTheAppropriateTimeNewApproach(Tasks_updated_at_each_iteration, TT_P, currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], !stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, e);
                //  temp[e] = Math.max(currentEnvelopeForFalseState_new_approach[e], calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[e]);

                if (currentEnvelopeForFalseState_new_approach[e] > calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[e]) {
                    temp[e] = currentEnvelopeForFalseState_new_approach[e];
                } else {
                    temp[e] = calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[e];
                }

                if (temp[e] > updateForFalse_new_approach[e]) {
                    updateForFalse_new_approach[e] = temp[e];
                }
            }
            if (checkTempUpdatedArray) {
                for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                    if (temp_updateForFalse_new_approach[e] > updateForFalse_new_approach[e]) {
                        updateForFalse_new_approach[e] = temp_updateForFalse_new_approach[e];
                    }
                }
            }

        } else {
            for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                currentEnvelopeForTrueState_new_approach[e] = computesEnvelopeAtTheAppropriateTimeNewApproach(Tasks_updated_at_each_iteration, TT_P, currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, e);
                calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[e] = computesEnvelopeAtTheAppropriateTimeNewApproach(Tasks_updated_at_each_iteration, TT_P, currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], !stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, e);
                // temp[e] = Math.max(currentEnvelopeForTrueState_new_approach[e], calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[e]);
                if (currentEnvelopeForTrueState_new_approach[e] > calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[e]) {
                    temp[e] = currentEnvelopeForTrueState_new_approach[e];
                } else {
                    temp[e] = calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[e];
                }

                if (temp[e] > updateForTrue_new_approach[e]) {
                    updateForTrue_new_approach[e] = temp[e];
                }
            }
            if (checkTempUpdatedArray) {
                for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                    if (temp_updateForTrue_new_approach[e] > updateForTrue_new_approach[e]) {
                        updateForTrue_new_approach[e] = temp_updateForTrue_new_approach[e];
                    }
                }
            }
        }
    }

    public void takeCareOfNecessaryUpdatingOnceThereIsSomeUnscheduledTask(int numberOfCapacitiesToProcess, boolean stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, boolean doTheEnvelopeComputation) {
        if (!stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
            if (doTheEnvelopeComputation) {
                for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                    currentEnvelopeForFalseState_new_approach[e] = computesEnvelopeAtTheAppropriateTimeNewApproach(Tasks_updated_at_each_iteration, TT_P, currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, e);
                }
            }
            if (doTheEnvelopeComputation) {
                //  for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                //   temp_updateForFalse_new_approach[e] = updateForFalse_new_approach[e];
                //    }
                System.arraycopy(updateForFalse_new_approach, 0, temp_updateForFalse_new_approach, 0, numberOfCapacitiesToProcess);
            }
            //  for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            //     updateForFalse_new_approach[e] = currentEnvelopeForFalseState_new_approach[e];
            //   }
            System.arraycopy(currentEnvelopeForFalseState_new_approach, 0, updateForFalse_new_approach, 0, numberOfCapacitiesToProcess);
        } else {
            if (doTheEnvelopeComputation) {
                for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                    currentEnvelopeForTrueState_new_approach[e] = computesEnvelopeAtTheAppropriateTimeNewApproach(Tasks_updated_at_each_iteration, TT_P, currentValueInValuesOfSortedAndMergedLctAndDelayedLctArray, allDistinctCapacities[indicesOfCapacitiesFromAllDistinceCapacitiesVectorWhichAreActuallyRequiredToGetProcessed[e]], stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray, e);
                }
            }
            if (doTheEnvelopeComputation) {
                //   for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
                //    System.a
                //  temp_updateForTrue_new_approach[e] = updateForTrue_new_approach[e];
                //  }
                System.arraycopy(updateForTrue_new_approach, 0, temp_updateForTrue_new_approach, 0, numberOfCapacitiesToProcess);
            }
            //  for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            //    updateForTrue_new_approach[e] = currentEnvelopeForTrueState_new_approach[e];
            //    }
            System.arraycopy(currentEnvelopeForTrueState_new_approach, 0, updateForTrue_new_approach, 0, numberOfCapacitiesToProcess);
        }
    }

    public void takeCareOfSomeNecessarryUpdatingOnceTheLoopIsNotIterated(int numberOfCapacitiesToProcess, boolean stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray) {
        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            //   temp[e] = Math.max(currentEnvelopeForFalseState_new_approach[e], calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[e]);

            if (currentEnvelopeForFalseState_new_approach[e] > calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[e]) {
                temp[e] = currentEnvelopeForFalseState_new_approach[e];
            } else {
                temp[e] = calculatedEnvelopeWithRDelayedTasksForLaterConsideration_new_approach[e];
            }

        }
        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            if (temp[e] > updateForFalse_new_approach[e]) {
                updateForFalse_new_approach[e] = temp[e];
            }
        }

        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            //    temp[e] = Math.max(currentEnvelopeForTrueState_new_approach[e], calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[e]);

            if (currentEnvelopeForTrueState_new_approach[e] > calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[e]) {
                temp[e] = currentEnvelopeForTrueState_new_approach[e];
            } else {
                temp[e] = calculatedEnvelopeWithRMinusOneDelayedTasksForLaterConsideration_new_approach[e];
            }

        }
        for (int e = 0; e < numberOfCapacitiesToProcess; e++) {
            if (temp[e] > updateForTrue_new_approach[e]) {
                updateForTrue_new_approach[e] = temp[e];
            }
        }
    }

    public boolean checkVariableBounds() {
        for (int t = 0; t < numberOfTasks; t++) {
            if (lowerBoundOfStartingTimes[t] + lowerBoundOfProcessingTime[t] > upperBoundOfEndingTimes[t]) {
                // System.out.print(" BUG task t+" + t + " // = > ");
                //  printTask(t);
                return false;
            }
        }
        return true;
    }

    public boolean decideStatus(int delayedOrNotDimension) {

//         if (delayedOrNotDimension == 0)
//            stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray = false;
//        else
//            stateOfDelayOfTheTaskBeingProcessedInTheOriginalArray = true;
        return (delayedOrNotDimension == 1);
    }

}
