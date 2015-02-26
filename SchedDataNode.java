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


import java.awt.*;
import java.util.*;
import java.text.*;

import javax.swing.*;
import javax.swing.tree.*;

class SchedDataNode extends DefaultMutableTreeNode {
    public static final long serialVersionUID = 1L;

    private int     m_NodeParentId;
    private int     m_NodeId;
    private String  m_NodeType;
    private int     m_ScreenNo;
    private int     m_ScreenId;
    private String  m_Owner;
    private int     m_IconType;
    private int     m_ConnectId;
    private boolean m_RunningPlan;
    private boolean m_TopPlan;
    private int     m_TabPane;
    private int     m_IsConnected;

    public SchedDataNode(String NodeName, int NodeId) {
        super(NodeName, true);
        m_NodeId = NodeId;
        m_NodeParentId = 0;
        m_NodeType = "F";
        m_ScreenNo = 0;
        m_ScreenId = 0;
        m_IconType = 0;
        m_ConnectId = 0;
        m_RunningPlan = false;
        m_TopPlan = false;
        m_TabPane = 0;
        m_IsConnected = 0;
    }
    public SchedDataNode(String NodeName,
                    int NodeId,
                    int ConnectId,
                    int ScreenNo,
                    int IconType) {
        super(NodeName, true);
        m_NodeId = NodeId;
        m_NodeParentId = 0;
        m_NodeType = "F";
        m_ScreenNo = ScreenNo;
        m_ScreenId = 0;
        m_ConnectId = ConnectId;
        m_IconType = IconType;
        m_RunningPlan = false;
        m_TopPlan = false;
        m_TabPane = 0;
        m_IsConnected = 0;
    }

    public SchedDataNode(String NodeName,
                    String Owner,
                    int    NodeParentId,
                    int    NodeId,
                    String NodeType,
                    int    ConnectId,
                    int    ScreenNo,
                    int    ScreenId,
                    int    IconType) {
        super(NodeName, true);
        m_Owner = Owner;
        m_NodeParentId = NodeParentId;
        m_NodeId = NodeId;
        m_NodeType = NodeType;
        m_ConnectId = ConnectId;
        m_ScreenNo = ScreenNo;
        m_ScreenId = ScreenId;
        m_IconType = IconType;
        m_RunningPlan = false;
        m_TopPlan = false;
        m_TabPane = 0;
        m_IsConnected = 0;
    }
    public String getNodeName() {
        if ( (String)userObject == null) return "   ";
        return (String)userObject;
    }
    public void setNodeName(String NodeName) {
        //super.setUserObject(NodeName);
        setUserObject(NodeName);
    }
    public String getOwner() {
        return m_Owner;
    }
    public String getNodeType() {
        return m_NodeType;
    }
    public int getNodeId() {
        return m_NodeId;
    }
    public int getNodeParentId() {
        return m_NodeParentId;
    }
    public void setNodeParentId(int NodeParentId) {
        m_NodeParentId = NodeParentId;
    }
    public int getScreenNo() {
        return m_ScreenNo;
    }
    public int getScreenId() {
        return m_ScreenId;
    }
    public int getIconType() {
        return m_IconType;
    }
    public int getConnectId() {
        return m_ConnectId;
    }
    public boolean getRunningPlan() {
        return m_RunningPlan;
    }
    public void setRunningPlan(boolean runningPlan) {
        m_RunningPlan = runningPlan;
    }
    public boolean getTopPlan() {
        return m_TopPlan;
    }
    public void setTopPlan(boolean topPlan) {
        m_TopPlan = topPlan;
    }
    public int getTabPane() {
        return m_TabPane;
    }
    public void setTabPane(int tabPane) {
        m_TabPane = tabPane;
    }
    public void setIsConnected(int isConnected) {
        m_IsConnected = isConnected;
    }
    public boolean isConnected() {
        if (m_IsConnected > 0) return true;
        else                   return false;
    }
    public boolean isFullConnected() {
        if (m_IsConnected > 1) return true;
        else                   return false;
    }
}
