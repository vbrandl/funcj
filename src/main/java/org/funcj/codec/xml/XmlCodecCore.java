package org.funcj.codec.xml;

import org.funcj.codec.*;
import org.funcj.control.Exceptions;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.lang.reflect.*;
import java.util.*;

import static org.funcj.codec.xml.XmlUtils.*;

public class XmlCodecCore extends CodecCore<Node> {

    public final DocumentBuilder docBuilder;

    public Document doc;

    public XmlCodecCore(DocumentBuilder docBuilder) {
        this.docBuilder = docBuilder;
    }

    public XmlCodecCore() {
        this(Exceptions.wrap(
                () -> DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                XmlCodecException::new));
    }

    public Document setNewDocument() {
        doc = docBuilder.newDocument();
        return doc;
    }

    protected String typeAttrName() {
        return "type";
    }

    protected String keyAttrName() {
        return "key";
    }

    protected String keyElemName() {
        return "key";
    }

    protected String valueElemName() {
        return "value";
    }

    protected String indexElem(int i) {
        return "_" + Integer.toString(i);
    }

    private final Codec.NullCodec<Node> nullCodec = new Codec.NullCodec<Node>() {
        @Override
        public boolean isNull(Node in) {
            if (!in.hasChildNodes()) {
                return true;
            } else {
                return in.getChildNodes().getLength() == 1 &&
                        firstChildTextOpt(in).map(t -> t.getWholeText().isEmpty())
                                .orElse(false);
            }
        }

        @Override
        public Node encode(Object val, Node out) {
            out.appendChild(doc.createTextNode(""));
            return out;
        }

        @Override
        public Object decode(Node in) {
            if (isNull(in)) {
                return null;
            } else {
                throw new IllegalStateException("Expected a null node but got " + in.getNodeName());
            }
        }
    };

    @Override
    public <T> Node encode(Class<T> type, T val) {
        return encode(type, val, "_");
    }

    public <T> Node encode(Class<T> type, T val, String rootName) {
        return encode(type, val, doc.appendChild(doc.createElement(rootName)));
    }

    @Override
    protected Codec.NullCodec<Node> nullCodec() {
        return nullCodec;
    }

    private final Codec.BooleanCodec<Node> booleanCodec = new Codec.BooleanCodec<Node>() {

        @Override
        public Node encodePrim(boolean val, Node out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public boolean decodePrim(Node in) {
            return Boolean.parseBoolean(firstChildText(in).getWholeText());
        }
    };

    @Override
    protected Codec.BooleanCodec<Node> booleanCodec() {
        return booleanCodec;
    }

    private final Codec<boolean[], Node> booleanArrayCodec = new Codec<boolean[], Node>() {

        @Override
        public Node encode(boolean[] vals, Node out) {
            for (int i = 0; i < vals.length; ++i) {
                final Node node = out.appendChild(doc.createElement(indexElem(i)));
                booleanCodec.encode(vals[i], node);
            }

            return out;
        }

        @Override
        public boolean[] decode(Node in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec.decode(boolean.class, nodes.item(i));
            }

            return vals;
        }
    };

    @Override
    protected Codec<boolean[], Node> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    private final Codec.IntegerCodec<Node> integerCodec = new Codec.IntegerCodec<Node>() {

        @Override
        public Node encodePrim(int val, Node out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public int decodePrim(Node in) {
            return Integer.parseInt(firstChildText(in).getWholeText());
        }
    };

    @Override
    protected Codec.IntegerCodec<Node> integerCodec() {
        return integerCodec;
    }

    private final Codec<int[], Node> integerArrayCodec = new Codec<int[], Node>() {

        @Override
        public Node encode(int[] vals, Node out) {
            for (int i = 0; i < vals.length; ++i) {
                final Node node = out.appendChild(doc.createElement(indexElem(i)));
                integerCodec.encode(vals[i], node);
            }

            return out;
        }

        @Override
        public int[] decode(Node in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = integerCodec.decode(int.class, nodes.item(i));
            }

            return vals;
        }
    };

    @Override
    protected Codec<int[], Node> integerArrayCodec() {
        return integerArrayCodec;
    }

    private final Codec<String, Node> stringCodec = new Codec<String, Node>() {
        @Override
        public Node encode(String val, Node out) {
            out.appendChild(doc.createTextNode(val));
            return out;
        }

        @Override
        public String decode(Node in) {
            return firstChildText(in).getWholeText();
        }
    };

