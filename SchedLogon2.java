/**
 * Written by Colin Greenwood
 * Date June 2008
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

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;

import java.awt.*;

class SchedLogon2 
{
    public static final long serialVersionUID = 1L;

    private JTextField      userName, passWord;

    // Instance attributes used in this dialog.
    public Scheduler        mParentFrame;
    public SchedGlobalData  mArea;

    public SchedLogon2(Scheduler       parentFrame,
                       SchedGlobalData Area)
    {
        // Save the owner frame in case we need it later.
        mParentFrame = parentFrame;
        mArea = Area;
    }

    public void getConnection(SchedGlobalData.connectionItem lConnectItem) {

        String m_Username = lConnectItem.getAcName();
        String m_Password = lConnectItem.getPassword();

        String m_HostName = "//" + lConnectItem.getHost() + ":" + 
                                   lConnectItem.getPort() + "/" + 
                                   lConnectItem.getDatabase();

        if ((m_Username != null) && (m_Password != null)) {
            SchedDataArea mDataArea = new SchedDataArea(lConnectItem.getName());
            mParentFrame.setDataArea(mDataArea);
            mDataArea.setConnectId(lConnectItem.getConnectionId());

            // System.out.println("1---" + m_HostName + "--");

            mDataArea.GetConnection(m_Username, m_Password, m_HostName, lConnectItem.isSysdba());

            if ((mDataArea.getConnectStatus() == 0) &&
                (mDataArea.getVersionNo() > 0)) {
                lConnectItem.setAutoConnected(true);
                mParentFrame.connectAutoOpen(mDataArea, lConnectItem);
            }
        }
    }
}

