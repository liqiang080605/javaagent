package com.jd.jvm.jmonitor.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 默认实现自动释放连接释放管理
 */
public class DefaultModuleResourceManager implements ModuleResourceManager{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, List<WeakResource<?>>> moduleResourceListMapping
            = new HashMap<String, List<WeakResource<?>>>();

    public synchronized <T> T append(String uniqueId, WeakResource<T> resource) {
        if (null == resource
                || null == resource.get()) {
            return null;
        }
        final List<WeakResource<?>> moduleResourceList;
        if (moduleResourceListMapping.containsKey(uniqueId)) {
            moduleResourceList = moduleResourceListMapping.get(uniqueId);
        } else {
            moduleResourceListMapping.put(uniqueId, moduleResourceList
                    = new ArrayList<WeakResource<?>>());
        }
        moduleResourceList.add(resource);
        logger.debug("append resource={} in module[id={};]", resource.get(), uniqueId);
        return resource.get();
    }

    public <T> void remove(String uniqueId, T target) {
        if (null == target) {
            return;
        }
        synchronized (this) {
            final List<WeakResource<?>> moduleResourceList = moduleResourceListMapping.get(uniqueId);
            if (null == moduleResourceList) {
                return;
            }
            final Iterator<WeakResource<?>> resourceRefIt = moduleResourceList.iterator();
            while (resourceRefIt.hasNext()) {
                final WeakResource<?> resourceRef = resourceRefIt.next();

                // 删除掉无效的资源
                if (null == resourceRef) {
                    resourceRefIt.remove();
                    logger.debug("remove illegal resource in module[id={};]", uniqueId);
                    continue;
                }

                // 删除掉已经被GC掉的资源
                if (null == resourceRef.get()) {
                    resourceRefIt.remove();
                    logger.debug("remove empty resource in module[id={};]", uniqueId);
                    continue;
                }

                if (target.equals(resourceRef.get())) {
                    resourceRefIt.remove();
                    logger.debug("remove resource={} in module[id={};]", resourceRef.get(), uniqueId);
                }
            }//while
        }//sync
    }
}
