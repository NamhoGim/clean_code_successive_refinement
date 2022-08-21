package args;

import java.util.Iterator;

public class BooleanArgumentMarshaller implements ArgumentMarshaller {

  private boolean booleanValue = false;

  @Override
  public void set(Iterator<String> currentArgument) throws ArgsException {
    booleanValue = true;
  }

  @Override
  public Object get() {
    return booleanValue;
  }
}
