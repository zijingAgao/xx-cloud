package com.xx.awdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.googlecode.ipv6.IPv6Address;
import com.xx.awdb.entity.AwdbMetaData;
import com.xx.awdb.enumerate.FileOpenMode;
import com.xx.awdb.exception.AwdbCloseException;
import com.xx.awdb.exception.IpTypeException;
import com.xx.awdb.impl.AwdbNoCacheImpl;
import inet.ipaddr.IPAddressString;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * awdb文件读取器
 */
public class AwdbReader implements Closeable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AwdbMetaData awdbMetaData;

    private final AtomicReference<AwdbBufferHolder> bufferRef;

    private final AwdbNodeCache cache;

    /**
     * 不带缓存的构造器
     *
     * @param file awdb文件
     */
    public AwdbReader(File file) throws IOException {
        this(file, AwdbNoCacheImpl.getInstance());
    }

    /**
     * 指定缓存的构造器
     *
     * @param file  awdb文件
     * @param cache 缓存实例
     */
    public AwdbReader(File file, AwdbNodeCache cache) throws IOException {
        this(file, cache, FileOpenMode.MEMORY_MAPPED);
    }

    /**
     * 指定缓存的构造器
     *
     * @param file  awdb文件
     * @param cache 缓存实例
     * @param mode  打开文件的模式
     */
    public AwdbReader(File file, AwdbNodeCache cache, FileOpenMode mode) throws IOException {
        this(new AwdbBufferHolder(file, mode), cache);
    }

    /**
     * 不带缓存的构造器
     *
     * @param source 输入流
     */
    public AwdbReader(InputStream source) throws IOException {
        this(source, AwdbNoCacheImpl.getInstance());
    }

    /**
     * 指定缓存的构造器
     *
     * @param source 输入流
     * @param cache  缓存实例
     */
    public AwdbReader(InputStream source, AwdbNodeCache cache) throws IOException {
        this(source, cache, FileOpenMode.MEMORY);
    }

    /**
     * 指定缓存的构造器
     *
     * @param source 输入流
     * @param cache  缓存实例
     * @param mode   打开文件的模式
     */
    public AwdbReader(InputStream source, AwdbNodeCache cache, FileOpenMode mode) throws IOException {
        this(new AwdbBufferHolder(source, mode), cache);
    }

    /**
     * 指定缓存的构造器
     *
     * @param holder 缓冲器
     * @param cache  缓存实例
     */
    private AwdbReader(AwdbBufferHolder holder, AwdbNodeCache cache) throws IOException {
        this.bufferRef = new AtomicReference<>(holder);

        if (cache == null) {
            throw new NullPointerException("cache cannot be null");
        }
        this.cache = cache;

        ByteBuffer buffer = holder.getCurrBuffer();

        int metaLen = metaLength(buffer);
        AwdbDataParser awdbDataParser = new AwdbDataParser(this.cache, buffer);

        awdbMetaData = awdbDataParser.parseMeta(metaLen);
    }

    /**
     * 读取元数据长度
     *
     * @param buffer buffer
     * @return 元数据长度
     */
    private int metaLength(ByteBuffer buffer) {
        return AwdbDataParser.buffer2Integer(buffer, 0, 2);
    }

    @Override
    public void close() {
        bufferRef.set(null);
    }

    /**
     * 在awdb文件中查找ip地址的位置
     *
     * @param ip IP地址字符串
     * @return ip地址位置
     */
    public JsonNode findIpLocation(String ip) throws IOException {
        String dot = ".";
        String colon = ":";
        if (ip.contains(dot) || ip.contains(colon)) {
            return findIpLocation(InetAddress.getByName(ip));
        } else {
            BigInteger integerIp = new BigInteger(ip);
            BigInteger sqrt = BigInteger.valueOf((long) Math.pow(2, 32));
            if (integerIp.compareTo(sqrt) < 0) {
                return findIpLocation(InetAddress.getByName(ip));
            }
            IPv6Address ipv6 = IPv6Address.fromBigInteger(integerIp);
            return findIpLocation(ipv6.toInetAddress());

        }
    }

    /**
     * 在awdb文件中查找ip地址的位置
     *
     * @param ipAddr IP地址
     * @return ip地址位置
     */
    private JsonNode findIpLocation(InetAddress ipAddr) throws IOException {
        String ipVersion = awdbMetaData.getIpVersion();

        String v6 = "6";
        if (v6.equals(ipVersion) && ipAddr instanceof Inet4Address) {
            throw new IpTypeException(String.format("The database is IPv4 library, but you are using ipv6 queries! The IP is: %s", ipAddr.getHostAddress()));
        }

        String v4 = "4";
        if (v4.equals(ipVersion) && ipAddr instanceof Inet6Address) {
            throw new IpTypeException(String.format("The database is IPv6 library, but you are using ipv4 queries! The IP is: %s", ipAddr.getHostAddress()));
        }

        String mix = "4_6";
        int nodeIndex = 0;

        if (mix.equals(ipVersion)) {

            if (ipAddr instanceof Inet4Address) {
                String ipStr = new IPAddressString(ipAddr.getHostAddress()).getAddress().toIPv6().toFullString();
                ipAddr = InetAddress.getByName(ipStr);
                BigInteger intIp = IPv6Address.fromString(ipStr).toBigInteger();

                int leftBit = 32;
                int hex = 0xFFFF;

                if (intIp.shiftRight(leftBit).compareTo(BigInteger.valueOf(hex)) == 0) {
                    nodeIndex = 96;
                }
            }
        }

        nodeIndex = findTreeIndex(ipAddr, nodeIndex);

        int pointer = awdbMetaData.getBaseOffset() + nodeIndex - awdbMetaData.getNodeCount() - 10;
        switch (awdbMetaData.getDecodeType()) {
            case 1:
                return decodeContentStructure(pointer);
            case 2:
                return decodeContentDirect(pointer);
            default:
                return new TextNode("Invalid decode type: " + awdbMetaData.getDecodeType());
        }
    }

    /**
     * 解析awdb内容
     *
     * @param offset 偏移量
     * @return ip地址位置
     * @throws IOException 打开或读取失败
     */
    private JsonNode decodeContentStructure(int offset) throws IOException {
        JsonNode valuesJson = parseDataPointer(offset);

        return mapKeyValue(awdbMetaData.getColumns(), valuesJson);
    }

    /**
     * 数据索引指向内容直接解析，以\t分割
     *
     * @param offset 偏移量
     * @return ip地址位置
     * @throws IOException 打开或读取失败
     */
    private JsonNode decodeContentDirect(int offset) throws IOException {
        ByteBuffer buffer = getBuffer().getCurrBuffer();
        int position = buffer.position();
        buffer.position(offset);

        int dataLen = AwdbDataParser.buffer2Integer(buffer, 0, 4);

        buffer.limit(offset + 4 + dataLen);
        String[] values = StandardCharsets.UTF_8.newDecoder().decode(buffer).toString().split("\t");
        buffer.position(position);

        Map<String, JsonNode> result = new HashMap<>(awdbMetaData.getColumns().size());
        for (int i = 0; i < awdbMetaData.getColumns().size(); i++) {
            String value = values.length - i > 0 ? values[i] : "";
            result.put((String) awdbMetaData.getColumns().get(i), new TextNode(value));
        }

        return new ObjectNode(OBJECT_MAPPER.getNodeFactory(), Collections.unmodifiableMap(result));
    }

    /**
     * 根据数据索引解析数据
     *
     * @param offset 偏移量
     * @return 数据
     */
    private JsonNode parseDataPointer(int offset) throws IOException {
        ByteBuffer buffer = getBuffer().getCurrBuffer();

        AwdbDataParser awdbDataParser = new AwdbDataParser(this.cache, buffer, awdbMetaData.getBaseOffset());

        return awdbDataParser.parseData(offset);
    }

    /**
     * 查找数据索引
     *
     * @param ipAddress IP地址
     * @return 索引位置
     */
    private int findTreeIndex(InetAddress ipAddress, int nodeIndex) throws IOException {
        ByteBuffer buffer = getBuffer().getCurrBuffer();
        byte[] rawAddr = ipAddress.getAddress();

        int bitLength = rawAddr.length * 8;
        int nodeCount = awdbMetaData.getNodeCount();

        for (int pl = 0; pl < bitLength && nodeIndex < nodeCount; pl++) {
            int b = 0xFF & rawAddr[pl / 8];
            int bit = 1 & (b >> 7 - (pl % 8));
            nodeIndex = readNodeIndex(buffer, nodeIndex, bit);
        }

        // 每个节点由两条记录组成，每条记录都是指向文件中地址的指针。如果记录值大于搜索树中的节点数，则实际指针值指向数据部分
        if (nodeIndex == nodeCount) {
            return 0;
        } else if (nodeIndex > nodeCount) {
            return nodeIndex;
        }

        throw new IOException("Invalid node_index in search tree");
    }

    /**
     * 查找索引位置
     *
     * @param buffer    数据
     * @param nodeIndex 节点索引
     * @param bit       二进制位
     * @return 索引位置
     */
    private int readNodeIndex(ByteBuffer buffer, int nodeIndex, int bit) {
        int offset = nodeIndex * awdbMetaData.getByteLen() * 2 + bit * awdbMetaData.getByteLen() + awdbMetaData.getStartLength();
        buffer.position(offset);
        return AwdbDataParser.buffer2Integer(buffer, 0, awdbMetaData.getByteLen());
    }

    /**
     * 获取buffer
     *
     * @return AwdbBufferHolder
     */
    private AwdbBufferHolder getBuffer() throws AwdbCloseException {
        AwdbBufferHolder awdbBufferHolder = bufferRef.get();
        if (awdbBufferHolder == null) {
            throw new AwdbCloseException();
        }
        return awdbBufferHolder;
    }

    /**
     * key和value映射
     *
     * @param keys   key列表
     * @param values value列表
     * @return JsonNode对象
     */
    private JsonNode mapKeyValue(List<Object> keys, JsonNode values) {
        Map<String, JsonNode> resultDict = new HashMap<>(keys.size());

        if (keys.size() == values.size()) {
            for (int i = 0; i < keys.size(); i++) {
                resultDict.put((String) keys.get(i), values.get(i));
            }
        } else {
            for (int i = 0; i < values.size() - 1; i++) {
                resultDict.put((String) keys.get(i), values.get(i));
            }

            String multiAreasName = (String) keys.get(keys.size() - 2);
            List<?> keysList = (List<?>) keys.get(keys.size() - 1);
            JsonNode valuesJsonNode = values.get(values.size() - 1);

            ArrayNode nodes = new ArrayNode(OBJECT_MAPPER.getNodeFactory());
            for (JsonNode value : valuesJsonNode) {
                Map<String, JsonNode> tempDic = new HashMap<>(keysList.size());

                for (int i = 0; i < keysList.size(); i++) {
                    tempDic.put((String) keysList.get(i), value.get(i));
                }
                nodes.add(new ObjectNode(OBJECT_MAPPER.getNodeFactory(), Collections.unmodifiableMap(tempDic)));
            }
            resultDict.put(multiAreasName, nodes);
        }


        return new ObjectNode(OBJECT_MAPPER.getNodeFactory(), Collections.unmodifiableMap(resultDict));
    }
}