package cellularautomata.grid;

public class IntGrid3DXSection extends IntGrid2D {

	private IntGrid3D source;
	private int z;
	
	public IntGrid3DXSection(IntGrid3D source, int z) {
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
	public int getValueAt(int x, int y) {
		return source.getValueAt(x, y, z);
	}

}
