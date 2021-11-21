package cellularautomata.grid3d;

public class IntGrid3DIterator extends Grid3DIterator<IntGrid3D, Integer> {

	public IntGrid3DIterator(IntGrid3D grid) {
		super(grid);
	}

	@Override
	protected Integer getFromGridPosition(int x, int y, int z) throws Exception {
		return grid.getFromPosition(x, y, z);
	}

}
