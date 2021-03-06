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

package org.apache.carbondata.core.metadata.datatype;

import java.io.Serializable;
import java.util.List;

public class StructField implements Serializable {

  private static final long serialVersionUID = 3271726L;

  private String fieldName;

  private DataType dataType;

  private List<StructField> children;

  public StructField(String fieldName, DataType dataType) {
    this.fieldName = fieldName;
    this.dataType = dataType;
    this.children = null;
  }


  public StructField(String fieldName, DataType dataType, List<StructField> children) {
    this.fieldName = fieldName;
    this.dataType = dataType;
    this.children = children;
  }

  public DataType getDataType() {
    return dataType;
  }

  public String getFieldName() {
    return fieldName;
  }

  public List<StructField> getChildren() {
    return children;
  }
}
