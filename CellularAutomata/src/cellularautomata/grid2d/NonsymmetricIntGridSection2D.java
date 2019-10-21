package cellularautomata.grid2d;

public class NonsymmetricIntGridSection2D extends NonsymmetricGridSection2D implements IntGrid2D{
	
	public NonsymmetricIntGridSection2D(SymmetricIntGrid2D grid) {
		super(grid);
	}

	@Override
	public int getValueAtPosition(int x, int y) throws Exception {
		return ((SymmetricIntGrid2D) grid).getValueAtNonsymmetricPosition(x, y);
	}

}
