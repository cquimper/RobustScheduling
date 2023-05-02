package robustcumulativescheduling;

import java.util.ArrayList;
import java.util.Comparator;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import org.chocosolver.util.tools.ArrayUtils;

public class Propagator_overloadCheckingConstraint extends Propagator<IntVar> {

    private final int processing_times[];
    private final int heights[];
    private final int delays[];
    private final int resourceHeight;
    private final int delayNumber;
    private final Integer[] indices_of_tasks_sorted_by_est;
    private final Integer[] indices_of_tasks_sorted_by_lct_delayed;
    private final Task[] Tasks_updated_at_each_iteration;
    private final Integer[] indices_of_tasks_sorted_by_lct;
    private final thetaTree thisThetaTree;
    private final int num;
    boolean filterMakespan;
    private final int[] task_index_to_node_index_for_est;
    boolean delayedcaseIsConsidered = true;
    //   private int z1 = 0;

    public Propagator_overloadCheckingConstraint(IntVar[] vars, int nbTasks, int nbResources, int[] capacities, int[] ka, boolean filterMakespan, int r) {
        super(ArrayUtils.append(vars), PropagatorPriority.VERY_SLOW, false);
        this.delays = ka;
        this.resourceHeight = capacities[0];
        this.num = nbTasks;
        this.filterMakespan = filterMakespan;
        this.delayNumber = r;
        this.Tasks_updated_at_each_iteration = new Task[num];
        this.processing_times = new int[nbTasks];
        this.heights = new int[nbTasks];
        for (int i = 0; i < nbTasks; i++) {
            processing_times[i] = vars[nbTasks + i].getLB();
            heights[i] = vars[3 * nbTasks + i].getLB();
        }
        for (int j = 0; j < num; j++) {
            Tasks_updated_at_each_iteration[j] = new Task(vars[j].getLB() - 1, vars[j].getUB() + processing_times[j], vars[j].getUB() + processing_times[j] + ka[j], processing_times[j], ka[j], heights[j]);
            //    System.out.println("j: " + j + Tasks_updated_at_each_iteration[j]);
        }
        this.thisThetaTree = new thetaTree(Tasks_updated_at_each_iteration, delayNumber, resourceHeight, false);
        this.indices_of_tasks_sorted_by_est = new Integer[num];
        for (int o = 0; o < num; o++) {
            indices_of_tasks_sorted_by_est[o] = o;
        }
        this.indices_of_tasks_sorted_by_lct_delayed = new Integer[num];
        for (int o = 0; o < num; o++) {
            indices_of_tasks_sorted_by_lct_delayed[o] = o;
        }
        this.indices_of_tasks_sorted_by_lct = new Integer[Tasks_updated_at_each_iteration.length];
        for (int q = 0; q < Tasks_updated_at_each_iteration.length; q++) {
            indices_of_tasks_sorted_by_lct[q] = q;
        }
        this.task_index_to_node_index_for_est = new int[num];
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
        if (!isConsistent()) {
            throw new ContradictionException();
        }
    }

    @Override
    public ESat isEntailed() {
        return ESat.UNDEFINED;
    }

