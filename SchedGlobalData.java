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

import java.io.*;
import java.awt.*;
import java.sql.SQLException;

import java.util.*;
import java.text.SimpleDateFormat;

class SchedGlobalData {

    private  static final int          LINE_COLOR_NO = 20;
    private  static final int          BACKGROUND_COLOR_NO = 21;
    private  static final int          BUTTON_COLOR_NO = 22;

    private Calendar                   cal;
    private SimpleDateFormat           sdf_short;
    private int                        mFramePointX;
    private int                        mFramePointY;
    private int                        mRunningChainId, mRunningJobId;

    static SchedScreenArea.PaneObject  mPane;
    static String                      mText;
    static Scheduler                   mScheduler;

    private JobsRunningItem            mJobsRunningItem;
    private ChainsRunningItem          mChainsRunningItem;

    private Vector<connectionItem>     connectionObj;
    private Vector<screenColor>        colorObj;
    private Vector<screenSecurity>     securityObj;
    private Vector<screenPopup>        popupObj;
    private Vector<screenCombo>        comboObj;
    private Vector<paramItem>          paramObj;
    private Vector<SchedDataArea>      dataAreaObj;

    public SchedGlobalData() {

        cal = Calendar.getInstance();
        sdf_short = new SimpleDateFormat("yyyy-MM-dd");

        colorObj = new Vector<screenColor>(10,10);
        securityObj = new Vector<screenSecurity>(40,10);
        popupObj = new Vector<screenPopup>(10,10);
        comboObj = new Vector<screenCombo>(40,10);
        paramObj = new Vector<paramItem>(5,5);
        connectionObj = new Vector<connectionItem>(5,5);
        dataAreaObj = new Vector<SchedDataArea>(5,5);

        JobLogVector           = new Vector<JobLogItem>(100, 20);
        JobDetLogVector        = new Vector<JobDetLogItem>(100, 20);

        WindowLogVector        = new Vector<WindowLogItem>(50, 10);
        WindowDetLogVector     = new Vector<WindowDetLogItem>(50, 10);

        JobsRunningVector      = new Vector<JobsRunningItem>(50, 10);
        ChainsRunningVector    = new Vector<ChainsRunningItem>(50, 10);
    }

    public void clearVectors() {

        for (int i = 0; i < comboObj.size(); i++) {
            screenCombo mScreenCombo = comboObj.get(i);
            if (! mScreenCombo.getFixedEntry()) {
                comboObj.removeElementAt(i);
                i = i - 1;
            }
        }

        JobLogVector.clear();
        JobDetLogVector.clear();
        WindowLogVector.clear();
        WindowDetLogVector.clear();
        JobsRunningVector.clear();
        ChainsRunningVector.clear();
    }

    public void clearJobLogVector() {
        JobLogVector.clear();
    }
    public void clearDetJobLogVector() {
        JobDetLogVector.clear();
    }
    public void clearWindowLogVector() {
        WindowLogVector.clear();
    }
    public void clearDetWindowLogVector() {
        WindowDetLogVector.clear();
    }
    public void clearJobsRunningVector() {
        JobsRunningVector.clear();
    }
    public void clearChainsRunningVector() {
        ChainsRunningVector.clear();
    }

    public void dropDataAreaObj(int mConnectId) {

        for (int i = 0; i < dataAreaObj.size(); i++) {
            SchedDataArea mDataArea = dataAreaObj.get(i);
            if (mDataArea.getConnectId() == mConnectId) {
                dataAreaObj.removeElementAt(i);
                break;
            }
        }
    }
    public SchedDataArea getDataAreaObjById(int mConnectId) {

        for (int i = 0; i < dataAreaObj.size(); i++) {
            SchedDataArea mDataArea = dataAreaObj.get(i);

            if (mDataArea.getConnectId() == mConnectId)
                return mDataArea;
        }
        return null;
    }
    public int sizeDataAreaObj() {
        return dataAreaObj.size();
    }
    public SchedDataArea getDataAreaObj(int m_Row) {
        return dataAreaObj.get(m_Row);
    }
    public boolean addDataAreaObj(SchedDataArea m_DataArea) {
        return dataAreaObj.add(m_DataArea);
    }

    public static SchedScreenArea.PaneObject getPane() {
        return mPane;
    }
    public static void setPane(SchedScreenArea.PaneObject gPane) {
        mPane = gPane;
    }
    public static String getText() {
        return mText;
    }
    public static void setText(String gText) {
        mText = gText;
    }
    public static void setScheduler(Scheduler m_Scheduler) {
        mScheduler = m_Scheduler;
    }
    public static Scheduler getScheduler() {
        return mScheduler;
    }

    public void setFramePosition(int mX, int mY) {
        mFramePointX = mX;
        mFramePointY = mY;
    }
    public int getFramePointX() {
        return mFramePointX;
    }
    public int getFramePointY() {
        return mFramePointY;
    }

    public int sizeColorObj() {
        return colorObj.size();
    }
    public boolean addColorObj(screenColor m_Color) {
        return colorObj.add(m_Color);
    }
    public screenColor getColorObj(int m_Row) {
        return colorObj.get(m_Row);
    }
    public Color getScreenColor(int m_ColorNo) {
        for (int i3 = 0; i3 < colorObj.size(); i3++) {
            screenColor mScreenColor = colorObj.get(i3);
            if (mScreenColor.getColorNo() == m_ColorNo) {
                return mScreenColor.getColor();
            }
        }
        return null;
    }

    public Color getLineColor() {
        return getScreenColor(LINE_COLOR_NO);
    }
    public Color getBackgroundColor() {
        return getScreenColor(BACKGROUND_COLOR_NO);
    }
    public Color getButtonColor() {
        return getScreenColor(BUTTON_COLOR_NO);
    }

    public Vector getColorSets() {
        Vector<String> vr = new Vector<String>( sizeColorObj() );
        vr.clear();

        for (int i = 0; i < sizeColorObj(); i++) {
            screenColor cs = colorObj.get(i);
            vr.add(cs.getColorDesc());
        }
        return vr;
    }

    class screenColor {


        private int   m_ColorNo;
        private Color m_Color;
        private String m_Description;

