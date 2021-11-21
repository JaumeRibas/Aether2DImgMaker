package cellularautomata.grid4d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Grid4DIterator<G extends Grid4D, T> implements Iterator<T> {
	
	protected G grid;
	protected int w;
	protected int x;
	protected int y;
	protected int z;
	
	public Grid4DIterator(G grid) {
		this.grid = grid;
		this.w = grid.getMinW();
		this.x = grid.getMinXAtW(w);
		this.y = grid.getMinYAtWX(w, x);
		this.z = grid.getMinZ(w, x, y);
	}

	@Override
	public boolean hasNext() {
		return w <= grid.getMaxW();
	}

	@Override
	public T next() {
		if (w > grid.getMaxW())
			throw new NoSuchElementException();
		T next = null;
		try {
			next = getFromGridPosition(w, x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.getMessage());//what should be done here?
		}
		if (z < grid.getMaxZ(w, x, y)) {
			z++;
		} else {
			if (y < grid.getMaxYAtWX(w, x)) {
				y++;
				z = grid.getMinZ(w, x, y);
			} else {
				if (x < grid.getMaxXAtW(w)) {
					x++;
					y = grid.getMinYAtWX(w, x);
					z = grid.getMinZ(w, x, y);
				} else {
					w++;
					if (w <= grid.getMaxW()) {
						x = grid.getMinXAtW(w);
						y = grid.getMinYAtWX(w, x);
						z = grid.getMinZ(w, x, y);
					}
				}
			}
		}
		return next;
	}
	
	protected abstract T getFromGridPosition(int w, int x, int y, int z) throws Exception;

}
