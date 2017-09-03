package cellularautomata.grid;

public class SymmetricShortGrid3DXSection extends SymmetricShortGrid2D {

	private SymmetricShortGrid3D source;
	private int z;
	
	public SymmetricShortGrid3DXSection(SymmetricShortGrid3D source, int z) {
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

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxY();
	}

	@Override
	public short getNonSymmetricValueAt(int x, int y) {
		return source.getNonSymmetricValueAt(x, y, z);
	}

}