        public screenColor(int colorNo,
                           int m_red,
                           int m_green,
                           int m_blue,
                           int m_alpha,
                           String m_desc) {

            m_ColorNo = colorNo;
            m_Color = new Color(m_red, m_green, m_blue, m_alpha);
            m_Description = m_desc;
        }
        public Color getColor() {
            return m_Color;
        }
        public int getColorNo() {
            return m_ColorNo;
        }
        public String getColorDesc() {
            return m_Description;
        }
    }

    public boolean addSecurityObj(screenSecurity m_Security) {
        return securityObj.add(m_Security);
    }

    public boolean blockedOption(int m_ScreenNo, int m_OptionNo) {
        for (int i = 0; i < securityObj.size(); i++) {
            screenSecurity sc1 = securityObj.get(i);
            if ( (sc1.getScreenNo() == m_ScreenNo) && (sc1.getOptionNo() == m_OptionNo) )
            {
                return true;
            }
        }
        return false;
    }

    class screenSecurity {
        private int m_ScreenNo;
        private int m_OptionNo;

        public screenSecurity(int screenNo,
                              int optionNo) {
            m_ScreenNo = screenNo;
            m_OptionNo = optionNo;
        }
        public int getScreenNo() {
            return m_ScreenNo;
        }
        public int getOptionNo() {
            return m_OptionNo;
        }

    }

    public boolean addParamObj(paramItem mParamItem) {
        return paramObj.add(mParamItem);
    }
    public boolean findParamItem(int mParamId) {
        for (int i = 0; i < paramObj.size(); i++) {
            paramItem mParamItem = paramObj.get(i);
            if (mParamItem.getParamId() == mParamId)
                return true;
        }
        return false;
    }
    public String getParamValue(int paramId) {
        for (int i = 0; i < paramObj.size(); i++) {
            paramItem mParamItem = paramObj.get(i);
            if (mParamItem.getParamId() == paramId)
                return mParamItem.getParamValue();
        }
        return null;
    }
    class paramItem {
        private int    m_ParamId;
        private String m_ParamValue;

        public paramItem(int    paramId,
                         String paramValue) {
            m_ParamId = paramId;
            m_ParamValue = paramValue;
        }
        public int getParamId() {
            return m_ParamId;
        }
        public String getParamValue() {
            return m_ParamValue;
        }
    }

    public boolean addPopupObj(screenPopup m_Popup) {
        return popupObj.add(m_Popup);
    }
    public int sizePopupObj() {
        return popupObj.size();
    }

    public screenPopup getPopupObj(int m_ComboId) {
        screenPopup mScreenPopup = null;
        for (int i = 0; i < popupObj.size(); i++) {
            mScreenPopup = popupObj.get(i);
            if (mScreenPopup.getComboId() == m_ComboId)
            {
                 break;
            }
        }
        return mScreenPopup;
    }
    class screenPopup {
        private int        m_ComboId;
        private String     m_PopupDesc;
        private String     m_PopupType;
        private String     m_PopupKey;

        public screenPopup(int     comboId,
                           String  popupDesc,
                           String  popupType,
                           String  popupKey) {
            m_ComboId = comboId;
            m_PopupDesc = popupDesc;
            m_PopupType = popupType;
            m_PopupKey = popupKey;
        }
        public int getComboId() {
            return m_ComboId;
        }
        public String getPopupDesc() {
            return m_PopupDesc;
        }
        public String getPopupType() {
            return m_PopupType;
        }
        public String getPopupKey() {
            return m_PopupKey;
        }
    }

    public boolean addComboObj(screenCombo m_Combo) {
        return comboObj.add(m_Combo);
    }
    public void dropComboObj(int m_ComboId,
                             String m_ObjectName) {
        for (int i = 0; i < comboObj.size(); i++) {
            screenCombo mScreenCombo = comboObj.get(i);
            if ((mScreenCombo.getComboId() == m_ComboId) &&
                (mScreenCombo.getColumnText().equals(m_ObjectName)))
            {
                 comboObj.removeElementAt(i);
            }
        }
    }

    public int countComboObj(int m_ComboId) {
        int mCount = 0;
        for (int i = 0; i < comboObj.size(); i++) {
            screenCombo mScreenCombo = comboObj.get(i);
            if (mScreenCombo.getComboId() == m_ComboId)
            {
                 mCount = mCount + 1;
            }
        }
        return mCount;
    }

    public void dropComboObj(int m_ComboId,
                             String m_Owner,
                             String m_ObjectName) {
        for (int i = 0; i < comboObj.size(); i++) {
            screenCombo mScreenCombo = comboObj.get(i);
            if ((mScreenCombo.getComboId() == m_ComboId) &&
                (mScreenCombo.getOwner().equals(m_Owner)) && 
                (mScreenCombo.getColumnText().equals(m_ObjectName)))
            {
                 comboObj.removeElementAt(i);
            }
        }
    }
    public int sizeComboObj() {
        return comboObj.size();
    }
    public screenCombo getComboObj(int m_Row) {
        return comboObj.get(m_Row);
    }

    public screenCombo getComboObj(int m_ComboId, int m_ItemId) {
        screenCombo mScreenCombo = null;
        for (int i = 0; i < comboObj.size(); i++) {
            mScreenCombo = comboObj.get(i);
            if ((mScreenCombo.getComboId() == m_ComboId) &&
                (mScreenCombo.getItemId() == m_ItemId))
            {
                 break;
            }
        }
        return mScreenCombo;
    }

    public int getMaxItemId(int comboId) {
        int mMaxId = 0;
        for (int i = 0; i < comboObj.size(); i++) {
            screenCombo mScreenCombo = comboObj.get(i);
            if (mScreenCombo.getItemId() > mMaxId)
                mMaxId = mScreenCombo.getItemId();
        }
        return mMaxId;
    }

    class screenCombo {
        private int        m_ComboId;
        private int        m_ItemId;
        private boolean    m_FixedEntry;
        private String     m_Owner;
        private String     m_ColumnText;
        private int        m_ItemValue;

