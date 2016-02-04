package bridge;

import inf.minife.fe.Beam3D;
import inf.minife.fe.Constraint;
import inf.minife.fe.DOF;
import inf.minife.fe.Force;
import inf.minife.fe.HollowCircleS;
import inf.minife.fe.Material;
import inf.minife.fe.Model;
import inf.minife.fe.Node;
import inf.minife.view2.Viewer2;

public class BridgeStructure
{
	private Model model;

	private final int bottom = 0;
	private final int middle = 1000000;
	private final int top = 2000000;
	private final int front = 0;
	private final int center = 100000;
	private final int back = 200000;
	private final int crosswise = 10000;
	private final int angular = 20000;
	private final int topDown = 30000;

	private double length = 20.0;
	private int nodeCountLength = 17;
	private double width = 7.0;
	private double height = 4.0;

	private HollowCircleS sectionNormal;
	private HollowCircleS sectionAngular;

	public BridgeStructure()
	{
		this.model = new Model();

		this.model.getSettings().setAcceleration(DOF.T_Y, 9.81);

		double eModulus = 21000000.0; // kN/m^2
		double rho = 7850.0; // kg/m^3
		Material material = model.createMaterial(1, eModulus, rho);

		sectionNormal = model.createSection(1, HollowCircleS.TYPE, Beam3D.TYPE);
		sectionNormal.setDiameter(0.15);
		sectionNormal.setWTK(0.002);

		sectionAngular = model.createSection(2, HollowCircleS.TYPE, Beam3D.TYPE);
		sectionAngular.setDiameter(0.05);
		sectionAngular.setWTK(0.002);

		double nodeLengthDelta = length / (nodeCountLength - 1.0);

		createBottomGrid(nodeCountLength, width, bottom, front, back, crosswise, material, sectionNormal,
				nodeLengthDelta);

		createAngularStart(height, bottom, middle, top, front, angular, material, sectionNormal, nodeLengthDelta, 0);

		createAngularStart(height, bottom, middle, top, back, angular, material, sectionNormal, nodeLengthDelta, width);

		createAngularEnd(height, bottom, middle, top, front, angular, material, sectionNormal, nodeLengthDelta, 0);

		createAngularEnd(height, bottom, middle, top, back, angular, material, sectionNormal, nodeLengthDelta, width);

		createTopGrid(material, sectionNormal, nodeLengthDelta);

		createTopDown(material, sectionNormal, nodeLengthDelta);

		for (int i = 2; i < nodeCountLength / 2; i++)
		{
			model.createElement(angular + front + i + middle, Beam3D.TYPE, material, sectionAngular,
					model.getNode(bottom + front + i), model.getNode(middle + front + i + 1));

			model.createElement(angular + front + i + top, Beam3D.TYPE, material, sectionAngular,
					model.getNode(top + front + i), model.getNode(middle + front + i + 1));

			model.createElement(angular + back + i + middle, Beam3D.TYPE, material, sectionAngular,
					model.getNode(bottom + back + i), model.getNode(middle + back + i + 1));

			model.createElement(angular + back + i + top, Beam3D.TYPE, material, sectionAngular,
					model.getNode(top + back + i), model.getNode(middle + back + i + 1));
		}

		for (int i = nodeCountLength - 3; i > nodeCountLength / 2; i--)
		{
			model.createElement(angular + front + i + middle, Beam3D.TYPE, material, sectionAngular,
					model.getNode(bottom + front + i), model.getNode(middle + front + i - 1));

			model.createElement(angular + front + i + top, Beam3D.TYPE, material, sectionAngular,
					model.getNode(top + front + i), model.getNode(middle + front + i - 1));

			model.createElement(angular + back + i + middle, Beam3D.TYPE, material, sectionAngular,
					model.getNode(bottom + back + i), model.getNode(middle + back + i - 1));

			model.createElement(angular + back + i + top, Beam3D.TYPE, material, sectionAngular,
					model.getNode(top + back + i), model.getNode(middle + back + i - 1));
		}

		setConstraints();

		setForce();
	}

