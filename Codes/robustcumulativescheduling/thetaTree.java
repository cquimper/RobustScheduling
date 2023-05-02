package robustcumulativescheduling;

public class thetaTree {

    Task[] tasks;
    private int theta_energy_of_nodes[][];
    private int theta_envelope_of_nodes[][];
    private int Env_w[];
    private int h_w_vector_of_nodes[][];
    private int H_w_vector_of_nodes[][];
    //  private boolean flagForRightChildSituationIsActivated;
    private int theta_c_envelope_of_nodes[][];
    private int theta_c_envelope_of_nodes_new_approach[][][];
    private int theta_energy_of_nodes_upperbound[][];
    private int theta_envelope_of_nodes_upperbound[][];
    private int theta_c_envelope_of_nodes_upperbound[][];
    private int theta_c_envelope_of_nodes_upperbound_new_approach[][][];
    private final int numberOfTasks;
    private int height;
    private final int twiceTheNumberOfTasks;
    private int heightInTheTreeOfUpperBounds;
    private int firstIndexOnTheLowestLevel;
    private int firstIndexOnTheLowestLevelUpperbounds;
    private int numberOfNodesInTheTreeOfUpperBounds;
    private static final int MINUS_INFINITY = -1073741824;
    private static final int INFINITY = 1073741824;
    private final int maximumNumberOfDelayedTasks;
    private static int Env_Root;
    private int maximumDelayPerNode;
    private int numberOfNodesInTheTree;
    private final int resourceCapacity;
    private int[] aggregatedEnergy1;
    private int[] aggregatedEnergy2;
    private int[] alpha_energy1;
    private int[] alpha_envelope1;
    private int[] beta_energy1;
    private int[] alpha_energy2;
    private int[] alpha_envelope2;
    private int[] beta_energy2;
    private int[] aggregatedEnergy1_upperbound;
    private int[] aggregatedEnergy2_upperbound;
    private int[] alpha_energy1_upperbound;
    private int[] alpha_envelope1_upperbound;
    private int[] beta_energy1_upperbound;
    private int[] alpha_energy2_upperbound;
    private int[] alpha_envelope2_upperbound;
    private int[] beta_energy2_upperbound;
    private int[] numberOfScheduledTasksAtThisRoot;
    //I need this for when I want to fill in the arrays of energy and envelope, I need to know how many
    //cells can be filled, according to the number of scheduled tasks at that root.

