/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.carbondata.spark.testsuite.iud

import java.io.File

import org.apache.spark.sql.test.util.QueryTest
import org.apache.spark.sql.{CarbonEnv, Row, SaveMode}
import org.scalatest.BeforeAndAfterAll

import org.apache.carbondata.core.datastore.filesystem.{CarbonFile, CarbonFileFilter}
import org.apache.carbondata.core.datastore.impl.FileFactory
import org.apache.carbondata.core.util.path.CarbonTablePath


class DeleteCarbonTableTestCase extends QueryTest with BeforeAndAfterAll {
  override def beforeAll {
    sql("use default")
    sql("drop database  if exists iud_db cascade")
    sql("create database  iud_db")

    sql("""create table iud_db.source2 (c11 string,c22 int,c33 string,c55 string, c66 int) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/source2.csv' INTO table iud_db.source2""")
    sql("use iud_db")
  }
  test("delete data from carbon table with alias [where clause ]") {
    sql("""create table iud_db.dest (c1 string,c2 int,c3 string,c5 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from iud_db.dest d where d.c1 = 'a'""").show
    checkAnswer(
      sql("""select c2 from iud_db.dest"""),
      Seq(Row(2), Row(3),Row(4), Row(5))
    )
  }
  test("delete data from  carbon table[where clause ]") {
    sql("""drop table if exists iud_db.dest""")
    sql("""create table iud_db.dest (c1 string,c2 int,c3 string,c5 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from iud_db.dest where c2 = 2""").show
    checkAnswer(
      sql("""select c1 from iud_db.dest"""),
      Seq(Row("a"), Row("c"), Row("d"), Row("e"))
    )
  }
  test("delete data from  carbon table[where IN  ]") {
    sql("""drop table if exists iud_db.dest""")
    sql("""create table iud_db.dest (c1 string,c2 int,c3 string,c5 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from dest where c1 IN ('d', 'e')""").show
    checkAnswer(
      sql("""select c1 from dest"""),
      Seq(Row("a"), Row("b"),Row("c"))
    )
  }

  test("delete data from  carbon table[with alias No where clause]") {
    sql("""drop table if exists iud_db.dest""")
    sql("""create table iud_db.dest (c1 string,c2 int,c3 string,c5 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from iud_db.dest a""").show
    checkAnswer(
      sql("""select c1 from iud_db.dest"""),
      Seq()
    )
  }
  test("delete data from  carbon table[No alias No where clause]") {
    sql("""drop table if exists iud_db.dest""")
    sql("""create table iud_db.dest (c1 string,c2 int,c3 string,c5 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from dest""").show()
    checkAnswer(
      sql("""select c1 from dest"""),
      Seq()
    )
  }

