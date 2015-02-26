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

import javax.swing.tree.*;

import java.sql.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.OracleConnection;

import java.util.*;

class SchedDataArea {

    public  static final String       PENDING_STATUS     = "PENDING";

    public  static  String[]  mapObjects = {"CLIENT_MACHINE", "CLIENT_OS_USER", "CLIENT_PROGRAM",
                                   "EXPLICIT","MODULE_NAME","MODULE_NAME_ACTION",
                                   "ORACLE_USER","SERVICE_MODULE","SERVICE_MODULE_ACTION",
                                   "SERVICE_NAME"};

    private int                    mSequenceNo;
    private int                    mConnectId;
    private boolean                mPendingArea;

    private String                 version;
    private int                    versionNo;

    private Connection             conn;
    private Properties             info;

    private SchedScreenArea.PaneObject.LabelItem         m_PaneItem;
    private SchedScreenArea.PaneObject.TextItem          m_TextItem;
    private SchedScreenArea.PaneObject.TextAreaItem      m_TextAreaItem;
    private SchedScreenArea.PaneObject.ColumnItem        m_ColumnItem;

    private SchedInpScreenArea.PaneObject.LabelItem      m_InpPaneItem;
    private SchedInpScreenArea.PaneObject.TextItem       m_InpTextItem;
    private SchedInpScreenArea.PaneObject.TextAreaItem   m_InpTextAreaItem;

    private Statement                            stmt;
    private ResultSet                            rset;

    private PreparedStatement                    getUserIdStmt;
    private PreparedStatement                    getDataStmt;
    private PreparedStatement                    getJobLogStmt;
    private PreparedStatement                    getItemStmt;
    private PreparedStatement                    getJobsRunningDataStmt;
    private PreparedStatement                    getChainsRunningDataStmt;
    private PreparedStatement                    insertErrorStmt;

    private ResultSet                            jlset;
    private ResultSet                            wlset;

    private JobItem                        m_JobItem;
    private JobArgsItem                    m_JobArgsItem;
    private ProgramItem                    m_ProgramItem;
    private ProgramArgsItem                m_ProgramArgsItem;
    private ScheduleItem                   m_ScheduleItem;
    private JobClassItem                   m_JobClassItem;
    private WindowItem                     m_WindowItem;
    private WindowGroupItem                m_WindowGroupItem;
    private WinGroupMembersItem            m_WinGroupMembersItem;
    private ChainsItem                     m_ChainsItem;
    private ChainStepsItem                 m_ChainStepsItem;
    private ChainRulesItem                 m_ChainRulesItem;
    private GlobalAttributesItem           m_GlobalAttributesItem;
    private CredentialsItem                m_CredentialsItem;
    private NotificationsItem              m_NotificationsItem;
    private JobDestsItem                   m_JobDestsItem;
    private GroupItem                      m_GroupItem;
    private ExtDestsItem                   m_ExtDestsItem;
    private DbDestsItem                    m_DbDestsItem;
    private DestsItem                      m_DestsItem;
    private FileWatchersItem               m_FileWatchersItem;
    private GroupMembersItem               m_GroupMembersItem;

    private PlanItem                       m_PlanItem;
    private ConsumerGroupItem              m_ConsumerGroupItem;
    private PlanDirectiveItem              m_PlanDirectiveItem;
    private MappingPrioritiesItem          m_MappingPrioritiesItem;
    private GroupMappingsItem              m_GroupMappingsItem;
    private ConsumerPrivItem               m_ConsumerPrivItem;
    private ConsumerGroupStatsItem         m_ConsumerGroupStatsItem;
    private SessionItem                    m_SessionItem;
    private CurrentPlanItem                m_CurrentPlanItem;
    private CdbPlanItem                    m_CdbPlanItem;
    private CdbPlanDirectiveItem           m_CdbPlanDirectiveItem;

    private final String           DEFAULT_JOB_CLASS  = "DEFAULT_JOB_CLASS";

    private String                 userName;
    private String                 userFullName;
    private String                 hostName;
    private String                 mHostName;
    private String                 mDatabaseName;

    private int                    m_UserId = 0;

    private int                    winWidth;
    private int                    winHeight;
    private int                    divALocation;
    private int                    divBLocation;

    private int                    mScreenId;
    private int                    mReturnNo;
    private int                    sysStatus;
    private StringBuffer           sysMessage;

    private int                    dbaNo;

    private java.sql.Timestamp     mSDate, mEDate, mNDate;


    public SchedDataArea(String  database_Name) {
        mDatabaseName = database_Name;
        mSequenceNo = 30000;
        versionNo = 0;

        JobsVector             = new Vector<JobItem>(50, 10);
        JobArgsVector          = new Vector<JobArgsItem>(50, 10);
        ProgramsVector         = new Vector<ProgramItem>(50, 10);
        ProgramArgsVector      = new Vector<ProgramArgsItem>(50, 10);
        SchedulesVector        = new Vector<ScheduleItem>(50, 10);
        JobClassesVector       = new Vector<JobClassItem>(50, 10);
        WindowsVector          = new Vector<WindowItem>(50, 10);
        WindowsGroupVector     = new Vector<WindowGroupItem>(50, 10);
        WinGroupMembersVector  = new Vector<WinGroupMembersItem>(50, 10);

        // Vectors required by Oracle 10.2 and above.
        ChainsVector           = new Vector<ChainsItem>(50, 10);
        ChainStepsVector       = new Vector<ChainStepsItem>(50, 10);
        ChainRulesVector       = new Vector<ChainRulesItem>(50, 10);
        GlobalAttributesVector = new Vector<GlobalAttributesItem>(10, 10);

        // Vectors required by Oracle 11.1 and above.
        CredentialsVector      = new Vector<CredentialsItem>(10, 10);

        // Vectors required by Oracle 11.2 and above.
        GroupsVector           = new Vector<GroupItem>(10, 10);
        GroupMembersVector     = new Vector<GroupMembersItem>(10, 10);
        FileWatchersVector     = new Vector<FileWatchersItem>(10, 10);
        NotificationsVector    = new Vector<NotificationsItem>(10, 10);
        JobDestsVector         = new Vector<JobDestsItem>(10, 10);
        DestsVector            = new Vector<DestsItem>(10, 10);
        DbDestsVector          = new Vector<DbDestsItem>(10, 10);
        ExtDestsVector         = new Vector<ExtDestsItem>(10, 10);

        // Vectors required by Resource Manager.
        ConsumerGroupStatsVector = new Vector<ConsumerGroupStatsItem>(10,5);
        SessionsVector           = new Vector<SessionItem>(50, 10);
        PlansVector              = new Vector<PlanItem>(50, 10);
        ConsumerGroupsVector     = new Vector<ConsumerGroupItem>(50,10);
        PlanDirectivesVector     = new Vector<PlanDirectiveItem>(50,10);
        MappingPrioritiesVector  = new Vector<MappingPrioritiesItem>(50,10);
        GroupMappingsVector      = new Vector<GroupMappingsItem>(50,10);
        ConsumerPrivsVector      = new Vector<ConsumerPrivItem>(50,10);
        CurrentPlansVector       = new Vector<CurrentPlanItem>(10,5);

        // Vectors required by CDB Resource Manager.
        CdbPlansVector           = new Vector<CdbPlanItem>(50, 10);
        CdbPlanDirectivesVector  = new Vector<CdbPlanDirectiveItem>(50,10);

        // Vectors created for the pop up screenCombo.
        comboObj               = new Vector<screenCombo>(40,10);
    }

    public void clearSchedulerVectors() {
        JobsVector.clear();
        JobArgsVector.clear();
        ProgramsVector.clear();
        ProgramArgsVector.clear();
        SchedulesVector.clear();
        JobClassesVector.clear();
        WindowsVector.clear();
        WindowsGroupVector.clear();
        WinGroupMembersVector.clear();

        if (getVersionNo() > 1) {
            ChainsVector.clear();
            ChainStepsVector.clear();
            ChainRulesVector.clear();
            GlobalAttributesVector.clear();
        }
        if (getVersionNo() > 2) {
            CredentialsVector.clear();
        }
        if (getVersionNo() > 3) {
            GroupsVector.clear();
            GroupMembersVector.clear();
            FileWatchersVector.clear();
            NotificationsVector.clear();
            JobDestsVector.clear();
            DestsVector.clear();
            DbDestsVector.clear();
            ExtDestsVector.clear();
        }

        comboObj.clear();

        mSequenceNo = 30000;
    }

    public void clearResourceVectors() {
        ConsumerGroupStatsVector.clear();
        SessionsVector.clear();
        PlansVector.clear();
        ConsumerGroupsVector.clear();
        PlanDirectivesVector.clear();
        MappingPrioritiesVector.clear();
        GroupMappingsVector.clear();
        ConsumerPrivsVector.clear();

        CdbPlansVector.clear();
        CdbPlanDirectivesVector.clear();

    }

    public void clearMappingPrioritiesVector() {
        MappingPrioritiesVector.clear();
    }
    public void clearConsumerGroupStatsVector() {
        ConsumerGroupStatsVector.clear();
    }
    public void clearSessionsVector() {
        SessionsVector.clear();
    }
    public void clearCurrentPlansVector() {
        CurrentPlansVector.clear();
    }

    public int getNextSeqNo() {
        mSequenceNo = mSequenceNo + 1;
        return mSequenceNo;
    }
    public int getConnectId() {
        return mConnectId;
    }
    public String getDatabaseName() {
        return mDatabaseName;
    }
    public void setConnectId(int connectId) {
        mConnectId = connectId;
    }

    public boolean isPendingArea(boolean mFullScan) {
        if (mFullScan) {
            boolean mtemp = false;
            for (int i = 0; i < ConsumerGroupsVector.size(); i++) {
                m_ConsumerGroupItem = ConsumerGroupsVector.get(i);
                if (m_ConsumerGroupItem.getStatus() != null) {
                    if (m_ConsumerGroupItem.getStatus().equals(PENDING_STATUS)) {
                        mtemp = true;
                        break;
                    }
                }
            }
            if (mtemp == false) {
                for (int i = 0; i < PlansVector.size(); i++) {
                    m_PlanItem = PlansVector.get(i);
                    if (m_PlanItem.getStatus() != null) {
                        if (m_PlanItem.getStatus().equals(PENDING_STATUS)) {
                            mtemp = true;
                            break;
                        }
                    }
                }
            }
            mPendingArea = mtemp;
        }
        return mPendingArea;
    }

    public boolean isPendingArea() {
        return mPendingArea;
    }

    public boolean addJob(JobItem m_JobItem) {
        return JobsVector.add(m_JobItem);
    }
    public JobItem getJob(int JobNo) {
        m_JobItem = JobsVector.get(JobNo);
        return m_JobItem;
    }

    public void removeJob(int mJobId) {
        for (int i = 0; i < JobsVector.size(); i++) {
            m_JobItem = JobsVector.get(i);
            if (m_JobItem.getId() == mJobId)
                    JobsVector.removeElementAt(i);
        }
    }

    public void setJobEnabled(int mJobId,
                              String mEnabled) {
        for (int i = 0; i < JobsVector.size(); i++) {
            m_JobItem = JobsVector.get(i);
            if (m_JobItem.getId() == mJobId) {
                m_JobItem.setEnabled(mEnabled);
                break;
            }
        }
    }

    public JobItem getJobId(int JobId) {
        for (int i = 0; i < JobsVector.size(); i++) {
            m_JobItem = JobsVector.get(i);
            if (m_JobItem.getId() == JobId) {
                break;
            }
        }
        return m_JobItem;
    }

    public int jobSize() {
        return JobsVector.size();
    }

    public int getNextJobId() {
        int mMaxId = 0;
        for (int i = 0; i < JobsVector.size(); i++) {
            m_JobItem = JobsVector.get(i);
            if (m_JobItem.getId() > mMaxId)
                mMaxId = m_JobItem.getId();
        }
        return mMaxId + 1;
    }
    public JobItem updateJobItem(
                       String  Owner,
                       String  JobName,
                       String  JobCreator,
                       String  ClientId,
                       String  GlobalUid,
                       String  ProgramOwner,
                       String  ProgramName,
                       String  JobType,
                       String  JobAction,
                       int     NumberOfArguments,
                       String  ScheduleOwner,
                       String  ScheduleName,
                       String  StartDate,
                       String  RepeatInterval,
                       String  EndDate,
                       String  JobClass,
                       String  Enabled,
                       String  AutoDrop,
                       String  Restartable,
                       String  State,
                       int     JobPriority,
                       int     RunCount,
                       int     MaxRuns,
                       int     FailureCount,
                       int     MaxFailures,
                       int     RetryCount,
                       String  LastStartDate,
                       String  LastRunDuration,
                       String  NextRunDate,
                       String  ScheduleLimit,
                       String  MaxRunDuration,
                       String  LoggingLevel,
                       String  StopOnWindowClose,
                       String  InstanceStickiness,
                       String  System,
                       int     JobWeight,
                       String  NlsEnv,
                       String  Source,
                       String  Destination,
                       String  Comments,
                       String  JobSubName,
                       String  ScheduleType,
                       String  EventQueueOwner,
                       String  EventQueueName,
                       String  EventQueueAgent,
                       String  EventCondition,
                       String  EventRule,
                       String  RaiseEvents,
                       String  JobStyle,
                       String  CredentialOwner,
                       String  CredentialName,
                       int     InstanceId,
                       String  DeferredDrop,
                       String  FileWatcherOwner,
                       String  FileWatcherName,
                       int     NumberOfDestinations,
                       String  DestinationOwner,
                       String  AllowRunsInRestrictedMode) {
        int mId = 0;
        for (int i = 0; i < JobsVector.size(); i++) {
            m_JobItem = JobsVector.get(i);
            if ((m_JobItem.getOwner().equals(Owner)) &&
                (m_JobItem.getJobName().equals(JobName))) {
                mId = m_JobItem.getId();

                m_JobItem = new JobItem(
                               mId,
                               Owner,
                               JobName,
                               JobCreator,
                               ClientId,
                               GlobalUid,
                               ProgramOwner,
                               ProgramName,
                               JobType,
                               JobAction,
                               NumberOfArguments,
                               ScheduleOwner,
                               ScheduleName,
                               StartDate,
                               RepeatInterval,
                               EndDate,
                               JobClass,
                               Enabled,
                               AutoDrop,
                               Restartable,
                               State,
                               JobPriority,
                               RunCount,
                               MaxRuns,
                               FailureCount,
                               MaxFailures,
                               RetryCount,
                               LastStartDate,
                               LastRunDuration,
                               NextRunDate,
                               ScheduleLimit,
                               MaxRunDuration,
                               LoggingLevel,
                               StopOnWindowClose,
                               InstanceStickiness,
                               System,
                               JobWeight,
                               NlsEnv,
                               Source,
                               Destination,
                               Comments,
                               JobSubName,
                               ScheduleType,
                               EventQueueOwner,
                               EventQueueName,
                               EventQueueAgent,
                               EventCondition,
                               EventRule,
                               RaiseEvents,
                               JobStyle,
                               CredentialOwner,
                               CredentialName,
                               InstanceId,
                               DeferredDrop,
                               FileWatcherOwner,
                               FileWatcherName,
                               NumberOfDestinations,
                               DestinationOwner,
                               AllowRunsInRestrictedMode);

                JobsVector.remove(i);

                JobsVector.add(m_JobItem);

                break;
            }
        }
        return m_JobItem;
    }


    class JobItem {

        public JobItem(int     Id,
                       String  Owner,
                       String  JobName,
                       String  JobCreator,
                       String  ClientId,
                       String  GlobalUid,
                       String  ProgramOwner,
                       String  ProgramName,
                       String  JobType,
                       String  JobAction,
                       int     NumberOfArguments,
                       String  ScheduleOwner,
                       String  ScheduleName,
                       String  StartDate,
                       String  RepeatInterval,
                       String  EndDate,
                       String  JobClass,
                       String  Enabled,
                       String  AutoDrop,
                       String  Restartable,
                       String  State,
                       int     JobPriority,
                       int     RunCount,
                       int     MaxRuns,
                       int     FailureCount,
                       int     MaxFailures,
                       int     RetryCount,
                       String  LastStartDate,
                       String  LastRunDuration,
                       String  NextRunDate,
                       String  ScheduleLimit,
                       String  MaxRunDuration,
                       String  LoggingLevel,
                       String  StopOnWindowClose,
                       String  InstanceStickiness,
                       String  System,
                       int     JobWeight,
                       String  NlsEnv,
                       String  Source,
                       String  Destination,
                       String  Comments,
                       String  JobSubName,
                       String  ScheduleType,
                       String  EventQueueOwner,
                       String  EventQueueName,
                       String  EventQueueAgent,
                       String  EventCondition,
                       String  EventRule,
                       String  RaiseEvents,
                       String  JobStyle,
                       String  CredentialOwner,
                       String  CredentialName,
                       int     InstanceId,
                       String  DeferredDrop,
                       String  FileWatcherOwner,
                       String  FileWatcherName,
                       int     NumberOfDestinations,
                       String  DestinationOwner,
                       String  AllowRunsInRestricterMode) {

            m_Id = Id;
            m_Owner = Owner;
            m_JobName = JobName;
            m_JobCreator = JobCreator;
            m_ClientId = ClientId;
            m_GlobalUid = GlobalUid;
            m_ProgramOwner = ProgramOwner;
            m_ProgramName = ProgramName;
            m_JobType = JobType;
            m_JobAction = JobAction;
            m_NumberOfArguments = NumberOfArguments;
            m_ScheduleOwner = ScheduleOwner;
            m_ScheduleName = ScheduleName;
            m_StartDate = StartDate;
            m_RepeatInterval = RepeatInterval;
            m_EndDate = EndDate;
            m_JobClass = JobClass;
            m_Enabled = Enabled;
            m_AutoDrop = AutoDrop;
            m_Restartable = Restartable;
            m_State = State;
            m_JobPriority = JobPriority;
            m_RunCount = RunCount;
            m_MaxRuns = MaxRuns;
            m_FailureCount = FailureCount;
            m_MaxFailures = MaxFailures;
            m_RetryCount = RetryCount;
            m_LastStartDate = LastStartDate;
            m_LastRunDuration = LastRunDuration;
            m_NextRunDate = NextRunDate;
            m_ScheduleLimit = ScheduleLimit;
            m_MaxRunDuration = MaxRunDuration;
            m_LoggingLevel = LoggingLevel;
            m_StopOnWindowClose = StopOnWindowClose;
            m_InstanceStickiness = InstanceStickiness;
            m_System = System;
            m_JobWeight = JobWeight;
            m_NlsEnv = NlsEnv;
            m_Source = Source;
            m_Destination = Destination;
            m_Comments = Comments;

            m_JobSubName = JobSubName;
            m_ScheduleType = ScheduleType;
            m_EventQueueOwner = EventQueueOwner;
            m_EventQueueName = EventQueueName;
            m_EventQueueAgent = EventQueueAgent;
            m_EventCondition = EventCondition;
            m_EventRule = EventRule;
            m_RaiseEvents = RaiseEvents;
            m_JobStyle = JobStyle;
            m_CredentialOwner = CredentialOwner;
            m_CredentialName = CredentialName;
            m_InstanceId = InstanceId;
            m_DeferredDrop = DeferredDrop;
            m_FileWatcherOwner = FileWatcherOwner;
            m_FileWatcherName = FileWatcherName;
            m_NumberOfDestinations = NumberOfDestinations;
            m_DestinationOwner = DestinationOwner;
            m_AllowRunsInRestricterMode = AllowRunsInRestricterMode;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getJobName() {
            return m_JobName;
        }
        public String getJobCreator() {
            return m_JobCreator;
        }
        public String getClientId() {
            return m_ClientId;
        }
        public String getGlobalUid() {
            return m_GlobalUid;
        }
        public String getProgramOwner() {
            return m_ProgramOwner;
        }
        public String getProgramName() {
            return m_ProgramName;
        }
        public String getJobType() {
            return m_JobType;
        }
        public String getJobAction() {
            return m_JobAction;
        }
        public int getNumberOfArguments() {
            return m_NumberOfArguments;
        }
        public String getScheduleOwner() {
            return m_ScheduleOwner;
        }
        public String getScheduleName() {
            return m_ScheduleName;
        }
        public String getStartDate() {
            return m_StartDate;
        }
        public String getRepeatInterval() {
            return m_RepeatInterval;
        }
        public String getEndDate() {
            return m_EndDate;
        }
        public String getJobClass() {
            return m_JobClass;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getAutoDrop() {
            return m_AutoDrop;
        }
        public String getRestartable() {
            return m_Restartable;
        }
        public String getState() {
            return m_State;
        }
        public int getJobPriority() {
            return m_JobPriority;
        }
        public int getRunCount() {
            return m_RunCount;
        }
        public int getMaxRuns() {
            return m_MaxRuns;
        }
        public int getFailureCount() {
            return m_FailureCount;
        }
        public int getMaxFailures() {
            return m_MaxFailures;
        }
        public int getRetryCount() {
            return m_RetryCount;
        }
        public String getLastStartDate() {
            return m_LastStartDate;
        }
        public String getLastRunDuration() {
            return m_LastRunDuration;
        }
        public String getNextRunDate() {
            return m_NextRunDate;
        }
        public String getScheduleLimit() {
            return m_ScheduleLimit;
        }
        public String getMaxRunDuration() {
            return m_MaxRunDuration;
        }
        public String getLoggingLevel() {
            return m_LoggingLevel;
        }
        public String getStopOnWindowClose() {
            return m_StopOnWindowClose;
        }
        public String getInstanceStickiness() {
            return m_InstanceStickiness;
        }

        public String getSystem() {
            return m_System;
        }
        public int getJobWeight() {
            return m_JobWeight;
        }
        public String getNlsEnv() {
            return m_NlsEnv;
        }
        public String getSource() {
            return m_Source;
        }
        public String getDestination() {
            return m_Destination;
        }
        public String getComments() {
            return m_Comments;
        }
        public String getJobSubName() {
            return m_JobSubName;
        }
        public String getScheduleType() {
            return m_ScheduleType;
        }
        public String getEventQueueOwner() {
            return m_EventQueueOwner;
        }
        public String getEventQueueName() {
            return m_EventQueueName;
        }
        public String getEventQueueAgent() {
            return m_EventQueueAgent;
        }
        public String getEventCondition() {
            return m_EventCondition;
        }
        public String getEventRule() {
            return m_EventRule;
        }
        public String getRaiseEvents() {
            return m_RaiseEvents;
        }
        public String getJobStyle() {
            return m_JobStyle;
        }
        public String getCredentialOwner() {
            return m_CredentialOwner;
        }
        public String getCredentialName() {
            return m_CredentialName;
        }
        public int getInstanceId() {
            return m_InstanceId;
        }
        public String getDeferredDrop() {
            return m_DeferredDrop;
        }
        public String getFileWatcherOwner() {
            return m_FileWatcherOwner;
        }
        public String getFileWatcherName() {
            return m_FileWatcherName;
        }
        public int getNumberOfDestinations() {
            return m_NumberOfDestinations;
        }
        public String getDestinationOwner() {
            return m_DestinationOwner;
        }
        public String getAllowRunsInRestricterMode() {
            return m_AllowRunsInRestricterMode;
        }

        public void setEnabled(String mEnabled) {
            m_Enabled = mEnabled;
        }

        private int    m_Id;
        private String m_Owner;
        private String m_JobName;
        private String m_JobCreator;
        private String m_ClientId;
        private String m_GlobalUid;
        private String m_ProgramOwner;
        private String m_ProgramName;
        private String m_JobType;
        private String m_JobAction;
        private int    m_NumberOfArguments;
        private String m_ScheduleOwner;
        private String m_ScheduleName;
        private String m_StartDate;
        private String m_RepeatInterval;
        private String m_EndDate;
        private String m_JobClass;
        private String m_Enabled;
        private String m_AutoDrop;
        private String m_Restartable;
        private String m_State;
        private int    m_JobPriority;
        private int    m_RunCount;
        private int    m_MaxRuns;
        private int    m_FailureCount;
        private int    m_MaxFailures;
        private int    m_RetryCount;
        private String m_LastStartDate;
        private String m_LastRunDuration;
        private String m_NextRunDate;
        private String m_ScheduleLimit;
        private String m_MaxRunDuration;
        private String m_LoggingLevel;
        private String m_StopOnWindowClose;
        private String m_InstanceStickiness;
        private String m_System;
        private int    m_JobWeight;
        private String m_NlsEnv;
        private String m_Source;
        private String m_Destination;
        private String m_Comments;

        private String m_JobSubName;
        private String m_ScheduleType;
        private String m_EventQueueOwner;
        private String m_EventQueueName;
        private String m_EventQueueAgent;
        private String m_EventCondition;
        private String m_EventRule;
        private String m_RaiseEvents;
        private String m_JobStyle;
        private String m_CredentialOwner;
        private String m_CredentialName;
        private int    m_InstanceId;
        private String m_DeferredDrop;
        private String m_FileWatcherOwner;
        private String m_FileWatcherName;
        private int    m_NumberOfDestinations;
        private String m_DestinationOwner;
        private String m_AllowRunsInRestricterMode;

    }

    public boolean addJobArgs(JobArgsItem m_JobArgsItem) {
        return JobArgsVector.add(m_JobArgsItem);
    }

    public JobArgsItem getJobArgs(int JobArgsNo) {
        m_JobArgsItem = JobArgsVector.get(JobArgsNo);
        return m_JobArgsItem;
    }
    public boolean jobArgExists(
                           String  Owner,
                           String  JobName,
                           int     ArgumentPosition) {
        for (int i = 0; i < JobArgsVector.size(); i++) {
            m_JobArgsItem = JobArgsVector.get(i);
            if ((m_JobArgsItem.getOwner().equals(Owner)) &&
                (m_JobArgsItem.getJobName().equals(JobName)) &&
                (m_JobArgsItem.getArgumentPosition() == ArgumentPosition)) {
                return true;
            }
        }
        return false;
    }
    public JobArgsItem getJobArgsId(int JobArgsId) {
        for (int i = 0; i < JobArgsVector.size(); i++) {
            m_JobArgsItem = JobArgsVector.get(i);
            if (m_JobArgsItem.getId() == JobArgsId) {
                break;
            }
        }
        return m_JobArgsItem;
    }

    public void removeJobArg(int mJobArgId) {
        for (int i = 0; i < JobArgsVector.size(); i++) {
            m_JobArgsItem = JobArgsVector.get(i);
            if (m_JobArgsItem.getId() == mJobArgId)
                    JobArgsVector.removeElementAt(i);
        }
    }
    public int jobArgsSize() {
        return JobArgsVector.size();
    }
    public int getNextJobArgId() {
        int mMaxId = 0;
        for (int i = 0; i < JobArgsVector.size(); i++) {
            m_JobArgsItem = JobArgsVector.get(i);
            if (m_JobArgsItem.getId() > mMaxId)
                mMaxId = m_JobArgsItem.getId();
        }
        return mMaxId + 1;
    }
    public JobArgsItem updateJobArgsItem(
                           String  Owner,
                           String  JobName,
                           String  ArgumentName,
                           int     ArgumentPosition,
                           String  ArgumentType,
                           String  Value,
                           String  OutArgument) {
        int mId = 0;
        for (int i = 0; i < JobArgsVector.size(); i++) {
            m_JobArgsItem = JobArgsVector.get(i);
            if ((m_JobArgsItem.getOwner().equals(Owner)) &&
                (m_JobArgsItem.getJobName().equals(JobName)) &&
                (m_JobArgsItem.getArgumentPosition() == ArgumentPosition))
            {

                mId = m_JobArgsItem.getId();

                m_JobArgsItem = new JobArgsItem(
                        mId,
                        Owner,
                        JobName,
                        ArgumentName,
                        ArgumentPosition,
                        ArgumentType,
                        Value,
                        OutArgument);

                JobArgsVector.remove(i);

                JobArgsVector.add(m_JobArgsItem);

                break;
            }
        }
        return m_JobArgsItem;
    }

    class JobArgsItem {

        public JobArgsItem(int     Id,
                           String  Owner,
                           String  JobName,
                           String  ArgumentName,
                           int     ArgumentPosition,
                           String  ArgumentType,
                           String  Value,
                           String  OutArgument) {
            m_Id = Id;
            m_Owner = Owner;
            m_JobName = JobName;
            m_ArgumentName = ArgumentName;
            m_ArgumentPosition = ArgumentPosition;
            m_ArgumentType = ArgumentType;
            m_Value = Value;
            m_OutArgument = OutArgument;

        }
        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getJobName() {
            return m_JobName;
        }
        public String getArgumentName() {
            return m_ArgumentName;
        }
        public int getArgumentPosition() {
            return m_ArgumentPosition;
        }
        public String getArgumentType() {
            return m_ArgumentType;
        }
        public String getValue() {
            return m_Value;
        }
        public String getOutArgument() {
            return m_OutArgument;
        }

        private int      m_Id;
        private String   m_Owner;
        private String   m_JobName;
        private String   m_ArgumentName;
        private int      m_ArgumentPosition;
        private String   m_ArgumentType;
        private String   m_Value;
        private String   m_OutArgument;
    }

    public boolean addProgram(ProgramItem m_ProgramItem) {
        return ProgramsVector.add(m_ProgramItem);
    }

    public ProgramItem getProgram(int ProgramNo) {
        m_ProgramItem = ProgramsVector.get(ProgramNo);
        return m_ProgramItem;
    }

    public void removeProgram(int mProgramId) {
        for (int i = 0; i < ProgramsVector.size(); i++) {
            m_ProgramItem = ProgramsVector.get(i);
            if (m_ProgramItem.getId() == mProgramId)
                    ProgramsVector.removeElementAt(i);
        }
    }

    public ProgramItem getProgramId(int ProgramId) {
        for (int i = 0; i < ProgramsVector.size(); i++) {
            m_ProgramItem = ProgramsVector.get(i);
            if (m_ProgramItem.getId() == ProgramId) {
                break;
            }
        }
        return m_ProgramItem;
    }
    public void setProgramEnabled(int mProgramId,
                                  String mEnabled) {
        for (int i = 0; i < ProgramsVector.size(); i++) {
            m_ProgramItem = ProgramsVector.get(i);
            if (m_ProgramItem.getId() == mProgramId) {
                m_ProgramItem.setEnabled(mEnabled);
                break;
            }
        }
    }

    public int programSize() {
        return ProgramsVector.size();
    }

    public int getNextProgramId() {
        int mMaxId = 0;
        for (int i = 0; i < ProgramsVector.size(); i++) {
            m_ProgramItem = ProgramsVector.get(i);
            if (m_ProgramItem.getId() > mMaxId)
                mMaxId = m_ProgramItem.getId();
        }
        return mMaxId + 1;
    }
    public ProgramItem updateProgramItem(String  Owner,
                                 String  ProgramName,
                                 String  ProgramType,
                                 String  ProgramAction,
                                 int     NumberOfArguments,
                                 String  Enabled,
                                 String  Comments,
                                 String  Detached,
                                 String  ScheduleLimit,
                                 int     Priority,
                                 int     Weight,
                                 int     MaxRuns,
                                 int     MaxFailures,
                                 String  MaxRunDuration,
                                 String  NlsEnv) {

        int mId = 0;
        for (int i = 0; i < ProgramsVector.size(); i++) {
            m_ProgramItem = ProgramsVector.get(i);
            if ((m_ProgramItem.getOwner().equals(Owner)) &&
                (m_ProgramItem.getProgramName().equals(ProgramName))) {
                mId = m_ProgramItem.getId();

                m_ProgramItem = new ProgramItem(
                        mId,
                        Owner,
                        ProgramName,
                        ProgramType,
                        ProgramAction,
                        NumberOfArguments,
                        Enabled,
                        Comments,
                        Detached,
                        ScheduleLimit,
                        Priority,
                        Weight,
                        MaxRuns,
                        MaxFailures,
                        MaxRunDuration,
                        NlsEnv);

                ProgramsVector.remove(i);

                ProgramsVector.add(m_ProgramItem);

                break;
            }
        }
        return m_ProgramItem;
    }

        private String m_ScheduleLimit;
        private int    m_Priority;
        private int    m_weight;
        private int    m_MaxRuns;
        private int    m_MaxFailures;
        private String m_MaxRunDuration;
        private String m_NlsEnv;


    class ProgramItem {
        public ProgramItem(int    Id,
                           String Owner,
                           String ProgramName,
                           String ProgramType,
                           String ProgramAction,
                           int    NumberOfArguments,
                           String Enabled,
                           String Comments,
                           String Detached,
                           String ScheduleLimit,
                           int    Priority,
                           int    Weight,
                           int    MaxRuns,
                           int    MaxFailures,
                           String MaxRunDuration,
                           String NlsEnv) {
            m_Id = Id;
            m_Owner = Owner;
            m_ProgramName = ProgramName;
            m_ProgramType = ProgramType;
            m_ProgramAction = ProgramAction;
            m_NumberOfArguments = NumberOfArguments;
            m_Enabled = Enabled;
            m_Comments = Comments;
            m_Detached = Detached;
            m_ScheduleLimit = ScheduleLimit;
            m_Priority = Priority;
            m_Weight = Weight;
            m_MaxRuns = MaxRuns;
            m_MaxFailures = MaxFailures;
            m_MaxRunDuration = MaxRunDuration;
            m_NlsEnv = NlsEnv;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getProgramName() {
            return m_ProgramName;
        }
        public String getProgramType() {
            return m_ProgramType;
        }
        public int getNumberOfArguments() {
            return m_NumberOfArguments;
        }
        public String getProgramAction() {
            return m_ProgramAction;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getComments() {
            return m_Comments;
        }
        public String getDetached() {
            return m_Detached;
        }
        public String getScheduleLimit() {
            return m_ScheduleLimit;
        }
        public int getPriority() {
            return m_Priority;
        }
        public int getWeight() {
            return m_Weight;
        }
        public int getMaxRuns() {
            return m_MaxRuns;
        }
        public int getMaxFailures() {
            return m_MaxFailures;
        }
        public String getMaxRunDuration() {
            return m_MaxRunDuration;
        }
        public String getNlsEnv() {
            return m_NlsEnv;
        }

        public void setEnabled(String mEnabled) {
            m_Enabled = mEnabled;
        }

        private int    m_Id;
        private String m_Owner;
        private String m_ProgramName;
        private String m_ProgramType;
        private String m_ProgramAction;
        private int    m_NumberOfArguments;
        private String m_Enabled;
        private String m_Comments;
        private String m_Detached;
        private String m_ScheduleLimit;
        private int    m_Priority;
        private int    m_Weight;
        private int    m_MaxRuns;
        private int    m_MaxFailures;
        private String m_MaxRunDuration;
        private String m_NlsEnv;
    }

    public boolean addProgramArgs(ProgramArgsItem m_ProgramArgsItem) {
        return ProgramArgsVector.add(m_ProgramArgsItem);
    }

    public ProgramArgsItem getProgramArgs(int ProgramArgsNo) {
        m_ProgramArgsItem = ProgramArgsVector.get(ProgramArgsNo);
        return m_ProgramArgsItem;
    }
    public void removeProgramArg(int mProgramArgId) {
        for (int i = 0; i < ProgramArgsVector.size(); i++) {
            m_ProgramArgsItem = ProgramArgsVector.get(i);
            if (m_ProgramArgsItem.getId() == mProgramArgId)
                    ProgramArgsVector.removeElementAt(i);
        }
    }
    public ProgramArgsItem getProgramArgsId(int ProgramArgsId) {
        for (int i = 0; i < ProgramArgsVector.size(); i++) {
            m_ProgramArgsItem = ProgramArgsVector.get(i);
            if (m_ProgramArgsItem.getId() == ProgramArgsId) {
                break;
            }
        }
        return m_ProgramArgsItem;
    }

    public int programArgsSize() {
        return ProgramArgsVector.size();
    }

    public int getNextProgramArgId() {
        int mMaxId = 0;
        for (int i = 0; i < ProgramArgsVector.size(); i++) {
            m_ProgramArgsItem = ProgramArgsVector.get(i);
            if (m_ProgramArgsItem.getId() > mMaxId)
                mMaxId = m_ProgramArgsItem.getId();
        }
        return mMaxId + 1;
    }
    public ProgramArgsItem updateProgramArgsItem(
                               String Owner,
                               String ProgramName,
                               String ArgumentName,
                               int    ArgumentPosition,
                               String ArgumentType,
                               String MetaDataAttribute,
                               String DefaultValue,
                               String OutArgument) {
        int mId = 0;
        for (int i = 0; i < ProgramArgsVector.size(); i++) {
            m_ProgramArgsItem = ProgramArgsVector.get(i);
            if ((m_ProgramArgsItem.getOwner().equals(Owner)) &&
                (m_ProgramArgsItem.getProgramName().equals(ProgramName)) &&
                (m_ProgramArgsItem.getArgumentPosition() == ArgumentPosition))
            {

                mId = m_ProgramArgsItem.getId();

                m_ProgramArgsItem = new ProgramArgsItem(
                            mId,
                            Owner,
                            ProgramName,
                            ArgumentName,
                            ArgumentPosition,
                            ArgumentType,
                            MetaDataAttribute,
                            DefaultValue,
                            OutArgument);

                ProgramArgsVector.remove(i);

                ProgramArgsVector.add(m_ProgramArgsItem);

                break;
            }
        }
        return m_ProgramArgsItem;
    }

    class ProgramArgsItem {

        public ProgramArgsItem(int    Id,
                               String Owner,
                               String ProgramName,
                               String ArgumentName,
                               int ArgumentPosition,
                               String ArgumentType,
                               String MetaDataAttribute,
                               String DefaultValue,
                               String OutArgument) {
            m_Id = Id;
            m_Owner = Owner;
            m_ProgramName = ProgramName;
            m_ArgumentName = ArgumentName;
            m_ArgumentPosition = ArgumentPosition;
            m_ArgumentType = ArgumentType;
            m_MetaDataAttribute = MetaDataAttribute;
            m_DefaultValue = DefaultValue;
            m_OutArgument = OutArgument;

        }
        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getProgramName() {
            return m_ProgramName;
        }
        public String getArgumentName() {
            return m_ArgumentName;
        }
        public int getArgumentPosition() {
            return m_ArgumentPosition;
        }
        public String getArgumentType() {
            return m_ArgumentType;
        }
        public String getMetadataAttribute() {
            return m_MetaDataAttribute;
        }
        public String getDefaultValue() {
            return m_DefaultValue;
        }
        public String getOutArgument() {
            return m_OutArgument;
        }

        private int       m_Id;
        private String    m_Owner;
        private String    m_ProgramName;
        private String    m_ArgumentName;
        private int       m_ArgumentPosition;
        private String    m_ArgumentType;
        private String    m_MetaDataAttribute;
        private String    m_DefaultValue;
        private String    m_OutArgument;

    }

    public boolean addSchedule(ScheduleItem m_ScheduleItem) {
        return SchedulesVector.add(m_ScheduleItem);
    }

    public ScheduleItem getSchedule(int ScheduleNo) {
        m_ScheduleItem = SchedulesVector.get(ScheduleNo);
        return m_ScheduleItem;
    }

    public ScheduleItem getSchedule(String mOwner, String mScheduleName) {
        for (int i = 0; i < SchedulesVector.size(); i++) {
            m_ScheduleItem = SchedulesVector.get(i);
            if (m_ScheduleItem.getOwner().equals(mOwner) &&
                m_ScheduleItem.getScheduleName().equals(mScheduleName)) {
                break;
            }
        }
        return m_ScheduleItem;
    }

    public void removeSchedule(int mScheduleId) {
        for (int i = 0; i < SchedulesVector.size(); i++) {
            m_ScheduleItem = SchedulesVector.get(i);
            if (m_ScheduleItem.getId() == mScheduleId)
                    SchedulesVector.removeElementAt(i);
        }
    }

    public ScheduleItem getScheduleId(int ScheduleId) {
        for (int i = 0; i < SchedulesVector.size(); i++) {
            m_ScheduleItem = SchedulesVector.get(i);
            if (m_ScheduleItem.getId() == ScheduleId) {
                break;
            }
        }
        return m_ScheduleItem;
    }

    public int scheduleSize() {
        return SchedulesVector.size();
    }

    public int getNextScheduleId() {
        int mMaxId = 0;
        for (int i = 0; i < SchedulesVector.size(); i++) {
            m_ScheduleItem = SchedulesVector.get(i);
            if (m_ScheduleItem.getId() > mMaxId)
                mMaxId = m_ScheduleItem.getId();
        }
        return mMaxId + 1;
    }
    public ScheduleItem updateScheduleItem(String Owner,
                                  String ScheduleName,
                                  String StartDate,
                                  String RepeatInterval,
                                  String EndDate,
                                  String Comments,
                                  String ScheduleType,
                                  String EventQueueOwner,
                                  String EventQueueName,
                                  String EventQueueAgent,
                                  String EventCondition,
                                  String FileWatcherOwner,
                                  String FileWatcherName) {
        int mId = 0;
        for (int i = 0; i < SchedulesVector.size(); i++) {
            m_ScheduleItem = SchedulesVector.get(i);
            if ((m_ScheduleItem.getOwner().equals(Owner)) &&
                (m_ScheduleItem.getScheduleName().equals(ScheduleName))) {
                mId = m_ScheduleItem.getId();

                m_ScheduleItem = new ScheduleItem(
                    mId,
                    Owner,
                    ScheduleName,
                    StartDate,
                    RepeatInterval,
                    EndDate,
                    Comments,
                    ScheduleType,
                    EventQueueOwner,
                    EventQueueName,
                    EventQueueAgent,
                    EventCondition,
                    FileWatcherOwner,
                    FileWatcherName);

                SchedulesVector.remove(i);

                SchedulesVector.add(m_ScheduleItem);

                break;
            }
        }
        return m_ScheduleItem;
    }

    class ScheduleItem {

        public ScheduleItem(int    Id,
                            String Owner,
                            String ScheduleName,
                            String StartDate,
                            String RepeatInterval,
                            String EndDate,
                            String Comments,
                            String ScheduleType,
                            String EventQueueOwner,
                            String EventQueueName,
                            String EventQueueAgent,
                            String EventCondition,
                            String FileWatcherOwner,
                            String FileWatcherName) {
            m_Id = Id;
            m_Owner = Owner;
            m_ScheduleName = ScheduleName;
            m_StartDate = StartDate;
            m_RepeatInterval = RepeatInterval;
            m_EndDate = EndDate;
            m_Comments = Comments;
            m_ScheduleType = ScheduleType;
            m_EventQueueOwner = EventQueueOwner;
            m_EventQueueName = EventQueueName;
            m_EventQueueAgent = EventQueueAgent;
            m_EventCondition = EventCondition;
            m_FileWatcherOwner = FileWatcherOwner;
            m_FileWatcherName = FileWatcherName;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getScheduleName() {
            return m_ScheduleName;
        }
        public String getStartDate() {
            return m_StartDate;
        }
        public String getRepeatInterval() {
            return m_RepeatInterval;
        }
        public String getEndDate() {
            return m_EndDate;
        }
        public String getComments() {
            return m_Comments;
        }
        public String getScheduleType() {
            return m_ScheduleType;
        }
        public String getEventQueueOwner() {
            return m_EventQueueOwner;
        }
        public String getEventQueueName() {
            return m_EventQueueName;
        }
        public String getEventQueueAgent() {
            return m_EventQueueAgent;
        }
        public String getEventCondition() {
            return m_EventCondition;
        }
        public String getFileWatcherOwner() {
            return m_FileWatcherOwner;
        }
        public String getFileWatcherName() {
            return m_FileWatcherName;
        }

        private int    m_Id;
        private String m_Owner;
        private String m_ScheduleName;
        private String m_StartDate;
        private String m_RepeatInterval;
        private String m_EndDate;
        private String m_Comments;
        private String m_ScheduleType;
        private String m_EventQueueOwner;
        private String m_EventQueueName;
        private String m_EventQueueAgent;
        private String m_EventCondition;
        private String m_FileWatcherOwner;
        private String m_FileWatcherName;
    }

    public boolean addJobClass(JobClassItem m_JobClassItem) {
        return JobClassesVector.add(m_JobClassItem);
    }

    public JobClassItem getJobClass(int JobClassNo) {
        m_JobClassItem = JobClassesVector.get(JobClassNo);
        return m_JobClassItem;
    }

    public void removeJobClass(int mJobClassId) {
        for (int i = 0; i < JobClassesVector.size(); i++) {
            m_JobClassItem = JobClassesVector.get(i);
            if (m_JobClassItem.getId() == mJobClassId)
                    JobClassesVector.removeElementAt(i);
        }
    }

    public JobClassItem getJobClassId(int JobClassId) {
        for (int i = 0; i < JobClassesVector.size(); i++) {
            m_JobClassItem = JobClassesVector.get(i);
            if (m_JobClassItem.getId() == JobClassId) {
                break;
            }
        }
        return m_JobClassItem;
    }

    public int jobClassSize() {
        return JobClassesVector.size();
    }

    public int getNextJobClassId() {
        int mMaxId = 0;
        for (int i = 0; i < JobClassesVector.size(); i++) {
            m_JobClassItem = JobClassesVector.get(i);
            if (m_JobClassItem.getId() > mMaxId)
                mMaxId = m_JobClassItem.getId();
        }
        return mMaxId + 1;
    }
    public JobClassItem updateJobClassItem(String JobClassName,
                                   String ResourceConsumerGroup,
                                   String Service,
                                   String LoggingLevel,
                                   int    LogHistory,
                                   String Comments) {
        int mId = 0;
        for (int i = 0; i < JobClassesVector.size(); i++) {
            m_JobClassItem = JobClassesVector.get(i);
            if (m_JobClassItem.getJobClassName().equals(JobClassName)) {

                mId = m_JobClassItem.getId();

                m_JobClassItem = new JobClassItem(
                        mId,
                        JobClassName,
                        ResourceConsumerGroup,
                        Service,
                        LoggingLevel,
                        LogHistory,
                        Comments);

                JobClassesVector.remove(i);

                JobClassesVector.add(m_JobClassItem);

                break;
            }
        }
        return m_JobClassItem;
    }

    class JobClassItem {

        public JobClassItem(int     Id,
                            String  JobClassName,
                            String  ResourceConsumerGroup,
                            String  Service,
                            String  LoggingLevel,
                            int     LogHistory,
                            String  Comments) {
            m_Id = Id;
            m_JobClassName = JobClassName;
            m_ResourceConsumerGroup = ResourceConsumerGroup;
            m_Service = Service;
            m_LoggingLevel = LoggingLevel;
            m_LogHistory = LogHistory;
            m_Comments = Comments;
        }

        public int getId() {
            return m_Id;
        }
        public String getJobClassName() {
            return m_JobClassName;
        }
        public String getResourceConsumerGroup() {
            return m_ResourceConsumerGroup;
        }
        public String getService() {
            return m_Service;
        }
        public String getLoggingLevel() {
            return m_LoggingLevel;
        }
        public int getLogHistory() {
            return m_LogHistory;
        }
        public String getComments() {
            return m_Comments;
        }

        private int     m_Id;
        private String  m_JobClassName;
        private String  m_ResourceConsumerGroup;
        private String  m_Service;
        private String  m_LoggingLevel;
        private int     m_LogHistory;
        private String  m_Comments;
    }


    public boolean addWindow(WindowItem m_WindowItem) {
        return WindowsVector.add(m_WindowItem);
    }

    public WindowItem getWindow(int WindowNo) {
        m_WindowItem = WindowsVector.get(WindowNo);
        return m_WindowItem;
    }

    public WindowItem getWindow(String WindowName) {
        for (int i = 0; i < WindowsVector.size(); i++) {
            m_WindowItem = WindowsVector.get(i);
            if ( m_WindowItem.getWindowName().equals(WindowName) ) {
                break;
            }
        }
        return m_WindowItem;
    }

    public void removeWindow(int mWindowId) {
        for (int i = 0; i < WindowsVector.size(); i++) {
            m_WindowItem = WindowsVector.get(i);
            if (m_WindowItem.getId() == mWindowId)
                    WindowsVector.removeElementAt(i);
        }
    }
    public void setWindowEnabled(int mWindowId,
                                 String mEnabled) {
        for (int i = 0; i < WindowsVector.size(); i++) {
            m_WindowItem = WindowsVector.get(i);
            if (m_WindowItem.getId() == mWindowId) {
                m_WindowItem.setEnabled(mEnabled);
                break;
            }
        }
    }
    public WindowItem getWindowId(int WindowId) {

        for (int i = 0; i < WindowsVector.size(); i++) {
            m_WindowItem = WindowsVector.get(i);

            if (m_WindowItem.getId() == WindowId) {
                break;
            }
        }
        return m_WindowItem;
    }

    public int WindowSize() {
        return WindowsVector.size();
    }

    public int getNextWindowId() {
        int mMaxId = 0;
        for (int i = 0; i < WindowsVector.size(); i++) {
            m_WindowItem = WindowsVector.get(i);
            if (m_WindowItem.getId() > mMaxId)
                mMaxId = m_WindowItem.getId();
        }
        return mMaxId + 1;
    }
    public WindowItem updateWindowItem(String WindowName,
                                 String ResourcePlan,
                                 String ScheduleOwner,
                                 String ScheduleName,
                                 String StartDate,
                                 String RepeatInterval,
                                 String EndDate,
                                 String Duration,
                                 String WindowPriority,
                                 String NextStartDate,
                                 String LastStartDate,
                                 String Enabled,
                                 String Active,
                                 String Comments,
                                 String ScheduleType,
                                 String ManualOpenTime,
                                 String ManualDuration) {

        int mId = 0;
        for (int i = 0; i < WindowsVector.size(); i++) {
            m_WindowItem = WindowsVector.get(i);
            if (m_WindowItem.getWindowName().equals(WindowName)) {

                mId = m_WindowItem.getId();

                m_WindowItem = new WindowItem(
                    mId,
                    WindowName,
                    ResourcePlan,
                    ScheduleOwner,
                    ScheduleName,
                    StartDate,
                    RepeatInterval,
                    EndDate,
                    Duration,
                    WindowPriority,
                    NextStartDate,
                    LastStartDate,
                    Enabled,
                    Active,
                    Comments,
                    ScheduleType,
                    ManualOpenTime,
                    ManualDuration);

                WindowsVector.remove(i);

                WindowsVector.add(m_WindowItem);

                break;
            }
        }
        return m_WindowItem;
    }

    class WindowItem {

        public WindowItem(int     Id,
                          String  WindowName,
                          String  ResourcePlan,
                          String  ScheduleOwner,
                          String  ScheduleName,
                          String  StartDate,
                          String  RepeatInterval,
                          String  EndDate,
                          String  Duration,
                          String  WindowPriority,
                          String  NextStartDate,
                          String  LastStartDate,
                          String  Enabled,
                          String  Active,
                          String  Comments,
                          String  ScheduleType,
                          String  ManualOpenTime,
                          String  ManualDuration) {
            m_Id = Id;
            m_WindowName = WindowName;
            m_ResourcePlan = ResourcePlan;
            m_ScheduleOwner = ScheduleOwner;
            m_ScheduleName = ScheduleName;
            m_StartDate = StartDate;
            m_RepeatInterval = RepeatInterval;
            m_EndDate = EndDate;
            m_Duration = Duration;
            m_WindowPriority = WindowPriority;
            m_NextStartDate = NextStartDate;
            m_LastStartDate = LastStartDate;
            m_Enabled = Enabled;
            m_Active = Active;
            m_Comments = Comments;
            m_ScheduleType = ScheduleType;
            m_ManualOpenTime = ManualOpenTime;
            m_ManualDuration = ManualDuration;
        }

        public int getId() {
            return m_Id;
        }
        public String getWindowName() {
            return m_WindowName;
        }
        public String getResourcePlan() {
            return m_ResourcePlan;
        }
        public String getScheduleOwner() {
            return m_ScheduleOwner;
        }
        public String getScheduleName() {
            return m_ScheduleName;
        }
        public String getStartDate() {
            return m_StartDate;
        }
        public String getRepeatInterval() {
            return m_RepeatInterval;
        }
        public String getEndDate() {
            return m_EndDate;
        }
        public String getDuration() {
            return m_Duration;
        }
        public String getWindowPriority() {
            return m_WindowPriority;
        }
        public String getNextStartDate() {
            return m_NextStartDate;
        }
        public String getLastStartDate() {
            return m_LastStartDate;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getActive() {
            return m_Active;
        }
        public String getComments() {
            return m_Comments;
        }
        public String getScheduleType() {
            return m_ScheduleType;
        }
        public String getManualOpenTime() {
            return m_ManualOpenTime;
        }
        public String getManualDuration() {
            return m_ManualDuration;
        }

        public void setEnabled(String mEnabled) {
            m_Enabled = mEnabled;
        }

        private int     m_Id;
        private String  m_WindowName;
        private String  m_ResourcePlan;
        private String  m_ScheduleOwner;
        private String  m_ScheduleName;
        private String  m_StartDate;
        private String  m_RepeatInterval;
        private String  m_EndDate;
        private String  m_Duration;
        private String  m_WindowPriority;
        private String  m_NextStartDate;
        private String  m_LastStartDate;
        private String  m_Enabled;
        private String  m_Active;
        private String  m_Comments;
        private String  m_ScheduleType;
        private String  m_ManualOpenTime;
        private String  m_ManualDuration;
    }

    public boolean addWindowGroup(WindowGroupItem m_WindowGroupItem) {
        return WindowsGroupVector.add(m_WindowGroupItem);
    }

    public WindowGroupItem getWindowGroup(int WindowGroupNo) {
        m_WindowGroupItem = WindowsGroupVector.get(WindowGroupNo);
        return m_WindowGroupItem;
    }

    public WindowGroupItem getWindowGroup(String WindowGroupName) {
        for (int i = 0; i < WindowsGroupVector.size(); i++) {

            m_WindowGroupItem = WindowsGroupVector.get(i);

            if (m_WindowGroupItem.getWindowGroupName().equals(WindowGroupName)) {
                break;
            }
        }
        return m_WindowGroupItem;
    }
    public void setWindowGroupEnabled(int mWindowGroupId,
                                      String mEnabled) {
        for (int i = 0; i < WindowsGroupVector.size(); i++) {
            m_WindowGroupItem = WindowsGroupVector.get(i);
            if (m_WindowGroupItem.getId() == mWindowGroupId) {
                m_WindowGroupItem.setEnabled(mEnabled);
                break;
            }
        }
    }

    public void removeWindowGroup(int mWindowGroupId) {
        for (int i = 0; i < WindowsGroupVector.size(); i++) {
            m_WindowGroupItem = WindowsGroupVector.get(i);
            if (m_WindowGroupItem.getId() == mWindowGroupId)
                    WindowsGroupVector.removeElementAt(i);
        }
    }

    public WindowGroupItem getWindowGroupId(int WindowGroupId) {
        for (int i = 0; i < WindowsGroupVector.size(); i++) {

            m_WindowGroupItem = WindowsGroupVector.get(i);

            if (m_WindowGroupItem.getId() == WindowGroupId) {
                break;
            }
        }
        return m_WindowGroupItem;
    }

    public int WindowGroupSize() {
        return WindowsGroupVector.size();
    }

    public int getNextWindowGroupId() {
        int mMaxId = 0;
        for (int i = 0; i < WindowsGroupVector.size(); i++) {
            m_WindowGroupItem = WindowsGroupVector.get(i);
            if (m_WindowGroupItem.getId() > mMaxId)
                mMaxId = m_WindowGroupItem.getId();
        }
        return mMaxId + 1;
    }
    public WindowGroupItem updateWindowGroupItem(String  WindowGroupName,
                                      String  Enabled,
                                      int     NumberOfWindows,
                                      String  NextStartDate,
                                      String  Comments) {
        int mId = 0;
        for (int i = 0; i < WindowsGroupVector.size(); i++) {
            m_WindowGroupItem = WindowsGroupVector.get(i);
            if (m_WindowGroupItem.getWindowGroupName().equals(WindowGroupName)) {

                mId = m_WindowGroupItem.getId();

                m_WindowGroupItem = new WindowGroupItem(
                                    mId,
                                    WindowGroupName,
                                    Enabled,
                                    NumberOfWindows,
                                    NextStartDate,
                                    Comments);

                WindowsGroupVector.remove(i);

                WindowsGroupVector.add(m_WindowGroupItem);

                break;
            }
        }
        return m_WindowGroupItem;
    }

    class WindowGroupItem {

        public WindowGroupItem(int     Id,
                               String  WindowGroupName,
                               String  Enabled,
                               int     NumberOfWindows,
                               String  NextStartDate,
                               String  Comments) {

            m_Id = Id;
            m_WindowGroupName = WindowGroupName;
            m_Enabled = Enabled;
            m_NumberOfWindows = NumberOfWindows;
            m_NextStartDate = NextStartDate;
            m_Comments = Comments;
        }


        public int getId() {
            return m_Id;
        }
        public String getWindowGroupName() {
            return m_WindowGroupName;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public int getNumberOfWindows() {
            return m_NumberOfWindows;
        }
        public String getNextStartDate() {
            return m_NextStartDate;
        }
        public String getComments() {
            return m_Comments;
        }
        public void setEnabled(String mEnabled) {
            m_Enabled = mEnabled;
        }

        private int     m_Id;
        private String  m_WindowGroupName;
        private String  m_Enabled;
        private int     m_NumberOfWindows;
        private String  m_NextStartDate;
        private String  m_Comments;

    }

    public boolean addWinGroupMembers(WinGroupMembersItem m_WinGroupMembersItem) {
        return WinGroupMembersVector.add(m_WinGroupMembersItem);
    }

    public WinGroupMembersItem getWinGroupMembers(int WinGroupMembersNo) {
        m_WinGroupMembersItem = WinGroupMembersVector.get(WinGroupMembersNo);
        return m_WinGroupMembersItem;
    }
    public WinGroupMembersItem getWinGroupId(int WinGroupMembersId) {

        for (int i = 0; i < WinGroupMembersVector.size(); i++) {

            m_WinGroupMembersItem = WinGroupMembersVector.get(i);

            if (m_WinGroupMembersItem.getId() == WinGroupMembersId) {
                break;
            }
        }
        return m_WinGroupMembersItem;
    }

    public int WinGroupMembersSize() {
        return WinGroupMembersVector.size();
    }

    class WinGroupMembersItem {

        public WinGroupMembersItem(int     Id,
                                   String  WindowGroupName,
                                   String  WindowName) {

            m_Id = Id;
            m_WindowGroupName = WindowGroupName;
            m_WindowName = WindowName;
        }


        public int getId() {
            return m_Id;
        }
        public String getWindowGroupName() {
            return m_WindowGroupName;
        }
        public String getWindowName() {
            return m_WindowName;
        }

        private int     m_Id;
        private String  m_WindowGroupName;
        private String  m_WindowName;
    }


    public boolean addChains(ChainsItem m_ChainsItem) {
        return ChainsVector.add(m_ChainsItem);
    }

    public ChainsItem getChains(int ChainsNo) {
        m_ChainsItem = ChainsVector.get(ChainsNo);
        return m_ChainsItem;
    }
    public void removeChain(int mChainId) {
        for (int i = 0; i < ChainsVector.size(); i++) {
            m_ChainsItem = ChainsVector.get(i);
            if (m_ChainsItem.getId() == mChainId)
                    ChainsVector.removeElementAt(i);
        }
    }
    public void setChainEnabled(int mChainId,
                                String mEnabled) {
        for (int i = 0; i < ChainsVector.size(); i++) {
            m_ChainsItem = ChainsVector.get(i);
            if (m_ChainsItem.getId() == mChainId) {
                m_ChainsItem.setEnabled(mEnabled);
                break;
            }
        }
    }

    public ChainsItem getChainsId(int ChainsId) {

        for (int i = 0; i < ChainsVector.size(); i++) {

            m_ChainsItem = ChainsVector.get(i);

            if (m_ChainsItem.getId() == ChainsId) {
                break;
            }
        }
        return m_ChainsItem;
    }

    public int ChainsSize() {
        return ChainsVector.size();
    }

    public ChainsItem updateChainItem(
                          String  Owner,
                          String  ChainName,
                          String  RuleSetOwner,
                          String  RuleSetName,
                          int     NumberOfRules,
                          int     NumberOfSteps,
                          String  Enabled,
                          String  EvaluationInterval,
                          String  UserRuleSet,
                          String  Comments) {
        int mId = 0;
        for (int i = 0; i < ChainsVector.size(); i++) {
            m_ChainsItem = ChainsVector.get(i);
            if (m_ChainsItem.getChainName().equals(ChainName)) {

                mId = m_ChainsItem.getId();

                m_ChainsItem = new ChainsItem(
                        mId,
                        Owner,
                        ChainName,
                        RuleSetOwner,
                        RuleSetName,
                        NumberOfRules,
                        NumberOfSteps,
                        Enabled,
                        EvaluationInterval,
                        UserRuleSet,
                        Comments);
                ChainsVector.remove(i);

                ChainsVector.add(m_ChainsItem);

                break;
            }
        }
        return m_ChainsItem;
    }

    class ChainsItem {

        public ChainsItem(int     Id,
                          String  Owner,
                          String  ChainName,
                          String  RuleSetOwner,
                          String  RuleSetName,
                          int     NumberOfRules,
                          int     NumberOfSteps,
                          String  Enabled,
                          String  EvaluationInterval,
                          String  UserRuleSet,
                          String  Comments) {

            m_Id = Id;
            m_Owner = Owner;
            m_ChainName = ChainName;
            m_RuleSetOwner = RuleSetOwner;
            m_RuleSetName = RuleSetName;
            m_NumberOfRules = NumberOfRules;
            m_NumberOfSteps = NumberOfSteps;
            m_Enabled = Enabled;
            m_EvaluationInterval = EvaluationInterval;
            m_UserRuleSet = UserRuleSet;
            m_Comments = Comments;
        }


        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getChainName() {
            return m_ChainName;
        }
        public String getRuleSetOwner() {
            return m_RuleSetOwner;
        }
        public String getRuleSetName() {
            return m_RuleSetName;
        }
        public int getNumberOfRules() {
            return m_NumberOfRules;
        }
        public int getNumberOfSteps() {
            return m_NumberOfSteps;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getEvaluationInterval() {
            return m_EvaluationInterval;
        }
        public String getUserRuleSet() {
            return m_UserRuleSet;
        }
        public String getComments() {
            return m_Comments;
        }
        public void setEnabled(String mEnabled) {
            m_Enabled = mEnabled;
        }

        private int     m_Id;
        private String  m_Owner;
        private String  m_ChainName;
        private String  m_RuleSetOwner;
        private String  m_RuleSetName;
        private int     m_NumberOfRules;
        private int     m_NumberOfSteps;
        private String  m_Enabled;
        private String  m_EvaluationInterval;
        private String  m_UserRuleSet;
        private String  m_Comments;
    }

    public boolean addChainSteps(ChainStepsItem m_ChainStepsItem) {
        return ChainStepsVector.add(m_ChainStepsItem);
    }

    public ChainStepsItem getChainSteps(int ChainStepsNo) {
        m_ChainStepsItem = ChainStepsVector.get(ChainStepsNo);
        return m_ChainStepsItem;
    }

    public ChainStepsItem getChainStep(String m_ChainStepOwner,
                                       String m_ChainName,
                                       String m_StepName) {
        for (int i = 0; i < ChainStepsVector.size(); i++) {
            m_ChainStepsItem = ChainStepsVector.get(i);
            if ((m_ChainStepsItem.getOwner().equals(m_ChainStepOwner)) &&
                (m_ChainStepsItem.getChainName().equals(m_ChainName)) &&
                (m_ChainStepsItem.getStepName().equals(m_StepName))) {
                 break;
            }
        }
        return m_ChainStepsItem;
    }

    public void removeChainStep(int mChainStepId) {
        for (int i = 0; i < ChainStepsVector.size(); i++) {
            m_ChainStepsItem = ChainStepsVector.get(i);
            if (m_ChainStepsItem.getId() == mChainStepId)
                    ChainStepsVector.removeElementAt(i);
        }
    }
    public ChainStepsItem getChainStepsId(int ChainStepsId) {

        for (int i = 0; i < ChainStepsVector.size(); i++) {

            m_ChainStepsItem = ChainStepsVector.get(i);

            if (m_ChainStepsItem.getId() == ChainStepsId) {
                break;
            }
        }
        return m_ChainStepsItem;
    }

    public int ChainStepsSize() {
        return ChainStepsVector.size();
    }

    public ChainStepsItem updateChainStepItem(
                              String  Owner,
                              String  ChainName,
                              String  StepName,
                              String  ProgramOwner,
                              String  ProgramName,
                              String  EventScheduleOwner,
                              String  EventScheduleName,
                              String  EventQueueOwner,
                              String  EventQueueName,
                              String  EventQueueAgent,
                              String  EventCondition,
                              String  Skip,
                              String  Pause,
                              String  RestartOnRecovery,
                              String  StepType,
                              String  Timeout,
                              String  CredentialOwner,
                              String  CredentialName,
                              String  Destination,
                              String  RestartOnFailure) {
        int mId = 0;
        for (int i = 0; i < ChainStepsVector.size(); i++) {
            m_ChainStepsItem = ChainStepsVector.get(i);
            if ((m_ChainStepsItem.getOwner().equals(Owner)) &&
                (m_ChainStepsItem.getChainName().equals(ChainName)) &&
                (m_ChainStepsItem.getStepName().equals(StepName)))
            {

                mId = m_ChainStepsItem.getId();

                m_ChainStepsItem = new ChainStepsItem(
                        mId,
                        Owner,
                        ChainName,
                        StepName,
                        ProgramOwner,
                        ProgramName,
                        EventScheduleOwner,
                        EventScheduleName,
                        EventQueueOwner,
                        EventQueueName,
                        EventQueueAgent,
                        EventCondition,
                        Skip,
                        Pause,
                        RestartOnRecovery,
                        StepType,
                        Timeout,
                        CredentialOwner,
                        CredentialName,
                        Destination,
                        RestartOnFailure);

                ChainStepsVector.remove(i);

                ChainStepsVector.add(m_ChainStepsItem);

                break;
            }
        }
        return m_ChainStepsItem;
    }

    class ChainStepsItem {

        public ChainStepsItem(int     Id,
                              String  Owner,
                              String  ChainName,
                              String  StepName,
                              String  ProgramOwner,
                              String  ProgramName,
                              String  EventScheduleOwner,
                              String  EventScheduleName,
                              String  EventQueueOwner,
                              String  EventQueueName,
                              String  EventQueueAgent,
                              String  EventCondition,
                              String  Skip,
                              String  Pause,
                              String  RestartOnRecovery,
                              String  StepType,
                              String  Timeout,
                              String  CredentialOwner,
                              String  CredentialName,
                              String  Destination,
                              String  RestartOnFailure) {

            m_Id = Id;
            m_Owner = Owner;
            m_ChainName = ChainName;
            m_StepName = StepName;
            m_ProgramOwner = ProgramOwner;
            m_ProgramName = ProgramName;
            m_EventScheduleOwner = EventScheduleOwner;
            m_EventScheduleName = EventScheduleName;
            m_EventQueueOwner = EventQueueOwner;
            m_EventQueueName = EventQueueName;
            m_EventQueueAgent = EventQueueAgent;
            m_EventCondition = EventCondition;
            m_Skip = Skip;
            m_Pause = Pause;
            m_RestartOnRecovery = RestartOnRecovery;
            m_StepType = StepType;
            m_Timeout = Timeout;
            m_CredentialOwner = CredentialOwner;
            m_CredentialName = CredentialName;
            m_Destination = Destination;
            m_RestartOnFailure = RestartOnFailure;
        }
        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getChainName() {
            return m_ChainName;
        }
        public String getStepName() {
            return m_StepName;
        }
        public String getProgramOwner() {
            return m_ProgramOwner;
        }
        public String getProgramName() {
            return m_ProgramName;
        }
        public String getEventScheduleOwner() {
            return m_EventScheduleOwner;
        }
        public String getEventScheduleName() {
            return m_EventScheduleName;
        }
        public String getEventQueueOwner() {
            return m_EventQueueOwner;
        }
        public String getEventQueueName() {
            return m_EventQueueName;
        }
        public String getEventQueueAgent() {
            return m_EventQueueAgent;
        }
        public String getEventCondition() {
            return m_EventCondition;
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
        public String getStepType() {
            return m_StepType;
        }
        public String getTimeOut() {
            return m_Timeout;
        }
        public String getCredentialOwner() {
            return m_CredentialOwner;
        }
        public String getCredentialName() {
            return m_CredentialName;
        }
        public String getDestination() {
            return m_Destination;
        }
        public String getRestartOnFailure() {
            return m_RestartOnFailure;
        }

        private int     m_Id;
        private String  m_Owner;
        private String  m_ChainName;
        private String  m_StepName;
        private String  m_ProgramOwner;
        private String  m_ProgramName;
        private String  m_EventScheduleOwner;
        private String  m_EventScheduleName;
        private String  m_EventQueueOwner;
        private String  m_EventQueueName;
        private String  m_EventQueueAgent;
        private String  m_EventCondition;
        private String  m_Skip;
        private String  m_Pause;
        private String  m_RestartOnRecovery;
        private String  m_StepType;
        private String  m_Timeout;
        private String  m_CredentialOwner;
        private String  m_CredentialName;
        private String  m_Destination;
        private String  m_RestartOnFailure;
    }

    public boolean addChainRules(ChainRulesItem m_ChainRulesItem) {
        return ChainRulesVector.add(m_ChainRulesItem);
    }

    public ChainRulesItem getChainRules(int ChainRulesNo) {
        m_ChainRulesItem = ChainRulesVector.get(ChainRulesNo);
        return m_ChainRulesItem;
    }

    public ChainRulesItem getChainRule(String m_ChainOwner,
                                       String m_ChainName,
                                       String m_RuleOwner,
                                       String m_RuleName) {
        for (int i = 0; i < ChainRulesVector.size(); i++) {
            m_ChainRulesItem = ChainRulesVector.get(i);
            if ((m_ChainRulesItem.getOwner().equals(m_ChainOwner)) &&
                (m_ChainRulesItem.getChainName().equals(m_ChainName)) &&
                (m_ChainRulesItem.getRuleOwner().equals(m_RuleOwner)) &&
                (m_ChainRulesItem.getRuleName().equals(m_RuleName))) {
                 break;
            }
        }
        return m_ChainRulesItem;
    }

    public void removeChainRule(int mChainRuleId) {
        for (int i = 0; i < ChainRulesVector.size(); i++) {
            m_ChainRulesItem = ChainRulesVector.get(i);
            if (m_ChainRulesItem.getId() == mChainRuleId)
                    ChainRulesVector.removeElementAt(i);
        }
    }

    public ChainRulesItem getChainRulesId(int ChainRulesId) {

        for (int i = 0; i < ChainRulesVector.size(); i++) {

            m_ChainRulesItem = ChainRulesVector.get(i);

            if (m_ChainRulesItem.getId() == ChainRulesId) {
                break;
            }
        }
        return m_ChainRulesItem;
    }

    public int ChainRulesSize() {
        return ChainRulesVector.size();
    }

    class ChainRulesItem {

        public ChainRulesItem(int     Id,
                              String  Owner,
                              String  ChainName,
                              String  RuleOwner,
                              String  RuleName,
                              String  Conditions,
                              String  Action,
                              String  Comments) {
            m_Id = Id;
            m_Owner = Owner;
            m_ChainName = ChainName;
            m_RuleOwner = RuleOwner;
            m_RuleName = RuleName;
            m_Conditions = Conditions;
            m_Action = Action;
            m_Comments = Comments;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getChainName() {
            return m_ChainName;
        }
        public String getRuleOwner() {
            return m_RuleOwner;
        }
        public String getRuleName() {
            return m_RuleName;
        }
        public String getConditions() {
            return m_Conditions;
        }
        public String getAction() {
            return m_Action;
        }
        public String getComments() {
            return m_Comments;
        }

        private int     m_Id;
        private String  m_Owner;
        private String  m_ChainName;
        private String  m_RuleOwner;
        private String  m_RuleName;
        private String  m_Conditions;
        private String  m_Action;
        private String  m_Comments;
    }

    public int CredentialsSize() {
        return CredentialsVector.size();
    }
    public boolean addCredentials(CredentialsItem m_CredentialDetailsItem) {
        return CredentialsVector.add(m_CredentialDetailsItem);
    }
    public CredentialsItem getCredentials(int CredentialsNo) {
        m_CredentialsItem = CredentialsVector.get(CredentialsNo);
        return m_CredentialsItem;
    }

    public CredentialsItem getCredentials(String CredentialOwner,
                                          String CredentialName) {
        for (int i = 0; i < CredentialsVector.size(); i++) {
            m_CredentialsItem = CredentialsVector.get(i);
            if ((m_CredentialsItem.getCredentialName().equals(CredentialName)) &&
                (m_CredentialsItem.getOwner().equals(CredentialOwner))) {
                break;
            }
        }
        return m_CredentialsItem;
    }

    public CredentialsItem getCredentialsId(int CredentialId) {
        for (int i = 0; i < CredentialsVector.size(); i++) {
            m_CredentialsItem = CredentialsVector.get(i);
            if (m_CredentialsItem.getId() == CredentialId) {
                break;
            }
        }
        return m_CredentialsItem;
    }

    public void removeCredential(int mCredentialId) {
        for (int i = 0; i < CredentialsVector.size(); i++) {
            m_CredentialsItem = CredentialsVector.get(i);
            if (m_CredentialsItem.getId() == mCredentialId)
                CredentialsVector.removeElementAt(i);
        }
    }

    public CredentialsItem updateCredentialItem(
                    String  Owner,
                    String  CredentialName,
                    String  Username,
                    String  DatabaseRole,
                    String  WindowsDomain,
                    String  Comments) {
        int mId = 0;
        for (int i = 0; i < CredentialsVector.size(); i++) {
            m_CredentialsItem = CredentialsVector.get(i);
            if (m_CredentialsItem.getCredentialName().equals(CredentialName)) {

                mId = m_CredentialsItem.getId();

                m_CredentialsItem = new CredentialsItem(
                                    mId,
                                    Owner,
                                    CredentialName,
                                    Username,
                                    DatabaseRole,
                                    WindowsDomain,
                                    Comments);

                CredentialsVector.remove(i);

                CredentialsVector.add(m_CredentialsItem);

                break;
            }
        }
        return m_CredentialsItem;
    }

    class CredentialsItem {

        public CredentialsItem(int     Id,
                               String  Owner,
                               String  CredentialName,
                               String  Username,
                               String  DatabaseRole,
                               String  WindowsDomain,
                               String  Comments) {
            m_Id = Id;
            m_Owner = Owner;
            m_CredentialName = CredentialName;
            m_Username = Username;
            m_DatabaseRole = DatabaseRole;
            m_WindowsDomain = WindowsDomain;
            m_Comments = Comments;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getCredentialName() {
            return m_CredentialName;
        }
        public String getUsername() {
            return m_Username;
        }
        public String getDatabaseRole() {
            return m_DatabaseRole;
        }
        public String getWindowsDomain() {
            return m_WindowsDomain;
        }
        public String getComments() {
            return m_Comments;
        }

        private int     m_Id;
        private String  m_Owner;
        private String  m_CredentialName;
        private String  m_Username;
        private String  m_DatabaseRole;
        private String  m_WindowsDomain;
        private String  m_Comments;
    }

    public void setGroupEnabled(int mGroupId,
                                 String mEnabled) {
        for (int i = 0; i < GroupsVector.size(); i++) {
            m_GroupItem = GroupsVector.get(i);
            if (m_GroupItem.getId() == mGroupId) {
                m_GroupItem.setEnabled(mEnabled);
                break;
            }
        }
    }

    public void removeGroup(int mGroupId) {
        for (int i = 0; i < GroupsVector.size(); i++) {
            m_GroupItem = GroupsVector.get(i);
            if (m_GroupItem.getId() == mGroupId) {
                GroupsVector.removeElementAt(i);
                break;
            }
        }
    }

    public GroupItem getGroupsId(int GroupId) {
        for (int i = 0; i < GroupsVector.size(); i++) {
            m_GroupItem = GroupsVector.get(i);
            if (m_GroupItem.getId() == GroupId) {
                break;
            }
        }
        return m_GroupItem;
    }

    public int groupsSize() {
        return GroupsVector.size();
    }
    public GroupItem getGroup(int GroupNo) {
        m_GroupItem = GroupsVector.get(GroupNo);
        return m_GroupItem;
    }

    public boolean addGroup(GroupItem m_GroupItem) {
        return GroupsVector.add(m_GroupItem);
    }

    public GroupItem updateGroupItem(
                    String  mOwner,
                    String  mGroupName,
                    String  mGroupType,
                    String  mEnabled,
                    int     mNumberOfMembers,
                    String  mComments) {
        int mId = 0;
        for (int i = 0; i < GroupsVector.size(); i++) {
            m_GroupItem = GroupsVector.get(i);
            if ((m_GroupItem.getGroupName().equals(mGroupName)) &&
                (m_GroupItem.getOwner().equals(mOwner))) {

                mId = m_GroupItem.getId();

                m_GroupItem = new GroupItem(
                                    mId,
                                    mOwner,
                                    mGroupName,
                                    mGroupType,
                                    mEnabled,
                                    mNumberOfMembers,
                                    mComments);

                GroupsVector.remove(i);

                GroupsVector.add(m_GroupItem);
                break;
            }
        }
        return m_GroupItem;
    }

    class GroupItem {

        public GroupItem(int  Id,
                         String Owner,
                         String GroupName,
                         String GroupType,
                         String Enabled,
                         int    NumberOfMembers,
                         String Comments) {

            m_Id = Id;
            m_Owner = Owner;
            m_GroupName = GroupName;
            m_GroupType = GroupType;
            m_Enabled = Enabled;
            m_NumberOfMembers = NumberOfMembers;
            m_Comments = Comments;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getGroupName() {
            return m_GroupName;
        }
        public String getGroupType() {
            return m_GroupType;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public int getNumberOfMembers() {
            return m_NumberOfMembers;
        }
        public String getComments() {
            return m_Comments;
        }
        public void setEnabled(String mEnabled) {
            m_Enabled = mEnabled;
        }

        private int     m_Id;
        private String  m_Owner;
        private String  m_GroupName;
        private String  m_GroupType;
        private String  m_Enabled;
        private int     m_NumberOfMembers;
        private String  m_Comments;
    }

    public void removeGroupMember(int mGroupId) {
        for (int i = 0; i < GroupMembersVector.size(); i++) {
            m_GroupMembersItem = GroupMembersVector.get(i);
            if (m_GroupMembersItem.getId() == mGroupId) {
                GroupMembersVector.removeElementAt(i);
                break;
            }
        }
    }

    public GroupMembersItem getGroupMembers(int mId) {
        m_GroupMembersItem = GroupMembersVector.get(mId);
        return m_GroupMembersItem;
    }

    public int groupMembersSize() {
        return GroupMembersVector.size();
    }
    public boolean addGroupMember(GroupMembersItem m_GroupMembersItem) {
        return GroupMembersVector.add(m_GroupMembersItem);
    }

    class GroupMembersItem {

        public GroupMembersItem(int    Id,
                                String Owner,
                                String GroupName,
                                String MemberName) {
            m_Id = Id;
            m_Owner = Owner;
            m_GroupName = GroupName;
            m_MemberName = MemberName;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getGroupName() {
            return m_GroupName;
        }
        public String getMemberName() {
            return m_MemberName;
        }

        private int     m_Id;
        private String  m_Owner;
        private String  m_GroupName;
        private String  m_MemberName;
    }

    public void setFileWatcherEnabled(int mFileWatcherId,
                                      String mEnabled) {
        for (int i = 0; i < FileWatchersVector.size(); i++) {
            m_FileWatchersItem = FileWatchersVector.get(i);
            if (m_FileWatchersItem.getId() == mFileWatcherId) {
                m_FileWatchersItem.setEnabled(mEnabled);
                break;
            }
        }
    }

    public FileWatchersItem getFileWatchers(int FileWatchersNo) {
        m_FileWatchersItem = FileWatchersVector.get(FileWatchersNo);
        return m_FileWatchersItem;
    }
    public FileWatchersItem getFileWatchersId(int FileWatcherId) {
        for (int i = 0; i < FileWatchersVector.size(); i++) {
            m_FileWatchersItem = FileWatchersVector.get(i);
            if (m_FileWatchersItem.getId() == FileWatcherId) {
                break;
            }
        }
        return m_FileWatchersItem;
    }

    public void removeFileWatcher(int mFileWatcherId) {
        for (int i = 0; i < FileWatchersVector.size(); i++) {
            m_FileWatchersItem = FileWatchersVector.get(i);
            if (m_FileWatchersItem.getId() == mFileWatcherId)
                    FileWatchersVector.removeElementAt(i);
        }
    }

    public int fileWatchersSize() {
        return FileWatchersVector.size();
    }
    public boolean addFileWatcher(FileWatchersItem m_FileWatcherItem) {
        return FileWatchersVector.add(m_FileWatcherItem);
    }

    public FileWatchersItem updateFileWatchersItem(
                    String  mOwner,
                    String  mFileWatcherName,
                    String  mEnabled,
                    String  mDestinationOwner,
                    String  mDestinationName,
                    String  mDirectoryPath,
                    String  mFileName,
                    String  mCredentialOwner,
                    String  mCredentialName,
                    int     mMinFileSize,
                    String  mSteadyStateDuration,
                    String  mLastModifiedTime,
                    String  mComments) {
        int mId = 0;
        for (int i = 0; i < FileWatchersVector.size(); i++) {
            m_FileWatchersItem = FileWatchersVector.get(i);
            if (m_FileWatchersItem.getFileWatcherName().equals(mFileWatcherName)) {

                mId = m_FileWatchersItem.getId();

                m_FileWatchersItem = new FileWatchersItem(
                                     mId,
                                     mOwner,
                                     mFileWatcherName,
                                     mEnabled,
                                     mDestinationOwner,
                                     mDestinationName,
                                     mDirectoryPath,
                                     mFileName,
                                     mCredentialOwner,
                                     mCredentialName,
                                     mMinFileSize,
                                     mSteadyStateDuration,
                                     mLastModifiedTime,
                                     mComments);

                FileWatchersVector.remove(i);

                FileWatchersVector.add(m_FileWatchersItem);

                break;
            }
        }
        return m_FileWatchersItem;
    }

    class FileWatchersItem {

        public FileWatchersItem(int    Id,
                                String Owner,
                                String FileWatcherName,
                                String Enabled,
                                String DestinationOwner,
                                String Destination,
                                String DirectoryPath,
                                String FileName,
                                String CredentialOwner,
                                String CredentialName,
                                int    MinFileSize,
                                String SteadyStateDuration,
                                String LastModifiedTime,
                                String Comments) {
            m_Id = Id;
            m_Owner = Owner;
            m_FileWatcherName = FileWatcherName;
            m_Enabled = Enabled;
            m_DestinationOwner = DestinationOwner;
            m_Destination = Destination;
            m_DirectoryPath = DirectoryPath;
            m_FileName = FileName;
            m_CredentialOwner = CredentialOwner;
            m_CredentialName = CredentialName;
            m_MinFileSize = MinFileSize;
            m_SteadyStateDuration = SteadyStateDuration;
            m_LastModifiedTime = LastModifiedTime;
            m_Comments = Comments;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getFileWatcherName() {
            return m_FileWatcherName;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getDestinationOwner() {
            return m_DestinationOwner;
        }
        public String getDestination() {
            return m_Destination;
        }
        public String getDirectoryPath() {
            return m_DirectoryPath;
        }
        public String getFileName() {
            return m_FileName;
        }
        public String getCredentialOwner() {
            return m_CredentialOwner;
        }
        public String getCredentialName() {
            return m_CredentialName;
        }
        public int getMinFileSize() {
            return m_MinFileSize;
        }
        public String getSteadyStateDuration() {
            return m_SteadyStateDuration;
        }
        public String getLastModifiedTime() {
            return m_LastModifiedTime;
        }
        public String getComments() {
            return m_Comments;
        }
        public void setEnabled(String mEnabled) {
            m_Enabled = mEnabled;
        }
        private int     m_Id;
        private String  m_Owner;
        private String  m_FileWatcherName;
        private String  m_Enabled;
        private String  m_DestinationOwner;
        private String  m_Destination;
        private String  m_DirectoryPath;
        private String  m_FileName;
        private String  m_CredentialOwner;
        private String  m_CredentialName;
        private int     m_MinFileSize;
        private String  m_SteadyStateDuration;
        private String  m_LastModifiedTime;
        private String  m_Comments;
    }

    public NotificationsItem getNotificationsId(int mNotificationsId) {
        for (int i = 0; i < NotificationsVector.size(); i++) {
            m_NotificationsItem = NotificationsVector.get(i);
            if (m_NotificationsItem.getId() == mNotificationsId)
                break;
        }
        return m_NotificationsItem;
    }
    public NotificationsItem getNotifications(int mNotificationsNo) {
        return NotificationsVector.get(mNotificationsNo);
    }
    public void removeNotification(int mNotificationsId) {
        for (int i = 0; i < NotificationsVector.size(); i++) {
            m_NotificationsItem = NotificationsVector.get(i);
            if (m_NotificationsItem.getId() == mNotificationsId)
                NotificationsVector.removeElementAt(i);
        }
    }
    public int NotificationsSize() {
        return NotificationsVector.size();
    }
    public boolean addNotification(NotificationsItem m_NotificationsItem) {
        return NotificationsVector.add(m_NotificationsItem);
    }

    class NotificationsItem {

        public NotificationsItem(int     Id,
                                 String  Owner,
                                 String  JobName,
                                 String  JobSubname,
                                 String  Recipient,
                                 String  Sender,
                                 String  Subject,
                                 String  Body,
                                 String  FilterCondition,
                                 String  Event,
                                 int     EventFlag) {
            m_Id = Id;
            m_Owner = Owner;
            m_JobName = JobName;
            m_JobSubname = JobSubname;
            m_Recipient = Recipient;
            m_Sender = Sender;
            m_Subject = Subject;
            m_Body = Body;
            m_FilterCondition = FilterCondition;
            m_Event = Event;
            m_EventFlag = EventFlag;
        }

        public int getId() {
            return m_Id;
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
        public String getRecipient() {
            return m_Recipient;
        }
        public String getSender() {
            return m_Sender;
        }
        public String getSubject() {
            return m_Subject;
        }
        public String getBody() {
            return m_Body;
        }
        public String getFilterCondition() {
            return m_FilterCondition;
        }
        public String getEvent() {
            return m_Event;
        }
        public int getEventFlag() {
            return m_EventFlag;
        }

        private int     m_Id;
        private String  m_Owner;
        private String  m_JobName;
        private String  m_JobSubname;
        private String  m_Recipient;
        private String  m_Sender;
        private String  m_Subject;
        private String  m_Body;
        private String  m_FilterCondition;
        private String  m_Event;
        private int     m_EventFlag;
    }

    public int JobDestsSize() {
        return JobDestsVector.size();
    }
    public boolean addJobDests(JobDestsItem m_JobDestsItem) {
        return JobDestsVector.add(m_JobDestsItem);
    }

    class JobDestsItem {
        public JobDestsItem(int    Id,
                            String Owner,
                            String JobName,
                            String JobSubname,
                            String CredentialOwner,
                            String CredentialName,
                            String DestinationOwner,
                            String Destination,
                            int    JobDestId,
                            String Enabled,
                            String RefsEnabled,
                            String State,
                            String NextStartDate,
                            int    RunCount,
                            int    RetryCount,
                            int    FailureCount,
                            String LastStartDate,
                            String LastEndDate) {
            m_Id = Id;
            m_Owner = Owner;
            m_JobName = JobName;
            m_JobSubname = JobSubname;
            m_CredentialOwner = CredentialOwner;
            m_CredentialName = CredentialName;
            m_DestinationOwner = DestinationOwner;
            m_Destination = Destination;
            m_JobDestId = JobDestId;
            m_Enabled = Enabled;
            m_RefsEnabled = RefsEnabled;
            m_State = State;
            m_NextStartDate = NextStartDate;
            m_RunCount = RunCount;
            m_RetryCount = RetryCount;
            m_FailureCount = FailureCount;
            m_LastStartDate = LastStartDate;
            m_LastEndDate = LastEndDate;
        }
        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getJobName() {
            return m_JobName;
        }
        public String getJobSubname() {
            return m_JobSubname;
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
        public String getDestination() {
            return m_Destination;
        }
        public int getJobDestId() {
            return m_JobDestId;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getRefsEnabled() {
            return m_RefsEnabled;
        }
        public String getState() {
            return m_State;
        }
        public String getNextStartDate() {
            return m_NextStartDate;
        }
        public int getRunCount() {
            return m_RunCount;
        }
        public int getRetryCount() {
            return m_RetryCount;
        }
        public int getFailureCount() {
            return m_FailureCount;
        }
        public String getLastStartDate() {
            return m_LastStartDate;
        }
        public String getLastEndDate() {
            return m_LastEndDate;
        }

        int  m_Id;
        String  m_Owner;
        String  m_JobName;
        String  m_JobSubname;
        String  m_CredentialOwner;
        String  m_CredentialName;
        String  m_DestinationOwner;
        String  m_Destination;
        int     m_JobDestId;
        String  m_Enabled;
        String  m_RefsEnabled;
        String  m_State;
        String  m_NextStartDate;
        int     m_RunCount;
        int     m_RetryCount;
        int     m_FailureCount;
        String  m_LastStartDate;
        String  m_LastEndDate;

    }

    public int DestsSize() {
        return DestsVector.size();
    }
    public boolean addDests(DestsItem m_DestsItem) {
        return DestsVector.add(m_DestsItem);
    }

    class DestsItem {
        public DestsItem(int    Id,
                         String Owner,
                         String DestinationName,
                         String DestinationType,
                         String Enabled,
                         String Comments) {
            m_Id = Id;
            m_DestinationName = DestinationName;
            m_DestinationType = DestinationType;
            m_Enabled = Enabled;
            m_Comments = Comments;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getDestinationName() {
            return m_DestinationName;
        }
        public String getDestinationType() {
            return m_DestinationType;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getComments() {
            return m_Comments;
        }

        int     m_Id;
        String  m_Owner;
        String  m_DestinationName;
        String  m_DestinationType;
        String  m_Enabled;
        String  m_Comments;
    }

    public DbDestsItem getDbDests(int mDbDestsNo) {
        return DbDestsVector.get(mDbDestsNo);
    }

    public DbDestsItem getDbDestsId(int mDbDestsId) {
        for (int i = 0; i < DbDestsVector.size(); i++) {
            m_DbDestsItem = DbDestsVector.get(i);
            if (m_DbDestsItem.getId() == mDbDestsId)
                break;
        }
        return m_DbDestsItem;
    }
    public DbDestsItem getDbDests(String DbDestsName) {

        String lOwner = null;
        String lName = null;
        if (DbDestsName.indexOf('.') > 0) {
            lOwner = DbDestsName.substring(1, DbDestsName.indexOf('.') - 1);
            lName = DbDestsName.substring(DbDestsName.indexOf('.') + 2, DbDestsName.length() - 1);
        }

        for (int i = 0; i < DbDestsVector.size(); i++) {
            m_DbDestsItem = DbDestsVector.get(i);
            if ((DbDestsName.indexOf('.') < 0) &&
                (m_DbDestsItem.getDestinationName().equals(DbDestsName)))
                break;
            else {
                if (m_DbDestsItem.getOwner().equals(lOwner) &&
                    m_DbDestsItem.getDestinationName().equals(lName))
                    break;
            }
        }
        return m_DbDestsItem;
    }

    public int DbDestsSize() {
        return DbDestsVector.size();
    }
    public boolean addDbDests(DbDestsItem mDbDestsItem) {
        return DbDestsVector.add(mDbDestsItem);
    }

    class DbDestsItem {

        public DbDestsItem(int    Id,
                           String Owner,
                           String DestinationName,
                           String ConnectInfo,
                           String Agent,
                           String Enabled,
                           String RefsEnabled,
                           String Comment) {
            m_Id = Id;
            m_Owner = Owner;
            m_DestinationName = DestinationName;
            m_ConnectInfo = ConnectInfo;
            m_Agent = Agent;
            m_Enabled = Enabled;
            m_RefsEnabled = RefsEnabled;
            m_Comment = Comment;
        }

        public int getId() {
            return m_Id;
        }
        public String getOwner() {
            return m_Owner;
        }
        public String getDestinationName() {
            return m_DestinationName;
        }
        public String getConnectInfo() {
            return m_ConnectInfo;
        }
        public String getAgent() {
            return m_Agent;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getRefsEnabled() {
            return m_RefsEnabled;
        }
        public String getComment() {
            return m_Comment;
        }

        int     m_Id;
        String  m_Owner;
        String  m_DestinationName;
        String  m_ConnectInfo;
        String  m_Agent;
        String  m_Enabled;
        String  m_RefsEnabled;
        String  m_Comment;
    }

    public ExtDestsItem getExtDests(int mExtDestsNo) {
        return ExtDestsVector.get(mExtDestsNo);
    }

    public ExtDestsItem getExtDestsId(int mExtDestsId) {
        for (int i = 0; i < ExtDestsVector.size(); i++) {
            m_ExtDestsItem = ExtDestsVector.get(i);
            if (m_ExtDestsItem.getId() == mExtDestsId)
                break;
        }
        return m_ExtDestsItem;
    }
    public ExtDestsItem getExtDests(String DestName) {
        for (int i = 0; i < ExtDestsVector.size(); i++) {
            m_ExtDestsItem = ExtDestsVector.get(i);
            if (m_ExtDestsItem.getDestinationName().equals(DestName))
                break;
        }
        return m_ExtDestsItem;
    }

    public int ExtDestsSize() {
        return ExtDestsVector.size();
    }
    public boolean addExtDests(ExtDestsItem m_ExtDestsItem) {
        return ExtDestsVector.add(m_ExtDestsItem);
    }

    class ExtDestsItem {

        public ExtDestsItem(int    Id,
                            String DestinationName,
                            String Hostname,
                            String Port,
                            String IpAddress,
                            String Enabled,
                            String Comment) {
            m_Id = Id;
            m_DestinationName = DestinationName;
            m_Hostname = Hostname;
            m_Port = Port;
            m_IpAddress = IpAddress;
            m_Enabled = Enabled;
            m_Comment = Comment;
        }

        public int getId() {
            return m_Id;
        }
        public String getDestinationName() {
            return m_DestinationName;
        }
        public String getHostname() {
            return m_Hostname;
        }
        public String getPort() {
            return m_Port;
        }
        public String getIpAddress() {
            return m_IpAddress;
        }
        public String getEnabled() {
            return m_Enabled;
        }
        public String getComment() {
            return m_Comment;
        }

        int     m_Id;
        String  m_DestinationName;
        String  m_Hostname;
        String  m_Port;
        String  m_IpAddress;
        String  m_Enabled;
        String  m_Comment;
    }

    public boolean addGlobalAttributes(GlobalAttributesItem m_GlobalAttributesItem) {
        return GlobalAttributesVector.add(m_GlobalAttributesItem);
    }

    public GlobalAttributesItem getGlobalAttributes(int GlobalAttributesNo) {
        m_GlobalAttributesItem = GlobalAttributesVector.get(GlobalAttributesNo);
        return m_GlobalAttributesItem;
    }
    public GlobalAttributesItem getGlobalAttributesId(int GlobalAttributesId) {

        for (int i = 0; i < GlobalAttributesVector.size(); i++) {

            m_GlobalAttributesItem = GlobalAttributesVector.get(i);

            if (m_GlobalAttributesItem.getId() == GlobalAttributesId) {
                break;
            }
        }
        return m_GlobalAttributesItem;
    }
    public int GlobalAttributesSize() {
        return GlobalAttributesVector.size();
    }

    public GlobalAttributesItem updateGlobalAttributesItem(
                                    String  AttributeName,
                                    String  AttributeValue) {
        int mId = 0;
        for (int i = 0; i < GlobalAttributesVector.size(); i++) {
            m_GlobalAttributesItem = GlobalAttributesVector.get(i);
            if (m_GlobalAttributesItem.getAttributeName().equals(AttributeName)) {

                mId = m_GlobalAttributesItem.getId();

                m_GlobalAttributesItem = new GlobalAttributesItem(
                        mId,
                        AttributeName,
                        AttributeValue);

                GlobalAttributesVector.remove(i);

                GlobalAttributesVector.add(m_GlobalAttributesItem);

                break;
            }
        }
        return m_GlobalAttributesItem;
    }

    class GlobalAttributesItem {

        public GlobalAttributesItem(int     Id,
                                    String  AttributeName,
                                    String  AttributeValue) {
            m_Id = Id;
            m_AttributeName = AttributeName;
            m_AttributeValue = AttributeValue;
        }

        public int getId() {
            return m_Id;
        }
        public String getAttributeName() {
            return m_AttributeName;
        }
        public String getAttributeValue() {
            return m_AttributeValue;
        }

        private int     m_Id;
        private String  m_AttributeName;
        private String  m_AttributeValue;
    }

    public boolean addPlan(PlanItem planItem) {
        return PlansVector.add(planItem);
    }

    public PlanItem getPlan(int PlanId) {
        m_PlanItem = PlansVector.get(PlanId);
        return m_PlanItem;
    }

    public PlanItem getPlanId(int PlanId) {
        for (int i = 0; i < PlansVector.size(); i++) {
            m_PlanItem = PlansVector.get(i);
            if (m_PlanItem.getId() == PlanId) {
                break;
            }
        }
        return m_PlanItem;
    }
    public void removePlan(int PlanId) {
        for (int i = 0; i < PlansVector.size(); i++) {
            m_PlanItem = PlansVector.get(i);
            if (m_PlanItem.getId() == PlanId) {
                PlansVector.removeElementAt(i);
                break;
            }
        }
    }
    public int planSize() {
        return PlansVector.size();
    }

    public PlanItem updatePlanItem(String PlanName,
                                 String MgmtMethod,
                                 String Comments) {

        for (int i = 0; i < PlansVector.size(); i++) {
            m_PlanItem = PlansVector.get(i);
            if (m_PlanItem.getPlan().equals(PlanName) &&
                m_PlanItem.getStatus() != null) {

                if (m_PlanItem.getStatus().equals(PENDING_STATUS)) {
                    int mId = m_PlanItem.getId();
                    int mPlanId = m_PlanItem.getPlanId();
                    int mNumPlanDirectives = m_PlanItem.getNumPlanDirectives();
                    String mCpuMethod = m_PlanItem.getCpuMethod();
                    String mActiveSession = m_PlanItem.getActiveSession();
                    String mParallelDegreeLimitMth = m_PlanItem.getParallelDegreeLimitMth();
                    String mQueueingMth = m_PlanItem.getQueueingMth();
                    String mSubPlan = m_PlanItem.getSubPlan();
                    String mStatus = m_PlanItem.getStatus();
                    String mMandatory = m_PlanItem.getMandatory();

                    m_PlanItem = new PlanItem(
                        mId,
                        mPlanId,
                        PlanName,
                        mNumPlanDirectives,
                        mCpuMethod,
                        MgmtMethod,
                        mActiveSession,
                        mParallelDegreeLimitMth,
                        mQueueingMth,
                        mSubPlan,
                        Comments,
                        mStatus,
                        mMandatory);

                    PlansVector.remove(i);

                    PlansVector.add(m_PlanItem);

                    break;
                }
            }
        }
        return m_PlanItem;
    }

    class PlanItem {

        public PlanItem(int     Id,
                        int     PlanId,
                        String  Plan,
                        int     NumPlanDirectives,
                        String  CpuMethod,
                        String  MgmtMethod,
                        String  ActiveSession,
                        String  ParallelDegreeLimitMth,
                        String  QueueingMth,
                        String  SubPlan,
                        String  Comments,
                        String  Status,
                        String  Mandatory) {
            m_Id = Id;
            m_PlanId = PlanId;
            m_Plan = Plan;
            m_NumPlanDirectives = NumPlanDirectives;
            m_CpuMethod = CpuMethod;
            m_MgmtMethod = MgmtMethod;
            m_ActiveSession = ActiveSession;
            m_ParallelDegreeLimitMth = ParallelDegreeLimitMth;
            m_QueueingMth = QueueingMth;
            m_SubPlan = SubPlan;
            m_Comments = Comments;
            m_Status = Status;
            m_Mandatory = Mandatory;
        }

        public int getId() {
            return m_Id;
        }
        public int getPlanId() {
            return m_PlanId;
        }
        public String getPlan() {
            return m_Plan;
        }
        public int getNumPlanDirectives() {
            return m_NumPlanDirectives;
        }
        public String getCpuMethod() {
            return m_CpuMethod;
        }
        public String getMgmtMethod() {
            return m_MgmtMethod;
        }
        public String getActiveSession() {
            return m_ActiveSession;
        }
        public String getParallelDegreeLimitMth() {
            return m_ParallelDegreeLimitMth;
        }
        public String getQueueingMth() {
            return m_QueueingMth;
        }
        public String getSubPlan() {
            return m_SubPlan;
        };
        public String getComments() {
            return m_Comments;
        }
        public String getStatus() {
            return m_Status;
        }
        public String getMandatory() {
            return m_Mandatory;
        }

        private int     m_Id;
        private int     m_PlanId;
        private String  m_Plan;
        private int     m_NumPlanDirectives;
        private String  m_CpuMethod;
        private String  m_MgmtMethod;
        private String  m_ActiveSession;
        private String  m_ParallelDegreeLimitMth;
        private String  m_QueueingMth;
        private String  m_SubPlan;
        private String  m_Comments;
        private String  m_Status;
        private String  m_Mandatory;
    }

    public boolean addCdbPlan(CdbPlanItem cdbPlanItem) {
        return CdbPlansVector.add(cdbPlanItem);
    }

    public CdbPlanItem getCdbPlan(int CdbPlanId) {
        m_CdbPlanItem = CdbPlansVector.get(CdbPlanId);
        return m_CdbPlanItem;
    }

    public CdbPlanItem getCdbPlanId(int CdbPlanId) {
        for (int i = 0; i < CdbPlansVector.size(); i++) {
            m_CdbPlanItem = CdbPlansVector.get(i);
            if (m_CdbPlanItem.getId() == CdbPlanId) {
                break;
            }
        }
        return m_CdbPlanItem;
    }
    public void removeCdbPlan(int CdbPlanId) {
        for (int i = 0; i < CdbPlansVector.size(); i++) {
            m_CdbPlanItem = CdbPlansVector.get(i);
            if (m_CdbPlanItem.getId() == CdbPlanId) {
                CdbPlansVector.removeElementAt(i);
                break;
            }
        }
    }
    public int cdbPlanSize() {
        return CdbPlansVector.size();
    }
    public CdbPlanItem updateCdbPlanItem(String PlanName,
                                         String Comments) {

        for (int i = 0; i < CdbPlansVector.size(); i++) {
            m_CdbPlanItem = CdbPlansVector.get(i);
            if (m_CdbPlanItem.getPlan().equals(PlanName) &&
                m_CdbPlanItem.getStatus() != null) {

                if (m_CdbPlanItem.getStatus().equals(PENDING_STATUS)) {
                    int mId = m_CdbPlanItem.getId();
                    int mPlanId = m_CdbPlanItem.getPlanId();
                    String mStatus = m_CdbPlanItem.getStatus();
                    String mMandatory = m_CdbPlanItem.getMandatory();

                    m_CdbPlanItem = new CdbPlanItem(
                        mId,
                        mPlanId,
                        PlanName,
                        Comments,
                        mStatus,
                        mMandatory);

                    CdbPlansVector.remove(i);

                    CdbPlansVector.add(m_CdbPlanItem);

                    break;
                }
            }
        }
        return m_CdbPlanItem;
    }


    class CdbPlanItem {

        public CdbPlanItem(int     Id,
                           int     PlanId,
                           String  Plan,
                           String  Comments,
                           String  Status,
                           String  Mandatory) {
            m_Id = Id;
            m_PlanId = PlanId;
            m_Plan = Plan;
            m_Comments = Comments;
            m_Status = Status;
            m_Mandatory = Mandatory;
        }

        public int getId() {
            return m_Id;
        }
        public int getPlanId() {
            return m_PlanId;
        }
        public String getPlan() {
            return m_Plan;
        }
        public String getComments() {
            return m_Comments;
        }
        public String getStatus() {
            return m_Status;
        }
        public String getMandatory() {
            return m_Mandatory;
        }

        private int     m_Id;
        private int     m_PlanId;
        private String  m_Plan;
        private String  m_Comments;
        private String  m_Status;
        private String  m_Mandatory;
    }


    public boolean addConsumerGroup(ConsumerGroupItem consumerGroupItem) {
        return ConsumerGroupsVector.add(consumerGroupItem);
    }

    public ConsumerGroupItem getConsumerGroup(int ConsumerGroupId) {
        m_ConsumerGroupItem = ConsumerGroupsVector.get(ConsumerGroupId);
        return m_ConsumerGroupItem;
    }

    public ConsumerGroupItem getConsumerGroupId(int ConsumerGroupId) {
        for (int i = 0; i < ConsumerGroupsVector.size(); i++) {
            m_ConsumerGroupItem = ConsumerGroupsVector.get(i);
            if (m_ConsumerGroupItem.getId() == ConsumerGroupId) {
                break;
            }
        }
        return m_ConsumerGroupItem;
    }
    public void removeConsumerGroup(int ConsumerGroupId) {
        for (int i = 0; i < ConsumerGroupsVector.size(); i++) {
            m_ConsumerGroupItem = ConsumerGroupsVector.get(i);
            if (m_ConsumerGroupItem.getId() == ConsumerGroupId) {
                ConsumerGroupsVector.removeElementAt(i);
                break;
            }
        }
    }
    public int consumerGroupSize() {
        return ConsumerGroupsVector.size();
    }

    public ConsumerGroupItem updateConsumerGroupItem(
                                 String ConsumerGroup,
                                 String Comments) {

        for (int i = 0; i < ConsumerGroupsVector.size(); i++) {
            m_ConsumerGroupItem = ConsumerGroupsVector.get(i);
            if (m_ConsumerGroupItem.getConsumerGroup().equals(ConsumerGroup) &&
                m_ConsumerGroupItem.getStatus() != null) {

                if (m_ConsumerGroupItem.getStatus().equals(PENDING_STATUS)) {
                    int mId = m_ConsumerGroupItem.getId();
                    int mConsumerGroupId = m_ConsumerGroupItem.getConsumerGroupId();
                    String mCpuMethod = m_ConsumerGroupItem.getCpuMethod();
                    String mMgmtMethod = m_ConsumerGroupItem.getMgmtMethod();
                    String mInternalUse = m_ConsumerGroupItem.getInternalUse();
                    String mCategory = m_ConsumerGroupItem.getCategory();
                    String mStatus = m_ConsumerGroupItem.getStatus();
                    String mMandatory = m_ConsumerGroupItem.getMandatory();

                    m_ConsumerGroupItem = new ConsumerGroupItem(
                        mId,
                        mConsumerGroupId,
                        ConsumerGroup,
                        mCpuMethod,
                        mMgmtMethod,
                        mInternalUse,
                        Comments,
                        mCategory,
                        mStatus,
                        mMandatory);

                    ConsumerGroupsVector.remove(i);

                    ConsumerGroupsVector.add(m_ConsumerGroupItem);

                    break;
                }
            }
        }
        return m_ConsumerGroupItem;
    }

    class ConsumerGroupItem {

        public ConsumerGroupItem(
                        int     Id,
                        int     ConsumerGroupId,
                        String  ConsumerGroup,
                        String  CpuMethod,
                        String  MgmtMethod,
                        String  InternalUse,
                        String  Comments,
                        String  Category,
                        String  Status,
                        String  Mandatory)
        {
            m_Id = Id;
            m_ConsumerGroupId = ConsumerGroupId;
            m_ConsumerGroup = ConsumerGroup;
            m_CpuMethod = CpuMethod;
            m_MgmtMethod = MgmtMethod;
            m_InternalUse = InternalUse;
            m_Comments = Comments;
            m_Category = Category;
            m_Status = Status;
            m_Mandatory = Mandatory;
        }

        public int getId() {
            return m_Id;
        }
        public int getConsumerGroupId() {
            return m_ConsumerGroupId;
        }
        public String getConsumerGroup() {
            return m_ConsumerGroup;
        }
        public String getCpuMethod() {
            return m_CpuMethod;
        }
        public String getMgmtMethod() {
            return m_MgmtMethod;
        }
        public String getInternalUse() {
            return m_InternalUse;
        } 
        public String getComments() {
            return m_Comments;
        }
        public String getCategory() {
            return m_Category;
        }
        public String getStatus() {
            return m_Status;
        }
        public String getMandatory() {
            return m_Mandatory;
        }

        private int     m_Id;
        private int     m_ConsumerGroupId;
        private String  m_ConsumerGroup;
        private String  m_CpuMethod;
        private String  m_MgmtMethod;
        private String  m_InternalUse;
        private String  m_Comments;
        private String  m_Category;
        private String  m_Status;
        private String  m_Mandatory;
    }

    public boolean addPlanDirective(PlanDirectiveItem planDirectiveItem) {
        return PlanDirectivesVector.add(planDirectiveItem);
    }
    public PlanDirectiveItem getPlanDirective(int PlanDirectiveId) {
        m_PlanDirectiveItem = PlanDirectivesVector.get(PlanDirectiveId);
        return m_PlanDirectiveItem;
    }
    public PlanDirectiveItem getPlanDirectiveId(int PlanDirectiveId) {
        for (int i = 0; i < PlanDirectivesVector.size(); i++) {
            m_PlanDirectiveItem = PlanDirectivesVector.get(i);
            if (m_PlanDirectiveItem.getId() == PlanDirectiveId) {
                break;
            }
        }
        return m_PlanDirectiveItem;
    }

    public void removePlanDirective(String mPlan,
                                    String mGroup) {
        for (int i = 0; i < PlanDirectivesVector.size(); i++) {
            m_PlanDirectiveItem = PlanDirectivesVector.get(i);
            if (m_PlanDirectiveItem.getPlan().equals(mPlan) &&
                m_PlanDirectiveItem.getGroup().equals(mGroup) &&
                m_PlanDirectiveItem.getStatus() != null)
            {
                if (m_PlanDirectiveItem.getStatus().equals(PENDING_STATUS))
                {
                    PlanDirectivesVector.removeElementAt(i);
                    break;
                }
            }
        }
    }

    public int SpecificPlanDirectiveSize(String mPlan) {
        int mRetNo = 0;
        for (int i = 0; i < PlanDirectivesVector.size(); i++) {
            m_PlanDirectiveItem = PlanDirectivesVector.get(i);
            if (m_PlanDirectiveItem.getPlan().equals(mPlan) &&
                m_PlanDirectiveItem.getStatus() != null)
            {
                if (m_PlanDirectiveItem.getStatus().equals(PENDING_STATUS))
                    mRetNo = mRetNo + 1;
            }
        }
        return mRetNo;
    }

    public int PlanDirectiveSize() {
        return PlanDirectivesVector.size();
    }

    public PlanDirectiveItem updatePlanDirectiveItem(
                    String  Plan,
                    String  Group,
                    String  Type,
                    int     Mgmt_P1,
                    int     Mgmt_P2,
                    int     Mgmt_P3,
                    int     Mgmt_P4,
                    int     Mgmt_P5,
                    int     Mgmt_P6,
                    int     Mgmt_P7,
                    int     Mgmt_P8,
                    int     ActiveSessPool,
                    int     QueueingP1,
                    int     ParallelDegreeLimitP1,
                    String  SwitchGroup,
                    String  SwitchForCall,
                    int     SwitchTime,
                    int     SwitchIoMegabytes,
                    int     SwitchIoReqs,
                    String  SwitchEstimate,
                    int     MaxEstExecTime,
                    int     UndoPool,
                    int     MaxIdleTime,
                    int     MaxIdleBlockerTime,
                    int     SwitchTimeInCall,
                    String  Comments,
                    String  Status,
                    String  Mandatory,
                    int     MaxUtilLimit)
    {
        int mId = 0;
        for (int i = 0; i < PlanDirectivesVector.size(); i++) {
            m_PlanDirectiveItem = PlanDirectivesVector.get(i);
            if ((m_PlanDirectiveItem.getPlan().equals(Plan)) &&
                (m_PlanDirectiveItem.getGroup().equals(Group)) &&
                (m_PlanDirectiveItem.getStatus() != null)) {

                if (m_PlanDirectiveItem.getStatus().equals(PENDING_STATUS)) {
                    mId = m_PlanDirectiveItem.getId();

                    m_PlanDirectiveItem = new PlanDirectiveItem(
                            mId,
                            Plan,
                            Group,
                            Type,
                            Mgmt_P1,
                            Mgmt_P2,
                            Mgmt_P3,
                            Mgmt_P4,
                            Mgmt_P5,
                            Mgmt_P6,
                            Mgmt_P7,
                            Mgmt_P8,
                            ActiveSessPool,
                            QueueingP1,
                            ParallelDegreeLimitP1,
                            SwitchGroup,
                            SwitchForCall,
                            SwitchTime,
                            SwitchIoMegabytes,
                            SwitchIoReqs,
                            SwitchEstimate,
                            MaxEstExecTime,
                            UndoPool,
                            MaxIdleTime,
                            MaxIdleBlockerTime,
                            SwitchTimeInCall,
                            Comments,
                            Status,
                            Mandatory,
                            MaxUtilLimit);

                    PlanDirectivesVector.remove(i);

                    PlanDirectivesVector.add(m_PlanDirectiveItem);

                    break;
                }
            }
        }
        return m_PlanDirectiveItem;
    }


    class PlanDirectiveItem {

        public PlanDirectiveItem(
                        int     Id,
                        String  Plan,
                        String  Group,
                        String  Type,
                        int     Mgmt_P1,
                        int     Mgmt_P2,
                        int     Mgmt_P3,
                        int     Mgmt_P4,
                        int     Mgmt_P5,
                        int     Mgmt_P6,
                        int     Mgmt_P7,
                        int     Mgmt_P8,
                        int     ActiveSessPool,
                        int     QueueingP1,
                        int     ParallelDegreeLimitP1,
                        String  SwitchGroup,
                        String  SwitchForCall,
                        int     SwitchTime,
                        int     SwitchIoMegabytes,
                        int     SwitchIoReqs,
                        String  SwitchEstimate,
                        int     MaxEstExecTime,
                        int     UndoPool,
                        int     MaxIdleTime,
                        int     MaxIdleBlockerTime,
                        int     SwitchTimeInCall,
                        String  Comments,
                        String  Status,
                        String  Mandatory,
                        int     MaxUtilLimit)
        {
            m_Id = Id;
            m_Plan = Plan;
            m_Group = Group;
            m_Type = Type;
            m_Mgmt_P1 = Mgmt_P1;
            m_Mgmt_P2 = Mgmt_P2;
            m_Mgmt_P3 = Mgmt_P3;
            m_Mgmt_P4 = Mgmt_P4;
            m_Mgmt_P5 = Mgmt_P5;
            m_Mgmt_P6 = Mgmt_P6;
            m_Mgmt_P7 = Mgmt_P7;
            m_Mgmt_P8 = Mgmt_P8;
            m_ActiveSessPool = ActiveSessPool;
            m_QueueingP1 = QueueingP1;
            m_ParallelDegreeLimitP1 = ParallelDegreeLimitP1;
            m_SwitchGroup = SwitchGroup;
            m_SwitchForCall = SwitchForCall;
            m_SwitchTime = SwitchTime;
            m_SwitchIoMegabytes = SwitchIoMegabytes;
            m_SwitchIoReqs = SwitchIoReqs;
            m_SwitchEstimate = SwitchEstimate;
            m_MaxEstExecTime = MaxEstExecTime;
            m_UndoPool = UndoPool;
            m_MaxIdleTime = MaxIdleTime;
            m_MaxIdleBlockerTime = MaxIdleBlockerTime;
            m_SwitchTimeInCall = SwitchTimeInCall;
            m_Comments = Comments;
            m_Status = Status;
            m_Mandatory = Mandatory;
            m_MaxUtilLimit = MaxUtilLimit;
        }

        public int getId() {
            return m_Id;
        }
        public String getPlan() {
            return m_Plan;
        }
        public String getGroup() {
            return m_Group;
        }
        public String getType() {
            return m_Type;
        }
        public int getMgmt_P1() {
            return m_Mgmt_P1;
        }
        public int getMgmt_P2() {
            return m_Mgmt_P2;
        }
        public int getMgmt_P3() {
            return m_Mgmt_P3;
        }
        public int getMgmt_P4() {
            return m_Mgmt_P4;
        }
        public int getMgmt_P5() {
            return m_Mgmt_P5;
        }
        public int getMgmt_P6() {
            return m_Mgmt_P6;
        }
        public int getMgmt_P7() {
            return m_Mgmt_P7;
        }
        public int getMgmt_P8() {
            return m_Mgmt_P8;
        }
        public int getActiveSessPool() {
            return m_ActiveSessPool;
        }
        public int getQueueingP1() {
            return m_QueueingP1;
        }
        public int getParallelDegreeLimitP1() {
            return m_ParallelDegreeLimitP1;
        }
        public String getSwitchGroup() {
            return m_SwitchGroup;
        }
        public String getSwitchForCall() {
            return m_SwitchForCall;
        }
        public int getSwitchTime() {
            return m_SwitchTime;
        }
        public int getSwitchIoMegabytes() {
            return m_SwitchIoMegabytes;
        }
        public int getSwitchIoReqs() {
            return m_SwitchIoReqs;
        }
        public String getSwitchEstimate() {
            return m_SwitchEstimate;
        }
        public int getMaxEstExecTime() {
            return m_MaxEstExecTime;
        }
        public int getUndoPool() {
            return m_UndoPool;
        }
        public int getMaxIdleTime() {
            return m_MaxIdleTime;
        }
        public int getMaxIdleBlockerTime() {
            return m_MaxIdleBlockerTime;
        }
        public int getSwitchTimeInCall() {
            return m_SwitchTimeInCall;
        }
        public String getComments() {
            return m_Comments;
        }
        public String getStatus() {
            return m_Status;
        }
        public String getMandatory() {
            return m_Mandatory;
        }
        public int getMaxUtilLimit() {
            return m_MaxUtilLimit;
        }

        private int     m_Id;
        private String  m_Plan;
        private String  m_Group;
        private String  m_Type;
        private int     m_Mgmt_P1;
        private int     m_Mgmt_P2;
        private int     m_Mgmt_P3;
        private int     m_Mgmt_P4;
        private int     m_Mgmt_P5;
        private int     m_Mgmt_P6;
        private int     m_Mgmt_P7;
        private int     m_Mgmt_P8;
        private int     m_ActiveSessPool;
        private int     m_QueueingP1;
        private int     m_ParallelDegreeLimitP1;
        private String  m_SwitchGroup;
        private String  m_SwitchForCall;
        private int     m_SwitchTime;
        private int     m_SwitchIoMegabytes;
        private int     m_SwitchIoReqs;
        private String  m_SwitchEstimate;
        private int     m_MaxEstExecTime;
        private int     m_UndoPool;
        private int     m_MaxIdleTime;
        private int     m_MaxIdleBlockerTime;
        private int     m_SwitchTimeInCall;
        private String  m_Comments;
        private String  m_Status;
        private String  m_Mandatory;
        private int     m_MaxUtilLimit;
    }

    public boolean addCdbPlanDirective(CdbPlanDirectiveItem cdbPlanDirectiveItem) {
        return CdbPlanDirectivesVector.add(cdbPlanDirectiveItem);
    }
    public CdbPlanDirectiveItem getCdbPlanDirective(int CdbPlanDirectiveId) {
        m_CdbPlanDirectiveItem = CdbPlanDirectivesVector.get(CdbPlanDirectiveId);
        return m_CdbPlanDirectiveItem;
    }
    public CdbPlanDirectiveItem getCdbPlanDirectiveId(int CdbPlanDirectiveId) {
        for (int i = 0; i < CdbPlanDirectivesVector.size(); i++) {
            m_CdbPlanDirectiveItem = CdbPlanDirectivesVector.get(i);
            if (m_CdbPlanDirectiveItem.getId() == CdbPlanDirectiveId) {
                break;
            }
        }
        return m_CdbPlanDirectiveItem;
    }

    public void removeCdbPlanDirective(String mPlan,
                                       String mPluggableDb) {
        for (int i = 0; i < CdbPlanDirectivesVector.size(); i++) {
            m_CdbPlanDirectiveItem = CdbPlanDirectivesVector.get(i);
            if (m_CdbPlanDirectiveItem.getPlan().equals(mPlan) &&
                m_CdbPlanDirectiveItem.getPluggableDatabase().equals(mPluggableDb) &&
                m_CdbPlanDirectiveItem.getStatus() != null)
            {
                if (m_CdbPlanDirectiveItem.getStatus().equals(PENDING_STATUS))
                {
                    CdbPlanDirectivesVector.removeElementAt(i);
                    break;
                }
            }
        }
    }

    public int SpecificCdbPlanDirectiveSize(String mPlan) {
        int mRetNo = 0;
        for (int i = 0; i < CdbPlanDirectivesVector.size(); i++) {
            m_CdbPlanDirectiveItem = CdbPlanDirectivesVector.get(i);
            if (m_CdbPlanDirectiveItem.getPlan().equals(mPlan) &&
                m_CdbPlanDirectiveItem.getStatus() != null)
            {
                if (m_CdbPlanDirectiveItem.getStatus().equals(PENDING_STATUS))
                    mRetNo = mRetNo + 1;
            }
        }
        return mRetNo;
    }
    public int CdbPlanDirectiveSize() {
        return CdbPlanDirectivesVector.size();
    }

    public CdbPlanDirectiveItem updateCdbPlanDirectiveItem(
                    String  Plan,
                    String  PluggableDatabase,
                    int     Shares,
                    int     UtilizationLimit,
                    int     ParallelServerLimit,
                    String  Comments,
                    String  Status,
                    String  Mandatory)
    {
        int mId = 0;
        for (int i = 0; i < CdbPlanDirectivesVector.size(); i++) {
            m_CdbPlanDirectiveItem = CdbPlanDirectivesVector.get(i);
            if ((m_CdbPlanDirectiveItem.getPlan().equals(Plan)) &&
                (m_CdbPlanDirectiveItem.getPluggableDatabase().equals(PluggableDatabase)) &&
                (m_CdbPlanDirectiveItem.getStatus() != null)) {

                if (m_CdbPlanDirectiveItem.getStatus().equals(PENDING_STATUS)) {
                    mId = m_CdbPlanDirectiveItem.getId();

                    m_CdbPlanDirectiveItem = new CdbPlanDirectiveItem(
                            mId,
                            Plan,
                            PluggableDatabase,
                            Shares,
                            UtilizationLimit,
                            ParallelServerLimit,
                            Comments,
                            Status,
                            Mandatory);

                    CdbPlanDirectivesVector.remove(i);

                    CdbPlanDirectivesVector.add(m_CdbPlanDirectiveItem);

                    break;
                }
            }
        }
        return m_CdbPlanDirectiveItem;
    }


    class CdbPlanDirectiveItem {

        public CdbPlanDirectiveItem(
                        int     Id,
                        String  Plan,
                        String  PluggableDatabase,
                        int     Shares,
                        int     UtilizationLimit,
                        int     ParallelServerLimit,
                        String  Comments,
                        String  Status,
                        String  Mandatory)
        {
            m_Id = Id;
            m_Plan = Plan;
            m_PluggableDatabase = PluggableDatabase;
            m_Shares = Shares;
            m_UtilizationLimit = UtilizationLimit;
            m_ParallelServerLimit = ParallelServerLimit;
            m_Comments = Comments;
            m_Status = Status;
            m_Mandatory = Mandatory;
        }

        public int getId() {
            return m_Id;
        }
        public String getPluggableDatabase() {
            return m_PluggableDatabase;
        }
        public String getPlan() {
            return m_Plan;
        }
        public int getShares() {
            return m_Shares;
        }
        public int getUtilizationLimit() {
            return m_UtilizationLimit;
        }
        public int getParallelServerLimit() {
            return m_ParallelServerLimit;
        }
        public String getComments() {
            return m_Comments;
        }
        public String getStatus() {
            return m_Status;
        }
        public String getMandatory() {
            return m_Mandatory;
        }

        private int     m_Id;
        private String  m_Plan;
        private String  m_PluggableDatabase;
        private int     m_Shares;
        private int     m_UtilizationLimit;
        private int     m_ParallelServerLimit;
        private String  m_Comments;
        private String  m_Status;
        private String  m_Mandatory;
    }

    public boolean addMappingPriority(MappingPrioritiesItem  mMappingPriorityItem) {
        return MappingPrioritiesVector.add(mMappingPriorityItem);
    }
    public int mappingPrioritySize() {
        return MappingPrioritiesVector.size();
    }
    public MappingPrioritiesItem getMappingPriority(int mPriority, int mType) {

        for (int i = 0; i < MappingPrioritiesVector.size(); i++) {
            m_MappingPrioritiesItem = MappingPrioritiesVector.get(i);


            if (m_MappingPrioritiesItem.getStatus() == null) {
                if ((mType == 1) &&
                    (m_MappingPrioritiesItem.getPriority() == mPriority))
                     break;
            }
            else {
                if ((mType == 0) &&
                    (m_MappingPrioritiesItem.getPriority() == mPriority))
                     break;
            }
        }
        return m_MappingPrioritiesItem;
    }
    public int getMappingPriority(String mappingName) {
        for (int i = 0; i < MappingPrioritiesVector.size(); i++) {
            m_MappingPrioritiesItem = MappingPrioritiesVector.get(i);
            if (m_MappingPrioritiesItem.getStatus() != null) {
                if (m_MappingPrioritiesItem.getAttribute().equals(mappingName))
                    break;
            }
        }
        return m_MappingPrioritiesItem.getPriority();
    }
    public MappingPrioritiesItem getMappingPriority(int mId) {
        return MappingPrioritiesVector.get(mId);
    }

    public void raiseMappingPriority(int mPriority) {
        int mHigherPriority = mPriority - 1;
        int higherId = 0;
        int lowerId = 0;

        for (int i = 0; i < MappingPrioritiesVector.size(); i++) {
            m_MappingPrioritiesItem = MappingPrioritiesVector.get(i);
            if (m_MappingPrioritiesItem.getStatus() != null) {
                if (m_MappingPrioritiesItem.getPriority() == mPriority)
                    lowerId = i;
                if (m_MappingPrioritiesItem.getPriority() == mHigherPriority)
                    higherId = i;
            }
        }
        m_MappingPrioritiesItem = MappingPrioritiesVector.get(higherId);
        m_MappingPrioritiesItem.setPriority(mPriority);
        m_MappingPrioritiesItem = MappingPrioritiesVector.get(lowerId);
        m_MappingPrioritiesItem.setPriority(mHigherPriority);
    }

    public void lowerMappingPriority(int mPriority) {
        int mLowerPriority = mPriority + 1;
        int higherId = 0;
        int lowerId = 0;

        for (int i = 0; i < MappingPrioritiesVector.size(); i++) {
            m_MappingPrioritiesItem = MappingPrioritiesVector.get(i);
            if (m_MappingPrioritiesItem.getStatus() != null) {
                if (m_MappingPrioritiesItem.getPriority() == mPriority)
                    higherId = i;
                if (m_MappingPrioritiesItem.getPriority() == mLowerPriority)
                    lowerId = i;
            }
        }
        m_MappingPrioritiesItem = MappingPrioritiesVector.get(higherId);
        m_MappingPrioritiesItem.setPriority(mLowerPriority);
        m_MappingPrioritiesItem = MappingPrioritiesVector.get(lowerId);
        m_MappingPrioritiesItem.setPriority(mPriority);

    }

    class MappingPrioritiesItem {

        public MappingPrioritiesItem(
                                     int     Id,
                                     String  Attribute,
                                     int     Priority,
                                     String  Status) {
            mId = Id;
            mAttribute = Attribute;
            mPriority = Priority;
            mStatus = Status;
        }

        public int getId() {
            return mId;
        }
        public String getAttribute() {
            return mAttribute;
        }
        public int getPriority() {
            return mPriority;
        }
        public String getStatus() {
            return mStatus;
        }
        public void setPriority(int Priority) {
            mPriority = Priority;
        }

        int     mId;
        String  mAttribute;
        int     mPriority;
        String  mStatus;
    }

    public boolean addGroupMappings(GroupMappingsItem  mGroupMappingsItem) {
        return GroupMappingsVector.add(mGroupMappingsItem);
    }
    public int groupMappingsSize() {
        return GroupMappingsVector.size();
    }
    public GroupMappingsItem getGroupMappings(int mId) {
        return GroupMappingsVector.get(mId);
    }
    public GroupMappingsItem getGroupMappings(String Attribute,
                                              String Value) {
        for (int i = 0; i < GroupMappingsVector.size(); i++) {
            m_GroupMappingsItem = GroupMappingsVector.get(i);

            // System.out.println( " Point 2-" + m_GroupMappingsItem.getAttribute() + "--" +
            //                                   m_GroupMappingsItem.getValue() + "--" +
            //                                   m_GroupMappingsItem.getStatus());

            if (m_GroupMappingsItem.getAttribute().equals(Attribute) &&
                m_GroupMappingsItem.getValue().equals(Value) &&
                m_GroupMappingsItem.getStatus() != null)
            {
                if (m_GroupMappingsItem.getStatus().equals(PENDING_STATUS)) {
                    break;
                }
            }
        }
        return m_GroupMappingsItem;
    }
    public GroupMappingsItem getGroupMappingsId(int mId) {
        for (int i = 0; i < GroupMappingsVector.size(); i++) {
            m_GroupMappingsItem = GroupMappingsVector.get(i);
            if (m_GroupMappingsItem.getId() == mId) {
                break;
            }
        }
        return m_GroupMappingsItem;
    }
    public void removeGroupMapping(String  Attribute,
                                   String  Value) {
        for (int i = 0; i < GroupMappingsVector.size(); i++) {
            m_GroupMappingsItem = GroupMappingsVector.get(i);
            if ((m_GroupMappingsItem.getAttribute().equals(Attribute)) &&
                (m_GroupMappingsItem.getValue().equals(Value)))
            {
                GroupMappingsVector.removeElementAt(i);
                break;
            }
        }
    }
    public GroupMappingsItem updateGroupMappingsItem(
                                 String  Attribute,
                                 String  Value,
                                 String  ConsumerGroup,
                                 String  Status) {
        for (int i = 0; i < GroupMappingsVector.size(); i++) {
            m_GroupMappingsItem = GroupMappingsVector.get(i);
            if ((m_GroupMappingsItem.getAttribute().equals(Attribute)) &&
                (m_GroupMappingsItem.getValue().equals(Value)) &&
                (m_GroupMappingsItem.getStatus() != null))
            {

                if (m_GroupMappingsItem.getStatus().equals(PENDING_STATUS))
                {
                    int mId = m_GroupMappingsItem.getId();

                    m_GroupMappingsItem = new GroupMappingsItem(
                            mId,
                            Attribute,
                            Value,
                            ConsumerGroup,
                            Status);

                    GroupMappingsVector.remove(i);

                    GroupMappingsVector.add(m_GroupMappingsItem);

                    break;
                }
            }
        }
        return m_GroupMappingsItem;
    }

    class GroupMappingsItem {
        public GroupMappingsItem(
                                 int     Id,
                                 String  Attribute,
                                 String  Value,
                                 String  ConsumerGroup,
                                 String  Status) {
            mId = Id;
            mAttribute = Attribute;
            mValue = Value;
            mConsumerGroup = ConsumerGroup;
            mStatus = Status;
        }

        public int getId() {
            return mId;
        }
        public String getAttribute() {
            return mAttribute;
        }
        public String getValue() {
            return mValue;
        }
        public String getConsumerGroup() {
            return mConsumerGroup;
        }
        public String getStatus() {
            return mStatus;
        }

        int     mId;
        String  mAttribute;
        String  mValue;
        String  mConsumerGroup;
        String  mStatus;
    }

    public boolean addSession(SessionItem mSessionItem) {
        return SessionsVector.add(mSessionItem);
    }
    public int sessionsSize() {
        return SessionsVector.size();
    }
    public SessionItem getSession(int mNo) {
        return SessionsVector.get(mNo);
    }
    public SessionItem getSessionId(int mId) {
        for (int i = 0; i < SessionsVector.size(); i++) {
            m_SessionItem = SessionsVector.get(i);
            if (m_SessionItem.getSid() == mId) {
                break;
            }
        }
        return m_SessionItem;
    }

    class SessionItem {
        public SessionItem(
                           int     Sid,
                           int     Serial,
                           String  Username,
                           String  Osuser,
                           String  Machine,
                           String  Module,
                           String  ConsGroup,
                           String  OrigConsGroup,
                           String  MappingAttribute,
                           String  MappedConsumerGroup,
                           String  State,
                           String  Active,
                           int     CurrentIdleTime,
                           int     CurrentCpuWaitTime,
                           int     CpuWaitTime,
                           int     CurrentCpuWaits,
                           int     CpuWaits,
                           int     CurrentConsumedCpuTime,
                           int     ConsumedCpuTime,
                           int     CurrentActiveTime,
                           int     ActiveTime,
                           int     CurrentQueuedTime,
                           int     QueuedTime,
                           int     CurrentYields,
                           int     Yields,
                           int     CurrentUndoConsumption,
                           int     MaxUndoConsumption,
                           int     SqlCanceled,
                           int     QueueTimeOuts,
                           int     EstimatedExecutionLimitHit,
                           int     CurrentIoServiceTime,
                           int     IoServiceTime,
                           int     CurrentIoServiceWaits,
                           int     IoServiceWaits,
                           int     CurSmallReadMegabytes,
                           int     SmallReadMegabytes,
                           int     CurLargeReadMegabytes,
                           int     LargeReadMegabytes,
                           int     CurSmallWriteMegabytes,
                           int     SmallWriteMegabytes,
                           int     CurLargeWriteMegabytes,
                           int     LargeWriteMegabytes,
                           int     CurSmallReadRequests,
                           int     SmallReadRequests,
                           int     CurSmallWriteRequests,
                           int     SmallWriteRequests,
                           int     CurLargeReadRequests,
                           int     LargeReadRequests,
                           int     CurLargeWriteRequests,
                           int     LargeWriteRequests,
                           int     CurrentPqActiveTime,
                           int     PqActiveTime,
                           int     Dop,
                           int     PqServers,
                           int     EstimatedExecutionTime,
                           int     CurrentPqQueuedTime,
                           int     PqQueuedTime,
                           int     PqQueued,
                           int     PqQueueTimeOuts,
                           String  PqActive,
                           String  PqStatus,
                           int     CurrentLogicalIos,
                           int     LogicalIos,
                           int     CurrentElapsedTime,
                           int     ElapsedTime,
                           String  LastAction,
                           String  LastActionReason,
                           int     LastActionTime,
                           int     ConId) {

             mSid = Sid;
             mSerial = Serial;
             mUsername = Username;
             mOsuser = Osuser;
             mMachine = Machine;
             mModule = Module;
             mConsumerGroup = ConsGroup;
             mOrigConsumerGroup = OrigConsGroup;
             mMappingAttribute = MappingAttribute;
             mMappedConsumerGroup = MappedConsumerGroup;
             mState = State;
             mActive = Active;
             mCurrentIdleTime = CurrentIdleTime;
             mCurrentCpuWaitTime = CurrentCpuWaitTime;
             mCpuWaitTime = CpuWaitTime;
             mCurrentCpuWaits = CurrentCpuWaits;
             mCpuWaits = CpuWaits;
             mCurrentConsumedCpuTime = CurrentConsumedCpuTime;
             mConsumedCpuTime = ConsumedCpuTime;
             mCurrentActiveTime = CurrentActiveTime;
             mActiveTime = ActiveTime;
             mCurrentQueuedTime = CurrentQueuedTime;
             mQueuedTime = QueuedTime;
             mCurrentYields = CurrentYields;
             mYields = Yields;
             mCurrentUndoConsumption = CurrentUndoConsumption;
             mMaxUndoConsumption = MaxUndoConsumption;
             mSqlCanceled = SqlCanceled;
             mQueueTimeOuts = QueueTimeOuts;
             mEstimatedExecutionLimitHit = EstimatedExecutionLimitHit;
             mCurrentIoServiceTime = CurrentIoServiceTime;
             mIoServiceTime = IoServiceTime;
             mCurrentIoServiceWaits = CurrentIoServiceWaits;
             mIoServiceWaits = IoServiceWaits;
             mCurSmallReadMegabytes = CurSmallReadMegabytes;
             mSmallReadMegabytes = SmallReadMegabytes;
             mCurLargeReadMegabytes = CurLargeReadMegabytes;
             mLargeReadMegabytes = LargeReadMegabytes;
             mCurSmallWriteMegabytes = CurSmallWriteMegabytes;
             mSmallWriteMegabytes = SmallWriteMegabytes;
             mCurLargeWriteMegabytes = CurLargeWriteMegabytes;
             mLargeWriteMegabytes = LargeWriteMegabytes;
             mCurSmallReadRequests = CurSmallReadRequests;
             mSmallReadRequests = SmallReadRequests;
             mCurSmallWriteRequests = CurSmallWriteRequests;
             mSmallWriteRequests = SmallWriteRequests;
             mCurLargeReadRequests = CurLargeReadRequests;
             mLargeReadRequests = LargeReadRequests;
             mCurLargeWriteRequests = CurLargeWriteRequests;
             mLargeWriteRequests = LargeWriteRequests;

             mCurrentPqActiveTime = CurrentPqActiveTime;
             mPqActiveTime = PqActiveTime;
             mDop = Dop;
             mPqServers = PqServers;
             mEstimatedExecutionTime = EstimatedExecutionTime;
             mCurrentPqQueuedTime = CurrentPqQueuedTime;
             mPqQueuedTime = PqQueuedTime;
             mPqQueued = PqQueued;
             mPqQueueTimeOuts = PqQueueTimeOuts;
             mPqActive = PqActive;
             mPqStatus = PqStatus;
             mCurrentLogicalIos = CurrentLogicalIos;
             mLogicalIos = LogicalIos;
             mCurrentElapsedTime = CurrentElapsedTime;
             mElapsedTime = ElapsedTime;
             mLastAction = LastAction;
             mLastActionReason = LastActionReason;
             mLastActionTime = LastActionTime;
             mConId = ConId;
        }
        public int getSid() {
            return mSid;
        }
        public int getSerial() {
            return mSerial;
        }
        public String getUsername() {
            return mUsername;
        }
        public String getOsuser() {
            return mOsuser;
        }
        public String getMachine() {
            return mMachine;
        }
        public String getModule() {
            return mModule;
        }
        public String getConsumerGroup() {
            return mConsumerGroup;
        }
        public String getOrigConsumerGroup() {
            return mOrigConsumerGroup;
        }
        public String getMappingAttribute() {
            return mMappingAttribute;
        }
        public String getMappedConsumerGroup() {
            return mMappedConsumerGroup;
        }
        public String getState() {
            return mState;
        }
        public String getActive() {
            return mActive;
        }
        public int getCurrentIdleTime() {
            return mCurrentIdleTime;
        }
        public int getmCurrentCpuWaitTime() {
            return mCurrentCpuWaitTime;
        }
        public int getCpuWaitTime() {
            return mCpuWaitTime;
        }
        public int getCurrentCpuWaits() {
            return mCurrentCpuWaits;
        }
        public int getCpuWaits() {
            return mCpuWaits;
        }
        public int getCurrentConsumedCpuTime() {
            return mCurrentConsumedCpuTime;
        }
        public int getConsumedCpuTime() {
            return mConsumedCpuTime;
        }
        public int getCurrentActiveTime() {
            return mCurrentActiveTime;
        }
        public int getActiveTime() {
            return mActiveTime;
        }
        public int getCurrentQueuedTime() {
            return mCurrentQueuedTime;
        }
        public int getQueuedTime() {
            return mQueuedTime;
        }
        public int getCurrentYields() {
            return mCurrentYields;
        }
        public int getYields() {
            return mYields;
        }
        public int getCurrentUndoConsumption() {
            return mCurrentUndoConsumption;
        }
        public int getMaxUndoConsumption() {
            return mMaxUndoConsumption;
        }
        public int getSqlCanceled() {
            return mSqlCanceled;
        }
        public int getQueueTimeOuts() {
            return mQueueTimeOuts;
        }
        public int getEstimatedExecutionLimitHit() {
            return mEstimatedExecutionLimitHit;
        }
        public int getCurrentIoServiceTime() {
            return mCurrentIoServiceTime;
        }
        public int getIoServiceTime() {
            return mIoServiceTime;
        }
        public int getCurrentIoServiceWaits() {
            return mCurrentIoServiceWaits;
        }
        public int getIoServiceWaits() {
            return mIoServiceWaits;
        }
        public int getCurSmallReadMegabytes() {
            return mCurSmallReadMegabytes;
        }
        public int getSmallReadMegabytes() {
            return mSmallReadMegabytes;
        }
        public int getCurLargeReadMegabytes() {
            return mCurLargeReadMegabytes;
        }
        public int getLargeReadMegabytes() {
            return mLargeReadMegabytes;
        }
        public int getCurSmallWriteMegabytes() {
            return mCurSmallWriteMegabytes;
        }
        public int getSmallWriteMegabytes() {
            return mSmallWriteMegabytes;
        }
        public int getCurLargeWriteMegabytes() {
            return mCurLargeWriteMegabytes;
        }
        public int getLargeWriteMegabytes() {
            return mLargeWriteMegabytes;
        }
        public int getCurSmallReadRequests() {
            return mCurSmallReadRequests;
        }
        public int getSmallReadRequests() {
            return mSmallReadRequests;
        }
        public int getCurSmallWriteRequests() {
            return mCurSmallWriteRequests;
        }
        public int getSmallWriteRequests() {
            return mSmallWriteRequests;
        }
        public int getCurLargeReadRequests() {
            return mCurLargeReadRequests;
        }
        public int getLargeReadRequests() {
            return mLargeReadRequests;
        }
        public int getCurLargeWriteRequests() {
            return mCurLargeWriteRequests;
        }
        public int getLargeWriteRequests() {
            return mLargeWriteRequests;
        }

        public int getCurrentPqActiveTime() {
            return mCurrentPqActiveTime;
        }
        public int getPqActiveTime() {
            return mPqActiveTime;
        }
        public int getDop() {
            return mDop;
        }
        public int getPqServers() {
            return mPqServers;
        }
        public int getEstimatedExecutionTime() {
            return mEstimatedExecutionTime;
        }
        public int getCurrentPqQueuedTime() {
            return mCurrentPqQueuedTime;
        }
        public int getPqQueuedTime() {
            return mPqQueuedTime;
        }
        public int getPqQueued() {
            return mPqQueued;
        }
        public int getPqQueueTimeOuts() {
            return mPqQueueTimeOuts;
        }
        public String getPqActive() {
            return mPqActive;
        }
        public String getPqStatus() {
            return mPqStatus;
        }
        public int getCurrentLogicalIos() {
            return mCurrentLogicalIos;
        }
        public int getLogicalIos() {
            return mLogicalIos;
        }
        public int getCurrentElapsedTime() {
            return mCurrentElapsedTime;
        }
        public int getElapsedTime() {
            return mElapsedTime;
        }
        public String getLastAction() {
            return mLastAction;
        }
        public String getLastActionReason() {
            return mLastActionReason;
        }
        public int getLastActionTime() {
            return mLastActionTime;
        }
        public int getConId() {
            return mConId;
        }

        int     mSid;
        int     mSerial;
        String  mUsername;
        String  mOsuser;
        String  mMachine;
        String  mModule;
        String  mConsumerGroup;
        String  mOrigConsumerGroup;
        String  mMappingAttribute;
        String  mMappedConsumerGroup;
        String  mState;
        String  mActive;
        int     mCurrentIdleTime;
        int     mCurrentCpuWaitTime;
        int     mCpuWaitTime;
        int     mCurrentCpuWaits;
        int     mCpuWaits;
        int     mCurrentConsumedCpuTime;
        int     mConsumedCpuTime;
        int     mCurrentActiveTime;
        int     mActiveTime;
        int     mCurrentQueuedTime;
        int     mQueuedTime;
        int     mCurrentYields;
        int     mYields;
        int     mCurrentUndoConsumption;
        int     mMaxUndoConsumption;
        int     mSqlCanceled;
        int     mQueueTimeOuts;
        int     mEstimatedExecutionLimitHit;
        int     mCurrentIoServiceTime;
        int     mIoServiceTime;
        int     mCurrentIoServiceWaits;
        int     mIoServiceWaits;
        int     mCurSmallReadMegabytes;
        int     mSmallReadMegabytes;
        int     mCurLargeReadMegabytes;
        int     mLargeReadMegabytes;
        int     mCurSmallWriteMegabytes;
        int     mSmallWriteMegabytes;
        int     mCurLargeWriteMegabytes;
        int     mLargeWriteMegabytes;
        int     mCurSmallReadRequests;
        int     mSmallReadRequests;
        int     mCurSmallWriteRequests;
        int     mSmallWriteRequests;
        int     mCurLargeReadRequests;
        int     mLargeReadRequests;
        int     mCurLargeWriteRequests;
        int     mLargeWriteRequests;

        int     mCurrentPqActiveTime;
        int     mPqActiveTime;
        int     mDop;
        int     mPqServers;
        int     mEstimatedExecutionTime;
        int     mCurrentPqQueuedTime;
        int     mPqQueuedTime;
        int     mPqQueued;
        int     mPqQueueTimeOuts;
        String  mPqActive;
        String  mPqStatus;
        int     mCurrentLogicalIos;
        int     mLogicalIos;
        int     mCurrentElapsedTime;
        int     mElapsedTime;
        String  mLastAction;
        String  mLastActionReason;
        int     mLastActionTime;
        int     mConId;
    }

    public boolean addCurrentPlan(CurrentPlanItem  mCurrentPlanItem) {
        return CurrentPlansVector.add(mCurrentPlanItem);
    }
    public int currentPlansSize() {
        return CurrentPlansVector.size();
    }
    public CurrentPlanItem getCurrentPlan(int mNo) {
        return CurrentPlansVector.get(mNo);
    }

    class CurrentPlanItem {
        public CurrentPlanItem(
                               int     Id,
                               String  Name,
                               String  IsTopPlan,
                               String  CpuManaged) {
            mId = Id;
            mName = Name;
            mIsTopPlan = IsTopPlan;
            mCpuManaged = CpuManaged;
        }

        public int getId() {
            return mId;
        }
        public String getName() {
            return mName;
        }
        public String getIsTopPlan() {
            return mIsTopPlan;
        }
        public String getCpuManaged() {
            return mCpuManaged;
        }

        int     mId;
        String  mName;
        String  mIsTopPlan;
        String  mCpuManaged;
    }

    public boolean addConsumerPriv(ConsumerPrivItem  mConsumerPrivItem) {
        return ConsumerPrivsVector.add(mConsumerPrivItem);
    }
    public ConsumerPrivItem getConsumerPriv(int mId) {
        return ConsumerPrivsVector.get(mId);
    }
    public ConsumerPrivItem getConsumerPriv(String Grantedgroup,
                                            String Grantee) {
        for (int i = 0; i < ConsumerPrivsVector.size(); i++) {
            m_ConsumerPrivItem = ConsumerPrivsVector.get(i);
            if ( m_ConsumerPrivItem.getGrantedGroup().equals(Grantedgroup) &&
                 m_ConsumerPrivItem.getGrantee().equals(Grantee) )
                 break;
        }
        return m_ConsumerPrivItem;
    }
    public int consumerPrivsSize() {
        return ConsumerPrivsVector.size();
    }
    public void removeConsumerPriv(String  ConsumerGroup,
                                   String  Grantee) {
        for (int i = 0; i < ConsumerPrivsVector.size(); i++) {
            m_ConsumerPrivItem = ConsumerPrivsVector.get(i);
            if ((m_ConsumerPrivItem.getGrantee().equals(Grantee)) &&
                (m_ConsumerPrivItem.getGrantedGroup().equals(ConsumerGroup)))
            {
                ConsumerPrivsVector.removeElementAt(i);
                break;
            }
        }
    }

    class ConsumerPrivItem {
        public ConsumerPrivItem(String  Grantee,
                                String  GrantedGroup,
                                String  GrantOption,
                                String  InitialGroup) {
            mGrantee = Grantee;
            mGrantedGroup = GrantedGroup;
            mGrantOption = GrantOption;
            mInitialGroup = InitialGroup;
        }

        public String getGrantee() {
            return mGrantee;
        }
        public String getGrantedGroup() {
            return mGrantedGroup;
        }
        public String getGrantOption() {
            return mGrantOption;
        }
        public String getInitialGroup() {
            return mInitialGroup;
        }

        String  mGrantee;
        String  mGrantedGroup;
        String  mGrantOption;
        String  mInitialGroup;
    }

    public boolean addConsumerGroupStats(ConsumerGroupStatsItem mConsumerGroupStatsItem) {
        return ConsumerGroupStatsVector.add(mConsumerGroupStatsItem);
    }
    public ConsumerGroupStatsItem getConsumerGroupStatsId(int mId) {
        for (int i = 0; i < ConsumerGroupStatsVector.size(); i++) {
            m_ConsumerGroupStatsItem = ConsumerGroupStatsVector.get(i);
            if (m_ConsumerGroupStatsItem.getId() == mId) {
                break;
            }
        }
        return m_ConsumerGroupStatsItem;
    }
    public int consumerGroupStatsSize() {
        return ConsumerGroupStatsVector.size();
    }
    public ConsumerGroupStatsItem getConsumerGroupStats(int mNo) {
        return ConsumerGroupStatsVector.get(mNo);
    }

    class ConsumerGroupStatsItem {
        public ConsumerGroupStatsItem(
                                      int     id,
                                      String  name,
                                      int     activeSessions,
                                      int     executionWaiters,
                                      int     requests,
                                      int     cpuWaitTime,
                                      int     cpuWaits,
                                      int     consumedCpuTime,
                                      int     yields,
                                      int     queueLength,
                                      int     currentUndoConsumption,
                                      int     activeSessionLimitHit,
                                      int     undoLimitHit,
                                      int     switchesInCpuTime,
                                      int     switchesOutCpuTime,
                                      int     switchesInIoMegabytes,
                                      int     switchesOutIoMegabytes,
                                      int     switchesInIoRequests,
                                      int     switchesOutIoRequests,
                                      int     sqlCanceled,
                                      int     activeSessionsKilled,
                                      int     idleSessionsKilled,
                                      int     idleBlkrSessionsKilled,
                                      int     queueTime,
                                      int     queueTimeOut,
                                      int     ioServiceTime,
                                      int     ioServiceWaits,
                                      int     smallReadMegabytes,
                                      int     smallWriteMegabytes,
                                      int     largeReadMegabytes,
                                      int     largeWriteMegabytes,
                                      int     smallReadRequests,
                                      int     smallWriteRequests,
                                      int     largeReadRequests,
                                      int     largeWriteRequests,
                                      int     cpuDecisions,
                                      int     cpuDecisionsExcl,
                                      int     cpuDecisionsWon,
                                      int     switchesInIoLogical,
                                      int     switchesOutIoLogical,
                                      int     switchesInElapsedTime,
                                      int     switchesOutElapsedTime,
                                      int     currentPqsActive,
                                      int     currentPqServersActive,
                                      int     pqsQueued,
                                      int     pqsCompleted,
                                      int     pqServersUsed,
                                      int     pqActiveTime,
                                      int     currentPqsQueued,
                                      int     pqQueuedTime,
                                      int     pqQueueTimeOuts,
                                      int     conId) {
            mId = id;
            mName = name;
            mActiveSessions = activeSessions;
            mExecutionWaiters = executionWaiters;
            mRequests = requests;
            mCpuWaitTime = cpuWaitTime;
            mCpuWaits = cpuWaits;
            mConsumedCpuTime = consumedCpuTime;
            mYields = yields;
            mQueueLength = queueLength;
            mCurrentUndoConsumption = currentUndoConsumption;
            mActiveSessionLimitHit = activeSessionLimitHit;
            mUndoLimitHit = undoLimitHit;
            mSwitchesInCpuTime = switchesInCpuTime;
            mSwitchesOutCpuTime = switchesOutCpuTime;
            mSwitchesInIoMegabytes = switchesInIoMegabytes;
            mSwitchesOutIoMegabytes = switchesOutIoMegabytes;
            mSwitchesInIoRequests = switchesInIoRequests;
            mSwitchesOutIoRequests = switchesOutIoRequests;
            mSqlCanceled = sqlCanceled;
            mActiveSessionsKilled = activeSessionsKilled;
            mIdleSessionsKilled = idleSessionsKilled;
            mIdleBlkrSessionsKilled = idleBlkrSessionsKilled;
            mQueueTime = queueTime;
            mQueueTimeOut = queueTimeOut;
            mIoServiceTime = ioServiceTime;
            mIoServiceWaits = ioServiceWaits;
            mSmallReadMegabytes = smallReadMegabytes;
            mSmallWriteMegabytes = smallWriteMegabytes;
            mLargeReadMegabytes = largeReadMegabytes;
            mLargeWriteMegabytes = largeWriteMegabytes;
            mSmallReadRequests = smallReadRequests;
            mSmallWriteRequests = smallWriteRequests;
            mLargeReadRequests = largeReadRequests;
            mLargeWriteRequests = largeWriteRequests;
            mCpuDecisions = cpuDecisions;
            mCpuDecisionsExcl = cpuDecisionsExcl;
            mCpuDecisionsWon = cpuDecisionsWon;

            mSwitchesInIoLogical = switchesInIoLogical;
            mSwitchesOutIoLogical = switchesOutIoLogical;
            mSwitchesInElapsedTime = switchesInElapsedTime;
            mSwitchesOutElapsedTime = switchesOutElapsedTime;
            mCurrentPqsActive = currentPqsActive;
            mCurrentPqServersActive = currentPqServersActive;
            mPqsQueued = pqsQueued;
            mPqsCompleted = pqsCompleted;
            mPqServersUsed = pqServersUsed;
            mPqActiveTime = pqActiveTime;
            mCurrentPqsQueued = currentPqsQueued;
            mPqQueuedTime = pqQueuedTime;
            mPqQueueTimeOuts = pqQueueTimeOuts;
            mConId = conId;
        }
        public int getId() {
            return mId;
        }
        public String getName() {
            return mName;
        }
        public int getActiveSessions() {
            return mActiveSessions;
        }
        public int getExecutionWaiters() {
            return mExecutionWaiters;
        }
        public int getRequests() {
            return mRequests;
        }
        public int getCpuWaitTime() {
            return mCpuWaitTime;
        }
        public int getCpuWaits() {
            return mCpuWaits;
        }
        public int getConsumedCpuTime() {
            return mConsumedCpuTime;
        }
        public int getYields() {
            return mYields;
        }
        public int getQueueLength() {
            return mQueueLength;
        }
        public int getCurrentUndoConsumption() {
            return mCurrentUndoConsumption;
        }
        public int getActiveSessionLimitHit() {
            return mActiveSessionLimitHit;
        }
        public int getUndoLimitHit() {
            return mUndoLimitHit;
        }
        public int getSwitchesInCpuTime() {
            return mSwitchesInCpuTime;
        }
        public int getSwitchesOutCpuTime() {
            return mSwitchesOutCpuTime;
        }
        public int getSwitchesInIoMegabytes() {
            return mSwitchesInIoMegabytes;
        }
        public int getSwitchesOutIoMegabytes() {
            return mSwitchesOutIoMegabytes;
        }
        public int getSwitchesInIoRequests() {
            return mSwitchesInIoRequests;
        }
        public int getSwitchesOutIoRequests() {
            return mSwitchesOutIoRequests;
        }
        public int getSqlCanceled() {
            return mSqlCanceled;
        }
        public int getActiveSessionsKilled() {
            return mActiveSessionsKilled;
        }
        public int getIdleSessionsKilled() {
            return mIdleSessionsKilled;
        }
        public int getIdleBlkrSessionsKilled() {
            return mIdleBlkrSessionsKilled;
        }
        public int getQueueTime() {
            return mQueueTime;
        }
        public int getQueueTimeOut() {
            return mQueueTimeOut;
        }
        public int getIoServiceTime() {
            return mIoServiceTime;
        }
        public int getIoServiceWaits() {
            return mIoServiceWaits;
        }
        public int getSmallReadMegabytes() {
            return mSmallReadMegabytes;
        }
        public int getSmallWriteMegabytes() {
            return mSmallWriteMegabytes;
        }
        public int getLargeReadMegabytes() {
            return mLargeReadMegabytes;
        }
        public int getLargeWriteMegabytes() {
            return mLargeWriteMegabytes;
        }
        public int getSmallReadRequests() {
            return mSmallReadRequests;
        }
        public int getSmallWriteRequests() {
            return mSmallWriteRequests;
        }
        public int getLargeReadRequests() {
            return mLargeReadRequests;
        }
        public int getLargeWriteRequests() {
            return mLargeWriteRequests;
        }
        public int getCpuDecisions() {
            return mCpuDecisions;
        }
        public int getCpuDecisionsExcl() {
            return mCpuDecisionsExcl;
        }
        public int getCpuDecisionsWon() {
            return mCpuDecisionsWon;
        }

        public int getSwitchesInIoLogical() {
            return mSwitchesInIoLogical;
        }
        public int getSwitchesOutIoLogical() {
            return mSwitchesOutIoLogical;
        }
        public int getSwitchesInElapsedTime() {
            return mSwitchesInElapsedTime;
        }
        public int getSwitchesOutElapsedTime() {
            return mSwitchesOutElapsedTime;
        }
        public int getCurrentPqsActive() {
            return mCurrentPqsActive;
        }
        public int getCurrentPqServersActive() {
            return mCurrentPqServersActive;
        }
        public int getPqsQueued() {
            return mPqsQueued;
        }
        public int getPqsCompleted() {
            return mPqsCompleted;
        }
        public int getPqServersUsed() {
            return mPqServersUsed;
        }
        public int getPqActiveTime() {
            return mPqActiveTime;
        }
        public int getCurrentPqsQueued() {
            return mCurrentPqsQueued;
        }
        public int getPqQueuedTime() {
            return mPqQueuedTime;
        }
        public int getPqQueueTimeOuts() {
            return mPqQueueTimeOuts;
        }
        public int getConId() {
            return mConId;
        }

        private int    mId;
        private String mName;
        private int    mActiveSessions;
        private int    mExecutionWaiters;
        private int    mRequests;
        private int    mCpuWaitTime;
        private int    mCpuWaits;
        private int    mConsumedCpuTime;
        private int    mYields;
        private int    mQueueLength;
        private int    mCurrentUndoConsumption;
        private int    mActiveSessionLimitHit;
        private int    mUndoLimitHit;
        private int    mSwitchesInCpuTime;
        private int    mSwitchesOutCpuTime;
        private int    mSwitchesInIoMegabytes;
        private int    mSwitchesOutIoMegabytes;
        private int    mSwitchesInIoRequests;
        private int    mSwitchesOutIoRequests;
        private int    mSqlCanceled;
        private int    mActiveSessionsKilled;
        private int    mIdleSessionsKilled;
        private int    mIdleBlkrSessionsKilled;
        private int    mQueueTime;
        private int    mQueueTimeOut;
        private int    mIoServiceTime;
        private int    mIoServiceWaits;
        private int    mSmallReadMegabytes;
        private int    mSmallWriteMegabytes;
        private int    mLargeReadMegabytes;
        private int    mLargeWriteMegabytes;
        private int    mSmallReadRequests;
        private int    mSmallWriteRequests;
        private int    mLargeReadRequests;
        private int    mLargeWriteRequests;
        private int    mCpuDecisions;
        private int    mCpuDecisionsExcl;
        private int    mCpuDecisionsWon;

        private int    mSwitchesInIoLogical;
        private int    mSwitchesOutIoLogical;
        private int    mSwitchesInElapsedTime;
        private int    mSwitchesOutElapsedTime;
        private int    mCurrentPqsActive;
        private int    mCurrentPqServersActive;
        private int    mPqsQueued;
        private int    mPqsCompleted;
        private int    mPqServersUsed;
        private int    mPqActiveTime;
        private int    mCurrentPqsQueued;
        private int    mPqQueuedTime;
        private int    mPqQueueTimeOuts;
        private int    mConId;
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

    public void clearComboObj(int mComboId) {
        screenCombo mScreenCombo = null;
        for (int i = 0; i < comboObj.size(); i++) {
            mScreenCombo = comboObj.get(i);
            if (mScreenCombo.getComboId() == mComboId) {
                comboObj.removeElementAt(i);
                i = i - 1;
            }
        }
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

    public String getVersion() {
        return version;
    }
    public int getVersionNo() {
        return versionNo;
    }


    public void setConnectStatus(int m_SysStatus) {
        sysStatus = m_SysStatus;
    }

    public int getConnectStatus() {
        return sysStatus;
    }

    public void setSysMessage(String m_SysMessage) {
        sysMessage.delete(0, sysMessage.length());
        sysMessage.insert(0,m_SysMessage);
    }

    public StringBuffer getSysMessage() {
        return sysMessage;
    }

    public int getScreenId() {
        return mScreenId;
    }
    public void setScreenId(int ScreenId) {
        mScreenId = ScreenId;
    }

    public void setReturnNo(int ReturnNo) {
        mReturnNo = ReturnNo;
    }

    public int getReturnNo() {
        return mReturnNo;
    }

    public String getUserName() {
        return userName;
    }
    public int getUserId() {
        return m_UserId;
    }

    public String getHostName() {
        return hostName;
    }

    // public void GetDriver() {
    //     try {
    //         // The newInstance() call is a work around for some
    //         // broken Java implementations
    //         Class.forName("com.mysql.jdbc.Driver").newInstance();
    //     } catch (Exception e) {
    //         sysMessage.insert(0,"1" + e.getMessage());
    //         setConnectStatus(3);
    //     }
    // }

    public void GetConnection(String m_Username,
                              String m_Password,
                              String m_Hostname,
                              boolean m_isSysdba) {
        try {
            mHostName = m_Hostname.substring(m_Hostname.lastIndexOf("/") + 1);
            sysMessage = new StringBuffer(100);
            setConnectStatus(0);
            userName = m_Username;
            hostName = m_Hostname;

            info = new Properties();
            info.put("user", m_Username);
            info.put("password", m_Password);
            info.put("database", m_Hostname);

            if (m_isSysdba) info.put("internal_logon", "sysdba");

            // Load the Oracle JDBC driver
            // DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

            Class.forName("oracle.jdbc.OracleDriver");
        }
        catch(ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        }
        try {
            // Connect to the Oracle database Dev1
            // conn = DriverManager.getConnection("jdbc:oracle:oci8:" + 
            //         userName + "/" + m_Password + "@" + m_Hostname);
            conn = DriverManager.getConnection("jdbc:oracle:thin:@", info);
            SchedFile.WriteAudit(mHostName + ":Opening Connection.");

            GetDbVersion();
            if (getVersionNo() == 0) {
                sysMessage.insert(0,"    Invalid Database Version.");
                CloseConnection();
            }
            else {
                GetDbaRole();
                SetupCursors();
            }
        }
        catch(SQLException e) {
            sysMessage.insert(0,e.getMessage());
            SchedFile.EnterErrorEntry("GetConnection "," : Error..." + mHostName + ".." + e.getMessage());
            SchedFile.WriteAudit(mHostName + ":Error Attempting to Connect.");

            setConnectStatus(3);
        }
    }

    public void SetupCursors() {
        StringBuffer selectStr1 = new StringBuffer("a");
        StringBuffer selectStr2 = new StringBuffer("a");

        selectStr1.delete(0, selectStr1.length());
        selectStr2.delete(0, selectStr2.length());

        switch (versionNo) {

            case 1:
                selectStr1.insert(0,"select owner, job_name, "
                      + " session_id, slave_process_id, running_instance, "
                      + " resource_consumer_group, elapsed_time, cpu_used ");
                break;
            case 2:
                selectStr1.insert(0,"select owner, job_name, "
                      + " session_id, slave_process_id, running_instance, "
                      + " resource_consumer_group, elapsed_time, cpu_used, "
                      + " job_subname, slave_os_process_id ");
                selectStr2.insert(0,"select owner, job_name, job_subname, "
                      + " chain_owner, chain_name, step_name, state, "
                      + " error_code, completed, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " duration, skip, pause, restart_on_recovery, "
                      + " step_job_subname, step_job_log_id ");
                break;
            case 3:
                selectStr1.insert(0,"select owner, job_name, "
                      + " session_id, slave_process_id, running_instance, "
                      + " resource_consumer_group, elapsed_time, cpu_used, "
                      + " job_subname, slave_os_process_id, "
                      + " job_style, detached ");
                selectStr2.insert(0,"select owner, job_name, job_subname, "
                      + " chain_owner, chain_name, step_name, state, "
                      + " error_code, completed, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " duration, skip, pause, restart_on_recovery, "
                      + " step_job_subname, step_job_log_id ");
                break;
            case 4:
            case 5:
                selectStr1.insert(0,"select owner, job_name, "
                      + " session_id, slave_process_id, running_instance, "
                      + " resource_consumer_group, elapsed_time, cpu_used, "
                      + " job_subname, slave_os_process_id, "
                      + " job_style, detached, "
                      + " destination_owner, destination, "
                      + " credential_owner, credential_name ");
                selectStr2.insert(0,"select owner, job_name, job_subname, "
                      + " chain_owner, chain_name, step_name, state, "
                      + " error_code, completed, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " duration, skip, pause, restart_on_recovery, "
                      + " step_job_subname, step_job_log_id, "
                      + " restart_on_failure ");
                break;
        }

        if (dbaNo == 1) {
            try {
                getJobsRunningDataStmt = conn.prepareStatement( selectStr1.toString()
                    + " from DBA_SCHEDULER_RUNNING_JOBS "
                    + " order by job_name");

                getChainsRunningDataStmt = conn.prepareStatement( selectStr2.toString()
                    + " from DBA_SCHEDULER_RUNNING_CHAINS "
                    + " order by job_name");

            }
            catch(SQLException e) {
                SchedFile.EnterErrorEntry("SetupCursors (3) "," : Error..." + e.getMessage());
            }
        }
        else {
            try {

                getJobsRunningDataStmt = conn.prepareStatement( selectStr1.toString()
                    + " from ALL_SCHEDULER_RUNNING_JOBS "
                    + " order by elapsed_time");

                getChainsRunningDataStmt = conn.prepareStatement( selectStr2.toString()
                    + " from ALL_SCHEDULER_RUNNING_CHAINS "
                    + " order by start_date");


            }
            catch(SQLException e) {
                SchedFile.EnterErrorEntry("SetupCursors (4) "," : Error..." + e.getMessage());
            }
        }
    }


    public void CloseConnection() {
        try {
            conn.close();

            SchedFile.WriteAudit(mHostName + ":Closing Connection.");
        }
        catch(SQLException e) {
            SchedFile.EnterErrorEntry("CloseConnection"," : Error..." + e.getMessage());
        }
        setConnectStatus(0);
    }

    public void GetDbaRole() {
        try {

            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                          ResultSet.CONCUR_READ_ONLY);
            dbaNo = 0;

            rset = stmt.executeQuery(
                "select count(*) from SESSION_ROLES "
                + " where role = 'DBA'" );

            while (rset.next()) {
                dbaNo = rset.getInt(1);
            }
            // close the result set, the statement and connect
            rset.close();
            stmt.close();
        }
        catch(SQLException e) {
            SchedFile.EnterErrorEntry("SetupCursors (1) "," : Error..." + e.getMessage());
        }
    }

    public void GetDbVersion() {
    try {
        CallableStatement cs = conn.prepareCall("begin dbms_utility.db_version( ?, ?); end;");
        cs.registerOutParameter(1, Types.CHAR);
        cs.registerOutParameter(2, Types.CHAR);
        cs.execute();
        version = cs.getString(1).substring(0,4);

        versionNo = CalcVersionNo(version);
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetVersionNo"," : Error..." + e.getMessage());
    }
    }

    public int CalcVersionNo(String mVersion) {
        int mVerNo = 0;
        if (mVersion.equals("10.1")) mVerNo = 1;
        if (mVersion.equals("10.2")) mVerNo = 2;
        if (mVersion.equals("11.1")) mVerNo = 3;
        if (mVersion.equals("11.2")) mVerNo = 4;
        if (mVersion.equals("12.1")) mVerNo = 5;
        if (mVerNo == 0)
            SchedFile.EnterErrorEntry(
                    "CalcVersionNo"," : Error... Version " +
                    mVersion + " is invalid.");
        return mVerNo;
    }

    public boolean isCdbDatabase() {
    try {
        boolean mCdbDatabase = false;
        if (versionNo > 4) {
            getDataStmt = conn.prepareStatement("Select SYS_CONTEXT('USERENV','CON_NAME') from DUAL ");

            ResultSet rset = getDataStmt.executeQuery();

            while (rset.next()) {
                String m_Database = rset.getString(1);
                if (m_Database.equals("CDB$ROOT")) mCdbDatabase = true;
            }
            rset.close();
            getDataStmt.close();
        }
        return mCdbDatabase;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("isCdbDatabase"," : Error..." + e.getMessage());
        return false;
    }
    }

    private String GetJobString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
            case 1:
                selectStr.insert(0,"select owner, job_name, "
                      + " job_creator, client_id, global_uid, "
                      + " program_owner, program_name, job_type, job_action, "
                      + " number_of_arguments, schedule_owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), job_class, "
                      + " enabled, auto_drop, restartable, state, job_priority, "
                      + " run_count, max_runs, failure_count, max_failures, "
                      + " retry_count, to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), last_run_duration, "
                      + " to_char(next_run_date, 'YYYY-MM-DD HH24:MI:SS'), schedule_limit, max_run_duration, "
                      + " logging_level, stop_on_window_close, instance_stickiness, "
                      + " system, job_weight, nls_env, source, destination, comments ");
                break;
            case 2:
                selectStr.insert(0,"select owner, job_name, "
                      + " job_creator, client_id, global_uid, "
                      + " program_owner, program_name, job_type, job_action, "
                      + " number_of_arguments, schedule_owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), job_class, "
                      + " enabled, auto_drop, restartable, state, job_priority, "
                      + " run_count, max_runs, failure_count, max_failures, "
                      + " retry_count, to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), last_run_duration, "
                      + " to_char(next_run_date, 'YYYY-MM-DD HH24:MI:SS'), schedule_limit, max_run_duration, "
                      + " logging_level, stop_on_window_close, instance_stickiness, "
                      + " system, job_weight, nls_env, source, destination, comments, "
                      + " job_subname, schedule_type, event_queue_owner, event_queue_name, "
                      + " event_queue_agent, event_condition, event_rule, raise_events ");
                break;
            case 3:
                selectStr.insert(0,"select owner, job_name, "
                      + " job_creator, client_id, global_uid, "
                      + " program_owner, program_name, job_type, job_action, "
                      + " number_of_arguments, schedule_owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), job_class, "
                      + " enabled, auto_drop, restartable, state, job_priority, "
                      + " run_count, max_runs, failure_count, max_failures, "
                      + " retry_count, to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), last_run_duration, "
                      + " to_char(next_run_date, 'YYYY-MM-DD HH24:MI:SS'), schedule_limit, max_run_duration, "
                      + " logging_level, stop_on_window_close, instance_stickiness, "
                      + " system, job_weight, nls_env, source, destination, comments, "
                      + " job_subname, schedule_type, event_queue_owner, event_queue_name, "
                      + " event_queue_agent, event_condition, event_rule, raise_events, "
                      + " job_style, credential_owner, credential_name, instance_id, deferred_drop ");
                break;
            case 4:
            case 5:
                selectStr.insert(0,"select owner, job_name, "
                      + " job_creator, client_id, global_uid, "
                      + " program_owner, program_name, job_type, job_action, "
                      + " number_of_arguments, schedule_owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), job_class, "
                      + " enabled, auto_drop, restartable, state, job_priority, "
                      + " run_count, max_runs, failure_count, max_failures, "
                      + " retry_count, to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), last_run_duration, "
                      + " to_char(next_run_date, 'YYYY-MM-DD HH24:MI:SS'), schedule_limit, max_run_duration, "
                      + " logging_level, stop_on_window_close, instance_stickiness, "
                      + " system, job_weight, nls_env, source, destination, comments, "
                      + " job_subname, schedule_type, event_queue_owner, event_queue_name, "
                      + " event_queue_agent, event_condition, event_rule, raise_events, "
                      + " job_style, credential_owner, credential_name, instance_id, deferred_drop, "
                      + " file_watcher_owner, file_watcher_name, number_of_destinations, "
                      + " destination_owner, allow_runs_in_restricted_mode ");
                break;
        }
        return selectStr.toString();
    }

    public void GetJobData() {
    try {

        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(GetJobString() + " from DBA_SCHEDULER_JOBS "
                                                               + " order by owner, job_name");
        }
        else {

            getDataStmt = conn.prepareStatement(GetJobString() + " from ALL_SCHEDULER_JOBS "
                                                               + " order by owner, job_name");
        }
        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);

            String m_JobCreator = rset.getString(3);
            String m_ClientId = rset.getString(4);
            String m_GlobalUid = rset.getString(5);
            String m_ProgramOwner = rset.getString(6);
            String m_ProgramName = rset.getString(7);
            String m_JobType = rset.getString(8);

            String m_JobAction = rset.getString(9);
            int    m_NumberOfArguments = rset.getInt(10);
            String m_ScheduleOwner = rset.getString(11);
            String m_ScheduleName = rset.getString(12);
            String m_StartDate = rset.getString(13);
            String m_RepeatInterval = rset.getString(14);

            String m_EndDate = rset.getString(15);
            String m_JobClass = rset.getString(16);
            String m_Enabled = rset.getString(17);
            String m_AutoDrop = rset.getString(18);
            String m_Restartable = rset.getString(19);
            String m_State = rset.getString(20);
            int    m_JobPriority = rset.getInt(21);
            int    m_RunCount = rset.getInt(22);
            int    m_MaxRuns = rset.getInt(23);
            int    m_FailureCount = rset.getInt(24);
            int    m_MaxFailures = rset.getInt(25);
            int    m_RetryCount = rset.getInt(26);
            String m_LastStartDate = rset.getString(27);
            String m_LastRunDuration = rset.getString(28);
            String m_NextRunDate = rset.getString(29);
            String m_ScheduleLimit = rset.getString(30);
            String m_MaxRunDuration = rset.getString(31);
            String m_LoggingLevel = rset.getString(32);
            String m_StopOnWindowClose = rset.getString(33);
            String m_InstanceStickiness = rset.getString(34);
            String m_System = rset.getString(35);
            int    m_JobWeight = rset.getInt(36);
            String m_NlsEnv = rset.getString(37);
            String m_Source = rset.getString(38);
            String m_Destination = rset.getString(39);
            String m_Comments = rset.getString(40);

            String m_JobSubName = "";
            String m_ScheduleType = "";
            String m_EventQueueOwner = "";
            String m_EventQueueName = "";
            String m_EventQueueAgent = "";
            String m_EventCondition = "";
            String m_EventRule = "";
            String m_RaiseEvents = "";
            String m_JobStyle = "";
            String m_CredentialOwner = "";
            String m_CredentialName = "";
            int    m_InstanceId = 0;
            String m_DeferredDrop = "";
            String m_FileWatcherOwner = "";
            String m_FileWatcherName = "";
            int    m_NumberOfDestinations = 0;
            String m_DestinationOwner = "";
            String m_AllowRunsInRestrictedMode = "";

            switch (versionNo) {
                case 2:
                    m_JobSubName = rset.getString(41);
                    m_ScheduleType = rset.getString(42);
                    m_EventQueueOwner = rset.getString(43);
                    m_EventQueueName = rset.getString(44);
                    m_EventQueueAgent = rset.getString(45);
                    m_EventCondition = rset.getString(46);
                    m_EventRule = rset.getString(47);
                    m_RaiseEvents = rset.getString(48);

                    break;
                case 3:
                    m_JobSubName = rset.getString(41);
                    m_ScheduleType = rset.getString(42);
                    m_EventQueueOwner = rset.getString(43);
                    m_EventQueueName = rset.getString(44);
                    m_EventQueueAgent = rset.getString(45);
                    m_EventCondition = rset.getString(46);
                    m_EventRule = rset.getString(47);
                    m_RaiseEvents = rset.getString(48);
                    m_JobStyle = rset.getString(49);
                    m_CredentialOwner = rset.getString(50);
                    m_CredentialName = rset.getString(51);
                    m_InstanceId = rset.getInt(52);
                    m_DeferredDrop = rset.getString(53);

                    break;
                case 4:
                case 5:
                    m_JobSubName = rset.getString(41);
                    m_ScheduleType = rset.getString(42);
                    m_EventQueueOwner = rset.getString(43);
                    m_EventQueueName = rset.getString(44);
                    m_EventQueueAgent = rset.getString(45);
                    m_EventCondition = rset.getString(46);
                    m_EventRule = rset.getString(47);
                    m_RaiseEvents = rset.getString(48);
                    m_JobStyle = rset.getString(49);
                    m_CredentialOwner = rset.getString(50);
                    m_CredentialName = rset.getString(51);
                    m_InstanceId = rset.getInt(52);
                    m_DeferredDrop = rset.getString(53);
                    m_FileWatcherOwner = rset.getString(54);
                    m_FileWatcherName = rset.getString(55);
                    m_NumberOfDestinations = rset.getInt(56);
                    m_DestinationOwner = rset.getString(57);
                    m_AllowRunsInRestrictedMode = rset.getString(58);

                    break;
            }
            m_JobItem = new JobItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_JobName, 
                    m_JobCreator,
                    m_ClientId,
                    m_GlobalUid,
                    m_ProgramOwner,
                    m_ProgramName,
                    m_JobType,
                    m_JobAction,
                    m_NumberOfArguments,
                    m_ScheduleOwner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_JobClass,
                    m_Enabled,
                    m_AutoDrop,
                    m_Restartable,
                    m_State,
                    m_JobPriority,
                    m_RunCount,
                    m_MaxRuns,
                    m_FailureCount,
                    m_MaxFailures,
                    m_RetryCount,
                    m_LastStartDate,
                    m_LastRunDuration,
                    m_NextRunDate,
                    m_ScheduleLimit,
                    m_MaxRunDuration,
                    m_LoggingLevel,
                    m_StopOnWindowClose,
                    m_InstanceStickiness,
                    m_System,
                    m_JobWeight,
                    m_NlsEnv,
                    m_Source,
                    m_Destination,
                    m_Comments,
                    m_JobSubName,
                    m_ScheduleType,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_EventRule,
                    m_RaiseEvents,
                    m_JobStyle,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_InstanceId,
                    m_DeferredDrop,
                    m_FileWatcherOwner,
                    m_FileWatcherName,
                    m_NumberOfDestinations,
                    m_DestinationOwner,
                    m_AllowRunsInRestrictedMode);

            addJob(m_JobItem);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.JobItem GetJobItem(
                                   String   mOwner,
                                   String mJobName,
                                   int mAddUpdate) {
    try {
        // Get one job data item.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(GetJobString()
                + " from DBA_SCHEDULER_JOBS "
                + " where owner = ? and job_name = ?");

        }
        else {

            getItemStmt = conn.prepareStatement(GetJobString()
                + " from ALL_SCHEDULER_JOBS "
                + " where owner = ? and job_name = ?");
        }

        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mJobName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            String m_JobCreator = rset.getString(3);
            String m_ClientId = rset.getString(4);
            String m_GlobalUid = rset.getString(5);
            String m_ProgramOwner = rset.getString(6);
            String m_ProgramName = rset.getString(7);
            String m_JobType = rset.getString(8);
            String m_JobAction = rset.getString(9);
            int    m_NumberOfArguments = rset.getInt(10);
            String m_ScheduleOwner = rset.getString(11);
            String m_ScheduleName = rset.getString(12);
            String m_StartDate = rset.getString(13);
            String m_RepeatInterval = rset.getString(14);
            String m_EndDate = rset.getString(15);
            String m_JobClass = rset.getString(16);
            String m_Enabled = rset.getString(17);
            String m_AutoDrop = rset.getString(18);
            String m_Restartable = rset.getString(19);
            String m_State = rset.getString(20);
            int    m_JobPriority = rset.getInt(21);
            int    m_RunCount = rset.getInt(22);
            int    m_MaxRuns = rset.getInt(23);
            int    m_FailureCount = rset.getInt(24);
            int    m_MaxFailures = rset.getInt(25);
            int    m_RetryCount = rset.getInt(26);
            String m_LastStartDate = rset.getString(27);
            String m_LastRunDuration = rset.getString(28);
            String m_NextRunDate = rset.getString(29);
            String m_ScheduleLimit = rset.getString(30);
            String m_MaxRunDuration = rset.getString(31);
            String m_LoggingLevel = rset.getString(32);
            String m_StopOnWindowClose = rset.getString(33);
            String m_InstanceStickiness = rset.getString(34);
            String m_System = rset.getString(35);
            int    m_JobWeight = rset.getInt(36);
            String m_NlsEnv = rset.getString(37);
            String m_Source = rset.getString(38);
            String m_Destination = rset.getString(39);
            String m_Comments = rset.getString(40);

            String m_JobSubName = "";
            String m_ScheduleType = "";
            String m_EventQueueOwner = "";
            String m_EventQueueName = "";
            String m_EventQueueAgent = "";
            String m_EventCondition = "";
            String m_EventRule = "";
            String m_RaiseEvents = "";
            String m_JobStyle = "";
            String m_CredentialOwner = "";
            String m_CredentialName = "";
            int    m_InstanceId = 0;
            String m_DeferredDrop = "";
            String m_FileWatcherOwner = "";
            String m_FileWatcherName = "";
            int    m_NumberOfDestinations = 0;
            String m_DestinationOwner = "";
            String m_AllowRunsInRestrictedMode = "";

            switch (versionNo) {
                case 2:
                    m_JobSubName = rset.getString(41);
                    m_ScheduleType = rset.getString(42);
                    m_EventQueueOwner = rset.getString(43);
                    m_EventQueueName = rset.getString(44);
                    m_EventQueueAgent = rset.getString(45);
                    m_EventCondition = rset.getString(46);
                    m_EventRule = rset.getString(47);
                    m_RaiseEvents = rset.getString(48);

                    break;
                case 3:
                    m_JobSubName = rset.getString(41);
                    m_ScheduleType = rset.getString(42);
                    m_EventQueueOwner = rset.getString(43);
                    m_EventQueueName = rset.getString(44);
                    m_EventQueueAgent = rset.getString(45);
                    m_EventCondition = rset.getString(46);
                    m_EventRule = rset.getString(47);
                    m_RaiseEvents = rset.getString(48);
                    m_JobStyle = rset.getString(49);
                    m_CredentialOwner = rset.getString(50);
                    m_CredentialName = rset.getString(51);
                    m_InstanceId = rset.getInt(52);
                    m_DeferredDrop = rset.getString(53);

                    break;
                case 4:
                case 5:
                    m_JobSubName = rset.getString(41);
                    m_ScheduleType = rset.getString(42);
                    m_EventQueueOwner = rset.getString(43);
                    m_EventQueueName = rset.getString(44);
                    m_EventQueueAgent = rset.getString(45);
                    m_EventCondition = rset.getString(46);
                    m_EventRule = rset.getString(47);
                    m_RaiseEvents = rset.getString(48);
                    m_JobStyle = rset.getString(49);
                    m_CredentialOwner = rset.getString(50);
                    m_CredentialName = rset.getString(51);
                    m_InstanceId = rset.getInt(52);
                    m_DeferredDrop = rset.getString(53);
                    m_FileWatcherOwner = rset.getString(54);
                    m_FileWatcherName = rset.getString(55);
                    m_NumberOfDestinations = rset.getInt(56);
                    m_DestinationOwner = rset.getString(57);
                    m_AllowRunsInRestrictedMode = rset.getString(58);

                    break;
            }

            if (mAddUpdate == 0) {

                m_JobItem = new JobItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_JobName, 
                    m_JobCreator,
                    m_ClientId,
                    m_GlobalUid,
                    m_ProgramOwner,
                    m_ProgramName,
                    m_JobType,
                    m_JobAction,
                    m_NumberOfArguments,
                    m_ScheduleOwner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_JobClass,
                    m_Enabled,
                    m_AutoDrop,
                    m_Restartable,
                    m_State,
                    m_JobPriority,
                    m_RunCount,
                    m_MaxRuns,
                    m_FailureCount,
                    m_MaxFailures,
                    m_RetryCount,
                    m_LastStartDate,
                    m_LastRunDuration,
                    m_NextRunDate,
                    m_ScheduleLimit,
                    m_MaxRunDuration,
                    m_LoggingLevel,
                    m_StopOnWindowClose,
                    m_InstanceStickiness,
                    m_System,
                    m_JobWeight,
                    m_NlsEnv,
                    m_Source,
                    m_Destination,
                    m_Comments,
                    m_JobSubName,
                    m_ScheduleType,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_EventRule,
                    m_RaiseEvents,
                    m_JobStyle,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_InstanceId,
                    m_DeferredDrop,
                    m_FileWatcherOwner,
                    m_FileWatcherName,
                    m_NumberOfDestinations,
                    m_DestinationOwner,
                    m_AllowRunsInRestrictedMode);

                addJob(m_JobItem);
            }
            if (mAddUpdate == 1) {
                m_JobItem = updateJobItem(
                    m_Owner,
                    m_JobName, 
                    m_JobCreator,
                    m_ClientId,
                    m_GlobalUid,
                    m_ProgramOwner,
                    m_ProgramName,
                    m_JobType,
                    m_JobAction,
                    m_NumberOfArguments,
                    m_ScheduleOwner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_JobClass,
                    m_Enabled,
                    m_AutoDrop,
                    m_Restartable,
                    m_State,
                    m_JobPriority,
                    m_RunCount,
                    m_MaxRuns,
                    m_FailureCount,
                    m_MaxFailures,
                    m_RetryCount,
                    m_LastStartDate,
                    m_LastRunDuration,
                    m_NextRunDate,
                    m_ScheduleLimit,
                    m_MaxRunDuration,
                    m_LoggingLevel,
                    m_StopOnWindowClose,
                    m_InstanceStickiness,
                    m_System,
                    m_JobWeight,
                    m_NlsEnv,
                    m_Source,
                    m_Destination,
                    m_Comments,
                    m_JobSubName,
                    m_ScheduleType,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_EventRule,
                    m_RaiseEvents,
                    m_JobStyle,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_InstanceId,
                    m_DeferredDrop,
                    m_FileWatcherOwner,
                    m_FileWatcherName,
                    m_NumberOfDestinations,
                    m_DestinationOwner,
                    m_AllowRunsInRestrictedMode);
            }
        }
        rset.close();
        getItemStmt.close();

        return m_JobItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetJobArgsData() {
    try {
        // Get the job arguments data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(
                "select owner, job_name, argument_name, "
                + " argument_position, argument_type, value, "
                + " out_argument "
                + " from DBA_SCHEDULER_JOB_ARGS "
                + " order by owner, job_name, argument_position");
        }
        else {
            getDataStmt = conn.prepareStatement(
                "select owner, job_name, argument_name, "
                + " argument_position, argument_type, value, "
                + " out_argument "
                + " from ALL_SCHEDULER_JOB_ARGS "
                + " order by owner, job_name, argument_position");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            String m_ArgumentName = rset.getString(3);
            int m_ArgumentPosition = rset.getInt(4);
            String m_ArgumentType = rset.getString(5);
            String m_Value = rset.getString(6);
            String m_OutArgument = rset.getString(7);

            m_JobArgsItem = new JobArgsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_JobName,
                    m_ArgumentName,
                    m_ArgumentPosition,
                    m_ArgumentType,
                    m_Value,
                    m_OutArgument);

            addJobArgs(m_JobArgsItem);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetJobArgsData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.JobArgsItem GetJobArgsItem(
                                         String mOwner,
                                         String mJobName,
                                         int mJobArgsPos,
                                         int mAddUpdate) {
    try {
        // Get the job arguments data.
        if (dbaNo == 1) {
            getItemStmt = conn.prepareStatement(
                "select owner, job_name, argument_name, "
                + " argument_position, argument_type, value, "
                + " out_argument "
                + " from DBA_SCHEDULER_JOB_ARGS "
                + " where owner = ? and job_name = ? and argument_position = ?");
        }
        else {
            getItemStmt = conn.prepareStatement(
                "select owner, job_name, argument_name, "
                + " argument_position, argument_type, value, "
                + " out_argument "
                + " from ALL_SCHEDULER_JOB_ARGS "
                + " where owner = ? and job_name = ? and argument_position = ?");
        }

        // getItemStmt.setString(1, getUserName().toUpperCase());
        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mJobName.toUpperCase());
        getItemStmt.setInt(3, mJobArgsPos);

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            String m_ArgumentName = rset.getString(3);
            int m_ArgumentPosition = rset.getInt(4);
            String m_ArgumentType = rset.getString(5);
            String m_Value = rset.getString(6);
            String m_OutArgument = rset.getString(7);

            if (mAddUpdate == 0) {
                m_JobArgsItem = new JobArgsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_JobName,
                    m_ArgumentName,
                    m_ArgumentPosition,
                    m_ArgumentType,
                    m_Value,
                    m_OutArgument);

                addJobArgs(m_JobArgsItem);
            }
            if (mAddUpdate == 1) {
                m_JobArgsItem = updateJobArgsItem(
                    m_Owner,
                    m_JobName,
                    m_ArgumentName,
                    m_ArgumentPosition,
                    m_ArgumentType,
                    m_Value,
                    m_OutArgument);
            }
        }
        rset.close();
        getItemStmt.close();
        return m_JobArgsItem;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetJobArgsItem"," : Error..." + e.getMessage());
      return null;
    }
    }

    public void GetProgramData() {
    try {
        // Get the programs data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(GetProgramString()
                + " from DBA_SCHEDULER_PROGRAMS "
                + " order by owner, program_name");
        }
        else {
            getDataStmt = conn.prepareStatement(GetProgramString()
                + " from ALL_SCHEDULER_PROGRAMS "
                + " order by owner, program_name");
        }

        ResultSet rset = getDataStmt.executeQuery();
        int i2 = 0;

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ProgramName = rset.getString(2);
            String m_ProgramType = rset.getString(3);
            String m_ProgramAction = rset.getString(4);
            int    m_NoArguments = rset.getInt(5);
            String m_Enabled = rset.getString(6);
            String m_Comments = rset.getString(7);

            String m_Detached = "";
            String m_ScheduleLimit = "";
            int    m_Priority = 0;
            int    m_Weight = 0;
            int    m_MaxRuns = 0;
            int    m_MaxFailures = 0;
            String m_MaxRunDuration = "";
            String m_NlsEnv = "";


            switch (versionNo) {
                case 2:
                    m_Detached = rset.getString(8);
                    break;
                case 3:
                case 4:
                case 5:
                    m_Detached = rset.getString(8);
                    m_ScheduleLimit = rset.getString(9);
                    m_Priority = rset.getInt(10);
                    m_Weight = rset.getInt(11);
                    m_MaxRuns = rset.getInt(12);
                    m_MaxFailures = rset.getInt(13);
                    m_MaxRunDuration = rset.getString(14);
                    m_NlsEnv = rset.getString(15);
                    break;
            }

            m_ProgramItem = new ProgramItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ProgramName,
                    m_ProgramType,
                    m_ProgramAction,
                    m_NoArguments,
                    m_Enabled,
                    m_Comments,
                    m_Detached,
                    m_ScheduleLimit,
                    m_Priority,
                    m_Weight,
                    m_MaxRuns,
                    m_MaxFailures,
                    m_MaxRunDuration,
                    m_NlsEnv);

            addProgram(m_ProgramItem);

            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(SchedConsts.PROGRAM_ID,
                                  i2,
                                  m_Owner,
                                  m_ProgramName);
            addComboObj(m_ScreenCombo);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetProgramData"," : Error..." + e.getMessage());
    }
    }

    private String GetProgramString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
            case 1:
                selectStr.insert(0,"select owner, program_name, program_type, "
                      + " program_action, number_of_arguments, "
                      + " enabled, comments ");
                break;
            case 2:
                selectStr.insert(0,"select owner, program_name, program_type, "
                      + " program_action, number_of_arguments, "
                      + " enabled, comments, detached ");
                break;
            case 3:
            case 4:
            case 5:
                selectStr.insert(0,"select owner, program_name, program_type, "
                      + " program_action, number_of_arguments, "
                      + " enabled, comments, detached, "
                      + " schedule_limit, priority, weight, "
                      + " max_runs, max_failures, max_run_duration, nls_env ");
                break;
        }
        return selectStr.toString();
    }

    public SchedDataArea.ProgramItem GetProgramItem(String   mOwner,
                                                    String   mProgramName,
                                                    int      mAddUpdate) {
    try {
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(GetProgramString()
                + " from DBA_SCHEDULER_PROGRAMS "
                + " where owner = ? and program_name = ?");

        }
        else {

            getItemStmt = conn.prepareStatement(GetProgramString()
                + " from ALL_SCHEDULER_PROGRAMS "
                + " where owner = ? and program_name = ?");
        }

        // getItemStmt.setString(1, getUserName().toUpperCase());
        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mProgramName.toUpperCase());

        // Get the programs data.

        ResultSet rset = getItemStmt.executeQuery();
        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ProgramName = rset.getString(2);
            String m_ProgramType = rset.getString(3);
            String m_ProgramAction = rset.getString(4);
            int m_NoArguments = rset.getInt(5);
            String m_Enabled = rset.getString(6);
            String m_Comments = rset.getString(7);

            String m_Detached = "";
            String m_ScheduleLimit = "";
            int    m_Priority = 0;
            int    m_Weight = 0;
            int    m_MaxRuns = 0;
            int    m_MaxFailures = 0;
            String m_MaxRunDuration = "";
            String m_NlsEnv = "";

            switch (versionNo) {
                case 2:
                    m_Detached = rset.getString(8);
                    break;
                case 3:
                case 4:
                case 5:
                    m_Detached = rset.getString(8);
                    m_ScheduleLimit = rset.getString(9);
                    m_Priority = rset.getInt(10);
                    m_Weight = rset.getInt(11);
                    m_MaxRuns = rset.getInt(12);
                    m_MaxFailures = rset.getInt(13);
                    m_MaxRunDuration = rset.getString(14);
                    m_NlsEnv = rset.getString(15);
                    break;
            }

            if (mAddUpdate == 0) {
                m_ProgramItem = new ProgramItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ProgramName,
                    m_ProgramType,
                    m_ProgramAction,
                    m_NoArguments,
                    m_Enabled,
                    m_Comments,
                    m_Detached,
                    m_ScheduleLimit,
                    m_Priority,
                    m_Weight,
                    m_MaxRuns,
                    m_MaxFailures,
                    m_MaxRunDuration,
                    m_NlsEnv);


                addProgram(m_ProgramItem);

                screenCombo m_ScreenCombo =
                    new screenCombo(SchedConsts.PROGRAM_ID,
                                  getMaxItemId(SchedConsts.PROGRAM_ID) + 1,
                                  m_Owner,
                                  m_ProgramName);
                addComboObj(m_ScreenCombo);
            }
            if (mAddUpdate == 1) {
                m_ProgramItem = updateProgramItem(
                    m_Owner,
                    m_ProgramName,
                    m_ProgramType,
                    m_ProgramAction,
                    m_NoArguments,
                    m_Enabled,
                    m_Comments,
                    m_Detached,
                    m_ScheduleLimit,
                    m_Priority,
                    m_Weight,
                    m_MaxRuns,
                    m_MaxFailures,
                    m_MaxRunDuration,
                    m_NlsEnv);
            }
        }

        rset.close();
        getItemStmt.close();

        return m_ProgramItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetProgramItem"," : Error..." + e.getMessage());
        return null;
    }
    }


    public void GetProgramArgsData() {
    try {
        // Get the program arguments data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(
                "select owner, program_name, argument_name, "
                + " argument_position, argument_type, metadata_attribute, "
                + " default_value, out_argument "
                + " from DBA_SCHEDULER_PROGRAM_ARGS "
                + " order by owner, program_name, argument_position");
        }
        else {
            getDataStmt = conn.prepareStatement(
                "select owner, program_name, argument_name, "
                + " argument_position, argument_type, metadata_attribute, "
                + " default_value, out_argument "
                + " from ALL_SCHEDULER_PROGRAM_ARGS "
                + " order by owner, program_name, argument_position");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ProgramName = rset.getString(2);
            String m_ArgumentName = rset.getString(3);
            int m_ArgumentPosition = rset.getInt(4);
            String m_ArgumentType = rset.getString(5);
            String m_MetadataAttribute = rset.getString(6);
            String m_DefaultValue = rset.getString(7);
            String m_OutArgument = rset.getString(8);

            m_ProgramArgsItem = new ProgramArgsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ProgramName,
                    m_ArgumentName,
                    m_ArgumentPosition,
                    m_ArgumentType,
                    m_MetadataAttribute,
                    m_DefaultValue,
                    m_OutArgument);

            addProgramArgs(m_ProgramArgsItem);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetProgramArgsData"," : Error..." + e.getMessage());
    }

    }


    public SchedDataArea.ProgramArgsItem GetProgramArgsItem(
                   String        mOwner,
                   String        mProgramName,
                   int           mProgramArgsPos,
                   int           mAddUpdate) {
    try {
        // Get the program arguments data.

        if (dbaNo == 1) {
            getItemStmt = conn.prepareStatement(
                "select owner, program_name, argument_name, "
                + " argument_position, argument_type, metadata_attribute, "
                + " default_value, out_argument "
                + " from DBA_SCHEDULER_PROGRAM_ARGS "
                + " where owner = ? and program_name = ? and argument_position = ?");
        }
        else {
            getItemStmt = conn.prepareStatement(
                "select owner, program_name, argument_name, "
                + " argument_position, argument_type, metadata_attribute, "
                + " default_value, out_argument "
                + " from ALL_SCHEDULER_PROGRAM_ARGS "
                + " where owner = ? and program_name = ? and argument_position = ?");
        }

        // getItemStmt.setString(1, getUserName().toUpperCase());
        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mProgramName.toUpperCase());
        getItemStmt.setInt(3, mProgramArgsPos);

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ProgramName = rset.getString(2);
            String m_ArgumentName = rset.getString(3);
            int m_ArgumentPosition = rset.getInt(4);
            String m_ArgumentType = rset.getString(5);
            String m_MetadataAttribute = rset.getString(6);
            String m_DefaultValue = rset.getString(7);
            String m_OutArgument = rset.getString(8);

            if (mAddUpdate == 0) {
                m_ProgramArgsItem = new ProgramArgsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ProgramName,
                    m_ArgumentName,
                    m_ArgumentPosition,
                    m_ArgumentType,
                    m_MetadataAttribute,
                    m_DefaultValue,
                    m_OutArgument);

                addProgramArgs(m_ProgramArgsItem);
            }
            if (mAddUpdate == 1) {
                m_ProgramArgsItem = updateProgramArgsItem(
                    m_Owner,
                    m_ProgramName,
                    m_ArgumentName,
                    m_ArgumentPosition,
                    m_ArgumentType,
                    m_MetadataAttribute,
                    m_DefaultValue,
                    m_OutArgument);
            }
        }
        rset.close();
        getItemStmt.close();
        return m_ProgramArgsItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetProgramArgsData"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetScheduleData() {
    try {
        // Get the schedules data.

        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(GetScheduleString() 
                + " from DBA_SCHEDULER_SCHEDULES "
                + " order by owner, schedule_name");
        }
        else {
            getDataStmt = conn.prepareStatement(GetScheduleString() 
                + " from ALL_SCHEDULER_SCHEDULES "
                + " order by owner, schedule_name");
        }

        ResultSet rset = getDataStmt.executeQuery();
        int i2 = 0;

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ScheduleName = rset.getString(2);
            String m_StartDate = rset.getString(3);
            String m_RepeatInterval = rset.getString(4);
            String m_EndDate = rset.getString(5);
            String m_Comments = rset.getString(6);

            String m_ScheduleType = "";
            String m_EventQueueOwner = "";
            String m_EventQueueName = "";
            String m_EventQueueAgent = "";
            String m_EventCondition = "";
            String m_FileWatcherOwner = "";
            String m_FileWatcherName = "";

            switch (versionNo) {
                case 2:
                case 3:
                    m_ScheduleType = rset.getString(7);
                    m_EventQueueOwner = rset.getString(8);
                    m_EventQueueName = rset.getString(9);
                    m_EventQueueAgent = rset.getString(10);
                    m_EventCondition = rset.getString(11);
                    break;
                case 4:
                case 5:
                    m_ScheduleType = rset.getString(7);
                    m_EventQueueOwner = rset.getString(8);
                    m_EventQueueName = rset.getString(9);
                    m_EventQueueAgent = rset.getString(10);
                    m_EventCondition = rset.getString(11);
                    m_FileWatcherOwner = rset.getString(12);
                    m_FileWatcherName = rset.getString(13);
                    break;
            }

            m_ScheduleItem = new ScheduleItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_Comments,
                    m_ScheduleType,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_FileWatcherOwner,
                    m_FileWatcherName);

            addSchedule(m_ScheduleItem);

            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(SchedConsts.SCHEDULE_ID,
                                  i2,
                                  m_Owner,
                                  m_ScheduleName);
            addComboObj(m_ScreenCombo);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetScheduleData"," : Error..." + e.getMessage());
    }
    }


    private String GetScheduleString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
            case 1:
                selectStr.insert(0,"select owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), comments ");
                break;
            case 2:
                selectStr.insert(0,"select owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), comments, "
                      + " schedule_type, event_queue_owner, event_queue_name, "
                      + " event_queue_agent, event_condition ");
                break;
            case 3:
                selectStr.insert(0,"select owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), comments, "
                      + " schedule_type, event_queue_owner, event_queue_name, "
                      + " event_queue_agent, event_condition ");
                break;
            case 4:
            case 5:
                selectStr.insert(0,"select owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), comments, "
                      + " schedule_type, event_queue_owner, event_queue_name, "
                      + " event_queue_agent, event_condition, "
                      + " file_watcher_owner, file_watcher_name ");
                break;
        }

        return selectStr.toString();

    }

    public SchedDataArea.ScheduleItem GetScheduleItem(String   mOwner,
                                                      String   mScheduleName,
                                                      int      mAddUpdate) {
    try {
        // Get the schedule data item.
        if (dbaNo == 1) {
            getItemStmt = conn.prepareStatement(GetScheduleString() 
                + " from DBA_SCHEDULER_SCHEDULES "
                + " where owner = ? and schedule_name = ?");

        }
        else {
            getItemStmt = conn.prepareStatement(GetScheduleString() 
                + " from ALL_SCHEDULER_SCHEDULES "
                + " where owner = ? and schedule_name = ?");
        }

        getItemStmt.setString(1, getUserName().toUpperCase());
        getItemStmt.setString(2, mScheduleName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ScheduleName = rset.getString(2);
            String m_StartDate = rset.getString(3);
            String m_RepeatInterval = rset.getString(4);
            String m_EndDate = rset.getString(5);
            String m_Comments = rset.getString(6);

            String m_ScheduleType = "";
            String m_EventQueueOwner = "";
            String m_EventQueueName = "";
            String m_EventQueueAgent = "";
            String m_EventCondition = "";
            String m_FileWatcherOwner = "";
            String m_FileWatcherName = "";

            switch (versionNo) {
                case 2: case 3:
                    m_ScheduleType = rset.getString(7);
                    m_EventQueueOwner = rset.getString(8);
                    m_EventQueueName = rset.getString(9);
                    m_EventQueueAgent = rset.getString(10);
                    m_EventCondition = rset.getString(11);
                    break;
                case 4:
                case 5:
                    m_ScheduleType = rset.getString(7);
                    m_EventQueueOwner = rset.getString(8);
                    m_EventQueueName = rset.getString(9);
                    m_EventQueueAgent = rset.getString(10);
                    m_EventCondition = rset.getString(11);
                    m_FileWatcherOwner = rset.getString(12);
                    m_FileWatcherName = rset.getString(13);
                    break;
            }

            if (mAddUpdate == 0) {
                m_ScheduleItem = new ScheduleItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_Comments,
                    m_ScheduleType,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_FileWatcherOwner,
                    m_FileWatcherName);

                addSchedule(m_ScheduleItem);

                screenCombo m_ScreenCombo =
                    new screenCombo(SchedConsts.SCHEDULE_ID,
                                  getMaxItemId(SchedConsts.SCHEDULE_ID) + 1,
                                  m_Owner,
                                  m_ScheduleName);
                addComboObj(m_ScreenCombo);
            }
            if (mAddUpdate == 1) {
                m_ScheduleItem = updateScheduleItem(
                    m_Owner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_Comments,
                    m_ScheduleType,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_FileWatcherOwner,
                    m_FileWatcherName);
            }
        }
        rset.close();
        getItemStmt.close();

        return m_ScheduleItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetScheduleItem"," : Error..." + e.getMessage());
        return null;
    }
    }


    public void GetJobClassData() {
    try {
        // Get the job class data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(
                "select job_class_name, resource_consumer_group, "
                + " service, logging_level, log_history, comments "
                + " from DBA_SCHEDULER_JOB_CLASSES  "
                + " order by job_class_name");
        }
        else {
            getDataStmt = conn.prepareStatement(
                "select job_class_name, resource_consumer_group, "
                + " service, logging_level, log_history, comments "
                + " from ALL_SCHEDULER_JOB_CLASSES  "
                + " order by job_class_name");
        }
        ResultSet rset = getDataStmt.executeQuery();
        int i2 = 0;

        while (rset.next()) {

            String m_JobClassName = rset.getString(1);
            String m_ResourceConsumerGroup = rset.getString(2);
            String m_Service = rset.getString(3);
            String m_LoggingLevel = rset.getString(4);
            int m_LogHistory = rset.getInt(5);
            String m_Comments = rset.getString(6);

            m_JobClassItem = new JobClassItem(
                    getNextSeqNo(),
                    m_JobClassName,
                    m_ResourceConsumerGroup,
                    m_Service,
                    m_LoggingLevel,
                    m_LogHistory,
                    m_Comments);

            addJobClass(m_JobClassItem);

            // Add the job class to the look up list.
            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(SchedConsts.JOB_CLASS_ID,
                                  i2,
                                  m_JobClassName);

            addComboObj(m_ScreenCombo);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetJobClassData"," : Error..." + e.getMessage());
    }

    }

    public SchedDataArea.JobClassItem GetJobClassItem(
          String mJobClassName, int mAddUpdate) {
    try {
        // Get the job class item data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select job_class_name, resource_consumer_group, "
                + " service, logging_level, log_history, comments "
                + " from DBA_SCHEDULER_JOB_CLASSES  "
                + " where job_class_name = ?");

        }
        else {

            getItemStmt = conn.prepareStatement(
                "select job_class_name, resource_consumer_group, "
                + " service, logging_level, log_history, comments "
                + " from ALL_SCHEDULER_JOB_CLASSES  "
                + " where job_class_name = ?");

        }

        getItemStmt.setString(1, mJobClassName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_JobClassName = rset.getString(1);
            String m_ResourceConsumerGroup = rset.getString(2);
            String m_Service = rset.getString(3);
            String m_LoggingLevel = rset.getString(4);
            int m_LogHistory = rset.getInt(5);
            String m_Comments = rset.getString(6);

            if (mAddUpdate == 0) {
                m_JobClassItem = new JobClassItem(
                    getNextSeqNo(),
                    m_JobClassName,
                    m_ResourceConsumerGroup,
                    m_Service,
                    m_LoggingLevel,
                    m_LogHistory,
                    m_Comments);

                addJobClass(m_JobClassItem);

                screenCombo m_ScreenCombo =
                    new screenCombo(SchedConsts.JOB_CLASS_ID,
                                  getMaxItemId(SchedConsts.JOB_CLASS_ID) + 1,
                                  m_JobClassName);

                addComboObj(m_ScreenCombo);
            }
            if (mAddUpdate == 1) {
                m_JobClassItem = updateJobClassItem(
                    m_JobClassName,
                    m_ResourceConsumerGroup,
                    m_Service,
                    m_LoggingLevel,
                    m_LogHistory,
                    m_Comments);
            }
        }
        rset.close();
        getItemStmt.close();

        return m_JobClassItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobClassData"," : Error..." + e.getMessage());
        return null;
    }
    }


    public void GetWindowData() {
    try {
        // Get the window data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(GetWindowString() 
                + " from DBA_SCHEDULER_WINDOWS "
                + " order by window_name");
        }
        else {
            getDataStmt = conn.prepareStatement(GetWindowString() 
                + " from ALL_SCHEDULER_WINDOWS "
                + " order by window_name");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_WindowName = rset.getString(1);
            String m_ResourcePlan = rset.getString(2);
            String m_ScheduleOwner = rset.getString(3);
            String m_ScheduleName = rset.getString(4);
            String m_StartDate = rset.getString(5);
            String m_RepeatInterval = rset.getString(6);
            String m_EndDate = rset.getString(7);
            String m_Duration = rset.getString(8);
            String m_WindowPriority = rset.getString(9);
            String m_NextStartDate = rset.getString(10);
            String m_LastStartDate = rset.getString(11);
            String m_Enabled = rset.getString(12);
            String m_Active = rset.getString(13);
            String m_Comments = rset.getString(14);

            String m_ScheduleType = "";
            String m_ManualOpenTime = "";
            String m_ManualDuration = "";

            switch (versionNo) {
                case 2:
                case 3:
                case 4:
                case 5:
                    m_ScheduleType = rset.getString(15);
                    m_ManualOpenTime = rset.getString(16);
                    m_ManualDuration = rset.getString(17);
                    break;
            }

            m_WindowItem = new WindowItem(
                    getNextSeqNo(),
                    m_WindowName,
                    m_ResourcePlan,
                    m_ScheduleOwner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_Duration,
                    m_WindowPriority,
                    m_NextStartDate,
                    m_LastStartDate,
                    m_Enabled,
                    m_Active,
                    m_Comments,
                    m_ScheduleType,
                    m_ManualOpenTime,
                    m_ManualDuration);

            addWindow(m_WindowItem);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowData"," : Error..." + e.getMessage());
    }
    }

    private String GetWindowString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
            case 1:
                selectStr.insert(0,"select window_name, resource_plan, "
                      + " schedule_owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), duration, "
                      + " window_priority, to_char(next_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), enabled, "
                      + " active, comments ");
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                selectStr.insert(0,"select window_name, resource_plan, "
                      + " schedule_owner, schedule_name, "
                      + " to_char(start_date, 'YYYY-MM-DD HH24:MI:SS'), repeat_interval, "
                      + " to_char(end_date, 'YYYY-MM-DD HH24:MI:SS'), duration, "
                      + " window_priority, to_char(next_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                      + " to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), enabled, "
                      + " active, comments, schedule_type, "
                      + " to_char(manual_open_time, 'YYYY-MM-DD HH24:MI:SS'), manual_duration ");
                break;
        }

        return selectStr.toString();
    }


    public SchedDataArea.WindowItem GetWindowItem(String mWindowName,
                                                  int mAddUpdate)   {

    try {
        // Get the window data.
        if (dbaNo == 1) {
            getItemStmt = conn.prepareStatement(GetWindowString() 
                + " from DBA_SCHEDULER_WINDOWS "
                + " where window_name = ?");
        }
        else {
            getItemStmt = conn.prepareStatement(GetWindowString() 
                + " from ALL_SCHEDULER_WINDOWS "
                + " where window_name = ?");
        }

        getItemStmt.setString(1, mWindowName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_WindowName = rset.getString(1);
            String m_ResourcePlan = rset.getString(2);
            String m_ScheduleOwner = rset.getString(3);
            String m_ScheduleName = rset.getString(4);

            String m_StartDate = rset.getString(5);
            String m_RepeatInterval = rset.getString(6);
            String m_EndDate = rset.getString(7);
            String m_Duration = rset.getString(8);
            String m_WindowPriority = rset.getString(9);
            String m_NextStartDate = rset.getString(10);
            String m_LastStartDate = rset.getString(11);
            String m_Enabled = rset.getString(12);
            String m_Active = rset.getString(13);
            String m_Comments = rset.getString(14);

            String m_ScheduleType = "";
            String m_ManualOpenTime = "";
            String m_ManualDuration = "";

            switch (versionNo) {
                case 2:
                case 3:
                case 4:
                case 5:
                    m_ScheduleType = rset.getString(15);
                    m_ManualOpenTime = rset.getString(16);
                    m_ManualDuration = rset.getString(17);
                    break;
            }

            if (mAddUpdate == 0) {
                m_WindowItem = new WindowItem(
                    getNextSeqNo(),
                    m_WindowName,
                    m_ResourcePlan,
                    m_ScheduleOwner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_Duration,
                    m_WindowPriority,
                    m_NextStartDate,
                    m_LastStartDate,
                    m_Enabled,
                    m_Active,
                    m_Comments,
                    m_ScheduleType,
                    m_ManualOpenTime,
                    m_ManualDuration);

                addWindow(m_WindowItem);
            }
            if (mAddUpdate == 1) {
                m_WindowItem = updateWindowItem(
                    m_WindowName,
                    m_ResourcePlan,
                    m_ScheduleOwner,
                    m_ScheduleName,
                    m_StartDate,
                    m_RepeatInterval,
                    m_EndDate,
                    m_Duration,
                    m_WindowPriority,
                    m_NextStartDate,
                    m_LastStartDate,
                    m_Enabled,
                    m_Active,
                    m_Comments,
                    m_ScheduleType,
                    m_ManualOpenTime,
                    m_ManualDuration);
            }
        }
        rset.close();
        getItemStmt.close();

        return m_WindowItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowItem"," : Error..." + e.getMessage());
        return null;
    }
    }


    public void GetWindowGroupData() {
    try {
        // Get the window group data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select window_group_name, enabled, "
                + " number_of_windows, next_start_date, comments "
                + " from DBA_SCHEDULER_WINDOW_GROUPS "
                + " order by window_group_name");

        }
        else {

            getDataStmt = conn.prepareStatement(
                "select window_group_name, enabled, "
                + " number_of_windows, next_start_date, comments "
                + " from ALL_SCHEDULER_WINDOW_GROUPS "
                + " order by window_group_name");
        }

        ResultSet rset = getDataStmt.executeQuery();
        int i2 = 0;
        while (rset.next()) {

            String m_WindowGroupName = rset.getString(1);
            String m_Enabled = rset.getString(2);
            int    m_NumberOfWindows = rset.getInt(3);
            String m_NextStartDate = rset.getString(4);
            String m_Comments = rset.getString(5);

            m_WindowGroupItem = new WindowGroupItem(
                    getNextSeqNo(),
                    m_WindowGroupName,
                    m_Enabled,
                    m_NumberOfWindows,
                    m_NextStartDate,
                    m_Comments);

            addWindowGroup(m_WindowGroupItem);

            // Add the window group to the look up list.
            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(SchedConsts.WINDOW_GROUP_ID,
                                  i2,
                                  m_WindowGroupName);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowGroupData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.WindowGroupItem GetWindowGroupItem(
                            String mWindowGroupName,
                            int      mAddUpdate) {
    try {
        // Get the window group data item.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select window_group_name, enabled, "
                + " number_of_windows, next_start_date, comments "
                + " from DBA_SCHEDULER_WINDOW_GROUPS "
                + " where window_group_name = ?");

        }
        else {

            getItemStmt = conn.prepareStatement(
                "select window_group_name, enabled, "
                + " number_of_windows, next_start_date, comments "
                + " from ALL_SCHEDULER_WINDOW_GROUPS "
                + " where window_group_name = ?");
        }

        getItemStmt.setString(1, mWindowGroupName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {
            String m_WindowGroupName = rset.getString(1);
            String m_Enabled = rset.getString(2);
            int    m_NumberOfWindows = rset.getInt(3);
            String m_NextStartDate = rset.getString(4);
            String m_Comments = rset.getString(5);

            if (mAddUpdate == 0) {
                m_WindowGroupItem = new WindowGroupItem(
                    getNextSeqNo(),
                    m_WindowGroupName,
                    m_Enabled,
                    m_NumberOfWindows,
                    m_NextStartDate,
                    m_Comments);

                addWindowGroup(m_WindowGroupItem);

            }
            if (mAddUpdate == 1) {
                m_WindowGroupItem = updateWindowGroupItem(
                    m_WindowGroupName,
                    m_Enabled,
                    m_NumberOfWindows,
                    m_NextStartDate,
                    m_Comments);
            }
        }
        rset.close();
        getItemStmt.close();

        return m_WindowGroupItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowGroupItem"," : Error..." + e.getMessage());
        return null;
    }

    }

    public void GetWinGroupMembersData() {
    try {
        // Get the window group members data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select window_group_name, window_name "
                + " from DBA_SCHEDULER_WINGROUP_MEMBERS "
                + " order by window_group_name, window_name");

        }
        else {

            getDataStmt = conn.prepareStatement(
                "select window_group_name, window_name "
                + " from ALL_SCHEDULER_WINGROUP_MEMBERS "
                + " order by window_group_name, window_name");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {
            String m_WindowGroupName = rset.getString(1);
            String m_WindowName = rset.getString(2);

            m_WinGroupMembersItem = new WinGroupMembersItem(
                    getNextSeqNo(),
                    m_WindowGroupName,
                    m_WindowName);

            addWinGroupMembers(m_WinGroupMembersItem);
        }
        rset.close();
        getDataStmt.close();

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowGroupData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.WinGroupMembersItem GetWinGroupMemberItem(
                          String        mWindowName,
                          String        mWindowGroupName) {
    try {
        // Get the window group members data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select window_group_name, window_name "
                + " from DBA_SCHEDULER_WINGROUP_MEMBERS "
                + " where window_group_name = ? "
                + "   and window_name = ? ");

        }
        else {

            getItemStmt = conn.prepareStatement(
                "select window_group_name, window_name "
                + " from ALL_SCHEDULER_WINGROUP_MEMBERS "
                + " where window_group_name = ? "
                + "   and window_name = ? ");
        }

        getItemStmt.setString(1, mWindowGroupName.toUpperCase());
        getItemStmt.setString(2, mWindowName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {
            String m_WindowGroupName = rset.getString(1);
            String m_WindowName = rset.getString(2);

            m_WinGroupMembersItem = new WinGroupMembersItem(
                    getNextSeqNo(),
                    m_WindowGroupName,
                    m_WindowName);

            addWinGroupMembers(m_WinGroupMembersItem);
        }
        rset.close();
        getItemStmt.close();

        return m_WinGroupMembersItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWinGroupMemberItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetJobsRunningData(SchedGlobalData mGlobalData) {
    try {
        // Get the job arguments data.

        ResultSet rset = getJobsRunningDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            int    m_SessionId = rset.getInt(3);
            int    m_SlaveProcessId = rset.getInt(4);
            int    m_RunningInstance = rset.getInt(5);
            String m_ResourceConsumerGroup = rset.getString(6);
            String m_ElapsedTime = rset.getString(7);
            String m_CpuUsed = rset.getString(8);

            String m_JobSubname = "";
            int    m_SlaveOsProcessId = 0;
            String m_JobStyle = "";
            String m_Detached = "";
            String m_DestinationOwner = "";
            String m_Destination = "";
            String m_CredentialOwner = "";
            String m_CredentialName = "";

            switch (versionNo) {
                case 2:
                    m_JobSubname = rset.getString(9);
                    m_SlaveOsProcessId = rset.getInt(10);
                    break;
                case 3:
                    m_JobSubname = rset.getString(9);
                    m_SlaveOsProcessId = rset.getInt(10);
                    m_JobStyle = rset.getString(11);
                    m_Detached = rset.getString(12);
                    break;
                case 4:
                case 5:
                    m_JobSubname = rset.getString(9);
                    m_SlaveOsProcessId = rset.getInt(10);
                    m_JobStyle = rset.getString(11);
                    m_Detached = rset.getString(12);
                    m_DestinationOwner = rset.getString(13);
                    m_Destination = rset.getString(14);
                    m_CredentialOwner = rset.getString(15);
                    m_CredentialName = rset.getString(16);
                    break;
            }

            SchedGlobalData.JobsRunningItem m_JobsRunningItem = mGlobalData.new JobsRunningItem(
                    getDatabaseName(),
                    m_Owner,
                    m_JobName,
                    m_SessionId,
                    m_SlaveProcessId,
                    m_RunningInstance,
                    m_ResourceConsumerGroup,
                    m_ElapsedTime,
                    m_CpuUsed,
                    m_JobSubname,
                    m_SlaveOsProcessId,
                    m_JobStyle,
                    m_Detached,
                    m_DestinationOwner,
                    m_Destination,
                    m_CredentialOwner,
                    m_CredentialName);

            mGlobalData.addJobsRunning(m_JobsRunningItem);

        }
        rset.close();

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobsRunningData"," : Error..." + e.getMessage());
    }
    }



    public void GetChainsRunningData(SchedGlobalData mGlobalData) {
    try {
        // Get the chain running data.

        ResultSet rset = getChainsRunningDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            String m_JobSubName = rset.getString(3);
            String m_ChainOwner = rset.getString(4);
            String m_ChainName = rset.getString(5);
            String m_StepName = rset.getString(6);
            String m_State = rset.getString(7);
            int m_ErrorCode = rset.getInt(8);
            String m_Completed = rset.getString(9);
            String m_StartDate = rset.getString(10);
            String m_EndDate = rset.getString(11);
            String m_Duration = rset.getString(12);
            String m_Skip = rset.getString(13);
            String m_Pause = rset.getString(14);
            String m_RestartOnRecovery = rset.getString(15);
            String m_StepJobSubname = rset.getString(16);
            int m_StepJobLogId = rset.getInt(17);
            String m_RestartOnFailure = rset.getString(18);

            SchedGlobalData.ChainsRunningItem m_ChainsRunningItem = mGlobalData.new ChainsRunningItem(
                    getDatabaseName(),
                    m_Owner,
                    m_JobName,
                    m_JobSubName,
                    m_ChainOwner,
                    m_ChainName,
                    m_StepName,
                    m_State,
                    m_ErrorCode,
                    m_Completed,
                    m_StartDate,
                    m_EndDate,
                    m_Duration,
                    m_Skip,
                    m_Pause,
                    m_RestartOnRecovery,
                    m_StepJobSubname,
                    m_StepJobLogId,
                    m_RestartOnFailure);

            mGlobalData.addChainsRunning(m_ChainsRunningItem);

        }
        rset.close();

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetChainsRunningData"," : Error..." + e.getMessage());
    }
    }


    public void GetChainsData() {
    try {
        // Get the chain data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, chain_name, rule_set_owner, "
                + " rule_set_name, number_of_rules, "
                + " number_of_steps, enabled, "
                + " evaluation_interval, user_rule_set, comments "
                + " from DBA_SCHEDULER_CHAINS "
                + " order by owner, chain_name");

        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, chain_name, rule_set_owner, "
                + " rule_set_name, number_of_rules, "
                + " number_of_steps, enabled, "
                + " evaluation_interval, user_rule_set, comments "
                + " from ALL_SCHEDULER_CHAINS "
                + " order by owner, chain_name");

        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ChainName = rset.getString(2);
            String m_RuleSetOwner = rset.getString(3);
            String m_RuleSetName = rset.getString(4);
            int m_NumberOfRules = rset.getInt(5);
            int m_NumberOfSteps = rset.getInt(6);
            String m_Enabled = rset.getString(7);
            String m_EvaluationInterval = rset.getString(8);
            String m_UserRuleSet = rset.getString(9);
            String m_Comments = rset.getString(10);

            m_ChainsItem = new ChainsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ChainName,
                    m_RuleSetOwner,
                    m_RuleSetName,
                    m_NumberOfRules,
                    m_NumberOfSteps,
                    m_Enabled,
                    m_EvaluationInterval,
                    m_UserRuleSet,
                    m_Comments);

            addChains(m_ChainsItem);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetChainData"," : Error..." + e.getMessage());
    }
    }


    public SchedDataArea.ChainsItem GetChainsItem(
                                      String   mOwner,
                                      String   mChainName,
                                      int      mAddUpdate) {
    try {
        // Get the chain data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select owner, chain_name, rule_set_owner, "
                + " rule_set_name, number_of_rules, "
                + " number_of_steps, enabled, "
                + " evaluation_interval, user_rule_set, comments "
                + " from DBA_SCHEDULER_CHAINS "
                + " where owner = ? and chain_name = ?");

        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, chain_name, rule_set_owner, "
                + " rule_set_name, number_of_rules, "
                + " number_of_steps, enabled, "
                + " evaluation_interval, user_rule_set, comments "
                + " from ALL_SCHEDULER_CHAINS "
                + " where owner = ? and chain_name = ?");

        }

        // getItemStmt.setString(1, getUserName().toUpperCase());
        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mChainName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ChainName = rset.getString(2);
            String m_RuleSetOwner = rset.getString(3);
            String m_RuleSetName = rset.getString(4);
            int m_NumberOfRules = rset.getInt(5);
            int m_NumberOfSteps = rset.getInt(6);
            String m_Enabled = rset.getString(7);
            String m_EvaluationInterval = rset.getString(8);
            String m_UserRuleSet = rset.getString(9);
            String m_Comments = rset.getString(10);

            if (mAddUpdate == 0) {
                m_ChainsItem = new ChainsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ChainName,
                    m_RuleSetOwner,
                    m_RuleSetName,
                    m_NumberOfRules,
                    m_NumberOfSteps,
                    m_Enabled,
                    m_EvaluationInterval,
                    m_UserRuleSet,
                    m_Comments);

                addChains(m_ChainsItem);
            }
            if (mAddUpdate == 1) {
                m_ChainsItem = updateChainItem(
                    m_Owner,
                    m_ChainName,
                    m_RuleSetOwner,
                    m_RuleSetName,
                    m_NumberOfRules,
                    m_NumberOfSteps,
                    m_Enabled,
                    m_EvaluationInterval,
                    m_UserRuleSet,
                    m_Comments);
            }
        }
        rset.close();
        getItemStmt.close();
        return m_ChainsItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetChainItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetChainStepsData() {
    try {
        // Get the chain steps data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(GetChainStepsString() 
                + " from DBA_SCHEDULER_CHAIN_STEPS "
                + " order by owner, chain_name, step_name");
        }
        else {
            getDataStmt = conn.prepareStatement(GetChainStepsString()
                + " from ALL_SCHEDULER_CHAIN_STEPS "
                + " order by owner, chain_name, step_name");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ChainName = rset.getString(2);
            String m_StepName = rset.getString(3);
            String m_ProgramOwner = rset.getString(4);
            String m_ProgramName = rset.getString(5);
            String m_EventScheduleOwner = rset.getString(6);
            String m_EventScheduleName = rset.getString(7);
            String m_EventQueueOwner = rset.getString(8);
            String m_EventQueueName = rset.getString(9);
            String m_EventQueueAgent = rset.getString(10);
            String m_EventCondition = rset.getString(11);
            String m_Skip = rset.getString(12);
            String m_Pause = rset.getString(13);
            String m_RestartOnRecovery = rset.getString(14);
            String m_StepType = rset.getString(15);
            String m_Timeout = rset.getString(16);

            String m_CredentialOwner = "";
            String m_CredentialName = "";
            String m_Destination = "";
            String m_RestartOnFailure = "";

            switch (versionNo) {
                case 3: 
                    m_CredentialOwner = rset.getString(17);
                    m_CredentialName = rset.getString(18);
                    m_Destination = rset.getString(19);
                    break;
                case 4:
                case 5:
                    m_CredentialOwner = rset.getString(17);
                    m_CredentialName = rset.getString(18);
                    m_Destination = rset.getString(19);
                    m_RestartOnFailure = rset.getString(20); 
                    break;
            }

            m_ChainStepsItem = new ChainStepsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ChainName,
                    m_StepName,
                    m_ProgramOwner,
                    m_ProgramName,
                    m_EventScheduleOwner,
                    m_EventScheduleName,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_Skip,
                    m_Pause,
                    m_RestartOnRecovery,
                    m_StepType,
                    m_Timeout,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_Destination,
                    m_RestartOnFailure);

            addChainSteps(m_ChainStepsItem);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetChainStepsData"," : Error..." + e.getMessage());
    }
    }

    private String GetChainStepsString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
            case 1: case 2:
                selectStr.insert(0,"select owner, chain_name, "
                      + " step_name, program_owner, program_name, "
                      + " event_schedule_owner, event_schedule_name, "
                      + " event_queue_owner, event_queue_name, event_queue_agent, "
                      + " event_condition, skip, pause, restart_on_recovery, "
                      + " step_type, timeout ");
                break;
            case 3:
                selectStr.insert(0,"select owner, chain_name, "
                      + " step_name, program_owner, program_name, "
                      + " event_schedule_owner, event_schedule_name, "
                      + " event_queue_owner, event_queue_name, event_queue_agent, "
                      + " event_condition, skip, pause, restart_on_recovery, "
                      + " step_type, timeout, credential_owner, "
                      + " credential_name, destination ");

                break;
            case 4:
            case 5:
                selectStr.insert(0,"select owner, chain_name, "
                      + " step_name, program_owner, program_name, "
                      + " event_schedule_owner, event_schedule_name, "
                      + " event_queue_owner, event_queue_name, event_queue_agent, "
                      + " event_condition, skip, pause, restart_on_recovery, "
                      + " step_type, timeout, credential_owner, "
                      + " credential_name, destination, restart_on_failure ");
                break;
        }

        return selectStr.toString();
    }


    public SchedDataArea.ChainStepsItem GetChainStepsItem(
                             String mOwner,
                             String mChainName, 
                             String mChainStepName,
                             int mAddUpdate) {
    try {
        // Get the chain steps data.
        if (dbaNo == 1) {
            getItemStmt = conn.prepareStatement(GetChainStepsString() 
                + " from DBA_SCHEDULER_CHAIN_STEPS "
                + " where owner = ? and chain_name = ? and step_name = ?");
        }
        else {
            getItemStmt = conn.prepareStatement(GetChainStepsString()
                + " from ALL_SCHEDULER_CHAIN_STEPS "
                + " where owner = ? and chain_name = ? and step_name = ?");
        }

        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mChainName.toUpperCase());
        getItemStmt.setString(3, mChainStepName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ChainName = rset.getString(2);
            String m_StepName = rset.getString(3);
            String m_ProgramOwner = rset.getString(4);
            String m_ProgramName = rset.getString(5);
            String m_EventScheduleOwner = rset.getString(6);
            String m_EventScheduleName = rset.getString(7);
            String m_EventQueueOwner = rset.getString(8);
            String m_EventQueueName = rset.getString(9);
            String m_EventQueueAgent = rset.getString(10);
            String m_EventCondition = rset.getString(11);
            String m_Skip = rset.getString(12);
            String m_Pause = rset.getString(13);
            String m_RestartOnRecovery = rset.getString(14);
            String m_StepType = rset.getString(15);
            String m_Timeout = rset.getString(16);

            String m_CredentialOwner = "";
            String m_CredentialName = "";
            String m_Destination = "";
            String m_RestartOnFailure = "";

            switch (versionNo) {
                case 3: 
                    m_CredentialOwner = rset.getString(17);
                    m_CredentialName = rset.getString(18);
                    m_Destination = rset.getString(19);
                    break;
                case 4:
                case 5:
                    m_CredentialOwner = rset.getString(17);
                    m_CredentialName = rset.getString(18);
                    m_Destination = rset.getString(19);
                    m_RestartOnFailure = rset.getString(20); 
                    break;
            }

            if (mAddUpdate == 0) {
                m_ChainStepsItem = new ChainStepsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ChainName,
                    m_StepName,
                    m_ProgramOwner,
                    m_ProgramName,
                    m_EventScheduleOwner,
                    m_EventScheduleName,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_Skip,
                    m_Pause,
                    m_RestartOnRecovery,
                    m_StepType,
                    m_Timeout,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_Destination,
                    m_RestartOnFailure);

                addChainSteps(m_ChainStepsItem);
            }
            if (mAddUpdate == 1) {
                m_ChainStepsItem = updateChainStepItem(
                    m_Owner,
                    m_ChainName,
                    m_StepName,
                    m_ProgramOwner,
                    m_ProgramName,
                    m_EventScheduleOwner,
                    m_EventScheduleName,
                    m_EventQueueOwner,
                    m_EventQueueName,
                    m_EventQueueAgent,
                    m_EventCondition,
                    m_Skip,
                    m_Pause,
                    m_RestartOnRecovery,
                    m_StepType,
                    m_Timeout,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_Destination,
                    m_RestartOnFailure);
            }
        }
        rset.close();
        getItemStmt.close();
        return m_ChainStepsItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetChainStepsItem"," : Error..." + e.getMessage());
        return null;
    }
    }


    public void GetChainRulesData() {
    try {
        // Get the chain rules data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, chain_name, rule_owner, rule_name, "
                + " condition, action, comments "
                + " from DBA_SCHEDULER_CHAIN_RULES "
                + " order by owner, chain_name, rule_name");

        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, chain_name, rule_owner, rule_name, "
                + " condition, action, comments "
                + " from ALL_SCHEDULER_CHAIN_RULES "
                + " order by owner, chain_name, rule_name");

        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ChainName = rset.getString(2);
            String m_RuleOwner = rset.getString(3);
            String m_RuleName = rset.getString(4);
            String m_Conditions = rset.getString(5);
            String m_Action = rset.getString(6);
            String m_Comments = rset.getString(7);

            m_ChainRulesItem = new ChainRulesItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ChainName,
                    m_RuleOwner,
                    m_RuleName,
                    m_Conditions,
                    m_Action,
                    m_Comments);

            addChainRules(m_ChainRulesItem);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetChainRulesData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.ChainRulesItem GetChainRulesItem(
              String mChainOwner,
              String mChainName,
              String mChainRuleName) {
    try {
        // Get the chain rules data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select owner, chain_name, rule_owner, rule_name, "
                + " condition, action, comments "
                + " from DBA_SCHEDULER_CHAIN_RULES "
                + " where owner = ? and chain_name = ? and rule_name = ?");

        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, chain_name, rule_owner, rule_name, "
                + " condition, action, comments "
                + " from ALL_SCHEDULER_CHAIN_RULES "
                + " where owner = ? and chain_name = ? and rule_name = ?");

        }

        getItemStmt.setString(1, mChainOwner.toUpperCase());
        getItemStmt.setString(2, mChainName.toUpperCase());
        getItemStmt.setString(3, mChainRuleName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_ChainName = rset.getString(2);
            String m_RuleOwner = rset.getString(3);
            String m_RuleName = rset.getString(4);
            String m_Conditions = rset.getString(5);
            String m_Action = rset.getString(6);
            String m_Comments = rset.getString(7);

            m_ChainRulesItem = new ChainRulesItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_ChainName,
                    m_RuleOwner,
                    m_RuleName,
                    m_Conditions,
                    m_Action,
                    m_Comments);

            addChainRules(m_ChainRulesItem);

        }
        rset.close();
        getItemStmt.close();
        return m_ChainRulesItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetChainRulesItem"," : Error..." + e.getMessage());
        return null;
    }
    }


    public void SetupJobLogData(String  currentDate, String whereStmt) throws SQLException {
    try {
        String mWhereStmt = null;
        if (whereStmt == null) 
            mWhereStmt = " ";
        else {
            if (whereStmt.trim().length() > 0)
                mWhereStmt = " and " + whereStmt;
            else
                mWhereStmt = " ";
        }

        // System.out.println( "1. " + mWhereStmt);
        // Get the Job Log (Log) data.
        if (dbaNo == 1) {
            getJobLogStmt = conn.prepareStatement(GetJobLogString()
                + " from DBA_SCHEDULER_JOB_LOG "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }
        else {
            getJobLogStmt = conn.prepareStatement(GetJobLogString()
                + " from ALL_SCHEDULER_JOB_LOG "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }
        String mFromDate = currentDate + " 00:00";
        String mToDate = currentDate + " 23:59";

        getJobLogStmt.setString(1, mFromDate);
        getJobLogStmt.setString(2, mToDate);

        jlset = getJobLogStmt.executeQuery();
    }
    catch(SQLException e1) {
        SchedFile.EnterErrorEntry("SetupJobLogData"," : Error..." + e1.getMessage());
        try {
            if (jlset != null) jlset.close();
        } catch (SQLException e2) {
            ;
        }
        throw e1;
    }
    }

    public boolean getNextJobLogRow() {
    try {
        if (jlset.isClosed())
            return false;
        else
            return jlset.next();

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetNextJobLogRow"," : Error..." + e.getMessage());
        return false;
    }
    }

    public String getJobLogDate() {
    try {
        String m_LogDate = jlset.getString(2);
        return m_LogDate;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobLogDate"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void getJobLogData(SchedGlobalData mGlobalArea) {
    try {
        int    m_LogId = jlset.getInt(1);
        String m_LogDate = jlset.getString(2);
        String m_Owner = jlset.getString(3);
        String m_JobName = jlset.getString(4);
        String m_JobClass = jlset.getString(5);
        String m_Operation = jlset.getString(6);
        String m_Status = jlset.getString(7);
        String m_UserName = jlset.getString(8);
        String m_ClientId = jlset.getString(9);
        String m_GlobalUid = jlset.getString(10);
        String m_AdditionalInfo = jlset.getString(11);

        String m_JobSubName = "";
        String m_Destination = "";
        String m_CredentialOwner = "";
        String m_CredentialName = "";
        String m_DestinationOwner = "";

        if (versionNo > 1) {
            m_JobSubName = jlset.getString(12);
        }
        if (versionNo > 2) {
            m_Destination = jlset.getString(13);
        }
        if (versionNo > 3) {
            m_CredentialOwner = jlset.getString(14);
            m_CredentialName = jlset.getString(15);
            m_DestinationOwner = jlset.getString(16);
        }

        SchedGlobalData.JobLogItem m_JobLogItem = mGlobalArea.new JobLogItem(
                    mDatabaseName,
                    m_LogId,
                    m_LogDate,
                    m_Owner,
                    m_JobName,
                    m_JobClass,
                    m_Operation,
                    m_Status,
                    m_UserName,
                    m_ClientId,
                    m_GlobalUid,
                    m_AdditionalInfo,
                    m_JobSubName,
                    m_Destination,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_DestinationOwner);

        mGlobalArea.addJobLog(m_JobLogItem);

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobLogData"," : Error..." + e.getMessage());
    }
    }

    public void closeJobLogCursor() {
    try {
        jlset.close();
        getJobLogStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobLogData"," : Error..." + e.getMessage());
    }
    }

    private String GetJobLogString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
          case 1:
              selectStr.insert(0,"select log_id, "
                  + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), owner, job_name, "
                  + " job_class, operation, status, user_name, client_id, "
                  + " global_uid, additional_info ");
              break;
          case 2:
              selectStr.insert(0,"select log_id, "
                  + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), owner, job_name, "
                  + " job_class, operation, status, user_name, client_id, "
                  + " global_uid, additional_info, job_subname ");
              break;
          case 3:
              selectStr.insert(0,"select log_id, "
                  + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), owner, job_name, "
                  + " job_class, operation, status, user_name, client_id, "
                  + " global_uid, additional_info, job_subname, destination ");
              break;
          case 4:
          case 5:
              selectStr.insert(0,"select log_id, "
                  + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), owner, job_name, "
                  + " job_class, operation, status, user_name, client_id, "
                  + " global_uid, additional_info, job_subname, destination, "
                  + " credential_owner, credential_name, destination_owner ");
              break;
      }

      return selectStr.toString();
    }

    public void SetupJobDetailsLogData(String   currentDate, String whereStmt) throws SQLException {
    try {
        String mWhereStmt = null;
        if (whereStmt == null) 
            mWhereStmt = " ";
        else {
            if (whereStmt.trim().length() > 0)
                mWhereStmt = " and " + whereStmt;
            else
                mWhereStmt = " ";
        }

        // Get the Job Log (Log) data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(GetJobRunDetString()
                + " from DBA_SCHEDULER_JOB_RUN_DETAILS "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }
        else {
            getDataStmt = conn.prepareStatement(GetJobRunDetString()
                + " from ALL_SCHEDULER_JOB_RUN_DETAILS "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }

        String mFromDate = currentDate + " 00:00";
        String mToDate = currentDate + " 23:59";

        getDataStmt.setString(1, mFromDate);
        getDataStmt.setString(2, mToDate);

        jlset = getDataStmt.executeQuery();
    }
    catch(SQLException e1) {
        SchedFile.EnterErrorEntry("SetupJobDetailsLogData"," : Error..." + e1.getMessage());
        try {
            if (jlset != null) jlset.close();
        } catch (SQLException e2) {
            ;
        }
        throw e1;
    }
    }

    public boolean getNextJobDetailsLogRow() {
    try {
        if (jlset.isClosed())
            return false;
        else
            return jlset.next();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetNextJobDetailsLogRow"," : Error..." + e.getMessage());
        return false;
    }
    }

    public String getJobDetailsLogDate() {
    try {
        String m_LogDate = jlset.getString(2);
        return m_LogDate;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobDetailsLogDate"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void getJobDetailsLogData(SchedGlobalData mGlobalData) {
    try {

        int    m_LogId = jlset.getInt(1);
        String m_LogDate = jlset.getString(2);
        String m_Owner = jlset.getString(3);
        String m_JobName = jlset.getString(4);
        String m_Status = jlset.getString(5);
        int    m_ErrorNo = jlset.getInt(6);
        String m_ReqStartDate = jlset.getString(7);
        String m_ActualStartDate = jlset.getString(8);
        String m_RunDuration = jlset.getString(9);
        int    m_InstanceId = jlset.getInt(10);
        String m_SessionId = jlset.getString(11);
        String m_SlavePid = jlset.getString(12);
        String m_CpuUsed = jlset.getString(13);
        String m_AdditionalInfo = jlset.getString(14);

        String m_JobSubName = "";
        String m_Destination = "";
        String m_CredentialOwner = "";
        String m_CredentialName = "";
        String m_DestinationOwner = "";

        if (versionNo > 2) {
            m_JobSubName = jlset.getString(15);
            m_Destination = jlset.getString(16);
        }
        if (versionNo > 3) {
            m_CredentialOwner = jlset.getString(17);
            m_CredentialName = jlset.getString(18);
            m_DestinationOwner = jlset.getString(19);
        }
        SchedGlobalData.JobDetLogItem m_JobDetLogItem = mGlobalData.new JobDetLogItem(
                    mDatabaseName,
                    m_LogId,
                    m_LogDate,
                    m_Owner,
                    m_JobName,
                    m_Status,
                    m_ErrorNo,
                    m_ReqStartDate,
                    m_ActualStartDate,
                    m_RunDuration,
                    m_InstanceId,
                    m_SessionId,
                    m_SlavePid,
                    m_CpuUsed,
                    m_AdditionalInfo,
                    m_JobSubName,
                    m_Destination,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_DestinationOwner);

        mGlobalData.addJobRunDetLog(m_JobDetLogItem);
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobDetailsLogData"," : Error..." + e.getMessage());
    }
    }

    public void closeJobDetailsLogCursor() {
    try {
        jlset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("closeJobDetailsLogCursor"," : Error..." + e.getMessage());
    }
    }

    private String GetJobRunDetString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
            case 1:
            case 2:
                selectStr.insert(0,"select log_id, "
                    + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), owner, job_name, "
                    + " status, error#, to_char(req_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                    + " to_char(actual_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                    + " to_char(run_duration,'DDD HH24:MI:SS') , instance_id, "
                    + " session_id, slave_pid, to_char(cpu_used, 'DDD HH24:MI:SS'), "
                    + " additional_info ");
                break;
            case 3:
                selectStr.insert(0,"select log_id, "
                    + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), owner, job_name, "
                    + " status, error#, to_char(req_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                    + " to_char(actual_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                    + " to_char(run_duration,'DDD HH24:MI:SS') , instance_id, "
                    + " session_id, slave_pid, to_char(cpu_used, 'DDD HH24:MI:SS'), "
                    + " additional_info, job_subname, destination ");
                break;
            case 4:
            case 5:
                selectStr.insert(0,"select log_id, "
                    + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), owner, job_name, "
                    + " status, error#, to_char(req_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                    + " to_char(actual_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                    + " to_char(run_duration,'DDD HH24:MI:SS') , instance_id, "
                    + " session_id, slave_pid, to_char(cpu_used, 'DDD HH24:MI:SS'), "
                    + " additional_info, job_subname, destination, "
                    + " credential_owner, credential_name, destination_owner ");
                break;
        }

        return selectStr.toString();
    }

    public void SetupWindowDetLogData(String currentDate, String whereStmt) throws SQLException {
    try {
        String mWhereStmt = null;
        if (whereStmt == null) 
            mWhereStmt = " ";
        else {
            if (whereStmt.trim().length() > 0)
                mWhereStmt = " and " + whereStmt;
            else
                mWhereStmt = " ";
        }

        // Get the Window Details (Log) data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(GetWindowDetLogString()
                + " from DBA_SCHEDULER_WINDOW_DETAILS "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }
        else {
            getDataStmt = conn.prepareStatement(GetWindowDetLogString()
                + " from ALL_SCHEDULER_WINDOW_DETAILS "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }

        String mFromDate = currentDate + " 00:00";
        String mToDate = currentDate + " 23:59";

        getDataStmt.setString(1, mFromDate);
        getDataStmt.setString(2, mToDate);

        wlset = getDataStmt.executeQuery();
    }
    catch(SQLException e1) {
        SchedFile.EnterErrorEntry("SetupWindowDetailsData"," : Error..." + e1.getMessage());
        try {
            if (wlset != null) wlset.close();
        } catch (SQLException e2) {
            ;
        }
        throw e1;
    }
    }

    public String getWindowDetLogDate() {
    try {
        String m_LogDate = wlset.getString(2);
        return m_LogDate;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowLogDate"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void getWindowDetLogData(SchedGlobalData mGlobalData) {
    try {

        int    m_LogId = wlset.getInt(1);
        String m_LogDate = wlset.getString(2);
        String m_WindowName = wlset.getString(3);
        String m_ReqStartDate = wlset.getString(4);
        String m_WindowDuration = wlset.getString(5);
        String m_ActualDuration = wlset.getString(6);
        int    m_InstanceId = wlset.getInt(7);
        String m_AdditionalInfo = wlset.getString(8);
        String m_ActStartDate = "";

        if (versionNo > 2) {
            m_ActStartDate = wlset.getString(9);
        }

        SchedGlobalData.WindowDetLogItem m_WindowDetLogItem = mGlobalData.new WindowDetLogItem(
                    mDatabaseName,
                    m_LogId,
                    m_LogDate,
                    m_WindowName,
                    m_ReqStartDate,
                    m_ActStartDate,
                    m_WindowDuration,
                    m_ActualDuration,
                    m_InstanceId,
                    m_AdditionalInfo);

        mGlobalData.addWindowDetails(m_WindowDetLogItem);

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("getWindowDetLogData"," : Error..." + e.getMessage());
    }
    }

    public boolean getNextWindowDetailRow() {
    try {
        if (wlset.isClosed())
            return false;
        else
            return wlset.next();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetNextWindowDetailRow"," : Error..." + e.getMessage());
        return false;
    }
    }

    public void closeWindowDetLogCursor() {
    try {
        wlset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("closeWindowDetLogCursor"," : Error..." + e.getMessage());
    }
    }

    private String GetWindowDetLogString() {
        StringBuffer selectStr = new StringBuffer("a");
        selectStr.delete(0, selectStr.length());
        switch (versionNo) {
          case 1:
              selectStr.insert(0,"select log_id, "
                  + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), window_name, "
                  + " to_char(req_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                  + " to_char(window_duration,'DDD HH24:MI:SS') , "
                  + " to_char(actual_duration,'DDD HH24:MI:SS') , "
                  + " instance_id, additional_info ");


              break;
          case 2:
          case 3:
          case 4:
          case 5:
              selectStr.insert(0,"select log_id, "
                  + " to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), window_name, "
                  + " to_char(req_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                  + " to_char(window_duration,'DDD HH24:MI:SS') , "
                  + " to_char(actual_duration,'DDD HH24:MI:SS') , "
                  + " instance_id, additional_info, "
                  + " to_char(actual_start_date,  'YYYY-MM-DD HH24:MI:SS') ");
              break;
      }

      return selectStr.toString();
    }

    public String getWindowLogDate() {
    try {
        String m_LogDate = wlset.getString(2);
        return m_LogDate;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowLogDate"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void SetupWindowLogData(
                          String  currentDate, String whereStmt) throws SQLException {
    try {
        // System.out.println( "1. ");
        String mWhereStmt = null;
        if (whereStmt == null) 
            mWhereStmt = " ";
        else {
            if (whereStmt.trim().length() > 0)
                mWhereStmt = " and " + whereStmt;
            else
                mWhereStmt = " ";
        }

        // Get the Window Details (Log) data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select log_id, to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), window_name, "
                + " operation, status, user_name, client_id, "
                + " global_uid, additional_info "
                + " from DBA_SCHEDULER_WINDOW_LOG "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select log_id, to_char(log_date, 'YYYY-MM-DD HH24:MI:SS'), window_name, "
                + " operation, status, user_name, client_id, "
                + " global_uid, additional_info "
                + " from ALL_SCHEDULER_WINDOW_LOG "
                + " where log_date between TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + "                    and TO_DATE( ? ,'DD-MON-YYYY HH24:MI') "
                + mWhereStmt
                + " order by log_date");
        }
        String mFromDate = currentDate + " 00:00";
        String mToDate = currentDate + " 23:59";

        getDataStmt.setString(1, mFromDate);
        getDataStmt.setString(2, mToDate);

        wlset = getDataStmt.executeQuery();
    }
    catch(SQLException e1) {
        SchedFile.EnterErrorEntry("SetupWindowLogData"," : Error..." + e1.getMessage());
        try {
            if (wlset != null) wlset.close();
        } catch (SQLException e2) {
            ;
        }
        throw e1;
    }
    }

    public boolean getNextWindowLogRow() {
    try {
        if (wlset.isClosed())
            return false;
        else
            return wlset.next();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetNextWindowLogRow"," : Error..." + e.getMessage());
        return false;
    }
    }

    public void getWindowLogData(SchedGlobalData mGlobalArea) {
    try {
        int    m_LogId = wlset.getInt(1);
        String m_LogDate = wlset.getString(2);
        String m_WindowName = wlset.getString(3);
        String m_Operation = wlset.getString(4);
        String m_Status = wlset.getString(5);
        String m_UserName = wlset.getString(6);
        String m_ClientId = wlset.getString(7);
        String m_GlobalUid = wlset.getString(8);
        String m_AdditionalInfo = wlset.getString(9);

        SchedGlobalData.WindowLogItem m_WindowLogItem = mGlobalArea.new WindowLogItem(
                    mDatabaseName,
                    m_LogId,
                    m_LogDate,
                    m_WindowName,
                    m_Operation,
                    m_Status,
                    m_UserName,
                    m_ClientId,
                    m_GlobalUid,
                    m_AdditionalInfo);

        mGlobalArea.addWindowLog(m_WindowLogItem);
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetWindowLogData"," : Error..." + e.getMessage());
    }
    }

    public void closeWindowLogCursor() {
    try {
        wlset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("closeWindowLogCursor"," : Error..." + e.getMessage());
    }
    }

    public void GetGlobalAttributesData() {
    try {
        // Get the global attributes data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select attribute_name, value "
                + " from DBA_SCHEDULER_GLOBAL_ATTRIBUTE "
                + " order by attribute_name");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select attribute_name, value "
                + " from ALL_SCHEDULER_GLOBAL_ATTRIBUTE "
                + " order by attribute_name");

        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_AttributeName = rset.getString(1);
            String m_AttribteValue = rset.getString(2);

            m_GlobalAttributesItem = new GlobalAttributesItem(
                    getNextSeqNo(),
                    m_AttributeName,
                    m_AttribteValue);

            addGlobalAttributes(m_GlobalAttributesItem);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetGlobalAttributesData"," : Error..." + e.getMessage());
    }
    }

    public void GetGlobalAttributesItem(
                            String        mAttributeName) {
    try {
        // Get the global attributes data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select attribute_name, value "
                + " from DBA_SCHEDULER_GLOBAL_ATTRIBUTE "
                + " where attribute_name = ? "
                + " order by attribute_name");
        }
        else {

            getItemStmt = conn.prepareStatement(
                "select attribute_name, value "
                + " from ALL_SCHEDULER_GLOBAL_ATTRIBUTE "
                + " where attribute_name = ? "
                + " order by attribute_name");

        }

        getItemStmt.setString(1, mAttributeName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_AttributeName = rset.getString(1);
            String m_AttribteValue = rset.getString(2);

            m_GlobalAttributesItem = updateGlobalAttributesItem(
                    m_AttributeName,
                    m_AttribteValue);

            addGlobalAttributes(m_GlobalAttributesItem);

        }
        rset.close();
        getItemStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetGlobalAttributesItem"," : Error..." + e.getMessage());
    }
    }


    public void GetCredentialsData() {
    try {
        // Get the global attributes data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, credential_name, username, "
                + " database_role, windows_domain, comments "
                + " from DBA_SCHEDULER_CREDENTIALS "
                + " order by owner, credential_name");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, credential_name, username, "
                + " database_role, windows_domain, comments "
                + " from ALL_SCHEDULER_CREDENTIALS "
                + " order by owner, credential_name");
        }

        ResultSet rset = getDataStmt.executeQuery();
        int i1 = 18000;
        int i2 = 0;

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_CredentialName = rset.getString(2);
            String m_Username = rset.getString(3);
            String m_DatabaseRole = rset.getString(4);
            String m_WindowsDomain = rset.getString(5);
            String m_Comments = rset.getString(6);

            m_CredentialsItem = new CredentialsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_CredentialName,
                    m_Username,
                    m_DatabaseRole,
                    m_WindowsDomain,
                    m_Comments);

            addCredentials(m_CredentialsItem);

            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(SchedConsts.CREDENTIAL_ID,
                                  i2,
                                  m_Owner,
                                  m_CredentialName);
            addComboObj(m_ScreenCombo);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetCredentialsData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.CredentialsItem GetCredentialItem(
                 String mOwner,
                 String mCredentialName,
                 int mAddUpdate) {
    try {
        // Get the global attributes data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select owner, credential_name, username, "
                + " database_role, windows_domain, Comments "
                + " from DBA_SCHEDULER_CREDENTIALS "
                + " where owner = ? and credential_name = ? "
                + " order by owner, credential_name");

        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, credential_name, username, "
                + " database_role, windows_domain, Comments "
                + " from ALL_SCHEDULER_CREDENTIALS "
                + " where owner = ? and credential_name = ? "
                + " order by owner, credential_name");

        }

        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mCredentialName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_CredentialName = rset.getString(2);
            String m_Username = rset.getString(3);
            String m_DatabaseRole = rset.getString(4);
            String m_WindowsDomain = rset.getString(5);
            String m_Comments = rset.getString(6);

            if (mAddUpdate == 0) {
                m_CredentialsItem = new CredentialsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_CredentialName,
                    m_Username,
                    m_DatabaseRole,
                    m_WindowsDomain,
                    m_Comments);

                addCredentials(m_CredentialsItem);

                screenCombo m_ScreenCombo =
                    new screenCombo(SchedConsts.CREDENTIAL_ID,
                                  getMaxItemId(SchedConsts.CREDENTIAL_ID),
                                  m_Owner,
                                  m_CredentialName);
                addComboObj(m_ScreenCombo);
            }
            if (mAddUpdate == 1) {
                m_CredentialsItem = updateCredentialItem(
                    m_Owner,
                    m_CredentialName,
                    m_Username,
                    m_DatabaseRole,
                    m_WindowsDomain,
                    m_Comments);
            }
        }
        rset.close();
        getItemStmt.close();
        return m_CredentialsItem;

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetCredentialsItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetGroupsData() {
    try {
        int i2 = 0;

        // Get the groups data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, group_name, group_type, "
                + " enabled, number_of_members, comments "
                + " from DBA_SCHEDULER_GROUPS "
                + " order by owner, group_name");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, group_name, group_type, "
                + " enabled, number_of_members, comments "
                + " from ALL_SCHEDULER_GROUPS "
                + " order by owner, group_name");
        }

        ResultSet rset = getDataStmt.executeQuery();
        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_GroupName = rset.getString(2);
            String m_GroupType = rset.getString(3);
            String m_Enabled = rset.getString(4);
            int    m_NumberOfMembers = rset.getInt(5);
            String m_Comments = rset.getString(6);

            m_GroupItem = new GroupItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_GroupName,
                    m_GroupType,
                    m_Enabled,
                    m_NumberOfMembers,
                    m_Comments);

            addGroup(m_GroupItem);

            // Add the window group to the look up list.
            int lGroupId = 0;
            if (m_GroupType.equals("WINDOW"))        lGroupId = SchedConsts.WINDOW_GROUP_ID;
            if (m_GroupType.equals("DB_DEST"))       lGroupId = SchedConsts.DB_DEST_ID;
            if (m_GroupType.equals("EXTERNAL_DEST")) lGroupId = SchedConsts.EXT_DEST_ID;

            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(lGroupId,
                                i2,
                                m_GroupName);
            addComboObj(m_ScreenCombo);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetGroupsData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.GroupItem GetGroupItem(
                                   String        mGroupOwner,
                                   String        mGroupName,
                                   int           mAddUpdate) {
    try {
        // Get the groups data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select owner, group_name, group_type, "
                + " enabled, number_of_members, comments "
                + " from DBA_SCHEDULER_GROUPS "
                + " where owner = ? and group_name = ? "
                + " order by owner, group_name");
        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, group_name, group_type, "
                + " enabled, number_of_members, comments "
                + " from ALL_SCHEDULER_GROUPS "
                + " where owner = ? and group_name = ? "
                + " order by owner, group_name");
        }

        getItemStmt.setString(1, mGroupOwner.toUpperCase());
        getItemStmt.setString(2, mGroupName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_GroupName = rset.getString(2);
            String m_GroupType = rset.getString(3);
            String m_Enabled = rset.getString(4);
            int    m_NumberOfMembers = rset.getInt(5);
            String m_Comments = rset.getString(6);

            if (mAddUpdate == 0) {
                m_GroupItem = new GroupItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_GroupName,
                    m_GroupType,
                    m_Enabled,
                    m_NumberOfMembers,
                    m_Comments);

                addGroup(m_GroupItem);

                // Add the window group to the look up list.
                int lGroupId = 0;
                if (m_GroupType.equals("WINDOW"))        lGroupId = SchedConsts.WINDOW_GROUP_ID;
                if (m_GroupType.equals("DB_DEST"))       lGroupId = SchedConsts.DB_DEST_ID;
                if (m_GroupType.equals("EXTERNAL_DEST")) lGroupId = SchedConsts.EXT_DEST_ID;

                screenCombo m_ScreenCombo =
                    new screenCombo(lGroupId,
                                getMaxItemId(lGroupId),
                                m_GroupName);
                addComboObj(m_ScreenCombo);
            }

            if (mAddUpdate == 1) {

                m_GroupItem = updateGroupItem(
                    m_Owner,
                    m_GroupName,
                    m_GroupType,
                    m_Enabled,
                    m_NumberOfMembers,
                    m_Comments);
            }
        }
        rset.close();
        getItemStmt.close();
        return m_GroupItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetGroupItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetGroupMembersData() {
    try {
        // Get the group members data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, group_name, member_name "
                + " from DBA_SCHEDULER_GROUP_MEMBERS "
                + " order by owner, group_name, member_name");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, group_name, member_name "
                + " from ALL_SCHEDULER_GROUP_MEMBERS "
                + " order by owner, group_name, member_name");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_GroupName = rset.getString(2);
            String m_MemberName = rset.getString(3);

            m_GroupMembersItem = new GroupMembersItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_GroupName,
                    m_MemberName);

            addGroupMember(m_GroupMembersItem);

        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetGroupMembersData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.GroupMembersItem GetGroupMemberItem(
                                        String m_GroupOwner,
                                        String m_GroupName,
                                        String m_MemberName) {
    try {
        // Get the group members data.

        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select owner, group_name, member_name "
                + " from DBA_SCHEDULER_GROUP_MEMBERS "
                + " where owner = ? and group_name = ? and member_name = ? "
                + " order by owner, group_name, member_name");
        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, group_name, member_name "
                + " from ALL_SCHEDULER_GROUP_MEMBERS "
                + " where owner = ? and group_name = ? and member_name = ? "
                + " order by owner, group_name, member_name");
        }

        getItemStmt.setString(1, m_GroupOwner.toUpperCase());
        getItemStmt.setString(2, m_GroupName.toUpperCase());
        getItemStmt.setString(3, m_MemberName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String l_Owner = rset.getString(1);
            String l_GroupName = rset.getString(2);
            String l_MemberName = rset.getString(3);

            m_GroupMembersItem = new GroupMembersItem(
                    getNextSeqNo(),
                    l_Owner,
                    l_GroupName,
                    l_MemberName);

            addGroupMember(m_GroupMembersItem);

        }
        rset.close();
        getItemStmt.close();
        return m_GroupMembersItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetGroupMembersItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetFileWatchersData() {
    try {
        // Get the file watchers data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, file_watcher_name, enabled, "
                + " destination_owner, destination, "
                + " directory_path, file_name, "
                + " credential_owner, credential_name, "
                + " min_file_size, steady_state_duration, "
                + " to_char(last_modified_time, 'YYYY-MM-DD HH24:MI:SS'), comments "
                + " from DBA_SCHEDULER_FILE_WATCHERS "
                + " order by owner, file_watcher_name");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, file_watcher_name, enabled, "
                + " destination_owner, destination, "
                + " directory_path, file_name, "
                + " credential_owner, credential_name, "
                + " min_file_size, steady_state_duration, "
                + " to_char(last_modified_time, 'YYYY-MM-DD HH24:MI:SS'), comments "
                + " from ALL_SCHEDULER_FILE_WATCHERS "
                + " order by owner, file_watcher_name");
        }
        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_FileWatcherName = rset.getString(2);
            String m_Enabled = rset.getString(3);
            String m_DestinationOwner = rset.getString(4);
            String m_Destination = rset.getString(5);
            String m_DirectoryPath = rset.getString(6);
            String m_FileName = rset.getString(7);
            String m_CredentialOwner = rset.getString(8);
            String m_CredentialName = rset.getString(9);
            int    m_MinFileSize = rset.getInt(10);
            String m_SteadyStateDuration = rset.getString(11);
            String m_LastModifiedTime = rset.getString(12);
            String m_Comments = rset.getString(13);


            m_FileWatchersItem = new FileWatchersItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_FileWatcherName,
                    m_Enabled,
                    m_DestinationOwner,
                    m_Destination,
                    m_DirectoryPath,
                    m_FileName,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_MinFileSize,
                    m_SteadyStateDuration,
                    m_LastModifiedTime,
                    m_Comments);

            addFileWatcher(m_FileWatchersItem);

        }
        rset.close();
        getDataStmt.close();
    }

    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetFileWatchersData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.FileWatchersItem GetFileWatcherItem(
                                        String mOwner,
                                        String mFileWatcherName,
                                        int  mAddUpdate) {
    try {

        // Get the file watchers item.
        if (dbaNo == 1) {
            getItemStmt = conn.prepareStatement(
            "select owner, file_watcher_name, enabled, "
                + " destination_owner, destination, "
                + " directory_path, file_name, "
                + " credential_owner, credential_name, "
                + " min_file_size, steady_state_duration, "
                + " to_char(last_modified_time, 'YYYY-MM-DD HH24:MI:SS'), comments "
                + " from DBA_SCHEDULER_FILE_WATCHERS "
                + " where owner = ? and file_watcher_name = ? "
                + " order by owner, file_watcher_name");
        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, file_watcher_name, enabled, "
                + " destination_owner, destination, "
                + " directory_path, file_name, "
                + " credential_owner, credential_name, "
                + " min_file_size, steady_state_duration, "
                + " to_char(last_modified_time, 'YYYY-MM-DD HH24:MI:SS'), comments "
                + " from ALL_SCHEDULER_FILE_WATCHERS "
                + " where owner = ? and file_watcher_name = ? "
                + " order by owner, file_watcher_name");
        }

        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mFileWatcherName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_FileWatcherName = rset.getString(2);
            String m_Enabled = rset.getString(3);
            String m_DestinationOwner = rset.getString(4);
            String m_Destination = rset.getString(5);
            String m_DirectoryPath = rset.getString(6);
            String m_FileName = rset.getString(7);
            String m_CredentialOwner = rset.getString(8);
            String m_CredentialName = rset.getString(9);
            int    m_MinFileSize = rset.getInt(10);
            String m_SteadyStateDuration = rset.getString(11);
            String m_LastModifiedTime = rset.getString(12);
            String m_Comments = rset.getString(13);

            if (mAddUpdate == 0) {
                m_FileWatchersItem = new FileWatchersItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_FileWatcherName,
                    m_Enabled,
                    m_DestinationOwner,
                    m_Destination,
                    m_DirectoryPath,
                    m_FileName,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_MinFileSize,
                    m_SteadyStateDuration,
                    m_LastModifiedTime,
                    m_Comments);

                addFileWatcher(m_FileWatchersItem);

            }
            if (mAddUpdate == 1) {
                m_FileWatchersItem = updateFileWatchersItem(
                    m_Owner,
                    m_FileWatcherName,
                    m_Enabled,
                    m_DestinationOwner,
                    m_Destination,
                    m_DirectoryPath,
                    m_FileName,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_MinFileSize,
                    m_SteadyStateDuration,
                    m_LastModifiedTime,
                    m_Comments);
            }
        }
        rset.close();
        getItemStmt.close();
        return m_FileWatchersItem;
    }

    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetFileWatcherItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetNotificationsData() {
    try {
        // Get the notifications data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, job_name, job_subname, "
                + " recipient, sender, subject, "
                + " body, filter_condition, "
                + " event, event_flag "
                + " from DBA_SCHEDULER_NOTIFICATIONS "
                + " order by owner, job_name, job_subname ");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, job_name, job_subname, "
                + " recipient, sender, subject, "
                + " body, filter_condition, "
                + " event, event_flag "
                + " from ALL_SCHEDULER_NOTIFICATIONS "
                + " order by owner, job_name, job_subname ");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {
            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            String m_JobSubname = rset.getString(3);
            String m_Recipient = rset.getString(4);
            String m_Sender = rset.getString(5);
            String m_Subject = rset.getString(6);
            String m_Body = rset.getString(7);
            String m_FilterCondition = rset.getString(8);
            String m_Event = rset.getString(9);
            int m_EventFlag = rset.getInt(10);

            m_NotificationsItem = new NotificationsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_JobName,
                    m_JobSubname,
                    m_Recipient,
                    m_Sender,
                    m_Subject,
                    m_Body,
                    m_FilterCondition,
                    m_Event,
                    m_EventFlag);

            addNotification(m_NotificationsItem);

        }
        rset.close();
        getDataStmt.close();
    }

    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetNotificationsData"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.NotificationsItem GetNotificationsItem(
                                                   String        mOwner,
                                                   String        mJobName,
                                                   String        mRecipient,
                                                   String        mSender,
                                                   String        mEvent,
                                                   SchedDataArea mDataArea) {
    try {
        // Get the notifications data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select owner, job_name, job_subname, "
                + " recipient, sender, subject, "
                + " body, filter_condition, "
                + " event, event_flag "
                + " from DBA_SCHEDULER_NOTIFICATIONS "
                + " where owner = ? and job_name = ? "
                + " and recipient = ? and sender = ? "
                + " and event = ? ");
        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, job_name, job_subname, "
                + " recipient, sender, subject, "
                + " body, filter_condition, "
                + " event, event_flag "
                + " from ALL_SCHEDULER_NOTIFICATIONS "
                + " where owner = ? and job_name = ? "
                + " and recipient = ? and sender = ? "
                + " and event = ? ");
        }

        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mJobName.toUpperCase());
        getItemStmt.setString(3, mRecipient);
        getItemStmt.setString(4, mSender);
        getItemStmt.setString(5, mEvent);

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {
            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            String m_JobSubname = rset.getString(3);
            String m_Recipient = rset.getString(4);
            String m_Sender = rset.getString(5);
            String m_Subject = rset.getString(6);
            String m_Body = rset.getString(7);
            String m_FilterCondition = rset.getString(8);
            String m_Event = rset.getString(9);
            int m_EventFlag = rset.getInt(10);

            m_NotificationsItem = mDataArea.new NotificationsItem(
                    mDataArea.getNextSeqNo(),
                    m_Owner,
                    m_JobName,
                    m_JobSubname,
                    m_Recipient,
                    m_Sender,
                    m_Subject,
                    m_Body,
                    m_FilterCondition,
                    m_Event,
                    m_EventFlag);

            mDataArea.addNotification(m_NotificationsItem);

        }

        rset.close();
        getItemStmt.close();
        return m_NotificationsItem;

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetNotificationsItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetJobDests() {
    try {
        // Get the job destinations data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, job_name, job_subname, "
                + " credential_owner, credential_name, "
                + " destination_owner, destination, job_dest_id, "
                + " enabled, refs_enabled, state, "
                + " to_char(next_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                + " run_count, retry_count, failure_count, "
                + " to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                + " to_char(last_end_date, 'YYYY-MM-DD HH24:MI:SS') "
                + " from DBA_SCHEDULER_JOB_DESTS "
                + " order by owner, job_name, job_subname ");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, job_name, job_subname, "
                + " credential_owner, credential_name, "
                + " destination_owner, destination, job_dest_id, "
                + " enabled, refs_enabled, state, "
                + " to_char(next_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                + " run_count, retry_count, failure_count, "
                + " to_char(last_start_date, 'YYYY-MM-DD HH24:MI:SS'), "
                + " to_char(last_end_date, 'YYYY-MM-DD HH24:MI:SS') "
                + " from ALL_SCHEDULER_JOB_DESTS "
                + " order by owner, job_name, job_subname ");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {
            String m_Owner = rset.getString(1);
            String m_JobName = rset.getString(2);
            String m_JobSubname = rset.getString(3);
            String m_CredentialOwner = rset.getString(4);
            String m_CredentialName = rset.getString(5);
            String m_DestinationOwner = rset.getString(6);
            String m_Destination = rset.getString(7);
            int    m_JobDestId = rset.getInt(8);
            String m_Enabled = rset.getString(9);
            String m_RefsEnabled = rset.getString(10);
            String m_State = rset.getString(11);
            String m_NextStartDate = rset.getString(12);
            int    m_RunCount = rset.getInt(13);
            int    m_RetryCount = rset.getInt(14);
            int    m_FailureCount = rset.getInt(15);
            String m_LastStartDate = rset.getString(16);
            String m_LastEndDate = rset.getString(17);

            m_JobDestsItem = new JobDestsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_JobName,
                    m_JobSubname,
                    m_CredentialOwner,
                    m_CredentialName,
                    m_DestinationOwner,
                    m_Destination,
                    m_JobDestId,
                    m_Enabled,
                    m_RefsEnabled,
                    m_State,
                    m_NextStartDate,
                    m_RunCount,
                    m_RetryCount,
                    m_FailureCount,
                    m_LastStartDate,
                    m_LastEndDate);

            addJobDests(m_JobDestsItem);

        }
        rset.close();
        getDataStmt.close();
    }

    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetJobDests"," : Error..." + e.getMessage());
    }
    }

    public void GetDests() {
    try {
        // Get the destinations data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, destination_name, "
                + " destination_type, "
                + " enabled, comments "
                + " from DBA_SCHEDULER_DESTS "
                + " order by owner, destination_name ");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, destination_name, "
                + " destination_type, "
                + " enabled, comments "
                + " from ALL_SCHEDULER_DESTS "
                + " order by owner, destination_name ");
        }

        ResultSet rset = getDataStmt.executeQuery();

        while (rset.next()) {

            String m_Owner = rset.getString(1);
            String m_DestinationName = rset.getString(2);
            String m_DestinationType = rset.getString(3);
            String m_Enabled = rset.getString(4);
            String m_Comments = rset.getString(5);

            m_DestsItem = new DestsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_DestinationName,
                    m_DestinationType,
                    m_Enabled,
                    m_Comments);

            addDests(m_DestsItem);

        }
        rset.close();
        getDataStmt.close();
    }

    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetDests"," : Error..." + e.getMessage());
    }
    }

    public void GetDBDests() {
    try {
        // Get the DB Destinations data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select owner, destination_name, "
                + " connect_info, agent, "
                + " enabled, refs_enabled, comments "
                + " from DBA_SCHEDULER_DB_DESTS "
                + " order by owner, destination_name ");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select owner, destination_name, "
                + " connect_info, agent, "
                + " enabled, refs_enabled, comments "
                + " from ALL_SCHEDULER_DB_DESTS "
                + " order by owner, destination_name ");
        }

        ResultSet rset = getDataStmt.executeQuery();
        int i2 = 1;

        while (rset.next()) {
            String m_Owner = rset.getString(1);
            String m_DestinationName = rset.getString(2);
            String m_ConnectInfo = rset.getString(3);
            String m_Agent = rset.getString(4);
            String m_Enabled = rset.getString(5);
            String m_RefsEnabled = rset.getString(6);
            String m_Comments = rset.getString(7);

            m_DbDestsItem = new DbDestsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_DestinationName,
                    m_ConnectInfo,
                    m_Agent,
                    m_Enabled,
                    m_RefsEnabled,
                    m_Comments);

            addDbDests(m_DbDestsItem);

            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(SchedConsts.DB_DEST_ID,
                                  i2,
                                  m_Owner,
                                  m_DestinationName);
            addComboObj(m_ScreenCombo);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetDBDests"," : Error..." + e.getMessage());
    }
    }

    public SchedDataArea.DbDestsItem GetDBDestsItem(
                                    String mOwner,
                                    String mDestinationName) {
    try {

        // Get the DB Destinations data.
        if (dbaNo == 1) {

            getItemStmt = conn.prepareStatement(
                "select owner, destination_name, "
                + " connect_info, agent, "
                + " enabled, refs_enabled, comments "
                + " from DBA_SCHEDULER_DB_DESTS "
                + " where owner = ?  and destination_name = ? "
                + " order by owner, destination_name ");
        }
        else {

            getItemStmt = conn.prepareStatement(
                "select owner, destination_name, "
                + " connect_info, agent, "
                + " enabled, refs_enabled, comments "
                + " from ALL_SCHEDULER_DB_DESTS "
                + " order by owner, destination_name ");
        }

        getItemStmt.setString(1, mOwner.toUpperCase());
        getItemStmt.setString(2, mDestinationName.toUpperCase());

        ResultSet rset = getItemStmt.executeQuery();

        while (rset.next()) {
            String m_Owner = rset.getString(1);
            String m_DestinationName = rset.getString(2);
            String m_ConnectInfo = rset.getString(3);
            String m_Agent = rset.getString(4);
            String m_Enabled = rset.getString(5);
            String m_RefsEnabled = rset.getString(6);
            String m_Comments = rset.getString(7);

            m_DbDestsItem = new DbDestsItem(
                    getNextSeqNo(),
                    m_Owner,
                    m_DestinationName,
                    m_ConnectInfo,
                    m_Agent,
                    m_Enabled,
                    m_RefsEnabled,
                    m_Comments);

            addDbDests(m_DbDestsItem);
        }
        rset.close();
        getItemStmt.close();
        return m_DbDestsItem;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetDBDestsItem"," : Error..." + e.getMessage());
        return null;
    }
    }

    public void GetExtDests() {
    try {
        // Get the External Destinations data.
        if (dbaNo == 1) {

            getDataStmt = conn.prepareStatement(
                "select destination_name, hostname, "
                + " port, ip_address, "
                + " enabled, comments "
                + " from DBA_SCHEDULER_EXTERNAL_DESTS "
                + " order by destination_name ");
        }
        else {

            getDataStmt = conn.prepareStatement(
                "select destination_name, hostname, "
                + " port, ip_address, "
                + " enabled, comments "
                + " from ALL_SCHEDULER_EXTERNAL_DESTS "
                + " order by destination_name ");
        }
        ResultSet rset = getDataStmt.executeQuery();

        int i2 = 1;

        while (rset.next()) {
            String m_DestinationName = rset.getString(1);
            String m_Hostname = rset.getString(2);
            String m_Port = rset.getString(3);
            String m_IpAddress = rset.getString(4);
            String m_Enabled = rset.getString(5);
            String m_Comments = rset.getString(6);

            m_ExtDestsItem = new ExtDestsItem(
                    getNextSeqNo(),
                    m_DestinationName,
                    m_Hostname,
                    m_Port,
                    m_IpAddress,
                    m_Enabled,
                    m_Comments);

            addExtDests(m_ExtDestsItem);

            i2 = i2 + 1;
            screenCombo m_ScreenCombo =
                new screenCombo(SchedConsts.EXT_DEST_ID,
                                  i2,
                                  m_DestinationName);
            addComboObj(m_ScreenCombo);
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetExtDests"," : Error..." + e.getMessage());
    }
    }

    public void GetResourcePlanNames() {
    try {
        // Get the resource plans data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(
                "select plan "
                + " from DBA_RSRC_PLANS where STATUS is null "
                + " order by plan");

            ResultSet rset = getDataStmt.executeQuery();
            int i2 = 0;

            while (rset.next()) {

                String m_PlanName = rset.getString(1);

                i2 = i2 + 1;
                screenCombo m_ScreenCombo =
                    new screenCombo(SchedConsts.RESOURCE_PLAN_ID,
                                  i2,
                                  m_PlanName);

                addComboObj(m_ScreenCombo);
            }
            rset.close();
            getDataStmt.close();
        }
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetResourcePlanNames"," : Error..." + e.getMessage());
    }
    }

    public void GetConsumerGroupNames() {
    try {
        // Get the consumer groups data.
        if (dbaNo == 1) {
            getDataStmt = conn.prepareStatement(
                "select consumer_group "
                + " from DBA_RSRC_CONSUMER_GROUPS "
                + " order by consumer_group");

            ResultSet rset = getDataStmt.executeQuery();
            int i2 = 0;

            while (rset.next()) {
                String m_ConsumerGroup = rset.getString(1);

                i2 = i2 + 1;
                screenCombo m_ScreenCombo =
                    new screenCombo(SchedConsts.CONSUMER_GROUP_ID,
                                      i2,
                                      m_ConsumerGroup);

                addComboObj(m_ScreenCombo);
            }
            rset.close();
            getDataStmt.close();
        }
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetConsumerGroupNames"," : Error..." + e.getMessage());
    }
    }

    public void GetPluggableDbNames() {
    try {
        // Get the pluggable database data.
        getDataStmt = conn.prepareStatement(
                "select name "
                + " from V$PDBS "
                + " order by name");

        ResultSet rset = getDataStmt.executeQuery();
        int i2 = 0;

        while (rset.next()) {
            String m_PluggableDb = rset.getString(1);
            // System.out.println("2. -- " + m_PluggableDb);

            if (m_PluggableDb.compareTo("PDB$SEED") != 0) {
                i2 = i2 + 1;
                screenCombo m_ScreenCombo =
                        new screenCombo(SchedConsts.PDBS_ID,
                                      i2,
                                      m_PluggableDb);

                addComboObj(m_ScreenCombo);
            }
        }
        rset.close();
        getDataStmt.close();
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("GetPluggableDbNames"," : Error..." + e.getMessage());
    }
    }

    public int CreateJob1(String mJobName,
                               String mJobType,
                               String mJobAction,
                               int    mNoArguments,
                               String mStartDate,
                               String mRepeatInterval,
                               String mEndDate,
                               String mJobClass,
                               String mComments,
                               String mCredential,
                               String mDestination) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_JOB(" + 
                             " JOB_NAME => " + mJobName + 
                             ", JOB_TYPE => " + mJobType + 
                             ", JOB_ACTION => " + mJobAction + 
                             ", NUMBER_OF_ARGUMENTS => " + mNoArguments + 
                             ", START_DATE => " + mStartDate +  
                             ", REPEAT_INTERVAL => " + mRepeatInterval + 
                             ", END_DATE => " + mEndDate + 
                             ", JOB_CLASS => " + mJobClass + 
                             ", ENABLED => FALSE, AUTO_DROP => TRUE" + 
                             ", COMMENTS => " + mComments +
                             ", CREDENTIAL_NAME => " + mCredential +
                             ", DESTINATION_NAME => " + mDestination + ")";

    try {

        if ( mStartDate.length() != 0) {
            try {
                mSDate = Timestamp.valueOf(mStartDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("Start Date has wrong format.");
                return 1;
            }
        }
        if ( mEndDate.length() != 0) {
            try {
                mEDate = Timestamp.valueOf(mEndDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("End Date has wrong format.");
                return 1;
            }
        }
        if ( mJobClass.trim().length() == 0) {
            mJobClass = DEFAULT_JOB_CLASS;
        }

        CallableStatement cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.CREATE_JOB( " +
                " JOB_NAME => ?, " +
                " JOB_TYPE => ?, " +
                " JOB_ACTION => ?, " +
                " NUMBER_OF_ARGUMENTS => TO_NUMBER(?), " +
                " START_DATE =>  ?, " +
                " REPEAT_INTERVAL => ?, " +
                " END_DATE => ?, " +
                " JOB_CLASS => ?, " +
                " ENABLED => FALSE, " +
                " AUTO_DROP => TRUE, " +
                " COMMENTS => ? ); end; "
        );

        if ((versionNo == 4) || (versionNo == 5)) {
            cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.CREATE_JOB( " +
                " JOB_NAME => ?, " +
                " JOB_TYPE => ?, " +
                " JOB_ACTION => ?, " +
                " NUMBER_OF_ARGUMENTS => TO_NUMBER(?), " +
                " START_DATE =>  ?, " +
                " REPEAT_INTERVAL => ?, " +
                " END_DATE => ?, " +
                " JOB_CLASS => ?, " +
                " ENABLED => FALSE, " +
                " AUTO_DROP => TRUE, " +
                " COMMENTS => ?, " +
                " CREDENTIAL_NAME => ?, " +
                " DESTINATION_NAME => ? ); end;");
        }

        cs.setString(1, mJobName);
        cs.setString(2, mJobType);
        cs.setString(3, mJobAction);
        cs.setInt(4, mNoArguments);


        if ( mStartDate.length() == 0 ) {
            cs.setTimestamp(5, null);
        }
        else {
            cs.setTimestamp(5, mSDate);
        }

        cs.setString(6, mRepeatInterval);

        if ( mEndDate.length() == 0 ) {
            cs.setTimestamp(7, null);
        }
        else {
            cs.setTimestamp(7, mEDate);
        }
        cs.setString(8, mJobClass);
        cs.setString(9, mComments);

        if ((versionNo == 4) || (versionNo == 5)) {
            if ( mCredential.length() == 0 )
                cs.setString(10, null);
            else
                cs.setString(10, mCredential);

            if ( mDestination.length() == 0 )
                cs.setString(11, null);
            else
                cs.setString(11, mDestination);
        }

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJob1"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateJob2(String mJobName,
                               String mProgramName,
                               String mScheduleName,
                               String mJobClass,
                               String mComments,
                               String mCredential,
                               String mDestination) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_JOB(" + 
                             " JOB_NAME => " + mJobName + 
                             ", PROGRAM_NAME => " + mProgramName + 
                             ", SCHEDULE_NAME => " + mScheduleName +  
                             ", JOB_CLASS => " +mJobClass + 
                             ", ENABLED => FALSE, AUTO_DROP => TRUE" + 
                             ", COMMENTS => " + mComments +
                             ", CREDENTIAL_NAME => " + mCredential +
                             ", DESTINATION_NAME => " + mDestination + ")";

    try {

        if ( mJobClass.trim().length() == 0) {
            mJobClass = DEFAULT_JOB_CLASS;
        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_JOB( " +
            " JOB_NAME => ?, " +
            " PROGRAM_NAME => ?, " +
            " SCHEDULE_NAME => ?, " +
            " JOB_CLASS => ?, " +
            " ENABLED => FALSE, " +
            " AUTO_DROP => TRUE, " +
            " COMMENTS => ? ); end;"
        );

        if ((versionNo == 4) || (versionNo == 5)) {
            cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.CREATE_JOB( " +
                " JOB_NAME => ?, " +
                " PROGRAM_NAME => ?, " +
                " SCHEDULE_NAME => ?, " +
                " JOB_CLASS => ?, " +
                " ENABLED => FALSE, " +
                " AUTO_DROP => TRUE, " +
                " COMMENTS => ?, " +
                " CREDENTIAL_NAME => ?, " +
                " DESTINATION_NAME => ? ); end;");
        }

        cs.setString(1, mJobName);
        cs.setString(2, mProgramName);
        cs.setString(3, mScheduleName);
        cs.setString(4, mJobClass);
        cs.setString(5, mComments);

        if ((versionNo == 4) || (versionNo == 5)) {
            if ( mCredential.length() == 0 )
                cs.setString(6, null);
            else
                cs.setString(6, mCredential);

            if ( mDestination.length() == 0 )
                cs.setString(7, null);
            else
                cs.setString(7, mDestination);
        }

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJob2"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateJob3(String mJobName,
                               String mProgramName,
                               String mStartDate,
                               String mRepeatInterval,
                               String mEndDate,
                               String mJobClass,
                               String mComments,
                               String mCredential,
                               String mDestination) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_JOB(" + 
                            " JOB_NAME => " + mJobName + 
                            ", PROGRAM_NAME => " + mProgramName + 
                            ", START_DATE => " + mStartDate + 
                            ", REPEAT_INTERVAL => " + mRepeatInterval + 
                            ", END_DATE => " + mEndDate + 
                            ", JOB_CLASS => " + mJobClass + 
                            ", ENABLED => FALSE, AUTO_DROP => TRUE," + 
                            ", COMMENTS => " + mComments + 
                            ", CREDENTIAL_NAME => " + mCredential +
                            ", DESTINATION_NAME => " + mDestination + ")";
    try {

        if ( mStartDate.length() != 0) {
            try {
                mSDate = Timestamp.valueOf(mStartDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("Start Date has wrong format.");
                return 1;
            }
        }
        if ( mEndDate.length() != 0) {
            try {
                mEDate = Timestamp.valueOf(mEndDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("End Date has wrong format.");
                return 1;
            }
        }
        if ( mJobClass.trim().length() == 0) {
            mJobClass = DEFAULT_JOB_CLASS;
        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_JOB( " +
            " JOB_NAME => ?, " +
            " PROGRAM_NAME => ?, " +
            " START_DATE => ?, " +
            " REPEAT_INTERVAL => ?, " +
            " END_DATE => ?, " +
            " JOB_CLASS => ?, " +
            " ENABLED => FALSE, " +
            " AUTO_DROP => TRUE, " +
            " COMMENTS => ? ); end; " 
        );
        if ((versionNo == 4) || (versionNo == 5)) {
            cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.CREATE_JOB( " +
                " JOB_NAME => ?, " +
                " PROGRAM_NAME => ?, " +
                " START_DATE => ?, " +
                " REPEAT_INTERVAL => ?, " +
                " END_DATE => ?, " +
                " JOB_CLASS => ?, " +
                " ENABLED => FALSE, " +
                " AUTO_DROP => TRUE, " +
                " COMMENTS => ?, " +
                " CREDENTIAL_NAME => ?, " +
                " DESTINATION_NAME => ? ); end;");
        }

        cs.setString(1, mJobName);
        cs.setString(2, mProgramName);
        if ( mStartDate.length() == 0 ) {
            cs.setTimestamp(3, null);
        }
        else {
            cs.setTimestamp(3, mSDate);
        }
        cs.setString(4, mRepeatInterval);
        if ( mEndDate.length() == 0 ) {
            cs.setTimestamp(5, null);
        }
        else {
            cs.setTimestamp(5, mEDate);
        }
        cs.setString(6, mJobClass);
        cs.setString(7, mComments);

        if ((versionNo == 4) || (versionNo == 5)) {
            if ( mCredential.length() == 0 )
                cs.setString(8, null);
            else
                cs.setString(8, mCredential);

            if ( mDestination.length() == 0 )
                cs.setString(9, null);
            else
                cs.setString(9, mDestination);
        }

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJob3"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateJob4(String mJobName,
                               String mScheduleName,
                               String mJobType,
                               String mJobAction,
                               int    mNoArguments,
                               String mJobClass,
                               String mComments,
                               String mCredential,
                               String mDestination) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_JOB(" + 
                            "JOB_NAME => " + mJobName + 
                            ", SCHEDULE_NAME => " + mScheduleName + 
                            ", JOB_TYPE => " + mJobType + 
                            ", JOB_ACTION => " + mJobAction +
                            ", NUMBER_OF_ARGUMENTS => " + mNoArguments + 
                            ", JOB_CLASS => " + mJobClass + 
                            ", ENABLED => FALSE, AUTO_DROP => TRUE," + 
                            ", COMMENTS => " + mComments + 
                            ", CREDENTIAL_NAME => " + mCredential +
                            ", DESTINATION_NAME => " + mDestination + ")";

    try {

        if ( mJobClass.trim().length() == 0) {
            mJobClass = DEFAULT_JOB_CLASS;
        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_JOB( " +
            " JOB_NAME => ?, " +
            " SCHEDULE_NAME => ?, " +
            " JOB_TYPE => ?, " +
            " JOB_ACTION => ?, " +
            " NUMBER_OF_ARGUMENTS => TO_NUMBER(?), " +
            " JOB_CLASS => ?, " +
            " ENABLED => FALSE, " +
            " AUTO_DROP => TRUE, " +
            " COMMENTS => ?, " +
            " CREDENTIAL_NAME => ?, " +
            " DESTINATION_NAME => ? ); end;"
        );

        if ((versionNo == 4) || (versionNo == 5)) {
            cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.CREATE_JOB( " +
                " JOB_NAME => ?, " +
                " SCHEDULE_NAME => ?, " +
                " JOB_TYPE => ?, " +
                " JOB_ACTION => ?, " +
                " NUMBER_OF_ARGUMENTS => TO_NUMBER(?), " +
                " JOB_CLASS => ?, " +
                " ENABLED => FALSE, " +
                " AUTO_DROP => TRUE, " +
                " COMMENTS => ?, " +
                " CREDENTIAL_NAME => ?, " +
                " DESTINATION_NAME => ? ); end;");
        }

        cs.setString(1, mJobName);
        cs.setString(2, mScheduleName);
        cs.setString(3, mJobType);
        cs.setString(4, mJobAction);
        cs.setInt(5, mNoArguments);
        cs.setString(6, mJobClass);
        cs.setString(7, mComments);

        if ((versionNo == 4) || (versionNo == 5)) {
            if ( mCredential.length() == 0 )
                cs.setString(8, null);
            else
                cs.setString(8, mCredential);

            if ( mDestination.length() == 0 )
                cs.setString(9, null);
            else
                cs.setString(9, mDestination);
        }

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJob4"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateJob5(String mJobName,
                               String mJobType,
                               String mJobAction,
                               int    mNoArguments,
                               String mStartDate,
                               String mEventCondition,
                               String mQueueSpec,
                               String mEndDate,
                               String mJobClass,
                               String mComments,
                               String mCredential,
                               String mDestination) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_JOB(" + 
                            "JOB_NAME => " + mJobName +
                            ", JOB_TYPE => " + mJobType + 
                            ", JOB_ACTION => " + mJobAction +
                            ", NUMBER_OF_ARGUMENTS => " + mNoArguments + 
                            ", START_DATE => " + mStartDate +
                            ", EVENT_CONDITION => " + mEventCondition +
                            ", QUEUE_SPEC => " + mQueueSpec + 
                            ", END_DATE => " + mEndDate +
                            ", JOB_CLASS => " + mJobClass +
                            ", ENABLED => FALSE, AUTO_DROP => TRUE," + 
                            ", COMMENTS => " + mComments +
                            ", CREDENTIAL_NAME => " + mCredential +
                            ", DESTINATION_NAME => " + mDestination + ")";

    try {

        if ( mStartDate.length() != 0) {
            try {
                mSDate = Timestamp.valueOf(mStartDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("Start Date has wrong format.");
                return 1;
            }
        }
        if ( mEndDate.length() != 0) {
            try {
                mEDate = Timestamp.valueOf(mEndDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("End Date has wrong format.");
                return 1;
            }
        }

        if ( mJobClass.trim().length() == 0) {
            mJobClass = DEFAULT_JOB_CLASS;
        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_JOB( " +
            " JOB_NAME => ?, " +
            " JOB_TYPE => ?, " +
            " JOB_ACTION => ?, " +
            " NUMBER_OF_ARGUMENTS => TO_NUMBER(?), " +
            " START_DATE => ?, " +
            " EVENT_CONDITION => ?, " +
            " QUEUE_SPEC => ?, " +
            " END_DATE => ?, " +
            " JOB_CLASS => ?, " +
            " ENABLED => FALSE, " +
            " AUTO_DROP => TRUE, " +
            " COMMENTS => ? ); end;");

        if ((versionNo == 4) || (versionNo == 5)) {
            cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.CREATE_JOB( " +
                " JOB_NAME => ?, " +
                " JOB_TYPE => ?, " +
                " JOB_ACTION => ?, " +
                " NUMBER_OF_ARGUMENTS => TO_NUMBER(?), " +
                " START_DATE => ?, " +
                " EVENT_CONDITION => ?, " +
                " QUEUE_SPEC => ?, " +
                " END_DATE => ?, " +
                " JOB_CLASS => ?, " +
                " ENABLED => FALSE, " +
                " AUTO_DROP => TRUE, " +
                " COMMENTS => ?, " +
                " CREDENTIAL_NAME => ?, " +
                " DESTINATION_NAME => ? ); end;");
        }

        cs.setString(1, mJobName);
        cs.setString(2, mJobType);
        cs.setString(3, mJobAction);
        cs.setInt(4, mNoArguments);

        if ( mStartDate.length() == 0 ) {
            cs.setTimestamp(5, null);
        }
        else {
            cs.setTimestamp(5, mSDate);
        }
        cs.setString(6, mEventCondition);
        cs.setString(7, mQueueSpec);
        if ( mEndDate.length() == 0 ) {
            cs.setTimestamp(8, null);
        }
        else {
            cs.setTimestamp(8, mEDate);
        }
        cs.setString(9, mJobClass);
        cs.setString(10, mComments);

        if ((versionNo == 4) || (versionNo == 5)) {
            if ( mCredential.length() == 0 )
                cs.setString(11, null);
            else
                cs.setString(11, mCredential);

            if ( mDestination.length() == 0 )
                cs.setString(12, null);
            else
                cs.setString(12, mDestination);
        }

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJob5"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateJob6(String mJobName,
                               String mProgramName,
                               String mStartDate,
                               String mEventCondition,
                               String mQueueSpec,
                               String mEndDate,
                               String mJobClass,
                               String mComments,
                               String mCredential,
                               String mDestination) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_JOB(" + 
                            " JOB_NAME => " + mJobName +
                            ", PROGRAM_NAME => " + mProgramName +
                            ", START_DATE => " + mStartDate +
                            ", EVENT_CONDITION => " + mEventCondition +
                            ", QUEUE_SPEC => " + mQueueSpec + 
                            ", END_DATE => " + mEndDate +
                            ", JOB_CLASS => " + mJobClass + 
                            ", ENABLED => FALSE, AUTO_DROP => TRUE," + 
                            ", COMMENTS => " + mComments +
                            ", CREDENTIAL_NAME => " + mCredential +
                            ", DESTINATION_NAME => " + mDestination + ")";

    try {

        if ( mStartDate.length() != 0) {
            try {
                mSDate = Timestamp.valueOf(mStartDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("Start Date has wrong format.");
                return 1;
            }
        }
        if ( mEndDate.length() != 0) {
            try {
                mEDate = Timestamp.valueOf(mEndDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("End Date has wrong format.");
                return 1;
            }
        }

        if ( mJobClass.trim().length() == 0) {
            mJobClass = DEFAULT_JOB_CLASS;
        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_JOB( " +
            " JOB_NAME => ?, " +
            " PROGRAM_NAME => ?, " +
            " START_DATE => ?, " +
            " EVENT_CONDITION => ?, " +
            " QUEUE_SPEC => ?, " +
            " END_DATE => ?, " +
            " JOB_CLASS => ?, " +
            " ENABLED => FALSE, " +
            " AUTO_DROP => TRUE, " +
            " COMMENTS => ? ); end; "
        );

        if ((versionNo == 4) || (versionNo == 5)) {
            cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.CREATE_JOB( " +
                " JOB_NAME => ?, " +
                " PROGRAM_NAME => ?, " +
                " START_DATE => ?, " +
                " EVENT_CONDITION => ?, " +
                " QUEUE_SPEC => ?, " +
                " END_DATE => ?, " +
                " JOB_CLASS => ?, " +
                " ENABLED => FALSE, " +
                " AUTO_DROP => TRUE, " +
                " COMMENTS => ?, " +
                " CREDENTIAL_NAME => ?, " +
                " DESTINATION_NAME => ? ); end;"
            );
        }

        cs.setString(1, mJobName);
        cs.setString(2, mProgramName);
        if ( mStartDate.length() == 0 ) {
            cs.setTimestamp(3, null);
        }
        else {
            cs.setTimestamp(3, mSDate);
        }
        cs.setString(4, mEventCondition);
        cs.setString(5, mQueueSpec);
        if ( mEndDate.length() == 0 ) {
            cs.setTimestamp(6, null);
        }
        else {
            cs.setTimestamp(6, mEDate);
        }
        cs.setString(7, mJobClass);
        cs.setString(8, mComments);

        if ((versionNo == 4) || (versionNo == 5)) {
            if ( mCredential.length() == 0 )
                cs.setString(9, null);
            else
                cs.setString(9, mCredential);

            if ( mDestination.length() == 0 )
                cs.setString(10, null);
            else
                cs.setString(10, mDestination);
        }

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJob6"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }


    public int CreateProgram(String mProgramName,
                                  String mProgramType,
                                  String mProgramAction,
                                  String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_PROGRAM(" + 
                             " PROGRAM_NAME => " + mProgramName + 
                             ", PROGRAM_TYPE => " + mProgramType +
                             ", PROGRAM_ACTION => " + mProgramAction +
                             ", COMMENTS => " + mComment + ")";

    try {

        // Create a statement

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_PROGRAM( " +
            " PROGRAM_NAME => ?, " +
            " PROGRAM_TYPE => ?, " +
            " PROGRAM_ACTION => ?, " +
            " COMMENTS => ? ); end;"
        );

        cs.setString(1, mProgramName);
        cs.setString(2, mProgramType);
        cs.setString(3, mProgramAction);
        cs.setString(4, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateProgram"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateSchedule1(String mScheduleName,
                                   String mStartDate,
                                   String mRepeatInterval,
                                   String mEndDate,
                                   String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_SCHEDULE(" + 
                            " SCHEDULE_NAME => " + mScheduleName + 
                            ", START_DATE => " + mStartDate +
                            ", REPEAT_INTERVAL => " + mRepeatInterval +
                            ", END_DATE => " + mEndDate +
                            ", COMMENTS => " + mComment + ")";

    try {

        if ( mStartDate != null ) {
            if ( mStartDate.trim().length() != 0) {
                try {
                    mSDate = Timestamp.valueOf(mStartDate);
                }
                catch(IllegalArgumentException e) {
                    setSysMessage("Start Date has wrong format.");
                    return 1;
                }
            }
        }

        if ( mEndDate != null ) {
            if ( mEndDate.trim().length() != 0) {
                try {
                    mEDate = Timestamp.valueOf(mEndDate);
                }
                catch(IllegalArgumentException e) {
                    setSysMessage("End Date has wrong format.");
                    return 1;
                }
            }

        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_SCHEDULE( " +
            " SCHEDULE_NAME => ?, " +
            " START_DATE => ?, " +
            " REPEAT_INTERVAL => ?, " +
            " END_DATE => ?, " +
            " COMMENTS => ? ); end;"
        );

        cs.setString(1, mScheduleName);
        if (( mStartDate == null ) || ( mStartDate.trim().length() == 0 )) {
            cs.setTimestamp(2, null);
        }
        else {
            cs.setTimestamp(2, mSDate);
        }

        cs.setString(3, mRepeatInterval);

        if (( mEndDate == null ) || ( mEndDate.trim().length() == 0 ))  {
            cs.setTimestamp(4, null);
        }
        else {
            cs.setTimestamp(4, mEDate);
        }
        cs.setString(5, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateSchedule1"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateSchedule2(String mScheduleName,
                                    String mStartDate,
                                    String mEventCondition,
                                    String mQueueSpec,
                                    String mEndDate,
                                    String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_EVENT_SCHEDULE(" + 
                            " SCHEDULE_NAME => " + mScheduleName +
                            ", START_DATE => " + mStartDate +
                            ", EVENT_CONDITION => " + mEventCondition +
                            ", QUEUE_SPEC => " + mQueueSpec +
                            ", END_DATE => " + mEndDate +
                            ", COMMENTS => " + mComment + ")";
    try {

        if ( mStartDate.length() != 0) {
            try {
                mSDate = Timestamp.valueOf(mStartDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("Start Date has wrong format.");
                return 1;
            }
        }
        if ( mEndDate.length() != 0) {
            try {
                mEDate = Timestamp.valueOf(mEndDate);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("End Date has wrong format.");
                return 1;
            }
        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_EVENT_SCHEDULE( " +
            " SCHEDULE_NAME => ?, " +
            " START_DATE => ?, " +
            " EVENT_CONDITION => ?, " +
            " QUEUE_SPEC => ?, " +
            " END_DATE => ?, " +
            " COMMENTS => ? ); end;"
        );

        cs.setString(1, mScheduleName);
        if ( mStartDate.length() == 0 ) {
            cs.setTimestamp(2, null);
        }
        else {
            cs.setTimestamp(2, mSDate);
        }
        cs.setString(3, mEventCondition);
        cs.setString(4, mQueueSpec);
        if ( mEndDate.length() == 0 ) {
            cs.setTimestamp(5, null);
        }
        else {
            cs.setTimestamp(5, mEDate);
        }
        cs.setString(6, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateSchedule2"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateJobClass(String mJobClassName,
                                   String mRCG,
                                   String mService,
                                   String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_JOB_CLASS(" + 
                            " JOB_CLASS_NAME => " + mJobClassName + 
                            ", RESOURCE_CONSUMER_GROUP => " + mRCG + 
                            ", SERVICE => " + mService +
                            ", COMMENTS => " + mComment + ")";

    try {

        // Create a statement

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.CREATE_JOB_CLASS( " +
            " JOB_CLASS_NAME => ?, " +
            " RESOURCE_CONSUMER_GROUP => ?, " +
            " SERVICE => ?, " +
            " COMMENTS => ? ); end;"
        );

        cs.setString(1, mJobClassName);
        cs.setString(2, mRCG);
        cs.setString(3, mService);
        cs.setString(4, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJobClass"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateProgramArg(String mProgramName,
                                     String mArgName,
                                     int mArgPosition,
                                     String mArgType,
                                     String mDefaultValue) {

        String mCallStatement = null;

    try {

        // Create a statement

        if (mDefaultValue.length() == 0) {
            mCallStatement = "DBMS_SCHEDULER.DEFINE_PROGRAM_ARGUMENT(" + 
                                  " PROGRAM_NAME => " + mProgramName +
                                  ", ARGUMENT_POSITION => " + mArgPosition +
                                  ", ARGUMENT_NAME => " + mArgName +
                                  ", ARGUMENT_TYPE => " + mArgType + ")";

            CallableStatement cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.DEFINE_PROGRAM_ARGUMENT( " +
                " PROGRAM_NAME => ?, " +
                " ARGUMENT_POSITION => TO_NUMBER(?), " +
                " ARGUMENT_NAME => ?, " +
                " ARGUMENT_TYPE => ? ); end;"
            );
            cs.setString(1, mProgramName);
            cs.setInt(2, mArgPosition);
            cs.setString(3, mArgName);
            cs.setString(4, mArgType);

            cs.executeUpdate();
        }
        else {
            mCallStatement = "DBMS_SCHEDULER.DEFINE_PROGRAM_ARGUMENT(" + 
                                  " PROGRAM_NAME => " + mProgramName + 
                                  ", ARGUMENT_POSITION => " + mArgPosition +
                                  ", ARGUMENT_NAME => " + mArgName + 
                                  ", ARGUMENT_TYPE => " + mArgType +
                                  ", DEFAULT_VALUE => " + mDefaultValue + ")";

            CallableStatement cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.DEFINE_PROGRAM_ARGUMENT( " +
                " PROGRAM_NAME => ?, " +
                " ARGUMENT_POSITION => TO_NUMBER(?), " +
                " ARGUMENT_NAME => ?, " +
                " ARGUMENT_TYPE => ?, " +
                " DEFAULT_VALUE => ? ); end;"
            );
            cs.setString(1, mProgramName);
            cs.setInt(2, mArgPosition);
            cs.setString(3, mArgName);
            cs.setString(4, mArgType);
            cs.setString(5, mDefaultValue);

            cs.executeUpdate();
        };
        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateProgramArg"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }


    public int CreateMetadataArg(String mProgramName,
                                      String mArgName,
                                      int    mArgPosition,
                                      String mMetadataAttribute) {

        String mCallStatement = "DBMS_SCHEDULER.SET_JOB_ARGUMENT_VALUE(" + 
                            " PROGRAM_NAME => " + mProgramName +
                            ", METADATA_ATTRIBUTE => " + mMetadataAttribute +
                            ", ARGUMENT_POSITION => " + mArgPosition +
                            ", ARGUMENT_NAME => " + mArgName + ")";

    try {

        CallableStatement cs = conn.prepareCall (
              "begin DBMS_SCHEDULER.DEFINE_METADATA_ARGUMENT( " +
              " PROGRAM_NAME => ?, " +
              " METADATA_ATTRIBUTE => ?, " +
              " ARGUMENT_POSITION => TO_NUMBER(?), " +
              " ARGUMENT_NAME => ?); end;"
        );

        cs.setString(1, mProgramName);
        cs.setString(2, mMetadataAttribute);
        cs.setInt(3, mArgPosition);
        cs.setString(4, mArgName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateMetadataArg"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateJobArg(String mJobName,
                                 int mArgPosition,
                                 String mArgValue) {

        String mCallStatement = "DBMS_SCHEDULER.SET_JOB_ARGUMENT_VALUE(" + 
                            " JOB_NAME => " + mJobName +
                            ", ARGUMENT_POSITION => " + mArgPosition +
                            ", ARGUMENT_VALUE => " + mArgValue + ")";

    try {

        // Create a statement
        CallableStatement cs = conn.prepareCall (
              "begin DBMS_SCHEDULER.SET_JOB_ARGUMENT_VALUE( " +
              " JOB_NAME => ?, " +
              " ARGUMENT_POSITION => ?, " +
              " ARGUMENT_VALUE => ? ); end;"
        );
        cs.setString(1, mJobName);
        cs.setInt(2, mArgPosition);
        cs.setString(3, mArgValue);
        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateJobArg"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateWindow1(String mWindowName,
                                  String mResourcePlan,
                                  String mScheduleName,
                                  String mDuration,
                                  String mComments) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_WINDOW(" + 
                            " WINDOW_NAME => " + mWindowName +
                            ", RESOURCE_PLAN => " + mResourcePlan +
                            ", SCHEDULE_NAME => " + mScheduleName +
                            ", DURATION => " + mDuration +
                            ", COMMENTS => " + mComments + ")";

    try {

        // Create a statement
        CallableStatement cs = conn.prepareCall (
            "BEGIN DBMS_SCHEDULER.CREATE_WINDOW(" +
               " WINDOW_NAME => ?, " +
               " RESOURCE_PLAN => ?, " +
               " SCHEDULE_NAME => ?, " +
               " DURATION => ?, " +
               " COMMENTS => ? ); END;"
        );

        cs.setString(1, mWindowName);
        cs.setString(2, mResourcePlan);
        cs.setString(3, mScheduleName);
        cs.setString(4, mDuration);
        cs.setString(5, mComments);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateWindow1"," : Error..." + mCallStatement);

        if ( e.getErrorCode() == 27481 ) {
            setSysMessage(" Error - Invalid Schedule name entered.");
        }
        else {
            setSysMessage(e.getMessage());
        }
        return 1;
    }
    }

    public int CreateWindow2(String mWindowName,
                                  String mResourcePlan,
                                  String mStartDate,
                                  String mRepeatInterval,
                                  String mEndDate,
                                  String mDuration,
                                  String mComments) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_WINDOW(" + 
                             mWindowName +
                            "," + mResourcePlan +
                            "," + mStartDate +
                            "," + mRepeatInterval +
                            "," + mEndDate +
                            "," + mDuration +
                            ", LOW," + mComments + ")";

    try {

        if ( mStartDate != null) {
            if ( mStartDate.trim().length() != 0 ) {
                try {
                    mSDate = Timestamp.valueOf(mStartDate);
                }
                catch(IllegalArgumentException e) {
                    setSysMessage("Start Date has wrong format.");
                  return 1;
                }
            }
        }
        if ( mEndDate != null) {
            if ( mEndDate.trim().length() != 0) {
                try {
                    mEDate = Timestamp.valueOf(mEndDate);
                }
                catch(IllegalArgumentException e) {
                    setSysMessage("End Date has wrong format.");
                    return 1;
                }
            }
        }

        // Create a statement
        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.CREATE_WINDOW(?, ?, ?, ?, ?, ?, ?, ? )"
        );

        cs.setString(1, mWindowName);
        cs.setString(2, mResourcePlan);
        if (( mStartDate == null ) || (mStartDate.trim().length() == 0 )) {
            cs.setTimestamp(3, null);
        }
        else {
            cs.setTimestamp(3, mSDate);
        }
        cs.setString(4, mRepeatInterval);
        if (( mEndDate == null ) || (mEndDate.trim().length() == 0 )) {
            cs.setTimestamp(5, null);
        }
        else {
            cs.setTimestamp(5, mEDate);
        }
        cs.setString(6, mDuration);
        cs.setString(7, "LOW");
        cs.setString(8, mComments);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateWindow2"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateWindowGroup(String mGroupName,
                                 String mWindows,
                                 String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_WINDOW_GROUP(" + 
                             mGroupName + "," + mWindows + "," +
                             mComment + ")";

    try {

        CallableStatement cs = conn.prepareCall (
          "call DBMS_SCHEDULER.CREATE_WINDOW_GROUP(?, ?, ? )"
        );

        cs.setString(1, mGroupName);
        cs.setString(2, mWindows);
        cs.setString(3, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateWindowGroup"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateGroup(String mGroupName,
                           String mType,
                           String mMembers,
                           String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_GROUP(" + 
                             mGroupName + "," + mType + "," + mMembers + "," + 
                             mComment + ")";


    try {

        // Create a statement
        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.CREATE_GROUP(?, ?, ?, ? )"
        );

        cs.setString(1, mGroupName);
        cs.setString(2, mType);
        cs.setString(3, mMembers);
        cs.setString(4, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateGroup"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateChain(String mChainName,
                                String mRuleSetName,
                                String mEvaluationInterval,
                                String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_CHAIN(" + 
                             mChainName + "," + mRuleSetName + "," +
                             mEvaluationInterval + "," + mComment + ")";

    try {

        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.CREATE_CHAIN(?, ?, ?, ? )"
        );

        cs.setString(1, mChainName);
        cs.setString(2, mRuleSetName);
        cs.setString(3, mEvaluationInterval);
        cs.setString(4, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateChain"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateChainRule(String mChainName,
                                    String mChainRuleName,
                                    String mCondition,
                                    String mAction,
                                    String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.DEFINE_CHAIN_RULE(" + 
                             mChainName + "," + mCondition + "," +
                             mAction + "," + mChainRuleName + "," + mComment + ")";
    try {

        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.DEFINE_CHAIN_RULE(?, ?, ?, ?, ? )"
        );

        cs.setString(1, mChainName);
        cs.setString(2, mCondition);
        cs.setString(3, mAction);
        cs.setString(4, mChainRuleName);
        cs.setString(5, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateChainRule"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateChainStep1(String mChainName,
                                    String mChainStepName,
                                    String mProgramName) {

        String mCallStatement = "DBMS_SCHEDULER.DEFINE_CHAIN_STEP(" + 
                             mChainName + "," + mChainStepName + "," +
                             mProgramName + ")";

    try {

        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.DEFINE_CHAIN_STEP(?, ?, ?)"
        );

        cs.setString(1, mChainName);
        cs.setString(2, mChainStepName);
        cs.setString(3, mProgramName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateChainStep1"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateChainStep2(String mChainName,
                                     String mChainStepName,
                                     String mEventCondition,
                                     String mQueueSpec) {

        String mCallStatement = "DBMS_SCHEDULER.DEFINE_CHAIN_EVENT_STEP(" + 
                            " CHAIN_NAME => " + mChainName +
                            ", STEP_NAME => " + mChainStepName +
                            ", EVENT_CONDITION => " + mEventCondition +
                            ", QUEUE_SPEC => " + mQueueSpec + ")";
    try {

        CallableStatement cs = conn.prepareCall (
            "BEGIN DBMS_SCHEDULER.DEFINE_CHAIN_EVENT_STEP(" +
               " CHAIN_NAME => ?, " +
               " STEP_NAME => ?, " +
               " EVENT_CONDITION => ?, " +
               " QUEUE_SPEC => ? ); END;"
        );
        cs.setString(1, mChainName);
        cs.setString(2, mChainStepName);
        cs.setString(3, mEventCondition);
        cs.setString(4, mQueueSpec);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateChainStep2"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateChainStep3(String mChainName,
                                     String mChainStepName,
                                     String mScheduleName) {

        String mCallStatement = "DBMS_SCHEDULER.DEFINE_CHAIN_EVENT_STEP(" + 
                            " CHAIN_NAME => " + mChainName +
                            ", STEP_NAME => " + mChainStepName +
                            ", SCHEDULE_NAME => " + mScheduleName + ")";

    try {

        CallableStatement cs = conn.prepareCall (
            "BEGIN DBMS_SCHEDULER.DEFINE_CHAIN_EVENT_STEP(" +
               " CHAIN_NAME => ?, " +
               " STEP_NAME => ?, " +
               " SCHEDULE_NAME => ? ); END;"
        );

        cs.setString(1, mChainName);
        cs.setString(2, mChainStepName);
        cs.setString(3, mScheduleName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateChainStep3"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateCredential(String mCredentialName,
                                     String mUsername,
                                     String mPassword,
                                     String mWindowsDomain,
                                     String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_CREDENTIAL(" + 
                            " CREDENTIAL_NAME => " + mCredentialName +
                            ", USERNAME => " + mUsername +
                            ", PASSWORD =>  XXXXXX" +
                            ", WINDOWS_DOMAIN => " + mWindowsDomain +
                            ", COMMENTS => " + mComment + ")";
    try {

        CallableStatement cs = conn.prepareCall (
            "BEGIN DBMS_SCHEDULER.CREATE_CREDENTIAL(" +
               " CREDENTIAL_NAME => ?, " +
               " USERNAME => ?, " +
               " PASSWORD => ?, " +
               " WINDOWS_DOMAIN => ?, " +
               " COMMENTS =>  ? ); END;"
        );

        cs.setString(1, mCredentialName);
        cs.setString(2, mUsername);
        cs.setString(3, mPassword);
        cs.setString(4, mWindowsDomain);
        cs.setString(5, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateCredential"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateFileWatcher(String mFileWatcherName,
                                      String mDirectoryPath,
                                      String mFileName,
                                      String mCredentialName,
                                      String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_FILE_WATCHER(" + 
                            " FILE_WATCHER_NAME => " + mFileWatcherName +
                            ", DIRECTORY_PATH => " + mDirectoryPath +
                            ", FILE_NAME => " + mFileName +
                            ", CREDENTIAL_NAME => " + mCredentialName +
                            ", COMMENTS => " + mComment + ")";

    try {

        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.CREATE_FILE_WATCHER( " +
                       " FILE_WATCHER_NAME => ?, " +
                       " DIRECTORY_PATH => ?, " +
                       " FILE_NAME => ?, " +
                       " CREDENTIAL_NAME => ?, " +
                       " cOMMENTS => ? )"
        );

        cs.setString(1, mFileWatcherName);
        cs.setString(2, mDirectoryPath);
        cs.setString(3, mFileName);
        cs.setString(4, mCredentialName);
        cs.setString(5, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateFileWatcher"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateNotification(String mJobName,
                                       String mRecipients,
                                       String mSender,
                                       String mSubject,
                                       String mBody,
                                       String mEvents,
                                       String mFilterCondition) {

        String mCallStatement = "DBMS_SCHEDULER.ADD_JOB_EMAIL_NOTIFICATION(" + 
                            " JOB_NAME => " + mJobName +
                            ", RECIPIENTS => " + mRecipients +
                            ", SENDER => " + mSender +
                            ", SUBJECT => " + mSubject +
                            ", BODY => " + mBody + 
                            ", EVENTS => " + mEvents + 
                            ", FILTER_CONDITION => " + mFilterCondition + ")";

    try {

        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.ADD_JOB_EMAIL_NOTIFICATION( " +
                       " JOB_NAME => ?, " +
                       " RECIPIENTS => ?, " +
                       " SENDER => ?, " +
                       " SUBJECT => ?, " +
                       " BODY => ?, " +
                       " EVENTS => ?, " +
                       " FILTER_CONDITION => ? )"
        );

        cs.setString(1, mJobName);
        cs.setString(2, mRecipients);
        cs.setString(3, mSender);
        cs.setString(4, mSubject);
        cs.setString(5, mBody);
        cs.setString(6, "'" + mEvents + "'");
        cs.setString(7, mFilterCondition);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateNotification"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CreateDbDestination(String mDestinationName,
                                        String mAgent,
                                        String mTnsName,
                                        String mComment) {

        String mCallStatement = "DBMS_SCHEDULER.CREATE_DATABASE_DESTINATION(" + 
                            " DESTINATION_NAME => " + mDestinationName +
                            ", AGENT => " + mAgent +
                            ", TNS_NAME => " + mTnsName +
                            ", COMMENTS => " + mComment + ")";
    try {

        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.CREATE_DATABASE_DESTINATION( " +
                       " DESTINATION_NAME => ?, " +
                       " AGENT => ?, " +
                       " TNS_NAME => ?, " +
                       " COMMENTS => ? )"
        );

        cs.setString(1, mDestinationName);
        cs.setString(2, mAgent);
        cs.setString(3, mTnsName);
        cs.setString(4, mComment);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CreateDbDestination"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CopyJob(String mJobName,
                            String mNewJobName) {

        String mCallStatement = "DBMS_SCHEDULER.COPY_JOB(" + 
                             mJobName + "," + mNewJobName + ")";

    try {

        CallableStatement cs = conn.prepareCall (
            "call DBMS_SCHEDULER.COPY_JOB(?, ?)"
        );

        cs.setString(1, mJobName);
        cs.setString(2, mNewJobName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CopyJob"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }


    public int DropJob(String mJobName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_JOB(" + 
                                 mJobName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_JOB(?)");

        cs.setString(1, mJobName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropJob"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropProgram(String mProgramName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_PROGRAM(" + 
                                 mProgramName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_PROGRAM(?)");

        cs.setString(1, mProgramName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropProgram"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropSchedule(String mScheduleName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_SCHEDULE(" + 
                                 mScheduleName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_SCHEDULE(?)");

        cs.setString(1, mScheduleName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropSchedule"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropJobClass(String mJobClassName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_JOB_CLASS(" + 
                                 mJobClassName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_JOB_CLASS(?)");

        cs.setString(1, mJobClassName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropJobClass"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropWindow(String mWindowName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_WINDOW(" + 
                                 mWindowName + ")";

    try {
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_WINDOW(?)");

        cs.setString(1, mWindowName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropWindow"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropWindowGroup(String mWindowGroupName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_WINDOW_GROUP(" + 
                             mWindowGroupName + ")";

    try {
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_WINDOW_GROUP(?)");

        cs.setString(1, mWindowGroupName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("DropWindowGroup"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
    }


    public int DropGroup(String mGroupName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_GROUP(" + 
                             mGroupName + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_GROUP(?)");

        cs.setString(1, mGroupName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropGroup"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropJobArg(String mJobName,
                                 int mArgPos) {

        String mCallStatement = "DBMS_SCHEDULER.RESET_JOB_ARGUMENT_VALUE(" + 
                            " JOB_NAME => " + mJobName +
                            ", ARGUMENT_POSITION => " + mArgPos + ")";

    try {

        // Create a statement
        CallableStatement cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.RESET_JOB_ARGUMENT_VALUE( " +
                " JOB_NAME => ?, " +
                " ARGUMENT_POSITION => ? ); end;"
        );

        cs.setString(1, mJobName);
        cs.setInt(2, mArgPos);
        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropJobArg"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropProgramArgument(String mProgramName,
                                   int    mArgPos) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_PROGRAM_ARGUMENT(" + 
                                 mProgramName + "," + mArgPos + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_PROGRAM_ARGUMENT(?, ?)");

        cs.setString(1, mProgramName);
        cs.setInt(2, mArgPos);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropProgramArgument"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropChain(String mChainName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_CHAIN(" + 
                                 mChainName + ")";

    try {
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_CHAIN(?)");

        cs.setString(1, mChainName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropChain"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropChainStep(String mChainName,
                                    String mStepName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_CHAIN_STEP(" + 
                                   mChainName + "," + mStepName + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_CHAIN_STEP(?, ?)");

        cs.setString(1, mChainName);
        cs.setString(2, mStepName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropChainStep"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropChainRule(String mChainName,
                                    String mChainRule) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_CHAIN_RULE(" + 
                             mChainName + "," + mChainRule + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_CHAIN_RULE(?, ?)");

        cs.setString(1, mChainName);
        cs.setString(2, mChainRule);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropChainRule"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropCredential(String mCredentialName) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_CREDENTIAL(" + 
                                   mCredentialName + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_CREDENTIAL(?)");

        cs.setString(1, mCredentialName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropCredential"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

  public int DropFileWatcher(SchedDataArea.FileWatchersItem mFileWatchersItem) {

        String mCallStatement = "DBMS_SCHEDULER.DROP_FILE_WATCHER(" + 
                               mFileWatchersItem + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DROP_FILE_WATCHER(?)");

        cs.setString(1, mFileWatchersItem.getOwner() + "." +
                        mFileWatchersItem.getFileWatcherName());

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropFileWatcher"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DropNotification(SchedDataArea.NotificationsItem mNotificationsItem) {

        String mCallStatement = "DBMS_SCHEDULER.REMOVE_JOB_EMAIL_NOTIFICATION(" + 
                                 " JOB_NAME => " + mNotificationsItem.getOwner() + "." +
                                                   mNotificationsItem.getJobName() +
                                 ", RECIPIENTS => " + mNotificationsItem.getRecipient() +
                                 ", EVENTS => " + mNotificationsItem.getEvent() + ")";

    try {

        CallableStatement cs = conn.prepareCall (
              "call DBMS_SCHEDULER.REMOVE_JOB_EMAIL_NOTIFICATION( " +
               " JOB_NAME => ?, " +
               " RECIPIENTS => ?, " +
               " EVENTS => ? )");

        cs.setString(1, mNotificationsItem.getOwner() + "." + mNotificationsItem.getJobName());
        cs.setString(2, mNotificationsItem.getRecipient());
        cs.setString(3, mNotificationsItem.getEvent());

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;

    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DropFileWatcher"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int UpdateLoggingObject(
                                 String mObjectOwner,
                                 String mObject,
                                 String mAttribute,
                                 String mValue) {
        String  Str1, Str2;
        Str1 = mObjectOwner + "." + mObject;
        Str2 = "DBMS_SCHEDULER." + mValue;

        String mCallStatement = "DBMS_SCHEDULER.SET_ATTRIBUTE(" + 
                                 Str1 + "," + mAttribute + "," + Str2 + ")";

    try {

        Str2 = "{ call DBMS_SCHEDULER.SET_ATTRIBUTE(" + "'" + Str1 + "','" + mAttribute + "', " + Str2 + ") }";

        CallableStatement cs = conn.prepareCall(Str2);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("UpdateLoggingObject"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int UpdateStrObject(
                                 String mObjectOwner,
                                 String mObject,
                                 String mAttribute,
                                 String mValue) {
        String  Str1;
        Str1 = mObjectOwner + "." + mObject;

        String mCallStatement = "DBMS_SCHEDULER.SET_ATTRIBUTE(" + 
                                 Str1 + "," + mAttribute + "," + mValue + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.SET_ATTRIBUTE(?, ?, ?)");

        cs.setString(1, Str1);
        cs.setString(2, mAttribute);
        cs.setString(3, mValue);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("UpdateStrObject"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int UpdateDateObject(
                                 String mObjectOwner,
                                 String mObject,
                                 String mAttribute,
                                 String mValue) {
        String  Str1;
        Str1 = mObjectOwner + "." + mObject;

        String mCallStatement = "DBMS_SCHEDULER.SET_ATTRIBUTE(" + 
                             Str1 + "," + mAttribute + "," + mValue + ")";

    try {

        if ( mValue.length() != 0) {
            try {
                mSDate = Timestamp.valueOf(mValue);
            }
            catch(IllegalArgumentException e) {
                setSysMessage("Date has wrong format.");
                return 1;
            }
        }

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.SET_ATTRIBUTE(?, ?, ?)");

        cs.setString(1, Str1);
        cs.setString(2, mAttribute);
        cs.setTimestamp(3, mSDate);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("UpdateDateObject"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int UpdateBooleanObject(
                                 String mObjectOwner,
                                 String mObject,
                                 String mAttribute,
                                 String mValue) {
        String  Str1;
        Str1 = mObjectOwner + "." + mObject;

        String mCallStatement = "DBMS_SCHEDULER.SET_ATTRIBUTE(" + 
                             Str1 + "," + mAttribute + "," + mValue + ")";

    try {

        Str1 = "{ call DBMS_SCHEDULER.SET_ATTRIBUTE(" + "'" + Str1 + "','" + 
                                                  mAttribute + "', " + mValue + ") }";

        CallableStatement cs = conn.prepareCall (Str1);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("UpdateBooleanObject"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int UpdateChain(String mChain,
                                  String mStep,
                                  String mAttribute,
                                  String mValue) {

        String mCallStatement = "DBMS_SCHEDULER.ALTER_CHAIN(" + 
                            " CHAIN_NAME => " + mChain +
                            ", STEP_NAME => " + mStep +
                            ", ATTRIBUTE => " + mAttribute +
                            ", VALUE => " + mValue + ")";

    try {

        CallableStatement cs = conn.prepareCall (
            "BEGIN DBMS_SCHEDULER.ALTER_CHAIN(CHAIN_NAME => ? ," + 
                          " STEP_NAME => ? ," + 
                          " ATTRIBUTE => ? ," + 
                          " VALUE => " + mValue + "); END;");

        cs.setString(1, mChain);
        cs.setString(2, mStep);
        cs.setString(3, mAttribute);

        cs.execute();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("UpdateChain"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int UpdateObjectNull(String mObject,
                                       String mAttribute) {

        String mCallStatement = "DBMS_SCHEDULER.SET_ATTRIBUTE_NULL(" + 
                                 mObject + "," + mAttribute + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.SET_ATTRIBUTE_NULL(?, ?)");

        cs.setString(1, mObject);
        cs.setString(2, mAttribute);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("UpdateObjectNull"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int UpdateGlobalAttribute(
                                 String mAttribute,
                                 String mValue) {

        String mCallStatement = "DBMS_SCHEDULER.SET_SCHEDULER_ATTRIBUTE(" + 
                                 mAttribute + "," + mValue + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.SET_SCHEDULER_ATTRIBUTE(?, ?)");

        cs.setString(1, mAttribute);
        cs.setString(2, mValue);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("UpdateGlobalAttribute"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }


    public int EnableObject(String mObjectName) {

        String mCallStatement = "DBMS_SCHEDULER.ENABLE(" + mObjectName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.ENABLE(?)");

        cs.setString(1, mObjectName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("EnableObject"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int DisableObject(String mObjectName) {

        String mCallStatement = "DBMS_SCHEDULER.DISABLE(" + mObjectName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.DISABLE(?)");

        cs.setString(1, mObjectName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("DisableObject"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int RunJob(String mJobName, boolean mCurrentThread) {

        String mCallStatement = "DBMS_SCHEDULER.RUN_JOB(" + 
                                  " JOB_NAME => " + mJobName + 
                                  ", USE_CURRENT_SESSION => ";

    try {
        // Create a statement
        if (mCurrentThread) {

            mCallStatement = mCallStatement + "TRUE)";

            CallableStatement cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.RUN_JOB( " +
                " JOB_NAME => ?, " +
                " USE_CURRENT_SESSION => TRUE); end;");

            cs.setString(1, mJobName);

            cs.executeUpdate();
        }
        else {

            mCallStatement = mCallStatement + "FALSE)";

            CallableStatement cs = conn.prepareCall (
                "begin DBMS_SCHEDULER.RUN_JOB( " +
                " JOB_NAME => ?, " +
                " USE_CURRENT_SESSION => FALSE); end;");

            cs.setString(1, mJobName);

            cs.executeUpdate();
        }
        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("RunJob"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int StopJob(String mJobName) {

        String mCallStatement = "DBMS_SCHEDULER.STOP_JOB(" + mJobName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.STOP_JOB(?)");

        cs.setString(1, mJobName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("StopJob"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int PurgeLog(String logHistory,
                               String logName,
                               String jobName) {

        String mCallStatement = "DBMS_SCHEDULER.PURGE_LOG(" + logHistory + 
                                "," + logName + "," + jobName + ")";

    try {
        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.PURGE_LOG(?, ?, ?)");

        cs.setString(1, logHistory);
        cs.setString(2, logName);
        cs.setString(3, jobName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("PurgeLog"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int AssignToGroup(String mMemberName,
                                  String mGroupName) {

        String mCallStatement = "DBMS_SCHEDULER.ADD_WINDOW_GROUP_MEMBER(" + mGroupName + 
                                "," + mMemberName + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.ADD_GROUP_MEMBER(?, ?)");

        cs.setString(1, mGroupName);
        cs.setString(2, mMemberName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("AssignWindow"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int RemoveWindowGroupMember(String mWindowName,
                                 String mWindowGroupName) {

    String mCallStatement = "DBMS_SCHEDULER.REMOVE_WINDOW_GROUP_MEMBER(" + mWindowGroupName + 
                            "," + mWindowName + ")";

    try {

        CallableStatement cs = conn.prepareCall (" call DBMS_SCHEDULER.REMOVE_WINDOW_GROUP_MEMBER(?, ?)");

        cs.setString(1, mWindowGroupName);
        cs.setString(2, mWindowName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {

        SchedFile.EnterErrorEntry("RemoveWindowGroupMember"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int RemoveFromGroup(String mMemberName,
                                      String mGroupName) {

        String mCallStatement = "DBMS_SCHEDULER.REMOVE_GROUP_MEMBER(" + mGroupName + 
                                "," + mMemberName + ")";

    try {

        CallableStatement cs = conn.prepareCall (" call DBMS_SCHEDULER.REMOVE_GROUP_MEMBER(?, ?)");

        cs.setString(1, mGroupName);
        cs.setString(2, mMemberName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("RemoveFromGroup"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int OpenWindow(String mWindowName) {

        String mCallStatement = "DBMS_SCHEDULER.OPEN_WINDOW(" + mWindowName + ")";

    try {

        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.OPEN_WINDOW(?, null)");

        cs.setString(1, mWindowName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("OpenWindow"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public int CloseWindow(String mWindowName) {

        String mCallStatement = "DBMS_SCHEDULER.CLOSE_WINDOW(" + mWindowName + ")";

    try {

        // Create a statement
        CallableStatement cs = conn.prepareCall ("call DBMS_SCHEDULER.CLOSE_WINDOW(?)");

        cs.setString(1, mWindowName);

        cs.executeUpdate();

        SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

        return 0;
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CloseWindow"," : Error..." + mCallStatement);

        setSysMessage(e.getMessage());

        return 1;
    }
    }

    public String CalculateRunDates(String mCalendarString,
                                      String mStartDate,
                                      String mAfterDate,
                                      int    mFirst) {
    try {
        try {
            mSDate = Timestamp.valueOf(mStartDate);
        }
        catch(IllegalArgumentException e) {
            setSysMessage("Start Date has wrong format.");
            return null;
        }

        try {
            mEDate = Timestamp.valueOf(mAfterDate);
        }
        catch(IllegalArgumentException e) {
            setSysMessage("After Date has wrong format.");
            return null;
        }

        CallableStatement cs = conn.prepareCall (
            "begin DBMS_SCHEDULER.EVALUATE_CALENDAR_STRING( " +
            "    CALENDAR_STRING => ?, " +
            "    START_DATE => ?, " +
            "    RETURN_DATE_AFTER => ?, " +
            "    NEXT_RUN_DATE => ? ); end;"
        );

        cs.setString(1, mCalendarString);
        cs.setTimestamp(2, mSDate);
        cs.setTimestamp(3, mEDate);
        cs.registerOutParameter(4, java.sql.Types.TIMESTAMP);

        cs.executeUpdate();

        mNDate = cs.getTimestamp(4);

        cs.close();

        return mNDate.toString().substring(0,19);
    }
    catch(SQLException e) {
        SchedFile.EnterErrorEntry("CalculateRunDates"," : Error..." + e.getMessage());

        return null;
    }
    }

  // ************************************************** //
  // Start of the Resource statements.
  // ************************************************** //

  public void GetPlansData() {
    try {
      // Get the plans data.

      getDataStmt = conn.prepareStatement(GetPlansString() 
              + " from DBA_RSRC_PLANS "
              + " order by plan");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        String m_Plan = rset.getString(1);
        int    m_NumPlanDirectives = rset.getInt(2);
        String m_CpuMethod = rset.getString(3);
        String m_ActiveSessionPoolMth = rset.getString(4);
        String m_ParallelDegreeLimitMth = rset.getString(5);
        String m_QueueingMth = rset.getString(6);
        String m_Comments = rset.getString(7);
        String m_Status = rset.getString(8);
        String m_Mandatory = rset.getString(9);

        int    m_Plan_Id = 0;
        String m_Mgmt_Method = "";
        String m_Sub_Plan = "";

        if (versionNo > 2) {
            m_Plan_Id = rset.getInt(10);
            m_Mgmt_Method = rset.getString(11);
            m_Sub_Plan = rset.getString(12);
        }

        m_PlanItem = new PlanItem(
                    getNextSeqNo(),
                    m_Plan_Id,
                    m_Plan,
                    m_NumPlanDirectives,
                    m_CpuMethod,
                    m_Mgmt_Method,
                    m_ActiveSessionPoolMth,
                    m_ParallelDegreeLimitMth,
                    m_QueueingMth,
                    m_Sub_Plan,
                    m_Comments,
                    m_Status,
                    m_Mandatory);

        addPlan(m_PlanItem);

      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetPlansData"," : Error..." + e.getMessage());
    }
  }

  public SchedDataArea.PlanItem GetPlanItem(String mPlan,
                                            int    mAddUpdate)   {
    try {
      // Get the plan data.

      getDataStmt = conn.prepareStatement(GetPlansString() 
            + " from DBA_RSRC_PLANS "
            + " where status = 'PENDING' "
            + "   and plan = ?");

      getDataStmt.setString(1, mPlan.toUpperCase());
      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        String m_Plan = rset.getString(1);
        int    m_NumPlanDirectives = rset.getInt(2);
        String m_CpuMethod = rset.getString(3);
        String m_ActiveSessionPoolMth = rset.getString(4);
        String m_ParallelDegreeLimitMth = rset.getString(5);
        String m_QueueingMth = rset.getString(6);
        String m_Comments = rset.getString(7);
        String m_Status = rset.getString(8);
        String m_Mandatory = rset.getString(9);

        int    m_Plan_Id = 0;
        String m_Mgmt_Method = "";
        String m_Sub_Plan = "";

        if (versionNo > 2) {
            m_Plan_Id = rset.getInt(10);
            m_Mgmt_Method = rset.getString(11);
            m_Sub_Plan = rset.getString(12);
        }

        if (mAddUpdate == 0) {

            m_PlanItem = new PlanItem(
                    getNextSeqNo(),
                    m_Plan_Id,
                    m_Plan,
                    m_NumPlanDirectives,
                    m_CpuMethod,
                    m_Mgmt_Method,
                    m_ActiveSessionPoolMth,
                    m_ParallelDegreeLimitMth,
                    m_QueueingMth,
                    m_Sub_Plan,
                    m_Comments,
                    m_Status,
                    m_Mandatory);

            addPlan(m_PlanItem);

        }

        if (mAddUpdate == 1) {
            m_PlanItem = updatePlanItem(
                    mPlan,
                    m_Mgmt_Method,
                    m_Comments);
        }
      }
      rset.close();
      getDataStmt.close();

      return m_PlanItem;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetPlanItem"," : Error..." + e.getMessage());
      return null;
    }
  }

  private String GetPlansString() {
    StringBuffer selectStr = new StringBuffer("a");
    selectStr.delete(0, selectStr.length());
    switch(versionNo) {
        case 1:
        case 2:
            selectStr.insert(0,"select plan, num_plan_directives, "
                + "cpu_method, active_sess_pool_mth, "
                + " parallel_degree_limit_mth, queueing_mth, "
                + " comments, status, mandatory ");
                break;
        case 3:
        case 4:
        case 5:
            selectStr.insert(0,"select plan, num_plan_directives, "
                + "cpu_method, active_sess_pool_mth, "
                + " parallel_degree_limit_mth, queueing_mth, "
                + " comments, status, mandatory, plan_id, "
                + " mgmt_method, sub_plan ");
                break;
    }

    return selectStr.toString();

  }

  public void GetCdbPlansData() {
    try {
      // Get the cdb plans data.

      getDataStmt = conn.prepareStatement("SELECT plan_id, "
              + " plan, comments, status, mandatory " 
              + " from DBA_CDB_RSRC_PLANS "
              + " order by plan");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        int    m_Plan_Id = rset.getInt(1);
        String m_Plan = rset.getString(2);
        String m_Comments = rset.getString(3);
        String m_Status = rset.getString(4);
        String m_Mandatory = rset.getString(5);

        m_CdbPlanItem = new CdbPlanItem(
                    getNextSeqNo(),
                    m_Plan_Id,
                    m_Plan,
                    m_Comments,
                    m_Status,
                    m_Mandatory);

        addCdbPlan(m_CdbPlanItem);
      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetCdbPlansData"," : Error..." + e.getMessage());
    }
  }

  public SchedDataArea.CdbPlanItem GetCdbPlanItem(String mPlan,
                                                  int    mAddUpdate)   {
    try {
      // Get the cdb plans data.

      getDataStmt = conn.prepareStatement("SELECT plan_id, "
              + " plan, comments, status, mandatory " 
              + " from DBA_CDB_RSRC_PLANS "
              + " where status = 'PENDING' "
              + "   and plan = ?");

      getDataStmt.setString(1, mPlan.toUpperCase());
      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        int    m_Plan_Id = rset.getInt(1);
        String m_Plan = rset.getString(2);
        String m_Comments = rset.getString(3);
        String m_Status = rset.getString(4);
        String m_Mandatory = rset.getString(5);

        if (mAddUpdate == 0) {
            m_CdbPlanItem = new CdbPlanItem(
                    getNextSeqNo(),
                    m_Plan_Id,
                    m_Plan,
                    m_Comments,
                    m_Status,
                    m_Mandatory);

            addCdbPlan(m_CdbPlanItem);
        }
        if (mAddUpdate == 1) {
            m_CdbPlanItem = updateCdbPlanItem(
                    mPlan,
                    m_Comments);
        }
      }
      rset.close();
      getDataStmt.close();

      return m_CdbPlanItem;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetCdbPlansData"," : Error..." + e.getMessage());
      return null;
    }
  }


  public void GetConsumerGroupsData() {
    try {
      // Get the Consumer Groups data.

      getDataStmt = conn.prepareStatement(GetConsumerGroupsString() 
              + " from DBA_RSRC_CONSUMER_GROUPS "
              + " order by consumer_group");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        String m_ConsumerGroup = rset.getString(1);
        String m_CpuMethod = rset.getString(2);
        String m_Comments = rset.getString(3);
        String m_Status = rset.getString(4);
        String m_Mandatory = rset.getString(5);

        int    m_Consumer_Group_Id = 0;
        String m_Mgmt_Method = "";
        String m_Internal_Use = "";
        String m_Category = "";

        if (versionNo > 2) {
            m_Consumer_Group_Id = rset.getInt(6);
            m_Mgmt_Method = rset.getString(7);
            m_Internal_Use = rset.getString(8);
            m_Category = rset.getString(9);
        }

        m_ConsumerGroupItem = new ConsumerGroupItem(
                    getNextSeqNo(),
                    m_Consumer_Group_Id,
                    m_ConsumerGroup,
                    m_CpuMethod,
                    m_Mgmt_Method,
                    m_Internal_Use,
                    m_Comments,
                    m_Category,
                    m_Status,
                    m_Mandatory);

        addConsumerGroup(m_ConsumerGroupItem);
      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetConsumerGroupsData"," : Error..." + e.getMessage());
    }
  }

  public SchedDataArea.ConsumerGroupItem GetConsumerGroupItem(
                                 String mConsumerGroup,
                                 int mAddUpdate)   {
    try {
      // Get the Consumer Groups data.

      getDataStmt = conn.prepareStatement(GetConsumerGroupsString() 
            + " from DBA_RSRC_CONSUMER_GROUPS "
            + " where status = 'PENDING' "
            + "   and consumer_group = ?");

      getDataStmt.setString(1, mConsumerGroup.toUpperCase());

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        String m_ConsumerGroup = rset.getString(1);
        String m_CpuMethod = rset.getString(2);
        String m_Comments = rset.getString(3);
        String m_Status = rset.getString(4);
        String m_Mandatory = rset.getString(5);

        int    m_Consumer_Group_Id = 0;
        String m_Mgmt_Method = "";
        String m_Internal_Use = "";
        String m_Category = "";

        if (mAddUpdate == 0) {
            m_ConsumerGroupItem = new ConsumerGroupItem(
                    getNextSeqNo(),
                    m_Consumer_Group_Id,
                    m_ConsumerGroup,
                    m_CpuMethod,
                    m_Mgmt_Method,
                    m_Internal_Use,
                    m_Comments,
                    m_Category,
                    m_Status,
                    m_Mandatory);

            addConsumerGroup(m_ConsumerGroupItem);
        }
        if (mAddUpdate == 1) {
            m_ConsumerGroupItem = updateConsumerGroupItem(
                        m_ConsumerGroup,
                        m_Comments);

        }
      }
      rset.close();
      getDataStmt.close();

      return m_ConsumerGroupItem;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetConsumerGroupItem"," : Error..." + e.getMessage());
      return null;
    }
  }

  private String GetConsumerGroupsString() {
    StringBuffer selectStr = new StringBuffer("a");
    selectStr.delete(0, selectStr.length());
    switch (versionNo) {
        case 1:
        case 2:
            selectStr.insert(0,"select consumer_group, cpu_method, "
                + " comments, status, mandatory ");
                break;
        case 3:
        case 4:
        case 5:
            selectStr.insert(0,"select consumer_group, cpu_method, "
                + " comments, status, mandatory,  consumer_group_id, "
                + " mgmt_method, internal_use, category ");
                break;
    }

    return selectStr.toString();

  }

  public void GetPlanDirectivesData() {
    try {

      // Get the plan directives data.

      getDataStmt = conn.prepareStatement( GetPlanDirectiveString()
              + " from DBA_RSRC_PLAN_DIRECTIVES "
              + " order by plan, group_or_subplan ");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        String m_Plan = rset.getString(1);
        String m_Group = rset.getString(2);
        String m_Type = rset.getString(3);
        int    m_Mgmt_P1 = rset.getInt(4);
        int    m_Mgmt_P2 = rset.getInt(5);
        int    m_Mgmt_P3 = rset.getInt(6);
        int    m_Mgmt_P4 = rset.getInt(7);
        int    m_Mgmt_P5 = rset.getInt(8);
        int    m_Mgmt_P6 = rset.getInt(9);
        int    m_Mgmt_P7 = rset.getInt(10);
        int    m_Mgmt_P8 = rset.getInt(11);
        int    m_ActiveSessPool = rset.getInt(12);
        int    m_QueueingP1 = rset.getInt(13);
        int    m_ParallelDegreeLimitP1 = rset.getInt(14);
        String m_SwitchGroup = rset.getString(15);
        int    m_SwitchTime = rset.getInt(16);
        String m_SwitchEstimate = rset.getString(17);
        int    m_MaxEstExecTime = rset.getInt(18);
        int    m_UndoPool = rset.getInt(19);
        int    m_MaxIdleTime = rset.getInt(20);
        int    m_MaxIdleBlockerTime = rset.getInt(21);
        int    m_SwitchTimeInCall = rset.getInt(22);
        String m_Comments = rset.getString(23);
        String m_Status = rset.getString(24);
        String m_Mandatory = rset.getString(25);

        String m_SwitchForCall = "";
        int    m_SwitchIoMegabytes = 0;
        int    m_SwitchIoReqs = 0;
        int    m_MaxUtilLimit = 0;

        if (versionNo > 2) {
            m_SwitchForCall = rset.getString(26);
            m_SwitchIoMegabytes = rset.getInt(27);
            m_SwitchIoReqs = rset.getInt(28);
            if (versionNo > 3)
                m_MaxUtilLimit = rset.getInt(29);
        }
        // System.out.println(" Point Y1 - " + m_Plan + "--" + m_Group + "--" +
        //     m_Mgmt_P1 + "--" + m_Mgmt_P2 + "--" + m_Mgmt_P3 + "--" + m_Mgmt_P4 + "--" + m_Mgmt_P5 );

        m_PlanDirectiveItem = new PlanDirectiveItem(
                    getNextSeqNo(),
                    m_Plan,
                    m_Group,
                    m_Type,
                    m_Mgmt_P1,
                    m_Mgmt_P2,
                    m_Mgmt_P3,
                    m_Mgmt_P4,
                    m_Mgmt_P5,
                    m_Mgmt_P6,
                    m_Mgmt_P7,
                    m_Mgmt_P8,
                    m_ActiveSessPool,
                    m_QueueingP1,
                    m_ParallelDegreeLimitP1,
                    m_SwitchGroup,
                    m_SwitchForCall,
                    m_SwitchTime,
                    m_SwitchIoMegabytes,
                    m_SwitchIoReqs,
                    m_SwitchEstimate,
                    m_MaxEstExecTime,
                    m_UndoPool,
                    m_MaxIdleTime,
                    m_MaxIdleBlockerTime,
                    m_SwitchTimeInCall,
                    m_Comments,
                    m_Status,
                    m_Mandatory,
                    m_MaxUtilLimit);

        addPlanDirective(m_PlanDirectiveItem);

      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetPlanDirectivesData"," : Error..." + e.getMessage());
    }
  }

  public SchedDataArea.PlanDirectiveItem GetPlanDirectiveItem(
                                           String mPlan,
                                           String mGroup,
                                           int mAddUpdate)   {
    try {

      // Get the plan directives item.
      getDataStmt = conn.prepareStatement( GetPlanDirectiveString()
              + " from DBA_RSRC_PLAN_DIRECTIVES "
              + " where status = 'PENDING' and plan = ? and group_or_subplan = ? ");

      getDataStmt.setString(1, mPlan.toUpperCase());
      getDataStmt.setString(2, mGroup.toUpperCase());

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        String m_Plan = rset.getString(1);
        String m_Group = rset.getString(2);
        String m_Type = rset.getString(3);
        int    m_Mgmt_P1 = rset.getInt(4);
        int    m_Mgmt_P2 = rset.getInt(5);
        int    m_Mgmt_P3 = rset.getInt(6);
        int    m_Mgmt_P4 = rset.getInt(7);
        int    m_Mgmt_P5 = rset.getInt(8);
        int    m_Mgmt_P6 = rset.getInt(9);
        int    m_Mgmt_P7 = rset.getInt(10);
        int    m_Mgmt_P8 = rset.getInt(11);
        int    m_ActiveSessPool = rset.getInt(12);
        int    m_QueueingP1 = rset.getInt(13);
        int    m_ParallelDegreeLimitP1 = rset.getInt(14);
        String m_SwitchGroup = rset.getString(15);
        int    m_SwitchTime = rset.getInt(16);
        String m_SwitchEstimate = rset.getString(17);
        int    m_MaxEstExecTime = rset.getInt(18);
        int    m_UndoPool = rset.getInt(19);
        int    m_MaxIdleTime = rset.getInt(20);
        int    m_MaxIdleBlockerTime = rset.getInt(21);
        int    m_SwitchTimeInCall = rset.getInt(22);
        String m_Comments = rset.getString(23);
        String m_Status = rset.getString(24);
        String m_Mandatory = rset.getString(25);

        String m_SwitchForCall = "";
        int    m_SwitchIoMegabytes = 0;
        int    m_SwitchIoReqs = 0;
        int    m_MaxUtilLimit = 0;

        if (versionNo > 2) {
            m_SwitchForCall = rset.getString(26);
            m_SwitchIoMegabytes = rset.getInt(27);
            m_SwitchIoReqs = rset.getInt(28);
            if (versionNo > 3)
                m_MaxUtilLimit = rset.getInt(29);
        }

        if (mAddUpdate == 0) {
            m_PlanDirectiveItem = new PlanDirectiveItem(
                    getNextSeqNo(),
                    m_Plan,
                    m_Group,
                    m_Type,
                    m_Mgmt_P1,
                    m_Mgmt_P2,
                    m_Mgmt_P3,
                    m_Mgmt_P4,
                    m_Mgmt_P5,
                    m_Mgmt_P6,
                    m_Mgmt_P7,
                    m_Mgmt_P8,
                    m_ActiveSessPool,
                    m_QueueingP1,
                    m_ParallelDegreeLimitP1,
                    m_SwitchGroup,
                    m_SwitchForCall,
                    m_SwitchTime,
                    m_SwitchIoMegabytes,
                    m_SwitchIoReqs,
                    m_SwitchEstimate,
                    m_MaxEstExecTime,
                    m_UndoPool,
                    m_MaxIdleTime,
                    m_MaxIdleBlockerTime,
                    m_SwitchTimeInCall,
                    m_Comments,
                    m_Status,
                    m_Mandatory,
                    m_MaxUtilLimit);

            addPlanDirective(m_PlanDirectiveItem);
        }
        else {

            m_PlanDirectiveItem = updatePlanDirectiveItem(
                    m_Plan,
                    m_Group,
                    m_Type,
                    m_Mgmt_P1,
                    m_Mgmt_P2,
                    m_Mgmt_P3,
                    m_Mgmt_P4,
                    m_Mgmt_P5,
                    m_Mgmt_P6,
                    m_Mgmt_P7,
                    m_Mgmt_P8,
                    m_ActiveSessPool,
                    m_QueueingP1,
                    m_ParallelDegreeLimitP1,
                    m_SwitchGroup,
                    m_SwitchForCall,
                    m_SwitchTime,
                    m_SwitchIoMegabytes,
                    m_SwitchIoReqs,
                    m_SwitchEstimate,
                    m_MaxEstExecTime,
                    m_UndoPool,
                    m_MaxIdleTime,
                    m_MaxIdleBlockerTime,
                    m_SwitchTimeInCall,
                    m_Comments,
                    m_Status,
                    m_Mandatory,
                    m_MaxUtilLimit);
        }
      }
      rset.close();
      getDataStmt.close();
      return m_PlanDirectiveItem;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetPlanDirectivesItem"," : Error..." + e.getMessage());
      return null;
    }
  }

  private String GetPlanDirectiveString() {
    StringBuffer selectStr = new StringBuffer("a");
    selectStr.delete(0, selectStr.length());
    switch (versionNo) {
        case 1:
        case 2:
            selectStr.insert(0,"select plan, group_or_subplan, type, "
                + " cpu_p1, cpu_p2, cpu_p3, cpu_p4, "
                + " cpu_p5, cpu_p6, cpu_p7, cpu_p8, "
                + " active_sess_pool_p1, queueing_p1, "
                + " parallel_degree_limit_p1, switch_group, "
                + " switch_time, switch_estimate, "
                + " max_est_exec_time, "
                + " undo_pool, max_idle_time, max_idle_blocker_time, "
                + " switch_time_in_call, comments, status, mandatory ");
            break;
        case 3:
            selectStr.insert(0,"select plan, group_or_subplan, type, "
                + " mgmt_p1, mgmt_p2, mgmt_p3, mgmt_p4, "
                + " mgmt_p5, mgmt_p6, mgmt_p7, mgmt_p8, "
                + " active_sess_pool_p1, queueing_p1, "
                + " parallel_degree_limit_p1, switch_group, "
                + " switch_time, switch_estimate, "
                + " max_est_exec_time, "
                + " undo_pool, max_idle_time, max_idle_blocker_time, "
                + " switch_time_in_call, comments, status, mandatory, "
                + " switch_for_call, switch_io_megabytes, switch_io_reqs ");
            break;
        case 4:
        case 5:
            selectStr.insert(0,"select plan, group_or_subplan, type, "
                + " mgmt_p1, mgmt_p2, mgmt_p3, mgmt_p4, "
                + " mgmt_p5, mgmt_p6, mgmt_p7, mgmt_p8, "
                + " active_sess_pool_p1, queueing_p1, "
                + " parallel_degree_limit_p1, switch_group, "
                + " switch_time, switch_estimate, "
                + " max_est_exec_time, "
                + " undo_pool, max_idle_time, max_idle_blocker_time, "
                + " switch_time_in_call, comments, status, mandatory, "
                + " switch_for_call, switch_io_megabytes, switch_io_reqs, "
                + " max_utilization_limit ");
            break;
    }
    return selectStr.toString();
  }

  public void GetCdbPlanDirectivesData() {
    try {

      // Get the CDB plan directives data.

      getDataStmt = conn.prepareStatement( "select plan, "
              + " pluggable_database, shares, utilization_limit, "
              + " parallel_server_limit, comments, status, mandatory "
              + " from DBA_CDB_RSRC_PLAN_DIRECTIVES "
              + " order by plan, pluggable_database ");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        String m_Plan = rset.getString(1);
        String m_PluggableDatabase = rset.getString(2);
        int    m_Shares = rset.getInt(3);
        int    m_UtilizationLimit = rset.getInt(4);
        int    m_ParallelServerLimit = rset.getInt(5);
        String m_Comments = rset.getString(6);
        String m_Status = rset.getString(7);
        String m_Mandatory = rset.getString(8);

        m_CdbPlanDirectiveItem = new CdbPlanDirectiveItem(
                    getNextSeqNo(),
                    m_Plan,
                    m_PluggableDatabase,
                    m_Shares,
                    m_UtilizationLimit,
                    m_ParallelServerLimit,
                    m_Comments,
                    m_Status,
                    m_Mandatory);

        addCdbPlanDirective(m_CdbPlanDirectiveItem);

      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetCdbPlanDirectivesData"," : Error..." + e.getMessage());
    }
  }

  public SchedDataArea.CdbPlanDirectiveItem GetCdbPlanDirectiveItem(String mPlan,
                                                                    String mPluggableDatabase,
                                                                    int mAddUpdate)   {
    try {

      // Get the CDB plan directives data.

      getDataStmt = conn.prepareStatement( "select plan, "
              + " pluggable_database, shares, utilization_limit, "
              + " parallel_server_limit, comments, status, mandatory "
              + " from DBA_CDB_RSRC_PLAN_DIRECTIVES "
              + " where status = 'PENDING' and plan = ? and pluggable_database = ? ");

      getDataStmt.setString(1, mPlan.toUpperCase());
      getDataStmt.setString(2, mPluggableDatabase.toUpperCase());

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        String m_Plan = rset.getString(1);
        String m_PluggableDatabase = rset.getString(2);
        int    m_Shares = rset.getInt(3);
        int    m_UtilizationLimit = rset.getInt(4);
        int    m_ParallelServerLimit = rset.getInt(5);
        String m_Comments = rset.getString(6);
        String m_Status = rset.getString(7);
        String m_Mandatory = rset.getString(8);

        if (mAddUpdate == 0) {
            m_CdbPlanDirectiveItem = new CdbPlanDirectiveItem(
                    getNextSeqNo(),
                    m_Plan,
                    m_PluggableDatabase,
                    m_Shares,
                    m_UtilizationLimit,
                    m_ParallelServerLimit,
                    m_Comments,
                    m_Status,
                    m_Mandatory);

            addCdbPlanDirective(m_CdbPlanDirectiveItem);
        }
        else {

            m_CdbPlanDirectiveItem = updateCdbPlanDirectiveItem(
                    m_Plan,
                    m_PluggableDatabase,
                    m_Shares,
                    m_UtilizationLimit,
                    m_ParallelServerLimit,
                    m_Comments,
                    m_Status,
                    m_Mandatory);
        }
      }
      rset.close();
      getDataStmt.close();

      return m_CdbPlanDirectiveItem;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetCdbPlanDirectiveItem"," : Error..." + e.getMessage());
      return null;
    }
  }

  public void GetMappingPriorityData() {
    try {

      // Get the group mapping priorities data.
      getDataStmt = conn.prepareStatement(
            "select attribute, priority, status "
            + " from DBA_RSRC_MAPPING_PRIORITY "
            + " order by attribute");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        String m_Attribute = rset.getString(1);
        int    m_Priority = rset.getInt(2);
        String m_Status = rset.getString(3);

        m_MappingPrioritiesItem = new MappingPrioritiesItem(
                    getNextSeqNo(),
                    m_Attribute,
                    m_Priority,
                    m_Status);

        addMappingPriority(m_MappingPrioritiesItem);
      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetMappingPriorityData"," : Error..." + e.getMessage());
    }
  }

  public void GetGroupMappingsData() {
    try {

      // Get the group mappings data.

      getDataStmt = conn.prepareStatement(
            "select attribute, value, consumer_group, status "
            + " from DBA_RSRC_GROUP_MAPPINGS "
            + " order by consumer_group, value");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        String m_Attribute = rset.getString(1);
        String m_Value = rset.getString(2);
        String m_ConsumerGroup = rset.getString(3);
        String m_Status = rset.getString(4);

        m_GroupMappingsItem = new GroupMappingsItem(
                    getNextSeqNo(),
                    m_Attribute,
                    m_Value,
                    m_ConsumerGroup,
                    m_Status);

        addGroupMappings(m_GroupMappingsItem);
      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetGroupMappingsData"," : Error..." + e.getMessage());
    }
  }

  public SchedDataArea.GroupMappingsItem GetGroupMappingsItem(
                                          String mAttribute,
                                          String mValue,
                                          int    mAddUpdate)   {
    try {

      // Get the group mappings item.

      getItemStmt = conn.prepareStatement(
            "select attribute, value, consumer_group, status "
            + " from DBA_RSRC_GROUP_MAPPINGS "
            + " where status = 'PENDING' and attribute = ? and value = ? ");

      getItemStmt.setString(1, mAttribute.toUpperCase());
      getItemStmt.setString(2, mValue.toUpperCase());

      ResultSet rset = getItemStmt.executeQuery();

      while (rset.next()) {
        String m_Attribute = rset.getString(1);
        String m_Value = rset.getString(2);
        String m_ConsumerGroup = rset.getString(3);
        String m_Status = rset.getString(4);

        if (mAddUpdate == 0) {
            m_GroupMappingsItem = new GroupMappingsItem(
                    getNextSeqNo(),
                    m_Attribute,
                    m_Value,
                    m_ConsumerGroup,
                    m_Status);

            addGroupMappings(m_GroupMappingsItem);
        }
        else {
            m_GroupMappingsItem = updateGroupMappingsItem(
                    m_Attribute,
                    m_Value,
                    m_ConsumerGroup,
                    m_Status);
        }
      }
      rset.close();
      getItemStmt.close();
      return m_GroupMappingsItem;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetGroupMappingsItem"," : Error..." + e.getMessage());
      return null;
    }
  }

  public void GetSessionData() {
    try {

      // Get the plan directives data.

      getDataStmt = conn.prepareStatement( GetSessionString()
            + " from V$RSRC_SESSION_INFO si, V$SESSION s, "
            + " V$RSRC_CONSUMER_GROUP co1, V$RSRC_CONSUMER_GROUP co2 "
            + " where si.sid = s.sid(+) "
            + " and si.current_consumer_group_id = co1.id(+) "
            + " and si.orig_consumer_group_id = co2.id(+) "
            + " order by si.sid ");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        int    m_Sid = rset.getInt(1);
        int    m_Serial = rset.getInt(2);
        String m_Username = rset.getString(3);
        String m_Osuser = rset.getString(4);
        String m_Machine = rset.getString(5);
        String m_Module = rset.getString(6);
        String m_ConsGroup = rset.getString(7);
        String m_OrigConsGroup = rset.getString(8);
        String m_MappingAttribute = rset.getString(9);
        String m_MappedConsumerGroup = rset.getString(10);
        String m_State = rset.getString(11);
        String m_Active = rset.getString(12);
        int    m_CurrentIdleTime = rset.getInt(13);
        int    m_CurrentCpuWaitTime = rset.getInt(14);
        int    m_CpuWaitTime = rset.getInt(15);
        int    m_CurrentCpuWaits = rset.getInt(16);
        int    m_CpuWaits = rset.getInt(17);
        int    m_CurrentConsumedCpuTime = rset.getInt(18);
        int    m_ConsumedCpuTime = rset.getInt(19);
        int    m_CurrentActiveTime = rset.getInt(20);
        int    m_ActiveTime = rset.getInt(21);
        int    m_CurrentQueuedTime = rset.getInt(22);
        int    m_QueuedTime = rset.getInt(23);
        int    m_CurrentYields = rset.getInt(24);
        int    m_Yields = rset.getInt(25);
        int    m_CurrentUndoConsumption = rset.getInt(26);
        int    m_MaxUndoConsumption = rset.getInt(27);
        int    m_SqlCanceled = rset.getInt(28);
        int    m_QueueTimeOuts = rset.getInt(29);
        int    m_EstimatedExecutionLimitHit = rset.getInt(30);

        int    m_CurrentIoServiceTime = 0;
        int    m_IoServiceTime = 0;
        int    m_CurrentIoServiceWaits = 0;
        int    m_IoServiceWaits = 0;
        int    m_CurSmallReadMegabytes = 0;
        int    m_SmallReadMegabytes = 0;
        int    m_CurLargeReadMegabytes = 0;
        int    m_LargeReadMegabytes = 0;
        int    m_CurSmallWriteMegabytes = 0;
        int    m_SmallWriteMegabytes = 0;
        int    m_CurLargeWriteMegabytes = 0;
        int    m_LargeWriteMegabytes = 0;
        int    m_CurSmallReadRequests = 0;
        int    m_SmallReadRequests = 0;
        int    m_CurSmallWriteRequests = 0;
        int    m_SmallWriteRequests = 0;
        int    m_CurLargeReadRequests = 0;
        int    m_LargeReadRequests = 0;
        int    m_CurLargeWriteRequests = 0;
        int    m_LargeWriteRequests = 0;

        int    m_CurrentPqActiveTime = 0;
        int    m_PqActiveTime = 0;
        int    m_Dop = 0;
        int    m_PqServers = 0;
        int    m_EstimatedExecutionTime = 0;
        int    m_CurrentPqQueuedTime = 0;
        int    m_PqQueuedTime = 0;
        int    m_PqQueued = 0;
        int    m_PqQueueTimeOuts = 0;
        String m_PqActive = "";
        String m_PqStatus = "";
        int    m_CurrentLogicalIos = 0;
        int    m_LogicalIos = 0;
        int    m_CurrentElapsedTime = 0;
        int    m_ElapsedTime = 0;
        String m_LastAction = "";
        String m_LastActionReason = "";
        int    m_LastActionTime = 0;
        int    m_ConId = 0;


        if (versionNo > 2) {
            m_CurrentIoServiceTime = rset.getInt(31);
            m_IoServiceTime = rset.getInt(32);
            m_CurrentIoServiceWaits = rset.getInt(33);
            m_IoServiceWaits = rset.getInt(34);
            m_CurSmallReadMegabytes = rset.getInt(35);
            m_SmallReadMegabytes = rset.getInt(36);
            m_CurLargeReadMegabytes = rset.getInt(37);
            m_LargeReadMegabytes = rset.getInt(38);
            m_CurSmallWriteMegabytes = rset.getInt(39);
            m_SmallWriteMegabytes = rset.getInt(40);
            m_CurLargeWriteMegabytes = rset.getInt(41);
            m_LargeWriteMegabytes = rset.getInt(42);
            m_CurSmallReadRequests = rset.getInt(43);
            m_SmallReadRequests = rset.getInt(44);
            m_CurSmallWriteRequests = rset.getInt(45);
            m_SmallWriteRequests = rset.getInt(46);
            m_CurLargeReadRequests = rset.getInt(47);
            m_LargeReadRequests = rset.getInt(48);
            m_CurLargeWriteRequests = rset.getInt(49);
            m_LargeWriteRequests = rset.getInt(50);

            if (versionNo > 4) {
                m_CurrentPqActiveTime = rset.getInt(51);
                m_PqActiveTime = rset.getInt(52);
                m_Dop = rset.getInt(53);
                m_PqServers = rset.getInt(54);
                m_EstimatedExecutionTime = rset.getInt(55);
                m_CurrentPqQueuedTime = rset.getInt(56);
                m_PqQueuedTime = rset.getInt(57);
                m_PqQueued = rset.getInt(58);
                m_PqQueueTimeOuts = rset.getInt(59);
                m_PqActive = rset.getString(60);
                m_PqStatus = rset.getString(61);
                m_CurrentLogicalIos = rset.getInt(62);
                m_LogicalIos = rset.getInt(63);
                m_CurrentElapsedTime = rset.getInt(64);
                m_ElapsedTime = rset.getInt(65);
                m_LastAction = rset.getString(66);
                m_LastActionReason = rset.getString(67);
                m_LastActionTime = rset.getInt(68);
                m_ConId = rset.getInt(69);
            }
        }

        // System.out.println(" 1. " + m_Sid + "--" + m_Serial);

        m_SessionItem = new SessionItem(
                    m_Sid,
                    m_Serial,
                    m_Username,
                    m_Osuser,
                    m_Machine,
                    m_Module,
                    m_ConsGroup,
                    m_OrigConsGroup,
                    m_MappingAttribute,
                    m_MappedConsumerGroup,
                    m_State,
                    m_Active,
                    m_CurrentIdleTime,
                    m_CurrentCpuWaitTime,
                    m_CpuWaitTime,
                    m_CurrentCpuWaits,
                    m_CpuWaits,
                    m_CurrentConsumedCpuTime,
                    m_ConsumedCpuTime,
                    m_CurrentActiveTime,
                    m_ActiveTime,
                    m_CurrentQueuedTime,
                    m_QueuedTime,
                    m_CurrentYields,
                    m_Yields,
                    m_CurrentUndoConsumption,
                    m_MaxUndoConsumption,
                    m_SqlCanceled,
                    m_QueueTimeOuts,
                    m_EstimatedExecutionLimitHit,
                    m_CurrentIoServiceTime,
                    m_IoServiceTime,
                    m_CurrentIoServiceWaits,
                    m_IoServiceWaits,
                    m_CurSmallReadMegabytes,
                    m_SmallReadMegabytes,
                    m_CurLargeReadMegabytes,
                    m_LargeReadMegabytes,
                    m_CurSmallWriteMegabytes,
                    m_SmallWriteMegabytes,
                    m_CurLargeWriteMegabytes,
                    m_LargeWriteMegabytes,
                    m_CurSmallReadRequests,
                    m_SmallReadRequests,
                    m_CurSmallWriteRequests,
                    m_SmallWriteRequests,
                    m_CurLargeReadRequests,
                    m_LargeReadRequests,
                    m_CurLargeWriteRequests,
                    m_LargeWriteRequests,
                    m_CurrentPqActiveTime,
                    m_PqActiveTime,
                    m_Dop,
                    m_PqServers,
                    m_EstimatedExecutionTime,
                    m_CurrentPqQueuedTime,
                    m_PqQueuedTime,
                    m_PqQueued,
                    m_PqQueueTimeOuts,
                    m_PqActive,
                    m_PqStatus,
                    m_CurrentLogicalIos,
                    m_LogicalIos,
                    m_CurrentElapsedTime,
                    m_ElapsedTime,
                    m_LastAction,
                    m_LastActionReason,
                    m_LastActionTime,
                    m_ConId);

        addSession(m_SessionItem);

      }
      rset.close();
      getDataStmt.close();

    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetSessionData"," : Error..." + e.getMessage());
    }
  }

  private String GetSessionString() {
    StringBuffer selectStr = new StringBuffer("a");
    selectStr.delete(0, selectStr.length());
    switch (versionNo) {
        case 1:
        case 2:
            selectStr.insert(0, 
                "select si.sid, s.serial#, s.username, s.osuser, s.machine, s.module, "
                + " co1.name, co2.name, "
                + " si.mapping_attribute, si.mapped_consumer_group, "
                + " si.state, si.active, si.current_idle_time, "
                + " si.current_cpu_wait_time, "
                + " si.cpu_wait_time, si.current_cpu_waits, "
                + " si.cpu_waits, si.current_consumed_cpu_time, "
                + " si.consumed_cpu_time, si.current_active_time, si.active_time, "
                + " si.current_queued_time, si.queued_time, si.current_yields, "
                + " si.yields, si.current_undo_consumption, si.max_undo_consumption, "
                + " si.sql_canceled, si.queue_time_outs, si.estimated_execution_limit_hit ");
            break;
        case 3:
        case 4:
            selectStr.insert(0, 
            "select si.sid, s.serial#, s.username, s.osuser, s.machine, s.module, "
                + " co1.name, co2.name, "
                + " si.mapping_attribute, mapped_consumer_group, "
                + " si.state, si.active, si.current_idle_time, "
                + " si.current_cpu_wait_time, "
                + " si.cpu_wait_time, si.current_cpu_waits, "
                + " si.cpu_waits, si.current_consumed_cpu_time, "
                + " si.consumed_cpu_time, si.current_active_time, si.active_time, "
                + " si.current_queued_time, si.queued_time, si.current_yields, "
                + " si.yields, si.current_undo_consumption, si.max_undo_consumption, "
                + " si.sql_canceled, si.queue_time_outs, si.estimated_execution_limit_hit, "
                + " si.current_io_service_time, si.io_service_time, "
                + " si.current_io_service_waits, si.io_service_waits, "
                + " si.current_small_read_megabytes, si.small_read_megabytes, "
                + " si.current_large_read_megabytes, si.large_read_megabytes, "
                + " si.current_small_write_megabytes, si.small_write_megabytes, "
                + " si.current_large_write_megabytes, si.large_write_megabytes, "
                + " si.current_small_read_requests, si.small_read_requests, "
                + " si.current_small_write_requests, si.small_write_requests, "
                + " si.current_large_read_requests, si.large_read_requests, "
                + " si.current_large_write_requests, si.large_write_requests ");
            break;
        case 5:
            selectStr.insert(0, 
            "select si.sid, s.serial#, s.username, s.osuser, s.machine, s.module, "
                + " co1.name, co2.name, "
                + " si.mapping_attribute, mapped_consumer_group, "
                + " si.state, si.active, si.current_idle_time, "
                + " si.current_cpu_wait_time, "
                + " si.cpu_wait_time, si.current_cpu_waits, "
                + " si.cpu_waits, si.current_consumed_cpu_time, "
                + " si.consumed_cpu_time, si.current_active_time, si.active_time, "
                + " si.current_queued_time, si.queued_time, si.current_yields, "
                + " si.yields, si.current_undo_consumption, si.max_undo_consumption, "
                + " si.sql_canceled, si.queue_time_outs, si.estimated_execution_limit_hit, "
                + " si.current_io_service_time, si.io_service_time, "
                + " si.current_io_service_waits, si.io_service_waits, "
                + " si.current_small_read_megabytes, si.small_read_megabytes, "
                + " si.current_large_read_megabytes, si.large_read_megabytes, "
                + " si.current_small_write_megabytes, si.small_write_megabytes, "
                + " si.current_large_write_megabytes, si.large_write_megabytes, "
                + " si.current_small_read_requests, si.small_read_requests, "
                + " si.current_small_write_requests, si.small_write_requests, "
                + " si.current_large_read_requests, si.large_read_requests, "
                + " si.current_large_write_requests, si.large_write_requests, "
                + " si.current_pq_active_time, si.pq_active_time, "
                + " si.dop, si.pq_servers, si.estimated_execution_time, "
                + " si.current_pq_queued_time, si.pq_queued_time, "
                + " si.pq_queued, si.pq_queue_time_outs, "
                + " si.pq_active, si.pq_status, "
                + " si.current_logical_ios, si.logical_ios, "
                + " si.current_elapsed_time, si.elapsed_time, "
                + " si.last_action, si.last_action_reason, "
                + " si.last_action_time, si.con_id ");
            break;
    }
    return selectStr.toString();
  }

  public void GetConsumerGroupStatsData() {
    try {
      // Get the current consumer groups data.
      getDataStmt = conn.prepareStatement( GetConsumerGroupStatsString()
            + " from V$RSRC_CONSUMER_GROUP "
            + " order by id ");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {
        int    m_Id = rset.getInt(1);
        String m_Name = rset.getString(2);
        int    m_ActiveSessions = rset.getInt(3);
        int    m_ExecutionWaiters = rset.getInt(4);
        int    m_Requests = rset.getInt(5);
        int    m_CpuWaitTime = rset.getInt(6);
        int    m_CpuWaits = rset.getInt(7);
        int    m_ConsumedCpuTime = rset.getInt(8);
        int    m_Yields = rset.getInt(9);
        int    m_QueueLength = rset.getInt(10);
        int    m_CurrentUndoConsumption = rset.getInt(11);
        int    m_ActiveSessionLimitHit = rset.getInt(12);
        int    m_UndoLimitHit = rset.getInt(13);
        int    m_SwitchesInCpuTime = rset.getInt(14);
        int    m_SwitchesOutCpuTime = rset.getInt(15);
        int    m_SqlCanceled = rset.getInt(16);
        int    m_ActiveSessionsKilled = rset.getInt(17);
        int    m_IdleSessionsKilled = rset.getInt(18);
        int    m_IdleBlkrSessionsKilled = rset.getInt(19);
        int    m_QueueTime = rset.getInt(20);
        int    m_QueueTimeOut = rset.getInt(21);

        int    m_SwitchesInIoMegabytes = 0;
        int    m_SwitchesOutIoMegabytes = 0;
        int    m_SwitchesInIoRequests = 0;
        int    m_SwitchesOutIoRequests = 0;
        int    m_IoServiceTime = 0;
        int    m_IoServiceWaits = 0;
        int    m_SmallReadMegabytes = 0;
        int    m_SmallWriteMegabytes = 0;
        int    m_LargeReadMegabytes = 0;
        int    m_LargeWriteMegabytes = 0;
        int    m_SmallReadRequests = 0;
        int    m_SmallWriteRequests = 0;
        int    m_LargeReadRequests = 0;
        int    m_LargeWriteRequests = 0;
        int    m_CpuDecisions = 0;
        int    m_CpuDecisionsExcl = 0;
        int    m_CpuDecisionsWon = 0;

        int    m_SwitchesInIoLogical = 0;
        int    m_SwitchesOutIoLogical = 0;
        int    m_SwitchesInElapsedTime = 0;
        int    m_SwitchesOutElapsedTime = 0;
        int    m_CurrentPqsActive = 0;
        int    m_CurrentPqServersActive = 0;
        int    m_PqsQueued = 0;
        int    m_PqsCompleted = 0;
        int    m_PqServersUsed = 0;
        int    m_PqActiveTime = 0;
        int    m_CurrentPqsQueued = 0;
        int    m_PqQueuedTime = 0;
        int    m_PqQueueTimeOuts = 0;
        int    m_ConId = 0;

        if (versionNo > 2) {
            m_SwitchesInIoMegabytes = rset.getInt(22);
            m_SwitchesOutIoMegabytes = rset.getInt(23);
            m_SwitchesInIoRequests = rset.getInt(24);
            m_SwitchesOutIoRequests = rset.getInt(25);
            m_IoServiceTime = rset.getInt(26);
            m_IoServiceWaits = rset.getInt(27);
            m_SmallReadMegabytes = rset.getInt(28);
            m_SmallWriteMegabytes = rset.getInt(29);
            m_LargeReadMegabytes = rset.getInt(30);
            m_LargeWriteMegabytes = rset.getInt(31);
            m_SmallReadRequests = rset.getInt(32);
            m_SmallWriteRequests = rset.getInt(33);
            m_LargeReadRequests = rset.getInt(34);
            m_LargeWriteRequests = rset.getInt(35);

            if (versionNo > 3) {
                m_CpuDecisions = rset.getInt(36);
                m_CpuDecisionsExcl = rset.getInt(37);
                m_CpuDecisionsWon = rset.getInt(38);

                if (versionNo > 4) {
                    m_SwitchesInIoLogical = rset.getInt(39);
                    m_SwitchesOutIoLogical = rset.getInt(40);
                    m_SwitchesInElapsedTime = rset.getInt(41);
                    m_SwitchesOutElapsedTime = rset.getInt(42);
                    m_CurrentPqsQueued = rset.getInt(43);
                    m_CurrentPqServersActive = rset.getInt(44);
                    m_PqsQueued = rset.getInt(45);
                    m_PqsCompleted = rset.getInt(46);
                    m_PqServersUsed = rset.getInt(47);
                    m_PqActiveTime = rset.getInt(48);
                    m_CurrentPqsQueued = rset.getInt(49);
                    m_PqQueuedTime = rset.getInt(50);
                    m_PqQueueTimeOuts = rset.getInt(51);
                    m_ConId = rset.getInt(52);
                }
            }
        }

        m_ConsumerGroupStatsItem = new ConsumerGroupStatsItem(
                    m_Id,
                    m_Name,
                    m_ActiveSessions,
                    m_ExecutionWaiters,
                    m_Requests,
                    m_CpuWaitTime,
                    m_CpuWaits,
                    m_ConsumedCpuTime,
                    m_Yields,
                    m_QueueLength,
                    m_CurrentUndoConsumption,
                    m_ActiveSessionLimitHit,
                    m_UndoLimitHit,
                    m_SwitchesInCpuTime,
                    m_SwitchesOutCpuTime,
                    m_SwitchesInIoMegabytes,
                    m_SwitchesOutIoMegabytes,
                    m_SwitchesInIoRequests,
                    m_SwitchesOutIoRequests,
                    m_SqlCanceled,
                    m_ActiveSessionsKilled,
                    m_IdleSessionsKilled,
                    m_IdleBlkrSessionsKilled,
                    m_QueueTime,
                    m_QueueTimeOut,
                    m_IoServiceTime,
                    m_IoServiceWaits,
                    m_SmallReadMegabytes,
                    m_SmallWriteMegabytes,
                    m_LargeReadMegabytes,
                    m_LargeWriteMegabytes,
                    m_SmallReadRequests,
                    m_SmallWriteRequests,
                    m_LargeReadRequests,
                    m_LargeWriteRequests,
                    m_CpuDecisions,
                    m_CpuDecisionsExcl,
                    m_CpuDecisionsWon,
                    m_SwitchesInIoLogical,
                    m_SwitchesOutIoLogical,
                    m_SwitchesInElapsedTime,
                    m_SwitchesOutElapsedTime,
                    m_CurrentPqsQueued,
                    m_CurrentPqServersActive,
                    m_PqsQueued,
                    m_PqsCompleted,
                    m_PqServersUsed,
                    m_PqActiveTime,
                    m_CurrentPqsQueued,
                    m_PqQueuedTime,
                    m_PqQueueTimeOuts,
                    m_ConId);

        addConsumerGroupStats(m_ConsumerGroupStatsItem);

      }
      rset.close();
      getDataStmt.close();

    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetConsumerGroupStatsData"," : Error..." + e.getMessage());
    }
  }

  private String GetConsumerGroupStatsString() {
    StringBuffer selectStr = new StringBuffer("a");
    selectStr.delete(0, selectStr.length());
    switch (versionNo) {
        case 1:
        case 2:
            selectStr.insert(0, 
            "select id, name, active_sessions, "
            + " execution_waiters, requests, cpu_wait_time, "
            + " cpu_waits, consumed_cpu_time, yields, "
            + " queue_length, current_undo_consumption, "
            + " active_session_limit_hit, undo_limit_hit, "
            + " session_switches_in, session_switches_out, "
            + " sql_canceled, active_sessions_killed, idle_sessions_killed, "
            + " idle_blkr_sessions_killed, queued_time, queue_time_outs ");

            break;
        case 3:
            selectStr.insert(0, 
            "select id, name, active_sessions, "
            + " execution_waiters, requests, cpu_wait_time, "
            + " cpu_waits, consumed_cpu_time, yields, "
            + " queue_length, current_undo_consumption, "
            + " active_session_limit_hit, undo_limit_hit, "
            + " switches_in_cpu_time, switches_out_cpu_time, "
            + " sql_canceled, active_sessions_killed, idle_sessions_killed, "
            + " idle_blkr_sessions_killed, queued_time, queue_time_outs, "
            + " switches_in_io_megabytes, switches_out_io_megabytes, "
            + " switches_in_io_requests, switches_out_io_requests, "
            + " io_service_time, io_service_waits, small_read_megabytes, "
            + " small_write_megabytes, large_read_megabytes, "
            + " large_write_megabytes, small_read_requests, "
            + " small_write_requests, large_read_requests, "
            + " large_write_requests ");
            break;
        case 4:
            selectStr.insert(0, 
            "select id, name, active_sessions, "
            + " execution_waiters, requests, cpu_wait_time, "
            + " cpu_waits, consumed_cpu_time, yields, "
            + " queue_length, current_undo_consumption, "
            + " active_session_limit_hit, undo_limit_hit, "
            + " switches_in_cpu_time, switches_out_cpu_time, "
            + " sql_canceled, active_sessions_killed, idle_sessions_killed, "
            + " idle_blkr_sessions_killed, queued_time, queue_time_outs, "
            + " switches_in_io_megabytes, switches_out_io_megabytes, "
            + " switches_in_io_requests, switches_out_io_requests, "
            + " io_service_time, io_service_waits, small_read_megabytes, "
            + " small_write_megabytes, large_read_megabytes, "
            + " large_write_megabytes, small_read_requests, "
            + " small_write_requests, large_read_requests, "
            + " large_write_requests, cpu_decisions, "
            + " cpu_decisions_exclusive, cpu_decisions_won ");
            break;
        case 5:
            selectStr.insert(0, 
            "select id, name, active_sessions, "
            + " execution_waiters, requests, cpu_wait_time, "
            + " cpu_waits, consumed_cpu_time, yields, "
            + " queue_length, current_undo_consumption, "
            + " active_session_limit_hit, undo_limit_hit, "
            + " switches_in_cpu_time, switches_out_cpu_time, "
            + " sql_canceled, active_sessions_killed, idle_sessions_killed, "
            + " idle_blkr_sessions_killed, queued_time, queue_time_outs, "
            + " switches_in_io_megabytes, switches_out_io_megabytes, "
            + " switches_in_io_requests, switches_out_io_requests, "
            + " io_service_time, io_service_waits, small_read_megabytes, "
            + " small_write_megabytes, large_read_megabytes, "
            + " large_write_megabytes, small_read_requests, "
            + " small_write_requests, large_read_requests, "
            + " large_write_requests, cpu_decisions, "
            + " cpu_decisions_exclusive, cpu_decisions_won, "
            + " switches_in_io_logical, switches_out_io_logical, "
            + " switches_in_elapsed_time, switches_out_elapsed_time, "
            + " current_pqs_active, current_pq_servers_active, "
            + " pqs_queued, pqs_completed, pq_servers_used, pq_active_time, "
            + " current_pqs_queued, pq_queued_time, "
            + " pq_queue_time_outs, con_Id ");
            break;
    }
    return selectStr.toString();
  }

  public void GetCurrentPlansData(SchedDataArea m_DataArea) {
    try {
      // Get the current plans data.
      getDataStmt = conn.prepareStatement( GetCurrentPlansString()
            + " from V$RSRC_PLAN "
            + " order by name ");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        int    m_Id = rset.getInt(1);
        String m_Name = rset.getString(2);
        String m_IsTopPlan = rset.getString(3);
        String m_CpuManaged = "";

        if (versionNo > 2) {
            m_CpuManaged = rset.getString(4);
        }

        m_CurrentPlanItem = new CurrentPlanItem(
                    m_Id,
                    m_Name,
                    m_IsTopPlan,
                    m_CpuManaged);

        addCurrentPlan(m_CurrentPlanItem);

      }
      rset.close();
      getDataStmt.close();
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetCurrentPlansData"," : Error..." + e.getMessage());
    }
  }

  private String GetCurrentPlansString() {
    StringBuffer selectStr = new StringBuffer("a");
    selectStr.delete(0, selectStr.length());
    switch (versionNo) {
        case 1:
        case 2:
            selectStr.insert(0, "select id, name, is_top_plan ");
            break;
        case 3:
        case 4:
        case 5:
            selectStr.insert(0, "select id, name, is_top_plan, cpu_managed ");
            break;
    }
    return selectStr.toString();
  }

  public void GetConsumerPrivsData() {
    try {
      // get the Consumer Group privileges.
      getDataStmt = conn.prepareStatement(
            "select grantee, granted_group, "
            + " grant_option, initial_group "
            + " from dba_rsrc_consumer_group_privs "
            + " order by granted_group");

      ResultSet rset = getDataStmt.executeQuery();

      while (rset.next()) {

        String m_Grantee = rset.getString(1);
        String m_GrantedGroup = rset.getString(2);
        String m_GrantOption = rset.getString(3);
        String m_InitialGroup = rset.getString(4);

        m_ConsumerPrivItem = new ConsumerPrivItem(
                    m_Grantee,
                    m_GrantedGroup,
                    m_GrantOption,
                    m_InitialGroup);

        addConsumerPriv(m_ConsumerPrivItem);

      }
      rset.close();
      getDataStmt.close();

    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetConsumerPrivsData"," : Error..." + e.getMessage());
    }
  }

  public SchedDataArea.ConsumerPrivItem GetConsumerPrivItem(
                                         SchedDataArea mDataArea,
                                         String        mGrantedGroup,
                                         String        mGrantee) {
    try {
      // get the Consumer Group privileges.
      getItemStmt = conn.prepareStatement(
            "select grantee, granted_group, "
            + " grant_option, initial_group "
            + " from dba_rsrc_consumer_group_privs "
            + " where grantee = ? and granted_group = ? ");

      getItemStmt.setString(1, mGrantee.toUpperCase());
      getItemStmt.setString(2, mGrantedGroup.toUpperCase());

      ResultSet rset = getItemStmt.executeQuery();

      while (rset.next()) {

        String m_Grantee = rset.getString(1);
        String m_GrantedGroup = rset.getString(2);
        String m_GrantOption = rset.getString(3);
        String m_InitialGroup = rset.getString(4);

        m_ConsumerPrivItem = new ConsumerPrivItem(
                    m_Grantee,
                    m_GrantedGroup,
                    m_GrantOption,
                    m_InitialGroup);

        addConsumerPriv(m_ConsumerPrivItem);

      }
      rset.close();
      getItemStmt.close();

      return m_ConsumerPrivItem;

    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("GetConsumerPrivItem"," : Error..." + e.getMessage());
      return null;
    }
  }

  public int ClearPendingArea() {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.CLEAR_PENDING_AREA()";

    try {

      // Create the statement
      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.CLEAR_PENDING_AREA()");

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("ClearPendingArea"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int CreatePendingArea() {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.CREATE_PENDING_AREA()";

    try {

      // Create the statement
      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.CREATE_PENDING_AREA()");

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("CreatePendingArea"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int ValidatePendingArea() {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.VALIDATE_PENDING_AREA()";

    try {

      // Create the statement
      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.VALIDATE_PENDING_AREA()");

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("ValidatePendingArea"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int SubmitPendingArea() {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SUBMIT_PENDING_AREA()";

    try {

      // Create the statement
      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.SUBMIT_PENDING_AREA()");

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SubmitPendingArea"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int CreateResourcePlan(String mPlanName,
                                       String mgmtMth,
                                       String mComment) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.CREATE_PLAN(" + 
                            " PLAN => " + mPlanName + 
                            ", MGMT_MTH => " + mgmtMth + 
                            ", COMMENT => " + mComment + ")";

    try {
      // System.out.println( " Event - Resource Plan Created " + mPlanName + 
      //                      "--" + mgmtMth + "--" +
      //                      "--" + mComment);

      // Create a statement

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.CREATE_PLAN( " +
          " PLAN => ?, " +
          " MGMT_MTH => ?, " +
          " COMMENT => ? ); end;"
      );

      cs.setString(1, mPlanName);
      cs.setString(2, mgmtMth);
      cs.setString(3, mComment);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("CreateResourcePlan"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }


  public int CreateCdbResourcePlan(String mPlanName,
                                   String mComment) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.CREATE_CDB_PLAN(" + 
                            " PLAN => " + mPlanName + 
                            ", COMMENT => " + mComment + ")";

    try {
      // System.out.println( " Event - Resource Plan Created " + mPlanName + 
      //                      "--" + mComment);

      // Create a statement

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.CREATE_CDB_PLAN( " +
          " PLAN => ?, " +
          " COMMENT => ? ); end;"
      );

      cs.setString(1, mPlanName);
      cs.setString(2, mComment);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("CreateCdbResourcePlan"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }


  public int CreateConsumerGroup(String mConsumerGroup,
                                        String mComment) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.CREATE_CONSUMER_GROUP(" + 
                            " CONSUMER_GROUP => " + mConsumerGroup + 
                            ", COMMENT => " + mComment + ")";

    try {
      // System.out.println( " Event - Consumer Group Created " + mConsumerGroup + 
      //                      "--" + mComment);

      // Create a statement

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.CREATE_CONSUMER_GROUP( " +
          " CONSUMER_GROUP => ?, " +
          " COMMENT => ? ); end;"
      );

      cs.setString(1, mConsumerGroup);
      cs.setString(2, mComment);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("CreateConsumerGroup"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int CreateResourceDirective10g(String mPlan,
                                       String mGroup,
                                       int    mCpuP1,
                                       int    mCpuP2,
                                       int    mCpuP3,
                                       int    mCpuP4,
                                       int    mCpuP5,
                                       int    mCpuP6,
                                       int    mCpuP7,
                                       int    mCpuP8,
                                       String mSwitchGroup,
                                       int    mSwitchTime,
                                       String mSwitchEstimate,
                                       int    mActiveSessPoolP1,
                                       int    mParallelDegreeLimitP1,
                                       int    mMaxEstExecTime,
                                       int    mMaxIdleTime,
                                       int    mMaxIdleBlockerTime,
                                       int    mUndoPool,
                                       int    mQueueingP1,
                                       String mComment
                                       ) {

      String mCallStatement = "DBMS_RESOURCE_MANAGER.CREATE_PLAN_DIRECTIVE(" + 
                            " PLAN => " + mPlan + 
                            ",GROUP_OR_SUBPLAN => " + mGroup + 
                            ",COMMENT => " + mComment +
                            ",ACTIVE_SESS_POOL_P1 => " + mActiveSessPoolP1 +
                            ",QUEUEING_P1 => " + mQueueingP1 +
                            ",PARALLEL_DEGREE_LIMIT_P1 => " + mParallelDegreeLimitP1 +
                            ",SWITCH_GROUP => " + mSwitchGroup +
                            ",SWITCH_TIME => " + mSwitchTime +
                            ",SWITCH_ESTIMATE => " + mSwitchEstimate +
                            ",MAX_EST_EXEC_TIME => " + mMaxEstExecTime +
                            ",UNDO_POOL => " + mUndoPool +
                            ",MAX_IDLE_TIME => " + mMaxIdleTime +
                            ",MAX_IDLE_BLOCKER_TIME => " + mMaxIdleBlockerTime +
                            ",CPU_P1 => " + mCpuP1 +
                            ",CPU_P2 => " + mCpuP2 +
                            ",CPU_P3 => " + mCpuP3 +
                            ",CPU_P4 => " + mCpuP4 +
                            ",CPU_P5 => " + mCpuP5 +
                            ",CPU_P6 => " + mCpuP6 +
                            ",CPU_P7 => " + mCpuP7 +
                            ",CPU_P8 => " + mCpuP8 + ")";

    try {
      // Create a statement

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.CREATE_PLAN_DIRECTIVE( " +
          " PLAN => ?, " +
          " GROUP_OR_SUBPLAN => ?, " +
          " COMMENT => ?, " +
          " ACTIVE_SESS_POOL_P1 => ?, " +
          " QUEUEING_P1 => ?, " +
          " PARALLEL_DEGREE_LIMIT_P1 => ?, " +
          " SWITCH_GROUP => ?, " +
          " SWITCH_TIME => ?, " +
          " SWITCH_ESTIMATE => " + mSwitchEstimate + ", " +
          " MAX_EST_EXEC_TIME => ?, " +
          " UNDO_POOL => ?, " +
          " MAX_IDLE_TIME => ?, " +
          " MAX_IDLE_BLOCKER_TIME => ?, " +
          " CPU_P1 => ?, " +
          " CPU_P2 => ?, " +
          " CPU_P3 => ?, " +
          " CPU_P4 => ?, " +
          " CPU_P5 => ?, " +
          " CPU_P6 => ?, " +
          " CPU_P7 => ?, " +
          " CPU_P8 => ? ); end;"
      );

      cs.setString(1, mPlan);
      cs.setString(2, mGroup);
      cs.setString(3, mComment);
      if (mActiveSessPoolP1 == 0)            cs.setString(4, null);
      else                                   cs.setInt(4, mActiveSessPoolP1);
      if (mQueueingP1 == 0)                  cs.setString(5, null);
      else                                   cs.setInt(5, mQueueingP1);
      if (mParallelDegreeLimitP1 == 0)       cs.setString(6, null);
      else                                   cs.setInt(6, mParallelDegreeLimitP1);
      if (mSwitchGroup.trim().length() == 0) cs.setString(7, null);
      else                                   cs.setString(7, mSwitchGroup);
      if (mSwitchTime == 0)                  cs.setString(8, null);
      else                                   cs.setInt(8, mSwitchTime);
      if (mMaxEstExecTime == 0)              cs.setString(9, null);
      else                                   cs.setInt(9, mMaxEstExecTime);
      if (mUndoPool == 0)                    cs.setString(10, null);
      else                                   cs.setInt(10, mUndoPool);
      if (mMaxIdleTime == 0)                 cs.setString(11, null);
      else                                   cs.setInt(11, mMaxIdleTime);
      if (mMaxIdleBlockerTime == 0)          cs.setString(12, null);
      else                                   cs.setInt(12, mMaxIdleBlockerTime);
      cs.setInt(13, mCpuP1);
      cs.setInt(14, mCpuP2);
      cs.setInt(15, mCpuP3);
      cs.setInt(16, mCpuP4);
      cs.setInt(17, mCpuP5);
      cs.setInt(18, mCpuP6);
      cs.setInt(19, mCpuP7);
      cs.setInt(20, mCpuP8);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("CreateResourceDirective"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int CreateResourceDirective11g(String mPlan,
                                       String mGroup,
                                       int    mMgmtP1,
                                       int    mMgmtP2,
                                       int    mMgmtP3,
                                       int    mMgmtP4,
                                       int    mMgmtP5,
                                       int    mMgmtP6,
                                       int    mMgmtP7,
                                       int    mMgmtP8,
                                       String mSwitchGroup,
                                       int    mSwitchTime,
                                       String mSwitchEstimate,
                                       int    mActiveSessPoolP1,
                                       int    mParallelDegreeLimitP1,
                                       int    mMaxEstExecTime,
                                       int    mMaxIdleTime,
                                       int    mMaxIdleBlockerTime,
                                       int    mUndoPool,
                                       int    mQueueingP1,
                                       int    mSwitchIoReqs,
                                       int    mSwitchIoMegabytes,
                                       String mSwitchForCall,
                                       int    mMaxUtilLimit,
                                       String mComment
                                       ) {

      // System.out.println( "Plan Directive -" + mPlan + "-" + mGroup + "-" + mMgmtP1 + "-" + 
      //                     mMgmtP2 + "-" + mMgmtP3 + "-" + mMgmtP4 + "-" + mMgmtP5 + "-" +
      //                     mMgmtP6 + "-" + mMgmtP7 + "-" + mMgmtP8 + "-" + mSwitchGroup + "-" +
      //                     mSwitchTime + "-" + mSwitchIoReqs + "-" + mSwitchIoMegabytes + "-" +
      //                     mSwitchForCall + "-" + mSwitchEstimate + "--" + mActiveSessPoolP1 + "-" +
      //                     mParallelDegreeLimitP1 + "--" + mMaxEstExecTime + "-" + mMaxIdleTime + "--" +
      //                     mMaxIdleBlockerTime + "-" + mUndoPool + "--" + mQueueingP1 + "-" +
      //                     mMaxUtilLimit + "--" + mComment);

      String mCallStatement = "DBMS_RESOURCE_MANAGER.CREATE_PLAN_DIRECTIVE(" + 
                            " PLAN => " + mPlan + 
                            ",GROUP_OR_SUBPLAN => " + mGroup + 
                            ",COMMENT => " + mComment +
                            ",ACTIVE_SESS_POOL_P1 => " + mActiveSessPoolP1 +
                            ",QUEUEING_P1 => " + mQueueingP1 +
                            ",PARALLEL_DEGREE_LIMIT_P1 => " + mParallelDegreeLimitP1 +
                            ",SWITCH_GROUP => " + mSwitchGroup +
                            ",SWITCH_TIME => " + mSwitchTime +
                            ",SWITCH_ESTIMATE => " + mSwitchEstimate +
                            ",MAX_EST_EXEC_TIME => " + mMaxEstExecTime +
                            ",UNDO_POOL => " + mUndoPool +
                            ",MAX_IDLE_TIME => " + mMaxIdleTime +
                            ",MAX_IDLE_BLOCKER_TIME => " + mMaxIdleBlockerTime +
                            ",MGMT_P1 => " + mMgmtP1 +
                            ",MGMT_P2 => " + mMgmtP2 +
                            ",MGMT_P3 => " + mMgmtP3 +
                            ",MGMT_P4 => " + mMgmtP4 +
                            ",MGMT_P5 => " + mMgmtP5 +
                            ",MGMT_P6 => " + mMgmtP6 +
                            ",MGMT_P7 => " + mMgmtP7 +
                            ",MGMT_P8 => " + mMgmtP8 + 
                            ",SWITCH_IO_MEGABYTES => " + mSwitchIoMegabytes +
                            ",SWITCH_IO_REQS => " + mSwitchIoReqs + 
                            ",SWITCH_FOR_CALL => " + mSwitchForCall +
                            ",MAX_UTILIZATION_LIMIT => " + mMaxUtilLimit + ")";

    try {
      // Create a statement

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.CREATE_PLAN_DIRECTIVE( " +
          " PLAN => ?, " +
          " GROUP_OR_SUBPLAN => ?, " +
          " COMMENT => ?, " +
          " ACTIVE_SESS_POOL_P1 => ?, " +
          " QUEUEING_P1 => ?, " +
          " PARALLEL_DEGREE_LIMIT_P1 => ?, " +
          " SWITCH_GROUP => ?, " +
          " SWITCH_TIME => ?, " +
          " SWITCH_ESTIMATE => " + mSwitchEstimate + ", " +
          " MAX_EST_EXEC_TIME => ?, " +
          " UNDO_POOL => ?, " +
          " MAX_IDLE_TIME => ?, " +
          " MAX_IDLE_BLOCKER_TIME => ?, " +
          " MGMT_P1 => ?, " +
          " MGMT_P2 => ?, " +
          " MGMT_P3 => ?, " +
          " MGMT_P4 => ?, " +
          " MGMT_P5 => ?, " +
          " MGMT_P6 => ?, " +
          " MGMT_P7 => ?, " +
          " MGMT_P8 => ?, " +
          " SWITCH_IO_MEGABYTES => ?, " +
          " SWITCH_IO_REQS => ?, " +
          " SWITCH_FOR_CALL => " + mSwitchForCall + ", " +
          " MAX_UTILIZATION_LIMIT => ? ); end;"
      );

      cs.setString(1, mPlan);
      cs.setString(2, mGroup);
      cs.setString(3, mComment);
      if (mActiveSessPoolP1 == 0)            cs.setString(4, null);
      else                                   cs.setInt(4, mActiveSessPoolP1);
      if (mQueueingP1 == 0)                  cs.setString(5, null);
      else                                   cs.setInt(5, mQueueingP1);
      if (mParallelDegreeLimitP1 == 0)       cs.setString(6, null);
      else                                   cs.setInt(6, mParallelDegreeLimitP1);
      if (mSwitchGroup.trim().length() == 0) cs.setString(7, null);
      else                                   cs.setString(7, mSwitchGroup);
      if (mSwitchTime == 0)                  cs.setString(8, null);
      else                                   cs.setInt(8, mSwitchTime);
      if (mMaxEstExecTime == 0)              cs.setString(9, null);
      else                                   cs.setInt(9, mMaxEstExecTime);
      if (mUndoPool == 0)                    cs.setString(10, null);
      else                                   cs.setInt(10, mUndoPool);
      if (mMaxIdleTime == 0)                 cs.setString(11, null);
      else                                   cs.setInt(11, mMaxIdleTime);
      if (mMaxIdleBlockerTime == 0)          cs.setString(12, null);
      else                                   cs.setInt(12, mMaxIdleBlockerTime);
      cs.setInt(13, mMgmtP1);
      cs.setInt(14, mMgmtP2);
      cs.setInt(15, mMgmtP3);
      cs.setInt(16, mMgmtP4);
      cs.setInt(17, mMgmtP5);
      cs.setInt(18, mMgmtP6);
      cs.setInt(19, mMgmtP7);
      cs.setInt(20, mMgmtP8);

      if (mSwitchIoMegabytes == 0) cs.setString(21, null);
      else                         cs.setInt(21, mSwitchIoMegabytes);
      if (mSwitchIoReqs == 0)      cs.setString(22, null);
      else                         cs.setInt(22, mSwitchIoReqs);
      if (mMaxUtilLimit == 0)      cs.setString(23, null);
      else                         cs.setInt(23, mMaxUtilLimit);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("CreateResourceDirective"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int CreateCdbResourceDirective(String mPlan,
                                       String mPluggableDb,
                                       int    mShares,
                                       int    mUtilizationLimit,
                                       int    mParallelServerLimit,
                                       String mComment
                                       ) {

      String mCallStatement = "DBMS_RESOURCE_MANAGER.CREATE_CDB_PLAN_DIRECTIVE(" + 
                            " PLAN => " + mPlan + 
                            ",PLUGGABLE_DATABASE => " + mPluggableDb + 
                            ",COMMENT => " + mComment +
                            ",SHARES => " + mShares +
                            ",UTILIZATION_LIMIT => " + mUtilizationLimit +
                            ",PARALLEL_SERVER_LIMIT => " + mParallelServerLimit + ")";

    try {
      // Create a statement

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.CREATE_CDB_PLAN_DIRECTIVE( " +
          " PLAN => ?, " +
          " PLUGGABLE_DATABASE => ?, " +
          " COMMENT => ?, " +
          " SHARES => ?, " +
          " UTILIZATION_LIMIT => ?, " +
          " PARALLEL_SERVER_LIMIT => ? ); end;"
      );

      cs.setString(1, mPlan);
      cs.setString(2, mPluggableDb);
      cs.setString(3, mComment);
      if (mShares == 0)                      cs.setString(4, null);
      else                                   cs.setInt(4, mShares);
      if (mUtilizationLimit == 0)            cs.setString(5, null);
      else                                   cs.setInt(5, mUtilizationLimit);
      if (mParallelServerLimit == 0)         cs.setString(6, null);
      else                                   cs.setInt(6, mParallelServerLimit);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("CreateCdbResourceDirective"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }



  public int DropResourcePlan(String mResourcePlanName) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.DELETE_PLAN(" + 
                             mResourcePlanName + ")";

    try {
      // System.out.println( " Event - Resource Plan Dropped " + mResourcePlanName);

      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.DELETE_PLAN(?)");

      cs.setString(1, mResourcePlanName);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("DropResourcePlan"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int DropCdbResourcePlan(String mResourcePlanName) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.DELETE_CDB_PLAN(" + 
                             mResourcePlanName + ")";

    try {
      // System.out.println( " Event - Resource Plan Dropped " + mResourcePlanName);

      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.DELETE_CDB_PLAN(?)");

      cs.setString(1, mResourcePlanName);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("DropCdbResourcePlan"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int DropConsumerGroup(String mConsumerGroupName) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.DELETE_CONSUMER_GROUP(" + 
                             mConsumerGroupName + ")";

    try {
      // System.out.println( " Event - Consumer Group Dropped " + mConsumerGroupName);

      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.DELETE_CONSUMER_GROUP(?)");

      cs.setString(1, mConsumerGroupName);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("DeleteConsumerGroup"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int DropResourceDirective(String mResourcePlanName,
                                          String mGroup) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.DELETE_PLAN_DIRECTIVE(" + 
                             mResourcePlanName + "," + mGroup + ")";

    try {
      // System.out.println( " Event - Resource Plan Dropped " + mResourcePlanName + "," + mGroup);

      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.DELETE_PLAN_DIRECTIVE(?, ?)");

      cs.setString(1, mResourcePlanName);
      cs.setString(2, mGroup);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("DropResourceDirective"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int DropCdbResourceDirective(String mResourcePlanName,
                                      String mPluggableDb) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.DELETE_CDB_PLAN_DIRECTIVE(" + 
                             mResourcePlanName + "," + mPluggableDb + ")";

    try {
      // System.out.println( " Event - Resource Plan Dropped " + mResourcePlanName + "," + mPluggableDb);

      CallableStatement cs = conn.prepareCall ("call DBMS_RESOURCE_MANAGER.DELETE_CDB_PLAN_DIRECTIVE(?, ?)");

      cs.setString(1, mResourcePlanName);
      cs.setString(2, mPluggableDb);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("DropCdbResourceDirective"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int UpdateResourcePlan(String mPlanName,
                                String mColumnName,
                                String mValue) {

      String mCallStatement = "";

      mReturnNo = 0;
      try {

          mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_PLAN("
                             + " PLAN => '" + mPlanName + "', "
                             + mColumnName + " => '" + mValue + "')";

          CallableStatement cs = conn.prepareCall (
                  "begin DBMS_RESOURCE_MANAGER.UPDATE_PLAN( " +
                      " PLAN => ?, " + mColumnName + " => ? ); end;"
          );

          cs.setString(1, mPlanName);
          cs.setString(2, mValue);

          cs.executeUpdate();

          SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

          return 0;
      }
      catch(SQLException e) {
          SchedFile.EnterErrorEntry("UpdateResourcePlan"," : Error..." + mCallStatement);

          setSysMessage(e.getMessage());

          return 1;
      }
  }

  public int UpdateCdbResourcePlan(String mPlanName,
                                   String mColumnName,
                                   String mValue) {

      String mCallStatement = "";

      mReturnNo = 0;
      try {

          mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_CDB_PLAN("
                             + " PLAN => '" + mPlanName + "', "
                             + mColumnName + " => '" + mValue + "')";

          CallableStatement cs = conn.prepareCall (
                  "begin DBMS_RESOURCE_MANAGER.UPDATE_CDB_PLAN( " +
                      " PLAN => ?, " + mColumnName + " => ? ); end;"
          );

          cs.setString(1, mPlanName);
          cs.setString(2, mValue);

          cs.executeUpdate();

          SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

          return 0;
      }
      catch(SQLException e) {
          SchedFile.EnterErrorEntry("UpdateCdbResourcePlan"," : Error..." + mCallStatement);

          setSysMessage(e.getMessage());

          return 1;
      }
  }

  public int UpdateResourceDirectiveString(String mPlanName,
                                                  String mGroup,
                                                  String mColumn,
                                                  String mValue) {

      String mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_PLAN_DIRECTIVE(" + 
                            " PLAN => " + mPlanName + 
                            ", GROUP_OR_SUBPLAN => " + mGroup + 
                            ", " + mColumn + " => " + mValue + ")";

      try {

          CallableStatement cs = conn.prepareCall (
                "begin DBMS_RESOURCE_MANAGER.UPDATE_PLAN_DIRECTIVE( " +
                    " PLAN => ?, " +
                    " GROUP_OR_SUBPLAN => ?, " +
                    mColumn + " => ? ); end;"

          );
          cs.setString(1, mPlanName);
          cs.setString(2, mGroup);
          cs.setString(3, mValue);
          // cs.setInt(3, Integer.parseInt(mValue));

          cs.executeUpdate();

          SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

          return 0;

      }
      catch(SQLException e) {
          SchedFile.EnterErrorEntry("UpdateResourceDirectiveString"," : Error..." + mCallStatement);

          setSysMessage(e.getMessage());

          return 1;
      }

  }

  public int UpdateResourceDirectiveBoolean(String mPlanName,
                                                   String mGroup,
                                                   String mColumn,
                                                   String mValue) {

      String mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_PLAN_DIRECTIVE(" + 
                            " PLAN => " + mPlanName + 
                            ", GROUP_OR_SUBPLAN => " + mGroup + 
                            ", " + mColumn + " => " + mValue + ")";

      try {

          String Str1 = "{ call DBMS_RESOURCE_MANAGER.UPDATE_PLAN_DIRECTIVE(" +  
                           "PLAN => " + "'" + mPlanName + "'" +
                           ",GROUP_OR_SUBPLAN => " + "'" + mGroup + "'" +
                           "," + mColumn + " => " + mValue + ") }";

          CallableStatement cs = conn.prepareCall (Str1);

          cs.executeUpdate();

          SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

          return 0;

      }
      catch(SQLException e) {
          SchedFile.EnterErrorEntry("UpdateResourceDirectiveBoolean"," : Error..." + mCallStatement);

          setSysMessage(e.getMessage());

          return 1;
      }

  }

  public int UpdateCdbResourceDirective(String mPlanName,
                                        String mPluggableDb,
                                        String mColumn,
                                        String mValue) {
      int m_ret = 0;
      if (mPluggableDb.equals("ORA$DEFAULT_PDB_DIRECTIVE")) {
          m_ret = UpdateCdbDefaultDirective(mPlanName, mColumn, mValue);
      }
      else {
          if (mPluggableDb.equals("ORA$AUTOTASK")) {
              m_ret = UpdateCdbAutotaskDirective(mPlanName, mColumn, mValue);
          }
          else {
              m_ret = UpdateCdbPlanDirective(mPlanName, mPluggableDb, mColumn, mValue);
          }
      }
      return m_ret;
  }

  public int UpdateCdbPlanDirective(String mPlanName,
                                    String mPluggableDb,
                                    String mColumn,
                                    String mValue) {
      String mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_CDB_PLAN_DIRECTIVE(" + 
                            " PLAN => " + mPlanName + 
                            ", PLUGGABLE_DATABASE => " + mPluggableDb + 
                            ", " + mColumn + " => " + mValue + ")";

      try {

          CallableStatement cs = conn.prepareCall (
                "begin DBMS_RESOURCE_MANAGER.UPDATE_CDB_PLAN_DIRECTIVE( " +
                    " PLAN => ?, " +
                    " PLUGGABLE_DATABASE => ?, " +
                    mColumn + " => ? ); end;"

          );
          cs.setString(1, mPlanName);
          cs.setString(2, mPluggableDb);
          cs.setString(3, mValue);

          cs.executeUpdate();

          SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

          return 0;
      }
      catch(SQLException e) {
          SchedFile.EnterErrorEntry("UpdateCdbPlanDirective"," : Error..." + mCallStatement);
          setSysMessage(e.getMessage());
          return 1;
      }
  }

  public int UpdateCdbAutotaskDirective(String mPlanName,
                                    String mColumn,
                                    String mValue) {
      String mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_CDB_AUTOTASK_DIRECTIVE(" + 
                            " PLAN => " + mPlanName + 
                            ", " + mColumn + " => " + mValue + ")";

      try {

          CallableStatement cs = conn.prepareCall (
                "begin DBMS_RESOURCE_MANAGER.UPDATE_CDB_AUTOTASK_DIRECTIVE( " +
                    " PLAN => ?, " +
                    mColumn + " => ? ); end;"

          );
          cs.setString(1, mPlanName);
          cs.setString(2, mValue);

          cs.executeUpdate();

          SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

          return 0;
      }
      catch(SQLException e) {
          SchedFile.EnterErrorEntry("UpdateCdbAutotaskDirective"," : Error..." + mCallStatement);
          setSysMessage(e.getMessage());
          return 1;
      }
  }

  public int UpdateCdbDefaultDirective(String mPlanName,
                                    String mColumn,
                                    String mValue) {
      String mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_CDB_DEFAULT_DIRECTIVE(" + 
                            " PLAN => " + mPlanName + 
                            ", " + mColumn + " => " + mValue + ")";

      try {

          CallableStatement cs = conn.prepareCall (
                "begin DBMS_RESOURCE_MANAGER.UPDATE_CDB_DEFAULT_DIRECTIVE( " +
                    " PLAN => ?, " +
                    mColumn + " => ? ); end;"

          );
          cs.setString(1, mPlanName);
          cs.setString(2, mValue);

          cs.executeUpdate();

          SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

          return 0;
      }
      catch(SQLException e) {
          SchedFile.EnterErrorEntry("UpdateCdbDefaultDirective"," : Error..." + mCallStatement);
          setSysMessage(e.getMessage());
          return 1;
      }
  }

  public int UpdateConsumerGroup(String mConsumerGroupName,
                                        String mComment) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.UPDATE_CONSUMER_GROUP(" + 
                            " CONSUMER_GROUP => " + mConsumerGroupName + 
                            ", NEW_COMMENT => " + mComment + ")";

    try {
      // System.out.println( " Event - Consumer Group Updated " + mConsumerGroupName + "," + mComment);

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.UPDATE_CONSUMER_GROUP( " +
          " CONSUMER_GROUP => ?, " +
          " NEW_COMMENT => ? ); end;"
      );

      cs.setString(1, mConsumerGroupName);
      cs.setString(2, mComment);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("UpdateConsumerGroup"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int SetConsumerGroupMapping(String mConsumerGroupName,
                                            String mAttribute,
                                            String mValue) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SET_CONSUMER_GROUP_MAPPING(" + 
                            " CONSUMER_GROUP => " + mConsumerGroupName + 
                            ", ATTRIBUTE => " + mAttribute +
                            ", VALUE => " + mValue + ")";

    try {
      // System.out.println( " Event - Consumer Group Updated " + mConsumerGroupName + "," + mComment);

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.SET_CONSUMER_GROUP_MAPPING( " +
          " CONSUMER_GROUP => ?, " +
          " ATTRIBUTE => ?, " +
          " VALUE => ? ); end;"
      );
      cs.setString(1, mConsumerGroupName);
      cs.setString(2, mAttribute);
      cs.setString(3, mValue);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SetConsumerGroupMapping"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }
  public boolean DropConsumerGroupMapping(String mAttribute,
                                            String mValue) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SET_CONSUMER_GROUP_MAPPING(" + 
                            " ATTRIBUTE => " + mAttribute +
                            ", VALUE => " + mValue + ")";

    try {
      // System.out.println( " Event - Consumer Group Updated " + mConsumerGroupName + "," + mComment);

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.SET_CONSUMER_GROUP_MAPPING( " +
          " ATTRIBUTE => ?, " +
          " VALUE => ? ); end;"
      );

      cs.setString(1, mAttribute);
      cs.setString(2, mValue);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return true;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SetConsumerGroupMapping"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return false;
    }
  }

  public int SetConsumerPrivilege(String mConsumerGroupName,
                                             String mGrantee) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER_PRIVS.GRANT_SWITCH_CONSUMER_GROUP(" + 
                            " GRANTEE_NAME => " + mGrantee + 
                            ", CONSUMER_GROUP => " + mConsumerGroupName +
                            ", GRANT_OPTION => FALSE )";

    try {
      // System.out.println( " Event - Consumer Privilege Added " + mConsumerGroupName + "," + mComment);

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER_PRIVS.GRANT_SWITCH_CONSUMER_GROUP( " +
          " GRANTEE_NAME => ?, " +
          " CONSUMER_GROUP => ?, " +
          " GRANT_OPTION => FALSE ); end;"
      );
      cs.setString(1, mGrantee);
      cs.setString(2, mConsumerGroupName);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SetConsumerPrivilege"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public boolean DropConsumerPrivilege(String mConsumerGroupName,
                                              String mRevokee) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER_PRIVS.REVOKE_SWITCH_CONSUMER_GROUP(" + 
                            " REVOKEE_NAME => " + mRevokee + 
                            ", CONSUMER_GROUP => " + mConsumerGroupName + ")";

    try {
      // System.out.println( " Event - Consumer Privilege Added " + mConsumerGroupName + "," + mComment);

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER_PRIVS.REVOKE_SWITCH_CONSUMER_GROUP( " +
          " REVOKEE_NAME => ?, " +
          " CONSUMER_GROUP => ? ); end;"
      );
      cs.setString(1, mRevokee);
      cs.setString(2, mConsumerGroupName);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return true;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("DropConsumerPrivilege"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return false;
    }
  }



  public int SetConsumerGroupPriorities() {

    int[] Priorities = new int[10];

    for (int r2 = 0; r2 < mappingPrioritySize(); r2++) {
        m_MappingPrioritiesItem = getMappingPriority(r2);
        if (m_MappingPrioritiesItem.getStatus() != null) {
            for (int r3 = 0; r3 < 10; r3++) {
                if (m_MappingPrioritiesItem.getAttribute().equals(mapObjects[r3])) {
                    Priorities[r3] = getMappingPriority(mapObjects[r3]);
                    break;
                }
            }
        }
    }

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SET_CONSUMER_GROUP_MAPPING_PRI(" + 
                            " CLIENT_MACHINE => " + Priorities[0] + "," +
                            " CLIENT_OS_USER => " + Priorities[1] + "," +
                            " CLIENT_PROGRAM => " + Priorities[2] + "," +
                            " EXPLICIT => " + Priorities[3] + "," +
                            " MODULE_NAME => " + Priorities[4] + "," +
                            " MODULE_NAME_ACTION => " + Priorities[5] + "," +
                            " ORACLE_USER => " + Priorities[6] + "," +
                            " SERVICE_MODULE => " + Priorities[7] + "," +
                            " SERVICE_MODULE_ACTION => " + Priorities[8] + "," +
                            " SERVICE_NAME => " + Priorities[9] + ")";
    try {

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.SET_CONSUMER_GROUP_MAPPING_PRI( " +
          " CLIENT_MACHINE => ?, " +
          " CLIENT_OS_USER => ?, " +
          " CLIENT_PROGRAM => ?, " +
          " EXPLICIT => ?, " +
          " MODULE_NAME => ?, " +
          " MODULE_NAME_ACTION => ?, " +
          " ORACLE_USER => ?, " +
          " SERVICE_MODULE => ?, " +
          " SERVICE_MODULE_ACTION => ?, " +
          " SERVICE_NAME => ? ); end;"
      );

      cs.setInt(1, Priorities[0]);
      cs.setInt(2, Priorities[1]);
      cs.setInt(3, Priorities[2]);
      cs.setInt(4, Priorities[3]);
      cs.setInt(5, Priorities[4]);
      cs.setInt(6, Priorities[5]);
      cs.setInt(7, Priorities[6]);
      cs.setInt(8, Priorities[7]);
      cs.setInt(9, Priorities[8]);
      cs.setInt(10, Priorities[9]);


      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;

    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SetConsumerGroupEntries"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int SwitchConsumerGroupUsers(String mUser,
                                             String mConsumerGroup) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SWITCH_CONSUMER_GROUP_FOR_USER(" + 
                            "USER => " + mUser + 
                            ", CONSUMER_GROUP => " + mConsumerGroup + ")";

    try {

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.SWITCH_CONSUMER_GROUP_FOR_USER( " +
          " USER => ? , " +
          " CONSUMER_GROUP => ? ); end;"
      );
      cs.setString(1, mUser);
      cs.setString(2, mConsumerGroup);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SwitchConsumerGroupUsers"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int SwitchConsumerGroupSession(int    mSid,
                                               int    mSerial,
                                               String mConsumerGroup) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SWITCH_CONSUMER_GROUP_FOR_SESS(" + 
                            "SESSION_ID => " + mSid + 
                            ", SESSION_SERIAL => " + mSerial +
                            ", CONSUMER_GROUP => " + mConsumerGroup + ")";

    try {
      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.SWITCH_CONSUMER_GROUP_FOR_SESS( " +
          " SESSION_ID => ? , " +
          " SESSION_SERIAL => ? , " +
          " CONSUMER_GROUP => ? ); end;"
      );

      cs.setInt(1, mSid);
      cs.setInt(2, mSerial);
      cs.setString(3, mConsumerGroup);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;

    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SwitchConsumerGroupSession"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

  public int SwitchPlan(String mPlanName) {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SWITCH_PLAN(" + 
                            "PLAN_NAME => " + mPlanName + ")";

    try {

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.SWITCH_PLAN( " +
          " PLAN_NAME => ? ); end;"
      );
      cs.setString(1, mPlanName);

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SwitchPlan"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }
  public int SwitchPlanOff() {

    String mCallStatement = "DBMS_RESOURCE_MANAGER.SWITCH_PLAN()";

    try {

      CallableStatement cs = conn.prepareCall (
          "begin DBMS_RESOURCE_MANAGER.SWITCH_PLAN( " +
          " PLAN_NAME => ? ); end;"
      );
      cs.setString(1, "");

      cs.executeUpdate();

      SchedFile.WriteAudit(mHostName + ":" + mCallStatement);

      return 0;
    }
    catch(SQLException e) {
      SchedFile.EnterErrorEntry("SwitchPlanOff"," : Error..." + mCallStatement);

      setSysMessage(e.getMessage());

      return 1;
    }
  }

    private Vector<screenCombo>             comboObj;

    private Vector<JobItem>                 JobsVector;
    private Vector<JobArgsItem>             JobArgsVector;
    private Vector<ProgramItem>             ProgramsVector;
    private Vector<ProgramArgsItem>         ProgramArgsVector;
    private Vector<ScheduleItem>            SchedulesVector;
    private Vector<JobClassItem>            JobClassesVector;
    private Vector<WindowItem>              WindowsVector;
    private Vector<WindowGroupItem>         WindowsGroupVector;
    private Vector<WinGroupMembersItem>     WinGroupMembersVector;
    private Vector<ChainsItem>              ChainsVector;
    private Vector<ChainStepsItem>          ChainStepsVector;
    private Vector<ChainRulesItem>          ChainRulesVector;
    private Vector<CredentialsItem>         CredentialsVector;
    private Vector<GroupItem>               GroupsVector;
    private Vector<GroupMembersItem>        GroupMembersVector;
    private Vector<FileWatchersItem>        FileWatchersVector;
    private Vector<NotificationsItem>       NotificationsVector;
    private Vector<JobDestsItem>            JobDestsVector;
    private Vector<DestsItem>               DestsVector;
    private Vector<DbDestsItem>             DbDestsVector;
    private Vector<ExtDestsItem>            ExtDestsVector;
    private Vector<GlobalAttributesItem>    GlobalAttributesVector;

    private Vector<PlanItem>                PlansVector;
    private Vector<ConsumerGroupItem>       ConsumerGroupsVector;
    private Vector<PlanDirectiveItem>       PlanDirectivesVector;
    private Vector<MappingPrioritiesItem>   MappingPrioritiesVector;
    private Vector<GroupMappingsItem>       GroupMappingsVector;
    private Vector<ConsumerPrivItem>        ConsumerPrivsVector;
    private Vector<SessionItem>             SessionsVector;
    private Vector<ConsumerGroupStatsItem>  ConsumerGroupStatsVector;
    private Vector<CurrentPlanItem>         CurrentPlansVector;

    private Vector<CdbPlanItem>             CdbPlansVector;
    private Vector<CdbPlanDirectiveItem>    CdbPlanDirectivesVector;

}

