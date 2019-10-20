package cellularautomata.grid4d;

public class NonsymmetricShortGridSection4D extends NonsymmetricGridSection4D implements ShortGrid4D {

	private SymmetricShortGrid4D grid;
	
	public NonsymmetricShortGridSection4D(SymmetricShortGrid4D grid) {
		super(grid);
	}

	@Override
	public short getValueAtPosition(int w, int x, int y, int z) throws Exception {
		return grid.getValueAtNonsymmetricPosition(w, x, y, z);
	}

}