        public screenCombo(int     comboId,
                           int     itemId,
                           String  columnText,
                           int     itemValue,
                           boolean fixedEntry) {
            m_ComboId = comboId;
            m_ItemId = itemId;
            m_ColumnText = columnText;
            m_ItemValue = itemValue;
            m_FixedEntry = fixedEntry;
        }
        public screenCombo(int comboId,
                           int itemId,
                           String columnText) {
            m_ComboId = comboId;
            m_ItemId = itemId;
            m_ColumnText = columnText;
            m_ItemValue = 0;
            m_FixedEntry = false;
        }
        public screenCombo(int comboId,
                           int itemId,
                           String owner,
                           String columnText) {
            m_ComboId = comboId;
            m_ItemId = itemId;
            m_Owner = owner;
            m_ColumnText = columnText;
            m_ItemValue = 0;
            m_FixedEntry = false;
        }
        public int getComboId() {
            return m_ComboId;
        }
        public int getItemId() {
            return m_ItemId;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getColumnText() {
            return m_ColumnText;
        }
        public int getItemValue() {
            return m_ItemValue;
        }
        public boolean getFixedEntry() {
            return m_FixedEntry;
        }
    }

    public boolean addConnectionObj(connectionItem mConnectionItem) {
        return connectionObj.add(mConnectionItem);
    }

    public boolean dropConnection(int  mConnectionId) {
        for (int i = 0; i < connectionObj.size(); i++) {
            connectionItem lConnectionItem = connectionObj.get(i);
            if (lConnectionItem.getConnectionId() == mConnectionId) {
                connectionObj.removeElementAt(i);
                return true;
            }
        }
        return false;
    }
    public int getNextConnectionSeq() {
        int mConnectionId = 0;
        for (int i = 0; i < connectionObj.size(); i++) {
            connectionItem lConnectionItem = connectionObj.get(i);
            if (lConnectionItem.getConnectionId() >= mConnectionId)
                mConnectionId = lConnectionItem.getConnectionId() + 1;
        }
        return mConnectionId;
    }
    public void setPassword(String m_Name,
                            String m_Password) {
        for (int i = 0; i < connectionObj.size(); i++) {
            connectionItem lConnectionItem = connectionObj.get(i);
            if (lConnectionItem.getName().equals(m_Name))
                lConnectionItem.setPassword(m_Password);
        }
    }
    public int sizeConnectionObj() {
        return connectionObj.size();
    }
    public connectionItem getConnectionObj(int m_Row) {
        return connectionObj.get(m_Row);
    }
    public boolean isValidConnectionName(String  mName,
                                         int     mConnectionId) {
        boolean mValid = true;
        for (int i = 0; i < connectionObj.size(); i++) {
            connectionItem lConnectionItem = connectionObj.get(i);
            if ((lConnectionItem.getConnectionId() != mConnectionId) && (lConnectionItem.getName().equals(mName))) {
                mValid = false;
                break;
            }
        }
        return mValid;
    }
    public connectionItem getConnectionObjById(int m_ConnectionId) {
        for (int i = 0; i < connectionObj.size(); i++) {
            connectionItem lConnectionItem = connectionObj.get(i);
            if (lConnectionItem.getConnectionId() == m_ConnectionId)
                return lConnectionItem;
        }
        return null;
    }

    class connectionItem {

        public connectionItem(String   name,
                              String   acName,
                              String   password,
                              String   host,
                              String   port,
                              String   database,
                              boolean  sysdba,
                              boolean  savePassword,
                              boolean  autoConnect,
                              String   runWhereStmt,
                              String   stdLogWhereStmt,
                              String   detLogWhereStmt,
                              String   chainWhereStmt,
                              String   stdWinWhereStmt,
                              String   detWinWhereStmt) {

            m_Name = name;
            m_AcName = acName;
            m_Password = password;
            m_Host = host;
            m_Port = port;
            m_Database = database;
            m_Sysdba = sysdba;
            m_SavePassword = savePassword;
            m_AutoConnect = autoConnect;
            m_AutoConnected = false;
            m_Connected = 0;
            m_RunWhereStmt    = runWhereStmt;
            m_StdLogWhereStmt = stdLogWhereStmt;
            m_DetLogWhereStmt = detLogWhereStmt;
            m_ChainWhereStmt  = chainWhereStmt;
            m_StdWinWhereStmt = stdWinWhereStmt;
            m_DetWinWhereStmt = detWinWhereStmt;
            m_ErrorFlag = false;
        }
        public String getName() {
            return m_Name;
        }
        public String getPassword() {
            return m_Password;
        }
        public String getAcName() {
            return m_AcName;
        }
        public String getHost() {
            return m_Host;
        }
        public String getPort() {
            return m_Port;
        }
        public String getDatabase() {
            return m_Database;
        }
        public boolean isSysdba() {
            return m_Sysdba;
        }
        public boolean isSavePassword() {
            return m_SavePassword;
        }

        public boolean isAutoConnect() {
            return m_AutoConnect;
        }
        public boolean isAutoConnected() {
            return m_AutoConnected;
        }

        public boolean isConnected() {
            if (m_Connected > 0) return true;
            else                 return false;
        }
        public boolean isFullConnected() {
            if (m_Connected > 1) return true;
            else                 return false;
        }
        public int getConnectionId() {
            return m_ConnectionId;
        }

        public void setName(String name) {
            m_Name = name;
        }
        public void setPassword(String password) {
            m_Password = password;
        }
        public void setAcName(String acName) {
            m_AcName = acName;
        }
        public void setHost(String host) {
            m_Host = host;
        }
        public void setPort(String port) {
            m_Port = port;
        }
        public void setDatabase(String database) {
            m_Database = database;
        }
        public void setSysdba(boolean sysdba) {
            m_Sysdba = sysdba;
        }
        public void setSavePassword(boolean savePassword) {
            m_SavePassword = savePassword;
        }
        public void setAutoConnect(boolean autoConnect) {
            m_AutoConnect = autoConnect;
        }
        public void setAutoConnected(boolean autoConnected) {
            m_AutoConnected = autoConnected;
        }
        public void setConnectionId(int connectionId) {
            m_ConnectionId = connectionId;
        }
        public void setConnected() {
            m_Connected = 1;
        }
        public void setFullConnected() {
            m_Connected = 2;
        }
        public void setDisconnected() {
            m_Connected = 0;
        }
        public String getRunWhereStmt() {
            return m_RunWhereStmt;
        }
        public String getStdLogWhereStmt() {
            return m_StdLogWhereStmt;
        }
        public String getDetLogWhereStmt() {
            return m_DetLogWhereStmt;
        }
        public String getChainWhereStmt() {
            return m_ChainWhereStmt;
        }
        public String getStdWinWhereStmt() {
            return m_StdWinWhereStmt;
        }
        public String getDetWinWhereStmt() {
            return m_DetWinWhereStmt;
        }
        public void setRunWhereStmt(String runWhereStmt) {
            m_RunWhereStmt = runWhereStmt;
        }
        public void setStdLogWhereStmt(String stdLogWhereStmt) {
            m_StdLogWhereStmt = stdLogWhereStmt;
        }
        public void setDetLogWhereStmt(String detLogWhereStmt) {
            m_DetLogWhereStmt = detLogWhereStmt;
        }
        public void setChainWhereStmt(String chainWhereStmt) {
            m_ChainWhereStmt = chainWhereStmt;
        }
        public void setStdWinWhereStmt(String stdWinWhereStmt) {
            m_StdWinWhereStmt = stdWinWhereStmt;
        }
        public void setDetWinWhereStmt(String detWinWhereStmt) {
            m_DetWinWhereStmt = detWinWhereStmt;
        }
        public boolean getErrorFlag() {
            return m_ErrorFlag;
        }
        public void setErrorFlag(boolean errorFlag) {
            m_ErrorFlag = errorFlag;
        }

