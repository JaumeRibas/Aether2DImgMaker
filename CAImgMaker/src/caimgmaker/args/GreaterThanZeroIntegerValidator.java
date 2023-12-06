package caimgmaker.args;

import java.math.BigInteger;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import caimgmaker.AetherImgMaker;

public class GreaterThanZeroIntegerValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value)
			throws ParameterException {
		BigInteger n = new BigInteger(value);
		if (n.compareTo(BigInteger.ONE) < 0) {
			throw new ParameterException(String.format(AetherImgMaker.messages.getString("param-not-greater-than-zero-format"), name, value));
		}
	}
	
}