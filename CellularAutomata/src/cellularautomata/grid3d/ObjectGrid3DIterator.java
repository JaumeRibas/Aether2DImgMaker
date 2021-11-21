package cellularautomata.grid3d;

public class ObjectGrid3DIterator<T> extends Grid3DIterator<ObjectGrid3D<T>, T> {

	public ObjectGrid3DIterator(ObjectGrid3D<T> grid) {
		super(grid);
	}

	@Override
	protected T getFromGridPosition(int x, int y, int z) throws Exception {
		return grid.getFromPosition(x, y, z);
	}

}