        private int        m_ConnectionId;
        private String     m_Name;
        private String     m_Password;
        private String     m_AcName;
        private String     m_Host;
        private String     m_Port;
        private String     m_Database;
        private boolean    m_Sysdba;
        private boolean    m_SavePassword;
        private boolean    m_AutoConnect;
        private boolean    m_AutoConnected;
        private int        m_Connected;
        private String     m_RunWhereStmt;
        private String     m_StdLogWhereStmt;
        private String     m_DetLogWhereStmt;
        private String     m_ChainWhereStmt;
        private String     m_StdWinWhereStmt;
        private String     m_DetWinWhereStmt;
        private boolean    m_ErrorFlag;
    }


    public boolean getJobLogData(String  mCurrentDate) {

        boolean mErrorFound = false;
        boolean[] booleanArray = new boolean[sizeDataAreaObj()];
        int[] intArray = new int[sizeDataAreaObj()];

        clearJobLogVector();

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            intArray[i1] = 0;
            booleanArray[i1] = true;

            SchedDataArea lDataArea = getDataAreaObj(i1);

            // System.out.println( "1. " + lDataArea.getDatabaseName() + "---" + mCurrentDate);

            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            mConnectionItem.setErrorFlag(false);
            try {
                lDataArea.SetupJobLogData(mCurrentDate, mConnectionItem.getStdLogWhereStmt());
            }
            catch(SQLException e) {
                mConnectionItem.setErrorFlag(true);
                mErrorFound = true;
            }
            if ( ! mConnectionItem.getErrorFlag() ) {
                booleanArray[i1] = lDataArea.getNextJobLogRow();
            }
        }

