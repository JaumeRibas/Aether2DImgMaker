package cellularautomata.grid1d;

public class NonsymmetricLongGridSection1D extends NonsymmetricGridSection1D implements LongGrid1D{
	
	public NonsymmetricLongGridSection1D(SymmetricLongGrid1D grid) {
		super(grid);
	}

	@Override
	public long getValueAtPosition(int x) {
		return ((SymmetricLongGrid1D) grid).getValueAtNonsymmetricPosition(x);
	}

}
