package args;

import args.ArgsException.ErrorCode;
import java.util.Iterator;
import java.util.NoSuchElementException;

class IntegerArgumentMarshaller implements ArgumentMarshaller {

  private int integerValue = 0;

  @Override
  public void set(Iterator<String> currentArgument) throws ArgsException {
    String parameter = null;
    try {
      parameter = currentArgument.next();
      integerValue = Integer.parseInt(parameter);
    } catch (NoSuchElementException e) {
      throw new ArgsException(ErrorCode.MISSING_INTEGER);
    } catch (NumberFormatException e) {
      throw new ArgsException(ErrorCode.INVALID_INTEGER, parameter);
    }
  }

  @Override
  public Object get() {
    return integerValue;
  }
}
