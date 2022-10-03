package helpers;

import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

public class PooledDatasource extends PoolingDataSource {

	private GenericObjectPool gPool;

	public PooledDatasource(GenericObjectPool gPool) {
		super(gPool);
		this. gPool = gPool;
	}
		
}