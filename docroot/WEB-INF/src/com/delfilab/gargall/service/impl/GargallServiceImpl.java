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

package com.delfilab.gargall.service.impl;

import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portlet.asset.service.AssetTagServiceUtil;
import com.liferay.portlet.asset.model.AssetTag;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileShortcut;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.delfilab.gargall.model.GargallDLFileEntry;
import com.delfilab.gargall.service.base.GargallServiceBaseImpl;

import java.text.Format;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author James Falkner
 * @author Amos Fong
 */
public class GargallServiceImpl extends GargallServiceBaseImpl {

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public List<GargallDLFileEntry> getGargallDLFileEntries(
			long repositoryId, long folderId, int status, 
			int start, int end)
		throws Exception {

		List<GargallDLFileEntry> gargallDLFileEntries =
			new ArrayList<GargallDLFileEntry>();
		
		Set<Long> dlFileEntryIds = new HashSet<Long>();
		
		PermissionChecker folderPermissionChecker = getPermissionChecker();

		if (folderPermissionChecker.hasPermission(
				repositoryId, DLFolder.class.getName(),
				folderId, ActionKeys.VIEW)) {

			QueryDefinition queryDefinition = new QueryDefinition(
					status, start, end, null);
		
			List<Object> foldersAndFileEntriesAndFileShortcuts = 
					dlFolderLocalService.getFoldersAndFileEntriesAndFileShortcuts(
							repositoryId, folderId, null, false, queryDefinition);
			for (Object folderAndFileEntryAndFileShortcut: foldersAndFileEntriesAndFileShortcuts) {    
			    if (folderAndFileEntryAndFileShortcut instanceof FileEntry) {
			        FileEntry fileEntry = (FileEntry) folderAndFileEntryAndFileShortcut;
			        long fileEntryId2 = fileEntry.getFileEntryId();
			        DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(fileEntryId2);
					if (dlFileEntryIds.contains(dlFileEntry.getFileEntryId())) {
						continue;
					}
					dlFileEntryIds.add(dlFileEntry.getFileEntryId());
					try {
						PermissionChecker permissionChecker = getPermissionChecker();
	
						if (permissionChecker.hasPermission(
								dlFileEntry.getGroupId(), DLFileEntry.class.getName(),
								dlFileEntry.getPrimaryKey(), ActionKeys.VIEW)) {
	
							GargallDLFileEntry gargallDlFileEntry =
								getGargallDLFileEntry(dlFileEntry);
	
							gargallDLFileEntries.add(gargallDlFileEntry);
						}
					}
					catch (NoSuchFileEntryException nsae) {
					}
	
			    } else if (folderAndFileEntryAndFileShortcut instanceof Folder) {
			        //Folder subFolder = (Folder) folderAndFileEntryAndFileShortcut;
			        // qui ricorsione
			    } else if (folderAndFileEntryAndFileShortcut instanceof DLFileShortcut) {
			        //DLFileShortcut dlFileShorcut = (DLFileShortcut) folderAndFileEntryAndFileShortcut;
			        // qui non faccio nulla
			        continue;
			    } //end for su foldersAndFileEntriesAndFileShortcuts
			} 
		}
		return gargallDLFileEntries;
	}

	protected GargallDLFileEntry getGargallDLFileEntry(
			DLFileEntry dlFileEntry)
		throws Exception {

		List<String> tagNames =	new ArrayList<String>();
		for(AssetTag theTag : AssetTagServiceUtil.getTags(DLFileEntry.class.getName(),
				 dlFileEntry.getClassPK())) {
			tagNames.add(theTag.getName());
		}
		String jsonTags = JSONFactoryUtil.looseSerialize(tagNames);
		 
		GargallDLFileEntry gargallDLFileEntry = new GargallDLFileEntry();
		gargallDLFileEntry.addDynamicElement("uuid", dlFileEntry.getUuid());
		gargallDLFileEntry.addDynamicElement("description", dlFileEntry.getDescription());
		gargallDLFileEntry.addDynamicElement("title", dlFileEntry.getTitle());
		gargallDLFileEntry.addDynamicElement("size", String.valueOf(dlFileEntry.getSize()));
		gargallDLFileEntry.addDynamicElement("modifiedDate", _format.format(dlFileEntry.getModifiedDate()));
		gargallDLFileEntry.addDynamicElement("fileEntryId", String.valueOf(dlFileEntry.getFileEntryId()));
		gargallDLFileEntry.addDynamicElement("folderId", String.valueOf(dlFileEntry.getFolderId()));
		gargallDLFileEntry.addDynamicElement("tags", jsonTags);

		populateGargallDLFileEntry(gargallDLFileEntry);

		return gargallDLFileEntry;
	}

	protected void populateGargallDLFileEntry(
		GargallDLFileEntry gargallDLFileEntry) {
		return;
	}

	private Format _format = FastDateFormatFactoryUtil.getSimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");

}