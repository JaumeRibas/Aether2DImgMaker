package caimgmaker.args;

import java.math.BigInteger;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class GreaterThanZeroIntegerValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value)
			throws ParameterException {
		BigInteger n = new BigInteger(value);
		if (n.compareTo(BigInteger.ONE) < 0) {
			throw new ParameterException("The value of " + name + " must be greater than zero (found " + value +").");
		}
	}
	
}