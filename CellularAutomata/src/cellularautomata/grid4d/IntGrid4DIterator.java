package cellularautomata.grid4d;

public class IntGrid4DIterator extends Grid4DIterator<IntGrid4D, Integer> {

	public IntGrid4DIterator(IntGrid4D grid) {
		super(grid);
	}

	@Override
	protected Integer getFromGridPosition(int w, int x, int y, int z) throws Exception {
		return grid.getFromPosition(w, x, y, z);
	}

}
