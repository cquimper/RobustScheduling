package robustcumulativescheduling;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class edgeFindingConstraintTest {

    private static boolean activateImprovingDetectionLowerBound;

    public edgeFindingConstraintTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPropagate() throws Exception {

        Task[] T;
        int numberOfTasks;
        int resourceCapacity;
        int[] d;
        int[] outputArray;
        int[] outputArrayForNCumulative;
        int delayNumber;
        delayNumber = 1;

        activateImprovingDetectionLowerBound = true;
/*
        Task MyTask1570 = new Task(1, 13, 14, 4, 1, 6);//A
        Task MyTask1571 = new Task(3, 23, 24, 9, 1, 7);//B
        Task MyTask1572 = new Task(8, 36, 38, 8, 2, 2);//C
        Task MyTask1573 = new Task(10, 28, 29, 12, 1, 6);//D
        Task MyTask1574 = new Task(35, 57, 59, 10, 2, 5);//E
        Task MyTask1575 = new Task(41, 56, 57, 9, 1, 3);//F
        Task MyTask1576 = new Task(40, 57, 57, 3, 0, 3);//G
        T = new Task[7];
        T[0] = MyTask1570;
        T[1] = MyTask1571;
        T[2] = MyTask1572;
        T[3] = MyTask1573;
        T[4] = MyTask1574;
        T[5] = MyTask1575;
        T[6] = MyTask1576;
        resourceCapacity = 7;
        delayNumber = 1;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 1);
        assertEquals(outputArray[1], 6);
        assertEquals(outputArray[2], 22);
        assertEquals(outputArray[3], 15);
        assertEquals(outputArray[4], 35);
        assertEquals(outputArray[5], 41);
        assertEquals(outputArray[6], 40);
        assertEquals(outputArrayForNCumulative[0], 1);
        assertEquals(outputArrayForNCumulative[1], 6);
        assertEquals(outputArrayForNCumulative[2], 22);
        assertEquals(outputArrayForNCumulative[3], 15);
        assertEquals(outputArrayForNCumulative[4], 35);
        assertEquals(outputArrayForNCumulative[5], 41);
        assertEquals(outputArrayForNCumulative[6], 40);

        Task MyTask15700 = new Task(1, 13, 14, 4, 1, 6);//A
        Task MyTask15710 = new Task(6, 23, 24, 9, 1, 7);//B
        Task MyTask15720 = new Task(22, 36, 46, 8, 10, 2);//C
        Task MyTask15730 = new Task(15, 28, 29, 12, 1, 6);//D
        Task MyTask15740 = new Task(26, 57, 59, 10, 2, 5);//E
        Task MyTask15750 = new Task(1, 56, 57, 9, 1, 2);//F
        Task MyTask15760 = new Task(1, 57, 57, 3, 0, 3);//G
        T = new Task[7];
        T[0] = MyTask15700;
        T[1] = MyTask15710;
        T[2] = MyTask15720;
        T[3] = MyTask15730;
        T[4] = MyTask15740;
        T[5] = MyTask15750;
        T[6] = MyTask15760;
        resourceCapacity = 7;
        delayNumber = 1;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(outputArray[0], 6);
        assertEquals(outputArray[1], 16);
        assertEquals(outputArray[2], 36);
        assertEquals(outputArray[3], 28);
        assertEquals(outputArray[4], 57);
        assertEquals(outputArray[5], 56);
        assertEquals(outputArray[6], 57);
        assertEquals(outputArrayForNCumulative[0], 6);
        assertEquals(outputArrayForNCumulative[1], 16);
        assertEquals(outputArrayForNCumulative[2], 36);
        assertEquals(outputArrayForNCumulative[3], 28);
        assertEquals(outputArrayForNCumulative[4], 57);
        assertEquals(outputArrayForNCumulative[5], 56);
        assertEquals(outputArrayForNCumulative[6], 57);
*/
        /*
        Task MyTask0 = new Task(0, 5, 5, 1, 0, 3);//A
        Task MyTask1 = new Task(2, 5, 5, 3, 0, 1);//B
        Task MyTask2 = new Task(2, 5, 5, 2, 0, 2);//C
        Task MyTask3 = new Task(0, 10, 10, 3, 0, 2);//D
        T = new Task[4];
        T[0] = MyTask0;
        T[1] = MyTask1;
        T[2] = MyTask2;
        T[3] = MyTask3;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 0);
        assertEquals(outputArray[1], 2);
        assertEquals(outputArray[2], 2);
        assertEquals(outputArray[3], 4);
        assertEquals(outputArrayForNCumulative[0], 0);
        assertEquals(outputArrayForNCumulative[1], 2);
        assertEquals(outputArrayForNCumulative[2], 2);
        assertEquals(outputArrayForNCumulative[3], 4);

        Task MyTask1900 = new Task(2, 4, 4, 1, 0, 1);//A
        Task MyTask1901 = new Task(0, 10, 10, 5, 0, 1);//A
        T = new Task[2];
        T[0] = MyTask1900;
        T[1] = MyTask1901;
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 2);
        assertEquals(outputArray[1], 0);
        assertEquals(outputArrayForNCumulative[0], 2);
        assertEquals(outputArrayForNCumulative[1], 0);

        Task MyTask190 = new Task(1, 7, 8, 6, 1, 4);//A
        Task MyTask191 = new Task(1, 5, 6, 4, 1, 3);//B
        Task MyTask192 = new Task(2, 15, 15, 7, 0, 1);//C
        Task MyTask193 = new Task(1, 8, 10, 1, 2, 3);//D   //late chane est to 1
        Task MyTask194 = new Task(8, 13, 15, 1, 2, 7);//E
        T = new Task[5];
        T[0] = MyTask190;
        T[1] = MyTask191;
        T[2] = MyTask192;
        T[3] = MyTask193;
        T[4] = MyTask194;
        resourceCapacity = 7;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 1);
        assertEquals(outputArray[1], 1);
        assertEquals(outputArray[2], 4);
        assertEquals(outputArray[3], 5);
        assertEquals(outputArray[4], 8);
        assertEquals(outputArrayForNCumulative[0], 1);
        assertEquals(outputArrayForNCumulative[1], 1);
        assertEquals(outputArrayForNCumulative[2], 4);
        assertEquals(outputArrayForNCumulative[3], 5);
        assertEquals(outputArrayForNCumulative[4], 8);
*/
        //Instance for thesis (both filtering)
        Task MyTask157 = new Task(1, 13, 14, 4, 1, 6);//A
        Task MyTask158 = new Task(6, 23, 24, 9, 1, 7);//B
        Task MyTask159 = new Task(16, 36, 46, 8, 10, 2);//C
        Task MyTask160 = new Task(14, 28, 29, 12, 1, 6);//D
        T = new Task[4];
        T[0] = MyTask157;
        T[1] = MyTask158;
        T[2] = MyTask159;
        T[3] = MyTask160;
        resourceCapacity = 7;
        delayNumber = 1;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 1);
        assertEquals(outputArray[1], 6);
        assertEquals(outputArray[2], 22);
        assertEquals(outputArray[3], 15);
        assertEquals(outputArrayForNCumulative[0], 1);
        assertEquals(outputArrayForNCumulative[1], 6);
        assertEquals(outputArrayForNCumulative[2], 22);
        assertEquals(outputArrayForNCumulative[3], 15);

        Task MyTask246 = new Task(1, 19, 24, 5, 5, 5);//A
        Task MyTask247 = new Task(0, 3, 5, 3, 2, 4);//B
        Task MyTask248 = new Task(5, 12, 14, 7, 2, 3);//C
        Task MyTask249 = new Task(0, 19, 34, 7, 15, 1);//D

        T = new Task[4];
        T[0] = MyTask246;
        T[1] = MyTask247;
        T[2] = MyTask248;
        T[3] = MyTask249;

        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 8);
        assertEquals(outputArray[1], 0);
        assertEquals(outputArray[2], 5);
        assertEquals(outputArray[3], 0);

        assertEquals(outputArrayForNCumulative[0], 8);
        assertEquals(outputArrayForNCumulative[1], 0);
        assertEquals(outputArrayForNCumulative[2], 5);
        assertEquals(outputArrayForNCumulative[3], 0);

        Task MyTask119 = new Task(7, 11, 11, 4, 0, 1);//A
        Task MyTask120 = new Task(7, 8, 8, 1, 0, 7);//B
        Task MyTask121 = new Task(0, 17, 17, 1, 0, 5);//C
        Task MyTask122 = new Task(0, 7, 7, 3, 0, 9);//D
        Task MyTask123 = new Task(9, 17, 17, 5, 0, 3);//E
        Task MyTask124 = new Task(9, 17, 17, 4, 0, 2);//F
        Task MyTask125 = new Task(0, 17, 17, 2, 0, 6);//G
        Task MyTask126 = new Task(0, 7, 7, 4, 0, 10);//H
        Task MyTask127 = new Task(4, 17, 17, 10, 0, 2);//I
        T = new Task[9];
        T[0] = MyTask119;
        T[1] = MyTask120;
        T[2] = MyTask121;
        T[3] = MyTask122;
        T[4] = MyTask123;
        T[5] = MyTask124;
        T[6] = MyTask125;
        T[7] = MyTask126;
        T[8] = MyTask127;
        resourceCapacity = 10;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 7);
        assertEquals(outputArray[1], 7);
        assertEquals(outputArray[2], 8);
        assertEquals(outputArray[3], 0);
        assertEquals(outputArray[4], 9);
        assertEquals(outputArray[5], 9);
        assertEquals(outputArray[6], 8);
        assertEquals(outputArray[7], 0);
        assertEquals(outputArray[8], 6);
        assertEquals(outputArrayForNCumulative[0], 7);
        assertEquals(outputArrayForNCumulative[1], 7);
        assertEquals(outputArrayForNCumulative[2], 8);
        assertEquals(outputArrayForNCumulative[3], 0);
        assertEquals(outputArrayForNCumulative[4], 9);
        assertEquals(outputArrayForNCumulative[5], 9);
        assertEquals(outputArrayForNCumulative[6], 8);
        assertEquals(outputArrayForNCumulative[7], 0);
        assertEquals(outputArrayForNCumulative[8], 6);

        Task MyTask92 = new Task(18, 35, 39, 4, 4, 8);//A
        Task MyTask93 = new Task(0, 35, 45, 10, 10, 2);//B
        Task MyTask94 = new Task(2, 18, 26, 8, 8, 8);//C
        Task MyTask95 = new Task(18, 26, 28, 2, 2, 6);//D
        Task MyTask96 = new Task(22, 31, 31, 5, 0, 2);//E
        Task MyTask97 = new Task(1, 31, 33, 4, 2, 8);//F
        Task MyTask98 = new Task(27, 35, 37, 2, 2, 2);//G
        T = new Task[7];
        T[0] = MyTask92;
        T[1] = MyTask93;
        T[2] = MyTask94;
        T[3] = MyTask95;
        T[4] = MyTask96;
        T[5] = MyTask97;
        T[6] = MyTask98;
        resourceCapacity = 8;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(20, outputArray[0]);
        assertEquals(0, outputArray[1]);
        assertEquals(2, outputArray[2]);
        assertEquals(18, outputArray[3]);
        assertEquals(22, outputArray[4]);
        assertEquals(1, outputArray[5]);
        assertEquals(27, outputArray[6]);
        assertEquals(20, outputArrayForNCumulative[0]);
        assertEquals(0, outputArrayForNCumulative[1]);
        assertEquals(2, outputArrayForNCumulative[2]);
        assertEquals(18, outputArrayForNCumulative[3]);
        assertEquals(22, outputArrayForNCumulative[4]);
        assertEquals(1, outputArrayForNCumulative[5]);
        assertEquals(27, outputArrayForNCumulative[6]);

        Task MyTask19 = new Task(0, 5, 5, 1, 0, 3);//A
        Task MyTask20 = new Task(2, 5, 5, 3, 0, 1);//B
        Task MyTask21 = new Task(2, 5, 5, 2, 0, 2);//C
        Task MyTask22 = new Task(0, 8, 10, 1, 2, 2);//D
        T = new Task[4];
        T[0] = MyTask19;
        T[1] = MyTask20;
        T[2] = MyTask21;
        T[3] = MyTask22;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(0, outputArray[0]);
        assertEquals(2, outputArray[1]);
        assertEquals(2, outputArray[2]);
        assertEquals(4, outputArray[3]);
        assertEquals(0, outputArrayForNCumulative[0]);
        assertEquals(2, outputArrayForNCumulative[1]);
        assertEquals(2, outputArrayForNCumulative[2]);
        assertEquals(4, outputArrayForNCumulative[3]);

        Task MyTask168 = new Task(0, 3, 5, 3, 2, 4);//A
        Task MyTask169 = new Task(0, 7, 11, 7, 4, 1);//B
        T = new Task[2];
        T[0] = MyTask168;
        T[1] = MyTask169;
        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 0);
        assertEquals(outputArray[1], 0);
        assertEquals(outputArrayForNCumulative[0], 0);
        assertEquals(outputArrayForNCumulative[1], 0);

        Task MyTask232 = new Task(13, 47, 53, 6, 6, 1);//A
        Task MyTask233 = new Task(19, 56, 56, 9, 0, 2);//B
        Task MyTask234 = new Task(0, 37, 38, 2, 1, 1);//C
        Task MyTask235 = new Task(0, 52, 55, 5, 3, 1);//D
        Task MyTask236 = new Task(0, 56, 62, 4, 6, 3);//E
        Task MyTask237 = new Task(2, 41, 47, 4, 6, 5);//F
        Task MyTask238 = new Task(5, 56, 56, 4, 0, 1);//G  
        T = new Task[7];
        T[0] = MyTask232;
        T[1] = MyTask233;
        T[2] = MyTask234;
        T[3] = MyTask235;
        T[4] = MyTask236;
        T[5] = MyTask237;
        T[6] = MyTask238;

        resourceCapacity = 5;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 13);
        assertEquals(outputArray[1], 19);
        assertEquals(outputArray[2], 0);
        assertEquals(outputArray[3], 0);
        assertEquals(outputArray[4], 0);
        assertEquals(outputArray[5], 2);
        assertEquals(outputArray[6], 5);
        assertEquals(outputArrayForNCumulative[0], 13);
        assertEquals(outputArrayForNCumulative[1], 19);
        assertEquals(outputArrayForNCumulative[2], 0);
        assertEquals(outputArrayForNCumulative[3], 0);
        assertEquals(outputArrayForNCumulative[4], 0);
        assertEquals(outputArrayForNCumulative[5], 2);
        assertEquals(outputArrayForNCumulative[6], 5);

        Task MyTask225 = new Task(10, 16, 16, 6, 0, 1);//A
        Task MyTask226 = new Task(16, 30, 30, 9, 0, 4);//B
        Task MyTask227 = new Task(0, 5, 5, 2, 0, 3);//C
        Task MyTask228 = new Task(0, 21, 21, 5, 0, 3);//D
        Task MyTask229 = new Task(0, 29, 30, 4, 1, 4);//E
        Task MyTask230 = new Task(2, 9, 10, 4, 1, 4);//F
        Task MyTask231 = new Task(16, 30, 30, 4, 0, 4);//G  
        T = new Task[7];
        T[0] = MyTask225;
        T[1] = MyTask226;
        T[2] = MyTask227;
        T[3] = MyTask228;
        T[4] = MyTask229;
        T[5] = MyTask230;
        T[6] = MyTask231;

        resourceCapacity = 4;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 10);
        assertEquals(outputArray[1], 16);
        assertEquals(outputArray[2], 0);
        assertEquals(outputArray[3], 6);
        assertEquals(outputArray[4], 7);
        assertEquals(outputArray[5], 2);
        assertEquals(outputArray[6], 16);
        assertEquals(outputArrayForNCumulative[0], 10);
        assertEquals(outputArrayForNCumulative[1], 16);
        assertEquals(outputArrayForNCumulative[2], 0);
        assertEquals(outputArrayForNCumulative[3], 6);
        assertEquals(outputArrayForNCumulative[4], 7);
        assertEquals(outputArrayForNCumulative[5], 2);
        assertEquals(outputArrayForNCumulative[6], 16);

        Task MyTask131 = new Task(12, 21, 24, 3, 3, 7);
        Task MyTask132 = new Task(16, 25, 30, 4, 5, 10);
        T = new Task[2];
        T[0] = MyTask131;
        T[1] = MyTask132;
        resourceCapacity = 10;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 12);
        assertEquals(outputArray[1], 16);
        assertEquals(outputArrayForNCumulative[0], 12);
        assertEquals(outputArrayForNCumulative[1], 16);

        Task MyTask195 = new Task(6, 21, 25, 2, 4, 6);//A
        Task MyTask196 = new Task(5, 6, 6, 1, 0, 7);//B
        Task MyTask197 = new Task(6, 10, 11, 2, 1, 9);//C
        Task MyTask198 = new Task(7, 21, 22, 1, 1, 5);//D
        Task MyTask199 = new Task(9, 21, 30, 5, 9, 3);//E
        Task MyTask200 = new Task(5, 9, 16, 4, 7, 1);//F
        T = new Task[6];
        T[0] = MyTask195;
        T[1] = MyTask196;
        T[2] = MyTask197;
        T[3] = MyTask198;
        T[4] = MyTask199;
        T[5] = MyTask200;
        resourceCapacity = 10;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(7, outputArray[0]);
        assertEquals(5, outputArray[1]);
        assertEquals(6, outputArray[2]);
        assertEquals(7, outputArray[3]);
        assertEquals(9, outputArray[4]);
        assertEquals(5, outputArray[5]);
        assertEquals(7, outputArrayForNCumulative[0]);
        assertEquals(5, outputArrayForNCumulative[1]);
        assertEquals(6, outputArrayForNCumulative[2]);
        assertEquals(7, outputArrayForNCumulative[3]);
        assertEquals(9, outputArrayForNCumulative[4]);
        assertEquals(5, outputArrayForNCumulative[5]);

