package cellularautomata.grid4d;

public abstract class NonsymmetricGridSection4D implements Grid4D {

	protected SymmetricGrid4D grid;
	
	public NonsymmetricGridSection4D(SymmetricGrid4D grid) {
		super();
		this.grid = grid;
	}
	
	@Override
	public int getMinW() {
		return grid.getNonsymmetricMinW();
	}

	@Override
	public int getMaxW() {
		return grid.getNonsymmetricMaxW();
	}
	
	@Override
	public int getMinW(int x, int y, int z) {
		return grid.getNonsymmetricMinW(x, y, z);
	}
	
	@Override
	public int getMaxW(int x, int y, int z) {
		return grid.getNonsymmetricMaxW(x, y, z);
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
	public int getMinY() {
		return grid.getNonsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return grid.getNonsymmetricMaxY();
	}
	
	@Override
	public int getMinZ() {
		return grid.getNonsymmetricMinZ();
	}

	@Override
	public int getMaxZ() {
		return grid.getNonsymmetricMaxZ();
	}

}
