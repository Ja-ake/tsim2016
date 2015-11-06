package com.jakespringer.reagne.net;

public interface Protocol {
    /**
     * Serialize the message to be sent.
     * 
     * @return the byte array with the serialized message
     */
    public byte[] serialize();

    /**
     * Deserialize the message.
     * 
     * @param buffer
     *            the serialized message
     */
    public void deserialize(byte[] buffer);
}
