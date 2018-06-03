package longhm.clientpool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;

import java.util.function.BiFunction;
/**
 * @author longhm
 */
public class ClientFactory<T extends BaseClient> implements PooledObjectFactory<T> {
    private String host;
    private int port;
    private BiFunction<String, Integer, T> supplier;

    public ClientFactory(String host, int port, BiFunction<String, Integer, T> supplier) {
        this.host = host;
        this.port = port;
        this.supplier = supplier;
    }

    @Override
    public PooledObject<T> makeObject() throws Exception {
        return new DefaultPooledObject<>(supplier.apply(this.host, this.port));
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
