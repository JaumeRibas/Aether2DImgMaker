package cellularautomata.grid2d;

public class NonsymmetricLongGridSection2D extends NonsymmetricGridSection2D implements LongGrid2D{
	
	public NonsymmetricLongGridSection2D(SymmetricLongGrid2D grid) {
		super(grid);
	}

	@Override
	public long getValueAtPosition(int x, int y) throws Exception {
		return ((SymmetricLongGrid2D) grid).getValueAtNonsymmetricPosition(x, y);
	}

}
