package cellularautomata.grid;

public class ShortGrid3DProjectedSurfaceMaxX extends ShortGrid2D {

	private ShortGrid3D source;
	private short backgroundValue;
	
	public ShortGrid3DProjectedSurfaceMaxX(ShortGrid3D source, short backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getMinZ();
	}

	@Override
	public int getMaxX() {
		return source.getMaxZ();
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
		int sourceX, sourceY, sourceZ, minX;
		sourceY = y;
		sourceZ = x;
		sourceX = source.getMaxX();
		minX = source.getMinX();
		short value = source.getValueAt(sourceX, sourceY, sourceZ);
		while (value == backgroundValue && sourceX > minX) {
			sourceX--;
			value = source.getValueAt(sourceX, sourceY, sourceZ);
		}
		return value;
	}
}
