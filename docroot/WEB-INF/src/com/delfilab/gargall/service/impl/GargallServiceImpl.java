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

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portlet.asset.service.AssetTagServiceUtil;
import com.liferay.portlet.asset.model.AssetTag;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.delfilab.gargall.model.GargallDDLRecord;
import com.delfilab.gargall.model.GargallDLFileEntry;
import com.delfilab.gargall.service.base.GargallServiceBaseImpl;

import java.io.Serializable;
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
	public List<GargallDDLRecord> getGargallDDLRecords(long ddlRecordSetId)
		throws Exception {

		List<GargallDDLRecord> gargallDDLRecords =
			new ArrayList<GargallDDLRecord>();

		PermissionChecker permissionChecker = getPermissionChecker();

		DDLRecordSet ddlRecordSet = ddlRecordSetLocalService.getRecordSet(
			ddlRecordSetId);

		if (permissionChecker.hasPermission(
				ddlRecordSet.getGroupId(), DDLRecordSet.class.getName(),
				ddlRecordSet.getRecordSetId(), ActionKeys.VIEW)) {

			for (DDLRecord ddlRecord : ddlRecordSet.getRecords()) {
				GargallDDLRecord gargallDDLRecord = getGargallDDLRecord(ddlRecord);

				gargallDDLRecords.add(gargallDDLRecord);
			}
		}

		return gargallDDLRecords;
	}

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public List<GargallDLFileEntry> getGargallDLFileEntries(
			long repositoryId, long folderId, int status, 
			int start, int end)
		throws Exception {

		List<GargallDLFileEntry> gargallDLFileEntries =
			new ArrayList<GargallDLFileEntry>();
		
		Set<Long> dlFileEntryIds = new HashSet<Long>();

		List<DLFileEntry> dlFileEntries =
				dlFileEntryLocalService.getFileEntries(repositoryId, folderId, status, start, end, null);
		for (DLFileEntry dlFileEntry : dlFileEntries) {
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
		}


		return gargallDLFileEntries;
	}

	protected GargallDDLRecord getGargallDDLRecord(DDLRecord ddlRecord)
		throws Exception {

		GargallDDLRecord gargallDDLRecord = new GargallDDLRecord();

		gargallDDLRecord.addDynamicElement("uuid", ddlRecord.getUuid());

		Fields fields = ddlRecord.getFields();

		for (String fieldName : fields.getNames()) {
			String fieldValueString = StringPool.BLANK;

			String fieldDataType = GetterUtil.getString(
				ddlRecord.getFieldDataType(fieldName));

			Serializable fieldValue = ddlRecord.getFieldValue(fieldName);

			if (fieldDataType.equals("boolean")) {
				boolean booleanValue = GetterUtil.getBoolean(fieldValue);

				fieldValueString = String.valueOf(booleanValue);
			}
			else if (fieldDataType.equals("date")) {
				fieldValueString = _format.format(fieldValue);
			}
			else if (fieldDataType.equals("double")) {
				double doubleValue = GetterUtil.getDouble(fieldValue);

				fieldValueString = String.valueOf(doubleValue);
			}
			else if (fieldDataType.equals("integer") ||
					 fieldDataType.equals("number")) {

				int intValue = GetterUtil.getInteger(fieldValue);

				fieldValueString = String.valueOf(intValue);
			}
			else {
				fieldValueString = GetterUtil.getString(fieldValue);
			}

			gargallDDLRecord.addDynamicElement(fieldName, fieldValueString);
		}

		return gargallDDLRecord;
	}

	protected GargallDLFileEntry getGargallDLFileEntry(
			DLFileEntry dlFileEntry)
		throws Exception {

		 List<AssetTag> assetTags=AssetTagServiceUtil.getTags(DLFileEntry.class.getName(),
				 dlFileEntry.getClassPK());
		String jsonTags = JSONFactoryUtil.looseSerialize(assetTags);
		 
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