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

class SchedJobDetailScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton                              mEnter;
    private JPanel                               dataPane;
    private SchedGlobalData.JobDetLogItem        mJobRunDetailsItem;
    private SchedGlobalData.JobLogItem           mJobLogItem;
    private SchedGlobalData.WindowDetLogItem     mWindowDetailsItem;
    private SchedGlobalData.WindowLogItem        mWindowLogItem;

    private SchedGlobalData.JobsRunningItem      mJobsRunningItem;
    private SchedGlobalData.ChainsRunningItem    mChainsRunningItem;
    private SchedDataArea.SessionItem            mSessionItem;
    private SchedDataArea.ConsumerGroupStatsItem mConsGroupStatsItem;

    private Scheduler                            mParentFrame;
    private SchedDataArea                        mDataArea;
    private SchedGlobalData                      mArea;
    private SchedScreenArea.PaneObject           mPane;
    private int                                  mScreen;
    private int                                  mLogId;

    private ClassLoader  cl;

    public SchedJobDetailScreen(Scheduler                gParentFrame,
                             SchedDataArea               gDataArea,
                             SchedGlobalData             gArea,
                             SchedScreenArea.PaneObject  gPane,
                             int                         gScreen,
                             int                         gLogId)
    {
        // Call the parent setting the parent frame and making it modal.
        super( gParentFrame, true );

        mParentFrame = gParentFrame;
        mDataArea = gDataArea;
        mArea = gArea;
        mPane = gPane;
        mScreen = gScreen;
        mLogId = gLogId;

        mDataArea.setReturnNo(1);

        // setTitle( "Scheduler FE Grid" );
        setSize( 620, 460 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        this.setLocation((int)mParentFrame.getLocationOnScreen().getX() + 160,
                         (int)mParentFrame.getLocationOnScreen().getY() + 100);

        dataPane = new JPanel();
        dataPane.setBackground(mArea.getScreenColor(42));
        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        SpringLayout lm = new SpringLayout();
        dataPane.setLayout(lm);

        dataPane.add(mPane.getTabbedPane(),
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(0),
                                                 Spring.constant(606),
                                                 Spring.constant(350)));

        dataPane.add(setupButtons(),
                    new SpringLayout.Constraints(Spring.constant(0),
                                                 Spring.constant(360),
                                                 Spring.constant(606),
                                                 Spring.constant(100)));;

        getContentPane().add( dataPane );

        switch (mScreen) {
            case 2:
                setupJobLogStdData();
                break;
            case 3:
                setupWinStdData();
                break;
            case 4:
                setupJobLogDetData();
                break;
            case 5:
                setupWinDetData();
                break;
            case 6:
                setupRunData();
                break;
            case 7:
                setupChainData();
                break;
            case 8:
                setupSessionData(mLogId);
                break;
            case 9:
                setupConsumerGroupData(mLogId);
                break;
        }
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

        dataPane3.add(mEnter, new SpringLayout.Constraints(Spring.constant(270),
                                                 Spring.constant(15),
                                                 Spring.constant(80),
                                                 Spring.constant(30)));

        return dataPane3;
    }

    private void setupJobLogDetData() {

        mJobRunDetailsItem = mArea.getJobDetLogId(mLogId);

        mPane.updateTextObj(Integer.toString(mJobRunDetailsItem.getLogId()),1);
        mPane.updateTextObj(mJobRunDetailsItem.getLogDate(),2);
        mPane.updateTextObj(mJobRunDetailsItem.getOwner(),3);
        mPane.updateTextObj(mJobRunDetailsItem.getJobName(),4);
        mPane.updateTextObj(mJobRunDetailsItem.getStatus(),5);
        mPane.updateTextObj(Integer.toString(mJobRunDetailsItem.getErrorNo()),6);
        mPane.updateTextObj(mJobRunDetailsItem.getReqStartDate(),7);
        mPane.updateTextObj(mJobRunDetailsItem.getActualStartDate(),8);
        mPane.updateTextObj(mJobRunDetailsItem.getRunDuration(),9);
        mPane.updateTextObj(Integer.toString(mJobRunDetailsItem.getInstanceId()),10);
        mPane.updateTextObj(mJobRunDetailsItem.getSessionId(),11);
        mPane.updateTextObj(mJobRunDetailsItem.getSlavePid(),12);
        mPane.updateTextObj(mJobRunDetailsItem.getCpuUsed(),13);
        mPane.updateTextAreaObj(mJobRunDetailsItem.getAdditionalInfo(),14);
        if (mDataArea.getVersionNo() > 2) {
            mPane.updateTextObj(mJobRunDetailsItem.getJobSubName(),15);
            mPane.updateTextObj(mJobRunDetailsItem.getDestination(),16);
        }
        if (mDataArea.getVersionNo() > 3) {
            mPane.updateTextObj(mJobRunDetailsItem.getCredentialName(),17);
            mPane.updateTextObj(mJobRunDetailsItem.getCredentialOwner(),18);
            mPane.updateTextObj(mJobRunDetailsItem.getDestinationOwner(),19);
        }
    }

    private void setupJobLogStdData() {
        mJobLogItem = mArea.getJobLogId(mLogId);

        mPane.updateTextObj(Integer.toString(mJobLogItem.getLogId()),1);
        mPane.updateTextObj(mJobLogItem.getLogDate(),2);
        mPane.updateTextObj(mJobLogItem.getOwner(),3);
        mPane.updateTextObj(mJobLogItem.getJobName(),4);
        mPane.updateTextObj(mJobLogItem.getJobClass(),5);
        mPane.updateTextObj(mJobLogItem.getOperation(),6);
        mPane.updateTextObj(mJobLogItem.getStatus(),7);
        mPane.updateTextObj(mJobLogItem.getUserName(),8);
        mPane.updateTextObj(mJobLogItem.getClientId(),9);
        mPane.updateTextObj(mJobLogItem.getGlobalUid(),10);
        mPane.updateTextAreaObj(mJobLogItem.getAdditionalInfo(),11);
        if (mDataArea.getVersionNo() > 1) {
            mPane.updateTextObj(mJobLogItem.getJobSubName(),12);
        }
        if (mDataArea.getVersionNo() > 2) {
            mPane.updateTextObj(mJobLogItem.getDestination(),13);
        }
        if (mDataArea.getVersionNo() > 3) {
            mPane.updateTextObj(mJobLogItem.getCredentialOwner(),14);
            mPane.updateTextObj(mJobLogItem.getCredentialName(),15);
            mPane.updateTextObj(mJobLogItem.getDestinationOwner(),16);
        }
    }

    private void setupWinDetData() {
        mWindowDetailsItem = mArea.getWindowDetailsLogId(mLogId);

        mPane.updateTextObj(Integer.toString(mWindowDetailsItem.getLogId()),1);
        mPane.updateTextObj(mWindowDetailsItem.getLogDate(),2);
        mPane.updateTextObj(mWindowDetailsItem.getWindowName(),3);
        mPane.updateTextObj(mWindowDetailsItem.getReqStartDate(),4);
        mPane.updateTextObj(mWindowDetailsItem.getWindowDuration(),6);
        mPane.updateTextObj(mWindowDetailsItem.getActualDuration(),7);
        mPane.updateTextObj(Integer.toString(mWindowDetailsItem.getInstanceId()),8);
        mPane.updateTextAreaObj(mWindowDetailsItem.getAdditionalInfo(),9);
        if (mDataArea.getVersionNo() > 1) {
            mPane.updateTextObj(mWindowDetailsItem.getActualStartDate(),5);
        }
    }

    private void setupWinStdData() {
        mWindowLogItem = mArea.getWindowLogId(mLogId);

        mPane.updateTextObj(Integer.toString(mWindowLogItem.getLogId()),1);
        mPane.updateTextObj(mWindowLogItem.getLogDate(),2);
        mPane.updateTextObj(mWindowLogItem.getWindowName(),3);
        mPane.updateTextObj(mWindowLogItem.getOperation(),4);
        mPane.updateTextObj(mWindowLogItem.getStatus(),5);
        mPane.updateTextObj(mWindowLogItem.getUserName(),6);
        mPane.updateTextObj(mWindowLogItem.getClientId(),7);
        mPane.updateTextObj(mWindowLogItem.getGlobalUid(),8);
        mPane.updateTextAreaObj(mWindowLogItem.getAdditionalInfo(),9);
    }

    private void setupRunData() {
        mJobsRunningItem = mArea.getJobsRunningId(mLogId);

        mPane.updateTextObj(mJobsRunningItem.getOwner(),1);
        mPane.updateTextObj(mJobsRunningItem.getJobName(),2);
        mPane.updateTextObj(Integer.toString(mJobsRunningItem.getSessionId()),3);
        mPane.updateTextObj(Integer.toString(mJobsRunningItem.getSlaveProcessId()),4);
        mPane.updateTextObj(Integer.toString(mJobsRunningItem.getRunningInstance()),5);
        mPane.updateTextObj(mJobsRunningItem.getResourceConsumerGroup(),6);
        mPane.updateTextObj(mJobsRunningItem.getElapsedTime(),7);
        mPane.updateTextObj(mJobsRunningItem.getCpuUsed(),8);
        if (mDataArea.getVersionNo() > 1) {
            mPane.updateTextObj(mJobsRunningItem.getJobSubname(),9);
            mPane.updateTextObj(Integer.toString(mJobsRunningItem.getSlaveOsProcessId()),10);
        }
        if (mDataArea.getVersionNo() > 2) {
            mPane.updateTextObj(mJobsRunningItem.getJobStyle(),11);
            mPane.updateTextObj(mJobsRunningItem.getDetached(),12);
        }
        if (mDataArea.getVersionNo() > 3) {
            mPane.updateTextObj(mJobsRunningItem.getDestinationOwner(),13);
            mPane.updateTextObj(mJobsRunningItem.getDestination(),14);
            mPane.updateTextObj(mJobsRunningItem.getCredentialOwner(),15);
            mPane.updateTextObj(mJobsRunningItem.getCredentialName(),16);
        }
    }

    private void setupChainData() {

        mChainsRunningItem = mArea.getChainsRunningId(mLogId);

        mPane.updateTextObj(mChainsRunningItem.getOwner(),1);
        mPane.updateTextObj(mChainsRunningItem.getJobName(),2);
        mPane.updateTextObj(mChainsRunningItem.getJobSubName(),3);
        mPane.updateTextObj(mChainsRunningItem.getChainOwner(),4);
        mPane.updateTextObj(mChainsRunningItem.getChainName(),5);
        mPane.updateTextObj(mChainsRunningItem.getStepName(),6);
        mPane.updateTextObj(mChainsRunningItem.getState(),7);
        mPane.updateTextObj(Integer.toString(mChainsRunningItem.getErrorCode()),8);
        mPane.updateTextObj(mChainsRunningItem.getCompleted(),9);
        mPane.updateTextObj(mChainsRunningItem.getStartDate(),10);
        mPane.updateTextObj(mChainsRunningItem.getEndDate(),11);
        mPane.updateTextObj(mChainsRunningItem.getDuration(),12);
        mPane.updateTextObj(mChainsRunningItem.getSkip(),13);
        mPane.updateTextObj(mChainsRunningItem.getPause(),14);
        mPane.updateTextObj(mChainsRunningItem.getRestartOnRecovery(),15);
        mPane.updateTextObj(mChainsRunningItem.getStepJobSubname(),16);
        mPane.updateTextObj(Integer.toString(mChainsRunningItem.getStepJobLogId()),17);
        if (mDataArea.getVersionNo() > 3) {
            mPane.updateTextObj(mChainsRunningItem.getRestartOnFailure(),18);
        }
    }

    public void refreshConsumerGroupData(int lLogId) {
        setupConsumerGroupData(lLogId);
    }

    private void setupConsumerGroupData(int lId) {

        mConsGroupStatsItem = mDataArea.getConsumerGroupStatsId(lId);

        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getId()),1);
        mPane.updateTextObj(mConsGroupStatsItem.getName(),2);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getActiveSessions()),3);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getExecutionWaiters()),4);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getRequests()),5);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCpuWaitTime()),6);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCpuWaits()),7);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getConsumedCpuTime()),8);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getYields()),9);

        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getQueueLength()),10);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getQueueTime()),11);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getQueueTimeOut()),12);

        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCurrentUndoConsumption()),13);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getActiveSessionLimitHit()),14);
        mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getUndoLimitHit()),15);

        if (mDataArea.getVersionNo() < 3) {
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesInCpuTime()),16);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesOutCpuTime()),17);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSqlCanceled()),18);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getActiveSessionsKilled()),19);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getIdleSessionsKilled()),20);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getIdleBlkrSessionsKilled()),21);
        }
        else {
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesInCpuTime()),16);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesInIoMegabytes()),17);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesInIoRequests()),18);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesOutCpuTime()),19);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesOutIoMegabytes()),20);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesOutIoRequests()),21);

            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSmallReadMegabytes()),22);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSmallWriteMegabytes()),23);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getLargeReadMegabytes()),24);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getLargeWriteMegabytes()),25);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSmallReadRequests()),26);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSmallWriteRequests()),27);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getLargeReadRequests()),28);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getLargeWriteRequests()),29);

            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSqlCanceled()),30);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getIoServiceTime()),31);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getIoServiceWaits()),32);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getActiveSessionsKilled()),33);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getIdleSessionsKilled()),34);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getIdleBlkrSessionsKilled()),35);

            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCpuDecisions()),36);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCpuDecisionsExcl()),37);
            mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCpuDecisionsWon()),38);
            if (mDataArea.getVersionNo() > 4) {
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesInIoLogical()),39);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesOutIoLogical()),40);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesInElapsedTime()),41);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getSwitchesOutElapsedTime()),42);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCurrentPqsActive()),43);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCurrentPqServersActive()),44);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getPqsQueued()),45);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getPqsCompleted()),46);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getPqServersUsed()),47);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getPqActiveTime()),48);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getCurrentPqsQueued()),49);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getPqQueuedTime()),50);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getPqQueueTimeOuts()),51);
                mPane.updateTextObj(Integer.toString(mConsGroupStatsItem.getConId()),52);
            }
        }
    }

    public void refreshSessionData(int lSidId) {
        setupSessionData(lSidId);
    }

    private void setupSessionData(int mSidId) {
        mSessionItem = mDataArea.getSessionId(mSidId);

        mPane.updateTextObj(mSessionItem.getUsername(),1);
        mPane.updateTextObj(mSessionItem.getOsuser(),2);
        mPane.updateTextObj(mSessionItem.getMachine(),3);
        mPane.updateTextObj(mSessionItem.getModule(),4);

        mPane.updateTextObj(mSessionItem.getConsumerGroup(),5);
        mPane.updateTextObj(mSessionItem.getOrigConsumerGroup(),6);
        mPane.updateTextObj(mSessionItem.getMappingAttribute(),7);
        mPane.updateTextObj(mSessionItem.getMappedConsumerGroup(),8);
        mPane.updateTextObj(mSessionItem.getState(),9);
        mPane.updateTextObj(mSessionItem.getActive(),10);
        mPane.updateTextObj(Integer.toString(mSessionItem.getSqlCanceled()),11);
        mPane.updateTextObj(Integer.toString(mSessionItem.getQueueTimeOuts()),12);
        mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentUndoConsumption()),13);
        mPane.updateTextObj(Integer.toString(mSessionItem.getMaxUndoConsumption()),14);
        mPane.updateTextObj(Integer.toString(mSessionItem.getEstimatedExecutionLimitHit()),15);

        mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentIdleTime()),16);
        mPane.updateTextObj(Integer.toString(mSessionItem.getmCurrentCpuWaitTime()),17);
        mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentCpuWaits()),18);
        mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentConsumedCpuTime()),19);
        mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentActiveTime()),20);
        mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentQueuedTime()),21);
        mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentYields()),22);

        mPane.updateTextObj(Integer.toString(mSessionItem.getCpuWaitTime()),25);
        mPane.updateTextObj(Integer.toString(mSessionItem.getCpuWaits()),26);
        mPane.updateTextObj(Integer.toString(mSessionItem.getConsumedCpuTime()),27);
        mPane.updateTextObj(Integer.toString(mSessionItem.getActiveTime()),28);
        mPane.updateTextObj(Integer.toString(mSessionItem.getQueuedTime()),29);
        mPane.updateTextObj(Integer.toString(mSessionItem.getYields()),30);

        if (mDataArea.getVersionNo() > 2) {
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentIoServiceTime()),23);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentIoServiceWaits()),24);
            mPane.updateTextObj(Integer.toString(mSessionItem.getIoServiceTime()),31);
            mPane.updateTextObj(Integer.toString(mSessionItem.getIoServiceWaits()),32);

            mPane.updateTextObj(Integer.toString(mSessionItem.getCurSmallReadMegabytes()),33);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurLargeReadMegabytes()),34);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurSmallWriteMegabytes()),35);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurLargeWriteMegabytes()),36);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurSmallReadRequests()),37);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurSmallWriteRequests()),38);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurLargeReadRequests()),39);
            mPane.updateTextObj(Integer.toString(mSessionItem.getCurLargeWriteRequests()),40);

            mPane.updateTextObj(Integer.toString(mSessionItem.getSmallReadMegabytes()),41);
            mPane.updateTextObj(Integer.toString(mSessionItem.getLargeReadMegabytes()),42);
            mPane.updateTextObj(Integer.toString(mSessionItem.getSmallWriteMegabytes()),43);
            mPane.updateTextObj(Integer.toString(mSessionItem.getLargeWriteMegabytes()),44);
            mPane.updateTextObj(Integer.toString(mSessionItem.getSmallReadRequests()),45);
            mPane.updateTextObj(Integer.toString(mSessionItem.getSmallWriteRequests()),46);
            mPane.updateTextObj(Integer.toString(mSessionItem.getLargeReadRequests()),47);
            mPane.updateTextObj(Integer.toString(mSessionItem.getLargeWriteRequests()),48);
            if (mDataArea.getVersionNo() > 4) {
                mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentPqActiveTime()),49);
                mPane.updateTextObj(Integer.toString(mSessionItem.getPqActiveTime()),50);
                mPane.updateTextObj(Integer.toString(mSessionItem.getDop()),51);
                mPane.updateTextObj(Integer.toString(mSessionItem.getPqServers()),52);
                mPane.updateTextObj(Integer.toString(mSessionItem.getEstimatedExecutionTime()),53);
                mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentPqQueuedTime()),54);
                mPane.updateTextObj(Integer.toString(mSessionItem.getPqQueuedTime()),55);
                mPane.updateTextObj(Integer.toString(mSessionItem.getPqQueued()),56);
                mPane.updateTextObj(Integer.toString(mSessionItem.getPqQueueTimeOuts()),57);
                mPane.updateTextObj(mSessionItem.getPqActive(),58);
                mPane.updateTextObj(mSessionItem.getPqStatus(),59);
                mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentLogicalIos()),60);
                mPane.updateTextObj(Integer.toString(mSessionItem.getLogicalIos()),61);
                mPane.updateTextObj(Integer.toString(mSessionItem.getCurrentElapsedTime()),62);
                mPane.updateTextObj(Integer.toString(mSessionItem.getElapsedTime()),63);
                mPane.updateTextObj(mSessionItem.getLastAction(),64);
                mPane.updateTextObj(mSessionItem.getLastActionReason(),65);
                mPane.updateTextObj(Integer.toString(mSessionItem.getLastActionTime()),66);
                mPane.updateTextObj(Integer.toString(mSessionItem.getConId()),67);
            }
        }
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getActionCommand().equals("OK") ) {

            mDataArea.setReturnNo(0);
            dispose();
        }
    }
}
