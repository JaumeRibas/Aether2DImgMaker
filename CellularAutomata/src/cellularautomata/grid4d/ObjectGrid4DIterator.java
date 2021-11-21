package cellularautomata.grid4d;

public class ObjectGrid4DIterator<T> extends Grid4DIterator<ObjectGrid4D<T>, T> {

	public ObjectGrid4DIterator(ObjectGrid4D<T> grid) {
		super(grid);
	}

	@Override
	protected T getFromGridPosition(int w, int x, int y, int z) throws Exception {
		return grid.getFromPosition(w, x, y, z);
	}

}