//The next two consequtive examples are intended for improving detection part.
        Task MyTask99 = new Task(2, 4, 4, 1, 0, 1);//G
        Task MyTask100 = new Task(2, 4, 4, 2, 0, 2);//H
        Task MyTask101 = new Task(0, 10, 10, 5, 0, 1);//I
        T = new Task[3];
        T[0] = MyTask99;
        T[1] = MyTask100;
        T[2] = MyTask101;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 2);
        assertEquals(outputArray[1], 2);
        assertEquals(outputArray[2], 3);
        assertEquals(outputArrayForNCumulative[0], 2);
        assertEquals(outputArrayForNCumulative[1], 2);
        assertEquals(outputArrayForNCumulative[2], 3);

        Task MyTask239 = new Task(2, 5, 5, 1, 0, 3);//G
        Task MyTask240 = new Task(2, 5, 5, 2, 0, 3);//H
        Task MyTask241 = new Task(0, 10, 10, 5, 0, 1);//I
        T = new Task[3];
        T[0] = MyTask239;
        T[1] = MyTask240;
        T[2] = MyTask241;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 2);
        assertEquals(outputArray[1], 2);
        assertEquals(outputArray[2], 5);
        assertEquals(outputArrayForNCumulative[0], 2);
        assertEquals(outputArrayForNCumulative[1], 2);
        assertEquals(outputArrayForNCumulative[2], 5);

        Task MyTask201 = new Task(23, 41, 48, 13, 7, 1);//A
        Task MyTask202 = new Task(53, 69, 78, 9, 9, 7);//B
        Task MyTask203 = new Task(36, 54, 57, 12, 3, 9);//C
        Task MyTask204 = new Task(36, 49, 50, 3, 1, 1);//D
        Task MyTask205 = new Task(36, 68, 73, 8, 5, 6);//E
        T = new Task[5];
        T[0] = MyTask201;
        T[1] = MyTask202;
        T[2] = MyTask203;
        T[3] = MyTask204;
        T[4] = MyTask205;
        resourceCapacity = 9;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 23);
        assertEquals(outputArray[1], 53);
        assertEquals(outputArray[2], 37);
        assertEquals(outputArray[3], 36);
        assertEquals(outputArray[4], 50);
        assertEquals(outputArrayForNCumulative[0], 23);
        assertEquals(outputArrayForNCumulative[1], 53);
        assertEquals(outputArrayForNCumulative[2], 37);
        assertEquals(outputArrayForNCumulative[3], 36);
        assertEquals(outputArrayForNCumulative[4], 50);

        Task MyTask31 = new Task(27, 45, 45, 9, 0, 4);//A
        Task MyTask32 = new Task(5, 26, 26, 12, 0, 6);//B
        Task MyTask33 = new Task(0, 5, 5, 5, 0, 3);//C
        Task MyTask34 = new Task(17, 45, 45, 14, 0, 7);//D
        Task MyTask35 = new Task(17, 30, 30, 4, 0, 4);//E
        Task MyTask36 = new Task(5, 45, 45, 4, 0, 3);//F
        T = new Task[6];
        T[0] = MyTask31;
        T[1] = MyTask32;
        T[2] = MyTask33;
        T[3] = MyTask34;
        T[4] = MyTask35;
        T[5] = MyTask36;
        resourceCapacity = 7;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(27, outputArray[0]);
        assertEquals(5, outputArray[1]);
        assertEquals(0, outputArray[2]);
        assertEquals(20, outputArray[3]);
        assertEquals(17, outputArray[4]);
        assertEquals(5, outputArray[5]);
        assertEquals(27, outputArrayForNCumulative[0]);
        assertEquals(5, outputArrayForNCumulative[1]);
        assertEquals(0, outputArrayForNCumulative[2]);
        assertEquals(20, outputArrayForNCumulative[3]);
        assertEquals(17, outputArrayForNCumulative[4]);
        assertEquals(5, outputArrayForNCumulative[5]);

        Task MyTask27 = new Task(0, 2, 3, 1, 1, 2);//A
        Task MyTask28 = new Task(0, 2, 2, 1, 0, 2);//B
        Task MyTask29 = new Task(1, 4, 5, 1, 1, 1);//C
        Task MyTask30 = new Task(0, 10, 12, 1, 2, 2);//D
        T = new Task[4];
        T[0] = MyTask27;
        T[1] = MyTask28;
        T[2] = MyTask29;
        T[3] = MyTask30;
        //B<A^d<C<D^d
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(1, outputArray[0]);
        assertEquals(0, outputArray[1]);
        assertEquals(3, outputArray[2]);
        assertEquals(4, outputArray[3]);
        assertEquals(1, outputArrayForNCumulative[0]);
        assertEquals(0, outputArrayForNCumulative[1]);
        assertEquals(3, outputArrayForNCumulative[2]);
        assertEquals(4, outputArrayForNCumulative[3]);

        Task MyTask23 = new Task(0, 6, 7, 1, 1, 1);//A
        Task MyTask24 = new Task(0, 7, 7, 7, 0, 1);//B
        Task MyTask25 = new Task(6, 7, 7, 1, 0, 1);//C
        Task MyTask26 = new Task(0, 20, 20, 6, 0, 1);//D
        T = new Task[4];
        T[0] = MyTask23;
        T[1] = MyTask24;
        T[2] = MyTask25;
        T[3] = MyTask26;
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(0, outputArray[0]);
        assertEquals(0, outputArray[1]);
        assertEquals(6, outputArray[2]);
        assertEquals(3, outputArray[3]);
        assertEquals(0, outputArrayForNCumulative[0]);
        assertEquals(0, outputArrayForNCumulative[1]);
        assertEquals(6, outputArrayForNCumulative[2]);
        assertEquals(3, outputArrayForNCumulative[3]);

        Task MyTask4 = new Task(0, 7, 7, 2, 0, 1);//A
        Task MyTask5 = new Task(0, 7, 7, 7, 0, 1);//B
        Task MyTask6 = new Task(6, 7, 7, 1, 0, 1);//C
        Task MyTask7 = new Task(0, 20, 20, 6, 0, 1);//D
        T = new Task[4];
        T[0] = MyTask4;
        T[1] = MyTask5;
        T[2] = MyTask6;
        T[3] = MyTask7;
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(0, outputArray[0]);
        assertEquals(0, outputArray[1]);
        assertEquals(6, outputArray[2]);
        assertEquals(3, outputArray[3]);
        assertEquals(0, outputArrayForNCumulative[0]);
        assertEquals(0, outputArrayForNCumulative[1]);
        assertEquals(6, outputArrayForNCumulative[2]);
        assertEquals(3, outputArrayForNCumulative[3]);

        Task MyTask8 = new Task(0, 9, 12, 9, 3, 4);//A
        Task MyTask9 = new Task(12, 24, 24, 2, 0, 3);//B
        Task MyTask10 = new Task(12, 24, 25, 5, 1, 3);//C
        Task MyTask11 = new Task(12, 24, 28, 4, 4, 4);//D
        T = new Task[4];
        T[0] = MyTask8;
        T[1] = MyTask9;
        T[2] = MyTask10;
        T[3] = MyTask11;
        resourceCapacity = 4;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 0);
        assertEquals(outputArray[1], 12);
        assertEquals(outputArray[2], 12);
        assertEquals(outputArray[3], 18);
        assertEquals(outputArrayForNCumulative[0], 0);
        assertEquals(outputArrayForNCumulative[1], 12);
        assertEquals(outputArrayForNCumulative[2], 12);
        assertEquals(outputArrayForNCumulative[3], 18);

        Task MyTask133 = new Task(1, 8, 8, 4, 0, 2);//A
        Task MyTask134 = new Task(5, 7, 7, 2, 0, 2);//B
        Task MyTask135 = new Task(1, 8, 8, 1, 0, 2);//C
        Task MyTask136 = new Task(1, 8, 8, 3, 0, 1);//D
        Task MyTask137 = new Task(5, 7, 7, 1, 0, 1);//E
        Task MyTask138 = new Task(0, 16, 16, 7, 0, 1);//F
        T = new Task[6];
        T[0] = MyTask133;
        T[1] = MyTask134;
        T[2] = MyTask135;
        T[3] = MyTask136;
        T[4] = MyTask137;
        T[5] = MyTask138;
        resourceCapacity = 3;
        delayNumber = 1;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 1);
        assertEquals(outputArray[1], 5);
        assertEquals(outputArray[2], 1);
        assertEquals(outputArray[3], 1);
        assertEquals(outputArray[4], 5);
        assertEquals(outputArray[5], 6);//used to be 5, with the new update it is 6 now.
        assertEquals(outputArrayForNCumulative[0], 1);
        assertEquals(outputArrayForNCumulative[1], 5);
        assertEquals(outputArrayForNCumulative[2], 1);
        assertEquals(outputArrayForNCumulative[3], 1);
        assertEquals(outputArrayForNCumulative[4], 5);
        assertEquals(outputArrayForNCumulative[5], 6);

        Task MyTask56 = new Task(16, 17, 17, 1, 0, 1);//A
        Task MyTask57 = new Task(0, 15, 15, 8, 0, 8);//B
        Task MyTask58 = new Task(0, 16, 16, 7, 0, 8);//C
        Task MyTask59 = new Task(9, 27, 27, 10, 0, 2);//D
        T = new Task[4];
        T[0] = MyTask56;
        T[1] = MyTask57;
        T[2] = MyTask58;
        T[3] = MyTask59;
        resourceCapacity = 8;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 16);
        assertEquals(outputArray[1], 0);
        assertEquals(outputArray[2], 0);
        assertEquals(outputArray[3], 12);
        assertEquals(outputArrayForNCumulative[0], 16);
        assertEquals(outputArrayForNCumulative[1], 0);
        assertEquals(outputArrayForNCumulative[2], 0);
        assertEquals(outputArrayForNCumulative[3], 12);

        Task MyTask52 = new Task(7, 8, 8, 1, 0, 7);//A
        Task MyTask53 = new Task(0, 7, 7, 3, 0, 9);//B
        Task MyTask54 = new Task(0, 7, 7, 4, 0, 10);//C
        Task MyTask55 = new Task(4, 17, 17, 10, 0, 2);//D
        T = new Task[4];
        T[0] = MyTask52;
        T[1] = MyTask53;
        T[2] = MyTask54;
        T[3] = MyTask55;
        resourceCapacity = 10;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 7);
        assertEquals(outputArray[1], 0);
        assertEquals(outputArray[2], 0);
        assertEquals(outputArray[3], 6);
        assertEquals(outputArrayForNCumulative[0], 7);
        assertEquals(outputArrayForNCumulative[1], 0);
        assertEquals(outputArrayForNCumulative[2], 0);
        assertEquals(outputArrayForNCumulative[3], 6);

        Task MyTask105 = new Task(1, 4, 4, 1, 0, 2);//A
        Task MyTask106 = new Task(1, 4, 4, 2, 0, 2);//B
        Task MyTask107 = new Task(4, 6, 6, 2, 0, 3);//I
        Task MyTask108 = new Task(1, 12, 12, 4, 0, 1);//I
        T = new Task[4];
        T[0] = MyTask105;
        T[1] = MyTask106;
        T[2] = MyTask107;
        T[3] = MyTask108;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(1, outputArray[0]);
        assertEquals(1, outputArray[1]);
        assertEquals(4, outputArray[2]);
        assertEquals(6, outputArray[3]);
        assertEquals(1, outputArrayForNCumulative[0]);
        assertEquals(1, outputArrayForNCumulative[1]);
        assertEquals(4, outputArrayForNCumulative[2]);
        assertEquals(6, outputArrayForNCumulative[3]);

        Task MyTask206 = new Task(1, 5, 5, 2, 0, 3);//A
        Task MyTask207 = new Task(1, 5, 5, 1, 0, 1);//B
        Task MyTask208 = new Task(4, 5, 5, 1, 0, 2);//D
        Task MyTask209 = new Task(0, 12, 12, 4, 0, 2);//I   
        T = new Task[4];
        T[0] = MyTask206;
        T[1] = MyTask207;
        T[2] = MyTask208;
        T[3] = MyTask209;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 1);
        assertEquals(outputArray[1], 1);
        assertEquals(outputArray[2], 4);
        assertEquals(outputArray[3], 5);
        assertEquals(outputArrayForNCumulative[0], 1);
        assertEquals(outputArrayForNCumulative[1], 1);
        assertEquals(outputArrayForNCumulative[2], 4);
        assertEquals(outputArrayForNCumulative[3], 5);

        Task MyTask12 = new Task(1, 10, 10, 2, 0, 2);//A
        Task MyTask13 = new Task(1, 10, 11, 6, 1, 3);//B
        Task MyTask14 = new Task(1, 11, 12, 2, 1, 2);//C
        T = new Task[3];
        T[0] = MyTask12;
        T[1] = MyTask13;
        T[2] = MyTask14;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(1, outputArray[0]);
        assertEquals(1, outputArray[1]);
        assertEquals(8, outputArray[2]);
        assertEquals(1, outputArrayForNCumulative[0]);
        assertEquals(1, outputArrayForNCumulative[1]);
        assertEquals(8, outputArrayForNCumulative[2]);

        //numbering from 105 to 110 and 242 to 299 are not checked
        Task MyTask47 = new Task(6, 11, 15, 5, 4, 3);//A
        Task MyTask48 = new Task(2, 8, 8, 6, 0, 1);//B
        Task MyTask49 = new Task(2, 6, 8, 4, 2, 2);//C 
        Task MyTask50 = new Task(8, 11, 13, 3, 2, 3);//D
        Task MyTask51 = new Task(11, 12, 13, 1, 1, 1);//E 
        T = new Task[5];
        T[0] = MyTask47;
        T[1] = MyTask48;
        T[2] = MyTask49;
        T[3] = MyTask50;
        T[4] = MyTask51;

        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(6, outputArray[0]);
        assertEquals(2, outputArray[1]);
        assertEquals(2, outputArray[2]);
        assertEquals(8, outputArray[3]);
        assertEquals(11, outputArray[4]);
        assertEquals(6, outputArrayForNCumulative[0]);
        assertEquals(2, outputArrayForNCumulative[1]);
        assertEquals(2, outputArrayForNCumulative[2]);
        assertEquals(8, outputArrayForNCumulative[3]);
        assertEquals(11, outputArrayForNCumulative[4]);

        Task MyTask176 = new Task(17, 18, 19, 1, 1, 1);//B
        Task MyTask177 = new Task(27, 29, 30, 2, 1, 2);//D
        Task MyTask178 = new Task(0, 5, 7, 5, 2, 1);//E
        Task MyTask179 = new Task(5, 10, 15, 5, 5, 2);//F
        Task MyTask180 = new Task(18, 21, 22, 3, 1, 1);//G 
        Task MyTask181 = new Task(7, 11, 12, 4, 1, 2);//I
        Task MyTask182 = new Task(22, 27, 33, 5, 6, 2);//K 
        Task MyTask183 = new Task(26, 27, 29, 1, 2, 1);//L
        Task MyTask184 = new Task(0, 5, 7, 5, 2, 2);//M
        Task MyTask185 = new Task(15, 17, 20, 2, 3, 2);//N
        Task MyTask186 = new Task(11, 16, 24, 5, 8, 2);//N
        Task MyTask187 = new Task(29, 31, 34, 2, 3, 2);//N
        Task MyTask188 = new Task(18, 22, 29, 4, 7, 1);//N
        Task MyTask189 = new Task(25, 26, 26, 1, 0, 1);//N

        T = new Task[14];
        T[0] = MyTask176;
        T[1] = MyTask177;
        T[2] = MyTask178;
        T[3] = MyTask179;
        T[4] = MyTask180;
        T[5] = MyTask181;
        T[6] = MyTask182;
        T[7] = MyTask183;
        T[8] = MyTask184;
        T[9] = MyTask185;
        T[10] = MyTask186;
        T[11] = MyTask187;
        T[12] = MyTask188;
        T[13] = MyTask189;

        resourceCapacity = 4;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(17, outputArray[0]);
        assertEquals(27, outputArray[1]);
        assertEquals(0, outputArray[2]);
        assertEquals(5, outputArray[3]);
        assertEquals(18, outputArray[4]);
        assertEquals(7, outputArray[5]);
        assertEquals(22, outputArray[6]);
        assertEquals(26, outputArray[7]);
        assertEquals(0, outputArray[8]);
        assertEquals(15, outputArray[9]);
        assertEquals(11, outputArray[10]);
        assertEquals(29, outputArray[11]);
        assertEquals(18, outputArray[12]);
        assertEquals(25, outputArray[13]);
        assertEquals(17, outputArrayForNCumulative[0]);
        assertEquals(27, outputArrayForNCumulative[1]);
        assertEquals(0, outputArrayForNCumulative[2]);
        assertEquals(5, outputArrayForNCumulative[3]);
        assertEquals(18, outputArrayForNCumulative[4]);
        assertEquals(7, outputArrayForNCumulative[5]);
        assertEquals(22, outputArrayForNCumulative[6]);
        assertEquals(26, outputArrayForNCumulative[7]);
        assertEquals(0, outputArrayForNCumulative[8]);
        assertEquals(15, outputArrayForNCumulative[9]);
        assertEquals(11, outputArrayForNCumulative[10]);
        assertEquals(29, outputArrayForNCumulative[11]);
        assertEquals(18, outputArrayForNCumulative[12]);
        assertEquals(25, outputArrayForNCumulative[13]);

        Task MyTask171 = new Task(5, 22, 28, 3, 6, 3);//A  T0
        Task MyTask172 = new Task(5, 26, 26, 1, 0, 3);//B     T1
        Task MyTask173 = new Task(9, 10, 10, 1, 0, 3);//C   T2
        Task MyTask174 = new Task(7, 9, 10, 2, 1, 3);//D    T3
        Task MyTask175 = new Task(10, 22, 28, 5, 6, 1);//E     T4 
        T = new Task[5];
        T[0] = MyTask171;
        T[1] = MyTask172;
        T[2] = MyTask173;
        T[3] = MyTask174;
        T[4] = MyTask175;
        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(5, outputArray[0]);
        assertEquals(5, outputArray[1]);
        assertEquals(9, outputArray[2]);
        assertEquals(7, outputArray[3]);
        assertEquals(10, outputArray[4]);
        assertEquals(5, outputArrayForNCumulative[0]);
        assertEquals(5, outputArrayForNCumulative[1]);
        assertEquals(9, outputArrayForNCumulative[2]);
        assertEquals(7, outputArrayForNCumulative[3]);
        assertEquals(10, outputArrayForNCumulative[4]);

        Task MyTask15 = new Task(0, 6, 6, 3, 0, 2);
        Task MyTask16 = new Task(0, 6, 6, 3, 0, 2);
        Task MyTask17 = new Task(0, 6, 6, 4, 0, 1);
        Task MyTask18 = new Task(4, 7, 7, 3, 0, 1);
        T = new Task[4];
        T[0] = MyTask15;
        T[1] = MyTask16;
        T[2] = MyTask17;
        T[3] = MyTask18;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(0, outputArray[0]);
        assertEquals(0, outputArray[1]);
        assertEquals(0, outputArray[2]);
        assertEquals(4, outputArray[3]);
        assertEquals(0, outputArrayForNCumulative[0]);
        assertEquals(0, outputArrayForNCumulative[1]);
        assertEquals(0, outputArrayForNCumulative[2]);
        assertEquals(4, outputArrayForNCumulative[3]);

        Task MyTask258 = new Task(4, 30, 30, 4, 0, 1);//G
        Task MyTask259 = new Task(5, 13, 13, 3, 0, 1);//H
        Task MyTask260 = new Task(5, 13, 13, 3, 0, 1);//I
        Task MyTask261 = new Task(13, 18, 18, 5, 0, 1);//I
        T = new Task[4];
        T[0] = MyTask258;
        T[1] = MyTask259;
        T[2] = MyTask260;
        T[3] = MyTask261;
        resourceCapacity = 1;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 18);
        assertEquals(outputArray[1], 5);
        assertEquals(outputArray[2], 5);
        assertEquals(outputArray[3], 13);
        assertEquals(outputArrayForNCumulative[0], 18);
        assertEquals(outputArrayForNCumulative[1], 5);
        assertEquals(outputArrayForNCumulative[2], 5);
        assertEquals(outputArrayForNCumulative[3], 13);

        Task MyTask37 = new Task(1, 11, 11, 10, 0, 5);//A
        Task MyTask38 = new Task(1, 10, 11, 9, 1, 2);//B
        Task MyTask39 = new Task(0, 14, 14, 3, 0, 3);//C
        T = new Task[3];
        T[0] = MyTask37;
        T[1] = MyTask38;
        T[2] = MyTask39;
        resourceCapacity = 7;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 1);
        assertEquals(outputArray[1], 1);
        assertEquals(outputArray[2], 11);
        assertEquals(outputArrayForNCumulative[0], 1);
        assertEquals(outputArrayForNCumulative[1], 1);
        assertEquals(outputArrayForNCumulative[2], 11);

        Task MyTask40 = new Task(0, 4, 5, 2, 1, 3);//A
        Task MyTask41 = new Task(1, 5, 5, 3, 0, 2);//B
        Task MyTask42 = new Task(2, 8, 9, 3, 1, 4);//C
        T = new Task[3];
        T[0] = MyTask40;
        T[1] = MyTask41;
        T[2] = MyTask42;
        resourceCapacity = 5;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(0, outputArray[0]);
        assertEquals(1, outputArray[1]);
        assertEquals(3, outputArray[2]);
        assertEquals(0, outputArrayForNCumulative[0]);
        assertEquals(1, outputArrayForNCumulative[1]);
        assertEquals(3, outputArrayForNCumulative[2]);

        Task MyTask43 = new Task(15, 27, 29, 12, 2, 6);//A
        Task MyTask44 = new Task(15, 40, 44, 11, 4, 1);//B
        Task MyTask45 = new Task(18, 36, 38, 5, 2, 2);//C
        Task MyTask46 = new Task(16, 40, 40, 6, 0, 4);//D
        T = new Task[4];
        T[0] = MyTask43;
        T[1] = MyTask44;
        T[2] = MyTask45;
        T[3] = MyTask46;
        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 15);
        assertEquals(outputArray[1], 29);
        assertEquals(outputArray[2], 29);
        assertEquals(outputArray[3], 29);
        assertEquals(outputArrayForNCumulative[0], 15);
        assertEquals(outputArrayForNCumulative[1], 29);
        assertEquals(outputArrayForNCumulative[2], 29);
        assertEquals(outputArrayForNCumulative[3], 29);

        Task MyTask60 = new Task(10, 19, 22, 4, 3, 2);//A
        Task MyTask61 = new Task(12, 19, 21, 4, 2, 2);//B
        Task MyTask62 = new Task(10, 19, 19, 2, 0, 2);//C 
        Task MyTask63 = new Task(12, 19, 22, 5, 3, 2);//D
        Task MyTask64 = new Task(12, 19, 19, 1, 0, 1);//E
        Task MyTask65 = new Task(10, 19, 19, 3, 0, 1);//F
        Task MyTask66 = new Task(10, 19, 19, 3, 0, 3);//G 
        Task MyTask67 = new Task(10, 15, 15, 2, 0, 3);//H

        T = new Task[8];
        T[0] = MyTask60;
        T[1] = MyTask61;
        T[2] = MyTask62;
        T[3] = MyTask63;
        T[4] = MyTask64;
        T[5] = MyTask65;
        T[6] = MyTask66;
        T[7] = MyTask67;

        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(13, outputArray[0]);
        assertEquals(12, outputArray[1]);
        assertEquals(10, outputArray[2]);
        assertEquals(12, outputArray[3]);
        assertEquals(12, outputArray[4]);
        assertEquals(10, outputArray[5]);
        assertEquals(10, outputArray[6]);
        assertEquals(10, outputArray[7]);

        assertEquals(13, outputArrayForNCumulative[0]);
        assertEquals(12, outputArrayForNCumulative[1]);
        assertEquals(10, outputArrayForNCumulative[2]);
        assertEquals(12, outputArrayForNCumulative[3]);
        assertEquals(12, outputArrayForNCumulative[4]);
        assertEquals(10, outputArrayForNCumulative[5]);
        assertEquals(10, outputArrayForNCumulative[6]);
        assertEquals(10, outputArrayForNCumulative[7]);

        Task MyTask68 = new Task(6, 17, 17, 4, 0, 2);//A
        Task MyTask69 = new Task(6, 13, 14, 2, 1, 3);//B
        Task MyTask70 = new Task(9, 17, 20, 5, 3, 2);//C adjust
        Task MyTask71 = new Task(0, 5, 8, 5, 3, 3);//D
        Task MyTask72 = new Task(8, 17, 18, 1, 1, 1);//E
        Task MyTask73 = new Task(6, 12, 13, 3, 1, 1);//F
        Task MyTask74 = new Task(8, 17, 17, 4, 0, 2);//G 
        Task MyTask75 = new Task(12, 13, 14, 1, 1, 1);//H
        Task MyTask76 = new Task(0, 15, 16, 3, 1, 1);//I
        Task MyTask77 = new Task(14, 17, 17, 3, 0, 1);//J
        Task MyTask78 = new Task(6, 17, 20, 4, 3, 2);//K adjust
        Task MyTask79 = new Task(6, 17, 17, 2, 0, 2);//L
        Task MyTask80 = new Task(5, 6, 7, 1, 1, 1);//M
        Task MyTask81 = new Task(9, 17, 17, 3, 0, 3);//N
        Task MyTask82 = new Task(0, 13, 13, 2, 0, 3);//O
        Task MyTask83 = new Task(0, 14, 15, 3, 1, 2);//P

        T = new Task[16];
        T[0] = MyTask68;
        T[1] = MyTask69;
        T[2] = MyTask70;
        T[3] = MyTask71;
        T[4] = MyTask72;
        T[5] = MyTask73;
        T[6] = MyTask74;
        T[7] = MyTask75;
        T[8] = MyTask76;
        T[9] = MyTask77;
        T[10] = MyTask78;
        T[11] = MyTask79;
        T[12] = MyTask80;
        T[13] = MyTask81;
        T[14] = MyTask82;
        T[15] = MyTask83;

        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(6, outputArray[0]);
        assertEquals(6, outputArray[1]);
        assertEquals(10, outputArray[2]);
        assertEquals(0, outputArray[3]);
        assertEquals(8, outputArray[4]);
        assertEquals(6, outputArray[5]);
        assertEquals(8, outputArray[6]);
        assertEquals(12, outputArray[7]);
        assertEquals(0, outputArray[8]);
        assertEquals(14, outputArray[9]);
        assertEquals(11, outputArray[10]);
        assertEquals(6, outputArray[11]);
        assertEquals(5, outputArray[12]);
        assertEquals(9, outputArray[13]);
        assertEquals(0, outputArray[14]);
        assertEquals(0, outputArray[15]);

        assertEquals(6, outputArrayForNCumulative[0]);
        assertEquals(6, outputArrayForNCumulative[1]);
        assertEquals(10, outputArrayForNCumulative[2]);
        assertEquals(0, outputArrayForNCumulative[3]);
        assertEquals(8, outputArrayForNCumulative[4]);
        assertEquals(6, outputArrayForNCumulative[5]);
        assertEquals(8, outputArrayForNCumulative[6]);
        assertEquals(12, outputArrayForNCumulative[7]);
        assertEquals(0, outputArrayForNCumulative[8]);
        assertEquals(14, outputArrayForNCumulative[9]);
        assertEquals(11, outputArrayForNCumulative[10]);
        assertEquals(6, outputArrayForNCumulative[11]);
        assertEquals(5, outputArrayForNCumulative[12]);
        assertEquals(9, outputArrayForNCumulative[13]);
        assertEquals(0, outputArrayForNCumulative[14]);
        assertEquals(0, outputArrayForNCumulative[15]);

        Task MyTask84 = new Task(10, 19, 22, 4, 3, 2);//A
        Task MyTask85 = new Task(10, 15, 15, 2, 0, 3);//B
        Task MyTask86 = new Task(10, 19, 19, 2, 0, 2);//C
        Task MyTask87 = new Task(12, 19, 22, 5, 3, 2);//D
        Task MyTask88 = new Task(12, 19, 21, 4, 2, 2);//E
        Task MyTask89 = new Task(10, 19, 19, 3, 0, 1);//F
        Task MyTask90 = new Task(10, 19, 19, 3, 0, 3);//G 
        Task MyTask91 = new Task(12, 19, 19, 1, 0, 1);//A

        T = new Task[8];
        T[0] = MyTask84;
        T[1] = MyTask85;
        T[2] = MyTask86;
        T[3] = MyTask87;
        T[4] = MyTask88;
        T[5] = MyTask89;
        T[6] = MyTask90;
        T[7] = MyTask91;

        resourceCapacity = 6;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(13, outputArray[0]);
        assertEquals(10, outputArray[1]);
        assertEquals(10, outputArray[2]);
        assertEquals(12, outputArray[3]);
        assertEquals(12, outputArray[4]);
        assertEquals(10, outputArray[5]);
        assertEquals(10, outputArray[6]);
        assertEquals(12, outputArray[7]);

        assertEquals(13, outputArrayForNCumulative[0]);
        assertEquals(10, outputArrayForNCumulative[1]);
        assertEquals(10, outputArrayForNCumulative[2]);
        assertEquals(12, outputArrayForNCumulative[3]);
        assertEquals(12, outputArrayForNCumulative[4]);
        assertEquals(10, outputArrayForNCumulative[5]);
        assertEquals(10, outputArrayForNCumulative[6]);
        assertEquals(12, outputArrayForNCumulative[7]);

        Task MyTask210 = new Task(28, 32, 39, 4, 7, 6);
        Task MyTask211 = new Task(27, 28, 28, 1, 0, 1);
        Task MyTask212 = new Task(0, 8, 21, 8, 13, 8);
        Task MyTask213 = new Task(21, 23, 27, 2, 4, 6);
        Task MyTask214 = new Task(33, 39, 41, 5, 2, 2);
        Task MyTask215 = new Task(41, 43, 44, 2, 1, 2);
        Task MyTask216 = new Task(47, 51, 54, 4, 3, 8);
        Task MyTask217 = new Task(21, 31, 47, 10, 16, 2);
        T = new Task[8];
        T[0] = MyTask210;
        T[1] = MyTask211;
        T[2] = MyTask212;
        T[3] = MyTask213;
        T[4] = MyTask214;
        T[5] = MyTask215;
        T[6] = MyTask216;
        T[7] = MyTask217;
        resourceCapacity = 8;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 28);
        assertEquals(outputArray[1], 27);
        assertEquals(outputArray[2], 0);
        assertEquals(outputArray[3], 21);
        assertEquals(outputArray[4], 33);
        assertEquals(outputArray[5], 41);
        assertEquals(outputArray[6], 47);
        assertEquals(outputArray[7], 21);
        assertEquals(outputArrayForNCumulative[0], 28);
        assertEquals(outputArrayForNCumulative[1], 27);
        assertEquals(outputArrayForNCumulative[2], 0);
        assertEquals(outputArrayForNCumulative[3], 21);
        assertEquals(outputArrayForNCumulative[4], 33);
        assertEquals(outputArrayForNCumulative[5], 41);
        assertEquals(outputArrayForNCumulative[6], 47);
        assertEquals(outputArrayForNCumulative[7], 21);

        Task MyTask111 = new Task(13, 17, 21, 4, 4, 11);
        Task MyTask112 = new Task(0, 4, 4, 4, 0, 9);
        Task MyTask113 = new Task(4, 5, 5, 1, 0, 7);
        Task MyTask114 = new Task(5, 9, 11, 4, 2, 6);
        Task MyTask115 = new Task(12, 13, 13, 1, 0, 5);
        Task MyTask116 = new Task(10, 12, 13, 2, 1, 5);
        Task MyTask117 = new Task(5, 10, 10, 5, 0, 3);
        Task MyTask118 = new Task(0, 10, 12, 10, 2, 2);
        T = new Task[8];
        T[0] = MyTask111;
        T[1] = MyTask112;
        T[2] = MyTask113;
        T[3] = MyTask114;
        T[4] = MyTask115;
        T[5] = MyTask116;
        T[6] = MyTask117;
        T[7] = MyTask118;
        resourceCapacity = 11;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 13);
        assertEquals(outputArray[1], 0);
        assertEquals(outputArray[2], 4);
        assertEquals(outputArray[3], 5);
        assertEquals(outputArray[4], 12);
        assertEquals(outputArray[5], 10);
        assertEquals(outputArray[6], 5);
        assertEquals(outputArray[7], 0);
        assertEquals(outputArrayForNCumulative[0], 13);
        assertEquals(outputArrayForNCumulative[1], 0);
        assertEquals(outputArrayForNCumulative[2], 4);
        assertEquals(outputArrayForNCumulative[3], 5);
        assertEquals(outputArrayForNCumulative[4], 12);
        assertEquals(outputArrayForNCumulative[5], 10);
        assertEquals(outputArrayForNCumulative[6], 5);
        assertEquals(outputArrayForNCumulative[7], 0);

        Task MyTask128 = new Task(3, 7, 11, 4, 4, 1);//A
        Task MyTask129 = new Task(0, 1, 2, 1, 1, 6);//B
        Task MyTask130 = new Task(2, 10, 16, 8, 6, 8);//C
        T = new Task[3];
        T[0] = MyTask128;
        T[1] = MyTask129;
        T[2] = MyTask130;
        resourceCapacity = 9;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 3);
        assertEquals(outputArray[1], 0);
        assertEquals(outputArray[2], 2);
        assertEquals(outputArrayForNCumulative[0], 3);
        assertEquals(outputArrayForNCumulative[1], 0);
        assertEquals(outputArrayForNCumulative[2], 2);

        Task MyTask139 = new Task(0, 6, 8, 6, 2, 6);//A
        Task MyTask140 = new Task(8, 54, 56, 9, 2, 1);//B
        Task MyTask141 = new Task(29, 70, 74, 4, 4, 1);//C
        Task MyTask142 = new Task(24, 66, 68, 5, 2, 1);//D
        Task MyTask143 = new Task(24, 70, 76, 14, 6, 1);//E
        T = new Task[5];
        T[0] = MyTask139;
        T[1] = MyTask140;
        T[2] = MyTask141;
        T[3] = MyTask142;
        T[4] = MyTask143;
        resourceCapacity = 6;
        delayNumber = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 0);
        assertEquals(outputArray[1], 8);
        assertEquals(outputArray[2], 29);
        assertEquals(outputArray[3], 24);
        assertEquals(outputArray[4], 24);
        assertEquals(outputArrayForNCumulative[0], 0);
        assertEquals(outputArrayForNCumulative[1], 8);
        assertEquals(outputArrayForNCumulative[2], 29);
        assertEquals(outputArrayForNCumulative[3], 24);
        assertEquals(outputArrayForNCumulative[4], 24);

        Task MyTask144 = new Task(10, 17, 18, 5, 1, 1);//A
        Task MyTask145 = new Task(4, 17, 18, 4, 1, 3);//B
        Task MyTask146 = new Task(5, 23, 27, 9, 4, 2);//C
        Task MyTask147 = new Task(2, 12, 14, 6, 2, 2);//D
        Task MyTask148 = new Task(7, 33, 34, 4, 1, 6);//E
        Task MyTask149 = new Task(1, 16, 19, 8, 3, 5);//D
        Task MyTask150 = new Task(10, 30, 34, 7, 4, 4);//E
        T = new Task[7];
        T[0] = MyTask144;
        T[1] = MyTask145;
        T[2] = MyTask146;
        T[3] = MyTask147;
        T[4] = MyTask148;
        T[5] = MyTask149;
        T[6] = MyTask150;
        resourceCapacity = 7;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 10);
        assertEquals(outputArray[1], 4);
        assertEquals(outputArray[2], 5);
        assertEquals(outputArray[3], 2);
        assertEquals(outputArray[4], 7);
        assertEquals(outputArray[5], 1);
        assertEquals(outputArray[6], 10);
        assertEquals(outputArrayForNCumulative[0], 10);
        assertEquals(outputArrayForNCumulative[1], 4);
        assertEquals(outputArrayForNCumulative[2], 5);
        assertEquals(outputArrayForNCumulative[3], 2);
        assertEquals(outputArrayForNCumulative[4], 7);
        assertEquals(outputArrayForNCumulative[5], 1);
        assertEquals(outputArrayForNCumulative[6], 10);

        Task MyTask151 = new Task(0, 5, 10, 5, 5, 9);//A
        Task MyTask152 = new Task(11, 29, 31, 4, 2, 8);//B
        Task MyTask153 = new Task(10, 29, 38, 10, 9, 7);//C
        Task MyTask154 = new Task(10, 29, 31, 2, 2, 6);//D
        Task MyTask155 = new Task(10, 29, 29, 2, 0, 2);//E
        Task MyTask156 = new Task(10, 29, 29, 4, 0, 1);//E
        T = new Task[6];
        T[0] = MyTask151;
        T[1] = MyTask152;
        T[2] = MyTask153;
        T[3] = MyTask154;
        T[4] = MyTask155;
        T[5] = MyTask156;
        resourceCapacity = 9;
        delayNumber = 1;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(outputArray[0], 0);
        assertEquals(outputArray[1], 11);
        assertEquals(outputArray[2], 12);
        assertEquals(outputArray[3], 10);
        assertEquals(outputArray[4], 10);
        assertEquals(outputArray[5], 10);
        assertEquals(outputArrayForNCumulative[0], 0);
        assertEquals(outputArrayForNCumulative[1], 11);
        assertEquals(outputArrayForNCumulative[2], 12);
        assertEquals(outputArrayForNCumulative[3], 10);
        assertEquals(outputArrayForNCumulative[4], 10);
        assertEquals(outputArrayForNCumulative[5], 10);

        Task MyTask161 = new Task(16, 27, 28, 10, 1, 2);//A
        Task MyTask162 = new Task(0, 27, 29, 5, 2, 2);//B
        Task MyTask163 = new Task(0, 17, 18, 8, 1, 8);//C
        Task MyTask164 = new Task(0, 18, 20, 2, 2, 6);//D
        Task MyTask165 = new Task(18, 19, 19, 1, 0, 1);//E
        Task MyTask166 = new Task(0, 27, 27, 2, 0, 2);//F
        Task MyTask167 = new Task(0, 14, 18, 4, 4, 8);//G
        T = new Task[7];
        T[0] = MyTask161;
        T[1] = MyTask162;
        T[2] = MyTask163;
        T[3] = MyTask164;
        T[4] = MyTask165;
        T[5] = MyTask166;
        T[6] = MyTask167;
        resourceCapacity = 8;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(16, outputArray[0]);
        assertEquals(16, outputArray[1]);
        assertEquals(0, outputArray[2]);
        assertEquals(0, outputArray[3]);
        assertEquals(18, outputArray[4]);
        assertEquals(0, outputArray[5]);
        assertEquals(0, outputArray[6]);
        assertEquals(16, outputArrayForNCumulative[0]);
        assertEquals(16, outputArrayForNCumulative[1]);
        assertEquals(0, outputArrayForNCumulative[2]);
        assertEquals(0, outputArrayForNCumulative[3]);
        assertEquals(18, outputArrayForNCumulative[4]);
        assertEquals(0, outputArrayForNCumulative[5]);
        assertEquals(0, outputArrayForNCumulative[6]);

        Task MyTask250 = new Task(0, 12, 12, 3, 0, 2);//A
        Task MyTask251 = new Task(9, 19, 19, 4, 0, 1);//B
        T = new Task[2];
        T[0] = MyTask250;
        T[1] = MyTask251;
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
        assertEquals(0, outputArray[0]);
        assertEquals(9, outputArray[1]);
        assertEquals(0, outputArrayForNCumulative[0]);
        assertEquals(9, outputArrayForNCumulative[1]);

        // Filtering lct
        Task MyTask252 = new Task(6, 8, 8, 1, 0, 1);//G
        Task MyTask253 = new Task(6, 8, 8, 2, 0, 2);//H
        Task MyTask254 = new Task(0, 10, 10, 5, 0, 1);//I
        T = new Task[3];
        T[0] = MyTask252;
        T[1] = MyTask253;
        T[2] = MyTask254;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(outputArray[0], 8);
        assertEquals(outputArray[1], 8);
        assertEquals(outputArray[2], 10);
        assertEquals(outputArrayForNCumulative[0], 8);
        assertEquals(outputArrayForNCumulative[1], 8);
        assertEquals(outputArrayForNCumulative[2], 10);

        Task MyTask242 = new Task(7, 19, 19, 3, 0, 2);//A
        Task MyTask243 = new Task(0, 10, 10, 4, 0, 1);//B
        T = new Task[2];
        T[0] = MyTask242;
        T[1] = MyTask243;
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(19, outputArray[0]);
        assertEquals(10, outputArray[1]);
        assertEquals(19, outputArrayForNCumulative[0]);
        assertEquals(10, outputArrayForNCumulative[1]);

        Task MyTask318 = new Task(5, 10, 10, 1, 0, 3);//A
        Task MyTask319 = new Task(5, 8, 8, 3, 0, 1);//B
        Task MyTask320 = new Task(5, 8, 8, 2, 0, 2);//C
        Task MyTask321 = new Task(0, 9, 10, 2, 1, 2);//D
        T = new Task[4];
        T[0] = MyTask318;
        T[1] = MyTask319;
        T[2] = MyTask320;
        T[3] = MyTask321;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(10, outputArray[0]);
        assertEquals(8, outputArray[1]);
        assertEquals(8, outputArray[2]);
        assertEquals(5, outputArray[3]);
        assertEquals(10, outputArrayForNCumulative[0]);
        assertEquals(8, outputArrayForNCumulative[1]);
        assertEquals(8, outputArrayForNCumulative[2]);
        assertEquals(5, outputArrayForNCumulative[3]);

        Task MyTask393 = new Task(0, 9, 23, 5, 14, 3);//A
        Task MyTask394 = new Task(5, 12, 22, 4, 10, 4);//B
        Task MyTask395 = new Task(38, 44, 54, 6, 10, 1);//C
        Task MyTask396 = new Task(54, 63, 78, 9, 15, 4);//D
        Task MyTask397 = new Task(10, 24, 26, 2, 2, 3);//E
        T = new Task[5];
        T[0] = MyTask393;
        T[1] = MyTask394;
        T[2] = MyTask395;
        T[3] = MyTask396;
        T[4] = MyTask397;
        resourceCapacity = 4;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(9, outputArray[0]);
        assertEquals(12, outputArray[1]);
        assertEquals(44, outputArray[2]);
        assertEquals(63, outputArray[3]);
        assertEquals(24, outputArray[4]);
        assertEquals(9, outputArrayForNCumulative[0]);
        assertEquals(12, outputArrayForNCumulative[1]);
        assertEquals(44, outputArrayForNCumulative[2]);
        assertEquals(63, outputArrayForNCumulative[3]);
        assertEquals(24, outputArrayForNCumulative[4]);

        Task MyTask244 = new Task(1, 5, 5, 4, 0, 2);//A
        Task MyTask245 = new Task(0, 4, 4, 1, 0, 1);//B
        T = new Task[2];
        T[0] = MyTask244;
        T[1] = MyTask245;
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(5, outputArray[0]);
        assertEquals(1, outputArray[1]);
        assertEquals(5, outputArrayForNCumulative[0]);
        assertEquals(1, outputArrayForNCumulative[1]);

        Task MyTask300 = new Task(0, 11, 13, 2, 2, 1);//A
        Task MyTask301 = new Task(8, 14, 14, 5, 0, 2);//B
        Task MyTask302 = new Task(7, 14, 14, 1, 0, 1);//C
        T = new Task[3];
        T[0] = MyTask300;
        T[1] = MyTask301;
        T[2] = MyTask302;
        resourceCapacity = 2;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(8, outputArray[0]);
        assertEquals(14, outputArray[1]);
        assertEquals(14, outputArray[2]);
        assertEquals(8, outputArrayForNCumulative[0]);
        assertEquals(14, outputArrayForNCumulative[1]);
        assertEquals(14, outputArrayForNCumulative[2]);

        Task MyTask463 = new Task(17, 25, 25, 8, 0, 5);//A
        Task MyTask464 = new Task(13, 14, 14, 1, 0, 7);//B
        Task MyTask465 = new Task(0, 11, 13, 3, 2, 7);//C
        Task MyTask466 = new Task(5, 17, 17, 2, 0, 6);//D
        Task MyTask467 = new Task(0, 15, 17, 5, 2, 3);//E
        Task MyTask468 = new Task(4, 14, 17, 4, 3, 2);//F
        T = new Task[6];
        T[0] = MyTask463;
        T[1] = MyTask464;
        T[2] = MyTask465;
        T[3] = MyTask466;
        T[4] = MyTask467;
        T[5] = MyTask468;
        resourceCapacity = 10;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(25, outputArray[0]);
        assertEquals(14, outputArray[1]);
        assertEquals(11, outputArray[2]);
        assertEquals(17, outputArray[3]);
        assertEquals(15, outputArray[4]);
        assertEquals(14, outputArray[5]);
        assertEquals(25, outputArrayForNCumulative[0]);
        assertEquals(14, outputArrayForNCumulative[1]);
        assertEquals(11, outputArrayForNCumulative[2]);
        assertEquals(17, outputArrayForNCumulative[3]);
        assertEquals(15, outputArrayForNCumulative[4]);
        assertEquals(14, outputArrayForNCumulative[5]);

        Task MyTask329 = new Task(0, 4, 8, 4, 4, 1);//A
        Task MyTask330 = new Task(0, 1, 2, 1, 1, 6);//B
        Task MyTask331 = new Task(10, 27, 31, 8, 4, 8);//C
        Task MyTask332 = new Task(22, 30, 30, 2, 0, 6);//D
        Task MyTask333 = new Task(24, 35, 35, 5, 0, 9);//E
        Task MyTask334 = new Task(29, 37, 37, 2, 0, 2);//F
        Task MyTask335 = new Task(4, 19, 20, 4, 1, 8);//G
        Task MyTask336 = new Task(2, 37, 37, 10, 0, 7);//H
        T = new Task[8];
        T[0] = MyTask329;
        T[1] = MyTask330;
        T[2] = MyTask331;
        T[3] = MyTask332;
        T[4] = MyTask333;
        T[5] = MyTask334;
        T[6] = MyTask335;
        T[7] = MyTask336;
        resourceCapacity = 9;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(4, outputArray[0]);
        assertEquals(1, outputArray[1]);
        assertEquals(25, outputArray[2]);
        assertEquals(30, outputArray[3]);
        assertEquals(35, outputArray[4]);
        assertEquals(37, outputArray[5]);
        assertEquals(19, outputArray[6]);
        assertEquals(30, outputArray[7]);
        assertEquals(4, outputArrayForNCumulative[0]);
        assertEquals(1, outputArrayForNCumulative[1]);
        assertEquals(25, outputArrayForNCumulative[2]);
        assertEquals(30, outputArrayForNCumulative[3]);
        assertEquals(35, outputArrayForNCumulative[4]);
        assertEquals(37, outputArrayForNCumulative[5]);
        assertEquals(19, outputArrayForNCumulative[6]);
        assertEquals(30, outputArrayForNCumulative[7]);

        Task MyTask307 = new Task(11, 17, 21, 6, 4, 1);//A
        Task MyTask308 = new Task(22, 39, 39, 9, 0, 4);//B
        Task MyTask309 = new Task(0, 2, 3, 2, 1, 3);//C
        Task MyTask310 = new Task(11, 27, 30, 5, 3, 3);//D
        Task MyTask311 = new Task(21, 39, 42, 4, 3, 4);//E
        Task MyTask312 = new Task(3, 7, 11, 4, 4, 4);//G
        Task MyTask313 = new Task(21, 39, 41, 4, 2, 4);//H
        T = new Task[7];
        T[0] = MyTask307;
        T[1] = MyTask308;
        T[2] = MyTask309;
        T[3] = MyTask310;
        T[4] = MyTask311;
        T[5] = MyTask312;
        T[6] = MyTask313;
        resourceCapacity = 4;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(17, outputArray[0]);
        assertEquals(39, outputArray[1]);
        assertEquals(2, outputArray[2]);
        assertEquals(19, outputArray[3]);
        assertEquals(39, outputArray[4]);
        assertEquals(7, outputArray[5]);
        assertEquals(39, outputArray[6]);
        assertEquals(17, outputArrayForNCumulative[0]);
        assertEquals(39, outputArrayForNCumulative[1]);
        assertEquals(2, outputArrayForNCumulative[2]);
        assertEquals(19, outputArrayForNCumulative[3]);
        assertEquals(39, outputArrayForNCumulative[4]);
        assertEquals(7, outputArrayForNCumulative[5]);
        assertEquals(39, outputArrayForNCumulative[6]);

        Task MyTask370 = new Task(0, 4, 5, 4, 1, 1);//A
        Task MyTask371 = new Task(0, 1, 1, 1, 0, 7);//B
        Task MyTask372 = new Task(5, 13, 15, 8, 2, 5);//C
        Task MyTask373 = new Task(15, 17, 17, 2, 0, 9);//D
        Task MyTask374 = new Task(17, 52, 55, 5, 3, 3);//E
        Task MyTask375 = new Task(1, 54, 55, 3, 1, 7);//F
        Task MyTask376 = new Task(17, 54, 54, 4, 0, 2);//G
        Task MyTask377 = new Task(22, 54, 54, 2, 0, 6);//H
        Task MyTask378 = new Task(17, 54, 55, 4, 1, 10);//I
        Task MyTask379 = new Task(17, 54, 57, 10, 3, 2);//J
        T = new Task[10];
        T[0] = MyTask370;
        T[1] = MyTask371;
        T[2] = MyTask372;
        T[3] = MyTask373;
        T[4] = MyTask374;
        T[5] = MyTask375;
        T[6] = MyTask376;
        T[7] = MyTask377;
        T[8] = MyTask378;
        T[9] = MyTask379;
        resourceCapacity = 10;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(4, outputArray[0]);
        assertEquals(1, outputArray[1]);
        assertEquals(13, outputArray[2]);
        assertEquals(17, outputArray[3]);
        assertEquals(52, outputArray[4]);
        assertEquals(54, outputArray[5]);
        assertEquals(54, outputArray[6]);
        assertEquals(54, outputArray[7]);
        assertEquals(54, outputArray[8]);
        assertEquals(54, outputArray[9]);
        assertEquals(4, outputArrayForNCumulative[0]);
        assertEquals(1, outputArrayForNCumulative[1]);
        assertEquals(13, outputArrayForNCumulative[2]);
        assertEquals(17, outputArrayForNCumulative[3]);
        assertEquals(52, outputArrayForNCumulative[4]);
        assertEquals(54, outputArrayForNCumulative[5]);
        assertEquals(54, outputArrayForNCumulative[6]);
        assertEquals(54, outputArrayForNCumulative[7]);
        assertEquals(54, outputArrayForNCumulative[8]);
        assertEquals(54, outputArrayForNCumulative[9]);

        Task MyTask255 = new Task(5, 8, 8, 1, 0, 3);//G
        Task MyTask256 = new Task(5, 8, 8, 2, 0, 3);//H
        Task MyTask257 = new Task(0, 10, 10, 5, 0, 1);//I
        T = new Task[3];
        T[0] = MyTask255;
        T[1] = MyTask256;
        T[2] = MyTask257;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(outputArray[0], 8);
        assertEquals(outputArray[1], 8);
        assertEquals(outputArray[2], 10);
        assertEquals(outputArrayForNCumulative[0], 8);
        assertEquals(outputArrayForNCumulative[1], 8);
        assertEquals(outputArrayForNCumulative[2], 10);

        Task MyTask353 = new Task(0, 4, 5, 4, 1, 6);//A
        Task MyTask354 = new Task(10, 11, 11, 1, 0, 1);//B
        Task MyTask355 = new Task(24, 32, 34, 8, 2, 8);//C
        Task MyTask356 = new Task(13, 15, 15, 2, 0, 6);//D
        Task MyTask357 = new Task(5, 10, 13, 5, 3, 2);//E
        Task MyTask358 = new Task(15, 23, 24, 3, 1, 2);//F
        Task MyTask359 = new Task(15, 24, 24, 4, 0, 6);//G
        Task MyTask360 = new Task(11, 24, 24, 2, 0, 2);//H
        Task MyTask361 = new Task(15, 23, 24, 4, 1, 8);//I
        Task MyTask362 = new Task(0, 10, 13, 10, 3, 2);//J
        T = new Task[10];
        T[0] = MyTask353;
        T[1] = MyTask354;
        T[2] = MyTask355;
        T[3] = MyTask356;
        T[4] = MyTask357;
        T[5] = MyTask358;
        T[6] = MyTask359;
        T[7] = MyTask360;
        T[8] = MyTask361;
        T[9] = MyTask362;
        resourceCapacity = 8;

        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(4, outputArray[0]);
        assertEquals(11, outputArray[1]);
        assertEquals(32, outputArray[2]);
        assertEquals(15, outputArray[3]);
        assertEquals(10, outputArray[4]);
        assertEquals(23, outputArray[5]);
        assertEquals(24, outputArray[6]);
        assertEquals(16, outputArray[7]);
        assertEquals(23, outputArray[8]);
        assertEquals(10, outputArray[9]);
        assertEquals(4, outputArrayForNCumulative[0]);
        assertEquals(11, outputArrayForNCumulative[1]);
        assertEquals(32, outputArrayForNCumulative[2]);
        assertEquals(15, outputArrayForNCumulative[3]);
        assertEquals(10, outputArrayForNCumulative[4]);
        assertEquals(23, outputArrayForNCumulative[5]);
        assertEquals(24, outputArrayForNCumulative[6]);
        assertEquals(16, outputArrayForNCumulative[7]);
        assertEquals(23, outputArrayForNCumulative[8]);
        assertEquals(10, outputArrayForNCumulative[9]);

        Task MyTask314 = new Task(28, 37, 38, 2, 1, 6);//A
        Task MyTask315 = new Task(30, 42, 47, 5, 5, 9);//B
        Task MyTask316 = new Task(35, 44, 44, 2, 0, 2);//C
        Task MyTask317 = new Task(2, 37, 39, 10, 2, 7);//D
        T = new Task[4];
        T[0] = MyTask314;
        T[1] = MyTask315;
        T[2] = MyTask316;
        T[3] = MyTask317;
        resourceCapacity = 9;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(37, outputArray[0]);
        assertEquals(42, outputArray[1]);
        assertEquals(44, outputArray[2]);
        assertEquals(35, outputArray[3]);
        assertEquals(37, outputArrayForNCumulative[0]);
        assertEquals(42, outputArrayForNCumulative[1]);
        assertEquals(44, outputArrayForNCumulative[2]);
        assertEquals(35, outputArrayForNCumulative[3]);

        Task MyTask326 = new Task(3, 5, 5, 2, 0, 3);//A
        Task MyTask327 = new Task(1, 4, 5, 1, 1, 2);//B
        Task MyTask328 = new Task(2, 5, 5, 1, 0, 1);//B
        T = new Task[3];
        T[0] = MyTask326;
        T[1] = MyTask327;
        T[2] = MyTask328;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(5, outputArray[0]);
        assertEquals(2, outputArray[1]);
        assertEquals(3, outputArray[2]);
        assertEquals(5, outputArrayForNCumulative[0]);
        assertEquals(2, outputArrayForNCumulative[1]);
        assertEquals(3, outputArrayForNCumulative[2]);

        Task MyTask303 = new Task(13, 29, 35, 8, 6, 8);//C
        Task MyTask304 = new Task(30, 35, 36, 2, 1, 6);//D
        Task MyTask305 = new Task(37, 42, 42, 5, 0, 9);//E
        Task MyTask306 = new Task(42, 44, 44, 2, 0, 2);//F
        T = new Task[4];
        T[0] = MyTask303;
        T[1] = MyTask304;
        T[2] = MyTask305;
        T[3] = MyTask306;
        resourceCapacity = 9;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(28, outputArray[0]);
        assertEquals(35, outputArray[1]);
        assertEquals(42, outputArray[2]);
        assertEquals(44, outputArray[3]);
        assertEquals(28, outputArrayForNCumulative[0]);
        assertEquals(35, outputArrayForNCumulative[1]);
        assertEquals(42, outputArrayForNCumulative[2]);
        assertEquals(44, outputArrayForNCumulative[3]);

        Task MyTask337 = new Task(11, 17, 20, 6, 3, 1);//A
        Task MyTask338 = new Task(21, 37, 38, 9, 1, 4);//B
        Task MyTask339 = new Task(0, 2, 4, 2, 2, 3);//C
        Task MyTask340 = new Task(11, 27, 28, 5, 1, 3);//D
        Task MyTask341 = new Task(20, 37, 38, 4, 1, 4);//E
        Task MyTask342 = new Task(4, 8, 11, 4, 3, 4);//G
        Task MyTask343 = new Task(20, 37, 37, 4, 0, 4);//H
        T = new Task[7];
        T[0] = MyTask337;
        T[1] = MyTask338;
        T[2] = MyTask339;
        T[3] = MyTask340;
        T[4] = MyTask341;
        T[5] = MyTask342;
        T[6] = MyTask343;
        resourceCapacity = 4;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(17, outputArray[0]);
        assertEquals(37, outputArray[1]);
        assertEquals(2, outputArray[2]);
        assertEquals(19, outputArray[3]);
        assertEquals(37, outputArray[4]);
        assertEquals(8, outputArray[5]);
        assertEquals(37, outputArray[6]);
        assertEquals(17, outputArrayForNCumulative[0]);
        assertEquals(37, outputArrayForNCumulative[1]);
        assertEquals(2, outputArrayForNCumulative[2]);
        assertEquals(19, outputArrayForNCumulative[3]);
        assertEquals(37, outputArrayForNCumulative[4]);
        assertEquals(8, outputArrayForNCumulative[5]);
        assertEquals(37, outputArrayForNCumulative[6]);

        Task MyTask380 = new Task(0, 1, 1, 1, 0, 7);//A
        Task MyTask381 = new Task(15, 24, 26, 8, 2, 5);//B
        Task MyTask382 = new Task(25, 31, 31, 2, 0, 9);//C
        Task MyTask383 = new Task(6, 15, 15, 5, 0, 3);//D
        Task MyTask384 = new Task(6, 14, 15, 3, 1, 7);//E
        Task MyTask385 = new Task(6, 15, 15, 4, 0, 2);//F
        Task MyTask386 = new Task(11, 25, 25, 2, 0, 6);//G
        Task MyTask387 = new Task(25, 31, 31, 4, 0, 10);//H
        Task MyTask388 = new Task(1, 27, 29, 10, 2, 2);//I
        T = new Task[9];
        T[0] = MyTask380;
        T[1] = MyTask381;
        T[2] = MyTask382;
        T[3] = MyTask383;
        T[4] = MyTask384;
        T[5] = MyTask385;
        T[6] = MyTask386;
        T[7] = MyTask387;
        T[8] = MyTask388;
        resourceCapacity = 10;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(1, outputArray[0]);
        assertEquals(23, outputArray[1]);
        assertEquals(31, outputArray[2]);
        assertEquals(15, outputArray[3]);
        assertEquals(14, outputArray[4]);
        assertEquals(15, outputArray[5]);
        assertEquals(25, outputArray[6]);
        assertEquals(31, outputArray[7]);
        assertEquals(24, outputArray[8]);
        assertEquals(1, outputArrayForNCumulative[0]);
        assertEquals(23, outputArrayForNCumulative[1]);
        assertEquals(31, outputArrayForNCumulative[2]);
        assertEquals(15, outputArrayForNCumulative[3]);
        assertEquals(14, outputArrayForNCumulative[4]);
        assertEquals(15, outputArrayForNCumulative[5]);
        assertEquals(25, outputArrayForNCumulative[6]);
        assertEquals(31, outputArrayForNCumulative[7]);
        assertEquals(24, outputArrayForNCumulative[8]);

        //check this later. improving makes it better
        Task MyTask322 = new Task(9, 15, 15, 5, 0, 2);//A
        Task MyTask323 = new Task(7, 12, 12, 5, 0, 1);//B
        Task MyTask324 = new Task(6, 10, 12, 1, 2, 2);//C
        Task MyTask325 = new Task(0, 12, 15, 3, 3, 3);//D
        T = new Task[4];
        T[0] = MyTask322;
        T[1] = MyTask323;
        T[2] = MyTask324;
        T[3] = MyTask325;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(15, outputArray[0]);
        assertEquals(12, outputArray[1]);
        assertEquals(10, outputArray[2]);
        assertEquals(6, outputArray[3]);
        assertEquals(15, outputArrayForNCumulative[0]);
        assertEquals(12, outputArrayForNCumulative[1]);
        assertEquals(10, outputArrayForNCumulative[2]);
        assertEquals(6, outputArrayForNCumulative[3]);

        Task MyTask344 = new Task(1, 13, 14, 4, 1, 6);//A
        Task MyTask345 = new Task(6, 23, 24, 9, 1, 7);//B
        Task MyTask346 = new Task(22, 36, 46, 8, 10, 2);//C
        Task MyTask347 = new Task(15, 28, 29, 12, 1, 6);//D
        T = new Task[4];
        T[0] = MyTask344;
        T[1] = MyTask345;
        T[2] = MyTask346;
        T[3] = MyTask347;
        resourceCapacity = 7;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(outputArray[0], 6);//In the first iteration filters to 8, again filters to 6
        assertEquals(outputArray[1], 16);
        assertEquals(outputArray[2], 36);
        assertEquals(outputArray[3], 28);
        assertEquals(outputArrayForNCumulative[0], 6);
        assertEquals(outputArrayForNCumulative[1], 16);
        assertEquals(outputArrayForNCumulative[2], 36);
        assertEquals(outputArrayForNCumulative[3], 28);

        Task MyTask363 = new Task(7, 13, 19, 6, 6, 1);//C
        Task MyTask364 = new Task(33, 42, 56, 9, 14, 4);//D
        Task MyTask365 = new Task(0, 2, 2, 2, 0, 3);//E
        Task MyTask366 = new Task(7, 29, 29, 5, 0, 3);//F
        Task MyTask367 = new Task(19, 33, 39, 4, 6, 4);//D
        Task MyTask368 = new Task(2, 6, 7, 4, 1, 4);//E
        Task MyTask369 = new Task(19, 33, 34, 4, 1, 4);//F
        T = new Task[7];
        T[0] = MyTask363;
        T[1] = MyTask364;
        T[2] = MyTask365;
        T[3] = MyTask366;
        T[4] = MyTask367;
        T[5] = MyTask368;
        T[6] = MyTask369;
        resourceCapacity = 4;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(13, outputArray[0]);
        assertEquals(42, outputArray[1]);
        assertEquals(2, outputArray[2]);
        assertEquals(19, outputArray[3]);
        assertEquals(27, outputArray[4]);
        assertEquals(6, outputArray[5]);
        assertEquals(32, outputArray[6]);
        assertEquals(13, outputArrayForNCumulative[0]);
        assertEquals(42, outputArrayForNCumulative[1]);
        assertEquals(2, outputArrayForNCumulative[2]);
        assertEquals(19, outputArrayForNCumulative[3]);
        assertEquals(27, outputArrayForNCumulative[4]);
        assertEquals(6, outputArrayForNCumulative[5]);
        assertEquals(32, outputArrayForNCumulative[6]);

        Task MyTask348 = new Task(73, 86, 93, 13, 7, 1);//A
        Task MyTask349 = new Task(56, 69, 78, 9, 9, 7);//B
        Task MyTask350 = new Task(36, 54, 57, 15, 3, 9);//C
        Task MyTask351 = new Task(36, 100, 102, 3, 2, 1);//E
        Task MyTask352 = new Task(53, 68, 73, 8, 5, 6);//D
        T = new Task[5];
        T[0] = MyTask348;
        T[1] = MyTask349;
        T[2] = MyTask350;
        T[3] = MyTask351;
        T[4] = MyTask352;
        resourceCapacity = 9;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++) {
            d[o] = T[o].allowedDelay();
        }
        outputArray = postYourConstraintForUpdatingUpperBounds(T, resourceCapacity, d);
        outputArrayForNCumulative = postYourConstraintForNCumulativeUpperbound(T, resourceCapacity);
        assertEquals(outputArray[0], 86);
        assertEquals(outputArray[1], 69);
        assertEquals(outputArray[2], 53);
        assertEquals(outputArray[3], 100);
        assertEquals(outputArray[4], 68);
        assertEquals(outputArrayForNCumulative[0], 86);
        assertEquals(outputArrayForNCumulative[1], 69);
        assertEquals(outputArrayForNCumulative[2], 53);
        assertEquals(outputArrayForNCumulative[3], 100);
        assertEquals(outputArrayForNCumulative[4], 68);
         
        //Example for extended-edge-finding which should detect {A, B} < I.
        /*
      Task MyTask102 = new Task (1, 5, 5, 2, 0, 2);//A
   Task MyTask103 = new Task (1, 5, 5, 2, 0, 3);//B
   Task MyTask104 = new Task (0, 10, 10, 4, 0, 1);//I
   T = new Task[3];
   T[0] = MyTask102;
   T[1] = MyTask103;
   T[2] = MyTask104;
   resourceCapacity = 3;
   numberOfTasks = T.length;
   d = new int[numberOfTasks];
   for (int o = 0; o < numberOfTasks; o++)
       d[o] = T[o].allowedDelay();
   outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
   outputArrayForNCumulative = postYourConstraintForNCumulative(T, resourceCapacity);
   assertEquals(1, outputArray[0]);
   assertEquals(1, outputArray[1]);
   assertEquals(0, outputArray[2]);
   assertEquals(1, outputArrayForNCumulative[0]);
   assertEquals(1, outputArrayForNCumulative[1]);
   assertEquals(0, outputArrayForNCumulative[2]);
         */
 /*
   //notfirstnotlastExample
       Task MyTask0 = new Task (4, 5, 5, 1, 0, 3);//A
        Task MyTask1 = new Task (2, 4, 4, 1, 0, 3);//B
        Task MyTask2 = new Task (2, 4, 4, 1, 0, 2);//C
        Task MyTask3 = new Task (1, 9, 9, 4, 0, 1);//D
        T = new Task[4];
        T[0] = MyTask0;
        T[1] = MyTask1;
        T[2] = MyTask2;
        T[3] = MyTask3;
        resourceCapacity = 3;
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        for (int o = 0; o < numberOfTasks; o++)
            d[o] = T[o].allowedDelay();
        outputArray = postYourConstraintForUpdatingLowerBounds(T, resourceCapacity, d, delayNumber);
        assertEquals(outputArray[0], 4);
        assertEquals(outputArray[1], 2);
        assertEquals(outputArray[2], 2);
        assertEquals(outputArray[3], 1);
         */
    }

    public int[] postYourConstraintForUpdatingLowerBounds(Task[] T, int C, int[] delays, int delayNumber) {

        Solver solver = new Solver();
        int[] resourceCapacity = new int[1];
        resourceCapacity[0] = C;
        int numberOfTasks = T.length;
        IntVar[] startingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestStartingTime(), T[i].latestStartingTime(), solver);
        }
        IntVar[] processingTimeVariables = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariables[i] = VariableFactory.fixed(T[i].processingTime(), solver);
        }
        IntVar[] endingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            //   endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].processingTime(), 1000, solver);
            endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].earliestStartingTime() + T[i].processingTime(), T[i].latestStartingTime() + T[i].processingTime(), solver);
        }
        IntVar[] heightVars = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            heightVars[i] = VariableFactory.fixed(T[i].height(), solver);
        }
        IntVar capacityVar = VariableFactory.fixed(resourceCapacity[0], solver);
        IntVar makespan = VariableFactory.bounded("objective", 0, 100000, solver);
        IntVar[] vars = new IntVar[4 * numberOfTasks + 2];
        for (int g = 0; g < numberOfTasks; g++) {
            vars[g] = startingTimes[g];
            vars[numberOfTasks + g] = processingTimeVariables[g];
            vars[2 * numberOfTasks + g] = endingTimes[g];
            vars[3 * numberOfTasks + g] = heightVars[g];
            vars[4 * numberOfTasks] = capacityVar;
            vars[4 * numberOfTasks + 1] = makespan;

        }
        solver.post(new edgeFindingConstraint(vars, T.length, 1, resourceCapacity, delays, false, true, false, activateImprovingDetectionLowerBound, false, 0, false, delayNumber));
        try {
            solver.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
            Assert.fail();
        }
        int[] A = new int[numberOfTasks];
        for (int a = 0; a < numberOfTasks; a++) {
            A[a] = vars[a].getLB();
        }
        return A;
    }

    public int[] postYourConstraintForUpdatingUpperBounds(Task[] T, int C, int[] delays) {

        Solver solver = new Solver();
        int[] resourceCapacity = new int[1];
        resourceCapacity[0] = C;
        int numberOfTasks = T.length;
        IntVar[] startingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestStartingTime(), T[i].latestStartingTime(), solver);
        }
        IntVar[] processingTimeVariables = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariables[i] = VariableFactory.fixed(T[i].processingTime(), solver);
        }
        IntVar[] endingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            // endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].processingTime(), 1000, solver);
            endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].earliestStartingTime() + T[i].processingTime(), T[i].latestStartingTime() + T[i].processingTime(), solver);
        }
        IntVar[] heightVars = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            heightVars[i] = VariableFactory.fixed(T[i].height(), solver);
        }
        IntVar capacityVar = VariableFactory.fixed(resourceCapacity[0], solver);
        IntVar makespan = VariableFactory.bounded("objective", 0, 100000, solver);
        IntVar[] vars = new IntVar[4 * numberOfTasks + 2];
        for (int g = 0; g < numberOfTasks; g++) {
            vars[g] = startingTimes[g];
            vars[numberOfTasks + g] = processingTimeVariables[g];
            vars[2 * numberOfTasks + g] = endingTimes[g];
            vars[3 * numberOfTasks + g] = heightVars[g];
            vars[4 * numberOfTasks] = capacityVar;
            vars[4 * numberOfTasks + 1] = makespan;

        }
        solver.post(new edgeFindingConstraint(vars, T.length, 1, resourceCapacity, delays, false, false, true, activateImprovingDetectionLowerBound, false, 0, false, 1));
        try {
            solver.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
            Assert.fail();
        }
        int[] A = new int[numberOfTasks];
        for (int a = 0; a < numberOfTasks; a++) {
            A[a] = vars[a].getUB() + T[a].processingTime();
        }
        return A;
    }

    public int[] postYourConstraintForNCumulative(Task[] T, int C) {

        int[] zeroDelays = new int[T.length];
        Solver solver = new Solver();
        int[] resourceCapacity = new int[1];
        resourceCapacity[0] = C;
        int numberOfTasks = T.length;
        IntVar[] startingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestStartingTime(), T[i].latestStartingTime(), solver);
        }
        IntVar[] processingTimeVariables = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariables[i] = VariableFactory.fixed(T[i].processingTime(), solver);
        }
        IntVar[] processingTimeVariablesForNCumulative = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariablesForNCumulative[i] = VariableFactory.fixed(T[i].processingTime() + T[i].allowedDelay(), solver);
        }
        IntVar[] endingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].processingTime(), 1000, solver);
        }
        IntVar[] endingTimesForNCumulative = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            endingTimesForNCumulative[i] = VariableFactory.bounded("endingTimes1[i]", T[i].processingTime() + T[i].allowedDelay(), 1000, solver);
        }
        IntVar[] heightVars = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            heightVars[i] = VariableFactory.fixed(T[i].height(), solver);
        }
        IntVar capacityVar = VariableFactory.fixed(resourceCapacity[0], solver);
        IntVar makespan = VariableFactory.bounded("objective", 0, 1000, solver);
        IntVar[] vars = new IntVar[4 * numberOfTasks + 2];

        for (int constraintCounter = 0; constraintCounter < numberOfTasks; constraintCounter++) {
            for (int g = 0; g < numberOfTasks; g++) {
                vars[g] = startingTimes[g];
                if (g == constraintCounter) {
                    vars[numberOfTasks + g] = processingTimeVariablesForNCumulative[g];
                } else {
                    vars[numberOfTasks + g] = processingTimeVariables[g];
                }
                if (g == constraintCounter) {
                    vars[2 * numberOfTasks + g] = endingTimesForNCumulative[g];
                } else {
                    vars[2 * numberOfTasks + g] = endingTimes[g];
                }
                vars[3 * numberOfTasks + g] = heightVars[g];
                vars[4 * numberOfTasks] = capacityVar;
                vars[4 * numberOfTasks + 1] = makespan;

            }

//            for (int y = 0; y < vars.length; y++)
//                System.out.println("y = " + y + " -> " + vars[y].getLB() + ", " + vars[y].getUB());
//            System.out.println("gh");
//
            solver.post(new edgeFindingConstraint(vars, T.length, 1, resourceCapacity, zeroDelays, false, true, false, activateImprovingDetectionLowerBound, true, constraintCounter, false, 1));

        }

        //  System.out.println("debug");
        try {
            solver.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
            Assert.fail();
        }

        //  System.out.println("new var domains");
        //  for (int y = 0; y < numberOfTasks; y++)
        //System.out.println("y = " + y + " -> " + vars[y].getLB() + ", " + vars[y].getUB());
        int[] A = new int[numberOfTasks];
        for (int a = 0; a < numberOfTasks; a++) {
            A[a] = vars[a].getLB();
        }
        return A;
    }

    public int[] postYourConstraintForNCumulativeUpperbound(Task[] T, int C) {
        int numberOfTasks = T.length;

        int[] zeroDelays = new int[T.length];
        int[] actualDelays = new int[T.length];
        for (int i = 0; i < numberOfTasks; i++) {
            actualDelays[i] = T[i].allowedDelay();
        }

        Solver solver = new Solver();
        int[] resourceCapacity = new int[1];
        resourceCapacity[0] = C;
        IntVar[] startingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestStartingTime(), T[i].latestStartingTime(), solver);
        }
        IntVar[] processingTimeVariables = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariables[i] = VariableFactory.fixed(T[i].processingTime(), solver);
        }
        IntVar[] processingTimeVariablesForNCumulative = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariablesForNCumulative[i] = VariableFactory.fixed(T[i].processingTime() + T[i].allowedDelay(), solver);
        }
        IntVar[] endingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].processingTime(), 1000, solver);
        }
        IntVar[] endingTimesForNCumulative = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            endingTimesForNCumulative[i] = VariableFactory.bounded("endingTimes1[i]", T[i].processingTime() + T[i].allowedDelay(), 1000, solver);
        }
        IntVar[] heightVars = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            heightVars[i] = VariableFactory.fixed(T[i].height(), solver);
        }
        IntVar capacityVar = VariableFactory.fixed(resourceCapacity[0], solver);
        IntVar makespan = VariableFactory.bounded("objective", 0, 1000, solver);
        IntVar[] vars = new IntVar[4 * numberOfTasks + 2];

        for (int constraintCounter = 0; constraintCounter < numberOfTasks; constraintCounter++) {
            for (int g = 0; g < numberOfTasks; g++) {
                vars[g] = startingTimes[g];
                if (g == constraintCounter) {
                    vars[numberOfTasks + g] = processingTimeVariablesForNCumulative[g];
                } else {
                    vars[numberOfTasks + g] = processingTimeVariables[g];
                }
                if (g == constraintCounter) {
                    vars[2 * numberOfTasks + g] = endingTimesForNCumulative[g];
                } else {
                    vars[2 * numberOfTasks + g] = endingTimes[g];
                }
                vars[3 * numberOfTasks + g] = heightVars[g];
                vars[4 * numberOfTasks] = capacityVar;
                vars[4 * numberOfTasks + 1] = makespan;

            }

//            for (int y = 0; y < vars.length; y++)
//                System.out.println("y = " + y + " -> " + vars[y].getLB() + ", " + vars[y].getUB());
//            System.out.println("gh");
//
            solver.post(new edgeFindingConstraint(vars, T.length, 1, resourceCapacity, zeroDelays, false, false, true, activateImprovingDetectionLowerBound, true, constraintCounter, false, 1));

        }

        try {
            solver.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
            Assert.fail();
        }
        int[] A = new int[numberOfTasks];
        for (int a = 0; a < numberOfTasks; a++) {
            A[a] = vars[a].getUB() + T[a].processingTime();
        }
        return A;
    }
}
