package cellularautomata.grid5d;

public class IntGrid5DIterator extends Grid5DIterator<IntGrid5D, Integer> {

	public IntGrid5DIterator(IntGrid5D grid) {
		super(grid);
	}

	@Override
	protected Integer getFromGridPosition(int v, int w, int x, int y, int z) throws Exception {
		return grid.getFromPosition(v, w, x, y, z);
	}

}
