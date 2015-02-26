/**
 *
 * Copyright 2015 Blueshire Services Ltd
 *
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
 */

package uk.co.blueshireservices.schedulergrid;

import java.io.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.jdesktop.swingx.JXTable;

class SchedConsumerGroupMappingData extends AbstractTableModel {

    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Attribute", 160, JLabel.LEFT ),
      new SchedColumnData("Value", 160, JLabel.CENTER ),
      new SchedColumnData("Status", 80, JLabel.LEFT )
    };

    private Vector<groupMapping>         groupMappingObj;

    private groupMapping                 mGroupMapping;

    // Class constructor.
    public SchedConsumerGroupMappingData() {
        groupMappingObj = new Vector<groupMapping>(40,10);
    }

    public int getRowCount() {
        return groupMappingObj.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int nCol) {
        return m_columns[nCol].m_title;
    }

    public boolean isCellEditable(int nRow, int nCol) {
        return false;
    }

    public Object getValueAt(int nRow, int nCol) {
        if (nRow < 0 || nRow > getRowCount()) {
            return "";
        }
        mGroupMapping = getGroupMapping(nRow);

        switch ( nCol ) {
          case 0:
              return mGroupMapping.getAttributeName();
          case 1:
              return mGroupMapping.getValue();
          case 2:
              return mGroupMapping.getStatus();
        }
        return "";
    }

    public boolean addGroupMapping(
                             String mAttributeName,
                             String mValue,
                             String mStatus) {

        mGroupMapping = new groupMapping(mAttributeName,
                                         mValue,
                                         mStatus);

        return groupMappingObj.add(mGroupMapping);

    }

    public groupMapping getGroupMapping(int Id) {
        return groupMappingObj.get(Id);
    }

    public int sizeGroupMapping() {
        return groupMappingObj.size();
    }
    public void removeGroupMappingEntry(String mAttribute,
                                        String mValue) {
        for (int i1 = 0; i1 < groupMappingObj.size(); i1++) {
            mGroupMapping = groupMappingObj.get(i1);
            if (mGroupMapping.getAttributeName().equals(mAttribute) &&
                mGroupMapping.getValue().equals(mValue))
            {
                groupMappingObj.removeElementAt(i1);
            }
        }
    }
    public void clearGroupMapping() {
        groupMappingObj.clear();
    }

    class groupMapping {

        private String   mAttributeName;
        private String   mValue;
        private String   mStatus;

        public groupMapping(String attributeName,
                            String value,
                            String Status) {
            mAttributeName = attributeName;
            mValue = value;
            mStatus = Status;
        }
        public String getAttributeName() {
            return mAttributeName;
        }
        public String getValue() {
            return mValue;
        }
        public String getStatus() {
            return mStatus;
        }
    }
}
