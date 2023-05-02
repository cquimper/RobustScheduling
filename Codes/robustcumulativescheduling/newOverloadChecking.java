package robustcumulativescheduling;

import robustcumulativescheduling.thetaTree;
import java.util.Comparator;
import java.util.Vector;
import org.chocosolver.solver.variables.IntVar;
import static robustcumulativescheduling.Propagator_overloadCheckingConstraint.insertionSortWithComparator;

/**
 *
 * @author Hamed
 */
public class newOverloadChecking {

    boolean delayedcaseIsConsidered = true;

    public boolean checking_E_Feasibility(Task[] Tasks, int delayNumber, int resourceCapacity) {

        int n = Tasks.length;
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.println("i = " + i + ":" + Tasks[i]);
        }

        Integer[] indices_of_tasks_sorted_by_est = new Integer[n];
        Integer[] indices_of_tasks_sorted_by_lct = new Integer[n];
        Integer[] indices_of_tasks_sorted_by_lct_delayed = new Integer[n];
        int[] task_index_to_node_index_for_est = new int[n];
        for (int o = 0; o < Tasks.length; o++) {
            indices_of_tasks_sorted_by_est[o] = o;
            indices_of_tasks_sorted_by_lct[o] = o;
            indices_of_tasks_sorted_by_lct_delayed[o] = o;
        }
        insertionSortWithComparator(indices_of_tasks_sorted_by_est, new Task.ComparatorByEst(Tasks));
        for (int q = 0; q < n; q++) {
            task_index_to_node_index_for_est[indices_of_tasks_sorted_by_est[q]] = q;
        }
        insertionSortWithComparator(indices_of_tasks_sorted_by_lct, new Task.ComparatorByLct(Tasks));
        insertionSortWithComparator(indices_of_tasks_sorted_by_lct_delayed, new Task.ComparatorByDelayedLct(Tasks));
        Vector<Integer> T = new Vector();
        T = Merge(Tasks, indices_of_tasks_sorted_by_lct, indices_of_tasks_sorted_by_lct_delayed);
    //    int earliest_completion_time;
        int i = 0;
        int j = 0;
        int size = T.size();
        thetaTree thisThetaTree;
        thisThetaTree = new thetaTree(Tasks, delayNumber, resourceCapacity, false);
        thisThetaTree.initializeThetaTreeForOC();

        int delayed_energy_contribution_val = 0;

        for (int t = 0; t < size; t++) {
            int thisElementOfVectorT = T.elementAt(t);
            while (i < n && Tasks[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime() == thisElementOfVectorT) {
                int indexOfThisTaskAsALeafInTheTree = task_index_to_node_index_for_est[indices_of_tasks_sorted_by_lct[i]];
                //  thisThetaTree.scheduleSortedWithIndicesTasks(indexOfThisTaskAsALeafInTheTree, Tasks[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks[indices_of_tasks_sorted_by_lct[i]].envelope(resourceCapacity), Tasks[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks[indices_of_tasks_sorted_by_lct[i]].envelope(resourceCapacity), 0, 1, false);
                thisThetaTree.scheduleSortedWithIndicesTasks2(indexOfThisTaskAsALeafInTheTree, Tasks[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks[indices_of_tasks_sorted_by_lct[i]].envelope(resourceCapacity), Tasks[indices_of_tasks_sorted_by_lct[i]].energy(), Tasks[indices_of_tasks_sorted_by_lct[i]].envelope(resourceCapacity), 0, 1, delayed_energy_contribution_val, !delayedcaseIsConsidered, false);

           //     earliest_completion_time = (thisThetaTree.requiredEnvelopeInTheRoot(0) + resourceCapacity - 1) / resourceCapacity;
                //  System.out.println(earliest_completion_time);
                i++;
            }
            while (j < n && Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime() == thisElementOfVectorT) {
                int indexOfThisTaskAsALeafInTheTree = task_index_to_node_index_for_est[indices_of_tasks_sorted_by_lct_delayed[j]];
//Later check whether this loop could be avoided for tasks with d = 0
                delayed_energy_contribution_val = Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].allowedDelay() * Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].height();
                if (Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime() != 0) {
                    thisThetaTree.scheduleSortedWithIndicesTasks2(indexOfThisTaskAsALeafInTheTree, Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].energy(), Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].envelope(resourceCapacity), Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedEnergy(), Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedEnvelope(resourceCapacity), 0, 1, delayed_energy_contribution_val, delayedcaseIsConsidered, false);
                }
             //   earliest_completion_time = (thisThetaTree.requiredEnvelopeInTheRoot(delayNumber) + resourceCapacity - 1) / resourceCapacity;
                //   System.out.println(earliest_completion_time);
                j++;
            }
          System.out.println("This is the value = " + thisThetaTree.requiredEnvelopeInTheRoot(delayNumber));
           if (thisThetaTree.requiredEnvelopeInTheRoot(delayNumber) > resourceCapacity * thisElementOfVectorT) 
              return false;
          
//            System.out.println("This is the value = " + thisThetaTree.Env_Root());
//            if (thisThetaTree.Env_Root() > resourceCapacity * thisElementOfVectorT) {
//                return false;
//            }

//            System.out.println("This is the value = " + thisThetaTree.Env_w_Root());
//            if (thisThetaTree.Env_w_Root() > resourceCapacity * thisElementOfVectorT) {
//                return false;
//            }
        }

        return true;

    }

    public Vector Merge(Task[] Tasks, Integer[] indices_of_tasks_sorted_by_lct, Integer[] indices_of_tasks_sorted_by_lct_delayed) {

        // Here is the Merge part;
        Vector<Integer> T = new Vector();;
        //   T.add(Tasks[indices_of_tasks_sorted_by_lct[0]].latestCompletionTime());
        int i = 0;
        int n = Tasks.length;
        int j = 0;
        while (i < n || j < n) {
            if (j == n || (i < n && Tasks[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime() <= Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime())) {
                if (T.isEmpty() || T.lastElement() != Tasks[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime()) {
                    T.add(Tasks[indices_of_tasks_sorted_by_lct[i]].latestCompletionTime());
                }
                i++;
            } else if (j < n) {
                if (T.isEmpty() || T.lastElement() != Tasks[indices_of_tasks_sorted_by_lct_delayed[j]].delayedLatestCompletionTime()) {
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
