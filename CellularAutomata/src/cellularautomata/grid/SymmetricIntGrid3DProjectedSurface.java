package cellularautomata.grid;

public class SymmetricIntGrid3DProjectedSurface extends IntGrid2D {

	private SymmetricIntGrid3D source;
	private int backgroundValue;
	
	public SymmetricIntGrid3DProjectedSurface(SymmetricIntGrid3D source, int backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getNonSymmetricMinZ();
	}

	@Override
	public int getMaxX() {
		return source.getNonSymmetricMaxZ();
	}

	@Override
	public int getMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getNonSymmetricMaxY();
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