    @Override
    protected Codec<String, Node> stringCodec() {
        return stringCodec;
    }

    @Override
    protected <EM extends Enum<EM>> Codec<EM, Node> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, Node>() {
            @Override
            public Node encode(EM val, Node out) {
                out.appendChild(doc.createTextNode(val.name()));
                return out;
            }

            @Override
            public EM decode(Class<EM> dynType, Node in) {
                return EM.valueOf(dynType, firstChildText(in).getWholeText());
            }
        };
    }

    @Override
    protected <K, V> Codec<Map<K, V>, Node> mapCodec(
            Class<K> keyType,
            Class<V> valType) {
        final Codec<V, Node> valueCodec = dynamicCodec(valType);
        if (String.class.equals(keyType)) {
            return (Codec)new XmlMapCodecs.StringMapCodec<V>(this, valueCodec);
        } else {
            final Codec<K, Node> keyCodec = dynamicCodec(keyType);
            return new XmlMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
        }
    }

    @Override
    protected <T> Codec<T[], Node> objectArrayCodec(Class<T> elemType, Codec<T, Node> elemCodec) {
        return new Codec<T[], Node>() {
            @Override
            public Node encode(T[] vals, Node out) {
                for (int i = 0; i < vals.length; ++i) {
                    final Node node = out.appendChild(doc.createElement(indexElem(i)));
                    elemCodec.encode(vals[i], node);
                }

                return out;
            }

            @Override
            public T[] decode(Class<T[]> dynType, Node in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();

                final NodeList nodes = in.getChildNodes();
                final int l = nodes.getLength();
                final T[] vals = (T[]) Array.newInstance(elemType, l);

                for (int i = 0; i < l; ++i) {
                    vals[i] = elemCodec.decode(dynElemType, nodes.item(i));
                }

                return vals;
            }
        };
    }

    @Override
    protected <T> Codec<T, Node> dynamicCodec(Class<T> stcType) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (!dynType.equals(stcType) && !Modifier.isFinal(stcType.getModifiers())) {
                    setAttrValue((Element)out, typeAttrName(), classToName(dynType));
                }
                return encode2(XmlCodecCore.this.getNullUnsafeCodec(dynType), val, out);
            }

            protected <S extends T> Node encode2(Codec<S, Node> codec, T val, Node out) {
                return codec.encode((S)val, out);
            }

            @Override
            public T decode(Node in) {
                final Element elem = (Element)in;
                final String typeName = getAttrValue(elem, typeAttrName());
                final Class<T> dynType;
                if (typeName.isEmpty()) {
                    dynType = stcType;
                } else {
                    dynType = nameToClass(typeName);
                }

                final Codec<T, Node> codec = XmlCodecCore.this.getNullUnsafeCodec(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    protected <T> Codec<T, Node> dynamicCodec(Codec<T, Node> codec, Class<T> stcType) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (!dynType.equals(stcType) && !Modifier.isFinal(stcType.getModifiers())) {
                    setAttrValue((Element)out, typeAttrName(), classToName(dynType));
                }
                return codec.encode(val, out);
            }

            @Override
            public T decode(Node in) {
                final Element elem = (Element)in;
                final String typeName = getAttrValue(elem, typeAttrName());
                final Class<T> dynType;
                if (typeName.isEmpty()) {
                    dynType = stcType;
                } else {
                    dynType = nameToClass(typeName);
                }

                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    protected <T> Codec<T, Node> createObjectCodec(
            Class<T> type,
            Map<String, FieldCodec<Node>> fieldCodecs) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                fieldCodecs.forEach((name, codec) -> {
                    final Element node = (Element)out.appendChild(doc.createElement(name));
                    codec.encode(val, node);
                });
                return out;
            }

            @Override
            public T decode(Class<T> dynType, Node in) {
                final Element elem = (Element)in;
                final T val = Exceptions.wrap(() -> dynType.newInstance(), XmlCodecException::new);
                fieldCodecs.forEach((name, codec) -> {
                    codec.decode(val, firstChildElement(elem, name));
                });
                return val;
            }
        };
    }

    @Override
    protected String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "_" + name;
        }
        return name;
    }
}
