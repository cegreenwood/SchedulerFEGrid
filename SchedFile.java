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
import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import org.apache.commons.codec.binary.Base64;

public class SchedFile {

    static final String           DEFAULT_INP_FILE  = "SchedFileData.csv";
    static final String           DEFAULT_INIT_FILE  = "SchedFileInit.csv";
    static final String           DEFAULT_ERR_FILE  = "SchedErrors.log";
    static final String           DEFAULT_AUDIT_FILE  = "SchedAudit.log";
    static final String           DEFAULT_PASS_FILE  = "SchedFilePass.csv";

    // static final byte[]   iv = { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 };

    static final int              INIT_FILE_ID = 1;
    static final int              ERROR_FILE_ID = 2;
    static final int              AUDIT_FILE_ID = 3;
    static final int              PASSWORD_FILE_ID = 4;
    static final int              PASSWORD_IV_ID = 5;

    static int                    mXScreenSize, mYScreenSize;
    static int                    mXScreenLocation, mYScreenLocation;
    static int[]                  mDivLocation;
    static int[]                  fileOption;
    static boolean                mBlockAudit;
    static SchedGlobalData        mGlobalArea;

    static Color                  backGroundColor;

    static FileWriter             fileWriter;
    static BufferedWriter         buffWriter;
    static FileReader             fileReader;
    static BufferedReader         buffReader;

    static String                 mFileName;

    public static Color getBackGroundColor() {
        return backGroundColor;
    }

    public static void setBackGroundColor(Color mColor) {
        backGroundColor = mColor;
    }

    public static void setAudit(boolean m_BlockAudit) {
        mBlockAudit = m_BlockAudit;
    }

    public static void setMiscArea(SchedGlobalData m_GlobalArea) {
        mGlobalArea = m_GlobalArea;
    }
    public static SchedGlobalData getMiscArea() {
        return mGlobalArea;
    }

    public static void EnterErrorEntry(String m_Proc, String m_Error) {

        String mString = m_Proc + ":" + m_Error;

        WriteErrors(mString);

    }

    public static void WriteErrors(String m_OutputLine) {
        try {

            mFileName = null;
            if (mGlobalArea.findParamItem(ERROR_FILE_ID)) {
                mFileName = mGlobalArea.getParamValue(ERROR_FILE_ID);
            }
            else {
                mFileName = DEFAULT_ERR_FILE;
            }

            fileWriter = new FileWriter(mFileName, true);

            buffWriter = new BufferedWriter(fileWriter);

            String mOutputLine = getDateTime() + ":" + m_OutputLine;
            int mStart = 0;
            int mEnd = 80;

            boolean mContinue = true;
            while (mContinue) {
                if (mOutputLine.length() < 80) {
                    buffWriter.write(mOutputLine);
                    mContinue = false;
                }
                else {
                    if (mStart == 0) {
                        buffWriter.write(mOutputLine, mStart, mEnd);
                        mStart = 80;
                    }
                    else {
                        buffWriter.write("          " + mOutputLine.substring(mStart, mEnd));
                        mStart = mStart + 70;
                    }

                    mEnd = mEnd + 70;
                    if (mEnd > mOutputLine.length()) mEnd = mOutputLine.length();
                    if (mStart > mOutputLine.length()) mContinue = false;
                }
                buffWriter.newLine();
            }

            buffWriter.close();

        } catch (IOException e) {
            System.out.println("Error File, " + mFileName + ", " + e.toString() );
        } finally {
            try {
                if (buffWriter != null) buffWriter.close();
            } catch (IOException e) {
                System.out.println("Error File, " + mFileName + ", " + e.toString() );
            }
        }
    }

    public static void WriteAudit(String m_OutputLine) {
        if (! mBlockAudit) {
            try {
                mFileName = null;
                if (mGlobalArea.findParamItem(AUDIT_FILE_ID)) {
                    mFileName = mGlobalArea.getParamValue(AUDIT_FILE_ID);
                }
                else {
                    mFileName = DEFAULT_AUDIT_FILE;
                }

                fileWriter = new FileWriter(mFileName, true);

                buffWriter = new BufferedWriter(fileWriter);

                String mOutputLine = getDateTime() + ":" + m_OutputLine;
                int mStart = 0;
                int mEnd = 80;

                boolean mContinue = true;
                while (mContinue) {
                    if (mOutputLine.length() < 80) {
                        buffWriter.write(mOutputLine);
                        mContinue = false;
                    }
                    else {
                        if (mStart == 0) {
                            buffWriter.write(mOutputLine, mStart, mEnd);
                            mStart = 80;
                        }
                        else {
                            buffWriter.write("          " + mOutputLine.substring(mStart, mEnd));
                            mStart = mStart + 70;
                        }

                        mEnd = mEnd + 70;
                        if (mEnd > mOutputLine.length()) mEnd = mOutputLine.length();
                        if (mStart > mOutputLine.length()) mContinue = false;
                    }
                    buffWriter.newLine();
                }

                buffWriter.close();

            } catch (IOException e) {
                EnterErrorEntry("WriteAudit"," : Error:IOException..." + e.getMessage());
            } finally {
                try {
                    if (buffWriter != null) buffWriter.close();
                } catch (IOException e) {
                    EnterErrorEntry("WriteAudit"," : Error:IOException..." + e.getMessage());
                }
            }
        }
    }

