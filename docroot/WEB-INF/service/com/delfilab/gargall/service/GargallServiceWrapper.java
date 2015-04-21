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

package com.delfilab.gargall.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link GargallService}.
 *
 * @author Brian Wing Shun Chan
 * @see GargallService
 * @generated
 */
public class GargallServiceWrapper implements GargallService,
	ServiceWrapper<GargallService> {
	public GargallServiceWrapper(GargallService gargallService) {
		_gargallService = gargallService;
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _gargallService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_gargallService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _gargallService.invokeMethod(name, parameterTypes, arguments);
	}

	@Override
	public java.util.List<com.delfilab.gargall.model.GargallDLFileEntry> getGargallDLFileEntries(
		long repositoryId, long folderId, int status, int start, int end)
		throws java.lang.Exception {
		return _gargallService.getGargallDLFileEntries(repositoryId, folderId,
			status, start, end);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public GargallService getWrappedGargallService() {
		return _gargallService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedGargallService(GargallService gargallService) {
		_gargallService = gargallService;
	}

	@Override
	public GargallService getWrappedService() {
		return _gargallService;
	}

	@Override
	public void setWrappedService(GargallService gargallService) {
		_gargallService = gargallService;
	}

	private GargallService _gargallService;
}