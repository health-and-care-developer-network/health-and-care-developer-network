/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.merge;

import com.hazelcast.core.MapEntry;
import com.hazelcast.impl.base.DataRecordEntry;

public class LatestUpdateMergePolicy implements MergePolicy {
    public static final String NAME = "hz.LATEST_UPDATE";

    public Object merge(String mapName, MapEntry mergingEntry, MapEntry existingEntry) {
        DataRecordEntry mergingDataEntry = (DataRecordEntry) mergingEntry;
        DataRecordEntry existingDataEntry = (DataRecordEntry) existingEntry;
        if (existingEntry == null || !existingDataEntry.hasValue()) {
            return mergingDataEntry.getValueData();
        }
        return (mergingEntry.getLastUpdateTime() > existingEntry.getLastUpdateTime())
                ? mergingDataEntry.getValueData()
                : existingDataEntry.getValueData();
    }
}

