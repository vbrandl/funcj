package org.funcj.codec.xml;

import org.funcj.codec.Codec;
import org.funcj.codec.utils.ReflectionUtils;
import org.funcj.control.Exceptions;
import org.w3c.dom.*;

import java.util.Map;

import static org.funcj.codec.xml.XmlUtils.*;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Element> {
        private final XmlCodecCore core;
        private final Codec<K, Element> keyCodec;
        private final Codec<V, Element> valueCodec;

        public MapCodec(
                XmlCodecCore core,
                Codec<K, Element> keyCodec,
                Codec<V, Element> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Element encode(Map<K, V> map, Element out) {
            int i = 0;
            for (Map.Entry<K, V> entry : map.entrySet()) {
                final Element elem = core.addEntryElement(out);

                final Element keyNode = (Element)elem.appendChild(core.doc.createElement(core.keyElemName()));
                keyCodec.encode(entry.getKey(), keyNode);

                final Element valueNode = (Element)elem.appendChild(core.doc.createElement(core.valueElemName()));
                valueCodec.encode(entry.getValue(), valueNode);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();

            final Map<K, V> map = Exceptions.wrap(() -> ReflectionUtils.newInstance(dynType));

            for (int i = 0; i < l; ++i) {
                final Element elem = (Element)nodes.item(i);
                final K key = keyCodec.decode(firstChildElement(elem, core.keyElemName()));
                final V value = valueCodec.decode(firstChildElement(elem, core.valueElemName()));
                map.put(key, value);
            }

            return map;
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, Element> {
        private final XmlCodecCore core;
        private final Codec<V, Element> valueCodec;

        public StringMapCodec(XmlCodecCore core, Codec<V, Element> valueCodec) {
            this.core = core;
            this.valueCodec = valueCodec;
        }

        @Override
        public Element encode(Map<String, V> map, Element out) {
            int i = 0;
            for (Map.Entry<String, V> entry : map.entrySet()) {
                final Element elem = core.addEntryElement(out);
                setAttrValue(elem, core.keyAttrName(), entry.getKey());
                valueCodec.encode(entry.getValue(), elem);
            }

            return out;
        }

        @Override
        public Map<String, V> decode(Class<Map<String, V>> dynType, Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();

            final Map<String, V> map = Exceptions.wrap(
                    () -> ReflectionUtils.newInstance(dynType),
                    XmlCodecException::new);

            for (int i = 0; i < l; ++i) {
                final Element childElem = (Element)nodes.item(i);
                final String key = getAttrValue(childElem, core.keyAttrName());
                final V value = valueCodec.decode(childElem);
                map.put(key, value);
            }

            return map;
        }
    }
}
