package cellularautomata.grid2d;

public class NonsymmetricShortGridSection2D extends NonsymmetricGridSection2D implements ShortGrid2D{

	private SymmetricShortGrid2D grid;
	
	public NonsymmetricShortGridSection2D(SymmetricShortGrid2D grid) {
		super(grid);
	}

	@Override
	public short getValueAtPosition(int x, int y) throws Exception {
		return grid.getValueAtNonsymmetricPosition(x, y);
	}

}
