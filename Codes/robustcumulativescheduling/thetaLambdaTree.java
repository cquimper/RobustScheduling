    package robustcumulativescheduling;

    import java.util.Comparator;

    public class thetaLambdaTree {
        Task[] tasks;
        private int theta_energy_of_nodes_lowerbound[][];
        private  int theta_envelope_of_nodes_lowerbound[][];
        private int theta_energy_of_nodes_upperbound[][];
        private  int theta_envelope_of_nodes_upperbound[][];
        private int lambda_energy_of_nodes_lowerbound[][];
        private int lambda_envelope_of_nodes_lowerbound[][];
        private int lambda_energy_of_nodes_upperbound[][];
        private int lambda_envelope_of_nodes_upperbound[][];
        private int positionOfThisTaskAndItsDelayedVersionInTheTree[];
        private final int numberOfTasks;
        private int heightInTheTreeOfLowerBounds;
        private int heightInTheTreeOfUpperBounds;
        private int firstIndexOnTheLowestLevel;
        private final int maximumNumberOfDelayedTasks;
        private int maximumDelayPerNode;
        private int numberOfNodesInTheTreeOfLowerBounds;
        private int numberOfNodesInTheTreeOfUpperBounds;
        private int[] indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound;
        private int[][] positionOfThisTasksAsALeafOfTLTForUpperbound;
        private int numberOfLeavesLowerBound;
                private int numberOfLeavesUpperbound;
        private final int resourceCapacity;
        private static final int Infinity = 715827882;
        private static final int minusInfinity = -715827882;
        Integer[] task_indices_P;
        Integer[] task_indices_for_lct;
        Integer[] task_indices_for_delayed_lct;
        NewArrayOfPrecedences L ;
        NewArrayOfPrecedences U ;

        public thetaLambdaTree(Task[] tasks, Integer[] estArr, int u, int C, int stateOfNegativeTasks) {
            this.resourceCapacity = C;
            this.tasks = tasks;
            this.numberOfTasks = tasks.length;
            this.maximumNumberOfDelayedTasks = u;
            if (maximumNumberOfDelayedTasks > numberOfTasks) {
                throw new IllegalArgumentException("No more than the number of tasks can be delayed!");
            }
            this.numberOfNodesInTheTreeOfLowerBounds = nextPowerOfTwoMinusOne(2 * numberOfTasks);
            this.numberOfNodesInTheTreeOfUpperBounds = nextPowerOfTwoMinusOne(4 * numberOfTasks);
            this.heightInTheTreeOfLowerBounds = height(numberOfTasks);
            this.heightInTheTreeOfUpperBounds = height(2 * numberOfTasks);
            this.numberOfLeavesUpperbound = (numberOfNodesInTheTreeOfUpperBounds + 1) / 2;
            //  this.maximumDelayPerNode = Math.min(maximumNumberOfDelayedTasks, 1 << height);
            this.maximumDelayPerNode = maximumNumberOfDelayedTasks;

            this.theta_energy_of_nodes_lowerbound = new int[numberOfNodesInTheTreeOfLowerBounds][maximumDelayPerNode + 1];
            this.theta_envelope_of_nodes_lowerbound = new int[numberOfNodesInTheTreeOfLowerBounds][maximumDelayPerNode + 1];
            this.theta_energy_of_nodes_upperbound = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1];
            this.theta_envelope_of_nodes_upperbound = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1];
            this.indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound = new int[numberOfLeavesUpperbound];
            this.L = new NewArrayOfPrecedences(Infinity, false);
            this.U = new NewArrayOfPrecedences(Infinity, false);
            for (int i = 0; i < numberOfLeavesUpperbound; i++)
                indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound[i] = -1;
            this.positionOfThisTasksAsALeafOfTLTForUpperbound = new int[numberOfTasks][2];
            for (int i = 0; i < numberOfTasks; i++)
                for (int j = 0; j < 2; j++)
                    positionOfThisTasksAsALeafOfTLTForUpperbound[i][j] = -1;
            this.lambda_energy_of_nodes_lowerbound = new int[numberOfNodesInTheTreeOfLowerBounds][maximumDelayPerNode + 1];
            this.lambda_envelope_of_nodes_lowerbound = new int[numberOfNodesInTheTreeOfLowerBounds][maximumDelayPerNode + 1];
            this.lambda_energy_of_nodes_upperbound = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1];
            this.lambda_envelope_of_nodes_upperbound = new int[numberOfNodesInTheTreeOfUpperBounds][maximumDelayPerNode + 1];
            this.task_indices_for_lct = new Integer[numberOfTasks];
            this.positionOfThisTaskAndItsDelayedVersionInTheTree = new int[2];
            this.task_indices_for_delayed_lct = new Integer[numberOfTasks];
            for (int q = 0; q < numberOfTasks; q++) {
                task_indices_for_lct[q] = q;
            }
            for (int q = 0; q < numberOfTasks; q++) {
                task_indices_for_delayed_lct[q] = q;
            }
            this.task_indices_P = new Integer[numberOfTasks];
            for (int q = 0; q < numberOfTasks; q++) {
                task_indices_P[q] = q;
            }
        }

        public int initializethetaLambdaTreeForLowerBounds(int delayNumber) {

            int b;
            for (int i = 0; i < numberOfNodesInTheTreeOfLowerBounds; i++ ) {
                for (int j = 0; j <= maximumDelayPerNode; j++ ) {
                    theta_energy_of_nodes_lowerbound[i][j] = 0;
                    theta_envelope_of_nodes_lowerbound[i][j] = minusInfinity;
                    lambda_energy_of_nodes_lowerbound[i][j] = minusInfinity;
                    lambda_envelope_of_nodes_lowerbound[i][j] = minusInfinity;
                }
            }
            this.firstIndexOnTheLowestLevel = (1 << heightInTheTreeOfLowerBounds) - 1;
            this.numberOfLeavesLowerBound = (numberOfNodesInTheTreeOfLowerBounds + 1) / 2;
            insertionSortWithComparator(task_indices_P, new Task.ComparatorByEst(tasks));
            b = numberOfNodesInTheTreeOfLowerBounds - firstIndexOnTheLowestLevel;
            int numOfNodesOnTheLowestLevelOfTheTree = Math.min(b, numberOfTasks);
            for (int i = 0; i < numOfNodesOnTheLowestLevelOfTheTree; i++) {
                theta_energy_of_nodes_lowerbound[firstIndexOnTheLowestLevel + i][0] = tasks[task_indices_P[i]].energy();
                theta_envelope_of_nodes_lowerbound[firstIndexOnTheLowestLevel + i][0] = tasks[task_indices_P[i]].envelope(resourceCapacity);
                for (int l = 1; l <= maximumDelayPerNode; l++) {
                    theta_energy_of_nodes_lowerbound[firstIndexOnTheLowestLevel + i][l] = tasks[task_indices_P[i]].delayedEnergy();
                    theta_envelope_of_nodes_lowerbound[firstIndexOnTheLowestLevel + i][l] = tasks[task_indices_P[i]].delayedEnvelope(resourceCapacity);
                }
            }
            /*
            for (int q1 = 2; q1 < maximumNumberOfDelayedTasks + 1; q1++) {
            for (int q2 = 0; q2 < b; q2++) {
            theta_energy_of_nodes_lowerbound[firstIndexOnTheLowestLevel + q2][q1] = 0;
            theta_envelope_of_nodes_lowerbound[firstIndexOnTheLowestLevel + q2][q1] = minusInfinity;
            lambda_energy_of_nodes_lowerbound[firstIndexOnTheLowestLevel + q2][q1] = minusInfinity;
            lambda_envelope_of_nodes_lowerbound[firstIndexOnTheLowestLevel + q2][q1] = minusInfinity;
            }
            }
            */
            int temp = 2 * numberOfLeavesLowerBound;
            int indexOfThisTaskInTheTree = numberOfNodesInTheTreeOfLowerBounds - numberOfLeavesLowerBound - 1;
            int i = 0;
            for (int levelCounterFromBelow  = heightInTheTreeOfLowerBounds - 1; levelCounterFromBelow  >= 0; levelCounterFromBelow-- ) {
                i++;
                //   int allowedMaximumDelayedForThisLevel = Math.min(maximumNumberOfDelayedTasks, 1 << i);

                int allowedMaximumDelayedForThisLevel = maximumNumberOfDelayedTasks;

    //   int allowedMaximumDelayedForThisLevel = 1;
    temp = temp / 2;
    int z = (1 << levelCounterFromBelow);
    for (int counterOfNodesOfThisLevel = 0; counterOfNodesOfThisLevel < z; counterOfNodesOfThisLevel++) {
        int assumedMaximumDelayForThisIteration;  // k
        int counterToReachAssumedMaximumDelayForThisIteration;  // j
        int envelopeOfCurrentCase = minusInfinity;
        int indexOfLeftChild = 2 * indexOfThisTaskInTheTree + 1;
        int indexOfRightChild = indexOfLeftChild + 1;
        for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++) {
            int energyOfCurrentCase = 0;
            //   int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration =  Math.min(assumedMaximumDelayForThisIteration, 1 << i);

            int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration;


            for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++)
            {
                int candidate_energy = theta_energy_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                if (energyOfCurrentCase < candidate_energy)
                    energyOfCurrentCase = candidate_energy;
                if (levelCounterFromBelow == 0 || assumedMaximumDelayForThisIteration <= 1 << (levelCounterFromBelow - 1)) {
                    int  candidate_envelope = Math.max(theta_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration]);
                    if (envelopeOfCurrentCase < candidate_envelope)
                        envelopeOfCurrentCase = candidate_envelope;
                }
                else {
                    int candidate_envelope2 = theta_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                    if (envelopeOfCurrentCase < candidate_envelope2)
                        envelopeOfCurrentCase = candidate_envelope2;
                }
            }
            theta_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
            theta_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
        }
        indexOfThisTaskInTheTree--;
    }
            }
            return theta_envelope_of_nodes_lowerbound[0][delayNumber];
        }

        public void initializethetaLambdaTreeForUpperBounds(int delayNumber) {

            int b;
            for (int i = 0; i < numberOfNodesInTheTreeOfUpperBounds; i++ ) {
                for (int j = 0; j <= maximumDelayPerNode; j++ ) {
                    theta_energy_of_nodes_upperbound[i][j] = 0;
                    theta_envelope_of_nodes_upperbound[i][j] = Infinity;
                    lambda_energy_of_nodes_upperbound[i][j] = minusInfinity;
                    lambda_envelope_of_nodes_upperbound[i][j] = Infinity;
                }
            }
            this.firstIndexOnTheLowestLevel = (1 << heightInTheTreeOfUpperBounds) - 1;
            this.numberOfLeavesLowerBound = (numberOfNodesInTheTreeOfUpperBounds + 1) / 2;
            insertionSortWithComparator(task_indices_for_lct, new Task.ComparatorByLct(tasks));
            insertionSortWithComparator(task_indices_for_delayed_lct, new Task.ComparatorByDelayedLct(tasks));
            int parellelCounterOnLct = 0;
            int parellelCounterOnDelayedLct = 0;
            b = numberOfNodesInTheTreeOfUpperBounds - firstIndexOnTheLowestLevel;
            for (int i = 0; i < b; i++) {
                if (i < 2 * numberOfTasks) 	{
                    if (parellelCounterOnLct < numberOfTasks && tasks[task_indices_for_lct[parellelCounterOnLct]].latestCompletionTime() <= tasks[task_indices_for_delayed_lct[parellelCounterOnDelayedLct]].delayedLatestCompletionTime())
                    {
                        theta_energy_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][0] = tasks[task_indices_for_lct[parellelCounterOnLct]].energy();
                        theta_envelope_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][0] = tasks[task_indices_for_lct[parellelCounterOnLct]].envelopeForUpperBound(resourceCapacity);
                        for (int u = 1; u <= maximumDelayPerNode; u++) {
                            theta_energy_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][u] = tasks[task_indices_for_lct[parellelCounterOnLct]].energy();
                            theta_envelope_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][u] = tasks[task_indices_for_lct[parellelCounterOnLct]].envelopeForUpperBound(resourceCapacity);
                        }
                        indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound[i] = task_indices_for_lct[parellelCounterOnLct];
                        positionOfThisTasksAsALeafOfTLTForUpperbound[task_indices_for_lct[parellelCounterOnLct]][0] = i;
                        parellelCounterOnLct++;
                    }
                    else if (parellelCounterOnDelayedLct < numberOfTasks) {
                        theta_energy_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][0] = 0;
                        theta_envelope_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][0] = Infinity;
                        for (int u = 1; u <= maximumDelayPerNode; u++) {
                            theta_energy_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][u] = tasks[task_indices_for_delayed_lct[parellelCounterOnDelayedLct]].energyOnlyForTheDelayedPortion();
                            theta_envelope_of_nodes_upperbound[firstIndexOnTheLowestLevel + i][u] =  tasks[task_indices_for_delayed_lct[parellelCounterOnDelayedLct]].envelopeForUpperBoundForTheDelayedPortion(resourceCapacity);
                        }
                        indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound[i] = task_indices_for_delayed_lct[parellelCounterOnDelayedLct];
                        positionOfThisTasksAsALeafOfTLTForUpperbound[task_indices_for_delayed_lct[parellelCounterOnDelayedLct]][1] = i;
                        parellelCounterOnDelayedLct++;
                    }
                }
            }
            int temp = 2 * numberOfLeavesLowerBound;
            int indexOfThisTaskInTheTree = numberOfNodesInTheTreeOfUpperBounds - numberOfLeavesLowerBound - 1;
          //  int i = 0;
            for (int levelCounterFromBelow  = heightInTheTreeOfUpperBounds - 1; levelCounterFromBelow  >= 0; levelCounterFromBelow-- ) {
              //  i++;
                // int allowedMaximumDelayedForThisLevel = 1;
                int allowedMaximumDelayedForThisLevel = maximumDelayPerNode;
                temp = temp / 2;
                int z = (1 << levelCounterFromBelow);
                for (int counterOfNodesOfThisLevel = 0; counterOfNodesOfThisLevel < z; counterOfNodesOfThisLevel++) {
                    int assumedMaximumDelayForThisIteration;  // k
                    int counterToReachAssumedMaximumDelayForThisIteration;  // j
                //    int LambdaEnergyOfCurrentCase = minusInfinity;
                  //  int LambdaEnvelopeOfCurrentCase = Infinity;
                    int indexOfLeftChild = 2 * indexOfThisTaskInTheTree + 1;
                    int indexOfRightChild = indexOfLeftChild + 1;
                    for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++) {
                        //   int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration =  Math.min(assumedMaximumDelayForThisIteration, 1 << i);
                        int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration =  assumedMaximumDelayForThisIteration;

                        int energyOfCurrentCase = 0;
                        int envelopeOfCurrentCase = Infinity;
                        for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++)
                        {
                            int candidate_energy = theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                            if (energyOfCurrentCase < candidate_energy)
                                energyOfCurrentCase = candidate_energy;
                            int  candidate_envelope = Math.min(theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]);
                            if (envelopeOfCurrentCase > candidate_envelope)
                                envelopeOfCurrentCase = candidate_envelope;
                        }
                        theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
                        theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
                    }
                    indexOfThisTaskInTheTree--;
                }
            }
               //    return theta_envelope_of_nodes_upperbound[0][delayNumber];
 }

        // Returns 2^ceil(lg(n)) - 1
        private static int nextPowerOfTwoMinusOne(int n) {
            if ((n & (n - 1)) == 0)
                return n - 1;
            int shift = 1;
            int result = n;
            do {
                n = result;
                result = n | (n >> shift);
                shift += shift;
            }
            while (n != result);
            return result;
        }

        private void updateTheTreeForUpperBound(int task_index, int new_energy, int new_envelope, int new_delayed_energy, int new_delayed_envelope) {

            int allowedMaximumDelayedForThisLevel;
            int indexOfThisTaskInTheTree;
            int assumedMaximumDelayForThisIteration;  // k
            positionOfThisTaskAndItsDelayedVersionInTheTree[0] = positionOfThisTasksAsALeafOfTLTForUpperbound[task_index][0];
            positionOfThisTaskAndItsDelayedVersionInTheTree[1] = positionOfThisTasksAsALeafOfTLTForUpperbound[task_index][1];
            for (int i = 0; i < 2; i++) {
                int levelCounter = 0;
                indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + positionOfThisTaskAndItsDelayedVersionInTheTree[i];
                if (i == 0) {
                    lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                    lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                    for (int u = 1; u <= maximumDelayPerNode; u++) {
                        lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                        lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0];
                    }
                    theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_energy;
                    theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_envelope;
                    for (int u = 1; u <= maximumDelayPerNode; u++) {
                        theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_energy;
                        theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_envelope;
                    }
                }

                else {
                    lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_energy;
                    lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_envelope;
                    for (int u = 1; u <= maximumDelayPerNode; u++) {

                        lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = tasks[task_index].delayedEnergy();
                        lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = tasks[task_index].delayedEnvelopeForUpperBound(resourceCapacity);
                    }
                    theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_energy;
                    theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_envelope;
                    for (int u = 1; u <= maximumDelayPerNode; u++) {
                        theta_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_energy;
                        theta_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_envelope;
                    }
                }
                int indexOfParentInTheTree = (indexOfThisTaskInTheTree - 1) / 2;
                int indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                int indexOfRightChild = indexOfLeftChild + 1;
                if (indexOfParentInTheTree < indexOfThisTaskInTheTree) {
                    do {
                        levelCounter++;    //i (i'th level from below and h-i'th level from above.
                        //  allowedMaximumDelayedForThisLevel = 1;
                        allowedMaximumDelayedForThisLevel = maximumDelayPerNode;
                        for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++)
                        {
                            int energyOfCurrentCase = 0;
                            int envelopeOfCurrentCase = Infinity;
                            // int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = Math.min(assumedMaximumDelayForThisIteration, 1 << levelCounter - 1); // TODO: Use shift operator. After fixing the previous TODO, how is this variable different from allowedMaximumDelayedForThisLevel
                            int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration; // TODO: Use shift operator. After fixing the previous TODO, how is this variable different from allowedMaximumDelayedForThisLevel
                            int counterToReachAssumedMaximumDelayForThisIteration ;  // j
                            int current_energy_for_lambda = 0;
                            int current_envelope_for_lambda = Infinity;
                            for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++)
                            {
                                if (theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration] > energyOfCurrentCase)
                                    energyOfCurrentCase = theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                                if (assumedMaximumDelayForThisIteration > 0) {
                                    if (Math.min(Math.min(theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]), theta_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1]) < envelopeOfCurrentCase)
                                        envelopeOfCurrentCase = Math.min(Math.min(theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]), theta_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1]);
                                }
                                else {
                                    if (Math.min(theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]) < envelopeOfCurrentCase)
                                        envelopeOfCurrentCase = Math.min(theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]);
                                }
                                if (assumedMaximumDelayForThisIteration == 0) {
                                    lambda_energy_of_nodes_upperbound[indexOfParentInTheTree][0] =   Math.max(theta_energy_of_nodes_upperbound[indexOfLeftChild][0] + lambda_energy_of_nodes_upperbound[indexOfRightChild][0],lambda_energy_of_nodes_upperbound[indexOfLeftChild][0] + theta_energy_of_nodes_upperbound[indexOfRightChild][0]);
                                    lambda_envelope_of_nodes_upperbound[indexOfParentInTheTree][0] = Math.min(Math.min(lambda_envelope_of_nodes_upperbound[indexOfRightChild][0] - theta_energy_of_nodes_upperbound[indexOfLeftChild][0], lambda_envelope_of_nodes_upperbound[indexOfLeftChild][0]), theta_envelope_of_nodes_upperbound[indexOfRightChild][0] - lambda_energy_of_nodes_upperbound[indexOfLeftChild][0]);
                                }
                                else {
                                    int candidate_energy = Math.max(theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + lambda_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], lambda_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration]);
                                    int candidate_envelope = Math.min(Math.min(lambda_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], lambda_envelope_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration]), theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - lambda_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration]);
                                    if (current_energy_for_lambda < candidate_energy)
                                        current_energy_for_lambda = candidate_energy;
                                    if (current_envelope_for_lambda > candidate_envelope)
                                        current_envelope_for_lambda = candidate_envelope;
                                }
                            }
                            theta_energy_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
                            theta_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
                            if (assumedMaximumDelayForThisIteration > 0) {

                                lambda_energy_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = current_energy_for_lambda;
                                lambda_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = current_envelope_for_lambda;
                            }
                        }
                        indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
                        indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                        indexOfRightChild = indexOfLeftChild + 1;
                    } while (levelCounter < heightInTheTreeOfUpperBounds);
                }
            }
        }

        private void updateTheLeaves(int task_index, int indexOfThisTasksAmongOrdinaryTasks, boolean case1, boolean case2) {
            int indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + task_index;
            if (case1) {
                for (int t = 1; t <= maximumNumberOfDelayedTasks; t++) {

                    lambda_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][t] = tasks[indexOfThisTasksAmongOrdinaryTasks].delayedEnergy();
                    lambda_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][t] = tasks[indexOfThisTasksAmongOrdinaryTasks].delayedEnvelope(resourceCapacity);
                    theta_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][t] = tasks[indexOfThisTasksAmongOrdinaryTasks].energy();
                    theta_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][t] = tasks[indexOfThisTasksAmongOrdinaryTasks].envelope(resourceCapacity);
                }
            }
            else if (case2) {
                lambda_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = tasks[indexOfThisTasksAmongOrdinaryTasks].energy();
                lambda_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = tasks[indexOfThisTasksAmongOrdinaryTasks].envelope(resourceCapacity);
                theta_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = 0;
                theta_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = minusInfinity;
                for (int t = 1; t <= maximumNumberOfDelayedTasks; t++) {
                    theta_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][t] = 0;
                    theta_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][t] = minusInfinity;
                }
            }
            updateTheEntireTree(indexOfThisTaskInTheTree, true, true);
        }



        private void updateTheEntireTree(int indexOfThisTaskInTheTree, boolean updateLambda, boolean updateBothThetaAndLambda) {

            int levelCounter = 0;
            int allowedMaximumDelayedForThisLevel;
            int assumedMaximumDelayForThisIteration;  // k
            int indexOfParentInTheTree = (indexOfThisTaskInTheTree - 1) / 2;
            int indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
            int indexOfRightChild = indexOfLeftChild + 1;
            do {
                levelCounter++;    //i (i'th level from below and h-i'th level from above.
                //      allowedMaximumDelayedForThisLevel = Math.min(maximumNumberOfDelayedTasks, 1 << levelCounter);
                allowedMaximumDelayedForThisLevel = maximumNumberOfDelayedTasks;

                if(updateBothThetaAndLambda)
                {
                    for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++)
                    {
                        int energyOfCurrentCase = 0;
                        int envelopeOfCurrentCase = minusInfinity;
                        int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = Math.min(assumedMaximumDelayForThisIteration, 1 << levelCounter - 1); // TODO: Use shift operator. After fixing the previous TODO, how is this variable different from allowedMaximumDelayedForThisLevel
                        int counterToReachAssumedMaximumDelayForThisIteration ;  // j
                        for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++)
                        {
                            if (theta_energy_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration] > energyOfCurrentCase)
                                energyOfCurrentCase = theta_energy_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration];
                            if (assumedMaximumDelayForThisIteration > 0) {
                                if (Math.max(Math.max(theta_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration]), theta_envelope_of_nodes_lowerbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1]) > envelopeOfCurrentCase)
                                    envelopeOfCurrentCase = Math.max(Math.max(theta_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration]), theta_envelope_of_nodes_lowerbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration - 1]);
                            }
                            else {
                                if (Math.max(theta_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration]) > envelopeOfCurrentCase)
                                    envelopeOfCurrentCase = Math.max(theta_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], theta_envelope_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration]);
                            }
                        }
                        theta_energy_of_nodes_lowerbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = energyOfCurrentCase;
                        theta_envelope_of_nodes_lowerbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = envelopeOfCurrentCase;
                    }

                }
                if (updateLambda)
                {
                    for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++)
                    {
                        // int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = Math.min(assumedMaximumDelayForThisIteration, 1 << (levelCounter - 1));

                        int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration;

                        int current_lambda_energy = minusInfinity;
                        int current_lambda_envelope = minusInfinity;
                        int counterToReachAssumedMaximumDelayForThisIteration ;  // j
                        for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++)
                        {
                            if (assumedMaximumDelayForThisIteration == 0) {
                                lambda_energy_of_nodes_lowerbound[indexOfParentInTheTree][0] =   Math.max(theta_energy_of_nodes_lowerbound[indexOfLeftChild][0] + lambda_energy_of_nodes_lowerbound[indexOfRightChild][0],lambda_energy_of_nodes_lowerbound[indexOfLeftChild][0] + theta_energy_of_nodes_lowerbound[indexOfRightChild][0]);
                                lambda_envelope_of_nodes_lowerbound[indexOfParentInTheTree][0] = Math.max(Math.max(lambda_envelope_of_nodes_lowerbound[indexOfLeftChild][0] + theta_energy_of_nodes_lowerbound[indexOfRightChild][0], lambda_envelope_of_nodes_lowerbound[indexOfRightChild][0]), theta_envelope_of_nodes_lowerbound[indexOfLeftChild][0] + lambda_energy_of_nodes_lowerbound[indexOfRightChild][0]);
                            }
                            else {
                                int candidate_energy = Math.max(theta_energy_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + lambda_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], lambda_energy_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration]);
                                int candidate_envelope = Math.max(Math.max(lambda_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], lambda_envelope_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration]),
                                        theta_envelope_of_nodes_lowerbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + lambda_energy_of_nodes_lowerbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration]);
                                if (current_lambda_energy < candidate_energy)
                                    current_lambda_energy = candidate_energy;
                                if (current_lambda_envelope < candidate_envelope)
                                    current_lambda_envelope = candidate_envelope;
                            }

                        }
                        if (assumedMaximumDelayForThisIteration != 0) {
                            lambda_energy_of_nodes_lowerbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = current_lambda_energy;
                            lambda_envelope_of_nodes_lowerbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = current_lambda_envelope;
                        }
                    }
                }
                indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
                indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                indexOfRightChild = 2 * indexOfParentInTheTree + 2;
            } while (levelCounter < heightInTheTreeOfLowerBounds);


        }

        private void updateLeafAfterUnschedulingFromLambda(int indexOfThisTaskInTheTree,  boolean thisIsUnDelayedPortion, boolean thisIsDelayedPortion) {

            if (thisIsUnDelayedPortion && thisIsDelayedPortion) {
                lambda_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = minusInfinity;
                lambda_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = minusInfinity;

                for (int e = 1; e <= maximumNumberOfDelayedTasks; e++) {

                    lambda_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][e] = minusInfinity;
                    lambda_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][e] = minusInfinity;
                }
            }
            else  if (thisIsUnDelayedPortion && !thisIsDelayedPortion) {
                lambda_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = minusInfinity;
                lambda_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][0] = minusInfinity;
            }
            else if (!thisIsUnDelayedPortion && thisIsDelayedPortion) {
                for (int e = 1; e <= maximumNumberOfDelayedTasks; e++) {

                    lambda_energy_of_nodes_lowerbound[indexOfThisTaskInTheTree][e] = minusInfinity;
                    lambda_envelope_of_nodes_lowerbound[indexOfThisTaskInTheTree][e] = minusInfinity;
                }        }
            updateTheEntireTree(indexOfThisTaskInTheTree, true, false);
        }


        private void updateLeafAfterUnschedulingFromLambdaForUpperbound(int i, int new_energy, int new_envelope, int new_delayed_energy, int new_delayed_envelope, boolean thisIsUnDelayedPortion, boolean thisIsDelayedPortion) {

            int indexOfThisTaskInTheTree = 0;
            if (thisIsUnDelayedPortion && thisIsDelayedPortion)
            {
                for (int x = 0; x < 2; x++) {
                    indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + positionOfThisTasksAsALeafOfTLTForUpperbound[i][x];
                    lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_energy;
                    lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_envelope;
                    for (int u = 1; u <= maximumNumberOfDelayedTasks; u++) {
                        lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_energy;
                        lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_envelope;
                    }
                    updateTreeUpperBound(indexOfThisTaskInTheTree);
                }
            }
            else
                if (!thisIsUnDelayedPortion && thisIsDelayedPortion)

                    indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + positionOfThisTasksAsALeafOfTLTForUpperbound[i][1];

                else if (thisIsUnDelayedPortion && !thisIsDelayedPortion)

                    indexOfThisTaskInTheTree = firstIndexOnTheLowestLevel + positionOfThisTasksAsALeafOfTLTForUpperbound[i][0];
            lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_energy;
            lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][0] = new_envelope;
            for (int u = 1; u <= maximumNumberOfDelayedTasks; u++) {

                lambda_energy_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_energy;
                lambda_envelope_of_nodes_upperbound[indexOfThisTaskInTheTree][u] = new_delayed_envelope;}
            updateTreeUpperBound(indexOfThisTaskInTheTree);
        }



        public void updateTreeUpperBound(int indexOfThisTaskInTheTree) {

            int levelCounter = 0;
            int allowedMaximumDelayedForThisLevel;
            int assumedMaximumDelayForThisIteration;  // k
            int counterToReachAssumedMaximumDelayForThisIteration;  // j
            int indexOfParentInTheTree = (indexOfThisTaskInTheTree - 1) / 2;
            int indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
            int indexOfRightChild = indexOfLeftChild + 1;
            if (indexOfParentInTheTree < indexOfThisTaskInTheTree) {
                do {
                    levelCounter++;    //i (i'th level from below and h-i'th level from above.
                    //          allowedMaximumDelayedForThisLevel = Math.min(maximumNumberOfDelayedTasks, 1 << levelCounter);  //z (the possible number of delays for the tasks rooted at the subtree rooted at this level.)
                    allowedMaximumDelayedForThisLevel = maximumNumberOfDelayedTasks;  //z (the possible number of delays for the tasks rooted at the subtree rooted at this level.)

                    for (assumedMaximumDelayForThisIteration = 0; assumedMaximumDelayForThisIteration <= allowedMaximumDelayedForThisLevel; assumedMaximumDelayForThisIteration++)	{
                        //  int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = Math.min(assumedMaximumDelayForThisIteration, 1 << (levelCounter - 1));
                        int upperBoundForCounterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration;

                        int current_energy = minusInfinity;
                        int current_envelope = Infinity;
                        for (counterToReachAssumedMaximumDelayForThisIteration = assumedMaximumDelayForThisIteration - upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration <= upperBoundForCounterToReachAssumedMaximumDelayForThisIteration; counterToReachAssumedMaximumDelayForThisIteration++) {
                            {
                                int candidate_energy = Math.max(theta_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + lambda_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], lambda_energy_of_nodes_upperbound[indexOfLeftChild][counterToReachAssumedMaximumDelayForThisIteration] + theta_energy_of_nodes_upperbound[indexOfRightChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration]);
                                if (current_energy < candidate_energy)
                                    current_energy = candidate_energy;
                                int candidate_envelope = Math.min(Math.min(lambda_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - theta_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration], lambda_envelope_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration]), theta_envelope_of_nodes_upperbound[indexOfRightChild][counterToReachAssumedMaximumDelayForThisIteration] - lambda_energy_of_nodes_upperbound[indexOfLeftChild][assumedMaximumDelayForThisIteration - counterToReachAssumedMaximumDelayForThisIteration]);
                                if (current_envelope > candidate_envelope)
                                    current_envelope = candidate_envelope;
                            }
                            lambda_energy_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = current_energy;
                            lambda_envelope_of_nodes_upperbound[indexOfParentInTheTree][assumedMaximumDelayForThisIteration] = current_envelope;
                        }
                    }
                    indexOfParentInTheTree = (indexOfParentInTheTree - 1) / 2;
                    indexOfLeftChild = 2 * indexOfParentInTheTree + 1;
                    indexOfRightChild = indexOfLeftChild + 1;
                } while (levelCounter < heightInTheTreeOfUpperBounds);
            }
        }


        public NewArrayOfPrecedences responsibleTaskForLowerBound(int z, boolean NCumulativeConstraintsPosted) {
            boolean processingEnergy = false;
            boolean processingEnvelope = true;
            int upToDownCounter = 0;
            int toFindResponsibleIndex = 0;
            boolean delayedValueContributes = false;
            while (processingEnvelope && upToDownCounter < heightInTheTreeOfLowerBounds) {
                int indexOfLeftChild = 2 * toFindResponsibleIndex + 1;
                int indexOfRightChild = indexOfLeftChild + 1;
                for (int j = 0; j <= z; j++) {
                    if (lambda_envelope_of_nodes_lowerbound[toFindResponsibleIndex][z] == lambda_envelope_of_nodes_lowerbound[indexOfRightChild][j]) {
                        if (upToDownCounter == heightInTheTreeOfLowerBounds - 1 && j > 0 && lambda_envelope_of_nodes_lowerbound[indexOfRightChild][j] != lambda_envelope_of_nodes_lowerbound[indexOfRightChild][0])
                            delayedValueContributes = true;
                        z = j;
                        toFindResponsibleIndex = indexOfRightChild;
                        break;
                    }
                    else if (lambda_envelope_of_nodes_lowerbound[toFindResponsibleIndex][z] == lambda_envelope_of_nodes_lowerbound[indexOfLeftChild][j] + theta_energy_of_nodes_lowerbound[indexOfRightChild][z - j]) {
                        if (upToDownCounter == heightInTheTreeOfLowerBounds - 1 && j > 0 && lambda_envelope_of_nodes_lowerbound[indexOfLeftChild][j] != lambda_envelope_of_nodes_lowerbound[indexOfLeftChild][0])
                            delayedValueContributes = true;
                        z = j;
                        toFindResponsibleIndex = indexOfLeftChild;
                        break;
                    }
                    else if (lambda_envelope_of_nodes_lowerbound[toFindResponsibleIndex][z] == theta_envelope_of_nodes_lowerbound[indexOfLeftChild][j] + lambda_energy_of_nodes_lowerbound[indexOfRightChild][z - j]) {
                        if (upToDownCounter == heightInTheTreeOfLowerBounds - 1 && z - j > 0 && lambda_energy_of_nodes_lowerbound[indexOfRightChild][z - j] != lambda_energy_of_nodes_lowerbound[indexOfRightChild][0])
                            delayedValueContributes = true;
                        z = z - j;
                        toFindResponsibleIndex = indexOfRightChild;
                        processingEnergy = true;
                        processingEnvelope = false;
                        break;
                    }
                }
                upToDownCounter++;
            }
            while (processingEnergy && upToDownCounter < heightInTheTreeOfLowerBounds ) {
                int indexOfLeftChild = 2 * toFindResponsibleIndex + 1;
                int indexOfRightChild = 2 * toFindResponsibleIndex + 2;
                for (int j = 0; j <= z; j++) {
                    if (lambda_energy_of_nodes_lowerbound[toFindResponsibleIndex][z] == lambda_energy_of_nodes_lowerbound[indexOfLeftChild][j] + theta_energy_of_nodes_lowerbound[indexOfRightChild][z - j] ) {
                        if (upToDownCounter == heightInTheTreeOfLowerBounds - 1 && j > 0 && lambda_energy_of_nodes_lowerbound[indexOfLeftChild][j] != lambda_energy_of_nodes_lowerbound[indexOfLeftChild][0])
                            delayedValueContributes = true;
                        z = j;
                        toFindResponsibleIndex = indexOfLeftChild;
                        break;
                    }
                    else if (lambda_energy_of_nodes_lowerbound[toFindResponsibleIndex][z] ==  theta_energy_of_nodes_lowerbound[indexOfLeftChild][j] + lambda_energy_of_nodes_lowerbound[indexOfRightChild][z - j]) {
                        if (upToDownCounter == heightInTheTreeOfLowerBounds - 1 && z - j > 0 && lambda_energy_of_nodes_lowerbound[indexOfRightChild][z - j] != lambda_energy_of_nodes_lowerbound[indexOfRightChild][0])
                            delayedValueContributes = true;
                        z = z - j;
                        toFindResponsibleIndex = indexOfRightChild;
                        break;
                    }
                }
                upToDownCounter++;
            }
            if (NCumulativeConstraintsPosted) // TODO: The experiment should test twice the same algorithm in two different context. The algorithm should not adapt to a context
                delayedValueContributes = false;
            L.setTValue(toFindResponsibleIndex);
            L.setDelayStatus(delayedValueContributes);
            return L;
        }

        public NewArrayOfPrecedences responsibleTaskForUpperBound(int z, boolean NCumulativeConstraintsPosted) {

            boolean processingEnergy = false;
            boolean processingEnvelope = true;
            int upToDownCounter = 0;
            int toFindResponsibleIndex = 0;
            while (processingEnvelope && upToDownCounter < heightInTheTreeOfUpperBounds) {
                int indexOfLeftChild = 2 * toFindResponsibleIndex + 1;
                int indexOfRightChild = indexOfLeftChild + 1;
                for (int j = 0; j <= z; j++) {
                    if (lambda_envelope_of_nodes_upperbound[toFindResponsibleIndex][z] == lambda_envelope_of_nodes_upperbound[indexOfLeftChild][j]) {
                        toFindResponsibleIndex = indexOfLeftChild;
                        break;
                    }
                    else if (lambda_envelope_of_nodes_upperbound[toFindResponsibleIndex][z] == lambda_envelope_of_nodes_upperbound[indexOfRightChild][j] - theta_energy_of_nodes_upperbound[indexOfLeftChild][z - j]) {
                        toFindResponsibleIndex = indexOfRightChild;
                        break;
                    }
                    else if (lambda_envelope_of_nodes_upperbound[toFindResponsibleIndex][z] == theta_envelope_of_nodes_upperbound[indexOfRightChild][j] - lambda_energy_of_nodes_upperbound[indexOfLeftChild][z - j]) {
                        toFindResponsibleIndex = indexOfLeftChild;
                        processingEnergy = true;
                        processingEnvelope = false;
                        break;
                    }
                }
                upToDownCounter++;
            }
            while (processingEnergy && upToDownCounter < heightInTheTreeOfUpperBounds ) {
                int indexOfLeftChild = 2 * toFindResponsibleIndex + 1;
                int indexOfRightChild = indexOfLeftChild + 1;
                for (int j = 0; j <= z; j++) {
                    if (lambda_energy_of_nodes_upperbound[toFindResponsibleIndex][z] == lambda_energy_of_nodes_upperbound[indexOfLeftChild][j] + theta_energy_of_nodes_upperbound[indexOfRightChild][z - j] ) {
                        toFindResponsibleIndex = indexOfLeftChild;
                        break;
                    }
                    else if (lambda_energy_of_nodes_upperbound[toFindResponsibleIndex][z] ==  theta_energy_of_nodes_upperbound[indexOfLeftChild][j] + lambda_energy_of_nodes_upperbound[indexOfRightChild][z - j]) {
                        toFindResponsibleIndex = indexOfRightChild;
                        break;
                    }
                }
                upToDownCounter++;
            }
            //THIS SETS THE INDICE NOT THE VALUE, DON'T CONFUSE WITH THE NAME!
            U.setTValue(indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound[toFindResponsibleIndex - firstIndexOnTheLowestLevel]);
            if (positionOfThisTasksAsALeafOfTLTForUpperbound[indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound[toFindResponsibleIndex - firstIndexOnTheLowestLevel]][0] == toFindResponsibleIndex - firstIndexOnTheLowestLevel)
                U.setDelayStatus(false);
            else
                U.setDelayStatus(true);
            if (NCumulativeConstraintsPosted)
                U.setDelayStatus(false);
            return U;
        }




        public int indexOfResponsibleLeafAmongTheTasks(int o) {
            return task_indices_P[o - (numberOfNodesInTheTreeOfLowerBounds - numberOfLeavesLowerBound)];
        }

        public int[] indicesOfTasksCoreespondingToLeavesOfTTForUpperbound() {
            return  indicesOfTasksCoreespondingToLeavesOfTLTForUpperbound;
        }
        public int[][] positionOfTasksAsALeafOfTTForUpperbound() {

            return positionOfThisTasksAsALeafOfTLTForUpperbound;
        }


        public  void unScheduleFromThetaAndMoveToLambdaForUpperBound(int a) {
            updateTheTreeForUpperBound(a, 0, Infinity, 0, Infinity);
        }

        public  void undelayFromThetaAndDelayInLambdaOrMoveToLambda(int a, int indexOfThisTasksAmongOrdinaryTasks, boolean case1, boolean case2){//TODO something here
            updateTheLeaves(a, indexOfThisTasksAmongOrdinaryTasks, case1, case2);
        }

        public  void unScheduleTaskFromLambdaDependingOnSituation(int a, boolean thisIsUnDelayedPortion, boolean thisIsDelayedPortion) {
            updateLeafAfterUnschedulingFromLambda(a,  thisIsUnDelayedPortion, thisIsDelayedPortion);
        }


        public  void unScheduleDelayedOrUndelayedPortionFromLambdaForUpperbound(int a, boolean thisIsUnDelayedPortion, boolean thisIsDelayedPortion) {
            updateLeafAfterUnschedulingFromLambdaForUpperbound(a, minusInfinity, Infinity, minusInfinity, Infinity, thisIsUnDelayedPortion, thisIsDelayedPortion);
        }


        public static int height(int numberOfNodes)
        {

            int solution = 0;
            if ((numberOfNodes & (numberOfNodes - 1)) == 0) {
                while (numberOfNodes - 1 > 0) {
                    solution++;
                    numberOfNodes /= 2;
                }
            }
            else {
                while (numberOfNodes > 0) {
                    solution++;
                    numberOfNodes /= 2;
                }
            }
            return solution;
        }



        public int lambdaEnvelopeInTheRoot(int a) {
            return  (lambda_envelope_of_nodes_lowerbound[0][a]);
        }
        public int lambdaEnvelopeInTheRootForUpperBound(int a) {
            return  (lambda_envelope_of_nodes_upperbound[0][a]);
        }
        public int requiredEnvelopeInTheRoot(int a) {
            return  (theta_envelope_of_nodes_lowerbound[0][a]);
        }

        public int requiredEnvelopeInTheRootUpperBound(int a) {
            return  (theta_envelope_of_nodes_upperbound[0][a]);
        }
        public static void insertionSortWithComparator(Integer[] a, Comparator c) {

            for (int i = 0; i < a.length; i++) {
                Integer v = a[i];
                int j;
                for (j = i - 1; j >= 0; j--) {
                    if (c.compare(a[j], v) <= 0) break;
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
            }
        }
    }
