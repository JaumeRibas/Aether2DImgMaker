package cellularautomata.grid1d;

public abstract class NonsymmetricGridSection1D implements Grid1D {

	protected SymmetricGrid1D grid;
	
	public NonsymmetricGridSection1D(SymmetricGrid1D grid) {
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

}
