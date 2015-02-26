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

class SchedProgramArgInputScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton                         mEnter, mCancel, mLeft, mRight;
    private JTabbedPane                     mTabPane;
    private JPanel                          dataPane, dataPanel;
    private JPanel                          dataPane514, dataPane522;
    private SchedInpScreenArea              mInpScreens;
    private SchedInpScreenArea.PaneObject   mPane, mPane514, mPane522;
    private SchedGlobalData                 mArea;
    private int                             currentScreenId, oldScreenId;
    private int                             mItemNo;

    private SchedInpScreenArea.PaneObject.TextItem       mTextItem;
    private SchedInpScreenArea.PaneObject.TextAreaItem   mTextAreaItem;

    private Scheduler                       mParentFrame;
    private SchedDataArea                   mDataArea;
    private boolean                         mDispose;
    private String[]                        Str10;
    private SchedDataArea.ProgramArgsItem   mProgramArgsItem;
    private ClassLoader  cl;
    private SchedDataArea.ProgramItem       mProgramItem;

    public SchedProgramArgInputScreen(Scheduler                 parentFrame,
                                    SchedDataArea               dataArea,
                                    SchedGlobalData             Area,
                                    SchedDataArea.ProgramItem   ProgramItem,
                                    SchedInpScreenArea          InpScreens)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        mParentFrame = parentFrame;
        mDataArea = dataArea;
        mProgramItem = ProgramItem;

        mInpScreens = InpScreens;
        mArea = Area;
        mDataArea.setReturnNo(1);

        // setTitle( "Scheduler FE Grid" );
        setSize( 620, 460 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        // this.setLocation(150,100);
        this.setLocation((int)mParentFrame.getLocationOnScreen().getX() + 160,
                         (int)mParentFrame.getLocationOnScreen().getY() + 100);

        dataPane = new JPanel();
        dataPane.setSize(700,500);
        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        dataPane.setBackground(mArea.getScreenColor(42));

        SpringLayout lm = new SpringLayout();
        dataPane.setLayout(lm);

        UIManager.put("TabbedPane.selected", mArea.getScreenColor(42));
        mTabPane = new JTabbedPane();

        dataPane514 = new JPanel();
        dataPane514 = setupPane(514);
        mTabPane.add("Option 1", dataPane514);
        mTabPane.setMnemonicAt(0,KeyEvent.VK_1);
        mTabPane.setBackgroundAt(0, mArea.getScreenColor(42));

        dataPane522 = new JPanel();
        dataPane522 = setupPane(522);
        mTabPane.add("Option 2", dataPane522);
        mTabPane.setMnemonicAt(1,KeyEvent.VK_2);
        mTabPane.setBackgroundAt(1, mArea.getScreenColor(42));

        dataPane.add(mTabPane,
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(0),
                                                 Spring.constant(606),
                                                 Spring.constant(370)));



        dataPane.add(setupButtons(),
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(370),
                                                 Spring.constant(606),
                                                 Spring.constant(50)));;

        getContentPane().add( dataPane );
    }

    private JPanel setupPane(int mScreenNo) {

        int mScreenId = mInpScreens.getScreenId(mScreenNo, mDataArea.getVersion());

        mPane = mInpScreens.getScreen(mScreenId);

        if (mScreenId == 514) {
            mPane514 = mInpScreens.getScreen(mScreenId);
        }
        if (mScreenId == 522) {
            mPane522 = mInpScreens.getScreen(mScreenId);
        }


        // Border blackline = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        dataPanel = new JPanel();
        dataPanel.setSize( 620, 380 );

        // dataPanel.setBorder(blackline);
        dataPanel.setLayout(new SpringLayout());

        dataPanel.setBackground(mArea.getScreenColor(mPane.getBgrndColour()));
        dataPanel.setForeground(mArea.getScreenColor(mPane.getFgrndColour()));

        cl = this.getClass().getClassLoader();
        Icon LookIcon = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));

        for (int i2 = 0; i2 < mPane.sizeLabelObj(); i2++) {

            SchedInpScreenArea.PaneObject.LabelItem m_P1 = mPane.getLabelObj(i2);
            dataPanel.add(m_P1,
                        new SpringLayout.Constraints(Spring.constant(m_P1.get_XPoint()),
                                                 Spring.constant(m_P1.get_YPoint()),
                                                 Spring.constant(m_P1.get_Width()),
                                                 Spring.constant(m_P1.get_Height())));
        }

        for (int i3 = 0; i3 < mPane.sizeTextObj(); i3++) {

            SchedInpScreenArea.PaneObject.TextItem m_T1 = mPane.getTextObj(i3);
            if (m_T1.get_Combo().equals("Y")) {
                if (m_T1.get_ComboId() < 50) 
                    dataPanel.add(m_T1.getComboBoxType1(mArea),
                        new SpringLayout.Constraints(Spring.constant(m_T1.get_XPoint()),
                                                     Spring.constant(m_T1.get_YPoint()),
                                                     Spring.constant(m_T1.get_Width()),
                                                     Spring.constant(m_T1.get_Height())));
                else
                    dataPanel.add(m_T1.getComboBoxType2(mDataArea),
                        new SpringLayout.Constraints(Spring.constant(m_T1.get_XPoint()),
                                                     Spring.constant(m_T1.get_YPoint()),
                                                     Spring.constant(m_T1.get_Width()),
                                                     Spring.constant(m_T1.get_Height())));
            }
            else {

                if (m_T1.get_FormatType() == 1) {
                    m_T1.setZero();
                }
                else {
                    m_T1.update_Text("");
                }

                dataPanel.add(m_T1,
                        new SpringLayout.Constraints(Spring.constant(m_T1.get_XPoint()),
                                                     Spring.constant(m_T1.get_YPoint()),
                                                     Spring.constant(m_T1.get_Width()),
                                                     Spring.constant(m_T1.get_Height())));
                if (m_T1.get_Button().equals("Y")) {
                    JButton m_B1 = new JButton(LookIcon);

                    if (m_T1.get_BackColor() > 0) {
                        Color mColor = mArea.getScreenColor(m_T1.get_BackColor());
                        if (mColor != null) {
                            m_B1.setOpaque(true);
                            m_B1.setBackground(mPane.getBColor());
                        }
                    }
                    dataPanel.add(m_B1,
                           new SpringLayout.Constraints(
                               Spring.constant(m_T1.get_XPoint() + m_T1.get_Width() + 5),
                               Spring.constant(m_T1.get_YPoint()),
                               Spring.constant(30),
                               Spring.constant(m_T1.get_Height())));
                    m_B1.addActionListener( this );
                    m_B1.setActionCommand( String.valueOf("Edit" + i3) );
                }
            }
        }
        for (int i4 = 0; i4 < mPane.sizeTextAreaObj(); i4++) {

            SchedInpScreenArea.PaneObject.TextAreaItem m_T2 = mPane.getTextAreaObj(i4);
            m_T2.update_Text("");

            dataPanel.add(m_T2,
                        new SpringLayout.Constraints(Spring.constant(m_T2.get_XPoint()),
                                                     Spring.constant(m_T2.get_YPoint()),
                                                     Spring.constant(m_T2.get_Width()),
                                                     Spring.constant(m_T2.get_Height())));
        }
        return dataPanel;
    }

    private JPanel setupButtons() {

        JPanel dataPane3 = new JPanel();
        dataPane3.setSize( 620, 60 );
        dataPane3.setLayout(new SpringLayout());
        dataPane3.setBackground(mArea.getScreenColor(42));

        // Add button to screen.
        mEnter = new JButton("  OK  ");
        mEnter.setBackground(mArea.getButtonColor());
        mEnter.addActionListener( this );
        mEnter.setActionCommand("OK");
        mEnter.setMnemonic(KeyEvent.VK_O);

        dataPane3.add(mEnter, new SpringLayout.Constraints(Spring.constant(200),
                                                 Spring.constant(10),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));

        mCancel = new JButton("Cancel");
        mCancel.setBackground(mArea.getButtonColor());
        mCancel.addActionListener( this );
        mCancel.setActionCommand("Exit");
        mCancel.setMnemonic(KeyEvent.VK_C);

        dataPane3.add(mCancel, new SpringLayout.Constraints(Spring.constant(320),
                                                 Spring.constant(10),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        return dataPane3;
    }

    public void CreateProgramArg1() {

        Str10 = new String[4];

        for (int i4 = 0; i4 < 4; i4++) {
            mTextItem = mPane514.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else
                Str10[i4] = mTextItem.get_Text();
        }

        int Int1 = Integer.parseInt(Str10[1]);
        if (mProgramItem.getNumberOfArguments() < Int1) {
            mParentFrame.errorBox("Invalid Argument Number - Check Program No. of Arguments");
            mDispose = true;
        }
        else {
            String mProgramName = mProgramItem.getOwner() + "." + mProgramItem.getProgramName();


            int returnValue = mDataArea.CreateProgramArg(mProgramName, Str10[0], Int1, Str10[2], Str10[3]);

            if (returnValue == 0) {
                mProgramArgsItem = mDataArea.GetProgramArgsItem(
                                                   mProgramItem.getOwner(),
                                                   mProgramItem.getProgramName(),
                                                   Int1, 0);
                if (mProgramArgsItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Program Argument from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createProgramArgsTreeItem(mProgramArgsItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    public void CreateProgramArg2() {
        Str10 = new String[3];
        for (int i4 = 0; i4 < 3; i4++) {
            mTextItem = mPane522.getTextObj(i4);
            if ( mTextItem.get_Combo().equals("Y") )
                Str10[i4] = mTextItem.getSelectedItem();
            else 
                Str10[i4] = mTextItem.get_Text();
        }

        int Int1 = Integer.parseInt(Str10[1]);
        if (mProgramItem.getNumberOfArguments() < Int1) {
            mParentFrame.errorBox("Invalid Argument Number - Check Program No. of Arguments");
            mDispose = true;
        }
        else {
            String mProgramName = mProgramItem.getOwner() + "." + mProgramItem.getProgramName();

            int returnValue = mDataArea.CreateMetadataArg(mProgramName, Str10[0], Int1, Str10[2]);

            if (returnValue == 0) {
                mProgramArgsItem = mDataArea.GetProgramArgsItem(
                                                  mProgramItem.getOwner(),
                                                  mProgramItem.getProgramName(),
                                                  Int1, 0);
                if (mProgramArgsItem == null) {
                    mParentFrame.errorBox("Error - Cannot read Metadata Argument from Database.");
                    mDispose = false;
                }
                else {
                    mParentFrame.createProgramArgsTreeItem(mProgramArgsItem);
                }
            }
            else {
                mParentFrame.errorBox(mDataArea.getSysMessage().toString());
                mDispose = false;
            }
        }
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getActionCommand().equals("OK") ) {

            mDispose = true;

            switch ( mTabPane.getSelectedIndex() ) {
                case 0:
                    CreateProgramArg1();
                    break;
                case 1:
                    CreateProgramArg2();
                    break;
            }

            mDataArea.setReturnNo(0);
            if (mDispose) {
                dispose();
            }
        }
        if ( e.getActionCommand().equals("Exit") ) {
            mDataArea.setReturnNo(1);
            dispose();
        }
        if ( e.getActionCommand().startsWith("Edit") ) {
            mItemNo = Integer.parseInt( e.getActionCommand().substring(4) );
            switch ( mTabPane.getSelectedIndex() ) {
                case 0:
                    mTextItem = mPane514.getTextObj(mItemNo);
                    break;
                case 1:
                    mTextItem = mPane522.getTextObj(mItemNo);
                    break;
            }
            SchedDataInputScreen dataDialog = 
                    new SchedDataInputScreen(this, mTextItem );
            dataDialog.setVisible( true );

        }
    }
}