    public thetaTree(Task[] tasks, int u, int C, boolean processingEdgeFinder) {

        this.Env_Root = MINUS_INFINITY;
        this.resourceCapacity = C;
        this.tasks = tasks;
        this.numberOfTasks = tasks.length;
        this.maximumNumberOfDelayedTasks = u;
        if (processingEdgeFinder) {
            this.aggregatedEnergy1 = new int[maximumNumberOfDelayedTasks + 1];
            this.aggregatedEnergy2 = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_energy1 = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_envelope1 = new int[maximumNumberOfDelayedTasks + 1];
            this.beta_energy1 = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_energy2 = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_envelope2 = new int[maximumNumberOfDelayedTasks + 1];
            this.beta_energy2 = new int[maximumNumberOfDelayedTasks + 1];
            this.aggregatedEnergy1_upperbound = new int[maximumNumberOfDelayedTasks + 1];
            this.aggregatedEnergy2_upperbound = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_energy1_upperbound = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_envelope1_upperbound = new int[maximumNumberOfDelayedTasks + 1];
            this.beta_energy1_upperbound = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_energy2_upperbound = new int[maximumNumberOfDelayedTasks + 1];
            this.alpha_envelope2_upperbound = new int[maximumNumberOfDelayedTasks + 1];
            this.beta_energy2_upperbound = new int[maximumNumberOfDelayedTasks + 1];
        }
        if (maximumNumberOfDelayedTasks > numberOfTasks) {
            throw new IllegalArgumentException("No more than the number of tasks can be delayed!");
        }
        this.twiceTheNumberOfTasks = 2 * numberOfTasks;
        this.numberOfNodesInTheTree = nextPowerOfTwoMinusOne(twiceTheNumberOfTasks);
        this.numberOfNodesInTheTreeOfUpperBounds = nextPowerOfTwoMinusOne(2 * twiceTheNumberOfTasks);
        this.height = height(numberOfTasks);
        this.heightInTheTreeOfUpperBounds = height(twiceTheNumberOfTasks);;
        this.maximumDelayPerNode = maximumNumberOfDelayedTasks;
        this.theta_energy_of_nodes = new int[numberOfNodesInTheTree][maximumDelayPerNode + 1];
        this.theta_envelope_of_nodes = new int[numberOfNodesInTheTree][maximumDelayPerNode + 1];
        this.theta_c_envelope_of_nodes = new int[numberOfNodesInTheTree][maximumDelayPerNode + 1];
        this.Env_w = new int[numberOfNodesInTheTree];
        this.h_w_vector_of_nodes = new int[numberOfNodesInTheTree][maximumDelayPerNode];
        this.H_w_vector_of_nodes = new int[numberOfNodesInTheTree][maximumDelayPerNode];

        this.theta_energy_of_nodes_upperbound = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1];
        this.theta_envelope_of_nodes_upperbound = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1];
        this.theta_c_envelope_of_nodes_upperbound = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1];
        this.firstIndexOnTheLowestLevel = (1 << height) - 1;
        this.firstIndexOnTheLowestLevelUpperbounds = (1 << heightInTheTreeOfUpperBounds) - 1;
    }

    public void initializeThetaTreeForOC() {
        for (int i = 0; i < numberOfNodesInTheTree; i++) {
            for (int j = 0; j <= maximumDelayPerNode; j++) {
                theta_energy_of_nodes[i][j] = 0;
                theta_envelope_of_nodes[i][j] = MINUS_INFINITY;
                Env_w[i] = MINUS_INFINITY;
                if (j < maximumDelayPerNode) {
                    h_w_vector_of_nodes[i][j] = 0;
                    H_w_vector_of_nodes[i][j] = 0;
                }

            }
        }
    }

    public void initializeThetaTreeNewApproach(Task[] tasks, int numberOfCapacitiesToProcess) {

        for (int i = 0; i < numberOfNodesInTheTree; i++) {
            for (int j = 0; j <= maximumDelayPerNode; j++) {
                theta_energy_of_nodes[i][j] = 0;
                theta_envelope_of_nodes[i][j] = MINUS_INFINITY;
            }
        }
        this.theta_c_envelope_of_nodes_new_approach = new int[numberOfNodesInTheTree][maximumDelayPerNode + 1][numberOfCapacitiesToProcess];
        for (int i = 0; i < numberOfNodesInTheTree; i++) {
            for (int j = 0; j <= maximumDelayPerNode; j++) {
                for (int k = 0; k < numberOfCapacitiesToProcess; k++) {
                    theta_c_envelope_of_nodes_new_approach[i][j][k] = MINUS_INFINITY;
                }
            }
        }
        this.numberOfScheduledTasksAtThisRoot = new int[numberOfNodesInTheTree];
    }

    public void initializeThetaTreeForUpperBoundNewApproach(Task[] tasks, int numberOfCapacitiesToProcess) {

        for (int i = 0; i < numberOfNodesInTheTreeOfUpperBounds; i++) {
            for (int j = 0; j <= maximumDelayPerNode; j++) {
                theta_energy_of_nodes_upperbound[i][j] = 0;
                theta_envelope_of_nodes_upperbound[i][j] = INFINITY;
            }
        }
        this.theta_c_envelope_of_nodes_upperbound_new_approach = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1][numberOfCapacitiesToProcess];
        for (int i = 0; i < numberOfNodesInTheTreeOfUpperBounds; i++) {
            for (int j = 0; j <= maximumDelayPerNode; j++) {
                for (int k = 0; k < numberOfCapacitiesToProcess; k++) {
                    theta_c_envelope_of_nodes_upperbound_new_approach[i][j][k] = INFINITY;
                }
            }
        }
        this.numberOfScheduledTasksAtThisRoot = new int[numberOfNodesInTheTreeOfUpperBounds];
    }

    // Returns 2^ceil(lg(n)) - 1
    private static int nextPowerOfTwoMinusOne(int n) {
        if ((n & (n - 1)) == 0) {
            return n - 1;
        }
        int shift = 1;
        int result = n;
        do {
            n = result;
            result = n | (n >> shift);
            shift += shift;
        } while (n != result);
        return result;
    }

    private void UpdateLeaves(int task_index, int new_energy, int new_envelope, int new_delayed_energy, int new_delayed_envelope, int dimensionOfCapacity, int numberOfCapacitiesToProcess, boolean activateNewApproach) {
        int indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + task_index;
        theta_energy_of_nodes[indexOfThisTaskInTheTree][0] = new_energy;
        theta_envelope_of_nodes[indexOfThisTaskInTheTree][0] = new_envelope;
        for (int i = 1; i <= maximumDelayPerNode; i++) {
            theta_energy_of_nodes[indexOfThisTaskInTheTree][i] = new_delayed_energy;
            theta_envelope_of_nodes[indexOfThisTaskInTheTree][i] = new_delayed_envelope;
        }
        updateInnerNodes(indexOfThisTaskInTheTree, false, dimensionOfCapacity, numberOfCapacitiesToProcess);
    }

    private void UpdateLeaves2(int task_index, int new_energy, int new_envelope, int new_delayed_energy, int new_delayed_envelope, int dimensionOfCapacity, int numberOfCapacitiesToProcess, int delayed_energy_contribution_val, boolean delayedcaseIsConsidered, boolean activateNewApproach) {
        int indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + task_index;
        theta_energy_of_nodes[indexOfThisTaskInTheTree][0] = new_energy;
        theta_envelope_of_nodes[indexOfThisTaskInTheTree][0] = new_envelope;
        if (delayedcaseIsConsidered && delayed_energy_contribution_val != 0) {
            Env_w[indexOfThisTaskInTheTree] = new_delayed_envelope;
        } else {
            Env_w[indexOfThisTaskInTheTree] = new_envelope;
        }

        for (int i = 1; i <= maximumDelayPerNode; i++) {
            theta_energy_of_nodes[indexOfThisTaskInTheTree][i] = new_delayed_energy;
            theta_envelope_of_nodes[indexOfThisTaskInTheTree][i] = new_delayed_envelope;
            if (delayedcaseIsConsidered && delayed_energy_contribution_val != 0 && i == 1) {
                h_w_vector_of_nodes[indexOfThisTaskInTheTree][0] = delayed_energy_contribution_val;
                H_w_vector_of_nodes[indexOfThisTaskInTheTree][0] = delayed_energy_contribution_val;
            }
        }
        updateInnerNodes2(indexOfThisTaskInTheTree, false, dimensionOfCapacity, delayedcaseIsConsidered, delayed_energy_contribution_val, numberOfCapacitiesToProcess);
    }

    private int updateLeafWithCForUpperBound(int task_index, int b, int cForCenv, boolean delayedPortion, boolean unscheduleDelayedPortion) {

        int indexOfThisTaskInTheTree = firstIndexOnTheLowestLevelUpperbounds + task_index;
        numberOfScheduledTasksAtThisRoot[indexOfThisTaskInTheTree]++;
        if (!delayedPortion) {
            theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = tasks[b].energy();
            theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = tasks[b].envelopeForUpperBound(resourceCapacity);
            theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = (resourceCapacity - cForCenv) * tasks[b].latestCompletionTime() - tasks[b].energy();
            for (int u = 1; u <= maximumDelayPerNode; u++) {
                theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
            }
        } else if (delayedPortion && !unscheduleDelayedPortion) {
            theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = 0;
            theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = INFINITY;
            theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = INFINITY;
            for (int u = 1; u <= maximumDelayPerNode; u++) {

                theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = tasks[b].energyOnlyForTheDelayedPortion();
                theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = tasks[b].envelopeForUpperBoundForTheDelayedPortion(resourceCapacity);
                theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = (resourceCapacity - cForCenv) * tasks[b].delayedLatestCompletionTime() - tasks[b].energyOnlyForTheDelayedPortion();
            }
        } else if (delayedPortion && unscheduleDelayedPortion) {
            theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = 0;
            theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = INFINITY;
            theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = INFINITY;
            for (int u = 1; u <= maximumDelayPerNode; u++) {

                theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = 0;
                theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = INFINITY;
                theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = INFINITY;
            }
        }
        updateInnerNodesUppebounds(indexOfThisTaskInTheTree);
        return theta_envelope_of_nodes_upperbound[0][0];
    }

    private int updateLeafWithCForUpperBoundNewApproach(int task_index, int b, int cForCenv, boolean delayedPortion, boolean unscheduleDelayedPortion,
            int dimensionOfCapacity) {

        int indexOfThisTaskInTheTree = firstIndexOnTheLowestLevelUpperbounds + task_index;
        numberOfScheduledTasksAtThisRoot[indexOfThisTaskInTheTree]++;
        if (!delayedPortion) {
            theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = tasks[b].energy();
            theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = tasks[b].envelopeForUpperBound(resourceCapacity);
            // theta_c_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = (resourceCapacity - cForCenv) * tasks[b].latestCompletionTime() -  tasks[b].energy();

            theta_c_envelope_of_nodes_upperbound_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[b].latestCompletionTime() - tasks[b].energy();

            for (int u = 1; u <= maximumDelayPerNode; u++) {
                theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                theta_c_envelope_of_nodes_upperbound_new_approach[indexOfThisTaskInTheTree][u][dimensionOfCapacity] = theta_c_envelope_of_nodes_upperbound_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity];
            }
        } else if (delayedPortion && !unscheduleDelayedPortion) {
            theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = 0;
            theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = INFINITY;
            theta_c_envelope_of_nodes_upperbound_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = INFINITY;
            for (int u = 1; u <= maximumDelayPerNode; u++) {

                theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = tasks[b].energyOnlyForTheDelayedPortion();
                theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = tasks[b].envelopeForUpperBoundForTheDelayedPortion(resourceCapacity);
                theta_c_envelope_of_nodes_upperbound_new_approach[indexOfThisTaskInTheTree][u][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[b].delayedLatestCompletionTime() - tasks[b].energyOnlyForTheDelayedPortion();
            }
        } else if (delayedPortion && unscheduleDelayedPortion) {
            theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = 0;
            theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = INFINITY;
            theta_c_envelope_of_nodes_upperbound_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = INFINITY;
            for (int u = 1; u <= maximumDelayPerNode; u++) {

                theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = 0;
                theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = INFINITY;
                theta_c_envelope_of_nodes_upperbound_new_approach[indexOfThisTaskInTheTree][u][dimensionOfCapacity] = INFINITY;
            }
        }
        updateInnerNodesUppeboundsNewApproach(indexOfThisTaskInTheTree, dimensionOfCapacity);
        return theta_envelope_of_nodes_upperbound[0][0];
    }

    private void updateInnerNodesUppebounds(int indexOfThisTaskInTheTree) {

        int levelCounter = 0;
        int allowedMaximumDelayedForThisLevel;
        int assumedMaximumDelayForThisIteration;  // k
        int counterToReachAssumedMaximumDelayForThisIteration;  // j
        int indexOfParentInTheTree = (indexOfThisTaskInTheTree - 1) / 2;
        int indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
        int indexOfRightChild = indexOfLeftChild + 1;
        do {
            numberOfScheduledTasksAtThisRoot[indexOfParentInTheTree]++;
            levelCounter++;    //i (i'th level from below and h-i'th level from above.
            allowedMaximumDelayedForThisLevel = maximumNumberOfDelayedTasks;  //z (the possible number of delays for the tasks rooted at the subtree rooted at this level.)
            for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++) {
                int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration;
                int energyOfCurrentCase = 0;
                int envelopeOfCurrentCase = INFINITY;
                int cEnvelopeOfCurrentCase = INFINITY;
                for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++) {
                    int candidate_energy = theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                    if (energyOfCurrentCase < candidate_energy) {
                        energyOfCurrentCase = theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                    }
                    if (assumedMaximumDelayForThisIteration <= 1 << (levelCounter - 1)) {
                        int candidate_envelope = Math.min(theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]);
                        if (envelopeOfCurrentCase > candidate_envelope) {
                            envelopeOfCurrentCase = candidate_envelope;
                        }
                        int candidate_c_envelope = Math.min(theta_c_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_c_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]);
                        if (cEnvelopeOfCurrentCase > candidate_c_envelope) {
                            cEnvelopeOfCurrentCase = candidate_c_envelope;
                        }
                    } else {
                        envelopeOfCurrentCase = theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                        cEnvelopeOfCurrentCase = theta_c_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                    }
                }
                theta_energy_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
                theta_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
                theta_c_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = cEnvelopeOfCurrentCase;
            }
            indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
            indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
            indexOfRightChild = indexOfLeftChild + 1;
        } while (levelCounter < heightInTheTreeOfUpperBounds);
    }

    private void updateInnerNodesUppeboundsNewApproach(int indexOfThisTaskInTheTree, int dimensionOfCapacity) {

        int levelCounter = 0;
        int allowedMaximumDelayedForThisLevel;
        int assumedMaximumDelayForThisIteration;  // k
        int counterToReachAssumedMaximumDelayForThisIteration;  // j
        int indexOfParentInTheTree = (indexOfThisTaskInTheTree - 1) / 2;
        int indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
        int indexOfRightChild = indexOfLeftChild + 1;
        do {
            numberOfScheduledTasksAtThisRoot[indexOfParentInTheTree]++;
            levelCounter++;    //i (i'th level from below and h-i'th level from above.
            allowedMaximumDelayedForThisLevel = maximumNumberOfDelayedTasks;  //z (the possible number of delays for the tasks rooted at the subtree rooted at this level.)
            for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++) {
                int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration;
                int energyOfCurrentCase = 0;
                int envelopeOfCurrentCase = INFINITY;
                int cEnvelopeOfCurrentCase = INFINITY;
                for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++) {
                    int candidate_energy = theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                    if (energyOfCurrentCase < candidate_energy) {
                        energyOfCurrentCase = theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                    }
                    if (assumedMaximumDelayForThisIteration <= 1 << (levelCounter - 1)) {
                        int candidate_envelope = Math.min(theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]);
                        if (envelopeOfCurrentCase > candidate_envelope) {
                            envelopeOfCurrentCase = candidate_envelope;
                        }
                        int candidate_c_envelope = Math.min(theta_c_envelope_of_nodes_upperbound_new_approach[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration][dimensionOfCapacity] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_c_envelope_of_nodes_upperbound_new_approach[indexOfLeftChild][assumedMaximumDelayForThisIteration][dimensionOfCapacity]);
                        if (cEnvelopeOfCurrentCase > candidate_c_envelope) {
                            cEnvelopeOfCurrentCase = candidate_c_envelope;
                        }
                    } else {
                        envelopeOfCurrentCase = theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                        cEnvelopeOfCurrentCase = theta_c_envelope_of_nodes_upperbound_new_approach[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration][dimensionOfCapacity] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                    }
                }
                theta_energy_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
                theta_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
                theta_c_envelope_of_nodes_upperbound_new_approach[indexOfParentInTheTree][assumedMaximumDelayForThisIteration][dimensionOfCapacity] = cEnvelopeOfCurrentCase;
            }
            indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
            indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
            indexOfRightChild = indexOfLeftChild + 1;
        } while (levelCounter < heightInTheTreeOfUpperBounds);
    }

