package bridge;

import inf.minife.fe.HollowCircleS;
import inf.minife.fe.Material;
import inf.minife.fe.Model;
import inf.minife.fe.Truss2D;
import inf.minife.fe.Truss3D;
import inf.minife.view2.Viewer2;

public class BridgeStructure
{
	private Model model;

	public BridgeStructure()
	{
		this.model = new Model();

		double eModulus = 210000e3; // kN/m^2
		double rho = 7.850e6; // kg/m^3
		Material material = model.createMaterial(1, eModulus, rho);

		HollowCircleS section = model.createSection(1, HollowCircleS.TYPE, Truss3D.TYPE);
		section.setDiameter(0.15);
		section.setWTK(0.002);

		double x = 0.0;
		double dx = 2.0;

		for (int i = 0; i < 6; i++)
		{
			this.model.createNode(i + 1, x, i % 2 + 1, i % 2);
			x += dx;
		}

		for (int i = 0; i < 5; i++)
		{
			model.createElement(i + 1, Truss3D.TYPE, material, section, model.getNode(i + 1), model.getNode(i + 2));
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
}