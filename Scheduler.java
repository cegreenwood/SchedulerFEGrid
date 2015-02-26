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
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.*;

import java.nio.file.Paths;
import java.io.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import static uk.co.blueshireservices.schedulergrid.SchedConsts.*;

public class Scheduler extends JFrame implements ActionListener, TreeSelectionListener {

    public static final long serialVersionUID = 1L;

    private String                    Connect;
    static  JLabel                    userName1, hostName1, hostName2;
    static  JLabel                    userName2;
    static  JLabel                    dateName1, dateName2;
    private JLabel                    label;

    private SchedSingleTable          mTable8, mTable9;
    private SchedTable                mTable2, mTable3, mTable4, mTable5;
    private SchedTable                mTable6, mTable7;

    private String                    screenDesc;
    private String                    currentTree;
    private ImageIcon                 ii;
    private int                       currentScreenNo, currentScreenId, currentScreenDisp;
    private int                       currentRoleId;
    private int                       mNumber;
    private int                       mScreenId;

    private int                       parentId, folderId, childId;
    private int                       jobFolderId;
    private int                       scheduleFolderId, chainFolderId;
    private int                       elementNo;
    private int                       mConnectionId;

    private Calendar                  cal;
    private SimpleDateFormat          sdf_long, sdf_short, sdf_desc;
    private StringBuffer              mStartDate1, mCurrentDate, mCurrentFormatDate, mAfterDate;
    private String                    mCheckDate, mLogDate;
    private String                    mStartDate2, mEndDate2;
    private String                    mInterval, mJobOwner, mJobName, mDatabase;
    private String                    mDuration;
    private String                    mResourceConsumerGroup;
    private String                    mStatus, mElapsedTime;
    private String                    mUsername;
    private int                       mSid, mSerial;
    private int                       mId, mLogId;
    private int                       mSavedSid;

    final String  nullEndDate   = "3000-00-00 00:00:00";
    final String  nullStartDate = "2000-00-00 00:00:00";

    public static final int           mDebug = 1;

    private boolean                   firstEntry;

    public  String[]    objects  = {"Connection","Job", "Job Argument","Job Notification",
                                    "Program","Program Argument","Schedule",
                                   "Chain","Chain Rule","Chain Step","Credential",
                                   "Job Class","Window","Group","File Watcher",
                                   "Destination","Resource Plan","Consumer Group",
                                   "Plan Directive", "Group Mapping", "Group Privilege"
                                   };

    private String[]       views = {"1 Scheduler Manager Tree","2 Log View - Standard",
                                    "3 Log View - Detail","4 Run View",
                                    "5 Resource Manager Tree","6 Sessions View",
                                    "7 Consumer Groups View"
                                    };

    private Color                     backgroundColor, foregroundColor;

    private JScrollPane               gTreeView, gTreeView1, gTreeView2;
    private JScrollPane               sPane;
    private static JSplitPane         splitPane1, splitPane2, splitPane3;
    private static JSplitPane         splitPane4, splitPane5, splitPane6;
    private JPanel                    topPane;

    private JScrollPane               bottomPane, mPanel1, mPanel2, mPanel3, mPanel4, mPanel5;
    private JScrollPane               mPanel6, mPanel7, mPanel8, mPanel9;
    private JPanel                    mSPanel2, mSPanel3, mSPanel4, mSPanel5;
    private JPanel                    mSPanel6, mSPanel7, mSPanel8, mSPanel9;

    private JMenuBar                  menuBar1, menuBar2;
    private JMenuItem                 menuConnect, menuAutoConnect, menuDisconnect;
    private JMenuItem                 menuRefresh, menuFolder;

    private JMenuItem[]               menuAddItem;
    private JMenuItem[]               menuViews;

    private JMenuItem                 menuExit, menuOption, menuAboutFE;
    private JMenuItem                 menuJobRun, menuJobStop, menuJobDetail, menuPurge;
    private JMenuItem                 menuWClose, menuWOpen, menuPrev, menuNext;
    private JMenuItem                 menuDrop, menuCopy, menuUpdate, menuDisable, menuEnable;
    private JMenuItem                 menuAddGroup, menuRemoveGroup;
    private JMenuItem                 menuSwitchPlan, menuSwitchUser, menuSwitchSession;
    private JMenuItem                 menuPAClear, menuPACreate;
    private JMenuItem                 menuPAValidate, menuPASubmit;

    private JMenu                     menuFile, menuAdd1, menuAdd2, menuHelp;
    private JMenu                     menuJob, menuWindow, menuScreen, menuSwitch;
    private JMenu                     menuObject1, menuObject2, menuPending, menuGroup;
    private JMenu                     menuDate;


    public  static String             gChainName, gChainOwner;
    public  static String             gProgramName;
    public  static String             gWindowName;
    public  static String             gGroupName;

    private String                    mChainOwner, mChainName;
    private String                    mChainStepName;
    private String                    mChainRuleOwner, mChainRuleName;
    private boolean                   mChainStep, mChainRule;

    private JToolBar                  toolBar, userBar;
    private JButton                   buttonExit, buttonRefresh;
    private JButton                   buttonEnable, buttonDisable;
    private JButton                   buttonConnect, buttonDisconnect;
    private JButton                   buttonDetail;
    private JButton                   buttonRun, buttonStop;
    private JButton                   buttonUpdate, buttonDrop;
    // private JButton                   buttonPrev, buttonNext;
    private JButton                   buttonCopy;

    private boolean                   enableButton, disableButton;
    private boolean                   mResourceMenu;
    private boolean                   detailScreen = false;
    private boolean                   upDownButton;

    private Icon                      blankIcon;
    private Icon                      stopIcon, runIcon;
    private Icon                      nextIcon, prevIcon, upIcon, downIcon;
    private Icon                      detailIcon, switchIcon1, switchIcon2, switchIcon3;
    private Icon                      switchIcon4;

    private JTree                           gTree, gTree1, gTree2;
    private DefaultTreeModel                gTreeModel, gTreeModel1, gTreeModel2;

    private SchedDataArea                        dataArea;
    private SchedDataArea.JobItem                gJobItem;
    private SchedDataArea.JobArgsItem            gJobArgsItem;
    private SchedDataArea.ProgramItem            gProgramItem;
    private SchedDataArea.ProgramArgsItem        gProgramArgsItem;
    private SchedDataArea.ScheduleItem           gScheduleItem;
    private SchedDataArea.JobClassItem           gJobClassItem;
    private SchedDataArea.WindowItem             gWindowItem;
    private SchedDataArea.WindowGroupItem        gWindowGroupItem;
    private SchedDataArea.ChainsItem             gChainsItem;
    private SchedDataArea.ChainStepsItem         gChainStepsItem;
    private SchedDataArea.ChainRulesItem         gChainRulesItem;

    private SchedDataArea.GlobalAttributesItem   gGlobalAttributesItem;
    private SchedDataArea.WinGroupMembersItem    gWinGroupMembersItem;
    private SchedDataArea.CredentialsItem        gCredentialsItem;
    private SchedDataArea.FileWatchersItem       gFileWatchersItem;
    private SchedDataArea.NotificationsItem      gNotificationsItem;
    private SchedDataArea.DbDestsItem            gDbDestsItem;
    private SchedDataArea.ExtDestsItem           gExtDestsItem;
    private SchedDataArea.GroupItem              gGroupItem;

    private SchedGlobalData.JobLogItem           gJobLogItem;
    private SchedGlobalData.JobDetLogItem        gJobDetLogItem;
    private SchedGlobalData.WindowLogItem        gWindowLogItem;
    private SchedGlobalData.WindowDetLogItem     gWindowDetLogItem;
    private SchedGlobalData.JobsRunningItem      gJobsRunningItem;
    private SchedGlobalData.ChainsRunningItem    gChainsRunningItem;
    private SchedGlobalData.ChainsRunningItem    sChainsRunningItem;

    private SchedDataArea.PlanItem               gPlanItem;
    private SchedDataArea.CdbPlanItem            gCdbPlanItem;
    private SchedDataArea.ConsumerGroupItem      gConsumerGroupItem;
    private SchedDataArea.PlanDirectiveItem      gPlanDirectiveItem;
    private SchedDataArea.CdbPlanDirectiveItem   gCdbPlanDirectiveItem;
    private SchedDataArea.MappingPrioritiesItem  gMappingPrioritiesItem;
    private SchedDataArea.GroupMappingsItem      gGroupMappingsItem;
    private SchedDataArea.ConsumerPrivItem       gConsumerPrivItem;
    private SchedDataArea.CurrentPlanItem        gCurrentPlanItem;
    private SchedDataArea.SessionItem            gSessionItem;
    private SchedDataArea.ConsumerGroupStatsItem gConsumerGroupStatsItem;

    private SchedDataNode                        groot, groot1, groot2;
    private SchedDataNode                        ParentNode;
    private SchedDataNode                        CurrentNode, SchedDataNode;
    private SchedDataNode                        mTreeNode1, mTreeNode2;

    private TreePath                             CurrentPath;

    private SchedGlobalData                      mGlobalArea;
    private SchedGlobalData.connectionItem       mConnectionItem;

    private SchedScreenArea                         mScreen;
    private SchedScreenArea.PaneObject              mPane;

    private SchedInpScreenArea                      mScreenInp;
    private SchedInpScreenArea.PaneObject           mPaneInp;
    private SchedInpScreenArea.PaneObject.TextItem  mTextItemInp;

    private SchedRunData                            mDataRun;
    private SchedRunChainData                       mDataChainRun;
    private SchedJobLogStdData                      mDataJobLogStd;
    private SchedJobLogDetData                      mDataJobLogDet;
    private SchedWindowLogStdData                   mDataWindowLogStd;
    private SchedWindowLogDetData                   mDataWindowLogDet;

    private SchedSessionData                        mDataSession;
    private SchedConsumerGroupStatsData             mDataConsumerGroup;
    private SchedCopyScreen                         inputDialog;
    private SchedJobDetailScreen                    mJobDetailScreen;

    private SchedCellRenderer                       mCustomCellRenderer1;
    private SchedCellRenderer                       mCustomCellRenderer2;

    private threadGetRunningJobs                    runningJobs;
    private threadGetSessions                       runningSessions;
    private threadGetConsumerGroups                 runningConsumerGroups;
    private threadGetResourceData                   runResourceData;
    private threadRunJob                            runJob;
    private Thread                                  jobThread1, jobThread2, jobThread3;
    private Thread                                  jobThread4, jobThread5;

    private ClassLoader    cl;
    private FocusListener  mListener;

    public Scheduler() {

        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.Scheduler");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JDialog.setDefaultLookAndFeelDecorated(true);

        FlowLayout flm1 = new FlowLayout(FlowLayout.LEFT, 30, 10);
        FlowLayout flm2 = new FlowLayout(FlowLayout.LEADING);
        GridLayout glm1 = new GridLayout(1,0);

        mScreen = new SchedScreenArea();
        mScreenInp = new SchedInpScreenArea();
        mGlobalArea = new SchedGlobalData();

        mGlobalArea.setScheduler(this);
        mScreen.setScheduler(this);

        SchedFile.setMiscArea(mGlobalArea);

        SchedFile.ReadParams(mScreen, mScreenInp);

        SchedFile.ReadInitParams();

        SchedFile.ReadPassParams();

        SchedFile.setAudit(mGlobalArea.blockedOption(AUDIT_NO, 10));

        setTitle("Scheduler FE Grid");
        setSize( SchedFile.getXFrameSize(), SchedFile.getYFrameSize() );
        setLocation(SchedFile.getXLocation(), SchedFile.getYLocation());
        mGlobalArea.setFramePosition(SchedFile.getXLocation(), SchedFile.getYLocation());
        getContentPane().setLayout(new BorderLayout());

        //Create the menu bar.
        SetupMenu();

        setJMenuBar(menuBar1);
        mResourceMenu = false;

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setLayout(flm2);

        userBar = new JToolBar();
        userBar.setLayout(flm2);
        userBar.setBorder(BorderFactory.createLineBorder( mGlobalArea.getScreenColor(20) ));


        topPane = new JPanel();
        topPane.setSize(SchedFile.getXFrameSize(), 50);
        topPane.setLayout(flm2);
        topPane.setBorder(BorderFactory.createLineBorder( mGlobalArea.getScreenColor(20) ));

        SetupToolbar();

        ii = new ImageIcon(cl.getResource(PACKAGE_DIR + "SolarSystem1.jpg"));
        label = new JLabel(ii);
        bottomPane = new JScrollPane(label);
        bottomPane.setMinimumSize(new Dimension(600,200));

        getContentPane().add(toolBar,BorderLayout.PAGE_START);

        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane1.setTopComponent(topPane);

        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane2.setOneTouchExpandable(true);
        splitPane2.setDividerLocation(SchedFile.getDivLocation(2));

        splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane3.setDividerLocation(SchedFile.getDivLocation(3));
        splitPane3.setResizeWeight(0.5);

        splitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane4.setDividerLocation(SchedFile.getDivLocation(4));
        splitPane4.setResizeWeight(0.5);

        splitPane5 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane5.setDividerLocation(SchedFile.getDivLocation(5));
        splitPane5.setResizeWeight(0.5);

        splitPane6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane6.setDividerLocation(SchedFile.getDivLocation(6));
        splitPane6.setResizeWeight(0.0);

        createSchedulerTree();

        splitPane2.setLeftComponent(gTreeView);
        splitPane2.setRightComponent(bottomPane);

        splitPane1.setBottomComponent(splitPane2);
        getContentPane().add(splitPane1,BorderLayout.CENTER);

        // Set up the screen displays.
        for (int i1 = 0; i1 < mScreen.sizeScreenObj(); i1++) {
            mPane = mScreen.getScreenObj(i1);
            mScreen.PageSetup(mGlobalArea, mPane);
        }

        setupLogScreens();

        this.addWindowListener( new WindowAdapter()
        {
            public void WindowClosing( WindowEvent e )
            {
                System.exit( 0 );
            }
        } );
    }

    /* Sets up the Menu bar */
    public void SetupMenu() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.SetupMenu");

        //Set up the menu bar.
        menuFile = new JMenu("  File  ");
        menuFile.setMnemonic(KeyEvent.VK_F);

