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

class SchedConnect extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JTextField      connectionName, userName;
    private JTextField      hostName, databaseName, portName;
    private JPasswordField  password;

    private SchedGlobalData.connectionItem       mConnectionItem;

    private JTextArea       mSelect_1, mSelect_2, mSelect_3, mSelect_4, mSelect_5, mSelect_6;

    private String          m_userName;

    private JCheckBox       mCheckBox1, mCheckBox2, mCheckBox3;
    private JButton         mEnter1, mEnter2, mEnter3, mCancel1, mCancel2, mCancel3;
    private JTabbedPane     mTabPane;
    private JPanel          optionPane1, optionPane2, optionPane3;

    // Instance attributes used in this dialog.
    private Scheduler        mParentFrame;
    private SchedGlobalData  mArea;
    private int              mConnId;
    private int              mOptionId;

    // Dialog constructor.
    public SchedConnect(Scheduler parentFrame,
                        SchedGlobalData  Area,
                        int  OptionId)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        // Save the owner frame in case we need it later.
        mParentFrame = parentFrame;
        mArea = Area;
        mOptionId = OptionId;

        // Set the characteristics for this dialog instance.
        setTitle( "Connection Screen" );
        setSize( 400, 450 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        this.setLocation((int)parentFrame.getLocationOnScreen().getX() + 120,
                         (int)parentFrame.getLocationOnScreen().getY() + 80);

        // Create the tabbed pane.
        mTabPane = new JTabbedPane();

        // Create a panel for the components.
        optionPane1 = new JPanel();
        optionPane1.setLayout(new SpringLayout());
        optionPane1.setBackground( mArea.getBackgroundColor() );

        JLabel label_1 = new JLabel("Connection Name:");
        optionPane1.add(label_1, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(40),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));
        JLabel label_2 = new JLabel("Username:");
        optionPane1.add(label_2, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(70),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));

        JLabel label_3 = new JLabel("Password:");
        optionPane1.add(label_3, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(100),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));

        JLabel label_4 = new JLabel("Host:");
        optionPane1.add(label_4, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(130),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));
        JLabel label_5 = new JLabel("Port:");
        optionPane1.add(label_5, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(160),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));
        JLabel label_6 = new JLabel("Database:");
        optionPane1.add(label_6, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(190),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));

        connectionName = new JTextField(10);
        optionPane1.add(connectionName, new SpringLayout.Constraints(Spring.constant(170),
                                                 Spring.constant(40),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));
        userName = new JTextField(10);
        optionPane1.add(userName, new SpringLayout.Constraints(Spring.constant(170),
                                                 Spring.constant(70),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));

        // password = new JPasswordField(10);
        // optionPane1.add(password, new SpringLayout.Constraints(Spring.constant(170),
        //                                          Spring.constant(100),
        //                                          Spring.constant(120),
        //                                          Spring.constant(20)));


        hostName = new JTextField(10);
        optionPane1.add(hostName, new SpringLayout.Constraints(Spring.constant(170),
                                                 Spring.constant(130),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));

        portName = new JTextField(10);
        optionPane1.add(portName, new SpringLayout.Constraints(Spring.constant(170),
                                                 Spring.constant(160),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));
        databaseName = new JTextField(10);
        optionPane1.add(databaseName, new SpringLayout.Constraints(Spring.constant(170),
                                                 Spring.constant(190),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));

        mCheckBox1 = new JCheckBox("Logon as Sysdba");
        mCheckBox1.setBackground( mArea.getBackgroundColor() );
        optionPane1.add(mCheckBox1, new SpringLayout.Constraints(Spring.constant(120),
                                                 Spring.constant(230),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        mCheckBox2 = new JCheckBox("Save Password");
        mCheckBox2.setBackground( mArea.getBackgroundColor() );
        optionPane1.add(mCheckBox2, new SpringLayout.Constraints(Spring.constant(120),
                                                 Spring.constant(260),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        mCheckBox3 = new JCheckBox("Automatic Logon");
        mCheckBox3.setBackground( mArea.getBackgroundColor() );
        optionPane1.add(mCheckBox3, new SpringLayout.Constraints(Spring.constant(120),
                                                 Spring.constant(290),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        mEnter1 = new JButton("  OK  ");
        mEnter1.setBackground( mArea.getButtonColor() );
        optionPane1.add(mEnter1, new SpringLayout.Constraints(Spring.constant(80),
                                                 Spring.constant(340),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter1.addActionListener( this );
        mEnter1.setActionCommand("OK");
        mEnter1.setMnemonic(KeyEvent.VK_O);

        mCancel1 = new JButton("Cancel");
        mCancel1.setBackground( mArea.getButtonColor() );
        optionPane1.add(mCancel1, new SpringLayout.Constraints(Spring.constant(200),
                                                 Spring.constant(340),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mCancel1.addActionListener( this );
        mCancel1.setActionCommand("Cancel");
        mCancel1.setMnemonic(KeyEvent.VK_C);

        mTabPane.add("General", optionPane1);
        mTabPane.setBackground(mArea.getBackgroundColor());
        mTabPane.setMnemonicAt(0, KeyEvent.VK_G);

        optionPane2 = new JPanel();
        optionPane2.setLayout(new SpringLayout());
        optionPane2.setBackground( mArea.getBackgroundColor() );

        JLabel label_7 = new JLabel("Select Statement for Run Screen:");
        optionPane2.add(label_7, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(20),
                                                 Spring.constant(280),
                                                 Spring.constant(20)));

        mSelect_1 = new JTextArea();
        mSelect_1.setEditable(true);
        mSelect_1.setLineWrap(true);
        mSelect_1.setWrapStyleWord(true);
        mSelect_1.setBorder(BorderFactory.createLineBorder(mArea.getLineColor()));

        optionPane2.add(mSelect_1, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(45),
                                                 Spring.constant(300),
                                                 Spring.constant(60)));

        JLabel label_8 = new JLabel("Select Statement for Standard Log Screen:");
        optionPane2.add(label_8, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(125),
                                                 Spring.constant(280),
                                                 Spring.constant(20)));
        mSelect_2 = new JTextArea();
        mSelect_2.setEditable(true);
        mSelect_2.setLineWrap(true);
        mSelect_2.setWrapStyleWord(true);
        mSelect_2.setBorder(BorderFactory.createLineBorder(mArea.getLineColor()));

        optionPane2.add(mSelect_2, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(150),
                                                 Spring.constant(300),
                                                 Spring.constant(60)));

        JLabel label_9 = new JLabel("Select Statement for Detail Log Screen:");
        optionPane2.add(label_9, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(230),
                                                 Spring.constant(280),
                                                 Spring.constant(20)));

        mSelect_3 = new JTextArea();
        mSelect_3.setEditable(true);
        mSelect_3.setLineWrap(true);
        mSelect_3.setWrapStyleWord(true);
        mSelect_3.setBorder(BorderFactory.createLineBorder(mArea.getLineColor()));

        optionPane2.add(mSelect_3, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(255),
                                                 Spring.constant(300),
                                                 Spring.constant(60)));

        mEnter2 = new JButton("  OK  ");
        mEnter2.setBackground( mArea.getButtonColor() );
        optionPane2.add(mEnter2, new SpringLayout.Constraints(Spring.constant(80),
                                                 Spring.constant(340),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter2.addActionListener( this );
        mEnter2.setActionCommand("OK");
        mEnter2.setMnemonic(KeyEvent.VK_O);

        mCancel2 = new JButton("Cancel");
        mCancel2.setBackground( mArea.getButtonColor() );
        optionPane2.add(mCancel2, new SpringLayout.Constraints(Spring.constant(200),
                                                 Spring.constant(340),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mCancel2.addActionListener( this );
        mCancel2.setActionCommand("Cancel");
        mCancel2.setMnemonic(KeyEvent.VK_C);

        mTabPane.add("Select Criteria 1", optionPane2);
        mTabPane.setMnemonicAt(1, KeyEvent.VK_S);

        optionPane3 = new JPanel();
        optionPane3.setLayout(new SpringLayout());
        optionPane3.setBackground( mArea.getBackgroundColor() );

        JLabel label_10 = new JLabel("Select Statement for Chain Screen:");
        optionPane3.add(label_10, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(20),
                                                 Spring.constant(280),
                                                 Spring.constant(20)));

        mSelect_4 = new JTextArea();
        mSelect_4.setEditable(true);
        mSelect_4.setLineWrap(true);
        mSelect_4.setWrapStyleWord(true);
        mSelect_4.setBorder(BorderFactory.createLineBorder(mArea.getLineColor()));

        optionPane3.add(mSelect_4, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(45),
                                                 Spring.constant(300),
                                                 Spring.constant(60)));


        JLabel label_11 = new JLabel("Select Statement for Standard Window Screen:");
        optionPane3.add(label_11, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(125),
                                                 Spring.constant(280),
                                                 Spring.constant(20)));
        mSelect_5 = new JTextArea();
        mSelect_5.setEditable(true);
        mSelect_5.setLineWrap(true);
        mSelect_5.setWrapStyleWord(true);
        mSelect_5.setBorder(BorderFactory.createLineBorder(mArea.getLineColor()));

        optionPane3.add(mSelect_5, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(150),
                                                 Spring.constant(300),
                                                 Spring.constant(60)));

        JLabel label_12 = new JLabel("Select Statement for Detail Window Screen:");
        optionPane3.add(label_12, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(230),
                                                 Spring.constant(280),
                                                 Spring.constant(20)));

        mSelect_6 = new JTextArea();
        mSelect_6.setEditable(true);
        mSelect_6.setLineWrap(true);
        mSelect_6.setWrapStyleWord(true);
        mSelect_6.setBorder(BorderFactory.createLineBorder(mArea.getLineColor()));

        optionPane3.add(mSelect_6, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(255),
                                                 Spring.constant(300),
                                                 Spring.constant(60)));

        mEnter3 = new JButton("  OK  ");
        mEnter3.setBackground( mArea.getButtonColor() );
        optionPane3.add(mEnter3, new SpringLayout.Constraints(Spring.constant(80),
                                                 Spring.constant(340),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter3.addActionListener( this );
        mEnter3.setActionCommand("OK");
        mEnter3.setMnemonic(KeyEvent.VK_O);

        mCancel3 = new JButton("Cancel");
        mCancel3.setBackground( mArea.getButtonColor() );
        optionPane3.add(mCancel3, new SpringLayout.Constraints(Spring.constant(200),
                                                 Spring.constant(340),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mCancel3.addActionListener( this );
        mCancel3.setActionCommand("Cancel");
        mCancel3.setMnemonic(KeyEvent.VK_C);

        mTabPane.add("Select Criteria 2", optionPane3);
        mTabPane.setMnemonicAt(1, KeyEvent.VK_S);


        getContentPane().add( mTabPane );

    }

    public void setConnection(SchedGlobalData.connectionItem lConnectionItem) {
        mConnectionItem = lConnectionItem;

        connectionName.setText(mConnectionItem.getName());
        userName.setText(mConnectionItem.getAcName());
        hostName.setText(mConnectionItem.getHost());
        portName.setText(mConnectionItem.getPort());
        databaseName.setText(mConnectionItem.getDatabase());
        if (mConnectionItem.isSysdba()) mCheckBox1.setSelected(true);
        if (mConnectionItem.isSavePassword()) mCheckBox2.setSelected(true);
        if (mConnectionItem.isAutoConnect()) mCheckBox3.setSelected(true);

        if (mConnectionItem.getRunWhereStmt() != null)
            mSelect_1.append(mConnectionItem.getRunWhereStmt());
        if (mConnectionItem.getStdLogWhereStmt() != null)
            mSelect_2.append(mConnectionItem.getStdLogWhereStmt());
        if (mConnectionItem.getDetLogWhereStmt() != null)
            mSelect_3.append(mConnectionItem.getDetLogWhereStmt());
        if (mConnectionItem.getChainWhereStmt() != null)
            mSelect_4.append(mConnectionItem.getChainWhereStmt());
        if (mConnectionItem.getStdWinWhereStmt() != null)
            mSelect_5.append(mConnectionItem.getStdWinWhereStmt());
        if (mConnectionItem.getDetWinWhereStmt() != null)
            mSelect_6.append(mConnectionItem.getDetWinWhereStmt());

        password = new JPasswordField(mConnectionItem.getPassword());
        optionPane1.add(password, new SpringLayout.Constraints(Spring.constant(170),
                                                 Spring.constant(100),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));
    }
    public void setNewConnection() {

        password = new JPasswordField();
        optionPane1.add(password, new SpringLayout.Constraints(Spring.constant(170),
                                                 Spring.constant(100),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));
    }

    public void actionPerformed( ActionEvent e ) {
      if ( e.getActionCommand().equals("OK") ) {

        if (mOptionId == 0)
            addConnection();
        else {
            if (mArea.isValidConnectionName(connectionName.getText(),
                                            mConnectionItem.getConnectionId())) {

                String m_Password = new String(password.getPassword());

                // System.out.println(" Point A1. " + m_Password);

                mParentFrame.updateConnection(
                        connectionName.getText(),
                        userName.getText(),
                        m_Password,
                        hostName.getText(),
                        portName.getText(),
                        databaseName.getText(),
                        mCheckBox1.isSelected(),
                        mCheckBox2.isSelected(),
                        mCheckBox3.isSelected(),
                        mSelect_1.getText(),
                        mSelect_2.getText(),
                        mSelect_3.getText(),
                        mSelect_4.getText(),
                        mSelect_5.getText(),
                        mSelect_6.getText());
            }
            else {
                JOptionPane.showMessageDialog(null, "Error - Connection Name must be Unique.");
            }
        }
        dispose();
      }

      if ( e.getActionCommand().equals("Cancel") ) {
        dispose();
      }
    }

    private void addConnection() {
        if (mArea.isValidConnectionName(connectionName.getText(), 0)) {
            String m_ConnectionName = connectionName.getText();
            String m_Username = userName.getText();
            String m_Password = new String(password.getPassword());
            String m_Hostname = hostName.getText();
            String m_Portname = portName.getText();
            String m_Database = databaseName.getText();

            SchedGlobalData.connectionItem m_ConnectionItem =
                mArea.new connectionItem(m_ConnectionName,
                                     m_Username,
                                     m_Password,
                                     m_Hostname,
                                     m_Portname,
                                     m_Database,
                                     mCheckBox1.isSelected(),
                                     mCheckBox2.isSelected(),
                                     mCheckBox3.isSelected(),
                                     mSelect_1.getText(),
                                     mSelect_2.getText(),
                                     mSelect_3.getText(),
                                     mSelect_4.getText(),
                                     mSelect_5.getText(),
                                     mSelect_6.getText());

            mArea.addConnectionObj(m_ConnectionItem);
            mParentFrame.createConnectionTreeItem(m_ConnectionItem);
        }
        else {
            JOptionPane.showMessageDialog(null, "Error - Connection Name must be Unique.");
        }
    }
}

