package com.github.minhlong293.thrift.clientpool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;

import java.util.function.Supplier;

/**
 * @author minhlong293
 */
public class ClientFactory<T extends BaseClient> implements PooledObjectFactory<T> {
    private Supplier<T> clientSupplier;

    public ClientFactory(Supplier<T> clientSupplier) {
        this.clientSupplier = clientSupplier;
    }

    @Override
    public PooledObject<T> makeObject() throws Exception {
        return new DefaultPooledObject<>(clientSupplier.get());
    }

    @Override
    public void destroyObject(PooledObject<T> p) throws Exception {
        T object = p.getObject();
        if (object != null && object.isConnected()) {
            object.disconnect();
        }
    }

    /**
     * Ensures that the instance is safe to be returned by the pool.
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<T> p) {
        return p.getObject().isConnected();
    }

    /**
     * Reinitialize an instance to be returned by the pool.
     * @param p
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<T> p) throws Exception {
        if (!p.getObject().isConnected()) {
            p.getObject().connect();
        }
    }

    /**
     * Uninitialize an instance to be returned to the idle object pool.
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<T> p) throws Exception {

    }
}
