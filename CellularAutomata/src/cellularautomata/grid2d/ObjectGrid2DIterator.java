package cellularautomata.grid2d;

public class ObjectGrid2DIterator<T> extends Grid2DIterator<ObjectGrid2D<T>, T> {

	public ObjectGrid2DIterator(ObjectGrid2D<T> grid) {
		super(grid);
	}

	@Override
	protected T getFromGridPosition(int x, int y) throws Exception {
		return grid.getFromPosition(x, y);
	}

}
