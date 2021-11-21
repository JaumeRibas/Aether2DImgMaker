package cellularautomata.grid2d;

public class LongGrid2DIterator extends Grid2DIterator<LongGrid2D, Long> {

	public LongGrid2DIterator(LongGrid2D grid) {
		super(grid);
	}

	@Override
	protected Long getFromGridPosition(int x, int y) throws Exception {
		return grid.getFromPosition(x, y);
	}

}
