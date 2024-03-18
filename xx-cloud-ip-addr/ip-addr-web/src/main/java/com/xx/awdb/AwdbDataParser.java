package com.xx.awdb;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.xx.awdb.entity.AwdbMetaData;
import com.xx.awdb.enumerate.AwdbDataType;
import com.xx.awdb.exception.InvalidAwdbException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * awdb文件数据解析器
 */
class AwdbDataParser {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ByteBuffer buffer;

    private final AwdbNodeCache cache;

    private final CharsetDecoder strDecoder = StandardCharsets.UTF_8.newDecoder();

    private int basePointer = 0;

    public AwdbDataParser(AwdbNodeCache cache, ByteBuffer buffer) {
        this.cache = cache;
        this.buffer = buffer;
    }

    public AwdbDataParser(AwdbNodeCache cache, ByteBuffer buffer, int basePointer) {
        this(cache, buffer);
        this.basePointer = basePointer;
    }

    private final AwdbNodeCache.Loader loader = this::parseData;

    /**
     * 解析元数据
     *
     * @param length 元数据长度
     * @return 元数据
     */
    protected AwdbMetaData parseMeta(int length) throws CharacterCodingException {
        int startLen = 2 + length;
        buffer.limit(startLen);
        String meta = strDecoder.decode(buffer).toString();
        JSONObject metaJsonObj = JSONObject.parseObject(meta);
        return new AwdbMetaData(metaJsonObj, startLen);
    }

    /**
     * 解析数据
     *
     * @param offset 偏移量
     * @return JsonNode
     */
    protected JsonNode parseData(int offset) throws IOException {
        if (offset >= this.buffer.capacity()) {
            throw new InvalidAwdbException(
                    "The AWDB file's data section contains bad data: pointer larger than the database.");
        }

        buffer.position(offset);

        return parser();
    }

    /**
     * 解析器
     *
     * @return JsonNode
     */
    private JsonNode parser() throws IOException {
        // 获取控制字节
        int typeByte = 0xFF & buffer.get();

        // awdb数据类型
        AwdbDataType dataType = AwdbDataType.getDataType(typeByte);

        // 获取列表长度
        int len = 0xFF & buffer.get();

        return parseDataType(dataType, len);
    }

    /**
     * 根据数据类型来解析数据
     *
     * @param type 数据类型
     * @param len  数据长度
     * @return JsonNode
     */
    private JsonNode parseDataType(AwdbDataType type, int len) throws IOException {
        switch (type) {
            case ARRAY:
                return parseArray(len);
            case POINTER:
                return parsePointer(len);
            case STRING:
                return parseString(len);
            case TEXT:
                return parseText(len);
            case INT:
                return parseInt();
            case UINT:
                return parseUint(len);
            case FLOAT:
                return parseFloat();
            case DOUBLE:
                return parseDouble();
            default:
                throw new InvalidAwdbException("Unknown or unexpected type: " + type.name());
        }
    }

    /**
     * 解析数组数据（列表）
     *
     * @param len 数据长度
     * @return JsonNode
     */
    private JsonNode parseArray(int len) throws IOException {
        List<JsonNode> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            JsonNode node = parser();
            list.add(node);
        }

        ArrayNode nodes = new ArrayNode(OBJECT_MAPPER.getNodeFactory());
        return nodes.addAll(Collections.unmodifiableList(list));
    }

    /**
     * 解析指针数据
     *
     * @param len 数据长度
     * @return JsonNode
     */
    private JsonNode parsePointer(int len) throws IOException {
        int oldLimit = buffer.limit();
        buffer.limit(buffer.position() + len);
        int buf = AwdbDataParser.buffer2Integer(buffer, 0, len);
        int pointer = basePointer + buf;
        buffer.limit(oldLimit);
        int position = buffer.position();
        JsonNode jsonNode = cache.get(loader, pointer);
        buffer.position(position);
        return jsonNode;
    }

    /**
     * 解析字符串数据
     *
     * @param len 数据长度
     * @return JsonNode
     */
    private JsonNode parseString(int len) throws CharacterCodingException {
        int oldLimit = buffer.limit();
        buffer.limit(buffer.position() + len);
        String s = strDecoder.decode(buffer).toString();
        buffer.limit(oldLimit);
        return new TextNode(s);
    }

    /**
     * 解析长字符串数据
     *
     * @param len 数据长度
     * @return JsonNode
     */
    private JsonNode parseText(int len) throws CharacterCodingException {
        int oldLimit = buffer.limit();
        buffer.limit(buffer.position() + len);
        int dataLen = AwdbDataParser.buffer2Integer(buffer, 0, len);
        buffer.limit(oldLimit);
        buffer.limit(buffer.position() + dataLen);
        String s = strDecoder.decode(buffer).toString();
        buffer.limit(oldLimit);
        return new TextNode(s);
    }

    /**
     * 解析无符号整型数据
     *
     * @param len 数据长度
     * @return JsonNode
     */
    private JsonNode parseUint(int len) {
        return new LongNode(buffer2Long(len));
    }

    /**
     * 解析有符号整型
     *
     * @return JsonNode
     */
    private JsonNode parseInt() {
        buffer.position(buffer.position() - 1);
        return new IntNode(AwdbDataParser.buffer2Integer(buffer, 0, 4));
    }

    /**
     * 解析double类型的数据
     *
     * @return JsonNode
     */
    private JsonNode parseDouble() {
        buffer.position(buffer.position() - 1);
        return new DoubleNode(buffer.getDouble());
    }

    /**
     * 解析float类型的数据
     *
     * @return JsonNode
     */
    private JsonNode parseFloat() {
        buffer.position(buffer.position() - 1);
        return new FloatNode(buffer.getFloat());
    }

    /**
     * 解析无符号整型
     *
     * @param len 数据长度
     * @return 无符号整型
     */
    private long buffer2Long(int len) {
        long num = 0;
        for (int i = 0; i < len; i++) {
            num = (num << 8) | (buffer.get() & 0xFF);
        }
        return num;
    }

    /**
     * 解析有符号整型
     *
     * @param buffer 缓存器
     * @param base   基准位置
     * @param len    数据长度
     * @return 有符号整型
     */
    protected static int buffer2Integer(ByteBuffer buffer, int base, int len) {
        int num = base;
        for (int i = 0; i < len; i++) {
            num = (num << 8) | (buffer.get() & 0xFF);
        }
        return num;
    }
}