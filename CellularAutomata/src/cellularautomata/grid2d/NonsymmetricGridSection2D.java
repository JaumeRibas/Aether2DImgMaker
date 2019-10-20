package cellularautomata.grid2d;

public abstract class NonsymmetricGridSection2D implements Grid2D {

	protected SymmetricGrid2D grid;
	
	public NonsymmetricGridSection2D(SymmetricGrid2D grid) {
		super();
		this.grid = grid;
	}

	@Override
	public int getMinX() {
		return grid.getNonsymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return grid.getNonsymmetricMaxX();
	}
	
	@Override
	public int getMinX(int y) {
		return grid.getNonsymmetricMinX(y);
	}

	@Override
	public int getMaxX(int y) {
		return grid.getNonsymmetricMaxX(y);
	}

	@Override
	public int getMinY() {
		return grid.getNonsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return grid.getNonsymmetricMaxY();
	}
	
	@Override
	public int getMinY(int x) {
		return grid.getNonsymmetricMinY(x);
	}

	@Override
	public int getMaxY(int x) {
		return grid.getNonsymmetricMaxY(x);
	}

}
