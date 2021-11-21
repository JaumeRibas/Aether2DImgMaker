package cellularautomata.grid1d;

public class LongGrid1DIterator extends Grid1DIterator<LongGrid1D, Long> {

	public LongGrid1DIterator(LongGrid1D grid) {
		super(grid);
	}

	@Override
	protected Long getFromGridPosition(int x) {
		return grid.getFromPosition(x);
	}

}
