/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package cellularautomata.numbers;

import java.math.BigInteger;
import java.util.Random;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;

public class BigInt extends Number implements FieldElement<BigInt>, Comparable<BigInt> {
	
	private final BigInteger value;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3805431693787503905L;

	public static final BigInt ZERO = new BigInt(BigInteger.ZERO);
	public static final BigInt ONE = new BigInt(BigInteger.ONE);

	public BigInt(BigInteger val) {
		value = val;
	}
	
	public BigInt(byte[] val) {
		value = new BigInteger(val);
	}

	public BigInt(int signum, byte[] magnitude) {
		value = new BigInteger(signum, magnitude);
	}

	public BigInt(int bitLength, int certainty, Random rnd) {
		value = new BigInteger(bitLength, certainty, rnd);
	}

	public BigInt(int numBits, Random rnd) {
		value = new BigInteger(numBits, rnd);
	}

	public BigInt(String val) {
		value = new BigInteger(val);
	}

	public BigInt(String val, int radix) {
		value = new BigInteger(val, radix);
	}

	@Override
	public Field<BigInt> getField() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigInt multiply(int arg0) {
		return new BigInt(value.multiply(BigInteger.valueOf(arg0)));
	}

	@Override
	public BigInt reciprocal() throws MathArithmeticException {
		return new BigInt(BigInteger.ONE.divide(value));
	}

	@Override
	public int compareTo(BigInt o) {
		return value.compareTo(o.value);
	}

	@Override
	public BigInt add(BigInt arg0) throws NullArgumentException {
		return new BigInt(value.add(arg0.value));
	}

	@Override
	public BigInt divide(BigInt arg0) throws NullArgumentException, MathArithmeticException {
		return new BigInt(value.divide(arg0.value));
	}

	@Override
	public BigInt multiply(BigInt arg0) throws NullArgumentException {
		return new BigInt(value.multiply(arg0.value));
	}

	@Override
	public BigInt negate() {
		return new BigInt(value.negate());
	}

	@Override
	public BigInt subtract(BigInt arg0) throws NullArgumentException {
		return new BigInt(value.subtract(arg0.value));
	}

	public BigInt[] divideAndRemainder(BigInt arg0) {
		BigInteger[] result = value.divideAndRemainder(arg0.value);
		return new BigInt[] { new BigInt(result[0]), new BigInt(result[1]) };
	}

	public BigInt power(int arg0) {
		return new BigInt(value.pow(arg0));
	}

	public BigInt abs() {
		return new BigInt(value.abs());
	}

	public static BigInt valueOf(int arg0) {
		return new BigInt(BigInteger.valueOf(arg0));
	}

	public static BigInt valueOf(long arg0) {
		return new BigInt(BigInteger.valueOf(arg0));
	}

	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	@Override
	public float floatValue() {
		return value.floatValue();
	}

	@Override
	public int intValue() {
		return value.intValue();
	}

	@Override
	public long longValue() {
		return value.longValue();
	}
	
	public BigInteger bigIntegerValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	public String toString(int arg0) {
		return value.toString(arg0);
	}
	
	public boolean equals(BigInt arg0) {
		if (arg0 == null)
			return false;
		return value.equals(arg0.value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		return value.equals(((BigInt)obj).value);
	}

}
