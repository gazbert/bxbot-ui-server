/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Gareth Jon Lynch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gazbert.bxbot.ui.server.repository.remote.impl;

import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.repository.remote.StrategyConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the Strategy config repository.
 *
 * @author gazbert
 */
@Repository("strategyConfigRepository")
@Transactional
public class StrategyConfigRepositoryRestClient implements StrategyConfigRepository {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public List<StrategyConfig> findAll() {
        throw new UnsupportedOperationException("findAll() not implemented");
    }

    @Override
    public StrategyConfig findById(String id) {
        throw new UnsupportedOperationException("findById() not implemented");
    }

    @Override
    public StrategyConfig save(StrategyConfig config) {
        throw new UnsupportedOperationException("save() not implemented");
    }

    @Override
    public StrategyConfig delete(String id) {
        throw new UnsupportedOperationException("delete() not implemented");
    }

    // ------------------------------------------------------------------------------------------------
    // Adapter methods
    // ------------------------------------------------------------------------------------------------

//    private static List<StrategyConfig> adaptAllInternalToAllExternalConfig(TradingStrategiesType internalStrategiesConfig) {
//
//        final List<StrategyConfig> strategyConfigItems = new ArrayList<>();
//
//        final List<StrategyType> internalStrategyConfigItems = internalStrategiesConfig.getStrategies();
//        internalStrategyConfigItems.forEach((item) -> {
//
//            final StrategyConfig strategyConfig = new StrategyConfig();
//            strategyConfig.setId(item.getId());
//            strategyConfig.setLabel(item.getLabel());
//            strategyConfig.setDescription(item.getDescription());
//            strategyConfig.setClassName(item.getClassName());
//
//            item.getConfiguration().getConfigItem().forEach(internalConfigItem ->
//                    strategyConfig.getConfigItems().put(internalConfigItem.getName(), internalConfigItem.getValue()));
//
//            strategyConfigItems.add(strategyConfig);
//        });
//
//        return strategyConfigItems;
//    }

//    private static StrategyConfig adaptInternalToExternalConfig(List<StrategyType> internalStrategyConfigItems) {
//
//        final StrategyConfig strategyConfig = new StrategyConfig();
//
//        if (!internalStrategyConfigItems.isEmpty()) {
//
//            // Should only ever be 1 unique Strategy id
//            final StrategyType internalStrategyConfig = internalStrategyConfigItems.get(0);
//            strategyConfig.setId(internalStrategyConfig.getId());
//            strategyConfig.setLabel(internalStrategyConfig.getLabel());
//            strategyConfig.setDescription(internalStrategyConfig.getDescription());
//            strategyConfig.setClassName(internalStrategyConfig.getClassName());
//
//            internalStrategyConfig.getConfiguration().getConfigItem().forEach(internalConfigItem ->
//                    strategyConfig.getConfigItems().put(internalConfigItem.getName(), internalConfigItem.getValue()));
//        }
//        return strategyConfig;
//    }

//    private static StrategyType adaptExternalToInternalConfig(StrategyConfig externalStrategyConfig) {
//
//        final ConfigurationType configurationType = new ConfigurationType();
//        externalStrategyConfig.getConfigItems().entrySet()
//                .forEach(item -> {
//                    final ConfigItemType configItem = new ConfigItemType();
//                    configItem.setName(item.getKey());
//                    configItem.setValue(item.getValue());
//                    configurationType.getConfigItem().add(configItem);
//                });
//
//        final StrategyType strategyType = new StrategyType();
//        strategyType.setId(externalStrategyConfig.getId());
//        strategyType.setLabel(externalStrategyConfig.getLabel());
//        strategyType.setDescription(externalStrategyConfig.getDescription());
//        strategyType.setClassName(externalStrategyConfig.getClassName());
//        strategyType.setConfiguration(configurationType);
//        return strategyType;
//    }
}