// Complexity: O(k^2 log n)
    private void updateLeavesWithC(int index_in_est_sorted, int task_index, int cForCenv, boolean stateOfDelay, boolean unScheduleThisTask, int dimensionOfCapacity, int numberOfCapacitiesToProcess) {
        int indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + index_in_est_sorted;
        numberOfScheduledTasksAtThisRoot[indexOfThisTaskInTheTree]++;
        if (dimensionOfCapacity == 0) {
            if (unScheduleThisTask) {
                theta_energy_of_nodes[indexOfThisTaskInTheTree][0] = 0;
                theta_envelope_of_nodes[indexOfThisTaskInTheTree][0] = MINUS_INFINITY;
                theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = MINUS_INFINITY;
                for (int t = 1; t <= maximumDelayPerNode; t++) {
                    theta_energy_of_nodes[indexOfThisTaskInTheTree][t] = 0;
                    theta_envelope_of_nodes[indexOfThisTaskInTheTree][t] = MINUS_INFINITY;
                    theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][t][dimensionOfCapacity] = MINUS_INFINITY;
                }
            } else if (stateOfDelay) {
                theta_energy_of_nodes[indexOfThisTaskInTheTree][0] = tasks[task_index].energy();
                theta_envelope_of_nodes[indexOfThisTaskInTheTree][0] = tasks[task_index].envelope(resourceCapacity);
                theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[task_index].earliestStartingTime() + tasks[task_index].energy();
                for (int t = 1; t <= maximumDelayPerNode; t++) {
                    theta_energy_of_nodes[indexOfThisTaskInTheTree][t] = tasks[task_index].delayedEnergy();
                    theta_envelope_of_nodes[indexOfThisTaskInTheTree][t] = tasks[task_index].delayedEnvelope(resourceCapacity);
                    theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][t][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[task_index].earliestStartingTime() + tasks[task_index].delayedEnergy();
                }
            } else {
                theta_energy_of_nodes[indexOfThisTaskInTheTree][0] = tasks[task_index].energy();
                theta_envelope_of_nodes[indexOfThisTaskInTheTree][0] = tasks[task_index].envelope(resourceCapacity);
                theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[task_index].earliestStartingTime() + tasks[task_index].energy();
                for (int t = 1; t <= maximumDelayPerNode; t++) {
                    theta_energy_of_nodes[indexOfThisTaskInTheTree][t] = theta_energy_of_nodes[indexOfThisTaskInTheTree][0];
                    theta_envelope_of_nodes[indexOfThisTaskInTheTree][t] = theta_envelope_of_nodes[indexOfThisTaskInTheTree][0];
                    theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][t][dimensionOfCapacity] = theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity];
                }
            }
        } else {
            if (unScheduleThisTask) {
                theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = MINUS_INFINITY;
                for (int t = 1; t <= maximumDelayPerNode; t++) {
                    theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][t][dimensionOfCapacity] = MINUS_INFINITY;
                }
            } else if (stateOfDelay) {
                theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[task_index].earliestStartingTime() + tasks[task_index].energy();
                for (int t = 1; t <= maximumDelayPerNode; t++) {
                    theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][t][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[task_index].earliestStartingTime() + tasks[task_index].delayedEnergy();
                }
            } else {
                theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity] = (resourceCapacity - cForCenv) * tasks[task_index].earliestStartingTime() + tasks[task_index].energy();
                for (int t = 1; t <= maximumDelayPerNode; t++) {
                    theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][t][dimensionOfCapacity] = theta_c_envelope_of_nodes_new_approach[indexOfThisTaskInTheTree][0][dimensionOfCapacity];

                }
            }
        }
        updateInnerNodes(indexOfThisTaskInTheTree, true, dimensionOfCapacity, numberOfCapacitiesToProcess);
    }

    public void scheduleSortedWithIndicesTasks(int a, int energy, int envelope, int delayed_energy, int delayed_envelope, int dimensionOfCapacity, int numberOfCapacitiesToProcess, boolean activateNewApproach) {
        UpdateLeaves(a, energy, envelope, delayed_energy, delayed_envelope, dimensionOfCapacity, numberOfCapacitiesToProcess, activateNewApproach);
    }

    public void scheduleSortedWithIndicesTasks2(int a, int energy, int envelope, int delayed_energy, int delayed_envelope, int dimensionOfCapacity, int numberOfCapacitiesToProcess, int delayed_energy_contribution_val, boolean delayedcaseIsConsidered, boolean activateNewApproach) {
        UpdateLeaves2(a, energy, envelope, delayed_energy, delayed_envelope, dimensionOfCapacity, numberOfCapacitiesToProcess, delayed_energy_contribution_val, delayedcaseIsConsidered, activateNewApproach);
    }

    public int scheduleTaskWithCForUpperBound(int a, int b, int c, boolean delayedPortion, boolean unscheduleDelayedPortion) {
        return updateLeafWithCForUpperBound(a, b, c, delayedPortion, unscheduleDelayedPortion);
    }

    public int scheduleTaskWithCForUpperBoundNewApproach(int a, int b, int c, boolean delayedPortion, boolean unscheduleDelayedPortion, int dimensionOfCapacity, int numberOfCapacitiesToProcess) {
        return updateLeafWithCForUpperBoundNewApproach(a, b, c, delayedPortion, unscheduleDelayedPortion, dimensionOfCapacity);
    }

    public void scheduleWithC(int index_in_est_sorted, int task_index, int c, boolean stateOfDelay, boolean unScheduleThisTask, int dimensionOfCapacity, int numberOfCapacitiesToProcess) {
        updateLeavesWithC(index_in_est_sorted, task_index, c, stateOfDelay, unScheduleThisTask, dimensionOfCapacity, numberOfCapacitiesToProcess);
    }

    public static int height(int numberOfNodes) {
        int solution = 0;
        if ((numberOfNodes & (numberOfNodes - 1)) == 0) {
            while (numberOfNodes - 1 > 0) {
                solution++;
                numberOfNodes /= 2;
            }
        } else {
            while (numberOfNodes > 0) {
                solution++;
                numberOfNodes /= 2;
            }
        }
        return solution;
    }

    public int requiredEnvelopeInTheRoot(int a) {
        return (theta_envelope_of_nodes[0][a]);
    }

    public int Env_Root() {
        return Env_Root;
    }

    public int Env_w_Root() {
        return Env_w[0];
    }

    public int requiredCEnvelopeInTheRoot(int a) {
        return (theta_c_envelope_of_nodes[0][a]);
    }

    public int[][] energyOfTheWholeTree() {
        return theta_energy_of_nodes;
    }

    public static double log2(int n) {
        return (Math.log(n) / Math.log(2));
    }

    // Complexity: O(k^2 log n)
    public int maxEst(int c, int k, int tValue, boolean responsibleTaskIsDelayed) {

        boolean branchRight;
        int upToDownCounter = 0;
        int nodeIndexOfTheTree = 0;
        int max_Env_c = (resourceCapacity - c) * tValue;
        int leftNode;
        int rightNode;
        if (responsibleTaskIsDelayed) {
            k--;
        }
        while (upToDownCounter < height) {
            branchRight = false;
            leftNode = 2 * nodeIndexOfTheTree + 1;
            rightNode = leftNode + 1;
            for (int j = 0; j <= k; j++) {
                if (theta_c_envelope_of_nodes[rightNode][j] + aggregatedEnergy1[k - j] > max_Env_c) {
                    nodeIndexOfTheTree = rightNode;
                    branchRight = true;
                    break;
                }
            }
            if (!branchRight) {
                nodeIndexOfTheTree = leftNode;
                for (int j = 0; j <= k; j++) {
                    int current_energy = 0;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_energy = aggregatedEnergy1[i] + theta_energy_of_nodes[nodeIndexOfTheTree + 1][j - i];
                        } else {
                            int candidate_energy = Math.max(aggregatedEnergy1[i] + theta_energy_of_nodes[nodeIndexOfTheTree + 1][j - i], aggregatedEnergy2[j - 1]);
                            if (current_energy < candidate_energy) {
                                current_energy = candidate_energy;
                            }
                        }
                    }
                    aggregatedEnergy2[j] = current_energy;
                }
                System.arraycopy(aggregatedEnergy2, 0, aggregatedEnergy1, 0, aggregatedEnergy1.length);
            }
            upToDownCounter++;
        }
        for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
            aggregatedEnergy1[i] = 0;
            aggregatedEnergy2[i] = 0;
        }
        return nodeIndexOfTheTree;
    }

    public int maxEstNewApproach(int c, int k, int tValue, boolean responsibleTaskIsDelayed, int dimensionOfCapacity) {
        boolean branchRight;
        int upToDownCounter = 0;
        int nodeIndexOfTheTree = 0;
        int max_Env_c = (resourceCapacity - c) * tValue;
        int leftNode;
        int rightNode;
        if (responsibleTaskIsDelayed) {
            k--;
        }

        while (upToDownCounter < height) {
            branchRight = false;
            leftNode = 2 * nodeIndexOfTheTree + 1;
            rightNode = leftNode + 1;
            for (int j = 0; j <= k; j++) {
                if (theta_c_envelope_of_nodes_new_approach[rightNode][j][dimensionOfCapacity] + aggregatedEnergy1[k - j] > max_Env_c) {
                    nodeIndexOfTheTree = rightNode;
                    branchRight = true;
                    break;
                }
            }
            if (!branchRight) {
                nodeIndexOfTheTree = leftNode;
                for (int j = 0; j <= k; j++) {
                    int current_energy = 0;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_energy = aggregatedEnergy1[i] + theta_energy_of_nodes[nodeIndexOfTheTree + 1][j - i];
                        } else {
                            int candidate_energy = Math.max(aggregatedEnergy1[i] + theta_energy_of_nodes[nodeIndexOfTheTree + 1][j - i], aggregatedEnergy2[j - 1]);
                            if (current_energy < candidate_energy) {
                                current_energy = candidate_energy;
                            }
                        }
                    }
                    aggregatedEnergy2[j] = current_energy;
                }
                System.arraycopy(aggregatedEnergy2, 0, aggregatedEnergy1, 0, aggregatedEnergy1.length);
            }
            upToDownCounter++;
        }
        for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
            aggregatedEnergy1[i] = 0;
            aggregatedEnergy2[i] = 0;
        }
        return nodeIndexOfTheTree;
    }

    public int minLct(int c, int k, int estValue, boolean responsibleTaskIsDelayed) {

        boolean branchLeft;
        int nodeIndexOfTheTree = 0;
        int max_Env_c = (resourceCapacity - c) * estValue;
        int leftNode;
        int rightNode;
        if (responsibleTaskIsDelayed) {
            k--;
        }
        while (nodeIndexOfTheTree < firstIndexOnTheLowestLevelUpperbounds) {
            branchLeft = false;
            leftNode = 2 * nodeIndexOfTheTree + 1;
            rightNode = leftNode + 1;
            for (int j = 0; j <= k; j++) {
                if (theta_c_envelope_of_nodes_upperbound[leftNode][j] - aggregatedEnergy1_upperbound[k - j] < max_Env_c) {
                    nodeIndexOfTheTree = leftNode;
                    branchLeft = true;
                    break;
                }
            }
            if (!branchLeft) {
                nodeIndexOfTheTree = rightNode;
                for (int j = 0; j <= k; j++) {
                    int current_energy = 0;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_energy = aggregatedEnergy1_upperbound[i] + theta_energy_of_nodes_upperbound[nodeIndexOfTheTree - 1][j - i];
                        } else {
                            int candidate_energy = Math.max(aggregatedEnergy1_upperbound[i] + theta_energy_of_nodes_upperbound[nodeIndexOfTheTree - 1][j - i], aggregatedEnergy2_upperbound[j - 1]);
                            if (current_energy < candidate_energy) {
                                current_energy = candidate_energy;
                            }
                        }
                    }
                    aggregatedEnergy2_upperbound[j] = current_energy;
                }
                System.arraycopy(aggregatedEnergy2_upperbound, 0, aggregatedEnergy1_upperbound, 0, aggregatedEnergy1_upperbound.length);
            }
        }
        for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
            aggregatedEnergy1_upperbound[i] = 0;
            aggregatedEnergy2_upperbound[i] = 0;
        }
        return nodeIndexOfTheTree;
    }

    public int minLctNewApproach(int c, int k, int estValue, boolean responsibleTaskIsDelayed, int dimensionOfCapacity) {

        boolean branchLeft;
        int nodeIndexOfTheTree = 0;
        int max_Env_c = (resourceCapacity - c) * estValue;
        int leftNode;
        int rightNode;
        if (responsibleTaskIsDelayed) {
            k--;
        }
        while (nodeIndexOfTheTree < firstIndexOnTheLowestLevelUpperbounds) {
            branchLeft = false;
            leftNode = 2 * nodeIndexOfTheTree + 1;
            rightNode = leftNode + 1;
            for (int j = 0; j <= k; j++) {
                if (theta_c_envelope_of_nodes_upperbound_new_approach[leftNode][j][dimensionOfCapacity] - aggregatedEnergy1_upperbound[k - j] < max_Env_c) {
                    nodeIndexOfTheTree = leftNode;
                    branchLeft = true;
                    break;
                }
            }
            if (!branchLeft) {
                nodeIndexOfTheTree = rightNode;
                for (int j = 0; j <= k; j++) {
                    int current_energy = 0;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_energy = aggregatedEnergy1_upperbound[i] + theta_energy_of_nodes_upperbound[nodeIndexOfTheTree - 1][j - i];
                        } else {
                            int candidate_energy = Math.max(aggregatedEnergy1_upperbound[i] + theta_energy_of_nodes_upperbound[nodeIndexOfTheTree - 1][j - i], aggregatedEnergy2_upperbound[j - 1]);
                            if (current_energy < candidate_energy) {
                                current_energy = candidate_energy;
                            }
                        }
                    }
                    aggregatedEnergy2_upperbound[j] = current_energy;
                }
                System.arraycopy(aggregatedEnergy2_upperbound, 0, aggregatedEnergy1_upperbound, 0, aggregatedEnergy1_upperbound.length);
            }
        }
        for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
            aggregatedEnergy1_upperbound[i] = 0;
            aggregatedEnergy2_upperbound[i] = 0;
        }
        return nodeIndexOfTheTree;
    }

    // Complexity: O(k^2 * log n )
    public int computeEnvelope(int k, int nodeIndexOfTheTree, boolean responsibleTaskIsDelayed) {
        alpha_energy1[0] = theta_energy_of_nodes[nodeIndexOfTheTree][0];
        alpha_energy1[1] = theta_energy_of_nodes[nodeIndexOfTheTree][1];
        alpha_envelope1[0] = theta_envelope_of_nodes[nodeIndexOfTheTree][0];
        alpha_envelope1[1] = theta_envelope_of_nodes[nodeIndexOfTheTree][1];
        if (k > 1) {
            for (int g = 2; g <= maximumNumberOfDelayedTasks; g++) {
                alpha_energy1[g] = theta_energy_of_nodes[nodeIndexOfTheTree][1];
                alpha_envelope1[g] = theta_envelope_of_nodes[nodeIndexOfTheTree][1];
            }
        }
        for (int levelCounter = 0; levelCounter < height; levelCounter++) {
            if (nodeIndexOfTheTree % 2 == 1) {
                for (int j = 0; j <= k; j++) {
                    int current_energy = 0;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_energy = beta_energy1[i] + theta_energy_of_nodes[nodeIndexOfTheTree + 1][j - i];
                        } else {
                            int candidate_energy = Math.max(beta_energy1[i] + theta_energy_of_nodes[nodeIndexOfTheTree + 1][j - i], beta_energy2[j - 1]);
                            if (current_energy < candidate_energy) {
                                current_energy = candidate_energy;
                            }
                        }
                    }
                    beta_energy2[j] = current_energy;
                }
                System.arraycopy(beta_energy2, 0, beta_energy1, 0, beta_energy1.length);
            } else {
                for (int j = 0; j <= k; j++) {
                    int current_alpha_envelope = MINUS_INFINITY;
                    int current_alpha_energy = MINUS_INFINITY;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_alpha_envelope = Math.max(theta_envelope_of_nodes[nodeIndexOfTheTree - 1][i] + alpha_energy1[j - i], alpha_envelope1[j]);
                            current_alpha_energy = theta_energy_of_nodes[nodeIndexOfTheTree - 1][i] + alpha_energy1[j - i];
                        } else {
                            int candidate_alpha_envelope = Math.max(Math.max(theta_envelope_of_nodes[nodeIndexOfTheTree - 1][i] + alpha_energy1[j - i], alpha_envelope1[j]), alpha_envelope2[j - 1]);
                            int candidate_alpha_energy = Math.max(theta_energy_of_nodes[nodeIndexOfTheTree - 1][i] + alpha_energy1[j - i], alpha_energy2[j - 1]);
                            if (current_alpha_envelope < candidate_alpha_envelope) {
                                current_alpha_envelope = candidate_alpha_envelope;
                            }
                            if (current_alpha_energy < candidate_alpha_energy) {
                                current_alpha_energy = candidate_alpha_energy;
                            }
                        }
                    }
                    alpha_envelope2[j] = current_alpha_envelope;
                    alpha_energy2[j] = current_alpha_energy;

                }
                for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
                    alpha_envelope1[i] = alpha_envelope2[i];
                    alpha_energy1[i] = alpha_energy2[i];
                }
            }
            nodeIndexOfTheTree = (nodeIndexOfTheTree - 1) / 2;
        }
        int maxValue = MINUS_INFINITY;
        int temp;
        for (int i = 0; i <= k; i++) {
            if (alpha_envelope1[i] == MINUS_INFINITY) {
                alpha_envelope1[i] = 0;
            }
        }
        if (!responsibleTaskIsDelayed) {
            for (int i = 0; i <= k; i++) {
                temp = beta_energy1[i] + alpha_envelope1[k - i];

                if (maxValue < temp) {
                    maxValue = temp;
                }
            }
        } else {
            for (int i = 0; i <= k - 1; i++) {
                temp = beta_energy1[i] + alpha_envelope1[k - 1 - i];
                if (maxValue < temp) {
                    maxValue = temp;
                }
            }
        }
        for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
            alpha_energy1[i] = 0;
            alpha_envelope1[i] = 0;
            beta_energy1[i] = 0;
            alpha_energy2[i] = 0;
            alpha_envelope2[i] = 0;
            beta_energy2[i] = 0;
        }
        return maxValue;

    }

    public int computeEnvelopeForUpperbound(int k, int nodeIndexOfTheTree, boolean responsibleTaskIsDelayed) {

        alpha_energy1_upperbound[0] = theta_energy_of_nodes_upperbound[nodeIndexOfTheTree][0];
        alpha_energy1_upperbound[1] = theta_energy_of_nodes_upperbound[nodeIndexOfTheTree][1];
        alpha_envelope1_upperbound[0] = theta_envelope_of_nodes_upperbound[nodeIndexOfTheTree][0];
        alpha_envelope1_upperbound[1] = theta_envelope_of_nodes_upperbound[nodeIndexOfTheTree][1];

        if (k > 1) {
            for (int g = 2; g <= maximumNumberOfDelayedTasks; g++) {
                alpha_energy1_upperbound[g] = theta_energy_of_nodes_upperbound[nodeIndexOfTheTree][1];
                alpha_envelope1_upperbound[g] = theta_envelope_of_nodes_upperbound[nodeIndexOfTheTree][1];
            }
        }
        for (int levelCounter = 0; levelCounter < heightInTheTreeOfUpperBounds; levelCounter++) {
            if (nodeIndexOfTheTree % 2 == 0) {
                for (int j = 0; j <= k; j++) {
                    int current_energy = 0;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_energy = beta_energy1_upperbound[i] + theta_energy_of_nodes_upperbound[nodeIndexOfTheTree - 1][j - i];
                        } else {
                            int candidate_energy = Math.max(beta_energy1_upperbound[i] + theta_energy_of_nodes_upperbound[nodeIndexOfTheTree - 1][j - i], beta_energy2_upperbound[j - 1]);
                            if (current_energy < candidate_energy) {
                                current_energy = candidate_energy;
                            }
                        }
                    }
                    beta_energy2_upperbound[j] = current_energy;
                }
                System.arraycopy(beta_energy2_upperbound, 0, beta_energy1_upperbound, 0, beta_energy1_upperbound.length);
            } else {
                for (int j = 0; j <= k; j++) {
                    int current_alpha_envelope = INFINITY;
                    int current_alpha_energy = MINUS_INFINITY;
                    for (int i = 0; i <= j; i++) {
                        if (j == 0) {
                            current_alpha_envelope = Math.min(theta_envelope_of_nodes_upperbound[nodeIndexOfTheTree + 1][i] - alpha_energy1_upperbound[j - i], alpha_envelope1_upperbound[j]);
                            current_alpha_energy = theta_energy_of_nodes_upperbound[nodeIndexOfTheTree + 1][i] + alpha_energy1_upperbound[j - i];
                        } else {
                            int candidate_alpha_envelope = Math.min(Math.min(theta_envelope_of_nodes_upperbound[nodeIndexOfTheTree + 1][i] - alpha_energy1_upperbound[j - i], alpha_envelope1_upperbound[j]), alpha_envelope2_upperbound[j - 1]);
                            int candidate_alpha_energy = Math.max(theta_energy_of_nodes_upperbound[nodeIndexOfTheTree + 1][i] + alpha_energy1_upperbound[j - i], alpha_energy2_upperbound[j - 1]);
                            if (current_alpha_envelope > candidate_alpha_envelope) {
                                current_alpha_envelope = candidate_alpha_envelope;
                            }
                            if (current_alpha_energy < candidate_alpha_energy) {
                                current_alpha_energy = candidate_alpha_energy;
                            }
                        }
                    }
                    alpha_envelope2_upperbound[j] = current_alpha_envelope;
                    alpha_energy2_upperbound[j] = current_alpha_energy;

                }
                for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
                    alpha_envelope1_upperbound[i] = alpha_envelope2_upperbound[i];
                    alpha_energy1_upperbound[i] = alpha_energy2_upperbound[i];
                }
            }
            nodeIndexOfTheTree = (nodeIndexOfTheTree - 1) / 2;
        }
        int minValue = INFINITY;
        int temp;
        for (int i = 0; i <= k; i++) {
            if (alpha_envelope1_upperbound[i] == INFINITY) {
                alpha_envelope1_upperbound[i] = 0;
            }
        }
        if (!responsibleTaskIsDelayed) {
            for (int i = 0; i <= k; i++) {
                temp = -beta_energy1_upperbound[i] + alpha_envelope1_upperbound[k - i];
                if (minValue > temp) {
                    minValue = temp;
                }
            }
        } else {
            for (int i = 0; i <= k - 1; i++) {
                temp = -beta_energy1_upperbound[i] + alpha_envelope1_upperbound[k - 1 - i];
                if (minValue > temp) {
                    minValue = temp;
                }
            }
        }

        for (int i = 0; i <= maximumNumberOfDelayedTasks; i++) {
            alpha_energy1_upperbound[i] = 0;
            alpha_envelope1_upperbound[i] = 0;
            beta_energy1_upperbound[i] = 0;
            alpha_energy2_upperbound[i] = 0;
            alpha_envelope2_upperbound[i] = 0;
            beta_energy2_upperbound[i] = 0;
        }
        return minValue;

    }

    private void updateInnerNodes(int indexOfThisTaskInTheTree, boolean forEF, int dimensionOfCapacity, int numberOfCapacitiesToProcess) {
        int cEnvelopeOfCurrentCase = 0;
        int assumedMaximumDelayForThisIteration;  // k
        int indexOfParentInTheTree = indexOfThisTaskInTheTree;
        int indexOfLeftChild;
        int indexOfRightChild;

        if (dimensionOfCapacity == 0) {
            while (indexOfParentInTheTree != 0) {
                indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
                indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                indexOfRightChild = indexOfLeftChild + 1;
                for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= maximumNumberOfDelayedTasks; assumedMaximumDelayForThisIteration++) {
                    int energyOfCurrentCase = 0;
                    int envelopeOfCurrentCase = MINUS_INFINITY;
                    if (forEF) {
                        cEnvelopeOfCurrentCase = MINUS_INFINITY;
                    }
                    for (int counterToReachAssumedMaximumDelayForThisIteration = 0; counterToReachAssumedMaximumDelayForThisIteration <= assumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++) {
                        int candidate_energy = theta_energy_of_nodes[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                        if (candidate_energy > energyOfCurrentCase) {
                            energyOfCurrentCase = candidate_energy;
                        }
                        int candidate_enveloppe = Math.max(theta_envelope_of_nodes[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration]);
                        if (assumedMaximumDelayForThisIteration > 0) {
                            candidate_enveloppe = Math.max(candidate_enveloppe, theta_envelope_of_nodes[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1]);
                        }
                        if (candidate_enveloppe > envelopeOfCurrentCase) {
                            envelopeOfCurrentCase = candidate_enveloppe;
                        }
                        if (forEF) {
                            int candidate_c_enveloppe = Math.max(theta_c_envelope_of_nodes[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_c_envelope_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration]);
                            candidate_c_enveloppe = Math.max(theta_c_envelope_of_nodes_new_approach[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration][dimensionOfCapacity] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_c_envelope_of_nodes_new_approach[indexOfRightChild][assumedMaximumDelayForThisIteration][dimensionOfCapacity]);
                            if (assumedMaximumDelayForThisIteration > 0) {
                                candidate_c_enveloppe = Math.max(candidate_c_enveloppe, theta_c_envelope_of_nodes_new_approach[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1][dimensionOfCapacity]);
                            }
                            if (candidate_c_enveloppe > cEnvelopeOfCurrentCase) {
                                cEnvelopeOfCurrentCase = candidate_c_enveloppe;
                            }
                        }
                    }
                    theta_energy_of_nodes[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
                    theta_envelope_of_nodes[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
                    if (forEF) {
                        theta_c_envelope_of_nodes_new_approach[indexOfParentInTheTree][assumedMaximumDelayForThisIteration][dimensionOfCapacity] = cEnvelopeOfCurrentCase;
                    }
                }
            }
        } else {
            while (indexOfParentInTheTree != 0) {
                indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
                indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                indexOfRightChild = indexOfLeftChild + 1;
                for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= maximumNumberOfDelayedTasks; assumedMaximumDelayForThisIteration++) {
                    if (forEF) {
                        cEnvelopeOfCurrentCase = MINUS_INFINITY;
                    }
                    for (int counterToReachAssumedMaximumDelayForThisIteration = 0; counterToReachAssumedMaximumDelayForThisIteration <= assumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++) {
                        if (forEF) {
                            int candidate_c_enveloppe = Math.max(theta_c_envelope_of_nodes_new_approach[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration][dimensionOfCapacity] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_c_envelope_of_nodes_new_approach[indexOfRightChild][assumedMaximumDelayForThisIteration][dimensionOfCapacity]);

                            if (assumedMaximumDelayForThisIteration > 0) {
                                candidate_c_enveloppe = Math.max(candidate_c_enveloppe, theta_c_envelope_of_nodes_new_approach[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1][dimensionOfCapacity]);
                            }
                            if (candidate_c_enveloppe > cEnvelopeOfCurrentCase) {
                                cEnvelopeOfCurrentCase = candidate_c_enveloppe;
                            }
                        }
                    }
                    if (forEF) {
                        theta_c_envelope_of_nodes_new_approach[indexOfParentInTheTree][assumedMaximumDelayForThisIteration][dimensionOfCapacity] = cEnvelopeOfCurrentCase;
                    }
                }
            }
        }
    }

    private void updateInnerNodes2(int indexOfThisTaskInTheTree, boolean forEF, int dimensionOfCapacity, boolean delayedcaseIsConsidered, int delayed_energy_contribution_val, int numberOfCapacitiesToProcess) {
        int cEnvelopeOfCurrentCase = 0;
        int assumedMaximumDelayForThisIteration;  // k
        int indexOfParentInTheTree = indexOfThisTaskInTheTree;
        int indexOfLeftChild;
        int indexOfRightChild;
        if (dimensionOfCapacity == 0) {
            while (indexOfParentInTheTree != 0) {
                indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
                indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                indexOfRightChild = indexOfLeftChild + 1;
                int z1 = maximumNumberOfDelayedTasks - 1;
                int z2 = maximumNumberOfDelayedTasks - 1;
                int z3 = 0;
                int val_taken_from_first_list;
                int val_taken_from_second_list;
            //    if (delayedcaseIsConsidered && delayed_energy_contribution_val != 0) {
                    while (z1 >= 0 && h_w_vector_of_nodes[indexOfLeftChild][z1] == 0) {
                        z1--;
                    }
                    while (z2 >= 0 && h_w_vector_of_nodes[indexOfRightChild][z2] == 0) {
                        z2--;
                    }
                    if (z1 >= 0 && z2 >= 0) {
                        int i = 0;
                        int j = 0;
                        while (i <= z1) {
                            while (j <= z2) {
                                if (h_w_vector_of_nodes[indexOfLeftChild][i] >= h_w_vector_of_nodes[indexOfRightChild][j] //  &&
                                        ) {
                                    h_w_vector_of_nodes[indexOfParentInTheTree][z3] = h_w_vector_of_nodes[indexOfLeftChild][i];
                                    z3++;
                                    i++;
                                    if (i < maximumNumberOfDelayedTasks && h_w_vector_of_nodes[indexOfLeftChild][i] == 0 && z3 < maximumNumberOfDelayedTasks) {
                                        h_w_vector_of_nodes[indexOfParentInTheTree][z3] = h_w_vector_of_nodes[indexOfRightChild][j];
                                        z3++;
                                    }
                                } else if (z3 < maximumNumberOfDelayedTasks) {
                                    h_w_vector_of_nodes[indexOfParentInTheTree][z3] = h_w_vector_of_nodes[indexOfRightChild][j];
                                    z3++;
                                    if (j + 1 > z2 && z3 < maximumNumberOfDelayedTasks) {
                                        h_w_vector_of_nodes[indexOfParentInTheTree][z3] = h_w_vector_of_nodes[indexOfLeftChild][i];
                                        z3++;
                                    }
                                }
                                j++;
                            }
                            i++;
                        }
                    } else {
                        while (z1 >= 0) {
                            val_taken_from_first_list = h_w_vector_of_nodes[indexOfLeftChild][z1];
                            if (z2 == -1) {
                                while (z1 >= 0) {
                                    val_taken_from_first_list = h_w_vector_of_nodes[indexOfLeftChild][z1];
                                    h_w_vector_of_nodes[indexOfParentInTheTree][z1] = val_taken_from_first_list;
                                    z1--;
                                }
                            } else {
                                while (z2 >= 0) {
                                    val_taken_from_second_list = h_w_vector_of_nodes[indexOfRightChild][z2];
                                    if (val_taken_from_second_list < val_taken_from_first_list) {
                                        h_w_vector_of_nodes[indexOfParentInTheTree][z3] = val_taken_from_first_list;
                                        z3++;
                                        z1--;
                                        if (z2 - 1 < 0) {
                                            h_w_vector_of_nodes[indexOfParentInTheTree][z3] = val_taken_from_second_list;
                                            z3++;
                                        }
                                    } else {
                                        h_w_vector_of_nodes[indexOfParentInTheTree][z3] = val_taken_from_second_list;
                                        z3++;
                                        if (z2 - 1 < 0) {
                                            h_w_vector_of_nodes[indexOfParentInTheTree][z3] = val_taken_from_first_list;
                                            z3++;
                                        }
                                    }
                                    z2--;
                                }
                            }
                            z1--;
                        }
                        while (z2 >= 0) {
                            val_taken_from_second_list = h_w_vector_of_nodes[indexOfRightChild][z2];
                            h_w_vector_of_nodes[indexOfParentInTheTree][z3] = val_taken_from_second_list;
                            z3++;
                            z2--;
                        }
                    }

                    for (int o = 0; o < maximumNumberOfDelayedTasks; o++) {
                        H_w_vector_of_nodes[indexOfParentInTheTree][o] = h_w_vector_of_nodes[indexOfParentInTheTree][o];
                        if (o > 0) {
                            H_w_vector_of_nodes[indexOfParentInTheTree][o] += H_w_vector_of_nodes[indexOfParentInTheTree][o - 1];
                        }
                    }
              //  }
                workOnUpdating(indexOfLeftChild, indexOfRightChild, indexOfParentInTheTree);
                updateEnv_w(indexOfLeftChild, indexOfRightChild, indexOfParentInTheTree);
            }
        } else {
            while (indexOfParentInTheTree != 0) {
                indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
                indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                indexOfRightChild = indexOfLeftChild + 1;
                for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= maximumNumberOfDelayedTasks; assumedMaximumDelayForThisIteration++) {
                    if (forEF) {
                        cEnvelopeOfCurrentCase = MINUS_INFINITY;
                    }
                    for (int counterToReachAssumedMaximumDelayForThisIteration = 0; counterToReachAssumedMaximumDelayForThisIteration <= assumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++) {
                        if (forEF) {
                            int candidate_c_enveloppe = Math.max(theta_c_envelope_of_nodes_new_approach[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration][dimensionOfCapacity] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_c_envelope_of_nodes_new_approach[indexOfRightChild][assumedMaximumDelayForThisIteration][dimensionOfCapacity]);

                            if (assumedMaximumDelayForThisIteration > 0) {
                                candidate_c_enveloppe = Math.max(candidate_c_enveloppe, theta_c_envelope_of_nodes_new_approach[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1][dimensionOfCapacity]);
                            }
                            if (candidate_c_enveloppe > cEnvelopeOfCurrentCase) {
                                cEnvelopeOfCurrentCase = candidate_c_enveloppe;
                            }
                        }
                    }
                    if (forEF) {
                        theta_c_envelope_of_nodes_new_approach[indexOfParentInTheTree][assumedMaximumDelayForThisIteration][dimensionOfCapacity] = cEnvelopeOfCurrentCase;
                    }
                }
            }
        }
        //    flagForRightChildSituationIsActivated = false;
        int val;
        if (maximumNumberOfDelayedTasks == 1) {
            val = Math.max(
                    theta_envelope_of_nodes[1][0] + H_w_vector_of_nodes[1][0] + theta_energy_of_nodes[2][0],
                    Math.max(
                            theta_envelope_of_nodes[1][0] + theta_energy_of_nodes[2][0] + H_w_vector_of_nodes[2][0],
                            // theta_envelope_of_nodes[2][0]
                            Math.max(theta_envelope_of_nodes[2][0], theta_envelope_of_nodes[2][1])
                    )
            );
            if (Env_Root < val) {
                Env_Root = val;
            }
        } else {
            for (int j = 0; j < maximumNumberOfDelayedTasks; j++) {
                val = Math.max(theta_envelope_of_nodes[1][0] + H_w_vector_of_nodes[1][j] + theta_energy_of_nodes[2][0] + H_w_vector_of_nodes[2][maximumNumberOfDelayedTasks - 1 - j],
                        theta_envelope_of_nodes[2][0]);
                if (Env_Root < val) {
                    Env_Root = val;
                }
            }

        }
    }

    public void workOnUpdating(int indexOfLeftChild, int indexOfRightChild, int indexOfParentInTheTree) {

        int assumedMaximumDelayForThisIteration;
        for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= maximumNumberOfDelayedTasks; assumedMaximumDelayForThisIteration++) //    if (assumedMaximumDelayForThisIteration == 0) 
        {
            {
                int energyOfCurrentCase = 0;
                int envelopeOfCurrentCase = MINUS_INFINITY;

                for (int counterToReachAssumedMaximumDelayForThisIteration = 0; counterToReachAssumedMaximumDelayForThisIteration <= assumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++) // if (counterToReachAssumedMaximumDelayForThisIteration == 0) 
                {
                    {
                        int candidate_energy = theta_energy_of_nodes[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                        if (candidate_energy > energyOfCurrentCase) {
                            energyOfCurrentCase = candidate_energy;
                        }
                        int candidate_enveloppe = Math.max(theta_envelope_of_nodes[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes[indexOfRightChild][assumedMaximumDelayForThisIteration]);
                        if (assumedMaximumDelayForThisIteration > 0) {
                            candidate_enveloppe = Math.max(candidate_enveloppe, theta_envelope_of_nodes[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1]);
                        }
                        if (candidate_enveloppe > envelopeOfCurrentCase) {
                            envelopeOfCurrentCase = candidate_enveloppe;
                        }
                    }
                    theta_energy_of_nodes[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
                    theta_envelope_of_nodes[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
                }
            }
        }

    }

    public void updateEnv_w(int indexOfLeftChild, int indexOfRightChild, int indexOfParentInTheTree) {
        int val;
        boolean rightChosen = false;
        if (maximumNumberOfDelayedTasks == 1) {
            int a1 = theta_envelope_of_nodes[indexOfLeftChild][0] + H_w_vector_of_nodes[indexOfLeftChild][0] + theta_energy_of_nodes[indexOfRightChild][0];
            int a2 = theta_envelope_of_nodes[indexOfLeftChild][0] + theta_energy_of_nodes[indexOfRightChild][0] + H_w_vector_of_nodes[indexOfRightChild][0];
            val = Math.max(a1, a2);
            if (Env_w[indexOfRightChild] < val) {
                Env_w[indexOfParentInTheTree] = val;
            } else {
                Env_w[indexOfParentInTheTree] = Env_w[indexOfRightChild];
                rightChosen = true;
            }
        } else {
            for (int j = 0; j < maximumNumberOfDelayedTasks; j++) {
                val = Math.max(theta_envelope_of_nodes[indexOfLeftChild][0] + H_w_vector_of_nodes[indexOfLeftChild][j] + theta_energy_of_nodes[indexOfRightChild][0] + H_w_vector_of_nodes[indexOfRightChild][maximumNumberOfDelayedTasks - 1 - j],
                        Env_w[indexOfRightChild]);
                Env_w[indexOfParentInTheTree] = val;
            }
        }

        if ( Env_w[indexOfParentInTheTree] == Env_w[indexOfRightChild]
              //  rightChosen
                && maximumDelayPerNode == 1 //   && h_w_vector_of_nodes[indexOfRightChild][0] >=  h_w_vector_of_nodes[indexOfLeftChild][0]
                ) {
            for (int b = 0; b < maximumDelayPerNode; b++) {
                h_w_vector_of_nodes[indexOfParentInTheTree][b] = h_w_vector_of_nodes[indexOfRightChild][b];
                H_w_vector_of_nodes[indexOfParentInTheTree][b] = H_w_vector_of_nodes[indexOfRightChild][b];
            }
//            if (Env_w[indexOfRightChild] != MINUS_INFINITY) {
//                flagForRightChildSituationIsActivated = true;
//            } else {
//                flagForRightChildSituationIsActivated = false;
//            }

        } else if (Env_w[indexOfRightChild] == MINUS_INFINITY
                && Env_w[indexOfParentInTheTree] == Env_w[indexOfLeftChild]
                && maximumDelayPerNode == 1) {
            for (int b = 0; b < maximumDelayPerNode; b++) {
                h_w_vector_of_nodes[indexOfParentInTheTree][b] = h_w_vector_of_nodes[indexOfLeftChild][b];
                H_w_vector_of_nodes[indexOfParentInTheTree][b] = H_w_vector_of_nodes[indexOfLeftChild][b];
            }
        }
    }
}
