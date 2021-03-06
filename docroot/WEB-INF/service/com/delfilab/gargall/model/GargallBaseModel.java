/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.delfilab.gargall.model;

import com.liferay.portal.kernel.json.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Filippo Maria Del Prete
 */
public class GargallBaseModel {

	public void addDynamicElement(String name, String content) {
		_dynamicElements.put(name, content);
	}

	@JSON
	public Map<String, String> getDynamicElements() {
		return _dynamicElements;
	}

	public void setDynamicElements(Map<String, String> dynamicElements) {
		_dynamicElements = dynamicElements;
	}

	private Map<String, String> _dynamicElements =
		new HashMap<String, String>();

}