package cellularautomata.grid;

public class ShortGrid3DXSection extends ShortGrid2D {

	private ShortGrid3D source;
	private int z;
	
	public ShortGrid3DXSection(ShortGrid3D source, int z) {
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
	public short getValueAt(int x, int y) {
		return source.getValueAt(x, y, z);
	}

}