    public static void WriteDebugLine(String mOutputLine) {
        try {

            mFileName = "SchedDebug.txt";

            fileWriter = new FileWriter(mFileName, true);

            buffWriter = new BufferedWriter(fileWriter);

            buffWriter.write(mOutputLine);

            buffWriter.newLine();

            buffWriter.close();

        } catch (IOException e) {
            EnterErrorEntry("WriteDebugLine"," : Error:IOException..." + e.getMessage());
        } finally {
            try {
                if (buffWriter != null) buffWriter.close();
            } catch (IOException e) {
                EnterErrorEntry("WriteDebugLine"," : Error:IOException..." + e.getMessage());
            }
        }
    }

    public static StringBuffer getDateTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf_long = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuffer mCurrentDate = new StringBuffer(sdf_long.format(cal.getTime()));

        return mCurrentDate;
    }

    public static void ReadInitParams() {
        fileOption = new int[15];
        mDivLocation = new int[8];

        try {
            mFileName = null;
            if (mGlobalArea.findParamItem(INIT_FILE_ID)) {
                mFileName = mGlobalArea.getParamValue(INIT_FILE_ID);
            }
            else {
                mFileName = DEFAULT_INIT_FILE;
            }
            fileReader = new FileReader(mFileName);
            buffReader = new BufferedReader(fileReader);

            boolean eof = false;
            while (!eof) {

                String line1 = buffReader.readLine();

                if (line1 == null) {
                    eof = true;
                }
                else {

                    int point1 = line1.indexOf(",");

                    String subStr1 = line1.substring(0, point1 );

                    if (subStr1.equals("01")) {
                        readInit01Data(line1);
                    }
                    if (subStr1.equals("02")) {
                        readInit02Data(line1);
                    }
                    if (subStr1.equals("03")) {
                        readInit03Data(line1);
                    }
                    if (subStr1.equals("04")) {
                        readInit04Data(line1);
                    }
                }
            }
            buffReader.close();
        } catch (FileNotFoundException e) {
            mXScreenSize = 900;
            mYScreenSize = 600;
            mXScreenLocation = 40;
            mYScreenLocation = 10;
            mDivLocation[2] = 180;
            mDivLocation[3] = 250;
            mDivLocation[4] = 250;
            mDivLocation[5] = 250;
            mDivLocation[6] = 250;
            mDivLocation[7] = 250;

            mXScreenSize = 900;
            mYScreenSize = 600;
            mXScreenLocation = 40;
            mYScreenLocation = 10;

            fileOption[0] = 1;
            fileOption[1] = 1;
            fileOption[2] = 1;
            fileOption[3] = 1;
            fileOption[4] = 1;
            fileOption[5] = 1;

            fileOption[6] = 0;
            fileOption[7] = 0;
            fileOption[8] = 0;
            fileOption[9] = 0;
            fileOption[10] = 0;
            fileOption[11] = 0;
            fileOption[12] = 1;
            fileOption[13] = 0;
            fileOption[14] = 0;

        } catch (IOException e) {
            EnterErrorEntry("ReadInitParams"," : Error..." + e.getMessage());
        } finally {
            try {
                if (buffReader != null) buffReader.close();
            } catch (IOException e) {
                EnterErrorEntry("ReadInitParams"," : Error:IOException..." + e.getMessage());
            }
        }
    }

    public static void readInit01Data(String mLine) {
        int mPoint = 2;
        String[] fileText = new String[11];

        for (int i1=0; i1 < 10; i1++) {
            if (i1 < 9) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            switch (i1) {
              case 0:
                mXScreenSize = Integer.parseInt(fileText[0]);
                break;
              case 1:
                mYScreenSize = Integer.parseInt(fileText[1]);
                break;
              case 2:
                mXScreenLocation = Integer.parseInt(fileText[2]);
                break;
              case 3:
                mYScreenLocation = Integer.parseInt(fileText[3]);
                break;
              case 4:
                mDivLocation[2] = Integer.parseInt(fileText[4]);
                break;
              case 5:
                mDivLocation[3] = Integer.parseInt(fileText[5]);
                break;
              case 6:
                mDivLocation[4] = Integer.parseInt(fileText[6]);
                break;
              case 7:
                mDivLocation[5] = Integer.parseInt(fileText[7]);
                break;
              case 8:
                mDivLocation[6] = Integer.parseInt(fileText[8]);
                break;
              case 9:
                mDivLocation[7] = Integer.parseInt(fileText[9]);
                break;
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }
    }

    public static void readInit02Data(String mLine) {
        int mPoint = 2;
        String[] fileText = new String[15];

        for (int i1=0; i1 < 15; i1++) {
            if (i1 < 14) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }

            fileOption[i1] = Integer.parseInt(fileText[i1]);
            mPoint = mLine.indexOf(",", mPoint + 1);

        }
    }

    public static void readInit03Data(String mLine) {
        int mPoint = 2;
        String[] fileText = new String[8];
        boolean mSysdba = false;
        boolean mSavePassword = false;
        boolean mAutoConnect = false;

        for (int i1=0; i1 < 8; i1++) {
            if (i1 < 7) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }

            mPoint = mLine.indexOf(",", mPoint + 1);

        }
        if (fileText[5].equals("Y")) mSysdba = true;
        if (fileText[6].equals("Y")) mSavePassword = true;
        if (fileText[7].equals("Y")) mAutoConnect = true;

        SchedGlobalData.connectionItem m_ConnectionItem =
            mGlobalArea.new connectionItem(fileText[0],
                                         fileText[1],
                                         null,
                                         fileText[2],
                                         fileText[3],
                                         fileText[4],
                                         mSysdba,
                                         mSavePassword,
                                         mAutoConnect,
                                         null, null, null, null, null, null);

        mGlobalArea.addConnectionObj(m_ConnectionItem);
    }

    public static void readInit04Data(String mLine) {
        int mPoint = 2;
        String[] fileText = new String[3];

        for (int i1=0; i1 < 3; i1++) {
            if (i1 < 2) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }

            mPoint = mLine.indexOf(",", mPoint + 1);
        }

        for (int i1=0; i1 < mGlobalArea.sizeConnectionObj(); i1++) {
            SchedGlobalData.connectionItem m_ConnectionItem =
                        mGlobalArea.getConnectionObj(i1);

            if (m_ConnectionItem.getName().equals(fileText[0])) {
                if (fileText[1].equals("01"))
                    m_ConnectionItem.setRunWhereStmt(fileText[2]);
                if (fileText[1].equals("02"))
                    m_ConnectionItem.setStdLogWhereStmt(fileText[2]);
                if (fileText[1].equals("03"))
                    m_ConnectionItem.setDetLogWhereStmt(fileText[2]);
                if (fileText[1].equals("04"))
                    m_ConnectionItem.setChainWhereStmt(fileText[2]);
                if (fileText[1].equals("05"))
                    m_ConnectionItem.setStdWinWhereStmt(fileText[2]);
                if (fileText[1].equals("06"))
                    m_ConnectionItem.setDetWinWhereStmt(fileText[2]);
            }
        }
    }

    public static void writeInitParams() {
        try {
            mFileName = null;
            if (mGlobalArea.findParamItem(INIT_FILE_ID)) {
                mFileName = mGlobalArea.getParamValue(INIT_FILE_ID);
            }
            else {
                mFileName = DEFAULT_INIT_FILE;
            }
            fileWriter = new FileWriter(mFileName);

            buffWriter = new BufferedWriter(fileWriter);

            buffWriter.write("01," + mXScreenSize + "," + 
                       mYScreenSize + "," + 
                       mXScreenLocation + "," +
                       mYScreenLocation + "," +
                       mDivLocation[2] + "," +
                       mDivLocation[3] + "," +
                       mDivLocation[4] + "," +
                       mDivLocation[5] + "," +
                       mDivLocation[6] + "," +
                       mDivLocation[7]);
            buffWriter.newLine();

            buffWriter.write("02," + fileOption[0] + "," + 
                       fileOption[1] + "," + 
                       fileOption[2] + "," +
                       fileOption[3] + "," +
                       fileOption[4] + "," +
                       fileOption[5] + "," +
                       fileOption[6] + "," +
                       fileOption[7] + "," +
                       fileOption[8] + "," +
                       fileOption[9] + "," +
                       fileOption[10] + "," +
                       fileOption[11] + "," +
                       fileOption[12] + "," +
                       fileOption[13] + "," +
                       fileOption[14]);

            buffWriter.newLine();

            for (int i1=0; i1 < mGlobalArea.sizeConnectionObj(); i1++) {
                SchedGlobalData.connectionItem m_ConnectionItem =
                        mGlobalArea.getConnectionObj(i1);

                String m_Sysdba = "N";
                if (m_ConnectionItem.isSysdba()) m_Sysdba = "Y";
                String m_SavePassword = "N";
                if (m_ConnectionItem.isSavePassword()) m_SavePassword = "Y";
                String m_AutoConnect = "N";
                if (m_ConnectionItem.isAutoConnect()) m_AutoConnect = "Y";

                buffWriter.write("03," + m_ConnectionItem.getName() + "," + 
                                   m_ConnectionItem.getAcName() + "," +
                                   m_ConnectionItem.getHost() + "," +
                                   m_ConnectionItem.getPort() + "," +
                                   m_ConnectionItem.getDatabase() + "," +
                                   m_Sysdba + "," + m_SavePassword + "," +
                                   m_AutoConnect);
                buffWriter.newLine();
            }

            for (int i1=0; i1 < mGlobalArea.sizeConnectionObj(); i1++) {
                SchedGlobalData.connectionItem m_ConnectionItem =
                        mGlobalArea.getConnectionObj(i1);

                writeWhereStmt("01", m_ConnectionItem.getName(),
                               m_ConnectionItem.getRunWhereStmt(), buffWriter);

                writeWhereStmt("02", m_ConnectionItem.getName(),
                               m_ConnectionItem.getStdLogWhereStmt(), buffWriter);

                writeWhereStmt("03", m_ConnectionItem.getName(),
                               m_ConnectionItem.getDetLogWhereStmt(), buffWriter);

                writeWhereStmt("04", m_ConnectionItem.getName(),
                               m_ConnectionItem.getChainWhereStmt(), buffWriter);

                writeWhereStmt("05", m_ConnectionItem.getName(),
                               m_ConnectionItem.getDetWinWhereStmt(), buffWriter);

                writeWhereStmt("06", m_ConnectionItem.getName(),
                               m_ConnectionItem.getDetWinWhereStmt(), buffWriter);

            }
            buffWriter.close();
        } catch (IOException e) {
            EnterErrorEntry("writeInitParams"," : Error..." + e.getMessage());
        } finally {
            try {
                if (buffWriter != null) buffWriter.close();
            } catch (IOException e) {
                EnterErrorEntry("writeInitParams"," : Error:IOException..." + e.getMessage());
            }
        }
    }

    private static void writeWhereStmt(String mType, String mConnectName,
                                       String mStmt, BufferedWriter lBuff) {
        try {
            try {
                if (mStmt.length() > 0) {
                    lBuff.write("04," + mConnectName + "," + mType + "," +
                                       mStmt);
                    lBuff.newLine();
                }
            } catch (NullPointerException e) {
                ;
            }
        } catch (IOException e) {
            EnterErrorEntry("writeWhereStmt"," : Error..." + e.getMessage());
        }
    }

    public static void saveFrameSize(Dimension frameSize) {
        Double mWidth = new Double(frameSize.getWidth());
        mXScreenSize = mWidth.intValue();
        Double mHeight = new Double(frameSize.getHeight());
        mYScreenSize = mHeight.intValue();
    }

    public static void saveLocation(Point frameLocation) {
        Double xPoint = new Double(frameLocation.getX());
        mXScreenLocation = xPoint.intValue();
        Double yPoint = new Double(frameLocation.getY());
        mYScreenLocation = yPoint.intValue();
    }

    public static void saveDivLocation(int paneNo, int divLocation2) {
        mDivLocation[paneNo] = divLocation2;
    }
    public static int getDivLocation(int paneNo) {
        return mDivLocation[paneNo];
    }

    public static int getXFrameSize() {
        return mXScreenSize;
    }

    public static int getYFrameSize() {
        return mYScreenSize;
    }

    public static int getXLocation() {
        return mXScreenLocation;
    }

    public static int getYLocation() {
        return mYScreenLocation;
    }

    public static boolean getFileOption(int mNo) {
        if (fileOption[mNo] == 1)  return true;
        else                     return false;
    }

    public static void saveFileOption(int mNo, boolean mResult) {
        if (mResult)  fileOption[mNo] = 1;
        else          fileOption[mNo] = 0;
    }


    public static void ReadParams(SchedScreenArea m_Screen,
                                  SchedInpScreenArea m_ScreenInp) {
        try {

            fileReader = new FileReader(DEFAULT_INP_FILE);

            buffReader = new BufferedReader(fileReader);

            boolean eof = false;
            while (!eof) {

                String line1 = buffReader.readLine();

                if (line1 == null) {
                    eof = true;
                }
                else {

                    int point1 = line1.indexOf(",");

                    String subStr1 = line1.substring(0, point1 );

                    if (subStr1.equals("01")) {
                        handleColorData(line1, mGlobalArea);
                    }
                    else if (subStr1.equals("02")) {
                        handleScreenIdData(mGlobalArea, line1, m_Screen, m_ScreenInp);
                    }
                    else if (subStr1.equals("03")) {
                        handleTabData(line1, m_Screen);
                    }
                    else if (subStr1.equals("04")) {
                        handlePageData(line1, m_Screen, m_ScreenInp);
                    }
                    else if (subStr1.equals("05")) {
                        handleColumnData(line1, m_Screen);
                    }
                    else if (subStr1.equals("06")) {
                        handlePopupData(line1, mGlobalArea);
                    }
                    else if (subStr1.equals("07")) {
                        handlePopupColumnData(line1, mGlobalArea);
                    }
                    else if (subStr1.equals("08")) {
                        handleSecurityData(line1, mGlobalArea);
                    }
                    else if (subStr1.equals("09")) {
                        handleParamData(line1, mGlobalArea);
                    }
                }
            }
            buffReader.close();
        } catch (IOException e) {
            EnterErrorEntry("ReadParams"," : Error..." + e.getMessage());
        } finally {
            try {
                if (buffReader != null) buffReader.close();
            } catch (IOException e) {
                EnterErrorEntry("ReadParams"," : Error:IOException..." + e.getMessage());
            }
        }
    }

    public static void handleColorData(String mLine, SchedGlobalData mArea) {
        int mPoint = 2;
        String[] fileText = new String[6];

        for (int i1=0; i1 < 6; i1++) {
            if (i1 < 5) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);

        }
        SchedGlobalData.screenColor m_ScreenColor =
            mArea.new screenColor(Integer.parseInt(fileText[0]),
                                  Integer.parseInt(fileText[1]),
                                  Integer.parseInt(fileText[2]),
                                  Integer.parseInt(fileText[3]),
                                  Integer.parseInt(fileText[4]),
                                  fileText[5]);

        mArea.addColorObj(m_ScreenColor);
    }

    public static void handleScreenIdData(SchedGlobalData            mArea,
                                          String              mLine,
                                          SchedScreenArea     mScreen,
                                          SchedInpScreenArea  mScreenInp) {

        int mPoint = 2;
        String[] fileText = new String[8];
        for (int i1=0; i1 < 8; i1++) {
            if (i1 < 7) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }

        int mScreenId = Integer.parseInt(fileText[1]);
        int mScreenNo = Integer.parseInt(fileText[2]);

        if (mScreenId < 500) {
            mScreen.processScreenData(mScreenId, mScreenNo, fileText[0],
                              Integer.parseInt(fileText[5]),
                              Integer.parseInt(fileText[6]),
                              Integer.parseInt(fileText[7]),
                              Integer.parseInt(fileText[3]));
        }
        else {
            mScreenInp.processScreenData(mScreenId, mScreenNo, fileText[0],
                              Integer.parseInt(fileText[5]),
                              Integer.parseInt(fileText[6]));
        }

    }

    public static void handleTabData(String            mLine,
                                     SchedScreenArea   mScreen) {
        int mPoint = 2;
        String[] fileText = new String[5];

        for (int i1=0; i1 < 3; i1++) {
            if (i1 < 2) {
                 fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }
        int mScreenNo = Integer.parseInt(fileText[0]);


        mScreen.addTab(Integer.parseInt(fileText[0]), 
                               Integer.parseInt(fileText[1]),
                               fileText[2]);

    }

    public static void handlePageData(String              mLine,
                                      SchedScreenArea     mScreen,
                                      SchedInpScreenArea  mScreenInp) {
        // System.out.println("1. - " + mLine);
        int mPoint = 2;
        String[] fileText = new String[22];

        for (int i1=0; i1 < 20; i1++) {
            if (i1 < 19) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }

        int mScreenId = Integer.parseInt(fileText[0]);

        int mRowType = Integer.parseInt(fileText[1]);

        // String mDisplay = fileText[17];

        // if (mDisplay.equals("Y")) {
            if (mScreenId < 500) {

                SchedScreenArea.PaneObject mPane = mScreen.getScreen(mScreenId);

                if (mRowType == 1) {

                    SchedScreenArea.PaneObject.LabelItem m_PaneItem = mPane.new LabelItem(
                            fileText[9],
                            Integer.parseInt(fileText[5]),
                            Integer.parseInt(fileText[6]),
                            Integer.parseInt(fileText[7]),
                            Integer.parseInt(fileText[8]),
                            fileText[10],
                            Integer.parseInt(fileText[11]),
                            Integer.parseInt(fileText[12]),
                            Integer.parseInt(fileText[2]),
                            Integer.parseInt(fileText[13]),
                            Integer.parseInt(fileText[14]),
                            fileText[17]
                    );
                    mPane.addLabelObj(m_PaneItem);
                }
                if ((mRowType == 2) || (mRowType == 4) || (mRowType == 5)) {

                    SchedScreenArea.PaneObject.TextItem m_TextItem = mPane.new TextItem(
                            mRowType,
                            fileText[9],
                            Integer.parseInt(fileText[5]),
                            Integer.parseInt(fileText[6]),
                            Integer.parseInt(fileText[7]),
                            Integer.parseInt(fileText[8]),
                            fileText[10],
                            Integer.parseInt(fileText[11]),
                            Integer.parseInt(fileText[12]),
                            Integer.parseInt(fileText[2]),
                            Integer.parseInt(fileText[3]),
                            Integer.parseInt(fileText[13]),
                            Integer.parseInt(fileText[14]),
                            Integer.parseInt(fileText[4]),
                            Integer.parseInt(fileText[15]),
                            fileText[16],
                            fileText[18],
                            Integer.parseInt(fileText[19]),
                            fileText[17]
                    );
                    mPane.addTextObj(m_TextItem);
                }
                if (mRowType == 3) {
                    SchedScreenArea.PaneObject.TextAreaItem m_TextAreaItem = mPane.new TextAreaItem(
                            fileText[9],
                            Integer.parseInt(fileText[5]),
                            Integer.parseInt(fileText[6]),
                            Integer.parseInt(fileText[7]),
                            Integer.parseInt(fileText[8]),
                            fileText[10],
                            Integer.parseInt(fileText[11]),
                            Integer.parseInt(fileText[12]),
                            Integer.parseInt(fileText[2]),
                            Integer.parseInt(fileText[3]),
                            Integer.parseInt(fileText[13]),
                            Integer.parseInt(fileText[14]),
                            Integer.parseInt(fileText[4]),
                            Integer.parseInt(fileText[15]),
                            fileText[16],
                            fileText[17]
                    );
                    mPane.addTextAreaObj(m_TextAreaItem);
                }
            }
            else {

                SchedInpScreenArea.PaneObject mInpPane = mScreenInp.getScreen(mScreenId);

                if (mRowType == 1) {

                    SchedInpScreenArea.PaneObject.LabelItem m_PaneItem = mInpPane.new LabelItem(
                            fileText[9],
                            Integer.parseInt(fileText[5]),
                            Integer.parseInt(fileText[6]),
                            Integer.parseInt(fileText[7]),
                            Integer.parseInt(fileText[8]),
                            fileText[10],
                            Integer.parseInt(fileText[11]),
                            Integer.parseInt(fileText[12]),
                            Integer.parseInt(fileText[2]),
                            Integer.parseInt(fileText[13]),
                            Integer.parseInt(fileText[14])
                    );

                    mInpPane.addLabelObj(m_PaneItem);

                }
                if ((mRowType == 2) || (mRowType == 5)) {

                    SchedInpScreenArea.PaneObject.TextItem m_TextItem = mInpPane.new TextItem(
                            mRowType,
                            fileText[9],
                            Integer.parseInt(fileText[5]),
                            Integer.parseInt(fileText[6]),
                            Integer.parseInt(fileText[7]),
                            Integer.parseInt(fileText[8]),
                            fileText[10],
                            Integer.parseInt(fileText[11]),
                            Integer.parseInt(fileText[12]),
                            Integer.parseInt(fileText[2]),
                            Integer.parseInt(fileText[3]),
                            Integer.parseInt(fileText[13]),
                            Integer.parseInt(fileText[14]),
                            Integer.parseInt(fileText[4]),
                            Integer.parseInt(fileText[15]),
                            fileText[16],
                            fileText[18],
                            Integer.parseInt(fileText[19])
                    );
                    mInpPane.addTextObj(m_TextItem);
                }
                if (mRowType == 3) {

                    SchedInpScreenArea.PaneObject.TextAreaItem m_TextAreaItem = mInpPane.new TextAreaItem(
                            fileText[9],
                            Integer.parseInt(fileText[5]),
                            Integer.parseInt(fileText[6]),
                            Integer.parseInt(fileText[7]),
                            Integer.parseInt(fileText[8]),
                            fileText[10],
                            Integer.parseInt(fileText[11]),
                            Integer.parseInt(fileText[12]),
                            Integer.parseInt(fileText[2]),
                            Integer.parseInt(fileText[3]),
                            Integer.parseInt(fileText[13]),
                            Integer.parseInt(fileText[14]),
                            Integer.parseInt(fileText[4]),
                            Integer.parseInt(fileText[15]),
                            fileText[16]
                    );
                    mInpPane.addTextAreaObj(m_TextAreaItem);
                }
                if (mRowType == 4) {

                    SchedInpScreenArea.PaneObject.DateTimeItem m_DateTimeItem = mInpPane.new DateTimeItem(
                            fileText[9],
                            Integer.parseInt(fileText[5]),
                            Integer.parseInt(fileText[6]),
                            Integer.parseInt(fileText[7]),
                            Integer.parseInt(fileText[8]),
                            fileText[10],
                            Integer.parseInt(fileText[11]),
                            Integer.parseInt(fileText[12]),
                            Integer.parseInt(fileText[2]),
                            Integer.parseInt(fileText[3]),
                            Integer.parseInt(fileText[13]),
                            Integer.parseInt(fileText[14]),
                            Integer.parseInt(fileText[4]),
                            Integer.parseInt(fileText[15])
                    );
                    mInpPane.addDateTimeObj(m_DateTimeItem);
                }
            }
        // }
    }

    public static void handleColumnData(String            mLine,
                                        SchedScreenArea   mScreen) {
        int mPoint = 2;
        String[] fileText = new String[7];

        for (int i1=0; i1 < 7; i1++) {
            if (i1 < 6) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }

        int mScreenNo = Integer.parseInt(fileText[0]);

        SchedScreenArea.PaneObject mPane = mScreen.getScreen(mScreenNo);

        SchedScreenArea.PaneObject.ColumnItem mColumnItem = mPane.new ColumnItem(
                                                      Integer.parseInt(fileText[2]),
                                                      Integer.parseInt(fileText[1]),
                                                      fileText[3],
                                                      fileText[4],
                                                      fileText[5],
                                                      fileText[6]);

        mPane.addColumnObj(mColumnItem);


    }

    public static void handlePopupData(String mLine, SchedGlobalData mArea) {
        int mPoint = 2;
        String[] fileText = new String[4];

        for (int i1=0; i1 < 4; i1++) {
            if (i1 < 3) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }
        SchedGlobalData.screenPopup m_ScreenPopup =
            mArea.new screenPopup(Integer.parseInt(fileText[0]),
                                  fileText[1],
                                  fileText[2],
                                  fileText[3]);

        mArea.addPopupObj(m_ScreenPopup);
    }

    public static void handlePopupColumnData(String mLine, SchedGlobalData mArea) {
        int mPoint = 2;
        String[] fileText = new String[4];

        for (int i1=0; i1 < 4; i1++) {
            if (i1 < 3) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }
        SchedGlobalData.screenCombo m_ScreenCombo =
            mArea.new screenCombo(Integer.parseInt(fileText[0]),
                                  Integer.parseInt(fileText[1]),
                                  fileText[2],
                                  Integer.parseInt(fileText[3]),
                                  true);

        mArea.addComboObj(m_ScreenCombo);

    }

    public static void handleSecurityData(String mLine, SchedGlobalData mArea) {
        int mPoint = 2;
        String[] fileText = new String[3];

        for (int i1=0; i1 < 3; i1++) {
            if (i1 < 2) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }
        SchedGlobalData.screenSecurity m_ScreenSecurity =
            mArea.new screenSecurity(Integer.parseInt(fileText[0]),
                                     Integer.parseInt(fileText[1]));

        mArea.addSecurityObj(m_ScreenSecurity);
    }

    public static void handleParamData(String mLine, SchedGlobalData mArea) {
        int mPoint = 2;
        String[] fileText = new String[3];

        for (int i1=0; i1 < 2; i1++) {
            if (i1 < 1) {
                fileText[i1] = mLine.substring(mPoint + 1, mLine.indexOf(",", mPoint + 1) );
            }
            else {
                fileText[i1] = mLine.substring(mPoint + 1);
            }
            mPoint = mLine.indexOf(",", mPoint + 1);
        }
        SchedGlobalData.paramItem m_ParamItem =
            mArea.new paramItem(Integer.parseInt(fileText[0]),
                                fileText[1]);

        mArea.addParamObj(m_ParamItem);
    }

    public static void ReadPassParams() {
        try {
            mFileName = null;
            if (mGlobalArea.findParamItem(PASSWORD_FILE_ID)) {
                mFileName = mGlobalArea.getParamValue(PASSWORD_FILE_ID);
            }
            else {
                mFileName = DEFAULT_PASS_FILE;
            }
            fileReader = new FileReader(mFileName);

            buffReader = new BufferedReader(fileReader);

            try {

                SecretKey mKey = null;

                String mPassword_IvSpec = null;
                if (mGlobalArea.findParamItem(PASSWORD_IV_ID)) {
                    mPassword_IvSpec = mGlobalArea.getParamValue(PASSWORD_IV_ID);
                }
                else {
                    mPassword_IvSpec = "0102030405060708";
                }

                byte[]   iv = { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 };

                for (int mNo = 0; mNo < mPassword_IvSpec.length(); mNo++) {
                    try {
                        int mNumber = Integer.parseInt(mPassword_IvSpec.substring(mNo, mNo + 1));
                        iv[mNo] = (byte)mNumber;
                        if (mNo > 14) break;
                    } catch (NumberFormatException e) {
                        EnterErrorEntry("ReadPassParams"," : Error:NumberFormatException..." + e.getMessage());
                    }
                }
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                boolean eof = false;
                while (!eof) {

                    String line1 = buffReader.readLine();

                    if (line1 == null) {
                        eof = true;
                    }
                    else {
                        String[] fileText = new String[2];

                        fileText[0] = line1.substring(0, line1.indexOf(",", 0) );
                        fileText[1] = line1.substring(line1.indexOf(",", 0) + 1 );

                        if (fileText[0].equals("001")) {
                            byte[] encodedKey = Base64.decodeBase64(fileText[1]);
                            mKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
                        }
                        else {
                            String decryptedString = SchedEncryp.decrypt(fileText[1],mKey,ivSpec);
                            if (decryptedString != null) {
                                mGlobalArea.setPassword(fileText[0], decryptedString);
                            }
                        }
                    }
                }
                buffReader.close();
            } catch (IOException e) {
                EnterErrorEntry("ReadPassParams"," : Error:IOException..." + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
        	    EnterErrorEntry("ReadPassParams"," : Error:NoSuchAlgorithmException..." + e.getMessage());
            } catch (IllegalBlockSizeException e) {
        	    EnterErrorEntry("ReadPassParams"," : Error:IllegalBlockSizeException..." + e.getMessage());
            } catch (BadPaddingException e) {
        	    EnterErrorEntry("ReadPassParams"," : Error:BadPaddingException..." + e.getMessage());
            } catch (NoSuchPaddingException e) {
        	    EnterErrorEntry("ReadPassParams"," : Error:NoSuchPaddingException..." + e.getMessage());
            } catch (InvalidKeyException e) {
        	    EnterErrorEntry("ReadPassParams"," : Error:InvalidKeyException..." + e.getMessage());
            } catch (InvalidAlgorithmParameterException e) {
        	    EnterErrorEntry("ReadPassParams"," : Error:InvalidAlgorithmParameterException..." + e.getMessage());
            } finally {
                try {
                    if (buffWriter != null) buffWriter.close();
                } catch (IOException e) {
                    EnterErrorEntry("ReadPassParams"," : Error:IOException..." + e.getMessage());
                }
            }

        } catch (IOException e) {
            ;
        }
    }
    public static void writePassParams() {
        try {

            int mNoConnects = 0;
            for (int i1=0; i1 < mGlobalArea.sizeConnectionObj(); i1++) {
                SchedGlobalData.connectionItem m_ConnectionItem =
                        mGlobalArea.getConnectionObj(i1);

                if ((m_ConnectionItem.isSavePassword()) &&
                    (m_ConnectionItem.getPassword() != null)) mNoConnects = mNoConnects + 1;

            }

            if (mNoConnects > 0) {
                SecretKey mKey = SchedEncryp.generateKey();

                String mPassword_IvSpec = null;
                if (mGlobalArea.findParamItem(PASSWORD_IV_ID)) {
                    mPassword_IvSpec = mGlobalArea.getParamValue(PASSWORD_IV_ID);
                }
                else {
                    mPassword_IvSpec = "0102030405060708";
                }

                byte[]   iv = { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 };

                for (int mNo = 0; mNo < mPassword_IvSpec.length(); mNo++) {
                    try {
                        int mNumber = Integer.parseInt(mPassword_IvSpec.substring(mNo, mNo + 1));
                        iv[mNo] = (byte)mNumber;
                        if (mNo > 14) break;
                    } catch (NumberFormatException e) {
                        EnterErrorEntry("writePassParams"," : Error:NumberFormatException..." + e.getMessage());
                    }
                }
                // System.out.println( " Event  3 -");
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                mFileName = null;
                if (mGlobalArea.findParamItem(PASSWORD_FILE_ID)) {
                    mFileName = mGlobalArea.getParamValue(PASSWORD_FILE_ID);
                }
                else {
                    mFileName = DEFAULT_PASS_FILE;
                }
                fileWriter = new FileWriter(mFileName);
                buffWriter = new BufferedWriter(fileWriter);

                boolean firstWrite = true;
                for (int i1=0; i1 < mGlobalArea.sizeConnectionObj(); i1++) {
                    SchedGlobalData.connectionItem m_ConnectionItem =
                            mGlobalArea.getConnectionObj(i1);

                    if (m_ConnectionItem.isSavePassword()) {
                        if (m_ConnectionItem.getPassword() != null) {
                            if (firstWrite && (mKey != null)) {
                                String mStringKey = Base64.encodeBase64String(mKey.getEncoded());
                                buffWriter.write("001," + mStringKey);
                                buffWriter.newLine();
                                firstWrite = false;
                            }
                            String encryptedString = SchedEncryp.encrypt(m_ConnectionItem.getPassword(),mKey,ivSpec);
                            buffWriter.write(m_ConnectionItem.getName() + "," +
                                       encryptedString);
                            buffWriter.newLine();
                        }
                    }
                }

                buffWriter.close();
            }
        } catch (IOException e) {
            EnterErrorEntry("WritePassParams"," : Error:IOException..." + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
        	EnterErrorEntry("WritePassParams"," : Error:NoSuchAlgorithmException..." + e.getMessage());
        } catch (IllegalBlockSizeException e) {
        	EnterErrorEntry("WritePassParams"," : Error:IllegalBlockSizeException..." + e.getMessage());
        } catch (BadPaddingException e) {
        	EnterErrorEntry("WritePassParams"," : Error:BadPaddingException..." + e.getMessage());
        } catch (NoSuchPaddingException e) {
        	EnterErrorEntry("WritePassParams"," : Error:NoSuchPaddingException..." + e.getMessage());
        } catch (InvalidKeyException e) {
        	EnterErrorEntry("WritePassParams"," : Error:InvalidKeyException..." + e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
        	EnterErrorEntry("WritePassParams"," : Error:InvalidAlgorithmParameterException..." + e.getMessage());
        } finally {
            try {
                if (buffWriter != null) buffWriter.close();
            } catch (IOException e) {
                EnterErrorEntry("WritePassParams"," : Error:IOException..." + e.getMessage());
            }
        }
    }
}