	private void createTopDown(Material material, HollowCircleS section, double nodeLengthDelta)
	{
		for (int i = 3; i < nodeCountLength - 3; i++)
		{
			this.model.createNode(middle + front + i, nodeLengthDelta * i, height / 2.0, 0);
			this.model.createNode(middle + back + i, nodeLengthDelta * i, height / 2.0, width);

			model.createElement(topDown + front + i + middle, Beam3D.TYPE, material, section,
					model.getNode(bottom + front + i), model.getNode(middle + front + i));

			model.createElement(topDown + front + i + top, Beam3D.TYPE, material, section,
					model.getNode(top + front + i), model.getNode(middle + front + i));

			model.createElement(topDown + back + i + middle, Beam3D.TYPE, material, section,
					model.getNode(bottom + back + i), model.getNode(middle + back + i));

			model.createElement(topDown + back + i + top, Beam3D.TYPE, material, section, model.getNode(top + back + i),
					model.getNode(middle + back + i));
		}
	}

	private void setForce()
	{
		Force force = new Force();
		force.setValue(DOF.T_Y, -1000.0 / 3.0);

		Node forceNode1 = model.getNode(bottom + center + (nodeCountLength / 2));
		forceNode1.setForce(force);

		Node forceNode2 = model.getNode(center + bottom + (nodeCountLength / 2) - 3);
		forceNode2.setForce(force);

		Node forceNode3 = model.getNode(center + bottom + (nodeCountLength / 2) + 3);
		forceNode3.setForce(force);
	}

	private void setConstraints()
	{
		Constraint constraint = new Constraint();
		constraint.setFree(DOF.R_X, false);
		constraint.setFree(DOF.R_Y, false);
		constraint.setFree(DOF.R_Z, false);
		constraint.setFree(DOF.T_X, false);
		constraint.setFree(DOF.T_Y, false);
		constraint.setFree(DOF.T_Z, false);

		this.model.getNode(bottom + front).setConstraint(constraint);
		this.model.getNode(bottom + back).setConstraint(constraint);
		this.model.getNode(bottom + front + nodeCountLength - 1).setConstraint(constraint);
		this.model.getNode(bottom + back + nodeCountLength - 1).setConstraint(constraint);
	}

	private void createTopGrid(Material material, HollowCircleS section, double nodeLengthDelta)
	{
		for (int i = 3; i < nodeCountLength - 3; i++)
		{
			this.model.createNode(top + front + i, nodeLengthDelta * i, height, 0);
			this.model.createNode(top + back + i, nodeLengthDelta * i, height, width);
		}

		for (int i = 2; i < nodeCountLength - 3; i++)
		{
			model.createElement(top + front + i, Beam3D.TYPE, material, section, model.getNode(top + front + i),
					model.getNode(top + front + i + 1));

			model.createElement(top + back + i, Beam3D.TYPE, material, section, model.getNode(top + back + i),
					model.getNode(top + back + i + 1));
		}

		// for (int i = 2; i < nodeCountLength - 2; i++)
		// {
		// model.createElement(top + crosswise + i, Beam3D.TYPE, material,
		// section, model.getNode(top + front + i),
		// model.getNode(top + back + i));
		// }
	}

	private void createAngularStart(double height, final int bottom, final int middle, final int top, final int zLayer,
			final int angular, Material material, HollowCircleS section, double nodeLengthDelta, double width)
	{
		this.model.createNode(middle + zLayer + 1, nodeLengthDelta, height / 2.0, width);

		this.model.createNode(top + zLayer + 2, nodeLengthDelta * 2.0, height, width);

		model.createElement(bottom + zLayer + angular, Beam3D.TYPE, material, section, model.getNode(bottom + zLayer),
				model.getNode(middle + zLayer + 1));

		model.createElement(bottom + zLayer + angular + 1, Beam3D.TYPE, material, section,
				model.getNode(middle + zLayer + 1), model.getNode(top + zLayer + 2));

		model.createElement(bottom + zLayer + angular + 2, Beam3D.TYPE, material, section,
				model.getNode(bottom + zLayer + 1), model.getNode(middle + zLayer + 1));

		model.createElement(bottom + zLayer + angular + 3, Beam3D.TYPE, material, section,
				model.getNode(top + zLayer + 2), model.getNode(bottom + zLayer + 2));

		model.createElement(bottom + zLayer + angular + 4, Beam3D.TYPE, material, section,
				model.getNode(middle + zLayer + 1), model.getNode(bottom + zLayer + 2));
	}

