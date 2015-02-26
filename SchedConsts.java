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

public final class SchedConsts {

    public  static final String       SCHEDULER_ROOT_TREE  = "SCHEDULER MANAGER";
    public  static final String       RESOURCE_ROOT_TREE  = "RESOURCE MANAGER";
    public  static final String       PENDING_AREA_TREE  = "PENDING AREA";
    public  static final String       EMPTY_PENDING_AREA_TREE  = "PENDING AREA (EMPTY)";

    public  static final String       JOBS_TREE  = "JOBS";
    public  static final String       PROGRAMS_TREE  = "PROGRAMS";
    public  static final String       SCHEDULES_TREE  = "SCHEDULES";
    public  static final String       JOB_CLASS_TREE  = "JOB CLASSES";
    public  static final String       WINDOWS_TREE  = "WINDOWS";
    public  static final String       GROUP_TREE  = "GROUPS";
    public  static final String       WINDOW_GROUP_TREE  = "WINDOW GROUPS";
    public  static final String       DBDEST_GROUP_TREE  = "DATABASE DESTINATION GROUPS";
    public  static final String       EXTDEST_GROUP_TREE  = "EXTERNAL DESTINATION GROUPS";

    public  static final String       JOB_ARGS_TREE  = "ARGUMENTS";
    public  static final String       PROGRAM_ARGS_TREE  = "ARGUMENTS";
    public  static final String       CHAINS_TREE  = "CHAINS";
    public  static final String       CHAIN_STEPS_TREE  = "CHAIN STEPS";
    public  static final String       CHAIN_RULES_TREE  = "CHAIN RULES";
    public  static final String       GLOBAL_ATTRIBUTES_TREE  = "GLOBAL ATTRIBUTES";
    public  static final String       CREDENTIALS_TREE = "CREDENTIALS";
    public  static final String       DESTINATIONS_TREE = "DESTINATIONS";
    public  static final String       DBDEST_TREE = "DATABASE DESTINATIONS";
    public  static final String       EXTDEST_TREE = "EXTERNAL DESTINATIONS";
    public  static final String       FILE_WATCHERS_TREE = "FILE WATCHERS";
    public  static final String       NOTIFICATIONS_TREE = "E-MAIL NOTIFICATIONS";

    public  static final String       PLANS_TREE = "PLANS";
    public  static final String       CONSUMER_GROUPS_TREE = "CONSUMER GROUPS";
    public  static final String       MAPPING_PRIORITY_TREE = "MAPPING PRIORITY";
    public  static final String       GROUP_MAPPING_TREE = "GROUP MAPPINGS";
    public  static final String       USERS_TREE = "USERS";

    public  static final int          ROOT_SCREEN_NO  = 0;
    public  static final int          CONNECTION_SCREEN_NO  = 1;
    public  static final int          JOB_SCREEN_NO  = 2;
    public  static final int          PROGRAM_SCREEN_NO  = 3;
    public  static final int          SCHEDULE_SCREEN_NO  = 4;
    public  static final int          JOB_CLASS_SCREEN_NO  = 5;
    public  static final int          WINDOW_SCREEN_NO  = 6;
    public  static final int          WINDOW_GROUP_SCREEN_NO  = 7;
    public  static final int          JOB_ARGS_SCREEN_NO  = 8;
    public  static final int          PROGRAM_ARGS_SCREEN_NO  = 9;
    public  static final int          JOBS_RUNNING_SCREEN_NO  = 10;
    public  static final int          CHAINS_SCREEN_NO  = 11;
    public  static final int          CHAIN_STEPS_SCREEN_NO  = 12;
    public  static final int          CHAIN_RULES_SCREEN_NO  = 13;
    public  static final int          JOB_RUN_DETAILS_SCREEN_NO = 14;
    public  static final int          JOB_LOG_SCREEN_NO = 15;
    public  static final int          WINDOW_DETAILS_SCREEN_NO = 16;
    public  static final int          WINDOW_LOG_SCREEN_NO = 17;
    public  static final int          GLOBAL_ATTRIBUTES_SCREEN_NO  = 18;
    public  static final int          CHAINS_RUNNING_SCREEN_NO  = 19;
    public  static final int          CREDENTIALS_SCREEN_NO = 20;
    public  static final int          FILE_WATCHERS_SCREEN_NO = 21;
    public  static final int          NOTIFICATIONS_SCREEN_NO = 22;
    public  static final int          DB_DESTINATION_SCREEN_NO = 23;
    public  static final int          EXT_DESTINATION_SCREEN_NO = 24;
    public  static final int          GROUP_SCREEN_NO = 25;

    public  static final int          PLAN_SCREEN_NO  = 100;
    public  static final int          CONSUMER_GROUP_SCREEN_NO  = 101;
    public  static final int          USER_SCREEN_NO  = 102;
    public  static final int          MAPPING_PRIORITY_SCREEN_NO = 103;
    public  static final int          SESSION_INFO_SCREEN_NO = 105;
    public  static final int          CONSUMER_GROUP_INFO_SCREEN_NO = 106;
    public  static final int          CDB_PLAN_SCREEN_NO  = 107;

    public  static final int          AUDIT_NO = 30;
    public  static final String       PENDING_STATUS     = "PENDING";
    public  static final String       PACKAGE_DIR     = "uk/co/blueshireservices/schedulergrid/";

    public  static final int          DESTINATION_ICON_NO = 40;
    public  static final int          GROUP_ICON_NO = 41;
    public  static final int          DB_GROUP_ICON_NO = 42;
    public  static final int          EXT_GROUP_ICON_NO = 43;

    public static final int           RESOURCE_PLAN_ID  = 50;
    public static final int           CONSUMER_GROUP_ID = 51;
    public static final int           JOB_CLASS_ID = 52;
    public static final int           PROGRAM_ID = 53;
    public static final int           SCHEDULE_ID = 54;
    public static final int           CREDENTIAL_ID = 55;
    public static final int           WINDOW_GROUP_ID = 56;
    public static final int           DB_DEST_ID = 57;
    public static final int           EXT_DEST_ID = 58;
    public static final int           PDBS_ID = 59;

}
