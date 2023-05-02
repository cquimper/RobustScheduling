Results:

Section 6.1
There are two csv files, entitled "OC_n_ScalabilityResults" and "EG_n_ScalabilityResults". They correspond to the experiments of scalability tests
with respect to n for OC-TT and EG-TT, respectively. The first value at each of the rows 2 through 13 correspond to the number of tasks n. The 
second value on the same row denotes the elapsed time in seconds.
There are two csv files, entitled "OC_r_ScalabilityResults" and "EG_r_ScalabilityResults". They correspond to the experiments of scalability tests
with respect to r for OC-TT and EG-TT, respectively. The first value at each of the rows 2 through 13 correspond to the number of delayed tasks r. The 
second value on the same row denotes the elapsed time in seconds.


Section 6.2
The csv file, entitled "CAPResults" includes the results of running crane assignment problem. A description of each columns follows:
Data ID: Enumerates 130 instances generated. For every instance we executed the experiment 3 times. Once we filter both the lower and upper 
bounds. Once we only fiter the lower bounds and again we only filter the upper bounds. 
NA: The number of activities. 
NC: The number of resources (which is always initialized to 2). 
min_p: The lower bound of the interval from which a processing time was selected. 
max_p: The upper bound of the interval from which a processing time was selected. 
Seed: Argument to the Random object generator created in Java.
FilterLB: Specifies whether the lower bound of the domain of variables were filtered.
FilterLB: Specifies whether the upper bound of the domain of variables were filtered.
TT(bt): Number of backtracks achieved by running TT.
OC&TT(bt): Number of backtracks achieved by running OC-TT.	
EF&&TT(bt): Number of backtracks achieved by running EF-TT.
TT(t): Elapsed time achieved by running TT.
OC&TT(t): Elapsed time achieved by running OC-TT.	
EF&&TT(t): Elapsed time achieved by running EF-TT.
TT(makespan): Makespan achieved by running TT.	
OC&TT(makespan): Makespan achieved by running OC-TT.		
EF&&TT(makespan): Makespan achieved by running EF-TT.	
TT case number: 1 if the instance was solved up to optimality by TT within the timeout. 0 otherwise.
OC&TT case number: 1 if the instance was solved up to optimality by OC-TT within the timeout. 0 otherwise.	
EF&&TT case number: 1 if the instance was solved up to optimality by EF-TT within the timeout. 0 otherwise.	

Section 6.4
The csv file, entitled "CAPResults_TP" includes the results of running crane assignment problem where temporal protection is applied. The description of the file is similar to Section 6.2.




Data:
Section 6.1
A sample text file data produced for this section named newFile50-n90 means that this is the 50th file of 90 randomly generated tasks.  
A description of the content of each text file follows:
Line 1: The first number is the number of tasks. The second value denotes the number of resources. 
Line 2: Capacity of the resource.
Line 3: The first number is the processing time, the second number is the delay value,  the third is the height. 
If n tasks are enerated that way, we have n-1 consequtive lines with the same description.

The experiment of scalability with respect to r was executed over one fixed file out of 50 generated files with 100 tasks.



Codes:
All of the Java codes are included in one folder. Note that the naming of each file specifies its performance.






