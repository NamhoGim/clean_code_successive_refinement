package args;

import args.ArgsException.ErrorCode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Args {

  private final String schema;
  private final Map<Character, ArgumentMarshaller> marshaller = new HashMap<>();
  private final Set<Character> argsFound = new HashSet<>();
  private Iterator<String> currentArgument;
  private final List<String> argsList;

  public Args(String schema, String[] args) throws ArgsException {
    this.schema = schema;
    this.argsList = Arrays.asList(args);
    parse();
  }

  private void parse() throws ArgsException {
    parseSchema();
    parseArguments();
  }

  private boolean parseSchema() throws ArgsException {
    for (String element : schema.split(",")) {
      if (element.length() > 0) {
        parseSchemaElement(element.trim());
      }
    }
    return true;
  }

  private void parseSchemaElement(String element) throws ArgsException {
    char elementId = element.charAt(0);
    String elementTail = element.substring(1);
    validateSchemaElementId(elementId);
    if (elementTail.length() == 0) {
      marshaller.put(elementId, new BooleanArgumentMarshaller());
    } else if (elementTail.equals("*")) {
      marshaller.put(elementId, new StringArgumentMarshaller());
    } else if (elementTail.equals("#")) {
      marshaller.put(elementId, new IntegerArgumentMarshaller());
    } else if (elementTail.equals("##")) {
      marshaller.put(elementId, new DoubleArgumentMarshaller());
    } else {
      throw new ArgsException(ErrorCode.INVALID_FORMAT, elementId, elementTail);
    }
  }

  private void validateSchemaElementId(char elementId) throws ArgsException {
    if (!Character.isLetter(elementId)) {
      throw new ArgsException(ErrorCode.INVALID_ARGUMENT_NAME, elementId, null);
    }
  }

  private boolean parseArguments() throws ArgsException {
    for (currentArgument = argsList.iterator(); currentArgument.hasNext(); ) {
      String arg = currentArgument.next();
      parseArgument(arg);
    }
    return true;
  }

  private void parseArgument(String arg) throws ArgsException {
    if (arg.startsWith("-")) {
      parseElements(arg);
    }
  }

  private void parseElements(String arg) throws ArgsException {
    for (int i = 1; i < arg.length(); i++) {
      parseElement(arg.charAt(i));
    }
  }

  private void parseElement(char argChar) throws ArgsException {
    if (setArgument(argChar)) {
      argsFound.add(argChar);
    } else {
      throw new ArgsException(ErrorCode.UNEXPECTED_ARGUMENT, argChar, null);
    }
  }

  private boolean setArgument(char argChar) throws ArgsException {
    ArgumentMarshaller m = marshaller.get(argChar);
    if (m == null) {
      return false;
    }
    try {
      m.set(currentArgument);
      return true;
    } catch (ArgsException e) {
      e.setErrorArgumentId(argChar);
      throw e;
    }
  }

  public int cardinality() {
    return argsFound.size();
  }

  public String usage() {
    if (schema.length() > 0) {
      return "-[" + schema + "]";
    } else {
      return "";
    }
  }

  private boolean falseIfNull(Boolean b) {
    return b != null && b;
  }

  private int zeroIfNull(Integer i) {
    return i == null ? 0 : i;
  }

  private String blankIfNull(String s) {
    return s == null ? "" : s;
  }

  public String getString(char arg) {
    ArgumentMarshaller am = marshaller.get(arg);
    try {
      return am == null ? "" : (String) am.get();
    } catch (ClassCastException e) {
      return "";
    }
  }

  public int getInt(char arg) {
    ArgumentMarshaller am = marshaller.get(arg);
    try {
      return am == null ? 0 : (Integer) am.get();
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean getBoolean(char arg) {
    ArgumentMarshaller am = marshaller.get(arg);
    boolean b = false;
    try {
      b = am != null && (Boolean) am.get();
    } catch (ClassCastException e) {
      b = false;
    }
    return b;
  }

  public double getDouble(char arg) {
    ArgumentMarshaller am = marshaller.get(arg);
    try {
      return am == null ? 0.0 : (Double) am.get();
    } catch (Exception e) {
      return 0.0;
    }
  }

  public boolean has(char arg) {
    return argsFound.contains(arg);
  }
}