  test("delete data from  carbon table[ JOIN with another table ]") {
    sql("""drop table if exists iud_db.dest""")
    sql("""create table iud_db.dest (c1 string,c2 int,c3 string,c5 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql(""" DELETE FROM dest t1 INNER JOIN source2 t2 ON t1.c1 = t2.c11""").show(truncate = false)
    checkAnswer(
      sql("""select c1 from iud_db.dest"""),
      Seq(Row("c"), Row("d"), Row("e"))
    )
  }

  test("delete data from  carbon table[where numeric condition  ]") {
    sql("""drop table if exists iud_db.dest""")
    sql("""create table iud_db.dest (c1 string,c2 int,c3 string,c5 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from  iud_db.dest where c2 >= 4""").show()
    checkAnswer(
      sql("""select count(*) from iud_db.dest"""),
      Seq(Row(3))
    )
  }

  test("partition delete data from carbon table with alias [where clause ]") {
    sql("drop table if exists iud_db.dest")
    sql("""create table iud_db.dest (c1 string,c2 int,c5 string) PARTITIONED BY(c3 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from iud_db.dest d where d.c1 = 'a'""").show
    checkAnswer(
      sql("""select c2 from iud_db.dest"""),
      Seq(Row(2), Row(3),Row(4), Row(5))
    )
  }
  test("partition delete data from  carbon table[where clause ]") {
    sql("""drop table if exists iud_db.dest""")
    sql("""create table iud_db.dest (c1 string,c2 int,c5 string) PARTITIONED BY(c3 string) STORED BY 'org.apache.carbondata.format'""")
    sql(s"""LOAD DATA LOCAL INPATH '$resourcesPath/IUD/dest.csv' INTO table iud_db.dest""")
    sql("""delete from iud_db.dest where c2 = 2""").show
    checkAnswer(
      sql("""select c1 from iud_db.dest"""),
      Seq(Row("a"), Row("c"), Row("d"), Row("e"))
    )
  }

  test("Records more than one pagesize after delete operation ") {
    sql("DROP TABLE IF EXISTS carbon2")
    import sqlContext.implicits._
    val df = sqlContext.sparkContext.parallelize(1 to 2000000)
      .map(x => (x+"a", "b", x))
      .toDF("c1", "c2", "c3")
    df.write
      .format("carbondata")
      .option("tableName", "carbon2")
      .option("tempCSV", "true")
      .option("compress", "true")
      .mode(SaveMode.Overwrite)
      .save()

    checkAnswer(sql("select count(*) from carbon2"), Seq(Row(2000000)))

    sql("delete from carbon2 where c1 = '99999a'").show()

    checkAnswer(sql("select count(*) from carbon2"), Seq(Row(1999999)))

    checkAnswer(sql("select * from carbon2 where c1 = '99999a'"), Seq())

    sql("DROP TABLE IF EXISTS carbon2")
  }

  test("test if delete is unsupported for pre-aggregate tables") {
    sql("drop table if exists preaggMain")
    sql("drop table if exists preaggmain_preagg1")
    sql("create table preaggMain (a string, b string, c string) stored by 'carbondata'")
    sql("create datamap preagg1 on table PreAggMain USING 'preaggregate' as select a,sum(b) from PreAggMain group by a")
    intercept[RuntimeException] {
      sql("delete from preaggmain where a = 'abc'").show()
    }.getMessage.contains("Delete operation is not supported for tables")
    intercept[RuntimeException] {
      sql("delete from preaggmain_preagg1 where preaggmain_a = 'abc'").show()
    }.getMessage.contains("Delete operation is not supported for pre-aggregate table")
    sql("drop table if exists preaggMain")
    sql("drop table if exists preaggmain_preagg1")
  }

  test("test select query after compaction, delete and clean files") {
    sql("drop table if exists select_after_clean")
    sql("create table select_after_clean(id int, name string) stored by 'carbondata'")
    sql("insert into select_after_clean select 1,'abc'")
    sql("insert into select_after_clean select 2,'def'")
    sql("insert into select_after_clean select 3,'uhj'")
    sql("insert into select_after_clean select 4,'frg'")
    sql("alter table select_after_clean compact 'minor'")
    sql("clean files for table select_after_clean")
    sql("delete from select_after_clean where name='def'")
    sql("clean files for table select_after_clean")
    assertResult(false)(new File(
      CarbonTablePath.getSegmentPath(s"$storeLocation/iud_db.db/select_after_clean", "0")).exists())
    checkAnswer(sql("""select * from select_after_clean"""),
      Seq(Row(1, "abc"), Row(3, "uhj"), Row(4, "frg")))
  }

  test("test number of update table status files after delete query where no records are deleted") {
    sql("drop table if exists update_status_files")
    sql("create table update_status_files(name string,age int) stored by 'carbondata'")
    sql("insert into update_status_files select 'abc',1")
    sql("insert into update_status_files select 'def',2")
    sql("insert into update_status_files select 'xyz',4")
    sql("insert into update_status_files select 'abc',6")
    sql("alter table update_status_files compact 'minor'")
    sql("delete from update_status_files where age=3").show()
    sql("delete from update_status_files where age=5").show()
    val carbonTable = CarbonEnv
      .getCarbonTable(Some("iud_db"), "update_status_files")(sqlContext.sparkSession)
    val metaPath = carbonTable.getMetadataPath
    val files = FileFactory.getCarbonFile(metaPath)
    val result = CarbonEnv.getInstance(sqlContext.sparkSession).carbonMetastore.getClass
    if(result.getCanonicalName.contains("CarbonFileMetastore")) {
      assert(files.listFiles(new CarbonFileFilter {
        override def accept(file: CarbonFile): Boolean = !file.isDirectory
      }).length == 2)
    }
    else
      assert(files.listFiles().length == 1)

    sql("drop table update_status_files")
  }


  override def afterAll {
    sql("use default")
    sql("drop database  if exists iud_db cascade")
  }
}