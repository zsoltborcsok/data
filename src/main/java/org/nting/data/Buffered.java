package org.nting.data;

public interface Buffered {

    void commit();

    void discard();

    void setBuffered(boolean buffered);

    boolean isBuffered();

    boolean isModified();
}
