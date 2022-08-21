package args;

import args.ArgsException.ErrorCode;
import java.util.Iterator;
import java.util.NoSuchElementException;

class DoubleArgumentMarshaller implements ArgumentMarshaller {

  private double doubleValue = 0.0;

  @Override
  public void set(Iterator<String> currentArgument) throws ArgsException {
    String parameter = null;
    try {
      parameter = currentArgument.next();
      doubleValue = Double.parseDouble(parameter);
    } catch (NoSuchElementException e) {
      throw new ArgsException(ErrorCode.MISSING_DOUBLE);
    } catch (NumberFormatException e) {
      throw new ArgsException(ErrorCode.INVALID_DOUBLE, parameter);
    }
  }

  @Override
  public Object get() {
    return doubleValue;
  }
}
