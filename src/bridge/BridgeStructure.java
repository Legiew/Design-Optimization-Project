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
	public static void main(String[] args)
	{
		final BridgeStructure structure = new BridgeStructure();
		final Viewer2 viewer = new Viewer2(structure.getModel());
		viewer.setVisible(true);
	}

	private final Model model;
	private final int bottom = 0;
	private final int middle = 1000000;
	private final int top = 2000000;
	private final int front = 0;
	private final int center = 100000;
	private final int back = 200000;
	private final int crosswise = 10000;
	private final int angular = 20000;
	private final int topDown = 30000;

	private final double length = 20.0;
	private final int nodeCountLength = 13;
	private final double width = 7.0;
	private final double height = 4.0;

	private final double nodeLengthDelta;

	private final Material material;
	private final HollowCircleS sectionNormal;
	private final HollowCircleS sectionAngular;

	public BridgeStructure()
	{
		this.model = new Model();

		this.model.getSettings().setAcceleration(DOF.T_Y, 9.81);

		final double eModulus = 2.15e8; // kN/m^2
		final double rho = 7865.0; // kg/m^3

		this.material = this.model.createMaterial(1, eModulus, rho);
		this.material.setNu(0.3);

		this.sectionNormal = this.model.createSection(1, HollowCircleS.TYPE, Beam3D.TYPE);
		this.sectionNormal.setDiameter(0.15);
		this.sectionNormal.setWTK(0.005);

		this.sectionAngular = this.model.createSection(2, HollowCircleS.TYPE, Beam3D.TYPE);
		this.sectionAngular.setDiameter(0.05);
		this.sectionAngular.setWTK(0.005);

		this.nodeLengthDelta = this.length / (this.nodeCountLength - 1.0);

		this.createBottomGrid();

		this.createAngularStart(this.front, 0.0);

		this.createAngularStart(this.back, this.width);

		this.createAngularEnd(this.front, 0.0);

		this.createAngularEnd(this.back, this.width);

		this.createTopGrid();

		this.createTopDown();

		this.createAngularBeams();

		this.setConstraints();

		this.setForce();
	}

	private void createAngularBeams()
	{
		for (int i = 2; i < this.nodeCountLength / 2; i++)
		{
			this.model.createElement(this.angular + this.front + i + this.middle, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.bottom + this.front + i),
					this.model.getNode(this.middle + this.front + i + 1));

			this.model.createElement(this.angular + this.front + i + this.top, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.top + this.front + i),
					this.model.getNode(this.middle + this.front + i + 1));

			this.model.createElement(this.angular + this.back + i + this.middle, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.bottom + this.back + i),
					this.model.getNode(this.middle + this.back + i + 1));

			this.model.createElement(this.angular + this.back + i + this.top, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.top + this.back + i),
					this.model.getNode(this.middle + this.back + i + 1));
		}

		for (int i = this.nodeCountLength - 3; i > this.nodeCountLength / 2; i--)
		{
			this.model.createElement(this.angular + this.front + i + this.middle, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.bottom + this.front + i),
					this.model.getNode(this.middle + this.front + i - 1));

			this.model.createElement(this.angular + this.front + i + this.top, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.top + this.front + i),
					this.model.getNode(this.middle + this.front + i - 1));

			this.model.createElement(this.angular + this.back + i + this.middle, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.bottom + this.back + i),
					this.model.getNode(this.middle + this.back + i - 1));

			this.model.createElement(this.angular + this.back + i + this.top, Beam3D.TYPE, this.material,
					this.sectionAngular, this.model.getNode(this.top + this.back + i),
					this.model.getNode(this.middle + this.back + i - 1));
		}
	}

	private void createAngularEnd(final int zLayer, double widthPosition)
	{
		final int firstNode = this.middle + zLayer + this.nodeCountLength - 2;
		final int secondNode = this.top + zLayer + this.nodeCountLength - 3;

		final int endIndex = (this.nodeCountLength - 4) * 6;

		this.model.createNode(firstNode, this.nodeLengthDelta * (this.nodeCountLength - 2), this.height / 2.0,
				widthPosition);

		this.model.createNode(secondNode, this.nodeLengthDelta * (this.nodeCountLength - 3), this.height,
				widthPosition);

		this.model.createElement(this.bottom + zLayer + this.angular + endIndex, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(this.bottom + zLayer + this.nodeCountLength - 1),
				this.model.getNode(firstNode));

		this.model.createElement(this.bottom + zLayer + this.angular + endIndex + 1, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(firstNode), this.model.getNode(secondNode));

		this.model.createElement(this.bottom + zLayer + this.angular + endIndex + 2, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(this.bottom + zLayer + this.nodeCountLength - 2),
				this.model.getNode(firstNode));

		this.model.createElement(this.bottom + zLayer + this.angular + endIndex + 3, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(secondNode),
				this.model.getNode(this.bottom + zLayer + this.nodeCountLength - 3));

		this.model.createElement(this.bottom + zLayer + this.angular + endIndex + 4, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(firstNode),
				this.model.getNode(this.bottom + zLayer + this.nodeCountLength - 3));
	}

	private void createAngularStart(final int zLayer, double widthPosition)
	{
		this.model.createNode(this.middle + zLayer + 1, this.nodeLengthDelta, this.height / 2.0, widthPosition);

		this.model.createNode(this.top + zLayer + 2, this.nodeLengthDelta * 2.0, this.height, widthPosition);

		this.model.createElement(this.bottom + zLayer + this.angular, Beam3D.TYPE, this.material, this.sectionNormal,
				this.model.getNode(this.bottom + zLayer), this.model.getNode(this.middle + zLayer + 1));

		this.model.createElement(this.bottom + zLayer + this.angular + 1, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(this.middle + zLayer + 1),
				this.model.getNode(this.top + zLayer + 2));

		this.model.createElement(this.bottom + zLayer + this.angular + 2, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(this.bottom + zLayer + 1),
				this.model.getNode(this.middle + zLayer + 1));

		this.model.createElement(this.bottom + zLayer + this.angular + 3, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(this.top + zLayer + 2),
				this.model.getNode(this.bottom + zLayer + 2));

		this.model.createElement(this.bottom + zLayer + this.angular + 4, Beam3D.TYPE, this.material,
				this.sectionNormal, this.model.getNode(this.middle + zLayer + 1),
				this.model.getNode(this.bottom + zLayer + 2));
	}

	private void createBottomGrid()
	{
		for (int i = 0; i < this.nodeCountLength; i++)
		{
			this.model.createNode(this.bottom + this.front + i, this.nodeLengthDelta * i, 0, 0);
			this.model.createNode(this.bottom + this.center + i, this.nodeLengthDelta * i, 0, this.width / 2.0);
			this.model.createNode(this.bottom + this.back + i, this.nodeLengthDelta * i, 0, this.width);
		}

		for (int i = 0; i < this.nodeCountLength - 1; i++)
		{
			this.model.createElement(this.bottom + this.front + i, Beam3D.TYPE, this.material, this.sectionNormal,
					this.model.getNode(this.bottom + this.front + i),
					this.model.getNode(this.bottom + this.front + i + 1));

			this.model.createElement(this.bottom + this.back + i, Beam3D.TYPE, this.material, this.sectionNormal,
					this.model.getNode(this.bottom + this.back + i),
					this.model.getNode(this.bottom + this.back + i + 1));
		}

		for (int i = 0; i < this.nodeCountLength; i++)
		{
			this.model.createElement(this.bottom + this.crosswise + i, Beam3D.TYPE, this.material, this.sectionNormal,
					this.model.getNode(this.bottom + this.front + i),
					this.model.getNode(this.bottom + this.center + i));

			this.model.createElement(this.bottom + this.crosswise + this.center + i, Beam3D.TYPE, this.material,
					this.sectionNormal, this.model.getNode(this.bottom + this.center + i),
					this.model.getNode(this.bottom + this.back + i));
		}
	}

	private void createTopDown()
	{
		for (int i = 3; i < this.nodeCountLength - 3; i++)
		{
			this.model.createNode(this.middle + this.front + i, this.nodeLengthDelta * i, this.height / 2.0, 0);
			this.model.createNode(this.middle + this.back + i, this.nodeLengthDelta * i, this.height / 2.0, this.width);

			this.model.createElement(this.topDown + this.front + i + this.middle, Beam3D.TYPE, this.material,
					this.sectionNormal, this.model.getNode(this.bottom + this.front + i),
					this.model.getNode(this.middle + this.front + i));

			this.model.createElement(this.topDown + this.front + i + this.top, Beam3D.TYPE, this.material,
					this.sectionNormal, this.model.getNode(this.top + this.front + i),
					this.model.getNode(this.middle + this.front + i));

			this.model.createElement(this.topDown + this.back + i + this.middle, Beam3D.TYPE, this.material,
					this.sectionNormal, this.model.getNode(this.bottom + this.back + i),
					this.model.getNode(this.middle + this.back + i));

			this.model.createElement(this.topDown + this.back + i + this.top, Beam3D.TYPE, this.material,
					this.sectionNormal, this.model.getNode(this.top + this.back + i),
					this.model.getNode(this.middle + this.back + i));
		}
	}

	private void createTopGrid()
	{
		for (int i = 3; i < this.nodeCountLength - 3; i++)
		{
			this.model.createNode(this.top + this.front + i, this.nodeLengthDelta * i, this.height, 0);
			this.model.createNode(this.top + this.back + i, this.nodeLengthDelta * i, this.height, this.width);
		}

		for (int i = 2; i < this.nodeCountLength - 3; i++)
		{
			this.model.createElement(this.top + this.front + i, Beam3D.TYPE, this.material, this.sectionNormal,
					this.model.getNode(this.top + this.front + i), this.model.getNode(this.top + this.front + i + 1));

			this.model.createElement(this.top + this.back + i, Beam3D.TYPE, this.material, this.sectionNormal,
					this.model.getNode(this.top + this.back + i), this.model.getNode(this.top + this.back + i + 1));
		}
	}

	public HollowCircleS getAngularSection()
	{
		return this.sectionAngular;
	}

	public Model getModel()
	{
		return this.model;
	}

	public HollowCircleS getNormalSection()
	{
		return this.sectionNormal;
	}

	private void setConstraints()
	{
		final Constraint constraint = new Constraint();
		constraint.setFree(DOF.R_X, false);
		constraint.setFree(DOF.R_Y, false);
		constraint.setFree(DOF.R_Z, false);
		constraint.setFree(DOF.T_X, false);
		constraint.setFree(DOF.T_Y, false);
		constraint.setFree(DOF.T_Z, false);

		this.model.getNode(this.bottom + this.front).setConstraint(constraint);
		this.model.getNode(this.bottom + this.center).setConstraint(constraint);
		this.model.getNode(this.bottom + this.back).setConstraint(constraint);

		this.model.getNode(this.bottom + this.front + this.nodeCountLength - 1).setConstraint(constraint);
		this.model.getNode(this.bottom + this.center + this.nodeCountLength - 1).setConstraint(constraint);
		this.model.getNode(this.bottom + this.back + this.nodeCountLength - 1).setConstraint(constraint);
	}

	private void setForce()
	{
		final Force force = new Force();
		force.setValue(DOF.T_Y, -4412.0 / 3.0);

		final Node forceNode1 = this.model.getNode(this.bottom + this.center + (this.nodeCountLength / 2));
		forceNode1.setForce(force);

		final Node forceNode2 = this.model.getNode(this.center + this.bottom + (this.nodeCountLength / 2) - 3);
		forceNode2.setForce(force);

		final Node forceNode3 = this.model.getNode(this.center + this.bottom + (this.nodeCountLength / 2) + 3);
		forceNode3.setForce(force);
	}
}