package cellularautomata.grid3d;

public class LongGrid3DIterator extends Grid3DIterator<LongGrid3D, Long> {

	public LongGrid3DIterator(LongGrid3D grid) {
		super(grid);
	}

	@Override
	protected Long getFromGridPosition(int x, int y, int z) throws Exception {
		return grid.getFromPosition(x, y, z);
	}

}
