package cellularautomata.grid4d;

public class LongGrid4DIterator extends Grid4DIterator<LongGrid4D, Long> {

	public LongGrid4DIterator(LongGrid4D grid) {
		super(grid);
	}

	@Override
	protected Long getFromGridPosition(int w, int x, int y, int z) throws Exception {
		return grid.getFromPosition(w, x, y, z);
	}

}
