/**
 * Autogenerated by Thrift Compiler (0.15.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package pa3;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
/**
 * Response with structure of entire distributed file system
 */
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.15.0)", date = "2022-04-16")
public class StructResponse implements org.apache.thrift.TBase<StructResponse, StructResponse._Fields>, java.io.Serializable, Cloneable, Comparable<StructResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("StructResponse");

  private static final org.apache.thrift.protocol.TField FOLDERS_FIELD_DESC = new org.apache.thrift.protocol.TField("folders", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField MSG_FIELD_DESC = new org.apache.thrift.protocol.TField("msg", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new StructResponseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new StructResponseTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.util.List<Folder> folders; // required
  /**
   * 
   * @see Status
   */
  public @org.apache.thrift.annotation.Nullable Status status; // required
  public @org.apache.thrift.annotation.Nullable java.lang.String msg; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    FOLDERS((short)1, "folders"),
    /**
     * 
     * @see Status
     */
    STATUS((short)2, "status"),
    MSG((short)3, "msg");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // FOLDERS
          return FOLDERS;
        case 2: // STATUS
          return STATUS;
        case 3: // MSG
          return MSG;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.FOLDERS, new org.apache.thrift.meta_data.FieldMetaData("folders", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Folder.class))));
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, Status.class)));
    tmpMap.put(_Fields.MSG, new org.apache.thrift.meta_data.FieldMetaData("msg", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(StructResponse.class, metaDataMap);
  }

  public StructResponse() {
  }

  public StructResponse(
    java.util.List<Folder> folders,
    Status status,
    java.lang.String msg)
  {
    this();
    this.folders = folders;
    this.status = status;
    this.msg = msg;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public StructResponse(StructResponse other) {
    if (other.isSetFolders()) {
      java.util.List<Folder> __this__folders = new java.util.ArrayList<Folder>(other.folders.size());
      for (Folder other_element : other.folders) {
        __this__folders.add(new Folder(other_element));
      }
      this.folders = __this__folders;
    }
    if (other.isSetStatus()) {
      this.status = other.status;
    }
    if (other.isSetMsg()) {
      this.msg = other.msg;
    }
  }

  public StructResponse deepCopy() {
    return new StructResponse(this);
  }

  @Override
  public void clear() {
    this.folders = null;
    this.status = null;
    this.msg = null;
  }

  public int getFoldersSize() {
    return (this.folders == null) ? 0 : this.folders.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<Folder> getFoldersIterator() {
    return (this.folders == null) ? null : this.folders.iterator();
  }

  public void addToFolders(Folder elem) {
    if (this.folders == null) {
      this.folders = new java.util.ArrayList<Folder>();
    }
    this.folders.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<Folder> getFolders() {
    return this.folders;
  }

  public StructResponse setFolders(@org.apache.thrift.annotation.Nullable java.util.List<Folder> folders) {
    this.folders = folders;
    return this;
  }

  public void unsetFolders() {
    this.folders = null;
  }

  /** Returns true if field folders is set (has been assigned a value) and false otherwise */
  public boolean isSetFolders() {
    return this.folders != null;
  }

  public void setFoldersIsSet(boolean value) {
    if (!value) {
      this.folders = null;
    }
  }

  /**
   * 
   * @see Status
   */
  @org.apache.thrift.annotation.Nullable
  public Status getStatus() {
    return this.status;
  }

  /**
   * 
   * @see Status
   */
  public StructResponse setStatus(@org.apache.thrift.annotation.Nullable Status status) {
    this.status = status;
    return this;
  }

  public void unsetStatus() {
    this.status = null;
  }

  /** Returns true if field status is set (has been assigned a value) and false otherwise */
  public boolean isSetStatus() {
    return this.status != null;
  }

  public void setStatusIsSet(boolean value) {
    if (!value) {
      this.status = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getMsg() {
    return this.msg;
  }

  public StructResponse setMsg(@org.apache.thrift.annotation.Nullable java.lang.String msg) {
    this.msg = msg;
    return this;
  }

  public void unsetMsg() {
    this.msg = null;
  }

  /** Returns true if field msg is set (has been assigned a value) and false otherwise */
  public boolean isSetMsg() {
    return this.msg != null;
  }

  public void setMsgIsSet(boolean value) {
    if (!value) {
      this.msg = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case FOLDERS:
      if (value == null) {
        unsetFolders();
      } else {
        setFolders((java.util.List<Folder>)value);
      }
      break;

    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((Status)value);
      }
      break;

    case MSG:
      if (value == null) {
        unsetMsg();
      } else {
        setMsg((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case FOLDERS:
      return getFolders();

    case STATUS:
      return getStatus();

    case MSG:
      return getMsg();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case FOLDERS:
      return isSetFolders();
    case STATUS:
      return isSetStatus();
    case MSG:
      return isSetMsg();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof StructResponse)
      return this.equals((StructResponse)that);
    return false;
  }

  public boolean equals(StructResponse that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_folders = true && this.isSetFolders();
    boolean that_present_folders = true && that.isSetFolders();
    if (this_present_folders || that_present_folders) {
      if (!(this_present_folders && that_present_folders))
        return false;
      if (!this.folders.equals(that.folders))
        return false;
    }

    boolean this_present_status = true && this.isSetStatus();
    boolean that_present_status = true && that.isSetStatus();
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (!this.status.equals(that.status))
        return false;
    }

    boolean this_present_msg = true && this.isSetMsg();
    boolean that_present_msg = true && that.isSetMsg();
    if (this_present_msg || that_present_msg) {
      if (!(this_present_msg && that_present_msg))
        return false;
      if (!this.msg.equals(that.msg))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetFolders()) ? 131071 : 524287);
    if (isSetFolders())
      hashCode = hashCode * 8191 + folders.hashCode();

    hashCode = hashCode * 8191 + ((isSetStatus()) ? 131071 : 524287);
    if (isSetStatus())
      hashCode = hashCode * 8191 + status.getValue();

    hashCode = hashCode * 8191 + ((isSetMsg()) ? 131071 : 524287);
    if (isSetMsg())
      hashCode = hashCode * 8191 + msg.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(StructResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetFolders(), other.isSetFolders());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFolders()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.folders, other.folders);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetStatus(), other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.status, other.status);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetMsg(), other.isSetMsg());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMsg()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.msg, other.msg);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("StructResponse(");
    boolean first = true;

    sb.append("folders:");
    if (this.folders == null) {
      sb.append("null");
    } else {
      sb.append(this.folders);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("status:");
    if (this.status == null) {
      sb.append("null");
    } else {
      sb.append(this.status);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("msg:");
    if (this.msg == null) {
      sb.append("null");
    } else {
      sb.append(this.msg);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class StructResponseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public StructResponseStandardScheme getScheme() {
      return new StructResponseStandardScheme();
    }
  }

  private static class StructResponseStandardScheme extends org.apache.thrift.scheme.StandardScheme<StructResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, StructResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // FOLDERS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.folders = new java.util.ArrayList<Folder>(_list8.size);
                @org.apache.thrift.annotation.Nullable Folder _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = new Folder();
                  _elem9.read(iprot);
                  struct.folders.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setFoldersIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.status = pa3.Status.findByValue(iprot.readI32());
              struct.setStatusIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // MSG
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.msg = iprot.readString();
              struct.setMsgIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, StructResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.folders != null) {
        oprot.writeFieldBegin(FOLDERS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.folders.size()));
          for (Folder _iter11 : struct.folders)
          {
            _iter11.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.status != null) {
        oprot.writeFieldBegin(STATUS_FIELD_DESC);
        oprot.writeI32(struct.status.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.msg != null) {
        oprot.writeFieldBegin(MSG_FIELD_DESC);
        oprot.writeString(struct.msg);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class StructResponseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public StructResponseTupleScheme getScheme() {
      return new StructResponseTupleScheme();
    }
  }

  private static class StructResponseTupleScheme extends org.apache.thrift.scheme.TupleScheme<StructResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, StructResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetFolders()) {
        optionals.set(0);
      }
      if (struct.isSetStatus()) {
        optionals.set(1);
      }
      if (struct.isSetMsg()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetFolders()) {
        {
          oprot.writeI32(struct.folders.size());
          for (Folder _iter12 : struct.folders)
          {
            _iter12.write(oprot);
          }
        }
      }
      if (struct.isSetStatus()) {
        oprot.writeI32(struct.status.getValue());
      }
      if (struct.isSetMsg()) {
        oprot.writeString(struct.msg);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, StructResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list13 = iprot.readListBegin(org.apache.thrift.protocol.TType.STRUCT);
          struct.folders = new java.util.ArrayList<Folder>(_list13.size);
          @org.apache.thrift.annotation.Nullable Folder _elem14;
          for (int _i15 = 0; _i15 < _list13.size; ++_i15)
          {
            _elem14 = new Folder();
            _elem14.read(iprot);
            struct.folders.add(_elem14);
          }
        }
        struct.setFoldersIsSet(true);
      }
      if (incoming.get(1)) {
        struct.status = pa3.Status.findByValue(iprot.readI32());
        struct.setStatusIsSet(true);
      }
      if (incoming.get(2)) {
        struct.msg = iprot.readString();
        struct.setMsgIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

