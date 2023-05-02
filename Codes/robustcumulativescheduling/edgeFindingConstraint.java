package robustcumulativescheduling;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;



@SuppressWarnings("serial")
public class edgeFindingConstraint extends Constraint {

	public edgeFindingConstraint(IntVar[] vars, int nbTasks, int nbResources, int[] capacities, int[] ka,  boolean filterMakespan, boolean filterLowerBound, boolean filterUpperBound, boolean activateImprovingDetectionLowerBound, boolean NCumulativeConstraintsPosted, int cumulativeConstraintCounter, boolean CAPProblemConsidered, int r) {

		super("robustness", new Propagator_edgeFindingConstraint(vars, nbTasks, nbResources,  capacities, ka,  filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, NCumulativeConstraintsPosted, cumulativeConstraintCounter, CAPProblemConsidered, r));
	}

}
