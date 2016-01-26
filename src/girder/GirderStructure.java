package girder;

import inf.minife.fe.Constraint;
import inf.minife.fe.DOF;
import inf.minife.fe.Force;
import inf.minife.fe.HollowCircleS;
import inf.minife.fe.Material;
import inf.minife.fe.Model;
import inf.minife.fe.Truss2D;
import inf.minife.view2.Viewer2;

public class GirderStructure {

	public static void main(String[] args) {
		GirderStructure structure = new GirderStructure();
		Viewer2 viewer = new Viewer2(structure.getModel());
		viewer.setVisible(true);
	}

	private double height = 2.0; // m
	double length = 10.0; // m
	double eModulus = 210000e3; // kN/m^2
	double rho = 7.850e6; // kg/m^3
	private double diameter = 0.15; // m
	double wallThickness = 0.002; // m
	double forceY = -50; // kN

	Model model;
	private HollowCircleS sectionForDiagonalMembers;
	private HollowCircleS sectionForLowerMembers;
	private HollowCircleS sectionForUpperMembers;

	public GirderStructure() {
		model = new Model();

		// material
		Material material = model.createMaterial(1, eModulus, rho);

		// sections
		setSectionForUpperMembers((HollowCircleS) model.createSection(1,
				HollowCircleS.TYPE, Truss2D.TYPE));
		getSectionForUpperMembers().setDiameter(getDiameter());
		getSectionForUpperMembers().setWTK(wallThickness);

		setSectionForDiagonalMembers((HollowCircleS) model.createSection(2,
				HollowCircleS.TYPE, Truss2D.TYPE));
		getSectionForDiagonalMembers().setDiameter(getDiameter());
		getSectionForDiagonalMembers().setWTK(wallThickness);

		setSectionForLowerMembers((HollowCircleS) model.createSection(3,
				HollowCircleS.TYPE, Truss2D.TYPE));
		getSectionForLowerMembers().setDiameter(getDiameter());
		getSectionForLowerMembers().setWTK(wallThickness);

		// nodes
		double dx = length / 5;
		double x = 0;

		for (int i = 0; i < 6; i++) {
			model.createNode(i + 1, x, 0, 0);
			x += dx;
		}

		x = dx / 2.0;
		for (int i = 0; i < 5; i++) {
			model.createNode(i + 7, x, getHeight(), 0);
			x += dx;
		}

		// force
		Force force = new Force();
		force.setValue(DOF.T_Y, forceY);

		model.getNode(2).setForce(force);
		model.getNode(3).setForce(force);
		model.getNode(4).setForce(force);
		model.getNode(5).setForce(force);

		// constraints
		Constraint constraintLeft = new Constraint();
		constraintLeft.setFree(DOF.T_X, false);
		constraintLeft.setFree(DOF.T_Y, false);

		Constraint constraintRight = new Constraint();
		constraintRight.setFree(DOF.T_Y, false);

		model.getNode(1).setConstraint(constraintLeft);
		model.getNode(6).setConstraint(constraintRight);

		// elements
		for (int i = 0; i < 5; i++) {
			model.createElement(i + 1, Truss2D.TYPE, material,
					getSectionForLowerMembers(), model.getNode(i + 1),
					model.getNode(i + 2));
		}

		for (int i = 0; i < 4; i++) {
			model.createElement(2 * i + 6, Truss2D.TYPE, material,
					getSectionForDiagonalMembers(), model.getNode(i + 7),
					model.getNode(i + 2));
			model.createElement(2 * i + 7, Truss2D.TYPE, material,
					getSectionForDiagonalMembers(), model.getNode(i + 2),
					model.getNode(i + 8));
		}

		model.createElement(14, Truss2D.TYPE, material,
				getSectionForUpperMembers(), model.getNode(1), model.getNode(7));
		for (int i = 0; i < 4; i++) {
			model.createElement(i + 15, Truss2D.TYPE, material,
					getSectionForUpperMembers(), model.getNode(i + 7),
					model.getNode(i + 8));
		}
		model.createElement(19, Truss2D.TYPE, material,
				getSectionForUpperMembers(), model.getNode(11),
				model.getNode(6));
	}

	public Model getModel() {
		return model;
	}

	static public Model create() {
		GirderStructure gs = new GirderStructure();
		return gs.model;
	}

	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}

	public double getDiameter() {
		return diameter;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getHeight() {
		return height;
	}

	public void setSectionForDiagonalMembers(
			HollowCircleS sectionForDiagonalMembers) {
		this.sectionForDiagonalMembers = sectionForDiagonalMembers;
	}

	public HollowCircleS getSectionForDiagonalMembers() {
		return sectionForDiagonalMembers;
	}

	public void setSectionForLowerMembers(HollowCircleS sectionForLowerMembers) {
		this.sectionForLowerMembers = sectionForLowerMembers;
	}

	public HollowCircleS getSectionForLowerMembers() {
		return sectionForLowerMembers;
	}

	public void setSectionForUpperMembers(HollowCircleS sectionForUpperMembers) {
		this.sectionForUpperMembers = sectionForUpperMembers;
	}

	public HollowCircleS getSectionForUpperMembers() {
		return sectionForUpperMembers;
	}
}
