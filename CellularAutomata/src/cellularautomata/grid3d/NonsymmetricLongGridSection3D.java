package cellularautomata.grid3d;

public class NonsymmetricLongGridSection3D extends NonsymmetricGridSection3D implements LongGrid3D {

	private SymmetricLongGrid3D grid;
	
	public NonsymmetricLongGridSection3D(SymmetricLongGrid3D grid) {
		super(grid);
	}

	@Override
	public long getValueAtPosition(int x, int y, int z) throws Exception {
		return grid.getValueAtNonsymmetricPosition(x, y, z);
	}

}