	private void createAngularEnd(double height, final int bottom, final int middle, final int top, final int zLayer,
			final int angular, Material material, HollowCircleS section, double nodeLengthDelta, double width)
	{
		int firstNode = middle + zLayer + this.nodeCountLength - 2;
		int secondNode = top + zLayer + this.nodeCountLength - 3;

		int endIndex = (this.nodeCountLength - 4) * 6;

		this.model.createNode(firstNode, nodeLengthDelta * (this.nodeCountLength - 2), height / 2.0, width);

		this.model.createNode(secondNode, nodeLengthDelta * (this.nodeCountLength - 3), height, width);

		model.createElement(bottom + zLayer + angular + endIndex, Beam3D.TYPE, material, section,
				model.getNode(bottom + zLayer + this.nodeCountLength - 1), model.getNode(firstNode));

		model.createElement(bottom + zLayer + angular + endIndex + 1, Beam3D.TYPE, material, section,
				model.getNode(firstNode), model.getNode(secondNode));

		model.createElement(bottom + zLayer + angular + endIndex + 2, Beam3D.TYPE, material, section,
				model.getNode(bottom + zLayer + this.nodeCountLength - 2), model.getNode(firstNode));

		model.createElement(bottom + zLayer + angular + endIndex + 3, Beam3D.TYPE, material, section,
				model.getNode(secondNode), model.getNode(bottom + zLayer + this.nodeCountLength - 3));

		model.createElement(bottom + zLayer + angular + endIndex + 4, Beam3D.TYPE, material, section,
				model.getNode(firstNode), model.getNode(bottom + zLayer + this.nodeCountLength - 3));
	}

	private void createBottomGrid(int nodeCountLength, double width, final int bottom, final int front, final int back,
			final int crosswise, Material material, HollowCircleS section, double nodeLengthDelta)
	{
		for (int i = 0; i < nodeCountLength; i++)
		{
			this.model.createNode(bottom + front + i, nodeLengthDelta * i, 0, 0);
			this.model.createNode(bottom + center + i, nodeLengthDelta * i, 0, width / 2.0);
			this.model.createNode(bottom + back + i, nodeLengthDelta * i, 0, width);
		}

		for (int i = 0; i < nodeCountLength - 1; i++)
		{
			model.createElement(bottom + front + i, Beam3D.TYPE, material, section, model.getNode(bottom + front + i),
					model.getNode(bottom + front + i + 1));

			model.createElement(bottom + back + i, Beam3D.TYPE, material, section, model.getNode(bottom + back + i),
					model.getNode(bottom + back + i + 1));
		}

		for (int i = 0; i < nodeCountLength; i++)
		{
			model.createElement(bottom + crosswise + i, Beam3D.TYPE, material, section,
					model.getNode(bottom + front + i), model.getNode(bottom + center + i));

			model.createElement(bottom + crosswise + center + i, Beam3D.TYPE, material, section,
					model.getNode(bottom + center + i), model.getNode(bottom + back + i));
		}
	}

	public static void main(String[] args)
	{
		BridgeStructure structure = new BridgeStructure();
		Viewer2 viewer = new Viewer2(structure.getModel());
		viewer.setVisible(true);
	}

	public Model getModel()
	{
		return model;
	}

	public HollowCircleS getNormalSection()
	{
		return this.sectionNormal;
	}

	public HollowCircleS getAngularSection()
	{
		return this.sectionAngular;
	}
}