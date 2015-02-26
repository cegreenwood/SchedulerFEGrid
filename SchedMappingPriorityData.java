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

class SchedMappingPriorityData extends AbstractTableModel {

    public static final long serialVersionUID = 1L;

    String headers[] = {"Priority","Attribute","Status"};
    Class columnClasses[] = {String.class, String.class, String.class};

    int columnWidth[] = {60, 200, 100};
    int alignment[] = {JLabel.CENTER, JLabel.LEFT, JLabel.LEFT};

    private Vector<mappingPriority>      mappingPriorityObj;

    private mappingPriority              mMappingPriority;

    // Class constructor.
    public SchedMappingPriorityData() {
        mappingPriorityObj = new Vector<mappingPriority>(10,10);

    }

    public int getRowCount() {
        return mappingPriorityObj.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int nCol) {
        return headers[nCol];
    }

    public Class getColumnClass(int nCol) {
        return columnClasses[nCol];
    }

    public boolean isCellEditable(int nRow, int nCol) {
        return false;
    }

    public Object getValueAt(int nRow, int nCol) {
        if (nRow < 0 || nRow > getRowCount()) {
            return "";
        }
        mMappingPriority = getMappingPriority(nRow);

        switch ( nCol ) {
          case 0:
              return mMappingPriority.getPriority();
          case 1:
              return mMappingPriority.getAttributeName();
          case 2:
              return mMappingPriority.getStatus();
        }
        return "";
    }

    public boolean addMappingPriority(
                             String  mPriority,
                             String  mAttributeName,
                             String  mStatus) {

        mMappingPriority = new mappingPriority(mPriority,
                                               mAttributeName,
                                               mStatus);

        return mappingPriorityObj.add(mMappingPriority);

    }

    public mappingPriority getMappingPriority(int Id) {
        return mappingPriorityObj.get(Id);
    }

    public int sizeMappingPriority() {
        return mappingPriorityObj.size();
    }
    public void removeMappingPriority(String mAttributeName) {
        for (int i1 = 0; i1 < mappingPriorityObj.size(); i1++) {
            mMappingPriority = mappingPriorityObj.get(i1);
            if (mMappingPriority.getAttributeName().equals(mAttributeName)) {
                mappingPriorityObj.removeElementAt(i1);
            }
        }
    }
    public void clearMappingPriority() {
        mappingPriorityObj.clear();
    }

    class mappingPriority {

        private String   mPriority;
        private String   mAttributeName;
        private String   mStatus;

        public mappingPriority(String  priority,
                               String  attributeName,
                               String  status) {
            mPriority = priority;
            mAttributeName = attributeName;
            mStatus = status;
        }
        public String getPriority() {
            return mPriority;
        }
        public String getAttributeName() {
            return mAttributeName;
        }
        public String getStatus() {
            return mStatus;
        }
    }
}
