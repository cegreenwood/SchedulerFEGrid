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

// System.out.println(" Screen No ");

class SchedLogon extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JTextField      userName, passWord;

    private JTextArea       message_1;

    private String          l_userName;
    private String          m_HostName;
    private char[]          m_password;

    private JButton         mEnter, mCancel;
    private JPanel          logonPane;

    private SchedGlobalData.connectionItem mConnectionItem;

    // Instance attributes used in this dialog.
    public Scheduler        parentFrame;
    public SchedDataArea    mDataArea;
    public SchedGlobalData  mArea;
    public SchedDataNode    mNode;

    // Dialog constructor.
    public SchedLogon(Scheduler       parentFrame,
                      SchedGlobalData Area,
                      SchedDataNode   connectNode)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        // Save the owner frame in case we need it later.
        this.parentFrame = parentFrame;
        mArea = Area;
        mNode = connectNode;

        // Set the characteristics for this dialog instance.
        setTitle( "Logon Screen" );
        setSize( 320, 240 );
        setMinimumSize(new Dimension(320, 240));
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        this.setLocation((int)parentFrame.getLocationOnScreen().getX() + 120,
                         (int)parentFrame.getLocationOnScreen().getY() + 80);

        // Create a panel for the components.
        logonPane = new JPanel();
        logonPane.setLayout(new SpringLayout());
        logonPane.setBackground( mArea.getBackgroundColor() );

        JLabel label_1 = new JLabel("Username:");
        logonPane.add(label_1, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(50),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));
        JLabel label_2 = new JLabel("Password:");
        logonPane.add(label_2, new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(80),
                                                 Spring.constant(80),
                                                 Spring.constant(20)));

        userName = new JTextField(10);
        logonPane.add(userName, new SpringLayout.Constraints(Spring.constant(130),
                                                 Spring.constant(50),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));

        message_1 = new JTextArea();
        message_1.setEditable(false);
        message_1.setLineWrap(true);
        message_1.setBackground( mArea.getBackgroundColor() );

        logonPane.add(message_1, new SpringLayout.Constraints(Spring.constant(30),
                                                 Spring.constant(100),
                                                 Spring.constant(260),
                                                 Spring.constant(50)));

        mEnter = new JButton("  OK  ");
        mEnter.setBackground( mArea.getButtonColor() );
        logonPane.add(mEnter, new SpringLayout.Constraints(Spring.constant(60),
                                                 Spring.constant(160),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter.addActionListener( this );
        mEnter.setActionCommand("Connect");
        mEnter.setMnemonic(KeyEvent.VK_O);

        mCancel = new JButton("Cancel");
        mCancel.setBackground( mArea.getButtonColor() );
        logonPane.add(mCancel, new SpringLayout.Constraints(Spring.constant(180),
                                                 Spring.constant(160),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mCancel.addActionListener( this );
        mCancel.setActionCommand("Cancel");
        mCancel.setMnemonic(KeyEvent.VK_C);

        getContentPane().add( logonPane );

    }

    public void setupLogon(SchedGlobalData.connectionItem lConnectionItem) {
        mConnectionItem = lConnectionItem;
        userName.setText(mConnectionItem.getAcName());
        l_userName = mConnectionItem.getAcName();

        if (mConnectionItem.getPassword() != null) {
            passWord = new JPasswordField(mConnectionItem.getPassword());
            logonPane.add(passWord, new SpringLayout.Constraints(Spring.constant(130),
                                                 Spring.constant(80),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));
        }
        else {
            passWord = new JPasswordField(10);
            logonPane.add(passWord, new SpringLayout.Constraints(Spring.constant(130),
                                                 Spring.constant(80),
                                                 Spring.constant(120),
                                                 Spring.constant(20)));
        }
    }

    public void setFocus() {
        passWord.grabFocus();
    }

    public void actionPerformed( ActionEvent e ) {

      if ( e.getActionCommand().equals("Connect") ) {
        mDataArea = new SchedDataArea(mConnectionItem.getName());
        parentFrame.setDataArea(mDataArea);

        // m_HostName = mConnectItem.getHost() + ":" + 
        //              mConnectItem.getPort() + ":" + 
        //              mConnectItem.getDatabase();
        m_HostName = "//" + mConnectionItem.getHost() + ":" + 
                             mConnectionItem.getPort() + "/" + 
                             mConnectionItem.getDatabase();

        if (l_userName.compareTo(userName.getText()) != 0) {
            mConnectionItem.setAcName(userName.getText());
        }

        getConnection();

        if (mDataArea.getConnectStatus() == 0) {
            dispose();
        } else {
            message_1.setText(mDataArea.getSysMessage().toString());
        };
      }

      if ( e.getActionCommand().equals("Cancel") ) {
        dispose();
      }
    }

    private void getConnection() {

        String m_Username = userName.getText();
        String m_Password = passWord.getText();

        mDataArea.GetConnection(m_Username, m_Password, m_HostName, mConnectionItem.isSysdba());

        if ((mDataArea.getConnectStatus() == 0) &&
            (mDataArea.getVersionNo() > 0)) {
            if ( mConnectionItem.isSavePassword() )
                mConnectionItem.setPassword(m_Password);
            parentFrame.connectionOpen();
        }
    }
}

