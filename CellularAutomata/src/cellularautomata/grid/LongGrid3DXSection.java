package cellularautomata.grid;

public class LongGrid3DXSection extends LongGrid2D {

	private LongGrid3D source;
	private int z;
	
	public LongGrid3DXSection(LongGrid3D source, int z) {
		this.source = source;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinX();
	}

	@Override
	public int getMaxX() {
		return source.getMaxX();
	}

	@Override
	public int getMinY() {
		return source.getMinY();
	}

	@Override
	public int getMaxY() {
		return source.getMaxY();
	}

	@Override
	public long getValueAt(int x, int y) {
		return source.getValueAt(x, y, z);
	}

}