        menuConnect = new JMenuItem("Connect");
        ActionListener lst;
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              openConnection();
          }
        };
        menuConnect.addActionListener( lst );
        menuConnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HOME,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuConnect.setMnemonic(KeyEvent.VK_C);
        menuConnect.setEnabled(false);

        menuDisconnect = new JMenuItem("Disconnect");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              closeConnection();
          }
        };
        menuDisconnect.addActionListener( lst );
        menuDisconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_END,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuDisconnect.setMnemonic(KeyEvent.VK_D);
        menuDisconnect.setEnabled(false);

        menuAutoConnect = new JMenuItem("Auto-Connect");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              startAutoConnect();
          }
        };
        menuAutoConnect.addActionListener( lst );
        menuAutoConnect.setAccelerator(KeyStroke.getKeyStroke('C',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuAutoConnect.setMnemonic(KeyEvent.VK_A);
        menuAutoConnect.setEnabled(true);

        menuRefresh = new JMenuItem("Refresh");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              refreshData();
          }
        };
        menuRefresh.addActionListener( lst );
        menuRefresh.setAccelerator(KeyStroke.getKeyStroke('F',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuRefresh.setMnemonic(KeyEvent.VK_R);
        menuRefresh.setEnabled(false);

        menuExit = new JMenuItem("Exit");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            exitSystem();
          }
        };
        menuExit.addActionListener( lst );
        menuExit.setAccelerator(KeyStroke.getKeyStroke('X',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuExit.setMnemonic(KeyEvent.VK_E);

        menuOption = new JMenuItem("Options");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            optionDialogScreen();
          }
        };
        menuOption.addActionListener( lst );
        menuOption.setMnemonic(KeyEvent.VK_O);

        menuAdd1 = new JMenu(" Create ");
        menuAdd1.setMnemonic(KeyEvent.VK_C);
 
        menuAdd2 = new JMenu(" Add ");
        menuAdd2.setMnemonic(KeyEvent.VK_A);

        menuAddItem = new JMenuItem[21];
        for (int i1 = 0; i1 < 21; i1++) {

            menuAddItem[i1] = new JMenuItem(objects[i1]);

            lst = new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                addObject(e);
              }
            };
            menuAddItem[i1].addActionListener( lst );
            if (i1 > 0) menuAddItem[i1].setEnabled(false);
        }

        menuAddItem[0].setMnemonic(KeyEvent.VK_C);
        menuAddItem[1].setMnemonic(KeyEvent.VK_J);
        menuAddItem[2].setMnemonic(KeyEvent.VK_B);
        menuAddItem[3].setMnemonic(KeyEvent.VK_N);
        menuAddItem[4].setMnemonic(KeyEvent.VK_P);
        menuAddItem[5].setMnemonic(KeyEvent.VK_M);
        menuAddItem[6].setMnemonic(KeyEvent.VK_S);
        menuAddItem[7].setMnemonic(KeyEvent.VK_C);
        menuAddItem[8].setMnemonic(KeyEvent.VK_R);
        menuAddItem[9].setMnemonic(KeyEvent.VK_T);
        menuAddItem[10].setMnemonic(KeyEvent.VK_R);
        menuAddItem[11].setMnemonic(KeyEvent.VK_O);
        menuAddItem[12].setMnemonic(KeyEvent.VK_W);
        menuAddItem[13].setMnemonic(KeyEvent.VK_G);
        menuAddItem[14].setMnemonic(KeyEvent.VK_F);
        menuAddItem[15].setMnemonic(KeyEvent.VK_D);

        menuAddItem[16].setMnemonic(KeyEvent.VK_R);
        menuAddItem[17].setMnemonic(KeyEvent.VK_C);
        menuAddItem[18].setMnemonic(KeyEvent.VK_D);
        menuAddItem[19].setMnemonic(KeyEvent.VK_G);
        menuAddItem[20].setMnemonic(KeyEvent.VK_P);

        for (int i1 = 0; i1 < 21; i1++) {
            if (i1 < 16)
                menuAdd1.add(menuAddItem[i1]);
            else
                menuAdd2.add(menuAddItem[i1]);
        }

        menuObject1 = new JMenu(" Objects ");
        menuObject1.setMnemonic(KeyEvent.VK_O);

        menuDrop = new JMenuItem(" Drop ");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              dropObject();
          }
        };
        menuDrop.addActionListener( lst );
        menuDrop.setAccelerator(KeyStroke.getKeyStroke('D',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuDrop.setEnabled(false);
        menuDrop.setMnemonic(KeyEvent.VK_D);

        menuCopy = new JMenuItem(" Copy ");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              copyObject();
          }
        };
        menuCopy.addActionListener( lst );
        menuCopy.setAccelerator(KeyStroke.getKeyStroke('C',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuCopy.setEnabled(false);
        menuCopy.setMnemonic(KeyEvent.VK_C);

        menuUpdate = new JMenuItem(" Update ");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              updateObject();
          }
        };
        menuUpdate.addActionListener( lst );
        menuUpdate.setAccelerator(KeyStroke.getKeyStroke('U',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuUpdate.setEnabled(false);
        menuUpdate.setMnemonic(KeyEvent.VK_U);

        menuEnable = new JMenuItem(" Enable ");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              enableObject();
          }
        };
        menuEnable.addActionListener( lst );
        menuEnable.setAccelerator(KeyStroke.getKeyStroke('N',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuEnable.setEnabled(false);
        menuEnable.setMnemonic(KeyEvent.VK_E);

        menuDisable = new JMenuItem(" Disable ");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              disableObject();
          }
        };
        menuDisable.addActionListener( lst );
        menuDisable.setAccelerator(KeyStroke.getKeyStroke('S',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuDisable.setEnabled(false);
        menuDisable.setMnemonic(KeyEvent.VK_S);

        menuObject1.add(menuDrop);
        menuObject1.add(menuUpdate);
        menuObject1.add(menuCopy);
        menuObject1.addSeparator();
        menuObject1.add(menuEnable);
        menuObject1.add(menuDisable);

        menuObject2 = new JMenu(" Objects ");
        menuObject2.setMnemonic(KeyEvent.VK_O);

        menuJob = new JMenu(" Job ");
        menuJob.setMnemonic(KeyEvent.VK_J);

        menuJobRun = new JMenuItem(" Run Job");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              runJob();
          }
        };
        menuJobRun.addActionListener( lst );
        menuJobRun.setAccelerator(KeyStroke.getKeyStroke('R',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuJobRun.setEnabled(false);
        menuJobRun.setMnemonic(KeyEvent.VK_R);

        menuJobStop = new JMenuItem(" Stop Job");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              stopJob();
          }
        };
        menuJobStop.addActionListener( lst );
        menuJobStop.setAccelerator(KeyStroke.getKeyStroke('P',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuJobStop.setEnabled(false);
        menuJobStop.setMnemonic(KeyEvent.VK_S);

        menuPurge = new JMenuItem(" Purge Log");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              PurgeLog();
          }
        };
        menuPurge.addActionListener( lst );
        menuPurge.setEnabled(false);
        menuPurge.setMnemonic(KeyEvent.VK_P);

        menuJobDetail = new JMenuItem(" Job Detail");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              displayJobDetail();
          }
        };
        menuJobDetail.addActionListener( lst );
        menuJobDetail.setEnabled(false);
        menuJobDetail.setMnemonic(KeyEvent.VK_J);

        menuJob.add(menuJobStop);
        menuJob.add(menuJobRun);
        menuJob.add(menuPurge);
        menuJob.add(menuJobDetail);

        menuGroup = new JMenu("Group");
        menuGroup.setMnemonic(KeyEvent.VK_G);

        menuAddGroup = new JMenuItem("Assign to Group");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              AssignGroup();
          }
        };
        menuAddGroup.addActionListener( lst );
        menuAddGroup.setEnabled(false);
        menuAddGroup.setMnemonic(KeyEvent.VK_A);

        menuRemoveGroup = new JMenuItem("Remove from Group");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              RemoveGroup();
          }
        };
        menuRemoveGroup.addActionListener( lst );
        menuRemoveGroup.setEnabled(false);
        menuRemoveGroup.setMnemonic(KeyEvent.VK_R);

        menuGroup.add(menuAddGroup);
        menuGroup.add(menuRemoveGroup);

        menuWindow = new JMenu(" Window ");
        menuWindow.setMnemonic(KeyEvent.VK_W);

        menuWOpen = new JMenuItem(" Open Window");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              OpenWindow();
          }
        };
        menuWOpen.addActionListener( lst );
        menuWOpen.setEnabled(false);
        menuWOpen.setAccelerator(KeyStroke.getKeyStroke('O',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuWOpen.setMnemonic(KeyEvent.VK_O);

        menuWClose = new JMenuItem(" Close Window");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              CloseWindow();
          }
        };
        menuWClose.addActionListener( lst );
        menuWClose.setEnabled(false);
        menuWClose.setAccelerator(KeyStroke.getKeyStroke('L',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuWClose.setMnemonic(KeyEvent.VK_C);

        menuWindow.add(menuWOpen);
        menuWindow.add(menuWClose);

        menuDate = new JMenu("  Date  ");
        menuDate.setMnemonic(KeyEvent.VK_A);

        menuPrev = new JMenuItem(" Previous ");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              previousDate();
          }
        };
        menuPrev.addActionListener( lst );
        menuPrev.setEnabled(false);
        menuPrev.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuPrev.setMnemonic(KeyEvent.VK_P);

        menuNext = new JMenuItem(" Next ");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              nextDate();
          }
        };
        menuNext.addActionListener( lst );
        menuNext.setEnabled(false);
        menuNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(),false));
        menuNext.setMnemonic(KeyEvent.VK_N);

        menuDate.add(menuPrev);
        menuDate.add(menuNext);

        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              switchScreen();
          }
        };
        menuScreen = new JMenu(" Views");
        menuScreen.setMnemonic(KeyEvent.VK_V);
        ButtonGroup mGroup = new ButtonGroup();
        menuViews = new JMenuItem[views.length];
        for (int i1 = 0; i1 < 7; i1++) {
            menuViews[i1] = new JRadioButtonMenuItem(views[i1]);
            menuViews[i1].setEnabled(false);
            menuViews[i1].setSelected(i1 == 0);
            menuViews[i1].setMnemonic('1' + i1);
            menuViews[i1].addActionListener( lst );
            mGroup.add(menuViews[i1]);
            menuScreen.add(menuViews[i1]);
            if (i1 == 3) menuScreen.addSeparator();
        }

        menuSwitchPlan  = new JMenuItem(" Switch Plan");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              switchResourcePlan();
          }
        };
        menuSwitchPlan.addActionListener( lst );
        menuSwitchPlan.setEnabled(false);
        menuSwitchPlan.setMnemonic(KeyEvent.VK_P);

        menuSwitchUser  = new JMenuItem(" Switch User");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              switchUser();
          }
        };
        menuSwitchUser.addActionListener( lst );
        menuSwitchUser.setEnabled(false);
        menuSwitchUser.setMnemonic(KeyEvent.VK_U);

        menuSwitchSession  = new JMenuItem(" Switch Session");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              switchSession();
          }
        };
        menuSwitchSession.addActionListener( lst );
        menuSwitchSession.setEnabled(false);
        menuSwitchSession.setMnemonic(KeyEvent.VK_S);

        menuSwitch = new JMenu(" Switch");
        menuSwitch.setMnemonic(KeyEvent.VK_S);
        menuSwitch.add(menuSwitchPlan);
        menuSwitch.add(menuSwitchUser);
        menuSwitch.add(menuSwitchSession);

        menuAboutFE = new JMenuItem("About Scheduler FE Grid");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            helpDialogScreen();
          }
        };
        menuAboutFE.addActionListener( lst );
        menuAboutFE.setMnemonic(KeyEvent.VK_A);

        menuHelp = new JMenu("  Help  ");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuHelp.add(menuAboutFE);

        menuPending = new JMenu("Pending Area");
        menuPending.setMnemonic(KeyEvent.VK_P);

        menuPACreate = new JMenuItem("Create");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            createPendingArea();
          }
        };
        menuPACreate.addActionListener( lst );
        menuPACreate.setMnemonic(KeyEvent.VK_R);
        menuPACreate.setEnabled(false);

        menuPAClear = new JMenuItem("Clear");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            clearPendingArea();
          }
        };
        menuPAClear.addActionListener( lst );
        menuPAClear.setMnemonic(KeyEvent.VK_C);
        menuPAClear.setEnabled(false);

        menuPAValidate = new JMenuItem("Validate");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            validatePendingArea();
          }
        };
        menuPAValidate.addActionListener( lst );
        menuPAValidate.setMnemonic(KeyEvent.VK_V);
        menuPAValidate.setEnabled(false);

        menuPASubmit = new JMenuItem("Submit");
        lst = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            submitPendingArea();
          }
        };
        menuPASubmit.addActionListener( lst );
        menuPASubmit.setMnemonic(KeyEvent.VK_S);
        menuPASubmit.setEnabled(false);

        menuPending.add(menuPACreate);
        menuPending.add(menuPAClear);
        menuPending.add(menuPAValidate);
        menuPending.add(menuPASubmit);

        menuFile.add(menuConnect);
        menuFile.add(menuDisconnect);
        menuFile.addSeparator();
        menuFile.add(menuAutoConnect);
        menuFile.addSeparator();
        menuFile.add(menuRefresh);
        menuFile.add(menuOption);
        menuFile.addSeparator();
        menuFile.add(menuExit);

        menuBar1 = new JMenuBar();
        menuBar1.setBackground(mGlobalArea.getScreenColor(33));
        menuBar2 = new JMenuBar();
        menuBar2.setBackground(mGlobalArea.getScreenColor(33));

        menuBar1.add(menuFile);
        menuBar1.add(menuAdd1);
        menuBar1.add(menuObject1);
        menuBar1.add(menuJob);
        menuBar1.add(menuWindow);
        menuBar1.add(menuGroup);
        menuBar1.add(menuDate);
        menuBar1.add(menuScreen);
        menuBar1.add(menuHelp);
    }

    public void enableResourceMenu() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.enableResourceMenu");

        menuPACreate.setEnabled(false);
        menuPAClear.setEnabled(true);
        menuPAValidate.setEnabled(true);
        menuPASubmit.setEnabled(true);

        menuAddItem[16].setEnabled(true);
        menuAddItem[17].setEnabled(true);
        menuAddItem[18].setEnabled(false);
        menuAddItem[19].setEnabled(false);
        menuAddItem[20].setEnabled(false);
    }

    public void disableResourceMenu() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.disableResourceMenu");

        menuPACreate.setEnabled(true);
        menuPAClear.setEnabled(false);
        menuPAValidate.setEnabled(false);
        menuPASubmit.setEnabled(false);

        menuAddItem[16].setEnabled(false);
        menuAddItem[17].setEnabled(false);
        menuAddItem[18].setEnabled(false);
        menuAddItem[19].setEnabled(false);
        menuAddItem[20].setEnabled(false);

        buttonUpdate.setEnabled(false);
        menuUpdate.setEnabled(false);
        buttonDrop.setEnabled(false);
        menuDrop.setEnabled(false);
    }

    public void SetupToolbar()
    {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.SetupToolbar");

        // System.out.println( "1." + Paths.get("").toAbsolutePath().toString());
        // System.out.println( "2." + System.getProperty("user.dir"));
        // System.out.println( "2." + this.getClass().getName());

        cl = this.getClass().getClassLoader();

        Color bColor = mGlobalArea.getScreenColor(30);

        Font bFont = new Font("DIALOG",Font.PLAIN,12);

        Icon connectIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconConnect.gif"));
        Icon disconnectIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconDisconnect.gif"));
        Icon exitIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconExit.gif"));
        Icon refreshIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconRefresh.gif"));
        Icon enableIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconEnable.gif"));
        Icon disableIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconDisable.gif"));
        Icon dropIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconDrop.gif"));
        Icon updateIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconUpdate.gif"));
        Icon copyIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconCopy.gif"));

        switchIcon1 = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconSwitch1.gif"));
        switchIcon2 = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconSwitch2.gif"));
        switchIcon3 = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconSwitch3.gif"));
        switchIcon4 = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconSwitch4.gif"));

        prevIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconLeft.gif"));
        nextIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconRight.gif"));
        upIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconUp.gif"));
        downIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconDown.gif"));

        runIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconRun.gif"));
        stopIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconStop.gif"));
        blankIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconBlank.gif"));
        detailIcon = new ImageIcon(cl.getResource(PACKAGE_DIR + "IconDetail.gif"));

        upDownButton = false;

        buttonExit = new JButton(exitIcon);
        buttonExit.setToolTipText("Exit Scheduler FE System.");
        buttonExit.addActionListener( this );
        setButton(buttonExit, bFont, bColor,"Exit","    Exit   ");
        buttonExit.setEnabled(true);

        buttonRefresh = new JButton(refreshIcon);
        buttonRefresh.setToolTipText("Refresh all the Scheduler objects.");
        buttonRefresh.addActionListener( this );
        setButton(buttonRefresh, bFont, bColor,"Refresh","Refresh");

        buttonConnect = new JButton(connectIcon);
        buttonConnect.setToolTipText("Connect to database.");
        buttonConnect.addActionListener( this );
        setButton(buttonConnect, bFont, bColor,"Connect","Connect");
        buttonConnect.setEnabled(false);

        buttonDisconnect = new JButton(disconnectIcon);
        buttonDisconnect.setToolTipText("Disconnect from database.");
        buttonDisconnect.addActionListener( this );
        setButton(buttonDisconnect, bFont, bColor,"Disconnect","Disconnect");
        buttonDisconnect.setEnabled(false);

        buttonEnable = new JButton(enableIcon);
        buttonEnable.setToolTipText("Enable the currently selected object.");
        buttonEnable.addActionListener( this );
        setButton(buttonEnable, bFont, bColor,"Enable","Enable");
        enableButton = false;

        buttonDisable = new JButton(disableIcon);
        buttonDisable.setToolTipText("Disable the currently selected object.");
        buttonDisable.addActionListener( this );
        setButton(buttonDisable, bFont, bColor, "Disable", "Disable");
        disableButton = false;

        buttonCopy = new JButton(copyIcon);
        buttonCopy.setToolTipText("Copy the currently selected object.");
        buttonCopy.addActionListener( this );
        setButton(buttonCopy, bFont, bColor, "Copy", "Copy");

        buttonUpdate = new JButton(updateIcon);
        buttonUpdate.setToolTipText("Update the currently selected object.");
        buttonUpdate.addActionListener( this );
        setButton(buttonUpdate, bFont, bColor, "Update", "Update");

        buttonDrop = new JButton(dropIcon);
        buttonDrop.setToolTipText("Drop the currently selected object.");
        buttonDrop.addActionListener( this );
        setButton(buttonDrop, bFont, bColor, "Drop", "Drop");

        buttonRun = new JButton(blankIcon);
        buttonRun.setToolTipText(null);
        buttonRun.addActionListener( this );
        setButton(buttonRun, bFont, bColor, "Run", "  ");

        buttonStop = new JButton(blankIcon);
        buttonStop.setToolTipText(null);
        buttonStop.addActionListener( this );
        setButton(buttonStop, bFont, bColor, "Stop", "  ");

        buttonDetail = new JButton(blankIcon);
        buttonDetail.setToolTipText(null);
        buttonDetail.addActionListener( this );
        setButton(buttonDetail, bFont, bColor, "Detail", "  ");

        toolBar.setBackground(mGlobalArea.getScreenColor(33));
        toolBar.add(buttonExit);
        toolBar.add(buttonConnect);
        toolBar.add(buttonDisconnect);
        toolBar.add(buttonRefresh);
        toolBar.addSeparator();
        toolBar.add(buttonEnable);
        toolBar.add(buttonDisable);
        toolBar.addSeparator();
        toolBar.add(buttonUpdate);
        toolBar.add(buttonCopy);
        toolBar.add(buttonDrop);
        toolBar.addSeparator();
        toolBar.add(buttonRun);
        toolBar.add(buttonStop);
        toolBar.addSeparator();
        toolBar.add(buttonDetail);
        toolBar.addSeparator();

        cal = Calendar.getInstance();

        sdf_long = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf_short = new SimpleDateFormat("yyyy-MM-dd");
        sdf_desc = new SimpleDateFormat("dd-MMM-yyyy");

        mCurrentDate = new StringBuffer(sdf_long.format(cal.getTime()));
        mCurrentFormatDate = new StringBuffer(sdf_desc.format(cal.getTime()));
        mAfterDate = new StringBuffer("2000-01-01");
        // SchedFile.setDate(sdf_short.format(cal.getTime()));

        Dimension ld1 = new Dimension(80, 18);
        Dimension ld2 = new Dimension(120, 18);
        Dimension ld3 = new Dimension(160, 18);

        userName1 = new JLabel("  Username: ");
        userName1.setForeground( mGlobalArea.getScreenColor(20) );
        userName2 = new JLabel();
        userName2.setForeground( mGlobalArea.getScreenColor(20) );
        userName2.setPreferredSize(ld2);
        userName2.setBorder(BorderFactory.createLineBorder(mGlobalArea.getScreenColor(34)));

        hostName1 = new JLabel("      Hostname: ");
        hostName1.setForeground( mGlobalArea.getScreenColor(20) );
        hostName2 = new JLabel();
        hostName2.setForeground( mGlobalArea.getScreenColor(20) );
        hostName2.setPreferredSize(ld2);
        hostName2.setBorder(BorderFactory.createLineBorder(mGlobalArea.getScreenColor(34)));

        dateName1 = new JLabel("      Date: ");
        dateName1.setForeground( mGlobalArea.getScreenColor(20) );
        dateName2 = new JLabel();
        dateName2.setForeground( mGlobalArea.getScreenColor(20) );
        dateName2.setPreferredSize(ld1);
        dateName2.setBorder(BorderFactory.createLineBorder(mGlobalArea.getScreenColor(34)));
        dateName2.setText(mCurrentFormatDate.substring(0, 11));

        topPane.setBackground(mGlobalArea.getScreenColor(33));
        topPane.add(userName1);
        topPane.add(userName2);
        topPane.add(hostName1);
        topPane.add(hostName2);
        topPane.add(dateName1);
        topPane.add(dateName2);
    }

    private void setButton(JButton  mButton,
                             Font     mFont,
                             Color    mColor,
                             String   mCommand,
                             String   mText)
    {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setButton");

        if ( mDebug == 0) SchedFile.WriteDebugLine("setButton()");
        if ( SchedFile.getFileOption(10) ) mButton.setText(mText);
        mButton.setFont(mFont);
        mButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mButton.setHorizontalTextPosition(SwingConstants.CENTER);
        mButton.setBackground(mColor);
        mButton.setActionCommand( mCommand );
        mButton.setEnabled(false);
        mButton.setDisabledIcon(blankIcon);
        mButton.setPreferredSize(new Dimension(60,60));
    }

    // ActionListener handler to listen for button clicks
    // within this application frame.
    public void actionPerformed( ActionEvent event )
    {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.actionPerformed");

        if ( event.getActionCommand().equals("Connect") ) {
            openConnection();
        }
        if ( event.getActionCommand().equals("Disconnect") ) {
            closeConnection();
        }
        if ( event.getActionCommand().equals("Refresh") ) {
            refreshData();
        }
        if ( event.getActionCommand().equals("Exit") ) {
            exitSystem();
        }
        if ( event.getActionCommand().equals("Disable") ) {
            disableObject();
        }
        if ( event.getActionCommand().equals("Enable") ) {
            enableObject();
        }
        if ( event.getActionCommand().equals("Drop") ) {
            dropObject();
        }
        if ( event.getActionCommand().equals("Copy") ) {
            copyObject();
        }
        if ( event.getActionCommand().equals("Update") ) {
            updateObject();
        }
        if ( event.getActionCommand().equals("Run") ) {
            if (currentScreenDisp == 0) {
                if (currentScreenNo == JOB_SCREEN_NO)    runJob();
                if (currentScreenNo == WINDOW_SCREEN_NO) OpenWindow();
            }
            else {
            if ((currentScreenDisp == 1) || (currentScreenDisp == 2) || (currentScreenDisp == 3))
                    previousDate();
            }
            if (currentScreenDisp == 5)
                raiseMappingPriority();
            if (currentScreenDisp == 6)
                switchUser();
        }
        if ( event.getActionCommand().equals("Stop") ) {
            if (currentScreenDisp == 0) {
                if (currentScreenNo == JOB_SCREEN_NO)    stopJob();
                if (currentScreenNo == WINDOW_SCREEN_NO) CloseWindow();
            }

            if ((currentScreenDisp == 1) || (currentScreenDisp == 2) || (currentScreenDisp == 3))
                nextDate();
            if (currentScreenDisp == 4)
                stopJob();
            if (currentScreenDisp == 5)
                lowerMappingPriority();
            if (currentScreenDisp == 6)
                switchSession();
        }
        if ( event.getActionCommand().equals("Detail") ) {
            if (currentScreenDisp == 2) {
                displayLogDetail(2);
            }
            if (currentScreenDisp == 3) {
                displayLogDetail(3);
            }
            if (currentScreenDisp == 4) {
                displayLogDetail(4);
            }
            if (currentScreenDisp == 5) {
                switchResourcePlan();
            }
            if (currentScreenDisp == 6) {
                displayLogDetail(6);
            }
            if (currentScreenDisp == 7) {
                displayLogDetail(7);
            }
        }
        if ( event.getActionCommand().equals("Prev") ) {
            previousDate();
        }
        if ( event.getActionCommand().equals("Next") ) {
            nextDate();
        }
    }

    public void displayJobDetail() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.displayJobDetail");

        if (currentScreenDisp == 2)  displayLogDetail(2);
        if (currentScreenDisp == 3)  displayLogDetail(3);
        if (currentScreenDisp == 4)  displayLogDetail(4);
        if (currentScreenDisp == 6)  displayLogDetail(6);
        if (currentScreenDisp == 7)  displayLogDetail(7);
    }

    public void displayLogDetail(int mScreenNo) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.displayLogDetail");

        String mRet = null;
        int mRetInt = 0;
        int mScreenNo2 = mScreenNo;
        int lScreenNo = 0;
        NullPointerException npe2 = new NullPointerException();

        try {
            if (mScreenNo == 2) {

                if (mTable4.IsInFocus()) {
                    if (mTable4.getSelectedRow() == -1) {
                        throw npe2;
                    }
                    else {
                        mRet = (String)mDataJobLogStd.getValueAt(mTable4.getSelectedRow(),1);
                        mRetInt = Integer.parseInt(mRet);
                        mScreenNo2 = 2;
                        lScreenNo = JOB_LOG_SCREEN_NO;
                    }
                }
                else {
                    if (mTable5.IsInFocus()) {
                        if (mTable5.getSelectedRow() == -1) {
                            throw npe2;
                        }
                        else {
                            mRet = (String)mDataWindowLogStd.getValueAt(mTable5.getSelectedRow(),1);
                            mRetInt = Integer.parseInt(mRet);
                            mScreenNo2 = 3;
                            lScreenNo = WINDOW_LOG_SCREEN_NO;
                        }
                    }
                    else {
                        throw npe2;
                    }
                }
            }
            if (mScreenNo == 3) {
                if (mTable2.IsInFocus()) {
                    if (mTable2.getSelectedRow() == -1) {
                        throw npe2;
                    }
                    else {

                        mRet = (String)mDataJobLogDet.getValueAt(mTable2.getSelectedRow(),1);
                        mRetInt = Integer.parseInt(mRet);
                        mScreenNo2 = 4;
                        lScreenNo = JOB_RUN_DETAILS_SCREEN_NO;
                    }
                }
                else {
                    if (mTable3.IsInFocus()) {

                        if (mTable3.getSelectedRow() == -1) {
                            throw npe2;
                        }
                        else {
                            mRet = (String)mDataWindowLogDet.getValueAt(mTable3.getSelectedRow(),1);
                            mRetInt = Integer.parseInt(mRet);
                            mScreenNo2 = 5;
                            lScreenNo = WINDOW_DETAILS_SCREEN_NO;
                        }
                    }
                    else {
                        throw npe2;
                    }
                }
            }
            if (mScreenNo == 4) {
                if (mTable6.IsInFocus()) {

                    if (mTable6.getSelectedRow() == -1) {
                        throw npe2;
                    }
                    else {
                        mRet = (String)mDataRun.getValueAt(mTable6.getSelectedRow(),0);
                        mRetInt = Integer.parseInt(mRet);
                        mScreenNo2 = 6;
                        lScreenNo = JOBS_RUNNING_SCREEN_NO;
                    }
                }
                else {
                    if (mTable7.IsInFocus()) {

                        if (mTable7.getSelectedRow() == -1) {
                            throw npe2;
                        }
                        else {
                            mRet = (String)mDataChainRun.getValueAt(mTable7.getSelectedRow(),0);
                            mRetInt = Integer.parseInt(mRet);

                            mScreenNo2 = 7;
                            lScreenNo = CHAINS_RUNNING_SCREEN_NO;
                        }
                    }
                    else {
                        throw npe2;
                    }
                }
            }
            if (mScreenNo == 6) {
                // System.out.println( "3. " + mTable8.getSelectedRow());
                if (mTable8.getSelectedRow() == -1) {
                    throw npe2;
                }
                else {
                    mRet = (String)mDataSession.getValueAt(mTable8.getSelectedRow(),0);
                    mRetInt = Integer.parseInt(mRet);
                    mScreenNo2 = 8;
                    lScreenNo = SESSION_INFO_SCREEN_NO;
                }
            }
            if (mScreenNo == 7) {
                // System.out.println( "3. " + mTable9.getSelectedRow());
                if (mTable9.getSelectedRow() == -1) {
                    throw npe2;
                }
                else {
                    mRet = (String)mDataConsumerGroup.getValueAt(mTable9.getSelectedRow(),0);
                    mRetInt = Integer.parseInt(mRet);
                    mScreenNo2 = 9;
                    lScreenNo = CONSUMER_GROUP_INFO_SCREEN_NO;
                }
            }
        } catch (NullPointerException npe) {
            errorBox("Error - No Entry Selected");
            lScreenNo = 0;
        } catch (NumberFormatException nfe) {
            errorBox("Error - No Entry Selected");
            lScreenNo = 0;
        }
        if (lScreenNo > 0) {

            int lScreenId = mScreen.getScreenId(lScreenNo, dataArea.getVersion());

            // System.out.println( "3. Log Button Detail Pressed " + mRet +
            //                    "--" + lScreenNo + "--" + dataArea.getVersion() + "--" + lScreenId);

            for (int r2 = 0; r2 < mScreen.sizeScreenObj(); r2++) {
                mPane = mScreen.getScreenObj(r2);

                if (mPane.getScreenId() == lScreenId)
                {
                    detailScreen = true;
                    mJobDetailScreen = 
                            new SchedJobDetailScreen( this, dataArea, mGlobalArea, mPane, mScreenNo2, mRetInt);
                    mJobDetailScreen.setVisible( true );
                    detailScreen = false;
                    break;
                }
            }
        }
    }

    public void previousDate() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.previousDate");

        cal.add(Calendar.DATE, -1);
        mCurrentDate.delete(0, mCurrentDate.length());
        mCurrentDate.insert(0, sdf_long.format(cal.getTime()) );
        mStartDate1.delete(0, mStartDate1.length());

        mCurrentFormatDate.delete(0, mCurrentFormatDate.length());
        mCurrentFormatDate.insert(0, sdf_desc.format(cal.getTime()) );

        dateName2.setText(mCurrentFormatDate.substring(0, 11));

        cal.add(Calendar.DATE, -1);
        mStartDate1.insert(0, sdf_short.format(cal.getTime()) );
        mStartDate1.append(" 23:59:0");
        cal.add(Calendar.DATE, +1);

        if (currentScreenDisp == 2) {

            mGlobalArea.getJobLogData(mCurrentFormatDate.toString());
            mGlobalArea.getWindowLogData(mCurrentFormatDate.toString());

            mDataJobLogStd.clearJobLog();
            mDataWindowLogStd.clearWindowLog();

            createJobLogDataStd();

            createWindowLogDataStd();

            mTable4.tableChanged(new TableModelEvent(mDataJobLogStd));
            mTable4.repaint();

            mTable5.tableChanged(new TableModelEvent(mDataWindowLogStd));
            mTable5.repaint();
        }
        if (currentScreenDisp == 3) {

            mGlobalArea.getJobDetLogData(mCurrentFormatDate.toString());
            mGlobalArea.getWindowDetLogData(mCurrentFormatDate.toString());

            mDataJobLogDet.clearJobLog();
            mDataWindowLogDet.clearWindowLog();

            createJobLogDataDet();
            createWindowLogDataDet();

            mTable2.tableChanged(new TableModelEvent(mDataJobLogDet));
            mTable2.repaint();

            mTable3.tableChanged(new TableModelEvent(mDataWindowLogDet));
            mTable3.repaint();
        }
    }

    public void nextDate() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.nextDate");

        cal.add(Calendar.DATE, +1);
        mCurrentDate.delete(0, mCurrentDate.length());
        mCurrentDate.insert(0, sdf_long.format(cal.getTime()) );
        mStartDate1.delete(0, mStartDate1.length());

        mCurrentFormatDate.delete(0, mCurrentFormatDate.length());
        mCurrentFormatDate.insert(0, sdf_desc.format(cal.getTime()) );

        dateName2.setText(mCurrentFormatDate.substring(0, 11));

        cal.add(Calendar.DATE, -1);
        mStartDate1.insert(0, sdf_short.format(cal.getTime()) );
        mStartDate1.append(" 23:59:0");
        cal.add(Calendar.DATE, +1);

        if (currentScreenDisp == 2) {

            mGlobalArea.getJobLogData(mCurrentFormatDate.toString());
            mGlobalArea.getWindowLogData(mCurrentFormatDate.toString());

            mDataJobLogStd.clearJobLog();
            mDataWindowLogStd.clearWindowLog();

            createJobLogDataStd();
            createWindowLogDataStd();

            mTable4.tableChanged(new TableModelEvent(mDataJobLogStd));
            mTable4.repaint();

            mTable5.tableChanged(new TableModelEvent(mDataWindowLogStd));
            mTable5.repaint();
        }
        if (currentScreenDisp == 3) {

            mGlobalArea.getJobDetLogData(mCurrentFormatDate.toString());
            mGlobalArea.getWindowDetLogData(mCurrentFormatDate.toString());

            mDataJobLogDet.clearJobLog();
            mDataWindowLogDet.clearWindowLog();

            createJobLogDataDet();
            createWindowLogDataDet();

            mTable2.tableChanged(new TableModelEvent(mDataJobLogDet));
            mTable2.repaint();

            mTable3.tableChanged(new TableModelEvent(mDataWindowLogDet));
            mTable3.repaint();
        }
    }

    private void switchResourcePlan() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchResourcePlan");
        if ((currentScreenNo == PLAN_SCREEN_NO) ||
            (currentScreenNo == CDB_PLAN_SCREEN_NO)) {

            int mReturnNo = warningBox("Confirm that you want to switch the current " +
                              "Resource Plan to " + CurrentNode.getNodeName() + ".");
            if (mReturnNo == 0) {
                if (dataArea.SwitchPlan(CurrentNode.getNodeName()) != 0) {
                    errorBox("Error - " + dataArea.getSysMessage());
                }
            }
        }
        else {
            int mReturnNo = warningBox("Confirm that you want to switch the current " +
                              "Resource Plan Off.");
            if (mReturnNo == 0) {
                if (dataArea.SwitchPlanOff() != 0) {
                    errorBox("Error - " + dataArea.getSysMessage());
                }
            }
        }
    }

    public void switchUser() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchUser");

        // System.out.println( "1. Switch User.");

        if (mTable8.getSelectedRow() < 0) {
            errorBox("Error - No Row Currently Selected");
        }
        else {
            mUsername = (String)mDataSession.getValueAt(mTable8.getSelectedRow(),2);
            if (mUsername.length() == 0) {
                errorBox("Error - No Valid User For Currently Selected Row");
            }
            else {
                SchedInputScreen inputDialog = new SchedInputScreen(
                    this, dataArea, mGlobalArea, mScreenInp, 137, 0 );
                inputDialog.setVisible( true );
            }
        }
    }

    public String getUsername() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("getUsername");
        return mUsername;
    }

    public void switchSession() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchSession");

        // System.out.println( "1. Switch Session." + dataArea.consumerGroupSize());

        if (mTable8.getSelectedRow() < 0) {
            errorBox("Error - No Row Currently Selected");
        }
        else {
            mSid = Integer.parseInt((String)mDataSession.getValueAt(mTable8.getSelectedRow(),0));
            mSerial = Integer.parseInt((String)mDataSession.getValueAt(mTable8.getSelectedRow(),1));

            SchedInputScreen inputDialog = new SchedInputScreen(
                    this, dataArea, mGlobalArea, mScreenInp, 138, 0 );
            inputDialog.setVisible( true );
        }
    }

    public int getSid() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getSid");

        return mSid;
    }
    public int getSerial() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getSerial");

        return mSerial;
    }

    public void exitSystem() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.exitSystem");

        for (int i1 = 0; i1 < mGlobalArea.sizeDataAreaObj(); i1++) {
            dataArea = mGlobalArea.getDataAreaObj(i1);
            dataArea.CloseConnection();
            dataArea.clearSchedulerVectors();
        }
        mGlobalArea.clearVectors();

        SchedFile.saveFrameSize(getSize());
        SchedFile.saveLocation(getLocation());
        SchedFile.saveDivLocation(2, splitPane2.getDividerLocation());
        SchedFile.saveDivLocation(3, splitPane3.getDividerLocation());
        SchedFile.saveDivLocation(4, splitPane4.getDividerLocation());
        SchedFile.saveDivLocation(5, splitPane5.getDividerLocation());
        SchedFile.saveDivLocation(6, splitPane6.getDividerLocation());

        SchedFile.writeInitParams();
        SchedFile.writePassParams();

        System.exit(0);
    }

    private void openConnection() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.openConnection");

        if (CurrentNode.getIconType() == CONNECTION_SCREEN_NO) {
            if (CurrentNode.isConnected()) {

                CurrentNode.setAllowsChildren(true);
                CurrentNode.setIsConnected(2);

                currentScreenDisp = 0;
                mConnectionItem = mGlobalArea.getConnectionObjById(CurrentNode.getNodeId());
                mConnectionId = CurrentNode.getConnectId();
                mConnectionItem = mGlobalArea.getConnectionObjById(mConnectionId);
                mConnectionItem.setFullConnected();

                fetchSchedulerData();
                gTreeModel.nodeChanged( CurrentNode );

                splitPane2.setDividerLocation(SchedFile.getDivLocation(2));
                setupButtons();
                menuBar1.repaint();
            }
            else {
                SchedFile.saveDivLocation(2, splitPane2.getDividerLocation());

                mConnectionItem = mGlobalArea.getConnectionObjById(CurrentNode.getNodeId());

                SchedLogon logonDialog = new SchedLogon( this, mGlobalArea, CurrentNode);
                logonDialog.setupLogon(mConnectionItem);
                logonDialog.pack();
                logonDialog.setFocus();
                logonDialog.setVisible( true );
            }
        }
        else {
            errorBox("Error - Must be Positioned on a Connection Node");
        }
    }

    public void setDataArea(SchedDataArea lDataArea) {
        dataArea = lDataArea;
    }

    public void connectionOpen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.connectionOpen");

        if (dataArea.getVersionNo() == 0) {
            errorBox(dataArea.getSysMessage().toString());
        }
        else {
            CurrentNode.setAllowsChildren(true);
            CurrentNode.setIsConnected(2);

            mGlobalArea.addDataAreaObj(dataArea);
            currentScreenDisp = 0;
            mConnectionId = CurrentNode.getConnectId();
            mConnectionItem = mGlobalArea.getConnectionObjById(mConnectionId);
            mConnectionItem.setFullConnected();

            dataArea.setConnectId(mConnectionId);

            // Gets the Scheduler data from the database.

            fetchSchedulerData();
            gTreeModel.nodeChanged( CurrentNode );

            splitPane2.setDividerLocation(SchedFile.getDivLocation(2));
            setupButtons();
            menuBar1.repaint();
        }
    }

    private void startAutoConnect() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.startAutoConnect");

        SchedLogon2 logonConnection = new SchedLogon2(this, mGlobalArea);
        for (int i = 0; i < mGlobalArea.sizeConnectionObj(); i++) {
            SchedGlobalData.connectionItem lConnectionItem = mGlobalArea.getConnectionObj(i);
            if ( lConnectionItem.isAutoConnect() ) {
                logonConnection.getConnection(lConnectionItem);
            }
        }
    }

    public void connectAutoOpen(SchedDataArea  lDataArea,
                                SchedGlobalData.connectionItem  lConnectItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.connectAutoOpen");

        mGlobalArea.addDataAreaObj(lDataArea);
        lConnectItem.setConnected();

        SchedDataNode localNode = null;
        Enumeration e1 = groot.children();
        boolean mContinue = true;
        while (  mContinue ) {
            if ( e1.hasMoreElements() ) {
                localNode = (SchedDataNode)e1.nextElement();
                if ( localNode.getNodeId() == lConnectItem.getConnectionId())
                    mContinue = false;
            }
            else {
                mContinue = false;
            }
        }
        localNode.setIsConnected(1);
        localNode.setAllowsChildren(false);
        gTreeModel.nodeChanged( localNode );

    }

    public void setupLogScreens() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setupLogScreens");

        // Get todays date.
        cal.add(Calendar.DATE, -1);
        mStartDate1 = new StringBuffer(sdf_short.format(cal.getTime()) );
        mStartDate1.append(" 23:59:0");
        cal.add(Calendar.DATE, +1);

        // Set up the table for the log data.
        mDataJobLogDet = new SchedJobLogDetData();

        mTable2 = new SchedTable("mTable2");
        mTable3 = new SchedTable("mTable3");
        mTable2.setupOtherTable(mTable3);
        mTable3.setupOtherTable(mTable2);

        mTable2.setAutoCreateColumnsFromModel(false);
        mTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mTable2.setModel(mDataJobLogDet);
        mTable2.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer2 = new DefaultTableCellRenderer();
        renderer2.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataJobLogDet.getColumnCount(); j1++) {
            renderer2.setHorizontalAlignment(
                    SchedJobLogDetData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedJobLogDetData.m_columns[j1].m_width,
                    renderer2,
                    null);
            mTable2.addColumn(mColumn);
        }

        mSPanel2 = new JPanel();
        mSPanel2.setLayout(new BoxLayout(mSPanel2, BoxLayout.Y_AXIS));
        mSPanel2.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader2 = new JLabel("Detail Job Log (Dba_Scheduler_Job_Run_Details)");
        setHeader(mHeader2);
        mSPanel2.add(mHeader2);

        mPanel2 = new JScrollPane(mTable2);
        mPanel2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel2.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, false));
        mPanel2.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel2.add(mPanel2);

        mDataWindowLogDet = new SchedWindowLogDetData();

        mTable3.setAutoCreateColumnsFromModel(false);
        mTable3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mTable3.setModel(mDataWindowLogDet);
        mTable3.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer3 = new DefaultTableCellRenderer();
        renderer3.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataWindowLogDet.getColumnCount(); j1++) {
            renderer3.setHorizontalAlignment(
                    SchedWindowLogDetData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedWindowLogDetData.m_columns[j1].m_width,
                    renderer3,
                    null);
            mTable3.addColumn(mColumn);
        }

        mSPanel3 = new JPanel();
        mSPanel3.setLayout(new BoxLayout(mSPanel3, BoxLayout.Y_AXIS));
        mSPanel3.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader3 = new JLabel("Detail Window Log (Dba_Scheduler_Window_Details)");
        setHeader(mHeader3);
        mSPanel3.add(mHeader3);

        mPanel3 = new JScrollPane(mTable3);
        mPanel3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel3.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, true));
        mPanel3.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel3.add(mPanel3);

        splitPane3.setTopComponent(mSPanel2);
        splitPane3.setBottomComponent(mSPanel3);

        // Set up the table for the Log detail data.
        mTable4 = new SchedTable("mTable4");
        mTable5 = new SchedTable("mTable5");
        mTable4.setupOtherTable(mTable5);
        mTable5.setupOtherTable(mTable4);

        mDataJobLogStd = new SchedJobLogStdData();

        mTable4.setAutoCreateColumnsFromModel(false);
        mTable4.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mTable4.setModel(mDataJobLogStd);
        mTable4.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer4 = new DefaultTableCellRenderer();
        renderer4.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataJobLogStd.getColumnCount(); j1++) {
            renderer4.setHorizontalAlignment(
                    SchedJobLogStdData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedJobLogStdData.m_columns[j1].m_width,
                    renderer4,
                    null);
            mTable4.addColumn(mColumn);
        }

        mSPanel4 = new JPanel();
        mSPanel4.setLayout(new BoxLayout(mSPanel4, BoxLayout.Y_AXIS));
        mSPanel4.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader4 = new JLabel("Standard Job Log (Dba_Scheduler_Job_Log)");
        setHeader(mHeader4);
        mSPanel4.add(mHeader4);

        mPanel4 = new JScrollPane(mTable4);
        mPanel4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel4.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, false));
        mPanel4.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel4.add(mPanel4);

        mDataWindowLogStd = new SchedWindowLogStdData();

        mTable5.setAutoCreateColumnsFromModel(false);
        mTable5.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mTable5.setModel(mDataWindowLogStd);
        mTable5.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer5 = new DefaultTableCellRenderer();
        renderer5.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataWindowLogStd.getColumnCount(); j1++) {
            renderer5.setHorizontalAlignment(
                    SchedWindowLogStdData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedWindowLogStdData.m_columns[j1].m_width,
                    renderer5,
                    null);
            mTable5.addColumn(mColumn);
        }

        mSPanel5 = new JPanel();
        mSPanel5.setLayout(new BoxLayout(mSPanel5, BoxLayout.Y_AXIS));
        mSPanel5.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader5 = new JLabel("Standard Window Log (Dba_Scheduler_Window_Log)");
        setHeader(mHeader5);
        mSPanel5.add(mHeader5);

        mPanel5 = new JScrollPane(mTable5);
        mPanel5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel5.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, false));
        mPanel5.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel5.add(mPanel5);

        splitPane4.setTopComponent(mSPanel4);
        splitPane4.setBottomComponent(mSPanel5);

        // Set up the table for the running data.
        mDataRun = new SchedRunData();
        // createRunData1();

        mTable6 = new SchedTable("mTable6");
        mTable7 = new SchedTable("mTable7");
        mTable6.setupOtherTable(mTable7);
        mTable7.setupOtherTable(mTable6);

        mTable6.setAutoCreateColumnsFromModel(false);
        mTable6.setModel(mDataRun);
        mTable6.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer6 = new DefaultTableCellRenderer();
        renderer6.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataRun.getColumnCount(); j1++) {
            renderer6.setHorizontalAlignment(
                    SchedRunData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedRunData.m_columns[j1].m_width,
                    renderer6,
                    null);
            mTable6.addColumn(mColumn);
        }

        mSPanel6 = new JPanel();
        mSPanel6.setLayout(new BoxLayout(mSPanel6, BoxLayout.Y_AXIS));
        mSPanel6.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader6 = new JLabel("Jobs Running");
        setHeader(mHeader6);
        mSPanel6.add(mHeader6);

        mPanel6 = new JScrollPane(mTable6);
        mPanel6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel6.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, false));
        mPanel6.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel6.add(mPanel6);


        // Set up the table for the running chain data.
        mDataChainRun = new SchedRunChainData();

        mTable7.setAutoCreateColumnsFromModel(false);
        mTable7.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mTable7.setModel(mDataChainRun);
        mTable7.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer7 = new DefaultTableCellRenderer();
        renderer7.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataChainRun.getColumnCount(); j1++) {
            renderer7.setHorizontalAlignment(
                    SchedRunChainData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedRunChainData.m_columns[j1].m_width,
                    renderer7,
                    null);
            mTable7.addColumn(mColumn);
        }

        mSPanel7 = new JPanel();
        mSPanel7.setLayout(new BoxLayout(mSPanel7, BoxLayout.Y_AXIS));
        mSPanel7.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader7 = new JLabel("Run View - Chains Running");
        setHeader(mHeader7);
        mSPanel7.add(mHeader7);

        mPanel7 = new JScrollPane(mTable7);
        mPanel7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel7.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, false));
        mPanel7.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel7.add(mPanel7);

        splitPane5.setTopComponent(mSPanel6);
        splitPane5.setBottomComponent(mSPanel7);

        // Set up the table for the session data.
        mDataSession = new SchedSessionData();

        mTable8 = new SchedSingleTable("mTable8");
        // mTable8 = new JXTable();
        mTable8.setAutoCreateColumnsFromModel(false);
        mTable8.setModel(mDataSession);
        mTable8.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer8 = new DefaultTableCellRenderer();
        renderer8.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataSession.getColumnCount(); j1++) {
            renderer8.setHorizontalAlignment(
                    SchedSessionData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedSessionData.m_columns[j1].m_width,
                    renderer8,
                    null);
            mTable8.addColumn(mColumn);
        }
        mSPanel8 = new JPanel();
        mSPanel8.setLayout(new BoxLayout(mSPanel8, BoxLayout.Y_AXIS));
        mSPanel8.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader8 = new JLabel("Current Sessions Running");
        setHeader(mHeader8);
        mSPanel8.add(mHeader8);

        mPanel8 = new JScrollPane(mTable8);
        mPanel8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel8.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, false));
        mPanel8.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel8.add(mPanel8);

        // Set up the table for the consumer group data.
        mDataConsumerGroup = new SchedConsumerGroupStatsData();

        mTable9 = new SchedSingleTable("mTable9");
        mTable9.setAutoCreateColumnsFromModel(false);
        mTable9.setModel(mDataConsumerGroup);
        mTable9.addHighlighter(HighlighterFactory.createAlternateStriping(
                               mGlobalArea.getScreenColor(36),
                               mGlobalArea.getScreenColor(37)));

        DefaultTableCellRenderer renderer9 = new DefaultTableCellRenderer();
        renderer9.setBackground(mGlobalArea.getScreenColor(31));

        for (int j1 = 0; j1 < mDataConsumerGroup.getColumnCount(); j1++) {
            renderer9.setHorizontalAlignment(
                    SchedConsumerGroupStatsData.m_columns[j1].m_alignment);
            TableColumn mColumn = new TableColumn(j1,
                    SchedConsumerGroupStatsData.m_columns[j1].m_width,
                    renderer9,
                    null);
            mTable9.addColumn(mColumn);
        }
        mSPanel9 = new JPanel();
        mSPanel9.setLayout(new BoxLayout(mSPanel9, BoxLayout.Y_AXIS));
        mSPanel9.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 3, false));

        JLabel mHeader9 = new JLabel("Current Consumer Groups Running");
        setHeader(mHeader9);
        mSPanel9.add(mHeader9);

        mPanel9 = new JScrollPane(mTable9);
        mPanel9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mPanel9.setBorder(new LineBorder(mGlobalArea.getScreenColor(38), 1, false));
        mPanel9.getViewport().setBackground(mGlobalArea.getScreenColor(32));

        mSPanel9.add(mPanel9);
    }

    public void setHeader(JLabel mHeader) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setHeader");

        mHeader.setFont(new Font("Helvetica",Font.BOLD, 14));
        mHeader.setForeground(mGlobalArea.getScreenColor(38));
        mHeader.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    }

    public void fetchSchedulerData() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.fetchSchedulerData");

        dataArea.GetJobData();

        dataArea.GetJobArgsData();

        dataArea.GetProgramData();

        dataArea.GetProgramArgsData();

        dataArea.GetScheduleData();

        dataArea.GetJobClassData();

        dataArea.GetWindowData();

        if (dataArea.getVersionNo() < 4) {
            dataArea.GetWindowGroupData();
            dataArea.GetWinGroupMembersData();
        }
        else {
            dataArea.GetGroupsData();
            dataArea.GetGroupMembersData();
        }

        dataArea.GetResourcePlanNames();

        dataArea.GetConsumerGroupNames();

        if (dataArea.getVersionNo() > 1) {

            dataArea.GetChainsData();

            dataArea.GetChainStepsData();

            dataArea.GetChainRulesData();

            dataArea.GetGlobalAttributesData();

            if (dataArea.getVersionNo() > 2) {

                dataArea.GetCredentialsData();

                if (dataArea.getVersionNo() > 3) {

                    dataArea.GetFileWatchersData();

                    dataArea.GetNotificationsData();

                    dataArea.GetJobDests();

                    dataArea.GetDests();

                    dataArea.GetDBDests();

                    dataArea.GetExtDests();
                }
            }
        }

        currentTree = SCHEDULER_ROOT_TREE;

        createSchedulerTreeData();

        splitPane2.setLeftComponent(gTreeView);
        splitPane2.setRightComponent(bottomPane);
        // splitPane1.setBottomComponent(splitPane2);

        menuObject1.removeAll();
        menuObject1.add(menuDrop);
        menuObject1.add(menuUpdate);
        menuObject1.add(menuCopy);
        menuObject1.addSeparator();
        menuObject1.add(menuEnable);
        menuObject1.add(menuDisable);

        menuBar1.removeAll();
        menuBar1.add(menuFile);
        menuBar1.add(menuAdd1);
        menuBar1.add(menuObject1);
        menuBar1.add(menuJob);
        menuBar1.add(menuWindow);
        menuBar1.add(menuGroup);
        menuBar1.add(menuDate);
        menuBar1.add(menuScreen);
        menuBar1.add(menuHelp);

        setJMenuBar(menuBar1);
    }

    public void fetchResourceData() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.fetchResourceData");

        dataArea.clearSchedulerVectors();
        dataArea.clearResourceVectors();

        if (dataArea.isCdbDatabase()) {
            dataArea.GetCdbPlansData();
            dataArea.GetCdbPlanDirectivesData();
            dataArea.GetPluggableDbNames();
        }
        else {
            dataArea.GetPlansData();
            dataArea.GetPlanDirectivesData();
        }

        dataArea.GetConsumerGroupsData();

        dataArea.GetMappingPriorityData();

        dataArea.GetGroupMappingsData();

        dataArea.GetConsumerPrivsData();

        currentScreenNo = 0;
        currentTree = RESOURCE_ROOT_TREE; 

        createResourceTree();

        splitPane6.setTopComponent(gTreeView1);
        splitPane6.setBottomComponent(gTreeView2);

        splitPane2.setLeftComponent(splitPane6);
        splitPane2.setRightComponent(bottomPane);
        splitPane1.setBottomComponent(splitPane2);

        menuObject2.removeAll();
        menuObject2.add(menuUpdate);
        menuObject2.add(menuDrop);

        menuBar2.removeAll();
        menuBar2.add(menuFile);
        menuBar2.add(menuPending);
        menuBar2.add(menuAdd2);
        menuBar2.add(menuObject2);
        menuBar2.add(menuSwitch);
        menuBar2.add(menuScreen);
        menuBar2.add(menuHelp);

        if (dataArea.isPendingArea(true)) {
            enableResourceMenu();
        }
        else {
            disableResourceMenu();
        }

        setJMenuBar(menuBar2);
    }

    private void closeConnection() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.closeConnection");

        mConnectionItem = mGlobalArea.getConnectionObjById(CurrentNode.getConnectId());

        dataArea.CloseConnection();

        dataArea.clearSchedulerVectors();

        mGlobalArea.dropDataAreaObj(CurrentNode.getConnectId());
        mConnectionItem.setDisconnected();

        CurrentNode.removeAllChildren();
        CurrentNode.setAllowsChildren(false);
        CurrentNode.setIsConnected(0);
        gTreeModel.reload();

        mConnectionId = 0;
        setupButtons();
        menuBar1.repaint();
    }

    private void refreshData() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshData");

        SchedFile.saveDivLocation(2, splitPane2.getDividerLocation());

        CurrentNode.removeAllChildren();
        gTreeModel.reload();
        gTreeView.revalidate();
        gTreeView.repaint();
        dataArea.clearSchedulerVectors();

        // while ( CurrentNode.getChildCount() > 0 ) {
        //     SchedDataNode tempNode =
        //             (SchedDataNode)CurrentNode.getChildAt(CurrentNode.getChildCount() - 1);
        //     gTreeModel.removeNodeFromParent(tempNode);
        // }

        fetchSchedulerData();
        gTreeModel.nodeChanged( CurrentNode );

        splitPane2.setDividerLocation(SchedFile.getDivLocation(2));
        setupButtons();
        menuBar1.repaint();
    }

    private void helpDialogScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.helpDialogScreen");

        SchedHelpScreen helpDialog = new SchedHelpScreen( this, mGlobalArea );

        helpDialog.setVisible( true );
    }

    private void optionDialogScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.optionDialogScreen");
        SchedOptionScreen optionDialog = new SchedOptionScreen( this, mGlobalArea );

        optionDialog.setVisible( true );
    }

    private void switchScreen() {
        if (menuViews[0].isSelected()) switchToSchedulerScreen();
        if (menuViews[1].isSelected()) switchToStdLogScreen();
        if (menuViews[2].isSelected()) switchToDetLogScreen();
        if (menuViews[3].isSelected()) switchToRunScreen();
        if (menuViews[4].isSelected()) switchToResourceScreen();
        if (menuViews[5].isSelected()) switchToSessionScreen();
        if (menuViews[6].isSelected()) switchToConsumerStatsScreen();
    }

    private void switchToSchedulerScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchToSchedulerScreen");
        cleanScreenUp();
        if ( mResourceMenu ) {
            fetchSchedulerData();
            mResourceMenu = false;
        }

        if (currentScreenDisp == 5) CurrentNode = SchedDataNode;
        currentScreenDisp = 0;
        CurrentNode = groot;
        gTree.setSelectionRow(0);

        switchMenu(currentScreenDisp);
        setupButtons();

        splitPane2.setDividerLocation(SchedFile.getDivLocation(2));

        bottomPane.getViewport().add( label );
        splitPane2.setLeftComponent(gTreeView);
        splitPane2.setRightComponent(bottomPane);
        splitPane1.setBottomComponent(splitPane2);
    }

    private void switchToResourceScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchToResourceScreen");

        if (currentScreenDisp == 0) SchedDataNode = CurrentNode;
        cleanScreenUp();

        if ( ! mResourceMenu ) {
            fetchResourceData();
            mResourceMenu = true;
        }

        currentScreenDisp = 5;
        CurrentNode = groot1;
        gTree1.setSelectionRow(0);

        switchMenu(currentScreenDisp);
        setupButtons();

        bottomPane.getViewport().add( label );

        splitPane2.setDividerLocation(SchedFile.getDivLocation(7));
        splitPane6.setDividerLocation(SchedFile.getDivLocation(6));

        splitPane6.setTopComponent(gTreeView1);
        splitPane6.setBottomComponent(gTreeView2);

        splitPane2.setLeftComponent(splitPane6);
        splitPane2.setRightComponent(bottomPane);
        splitPane1.setBottomComponent(splitPane2);

        runResourceThread();
    }

    private void runResourceThread() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.runResourceThread");

        // Create a new thread that checks for Resource Data.
        runResourceData = new threadGetResourceData( this, dataArea, groot1, gTreeModel1);
        jobThread3 = new Thread(runResourceData);
        // jobThread3.setPriority(NORM_PRIORITY);
        jobThread3.start();
    }

    private void switchToDetLogScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchToDetLogScreen");

        cleanScreenUp();
        setupResourceMenu();

        currentScreenDisp = 3;
        switchMenu(currentScreenDisp);

        mGlobalArea.clearDetJobLogVector();
        mGlobalArea.clearDetWindowLogVector();

        if (mGlobalArea.getJobDetLogData(mCurrentFormatDate.toString()))
            errorBox("Error Fetching Detail Job Log Data - Check Error File.");

        if (mGlobalArea.getWindowDetLogData(mCurrentFormatDate.toString()))
            errorBox("Error Fetching Detail Window Log Data - Check Error File.");

        splitPane1.setBottomComponent(splitPane3);

        mDataJobLogDet.clearJobLog();
        mDataWindowLogDet.clearWindowLog();

        createJobLogDataDet();
        createWindowLogDataDet();

        mTable2.tableChanged(new TableModelEvent(mDataJobLogDet));
        mTable2.repaint();

        mTable3.tableChanged(new TableModelEvent(mDataWindowLogDet));
        mTable3.repaint();
    }

    private void switchToStdLogScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchToStdLogScreen");
        cleanScreenUp();
        setupResourceMenu();

        currentScreenDisp = 2;
        switchMenu(currentScreenDisp);

        mGlobalArea.clearJobLogVector();
        mGlobalArea.clearWindowLogVector();

        if (mGlobalArea.getJobLogData(mCurrentFormatDate.toString()))
            errorBox("Error Fetching Standard Job Log Data - Check Error File.");

        if (mGlobalArea.getWindowLogData(mCurrentFormatDate.toString()))
            errorBox("Error Fetching Standard Window Log Data - Check Error File.");

        splitPane1.setBottomComponent(splitPane4);

        mDataJobLogStd.clearJobLog();
        mDataWindowLogStd.clearWindowLog();

        createJobLogDataStd();
        createWindowLogDataStd();

        mTable4.tableChanged(new TableModelEvent(mDataJobLogStd));
        mTable4.repaint();

        mTable5.tableChanged(new TableModelEvent(mDataWindowLogStd));
        mTable5.repaint();
    }

    private void switchToRunScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchToRunScreen");
        cleanScreenUp();

        if ( mResourceMenu ) {
            fetchSchedulerData();
            mResourceMenu = false;
        }
        setupResourceMenu();

        currentScreenDisp = 4;
        switchMenu(currentScreenDisp);

        splitPane1.setBottomComponent(splitPane5);

        createRunData1();

        mTable6.tableChanged(new TableModelEvent(mDataRun));
        mTable6.repaint();

        if (dataArea.getVersionNo() > 1) {
            mTable7.tableChanged(new TableModelEvent(mDataChainRun));
            mTable7.repaint();
        }

        // Create a new thread that checks for running jobs.
        runningJobs = new threadGetRunningJobs( this, mGlobalArea );
        jobThread2 = new Thread(runningJobs);
        // jobThread.setPriority(NORM_PRIORITY);
        jobThread2.start();
    }

    private void switchToSessionScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchToSessionScreen");
        cleanScreenUp();

        if ( ! mResourceMenu ) {
            fetchResourceData();
            mResourceMenu = true;
        }
        setupResourceMenu();

        currentScreenDisp = 6;
        switchMenu(currentScreenDisp);

        splitPane1.setBottomComponent(mSPanel8);

        createSessionData(false);

        mTable8.tableChanged(new TableModelEvent(mDataSession));
        mTable8.repaint();

        // Create a new thread that checks for running jobs.
        runningSessions = new threadGetSessions( this, dataArea );
        jobThread4 = new Thread(runningSessions);
        jobThread4.start();
    }

    private void switchToConsumerStatsScreen() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchToConsumerStatsScreen");
        cleanScreenUp();

        if ( ! mResourceMenu ) {
            fetchResourceData();
            mResourceMenu = true;
        }
        setupResourceMenu();

        currentScreenDisp = 7;
        switchMenu(currentScreenDisp);

        splitPane1.setBottomComponent(mSPanel9);

        createConsumerGroupData(false);

        mTable9.tableChanged(new TableModelEvent(mDataSession));
        mTable9.repaint();

        // Create a new thread that checks for running jobs.
        runningConsumerGroups = new threadGetConsumerGroups( this, dataArea );
        jobThread5 = new Thread(runningConsumerGroups);
        jobThread5.start();

    }

    private void cleanScreenUp() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.cleanScreenUp");

        if (currentScreenDisp == 2) {
            mGlobalArea.clearJobLogVector();
            mGlobalArea.clearWindowLogVector();
        }
        if (currentScreenDisp == 3) {
            mGlobalArea.clearDetJobLogVector();
            mGlobalArea.clearDetWindowLogVector();
        }
        if (currentScreenDisp == 4) {
            runningJobs.stopJob();
        }
        if (currentScreenDisp == 5) {
            runResourceData.stopJob();

            SchedFile.saveDivLocation(6, splitPane6.getDividerLocation());
            SchedFile.saveDivLocation(7, splitPane2.getDividerLocation());
        }
        if (currentScreenDisp == 6) {
            runningSessions.stopJob();
            dataArea.clearSessionsVector();
        }
        if (currentScreenDisp == 7) {
            runningConsumerGroups.stopJob();
            dataArea.clearConsumerGroupStatsVector();
        }
    }

    private void switchMenu(int mScreenNo) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.switchMenu");

        enableButton = false;
        disableButton = false;

        buttonEnable.setEnabled(false);
        menuEnable.setEnabled(false);
        buttonDisable.setEnabled(false);
        menuDisable.setEnabled(false);
        buttonUpdate.setEnabled(false);
        menuUpdate.setEnabled(false);
        buttonCopy.setEnabled(false);
        menuCopy.setEnabled(false);
        buttonDrop.setEnabled(false);
        menuDrop.setEnabled(false);
        menuSwitchPlan.setEnabled(false);

        if (mScreenNo == 6) {
            menuSwitchUser.setEnabled(true);
            menuSwitchSession.setEnabled(true);
        }
        else {
            menuSwitchUser.setEnabled(false);
            menuSwitchSession.setEnabled(false);
        }

        if ((mScreenNo == 0) || (mScreenNo == 5)) {
            menuRefresh.setEnabled(true);
            buttonRefresh.setEnabled(true);

            if (mScreenNo == 0)
                buttonRefresh.setToolTipText("Refresh all the Scheduler objects.");
            else
                buttonRefresh.setToolTipText("Refresh all the Resource objects.");
        }
        else {
            menuRefresh.setEnabled(false);
            buttonRefresh.setEnabled(false);
            buttonRefresh.setToolTipText(null);
        }

        if ((mScreenNo == 0) || (mScreenNo == 4) || (mScreenNo == 5) ||
            (mScreenNo == 6) || (mScreenNo == 7)) {
            buttonRun.setEnabled(false);
            buttonStop.setEnabled(false);

            if ((mScreenNo == 0) || (mScreenNo == 4)) {
                buttonRun.setIcon(runIcon);
                if ( SchedFile.getFileOption(12) ) buttonRun.setText("Run");
                buttonRun.setToolTipText("Run the currently selected Job");
            }
            else {
                if (mScreenNo == 6) {
                    buttonRun.setIcon(switchIcon4);
                    if ( SchedFile.getFileOption(12) ) buttonRun.setText("Switch");
                    buttonRun.setToolTipText("Switch the Consumer Group for the selected User");
                    buttonRun.setEnabled(true);
                }
                else {
                    buttonRun.setIcon(blankIcon);
                    if ( SchedFile.getFileOption(12) ) buttonRun.setText(" ");
                    buttonRun.setToolTipText(null);
                }
            }

            if ((mScreenNo == 0) || (mScreenNo == 4)) {
                buttonStop.setIcon(stopIcon);
                if ( SchedFile.getFileOption(12) ) buttonStop.setText("Stop");
                buttonStop.setToolTipText("Stop the currently selected Job");
            }
            else {
                if (mScreenNo == 6) {
                    buttonStop.setIcon(switchIcon3);
                    if ( SchedFile.getFileOption(12) ) buttonStop.setText("Switch");
                    buttonStop.setToolTipText("Switch the Consumer Group for the selected Session");
                    buttonStop.setEnabled(true);
                }
                else {
                    buttonStop.setIcon(blankIcon);
                    if ( SchedFile.getFileOption(12) ) buttonStop.setText(" ");
                    buttonStop.setToolTipText(null);
                }
            }

            menuPrev.setEnabled(false);
            menuNext.setEnabled(false);
        }

        if ((mScreenNo == 1) || (mScreenNo == 2) || (mScreenNo == 3)) {
            buttonRun.setEnabled(true);
            buttonRun.setIcon(prevIcon);
            if ( SchedFile.getFileOption(12) ) buttonRun.setText("Prev");
            buttonRun.setToolTipText("Previous Day.");
            buttonStop.setEnabled(true);
            buttonStop.setIcon(nextIcon);
            if ( SchedFile.getFileOption(12) ) buttonStop.setText("Next");
            buttonStop.setToolTipText("Next Day.");

            menuJobStop.setEnabled(false);
            menuPrev.setEnabled(true);
            menuNext.setEnabled(true);
        }

        // System.out.println("A1 --" + mScreenNo);
        if ((mScreenNo == 2) || (mScreenNo == 3) || (mScreenNo == 4) ||
            (mScreenNo == 6) || (mScreenNo == 7)) {
            menuJobDetail.setEnabled(true);
        }
        else {
            if (mScreenNo != 5) {
                menuJobDetail.setEnabled(false);
            }
        }

        for (int i1 = 0; i1 < 15; i1++) {
            menuAddItem[i1].setEnabled(false);
        }
        // System.out.println("A1 --" + mScreenNo);
        if ((mScreenNo == 2) || (mScreenNo == 3) || (mScreenNo == 4) ||
            (mScreenNo == 6) || (mScreenNo == 7)) {
            buttonDetail.setEnabled(true);
            buttonDetail.setIcon(detailIcon);
            if ( SchedFile.getFileOption(12) ) buttonDetail.setText("Detail");
            if (mScreenNo == 6) {
                buttonDetail.setToolTipText("Display Session entry details.");
            }
            else {

                if (mScreenNo == 6) {
                    buttonDetail.setToolTipText("Display Consumer Group entry details.");
                }
                else {
                    buttonDetail.setToolTipText("Display log entry details.");
                }
            }
            menuJobDetail.setEnabled(true);
        }
        else {
            if (mScreenNo == 5) {
                buttonDetail.setEnabled(true);
                buttonDetail.setIcon(switchIcon2);
                if ( SchedFile.getFileOption(12) ) buttonDetail.setText("Switch");
                buttonDetail.setToolTipText("Switch the Resource Plan.");
                menuSwitchPlan.setEnabled(true);
            }
            else {
                buttonDetail.setEnabled(false);
                buttonDetail.setIcon(blankIcon);
                if ( SchedFile.getFileOption(12) ) buttonDetail.setText(" ");
                buttonDetail.setToolTipText(null);
                menuJobDetail.setEnabled(false);
            }
        }

        menuDrop.setEnabled(false);
        menuCopy.setEnabled(false);
        menuUpdate.setEnabled(false);
        menuEnable.setEnabled(false);
        menuDisable.setEnabled(false);
        menuJobRun.setEnabled(false);

        menuPurge.setEnabled(false);
        menuWOpen.setEnabled(false);
        menuWClose.setEnabled(false);
        menuAddGroup.setEnabled(false);
        menuRemoveGroup.setEnabled(false);
    }

    public void createSessionData(boolean mNotFirst) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createSessionData");

        boolean mTable8Selected = false;
        int mSidNo = 0;
        int mRowNo8 = -1;
        if ((mTable8.getSelectedRow() != -1) && (mNotFirst)) {
            mSidNo = Integer.parseInt((String)mTable8.getValueAt(mTable8.getSelectedRow(),0));
            mTable8Selected = true;
        }
        // System.out.println("Sid - " + mSidNo + "Row - " + mRowNo8);

        int mCount = 0;
        mDataSession.clearSessions();
        for (int j1 = 0; j1 < dataArea.sessionsSize(); j1++) {

            gSessionItem = dataArea.getSession(j1);
            String mState = gSessionItem.getState();

            if ( ( (mState.equals("NOT MANAGED") ) && ( ! SchedFile.getFileOption(14) ) ) ||
               ( ! mState.equals("NOT MANAGED") ) )
            {

                int mSid = gSessionItem.getSid();
                int mSerial = gSessionItem.getSerial();
                String mUsername = gSessionItem.getUsername();
                String mConsumerGroup = gSessionItem.getConsumerGroup();
                int mConsumedCpuTime = gSessionItem.getConsumedCpuTime();
                int mCpuWaitTime = gSessionItem.getCpuWaitTime();
                int mQueuedTime = gSessionItem.getQueuedTime();

                mDataSession.addSession(Integer.toString(mSid),
                                    Integer.toString(mSerial),
                                    mUsername,
                                    mConsumerGroup,
                                    mState,
                                    Integer.toString(mConsumedCpuTime),
                                    Integer.toString(mCpuWaitTime),
                                    Integer.toString(mQueuedTime));

                if (mSid == mSidNo) mRowNo8 = mCount;
                mCount = mCount + 1;
            }
        }
        mDataSession.fireTableDataChanged();
        if ((mRowNo8  != -1) && (mTable8Selected)) {
            mTable8.setRowSelectionInterval(mRowNo8,mRowNo8);
            if (detailScreen) mJobDetailScreen.refreshSessionData(mSidNo);
        }
    }

    public void createConsumerGroupData(boolean mNotFirst) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createConsumerGroupData");

        String mConsumerGroup = null;
        boolean mTable9Selected = false;
        int mIdNo = 0;
        int mRowNo9 = -1;
        if ((mTable9.getSelectedRow() != -1) && (mNotFirst)) {
            mConsumerGroup = mTable9.getValueAt(mTable9.getSelectedRow(),1).toString();
            mTable9Selected = true;
        }

        mDataConsumerGroup.clearConsumerGroups();
        for (int j1 = 0; j1 < dataArea.consumerGroupStatsSize(); j1++) {

            gConsumerGroupStatsItem = dataArea.getConsumerGroupStats(j1);

            int mId = gConsumerGroupStatsItem.getId();
            String mName = gConsumerGroupStatsItem.getName();
            int mActiveSessions = gConsumerGroupStatsItem.getActiveSessions();
            int mQueueLength = gConsumerGroupStatsItem.getQueueLength();
            int mConsumedCpuTime = gConsumerGroupStatsItem.getConsumedCpuTime();
            int mCpuWaits = gConsumerGroupStatsItem.getCpuWaits();
            int mCpuWaitTime = gConsumerGroupStatsItem.getCpuWaitTime();

            mDataConsumerGroup.addConsumerGroup(Integer.toString(mId),
                                                mName,
                                                Integer.toString(mActiveSessions),
                                                Integer.toString(mQueueLength),
                                                Integer.toString(mConsumedCpuTime),
                                                Integer.toString(mCpuWaits),
                                                Integer.toString(mCpuWaitTime));

            if ((mConsumerGroup != null) && 
                mConsumerGroup.equals(gConsumerGroupStatsItem.getName())) {
                mRowNo9 = j1;
                mIdNo = mId;
            }

        }
        mDataConsumerGroup.fireTableDataChanged();
        if ((mRowNo9  != -1) && (mTable9Selected)) {
            mTable9.setRowSelectionInterval(mRowNo9,mRowNo9);
            if (detailScreen) mJobDetailScreen.refreshConsumerGroupData(mIdNo);
        }
    }

    public void saveRunData() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.saveRunData");
        if (mTable3.getSelectedRow() != -1) {
            int mRowNo3 = mTable3.getSelectedRow();
            int mRunId = Integer.parseInt((String)mDataRun.getValueAt(mRowNo3,0));
            mSavedSid = mGlobalArea.getJobsRunningId(mRunId).getSessionId();
        }
        else {
            mSavedSid = -1;
        }
    }

    public void saveChainData() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.saveChainData");

        if (mTable7.getSelectedRow() != -1) {
            int mRowNo7 = mTable7.getSelectedRow();
            int mChainId = Integer.parseInt((String)mDataChainRun.getValueAt(mRowNo7,0));
            sChainsRunningItem = mGlobalArea.getChainsRunningId(mChainId);
        }
    }

    public void createRunData1() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createRunData1");
        boolean mTable3Selected = false;
        int mRowNo3 = -1;
        if (mTable3.getSelectedRow() != -1) {
            mTable3Selected = true;
        }

        mDataRun.clearJobRun();
        for (int j1 = 0; j1 < mGlobalArea.jobsRunningSize(); j1++) {

            gJobsRunningItem = mGlobalArea.getJobsRunning(j1);

            mId           = gJobsRunningItem.getId();
            mDatabase     = gJobsRunningItem.getDatabase();
            mJobOwner     = gJobsRunningItem.getOwner();
            mJobName      = gJobsRunningItem.getJobName();
            mResourceConsumerGroup  
                          = gJobsRunningItem.getResourceConsumerGroup();
            mElapsedTime  = gJobsRunningItem.getElapsedTime();

            mDataRun.addJobRun(
                          Integer.toString(mId),
                          mDatabase,
                          mJobOwner,
                          mJobName,
                          mResourceConsumerGroup,
                          mElapsedTime);

            if (gJobsRunningItem.getSessionId() == mSavedSid) {
                mRowNo3 = j1;
            }
        }
        mDataRun.fireTableDataChanged();

        if ((mRowNo3 != -1) && (mTable3Selected)) {
            mTable3.setRowSelectionInterval(mRowNo3,mRowNo3);
        }
    }

    private void createJobLogDataStd() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createJobLogDataStd");

        for (int j1 = 0; j1 < mGlobalArea.JobLogSize(); j1++) {

            gJobLogItem = mGlobalArea.getJobLog(j1);
            mLogDate      = gJobLogItem.getLogDate();
            mLogId        = gJobLogItem.getLogId();
            mDatabase     = gJobLogItem.getDatabase();
            mJobOwner     = gJobLogItem.getOwner();
            mJobName      = gJobLogItem.getJobName();
            String mOperation    = gJobLogItem.getOperation();
            String mStatus       = gJobLogItem.getStatus();

            mDataJobLogStd.addJobLog(
                          mLogDate,
                          Integer.toString(mLogId),
                          mDatabase,
                          mJobOwner,
                          mJobName,
                          mOperation,
                          mStatus);
        }
    }

    private void createWindowLogDataStd() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createWindowLogDataStd");

        for (int j1 = 0; j1 < mGlobalArea.windowLogSize(); j1++) {

            gWindowLogItem = mGlobalArea.getWindowLog(j1);
            mLogDate             = gWindowLogItem.getLogDate();
            mLogId               = gWindowLogItem.getLogId();
            mDatabase            = gWindowLogItem.getDatabase();
            String mWindowName   = gWindowLogItem.getWindowName();
            String mOperation    = gWindowLogItem.getOperation();
            String mStatus       = gWindowLogItem.getStatus();

            mDataWindowLogStd.addWindowLog(
                          mLogDate,
                          Integer.toString(mLogId),
                          mDatabase,
                          mWindowName,
                          mOperation,
                          mStatus);
        }
    }

    private void createJobLogDataDet() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createJobLogDataDet");

        for (int j1 = 0; j1 < mGlobalArea.jobDetLogSize(); j1++) {

            gJobDetLogItem = mGlobalArea.getJobDetLog(j1);

            mLogDate       = gJobDetLogItem.getLogDate();
            mLogId         = gJobDetLogItem.getLogId();
            mDatabase      = gJobDetLogItem.getDatabase();
            mJobOwner      = gJobDetLogItem.getOwner();
            mJobName       = gJobDetLogItem.getJobName();
            mDuration      = gJobDetLogItem.getRunDuration();
            String mStatus = gJobDetLogItem.getStatus();

            mDataJobLogDet.addJobLog(
                          mLogDate,
                          Integer.toString(mLogId),
                          mDatabase,
                          mJobOwner,
                          mJobName,
                          mDuration,
                          mStatus);
        }
    }

    private void createWindowLogDataDet() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createWindowLogDataDet");

        for (int j1 = 0; j1 < mGlobalArea.windowDetLogSize(); j1++) {

            gWindowDetLogItem = mGlobalArea.getWindowDetails(j1);
            mLogDate             = gWindowDetLogItem.getLogDate();
            mLogId               = gWindowDetLogItem.getLogId();
            mDatabase            = gWindowDetLogItem.getDatabase();
            String mWindowName   = gWindowDetLogItem.getWindowName();

            String mWinDuration = gWindowDetLogItem.getWindowDuration();
            String mActDuration = gWindowDetLogItem.getActualDuration();

            mDataWindowLogDet.addWindowLog(
                          mLogDate,
                          Integer.toString(mLogId),
                          mDatabase,
                          mWindowName,
                          mWinDuration,
                          mActDuration);

        }
    }


    private void addObject(ActionEvent e) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.addObject");

        Double xPoint = new Double(getLocation().getX());
        Double yPoint = new Double(getLocation().getY());

        mGlobalArea.setFramePosition(xPoint.intValue(), yPoint.intValue());

        if (e.getActionCommand() == "Connection") {

            SchedConnect inputDialog = new SchedConnect(
                                    this, mGlobalArea, 0);
            inputDialog.setNewConnection();
            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Job") {

            SchedJobInputScreen inputDialog = new SchedJobInputScreen(
                                    this, dataArea, mGlobalArea, mScreenInp );
            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Job Argument") {
            if (currentScreenNo == JOB_SCREEN_NO) {
                SchedInputScreen inputDialog =
                        new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 513, 0 );
                inputDialog.setVisible( true );
            }
            else {
                errorBox("Error - Cannot Create a Job Argument." +
                         "\nNo Job currently selected.");
            }
        }
        if (e.getActionCommand() == "Job Notification") {
            if (currentScreenNo == JOB_SCREEN_NO) {
                SchedInputScreen inputDialog =
                        new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 527, 0 );
                inputDialog.setVisible( true );
            }
            else {
                errorBox("Error - Cannot Create a Job Notification." +
                         "\nNo Job currently selected.");
            }
        }
        if (e.getActionCommand() == "Program") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 506, 0 );
            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Program Argument") {
            if (currentScreenNo == PROGRAM_SCREEN_NO) {
                gProgramName = CurrentNode.getNodeName();
                SchedProgramArgInputScreen inputDialog = 
                        new SchedProgramArgInputScreen( 
                                    this, dataArea, mGlobalArea, gProgramItem, mScreenInp);
                inputDialog.setVisible( true );
            }
            else {
                errorBox("Error - Cannot Create a Program Argument." +
                             "\nNo Program currently selected.");
            }
        }
        if (e.getActionCommand() == "Schedule") {
            SchedScheduleInputScreen inputDialog = 
                    new SchedScheduleInputScreen( this, dataArea, mGlobalArea, mScreenInp);
            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Chain") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 515, 0 );
            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Chain Step") {
            if (currentScreenNo == CHAINS_SCREEN_NO) {
                gChainName = CurrentNode.getNodeName();
                gChainOwner = CurrentNode.getOwner();

                SchedChainStepInputScreen inputDialog = 
                    new SchedChainStepInputScreen( this, dataArea, mGlobalArea, mScreenInp);
                inputDialog.setVisible( true );
            }
            else {
                errorBox("Error - Cannot Create a Chain Rule." +
                         "\nNo Chain currently selected.");
            }
        }
        if (e.getActionCommand() == "Chain Rule") {
            if (currentScreenNo == CHAINS_SCREEN_NO) {
                gChainName = CurrentNode.getNodeName();
                gChainOwner = CurrentNode.getOwner();

                SchedInputScreen inputDialog = 
                        new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 516, 0 );
                inputDialog.setVisible( true );
            }
            else {
                errorBox("Error - Cannot Create a Chain Rule." +
                         "\nNo Chain currently selected.");
            }
        }
        if (e.getActionCommand() == "Credential") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 523, 0);

             inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Job Class") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 509, 0);
            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Window") {

            SchedWindowInputScreen inputDialog = 
                    new SchedWindowInputScreen( this, dataArea, mGlobalArea, mScreenInp);
            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Group") {
            if (dataArea.getVersionNo() < 4) {
                SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 512, 0 );
                inputDialog.setVisible( true );
            }
            else {
                SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 533, 0 );
                inputDialog.setVisible( true );
            }
        }
        if (e.getActionCommand() == "File Watcher") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 526, 0);

            inputDialog.setVisible( true );
        }

        if (e.getActionCommand() == "Destination") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 528, 0);

            inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Resource Plan") {
            if (dataArea.isCdbDatabase()) {
                SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 568, 0);

                inputDialog.setVisible( true );
            }
            else {
                SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 561, 0);

                inputDialog.setVisible( true );
            }
        }
        if (e.getActionCommand() == "Consumer Group") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 562, 0);

             inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Plan Directive") {
            if (dataArea.isCdbDatabase()) {
                SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 569, 0);

                inputDialog.setVisible( true );
            }
            else {
                SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 560, 0);

                inputDialog.setVisible( true );
            }
        }
        if (e.getActionCommand() == "Group Mapping") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 563, 0);

             inputDialog.setVisible( true );
        }
        if (e.getActionCommand() == "Group Privilege") {
            SchedInputScreen inputDialog = 
                    new SchedInputScreen( this, dataArea, mGlobalArea, mScreenInp, 566, 0);

             inputDialog.setVisible( true );
        }
    }

    public void refreshTree() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshTree");

        // splitPane2.setRightComponent(bottomPane);
        bottomPane.getViewport().add( label );
        splitPane2.setLeftComponent(gTreeView);
    }

    private void copyObject() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.copyObject");

        int mNodeId = CurrentNode.getNodeId();
        switch (currentScreenNo) {
            case JOB_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "JOB", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case PROGRAM_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "PROGRAM", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case SCHEDULE_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "SCHEDULE", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case JOB_CLASS_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "JOB CLASS", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case WINDOW_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "WINDOW", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case WINDOW_GROUP_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "WINDOW GROUP", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case JOB_ARGS_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "JOB ARGUMENT", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case PROGRAM_ARGS_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "PROGRAM ARGUMENT", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case CHAINS_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "CHAIN", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case CHAIN_STEPS_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "CHAINSTEP", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case CHAIN_RULES_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "CHAINRULE", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case CREDENTIALS_SCREEN_NO:
                errorBox("Error - Cannot Create a Credential" +
                             "\nObject with the Copy Utility.");
                break;
            case FILE_WATCHERS_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "FILE WATCHER", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
                break;
            case GROUP_SCREEN_NO:
                inputDialog = new SchedCopyScreen(
                        this, dataArea, mGlobalArea, mScreenInp, "GROUP", getObjectName(), mNodeId);
                inputDialog.setVisible( true );
        }
    }

    private String getObjectName() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getObjectName");

        if ((currentScreenNo == JOB_CLASS_SCREEN_NO) ||
            (currentScreenNo == WINDOW_SCREEN_NO) ||
            (currentScreenNo == WINDOW_GROUP_SCREEN_NO))
        {
            return CurrentNode.getNodeName();
        }
        else {
            if (ParentNode.getNodeName().equals(dataArea.getUserName().toUpperCase())) {
                return CurrentNode.getNodeName();
            }
            else {
                return ParentNode.getNodeName() + "." + CurrentNode.getNodeName();
            }
        }
    }

    private void dropObject() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropObject");

        int mReturnNo = 0;
        switch (currentScreenNo) {
            case CONNECTION_SCREEN_NO:
                if ( mGlobalArea.blockedOption(1, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a Connection.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Connection " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        dropConnectionTreeItem();
                    }
                }
                break;
            case JOB_SCREEN_NO:
                // Job.
                if ( mGlobalArea.blockedOption(2, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a job.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Job " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropJob(CurrentNode.getOwner() + "." + 
                                              CurrentNode.getNodeName()) == 0) {
                            dataArea.removeJob(CurrentNode.getNodeId());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case PROGRAM_SCREEN_NO:
                // Program.
                if ( mGlobalArea.blockedOption(3, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a program.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Program " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropProgram(CurrentNode.getOwner() + "." +
                                                  CurrentNode.getNodeName()) == 0) {
                            dataArea.removeProgram(CurrentNode.getNodeId());
                            dataArea.dropComboObj(PROGRAM_ID,
                                               CurrentNode.getOwner(),
                                               CurrentNode.getNodeName());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case SCHEDULE_SCREEN_NO:
                // Schedule.
                if ( mGlobalArea.blockedOption(4, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a schedule.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Schedule " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropSchedule(CurrentNode.getOwner() + "." +
                                                  CurrentNode.getNodeName()) == 0) {
                            dataArea.removeSchedule(CurrentNode.getNodeId());
                            dataArea.dropComboObj(SCHEDULE_ID,
                                               CurrentNode.getOwner(),
                                               CurrentNode.getNodeName());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case JOB_CLASS_SCREEN_NO:
                // Job Class.
                if ( mGlobalArea.blockedOption(5, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a job class.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Job Class " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropJobClass(CurrentNode.getOwner() + "." +
                                                  CurrentNode.getNodeName()) == 0) {
                            dataArea.removeJobClass(CurrentNode.getNodeId());
                            dataArea.dropComboObj(JOB_CLASS_ID,
                                               CurrentNode.getNodeName());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case WINDOW_SCREEN_NO:
                // Window.
                if ( mGlobalArea.blockedOption(6, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a window.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Window " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropWindow(CurrentNode.getOwner() + "." +
                                                CurrentNode.getNodeName()) == 0) {
                            dataArea.removeWindow(CurrentNode.getNodeId());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case WINDOW_GROUP_SCREEN_NO:
                // Window Group.
                if ( mGlobalArea.blockedOption(7, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a window group.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Window Group " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropWindowGroup(CurrentNode.getOwner() + "." +
                                                     CurrentNode.getNodeName()) == 0) {
                            dataArea.removeWindowGroup(CurrentNode.getNodeId());
                            dataArea.dropComboObj(WINDOW_GROUP_ID,
                                               CurrentNode.getNodeName());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case GROUP_SCREEN_NO:
                // Window Group.
                if ( mGlobalArea.blockedOption(7, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a group.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Group " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropGroup(CurrentNode.getOwner() + "." +
                                                     CurrentNode.getNodeName()) == 0) {
                            dataArea.removeGroup(CurrentNode.getNodeId());
                            if (CurrentNode.getIconType() == WINDOW_GROUP_SCREEN_NO)
                                dataArea.dropComboObj(WINDOW_GROUP_ID,
                                               CurrentNode.getNodeName());
                            if (CurrentNode.getIconType() == DB_GROUP_ICON_NO)
                                dataArea.dropComboObj(DB_DEST_ID,
                                               CurrentNode.getNodeName());
                            if (CurrentNode.getIconType() == EXT_GROUP_ICON_NO)
                                dataArea.dropComboObj(EXT_DEST_ID,
                                               CurrentNode.getNodeName());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case JOB_ARGS_SCREEN_NO:
                // Job Argument.
                if ( mGlobalArea.blockedOption(8, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a Job Argument.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Job Argument " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        for (int i3 = 0; i3 < dataArea.jobArgsSize(); i3++) {
                            gJobArgsItem = dataArea.getJobArgs(i3);

                            if (gJobArgsItem.getId() == CurrentNode.getNodeId() ) {
                                if (dataArea.DropJobArg(
                                        CurrentNode.getOwner() + "." + gJobArgsItem.getJobName(),
                                        gJobArgsItem.getArgumentPosition()) == 0)
                                {
                                    dataArea.removeJobArg(CurrentNode.getNodeId());
                                    gTreeModel.removeNodeFromParent(CurrentNode);
                                }
                                else {
                                    errorBox("Error - " + dataArea.getSysMessage());
                                }
                                i3 = dataArea.jobArgsSize();
                            }
                        }
                    }
                }
                break;
            case PROGRAM_ARGS_SCREEN_NO:
                // Program Argument.
                if ( mGlobalArea.blockedOption(9, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a program argument.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Program Argument " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        for (int i3 = 0; i3 < dataArea.programArgsSize(); i3++) {
                            gProgramArgsItem = dataArea.getProgramArgs(i3);
                            if (gProgramArgsItem.getId() == CurrentNode.getNodeId() ) {
                                if (dataArea.DropProgramArgument(
                                        CurrentNode.getOwner() + "." + gProgramArgsItem.getProgramName(),
                                        gProgramArgsItem.getArgumentPosition()) == 0)
                                {
                                    dataArea.removeProgramArg(CurrentNode.getNodeId());
                                    gTreeModel.removeNodeFromParent(CurrentNode);
                                }
                                else {
                                    errorBox("Error - " + dataArea.getSysMessage());
                                }
                                i3 = dataArea.programArgsSize();
                            }
                        }
                    }
                }
                break;
            case JOBS_RUNNING_SCREEN_NO:
                errorBox("Error - Cannot drop running job." +
                         "\nUse the Stop Run command.");
                break;
            case CHAINS_SCREEN_NO:
                // Chain.
                if ( mGlobalArea.blockedOption(11, 3) == true ) {
                        errorBox("Error - You do not have permission to drop a Chain.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Chain " +
                              CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropChain(CurrentNode.getOwner() + 
                            "." + CurrentNode.getNodeName()) == 0) {
                            gChainsItem = dataArea.getChainsId(
                                              CurrentNode.getNodeId());
                            dataArea.removeChain(gChainsItem.getId());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case CHAIN_STEPS_SCREEN_NO:
                // Chain Step.
                if ( mGlobalArea.blockedOption(12, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a chain step.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Chain Step " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        for (int i3 = 0; i3 < dataArea.ChainStepsSize(); i3++) {
                            gChainStepsItem = dataArea.getChainSteps(i3);
                            if (gChainStepsItem.getId() == CurrentNode.getNodeId() ) {
                                if (dataArea.DropChainStep(
                                            CurrentNode.getOwner() + "." +
                                                            gChainStepsItem.getChainName(),
                                            gChainStepsItem.getStepName()) == 0)
                                {
                                    gChainStepsItem = dataArea.getChainStepsId(
                                              CurrentNode.getNodeId());
                                    dataArea.removeChainStep(gChainStepsItem.getId());
                                    gTreeModel.removeNodeFromParent(CurrentNode);
                                }
                                else {
                                    errorBox("Error - " + dataArea.getSysMessage());
                                }
                                i3 = dataArea.ChainStepsSize();
                            }
                        }
                    }
                }
                break;
            case CHAIN_RULES_SCREEN_NO:
                // Chain Rule.
                if ( mGlobalArea.blockedOption(13, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a chain rule.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Chain Rule " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        for (int i3 = 0; i3 < dataArea.ChainRulesSize(); i3++) {
                            gChainRulesItem = dataArea.getChainRules(i3);
                            if (gChainRulesItem.getId() == CurrentNode.getNodeId() ) {
                                if (dataArea.DropChainRule(
                                                CurrentNode.getOwner() + "." + 
                                                              gChainRulesItem.getChainName(),
                                                gChainRulesItem.getRuleName()) == 0)
                                {
                                    gChainRulesItem = dataArea.getChainRulesId(
                                              CurrentNode.getNodeId());
                                    dataArea.removeChainRule(gChainRulesItem.getId());
                                    gTreeModel.removeNodeFromParent(CurrentNode);
                                }
                                else {
                                    errorBox("Error - " + dataArea.getSysMessage());
                                }
                                i3 = dataArea.ChainRulesSize();
                            }
                        }
                    }
                }
                break;
            case JOB_RUN_DETAILS_SCREEN_NO:
                errorBox("Error - Cannot drop job log entries." +
                         "\nUse the Purge command.");
                break;
            case GLOBAL_ATTRIBUTES_SCREEN_NO:
                errorBox("Error - Cannot drop global attributes.");
                break;
            case CHAINS_RUNNING_SCREEN_NO:
                errorBox("Error - Cannot drop running chain." +
                         "\nUse the Stop Run command.");
                break;
            case CREDENTIALS_SCREEN_NO:
                // Credential.
                if ( mGlobalArea.blockedOption(CREDENTIALS_SCREEN_NO, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a Credential.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Credential " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        if (dataArea.DropCredential(CurrentNode.getOwner() + 
                            "." + CurrentNode.getNodeName()) == 0) {

                            dataArea.removeCredential(CurrentNode.getNodeId());
                            dataArea.dropComboObj(CREDENTIAL_ID,
                                               CurrentNode.getNodeName());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case FILE_WATCHERS_SCREEN_NO:
                // File Watcher
                if ( mGlobalArea.blockedOption(FILE_WATCHERS_SCREEN_NO, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a File Watcher.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want File Watcher " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        gFileWatchersItem = dataArea.getFileWatchersId(CurrentNode.getNodeId());
                        if (dataArea.DropFileWatcher(gFileWatchersItem) == 0) {

                            dataArea.removeFileWatcher(CurrentNode.getNodeId());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case NOTIFICATIONS_SCREEN_NO:
                // Notifications
                if ( mGlobalArea.blockedOption(NOTIFICATIONS_SCREEN_NO, 3) == true ) {
                    errorBox("Error - You do not have permission to drop a Notification.");
                }
                else {
                    mReturnNo = warningBox("Confirm that you want Notification " +
                          CurrentNode.getNodeName() + " dropped.");
                    if (mReturnNo == 0) {
                        gNotificationsItem = dataArea.getNotificationsId(CurrentNode.getNodeId());
                        if (dataArea.DropNotification(gNotificationsItem) == 0) {
                            SchedDataNode mNode = CurrentNode;

                            dataArea.removeNotification(mNode.getNodeId());
                            gTreeModel.removeNodeFromParent(CurrentNode);
                        }
                        else {
                            errorBox("Error - " + dataArea.getSysMessage());
                        }
                    }
                }
                break;
            case CONSUMER_GROUP_SCREEN_NO:
                // Drop the current Consumer Group.
                if (currentTree.equals(PENDING_AREA_TREE)) {
                    if (mPane.getTabbedPane().getSelectedIndex() == 1)  {
                        if (mPane.getSelectedGroupMapping() == -1) {
                            errorBox("Error - No Group Mapping Selected");
                        }
                        else {
                            dropGroupMapping();
                        }
                    }
                    else {
                        if (mPane.getTabbedPane().getSelectedIndex() == 0)  {
                            dropConsumerGroup();
                        }
                        else {
                            errorBox("Error - Cannot Delete Group Privilege From Pending Area");
                        }
                    }
                }
                else {
                    if (currentTree.equals(RESOURCE_ROOT_TREE)) {
                        if (mPane.getTabbedPane().getSelectedIndex() == 2) {
                            if (mPane.getSelectedGroupPriv() == -1) {
                                errorBox("Error - No Group Privilege Selected");
                            }
                            else {
                                dropConsumerPrivilege();
                            }
                        }
                        else {
                            errorBox("Error - Only Group Privilege Can Be Deleted From Resource Manager Tree.");
                        }
                    }
                }
                break;
            case PLAN_SCREEN_NO:
                // Drop the current Resource Plan.
                if (mPane.getTabbedPane().getSelectedIndex() == 1)  {
                    if (mPane.getSelectedPlanDirective() == -1) {
                        errorBox("Error - No Plan Directives Selected");
                    }
                    else {
                        dropPlanDirective();
                    }
                }
                else {
                    if (dataArea.SpecificPlanDirectiveSize(CurrentNode.getNodeName()) == 0) {
                        dropResourcePlan();
                    }
                    else {
                        errorBox("Error - Cannot drop Resource Plan." +
                                 "\nPlan Directives still exist for this Plan.");
                    }
                }
                break;
            case CDB_PLAN_SCREEN_NO:
                // Drop the current Resource Plan.
                if (mPane.getTabbedPane().getSelectedIndex() == 1)  {
                    if (mPane.getSelectedCdbPlanDirective() == -1) {
                        errorBox("Error - No Plan Directives Selected");
                    }
                    else {
                        dropCdbPlanDirective();
                    }
                }
                else {
                    dropCdbResourcePlan();
                }
                break;
            default:
                errorBox("Error - Cannot drop Object." +
                         "\nNo Object currently selected.");
        }
    }

    private void dropGroupMapping() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropGroupMapping");

        // System.out.println( " Point 1." + mScreen.getSelectedGroupMappingAttribute() + "--" +
        //                                   mScreen.getSelectedGroupMappingValue());
        gGroupMappingsItem = dataArea.getGroupMappings(
                                     mPane.getSelectedGroupMappingAttribute(),
                                     mPane.getSelectedGroupMappingValue());

        if (gGroupMappingsItem.getStatus() == null ||
            gGroupMappingsItem.getStatus().compareTo(PENDING_STATUS) != 0) {
            errorBox("Error - A Consumer Group Mapping can only be dropped from the Pending Area.");
        }
        else {
            int mReturnNo = warningBox("Confirm that you want Consumer Group Mapping " +
                      gGroupMappingsItem.getAttribute() + "/" + gGroupMappingsItem.getValue() +
                      " dropped.");
            if (mReturnNo == 0) {

                // Method that removes the group mapping.
                if (dataArea.DropConsumerGroupMapping(gGroupMappingsItem.getAttribute(),
                                                      gGroupMappingsItem.getValue())) {
                    dataArea.removeGroupMapping(gGroupMappingsItem.getAttribute(),
                                                gGroupMappingsItem.getValue());

                    mPane.removeGroupMapping(gGroupMappingsItem.getAttribute(),
                                               gGroupMappingsItem.getValue());
                }
                else {
                   errorBox("Error - " + dataArea.getSysMessage());
                }
            }
        }
    }

    private void dropConsumerGroup() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropConsumerGroup");

        if ( mGlobalArea.blockedOption(CONSUMER_GROUP_SCREEN_NO, 3) == true ) {
            errorBox("Error - You do not have permission to drop a Consumer Group.");
        }
        else {
            gConsumerGroupItem = dataArea.getConsumerGroupId(CurrentNode.getNodeId());

            // System.out.println( " Event - " + gConsumerGroupItem.getConsumerGroup());
            if (gConsumerGroupItem.getStatus() == null ||
                gConsumerGroupItem.getStatus().compareTo(PENDING_STATUS) != 0) {
                errorBox("Error - A Consumer Group can only be dropped from the Pending Area.");
            }
            else {
                int mReturnNo = warningBox("Confirm that you want Consumer Group " +
                      CurrentNode.getNodeName() + " dropped.");
                if (mReturnNo == 0) {
                    if (dataArea.DropConsumerGroup(CurrentNode.getNodeName()) == 0) {

                        dataArea.removeConsumerGroup(CurrentNode.getNodeId());

                        SchedDataNode tempNode = (SchedDataNode)CurrentNode.getParent();
                        gTree2.setSelectionPath( new TreePath(tempNode.getPath()) );

                        SchedResourceTree.dropConsumerGroupItem(gConsumerGroupItem,
                                                                gTreeModel2,
                                                                groot2);
                    }
                    else {
                        errorBox("Error - " + dataArea.getSysMessage());
                    }
                }
            }
        }
    }

    private void dropPlanDirective() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropPlanDirective");

        if ( mGlobalArea.blockedOption(PLAN_SCREEN_NO, 3) == true ) {
            errorBox("Error - You do not have permission to drop a Plan Directive.");
        }
        else {

            gPlanDirectiveItem = dataArea.getPlanDirectiveId(mPane.getSelectedPlanDirectiveId());
            int mReturnNo = warningBox("Confirm that you want Plan Directive for Plan " +
                  gPlanDirectiveItem.getPlan() + " and Group/Sub-Plan " + gPlanDirectiveItem.getGroup() +
                  " dropped.");
            if (mReturnNo == 0) {
                if (dataArea.DropResourceDirective(gPlanDirectiveItem.getPlan(),
                                                    gPlanDirectiveItem.getGroup()) == 0) {
                    dataArea.removePlanDirective(gPlanDirectiveItem.getPlan(),
                                                 gPlanDirectiveItem.getGroup());
                    mPane.removePlanDirective(gPlanDirectiveItem.getGroup());
                }
            }
        }
    }

    private void dropCdbPlanDirective() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropCdbPlanDirective");

        if ( mGlobalArea.blockedOption(CDB_PLAN_SCREEN_NO, 3) == true ) {
            errorBox("Error - You do not have permission to drop a CDB Plan Directive.");
        }
        else {

            gCdbPlanDirectiveItem = dataArea.getCdbPlanDirectiveId(mPane.getSelectedCdbPlanDirectiveId());
            int mReturnNo = warningBox("Confirm that you want Plan Directive for Plan " +
                  gCdbPlanDirectiveItem.getPlan() + " and Database " + 
                  gCdbPlanDirectiveItem.getPluggableDatabase() + " dropped.");
            if (mReturnNo == 0) {
                if (dataArea.DropCdbResourceDirective(gCdbPlanDirectiveItem.getPlan(),
                                                    gCdbPlanDirectiveItem.getPluggableDatabase()) == 0) {
                    dataArea.removeCdbPlanDirective(gCdbPlanDirectiveItem.getPlan(),
                                                 gCdbPlanDirectiveItem.getPluggableDatabase());
                    mPane.removeCdbPlanDirective(gCdbPlanDirectiveItem.getPluggableDatabase());
                }
            }
        }
    }

    private void dropResourcePlan() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropResourcePlan");

        if ( mGlobalArea.blockedOption(PLAN_SCREEN_NO, 3) == true ) {
            errorBox("Error - You do not have permission to drop a Resource Plan.");
        }
        else {

            gPlanItem = dataArea.getPlanId(CurrentNode.getNodeId());

            // System.out.println( "--1--");

            if (gPlanItem.getStatus() == null || gPlanItem.getStatus().compareTo(PENDING_STATUS) != 0) {
                errorBox("Error - A Resource Plan can only be dropped from the Pending Area.");
            }
            else {
                int mReturnNo = warningBox("Confirm that you want Resource Plan " +
                      CurrentNode.getNodeName() + " dropped.");
                if (mReturnNo == 0) {
                    if (dataArea.DropResourcePlan(CurrentNode.getNodeName()) == 0) {

                        dataArea.removePlan(CurrentNode.getNodeId());

                        SchedDataNode tempNode = (SchedDataNode)CurrentNode.getParent();
                        gTree2.setSelectionPath( new TreePath(tempNode.getPath()) );

                        SchedResourceTree.dropPlanItem(gPlanItem,
                                                       gTreeModel2,
                                                       groot2);
                    }
                    else {
                        errorBox("Error - " + dataArea.getSysMessage());
                    }
                }
            }
        }
    }

    private void dropCdbResourcePlan() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropCdbResourcePlan");

        if ( mGlobalArea.blockedOption(CDB_PLAN_SCREEN_NO, 3) == true ) {
            errorBox("Error - You do not have permission to drop a CDB Resource Plan.");
        }
        else {

            gCdbPlanItem = dataArea.getCdbPlanId(CurrentNode.getNodeId());

            // System.out.println( "--1--");

            if (gCdbPlanItem.getStatus() == null || gCdbPlanItem.getStatus().compareTo(PENDING_STATUS) != 0) {
                errorBox("Error - A Resource Plan can only be dropped from the Pending Area.");
            }
            else {
                int mReturnNo = warningBox("Confirm that you want Resource Plan " +
                      CurrentNode.getNodeName() + " dropped.");
                if (mReturnNo == 0) {
                    if (dataArea.DropCdbResourcePlan(CurrentNode.getNodeName()) == 0) {

                        dataArea.removeCdbPlan(CurrentNode.getNodeId());

                        SchedDataNode tempNode = (SchedDataNode)CurrentNode.getParent();
                        gTree2.setSelectionPath( new TreePath(tempNode.getPath()) );

                        SchedResourceTree.dropCdbPlanItem(gCdbPlanItem,
                                                          gTreeModel2,
                                                          groot2);
                    }
                    else {
                        errorBox("Error - " + dataArea.getSysMessage());
                    }
                }
            }
        }
    }

    private void dropConsumerPrivilege() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropConsumerPrivilege");

        gConsumerPrivItem = dataArea.getConsumerPriv(
                                    gConsumerGroupItem.getConsumerGroup(),
                                    mPane.getSelectedGroupPrivGrantee());

        int mReturnNo = warningBox("Confirm that you want Consumer Group Privilege to " +
                      gConsumerPrivItem.getGrantee() + 
                      " dropped.");
        if (mReturnNo == 0) {
            if (dataArea.DropConsumerPrivilege(
                                    gConsumerPrivItem.getGrantedGroup(),
                                    gConsumerPrivItem.getGrantee()) )
            {
                dataArea.removeConsumerPriv(
                                    gConsumerPrivItem.getGrantedGroup(),
                                    gConsumerPrivItem.getGrantee());
                mPane.removeGroupPriv(gConsumerPrivItem.getGrantee());
            }
            else {
               errorBox("Error - " + dataArea.getSysMessage());
            }
        }
    }

    private void updateObject() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.updateObject");

        Double xPoint = new Double(getLocation().getX());
        Double yPoint = new Double(getLocation().getY());

        mGlobalArea.setFramePosition(xPoint.intValue(), yPoint.intValue());

        // System.out.println( " Event  1 -" + currentScreenNo);

        if (((currentScreenNo == PLAN_SCREEN_NO) &&
             (mPane.getTabbedPane().getSelectedIndex() == 1)) ||
            ((currentScreenNo == CDB_PLAN_SCREEN_NO) &&
             (mPane.getTabbedPane().getSelectedIndex() == 1)) ||
            ((currentScreenNo == CONSUMER_GROUP_SCREEN_NO) &&
             (mPane.getTabbedPane().getSelectedIndex() == 1))) {

            if (currentScreenNo == PLAN_SCREEN_NO) {
                if (mPane.getSelectedPlanDirective() == -1) {
                    errorBox("Error - No Plan Directive Selected");
                }
                else {
                    SchedUpdate updateDialog = new SchedUpdate( this , 2, CurrentNode,
                                                                dataArea, mGlobalArea, mScreen, mPane);
                    updateDialog.setVisible( true );
                }
            }
            if (currentScreenNo == CDB_PLAN_SCREEN_NO) {
                if (mPane.getSelectedCdbPlanDirective() == -1) {
                    errorBox("Error - No CDB Plan Directive Selected");
                }
                else {
                    SchedUpdate updateDialog = new SchedUpdate( this , 2, CurrentNode,
                                                                dataArea, mGlobalArea, mScreen, mPane);
                    updateDialog.setVisible( true );
                }
            }
            if (currentScreenNo == CONSUMER_GROUP_SCREEN_NO) {
                errorBox("Error - Cannot Update Consumer Group Mappings. Use delete and then add instead.");
            }
        }
        else {
            try {
                switch (currentScreenNo) {
                    case CONNECTION_SCREEN_NO:
                        mConnectionItem = mGlobalArea.getConnectionObjById(CurrentNode.getNodeId());

                        SchedConnect inputDialog = new SchedConnect(
                                        this, mGlobalArea, 1);

                        inputDialog.setConnection(mConnectionItem);

                        inputDialog.setVisible( true );
                        break;
                    case JOBS_RUNNING_SCREEN_NO:
                        errorBox("Error - Cannot Update Running Jobs.");
                        break;
                    case JOB_RUN_DETAILS_SCREEN_NO:
                        errorBox("Error - Cannot Update Run Job Logs.");
                        break;
                    case GLOBAL_ATTRIBUTES_SCREEN_NO:
                        if ( (CurrentNode.getNodeName().equals("DEFAULT_TIMEZONE") ) ||
                             (CurrentNode.getNodeName().equals("MAX_JOB_SLAVE_PROCESSES") ) ||
                             (CurrentNode.getNodeName().equals("LOG_HISTORY") ) ||
                             (CurrentNode.getNodeName().equals("EVENT_EXPIRY_TIME") ) )
                        {
                            SchedUpdate updateDialog = new SchedUpdate( 
                                    this , 1, CurrentNode, dataArea, mGlobalArea, mScreen, mPane);
                            updateDialog.setVisible( true );
                        }
                        else {
                            errorBox("Error - Global Attribute that is not updatable.");
                        }
                        break;
                    case CHAINS_RUNNING_SCREEN_NO:
                        errorBox("Error - Cannot Update Running Chain.");
                        break;
                    default:
                        if ( mGlobalArea.blockedOption(currentScreenNo, 4) == true ) {
                            errorBox("Error - You do not have the permissions to update " +
                                          CurrentNode.getNodeName() + ".");
                        }
                        else {
                            SchedUpdate updateDialog = new SchedUpdate(
                                    this , 1, CurrentNode, dataArea, mGlobalArea, mScreen, mPane);
                            updateDialog.setVisible( true );
                        }
                }
            } catch (NullPointerException npe) {
                errorBox("Error - Cannot Update Object." +
                         "\nNo Object currently selected.");
            }
        }
    }

    public void updateConnection(String connectionName,
                                 String userName,
                                 String password,
                                 String host,
                                 String port,
                                 String database,
                                 boolean isSysdba,
                                 boolean isSavePassword,
                                 boolean isAutoConnect,
                                 String  selectStr1,
                                 String  selectStr2,
                                 String  selectStr3,
                                 String  selectStr4,
                                 String  selectStr5,
                                 String  selectStr6) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.updateConnection");

        mConnectionItem = mGlobalArea.getConnectionObjById(CurrentNode.getNodeId());
        if (mConnectionItem != null) {
            mConnectionItem.setName(connectionName);
            mConnectionItem.setAcName(userName);
            mConnectionItem.setPassword(password);
            mConnectionItem.setHost(host);
            mConnectionItem.setPort(port);
            mConnectionItem.setDatabase(database);
            mConnectionItem.setSysdba(isSysdba);
            mConnectionItem.setSavePassword(isSavePassword);
            mConnectionItem.setAutoConnect(isAutoConnect);
            mConnectionItem.setRunWhereStmt(selectStr1);
            mConnectionItem.setStdLogWhereStmt(selectStr2);
            mConnectionItem.setDetLogWhereStmt(selectStr3);
            mConnectionItem.setChainWhereStmt(selectStr4);
            mConnectionItem.setStdWinWhereStmt(selectStr5);
            mConnectionItem.setDetWinWhereStmt(selectStr6);

            CurrentNode.setNodeName(connectionName);
        }
    }

    private void disableObject() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.disableObject");

        if ((currentScreenNo == JOB_SCREEN_NO) ||
            (currentScreenNo == PROGRAM_SCREEN_NO) ||
            (currentScreenNo == WINDOW_SCREEN_NO) ||
            (currentScreenNo == WINDOW_GROUP_SCREEN_NO) ||
            (currentScreenNo == CHAINS_SCREEN_NO) ||
            (currentScreenNo == FILE_WATCHERS_SCREEN_NO) ||
            (currentScreenNo == GROUP_SCREEN_NO))
        {
            if (mGlobalArea.blockedOption(currentScreenNo, 6) == true ) {
                errorBox("Error - Cannot Disable " + CurrentNode.getNodeName() + "." +
                         "\nYou do not have the permissions to Disable this object.");
            }
            else {
                int mReturnNo = warningBox("Confirm that you want Object " +
                          CurrentNode.getNodeName() + " disabled.");
                if (mReturnNo == 0) {
                    int mReturnNo2 = 0;

                    if ((currentScreenNo == WINDOW_SCREEN_NO) ||
                        (currentScreenNo == WINDOW_GROUP_SCREEN_NO)) {
                        mReturnNo2 = dataArea.DisableObject( "SYS." + CurrentNode.getNodeName() );
                    }
                    else {
                        mReturnNo2 = dataArea.DisableObject( ParentNode.getNodeName() + "." +
                                                             CurrentNode.getNodeName() );
                    }
                    if (mReturnNo2 == 0) {
                        switch(currentScreenNo) {
                            case JOB_SCREEN_NO:
                                refreshTextItem("FALSE", 17);
                                dataArea.setJobEnabled(CurrentNode.getNodeId(), "FALSE");
                                break;
                            case PROGRAM_SCREEN_NO:
                                refreshTextItem("FALSE", 6);
                                dataArea.setProgramEnabled(CurrentNode.getNodeId(), "FALSE");
                                break;
                            case WINDOW_SCREEN_NO:
                                refreshTextItem("FALSE", 12);
                                dataArea.setWindowEnabled(CurrentNode.getNodeId(), "FALSE");
                                break;
                            case WINDOW_GROUP_SCREEN_NO:
                                refreshTextItem("FALSE", 2);
                                dataArea.setWindowGroupEnabled(CurrentNode.getNodeId(), "FALSE");
                                break;
                            case CHAINS_SCREEN_NO:
                                refreshTextItem("FALSE", 7);
                                dataArea.setChainEnabled(CurrentNode.getNodeId(), "FALSE");
                                break;
                            case FILE_WATCHERS_SCREEN_NO:
                                refreshTextItem("FALSE", 3);
                                dataArea.setFileWatcherEnabled(CurrentNode.getNodeId(), "FALSE");
                                break;
                            case GROUP_SCREEN_NO:
                                refreshTextItem("FALSE", 4);
                                dataArea.setGroupEnabled(CurrentNode.getNodeId(), "FALSE");
                                break;
                        }
                        setEnableButtonTrue();
                        setDisableButtonFalse();
                    }
                    else {
                        errorBox(dataArea.getSysMessage().toString());
                    }
                }
            }
        }
        else {
            errorBox("Error - Cannot disable " + CurrentNode.getNodeName() + "." +
                     "\nThis is not Enabled/Disabled.");
        }
    };

    private void enableObject() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.enableObject");

        if ((currentScreenNo == JOB_SCREEN_NO) ||
            (currentScreenNo == PROGRAM_SCREEN_NO) ||
            (currentScreenNo == WINDOW_SCREEN_NO) ||
            (currentScreenNo == WINDOW_GROUP_SCREEN_NO) ||
            (currentScreenNo == CHAINS_SCREEN_NO) ||
            (currentScreenNo == FILE_WATCHERS_SCREEN_NO) ||
            (currentScreenNo == GROUP_SCREEN_NO))
        {
            if (mGlobalArea.blockedOption(currentScreenNo, 6) == true ) {
                errorBox("Error - Cannot Enable " + CurrentNode.getNodeName() + "." +
                         "\nYou do not have the permissions to Enable this object.");
            }
            else {
                int mReturnNo = warningBox("Confirm that you want Object " +
                          CurrentNode.getNodeName() + " enabled.");
                if (mReturnNo == 0) {
                    int mReturnNo2 = 0;
                    if ((currentScreenNo == WINDOW_SCREEN_NO) ||
                        (currentScreenNo == WINDOW_GROUP_SCREEN_NO)) {
                        mReturnNo2 = dataArea.EnableObject( "SYS." + CurrentNode.getNodeName() );
                    }
                    else {
                        mReturnNo2 = dataArea.EnableObject( ParentNode.getNodeName() + "." +
                                                            CurrentNode.getNodeName() );
                    }
                    if (mReturnNo2 == 0) {
                        switch(currentScreenNo) {
                            case JOB_SCREEN_NO:
                                refreshTextItem("TRUE", 17);
                                dataArea.setJobEnabled(CurrentNode.getNodeId(), "TRUE");
                                break;
                            case PROGRAM_SCREEN_NO:
                                refreshTextItem("TRUE", 6);
                                dataArea.setProgramEnabled(CurrentNode.getNodeId(), "TRUE");
                                break;
                            case WINDOW_SCREEN_NO:
                                refreshTextItem("TRUE", 12);
                                dataArea.setWindowEnabled(CurrentNode.getNodeId(), "TRUE");
                                break;
                            case WINDOW_GROUP_SCREEN_NO:
                                refreshTextItem("TRUE", 2);
                                dataArea.setWindowGroupEnabled(CurrentNode.getNodeId(), "TRUE");
                                break;
                            case CHAINS_SCREEN_NO:
                                refreshTextItem("TRUE", 7);
                                dataArea.setChainEnabled(CurrentNode.getNodeId(), "TRUE");
                                break;
                            case FILE_WATCHERS_SCREEN_NO:
                                refreshTextItem("TRUE", 3);
                                dataArea.setFileWatcherEnabled(CurrentNode.getNodeId(), "TRUE");
                                break;
                            case GROUP_SCREEN_NO:
                                refreshTextItem("TRUE", 4);
                                dataArea.setGroupEnabled(CurrentNode.getNodeId(), "TRUE");
                                break;
                        }
                        setEnableButtonFalse();
                        setDisableButtonTrue();
                    }
                    else {
                        errorBox(dataArea.getSysMessage().toString());
                    }
                }
            }
        }
        else {
            errorBox("Error - Cannot enable " + CurrentNode.getNodeName() + "." +
                     "\nThis is not Enabled/Disabled.");
        }
    };

    private void stopJob() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.stopJob");

        if (mGlobalArea.blockedOption(2, 8) == true ) {
                errorBox("Error - Cannot Stop Job." +
                         "\nYou do not have the permissions to Stop a Job.");
        }
        else {
            if (currentScreenNo == 10) {
                int mReturnNo = warningBox("Confirm that you want Job " +
                              CurrentNode.getNodeName() + " stopped.");
                if (mReturnNo == 0) {
                    if ( dataArea.StopJob( CurrentNode.getOwner() + "." +
                                           CurrentNode.getNodeName() ) == 0 ) {

                        mGlobalArea.clearJobsRunningVector();
                        mGlobalArea.GetJobsRunningData();
                        refreshTree();
                    }
                    else {
                        errorBox("Error - " + dataArea.getSysMessage());
                    }
                }
            }
            else {
                errorBox("Error - Cannot Stop Job." +
                         "\nNo Running Job currently selected.");
            }
        }
    }

    private void runJob() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.runJob");
        if (mGlobalArea.blockedOption(2, 7) == true ) {
                errorBox("Error - Cannot Run Job." +
                         "\nYou do not have the permissions to Run a Job.");
        }
        else {
            if (currentScreenNo == JOB_SCREEN_NO) {
                int mReturnNo = warningBox("Confirm that you want to run Job " +
                              CurrentNode.getNodeName() + ".");
                if (mReturnNo == 0) {

                    runJob = new threadRunJob(this, dataArea,
                                              CurrentNode.getOwner() + 
                                              "." + CurrentNode.getNodeName(),
                                              SchedFile.getFileOption(11) );
                    Thread jobThread = new Thread(runJob);

                    jobThread.start();

                }
            }
            else {
                errorBox("Error - Cannot Run Job." +
                     "\nNo Job currently selected.");
            }
        }
    }

    private void PurgeLog() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.PurgeLog");
        if (mGlobalArea.blockedOption(20, 10) == true ) {
                errorBox("Error - Cannot Purge Logs." +
                         "\nYou do not have the permissions to Purge the Logs.");
        }
        else {
            SchedInputScreen inputDialog = new SchedInputScreen(
                    this, dataArea, mGlobalArea, mScreenInp, 520, 0 );
            inputDialog.setVisible( true );

        }
    }

    private void AssignGroup() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.AssignGroup");
        boolean mContinue = true;

        if (currentScreenNo == WINDOW_SCREEN_NO) {
            if (mGlobalArea.blockedOption(WINDOW_GROUP_SCREEN_NO, 9) == true ) {
                errorBox("Error - Cannot Assign Window." +
                         "\nYou do not have the permissions to Assign a Window.");
                mContinue = false;
            }
            else {
                gWindowName = CurrentNode.getNodeName();
                SchedInputScreen inputDialog = new SchedInputScreen(
                        this, dataArea, mGlobalArea, mScreenInp, 521, 0 );
                inputDialog.setVisible( true );
            }
        }
        if (currentScreenNo == DB_DESTINATION_SCREEN_NO) {
            if (mGlobalArea.blockedOption(GROUP_SCREEN_NO, 9) == true ) {
                errorBox("Error - Cannot Assign Database Destination." +
                         "\nYou do not have the permissions to Assign a Destination.");
                mContinue = false;
            }
            else {
                SchedInputScreen inputDialog = new SchedInputScreen(
                    this, dataArea, mGlobalArea, mScreenInp, 529, 0 );
                inputDialog.setVisible( true );
            }
        }
        if (currentScreenNo == EXT_DESTINATION_SCREEN_NO) {
            if (mGlobalArea.blockedOption(GROUP_SCREEN_NO, 9) == true ) {
                errorBox("Error - Cannot Assign External Destination." +
                         "\nYou do not have the permissions to Assign a Destination.");
                mContinue = false;
            }
            else {
                SchedInputScreen inputDialog = new SchedInputScreen(
                    this, dataArea, mGlobalArea, mScreenInp, 530, 0 );
                inputDialog.setVisible( true );
            }
        }
    }

    private void RemoveGroup() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.RemoveGroup");

        int mReturnNo = warningBox("Confirm that you want Group Object " +
                          CurrentNode.getNodeName() + ", dropped from Group.");

        if (mReturnNo == 0) {

            String lobjectName = CurrentNode.getNodeName();
            String lgroupName = ParentNode.getNodeName();
            int returnValue = 0;

            if ((currentScreenNo == WINDOW_SCREEN_NO) &&
                (ParentNode.getScreenNo() == WINDOW_GROUP_SCREEN_NO)) {
                if (dataArea.getVersionNo() > 3) {
                    returnValue = dataArea.RemoveFromGroup(
                                        lobjectName, lgroupName);
                }
                else {
                    returnValue = dataArea.RemoveWindowGroupMember(
                                        lobjectName, lgroupName);
                }
            }
            if ((currentScreenNo == DB_DESTINATION_SCREEN_NO) &&
                (ParentNode.getIconType() == DB_GROUP_ICON_NO)) {
                returnValue = dataArea.RemoveFromGroup(
                                    lobjectName, lgroupName);
            }
            if ((currentScreenNo == EXT_DESTINATION_SCREEN_NO) &&
                (ParentNode.getIconType() == EXT_GROUP_ICON_NO)) {
                returnValue = dataArea.RemoveFromGroup(
                                    lobjectName, lgroupName);
            }
            if (returnValue == 0) {
                dataArea.removeGroupMember(CurrentNode.getNodeId());
                gTreeModel.removeNodeFromParent(CurrentNode);
            }
            else {
                errorBox("Error - " + dataArea.getSysMessage());
            }
        }
    }

    private void OpenWindow() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.OpenWindow");

        if (mGlobalArea.blockedOption(6, 7) == true ) {
                errorBox("Error - Cannot Open Window." +
                         "\nYou do not have the permissions to Open a Window.");
        }
        else {
            if (currentScreenNo == WINDOW_SCREEN_NO) {
                int mReturnNo = warningBox("Confirm that you want to Open Window " +
                              CurrentNode.getNodeName() + ".");
                if (mReturnNo == 0) {
                    if ( dataArea.OpenWindow( CurrentNode.getNodeName() ) == 0 ) {
                        refreshTextItem("TRUE", 14);
                    }
                    else {
                        errorBox("Error - " + dataArea.getSysMessage());
                    }
                }
            }
            else {
                errorBox("Error - Cannot Open Window." +
                         "\nNo Window currently selected.");
            }
        }
    }

    private void CloseWindow() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.CloseWindow");

        if (mGlobalArea.blockedOption(6, 8) == true ) {
                errorBox("Error - Cannot Close Window." +
                         "\nYou do not have the permissions to Close a Window.");
        }
        else {
            if (currentScreenNo == WINDOW_SCREEN_NO) {
                int mReturnNo = warningBox("Confirm that you want to Close Window " +
                              CurrentNode.getNodeName() + ".");
                if (mReturnNo == 0) {
                    if ( dataArea.CloseWindow( CurrentNode.getNodeName() ) == 0 ) {
                        refreshTextItem("FALSE", 14);
                    }
                    else {
                        errorBox("Error - " + dataArea.getSysMessage());
                    }
                }
            }
            else {
                errorBox("Error - Cannot Close Window." +
                     "\nNo Window currently selected.");
            }
        }
    }

    private void createPendingArea() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createPendingArea");

        int mReturnNo = warningBox("Confirm that you want to Create a Pending Area.");
        if (mReturnNo == 0) {
            if (dataArea.CreatePendingArea() == 0) {

                SchedFile.saveDivLocation(7, splitPane2.getDividerLocation());
                SchedFile.saveDivLocation(6, splitPane6.getDividerLocation());

                runResourceData.stopJob();

                SchedResourceTree.createPendingArea(dataArea,groot2);

                dataArea.clearResourceVectors();

                fetchResourceData();
                refreshDataScreen(currentScreenNo);

                // bottomPane.getViewport().add( label );

                splitPane2.setDividerLocation(SchedFile.getDivLocation(7));
                splitPane6.setDividerLocation(SchedFile.getDivLocation(6));

                runResourceThread();

                enableResourceMenu();
            }
            else {
                errorBox(dataArea.getSysMessage().toString());
            }
        }
    }

    private void clearPendingArea() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.clearPendingArea");

        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.clearPendingArea");
        int mReturnNo = warningBox("Confirm that you want to Clear the Pending Area.");
        if (mReturnNo == 0) {
            if (dataArea.ClearPendingArea() == 0) {

                SchedFile.saveDivLocation(7, splitPane2.getDividerLocation());
                SchedFile.saveDivLocation(6, splitPane6.getDividerLocation());

                runResourceData.stopJob();

                SchedResourceTree.clearPendingArea(groot2);

                dataArea.clearResourceVectors();

                fetchResourceData();
                refreshDataScreen(currentScreenNo);

                // bottomPane.getViewport().add( label );

                splitPane2.setDividerLocation(SchedFile.getDivLocation(7));
                splitPane6.setDividerLocation(SchedFile.getDivLocation(6));

                runResourceThread();

                disableResourceMenu();

                setupButtons();
                gTree1.setSelectionRow(0);

            }
            else {
                errorBox(dataArea.getSysMessage().toString());
            }
        }
    }

    private void validatePendingArea() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.validatePendingArea");

        int mReturnNo = warningBox("Confirm that you want to Validate the Pending Area.");
        if (mReturnNo == 0) {
            if (dataArea.ValidatePendingArea() != 0) {
                errorBox(dataArea.getSysMessage().toString());
            }
            else {
                errorBox("Validation Successful");
            }
        }
    }

    private void submitPendingArea() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.submitPendingArea");

        int mReturnNo = warningBox("Confirm that you want to Submit the Pending Area.");
        if (mReturnNo == 0) {
            if (dataArea.SubmitPendingArea() == 0) {

                SchedFile.saveDivLocation(7, splitPane2.getDividerLocation());
                SchedFile.saveDivLocation(6, splitPane6.getDividerLocation());

                runResourceData.stopJob();

                dataArea.clearResourceVectors();

                fetchResourceData();
                refreshDataScreen(currentScreenNo);

                splitPane2.setDividerLocation(SchedFile.getDivLocation(7));
                splitPane6.setDividerLocation(SchedFile.getDivLocation(6));

                runResourceThread();

                disableResourceMenu();

                setupButtons();
            }
            else {
                errorBox(dataArea.getSysMessage().toString());
            }
        }
    }

    private void raiseMappingPriority() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.raiseMappingPriority");

        // System.out.println( "Raise Priority");
        if (mPane.getMappingTableRow() < 0) {
            errorBox("Error - No Row Currently Selected");
        }
        else {
            if (mPane.getMappingTableRow() == 0) {
                errorBox("Error - Current Row Already has Highest Priority. Cannot Raise its Priority.");
            }
            else {
                int mPriority = mPane.getMappingTableRow() + 1;
                dataArea.raiseMappingPriority(mPriority);
                dataArea.SetConsumerGroupPriorities();
                if (dataArea.SetConsumerGroupPriorities() == 1) {
                    errorBox(dataArea.getSysMessage().toString());
                }
                dataArea.clearMappingPrioritiesVector();
                dataArea.GetMappingPriorityData();
                refreshMappingPriority();
            }
        }
    }

    private void lowerMappingPriority() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.lowerMappingPriority");

        // System.out.println( "Lower Priority");
        if (mPane.getMappingTableRow() < 0) {
                errorBox("Error - No Row Currently Selected");
        }
        else {
            if (mPane.getMappingTableRow() == 9) {
                errorBox("Error - Current Row Already has Lowest Priority. Cannot Lower its Priority.");
            }
            else {
                int mPriority = mPane.getMappingTableRow() + 1;
                dataArea.lowerMappingPriority(mPriority);
                if (dataArea.SetConsumerGroupPriorities() == 1) {
                    errorBox(dataArea.getSysMessage().toString());
                }
                dataArea.clearMappingPrioritiesVector();
                dataArea.GetMappingPriorityData();
                refreshMappingPriority();
            }
        }
    }

    public int warningBox(String mMessage) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.warningBox");

        int returnValue = JOptionPane.showConfirmDialog(null, mMessage,"Confirmation",
                                                        JOptionPane.YES_NO_OPTION,
                                                        JOptionPane.QUESTION_MESSAGE);
        return returnValue;
    }

    public void errorBox(String mMessage) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.errorBox");

        JOptionPane.showMessageDialog(null, mMessage);
    }

    public void createConnectionTreeItem(SchedGlobalData.connectionItem connectionItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createConnectionTreeItem");

        int connectionId = mGlobalArea.getNextConnectionSeq();
        connectionItem.setConnectionId(connectionId);
        int mPosition = groot.getChildCount();

        SchedDataNode mNode1 = new SchedDataNode(connectionItem.getName(),
                                       connectionId,
                                       connectionId,
                                       CONNECTION_SCREEN_NO,
                                       CONNECTION_SCREEN_NO);
        mNode1.setAllowsChildren(false);
        groot.add(mNode1);
        gTreeModel.insertNodeInto(mNode1, groot, mPosition);
    }

    public void dropConnectionTreeItem() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.dropConnectionTreeItem");

        if (mGlobalArea.dropConnection(CurrentNode.getNodeId())) {
            gTreeModel.removeNodeFromParent(CurrentNode);
        }
        else {
            errorBox("Error - No Connection deleted.");
        }
    }

    private int findChildElementNo(SchedDataNode mParentNode,
                                   SchedDataNode mChildNode) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.findChildElementNo");

        Enumeration e1 = mParentNode.children();

        int mElementNo = 0;

        boolean mContinue = true;
        while (  mContinue ) {
            if ( e1.hasMoreElements() ) {
                SchedDataNode tempNode = (SchedDataNode)e1.nextElement();
                if ( tempNode.getNodeId() == mChildNode.getNodeId() ) {
                    break;
                }
                mElementNo +=  1;
            }
            else {
                mContinue = false;
                mElementNo = -2;
            }
        }
        return mElementNo;
    }

    private SchedDataNode findConnectNode(SchedDataNode mNode, int mId) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.findConnectNode");

        SchedDataNode tempNode = null;
        Enumeration e1 = mNode.children();

        boolean mContinue = true;
        while (  mContinue ) {
            if ( e1.hasMoreElements() ) {
                tempNode = (SchedDataNode)e1.nextElement();
                if ( tempNode.getNodeId() == mId) return tempNode;
            }
            else mContinue = false;
        }
        return null;
    }

    public void createJobTreeItem(SchedDataArea.JobItem mJobItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createJobTreeItem");

        SchedTree.createJobTreeItem(mJobItem,
                                    gTreeModel,
                                    groot,
                                    dataArea,
                                    mScreen);
    }

    public void createJobArgsTreeItem(SchedDataArea.JobArgsItem mJobArgsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createJobArgsTreeItem");

        SchedTree.createJobArgsTreeItem(mJobArgsItem,
                                        gTreeModel,
                                        groot,
                                        dataArea,
                                        mScreen);
    }

    public void createNotificationTreeItem(SchedDataArea.NotificationsItem mNotificationsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createNotificationTreeItem");

        SchedTree.createNotificationTreeItem(mNotificationsItem,
                                             gTreeModel,
                                             groot,
                                             dataArea,
                                             mScreen);
    }

    public void createProgramTreeItem(SchedDataArea.ProgramItem mProgramItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createProgramTreeItem");

        SchedTree.createProgramTreeItem(mProgramItem,
                                        gTreeModel,
                                        groot,
                                        dataArea,
                                        mScreen);
    }

    public void createProgramArgsTreeItem(SchedDataArea.ProgramArgsItem mProgramArgsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createProgramArgsTreeItem");

        SchedTree.createProgramArgsTreeItem(mProgramArgsItem,
                                            gTreeModel,
                                            groot,
                                            dataArea,
                                            mScreen);
    }

    public void createScheduleTreeItem(SchedDataArea.ScheduleItem mScheduleItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createScheduleTreeItem");

        SchedTree.createScheduleTreeItem(mScheduleItem,
                                         gTreeModel,
                                         groot,
                                         dataArea,
                                         mScreen);
    }

    public void createChainTreeItem(SchedDataArea.ChainsItem mChainsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createChainTreeItem");

        SchedTree.createChainTreeItem(mChainsItem,
                                          gTreeModel,
                                          groot,
                                          dataArea,
                                          mScreen);
    }

    public void createChainRuleTreeItem(SchedDataArea.ChainRulesItem mChainRulesItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createChainRuleTreeItem");

        SchedTree.createChainRuleTreeItem(mChainRulesItem,
                                          gTreeModel,
                                          groot,
                                          dataArea,
                                          mScreen);
    }

    public void createChainStepTreeItem(SchedDataArea.ChainStepsItem mChainStepsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createChainStepTreeItem");

        SchedTree.createChainStepTreeItem(mChainStepsItem,
                                          gTreeModel,
                                          groot,
                                          dataArea,
                                          mScreen);
    }

    public void createFileWatcherTreeItem(SchedDataArea.FileWatchersItem  mFileWatchersItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createFileWatcherTreeItem");

        SchedTree.createFileWatcherTreeItem(mFileWatchersItem,
                                            gTreeModel,
                                            groot,
                                            dataArea,
                                            mScreen);
    }

    public void createJobClassTreeItem(SchedDataArea.JobClassItem mJobClassItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createJobClassTreeItem");

        SchedTree.createJobClassTreeItem(mJobClassItem,
                                         gTreeModel,
                                         groot,
                                         dataArea,
                                         mScreen);
    }

    public void createWindowTreeItem(SchedDataArea.WindowItem mWindowItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createWindowTreeItem");

        SchedTree.createWindowTreeItem(mWindowItem,
                                       gTreeModel,
                                       groot,
                                       dataArea,
                                       mScreen);
    }

    public void createWindowGroupTreeItem(SchedDataArea.WindowGroupItem mWindowGroupItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createWindowGroupTreeItem");

        SchedTree.createWindowGroupTreeItem(mWindowGroupItem,
                                            gTreeModel,
                                            groot,
                                            dataArea,
                                            mScreen);
    }

    public void createGroupWindowTreeItem(SchedDataArea.GroupItem mGroupItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createGroupWindowTreeItem");

        SchedTree.createGroupWindowTreeItem(mGroupItem,
                                            gTreeModel,
                                            groot,
                                            dataArea,
                                            mScreen);
    }
    public void createGroupDbDestTreeItem(SchedDataArea.GroupItem  mGroupItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createGroupDbDestTreeItem");

        SchedTree.createGroupDbDestTreeItem(mGroupItem,
                                            gTreeModel,
                                            groot,
                                            dataArea,
                                            mScreen);
    }

    public void createGroupExtDestTreeItem(SchedDataArea.GroupItem  mGroupItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createGroupExtDestTreeItem");

        SchedTree.createGroupExtDestTreeItem(mGroupItem,
                                             gTreeModel,
                                             groot,
                                             dataArea,
                                             mScreen);
    }

    public void createWinGroupAssignTreeItem(SchedDataArea.WinGroupMembersItem mWinGroupMembersItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createWinGroupAssignTreeItem");

        SchedTree.createWinGroupAssignTreeItem(mWinGroupMembersItem,
                                               gTreeModel,
                                               groot,
                                               dataArea,
                                               mScreen);
    }

    public void createDbDestGroupAssignTreeItem(SchedDataArea.GroupMembersItem mGroupMembersItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createDbDestGroupAssignTreeItem");

        SchedTree.createDbDestGroupAssignTreeItem(mGroupMembersItem,
                                                  gTreeModel,
                                                  groot,
                                                  dataArea,
                                                  mScreen);
    }

    public void createExtDestGroupAssignTreeItem(SchedDataArea.GroupMembersItem mGroupMembersItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createExtDestGroupAssignTreeItem");

        SchedTree.createExtDestGroupAssignTreeItem(mGroupMembersItem,
                                                  gTreeModel,
                                                  groot,
                                                  dataArea,
                                                  mScreen);
    }

    public void removeWinGroupAssignTreeItem(String  mWindowGroupName,
                                             String  mWindowName) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.removeWinGroupAssignTreeItem");

        SchedTree.removeWinGroupAssignTreeItem(mWindowGroupName,
                                               mWindowName,
                                               gTreeModel,
                                               groot);
    }

    public void removeDbDestGroupAssignTreeItem(String  mOwner,
                                                String  mGroupName,
                                                String  mDbDestination) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.removeDbDestGroupAssignTreeItem");

        SchedTree.removeDbDestGroupAssignTreeItem(mOwner,
                                                  mGroupName,
                                                  mDbDestination,
                                                  gTreeModel,
                                                  groot);
    }

    public void removeExtDestGroupAssignTreeItem(String  mOwner,
                                                String  mGroupName,
                                                String  mDbDestination) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.removeExtDestGroupAssignTreeItem");

        SchedTree.removeExtDestGroupAssignTreeItem(mOwner,
                                                   mGroupName,
                                                   mDbDestination,
                                                   gTreeModel,
                                                   groot);
    }

    public void createDBDestTreeItem(SchedDataArea.DbDestsItem mDbDestsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createDBDestTreeItem");

        SchedTree.createDBDestTreeItem(mDbDestsItem,
                                       gTreeModel,
                                       groot,
                                       dataArea,
                                       mScreen);
    }

    public void createCredentialTreeItem(SchedDataArea.CredentialsItem  mCredentialsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createCredentialTreeItem");

        SchedTree.createCredentialTreeItem(mCredentialsItem,
                                           gTreeModel,
                                           groot,
                                           dataArea,
                                           mScreen);
    }

    public void createResourcePlanTreeItem(SchedDataArea.PlanItem mPlanItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createResourcePlanTreeItem");

        SchedResourceTree.createResourcePlanTreeItem(
                                            mPlanItem,
                                            gTreeModel2,
                                            groot2,
                                            dataArea,
                                            mScreen);
    }

    public void createCdbResourcePlanTreeItem(SchedDataArea.CdbPlanItem mCdbPlanItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createCdbResourcePlanTreeItem");

        SchedResourceTree.createCdbResourcePlanTreeItem(
                                            mCdbPlanItem,
                                            gTreeModel2,
                                            groot2,
                                            dataArea,
                                            mScreen);
    }

    public void createConsumerGroupTreeItem(SchedDataArea.ConsumerGroupItem mConsumerGroupItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createConsumerGroupTreeItem");

        SchedResourceTree.createConsumerGroupTreeItem(
                                            mConsumerGroupItem,
                                            gTreeModel2,
                                            groot2,
                                            dataArea,
                                            mScreen);
    }

    public void addPlanDirectiveItem(SchedDataArea.PlanDirectiveItem   mPlanDirectiveItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.addPlanDirectiveItem");

        mPane.populatePlanDirectiveTable(mPlanDirectiveItem.getId(),
                                         mPlanDirectiveItem.getGroup(),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P1()),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P2()),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P3()),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P4()),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P5()),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P6()),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P7()),
                                         Integer.toString(mPlanDirectiveItem.getMgmt_P8()));
    }

    public void addCdbPlanDirectiveItem(SchedDataArea.CdbPlanDirectiveItem   mCdbPlanDirectiveItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.addCdbPlanDirectiveItem");

        mPane.populateCdbPlanDirectiveTable(mCdbPlanDirectiveItem.getId(),
                                         mCdbPlanDirectiveItem.getPluggableDatabase(),
                                         Integer.toString(mCdbPlanDirectiveItem.getShares()),
                                         Integer.toString(mCdbPlanDirectiveItem.getUtilizationLimit()),
                                         Integer.toString(mCdbPlanDirectiveItem.getParallelServerLimit()));
    }

    public void addGroupMappingItem(SchedDataArea.GroupMappingsItem mGroupMappingsItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.addGroupMappingItem");

        mPane.populateGroupMappingTable(mGroupMappingsItem.getAttribute(),
                                        mGroupMappingsItem.getValue(),
                                        mGroupMappingsItem.getStatus());
    }

    public void addConsumerPrivItem(SchedDataArea.ConsumerPrivItem mConsumerPrivItem) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.addConsumerPrivItem");

        mPane.populateConsumerPrivTable(mConsumerPrivItem.getGrantee(),
                                        mConsumerPrivItem.getGrantOption(),
                                        mConsumerPrivItem.getInitialGroup());
    }

    public void createSchedulerTree() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createSchedulerTree");

        // *** Create the tree with the connection nodes. ***

        currentTree = SCHEDULER_ROOT_TREE;
        int connectionId = 1;
        groot = new SchedDataNode(SCHEDULER_ROOT_TREE, 0);

        for (int i2 = 0; i2 < mGlobalArea.sizeConnectionObj(); i2++) {

            SchedGlobalData.connectionItem lConnectionItem = mGlobalArea.getConnectionObj(i2);

            SchedDataNode tempNode = new SchedDataNode(
                                    lConnectionItem.getName(),
                                    connectionId,
                                    connectionId,
                                    CONNECTION_SCREEN_NO,
                                    CONNECTION_SCREEN_NO);
            tempNode.setAllowsChildren(false);
            groot.add(tempNode);
            lConnectionItem.setConnectionId(connectionId);
            connectionId = connectionId + 1;
        }

        CurrentNode = groot;

        SchedCellRenderer mSchedCellRenderer = new SchedCellRenderer();
        mSchedCellRenderer.setHighLightColor( mGlobalArea.getScreenColor(35) );
        mSchedCellRenderer.setSelected();

        gTreeModel = new DefaultTreeModel(groot, true);

        gTree = new JTree(gTreeModel);
        gTree.setCellRenderer( mSchedCellRenderer );

        TreePath rootPath = new TreePath(SCHEDULER_ROOT_TREE);

        gTree.setSelectionRow(0);

        gTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        gTree.addTreeSelectionListener(this);

        //Create the scroll pane and add the tree to it. 
        gTreeView = new JScrollPane(gTree);
    }

    public void createSchedulerTreeData() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createSchedulerTreeData");

        CurrentNode.setAllowsChildren(true);
        SchedTree.createTree(mGlobalArea, dataArea, mScreen, CurrentNode);
    }

    public void createResourceTree() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.createResourceTree");

        mTreeNode1 = null;
        mTreeNode2 = null;
        mCustomCellRenderer1 = new SchedCellRenderer();
        mCustomCellRenderer1.setHighLightColor( mGlobalArea.getScreenColor(35) );
        mCustomCellRenderer1.setRunningPlanColor2( mGlobalArea.getScreenColor(41) );
        mCustomCellRenderer1.setRunningPlanColor1( mGlobalArea.getScreenColor(42) );
        mCustomCellRenderer1.setSelected();

        mCustomCellRenderer2 = new SchedCellRenderer();
        mCustomCellRenderer2.setHighLightColor( mGlobalArea.getScreenColor(35) );
        mCustomCellRenderer2.setRunningPlanColor2( mGlobalArea.getScreenColor(41) );
        mCustomCellRenderer2.setRunningPlanColor1( mGlobalArea.getScreenColor(42) );
        mCustomCellRenderer2.setUnselected();

        mListener = new FocusListener() {
          public void focusGained(FocusEvent e) {
            // System.out.println(" Focus Gained " + name(e.getComponent()));
            if (name(e.getComponent()).equals("Tree1")) {
                mCustomCellRenderer2.setUnselected();
                if (mTreeNode2 != null) {
                    gTreeModel2.nodeChanged(mTreeNode2);
                }
                mCustomCellRenderer1.setSelected();
                SchedDataNode node = (SchedDataNode)gTree1.getLastSelectedPathComponent();
                gTreeModel1.nodeChanged(node);
                currentTree = RESOURCE_ROOT_TREE;
                if (mTreeNode1 != null) {
                    if (mTreeNode1.getNodeId() == node.getNodeId()) {
                        updateDataPane(node);
                    }
                }
            }
            if (name(e.getComponent()).equals("Tree2")) {
                mCustomCellRenderer1.setUnselected();
                if (mTreeNode1 != null) {
                    gTreeModel1.nodeChanged(mTreeNode1);
                }
                mCustomCellRenderer2.setSelected();
                SchedDataNode node = (SchedDataNode)gTree2.getLastSelectedPathComponent();
                gTreeModel2.nodeChanged(node);
                currentTree = PENDING_AREA_TREE;
                if (mTreeNode2 != null) {
                    if (mTreeNode2.getNodeId() == node.getNodeId()) {
                        updateDataPane(node);
                    }
                }
            }
          }
          public void focusLost(FocusEvent e) {
            // System.out.println(" Focus Lost " + name(e.getComponent()));
            if (name(e.getComponent()).equals("Tree1")) {
                mTreeNode1 = (SchedDataNode)gTree1.getLastSelectedPathComponent();
            }
            if (name(e.getComponent()).equals("Tree2")) {
                mTreeNode2 = (SchedDataNode)gTree2.getLastSelectedPathComponent();
            }
          }

          private String name(Component c) {
            return (c == null) ? null : c.getName();
          }
        };

        groot1 = new SchedDataNode(RESOURCE_ROOT_TREE, 0);
        if (dataArea.isPendingArea(true))
            groot2 = new SchedDataNode(PENDING_AREA_TREE, 0);
        else
            groot2 = new SchedDataNode(EMPTY_PENDING_AREA_TREE, 0);

        SchedResourceTree.createResourceTree(
                            mGlobalArea,
                            dataArea,
                            mScreen,
                            groot1,
                            groot2);

        CurrentNode = groot1;

        gTreeModel1 = new DefaultTreeModel(groot1, true);

        gTree1 = new JTree(gTreeModel1);
        gTree1.setName("Tree1");
        gTree1.setCellRenderer( mCustomCellRenderer1 );
        gTree1.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        gTree1.addFocusListener(mListener);
        gTree1.addTreeSelectionListener(this);

        // gTree1.setSelectionRow(0);
        // gTree1.requestFocus();

        //Create the scroll pane and add the tree to it. 
        gTreeView1 = new JScrollPane(gTree1);


        gTreeModel2 = new DefaultTreeModel(groot2, true);

        gTree2 = new JTree(gTreeModel2);
        gTree2.setName("Tree2");
        gTree2.setCellRenderer( mCustomCellRenderer2 );
        gTree2.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        // gTree2.setSelectionRow(0);

        //Listen for when the selection changes.
        gTree2.addFocusListener(mListener);
        gTree2.addTreeSelectionListener(this);

        gTreeView2 = new JScrollPane(gTree2);
    }

    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.valueChanged");

        if (e.getPath() != null) {
            int mTabNo = mPane.getTabbedPane().getSelectedIndex();
            if (mTabNo != -1) CurrentNode.setTabPane(mTabNo);

            if (e.getPath().getPathComponent(0).toString().equals(SCHEDULER_ROOT_TREE) &&
               ((SchedDataNode)gTree.getLastSelectedPathComponent() != null)) {
                currentTree = SCHEDULER_ROOT_TREE;
                CurrentNode = (SchedDataNode)gTree.getLastSelectedPathComponent();
            }

            if (e.getPath().getPathComponent(0).toString().equals(RESOURCE_ROOT_TREE) &&
               ((SchedDataNode)gTree1.getLastSelectedPathComponent() != null)) {
                currentTree = RESOURCE_ROOT_TREE;
                CurrentNode = (SchedDataNode)gTree1.getLastSelectedPathComponent();
            }
            if (e.getPath().getPathComponent(0).toString().equals(PENDING_AREA_TREE) &&
               ((SchedDataNode)gTree2.getLastSelectedPathComponent() != null)) {
                currentTree = PENDING_AREA_TREE;
                CurrentNode = (SchedDataNode)gTree2.getLastSelectedPathComponent();
            }
            if (e.getPath().getPathComponent(0).toString().equals(EMPTY_PENDING_AREA_TREE) &&
               ((SchedDataNode)gTree2.getLastSelectedPathComponent() != null)) {
                currentTree = EMPTY_PENDING_AREA_TREE;
                CurrentNode = (SchedDataNode)gTree2.getLastSelectedPathComponent();
            }

            CurrentPath = e.getNewLeadSelectionPath();

            // System.out.println(" G5 " + CurrentNode.getNodeId());
            checkValues(e);
        }
    }

    private void checkValues(TreeSelectionEvent e) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.checkValues");

        if (CurrentNode != null) {

            // Updates the screen display.
            if ((mConnectionId != CurrentNode.getConnectId()) &&
                (CurrentNode.getIconType() != ROOT_SCREEN_NO) &&
                (currentScreenDisp == 0) ) {
                mConnectionItem = mGlobalArea.getConnectionObjById(CurrentNode.getConnectId());
                mConnectionId = CurrentNode.getConnectId();

                // System.out.println(" G1 " + mConnectionId);
                if (mConnectionItem.isConnected()) {
                    dataArea = mGlobalArea.getDataAreaObjById(mConnectionId);
                }
            }

            if ((CurrentNode.getScreenId() != currentScreenId) ||
                (CurrentNode.getScreenNo() != currentScreenNo))
            {
                currentScreenNo = CurrentNode.getScreenNo();
                currentScreenId = CurrentNode.getScreenId();

                refreshDataScreen(CurrentNode.getScreenId());
            }
            ParentNode = (SchedDataNode)CurrentNode.getParent();

            setupButtons();
            refreshNode();
            if ((CurrentNode.getNodeType().equals("L")) &&
                (CurrentNode.getTabPane() != -1)) {
                mPane.getTabbedPane().setSelectedIndex(CurrentNode.getTabPane());
            }
        }
    }

    public void updateDataPane(SchedDataNode node) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.updateDataPane");

        if ( currentScreenDisp == 0 || currentScreenDisp == 5 )
        {
            if (node.getScreenNo() != currentScreenNo) {

                currentScreenNo = node.getScreenNo();

                refreshDataScreen(currentScreenNo);
            }
            if (node.getScreenNo() != ROOT_SCREEN_NO)
                mPane.setDataArea(dataArea);
            refreshNode(node);
            CurrentNode = node;
            ParentNode = (SchedDataNode)CurrentNode.getParent();

            setupButtons();
        }
    }

    public void setupResourceMenu(int tabIndex) {
        // System.out.println(" X2.");
        if (mResourceMenu) {
            // System.out.println(" X3." + tabIndex);
            if (tabIndex == 0) {
                menuUpdate.setEnabled(true);
                buttonUpdate.setEnabled(true);
            }
            else {
                menuUpdate.setEnabled(false);
                buttonUpdate.setEnabled(false);
            }
        }
    }

    public void setupButtons() {
        if ( currentTree.equals(SCHEDULER_ROOT_TREE) )
            setupSchedulerMenu();
        else
            setupResourceMenu();
    }

    public void setupSchedulerMenu() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setupButtons");

        menuJobRun.setEnabled(false);
        menuJobStop.setEnabled(false);
        menuWOpen.setEnabled(false);
        menuWClose.setEnabled(false);
        menuJobDetail.setEnabled(false);

        buttonRun.setToolTipText(null);
        buttonStop.setToolTipText(null);
        if ( SchedFile.getFileOption(10) ) buttonRun.setText(" ");
        if ( SchedFile.getFileOption(10) ) buttonStop.setText(" ");

        for (int i1 = 0; i1 < 4; i1++) {
            if ( ! menuViews[i1].isEnabled())
                menuViews[i1].setEnabled(true);
        }

        if ((mConnectionItem.isConnected()) &&
            (CurrentNode.getNodeId() != 0)) {

            SchedGlobalData.connectionItem lConnectionItem = 
                    mGlobalArea.getConnectionObjById(mConnectionId);
            userName2.setText(lConnectionItem.getAcName());
            userName2.repaint();
            hostName2.setText(lConnectionItem.getName());
            hostName2.repaint();

            if (mConnectionItem.isFullConnected()) {
                for (int i1 = 4; i1 < 7; i1++) {
                    if ( ! menuViews[i1].isEnabled())
                        menuViews[i1].setEnabled(true);
                }
                menuPurge.setEnabled(true);
            }
            else {
                for (int i1 = 4; i1 < 7; i1++) {
                    if (menuViews[i1].isEnabled())
                        menuViews[i1].setEnabled(false);
                }
                menuPurge.setEnabled(false);
            }
        }
        else {
            userName2.setText("");
            userName2.repaint();
            hostName2.setText("");
            hostName2.repaint();

            for (int i1 = 4; i1 < 7; i1++) {
                if (menuViews[i1].isEnabled())
                    menuViews[i1].setEnabled(false);
            }
            menuPurge.setEnabled(false);
        }

        if ((currentScreenNo == CONNECTION_SCREEN_NO) &&
            (currentScreenDisp == 0)) {

            if (mConnectionItem.isConnected() || CurrentNode.isConnected()) {
                if (! buttonDisconnect.isEnabled()) buttonDisconnect.setEnabled(true);
                if (! menuDisconnect.isEnabled())   menuDisconnect.setEnabled(true);

                if (mConnectionItem.isFullConnected()) {
                    if (buttonConnect.isEnabled())    buttonConnect.setEnabled(false);
                    if (menuConnect.isEnabled())      menuConnect.setEnabled(false);
                    if (! buttonRefresh.isEnabled())  buttonRefresh.setEnabled(true);
                    if (! menuRefresh.isEnabled())    menuRefresh.setEnabled(true);
                }
                else {
                    if (! buttonConnect.isEnabled())    buttonConnect.setEnabled(true);
                    if (! menuConnect.isEnabled())      menuConnect.setEnabled(true);
                    if (buttonRefresh.isEnabled())    buttonRefresh.setEnabled(false);
                    if (menuRefresh.isEnabled())      menuRefresh.setEnabled(false);
                }
            }
            else {
                if (! buttonConnect.isEnabled())  buttonConnect.setEnabled(true);
                if (buttonDisconnect.isEnabled()) buttonDisconnect.setEnabled(false);
                if (buttonRefresh.isEnabled())    buttonRefresh.setEnabled(false);
                if (! menuConnect.isEnabled())    menuConnect.setEnabled(true);
                if (menuDisconnect.isEnabled())   menuDisconnect.setEnabled(false);
                if (menuRefresh.isEnabled())      menuRefresh.setEnabled(false);
            }
        }
        else {
            if (buttonConnect.isEnabled())    buttonConnect.setEnabled(false);
            if (buttonDisconnect.isEnabled()) buttonDisconnect.setEnabled(false);
            if (buttonRefresh.isEnabled())    buttonRefresh.setEnabled(false);
            if (menuConnect.isEnabled())      menuConnect.setEnabled(false);
            if (menuDisconnect.isEnabled())   menuDisconnect.setEnabled(false);
            if (menuRefresh.isEnabled())      menuRefresh.setEnabled(false);
        }

        if ((currentScreenDisp > 0) ||
            (currentScreenNo == ROOT_SCREEN_NO) ||
            (currentScreenNo == JOB_RUN_DETAILS_SCREEN_NO) ||
            (currentScreenNo == JOBS_RUNNING_SCREEN_NO) ||
            (currentScreenNo == GLOBAL_ATTRIBUTES_SCREEN_NO) ||
            (mGlobalArea.blockedOption(currentScreenNo, 3)) ||
            ((currentScreenNo == CONNECTION_SCREEN_NO) &&
             (CurrentNode.getAllowsChildren() == true))
           )
        {
            if (buttonDrop.isEnabled()) {
                buttonDrop.setEnabled(false);
                menuDrop.setEnabled(false);
            }
        }
        else {
            if (! buttonDrop.isEnabled()) {
                buttonDrop.setEnabled(true);
                menuDrop.setEnabled(true);
            }
        }

        if ((currentScreenDisp > 0) ||
            (currentScreenNo == ROOT_SCREEN_NO) ||
            (currentScreenNo == JOB_RUN_DETAILS_SCREEN_NO) ||
            (currentScreenNo == JOBS_RUNNING_SCREEN_NO) ||
            (currentScreenNo == CHAINS_RUNNING_SCREEN_NO) ||
            (currentScreenNo == NOTIFICATIONS_SCREEN_NO) ||
            (currentScreenNo == CHAIN_RULES_SCREEN_NO) ||
            (currentScreenNo == EXT_DESTINATION_SCREEN_NO) ||
            (currentScreenNo == PROGRAM_ARGS_SCREEN_NO) ||
            (mGlobalArea.blockedOption(currentScreenNo, 4)))
        {
            if (buttonUpdate.isEnabled())
            {
                buttonUpdate.setEnabled(false);
                menuUpdate.setEnabled(false);
            }
        }
        else {
            if (! buttonUpdate.isEnabled())
            {
                buttonUpdate.setEnabled(true);
                menuUpdate.setEnabled(true);
            }
        }

        if ((currentScreenDisp > 0) ||
            (currentScreenNo == ROOT_SCREEN_NO) ||
            (currentScreenNo == CONNECTION_SCREEN_NO) ||
            (currentScreenNo == JOB_RUN_DETAILS_SCREEN_NO) ||
            (currentScreenNo == JOBS_RUNNING_SCREEN_NO) ||
            (currentScreenNo == CHAINS_RUNNING_SCREEN_NO) ||
            (currentScreenNo == CREDENTIALS_SCREEN_NO) ||
            (currentScreenNo == GLOBAL_ATTRIBUTES_SCREEN_NO) ||
            (currentScreenNo == NOTIFICATIONS_SCREEN_NO) ||
            (currentScreenNo == CHAIN_RULES_SCREEN_NO) ||
            (currentScreenNo == DB_DESTINATION_SCREEN_NO) ||
            (currentScreenNo == EXT_DESTINATION_SCREEN_NO) ||
            (mGlobalArea.blockedOption(currentScreenNo, 2)))
        {
            if (buttonCopy.isEnabled()) {
                 buttonCopy.setEnabled(false);
                 menuCopy.setEnabled(false);
            }
        }
        else {
            if (! buttonCopy.isEnabled()) {
                buttonCopy.setEnabled(true);
                menuCopy.setEnabled(true);
            }
        }

        if (enableButton) {
            buttonEnable.setEnabled(false);
            menuEnable.setEnabled(false);
            enableButton = false;
        }
        if (disableButton) {
            buttonDisable.setEnabled(false);
            menuDisable.setEnabled(false);
            disableButton = false;
        }

        if (ParentNode != null) {
            if (((currentScreenNo == WINDOW_SCREEN_NO) &&
                 (ParentNode.getIconType() == WINDOW_SCREEN_NO)) ||
                ((currentScreenNo == DB_DESTINATION_SCREEN_NO) &&
                 (ParentNode.getIconType() == DB_DESTINATION_SCREEN_NO)) ||
                ((currentScreenNo == EXT_DESTINATION_SCREEN_NO) &&
                 (ParentNode.getIconType() == EXT_DESTINATION_SCREEN_NO))) {
                if (! menuAddGroup.isEnabled())
                    menuAddGroup.setEnabled(true);
            }
            else {
                if (menuAddGroup.isEnabled())
                    menuAddGroup.setEnabled(false);
            }

            if (((currentScreenNo == WINDOW_SCREEN_NO) &&
                 (ParentNode.getScreenNo() == WINDOW_GROUP_SCREEN_NO)) ||
                ((currentScreenNo == WINDOW_SCREEN_NO) &&
                 (ParentNode.getScreenNo() == GROUP_SCREEN_NO)) ||
                ((currentScreenNo == DB_DESTINATION_SCREEN_NO) &&
                 (ParentNode.getIconType() == DB_GROUP_ICON_NO)) ||
                ((CurrentNode.getNodeName().equals("LOCAL")) &&
                 (ParentNode.getIconType() == DB_GROUP_ICON_NO)) ||
                ((currentScreenNo == EXT_DESTINATION_SCREEN_NO) &&
                 (ParentNode.getIconType() == EXT_GROUP_ICON_NO))) {
                if (! menuRemoveGroup.isEnabled())
                    menuRemoveGroup.setEnabled(true);
            }
            else {
                if (menuRemoveGroup.isEnabled())
                    menuRemoveGroup.setEnabled(false);
            }
        }
        else {
            menuAddGroup.setEnabled(false);
            menuRemoveGroup.setEnabled(false);
        }

        if ((currentScreenDisp == 0) &&
            ((currentScreenNo == JOB_SCREEN_NO) ||
             (currentScreenNo == WINDOW_SCREEN_NO))) {
            if (! mGlobalArea.blockedOption(currentScreenNo, 7)) {
                buttonRun.setEnabled(true);
                buttonRun.setIcon(runIcon);

                if (currentScreenNo == JOB_SCREEN_NO) {
                    menuJobRun.setEnabled(true);
                    buttonRun.setToolTipText("Run the currently selected Job");
                    if ( SchedFile.getFileOption(10) ) buttonRun.setText("Run");
                }
                if (currentScreenNo == WINDOW_SCREEN_NO) {
                    menuWOpen.setEnabled(true);
                    buttonRun.setToolTipText("Open the currently selected Window");
                    if ( SchedFile.getFileOption(10) ) buttonRun.setText("Open");
                }
            }
            if (! mGlobalArea.blockedOption(currentScreenNo, 8)) {
                buttonStop.setEnabled(true);
                buttonStop.setIcon(stopIcon);

                if (currentScreenNo == JOB_SCREEN_NO) {
                    menuJobStop.setEnabled(true);
                    buttonStop.setToolTipText("Stop the currently selected Job");
                    if ( SchedFile.getFileOption(10) ) buttonStop.setText("Stop");
                }
                if (currentScreenNo == WINDOW_SCREEN_NO) {
                    menuWClose.setEnabled(true);
                    buttonStop.setToolTipText("Close the currently selected Window");
                    if ( SchedFile.getFileOption(10) ) buttonStop.setText("Close");
                }
            }
        }
        else {
            if ((currentScreenDisp == 2) || (currentScreenDisp == 3)) {
                buttonRun.setEnabled(true);
                buttonRun.setIcon(prevIcon);
                if ( SchedFile.getFileOption(10) ) buttonRun.setText("Prev");
                buttonRun.setToolTipText("Previous Day.");
                buttonStop.setEnabled(true);
                buttonStop.setIcon(nextIcon);
                if ( SchedFile.getFileOption(10) ) buttonStop.setText("Next");
                buttonStop.setToolTipText("Next Day.");
            }
            else {
                if (buttonRun.isEnabled()) {
                    buttonRun.setEnabled(false);
                    buttonRun.setIcon(blankIcon);
                }
                if (buttonStop.isEnabled()) {
                    buttonStop.setEnabled(false);
                    buttonStop.setIcon(blankIcon);
                }
            }
        }

        if ((currentScreenDisp == 2) || (currentScreenDisp == 3) || (currentScreenDisp == 4)) {
            buttonDetail.setEnabled(true);
            buttonDetail.setIcon(detailIcon);
            if ( SchedFile.getFileOption(10) ) buttonDetail.setText("Detail");
            buttonDetail.setToolTipText("Display log entry details.");
            menuJobDetail.setEnabled(true);
        }
        else {
            if (buttonDetail.isEnabled()) {
                buttonDetail.setEnabled(false);
                buttonDetail.setIcon(blankIcon);
                buttonDetail.setToolTipText(null);
                if ( SchedFile.getFileOption(10) ) buttonDetail.setText(" ");
            }
        }
        // System.out.println(" X1.");

        if (! menuAddItem[0].isEnabled()) menuAddItem[0].setEnabled(true);

        if (mConnectionItem.isFullConnected()) {
            if (! menuAddItem[1].isEnabled()) menuAddItem[1].setEnabled(true);
            if (! menuAddItem[4].isEnabled()) menuAddItem[4].setEnabled(true);
            if (! menuAddItem[6].isEnabled()) menuAddItem[6].setEnabled(true);
            if (! menuAddItem[11].isEnabled()) menuAddItem[11].setEnabled(true);
            if (! menuAddItem[12].isEnabled()) menuAddItem[12].setEnabled(true);
            if (! menuAddItem[13].isEnabled()) menuAddItem[13].setEnabled(true);

            if (dataArea.getVersionNo() > 1) {
                if (! menuAddItem[7].isEnabled())   menuAddItem[7].setEnabled(true);
                if ((currentScreenNo == CHAINS_SCREEN_NO) &&
                    (! menuAddItem[8].isEnabled())) menuAddItem[8].setEnabled(true);
                if ((currentScreenNo == CHAINS_SCREEN_NO) &&
                    (! menuAddItem[9].isEnabled())) menuAddItem[9].setEnabled(true);
            }
            if ((dataArea.getVersionNo() > 2) &&
                (! menuAddItem[10].isEnabled()))  menuAddItem[10].setEnabled(true);

            if (dataArea.getVersionNo() > 3) {
                if (! menuAddItem[14].isEnabled()) menuAddItem[14].setEnabled(true);
                if (! menuAddItem[15].isEnabled()) menuAddItem[15].setEnabled(true);
            }
        }
        else {
            if (menuAddItem[1].isEnabled()) menuAddItem[1].setEnabled(false);
            if (menuAddItem[4].isEnabled()) menuAddItem[4].setEnabled(false);
            if (menuAddItem[6].isEnabled()) menuAddItem[6].setEnabled(false);
            if (menuAddItem[7].isEnabled()) menuAddItem[7].setEnabled(false);
            if (menuAddItem[8].isEnabled()) menuAddItem[8].setEnabled(false);
            if (menuAddItem[9].isEnabled()) menuAddItem[9].setEnabled(false);
            if (menuAddItem[10].isEnabled()) menuAddItem[10].setEnabled(false);
            if (menuAddItem[11].isEnabled()) menuAddItem[11].setEnabled(false);
            if (menuAddItem[12].isEnabled()) menuAddItem[12].setEnabled(false);
            if (menuAddItem[13].isEnabled()) menuAddItem[13].setEnabled(false);
            if (menuAddItem[14].isEnabled()) menuAddItem[14].setEnabled(false);
            if (menuAddItem[15].isEnabled()) menuAddItem[15].setEnabled(false);
        }

        if ((mConnectionId > 0) &&
            (currentScreenNo == JOB_SCREEN_NO) &&
            (! mGlobalArea.blockedOption(JOB_ARGS_SCREEN_NO, 2)))
        {
            if (! menuAddItem[2].isEnabled())  menuAddItem[2].setEnabled(true);
            if ((! menuAddItem[3].isEnabled()) &&
                (dataArea.getVersionNo() > 3)) menuAddItem[3].setEnabled(true);
        }
        else {
            if (menuAddItem[2].isEnabled()) menuAddItem[2].setEnabled(false);
            if (menuAddItem[3].isEnabled()) menuAddItem[3].setEnabled(false);
        }

        if ((mConnectionId > 0) &&
            (currentScreenNo == PROGRAM_SCREEN_NO) &&
            (! mGlobalArea.blockedOption(PROGRAM_ARGS_SCREEN_NO, 2)))
        {
            if (! menuAddItem[5].isEnabled()) menuAddItem[5].setEnabled(true);
        }
        else {
            if (menuAddItem[5].isEnabled()) menuAddItem[5].setEnabled(false);
        }
    }

    public void setupResourceMenu() {
        if (buttonConnect.isEnabled())    buttonConnect.setEnabled(false);
        if (buttonDisconnect.isEnabled()) buttonDisconnect.setEnabled(false);
        if (buttonRefresh.isEnabled())    buttonRefresh.setEnabled(false);
        if (menuConnect.isEnabled())      menuConnect.setEnabled(false);
        if (menuDisconnect.isEnabled())   menuDisconnect.setEnabled(false);
        if (menuRefresh.isEnabled())      menuRefresh.setEnabled(false);


        if (dataArea.isPendingArea()) {

            menuAddItem[16].setEnabled(true);
            menuAddItem[17].setEnabled(true);

            if (currentTree.equals(PENDING_AREA_TREE)) {
                if ((currentScreenNo == PLAN_SCREEN_NO) ||
                    (currentScreenNo == CDB_PLAN_SCREEN_NO))
                    menuAddItem[18].setEnabled(true);
                else
                    menuAddItem[18].setEnabled(false);

                if (currentScreenNo == CONSUMER_GROUP_SCREEN_NO) {
                    menuAddItem[19].setEnabled(true);
                    menuAddItem[20].setEnabled(true);
                }
                else {
                    menuAddItem[19].setEnabled(false);
                    menuAddItem[20].setEnabled(false);
                }

                if (currentScreenNo == MAPPING_PRIORITY_SCREEN_NO) {
                    buttonRun.setEnabled(true);
                    buttonRun.setIcon(upIcon);
                    if ( SchedFile.getFileOption(12) ) buttonRun.setText("Up");
                    buttonRun.setToolTipText("Move Priority Up.");
                    buttonStop.setEnabled(true);
                    buttonStop.setIcon(downIcon);
                    if ( SchedFile.getFileOption(12) ) buttonStop.setText("Down");
                    buttonStop.setToolTipText("Move Priority Down.");
                    upDownButton = true;
                }
                else {
                    if (upDownButton) {
                        buttonRun.setEnabled(false);
                        buttonRun.setIcon(blankIcon);
                        if ( SchedFile.getFileOption(12) ) buttonRun.setText(" ");
                        buttonRun.setToolTipText(" ");

                        buttonStop.setEnabled(false);
                        buttonStop.setIcon(blankIcon);
                        if ( SchedFile.getFileOption(12) ) buttonStop.setText(" ");
                        buttonStop.setToolTipText(" ");
                        upDownButton = false;
                    }
                }
                if ((currentScreenNo == PLAN_SCREEN_NO) ||
                    (currentScreenNo == CDB_PLAN_SCREEN_NO) ||
                    (currentScreenNo == CONSUMER_GROUP_SCREEN_NO))
                {
                    if ((currentScreenNo == CONSUMER_GROUP_SCREEN_NO) &&
                        ( (mPane.getTabbedPane()).getSelectedIndex() != 0) ) {
                        menuUpdate.setEnabled(false);
                        buttonUpdate.setEnabled(false);
                    }
                    else {
                        menuUpdate.setEnabled(true);
                        buttonUpdate.setEnabled(true);
                    }
                    menuDrop.setEnabled(true);
                    buttonDrop.setEnabled(true);
                }
                else {
                    menuUpdate.setEnabled(false);
                    menuDrop.setEnabled(false);
                    buttonUpdate.setEnabled(false);
                    buttonDrop.setEnabled(false);
                }
            }
            else {
                if (upDownButton) {
                    buttonStop.setIcon(blankIcon);
                    buttonStop.setEnabled(false);
                    buttonStop.setToolTipText(" ");
                    if ( SchedFile.getFileOption(12) ) buttonStop.setText(" ");

                    buttonRun.setIcon(blankIcon);
                    buttonRun.setEnabled(false);
                    buttonRun.setToolTipText(" ");
                    if ( SchedFile.getFileOption(12) ) buttonRun.setText(" ");
                    upDownButton = false;
                }
                menuAddItem[16].setEnabled(false);
                menuAddItem[17].setEnabled(false);
                menuAddItem[18].setEnabled(false);
                menuAddItem[19].setEnabled(false);
                menuAddItem[20].setEnabled(false);

                menuUpdate.setEnabled(false);
                menuDrop.setEnabled(false);
                buttonUpdate.setEnabled(false);
                buttonDrop.setEnabled(false);
            }
        }
        else {
            menuAddItem[16].setEnabled(false);
            menuAddItem[17].setEnabled(false);
            menuAddItem[18].setEnabled(false);
            menuAddItem[19].setEnabled(false);
            menuAddItem[20].setEnabled(false);

            menuUpdate.setEnabled(false);
            menuDrop.setEnabled(false);
            buttonUpdate.setEnabled(false);
            buttonDrop.setEnabled(false);
        }
    }

    public void setEnableButtonTrue() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setEnableButtonTrue");

        if (! mGlobalArea.blockedOption(currentScreenNo, 5)) {
            buttonEnable.setEnabled(true);
            menuEnable.setEnabled(true);
            enableButton = true;
        }
    }

    public void setEnableButtonFalse() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setEnableButtonFalse");

        buttonEnable.setEnabled(false);
        menuEnable.setEnabled(false);
        enableButton = false;
    }

    public void setDisableButtonTrue() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setDisableButtonTrue");

        if (! mGlobalArea.blockedOption(currentScreenNo, 6)) {
            buttonDisable.setEnabled(true);
            menuDisable.setEnabled(true);
            disableButton = true;
        }
    }

    public void setDisableButtonFalse() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.setDisableButtonFalse");

        buttonDisable.setEnabled(false);
        menuDisable.setEnabled(false);
        disableButton = false;
    }


    public void updateNode() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.updateNode");

        int nodeId = CurrentNode.getNodeId();

        switch(CurrentNode.getScreenNo()) {
              case JOB_SCREEN_NO:
                gJobItem = dataArea.getJobId(nodeId);
                gJobItem = dataArea.GetJobItem(
                                 gJobItem.getOwner(),
                                 gJobItem.getJobName(),
                                 1);
                break;
              case PROGRAM_SCREEN_NO:
                gProgramItem = dataArea.getProgramId(nodeId);
                gProgramItem = dataArea.GetProgramItem(
                                 gProgramItem.getOwner(),
                                 gProgramItem.getProgramName(),
                                 1);
                break;
              case SCHEDULE_SCREEN_NO:
                gScheduleItem = dataArea.getScheduleId(nodeId);
                gScheduleItem = dataArea.GetScheduleItem(
                                 gScheduleItem.getOwner(),
                                 gScheduleItem.getScheduleName(),
                                 1);
                break;
              case JOB_CLASS_SCREEN_NO:
                gJobClassItem = dataArea.getJobClassId(nodeId);
                gJobClassItem = dataArea.GetJobClassItem(
                                 gJobClassItem.getJobClassName(),
                                 1);
                break;
              case WINDOW_SCREEN_NO:
                gWindowItem = dataArea.getWindowId(nodeId);
                gWindowItem = dataArea.GetWindowItem(
                                gWindowItem.getWindowName(),
                                1);
                break;
              case WINDOW_GROUP_SCREEN_NO:
                gWindowGroupItem = dataArea.getWindowGroupId(nodeId);
                gWindowGroupItem = dataArea.GetWindowGroupItem(
                                gWindowGroupItem.getWindowGroupName(),
                                1);
                break;
              case JOB_ARGS_SCREEN_NO:
                gJobArgsItem = dataArea.getJobArgsId(nodeId);
                gJobArgsItem = dataArea.GetJobArgsItem(
                                gJobArgsItem.getOwner(),
                                gJobArgsItem.getJobName(),
                                gJobArgsItem.getArgumentPosition(),
                                1);
                break;
              case PROGRAM_ARGS_SCREEN_NO:
                gProgramArgsItem = dataArea.getProgramArgsId(nodeId);
                gProgramArgsItem = dataArea.GetProgramArgsItem(
                                gProgramArgsItem.getOwner(),
                                gProgramArgsItem.getProgramName(),
                                gProgramArgsItem.getArgumentPosition(),
                                1);
                break;
              case CHAINS_SCREEN_NO:
                gChainsItem = dataArea.getChainsId(nodeId);
                gChainsItem = dataArea.GetChainsItem(
                                gChainsItem.getOwner(),
                                gChainsItem.getChainName(),
                                1);
                break;
              case CHAIN_STEPS_SCREEN_NO:
                gChainStepsItem = dataArea.getChainStepsId(nodeId);
                gChainStepsItem = dataArea.GetChainStepsItem(
                                gChainStepsItem.getOwner(),
                                gChainStepsItem.getChainName(),
                                gChainStepsItem.getStepName(),
                                1);
                break;
              case GLOBAL_ATTRIBUTES_SCREEN_NO:
                gGlobalAttributesItem = dataArea.getGlobalAttributesId(nodeId);
                dataArea.GetGlobalAttributesItem(
                                gGlobalAttributesItem.getAttributeName());
                break;
              case CREDENTIALS_SCREEN_NO:
                gCredentialsItem = dataArea.getCredentialsId(nodeId);
                gCredentialsItem = dataArea.GetCredentialItem(
                                gCredentialsItem.getOwner(),
                                gCredentialsItem.getCredentialName(),
                                1);
                break;
              case FILE_WATCHERS_SCREEN_NO:
                gFileWatchersItem = dataArea.getFileWatchersId(nodeId);
                gFileWatchersItem = dataArea.GetFileWatcherItem(
                                gFileWatchersItem.getOwner(),
                                gFileWatchersItem.getFileWatcherName(),
                                1);
                break;
              case GROUP_SCREEN_NO:
                gGroupItem = dataArea.getGroupsId(nodeId);
                gGroupItem = dataArea.GetGroupItem(
                                gGroupItem.getOwner(),
                                gGroupItem.getGroupName(),
                                1);
                break;
              case PLAN_SCREEN_NO:
                if (mPane.getTabbedPane().getSelectedIndex() == 1) {
                    SchedDataArea.PlanDirectiveItem mPlanDirectiveItem = mPane.getPlanDirective();
                    dataArea.GetPlanDirectiveItem(
                                mPlanDirectiveItem.getPlan(),
                                mPlanDirectiveItem.getGroup(), 1);
                }
                else {
                    gPlanItem = dataArea.getPlanId(nodeId);
                    dataArea.GetPlanItem(gPlanItem.getPlan(), 1);
                }
                break;
              case CDB_PLAN_SCREEN_NO:
                if (mPane.getTabbedPane().getSelectedIndex() == 1) {
                    SchedDataArea.CdbPlanDirectiveItem mCdbPlanDirectiveItem = mPane.getCdbPlanDirective();
                    dataArea.GetCdbPlanDirectiveItem(
                                mCdbPlanDirectiveItem.getPlan(),
                                mCdbPlanDirectiveItem.getPluggableDatabase(), 1);
                }
                else {
                    gCdbPlanItem = dataArea.getCdbPlanId(nodeId);
                    dataArea.GetCdbPlanItem(gCdbPlanItem.getPlan(), 1);
                }
                break;
              case CONSUMER_GROUP_SCREEN_NO:
                gConsumerGroupItem = dataArea.getConsumerGroupId(nodeId);
                dataArea.GetConsumerGroupItem(
                                gConsumerGroupItem.getConsumerGroup(), 1);
                break;
        }
    }

    public void refreshNode() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshNode");

        refreshNode(CurrentNode);
    }

    public void refreshNode(SchedDataNode mNode) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshNode");

        int nodeId = mNode.getNodeId();
        // System.out.println(" G6 " + CurrentNode.getScreenNo());

        // Updates the screen area from the data area.
        switch(CurrentNode.getScreenNo()) {
              case JOB_SCREEN_NO:
                int mSeqNo = dataArea.getNextSeqNo();
                gJobItem = dataArea.getJobId(nodeId);
                refreshJob();
                break;
              case PROGRAM_SCREEN_NO:
                gProgramItem = dataArea.getProgramId(nodeId);
                refreshProgram();
                break;
              case SCHEDULE_SCREEN_NO:
                gScheduleItem = dataArea.getScheduleId(nodeId);
                refreshSchedule();
                break;
              case JOB_CLASS_SCREEN_NO:
                gJobClassItem = dataArea.getJobClassId(nodeId);
                refreshJobClass();
                break;
              case WINDOW_SCREEN_NO:
                gWindowItem = dataArea.getWindowId(nodeId);
                refreshWindow();
                break;
              case WINDOW_GROUP_SCREEN_NO:
                gWindowGroupItem = dataArea.getWindowGroupId(nodeId);
                refreshWindowGroup();
                break;
              case JOB_ARGS_SCREEN_NO:
                gJobArgsItem = dataArea.getJobArgsId(nodeId);
                refreshJobArgs();
                break;
              case PROGRAM_ARGS_SCREEN_NO:
                gProgramArgsItem = dataArea.getProgramArgsId(nodeId);
                refreshProgramArgs();
                break;
              case CHAINS_SCREEN_NO:
                gChainsItem = dataArea.getChainsId(nodeId);
                refreshChains();
                break;
              case CHAIN_STEPS_SCREEN_NO:
                gChainStepsItem = dataArea.getChainStepsId(nodeId);
                refreshChainSteps();
                break;
              case CHAIN_RULES_SCREEN_NO:
                gChainRulesItem = dataArea.getChainRulesId(nodeId);
                refreshChainRules();
                break;
              case GLOBAL_ATTRIBUTES_SCREEN_NO:
                gGlobalAttributesItem = dataArea.getGlobalAttributesId(nodeId);
                refreshGlobalAttributes();
                break;
              case CREDENTIALS_SCREEN_NO:
                gCredentialsItem = dataArea.getCredentialsId(nodeId);
                refreshCredentials();
                break;
              case FILE_WATCHERS_SCREEN_NO:
                gFileWatchersItem = dataArea.getFileWatchersId(nodeId);
                refreshFileWatchers();
                break;
              case NOTIFICATIONS_SCREEN_NO:
                gNotificationsItem = dataArea.getNotificationsId(nodeId);
                refreshNotifications();
                break;
              case DB_DESTINATION_SCREEN_NO:
                gDbDestsItem = dataArea.getDbDestsId(nodeId);
                refreshDbDests();
                break;
              case EXT_DESTINATION_SCREEN_NO:
                gExtDestsItem = dataArea.getExtDestsId(nodeId);
                refreshExtDests();
                break;
              case GROUP_SCREEN_NO:
                gGroupItem = dataArea.getGroupsId(nodeId);
                refreshGroups();
                break;
              case PLAN_SCREEN_NO:
                gPlanItem = dataArea.getPlanId(nodeId);
                refreshPlan();
                break;
              case CDB_PLAN_SCREEN_NO:
                gCdbPlanItem = dataArea.getCdbPlanId(nodeId);
                refreshCdbPlan();
                break;
              case CONSUMER_GROUP_SCREEN_NO:
                gConsumerGroupItem = dataArea.getConsumerGroupId(nodeId);
                refreshConsumerGroup();
                break;
              case MAPPING_PRIORITY_SCREEN_NO:
                refreshMappingPriority();
                break;
        }
    }

    private void refreshTextItem(String mStr, int mItemNo) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshTextItem");

        mPane.updateTextObj(mStr, mItemNo);
    }

    private void refreshJob() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshJob");

        mPane.updateTextObj(gJobItem.getOwner(),1);
        mPane.updateTextObj(gJobItem.getJobName(),2);
        mPane.updateTextObj(gJobItem.getJobCreator(),3);
        mPane.updateTextObj(gJobItem.getClientId(),4);
        mPane.updateTextObj(gJobItem.getGlobalUid(),5);
        mPane.updateTextObj(gJobItem.getProgramOwner(),6);
        mPane.updateTextObj(gJobItem.getProgramName(),7);
        mPane.updateTextObj(gJobItem.getJobType(),8);
        mPane.updateTextObj(gJobItem.getJobAction(),9);
        mPane.updateTextObj(Integer.toString(gJobItem.getNumberOfArguments()),10);
        mPane.updateTextObj(gJobItem.getScheduleOwner(),11);
        mPane.updateTextObj(gJobItem.getScheduleName(),12);
        mPane.updateTextObj(gJobItem.getStartDate(),13);
        mPane.updateTextObj(gJobItem.getRepeatInterval(),14);
        mPane.updateTextObj(gJobItem.getEndDate(),15);
        mPane.updateTextObj(gJobItem.getJobClass(),16);
        mPane.updateTextObj(gJobItem.getEnabled(),17);
        mPane.updateTextObj(gJobItem.getAutoDrop(),18);
        mPane.updateTextObj(gJobItem.getRestartable(),19);
        mPane.updateTextObj(gJobItem.getState(),20);
        mPane.updateTextObj(Integer.toString(gJobItem.getJobPriority()),21);
        mPane.updateTextObj(Integer.toString(gJobItem.getRunCount()),22);
        mPane.updateTextObj(Integer.toString(gJobItem.getMaxRuns()),23);
        mPane.updateTextObj(Integer.toString(gJobItem.getFailureCount()),24);
        mPane.updateTextObj(Integer.toString(gJobItem.getMaxFailures()),25);
        mPane.updateTextObj(Integer.toString(gJobItem.getRetryCount()),26);
        mPane.updateTextObj(gJobItem.getLastStartDate(),27);
        mPane.updateTextObj(gJobItem.getLastRunDuration(),28);
        mPane.updateTextObj(gJobItem.getNextRunDate(),29);
        mPane.updateTextObj(gJobItem.getScheduleLimit(),30);
        mPane.updateTextObj(gJobItem.getMaxRunDuration(),31);
        mPane.updateTextObj(gJobItem.getLoggingLevel(),32);
        mPane.updateTextObj(gJobItem.getStopOnWindowClose(),33);
        mPane.updateTextObj(gJobItem.getInstanceStickiness(),34);
        mPane.updateTextObj(gJobItem.getSystem(),35);
        mPane.updateTextObj(Integer.toString(gJobItem.getJobWeight()),36);
        mPane.updateTextObj(gJobItem.getNlsEnv(),37);
        mPane.updateTextObj(gJobItem.getSource(),38);
        mPane.updateTextObj(gJobItem.getDestination(),39);
        mPane.updateTextAreaObj(gJobItem.getComments(),40);

        if (dataArea.getVersionNo() > 1) {
            mPane.updateTextObj(gJobItem.getJobSubName(),41);
            mPane.updateTextObj(gJobItem.getScheduleType(),42);
            mPane.updateTextObj(gJobItem.getEventQueueOwner(),43);
            mPane.updateTextObj(gJobItem.getEventQueueName(),44);
            mPane.updateTextObj(gJobItem.getEventQueueAgent(),45);
            mPane.updateTextObj(gJobItem.getEventCondition(),46);
            mPane.updateTextObj(gJobItem.getEventRule(),47);
            mPane.updateTextObj(gJobItem.getRaiseEvents(),48);

            if (dataArea.getVersionNo() > 2) {
                mPane.updateTextObj(gJobItem.getJobStyle(),49);
                mPane.updateTextObj(gJobItem.getCredentialOwner(),50);
                mPane.updateTextObj(gJobItem.getCredentialName(),51);
                mPane.updateTextObj(Integer.toString(gJobItem.getInstanceId()),52);
                mPane.updateTextObj(gJobItem.getDeferredDrop(),53);

                if (dataArea.getVersionNo() > 3) {
                    mPane.updateTextObj(gJobItem.getFileWatcherOwner(),54);
                    mPane.updateTextObj(gJobItem.getFileWatcherName(),55);
                    mPane.updateTextObj(Integer.toString(gJobItem.getNumberOfDestinations()),56);
                    mPane.updateTextObj(gJobItem.getDestinationOwner(),57);
                    mPane.updateTextObj(gJobItem.getAllowRunsInRestricterMode(),58);
                }
            }
        }
        if (gJobItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshJobArgs() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshJobArgs");

        mPane.updateTextObj(gJobArgsItem.getOwner(),1);
        mPane.updateTextObj(gJobArgsItem.getJobName(),2);
        mPane.updateTextObj(gJobArgsItem.getArgumentName(),3);
        mPane.updateTextObj(Integer.toString(gJobArgsItem.getArgumentPosition()),4);
        mPane.updateTextObj(gJobArgsItem.getArgumentType(),5);
        mPane.updateTextObj(gJobArgsItem.getValue(),6);
        mPane.updateTextObj(gJobArgsItem.getOutArgument(),7);
    }

    private void refreshProgram() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshProgram");

        mPane.updateTextObj(gProgramItem.getOwner(),1);
        mPane.updateTextObj(gProgramItem.getProgramName(),2);
        mPane.updateTextObj(gProgramItem.getProgramType(),3);
        mPane.updateTextObj(gProgramItem.getProgramAction(),4);
        mPane.updateTextObj(Integer.toString(gProgramItem.getNumberOfArguments()),5);
        mPane.updateTextObj(gProgramItem.getEnabled(),6);
        mPane.updateTextAreaObj(gProgramItem.getComments(),7);

        if (dataArea.getVersionNo() > 1) {
            mPane.updateTextObj(gProgramItem.getDetached(),8);
            if (dataArea.getVersionNo() > 2) {
                mPane.updateTextObj(gProgramItem.getScheduleLimit(),9);
                mPane.updateTextObj(Integer.toString(gProgramItem.getPriority()),10);
                mPane.updateTextObj(Integer.toString(gProgramItem.getWeight()),11);
                mPane.updateTextObj(Integer.toString(gProgramItem.getMaxRuns()),12);
                mPane.updateTextObj(Integer.toString(gProgramItem.getMaxFailures()),13);
                mPane.updateTextObj(gProgramItem.getMaxRunDuration(),14);
                mPane.updateTextObj(gProgramItem.getNlsEnv(),15);
            }
        }

        if (gProgramItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshProgramArgs() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshProgramArgs");

        mPane.updateTextObj(gProgramArgsItem.getOwner(),1);
        mPane.updateTextObj(gProgramArgsItem.getProgramName(),2);
        mPane.updateTextObj(gProgramArgsItem.getArgumentName(),3);
        mPane.updateTextObj(Integer.toString(gProgramArgsItem.getArgumentPosition()),4);
        mPane.updateTextObj(gProgramArgsItem.getArgumentType(),5);
        mPane.updateTextObj(gProgramArgsItem.getMetadataAttribute(),6);
        mPane.updateTextObj(gProgramArgsItem.getDefaultValue(),7);
        mPane.updateTextObj(gProgramArgsItem.getOutArgument(),8);
    }

    private void refreshSchedule() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshSchedule");

        mPane.updateTextObj(gScheduleItem.getOwner(),1);
        mPane.updateTextObj(gScheduleItem.getScheduleName(),2);
        mPane.updateTextObj(gScheduleItem.getStartDate(),3);
        mPane.updateTextObj(gScheduleItem.getRepeatInterval(),4);
        mPane.updateTextObj(gScheduleItem.getEndDate(),5);
        mPane.updateTextAreaObj(gScheduleItem.getComments(),6);
        if (dataArea.getVersionNo() > 1) {
            mPane.updateTextObj(gScheduleItem.getScheduleType(),7);
            mPane.updateTextObj(gScheduleItem.getEventQueueOwner(),8);
            mPane.updateTextObj(gScheduleItem.getEventQueueName(),9);
            mPane.updateTextObj(gScheduleItem.getEventQueueAgent(),10);
            mPane.updateTextObj(gScheduleItem.getEventCondition(),11);

            if (dataArea.getVersionNo() > 3) {
                mPane.updateTextObj(gScheduleItem.getFileWatcherOwner(),12);
                mPane.updateTextObj(gScheduleItem.getFileWatcherName(),13);
            }
        }
    }

    private void refreshJobClass() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshJobClass");

        mPane.updateTextObj(gJobClassItem.getJobClassName(),1);
        mPane.updateTextObj(gJobClassItem.getResourceConsumerGroup(),2);
        mPane.updateTextObj(gJobClassItem.getService(),3);
        mPane.updateTextObj(gJobClassItem.getLoggingLevel(),4);
        mPane.updateTextObj(Integer.toString(gJobClassItem.getLogHistory()),5);
        mPane.updateTextAreaObj(gJobClassItem.getComments(),6);
    }

    private void refreshWindow() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshWindow");

        mPane.updateTextObj(gWindowItem.getWindowName(),1);
        mPane.updateTextObj(gWindowItem.getResourcePlan(),2);
        mPane.updateTextObj(gWindowItem.getScheduleOwner(),3);
        mPane.updateTextObj(gWindowItem.getScheduleName(),4);
        mPane.updateTextObj(gWindowItem.getStartDate(),5);
        mPane.updateTextObj(gWindowItem.getRepeatInterval(),6);
        mPane.updateTextObj(gWindowItem.getEndDate(),7);
        mPane.updateTextObj(gWindowItem.getDuration(),8);
        mPane.updateTextObj(gWindowItem.getWindowPriority(),9);
        mPane.updateTextObj(gWindowItem.getNextStartDate(),10);
        mPane.updateTextObj(gWindowItem.getLastStartDate(),11);
        mPane.updateTextObj(gWindowItem.getEnabled(),12);
        mPane.updateTextObj(gWindowItem.getActive(),13);
        mPane.updateTextAreaObj(gWindowItem.getComments(),14);
        if (dataArea.getVersionNo() > 1) {
            mPane.updateTextObj(gWindowItem.getScheduleType(),15);
            mPane.updateTextObj(gWindowItem.getManualOpenTime(),16);
            mPane.updateTextObj(gWindowItem.getManualDuration(),17);
        }

        if (gWindowItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshWindowGroup() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshWindowGroup");

        mPane.updateTextObj(gWindowGroupItem.getWindowGroupName(),1);
        mPane.updateTextObj(gWindowGroupItem.getEnabled(),2);
        mPane.updateTextObj(Integer.toString(gWindowGroupItem.getNumberOfWindows()),3);
        mPane.updateTextObj(gWindowGroupItem.getNextStartDate(),4);
        mPane.updateTextAreaObj(gWindowGroupItem.getComments(),5);

        if (gWindowGroupItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshChains() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshChains");

        mPane.updateTextObj(gChainsItem.getOwner(),1);
        mPane.updateTextObj(gChainsItem.getChainName(),2);
        mPane.updateTextObj(gChainsItem.getRuleSetOwner(),3);
        mPane.updateTextObj(gChainsItem.getRuleSetName(),4);
        mPane.updateTextObj(Integer.toString(gChainsItem.getNumberOfRules()),5);
        mPane.updateTextObj(Integer.toString(gChainsItem.getNumberOfSteps()),6);
        mPane.updateTextObj(gChainsItem.getEnabled(),7);
        mPane.updateTextObj(gChainsItem.getEvaluationInterval(),8);
        mPane.updateTextObj(gChainsItem.getUserRuleSet(),9);
        mPane.updateTextAreaObj(gChainsItem.getComments(),10);

        if (gChainsItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshChainSteps() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshChainSteps");

        mPane.updateTextObj(gChainStepsItem.getOwner(),1);
        mPane.updateTextObj(gChainStepsItem.getChainName(),2);
        mPane.updateTextObj(gChainStepsItem.getStepName(),3);
        mPane.updateTextObj(gChainStepsItem.getProgramOwner(),4);
        mPane.updateTextObj(gChainStepsItem.getProgramName(),5);
        mPane.updateTextObj(gChainStepsItem.getEventScheduleOwner(),6);
        mPane.updateTextObj(gChainStepsItem.getEventScheduleName(),7);
        mPane.updateTextObj(gChainStepsItem.getEventQueueOwner(),8);
        mPane.updateTextObj(gChainStepsItem.getEventQueueName(),9);
        mPane.updateTextObj(gChainStepsItem.getEventQueueAgent(),10);
        mPane.updateTextObj(gChainStepsItem.getEventCondition(),11);
        mPane.updateTextObj(gChainStepsItem.getSkip(),12);
        mPane.updateTextObj(gChainStepsItem.getPause(),13);
        mPane.updateTextObj(gChainStepsItem.getRestartOnRecovery(),14);
        mPane.updateTextObj(gChainStepsItem.getStepType(),15);
        mPane.updateTextObj(gChainStepsItem.getTimeOut(),16);
        if (dataArea.getVersionNo() > 2) {
            mPane.updateTextObj(gChainStepsItem.getCredentialOwner(),17);
            mPane.updateTextObj(gChainStepsItem.getCredentialName(),18);
            mPane.updateTextObj(gChainStepsItem.getDestination(),19);
            if (dataArea.getVersionNo() > 3) {
                mPane.updateTextObj(gChainStepsItem.getRestartOnFailure(),20);
            }
        }
    }

    private void refreshChainRules() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshChainRules");

        mPane.updateTextObj(gChainRulesItem.getOwner(),1);
        mPane.updateTextObj(gChainRulesItem.getChainName(),2);
        mPane.updateTextObj(gChainRulesItem.getRuleOwner(),3);
        mPane.updateTextObj(gChainRulesItem.getRuleName(),4);
        mPane.updateTextObj(gChainRulesItem.getConditions(),5);
        mPane.updateTextObj(gChainRulesItem.getAction(),6);
        mPane.updateTextAreaObj(gChainRulesItem.getComments(),7);
    }

    private void refreshGlobalAttributes() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshGlobalAttributes");

        mPane.updateTextObj(gGlobalAttributesItem.getAttributeName(),1);
        mPane.updateTextObj(gGlobalAttributesItem.getAttributeValue(),2);
    }

    private void refreshCredentials() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshCredentials");

        mPane.updateTextObj(gCredentialsItem.getOwner(),1);
        mPane.updateTextObj(gCredentialsItem.getCredentialName(),2);
        mPane.updateTextObj(gCredentialsItem.getUsername(),3);
        mPane.updateTextObj(gCredentialsItem.getDatabaseRole(),4);
        mPane.updateTextObj(gCredentialsItem.getWindowsDomain(),5);
        mPane.updateTextAreaObj(gCredentialsItem.getComments(),6);
    }

    private void refreshFileWatchers() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshFileWatchers");

        mPane.updateTextObj(gFileWatchersItem.getOwner(),1);
        mPane.updateTextObj(gFileWatchersItem.getFileWatcherName(),2);
        mPane.updateTextObj(gFileWatchersItem.getEnabled(),3);
        mPane.updateTextObj(gFileWatchersItem.getDestinationOwner(),4);
        mPane.updateTextObj(gFileWatchersItem.getDestination(),5);
        mPane.updateTextObj(gFileWatchersItem.getDirectoryPath(),6);
        mPane.updateTextObj(gFileWatchersItem.getFileName(),7);
        mPane.updateTextObj(gFileWatchersItem.getCredentialOwner(),8);
        mPane.updateTextObj(gFileWatchersItem.getCredentialName(),9);
        mPane.updateTextObj(Integer.toString(gFileWatchersItem.getMinFileSize()),10);
        mPane.updateTextObj(gFileWatchersItem.getSteadyStateDuration(),11);
        mPane.updateTextObj(gFileWatchersItem.getLastModifiedTime(),12);
        mPane.updateTextAreaObj(gFileWatchersItem.getComments(),13);

        if (gFileWatchersItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshNotifications() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshNotifications");

        mPane.updateTextObj(gNotificationsItem.getOwner(),1);
        mPane.updateTextObj(gNotificationsItem.getJobName(),2);
        mPane.updateTextObj(gNotificationsItem.getJobSubName(),3);
        mPane.updateTextObj(gNotificationsItem.getRecipient(),4);
        mPane.updateTextObj(gNotificationsItem.getSender(),5);
        mPane.updateTextObj(gNotificationsItem.getSubject(),6);
        mPane.updateTextObj(gNotificationsItem.getBody(),7);
        mPane.updateTextObj(gNotificationsItem.getFilterCondition(),8);
        mPane.updateTextObj(gNotificationsItem.getEvent(),9);
        mPane.updateTextObj(Integer.toString(gNotificationsItem.getEventFlag()),10);
    }

    private void refreshDbDests() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshDbDests");

        mPane.updateTextObj(gDbDestsItem.getOwner(),1);
        mPane.updateTextObj(gDbDestsItem.getDestinationName(),2);
        mPane.updateTextObj(gDbDestsItem.getConnectInfo(),3);
        mPane.updateTextObj(gDbDestsItem.getAgent(),4);
        mPane.updateTextObj(gDbDestsItem.getEnabled(),5);
        mPane.updateTextObj(gDbDestsItem.getRefsEnabled(),6);
        mPane.updateTextAreaObj(gDbDestsItem.getComment(),7);
        if (gDbDestsItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshExtDests() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshExtDests");

        mPane.updateTextObj(gExtDestsItem.getDestinationName(),1);
        mPane.updateTextObj(gExtDestsItem.getHostname(),2);
        mPane.updateTextObj(gExtDestsItem.getPort(),3);
        mPane.updateTextObj(gExtDestsItem.getIpAddress(),4);
        mPane.updateTextObj(gExtDestsItem.getEnabled(),5);
        mPane.updateTextAreaObj(gExtDestsItem.getComment(),6);
        if (gExtDestsItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshGroups() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshGroups");

        mPane.updateTextObj(gGroupItem.getOwner(),1);
        mPane.updateTextObj(gGroupItem.getGroupName(),2);
        mPane.updateTextObj(gGroupItem.getGroupType(),3);
        mPane.updateTextObj(gGroupItem.getEnabled(),4);
        mPane.updateTextObj(Integer.toString(gGroupItem.getNumberOfMembers()),5);
        mPane.updateTextAreaObj(gGroupItem.getComments(),6);

        if (gGroupItem.getEnabled().equals("TRUE")) {
            setEnableButtonFalse();
            setDisableButtonTrue();
        }
        else {
            setEnableButtonTrue();
            setDisableButtonFalse();
        }
    }

    private void refreshPlan() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshPlan");

        // System.out.println(" Point X1 - " + gPlanItem.getPlan());

        mPane.updateTextObj(Integer.toString(gPlanItem.getPlanId()),1);
        mPane.updateTextObj(gPlanItem.getPlan(),2);
        mPane.updateTextObj(Integer.toString(gPlanItem.getNumPlanDirectives()),3);
        mPane.updateTextObj(gPlanItem.getCpuMethod(),4);
        mPane.updateTextObj(gPlanItem.getMgmtMethod(),5);
        mPane.updateTextObj(gPlanItem.getActiveSession(),6);
        mPane.updateTextObj(gPlanItem.getParallelDegreeLimitMth(),7);
        mPane.updateTextObj(gPlanItem.getQueueingMth(),8);
        mPane.updateTextObj(gPlanItem.getSubPlan(),9);
        mPane.updateTextAreaObj(gPlanItem.getComments(),10);
        mPane.updateTextObj(gPlanItem.getStatus(),11);
        mPane.updateTextObj(gPlanItem.getMandatory(),12);

        for (int r2 = 13; r2 < 30; r2++) {
            mPane.updateTextObj(null, r2);
        }
        mPane.updateTextAreaObj(null, 30);

        mPane.setDataArea(dataArea);
        mPane.clearPlanDirective();

        // Populate the plan directive table.
        for (int r2 = 0; r2 < dataArea.PlanDirectiveSize(); r2++) {
            gPlanDirectiveItem = dataArea.getPlanDirective(r2);

            boolean mMatch = false;
            if (gPlanDirectiveItem.getPlan().equals(gPlanItem.getPlan()))
            {
                if ((gPlanDirectiveItem.getStatus() == null) ||
                    (gPlanItem.getStatus() == null))
                {
                    if ((gPlanDirectiveItem.getStatus() == null) &&
                        (gPlanItem.getStatus() == null))  mMatch = true;
                }
                else {
                    if (gPlanItem.getStatus().equals(gPlanDirectiveItem.getStatus()))
                        mMatch = true;
                }
                if (mMatch)
                {
                    // System.out.println(" Point X2 - " + gPlanDirectiveItem.getPlan() + "--" +
                    //        gPlanDirectiveItem.getGroup());
                    mPane.populatePlanDirectiveTable(gPlanDirectiveItem.getId(),
                                                 gPlanDirectiveItem.getGroup(),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P1()),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P2()),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P3()),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P4()),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P5()),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P6()),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P7()),
                                                 Integer.toString(gPlanDirectiveItem.getMgmt_P8()));
                }
            }
        }
    }

    private void refreshCdbPlan() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshCdbPlan");

        // System.out.println(" Point X1 - ");

        mPane.updateTextObj(gCdbPlanItem.getPlan(),2);
        mPane.updateTextAreaObj(gCdbPlanItem.getComments(),3);
        mPane.updateTextObj(gCdbPlanItem.getStatus(),4);
        mPane.updateTextObj(gCdbPlanItem.getMandatory(),5);

        for (int r2 = 6; r2 < 12; r2++) {
            if (r2 == 9) mPane.updateTextAreaObj(null, r2);
            else         mPane.updateTextObj(null, r2);
        }

        mPane.setDataArea(dataArea);
        mPane.clearCdbPlanDirective();

        // Populate the plan directive table.
        for (int r2 = 0; r2 < dataArea.CdbPlanDirectiveSize(); r2++) {
            gCdbPlanDirectiveItem = dataArea.getCdbPlanDirective(r2);

            boolean mMatch = false;
            if (gCdbPlanDirectiveItem.getPlan().equals(gCdbPlanItem.getPlan()))
            {
                if ((gCdbPlanDirectiveItem.getStatus() == null) ||
                    (gCdbPlanItem.getStatus() == null))
                {
                    if ((gCdbPlanDirectiveItem.getStatus() == null) &&
                        (gCdbPlanItem.getStatus() == null))  mMatch = true;
                }
                else {
                    if (gCdbPlanItem.getStatus().equals(gCdbPlanDirectiveItem.getStatus()))
                        mMatch = true;
                }

                if (mMatch)
                {
                    // System.out.println(" Point X2 - " + gPlanDirectiveItem.getPlan() + "--" +
                    //        gPlanDirectiveItem.getGroup());
                    mPane.populateCdbPlanDirectiveTable(gCdbPlanDirectiveItem.getId(),
                                                 gCdbPlanDirectiveItem.getPluggableDatabase(),
                                                 Integer.toString(gCdbPlanDirectiveItem.getShares()),
                                                 Integer.toString(gCdbPlanDirectiveItem.getUtilizationLimit()),
                                                 Integer.toString(gCdbPlanDirectiveItem.getParallelServerLimit()));
                }
            }
        }
    }


    private void refreshConsumerGroup() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshConsumerGroup");

        mPane.updateTextObj(gConsumerGroupItem.getConsumerGroup(),2);
        mPane.updateTextObj(gConsumerGroupItem.getCpuMethod(),3);
        mPane.updateTextAreaObj(gConsumerGroupItem.getComments(),6);
        mPane.updateTextObj(gConsumerGroupItem.getStatus(),8);
        mPane.updateTextObj(gConsumerGroupItem.getMandatory(),9);

        if (dataArea.getVersionNo() > 2) {
            mPane.updateTextObj(Integer.toString(gConsumerGroupItem.getConsumerGroupId()),1);
            mPane.updateTextObj(gConsumerGroupItem.getMgmtMethod(),4);
            mPane.updateTextObj(gConsumerGroupItem.getInternalUse(),5);
            mPane.updateTextObj(gConsumerGroupItem.getCategory(),7);
        }

        mPane.setDataArea(dataArea);
        mPane.clearGroupMapping();
        // Populate the group mapping table.
        for (int r2 = 0; r2 < dataArea.groupMappingsSize(); r2++) {
            gGroupMappingsItem = dataArea.getGroupMappings(r2);

            // System.out.println(" Point X1 - ");

            boolean mMatch = false;
            if (gGroupMappingsItem.getConsumerGroup().equals(gConsumerGroupItem.getConsumerGroup()))
            {

                if ((gGroupMappingsItem.getStatus() == null) ||
                    (gConsumerGroupItem.getStatus() == null))
                {
                    if ((gGroupMappingsItem.getStatus() == null) &&
                        (gConsumerGroupItem.getStatus() == null))  mMatch = true;
                }
                else {
                    if (gConsumerGroupItem.getStatus().equals(gGroupMappingsItem.getStatus()))
                        mMatch = true;
                }

                if (mMatch)
                {
                    mPane.populateGroupMappingTable(
                                                 gGroupMappingsItem.getAttribute(),
                                                 gGroupMappingsItem.getValue(),
                                                 gGroupMappingsItem.getStatus());
                }
            }
        }

        mPane.clearConsumerPriv();
        // Populate the consumer privilege table.
        for (int r2 = 0; r2 < dataArea.consumerPrivsSize(); r2++) {
            gConsumerPrivItem = dataArea.getConsumerPriv(r2);

            if (gConsumerPrivItem.getGrantedGroup().equals(gConsumerGroupItem.getConsumerGroup()))
            {
                mPane.populateConsumerPrivTable(
                                            gConsumerPrivItem.getGrantee(),
                                            gConsumerPrivItem.getGrantOption(),
                                            gConsumerPrivItem.getInitialGroup());
            }
        }
    }

    private void refreshMappingPriority() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshMappingPriority");

        mPane.clearMappingPriority();
        // Populate the mapping priority table.
        for (int r2 = 1; r2 < 11; r2++) {

            if (currentTree.equals(PENDING_AREA_TREE)) {
                gMappingPrioritiesItem = dataArea.getMappingPriority(r2, 0);
                mPane.populateMappingPriorityTable(
                                                 Integer.toString(gMappingPrioritiesItem.getPriority()),
                                                 gMappingPrioritiesItem.getAttribute(),
                                                 gMappingPrioritiesItem.getStatus());
            }
            if (currentTree.equals(RESOURCE_ROOT_TREE)) {
                gMappingPrioritiesItem = dataArea.getMappingPriority(r2, 1);
                mPane.populateMappingPriorityTable(
                                                 Integer.toString(gMappingPrioritiesItem.getPriority()),
                                                 gMappingPrioritiesItem.getAttribute(),
                                                 gMappingPrioritiesItem.getStatus());
            }
        }
        bottomPane.repaint();
    }

    private void refreshDataScreen(int m_ScreenId) {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.refreshDataScreen");

        boolean notFound = true;

        for (int r2 = 0; r2 < mScreen.sizeScreenObj(); r2++) {
            mPane = mScreen.getScreenObj(r2);

            if (m_ScreenId == mPane.getScreenId()) {

                bottomPane.getViewport().add( mPane.getTabbedPane() );
                bottomPane.repaint();

                mGlobalArea.setPane(mPane);
                SchedFile.setBackGroundColor(mGlobalArea.getScreenColor(mPane.getBgrndColour()));
                notFound = false;
                break;
            }
        }

        if (notFound) {
            bottomPane.getViewport().add( label );
        }
    }

    public int getCurrentScreenNo() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getCurrentScreenNo");

        return currentScreenNo;
    }
    public String getCurrentChain() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getCurrentChain");

        return gChainName;
    }
    public String getCurrentChainOwner() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getCurrentChainOwner");

        return gChainOwner;
    }
    public SchedDataNode getCurrentNode() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getCurrentNode");

        return CurrentNode;
    }
    public String getGroupName() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getGroupName");

        return gGroupName;
    }
    public String getWindowName() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getWindowName");

        return gWindowName;
    }
    public SchedDataArea getDataArea() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getDataArea");

        return dataArea;
    }

    public SchedDataNode getParentNode() {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.getParentNode");

        return ParentNode;
    }

    public static void main(String[] args)
    {
        if ( mDebug == 0) SchedFile.WriteDebugLine("Scheduler.main");

        //Create an instance of the application.
        Scheduler newFrame = new Scheduler();

        newFrame.setVisible(true);
    }
}

