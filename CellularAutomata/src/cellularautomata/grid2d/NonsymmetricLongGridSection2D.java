package cellularautomata.grid2d;

public class NonsymmetricLongGridSection2D extends NonsymmetricGridSection2D implements LongGrid2D{

	private SymmetricLongGrid2D grid;
	
	public NonsymmetricLongGridSection2D(SymmetricLongGrid2D grid) {
		super(grid);
	}

	@Override
	public long getValueAtPosition(int x, int y) throws Exception {
		return grid.getValueAtNonsymmetricPosition(x, y);
	}

}
