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

package org.springframework.boot.actuate.endpoint.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.ClearCacheEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Adapter to expose {@link ClearCacheEndpoint} as an {@link MvcEndpoint}.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Sergei Egorov
 */
public class ClearCacheMVCEndpoint extends EndpointMvcAdapter {

	private ClearCacheEndpoint delegate;

	@Autowired
	public ClearCacheMVCEndpoint(ClearCacheEndpoint delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@HypermediaDisabled
	public Object invoke() {
		if (!this.delegate.isEnabled()) {
			// Shouldn't happen - MVC endpoint shouldn't be registered when delegate's
			// disabled
			return getDisabledResponse();
		}
		return super.invoke();
	}

}
