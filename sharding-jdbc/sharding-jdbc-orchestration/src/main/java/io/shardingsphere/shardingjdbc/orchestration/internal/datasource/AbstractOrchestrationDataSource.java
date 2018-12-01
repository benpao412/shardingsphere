/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.shardingjdbc.orchestration.internal.datasource;

import com.google.common.eventbus.Subscribe;
import io.shardingsphere.core.constant.ShardingConstant;
import io.shardingsphere.orchestration.internal.OrchestrationFacade;
import io.shardingsphere.orchestration.internal.listener.ShardingOrchestrationEventBusInstance;
import io.shardingsphere.orchestration.internal.state.event.CircuitStateEvent;
import io.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import io.shardingsphere.shardingjdbc.orchestration.internal.util.DataSourceConverter;
import lombok.AccessLevel;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * Abstract orchestration data source.
 *
 * @author panjuan
 */
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractOrchestrationDataSource extends AbstractDataSourceAdapter {
    
    private final OrchestrationFacade orchestrationFacade;
    
    private boolean isCircuitBreak;
    
    public AbstractOrchestrationDataSource(final OrchestrationFacade orchestrationFacade, final Map<String, DataSource> dataSourceMap) throws SQLException {
        super(dataSourceMap);
        this.orchestrationFacade = orchestrationFacade;
        ShardingOrchestrationEventBusInstance.getInstance().register(this);
    }
    
    public AbstractOrchestrationDataSource(final OrchestrationFacade orchestrationFacade) throws SQLException {
        super(DataSourceConverter.getDataSourceMap(orchestrationFacade.getConfigService().loadDataSourceConfigurations(ShardingConstant.LOGIC_SCHEMA_NAME)));
        this.orchestrationFacade = orchestrationFacade;
        ShardingOrchestrationEventBusInstance.getInstance().register(this);
    }
    
    /**
     /**
     * Renew circuit breaker data source names.
     *
     * @param circuitStateEvent jdbc circuit event
     */
    @Subscribe
    public void renew(final CircuitStateEvent circuitStateEvent) {
        isCircuitBreak = circuitStateEvent.isCircuitBreak();
    }
}