        boolean mContinue = true;
        do {
            mContinue = false;
            String mCheckDate = "2300-01-01 00:00:00";
            int lCursorNo = -1;
            for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
                SchedDataArea lDataArea = getDataAreaObj(i1);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    if (booleanArray[i1]) {
                        String lJobLogDate = lDataArea.getJobLogDate();

                        if (lJobLogDate != null) {
                            if (lJobLogDate.compareTo(mCheckDate) < 0) {
                                lCursorNo = i1;
                                mCheckDate = lJobLogDate;
                            }
                        }
                    }
                }
            }
            if (lCursorNo > -1) {
                mContinue = true;
                SchedDataArea lDataArea = getDataAreaObj(lCursorNo);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    lDataArea.getJobLogData(this);
                    booleanArray[lCursorNo] = lDataArea.getNextJobLogRow();
                }
            }

        } while (mContinue);

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            SchedDataArea lDataArea = getDataAreaObj(i1);
            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            if ( ! mConnectionItem.getErrorFlag() ) {
                lDataArea.closeJobLogCursor();
            }
        }
        return mErrorFound;
    }

    public boolean addJobLog(JobLogItem m_JobLogItem) {
        return JobLogVector.add(m_JobLogItem);
    }

    public JobLogItem getJobLog(int JobLogNo) {
        JobLogItem m_JobLogItem = JobLogVector.get(JobLogNo);
        return m_JobLogItem;
    }

    public JobLogItem getJobLogId(int JobLogId) {

        JobLogItem m_JobLogItem = null;
        for (int i = 0; i < JobLogVector.size(); i++) {

            m_JobLogItem = JobLogVector.get(i);

            if (m_JobLogItem.getLogId() == JobLogId) {
                break;
            }
        }
        return m_JobLogItem;
    }

    public int JobLogSize() {
        return JobLogVector.size();
    }

    class JobLogItem {

        public JobLogItem(String  Database,
                          int     LogId,
                          String  LogDate,
                          String  Owner,
                          String  JobName,
                          String  JobClass,
                          String  Operation,
                          String  Status,
                          String  UserName,
                          String  ClientId,
                          String  GlobalUid,
                          String  AdditionalInfo,
                          String  JobSubName,
                          String  Destination,
                          String  CredentialOwner,
                          String  CredentialName,
                          String  DestinationOwner) {
            m_Database = Database;
            m_LogId = LogId;
            m_LogDate = LogDate;
            m_Owner = Owner;
            m_JobName = JobName;
            m_JobClass = JobClass;
            m_Operation = Operation;
            m_Status = Status;
            m_UserName = UserName;
            m_ClientId = ClientId;
            m_GlobalUid = GlobalUid;
            m_AdditionalInfo = AdditionalInfo;
            m_JobSubName = JobSubName;
            m_Destination = Destination;
            m_CredentialOwner = CredentialOwner;
            m_CredentialName = CredentialName;
            m_DestinationOwner = DestinationOwner;
        }

        public String getDatabase() {
            return m_Database;
        }
        public int getLogId() {
            return m_LogId;
        }
        public String getLogDate() {
            return m_LogDate;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getJobName() {
            return m_JobName;
        }
        public String getJobClass() {
            return m_JobClass;
        }
        public String getOperation() {
            return m_Operation;
        }
        public String getStatus() {
            return m_Status;
        }
        public String getUserName() {
            return m_UserName;
        }
        public String getClientId() {
            return m_ClientId;
        }
        public String getGlobalUid() {
            return m_GlobalUid;
        }
        public String getAdditionalInfo() {
            return m_AdditionalInfo;
        }
        public String getJobSubName() {
            return m_JobSubName;
        }
        public String getDestination() {
            return m_Destination;
        }
        public String getCredentialOwner() {
            return m_CredentialOwner;
        }
        public String getCredentialName() {
            return m_CredentialName;
        }
        public String getDestinationOwner() {
            return m_DestinationOwner;
        }

        private String  m_Database;
        private int     m_LogId;
        private String  m_LogDate;
        private String  m_Owner;
        private String  m_JobName;
        private String  m_JobClass;
        private String  m_Operation;
        private String  m_Status;
        private String  m_UserName;
        private String  m_ClientId;
        private String  m_GlobalUid;
        private String  m_AdditionalInfo;
        private String  m_JobSubName;
        private String  m_Destination;
        private String  m_CredentialOwner;
        private String  m_CredentialName;
        private String  m_DestinationOwner;
    }

    public boolean getJobDetLogData(String  mCurrentDate) {

        boolean mErrorFound = false;
        boolean[] booleanArray = new boolean[sizeDataAreaObj()];
        int[] intArray = new int[sizeDataAreaObj()];

        clearDetJobLogVector();

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            intArray[i1] = 0;
            booleanArray[i1] = true;

            SchedDataArea lDataArea = getDataAreaObj(i1);

            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            mConnectionItem.setErrorFlag(false);
            try {
                lDataArea.SetupJobDetailsLogData(mCurrentDate, mConnectionItem.getDetLogWhereStmt());
            }
            catch(SQLException e) {
                mConnectionItem.setErrorFlag(true);
                mErrorFound = true;
            }
            if ( ! mConnectionItem.getErrorFlag() ) {
                booleanArray[i1] = lDataArea.getNextJobDetailsLogRow();
            }
        }

        boolean mContinue = true;
        do {
            mContinue = false;
            String mCheckDate = "2300-01-01 00:00:00";
            int lCursorNo = -1;
            for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
                SchedDataArea lDataArea = getDataAreaObj(i1);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    if (booleanArray[i1]) {
                        String lJobLogDate = lDataArea.getJobDetailsLogDate();

                        if (lJobLogDate != null) {
                            if (lJobLogDate.compareTo(mCheckDate) < 0) {
                                lCursorNo = i1;
                                mCheckDate = lJobLogDate;
                            }
                        }
                    }
                }
            }
            if (lCursorNo > -1) {
                mContinue = true;
                SchedDataArea lDataArea = getDataAreaObj(lCursorNo);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    lDataArea.getJobDetailsLogData(this);
                    booleanArray[lCursorNo] = lDataArea.getNextJobDetailsLogRow();
                }
            }

        } while (mContinue);

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            SchedDataArea lDataArea = getDataAreaObj(i1);
            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            if ( ! mConnectionItem.getErrorFlag() ) {
                lDataArea.closeJobDetailsLogCursor();
            }
        }
        return mErrorFound;
    }

    public boolean addJobRunDetLog(JobDetLogItem m_JobDetLogItem) {
        return JobDetLogVector.add(m_JobDetLogItem);
    }

    public JobDetLogItem getJobDetLogId(int JobLogId) {

        JobDetLogItem m_JobDetLogItem = null;
        for (int i = 0; i < JobDetLogVector.size(); i++) {

            m_JobDetLogItem = JobDetLogVector.get(i);

            if (m_JobDetLogItem.getLogId() == JobLogId) {
                break;
            }
        }
        return m_JobDetLogItem;
    }

    public JobDetLogItem getJobDetLog(int mJobLog) {
        JobDetLogItem m_JobDetLogItem = JobDetLogVector.get(mJobLog);
        return m_JobDetLogItem;
    }

    public int jobDetLogSize() {
        return JobDetLogVector.size();
    }

    class JobDetLogItem {

        public JobDetLogItem(String  Database,
                             int     LogId,
                             String  LogDate,
                             String  Owner,
                             String  JobName,
                             String  Status,
                             int     ErrorNo,
                             String  ReqStartDate,
                             String  ActualStartDate,
                             String  RunDuration,
                             int     InstanceId,
                             String  SessionId,
                             String  SlavePid,
                             String  CpuUsed,
                             String  AdditionalInfo,
                             String  JobSubName,
                             String  Destination,
                             String  CredentialOwner,
                             String  CredentialName,
                             String  DestinationOwner) {
            m_Database = Database;
            m_LogId = LogId;
            m_LogDate = LogDate;
            m_Owner = Owner;
            m_JobName = JobName;
            m_Status = Status;
            m_ErrorNo = ErrorNo;
            m_ReqStartDate = ReqStartDate;
            m_ActualStartDate = ActualStartDate;
            m_RunDuration = RunDuration;
            m_InstanceId = InstanceId;
            m_SessionId = SessionId;
            m_SlavePid = SlavePid;
            m_CpuUsed = CpuUsed;
            m_AdditionalInfo = AdditionalInfo;
            m_JobSubName = JobSubName;
            m_Destination = Destination;
            m_CredentialOwner = CredentialOwner;
            m_CredentialName = CredentialName;
            m_DestinationOwner = DestinationOwner;
        }

        public String getDatabase() {
            return m_Database;
        }
        public int getLogId() {
            return m_LogId;
        }
        public String getLogDate() {
            return m_LogDate;
        }
        public String getLogDateOnly() {
            return m_LogDate.substring(0,10);
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getJobName() {
            return m_JobName;
        }
        public String getStatus() {
            return m_Status;
        }
        public int getErrorNo() {
            return m_ErrorNo;
        }
        public String getReqStartDate() {
            return m_ReqStartDate;
        }
        public String getActualStartDate() {
            return m_ActualStartDate;
        }

        public String getRunDuration() {
            return m_RunDuration;
        }
        public int getInstanceId() {
            return m_InstanceId;
        }
        public String getSessionId() {
            return m_SessionId;
        }
        public String getSlavePid() {
            return m_SlavePid;
        }
        public String getCpuUsed() {
            return m_CpuUsed;
        }
        public String getAdditionalInfo() {
            return m_AdditionalInfo;
        }
        public String getJobSubName() {
            return m_JobSubName;
        }
        public String getDestination() {
            return m_Destination;
        }
        public String getCredentialOwner() {
            return m_CredentialOwner;
        }
        public String getCredentialName() {
            return m_CredentialName;
        }
        public String getDestinationOwner() {
            return m_DestinationOwner;
        }

        private String  m_Database;
        private int     m_Id;
        private int     m_LogId;
        private String  m_LogDate;
        private String  m_Owner;
        private String  m_JobName;
        private String  m_Status;
        private int     m_ErrorNo;
        private String  m_ReqStartDate;
        private String  m_ActualStartDate;
        private String  m_RunDuration;
        private int     m_InstanceId;
        private String  m_SessionId;
        private String  m_SlavePid;
        private String  m_CpuUsed;
        private String  m_AdditionalInfo;
        private String  m_JobSubName;
        private String  m_Destination;
        private String  m_CredentialOwner;
        private String  m_CredentialName;
        private String  m_DestinationOwner;

    }

    public boolean getWindowLogData(String  mCurrentDate) {

        boolean mErrorFound = false;
        boolean[] booleanArray = new boolean[sizeDataAreaObj()];
        int[] intArray = new int[sizeDataAreaObj()];

        clearWindowLogVector();

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            intArray[i1] = 0;
            booleanArray[i1] = true;

            SchedDataArea lDataArea = getDataAreaObj(i1);

            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            mConnectionItem.setErrorFlag(false);
            try {
                lDataArea.SetupWindowLogData(mCurrentDate, mConnectionItem.getStdWinWhereStmt());
            }
            catch(SQLException e) {
                mConnectionItem.setErrorFlag(true);
                mErrorFound = true;
            }
            if ( ! mConnectionItem.getErrorFlag() ) {
                booleanArray[i1] = lDataArea.getNextWindowLogRow();
            }
        }

        boolean mContinue = true;
        do {
            mContinue = false;
            String mCheckDate = "2300-01-01 00:00:00";
            int lCursorNo = -1;
            for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
                SchedDataArea lDataArea = getDataAreaObj(i1);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    if (booleanArray[i1]) {
                        String lWindowLogDate = lDataArea.getWindowLogDate();

                        if (lWindowLogDate != null) {
                            if (lWindowLogDate.compareTo(mCheckDate) < 0) {
                                lCursorNo = i1;
                                mCheckDate = lWindowLogDate;
                            }
                        }
                    }
                }
            }
            if (lCursorNo > -1) {
                mContinue = true;
                SchedDataArea lDataArea = getDataAreaObj(lCursorNo);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    lDataArea.getWindowLogData(this);
                    booleanArray[lCursorNo] = lDataArea.getNextWindowLogRow();
                }
            }

        } while (mContinue);

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            SchedDataArea lDataArea = getDataAreaObj(i1);
            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            if ( ! mConnectionItem.getErrorFlag() ) {
                lDataArea.closeWindowLogCursor();
            }
        }
        return mErrorFound;
    }

    public boolean addWindowLog(WindowLogItem m_WindowLogItem) {
        return WindowLogVector.add(m_WindowLogItem);
    }

    public WindowLogItem getWindowLog(int mWindowLogNo) {
        WindowLogItem m_WindowLogItem = WindowLogVector.get(mWindowLogNo);
        return m_WindowLogItem;
    }

    public WindowLogItem getWindowLogId(int WindowLogId) {

        WindowLogItem m_WindowLogItem = null;
        for (int i = 0; i < WindowLogVector.size(); i++) {

            m_WindowLogItem = WindowLogVector.get(i);

            if (m_WindowLogItem.getLogId() == WindowLogId) {
                break;
            }
        }
        return m_WindowLogItem;
    }

    public int windowLogSize() {
        return WindowLogVector.size();
    }

    class WindowLogItem {

        public WindowLogItem(String  Database,
                             int     LogId,
                             String  LogDate,
                             String  WindowName,
                             String  Operation,
                             String  Status,
                             String  UserName,
                             String  ClientId,
                             String  GlobalUid,
                             String  AdditionalInfo) {
            m_Database = Database;
            m_LogId = LogId;
            m_LogDate = LogDate;
            m_WindowName = WindowName;
            m_Operation = Operation;
            m_Status = Status;
            m_UserName = UserName;
            m_ClientId = ClientId;
            m_GlobalUid = GlobalUid;
            m_AdditionalInfo = AdditionalInfo;
        }
        public String getDatabase() {
            return m_Database;
        }
        public int getLogId() {
            return m_LogId;
        }
        public String getLogDate() {
            return m_LogDate;
        }
        public String getWindowName() {
            return m_WindowName;
        }
        public String getOperation() {
            return m_Operation;
        }
        public String getStatus() {
            return m_Status;
        }
        public String getUserName() {
            return m_UserName;
        }
        public String getClientId() {
            return m_ClientId;
        }
        public String getGlobalUid() {
            return m_GlobalUid;
        }
        public String getAdditionalInfo() {
            return m_AdditionalInfo;
        }

        private String  m_Database;
        private int     m_LogId;
        private String  m_LogDate;
        private String  m_WindowName;
        private String  m_Operation;
        private String  m_Status;
        private String  m_UserName;
        private String  m_ClientId;
        private String  m_GlobalUid;
        private String  m_AdditionalInfo;
    }


    public boolean getWindowDetLogData(String  mCurrentDate) {

        boolean mErrorFound = false;
        boolean[] booleanArray = new boolean[sizeDataAreaObj()];
        int[] intArray = new int[sizeDataAreaObj()];

        clearWindowLogVector();

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            intArray[i1] = 0;
            booleanArray[i1] = true;

            SchedDataArea lDataArea = getDataAreaObj(i1);

            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            mConnectionItem.setErrorFlag(false);
            try {
                lDataArea.SetupWindowDetLogData(mCurrentDate, mConnectionItem.getDetWinWhereStmt());
            }
            catch(SQLException e) {
                mConnectionItem.setErrorFlag(true);
                mErrorFound = true;
            }
            if ( ! mConnectionItem.getErrorFlag() ) {
                booleanArray[i1] = lDataArea.getNextWindowDetailRow();
            }
        }

        boolean mContinue = true;
        do {
            mContinue = false;
            String mCheckDate = "2300-01-01 00:00:00";
            int lCursorNo = -1;
            for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
                SchedDataArea lDataArea = getDataAreaObj(i1);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    if (booleanArray[i1]) {
                        String lWindowLogDate = lDataArea.getWindowDetLogDate();

                        if (lWindowLogDate != null) {
                            if (lWindowLogDate.compareTo(mCheckDate) < 0) {
                                lCursorNo = i1;
                                mCheckDate = lWindowLogDate;
                            }
                        }
                    }
                }
            }
            if (lCursorNo > -1) {
                mContinue = true;
                SchedDataArea lDataArea = getDataAreaObj(lCursorNo);

                connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
                if ( ! mConnectionItem.getErrorFlag() ) {
                    lDataArea.getWindowDetLogData(this);
                    booleanArray[lCursorNo] = lDataArea.getNextWindowDetailRow();
                }
            }

        } while (mContinue);

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {
            SchedDataArea lDataArea = getDataAreaObj(i1);
            connectionItem mConnectionItem = getConnectionObjById(lDataArea.getConnectId());
            if ( ! mConnectionItem.getErrorFlag() ) {
                lDataArea.closeWindowDetLogCursor();
            }
        }
        return mErrorFound;
    }

    public boolean addWindowDetails(WindowDetLogItem m_WindowDetLogItem) {
        return WindowDetLogVector.add(m_WindowDetLogItem);
    }

    public WindowDetLogItem getWindowDetails(int mWindowLogNo) {
        WindowDetLogItem m_WindowDetLogItem = 
                WindowDetLogVector.get(mWindowLogNo);
        return m_WindowDetLogItem;
    }

    public WindowDetLogItem getWindowDetailsLogId(int WindowLogId) {

        WindowDetLogItem m_WindowDetLogItem = null;
        for (int i = 0; i < WindowDetLogVector.size(); i++) {

            m_WindowDetLogItem = WindowDetLogVector.get(i);

            if (m_WindowDetLogItem.getLogId() == WindowLogId) {
                break;
            }
        }
        return m_WindowDetLogItem;
    }

    public int windowDetLogSize() {
        return WindowDetLogVector.size();
    }

    class WindowDetLogItem {

        public WindowDetLogItem(String   Database,
                                 int     LogId,
                                 String  LogDate,
                                 String  WindowName,
                                 String  ReqStartDate,
                                 String  ActualStartDate,
                                 String  WindowDuration,
                                 String  ActualDuration,
                                 int     InstanceId,
                                 String  AdditionalInfo) {
            m_Database = Database;
            m_LogId = LogId;
            m_LogDate = LogDate;
            m_WindowName = WindowName;
            m_ReqStartDate = ReqStartDate;
            m_ActualStartDate = ActualStartDate;
            m_WindowDuration = WindowDuration;
            m_ActualDuration = ActualDuration;
            m_InstanceId = InstanceId;
            m_AdditionalInfo = AdditionalInfo;
        }

        public String getDatabase() {
            return m_Database;
        }
        public int getLogId() {
            return m_LogId;
        }
        public String getLogDate() {
            return m_LogDate;
        }
        public String getWindowName() {
            return m_WindowName;
        }
        public String getReqStartDate() {
            return m_ReqStartDate;
        }
        public String getActualStartDate() {
            return m_ActualStartDate;
        }
        public String getWindowDuration() {
            return m_WindowDuration;
        }
        public String getActualDuration() {
            return m_ActualDuration;
        }
        public int getInstanceId() {
            return m_InstanceId;
        }
        public String getAdditionalInfo() {
            return m_AdditionalInfo;
        }

        private String  m_Database;
        private int     m_LogId;
        private String  m_LogDate;
        private String  m_WindowName;
        private String  m_ReqStartDate;
        private String  m_ActualStartDate;
        private String  m_WindowDuration;
        private String  m_ActualDuration;
        private int     m_InstanceId;
        private String  m_AdditionalInfo;
    }

    public void GetJobsRunningData() {

        clearJobsRunningVector();
        mRunningJobId = 5000;

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {

            SchedDataArea lDataArea = getDataAreaObj(i1);

            lDataArea.GetJobsRunningData(this);
        }
    }

    public boolean addJobsRunning(JobsRunningItem m_JobsRunningItem) {
        return JobsRunningVector.add(m_JobsRunningItem);
    }

    public JobsRunningItem getJobsRunning(int JobsRunningNo) {
        mJobsRunningItem = JobsRunningVector.get(JobsRunningNo);
        return mJobsRunningItem;
    }
    public JobsRunningItem getJobsRunningId(int JobsRunningId) {

        for (int i = 0; i < JobsRunningVector.size(); i++) {

            mJobsRunningItem = JobsRunningVector.get(i);

            if (mJobsRunningItem.getId() == JobsRunningId) {
                break;
            }
        }
        return mJobsRunningItem;
    }

    public int jobsRunningSize() {
        return JobsRunningVector.size();
    }

    class JobsRunningItem {

        public JobsRunningItem(String  Database,
                               String  Owner,
                               String  JobName,
                               int     SessionId,
                               int     SlaveProcessId,
                               int     RunningInstance,
                               String  ResourceConsumerGroup,
                               String  ElapsedTime,
                               String  CpuUsed,
                               String  JobSubname,
                               int     SlaveOsProcessId,
                               String  JobStyle,
                               String  Detached,
                               String  DestinationOwner,
                               String  Destination,
                               String  CredentialOwner,
                               String  CredentialName) {

            mRunningJobId = mRunningJobId + 1;
            m_Id = mRunningJobId;
            m_Database = Database;
            m_Owner = Owner;
            m_JobName = JobName;
            m_SessionId = SessionId;
            m_SlaveProcessId = SlaveProcessId;
            m_RunningInstance = RunningInstance;
            m_ResourceConsumerGroup = ResourceConsumerGroup;
            m_ElapsedTime = ElapsedTime;
            m_CpuUsed = CpuUsed;
            m_JobSubname = JobSubname;
            m_SlaveOsProcessId = SlaveOsProcessId;
            m_JobStyle = JobStyle;
            m_Detached = Detached;
            m_DestinationOwner = DestinationOwner;
            m_Destination = Destination;
            m_CredentialOwner = CredentialOwner;
            m_CredentialName = CredentialName;
        }

        public int getId() {
            return m_Id;
        }
        public String getDatabase() {
            return m_Database;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getJobName() {
            return m_JobName;
        }
        public int getSessionId() {
            return m_SessionId;
        }
        public int getSlaveProcessId() {
            return m_SlaveProcessId;
        }
        public int getRunningInstance() {
            return m_RunningInstance;
        }
        public String getResourceConsumerGroup() {
            return m_ResourceConsumerGroup;
        }
        public String getElapsedTime() {
            return m_ElapsedTime;
        }
        public String getCpuUsed() {
            return m_CpuUsed;
        }
        public String getJobSubname() {
            return m_JobSubname;
        }
        public int getSlaveOsProcessId() {
            return m_SlaveOsProcessId;
        }
        public String getJobStyle() {
            return m_JobStyle;
        }
        public String getDetached() {
            return m_Detached;
        }
        public String getDestinationOwner() {
            return m_DestinationOwner;
        }
        public String getDestination() {
            return m_Destination;
        }
        public String getCredentialOwner() {
            return m_CredentialOwner;
        }
        public String getCredentialName() {
            return m_CredentialName;
        }

        private int     m_Id;
        private String  m_Database;
        private String  m_Owner;
        private String  m_JobName;
        private int     m_SessionId;
        private int     m_SlaveProcessId;
        private int     m_RunningInstance;
        private String  m_ResourceConsumerGroup;
        private String  m_ElapsedTime;
        private String  m_CpuUsed;
        private String  m_JobSubname;
        private int     m_SlaveOsProcessId;
        private String  m_JobStyle;
        private String  m_Detached;
        private String  m_DestinationOwner;
        private String  m_Destination;
        private String  m_CredentialOwner;
        private String  m_CredentialName;
    }

    public void GetChainsRunningData() {

        clearChainsRunningVector();
        mRunningChainId = 1000;

        for (int i1 = 0; i1 < sizeDataAreaObj(); i1++) {

            SchedDataArea lDataArea = getDataAreaObj(i1);

            if (lDataArea.getVersionNo() > 1) lDataArea.GetChainsRunningData(this);
        }
    }

    public boolean addChainsRunning(ChainsRunningItem m_ChainsRunningItem) {
        return ChainsRunningVector.add(m_ChainsRunningItem);
    }

    public ChainsRunningItem getChainsRunning(int ChainsRunningNo) {
        mChainsRunningItem = ChainsRunningVector.get(ChainsRunningNo);
        return mChainsRunningItem;
    }

    public ChainsRunningItem getChainsRunningId(int ChainsRunningId) {

        for (int i = 0; i < ChainsRunningVector.size(); i++) {

            mChainsRunningItem = ChainsRunningVector.get(i);

            if (mChainsRunningItem.getId() == ChainsRunningId) {
                break;
            }
        }
        return mChainsRunningItem;
    }
    public int chainsRunningSize() {
        return ChainsRunningVector.size();
    }

    class ChainsRunningItem {

        public ChainsRunningItem(String  Database,
                                 String  Owner,
                                 String  JobName,
                                 String  JobSubname,
                                 String  ChainOwner,
                                 String  ChainName,
                                 String  StepName,
                                 String  State,
                                 int     ErrorCode,
                                 String  Completed,
                                 String  StartDate,
                                 String  EndDate,
                                 String  Duration,
                                 String  Skip,
                                 String  Pause,
                                 String  RestartOnRecovery,
                                 String  StepJobSubname,
                                 int     StepJobLogId,
                                 String  RestartOnFailure) {
            mRunningChainId = mRunningChainId + 1;
            m_Id = mRunningChainId;
            m_Database = Database;
            m_Owner = Owner;
            m_JobName = JobName;
            m_JobSubname = JobSubname;
            m_ChainOwner = ChainOwner;
            m_ChainName = ChainName;
            m_StepName = StepName;
            m_State = State;
            m_ErrorCode = ErrorCode;
            m_Completed = Completed;
            m_StartDate = StartDate;
            m_EndDate = EndDate;
            m_Duration = Duration;
            m_Skip = Skip;
            m_Pause = Pause;
            m_RestartOnRecovery = RestartOnRecovery;
            m_StepJobSubname = StepJobSubname;
            m_StepJobLogId = StepJobLogId;
            m_RestartOnFailure = RestartOnFailure;
        }

        public int getId() {
            return m_Id;
        }
        public String getDatabase() {
            return m_Database;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getJobName() {
            return m_JobName;
        }
        public String getJobSubName() {
            return m_JobSubname;
        }
        public String getChainOwner() {
            return m_ChainOwner;
        }
        public String getChainName() {
            return m_ChainName;
        }
        public String getStepName() {
            return m_StepName;
        }
        public String getState() {
            return m_State;
        }
        public int getErrorCode() {
            return m_ErrorCode;
        }
        public String getCompleted() {
            return m_Completed;
        }
        public String getStartDate() {
            return m_StartDate;
        }
        public String getEndDate() {
            return m_EndDate;
        }
        public String getDuration() {
            return m_Duration;
        }
        public String getSkip() {
            return m_Skip;
        }
        public String getPause() {
            return m_Pause;
        }
        public String getRestartOnRecovery() {
            return m_RestartOnRecovery;
        }
        public String getStepJobSubname() {
            return m_StepJobSubname;
        }
        public int getStepJobLogId() {
            return m_StepJobLogId;
        }
        public String getRestartOnFailure() {
            return m_RestartOnFailure;
        }

        private int     m_Id;
        private String  m_Database;
        private String  m_Owner;
        private String  m_JobName;
        private String  m_JobSubname;
        private String  m_ChainOwner;
        private String  m_ChainName;
        private String  m_StepName;
        private String  m_State;
        private int     m_ErrorCode;
        private String  m_Completed;
        private String  m_StartDate;
        private String  m_EndDate;
        private String  m_Duration;
        private String  m_Skip;
        private String  m_Pause;
        private String  m_RestartOnRecovery;
        private String  m_StepJobSubname;
        private int     m_StepJobLogId;
        private String  m_RestartOnFailure;
    }


    private Vector<JobLogItem>              JobLogVector;
    private Vector<JobDetLogItem>           JobDetLogVector;

    private Vector<WindowLogItem>           WindowLogVector;
    private Vector<WindowDetLogItem>        WindowDetLogVector;

    private Vector<JobsRunningItem>         JobsRunningVector;
    private Vector<ChainsRunningItem>       ChainsRunningVector;
}

