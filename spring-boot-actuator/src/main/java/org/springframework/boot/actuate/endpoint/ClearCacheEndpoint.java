/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.endpoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * {@link Endpoint} to clear the caches.
 *
 * @author Maxime Deravet
 */
@ConfigurationProperties(prefix = "endpoints.dump", ignoreUnknownFields = false)
public class ClearCacheEndpoint extends AbstractEndpoint<Map<String, Object>> {

	private Map<String, CacheManager> cacheManagers;

	public ClearCacheEndpoint(Map<String, CacheManager> cacheManagers) {
		super("clear_cache");
		this.cacheManagers = cacheManagers;
	}

	private MultiValueMap<String, CacheManagerBean> getCacheManagerBeans() {
		MultiValueMap<String, CacheManagerBean> cacheManagerNamesByCacheName = new LinkedMultiValueMap<String, CacheManagerBean>();
		for (Map.Entry<String, CacheManager> entry : this.cacheManagers.entrySet()) {
			for (String cacheName : entry.getValue().getCacheNames()) {
				cacheManagerNamesByCacheName.add(cacheName,
						new CacheManagerBean(entry.getKey(), entry.getValue()));
			}
		}
		return cacheManagerNamesByCacheName;
	}

	@Override
	public Map<String, Object> invoke() {
		for (Map.Entry<String, List<CacheManagerBean>> entry : getCacheManagerBeans()
				.entrySet()) {
			clearCache(entry.getKey(), entry.getValue());
		}

		return Collections.<String, Object>singletonMap("message", "caches cleared");
	}

	private void clearCache(String cacheName, List<CacheManagerBean> cacheManagerBeans) {
		for (CacheManagerBean cacheManagerBean : cacheManagerBeans) {
			CacheManager cacheManager = cacheManagerBean.getCacheManager();
			Cache cache = cacheManager.getCache(cacheName);

			cache.clear();
		}
	}

	private static class CacheManagerBean {

		private final String beanName;

		private final CacheManager cacheManager;

		CacheManagerBean(String beanName, CacheManager cacheManager) {
			this.beanName = beanName;
			this.cacheManager = cacheManager;
		}

		public String getBeanName() {
			return this.beanName;
		}

		public CacheManager getCacheManager() {
			return this.cacheManager;
		}

	}
}
