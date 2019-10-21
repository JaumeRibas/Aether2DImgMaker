package cellularautomata.grid3d;

public class NonsymmetricShortGridSection3D extends NonsymmetricGridSection3D implements ShortGrid3D {
	
	public NonsymmetricShortGridSection3D(SymmetricShortGrid3D grid) {
		super(grid);
	}

	@Override
	public short getValueAtPosition(int x, int y, int z) throws Exception {
		return ((SymmetricShortGrid3D) grid).getValueAtNonsymmetricPosition(x, y, z);
	}

}
