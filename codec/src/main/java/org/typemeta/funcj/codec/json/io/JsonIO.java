package org.typemeta.funcj.codec.json.io;

import java.io.*;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class JsonIO {
    public static Input inputOf(Reader reader) {
        return new JsonParser(reader);
    }

    public static Input inputOf(InputStream is) {
        return new JsonParser(new InputStreamReader(is));
    }

    public static Output outputOf(Writer writer) {
        return new JsonGenerator(writer);
    }

    public static Output outputOf(OutputStream os) {
        return new JsonGenerator(new OutputStreamWriter(os));
    }

    public interface Input  {
        interface Event {
            Type type();

            enum Type implements Event {
                ARRAY_END,
                ARRAY_START,
                COMMA,          // internal use only
                COLON,          // internal use only
                EOF,
                FALSE,
                FIELD_NAME,
                NULL,
                NUMBER,
                OBJECT_END,
                OBJECT_START,
                STRING,
                TRUE;

                @Override
                public Type type() {
                    return this;
                }
            }

            final class FieldName implements Event {
                public final String value;

                public FieldName(String value) {
                    this.value = Objects.requireNonNull(value);
                }

                @Override
                public Type type() {
                    return Type.FIELD_NAME;
                }

                @Override
                public String toString() {
                    return "FieldName{" + value + "}";
                }

                @Override
                public boolean equals(Object rhs) {
                    if (this == rhs) {
                        return true;
                    } else if (rhs == null || getClass() != rhs.getClass()) {
                        return false;
                    } else {
                        FieldName rhsJS = (FieldName) rhs;
                        return value.equals(rhsJS.value);
                    }
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            }

            final class JString implements Event {
                public final String value;

                public JString(String value) {
                    this.value = Objects.requireNonNull(value);
                }

                @Override
                public Type type() {
                    return Type.STRING;
                }

                @Override
                public String toString() {
                    return "JString{" + value + "}";
                }

                @Override
                public boolean equals(Object rhs) {
                    if (this == rhs) {
                        return true;
                    } else if (rhs == null || getClass() != rhs.getClass()) {
                        return false;
                    } else {
                        final JString rhsJS = (JString) rhs;
                        return value.equals(rhsJS.value);
                    }
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            }

            final class JNumber implements Event {
                public final String value;

                public JNumber(String value) {
                    this.value = Objects.requireNonNull(value);
                }

                @Override
                public Type type() {
                    return Type.NUMBER;
                }

                @Override
                public String toString() {
                    return "JNumber{" + value + "}";
                }

                @Override
                public boolean equals(Object rhs) {
                    if (this == rhs) {
                        return true;
                    } else if (rhs == null || getClass() != rhs.getClass()) {
                        return false;
                    } else {
                        final JNumber rhsJN = (JNumber) rhs;
                        return value.equals(rhsJN.value);
                    }
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            }
        }

        boolean notEOF();

        Event.Type currentEventType();

        Event event(int lookahead);

        <T> T readNull();

        boolean readBoolean();

        String readString();
        char readChar();

        byte readByte();
        short readShort();
        int readInt();
        long readLong();
        float readFloat();
        double readDouble();
        BigDecimal readBigDecimal();
        Number readNumber();

        void startObject();
        String readFieldName();
        void endObject();

        void startArray();
        void endArray();
    }

    public interface Output {

        Output writeNull();

        Output writeBoolean(boolean value);

        Output writeStr(String value);
        Output writeChar(char value);

        Output writeNumber(byte value);
        Output writeNumber(short value);
        Output writeNumber(int value);
        Output writeNumber(long value);
        Output writeNumber(float value);
        Output writeNumber(double value);
        Output writeNumber(Number value);
        Output writeNumber(BigDecimal value);
        Output writeNumber(String value);

        Output startObject();
        Output writeField(String name);
        Output endObject();

        Output startArray();
        Output endArray();

        void close();
    }
}