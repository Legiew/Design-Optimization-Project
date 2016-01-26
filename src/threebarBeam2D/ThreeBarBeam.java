package threebarBeam2D;

import inf.minife.fe.Beam2D;
import inf.minife.fe.CircleS;
import inf.minife.fe.Constraint;
import inf.minife.fe.DOF;
import inf.minife.fe.Force;
import inf.minife.fe.Model;
import inf.minife.view2.Viewer2;

/**
 * @author kl Create a three bar beam structure similar to the ANSYS problem...
 */
public class ThreeBarBeam {

	private Model model;

	public static void main(String[] args) {
		Model m = new ThreeBarBeam().getModel();

		m.printStructure();
		m.solve();
		m.printResults();

		Viewer2 viewer = new Viewer2(m);
		viewer.setVisible(true);
	}

	public ThreeBarBeam() {

		// model
		model = new Model();

		double E = 210000; // N/mm^2 (modulus of elasticity)
		double rho = 7.88E-6; // kg/mm^2 (density of steel)
		model.createMaterial(1, E, rho);

		for (int i = 1; i <= 3; i++) {
			CircleS c = model.createSection(i, CircleS.TYPE, Beam2D.TYPE);
			c.setDiameter(20); // mm
		}

		// nodes
		double b = 1000.0; // mm
		double h = 1000.0; // mm
		model.createNode(1, -b, 0, 0);
		model.createNode(2, 0, 0, 0);
		model.createNode(3, b, 0, 0);
		model.createNode(4, 0, -h, 0);

		// forces
		Force f = new Force();
		f.setValue(DOF.T_X, 30000.0); // N
		f.setValue(DOF.T_Y, -20000.0); // N
		model.getNode(4).setForce(f);

		// constraints
		Constraint constraint = new Constraint();
		constraint.setFree(DOF.T_X, false);
		constraint.setFree(DOF.T_Y, false);
		constraint.setFree(DOF.R_Z, false); // no rotation...
		model.getNode(1).setConstraint(constraint);
		model.getNode(2).setConstraint(constraint);
		model.getNode(3).setConstraint(constraint);

		// elements
		model.createElement(1, Beam2D.TYPE, model.getMaterial(1), model
				.getRealtable(1), model.getNode(1), model.getNode(4));
		model.createElement(2, Beam2D.TYPE, model.getMaterial(1), model
				.getRealtable(2), model.getNode(2), model.getNode(4));
		model.createElement(3, Beam2D.TYPE, model.getMaterial(1), model
				.getRealtable(3), model.getNode(3), model.getNode(4));
	}

	public Model getModel() {
		return model;
	}
}