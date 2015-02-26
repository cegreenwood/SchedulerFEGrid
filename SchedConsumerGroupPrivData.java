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

class SchedConsumerGroupPrivData extends AbstractTableModel {

    public static final long serialVersionUID = 1L;

    static final public SchedColumnData m_columns[] = {
      new SchedColumnData("Grantee", 200, JLabel.LEFT ),
      new SchedColumnData("Grant Option", 100, JLabel.LEFT ),
      new SchedColumnData("Initial Group", 100, JLabel.LEFT )
    };

    private Vector<consumerPriv>         consumerPrivObj;

    private consumerPriv                 mConsumerPriv;

    // Class constructor.
    public SchedConsumerGroupPrivData() {
        consumerPrivObj = new Vector<consumerPriv>(20,10);
    }

    public int getRowCount() {
        return consumerPrivObj.size();
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
        mConsumerPriv = getConsumerPriv(nRow);

        switch ( nCol ) {
          case 0:
              return mConsumerPriv.getGrantee();
          case 1:
              return mConsumerPriv.getGrantOption();
          case 2:
              return mConsumerPriv.getInitialGroup();
        }
        return "";
    }

    public boolean addConsumerPriv(
                             String mGrantee,
                             String mGrantOption,
                             String mInitialGroup) {

        mConsumerPriv = new consumerPriv(mGrantee,
                                         mGrantOption,
                                         mInitialGroup);

        return consumerPrivObj.add(mConsumerPriv);

    }

    public consumerPriv getConsumerPriv(int Id) {
        return consumerPrivObj.get(Id);
    }

    public int sizeGroupMapping() {
        return consumerPrivObj.size();
    }
    public void removeConsumerPrivEntry(String mGrantee) {
        for (int i1 = 0; i1 < consumerPrivObj.size(); i1++) {
            mConsumerPriv = consumerPrivObj.get(i1);
            if (mConsumerPriv.getGrantee().equals(mGrantee))
            {
                consumerPrivObj.removeElementAt(i1);
            }
        }
    }
    public void clearConsumerPriv() {
        consumerPrivObj.clear();
    }

    class consumerPriv {

        private String   mGrantee;
        private String   mGrantOption;
        private String   mInitialGroup;

        public consumerPriv(String grantee,
                            String grantOption,
                            String initialGroup) {
            mGrantee = grantee;
            mGrantOption = grantOption;
            mInitialGroup = initialGroup;
        }
        public String getGrantee() {
            return mGrantee;
        }
        public String getGrantOption() {
            return mGrantOption;
        }
        public String getInitialGroup() {
            return mInitialGroup;
        }
    }
}

