package cellularautomata.grid1d;

public class ObjectGrid1DIterator<T> extends Grid1DIterator<ObjectGrid1D<T>, T> {

	public ObjectGrid1DIterator(ObjectGrid1D<T> grid) {
		super(grid);
	}

	@Override
	protected T getFromGridPosition(int x) {
		return grid.getFromPosition(x);
	}

}
