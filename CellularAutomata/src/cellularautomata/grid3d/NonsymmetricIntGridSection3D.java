package cellularautomata.grid3d;

public class NonsymmetricIntGridSection3D extends NonsymmetricGridSection3D implements IntGrid3D {
	
	public NonsymmetricIntGridSection3D(SymmetricIntGrid3D grid) {
		super(grid);
	}

	@Override
	public int getValueAtPosition(int x, int y, int z) throws Exception {
		return ((SymmetricIntGrid3D) grid).getValueAtNonsymmetricPosition(x, y, z);
	}

}
