package cellularautomata.grid2d;

public class NonsymmetricIntGridSection2D extends NonsymmetricGridSection2D implements IntGrid2D{

	private SymmetricIntGrid2D grid;
	
	public NonsymmetricIntGridSection2D(SymmetricIntGrid2D grid) {
		super(grid);
	}

	@Override
	public int getValueAtPosition(int x, int y) throws Exception {
		return grid.getValueAtNonsymmetricPosition(x, y);
	}

}
