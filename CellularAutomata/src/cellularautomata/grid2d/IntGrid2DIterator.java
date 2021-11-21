package cellularautomata.grid2d;

public class IntGrid2DIterator extends Grid2DIterator<IntGrid2D, Integer> {

	public IntGrid2DIterator(IntGrid2D grid) {
		super(grid);
	}

	@Override
	protected Integer getFromGridPosition(int x, int y) throws Exception {
		return grid.getFromPosition(x, y);
	}

}
