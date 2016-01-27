package planeExample;

import inf.minife.fe.Constraint;
import inf.minife.fe.DOF;
import inf.minife.fe.Force;
import inf.minife.fe.IsoPlane;
import inf.minife.fe.Material;
import inf.minife.fe.Model;
import inf.minife.fe.Node;
import inf.minife.fe.Realtable;
import inf.minife.view2.Viewer2;

/**
 * @author kl Create a three bar structure similar to the ANSYS problem...
 */

public class PlaneExample
{
	private Model model;

	public static void main(String[] args)
	{
		Model m = new PlaneExample().getModel();

		m.printStructure();
		m.solve();
		m.printResults();

		Viewer2 viewer = new Viewer2(m);
		viewer.setVisible(true);
	}

	public PlaneExample()
	{

		// model
		model = new Model();

		// material
		double E = 210000; // N/mm^2 (modulus of elasticity)
		double rho = 7.88E-6; // kg/mm^2 (density of steel)
		Material mat = model.createMaterial(1, E, rho);

		// set thickness
		Realtable rt = model.createRealtable(1, IsoPlane.TYPE);
		rt.setValue(IsoPlane.RT_T, 10);

		// nodes
		double b = 1000; // m
		Node n1 = model.createNode(1, 0, 0, 0);
		Node n2 = model.createNode(2, b, 0, 0);
		Node n3 = model.createNode(3, b, b, 0);
		Node n4 = model.createNode(4, 0, b, 0);

		// element
		model.createElement(1, IsoPlane.TYPE, mat, model.getRealtable(1), n1, n2, n3, n4);

		// forces
		Force f = new Force();
		f.setValue(DOF.T_X, 30000.0); // N
		f.setValue(DOF.T_Y, -20000.0); // N
		model.getNode(2).setForce(f);
		model.getNode(3).setForce(f);

		// constraints
		Constraint c = new Constraint();
		c.setFree(DOF.T_X, false);
		c.setFree(DOF.T_Y, false);
		c.setFree(DOF.T_Z, false);
		c.setFree(DOF.R_X, false);
		c.setFree(DOF.R_Y, false);
		c.setFree(DOF.R_Z, false);
		model.getNode(1).setConstraint(c);
		model.getNode(4).setConstraint(c);
	}

	public Model getModel()
	{
		return model;
	}
}