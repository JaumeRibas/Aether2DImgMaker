package cellularautomata.grid;

public class IntGrid3DProjectedSurfaceMaxX extends IntGrid2D {

	private IntGrid3D source;
	private int backgroundValue;
	
	public IntGrid3DProjectedSurfaceMaxX(IntGrid3D source, int backgroundValue) {
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
	public int getValueAt(int x, int y) {
		int sourceX, sourceY, sourceZ, minX;
		sourceY = y;
		sourceZ = x;
		sourceX = source.getMaxX();
		minX = source.getMinX();
		int value = source.getValueAt(sourceX, sourceY, sourceZ);
		while (value == backgroundValue && sourceX > minX) {
			sourceX--;
			value = source.getValueAt(sourceX, sourceY, sourceZ);
		}
		return value;
	}
}
