package cellularautomata.grid1d;

public class NonsymmetricLongGridSection1D extends NonsymmetricGridSection1D implements LongGrid1D{

	private SymmetricLongGrid1D grid;
	
	public NonsymmetricLongGridSection1D(SymmetricLongGrid1D grid) {
		super(grid);
	}

	@Override
	public long getValueAtPosition(int x) {
		return grid.getValueAtNonsymmetricPosition(x);
	}

}
