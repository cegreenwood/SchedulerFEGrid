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

class SchedUpdate extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private String                 mHeader;
    private JTextField             columnValue;
    private JTextArea              message_1;
    private int                    mColumnId;
    private String                 mColumnName;
    private String                 mStringValue;
    private JPanel                 updatePane;

    private JComboBox<String>      comboColumn1, comboColumn2;
    private JButton                mEnter, mCancel;
    private JButton                m_B1;
    private Icon                   LookIcon1, LookIcon2;

    // Instance attributes used in this dialog.
    public Scheduler        mParentFrame;

    private SchedScreenArea                            mScreens;
    private SchedScreenArea.PaneObject                 mPane;
    private SchedScreenArea.PaneObject.ColumnItem      mColumnItem;
    private SchedScreenArea.PaneObject.TextItem        mTextItem;
    private SchedScreenArea.PaneObject.TextAreaItem    mTextAreaItem;

    private Vector<SchedScreenArea.PaneObject.ColumnItem>   columnObj;

    private SchedDataArea                              mDataArea;
    private SchedDataNode                              mCurrentNode;
    private SchedGlobalData                            mArea;

    private SchedDateTime                              mSchedDateTime;
    private JFormattedTextField                        mIntegerField;


    private String         mObjectName;
    private boolean        mCombo = false;
    private boolean        mDateTime = false;
    private boolean        mTextAreaObj;
    private boolean        mFound;
    private int            mSelectColNo;
    private int            mIntValue;
    private int            mOptionNo;
    private JLabel         label_1, label_2, label_3;

    private ClassLoader  cl;

    // Dialog constructor.
    public SchedUpdate(Scheduler        parentFrame,
                       int              optionNo,
                       SchedDataNode    currentNode,
                       SchedDataArea    dataArea,
                       SchedGlobalData  Area,
                       SchedScreenArea  Screens,
                       SchedScreenArea.PaneObject currentPane)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        // Save the owner frame in case we need it later.
        mParentFrame = parentFrame;
        mOptionNo = optionNo;
        mDataArea = dataArea;
        mCurrentNode = currentNode;
        mArea = Area;
        mScreens = Screens;
        mPane = currentPane;

        // Set the characteristics for this dialog instance.
        setSize( 400, 280 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        this.setLocation((int)parentFrame.getLocationOnScreen().getX() + 160,
                         (int)parentFrame.getLocationOnScreen().getY() + 100);

        cl = this.getClass().getClassLoader();
        LookIcon1 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook1.jpg"));
        LookIcon2 = new ImageIcon(cl.getResource(SchedConsts.PACKAGE_DIR + "IconLook2.jpg"));

        // Create a panel for the components.
        updatePane = new JPanel();
        updatePane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        updatePane.setLayout(new SpringLayout());
        updatePane.setBackground(mArea.getScreenColor(42));

        mHeader = "Update - " + currentNode.getNodeName();
        label_1 = new JLabel(mHeader);
        updatePane.add(label_1, new SpringLayout.Constraints(
                                                 Spring.constant(40),
                                                 Spring.constant(30),
                                                 Spring.constant(240),
                                                 Spring.constant(20)));

        label_2 = new JLabel("Column Name:");
        updatePane.add(label_2, new SpringLayout.Constraints(
                                                 Spring.constant(40),
                                                 Spring.constant(70),
                                                 Spring.constant(100),
                                                 Spring.constant(20)));

        comboColumn1 = new JComboBox<String>();
        // comboColumn1.setPreferredSize( new Dimension(200,20) );
        comboColumn1.addItem("Select Column...");
        comboColumn1.addActionListener( this );
        comboColumn1.setActionCommand("Column");

        columnObj  = new Vector<SchedScreenArea.PaneObject.ColumnItem>(10,10);
        for (int r1 = 0; r1 < mPane.sizeColumnObj(); r1++) {
            mColumnItem = mPane.getColumnObj(r1);
            // System.out.println(" X1." + mColumnItem.getColumnNameDesc() + "--");
            if (mColumnItem.getOptionId() == mOptionNo) {
                columnObj.add(mColumnItem);
                comboColumn1.addItem(mColumnItem.getColumnNameDesc());
            }
        }

        updatePane.add(comboColumn1, new SpringLayout.Constraints(
                                                 Spring.constant(150),
                                                 Spring.constant(70),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        label_3 = new JLabel("Column Value:");
        updatePane.add(label_3, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(110),
                                                 Spring.constant(100),
                                                 Spring.constant(20)));
        label_3.setVisible(false);

        mIntegerField = new JFormattedTextField(NumberFormat.getIntegerInstance());

        mIntegerField.setColumns(6);

        mIntegerField.setEditable(false);
        mIntegerField.setFocusable(false);
        mIntegerField.setVisible(false);
        mIntegerField.setFocusLostBehavior(JFormattedTextField.COMMIT);
        mIntegerField.setHorizontalAlignment(JTextField.RIGHT);
        updatePane.add(mIntegerField, new SpringLayout.Constraints(
                                                 Spring.constant(150),
                                                 Spring.constant(110),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));

        columnValue = new JTextField(30);
        columnValue.setEditable(false);
        columnValue.setFocusable(false);
        columnValue.setVisible(false);


        updatePane.add(columnValue, new SpringLayout.Constraints(
                                                 Spring.constant(150),
                                                 Spring.constant(110),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        m_B1 = new JButton();

        m_B1.setOpaque(true);
        m_B1.setBackground(mArea.getButtonColor());
        m_B1.setEnabled(false);
        m_B1.setVisible(false);

        updatePane.add(m_B1,new SpringLayout.Constraints(
                                Spring.constant(332),
                                Spring.constant(110),
                                Spring.constant(30),
                                Spring.constant(20)));
        m_B1.addActionListener( this );
        m_B1.setActionCommand( "Edit" );

        comboColumn2 = new JComboBox<String>();
        // comboColumn2.setPreferredSize( new Dimension(200,20) );
        comboColumn2.addItem("Select Column...");
        comboColumn2.addActionListener( this );
        comboColumn2.setActionCommand("Text");
        comboColumn2.setVisible(false);
        comboColumn2.setEnabled(false);

        updatePane.add(comboColumn2, new SpringLayout.Constraints(
                                                 Spring.constant(150),
                                                 Spring.constant(110),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        message_1 = new JTextArea();
        message_1.setEditable(false);
        message_1.setLineWrap(true);
        message_1.setBackground(mArea.getScreenColor(42));

        updatePane.add(message_1, new SpringLayout.Constraints(Spring.constant(30),
                                                 Spring.constant(140),
                                                 Spring.constant(320),
                                                 Spring.constant(50)));

        mEnter = new JButton(" Save ");
        mEnter.setBackground(mArea.getButtonColor());
        updatePane.add(mEnter, new SpringLayout.Constraints(Spring.constant(80),
                                                 Spring.constant(200),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter.addActionListener( this );
        mEnter.setActionCommand("Save");
        mEnter.setMnemonic(KeyEvent.VK_S);

        mCancel = new JButton("Cancel");
        mCancel.setBackground(mArea.getButtonColor());
        updatePane.add(mCancel, new SpringLayout.Constraints(Spring.constant(200),
                                                 Spring.constant(200),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mCancel.addActionListener( this );
        mCancel.setActionCommand("Cancel");
        mCancel.setMnemonic(KeyEvent.VK_C);

        getContentPane().add( updatePane );

    }

    public void updateText(String mString) {
        columnValue.setText( mString );
    }
    public void updateValue(int mInt) {
        mIntValue = mInt;
    }

    public void actionPerformed( ActionEvent e ) {
      // System.out.println( " Event  1 -" + e.getActionCommand());

      if ( e.getActionCommand().equals("Save") ) {
          boolean mContinue = true;

          if ( mCombo ) {
              mStringValue = (String)comboColumn2.getSelectedItem();
          }
          else {
              if ( mDateTime ) {
                  mStringValue = mSchedDateTime.getDateString();
              }
              else {
                  if ( mTextAreaObj ) {
                      mStringValue = columnValue.getText();
                  }
                  else {
                      if (mTextItem.get_FormatType() == 1) {
                          try {
                              mIntegerField.commitEdit();
                              mStringValue = mIntegerField.getText();
                          }
                          catch(ParseException e1) {
                              mParentFrame.errorBox("Error - Invalid Number Entered.");
                              mContinue = false;
                          }
                      }
                      else {
                          if (mTextItem.get_RowType() == 5) {
                              mStringValue = Integer.toString(mIntValue);
                          }
                          else {
                              mStringValue = columnValue.getText();
                          }
                      }
                  }
              }
          }
          if (mContinue) {

              int returnValue = 0;
              switch ( mCurrentNode.getScreenNo() ) {
                case SchedConsts.JOB_ARGS_SCREEN_NO:
                  SchedDataArea.JobArgsItem mJobArgsItem =
                      mDataArea.getJobArgsId( mCurrentNode.getNodeId() );
                  mObjectName = mJobArgsItem.getOwner() + "." + mJobArgsItem.getJobName();
                  int mArgPos = mJobArgsItem.getArgumentPosition();
                  returnValue = mDataArea.CreateJobArg(mObjectName,
                                                  mArgPos,
                                                  mStringValue);
                  break;
                case SchedConsts.CHAIN_STEPS_SCREEN_NO:
                  SchedDataArea.ChainStepsItem mChainStepsItem =
                      mDataArea.getChainStepsId( mCurrentNode.getNodeId() );
                  mObjectName = mChainStepsItem.getOwner() + "." + mChainStepsItem.getChainName();
                  String mStepName = mChainStepsItem.getStepName();
                  returnValue = mDataArea.UpdateChain(mObjectName,
                                                  mStepName,
                                                  mColumnName,
                                                  mStringValue);
                  break;
                case SchedConsts.GLOBAL_ATTRIBUTES_SCREEN_NO:

                  mObjectName = mCurrentNode.getNodeName();
                  returnValue = mDataArea.UpdateGlobalAttribute(
                                                   mObjectName, 
                                                   mStringValue);
                  break;
                case SchedConsts.PLAN_SCREEN_NO:
                  mObjectName = mCurrentNode.getNodeName();
                  mColumnItem = columnObj.get(mSelectColNo);
                  if (mOptionNo == 1) {
                      // System.out.println( " Event  1 -" + mObjectName + "--" +
                      //                                   mColumnItem.getColumnName() + "--" +
                      //                                   mStringValue + "--");

                      returnValue = mDataArea.UpdateResourcePlan(
                                                   mObjectName,
                                                   mColumnItem.getColumnName(), 
                                                   mStringValue);
                  }
                  else {

                      if ( mColumnItem.getColumnType().equals("B") ) {
                          returnValue = mDataArea.UpdateResourceDirectiveBoolean(
                                                   mObjectName,
                                                   mPane.getPlanDirective().getGroup(),
                                                   mColumnName,
                                                   mStringValue);
                      }
                      if ( mColumnItem.getColumnType().equals("S") ) {
                          returnValue = mDataArea.UpdateResourceDirectiveString(
                                                   mObjectName,
                                                   mPane.getPlanDirective().getGroup(),
                                                   mColumnName,
                                                   mStringValue);
                      }
                  }
                  break;
                case SchedConsts.CDB_PLAN_SCREEN_NO:
                  mObjectName = mCurrentNode.getNodeName();
                  mColumnItem = columnObj.get(mSelectColNo);
                  if (mOptionNo == 1) {
                      // System.out.println( " Event  1 -" + mObjectName + "--" +
                      //                                   mColumnItem.getColumnName() + "--" +
                      //                                   mStringValue);
                      returnValue = mDataArea.UpdateCdbResourcePlan(
                                               mObjectName,
                                               mColumnItem.getColumnName(), 
                                               mStringValue);
                  }
                  else {
                      // System.out.println( " Event  1 -" + mObjectName + "--" +
                      //                                   mPane.getCdbPlanDirective().getPluggableDatabase() + "--" +
                      //                                   mColumnName + "--" +
                      //                                   mStringValue);
                      returnValue = mDataArea.UpdateCdbResourceDirective(
                                               mObjectName,
                                               mPane.getCdbPlanDirective().getPluggableDatabase(),
                                               mColumnName,
                                               mStringValue);
                  }
                  break;
                case SchedConsts.CONSUMER_GROUP_SCREEN_NO:
                  // System.out.println( " Event  1 -");

                  if (mOptionNo == 1) {
                      mObjectName = mCurrentNode.getNodeName();
                      returnValue = mDataArea.UpdateConsumerGroup(
                                                   mObjectName, 
                                                   mStringValue);
                  }
                  else {
                      mObjectName = mCurrentNode.getNodeName();
                      // System.out.println( " Event  1 -");
                      // System.out.println( " Event  2 -" + mObjectName );
                      // System.out.println( " Event  3 -" + mColumnName + "--" + mStringValue);

                      returnValue = mDataArea.SetConsumerGroupMapping(
                                                   mObjectName, mColumnName, mStringValue);
                      if (returnValue == 0) {
                          mPane.removeGroupMapping(mPane.getGroupMappingsItem().getAttribute(),
                                               mPane.getGroupMappingsItem().getValue());
                          SchedDataArea.GroupMappingsItem mGroupMappingsItem = 
                                                   mDataArea.GetGroupMappingsItem(
                                                   mPane.getGroupMappingsItem().getAttribute(),
                                                   mPane.getGroupMappingsItem().getValue(), 1);
                      }
                  }
                  break;
                default:
                  mObjectName = mCurrentNode.getOwner() + "." + mCurrentNode.getNodeName();

                  if (( mStringValue == null ) || 
                      ( mStringValue.trim().length() == 0 ) || 
                      ( mStringValue.equals("Select Column...") ) )
                  {
                      returnValue = mDataArea.UpdateObjectNull(mObjectName, 
                                                           mColumnName);
                      mStringValue = null;
                  }
                  else {

                      mColumnItem = columnObj.get(mSelectColNo);

                      if ( mColumnItem.getColumnType().equals("S") ) {
                          returnValue = mDataArea.UpdateStrObject(
                                                   mCurrentNode.getOwner(),
                                                   mCurrentNode.getNodeName(), 
                                                   mColumnName,
                                                   mStringValue);
                      }
                      if ( mColumnItem.getColumnType().equals("T") ) {
                          returnValue = mDataArea.UpdateDateObject(
                                                   mCurrentNode.getOwner(),
                                                   mCurrentNode.getNodeName(), 
                                                   mColumnName,
                                                   mStringValue);
                      }
                      if ( mColumnItem.getColumnType().equals("L") ) {
                          returnValue = mDataArea.UpdateLoggingObject(
                                                   mCurrentNode.getOwner(),
                                                   mCurrentNode.getNodeName(), 
                                                   mColumnName,
                                                   mStringValue);
                      }
                      if ( mColumnItem.getColumnType().equals("B") ) {
                          returnValue = mDataArea.UpdateBooleanObject(
                                                   mCurrentNode.getOwner(),
                                                   mCurrentNode.getNodeName(), 
                                                   mColumnName,
                                                   mStringValue);
                      }
                  }
              }
              if (returnValue == 0) {
                  mParentFrame.updateNode();
                  mParentFrame.refreshNode();
                  // mParentFrame.UpdateScreenObject(mStringValue, mColumnId);
                  dispose();
              }
              else {
                  mParentFrame.errorBox(mDataArea.getSysMessage().toString());
              }
          }
      }
      if ( e.getActionCommand().equals("Cancel") ) {
          dispose();
      }
      if ( e.getActionCommand().equals("Edit") ) {
          if (mTextItem.get_RowType() == 5) {
              SchedEventEditScreen eventFrame = new SchedEventEditScreen(
                                 this,
                                 mTextItem,
                                 this.getLocationOnScreen().getX(),
                                 this.getLocationOnScreen().getY());
              eventFrame.setVisible( true );
          }
          else {
              SchedDataEditScreen dataDialog = 
                      new SchedDataEditScreen( this, mArea, columnValue.getText() );
              dataDialog.setVisible( true );
          }
      }
      if ( e.getActionCommand().equals("Column") ) {

          // mPane = mDataArea.getPane();
          mTextAreaObj = false;

          if ( ! comboColumn1.getSelectedItem().equals("Select Column...") ) {

              label_3.setVisible(true);
              mSelectColNo = comboColumn1.getSelectedIndex();

              if (mSelectColNo >= 0) {
                  if (mSelectColNo > 0) {
                      mSelectColNo = mSelectColNo - 1;
                  }
                  mColumnItem = columnObj.get(mSelectColNo);
                  mColumnName = mColumnItem.getColumnName();
                  mColumnId = mColumnItem.getColumnId();
              }
              // System.out.println(" D2." + mColumnName + "--" + mColumnId);
              // System.out.println(" D3." + mPane.getScreenId());

              mFound = false;
              for (int r2 = 0; r2 < mPane.sizeTextObj(); r2++) {
                  mTextItem = mPane.getTextObj(r2);
                  // System.out.println(" D4." + mTextItem.get_Description() + "--" +
                  //                             mTextItem.get_Text() + "--" + mTextItem.get_ItemId());

                  if (mTextItem.get_ItemId() == mColumnId) {
                      mCombo = false;
                      mFound = true;
                      comboColumn1.setEnabled(false);

                      if ( mTextItem.get_RowType() == 5 ) {
                          columnValue.setEditable(false);
                          columnValue.setFocusable(false);
                          columnValue.setVisible(true);
                          columnValue.setText( mTextItem.getText() );

                          m_B1.setIcon(LookIcon1);
                          m_B1.setEnabled(true);
                          m_B1.setVisible(true);

                          break;
                      }

                      if ( mTextItem.get_Combo().equals("Y") ) {
                          mCombo = true;
                          comboColumn2.setVisible(true);
                          comboColumn2.setEnabled(true);

                          int i3 = 0;
                          int i4 = 1;

                          if (mTextItem.get_ComboId() < 50) {
                              for (int r3 = 0; r3 < mArea.sizeComboObj(); r3++) {
                                  SchedGlobalData.screenCombo mComboObj1 = mArea.getComboObj(r3);
                                  if ( mComboObj1.getComboId() == mTextItem.get_ComboId() ) {
                                      comboColumn2.addItem(mComboObj1.getColumnText());
                                  }
                              }
                          }
                          else {
                              for (int r3 = 0; r3 < mDataArea.sizeComboObj(); r3++) {
                                  SchedDataArea.screenCombo mComboObj2 = mDataArea.getComboObj(r3);
                                  if ( mComboObj2.getComboId() == mTextItem.get_ComboId() ) {
                                      if (mTextItem.get_ComboId() == 53 ||
                                          mTextItem.get_ComboId() == 54 ||
                                          mTextItem.get_ComboId() == 55) {
                                          if (mComboObj2.getOwner().equals(mDataArea.getUserName().toUpperCase())) {
                                              i3 = i3 + 1;
                                              comboColumn2.insertItemAt(mComboObj2.getColumnText(),i3);
                                          }
                                          else {
                                              comboColumn2.insertItemAt(mComboObj2.getOwner() + "." +
                                                   mComboObj2.getColumnText(), i4);
                                          }
                                          i4 = i4 + 1;
                                      }
                                      else {
                                          comboColumn2.addItem(mComboObj2.getColumnText());
                                      }
                                  }
                              }
                          }
                          break;
                      }

                      if ( mTextItem.get_RowType() == 4 ) {
                          mDateTime = true;
                          mSchedDateTime = new SchedDateTime();
                          mSchedDateTime.initObj(mArea, mTextItem.getText());
                          // mSchedDateTime.checkTimePanel();
                          updatePane.add(mSchedDateTime, new SpringLayout.Constraints(
                                                 Spring.constant(150),
                                                 Spring.constant(110),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));
                          break;
                      }

                      // If the item is a numeric item.
                      if (mTextItem.get_FormatType() == 1) {
                          mIntegerField.setValue(new Integer(Integer.parseInt( mTextItem.getText() )));

                          mIntegerField.setEditable(true);
                          mIntegerField.setFocusable(true);
                          mIntegerField.setVisible(true);
                      }
                      else {
                          columnValue.setEditable(true);
                          columnValue.setFocusable(true);
                          columnValue.setVisible(true);
                          columnValue.setText( mTextItem.getText() );
                          m_B1.setIcon(LookIcon1);
                          m_B1.setEnabled(true);
                          m_B1.setVisible(true);
                      }
                      break;
                  }
              }
              //
              // No textItem found. Search through the textAreaItems.
              //
              if ( ! mFound) {
                  for (int r2 = 0; r2 < mPane.sizeTextAreaObj(); r2++) {
                      mTextAreaItem = mPane.getTextAreaObj(r2);

                      if (mTextAreaItem.get_ItemId() == mColumnId) {
                          mCombo = false;
                          mTextAreaObj = true;

                          comboColumn1.setEnabled(false);

                          columnValue.setEditable(true);
                          columnValue.setFocusable(true);
                          columnValue.setVisible(true);
                          columnValue.setText( mTextAreaItem.getText() );
                          m_B1.setIcon(LookIcon1);
                          m_B1.setEnabled(true);
                          m_B1.setVisible(true);

                          break;
                      }
                  }
              }
          }
      }
    }
}

