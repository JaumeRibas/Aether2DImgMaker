package cellularautomata.grid5d;

public class LongGrid5DIterator extends Grid5DIterator<LongGrid5D, Long> {

	public LongGrid5DIterator(LongGrid5D grid) {
		super(grid);
	}

	@Override
	protected Long getFromGridPosition(int v, int w, int x, int y, int z) throws Exception {
		return grid.getFromPosition(v, w, x, y, z);
	}

}
