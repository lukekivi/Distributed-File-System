/**
 * Autogenerated by Thrift Compiler (0.15.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package pa3;


/**
 * App status
 * -   SUCCESS: everything went ok
 * -     ERROR: something went wrong
 * - NOT_FOUND: the file was not found
 */
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.15.0)", date = "2022-04-11")
public enum Status implements org.apache.thrift.TEnum {
  SUCCESS(0),
  ERROR(1),
  NOT_FOUND(2);

  private final int value;

  private Status(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  @org.apache.thrift.annotation.Nullable
  public static Status findByValue(int value) { 
    switch (value) {
      case 0:
        return SUCCESS;
      case 1:
        return ERROR;
      case 2:
        return NOT_FOUND;
      default:
        return null;
    }
  }
}