    public boolean isConsistent() throws ContradictionException {

        //    z1++;
        /*
        if (resourceHeight == 8) {
          
          //  System.out.println("z1 = " + z1);
        }
        else if (resourceHeight == 7) {
            z2++;
          //  System.out.println("z2 = " + z2);
        }
         */
        //    System.out.println("z1 = " + z1);
        //   System.out.println();
//        System.out.println("starting times");
//        for (int t = 0; t < num; t++) {
//            System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() + "]");
//        }

        /*
            System.out.println("processing times");
            for (int t = num; t < 2 * num; t++) {
                System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
            }
            System.out.println("ending times");
            for (int t = 2 * num; t < 3 * num; t++) {
                System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
            }
            System.out.println("heights");
            for (int t = 3 * num; t < 4 * num; t++) {
                System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
            }
        if (CAPProblemConsidered) {
            System.out.println("cranes");
            for (int t = 4 * num; t < 5 * num; t++) {
                System.out.println("t - > " + t + " " + "[" + vars[t].getLB() + ", " + vars[t].getUB() +"]");
            }
        }
            //HERE IN CRANE PROBLEM IS DIFFERENT. IN THAT PROBLEM CRANES ARE VARIABLES AS WELL, SO   capacity and makespan ARE AFTER THEM.   
                  
                  System.out.println("capacity");
                    if (CAPProblemConsidered) 
                  System.out.println("[" + vars[5 * num].getLB() + ", " + vars[5 * num].getUB() +"]");
           else
                  System.out.println("[" + vars[4 * num].getLB() + ", " + vars[4 * num].getUB() +"]");
       

                               if (CAPProblemConsidered) 
                    System.out.println("[" + vars[5 * num + 1].getLB() + ", " + vars[5 * num + 1].getUB() +"]");
       else

         */
        //  System.out.println("makespan");
        //   System.out.println("[" + vars[4 * num + 1].getLB() + ", " + vars[4 * num + 1].getUB() +"]");
        int currentMakespanBound;
        for (int j = 0; j < num; j++) {
            Tasks_updated_at_each_iteration[j].setEarliestStartingTime(vars[j].getLB());
            Tasks_updated_at_each_iteration[j].setLatestCompletionTime(vars[j].getUB() + vars[num + j].getLB());
            Tasks_updated_at_each_iteration[j].setDelayedLatestCompletionTime(vars[j].getUB() + vars[num + j].getLB() + delays[j]);
        }
        /*
        if (z1 == 170) {
        for (int j = 0; j < num; j++) {
          System.out.println(Tasks_updated_at_each_iteration[j]);
        }
                System.out.println("these are your tasks");

      }
         */
        //   System.out.println();
        insertionSortWithComparator(indices_of_tasks_sorted_by_est, new Task.ComparatorByEst(Tasks_updated_at_each_iteration));
        for (int q = 0; q < num; q++) {
            task_index_to_node_index_for_est[indices_of_tasks_sorted_by_est[q]] = q;
        }
        insertionSortWithComparator(indices_of_tasks_sorted_by_lct, new Task.ComparatorByLct(Tasks_updated_at_each_iteration));
        insertionSortWithComparator(indices_of_tasks_sorted_by_lct_delayed, new Task.ComparatorByDelayedLct(Tasks_updated_at_each_iteration));
        ArrayList<Integer> T = Merge(Tasks_updated_at_each_iteration);
        int i = 0;
        int j = 0;
        int size = T.size();
        // if (z1 == 54)
        //  System.out.printf("stop");
        //  int earliest_completion_time1;
        //        int earliest_dcompletion_time;
        thisThetaTree.initializeThetaTreeForOC();
        //    int delayed_energy_contribution_val = 0;

        for (int t = 0; t < size; t++) {
            //     int thisElementOfVectorT = T.elementAt(t);
            int thisElementOfVectorT = T.get(t);
            while (i < num && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime() == thisElementOfVectorT) {
                int indexOfThisTaskAsALeafInTheTree = task_index_to_node_index_for_est[indices_of_tasks_sorted_by_lct[i]];
                thisThetaTree.scheduleSortedWithIndicesTasks(indexOfThisTaskAsALeafInTheTree, Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].envelope(resourceHeight), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].envelope(resourceHeight), 0, 1, false);
                // thisThetaTree.scheduleSortedWithIndicesTasks2(indexOfThisTaskAsALeafInTheTree, Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].envelope(resourceHeight), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct[i]].envelope(resourceHeight), 0, 1, delayed_energy_contribution_val, !delayedcaseIsConsidered, false);

                i++;
            }
            while (j < num && Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime() == thisElementOfVectorT) {
                int indexOfThisTaskAsALeafInTheTree = task_index_to_node_index_for_est[indices_of_tasks_sorted_by_lct_delayed[j]];

                //      delayed_energy_contribution_val = Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].allowedDelay() * Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].height();
                if (Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime() != 0) {
                    thisThetaTree.scheduleSortedWithIndicesTasks(indexOfThisTaskAsALeafInTheTree, Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].energy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].envelope(resourceHeight), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].delayedEnergy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].delayedEnvelope(resourceHeight), 0, 1, false);

                    // thisThetaTree.scheduleSortedWithIndicesTasks2(indexOfThisTaskAsALeafInTheTree, Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].energy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].envelope(resourceHeight), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].delayedEnergy(), Tasks_updated_at_each_iteration[indices_of_tasks_sorted_by_lct_delayed[j]].delayedEnvelope(resourceHeight), 0, 1, delayed_energy_contribution_val, delayedcaseIsConsidered, false);
                }
                j++;
            }

            if (thisThetaTree.requiredEnvelopeInTheRoot(delayNumber) > resourceHeight * thisElementOfVectorT) {
                //   System.out.print("fail\n");
                return false;
            }

            /* if (thisThetaTree.Env_w_Root() > resourceHeight * thisElementOfVectorT) {
                System.out.print("fail\n");
                return false;
            }*/
        }
        if (filterMakespan) {
            IntVar makespan = vars[vars.length - 1];
            currentMakespanBound = (thisThetaTree.requiredEnvelopeInTheRoot(delayNumber) + resourceHeight - 1) / resourceHeight;
            if (makespan.getLB() < currentMakespanBound) {
                makespan.updateLowerBound(currentMakespanBound, aCause);
            }
        }
        return true;
    }

    public ArrayList Merge(Task[] Tasks) {
        ArrayList<Integer> T = new ArrayList<>();
        int i = 0;
        int n = Tasks.length;
        int j = 0;
        while (i < n || j < n) {
            if (j == n || (i < n && Tasks[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime() <= Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime())) {
                if (T.isEmpty() || T.get(T.size() - 1) != Tasks[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime()) {
                    T.add(Tasks[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime());
                }
                i++;
            } else if (j < n) {
                if (T.isEmpty() || T.get(T.size() - 1) != Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime()) {
                    T.add(Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime());
                }
                j++;
            }
        }
        return T;
    }

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

}
