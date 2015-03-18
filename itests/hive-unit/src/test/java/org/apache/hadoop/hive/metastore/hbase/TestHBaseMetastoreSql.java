/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hive.metastore.hbase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;

/**
 * Integration tests with HBase Mini-cluster using actual SQL
 */
public class TestHBaseMetastoreSql extends IMockUtils {

  private static final Log LOG = LogFactory.getLog(TestHBaseStoreIntegration.class.getName());

  @BeforeClass
  public static void startup() throws Exception {
    IMockUtils.startMiniCluster();

  }

  @AfterClass
  public static void shutdown() throws Exception {
    IMockUtils.shutdownMiniCluster();
  }

  @Before
  public void before() throws IOException {
    setupConnection();
    setupDriver();
  }

  @Test
  public void insertIntoTable() throws Exception {
    driver.run("create table iit (c int)");
    CommandProcessorResponse rsp = driver.run("insert into table iit values (3)");
    Assert.assertEquals(0, rsp.getResponseCode());
  }

  @Test
  public void insertIntoPartitionTable() throws Exception {
    driver.run("create table iipt (c int) partitioned by (ds string)");
    CommandProcessorResponse rsp =
        driver.run("insert into table iipt partition(ds) values (1, 'today'), (2, 'yesterday')," +
            "(3, 'tomorrow')");
    Assert.assertEquals(0, rsp.getResponseCode());
  }

  @Test
  public void database() throws Exception {
    CommandProcessorResponse rsp = driver.run("create database db");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("alter database db set owner user me");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("drop database db");
    Assert.assertEquals(0, rsp.getResponseCode());
  }

  @Ignore
  public void table() throws Exception {
    driver.run("create table tbl (c int)");
    CommandProcessorResponse rsp = driver.run("insert into table tbl values (3)");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("select * from tbl");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("alter table tbl set tblproperties ('example', 'true')");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("drop table tbl");
    Assert.assertEquals(0, rsp.getResponseCode());
  }

  @Ignore
  public void partitionedTable() throws Exception {
    driver.run("create table parttbl (c int) partitioned by (ds string)");
    CommandProcessorResponse rsp =
        driver.run("insert into table parttbl partition(ds) values (1, 'today'), (2, 'yesterday')" +
            ", (3, 'tomorrow')");
    Assert.assertEquals(0, rsp.getResponseCode());
    // Do it again, to check insert into existing partitions
    rsp = driver.run("insert into table parttbl partition(ds) values (4, 'today'), (5, 'yesterday')"
        + ", (6, 'tomorrow')");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("insert into table parttbl partition(ds = 'someday') values (1)");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("insert into table parttbl partition(ds = 'someday') values (2)");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("alter table parttbl add partition (ds = 'whenever')");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("insert into table parttbl partition(ds = 'whenever') values (2)");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("alter table parttbl touch partition (ds = 'whenever')");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("alter table parttbl drop partition (ds = 'whenever')");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("select * from parttbl");
    Assert.assertEquals(0, rsp.getResponseCode());
    rsp = driver.run("select * from parttbl where ds = 'today'");
    Assert.assertEquals(0, rsp.getResponseCode());
  }


}