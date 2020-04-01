package edu.uw.covidsafe.utils;

import com.google.protobuf.ByteString;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ByteUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static byte[] uuid2bytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    public static String byte2UUIDstring(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        UUID id = new UUID(bb.getLong(),bb.getLong());
        return id.toString();
    }

    public static UUID byte2uuid(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        return new UUID(bb.getLong(),bb.getLong());
    }

    public static byte[] string2byteArray(String s) {
        return uuid2bytes(UUID.fromString(s));
    }

    public static ByteString string2bytestring(String s) {
        return ByteString.copyFrom(uuid2bytes(UUID.fromString(s)));
    }
}